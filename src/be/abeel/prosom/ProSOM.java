/*
 * ProSOM.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package be.abeel.prosom;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import be.abeel.bioinformatics.dnaproperties.DNAProperty;

public class ProSOM {

    /* Human prediction threshold */
    private static double baseFactor = 0.19177694696027228;

    //
    // private double arathFactor = 0.00928389598325472;// 0.56;

    private static final String MODELNAME = "/model.dat";

    private static final int WINDOW = 250;

    private static final int STEP = 250;

    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        ProSOM som = new ProSOM();
        CmdLineParser parser = new CmdLineParser();

        CmdLineParser.Option factorO = parser.addStringOption('f', "factor");
        CmdLineParser.Option allValuesO = parser.addBooleanOption('a', "all");
        CmdLineParser.Option outputO = parser.addStringOption('o', "output");
        if (args.length == 0) {
            try {
                GUI.launchGUI();
            } catch (HeadlessException e) {
                System.err.println("No graphical environment.");
                // System.out.println(parser.toString());
            }
        } else {

            try {
                parser.parse(args);
            } catch (IllegalOptionValueException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnknownOptionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // double factor =baseFactor;
            double factor = Double.parseDouble(parser.getOptionValue(factorO, baseFactor).toString());
            Boolean allValues = (Boolean) parser.getOptionValue(allValuesO, Boolean.FALSE);
            // if(allValues==null)
            // allValues=Boolean.FALSE;
            String output = (String) parser.getOptionValue(outputO);
            // System.out.println("Factor: "+factor);
            // System.out.println("All values: "+allValues);
            // System.out.println("Output file: "+output);
            PrintWriter out;
            if (output == null)
                out = new PrintWriter(System.out);
            else
                out = new PrintWriter(output);
            // check for precalculated correction factor
            String[] remaining = parser.getRemainingArgs();
            for (int i = 0; i < remaining.length; i++) {
                som.predictFile(remaining[i], out, factor, allValues);
            }
            out.close();
        }
    }

    private double threshold;

    private Centroid[] model;

    /**
     * Make predictions a file
     * 
     * @param file
     *            input file
     * @param out
     *            output writer
     * @param speciesFactor
     *            correction factor
     * @param fullOutput
     *            output all values
     * @throws IOException
     */
    void predictFile(String file, PrintWriter out, double speciesFactor, boolean fullOutput) throws IOException {

        InputStream fileStream = null;
        if (file.startsWith("http://") || file.startsWith("ftp://")) {
            URI uri;
            try {
                uri = new URI(file);
            } catch (URISyntaxException e) {
                throw new IOException("Invalid URL specification: " + file);
            }
            fileStream = uri.toURL().openStream();
        } else {
            fileStream = new FileInputStream(file);
        }
        BufferedReader in = null;

        if (file.endsWith("gz")) {
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(fileStream)));
        } else if (file.endsWith("zip")) {
            System.err.println("ProSOM will only process the first file in your ZIP archive!!!");
            ZipInputStream zipinputstream = new ZipInputStream(fileStream);
            ZipEntry zipentry = zipinputstream.getNextEntry();
            // System.out.println(zipentry);
            if (zipentry != null) {
                // for each entry to be extracted
                String entryName = zipentry.getName();
                // System.out.println("File ::" + entryName);
                // RandomAccessFile rf;
                // System.out.println(zipentry.);
                System.out.println(entryName);
                // File newFile = new File(entryName);
                in = new BufferedReader(new InputStreamReader(zipinputstream));

                // out = read(it, classIndex, separator);
                // zipinputstream.closeEntry();

            } else {
                throw new IOException("No ZIP entry");
            }
        } else {
            in = new BufferedReader(new InputStreamReader(fileStream));
        }
        long position = 200;
        String line = in.readLine();
        String chrName = line.substring(1).split(" ")[0].split("\\|")[0];
        line = in.readLine();

        StringBuffer buf = new StringBuffer(WINDOW);
        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        nf.setMaximumFractionDigits(5);
        while (line != null) {
            while (line != null && buf.length() < WINDOW) {
                buf.append(line);
                line = in.readLine();
            }
            while (buf.length() >= WINDOW) {
                String sub = buf.substring(0, WINDOW);
                String lowerSub = sub.toLowerCase();

                if (!lowerSub.equals(sub)) {// no masked sequence
                    double[] tmp = DNAProperty.BaseStacking.normalizedProfile(sub);
                    double[] profile = new double[tmp.length];
                    System.arraycopy(tmp, 0, profile, 0, tmp.length);
                    smoothProfile(profile, 3, 2);
                    for (int i = 0; i < profile.length; i++)
                        profile[i] /= speciesFactor;
                    double score = predict(profile);

                    if (score >= threshold || fullOutput) {
                        out.println(chrName + "\t" + (position - 200) + "\t" + (position + 50) + "\t"
                                + nf.format(score) + "\t" + (score >= threshold));

                    }

                }
                buf.delete(0, STEP);
                position += STEP;
            }

        }
        in.close();
    }

    private void smoothProfile(double[] profile, int window, int times) {
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < profile.length; j++) {
                double sum = 0;
                for (int k = 0; k < window; k++) {
                    int pos = (j + k) % profile.length;
                    sum += profile[pos];
                }
                profile[j] = (double) sum / window;
            }
        }

    }

    private double predict(double[] profile) {
        double best = euclidean(model[0].values, profile);
        int index = 0;
        for (int i = 1; i < model.length; i++) {
            double d = euclidean(model[i].values, profile);
            if (d < best) {
                best = d;
                index = i;
            }
        }
        return model[index].prob;

    }

    private double euclidean(double[] a, double[] b) {
        if (a.length != b.length)
            throw new RuntimeException("Wrong profile length!");
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);

        }
        return Math.sqrt(sum);
    }

    ProSOM() {

        try {
            // Properties prop = new Properties();

            // prop.load(this.getClass().getResourceAsStream("organisms.properties"));
            // baseFactor = Double.parseDouble(prop.get("base").toString());
            model = load();
            double[] tmp = new double[36];
            for (int i = 0; i < model.length; i++) {
                tmp[i] = model[i].prob;

            }
            Arrays.sort(tmp);
            threshold = tmp[33];
            // System.out.println(Arrays.toString(tmp));
            // System.err.println("Threshold=" + threshold);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Centroid[] load() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(this.getClass().getResourceAsStream(MODELNAME));
        double[] values = (double[]) in.readObject();
        return loadFromArray(values);

    }

    public static Centroid[] loadFromArray(double[] values) {

        Centroid[] out = new Centroid[36];
        for (int i = 0; i < out.length; i++) {
            out[i] = new Centroid();
            out[i].prob = values[(values.length / 36) * i];
            double[] val = new double[values.length / 36 - 1];
            System.arraycopy(values, i * (values.length / 36) + 1, val, 0, val.length);
            for (int j = 0; j < val.length; j++)
                val[j] /= baseFactor;
            out[i].values = val;
        }
        return out;
    }

}
