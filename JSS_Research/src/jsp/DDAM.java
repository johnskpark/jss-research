/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsp;

import DDAMrules.DynamicDDAM;
import DDAMrules.DynamicDDAMTest;
import DDAMrules.DynamicDDAM_ODDAM_bestFCFS;
import DDAMrules.DynamicDDAM_ADDAM_bestFCFS;
import DDAMrules.Journal_DynamicDDAM_ADDAM_bestFCFS;
import DDAMrules.Journal_DynamicDDAM_ODDAM_bestFCFS;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TestUtils;

/**
 *
 * @author nguyensu
 */
public class DDAM {
    public static double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
    public static int[] numbeOfMachines = {4,5,6,10,20};
    public static String[] lowers = {"miss","full"};
    public static String[] dists = {"expo","erlang2","uniform"};
    public static double[] Utilisation = {0.6,0.7,0.8,0.9,0.95};
    public static int[] NumbeOfMachines = {4,5,6,10,20};
    public static String[] Lowers = {"missing","full"};
    public static String[] Dists = {"Exponential","Erlang-2","Uniform"};
    public static String[] _case = {"-","a","b","c","d"};
    
    public static String[] DDAM = {"$p^{\\mathtt{ADDAM}}$", "$p^{\\mathtt{ODDAM}}$",
        "$\\mathtt{DTWK}$", "$\\mathtt{DPPW}$", "$\\mathtt{ADRES}$"};
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, IllegalArgumentException, MathException {
        //double[][][][][][] Test = DynamicDDAMTest.simDDAM("ADRES",false,true,1);
        //double[][][][][][] e1 = DynamicDDAM_ODDAM_bestFCFS.simDDAM(false,true,30);
        //double[][][][][][] e2 = DynamicDDAM_ADDAM_bestFCFS.simDDAM(false,true,30);
        double[][][][][][] e1 = Journal_DynamicDDAM_ADDAM_bestFCFS.simDDAM(false,true,30);
        double[][][][][][] e2 = Journal_DynamicDDAM_ODDAM_bestFCFS.simDDAM(false,true,30);
        double[][][][][][] e3 = DynamicDDAM.simDDAM("DTWK",false,true,30);
        double[][][][][][] e4 = DynamicDDAM.simDDAM("DPPW",false,true,30);
        double[][][][][][] e5 = DynamicDDAM.simDDAM("ADRES",false,true,30);
        double[][][][][][][] aggregateE = new double[5][][][][][][];
        aggregateE[0]=e1; aggregateE[1]=e2; aggregateE[2]=e3; aggregateE[3]=e4; aggregateE[4]=e5;
        System.out.println("");
        getStatistic_case(aggregateE);
        //aggregateE[0]=e1; aggregateE[1]=e3; aggregateE[2]=e4; aggregateE[3]=e5;
        //getStatistic(aggregateE);
        //getStatistic(e1,e2);
        System.out.println("==============================================");
    }
//0.9 0.13006157128609253
//0.9 0.13006157128610035
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
    
    public static void getStatistic(double[][][][][][][] e) throws IllegalArgumentException, MathException{
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
}
