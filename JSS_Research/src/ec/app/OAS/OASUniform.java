/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;

import ec.EvolutionState;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
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
public class OASUniform {
    public int n;
    double[] r,p,due,d_,e,w,si;
    double[][] s;
    double p_;
    double[] s_;
    double[] rlr1;
    Random rnd;
    double theta = 0.5;
    public OASUniform(int seed, int iN, int iTao, int iR, int inst) throws IOException{
        rnd = new Random(seed);
        readBenchmark("/"+iN+"orders/Tao"+iTao+"/R"+iR+"/Dataslack_"+iN+"orders_Tao"+iTao+"R"+iR+"_"+inst+".txt");
        bestRevenue = Double.NEGATIVE_INFINITY;
    }
    public static void main(String[] args) throws IOException {
        int N = 50; int Tao = 3; int R = 3; int instance = 5;
        OASUniform oas = new OASUniform(111,N,Tao,R,instance);
        //oas.readBenchmark("/"+N+"orders/Tao"+Tao+"/R"+R+"/Dataslack_"+N+"orders_Tao"+Tao+"R"+R+"_"+instance+".txt");
        double best = oas.GAsubroutine(1000, 50, true);
        System.out.println("Done!!! with best objective = " + best);
    }
    public OASUniform(){

    }
    public double localsearch1(List<Integer> acc, double obj, double[] priority){
        double best = obj;
        while (true) {
            int bestPos = -1;
            for (int i = 0; i < acc.size() - 1; i++) {
                swap(acc.get(i),acc.get(i+1),priority);
                double tr = generateScheduleActive(priority, false);
                if (best < tr) {
                    best = tr;
                    bestPos = i;
                }
                swap(acc.get(i+1),acc.get(i),priority);
            }
            if (bestPos != -1) {
                swap(acc.get(bestPos+1),acc.get(bestPos),priority);
            }
            else break;
        }
        //double tr = generateScheduleActive(priority,false);
        return best;
    }
    public double localsearch2(List<Integer> acc, double obj, double[] priority){
        double best = obj;
        int max = 100;
        while (max-- > 0) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (a==b){
                b = rnd.nextInt(n);
            }
            swap(a,b,priority);
            double tr = generateScheduleActive(priority, false);
            if (best < tr) {
                best = tr;
            } else swap(a,b,priority);
        }
        double tr = generateScheduleActive(priority,false);
        return best;
    }
    public void swap(int a, int b, List<Integer> acc){
        int temp = acc.get(a);
        acc.set(a, acc.get(b));
        acc.set(b, temp);
    }
    public void swap(int a, int b, double[] priority){
        double temp = priority[a];
        priority[a] = priority[b];
        priority[b] = temp;
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
    public double generateScheduleActive(double[] priority, boolean first){
        double ready = 0;
        double revenue = 0;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> acc = new ArrayList<Integer>();
        int prev = -1;
        for (int i = 0; i < priority.length;i++) {
            if (priority[i] >= theta)
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
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            prev = i;
            remain.remove((Integer)i);
            acc.add(i);
            count++;
        }
        if (first) {
            if (rnd.nextDouble()<0.05)
                revenue = localsearch2(acc, revenue, priority);
        }
        if (bestRevenue < revenue) bestRevenue = revenue;
        return revenue ;
    }
    
    double[] boostPriority(int from, int to, double[] priority) {
        double[] temp = new double[priority.length];
        System.arraycopy(priority, 0, temp, 0, temp.length);
        for (int i = from; i < to; i++) {
            temp[i] += 1;
        }
        return temp;
    }
    public double[] generateMOSchedule(double[] priority, boolean first, int objIndex){
        double[] obj = new double[2];
        double thres = 0.5*priority[n];
        double ready = 0;
        double revenue = 0;
        //double tardy = 0;
        ///double fmax = 0;
        //double tmax = 0;
        double mae = 0;
        //int lsCan = -1;
        //double lsCri = Double.POSITIVE_INFINITY;
        //if (objIndex == 1) lsCri = Double.NEGATIVE_INFINITY;
        List<Integer> remain = new ArrayList<Integer>();
        List<Integer> reject = new ArrayList<Integer>();
        int prev = -1;
        for (int i = 0; i < priority.length - 1;i++) {
            if (priority[i] >= 0.0) remain.add(i);
            else reject.add(i);
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
                    reject.add(j);
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
            double setup = si[i];
            if (prev != -1) setup = s[prev][i];
            ready += setup;
            ready += p[i];
            revenue += e[i] - w[i]*maxPlus(ready-due[i]);
            prev = i;
            mae += Math.abs((ready - due[i]));
            //if (objIndex == 0) {
            //    if (e[i]/(p[i] + setup) < lsCri) {
            //        lsCri = e[i]/(p[i] + setup);
            //        lsCan = i;
            //    }
            //} else if (objIndex == 1) {
            //    if (Math.abs((ready - d[i])) > lsCri) {
             //       lsCri = Math.abs((ready - d[i]));
            //        lsCan = i;
            //    }
            //}
            //tardy += maxPlus(ready-d[i]);
            //if (tmax < maxPlus(ready-d[i])) tmax = maxPlus(ready-d[i]);
            remain.remove((Integer)i);
            count++;
        }
        obj[0] = revenue;
        //obj[1] = flowtime/(double)count;
        //obj[1] = fmax;
        //obj[1] = tardy/(double)count;
        obj[1] = mae/(double)count;
        //obj[1] = tmax;
        //if (first && !reject.isEmpty()) {
         //   if (objIndex == 0) obj = localsearchMOtr(lsCan, thres, reject, obj, priority);
        //    else if (objIndex == 1) obj = localsearchMOmae(lsCan, thres, reject, obj, priority);
        //}
        return obj;
    }
    public double[] localsearchMOtr(int lsCan, double thres, List<Integer> reject, double[] obj, double[] priority){
        double[] best = obj;
        int max = 10;
        int bestPos = -1;
        while (max -- > 0 && !reject.isEmpty()) {
            int rej = reject.get(rnd.nextInt(reject.size()));
            if (reject.size()>=2) {
                int alt = reject.get(rnd.nextInt(reject.size()));
                if (rlr1[alt] > rlr1[rej]) rej = alt;
            }
            swap(lsCan,rej,priority);
            reject.remove((Integer)rej);
            double[] tr = generateMOSchedule(priority, false, -1);
            if (best[0] < tr[0]) {
                bestPos = rej;
                best = tr;
            }
            swap(lsCan,rej,priority);
        }
        if (bestPos!=-1)  swap(lsCan,bestPos,priority);
        return best;
    }
    public double[] localsearchMOmae(int lsCan, double thres, List<Integer> reject, double[] obj, double[] priority){
        double[] best = obj;
        int max = 10;
        int bestPos = -1;
        while (max -- > 0 && !reject.isEmpty()) {
            int rej = reject.get(rnd.nextInt(reject.size()));
            swap(lsCan,rej,priority);
            double[] tr = generateMOSchedule(priority, false, -1);
            if (best[0] > tr[0]) {
                bestPos = rej;
                best = tr;
            }
            swap(lsCan,rej,priority);
        }
        if (bestPos!=-1)  swap(lsCan,bestPos,priority);
        return best;
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
        }
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
        s_ = new double[n]; rlr1 = new double[n];
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
            rlr1[i] = e[i]/(p[i] + s_[i]);
        }
        return 1;
    }
    public double[] getSolution(int size){
        if (nInit-->0) {
            //System.out.println(generateScheduleActive(initPop[nInit], true));
            return initPop[nInit];
        }
        double[] a = new double[size];
        // insert integers 0..N-1
        for (int i = 0; i < size; i++)
            a[i] = rnd.nextDouble();
        return a;
    }
    public static int nInit;
    public double[][] initPop;
    double GAsubroutine(int popsize, int maxGEN, boolean ls){
        System.out.println("=================================================================================================");
        int N = popsize;
        int maxGen = maxGEN;
        double[][] indi = new double[N][n];
        double[] fitness = new double[N];
        double cross = 0.90;
        double mute  = 0.01;
        double copy  = 0.10;
        double bestOBJ = Integer.MIN_VALUE;
        bestOBJ = evaluate(true, new boolean[N], ls, indi, fitness, bestOBJ);
        for (int gen = 0; gen < maxGen; gen++) {
            boolean[] eval = reproduce(cross, mute, copy, 5, fitness, indi);
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
    double evaluate(boolean first, boolean[] eval, boolean ls, double[][] indi, final double[] fitness, double best){
        for (int i = 0; i < indi.length; i++) {
            if (!eval[i]){
                if (first) {
                    if (i==0) {
                        double obj = testrule(1);
                        System.arraycopy(priority, 0, indi[i], 0, priority.length);
                    } else if (i==1){
                        double obj = testrule(3);
                        System.arraycopy(priority, 0, indi[i], 0, priority.length);
                    } else indi[i] = getSolution(n);
                }
                fitness[i] = generateScheduleActive(indi[i],true);
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
    int getBestID(double[] fitness){
        double best = Double.NEGATIVE_INFINITY;
        int bestID = -1;
        for (int i = 0; i < fitness.length; i++) {
            if (best < fitness[i]) {
                best = fitness[i];
                bestID = i;
            }
        }
        return bestID;
    }
    boolean[] reproduce(double cross, double mute, double copy, int toursize, double[] fitness, double[][] indi){
        boolean[] eval = new boolean[fitness.length];
        double[] newfitness = new double[fitness.length];
        int popcount = 0;
        double[][] newpop = new double[fitness.length][n];
        int bestIND = getBestID(fitness);
        System.arraycopy(indi[bestIND], 0, newpop[0], 0, n); eval[0] = true;
        newfitness[0] = fitness[bestIND];
        popcount++;
        while (popcount<fitness.length){
            double rn = rnd.nextDouble();
            if (rn<cross) {
                int p1 = select(toursize, fitness);
                int p2 = select(toursize, fitness);
                while (p2==p1) {
                    p2 = select(toursize, fitness);
                }
                double[] new1 = new double[n];
                System.arraycopy(indi[p1], 0, new1, 0, n);
                double[] new2 = new double[n];
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
                double[] newi = new double[n];
                System.arraycopy(indi[pi], 0, newi, 0, n);
                //if (rnd.nextDouble()<mute) mutate(newi);
                newpop[popcount] = newi; eval[popcount] = true;
                newfitness[popcount] = fitness[pi];
                popcount++;
            }
        }
        for (int i = 0; i < newpop.length; i++) {
            System.arraycopy(newpop[i], 0, indi[i], 0, newpop[i].length);
        }
        fitness = newfitness;
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
    void crossover(double[] new1, double[] new2, double[] p1, double[] p2){
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
    void mutate(double[] pi){
        int x = rnd.nextInt(n);
        pi[x] = rnd.nextDouble();
        double p_mutate = 1 / (double) pi.length;
        for (int i = 0; i < pi.length; i++) {
            if (rnd.nextDouble() < p_mutate) pi[i] = rnd.nextDouble();
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
    //GP related
    GPIndividual ind;
    oasData data;
    int thread;
    EvolutionState state;
    ADFStack stack;
    GPProblem gpproblem;
    double[] priority;
    public double[] generateMOGPSchedule(int treeIndex){
        double[] obj = new double[2];
        double ready = 0;
        double revenue = 0;
        double flowtime = 0;
        double tardy = 0;
        double fmax = 0;
        double tmax = 0;
        double mae = 0;
        int prev = -1;
        priority = new double[n];
        ArrayList<Integer> jobs = new ArrayList<Integer>();
        ArrayList<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            jobs.add(i);
        }
        int count = 0;
        while (!jobs.isEmpty()){
            double maxPriority = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            for (int i : jobs) {
                double setup = si[i];
                if (prev != -1) setup = s[prev][i];
                if (ready + setup + p[i] > d_[i]) {
                    continue;
                }
                data.updateData(p[i], r[i], w[i], due[i], d_[i], e[i], setup, ready);
                ind.trees[treeIndex].child.eval(state,thread,data,stack,((GPIndividual)ind),gpproblem);
                if (data.tempVal > maxPriority) {
                    maxPriority = data.tempVal;
                    maxIndex = i;
                }
            }
            if (maxIndex == -1) break;
            if (r[maxIndex]>ready) ready = r[maxIndex];
            if (prev == -1) ready += si[maxIndex];
            else ready += s[prev][maxIndex];
            ready += p[maxIndex];
            revenue += e[maxIndex] - w[maxIndex]*maxPlus(ready-due[maxIndex]);
            prev = maxIndex;
            jobs.remove(jobs.indexOf(maxIndex));
            flowtime += ready - r[maxIndex];
            if (fmax < ready - r[maxIndex]) fmax = ready - r[maxIndex];
            tardy += maxPlus(ready-due[maxIndex]);
            if (tmax < maxPlus(ready-due[maxIndex])) tmax = maxPlus(ready-due[maxIndex]);
            mae += Math.abs((ready - due[maxIndex]));
            order.add(maxIndex);
            count++;
        }
        //convert to real-code representation
       double[] priRand = new double[order.size()];
        for (int i = 0; i < order.size();i++) {
            priRand[i] = 0.5 + 0.5* rnd.nextDouble();
        }
       Arrays.sort(priRand);
        for (int i = 0; i < order.size(); i++) {
            priority[order.get(i)] = priRand[order.size() - i - 1];
        }
        for (int i = 0; i < priority.length; i++) {
            if (priority[i]==0) priority[i] = 0.5 * rnd.nextDouble();
        }
        if (count == 0) {
            obj[0] = 0;
            obj[1] = 10000000;
            return obj;
        }
        obj[0] = revenue;
        //obj[1] = flowtime/(double)count;
        //obj[1] = fmax;
        //obj[1] = tardy/(double)count;
        obj[1] = mae/(double)count;
        return obj;
    }
    public void setupGPIndividual(GPIndividual i,oasData d, int th, EvolutionState s, ADFStack st, GPProblem gp){
        ind = i;
        data = d;
        thread = th;
        state = s;
        stack = st;
        gpproblem = gp;
    }
    public double[] generateActiveGPSchedule(int treeIndex){
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
                    data.updateData(p[j], r[j], w[j], due[j], d_[j], e[j], setup, ready);
                    ind.trees[treeIndex].child.eval(state,thread,data,stack,((GPIndividual)ind),gpproblem);
                    priority[j] = data.tempVal;
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
        return obj;
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
                    //if (rule == 0) priority[j] = div((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) , (IF(div((0.16284561 - W) , (P * S)) * ((S + P) - div(d , (P * R))), P, P / 0.11434984) - ((t - d) * (0.79690695 * S) + S + P)));
                    //priority[j] = ((div((0.03508053 * (P + W)) , ((t - d) / (P + E) * t/S)) + E / D) / (P / 0.11434984 - ((t - d) * (0.79690695 * S) + S + P)));
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
        priority = new double[n];
        double[] priRand = new double[acc.size()];
        for (int i = 0; i < acc.size();i++) {
            priRand[i] = theta + (1-theta)* rnd.nextDouble();
        }
        Arrays.sort(priRand);
        for (int i = 0; i < acc.size(); i++) {
            priority[acc.get(i)] = priRand[acc.size() - i - 1];
        }
        for (int i = 0; i < n; i++) {
            if (priority[i]==0) priority[i] = theta * rnd.nextDouble();
        }
        //priority[n] = 0.5;
        if (bestRevenue < revenue) bestRevenue = revenue;
        obj[0] = revenue;
        obj[1] = mae/(double)count;
        return revenue;
    }
    public static double div(double a, double b){
        if (b!=0)
            return a/b;
        else
            return 1;
    }
}

// best evolved rule for maximise TR - Fitness: [-1003.9951 246.21506 min]:
//((((0.030931132 / 0.88171786) * (P + W)) / (((t - d) / (P + E)) * IF((t + P) - (P * R), t, t / S))) + IF((S + (P * 0.8110814)) - ((t - d) / ((S + P) + W)), E / D, IF(((t - d) * (d * 0.66045433)) - IF(W, S + P, 0.57044506), ((D * S) * (P * 0.8110814)) + (P * R), E * IF(0.19251698 / E, t / t, W)))) / (IF(((0.16284561 - W) / (P * S)) * ((S + P) - (d / (P * R))), IF(W, P, P), P / 0.11434984) - (IF((0.7456535 - d) * ((0.6715395 * E) * R), E, (t - d) * (0.79690695 * S)) + IF(S, S + P, IF(D, d, d))))