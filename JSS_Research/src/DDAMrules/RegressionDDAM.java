/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DDAMrules;

import jsp.*;
import TwoWaySchedulingPolicy.Dynamic2Way_SP;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TestUtils;
import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author nguyensu
 */

public class RegressionDDAM {
    public static double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
    public static int[] numbeOfMachines = {4,5,6,10,20};
    public static String[] lowers = {"miss","full"};
    public static String[] dists = {"expo","erlang2","uniform"};
    public static double[] Utilisation = {0.6,0.7,0.8,0.9,0.95};
    public static int[] NumbeOfMachines = {4,5,6,10,20};
    public static String[] Lowers = {"missing","full"};
    public static String[] Dists = {"Exponential","Erlang-2","Uniform"};
    public static String[] _case = {"-","a","b","c","d"};
    
    public static String[] DDAM = {"$\\mathcal{D}^{\\mathtt{ODDAM}}$", "$\\mathcal{D}^{\\mathtt{ADDAM}}$",
        "$\\mathtt{DTWK}$", "$\\mathtt{DPPW}$", "$\\mathtt{ADRES}$"};
    public static NumberFormat formatter = new DecimalFormat("#0.0000");
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, IllegalArgumentException, MathException {
        double[][][][][] detailedTest = new double [dists.length][lowers.length][numbeOfMachines.length][utilisation.length][30];
        for (int t = 0; t < dists.length; t++) {
            for (int j = 0; j < lowers.length; j++) {
                for (int k = 0; k < numbeOfMachines.length; k++) {
                    for (int l = 0; l < utilisation.length; l++) {
                        double[] coff = new double[]{1,1,1,1,1,1,1,1};
                        double[] tempcoff = new double[]{1,1,1,1,1,1,1,1};
                        double tempMeasure = Double.POSITIVE_INFINITY;
                        for (int i = 0; i < 10; i++) {
                            ArrayList x = new ArrayList();
                            ArrayList y = new ArrayList();
                            x.clear(); y.clear();
                            double[][][][][][] e1 = DynamicDDAMregression.simDDAM(utilisation[l], numbeOfMachines[k], lowers[j], dists[t], "",true,false,1,100000,320000,x,y,coff); //100000,320000
                            //double[] tempCoff = coff.clone();

                            //if (calDiffPercentage(tempCoff,coff)<=0.01) break;
                            writeArray(coff); System.out.println(mean(e1[0][0][0][0][2]) + " <== old ");
                            if (tempMeasure > mean(e1[0][0][0][0][2])) {
                                tempMeasure = mean(e1[0][0][0][0][2]);
                                System.arraycopy(coff, 0, tempcoff, 0, coff.length);
                            }
                            else {
                                System.arraycopy(tempcoff, 0, coff, 0, coff.length);
                                break;
                            }
                            coff = doRegression(x, y);
                        }
                        ArrayList x = new ArrayList();
                        ArrayList y = new ArrayList();
                        double[][][][][][] e1 = DynamicDDAMregression.simDDAM(utilisation[l], numbeOfMachines[k], lowers[j], dists[t],"",true,false,detailedTest[t][j][k][l].length,1000,5000,x,y,coff);
                        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                        detailedTest[t][j][k][l] = e1[0][0][0][0][2];
                        System.out.println("");
                        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                    }
                }
            }
        }
        System.out.println("========================== all results ===========================");
        for (int i = 0; i < dists.length; i++) {
            for (int j = 0; j < lowers.length; j++) {
                for (int k = 0; k < numbeOfMachines.length; k++) {
                    for (int l = 0; l < utilisation.length; l++) {
                        for (int m = 0; m < 30; m++) {
                            System.out.print(detailedTest[i][j][k][l][m]+",");
                        }
                        System.out.println("");
                    }
                }
            }
        }
    }

    public static double calDiffPercentage(double[] a, double[] b){
        double e = 0;
        for (int i = 0; i < b.length; i++) {
            e += Math.abs((a[i]-b[i])/max(a[i],b[i]));
        }
        return e/(double)b.length;
    }
    public static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    public static double[] doRegression(ArrayList x, ArrayList<Double> y) {
        int nCoff = ((double[]) x.get(0)).length;
        double[][] xx = new double[(int)(0.05*x.size())][nCoff];
        double[] yy = new double[(int)(0.05*x.size())];
        long[] sampleN = new long[(int)(0.05*x.size())];
        cern.jet.random.engine.RandomEngine rand = new cern.jet.random.engine.MersenneTwister(131); ; 
        cern.jet.random.sampling.RandomSampler.sample(sampleN.length, x.size(), sampleN.length, 0, sampleN, 0, rand);
        for (int i = 0; i < xx.length; i++) {
            System.arraycopy((double[]) x.get((int)sampleN[i]), 0, xx[i], 0, nCoff);
            yy[i] = y.get((int)sampleN[i]);
        }
        System.out.println("==============================================");
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);
        regression.newSampleData(yy, xx);
        return regression.estimateRegressionParameters();
    }

    private static void getStatistic_case(double[][][][][][][] e) throws IllegalArgumentException, MathException{
        for (int i = 0; i < e.length; i++) {
            String stat = DDAM[i];
            stat += " & " + getQuickStat(e[i][0][0][0][0][0]);
            stat += " & " + getQuickStat(e[i][0][0][0][0][5]);
            stat += " & " + getQuickStat(e[i][0][0][0][0][1]);
            stat += " & " + getQuickStat(e[i][0][0][0][0][3]);
            stat += " & " + getQuickStat(e[i][0][0][0][0][7]);
            stat += " & " + getQuickStat(e[i][0][0][0][0][6]);
            System.out.println(stat + "\\\\");
        }
    }
    
    private static void getStatistic(double[][][][][][][] e) throws IllegalArgumentException, MathException{
        for (int a = 0; a < dists.length; a++) {
            for (int k = 0; k < numbeOfMachines.length; k++) {
                String rowInfo = "\\multicolumn{1}{c}{$\\mathtt{"+NumbeOfMachines[k]+"}$}";
                String skip = "\\multicolumn{1}{c}{}  &";
                for (int j = 0; j < lowers.length; j++) {
                    for (int l = 0; l < utilisation.length; l++) {
                        String test = "$\\mathtt{";
                        test+= formatter.format(mean(e[0][a][j][k][l][0]));
                        String u_stat = "";
                        String l_stat = "";
                        for (int i = 1; i < e.length; i++) {
                            double p = TestUtils.tTest(e[0][a][j][k][l][0],e[i][a][j][k][l][0]);
                            if (p<=0.05){
                                if (mean(e[0][a][j][k][l][0])>mean(e[i][a][j][k][l][0])){
                                    l_stat+= _case[i];
                                }
                            } else {
                                u_stat+= _case[i];
                            }
                        }
                        test+="^{"+u_stat+"}_{"+l_stat+"}}$";
                        rowInfo+="& " + test;
                    }
                }
                if (k < numbeOfMachines.length-1) System.out.println(rowInfo + "\\\\" + "\n" +skip);
                else System.out.println(rowInfo + "\\\\" + "\\hline");
            }
        }        
    }
    
    private static void getStatistic(double[][][][][][] e1,double[][][][][][] e2) throws IllegalArgumentException, MathException{
        for (int a = 0; a < dists.length; a++) {
            for (int k = 0; k < numbeOfMachines.length; k++) {
                String rowInfo = "\\multicolumn{1}{c}{$\\mathtt{"+NumbeOfMachines[k]+"}$}";
                String skip = "\\multicolumn{1}{c}{}  &";
                for (int j = 0; j < lowers.length; j++) {
                    for (int l = 0; l < utilisation.length; l++) {
                        double p = TestUtils.tTest(e1[a][j][k][l][0],e2[a][j][k][l][0]);
                        String test = "";
                        if (p/2.0>0.05) test+="& -";
                        else { 
                            //test+="*";
                            if (mean(e1[a][j][k][l][0])<mean(e2[a][j][k][l][0])){
                                test+="& $\\bullet$";
                            } else {test+="& $\\circ$";}
                        }
                        rowInfo+=test;
                        //System.out.println(Dists[a] + " "
                        //            + Lowers[j] + " " + NumbeOfMachines[k]+ " " + Utilisation[l]
                        //            + " " + test);
                        
                    }
                }
                if (k < numbeOfMachines.length-1) System.out.println(rowInfo + "\\\\" + "\n" +skip);
                else System.out.println(rowInfo + "\\\\" + "\\hline");
            }
        }        
    }
    private static double mean(double[] val){
        double mean =0;
        for (int i = 0; i < val.length; i++) {
            mean+=val[i];
        }
        return mean/(double)val.length;
    }
    private static double mean(double[] val1,double[] val2){
        double diff_mean =0;
        for (int i = 0; i < val1.length; i++) {
            diff_mean+=val1[i]-val2[i];
        }
        return diff_mean/(double)val1.length;
    }
    private static String getQuickStat(double[] val){
        String stat = "";
        double sum  = 0;
        double sumS = 0;
        double count = val.length;
        for (int i = 0; i < val.length; i++) {
            sum +=val[i];
            sumS+=val[i]*val[i];
        }
        double var = sumS/count-Math.pow(sum/count, 2);
        return "$\\mathtt{" + formatter.format(sum/count)+" \\pm "+formatter.format(Math.sqrt(var)) + "}$";
    }
    public static void writeArray(double[] a){
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println("");
    }
}
