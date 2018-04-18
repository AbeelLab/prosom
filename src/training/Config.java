package training;
import be.abeel.bioinformatics.dnaproperties.DNAProperty;

/*
 * Config.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */

public class Config {

    public static final DNAProperty pp=DNAProperty.BaseStacking;
    
    public static String root;

    

    static {
        if (System.getProperty("os.name").equalsIgnoreCase("linux"))
            root = "~/";
        else
            root = "c:/";
    }

    public static void main(String[] args) {
        System.out.println("Root=" + root);

    }
}
