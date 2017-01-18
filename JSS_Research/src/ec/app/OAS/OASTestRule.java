/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;

import SmallStatistics.SmallStatistics;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nguyensu
 */
public class OASTestRule extends OASUniform {
    public static void main(String[] args) throws IOException {
        int[] tNN = {100,50,25}; int[] tTTao = {1,3,5,7,9}; int[] tRR = {1,3,5,7,9}; int[] tINS = {1,2,3,4,5,6,7,8,9,10};
        //int[] tNN = {100}; int[] tTTao = {9}; int[] tRR = {9}; int[] tINS = {1};
        double[][][][] tr = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] tr_m = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] tr_i = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] tr_r = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        for (int k = 0; k < tNN.length; k++) {
            for (int l = 0; l < tTTao.length; l++) {
                for (int m = 0; m < tRR.length; m++) {
                    for (int n = 0; n < tINS.length; n++) {
                        OASTestRule oas = new OASTestRule(111,tNN[k],tTTao[l],tRR[m],tINS[n]);
                        tr[k][l][m][n] = oas.testruleMul(00,0);
                        tr_r[k][l][m][n] = oas.testruleMul(00,3);
                        tr_m[k][l][m][n] = oas.testruleMul(00,1);
                        tr_i[k][l][m][n] = oas.testruleMul(100,2);
                    }
                }
            }
        }
        double[][][][] errorRule = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] errorRule_r = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] errorRule_m = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] errorRule_i = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        double[][][][] errorts = new double[tNN.length][tTTao.length][tRR.length][tINS.length];
        SmallStatistics [][][] TRruleSET = new SmallStatistics[tNN.length][tTTao.length][tRR.length];
        SmallStatistics [][][] TRruleSET_r = new SmallStatistics[tNN.length][tTTao.length][tRR.length];
        SmallStatistics [][][] TRruleSET_m = new SmallStatistics[tNN.length][tTTao.length][tRR.length];
        SmallStatistics [][][] TRruleSET_i = new SmallStatistics[tNN.length][tTTao.length][tRR.length];
        SmallStatistics [][][] TRtsSET = new SmallStatistics[tNN.length][tTTao.length][tRR.length];
        OASTestRule oas = new OASTestRule(111,10,1,1,1);
        oas.getRefSolution();
        for (int k = 0; k < tNN.length; k++) {
            for (int l = 0; l < tTTao.length; l++) {
                for (int m = 0; m < tRR.length; m++) {
                    TRruleSET[k][l][m] = new SmallStatistics();
                    TRruleSET_r[k][l][m] = new SmallStatistics();
                    TRruleSET_m[k][l][m] = new SmallStatistics();
                    TRruleSET_i[k][l][m] = new SmallStatistics();
                    TRtsSET[k][l][m] = new SmallStatistics();
                    for (int n = 0; n < tINS.length; n++) {
                        errorRule[k][l][m][n] = 100*(- tr[k][l][m][n] + OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1))/OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1);
                        TRruleSET[k][l][m].add(errorRule[k][l][m][n]);
                        errorRule_r[k][l][m][n] = 100*(- tr_r[k][l][m][n] + OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1))/OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1);
                        TRruleSET_r[k][l][m].add(errorRule_r[k][l][m][n]);
                        errorRule_m[k][l][m][n] = 100*(- tr_m[k][l][m][n] + OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1))/OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1);
                        TRruleSET_m[k][l][m].add(errorRule_m[k][l][m][n]);
                        errorRule_i[k][l][m][n] = 100*(- tr_i[k][l][m][n] + OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1))/OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1);
                        TRruleSET_i[k][l][m].add(errorRule_i[k][l][m][n]);
                        errorts[k][l][m][n] = 100*(- OASTestRule.getTabu(tNN[k], tTTao[l], tRR[m], n+1) + OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1))/OASTestRule.getUB(tNN[k], tTTao[l], tRR[m], n+1);
                        TRtsSET[k][l][m].add(errorts[k][l][m][n]);
                    }
                }
            }
        }
        double[] Tau = {0.1,0.3,0.5,0.7,0.9}; double[] R = {0.1,0.3,0.5,0.7,0.9};
        for (int k = 0; k < tNN.length; k++) {
            for (int l = 0; l < tTTao.length; l++) {
                for (int m = 0; m < tRR.length; m++) {
                    String prefix = "";
                    if (m==0) prefix = ""+ m(Tau[l]);
                    String report = prefix + " & " +  m(R[m]) + " & ";
                    //report += " & " + m2(minMILP[minMILP.length-k-1][l][m]) + " & " + m2(avgMILP[minMILP.length-k-1][l][m]) + " & " + m2(maxMILP[minMILP.length-k-1][l][m]) + " & ";
                    report += " & " + m2(minmATCS[minMILP.length-k-1][l][m]) + " & " + m2(avgmATCS[minMILP.length-k-1][l][m]) + " & " + m2(maxmATCS[minMILP.length-k-1][l][m]) + " & ";
                    //report += " & " + m2(minISFAN[minMILP.length-k-1][l][m]) + " & " + m2(avgISFAN[minMILP.length-k-1][l][m]) + " & " + m2(maxISFAN[minMILP.length-k-1][l][m]) + " & ";
                    //report += " & " + m2(TRtsSET[k][l][m].getMin()) + " & " + m2(TRtsSET[k][l][m].getAverage()) + " & " + m2(TRtsSET[k][l][m].getMax()) + " & ";
                    report += " & " + m2(TRruleSET_r[k][l][m].getMin()) + " & " + m2(TRruleSET_r[k][l][m].getAverage()) + " & " + m2(TRruleSET_r[k][l][m].getMax()) + " & ";
                    report += " & " + m2(TRruleSET[k][l][m].getMin()) + " & " + m2(TRruleSET[k][l][m].getAverage()) + " & " + m2(TRruleSET[k][l][m].getMax()) + " & ";
                    report += " & " + m2(TRruleSET_m[k][l][m].getMin()) + " & " + m2(TRruleSET_m[k][l][m].getAverage()) + " & " + m2(TRruleSET_m[k][l][m].getMax()) + " & ";
                    report += " & " + m2(TRruleSET_i[k][l][m].getMin()) + " & " + m2(TRruleSET_i[k][l][m].getAverage()) + " & " + m2(TRruleSET_i[k][l][m].getMax()) + " & " + m2(0.0) + " \\\\ ";
                    //report +=  " & & " + m2(0.0) + " & " + m2(timeTS[tNN.length-k-1][l][m]) + " \\\\ ";
                    System.out.println(report);
                }
                System.out.println("\\hline\\noalign{\\smallskip}");
            }
            System.out.println("");
            System.out.println("********************************************");
            System.out.println("");
        }
        System.out.println("ALL DONE!!!");
    }
    public double testruleMul(int maxstep, int rule){
        double best = testrule(rule);
        while (maxstep-->0){
            double obj = testrule(rule);
            if (obj > best) {
                best = obj;
            }
        }
        return best;
    }
    public double testrule(int rule){
        double[] obj = new double[2];
        double ready = 0;
        double revenue = 0;
        double mae = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        priority = new double[n];
        for (int i = 0; i < priority.length;i++) {
            remain.add(i);
        }
        int count = 0;
        while (!remain.isEmpty()){
            // get earliest completion time
            double ec = Double.POSITIVE_INFINITY;
            double er = Double.POSITIVE_INFINITY;
            List<Integer> late = new ArrayList<Integer>();
            for (int j:remain) {
                double setup = si[j];
                if (prev != -1) setup = s[prev][j];
                double c = Math.max(r[j], ready) + setup + p[j];
                if (c > d_[j]) {
                    late.add(j);
                    continue;
                } else {
                    ec = Math.min(ec, c);
                    er = Math.min(Math.max(r[j], ready), c);
                    double mag = 0.0;                    
                    if (rule == 2) mag = 0.2;
                    double noise = 1 - mag + rnd.nextDouble()*2*mag;
                    double d = due[j]; double t = ready; double S = setup; double P = p[j]; double R = r[j]; double E = e[j]*noise; double D = d_[j]; double W = w[j];
                    if (rule == 0) priority[j] = div((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) , (IF(div((0.16284561 - W) , (P * S)) * ((S + P) - div(d , (P * R))), P, P / 0.11434984) - ((t - d) * (0.79690695 * S) + S + P)));
                    //priority[j] = ((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) / (P / 0.11434984 - ((t - d) * (0.79690695 * S) + S + P)));
                    if (t-d>=0){
                        System.out.println("ac ac ac");
                    }
                    if (rule == 1 || rule == 2) {
                        double a = div((0.03508053 *P*P*S) , ((t - d) * t)) + E / D; //a = div((0.03508053 *P*P*S) , ((t - d) * t)) + E / D;
                        double b = 7.745092*P-S*(1+0.79690695*(t - d)); //b = 7.745092*P-S*(1+0.79690695*(t-d));
                        priority[j] = a/b;
                    }
                    if (rule == 3) {
                        priority[j] = rlr1[j];
                    }
                    //System.out.println(P + " " + E + " " + d + " " + (t-d) + " " + priority[j]);
                }
            }
            for (int j:late) {
                remain.remove((Integer)j);
            }
            if (remain.isEmpty()) break;
            int i = getHighestIndexActiveJob(remain, priority, r, ec, er);
            if (r[i]>ready) ready = r[i];
            if (prev == -1) ready += si[i];
            else ready += s[prev][i];
            ready += p[i];
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            mae += Math.abs((ready - due[i]));
            prev = i;
            remain.remove((Integer)i);
            acc.add(i);
            count++;
        }
        priority = new double[n + 1];
        double[] priRand = new double[acc.size()];
        for (int i = 0; i < acc.size();i++) {
            priRand[i] = 0.5 + 0.5* rnd.nextDouble();
        }
        Arrays.sort(priRand);
        for (int i = 0; i < acc.size(); i++) {
            priority[acc.get(i)] = priRand[acc.size() - i - 1];
        }
        for (int i = 0; i < n; i++) {
            if (priority[i]==0) priority[i] = 0.5 * rnd.nextDouble();
        }
        priority[n] = 0.5;
        if (bestRevenue < revenue) bestRevenue = revenue;
        obj[0] = revenue;
        obj[1] = mae/(double)count;
        return revenue;
    }
    private OASTestRule(int i, int N, int Tao, int R, int instance) throws IOException {
        super(i,N,Tao,R,instance);
    }
    public static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    public static double min(double a, double b){
        if (a<b) return a;
        else return b;
    }
    public static double div(double a, double b){
        if (b!=0)
            return a/b;
        else
            return 1;
    }
    public static double IF(double a, double b, double c){
        if (a>=0){
            return b;
        }else{
            return c;
        }
    }
    static NumberFormat ft = new DecimalFormat("#0.#");
    static NumberFormat ft2 = new DecimalFormat("#0");
    static String m2(double x){
        double left = x - (int)(x);
        if (left>=0.5) return "$"+ (int)(Math.ceil(x)) + "$";
        else return "$"+ (int)(x) + "$";
    }
    static String m(double x){
        return "$"+ ft.format(x) + "$";
    }
    static double[][][] minMILP = {{{8,6,5,1,1},{9,7,2,1,11},{6,4,3,2,6},{2,8,7,2,0},{0,0,0,0,0}}     ,       {{9,7,11,9,2},{13,18,18,13,13},{29,25,17,10,8},{13,11,13,9,14},{8,11,5,9,14}}     ,      {{37,42,43,46,38},{52,52,56,63,47},{71,47,56,55,48},{49,45,46,42,39},{31,28,23,27,27}}};
    static double[][][] avgMILP = {{{12,13,7,5,4},{15,11,5,5,18},{10,9,10,8,20},{10,12,13,9,10},{0,0,3,7,6}}     ,      {{24,16,17,13,10},{24,28,24,20,21},{28,42,31,21,18},{21,18,29,21,19},{14,16,15,17,21}}     ,      {{44,51,51,54,60},{62,64,66,73,66},{77,61,70,71,66},{67,57,55,60,53},{37,40,38,38,35}}};
    static double[][][] maxMILP = {{{19,23,9,11,10},{24,20,8,9,27},{17,13,17,13,42},{19,17,21,15,17},{0,0,12,22,21}}     ,      {{81,22,25,18,27},{33,48,33,26,34},{31,86,44,38,22},{25,23,52,28,26},{18,22,21,25,30}}     ,      {{56,88,60,60,70},{68,75,76,84,82},{87,86,88,92,100},{86,66,65,76,64},{48,50,54,48,39}}};
    static double[][][] minmATCS = {{{9,6,3,5,2},{13,7,10,8,6},{12,11,8,17,16},{12,18,26,15,8},{4,10,14,17,20}}     ,      {{9,9,6,7,1},{11,11,9,5,3},{13,18,15,12,17},{17,16,23,24,19},{17,21,33,26,24}}     ,      {{9,11,11,5,0},{15,15,14,15,10},{17,17,20,10,15},{17,18,20,22,23},{18,25,25,26,28}}};
    static double[][][] avgmATCS = {{{14,16,12,13,8},{19,18,18,17,16},{19,21,20,26,28},{20,26,33,29,29},{12,17,27,29,30}},    {{15,15,13,14,4},{16,20,18,14,17},{20,25,25,24,27},{22,24,33,31,29},{24,32,39,37,36}},    {{15,17,14,11,6},{19,18,18,20,15},{22,23,24,23,24},{22,24,26,31,31},{22,31,33,36,35}}};
    static double[][][] maxmATCS = {{{20,32,21,23,24},{29,24,26,32,27},{29,29,30,38,49},{26,33,43,40,53},{20,22,45,45,58}},    {{20,18,19,24,10},{24,30,33,24,26},{27,30,34,31,35},{30,33,46,35,41},{30,42,45,46,44}},    {{19,20,19,16,11},{24,23,21,32,18},{24,28,31,34,36},{28,34,37,38,37},{25,36,39,40,40}}};
    static double[][][] minISFAN = {{{4,3,3,2,1},{5,7,5,6,5},{7,6,8,7,6},{10,9,13,9,10},{0,1,1,0,2}},    {{6,7,5,4,4},{8,9,8,7,8},{10,11,7,9,6},{12,11,14,12,17},{12,18,18,17,16}},    {{8,7,6,6,8},{10,11,10,12,9},{12,12,14,13,12},{13,13,14,16,15},{14,16,19,15,13}}};
    static double[][][] avgISFAN = {{{8,7,7,5,6},{9,10,7,11,9},{11,11,13,13,15},{18,17,19,15,17},{8,5,12,13,13}},    {{8,9,8,9,7},{11,12,11,11,9},{14,16,16,15,15},{16,16,19,19,21},{17,23,21,27,22}},    {{9,9,9,9,12},{12,13,14,14,13},{16,15,17,17,18},{17,17,18,19,18},{17,20,21,21,21}}};
    static double[][][] maxISFAN = {{{12,11,11,9,10},{12,12,10,24,18},{14,17,20,17,25},{27,23,26,21,24},{25,8,44,31,32}},    {{12,12,10,22,11},{13,15,15,18,11},{19,20,21,18,19},{21,20,22,29,28},{22,28,26,72,29}},    {{13,10,11,12,16},{13,17,17,15,17},{18,18,19,21,24},{19,21,24,23,24},{20,26,25,24,28}}};
    static double[][][] timeTS = {{{0.08,0.06,0.06,0.06,0.05},{0.1,0.09,0.08,0.08,0.07},{0.07,0.08,0.09,0.08,0.08},{0.06,0.08,0.08,0.07,0.08},{0.06,0.06,0.07,0.08,0.09}},    {{1.19,0.98,0.89,0.58,0.46},{1.61,1.49,1.16,0.85,0.68},{1.36,1.18,1.35,1.22,1.16},{1.44,1.52,1.59,1.27,1.15},{1.25,1.26,1.42,1.43,1.37}},    {{16.76,17.26,10.87,6.21,3.53},{22.37,20.10,16.77,9.48,7.58},{25.96,28.87,20.56,15.57,12.15},{33.6,26.62,22.30,26.36,17.84},{29.46,26.32,21.51,22.70,17.48}}};
}
//simplified 1: priority[j] = div((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) , (IF(div((0.16284561 - W) , (P * S)) * ((S + P) - div(d , (P * R))), P, P / 0.11434984) - ((t - d) * (0.79690695 * S) + S + P)));
//simplified 2: priority[j] = div((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) , (IF(-1 * ((S + P) - d / (P * R)), P, P / 0.11434984) - ((t - d) * (0.79690695 * S) + S + P)));