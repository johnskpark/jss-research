/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;

import ec.EvolutionState;
import ec.app.QCSP.Core.IndexMaxPQ;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author nguyensu
 */
public final class OASByte {
    public int n;
    public int L;
    double[] r,p,d,d_,e,w,si;
    double[][] s;
    double p_;
    double[] s_;
    Random rnd;
    public OASByte(int seed, int iN, int iTao, int iR, int inst) throws IOException{
        rnd = new Random(seed);
        readBenchmark("/"+iN+"orders/Tao"+iTao+"/R"+iR+"/Dataslack_"+iN+"orders_Tao"+iTao+"R"+iR+"_"+inst+".txt");
        bestRevenue = Double.NEGATIVE_INFINITY;
        L = 3*n;
    }
    public static void main(String[] args) throws IOException {
        int N = 100; int Tao = 1; int R = 1; int instance = 1;
        OASByte oas = new OASByte(999,N,Tao,R,instance);
        //oas.readBenchmark("/"+N+"orders/Tao"+Tao+"/R"+R+"/Dataslack_"+N+"orders_Tao"+Tao+"R"+R+"_"+instance+".txt");
        //double best = oas.ILS(new byte[0]);
        double best = oas.GAsubroutine(500, 100, true);
        System.out.println("Done!!! with best objective = " + best);
    }

    int getHighestIndexActiveJob(List<Integer> remain, double[] priority, double[] r, double ec, double er) {
        int index = -1;
        double pr = Double.NEGATIVE_INFINITY;
        for (int i: remain) {
            if (pr < priority[i]  && r[i] <= er + 1*(ec - er)) {
                pr = priority[i];
                index = i;
            }
        }
        return index;
    }
    double bestRevenue = Double.NEGATIVE_INFINITY;
    public double localsearch(double obj, byte[] sol){
        double best = obj;
        int max = 100;
        for (int i = 0; i< sol.length; i++) {
            int a = i;
            flip(a,sol);
            double tr = generateScheduleBinActive(sol,false);
            if (best < tr) {
                best = tr;
                i = 0;
            } else flip(a,sol);
        }
        //double tr = generateScheduleBinActive(sol,false);
        return best;
    }
    public void flip(int a, byte[] sol){
        sol[a] = (byte) (1 - sol[a]);
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
        r = new double[n]; p = new double[n]; d = new double[n]; d_ = new double[n]; e = new double[n]; w = new double[n]; s = new double[n][n]; si = new double[n];
        str.nextToken();
        for (int i = 0; i < n; i++) {
            r[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            p[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        str.nextToken();
        for (int i = 0; i < n; i++) {
            d[i] = Integer.parseInt(str.nextToken());
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
        s_ = new double[n];
        for (int i = 0; i < n; i++) {
            si[i] = Double.parseDouble(str.nextToken());
            s_[i] += si[i];
        }
        for (int i = 0; i < n; i++) {
            line = _buffread.readLine(); str= new StringTokenizer(line,",");
            str.nextToken();
            for (int j = 0; j < n; j++) {
                s[i][j] = Double.parseDouble(str.nextToken());
                s_[j] += s[i][j];
            }
        }
        for (int i = 0; i < n; i++) {
            s_[i] = s_[i]/(double) n;
        }
        return 1;
    }
    public byte[] getSolution(){
        byte[] a = new byte[L];
        for (int i = 0; i < a.length; i++) {
            if(rnd.nextDouble()>0.5)          // Returns values in [0..1]
                a[i] = 1;
            else
                a[i] = 0;
        }
        return a;
    }
    public static int nInit;
    public double[][] initPop;
    double GAsubroutine(int popsize, int maxGEN, boolean ls){
        System.out.println("=================================================================================================");
        int N = popsize;
        int maxGen = maxGEN;
        byte[][] indi = new byte[N][L];
        double[] fitness = new double[N];
        double cross = 0.99;
        double mute  = 0.10;
        double copy  = 0.00;
        double bestOBJ = Integer.MIN_VALUE;
        bestOBJ = evaluate(true, new boolean[N], ls, indi, fitness, bestOBJ);
        for (int gen = 0; gen < maxGen; gen++) {
            boolean[] eval = reproduce(cross, mute, copy, 3, fitness, indi);
            bestOBJ = evaluate(false, eval, ls, indi, fitness, bestOBJ);
            System.out.println(average(fitness) + "-" + bestOBJ + "");
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
    double evaluate(boolean first, boolean[] eval, boolean ls, byte[][] indi, final double[] fitness, double best){
        for (int i = 0; i < indi.length; i++) {
            if (!eval[i]){
                if (first) {
                    indi[i] = getSolution();
                }
                fitness[i] = generateScheduleBinActive(indi[i], true);
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
    boolean[] reproduce(double cross, double mute, double copy, int toursize, double[] fitness, byte[][] indi){
        boolean[] eval = new boolean[fitness.length];
        int popcount = 0;
        byte[][] newpop = new byte[fitness.length][L];
        while (popcount<fitness.length){
            double rn = rnd.nextDouble();
            if (rn<cross) {
                int p1 = select(toursize, fitness);
                int p2 = select(toursize, fitness);
                while (p2==p1) {
                    p2 = select(toursize, fitness);
                }
                byte[] new1 = new byte[L];
                System.arraycopy(indi[p1], 0, new1, 0, L);
                byte[] new2 = new byte[L];
                System.arraycopy(indi[p2], 0, new2, 0, L);
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
                byte[] newi = new byte[L];
                System.arraycopy(indi[pi], 0, newi, 0, L);
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
    void crossover(byte[] new1, byte[] new2, byte[] p1, byte[] p2){
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
    void mutate(byte[] pi){
        double p_mutate = 1 / (double) pi.length;
        for (int i = 0; i < pi.length; i++) {
            if (rnd.nextDouble() < p_mutate) pi[i] = (byte) (1 - pi[i]);
        }
    }
    public static double[][][][] upperBound = new double[6][5][5][10];
    public static double[][][][] Tabu = new double[6][5][5][10];
    public void getRefSolution() throws IOException{
        InputStream _read = getClass().getResourceAsStream("/RefSol.txt");
        //BufferedReader buffread = new BufferedReader(read);
        InputStreamReader _inputFileReader = new InputStreamReader(_read);
        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
        String line = _buffread.readLine(); _buffread.readLine();
        for (int i = 0; i < upperBound.length; i++) {
            for (int j = 0; j < upperBound[i].length; j++) {
                for (int k = 0; k < upperBound[i][j].length; k++) {
                    _buffread.readLine();
                    for (int l = 0; l < upperBound[i][j][k].length; l++) {
                        line = _buffread.readLine();
                        StringTokenizer str= new StringTokenizer(line,"\t ");
                        str.nextToken();
                        upperBound[i][j][k][l] = Double.parseDouble(str.nextToken());
                        Tabu[i][j][k][l] = Double.parseDouble(str.nextToken());
                    }
                }
            }
        }
        StringTokenizer str= new StringTokenizer(line,",");
    }
    public static double getTabu(int NN, int Tao, int R, int Ins){
        int n = 0,t = 0,r = 0;
        if (NN == 10) n = 0; if (NN == 15) n = 1; if (NN == 20) n = 2; if (NN == 25) n = 3; if (NN == 50) n = 4; if (NN == 100) n = 5;
        if (Tao == 1) t = 0; if (Tao == 3) t = 1; if (Tao == 5) t = 2; if (Tao == 7) t = 3; if (Tao == 9) t = 4;
        if (R == 1) r = 0; if (R == 3) r = 1; if (R == 5) r = 2; if (R == 7) r = 3; if (R == 9) r = 4;
        return Tabu[n][t][r][Ins-1];
    }
    public static double getUB(int NN, int Tao, int R, int Ins){
        int n = 0,t = 0,r = 0;
        if (NN == 10) n = 0; if (NN == 15) n = 1; if (NN == 20) n = 2; if (NN == 25) n = 3; if (NN == 50) n = 4; if (NN == 100) n = 5;
        if (Tao == 1) t = 0; if (Tao == 3) t = 1; if (Tao == 5) t = 2; if (Tao == 7) t = 3; if (Tao == 9) t = 4;
        if (R == 1) r = 0; if (R == 3) r = 1; if (R == 5) r = 2; if (R == 7) r = 3; if (R == 9) r = 4;
        return upperBound[n][t][r][Ins-1];
    }
    public double generateScheduleBinActive(byte[] ind, boolean first){
        int step = ind.length/n;
        double ready = 0;
        double revenue = 0;
        double tardy = 0;
        double fmax = 0;
        double tmax = 0;
        double mae = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        double[] priority = new double[n];
        for (int i = 0; i < priority.length;i++) {
            if (ind[i*step]==1) {
                remain.add(i);
                int count = step - 2;
                for (int j = i*step + 1; j < i*step + step; j++) {
                    priority[i] += ind[j]*Math.pow(2, count);
                    count--;
                }
            }
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
                }
                ec = Math.min(ec, c);
                er = Math.min(Math.max(r[j], ready), c);
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
            revenue += e[i] - w[i]*maxPlus(ready-d[i]);
            prev = i;
            mae += Math.abs((ready - d[i]));
            tardy += maxPlus(ready-d[i]);
            if (tmax < maxPlus(ready-d[i])) tmax = maxPlus(ready-d[i]);
            remain.remove((Integer)i);
            acc.add(i);
            count++;
        }
        //if (first) revenue = localsearch(revenue, ind);
        if (bestRevenue < revenue) bestRevenue = revenue;
        return revenue ;
    }
    public double maxPlus (double x) {
        if (x>0) return x;
        return 0;
    }
    public double ILS(byte[] initSol){
        int max = 1000;
        byte[] bestSol = new byte[L];
        byte[] sol = new byte[L];
        byte[] homeSol = new byte[L];
        if (initSol.length == 0) {
            sol = getSolution();
        }
        else sol = initSol;
        double obj = generateScheduleBinActive(sol, false);
        double homeObj = obj; double bestObj = obj;
        System.arraycopy(sol, 0, sol, 0, bestSol.length);
        System.arraycopy(sol, 0, sol, 0, homeSol.length);
        //System.out.println(sol.obj);
        while (max-->0) {
            //sol.obj = localsearch3(sol,sol.obj);
            obj = localsearch(obj,sol);
            if (obj > bestObj) {
                System.arraycopy(sol, 0, bestSol, 0, bestSol.length);
                bestObj = obj;
            }
            if (obj >= homeObj) {
                System.arraycopy(sol, 0, homeSol, 0, homeSol.length);
                homeObj = obj;
            } else {
                //if (rnd.nextBoolean()) homeSol = new OASsol(sol);
            }
            System.arraycopy(homeSol, 0, sol, 0, bestSol.length);
            obj = kick(sol,1);
            System.out.println(bestObj);
        }
        return bestObj;
    }
    double kick(byte[] sol, int mag) {
        for (int i = 0; i < mag; i++) {
            int a = rnd.nextInt(n);
            sol[a*sol.length/n] -= 1;
        }
        return generateScheduleBinActive(sol,false);
    }
}
