/*
 * GUI.java 
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import be.abeel.gui.GridBagPanel;
import be.abeel.gui.MultiFileBean;
import be.abeel.util.ColumnIterator;
import be.abeel.util.GZIPPrintWriter;
import be.abeel.util.LineIterator;

public class GUI extends GridBagPanel {

    private static final long serialVersionUID = 5920538636356778143L;

    static void launchGUI() {
        Properties prop = new Properties();
        try {
            prop.load(GUI.class.getResourceAsStream("/prosom.properties"));
        } catch (IOException e) {
            System.err.println("Properties could not be loaded.");
        }
        JFrame window = new JFrame("ProSOM " + prop.getProperty("version", "dev")
                + " :: Copyright 2008 :: Thomas Abeel");
        window.setContentPane(new GUI(window));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        center(window);
        window.setVisible(true);
    }

    private static void center(JFrame window) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getPreferredSize();
        window.setLocation(screenSize.width / 2 - (windowSize.width / 2), screenSize.height / 2
                - (windowSize.height / 2));

    }

    private GUI(final JFrame parent) {
        // Properties organisms = new Properties();
        final JComboBox organismCombo = new JComboBox();

        final HashMap<String, Double> mapping = new HashMap<String, Double>();

        System.out.println("loading organisms");
        LineIterator it = new LineIterator(this.getClass().getResourceAsStream("/organisms.properties"));
        for (String line : it) {
            String[] arr = line.split("=");
            mapping.put(arr[0].trim(), Double.parseDouble(arr[1].trim()));
        }
        for (String key : mapping.keySet()) {
            // if (!key.equals("base"))
            organismCombo.addItem(key);
        }
        System.out.println("organisms loaded");
        final MultiFileBean input = new MultiFileBean();
        final JCheckBox outputAllValues = new JCheckBox("Output all values");
        final JCheckBox split = new JCheckBox("Split output");
        final JComboBox format = new JComboBox(OutputFormat.values());
        // final JTextField text = new JTextField("prosom2.predictions.tsv");
        JButton launch = new JButton("Start the promoter prediction");
        launch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final ProSOM som = new ProSOM();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File outFile;
                        PrintWriter out;
                        // File fileName = new File(text.getText());
                        try {

                            // System.out.println(fileName.getAbsolutePath());
                            if (input.getFiles().length > 1) {
                                outFile = new File(input.getFiles()[0].getParent(), "ProSOM.tsv");
                            } else {
                                outFile = new File(input.getFiles()[0] + ".prosom.tsv");
                            }

                            out = new PrintWriter(outFile + ".tmp");

                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            Splash.hide();
                            return;
                        }
                        for (File file : input.getFiles()) {
                            Splash.processing(file);
                            try {
                                som.predictFile(file.toString(), out, mapping.get(organismCombo.getSelectedItem()),
                                        outputAllValues.isSelected());
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        out.close();
                        try {
                            postProcess(outFile);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        JOptionPane.showMessageDialog(parent, "Output written to: " + outFile, "Done!",
                                JOptionPane.INFORMATION_MESSAGE);
                        Splash.hide();

                    }

                    private void postProcess(File outFile) throws IOException {
                        int ID = 0;
                        ColumnIterator it = new ColumnIterator(outFile + ".tmp");
                        PrintWriter out = null;
                        String currentChr = null;
                        for (String[] arr : it) {
                            if (!arr[0].equals(currentChr)) {
                                currentChr = arr[0];
                                System.out.println(currentChr);

                                if (split.isSelected() && out != null)
                                    out.close();
                                if (split.isSelected()) {
                                    String formatS=(format.getSelectedItem().toString().toLowerCase());
                                    out = new GZIPPrintWriter(new File(outFile.getParentFile(), currentChr
                                            + ".prosom."+formatS));
                                    printHeader(out, currentChr);
                                } else if (out == null) {
                                    out = new GZIPPrintWriter(outFile);
                                    printHeader(out, currentChr);
                                }
                            }
                            switch ((OutputFormat) format.getSelectedItem()) {
                            case GFF3:
                                out.println(arr[0] + "\tProSOM\tab-initio prediction\t" + arr[1] + "\t" + arr[2] + "\t"
                                        + arr[3] + "\t.\t.\tID=" + (ID++) + "predicted=" + arr[4]);
                                break;
                            case WIG:
                                out.println(arr[1] + "\t" + arr[3]);
                                break;
                            default:
                            case PLAIN:
                                out.println(arr[0] + "\t" + arr[1] + "\t" + arr[2] + "\t" + arr[3] + "\t" + arr[4]);
                                break;

                            }
                            
                        }
                        out.close();

                    }

                    private void printHeader(PrintWriter out, String chr) {
                        out.println("browser position " + chr);
                        out.println("browser hide all");
                        out.println("browser pack refGene");
                        switch ((OutputFormat) format.getSelectedItem()) {
                        case WIG:
                            out
                                    .println("track type=wiggle_0 name=\"ProSOM\" description=\"ProSOM core promoter prediction scores\" visibility=full");
                            out.println("variableStep chrom=" + chr + " span=250");
                            break;
                        case GFF3:

                            out
                                    .println("track name=\"EP3\" description=\"EP3 core promoter prediction scores\" visibility=full");
                            break;
                        default:
                        }
                    }

                }).start();
                Splash.show(parent);

            }

        });
        gc.gridwidth = 5;
        add(input, gc);
        gc.gridy++;
        // add(new TitledComponent("File name for predictions", text), gc);
        //        
        // gc.gridy++;
        gc.gridwidth = 1;
        add(organismCombo, gc);
        gc.gridx++;

        // add(outputAllValues,gc);
        gc.gridx++;
        add(format,gc);
        gc.gridx++;
        add(split,gc);
        gc.gridx++;
        add(launch, gc);
    }
}
