package training;
import java.io.IOException;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SimpleDataset;
import net.sf.javaml.core.SimpleInstance;
import be.abeel.bioinformatics.FastaIterator;
import be.abeel.bioinformatics.Record;


/*
 * PromoterDataLoader.java 
 * -----------------------
 * Copyright (C) 2005-2007  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */

public class PromoterDataLoader {
    
    public static Dataset loadPromoterData(int classValue, int totalInstances, String file,int window,int times,int start,int end) {
        Dataset data = new SimpleDataset();
        try {
            FastaIterator fi = new FastaIterator(file);
            for (Record rec : fi) {
                double[] profile = Config.pp.normalizedProfile(rec.getSequence().substring(start,end));
                double[]tmp=new double[profile.length];
                System.arraycopy(profile,0, tmp, 0, profile.length);
                profile=tmp;
                smoothProfile(profile,window,times);
                Instance inst = new SimpleInstance(profile,classValue);
                
                data.add(inst);
                
                if (data.size() >= totalInstances) {
                    fi.close();
                    return data;
                }
            }
            fi.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
    
    
    public static Dataset loadPromoterData(int classValue, int totalInstances, String file,int window,int times) {
       return loadPromoterData(classValue, totalInstances, file, window, times, 0, 250);
            
        
    }
    private static void smoothProfile(double[] profile,int window,int times) {
//        int window =10;
//        int times = 1;
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < profile.length; j++) {
                double sum = 0;
                for (int k = 0; k < window; k++) {
                    int pos=(j+k)%profile.length;
                    sum += profile[pos];
                }
                profile[j] =  sum / window;
            }
        }

    }
}
