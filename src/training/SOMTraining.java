package training;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DatasetTools;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import training.SOM.GridType;
import training.SOM.LearningType;
import training.SOM.NeighbourhoodFunction;
import be.abeel.prosom.ProSOM;

/*
 * PromoterSOM.java 
 * -----------------------
 * Copyright (C) 2005-2007  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */

public class SOMTraining {

    public final static String prefix = "training/";

    // public final static String prefix2 =
    // "/group/biocomp/users/thabe/data/hg17/";

    // private static int WINDOW = 250;
    //
    // private static int STEP = 250;

    public static void main(String[] args) throws Exception {
        int grid = 6;// Integer.parseInt(args[0]);
        int YGRID = grid;// Integer.parseInt(args[1]);
        int DATASIZE = 30000;// Integer.parseInt(args[2]);
        // String chr = args[0];
        int seed = 2;
        SOMTraining pps = new SOMTraining(grid, YGRID, DATASIZE, true, true, true, new Random(seed));
        // for(int i=0;i<pps.centroids.length;i++){
        // System.out.println("Centroid: "+i+"\t"+pps.promoterProb[i]);
        // System.out.println(pps.centroids[i]);
        // }
        StoreModel.store(pps.promoterProb, pps.centroids, "be/abeel/prosom/models/" + grid + "x" + grid + "_"
                + DATASIZE / 1000 + "k_" + seed);
        double[] values = (double[]) Serial.load("be/abeel/prosom/models/" + grid + "x" + grid + "_" + DATASIZE / 1000
                + "k_" + seed+".dat");
        System.out.println("Loaded: "+values);
        be.abeel.prosom.Centroid[] centroids = ProSOM.loadFromArray(values);
        PrintWriter out = new PrintWriter("training/"+grid + "x" + grid + "_" + DATASIZE / 1000 + "k_" + seed + ".log");
        for (int i = 0; i < pps.centroids.length; i++) {
            out.println("Centroid: " + i + "\t" + pps.promoterProb[i] + "\t" + centroids[i].prob);
            out.println(pps.centroids[i]);
            out.println(Arrays.toString(centroids[i].values));
        }
        out.close();
        // System.out.println(Arrays.toString(pps.centroids));
        // System.out.println(Arrays.toString(pps.promoterProb));
        // String[] chrs = { "21" };// , "1", "2", "3", "4", "5", "6", "7", "8",
        // "9", "10", "11", "12", "13", "14", "15",
        // "16",
        // "17", "18", "19", "20", "22", "X", "Y" };

        // double[] clusterProb = new double[XGRID * YGRID];
        // System.arraycopy(pps.promoterProb, 0, clusterProb, 0,
        // pps.promoterProb.length);
        //
        // Arrays.sort(clusterProb);
        //
        // PrintWriter[] out = new PrintWriter[XGRID * YGRID];
        // for (int i = XGRID * YGRID - 5; i < XGRID * YGRID; i++) {
        // out[i] = new PrintWriter(prefix2 + "prosom_"+STEP+"_" + args[0] + "_"
        // + i + ".tsv");
        // }
        // double[] total = new double[XGRID * YGRID];
        // double[] prom = new double[XGRID * YGRID];
        // // for (String chr : chrs) {
        // System.out.println("Predicting: Chromosome " + chr);
        // BufferedReader in = new BufferedReader(new FileReader(prefix2 + "chr"
        // + chr + ".fa"));
        // long position = 200;
        //
        // String line = in.readLine();// skip headerline
        // line = in.readLine();
        //
        // StringBuffer buf = new StringBuffer(WINDOW);
        // // String nn = createNN(WINDOW);
        // while (line != null) {
        //
        // while (line != null && buf.length() < WINDOW) {
        //
        // buf.append(line);
        // line = in.readLine();
        //
        // }
        //
        // while (buf.length() >= WINDOW) {
        // String sub = buf.substring(0, WINDOW);
        // String lowerSub = sub.toLowerCase();
        //
        // // if (!lowerSub.equals(nn)) {
        // if (!lowerSub.equals(sub)) {
        // double[] tmp = Config.pp.normalizedProfile(sub);
        // double[] profile = new double[tmp.length];
        // System.arraycopy(tmp, 0, profile, 0, tmp.length);
        // smoothProfile(profile, 3, 2);
        // Instance inst = new SimpleInstance(profile);
        // double prob = pps.getProbability(inst, Type.PROMOTER);
        // for (int i = (XGRID * YGRID) - 5; i < XGRID * YGRID; i++) {
        // // for(int i=30;i<XGRID*XGRID;i++){
        // if (prob >= clusterProb[i]) {
        // out[i].println(chr + "\t" + position + "\t" + prob + "\t" +
        // pps.predictIndex(inst));
        // prom[i]++;
        // }
        // total[i]++;
        // }
        // }
        // buf.delete(0, STEP);
        // position += STEP;
        //
        // }
        // }
        // // }
        // for (int i = 0; i < XGRID * YGRID; i++) {
        // if (out[i] != null)
        // out[i].close();
        // System.out.println("Threshold = " + clusterProb[i]);
        // System.out.println("Total chunks: " + total[i]);
        // System.out.println("Promoters predicted: " + prom[i] + " (" +
        // (prom[i] / total[i] + ")"));
        // System.out.println();
        // }

    }

    // private static String createNN(int window) {
    // StringBuffer n = new StringBuffer();
    // while (n.length() < window)
    // n.append("n");
    // return n.toString();
    // }

    // private static void smoothProfile(double[] profile, int window, int
    // times) {
    // for (int i = 0; i < times; i++) {
    // for (int j = 0; j < profile.length; j++) {
    // double sum = 0;
    // for (int k = 0; k < window; k++) {
    // int pos = (j + k) % profile.length;
    // sum += profile[pos];
    // }
    // profile[j] = (double) sum / window;
    // }
    // }
    //
    // }

    public enum Type {
        PROMOTER(0), EXON(2), TRANSCRIPT(1), INTERGENIC(3);

        private int index;

        Type(int i) {
            this.index = i;
        }

        public int value() {
            return index;
        }
    }

    private int predictIndex(Instance inst) {
        DistanceMeasure dm = new EuclideanDistance();
        int index = 0;
        double bestDistance = dm.calculateDistance(inst, centroids[0]);
        for (int i = 1; i < centroids.length; i++) {
            double dist = dm.calculateDistance(inst, centroids[i]);
            if (dist < bestDistance) {
                index = i;
                bestDistance = dist;
            }
        }
        return index;
    }

    public double getProbability(Instance inst, Type type) {
        switch (type) {
        case PROMOTER:
            return promoterProb[predictIndex(inst)];

        case INTERGENIC:
            return intergenicProb[predictIndex(inst)];
        case EXON:
            return exonProb[predictIndex(inst)];
        case TRANSCRIPT:
            return transcriptProb[predictIndex(inst)];
        }
        return -1;
    }

    public Type predict(Instance inst) {
        int index = predictIndex(inst);
        if (promoterProb[index] > 0.5) {
            return Type.PROMOTER;
        } else
            return Type.INTERGENIC;

    }

    private double[] promoterProb;

    private double[] exonProb;

    private double[] intergenicProb;

    private double[] transcriptProb;

    private Instance[] centroids;

    public SOMTraining(int XGRID, int YGRID, int DATASIZE, boolean promoters, boolean transcripts, boolean intergenics,
            Random r) {

        SOM som = new SOM(XGRID, YGRID, GridType.RECTANGLES, 10000, 0.1, 1, LearningType.LINEAR,
                NeighbourhoodFunction.GAUSSIAN);
        som.generator = r;
        Dataset data = new SimpleDataset();
        if (promoters) {
            Dataset prom = PromoterDataLoader.loadPromoterData(Type.PROMOTER.value(), DATASIZE, prefix
                    + "homo_sapiens_250_dbtss.fa", 3, 2);
            System.out.println("Promoters: " + prom.size());
            // DatasetTools.merge(data, prom);
            data.addAll(prom);
        }
        if (transcripts) {
            Dataset trans = PromoterDataLoader.loadPromoterData(Type.TRANSCRIPT.value(), DATASIZE, prefix
                    + "human_transcript.fa", 3, 2);
            System.out.println("Transcripts: " + trans.size());
            // DatasetTools.merge(data, trans);
            data.addAll(trans);
        }

        if (intergenics) {
            Dataset intergenic = PromoterDataLoader.loadPromoterData(Type.INTERGENIC.value(), DATASIZE, prefix
                    + "human_intergenic.fa", 3, 2);
            System.out.println("Intergenics: " + intergenic.size());
            // DatasetTools.merge(data, intergenic);
            data.addAll(intergenic);
        }

        System.out.println("Datasetsize: " + data.size());
        System.out.println("Started training...");
        Dataset[] clusters = som.executeClustering(data);
        // GridDataset[] lClusters = som.getLabeledClustering();
        centroids = new Instance[clusters.length];
        promoterProb = new double[clusters.length];
        exonProb = new double[clusters.length];
        intergenicProb = new double[clusters.length];
        transcriptProb = new double[clusters.length];

        for (int i = 0; i < clusters.length; i++) {
            centroids[i] = DatasetTools.getCentroid(clusters[i]);
            double[] counts = new double[4];
            for (int j = 0; j < clusters[i].size(); j++) {
                counts[clusters[i].instance(j).classValue()]++;
            }
            promoterProb[i] = counts[Type.PROMOTER.value()] / clusters[i].size();
            exonProb[i] = counts[Type.EXON.value()] / clusters[i].size();
            intergenicProb[i] = counts[Type.INTERGENIC.value()] / clusters[i].size();
            transcriptProb[i] = counts[Type.TRANSCRIPT.value()] / clusters[i].size();

        }
        System.out.println("Training finished!");

    }

}
