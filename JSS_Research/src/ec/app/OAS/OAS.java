/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;

import ec.EvolutionState;
import ec.app.QCSP.Core.IndexMaxPQ;
import ec.app.QCSP.qcspData;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author nguyensu
 */
public final class OAS {
    public int n;
    double[] r,p,d,d_,e,w,si;
    double[][] s;
    double p_;
    double[] s_;
    Random rnd;
    //GP related
    GPIndividual ind;
    oasData data;
    int thread;
    EvolutionState state;
    ADFStack stack;
    GPProblem gpproblem;
    public OAS(int seed, int iN, int iTao, int iR, int inst) throws IOException{
        rnd = new Random(seed);
        readBenchmark("/"+iN+"orders/Tao"+iTao+"/R"+iR+"/Dataslack_"+iN+"orders_Tao"+iTao+"R"+iR+"_"+inst+".txt");
    }
    public static void main(String[] args) throws IOException {
        OAS oas = new OAS(111,100,5,5,1);
        //double best = oas.GAsubroutine(200, 500, true);
        double best = oas.ILS(new OASsol());
        System.out.println("Done!!! with best objective = " + best);
    }
    int getHighestIndexActiveJob(List<Integer> remain, double[] r, double ec) {
        int index = -1;
        for (int i: remain) {
            if (r[i] < ec) {
                index = i;
                break;
            }
        }
        return index;
    }
    public double ILS(OASsol initSol){
        int max = 1;
        OASsol bestSol = new OASsol();
        OASsol sol = new OASsol();
        if (initSol.obj == -1) sol = new OASsol(n, rnd);
        else sol = initSol;
        generateSchedule(sol);
        OASsol homeSol = new OASsol(sol);
        //System.out.println(sol.obj);
        while (max-->0) {
            //sol.obj = localsearch3(sol,sol.obj);
            sol.obj = localsearch1(sol,sol.obj);
            sol.obj = localsearch2(sol,sol.obj);
            if (sol.obj > bestSol.obj) bestSol = new OASsol(sol);
            if (sol.obj >= homeSol.obj) {
                homeSol = new OASsol(sol);
            } else {
                //if (rnd.nextBoolean()) homeSol = new OASsol(sol);
            }
            sol = new OASsol(homeSol);
            kick(sol,1);
            //System.out.println(bestSol.obj);
        }
        return bestSol.obj;
    }
    void kick(OASsol sol, int mag) {
        for (int i = 0; i < mag; i++) {
            int a = rnd.nextInt(n);
            sol.acc[a] = !sol.acc[a];
        }
        generateSchedule(sol);
    }
    double localsearch1(OASsol sol, double obj){
        List<Integer> accepted_order = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            if (sol.acc[sol.order[i]]) accepted_order.add(i);
        }
        double best = obj;
        while (true) {
            int bestPos = -1;
            for (int i = 0; i < accepted_order.size() - 1; i++) {
                swap(accepted_order.get(i),accepted_order.get(i+1),sol.order);
                double tr = generateSchedule(sol);
                if (best < tr) {
                    best = tr;
                    bestPos = i;
                }
                swap(accepted_order.get(i+1),accepted_order.get(i),sol.order);
            }
            if (bestPos != -1) {
                swap(accepted_order.get(bestPos+1),accepted_order.get(bestPos),sol.order);
                sol.obj = best;
            }
            else break;
        }
        //double tr = generateScheduleActive(priority,false);
        return best;
    }
    double localsearch2(OASsol sol, double obj){
        List<Integer> accepted_order = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            if (sol.acc[sol.order[i]]) accepted_order.add(i);
        }
        double best = obj;
        int max = 1000;
        while (max-- > 0) {
            int a = rnd.nextInt(accepted_order.size());
            int b = rnd.nextInt(accepted_order.size());
            while (a==b){
                b = rnd.nextInt(accepted_order.size());
            }
            swap(a,b,sol.order);
            double tr = generateSchedule(sol);
            if (best < tr) {
                best = tr;
            } else swap(a,b,sol.order);
        }
        //double tr = generateSchedule(sol);
        return best;
    }
    double localsearch3(OASsol sol, double obj){
        double best = obj;
        while (true) {
            int bestPos = -1;
            for (int i = 0; i < n; i++) {
                sol.acc[i] = !sol.acc[i];
                double tr = generateSchedule(sol);
                if (best < tr) {
                    best = tr;
                    bestPos = i;
                }
                sol.acc[i] = !sol.acc[i];
            }
            if (bestPos != -1) {
                sol.acc[bestPos] = !sol.acc[bestPos];
                sol.obj = best;
            }
            else break;
        }
        //double tr = generateSchedule(sol);
        return best;
    }
    public void swap(int a, int b, int[] priority){
        int temp = priority[a];
        priority[a] = priority[b];
        priority[b] = temp;
    }
    public double generateSchedule(OASsol sol){
        double ready = 0;
        double revenue = 0;
        double tardy = 0;
        double fmax = 0;
        double tmax = 0;
        double mae = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        for (int i = 0; i < sol.order.length;i++) {
            if (sol.acc[sol.order[i]])
                remain.add(sol.order[i]);
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
                }
                ec = Math.min(ec, c);
            }
            for (int j:late) {
                remain.remove((Integer)j);
            }
            if (remain.isEmpty()) break;
            int i = getHighestIndexActiveJob(remain, r, ec);

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
        sol.obj = revenue;
        return revenue;
    }

    public double generateSchedule(int[] order){
        double ready = 0;
        double revenue = 0;
        double tardy = 0;
        double fmax = 0;
        double tmax = 0;
        double mae = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        for (int i = 0; i < order.length;i++) {
            remain.add(order[i]);
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
                }
                ec = Math.min(ec, c);
            }
            for (int j:late) {
                remain.remove((Integer)j);
            }
            if (remain.isEmpty()) break;
            int i = getHighestIndexActiveJob(remain, r, ec);

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
        return revenue;
    }
    public double maxPlus (double x) {
        if (x>0) return x;
        return 0;
    }
    public double localsearch(int[] order, double best){
        int[] tempOrder = new int[order.length];
        int[] bestOrder = new int[order.length];
        System.arraycopy(order, 0, bestOrder, 0, order.length);
        for (int i = 1; i < order.length; i++) {
            System.arraycopy(order, 0, tempOrder, 0, order.length);
            int temp = tempOrder[i];
            tempOrder[i] = tempOrder[i-1];
            tempOrder[i-1] = temp;
            double obj = generateSchedule(tempOrder);
            if (obj > best) {
                best = obj;
                System.arraycopy(tempOrder, 0, bestOrder, 0, order.length);
            }
        }
        System.arraycopy(bestOrder, 0, order, 0, order.length);
        return best;
    }
    public double localsearchRandom(int maxstep, int[] order, double best){
        int[] tempOrder = new int[order.length];
        for (int i = 1; i < maxstep; i++) {
            System.arraycopy(order, 0, tempOrder, 0, order.length);
            int a = rnd.nextInt(order.length);
            int b = rnd.nextInt(order.length);
            int temp = tempOrder[a];
            tempOrder[a] = tempOrder[b];
            tempOrder[b] = temp;
            double obj = generateSchedule(tempOrder);
            if (obj > best) {
                best = obj;
                System.arraycopy(tempOrder, 0, order, 0, order.length);
            }
        }
        return best;
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
    public int[] getRandomPermutation(int size){
        int[] a = new int[size];

        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = i;

        // shuffle
        for (int i = 0; i < size; i++) {
            int r = (int) (rnd.nextDouble() * (i+1));     // int between 0 and i
            int swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }
        return a;
    }
    double GAsubroutine(int popsize, int maxGEN, boolean ls){
        int N = popsize;
        int maxGen = maxGEN;
        int[][] indi = new int[N][n];
        double[] fitness = new double[N];
        double cross = 0.80;
        double mute  = 0.10;
        double copy  = 0.10;
        double bestOBJ = Integer.MIN_VALUE;
        bestOBJ = evaluate(true, new boolean[N], ls, indi, fitness, bestOBJ);
        for (int gen = 0; gen < maxGen; gen++) {
            boolean[] eval = reproduce(cross, mute, copy, 3, fitness, indi);
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
    double evaluate(boolean first, boolean[] eval, boolean ls, int[][] indi, final double[] fitness, double best){
        for (int i = 0; i < indi.length; i++) {
            if (!eval[i]){
                if (first) {
                    indi[i] = getRandomPermutation(n);
                }
                fitness[i] = generateSchedule(indi[i]);
                //fitness[i] = localsearch(indi[i], fitness[i]);
                //fitness[i] = localsearch(indi[i], fitness[i]);
                if (best<fitness[i]) {
                    best=fitness[i];
                }
            }
            //System.out.println(fitness[i]);
        }
        //System.exit(0);
       return best;
    }
    boolean[] reproduce(double cross, double mute, double copy, int toursize, double[] fitness, int[][] indi){
        boolean[] eval = new boolean[fitness.length];
        int popcount = 0;
        int[][] newpop = new int[fitness.length][n];
        
        while (popcount<fitness.length){
            double rn = rnd.nextDouble();
            if (rn<cross) {
                int p1 = select(toursize, fitness);
                int p2 = select(toursize, fitness);
                while (p2==p1) {
                    p2 = select(toursize, fitness);
                }
                int[] new1 = new int[n];
                System.arraycopy(indi[p1], 0, new1, 0, n);
                int[] new2 = new int[n];
                System.arraycopy(indi[p2], 0, new2, 0, n);
                crossover(new1, new2, indi[p1], indi[p2]);
                newpop[popcount] = new1;
                popcount++;
                if (popcount == fitness.length) break;
                else {
                    newpop[popcount] = new2;
                    popcount++;
                }
            } else if (rn < cross + mute) {
                int pi = select(toursize, fitness);
                int[] newi = new int[n];
                System.arraycopy(indi[pi], 0, newi, 0, n);
                mutate(newi);
                newpop[popcount] = newi;
                popcount++;
            } else {
                int pi = select(toursize, fitness);
                int[] newi = new int[n];
                System.arraycopy(indi[pi], 0, newi, 0, n);
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
    void crossover(int[] new1, int[] new2, int[] p1, int[] p2){
        int x1 = rnd.nextInt(n-1);
        int x2 = rnd.nextInt(n);
        while (x2<x1) { //&&x2-x1>4
            x2 = rnd.nextInt(n);
        }
        for (int i = x1; i <= x2; i++) {
            int temp = new1[i];
            int pos = findIndex(p2[i], new1);
            new1[i] = new1[pos];
            new1[pos] = temp;
            temp = new2[i];
            pos = findIndex(p1[i], new2);
            new2[i] = new2[pos];
            new2[pos] = temp;
        }
    }
    int findIndex (int key, int[] sol){
        for (int i = 0; i < sol.length; i++) {
            if (sol[i] == key) return i;
        }
        return -1;
    }
    void mutate(int[] pi){
        int x1 = rnd.nextInt(n-1);
        int x2 = rnd.nextInt(n);
        while (x2<x1) {
            x2 = rnd.nextInt(n);
        }
        int temp = pi[x1];
        pi[x1] = pi[x2];
        pi[x2] = temp;
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
    public void setupGPIndividual(GPIndividual i,oasData d, int th, EvolutionState s, ADFStack st, GPProblem gp){
        ind = i;
        data = d;
        thread = th;
        state = s;
        stack = st;
        gpproblem = gp;
    }
}

class OASsol {
    public int n;
    public int[] order;
    public boolean[] acc;
    public double obj = -1;
    public int[] getRandomPermutation(Random rnd, int size){
        int[] a = new int[size];

        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = i;

        // shuffle
        for (int i = 0; i < size; i++) {
            int r = (int) (rnd.nextDouble() * (i+1));     // int between 0 and i
            int swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }
        return a;
    }
    public OASsol (int n, Random rnd){
        this.n = n;
        randomize(rnd);
    }
    public void randomize(Random rnd) {
        order = getRandomPermutation(rnd, n);
        acc = new boolean[n];
        for (int i = 0; i < acc.length; i++) {
            acc[i] = rnd.nextBoolean();
        }
    }
    public OASsol(OASsol x){
        this.n = x.n;
        this.obj = x.obj;
        this.order = new int[n];
        this.acc = new boolean[n];
        System.arraycopy(x.order, 0, this.order, 0, order.length);
        System.arraycopy(x.acc, 0, this.acc, 0, acc.length);
    }
    public OASsol (){
    }
}
