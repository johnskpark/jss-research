/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP.Core;

import ec.EvolutionState;
import ec.app.QCSP.qcspData;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author nguyensu
 */
public class QCSPTest {
    public int n; // number of task
    public int b; // number of bays
    public int nPrec;// number of precedence relations
    public int nNS; // number of non-simultaneous relations
    public int q; // number of quays
    public int t; // traveling time (per bay)
    public int s; // safety margin
    public double[] penalty; //penalties for tasks
    public int[] ft = new int[n]; //finish times of tasks
    public int[] p; //processing times of tasks
    public int[] l; // bay locations of tasks
    public int[] r; // crane ready times
    public int[] c; // crane ready times
    public int[] il; // initial locations of cranes
    public int[][] pt; // precedent tasks of a task
    public int[][] st; // successive tasks of a task
    public int[][] ns; //non-simul tasks of a task
    public int[] hwl; //holding workload
    public int[] cql; // current qc location
    public double incrementQC[]; //increment QC
    public boolean[] finished;
    public static boolean printSolvingStep = false;
    public static boolean checkConstraints = false;
    public int lb; //lower bound
    public int ub = Integer.MAX_VALUE; //upper bound
    //GP related
    public GPIndividual ind;
    public qcspData data;
    public int thread;
    public EvolutionState state;
    public ADFStack stack;
    public GPProblem gpproblem;
    public Random rnd = new Random(111);
    double[] solBest;
    ArrayList<Integer> sequence = new ArrayList<Integer>();
    ArrayList<Integer>[] qc_task_sequence;
    ArrayList<Integer>[] qc_task_sequence_best;
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        int set = 30;
        QCSPTest[] train = new QCSPTest[10];
        for (int i = 0; i < train.length; i++) {
            DecimalFormat df2 = new DecimalFormat( "000" );
            System.out.println("======================================================");
            System.out.println(dataset[set]+df2.format(i+1)+".txt");
            System.out.println("------------------------------------------------------");
            train[i] = new QCSPTest(dataset[set]+df2.format(i+1)+".txt");
            int best  = Integer.MAX_VALUE;
            double[] pr = new double[train[i].n];
            double[] bestPR = new double[pr.length];
            int step = 0;
            int obj = -1;
            while (step<100) {
                if (step==0) obj = train[i].iterativeConstructSchedule(1000, false);
                else obj = train[i].iterativeConstructSchedule(1000, true);
                obj = train[i].LS2(obj,train[i].solBest,train[i].qc_task_sequence_best);
                System.arraycopy(train[i].solBest, 0, train[i].penalty, 0, train[i].solBest.length);
                step++;
            }
            System.out.println("Best found makespan = " + train[i].ub);
            System.out.println("======================================================");
        }
    }
    public int iterativeConstructSchedule(int maxStep, boolean withInitPenalty){
        qc_task_sequence = new ArrayList[q];
        qc_task_sequence_best = new ArrayList[q];
        for (int i = 0; i < q; i++) {
            qc_task_sequence[i] = new ArrayList<Integer>();
            qc_task_sequence_best[i] = new ArrayList<Integer>();
        }
        if (!withInitPenalty) penalty = new double[n];
        //else penalty = reverse(penalty);
        int best = Integer.MAX_VALUE;
        int count = 0;
        while (count<maxStep){
            int obj = constructSchedule();
            if (best>obj){
                best = obj;
                solBest = getOrder(sequence);
                copyQCSequence(qc_task_sequence, qc_task_sequence_best);
            }
            count++;
        }
        return best;
    }
    public double[] reverse(double[] x){
        double[] rev = new double[x.length];
        for (int i = 0; i < rev.length; i++) {
            rev[i] = x[x.length-1-i];
        }
        return rev;
    }
    public void copyQCSequence( ArrayList<Integer>[] from,  ArrayList<Integer>[] to){
        for (int i = 0; i < from.length; i++) {
            to[i].clear();
            for (int j:from[i]) {
                to[i].add(j);
            }
        }
    }
    public int  LS1(int best, double[] sol, ArrayList<Integer>[] qc_task_seq){
        while (true){
            int currentBest = best;
            double[] currentBestSol = new double[n];
            ArrayList<Integer>[] qc_task_sequence_temp = new ArrayList[q];
            for (int i = 0; i < qc_task_sequence_temp.length; i++) qc_task_sequence_temp[i] = new ArrayList<Integer>();
            boolean improve = false;
            for (int i = 1; i < n; i++) {
                System.arraycopy(sol, 0, penalty, 0, sol.length);
                double temp = penalty[i-1]; penalty[i-1] = penalty[i]; penalty[i] = temp;
                int obj = constructSchedule();
                if (currentBest>obj){
                    improve = true;
                    currentBest = obj;
                    currentBestSol = getOrder(sequence);
                    copyQCSequence(qc_task_sequence, qc_task_sequence_temp);
                }
            }
            if (improve) {
                best = currentBest;
                System.arraycopy(currentBestSol, 0, sol, 0, sol.length);
                copyQCSequence(qc_task_sequence_temp, qc_task_seq);
            } else break;
        }
        return best;
    }
    public int  LS2(int best, double[] sol, ArrayList<Integer>[] qc_task_seq){

        int currentBest = best;
        double[] currentBestSol = new double[n];
        System.arraycopy(sol, 0, currentBestSol, 0, sol.length);
        ArrayList<Integer>[] qc_task_sequence_temp = new ArrayList[q];
        for (int i = 0; i < qc_task_sequence_temp.length; i++) qc_task_sequence_temp[i] = new ArrayList<Integer>();
        while (true){
            boolean improve = false;
            for (int i = 0; i < q; i++) {
                for (int j = 1; j < qc_task_seq[i].size(); j++) {
                    System.arraycopy(sol, 0, penalty, 0, sol.length);
                    double temp = penalty[qc_task_seq[i].get(j-1)];
                    penalty[qc_task_seq[i].get(j-1)] = penalty[qc_task_seq[i].get(j)];
                    penalty[qc_task_seq[i].get(j)] = temp;
                    int obj = constructSchedule();
                    if (currentBest>obj){
                        improve = true;
                        currentBest = obj;
                        currentBestSol = getOrder(sequence);
                        copyQCSequence(qc_task_sequence, qc_task_sequence_temp);
                    }
                }
            }
            if (improve) {
                best = currentBest;
                System.arraycopy(currentBestSol, 0, sol, 0, sol.length);
                copyQCSequence(qc_task_sequence_temp, qc_task_seq);
            } else break;
        }
        return best;
    }

    public double[] getOrder(ArrayList<Integer> seq){
        double[] order = new double[seq.size()];
        int count = seq.size();
        for (int i : seq) {
            order[i] = count;
            count--;
        }
        return order;
    }
    //construct a new QCSP instance
    public QCSPTest(String problemfile) throws IOException{
        readBenchmark(problemfile);
    }
    public void setupGPIndividual(GPIndividual i,qcspData d, int th, EvolutionState s, ADFStack st, GPProblem gp){
        ind = i;
        data = d;
        thread = th;
        state = s;
        stack = st;
        gpproblem = gp;
    }
    public int constructSchedule(){
        for (int i = 0; i < q; i++) {
            qc_task_sequence[i].clear();
        }
        sequence.clear();
        System.arraycopy(il, 0, cql, 0, cql.length);
        System.arraycopy(r, 0, c, 0, c.length);
        boolean[] readyTask = new boolean[n];
        finished = new boolean[n];
        //ft = new int[n];
        incrementQC = new double[q];
        IndexMaxPQ<Double> tasks = new IndexMaxPQ<Double>(n);
        for (int i = 0; i < readyTask.length; i++) {
            if (pt[i].length==0) {
                readyTask[i] = true;
                tasks.insert(i, 0.0);
            }
        }
        if (printSolvingStep)
            printState();
        //start scheduling
        while(!tasks.isEmpty()){
            double minC = Double.POSITIVE_INFINITY;
            int qc = -1;
            //select earliest complete quay crane
            for (int i = 0; i < c.length; i++) {
                if (minC>c[i]+incrementQC[i]) {
                    minC=c[i]+incrementQC[i];
                    qc = i;
                }
            }
            IndexMaxPQ<Double> newtasks = new IndexMaxPQ<Double>(n);
            for (int task:tasks){
                double PR = p[task]; //processing time
                double HWL = hwl[task]; //holding workload (caused by precedent constraints)
                double D = Math.abs(l[task]-cql[qc]); //distance between task and the considered quay crane
                double LWL = getLocalWorkload(task, s+1, finished); //the remaining workload near the considered task
                double LQC = getLocalQC(task, 1*(s+1)); //the number of local quay crane
                double DNQ = getDistanceToNearestQC(task); //distance to the nearest quay crane
                double C = c[qc]; //completion time (ready time) of the considered quay crane
                double CNQ = getCompletionTimeOfNearestQC(task); //get completion time of the nearest quay crane
                double NBQT = getNumberOfQCBetweenTaskandQC(qc, task); //number of QC between the considered quay crane and task
                double T = t; //moving speed of quay cranes
                double S = s; //safe distance between two quay crane
                double FT = ft[task]; //finished time of the task (from previous schedule)
                double PT = penalty[task];
                double Q = q; // number of quay cranes
                double B = b; // number of bays
                //data.updateData(B, Q, PR, HWL, D, LWL, LQC, DNQ, C, CNQ, NBQT, T, S, FT, PT);
                //ind.trees[0].child.eval(state,thread,data,stack,((GPIndividual)ind),gpproblem);
                //newtasks.insert(task, data.tempVal);
                newtasks.insert(task, PT);
            }
            tasks = newtasks;
            int nextTask = -1;
            for (int task:tasks){
                int shiftTo;
                if (qc==0){
                    if (l[task]<cql[1]-s) {
                        nextTask = task;
                        break;
                    }
                    else if ((shiftTo = tryShiftRight(s - (cql[1] - l[task] -1), c[qc] ,1))>=0) {
                        nextTask = task; shiftRight(s - (cql[1] - l[task] -1), 1, shiftTo);
                        break;
                    }
                } else if (qc==q-1){
                    if (l[task]>cql[qc-1]+s) {
                        nextTask = task;
                        break;
                    }
                    else if ((shiftTo = tryShiftLeft(s - (l[task] - cql[qc-1] -1), c[qc] ,qc-1))>=0) {
                        nextTask = task; shiftLeft(s - (l[task] - cql[qc-1] -1), qc-1, shiftTo);
                        break;
                    }
                } else {
                    if (l[task]<cql[qc+1]-s && l[task]>cql[qc-1]+s) {
                        nextTask = task;
                        break;
                    } else if (l[task]>=cql[qc+1]-s){
                        if ((shiftTo = tryShiftRight(s - (cql[qc+1] - l[task] -1), c[qc] , qc+1))>=0) {
                            nextTask = task; shiftRight(s - (cql[qc+1] - l[task] -1), qc+1, shiftTo);
                            break;
                        }
                    } else if (l[task]<=cql[qc-1]+s) {
                        if ((shiftTo = tryShiftLeft(s - (l[task] - cql[qc-1] -1), c[qc] ,qc-1))>=0) {
                            nextTask = task; shiftLeft(s - (l[task] - cql[qc-1] -1), qc-1, shiftTo);
                            break;
                        }
                    }
                }
            }
            //no task can be done at the moment due to conflict/block
            if (nextTask==-1) {
                updateQCtoEarliestCompletiontime(qc);
                continue;
            }
            //schedule the next task with the selected quay crane
            if (printSolvingStep) printTask(nextTask);
            c[qc] += Math.abs(l[nextTask]-cql[qc]) + p[nextTask];
            cql[qc] = l[nextTask];
            incrementQC[qc] = 0.0;
            finished[nextTask] = true;
            ft[nextTask] = c[qc];
            tasks.delete(nextTask);
            if (c[qc]>lb) penaltise(nextTask, lb, 1);
            sequence.add(nextTask);
            qc_task_sequence[qc].add(nextTask);
            //examine the successive task of this task
            for (int sucTask: st[nextTask]) {
                if (!readyTask[sucTask]){
                    boolean newTaskisReady = true;
                    for (int precTask:pt[sucTask]) {
                        if (!finished[precTask]) {
                            newTaskisReady = false;
                            break;
                        }
                    }
                    if (newTaskisReady) {
                        tasks.insert(sucTask, 0.0);
                        readyTask[sucTask] = true;
                    }
                }
            }
            if (printSolvingStep) printState();
            if (checkConstraints) checkInferenceContraints();
        }
        int cmax = makespan();
        if (ub>cmax) ub = cmax;
        return cmax;
    }
    public double getMoveableRange(int qc){
        double range = 0;
        if (qc==0) {
            range += cql[qc]-1;
            if (cql[qc+1]-cql[qc]>s+1) range += cql[qc+1]-cql[qc] - s - 1;
        } else if (qc==q-1){
            range += b - cql[qc];
            if (cql[qc]-cql[qc-1]>s+1) range += cql[qc]-cql[qc-1] - s - 1;
        } else {
            if (cql[qc+1]-cql[qc]>s+1) range += cql[qc+1]-cql[qc] - s - 1;
            if (cql[qc]-cql[qc-1]>s+1) range += cql[qc]-cql[qc-1] - s - 1;
        }
        return range;
    }
    public double getDistanceToNearestQC(int task){
        int d = l[task] - cql[0];
        for (int i = 1; i < q; i++) {
            if (d>l[task] - cql[i]) d = l[task] - cql[i];
        }
        return d;
    }
    public double getCompletionTimeOfNearestQC(int task){
        int d = l[task] - cql[0];
        int id = 0;
        for (int i = 1; i < q; i++) {
            if (d>l[task] - cql[i]) {
                d = l[task] - cql[i];
                id = i;
            }
        }
        return c[id];
    }
    public double getNumberOfQCBetweenTaskandQC(int qc, int task){
        double count = 0;
        for (int i = 0; i < q; i++) {
            if (cql[i]>cql[qc]&&cql[i]<l[task]) count++;
        }
        return count;
    }
    public double getLocalWorkload(int task, int radius, boolean[] finished){
        double wl = 0;
        for (int i = 0; i < n; i++) {
            if (!finished[i]&&i!=task&&Math.abs(l[task]-l[i])<=radius) {
                wl += p[i];
            }
        }
        return wl;
    }
    public double getLocalQC(int task, int radius){
        double lqc = 0;
        for (int i = 0; i < q; i++) {
            if (Math.abs(l[task]-cql[i])<=radius) {
                lqc ++;
            }
        }
        return lqc;
    }
    public void penaltise(int task, double lb, double ratio){
        penalty[task] += ft[task];
        for (int i = 0; i < pt[task].length; i++) {
            if (ft[pt[task][i]]+p[task]>lb) penaltise(pt[task][i],lb-p[task], ratio);
        }
    }
    public int makespan(){
        int Cmax = Integer.MIN_VALUE;
        for (int i = 0; i < q; i++) {
            if (Cmax<c[i]) Cmax = c[i];
        }
        return Cmax;
    }
    public void updateQCtoEarliestCompletiontime(int qc){
        int min = c[qc];
        for (int i = 0; i < q; i++) {
            if (i!=qc&&c[qc]<c[i]) min = c[i];
        }
        c[qc] = min;
        incrementQC[qc] += 0.1;
        if (printSolvingStep) {
            System.out.println("Increment completion time of QC #" + qc);
            printState();
        }
    }
    public void printTask(int task){
        System.out.println("task #" + task + " at " + l[task] + ", pr = " + p[task]);
    }
    public void printState(){
        for (int i = 0; i < n; i++) {
            System.out.print(i+"/"+finished[i]+"--");
        }
        System.out.println("");
        for (int i = 0; i < q; i++) {
            System.out.print(i+"/("+c[i]+","+cql[i]+")--");
        }
        System.out.println("");
    }
    public void shiftRight(int gap, int from, int to){
        for (int i = from; i <= to; i++) {
            cql[i]+=gap;
            c[i]+=gap*t;
        }
    }
    public void shiftLeft(int gap, int from, int to){
        for (int i = from; i >= to; i--) {
            cql[i]-=gap;
            c[i]+=gap*t;
        }
    }
    public int tryShiftRight(int gap, int time, int nextR){
        while (true){
            if (c[nextR]<=time) {
                if (nextR==q-1){
                    if (b/2+b-cql[nextR]>=gap) return nextR;
                    else return -1;
                } else {
                    if (cql[nextR+1]-cql[nextR]>s+gap) return nextR;
                }
                nextR++;
                if (nextR==q) return -1;
            } else return -1;
        }
    }
    public int tryShiftLeft(int gap, int time, int nextL){
        while (true){
            if (c[nextL]<=time){
                if (nextL==0){
                    if (b/2+cql[nextL]>gap) return nextL;
                    else return -1;
                } else {
                    if (cql[nextL]-cql[nextL-1]>s+gap) return nextL;
                }
                nextL--;
                if (nextL<0) return -1;
            } else return -1;
        }
    }
    public void checkInferenceContraints(){
        for (int i = 0; i < q - 1; i++) {
            if (cql[i+1]-cql[i]<s+1)
                System.err.println("Inference constraints are violated!!!!!");
        }
    }
    public void readBenchmark(String problemFile) throws IOException {
        //problemFile = "/benchmark/"+problemFile;
        InputStream _read = getClass().getResourceAsStream(problemFile);//new FileReader(problemFile);
        //BufferedReader buffread = new BufferedReader(read);
        InputStreamReader _inputFileReader = new InputStreamReader(_read);
        BufferedReader _buffread   = new BufferedReader(_inputFileReader);
        String line = _buffread.readLine();
        StringTokenizer str= new StringTokenizer(line,",");
        n = Integer.parseInt(str.nextToken());
        b = Integer.parseInt(str.nextToken());
        nPrec = Integer.parseInt(str.nextToken());
        nNS = Integer.parseInt(str.nextToken());
        q = Integer.parseInt(str.nextToken());
        t = Integer.parseInt(str.nextToken());
        s = Integer.parseInt(str.nextToken());
        p = new int[n]; l = new int[n]; il = new int[q]; r = new int[q]; cql = new int[q]; c = new int[q]; pt = new int[n][]; st = new int[n][]; ns = new int[n][]; 
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        for (int i = 0; i < p.length; i++) p[i] = Integer.parseInt(str.nextToken());
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        for (int i = 0; i < l.length; i++) {
            l[i] = Integer.parseInt(str.nextToken());
            if (i>0&&l[i]<l[i-1]) System.err.println("initial locations of bays violates the constraints!!!");
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        for (int i = 0; i < r.length; i++) {
            c[i] = r[i] = Integer.parseInt(str.nextToken());
        }
        line = _buffread.readLine(); str= new StringTokenizer(line,",");
        for (int i = 0; i < il.length; i++) {
            cql[i] = il[i] = Integer.parseInt(str.nextToken());
            if (i>0&&il[i]<=il[i-1]) System.err.println("initial locations of bays violates the constraints!!!");
        }
        line = _buffread.readLine();
        ArrayList<Integer>[] PT = new ArrayList[n];
        ArrayList<Integer>[] ST = new ArrayList[n];
        for (int i = 0; i < PT.length; i++) {
            PT[i] = new ArrayList<Integer>();
            ST[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < nPrec; i++) {
            line = _buffread.readLine(); str= new StringTokenizer(line,",");
            int prec = Integer.parseInt(str.nextToken()) - 1;
            int task = Integer.parseInt(str.nextToken()) - 1;
            PT[task].add(prec); ST[prec].add(task);
        }
        for (int i = 0; i < PT.length; i++) {
            pt[i]  = new int[PT[i].size()];
            for (int j = 0; j < PT[i].size(); j++) {
                pt[i][j] = PT[i].get(j);
            }
        }
        hwl = new int[n];
        for (int i = 0; i < ST.length; i++) {
            st[i]  = new int[ST[i].size()];
            for (int j = 0; j < ST[i].size(); j++) {
                st[i][j] = ST[i].get(j);
                hwl[i] += p[st[i][j]];
            }
        }

        line = _buffread.readLine();
        ArrayList<Integer>[] NS = new ArrayList[n];
        for (int i = 0; i < NS.length; i++) {
            NS[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < nNS; i++) {
            line = _buffread.readLine(); str= new StringTokenizer(line,",");
            int m = Integer.parseInt(str.nextToken()) - 1;
            int n = Integer.parseInt(str.nextToken()) - 1;
            NS[m].add(n); NS[n].add(m);
        }
        for (int i = 0; i < NS.length; i++) {
            ns[i]  = new int[NS[i].size()];
            for (int j = 0; j < NS[i].size(); j++) {
                ns[i][j] = NS[i].get(j);
            }
        }
        penalty = new double[n];
        ft = new int[n];
        System.arraycopy(p, 0, ft, 0, n);
        int twl = 0;
        for (int i = 0; i < n; i++) {
            twl += p[i];
        }
        lb = twl/q;
    }
    public static String[] dataset = { "/benchmark/QCSP_n10_b10_c200_f50_uni_d100_g0_q2_t1_s1_", // set A (0)
                                "/benchmark/QCSP_n15_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n20_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n25_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n30_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n35_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n40_b10_c200_f50_uni_d100_g0_q2_t1_s1_",
                                "/benchmark/QCSP_n45_b15_c400_f50_uni_d100_g0_q4_t1_s1_", // set B  (7)
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n55_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n60_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n65_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n70_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n75_b20_c600_f50_uni_d100_g0_q6_t1_s1_", //set C (13)
                                "/benchmark/QCSP_n80_b20_c600_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n85_b20_c600_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n90_b20_c600_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n95_b20_c600_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n100_b20_c600_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f20_cl1_d100_g0_q4_t1_s1_", //set D (19)
                                "/benchmark/QCSP_n50_b15_c400_f20_cl2_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f20_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f80_cl1_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f80_cl2_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f80_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d80_g0_q4_t1_s1_", //set E (25)
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d85_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d90_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d95_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q2_t1_s1_", //set F (30)
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q3_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q5_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q6_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s0_", //set G (35)
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s1_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s2_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s3_",
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s4_"};

    public static double[][] bestKnown = {{520,508,513,510,515,513,511,513,512,549}, //set A
                                {514,507,515,513,507,508,507,508,507,513},
                                {508,509,509,509,506,508,507,510,508,507},
                                {508,507,507,507,507,507,508,507,506,506},
                                {506,508,507,507,506,506,508,508,506,506},
                                {506,507,506,507,507,511,507,506,506,508},
                                {506,506,505,507,506,507,507,506,506,507},
                                {758,759,759,789,758,789,798,759,797,792}, //set B
                                {774,771,772,765,762,765,782,761,798,759},
                                {758,783,779,759,758,789,768,767,801,757},
                                {781,756,758,765,760,758,786,757,785,805},
                                {758,799,803,758,758,757,757,756,758,786},
                                {766,764,760,760,757,761,759,758,757,779},
                                {1178,1011,1182,1107,1192,1123,1200,1174,1074,1188}, //set C
                                {1173,1023,1013,1202,1036,1117,1201,1040,1192,1207},
                                {1049,1017,1027,1186,1082,1010,1195,1105,1010,1166},
                                {1014,1020,1011,1063,1062,1193,1108,1094,1075,1049},
                                {1174,1090,1014,1138,1144,1055,1173,1015,1019,1011},
                                {1014,1104,1107,1202,1015,1136,1098,1151,1023,1015},
                                {544,556,680,578,356,414,439,383,420,380}, //set D
                                {453,430,439,312,349,307,373,308,308,397},
                                {415,307,426,324,309,307,325,349,387,346},
                                {1214,1206,1222,1221,1210,1213,1217,1213,1209,1212},
                                {1207,1208,1211,1209,1210,1212,1211,1208,1207,1208},
                                {1207,1209,1216,1210,1207,1207,1208,1208,1208,1210},
                                {774,771,772,758,761,757,782,758,798,759}, //set E
                                {774,771,772,761,761,757,782,758,798,759},
                                {774,771,772,762,761,757,782,761,798,759},
                                {774,771,772,762,761,757,782,761,798,759},
                                {774,771,772,765,762,765,782,761,798,759},
                                {1509,1510,1510,1510,1509,1509,1511,1509,1510,1510}, //set F
                                {1007,1008,1008,1009,1007,1008,1009,1008,1012,1008},
                                {774,771,772,765,762,765,782,761,798,759},
                                {730,768,770,748,732,714,780,650,797,684},
                                {730,768,769,746,732,714,779,643,797,683},
                                {757,759,759,759,758,758,760,757,758,759}, //set G
                                {774,771,772,765,762,765,782,761,798,759},
                                {1059,950,976,1104,833,1031,1042,954,1075,930},
                                {1381,1288,1098,1365,1040,1262,1431,1092,1252,1190},
                                {1501,1328,1275,1443,1327,1622,1448,1345,1328,1437}

    };
    private static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    private static double min(double a, double b){
        if (a<b) return a;
        else return b;
    }
}
