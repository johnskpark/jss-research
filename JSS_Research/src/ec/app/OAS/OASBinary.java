/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author nguyensu
 */
public class OASBinary {
    public int n;
    double[] r,p,due,d_,e,w,si;
    double[][] s;
    double p_;
    double s_;
    Random rnd;
    public int[] bestOrder; public double bestFINAL = Double.NEGATIVE_INFINITY;
    public OASBinary(int seed){
        rnd = new Random(seed);
    }
    public static void main(String[] args) throws IOException {
        int N = 100; int Tao = 5; int R = 5; int instance = 2;
        OASBinary oas = new OASBinary(5435);
        oas.readBenchmark("/"+N+"orders/Tao"+Tao+"/R"+R+"/Dataslack_"+N+"orders_Tao"+Tao+"R"+R+"_"+instance+".txt");
        double best = oas.GAsubroutine(500, 100, true);
        //best = oas.localsearch();
        System.out.println("Done!!! with best objective = " + best);
    }
    public double localsearch(){
        return localsearchRandom(1000000, bestOrder, bestFINAL);
    }
    public int[] getRandomPermutation(int size){
        int[] a = new int[size];
        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = 1+i;
        // shuffle
        for (int i = 0; i < size; i++) {
            int r = (int) (rnd.nextDouble() * (i+1));     // int between 0 and i
            int swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }
        int x = rnd.nextInt(n);
        for (int i = x; i < a.length; i++) {
            a[i] *= -1;
        }
        return a;
    }
    public double localsearchRandom(int maxStep, int[] order, double bestObj) {
        double tempBest = bestObj;
        for (int h = 0; h < maxStep; h++) {
            for (int i = 0; i < 100; i++) {
                int[] tempOrder = new int[n];
                System.arraycopy(order, 0, tempOrder, 0, order.length);
                swapSequence(tempOrder);
                double obj = generateSchedule(tempOrder);
                if (tempBest < obj) {
                    tempBest = obj;
                    System.arraycopy(tempOrder, 0, order, 0, order.length);
                }
            }
            insertRemove(order); insertAdd(order); swapSequence(order); swapAcceptance(order);
        }
        
        
        return tempBest;
    }
    public void insertAdd(int[] order){
        int split = -1;
        for (int i = 0; i < order.length; i++) {
            if (order[i] < 0) {
                split = i;
                break;
            }
        }
        if (split == -1)  {
            swapSequence(order);
            return;
        }
        int a = rnd.nextInt(split);
        int b = split + rnd.nextInt(n - split);
        int temp = - order[b];
        System.arraycopy(order, a, order, a + 1, b - a);
        order[a] = temp;
    }
    public void insertRemove(int[] order){
        int split = -1;
        for (int i = 0; i < order.length; i++) {
            if (order[i] < 0) {
                split = i;
                break;
            }
        }
        if (split == -1)  {
            swapSequence(order);
            return;
        }
        int a = rnd.nextInt(split);
        int b = split - 1;
        int temp = - order[a];
        for (int i = a; i < b; i++) {
            order[i] = order[i+1];
        }
        order[b] = temp;
    }
    public void swapSequence(int[] order){
        int a = -1, b = -1;
        do {
            a = rnd.nextInt(n);
        } while (order[a] < 0);
        do {
            b = rnd.nextInt(n);
        } while (order[b] < 0);
        int temp = order[a];
        order[a] = order[b];
        order[b] = temp;
    }
    public void swapAcceptance(int[] order){
        int a = -1, b = -1;
        do {
            a = rnd.nextInt(n);
        } while (order[a] < 0);
        do {
            b = rnd.nextInt(n);
        } while (order[b] > 0);
        int temp = -order[a];
        order[a] = -order[b];
        order[b] = temp;
    }
    int getHighestIndexActiveJob(List<Integer> remain, double[] priority, double[] r, double ec) {
        int index = -1;
        double pr = Double.NEGATIVE_INFINITY;
        for (int i: remain) {
            if (pr < priority[i] && r[i] < ec) {
                pr = priority[i];
                index = i;
            }
        }
        return index;
    }
    public double generateSchedule(boolean[] ACC){
        double ready = 0;
        double revenue = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        double[] priority = new double[n];
        for (int i = 0; i < priority.length;i++) {
            if (ACC[i])
                remain.add(i);
        }
        int count = 0;
        while (!remain.isEmpty()){
            // get earliest completion time
            double ec = Double.POSITIVE_INFINITY;
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
                    double noise = 0;
                    double d = due[j]; double t = ready; double S = setup; double P = (1 - noise + 2*noise*rnd.nextDouble())*p[j]; double R = r[j]; double E = e[j]; double D = d_[j]; double W = w[j];
                        double a = div((0.03508053 *P*P*S) , ((t - d) * t)) + E / D; //a = div((0.03508053 *P*P*S) , ((t - d) * t)) + E / D;
                        double b = 7.745092*P-S*(1+0.79690695*(t - d)); //b = 7.745092*P-S*(1+0.79690695*(t-d));
                        priority[j] = a/b;
                    //priority[j] = (((((d + d) + (0.35844433 + (d * W))) * (((d + d) + (0.35844433 + R)) * P)) / ((E * (IF(P, t, R) / S)) + (R - P))) * ((R + t) * (S - (E / (0.35844433 + (d * W)))))) * (IF(IF(R - P, d, (P / t) - (d - E)), (S / R) - ((d * d) * d), (S - (S + IF(P, E, W))) - 0.35844433) / (S + IF(P, E, W)));
                            //((d - R) / R) / ((((P + (S * d)) + (((d / W) * t) * t)) * ((d - R) + ((d - R) + (S * d)))) * (((D - t) - (P - (0.8127463 - S))) - IF((t + (S * d)) - ((d - t) + (d / W)), P, (E * R) - ((0.8127463 - S) - P))));
                }
            }
            for (int j:late) {
                remain.remove((Integer)j);
            }
            if (remain.isEmpty()) break;
            int i = getHighestIndexActiveJob(remain, priority, r, ec);

            if (r[i]>ready) ready = r[i];
            if (prev == -1) ready += si[i];
            else ready += s[prev][i];
            ready += p[i];
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            prev = i;
            remain.remove((Integer)i);
            acc.add(i);
            count++;
        }
        priority = new double[n];
        double[] priRand = new double[acc.size()];
        for (int i = 0; i < acc.size();i++) {
            priRand[i] = 0.5 + 0.5* rnd.nextDouble();
        }
        Arrays.sort(priRand);
        for (int i = 0; i < acc.size(); i++) {
            priority[acc.get(i)] = priRand[acc.size() - i - 1];
        }
        for (int i = 0; i < priority.length; i++) {
            if (priority[i]==0) priority[i] = 0.5 * rnd.nextDouble();
        }
        return revenue;
    }
    public double generateSchedule(int[] order){
        double ready = 0;
        double revenue = 0;
        int prev = -1;
        for (int o:order) {
            int i = o - 1;
            if (i<0) break;
            double setup = si[i];
            if (prev != -1) setup = s[prev][i];
            if (ready + setup + p[i] > d_[i]) {
                continue;
            }
            if (r[i]>ready) ready = r[i];
            if (prev == -1) ready += si[i];
            else ready += s[prev][i];
            ready += p[i];
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            prev = i;
        }
        return revenue;
    }
    public double maxPlus (double x) {
        if (x>0) return x;
        return 0;
    }
    public int readBenchmark(String problemFile) throws IOException {
        //problemFile = "/benchmark/"+problemFile;
        InputStream _read = getClass().getResourceAsStream(problemFile);//new FileReader(problemFile);
        //BufferedReader buffread = new BufferedReader(read);
        InputStreamReader _inputFileReader = new InputStreamReader(_read);
        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
        String line = _buffread.readLine();
        StringTokenizer str= new StringTokenizer(line,",");
        n = str.countTokens() - 2;
        r = new double[n]; p = new double[n]; due = new double[n]; d_ = new double[n]; e = new double[n]; w = new double[n]; s = new double[n][n]; si = new double[n];
        str.nextToken();
        for (int i = 0; i < n; i++) {
            r[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            p[i] = Integer.parseInt(str.nextToken());
            p_ += p[i];
        }
        p_ = p_ / (double) n;
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            due[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            d_[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            e[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            w[i] = Double.parseDouble(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        int counts = 0;
        for (int i = 0; i < n; i++) {
            si[i] = Double.parseDouble(str.nextToken());
            s_ += si[i]; counts++;
        }
        for (int i = 0; i < n; i++) {
            line = _buffread.readLine(); str= new StringTokenizer(line,",");
            str.nextToken();
            for (int j = 0; j < n; j++) {
                s[i][j] = Double.parseDouble(str.nextToken());
                s_ += s[i][j]; counts++;
            }
        }
        s_ = s_ / (double) counts;
        bestOrder = new int[n];
        return 1;
    }
    public boolean[] getSolution(int size){
        boolean[] a = new boolean[size];
        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = rnd.nextBoolean();
        return a;
    }
    double GAsubroutine(int popsize, int maxGEN, boolean ls){
        int N = popsize;
        int maxGen = maxGEN;
        boolean[][] indi = new boolean[N][n];
        double[] fitness = new double[N];
        double cross = 0.80;
        double mute  = 0.10;
        double copy  = 0.10;
        double bestOBJ = Integer.MIN_VALUE;
        bestOBJ = evaluate(true, new boolean[N], ls, indi, fitness, bestOBJ);
        for (int gen = 0; gen < maxGen; gen++) {
            boolean[] eval = reproduce(cross, mute, copy, 5, fitness, indi);
            bestOBJ = evaluate(false, eval, ls, indi, fitness, bestOBJ);
            System.out.println(average(fitness) + "(" + bestOBJ + ")");
        }
        return bestOBJ;
    }
    double average(double[] fitness){
        double avg = 0;
        for (int i = 0; i < fitness.length; i++) {
            avg += fitness[i];
        }
        return (double) avg / (double) fitness.length;
    }
    double evaluate(boolean first, boolean[] eval, boolean ls, boolean[][] indi, final double[] fitness, double best){
        for (int i = 0; i < indi.length; i++) {
            if (!eval[i]){
                if (first) {
                    indi[i] = getSolution(n);
                }
                fitness[i] = generateSchedule(indi[i]);
                //fitness[i] = localsearchRandom(100, indi[i], best);
                if (best<fitness[i]) {
                    best=fitness[i];
                }
            }
            //System.out.println(fitness[i]);
        }
        //System.exit(0);
       return best;
    }
    boolean[] reproduce(double cross, double mute, double copy, int toursize, double[] fitness, boolean[][] indi){
        boolean[] eval = new boolean[fitness.length];
        int popcount = 0;
        boolean[][] newpop = new boolean[fitness.length][n];
        while (popcount<fitness.length){
            double rn = rnd.nextDouble();
            if (rn<cross) {
                int p1 = select(toursize, fitness);
                int p2 = select(toursize, fitness);
                while (p2==p1) {
                    p2 = select(toursize, fitness);
                }
                boolean[] new1 = new boolean[n];
                System.arraycopy(indi[p1], 0, new1, 0, n);
                boolean[] new2 = new boolean[n];
                System.arraycopy(indi[p2], 0, new2, 0, n);
                crossover(new1, new2, indi[p1], indi[p2]);
                if (rnd.nextDouble()<mute) mutate(new1);
                newpop[popcount] = new1;
                popcount++;
                if (popcount == fitness.length) break;
                else {
                    if (rnd.nextDouble()<mute) mutate(new2);
                    newpop[popcount] = new2;
                    popcount++;
                }
            } else {
                int pi = select(toursize, fitness);
                boolean[] newi = new boolean[n];
                System.arraycopy(indi[pi], 0, newi, 0, n);
                if (rnd.nextDouble()<mute) mutate(newi);
                newpop[popcount] = newi; eval[popcount] = true;
                popcount++;
            }
        }
        for (int i = 0; i < newpop.length; i++) {
            System.arraycopy(newpop[i], 0, indi[i], 0, newpop[i].length);
        }
        return eval;
    }
    int select(int toursize, double[] fitness){
       int index = rnd.nextInt(fitness.length);
       double bestFit = fitness[index];
        for (int i = 0; i < toursize - 1; i++) {
            int k = rnd.nextInt(fitness.length);
            if (bestFit < fitness[k]) {
                bestFit = fitness[k];
                index = k;
            }
        }
       return index;
    }
    void crossover(boolean[] new1, boolean[] new2, boolean[] p1, boolean[] p2){
        for (int i = 0; i < p2.length; i++) {
            if (rnd.nextDouble()<0.5) new1[i] = p2[i];
            if (rnd.nextDouble()<0.5) new2[i] = p1[i];
        }
    }
    int findIndex (int key, int[] sol){
        for (int i = 0; i < sol.length; i++) {
            if (sol[i] == key) return i;
        }
        return -1;
    }
    void mutate(boolean[] pi){
        double p_mutate = 1 / (double) pi.length;
        for (int i = 0; i < pi.length; i++) {
            if (rnd.nextDouble() < p_mutate) pi[i] = rnd.nextBoolean();
        }
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
}
//double d = due[i]; double t = ready; double S = setup; double P = (1 - noise + 2*noise*rnd.nextDouble())*p[i]; double R = r[i]; double E = e[i]; double D = d_[i]; double W = w[i];
                //double prior = 0;
                //double prior = ((d - R) / R) / ((((P + (S * d)) + (((d / W) * t) * t)) * ((d - R) + ((d - R) + (S * d)))) * (((D - t) - (P - (0.8127463 - S))) - IF((t + (S * d)) - ((d - t) + (d / W)), P, (E * R) - ((0.8127463 - S) - P))));