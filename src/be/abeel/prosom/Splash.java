/*
 * Splash.java 
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Splash {

    private static JDialog splash = null;

    private static JLabel processing = new JLabel("<empty>");

//    public static void main(String[]args){
//        show(null);
//    }
//    
    public static void show(JFrame parent) {
        if (splash == null) {
            splash = new JDialog(parent, "");
            splash.setUndecorated(true);
            
            splash.setModal(true);
            splash.setLayout(new GridBagLayout());
            
            
            JLabel text = new JLabel("Predicting promoters, this may take a while...");
            
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(3, 3, 3, 3);
            gc.fill = GridBagConstraints.BOTH;
            gc.gridx=0;
            gc.gridy=0;
            gc.gridheight=1;
            gc.gridwidth=1;
            
            splash.add(text, gc);
            JProgressBar pg = new JProgressBar();
            pg.setIndeterminate(true);
            gc.gridy++;
            splash.add(processing, gc);
            gc.gridy++;
            splash.add(pg, gc);
           
            center(splash);
        }
        processing.setText("Initializing...");
        splash.pack();
        splash.setVisible(true);

    }

    public static void hide() {
        if (splash != null)
            splash.setVisible(false);

    }

    private static void center(JDialog window) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getPreferredSize();
        window.setLocation(screenSize.width / 2 - (windowSize.width / 2), screenSize.height / 2
                - (windowSize.height / 2));

    }

    public static void processing(File file) {
        processing.setText("Processing: "+file.getName());

    }
}
