/*
 * StoreModel.java 
 * -----------------------
 * Copyright (C) 2008  Thomas Abeel
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Author: Thomas Abeel
 */
package training;

import net.sf.javaml.core.Instance;

public class StoreModel {
    public static void store(double[] probabilities, Instance[] centroids,String file) {
        double[] values = new double[(centroids[0].size() + 1 )* 36];
        for(int i=0;i<probabilities.length;i++){
            values[i*(centroids[0].size()+1)]=probabilities[i];
            System.arraycopy(centroids[i].toArray(), 0, values, i*(centroids[0].size()+1)+1, centroids[0].size());
        }
            
        training.Serial.store(values, file+".dat");
    }
//    public static Centroid[]load(){
//        double[]values=(double[])Serial.load("prosom.dat");
//        Centroid[]out=new Centroid[36];
//        for(int i=0;i<out.length;i++){
//            out[i]=new Centroid();
//            out[i].prob=values[i*36];
////            values[i*49]=probabilities[i];
//            double[]val=new double[values.length/36-1];
//            System.arraycopy(values, i*36+1, val, 0, val.length);
//            out[i].values=val;
//        }
//        return out;
//    }
    
   
}

