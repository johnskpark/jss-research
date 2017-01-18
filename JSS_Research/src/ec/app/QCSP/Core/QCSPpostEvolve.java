/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP.Core;

import SmallStatistics.SmallStatistics;
import ec.EvolutionState;
import ec.app.QCSP.ExperimentQCSP;
import ec.app.QCSP.qcspData;
import ec.gp.ADFStack;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
public class QCSPpostEvolve {
    int n; // number of task
    public static int maxRangeLS = 2;
    public int getNumberOfTask(){
        return n;
    }
    int alpha = 1;
    int b; // number of bays
    int nPrec;// number of precedence relations
    int nNS; // number of non-simultaneous relations
    int q; // number of quays
    int t; // traveling time (per bay)
    int s; // safety margin
    public double[] penalty; //penalties for tasks
    double[] ft = new double[n]; //finish times of tasks
    double[] p; //processing times of tasks
    int[] l; // bay locations of tasks
    int[] r; // crane ready times
    double[] c; // crane ready times
    int[] il; // initial locations of cranes
    int[][] pt; // precedent tasks of a task
    int[][] st; // successive tasks of a task
    int[][] ns; //non-simul tasks of a task
    double[] hwl; //holding workload
    int[] cql; // current qc location
    double incrementQC[]; //increment QC
    boolean[] finished;
    public boolean LOCAL_MODE = false;
    public static int maxStepLS;
    public boolean unidirection = true;
    public boolean upward = true;
    public static boolean printSolvingStep = false;
    public static boolean checkConstraints = false;
    public static long timeLimit = 10000;
    public static boolean uncertainty = false;
    public static double noiseLevel = 0.01;
    public static double repTrain = 100;
    public static double repTest = 10000;
    int lb; //lower bound
    double ub = Double.POSITIVE_INFINITY; //upper bound
    public int iterativeMode = -1;
    //GP related
    GPIndividual ind;
    qcspData data;
    int thread;
    EvolutionState state;
    ADFStack stack;
    GPProblem gpproblem;
    public Random rnd = new Random(111);
    public Random noise;
    public double[] solBest;
    public ArrayList<Integer> sequence = new ArrayList<Integer>();
    int[] travelTime = new int[n];
    double[] bestPenalty;
    boolean bestDirection;
    public void resetBestPenalty(){
        bestPenalty = new double[n];
        bestECmax = Integer.MAX_VALUE;
    }
    public double[] constructTestSchedule(){
        unidirection = bestDirection;
        LOCAL_MODE = true;
        SmallStatistics e_cmax = new SmallStatistics();
        rep = 0;
        System.arraycopy(bestPenalty, 0, penalty, 0, n);
        double[] pen = new double[n];
        System.arraycopy(penalty, 0, pen, 0, n);
        for (int i = 0; i < repTest; i++) {
            System.arraycopy(pen, 0, penalty, 0, n);
            e_cmax.add(constructSchedule(i+99));
        }
        double[] result = {e_cmax.getAverage(),e_cmax.getUnbiasedStandardDeviation()};
        return result;
    }
    public static void main(String[] args) throws IOException {
        boolean localsearch = false;
        // TODO code application logic here
        int nSet = 1;
        int nRep = 30;
        int nInPerSet = 1;
        int fromT = 6;
        int fromInstanceT = 0;
        QCSPpostEvolve.uncertainty =false;
        QCSPpostEvolve.noiseLevel = 0.3;
        String outname = "";
        if (args.length!=0){
            nSet = Integer.parseInt(args[0]);
            fromT = Integer.parseInt(args[1]);
            nRep = Integer.parseInt(args[2]);
            localsearch = Boolean.parseBoolean(args[3]);
            timeLimit = 1000*Long.parseLong(args[4]);
            QCSPpostEvolve.uncertainty = Boolean.parseBoolean(args[5]);
            QCSPpostEvolve.noiseLevel = Double.parseDouble(args[6]);
            outname = args[7];
        }
        QCSPpostEvolve[][] test = new QCSPpostEvolve[nSet][nInPerSet];
        double[][][] OBJ = new double[nSet][nRep][nInPerSet];
        double[][][][] OBJTEST = new double[nSet][nRep][nInPerSet][2];
        long[][][] TIME = new long[nSet][nRep][nInPerSet];
        long[][][] BESTTIME = new long[nSet][nRep][nInPerSet];
        QCSPpostEvolve.maxRangeLS = 3;
        maxStepLS = 200;
        DecimalFormat df2 = new DecimalFormat( "000" );
        String report ="";
        writeResult("detaileREPORT" + outname, "results from " + nSet + " sets \n", false);
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < test[i].length; j++) {
                test[i][j] = new QCSPpostEvolve(QCSPpostEvolve.dataset[i+fromT] + df2.format(j + fromInstanceT + 1) + ".txt");
                for (int k = 0; k < nRep; k++) {
                    test[i][j].rnd = new Random(ExperimentQCSP.Seed[k]);
                    test[i][j].resetBestPenalty();
                    OBJ[i][k][j] = test[i][j].GAsubroutine(500, 100, localsearch);
                    OBJTEST[i][k][j] = test[i][j].constructTestSchedule();
                    TIME[i][k][j] = test[i][j].runningTime;
                    BESTTIME[i][k][j] = test[i][j].findTime;
                    System.out.println("Test perform = " + OBJTEST[i][k][j][0] +" (" + + OBJTEST[i][k][j][1] + ")" );
                    System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "/" + TIME[i][k][j] + ")");
                    writeResult("detaileREPORT" + outname, "Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "/" + TIME[i][k][j] + ") \n", true);
                }
            }
        }
        System.out.println("Detailed Result" + outname);
        SmallStatistics[][] avgOBJ = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][][] avgOBJTEST = new SmallStatistics[nSet][nInPerSet][2];
        SmallStatistics[][] avgREL = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][] avgTIME = new SmallStatistics[nSet][nInPerSet];
        SmallStatistics[][] avgBESTTIME = new SmallStatistics[nSet][nInPerSet];
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < nInPerSet; j++) {
                avgOBJ[i][j] = new SmallStatistics();
                avgOBJTEST[i][j][0] = new SmallStatistics(); avgOBJTEST[i][j][1] = new SmallStatistics();
                avgREL[i][j] = new SmallStatistics();
                avgTIME[i][j] = new SmallStatistics();
                avgBESTTIME[i][j] = new SmallStatistics();
                for (int k = 0; k < nRep; k++) {
                    System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + "/" + k + ": " + OBJ[i][k][j] + "(" + BESTTIME[i][k][j] + "--" + TIME[i][k][j] + ")");
                    avgOBJ[i][j].add(OBJ[i][k][j]);
                    avgOBJTEST[i][j][0].add(OBJTEST[i][k][j][0]); avgOBJTEST[i][j][1].add(OBJTEST[i][k][j][1]);
                    avgREL[i][j].add((OBJ[i][k][j]-QCSPpostEvolve.bestKnown[i+fromT][j+fromInstanceT])/QCSPpostEvolve.bestKnown[i+fromT][j+fromInstanceT]);
                    avgTIME[i][j].add(TIME[i][k][j]);
                    avgBESTTIME[i][j].add(BESTTIME[i][k][j]);
                }
            }
        }
        System.out.println("===================================================================================");
        System.out.println("Summarised Result");
        DecimalFormat df = new DecimalFormat( "0.00" );
        String testReport ="";
        for (int i = 0; i < nSet; i++) {
            for (int j = 0; j < nInPerSet; j++) {
                System.out.println("Instance " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJ[i][j].getMin() + "/" + avgOBJ[i][j].getAverage() + "/" + avgOBJ[i][j].getMax() + " -- " + df.format(avgREL[i][j].getAverage()*100) + "/" + df.format(avgREL[i][j].getMax()*100) +  " (" + avgBESTTIME[i][j].getAverage()
                        + "/" + avgBESTTIME[i][j].getMax() + "--" + avgTIME[i][j].getAverage() + "/" + avgTIME[i][j].getMax() + ")");
                report += "Instance " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJ[i][j].getMin() + "/" + avgOBJ[i][j].getAverage() + "/" + avgOBJ[i][j].getMax() + " -- " + df.format(avgREL[i][j].getAverage()*100) + "/" + df.format(avgREL[i][j].getMax()*100) + " (" + avgBESTTIME[i][j].getAverage()
                        + "/" + avgBESTTIME[i][j].getMax() + "--" + avgTIME[i][j].getAverage() + "/" + avgTIME[i][j].getMax() + ") \n";
                testReport += "Expected " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJTEST[i][j][0].getMin() + "/" + avgOBJTEST[i][j][0].getAverage() + "/" + avgOBJTEST[i][j][0].getMax() + "\n";
                testReport += "Std.Devi " + (i+fromT) + "/" + (j+fromInstanceT) + ": " + avgOBJTEST[i][j][1].getMin() + "/" + avgOBJTEST[i][j][1].getAverage() + "/" + avgOBJTEST[i][j][1].getMax() + "\n";            }
        }
        writeResult("nqcspREPORT" + QCSPpostEvolve.uncertainty + outname, report + "\n === robust test === \n" + testReport, false);
        System.out.println("\n === robust test === \n" + testReport);
        System.out.println("Experiments are done!!!");
    }

    public void assignRandomPenalty(){
        penalty = new double[n];
        for (int i = 0; i < penalty.length; i++) penalty[i] = rnd.nextDouble();
    }
    public double iterativeConstructSchedule(int maxStep){
        penalty = new double[n];
        double best = Integer.MAX_VALUE;
        int count = 0;
        while (count<maxStep){
            //for (int i = 0; i < penalty.length; i++) penalty[i] = rnd.nextDouble();
            double obj = constructSchedule();
            if (best > obj){
                best = obj;
                solBest = getOrder(sequence);
            }
            count++;
        }
        return best;
    }
    public double iterativeIMPROVEONLY(){
        penalty = new double[n];
        double best = Integer.MAX_VALUE;
        while (true){
            double obj = constructSchedule();
            if (best>obj){
                best = obj;
                solBest = getOrder(sequence);
            } else break;
        }
        return best;
    }


    public double  ILS(int maxStep){
        double BEST = ub; double HOMEBEST = ub;
        double[] BESTSOL = new double[n];
        double[] HOMESOL = new double[n];
        System.arraycopy(solBest, 0, BESTSOL, 0, solBest.length);
        System.arraycopy(solBest, 0, HOMESOL, 0, solBest.length);
        for (int i = 0; i < maxStep; i++) {
            System.arraycopy(HOMESOL, 0, solBest, 0, solBest.length);
            swap(solBest);
            double bestLS = LS1(100, HOMEBEST,solBest);
            if (BEST>bestLS){
                BEST = HOMEBEST = bestLS;
                System.arraycopy(solBest, 0, BESTSOL, 0, solBest.length);
                System.arraycopy(solBest, 0, HOMESOL, 0, solBest.length);
            } //else {        if (rnd.nextDouble()<0.3) {                    HOMEBEST = bestLS;                    System.arraycopy(solBest, 0, HOMESOL, 0, solBest.length);   }
        }
        return BEST;
    }
    public void localsearchBest(double best, int maxtep){
        LS1(2*maxtep, best, bestPenalty);
    }
    public double  LS1(int maxStep, double best, double[] sol){
        LOCAL_MODE = true;
        penalty = new double[n];
        for (int i = 0; i < maxStep; i++) {
            System.arraycopy(sol, 0, penalty, 0, sol.length);
            swap(penalty);
            double obj = constructSchedule();
            if (best>obj){
                best = obj;
                sol = getOrder(sequence);
            }
        }
        System.arraycopy(sol, 0, penalty, 0, sol.length);
        return best;
    }
    public double LS2(int maxStep, double best, double[] sol){
        double[] newsol = new double[n];
        penalty = new double[n];
        boolean improve = false;
        for (int i = 0; i < maxStep; i++) {
            System.arraycopy(sol, 0, penalty, 0, sol.length);
            insertion(penalty);
            double obj = constructSchedule();
            if (best>obj){
                best = obj;
                newsol = getOrder(sequence);
                improve = true;
            }
        }
        if (improve) System.arraycopy(newsol, 0, sol, 0, sol.length);
        return best;
    }
    public void swap(double[] sol){
        int l1 = rnd.nextInt(n);
        int l2 = rnd.nextInt(n);
        while (l1==l2&&Math.abs(sol[l1]-sol[l2])>maxRangeLS){
            l2 = rnd.nextInt(n);
        }
        double temp = sol[l1];
        sol[l1] = sol[l2];
        sol[l2] = temp;
    }
    public void insertion(double[] sol){
        int l1 = rnd.nextInt(n-1);
        int l2 = rnd.nextInt(n);
        while (l1>=l2){
            l2 = rnd.nextInt(n);
        }
        double temp = sol[l2];
        System.arraycopy(sol, l1, sol, l1 + 1, l2 - l1);
        sol[l1] = temp;
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
    public double[] getOrder(int[] seq){
        double[] order = new double[seq.length];
        int count = seq.length;
        for (int i : seq) {
            order[i] = count;
            count--;
        }
        return order;
    }
    //construct a new QCSP instance
    public QCSPpostEvolve(String problemfile) throws IOException{
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















    void reverseQC(){
        for (int i = 0; i < q; i++) {
            int newpos = b - il[i] + 1;
            cql[q-i-1] = newpos;
            c[q-i-1] += t*Math.abs(newpos-il[q-i-1]);
        }
    }
    int rep;
    double bestECmax;
    public double constructSchedule(){
        double e_cmax = 0;
        double cmax = 0;
        double[] pen = new double[n];
        if (LOCAL_MODE) System.arraycopy(penalty, 0, pen, 0, n);
        if (!LOCAL_MODE) {
            cmax = constructSchedule(-1);
            pen = getOrder(sequence);
        }
        rep = 0;
        if (uncertainty){
            LOCAL_MODE = true;
            for (int i = 0; i < repTrain; i++) {
                System.arraycopy(pen, 0, penalty, 0, n);
                e_cmax += constructSchedule(i);
            }
        }
        else if (LOCAL_MODE) e_cmax += constructSchedule(-1);
        else {
            e_cmax = cmax;
            rep = 1;
        }
        if (bestECmax > e_cmax/rep) {
            bestECmax = e_cmax/rep;
            pen = getOrder(sequence);
            System.arraycopy(pen, 0, bestPenalty, 0, n);
            bestDirection = unidirection;
        }
        return e_cmax/rep;
    }
    double getNoise(double mag){
        return 1 - mag + 2*mag*noise.nextDouble();
    }

//---------------------------------------------------------

    public double constructSchedule(int seed){
        rep ++;
        double[] tempNoise = new double[n];
        if (seed>-1) {
            noise = new Random(seed);
            for (int i = 0; i < tempNoise.length; i++) {
                tempNoise[i] = getNoise(noiseLevel);
            }
        }

        int totalGrantry = 0;
        sequence.clear();
        upward = true;
        for (int i = 0; i < r.length; i++) c[i] = r[i];
        if (upward) System.arraycopy(il, 0, cql, 0, cql.length);
        else reverseQC();        
        boolean[] readyTask = new boolean[n];
        double[] readyTime = new double[n];
        travelTime = new int[n];
        finished = new boolean[n];
        //ft = new int[n];
        incrementQC = new double[q];
        IndexMaxPQ<Double> tasks = new IndexMaxPQ<Double>(n);
        for (int i = 0; i < readyTask.length; i++) {
            if (pt[i].length==0) {
                readyTask[i] = true;
                tasks.insert(i, 0.0);
            } else readyTime[i] = Integer.MAX_VALUE;
        }
        if (printSolvingStep) printState();
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
                if (!LOCAL_MODE){
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
                    double RFT = ft[task]; //finished time of the task (from previous schedule)
                    double PT = penalty[task];
                    double Q = q; // number of quay cranes
                    double B = b; // number of bays
                    data.updateData(-1,B, Q, PR, HWL, D, LWL, LQC, DNQ, C, CNQ, NBQT, T, S, RFT, PT);
                    ind.trees[0].child.eval(state,thread,data,stack,((GPIndividual)ind),gpproblem);
                    newtasks.insert(task, data.tempVal);
                } else {
                    newtasks.insert(task, penalty[task]);
                }
            }
            //if (uncertainty) unidirection = false;
            tasks = newtasks;
            int nextTask = -1;
            for (int task:tasks){
                if (readyTime[task]>c[qc]) continue;
                if (unidirection){
                    if (upward){
                        if (l[task]<cql[qc]) continue;
                        boolean leftTaskAvail = false;
                        for (int i = 0; i < n; i++) {
                            if (qc == 0){
                                if (!finished[i]&&i!=task&&l[i]<l[task]) {
                                    leftTaskAvail = true;
                                    break;
                                }
                            }
                        }
                        if (leftTaskAvail) continue;
                    } else {
                        if (l[task]>cql[qc]) continue;
                        boolean rightTaskAvail = false;
                        for (int i = 0; i < n; i++) {
                            if (qc == q-1){
                                if (!finished[i]&&i!=task&&l[i]>l[task]) {
                                    rightTaskAvail = true;
                                    break;
                                }
                            }
                        }
                        if (rightTaskAvail) continue;
                    }
                }
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
                updateQCtoEarliestCompletiontime(qc,l[tasks.maxIndex()]);
                continue;
            }
            //schedule the next task with the selected quay crane
            if (printSolvingStep) printTask(nextTask);
            if (l[nextTask]-cql[qc]<0){
                totalGrantry += t*Math.abs(l[nextTask]-cql[qc]);
            }
            if (l[nextTask]-cql[qc]<0)
                travelTime[nextTask]=t*Math.abs(l[nextTask]-cql[qc]);
            if (seed==-1) c[qc] += t*Math.abs(l[nextTask]-cql[qc]) + p[nextTask];
            else c[qc] += t*Math.abs(l[nextTask]-cql[qc]) + p[nextTask]*tempNoise[nextTask];
            cql[qc] = l[nextTask];
            incrementQC[qc] = 0.0;
            finished[nextTask] = true;
            ft[nextTask] = c[qc];
            tasks.delete(nextTask);
            if (c[qc]>lb) penaltise(nextTask, lb, 1);
            sequence.add(nextTask);
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
                        readyTime[sucTask] = c[qc];
                    }
                }
            }
            if (printSolvingStep) printState();            
            if (checkConstraints) checkInferenceContraints();
        }
        if (printSolvingStep) System.out.println("Total Gantrying Time = " + totalGrantry);
        double cmax = alpha*makespan();
        if (ub>cmax) ub = cmax;
        return cmax;
    }







////////////////////////////////////
    void printPenalty(){
        String out = "double[] checkPel = {";
        for (int i = 0; i < penalty.length; i++) {
            out += penalty[i] + ",";
        }
        out += "};";
        System.out.println(out);
    }
    public void penaltise(int task, double lb, double ratio){
        penalty[task] += p[task] + travelTime[task];
        for (int i = 0; i < pt[task].length; i++) {
            if (ft[pt[task][i]]+p[task]>lb) penaltise(pt[task][i],lb-p[task], ratio);
        }
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
    public double makespan(){
        double Cmax = Integer.MIN_VALUE;
        for (int i = 0; i < q; i++) {
            if (Cmax<c[i]) Cmax = c[i];
        }
        return Cmax;
    }
    public void updateQCtoEarliestCompletiontime(int qc, int nextloc){
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < q; i++) {
            if (i!=qc&&c[qc]<c[i]&&min>c[i]) min = c[i];
        }
        if (min==Double.POSITIVE_INFINITY) min = c[qc];
        if (false&&min>c[qc]){
            int des = 0;
            boolean left = false;
            if (cql[qc]-nextloc > 0) {
                if (qc==0){
                    printPenalty();
                }
                left = true;
                des = cql[qc-1] + s +1;
            } else des = cql[qc+1] - s - 1;
            int freetime = (int) min(min - c[qc], t * Math.abs(des - cql[qc]));
            if (left) cql[qc] -= freetime/t;
            else cql[qc] += freetime/t;
        }
        c[qc] = min;
        incrementQC[qc] += 0.00001;
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
    public int tryShiftRight(int gap, double time, int nextR){
        while (true){
            if (c[nextR]<=time) {
                if (nextR==q-1){
                    if (2*b-cql[nextR]>=gap) return nextR;
                    else return -1;
                } else {
                    if (cql[nextR+1]-cql[nextR]>s+gap) return nextR;
                }
                nextR++;
                if (nextR==q) return -1;
            } else return -1;
        }
    }
    public int tryShiftLeft(int gap, double time, int nextL){
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
    public int readBenchmark (int kp, int instance){
        int[][] rawdata;
        if (kp==1) rawdata = KimPark1.getIN(instance);
        else rawdata = KimPark2.getIN(instance);
        if (instance<=22) {
            q = 2; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 6;
        } else if (instance<=32) {
            q = 2; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 8;
        } else if (instance<=42) {
            q = 3; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 7; cql[2] = il[2] = 14;
        } else if (instance<=52) {
            q = 3; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 9; cql[2] = il[2] = 17;
        } else if (instance<=62) {
            q = 4; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 8; cql[2] = il[2] = 15; cql[3] = il[3] = 22;
        }  else if (instance<=72) {
            q = 4; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 9; cql[2] = il[2] = 18; cql[3] = il[3] = 27;
        }  else if (instance<=82) {
            q = 5; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 9; cql[2] = il[2] = 17; cql[3] = il[3] = 25; cql[4] = il[4] = 33;
        }  else if (instance<=92) {
            q = 5; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 10; cql[2] = il[2] = 19; cql[3] = il[3] = 28; cql[4] = il[4] = 37;
        }  else if (instance<=102) {
            q = 6; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q];
            cql[0] = il[0] = 1; cql[1] = il[1] = 9; cql[2] = il[2] = 18; cql[3] = il[3] = 26; cql[4] = il[4] = 35; cql[5] = il[5] = 43;
        }
        n = rawdata.length;
        t = 1;
        s = 1;
        p = new double[n]; l = new int[n]; pt = new int[n][]; st = new int[n][]; ns = new int[n][];
        b = rawdata[0][0];
        ArrayList<Integer>[] PT = new ArrayList[n];
        ArrayList<Integer>[] ST = new ArrayList[n];
        for (int i = 0; i < PT.length; i++) {
            PT[i] = new ArrayList<Integer>();
            ST[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < rawdata.length; i++) {
            p[i] = rawdata[i][2];
            l[i] = rawdata[i][0];
            if (b < l[i]) b = l[i];
            if (rawdata[i][1] == 1 && rawdata[i][3] == 2) {
                int suc = searchTask(rawdata, l[i], 2, 2);
                if (suc!=-1) ST[i].add(suc);
            } else if (rawdata[i][1] == 2 && rawdata[i][3] == 2) {
                int pred = searchTask(rawdata, l[i], 1, 2);
                if (pred!=-1) PT[i].add(pred);
                int suc = searchTask(rawdata, l[i], 2, 1);
                if (suc!=-1) ST[i].add(suc);
            } else if (rawdata[i][1] == 2 && rawdata[i][3] == 1) {
                int pred = searchTask(rawdata, l[i], 2, 2);
                if (pred!=-1) PT[i].add(pred);
                int suc = searchTask(rawdata, l[i], 1, 1);
                if (suc!=-1) ST[i].add(suc);
            } else if (rawdata[i][1] == 1 && rawdata[i][3] == 1) {
                int pred = searchTask(rawdata, l[i], 2, 1);
                if (pred!=-1) PT[i].add(pred);
            }
        }
                for (int i = 0; i < PT.length; i++) {
            pt[i]  = new int[PT[i].size()];
            for (int j = 0; j < PT[i].size(); j++) {
                pt[i][j] = PT[i].get(j);
            }
        }

        hwl = new double[n];
        for (int i = 0; i < ST.length; i++) {
            st[i]  = new int[ST[i].size()];
            for (int j = 0; j < ST[i].size(); j++) {
                st[i][j] = ST[i].get(j);
                hwl[i] += p[st[i][j]];
            }
        }
        penalty = new double[n];
        ft = new double[n];
        System.arraycopy(p, 0, ft, 0, n);
        int twl = 0;
        for (int i = 0; i < n; i++) {
            twl += p[i];
        }
        lb = twl/q;
        return 1;
    }
    int searchTask(int[][] dat, int bay, int pos, int op){
        for (int i = 0; i < dat.length; i++) {
            if (dat[i][0]==bay && dat[i][1]==pos && dat[i][3]==op) return i;
        }
        return -1;
    }
    public int readBenchmark(String problemFile) throws IOException {
        if (problemFile.split("park")[0].equals("kim")) {
            alpha =3;
            String[] ins = problemFile.split("[_.]+");
            int from = 12;
            if (Integer.parseInt(ins[1])==2) from = 62;
            readBenchmark(Integer.parseInt(ins[1]),Integer.parseInt(ins[2])+from);
            return 1;
        }
        alpha = 1;
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
        p = new double[n]; l = new int[n]; il = new int[q]; r = new int[q]; cql = new int[q]; c = new double[q]; pt = new int[n][]; st = new int[n][]; ns = new int[n][];
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
        
        hwl = new double[n];
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
        ft = new double[n];
        System.arraycopy(p, 0, ft, 0, n);
        int twl = 0;
        for (int i = 0; i < n; i++) {
            twl += p[i];
        }
        lb = twl/q;
        return 1;
    }

    public static String[] dataset = {"/benchmark/QCSP_n10_b10_c200_f50_uni_d100_g0_q2_t1_s1_", // set A (0)
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
                                "/benchmark/QCSP_n50_b15_c400_f50_uni_d100_g0_q4_t1_s4_",
                                "kimpark_1_", "kimpark_2_"};


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
                                {1501,1328,1275,1443,1327,1622,1448,1345,1328,1437}, //kimpark1
                                {453,546,513,312,453,375,543,399,465,537,576,666,738,639,657,531,807,891,570,591,603,717,684,678,510,613.67,508.38,564,585.06,560.31,859.32,820.35,824.88,690,792,628.87,879.22}
                                
    };
    private static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    private static double min(double a, double b){
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
    public static void swap(double[] X, int p1, int p2){
        double temp = X[p1];
        X[p1]=X[p2];
        X[p2]=temp;
    }
    // genetic algorithm for QCSP
    public long runningTime;
    public long findTime;
    public int[][] iniPop;
    double GAsubroutine(int popsize, int maxGEN, boolean ls){
        runningTime = System.currentTimeMillis();
        int N = popsize;
        int maxGen = maxGEN;
        LOCAL_MODE = true;
        int[][] indi = new int[N][n];
        double[] fitness = new double[N];
        double cross = 0.80;
        double mute  = 0.10;
        double copy  = 0.10;
        double bestOBJ = Integer.MAX_VALUE;
        bestOBJ = evaluate(true, new boolean[N], ls, indi, fitness, bestOBJ);
        for (int gen = 0; gen < maxGen; gen++) {
            boolean[] eval = reproduce(cross, mute, copy, 7, fitness, indi);
            bestOBJ = evaluate(false, eval, ls, indi, fitness, bestOBJ);
            System.out.println(average(fitness) + "(" + bestOBJ + ")");
            if (!ls&&uncertainty) localsearchBest(bestOBJ, maxStepLS);
            if (System.currentTimeMillis()-runningTime>timeLimit) break;
        }
        runningTime = (System.currentTimeMillis()-runningTime)/1000;
        return bestOBJ;
    }
    double average(double[] fitness){
        double avg = 0;
        for (int i = 0; i < fitness.length; i++) {
            avg += fitness[i];
        }
        return (double) avg / (double) fitness.length;
    }
    void convertToPenalty(int[] sol){
        for (int i = 0; i < sol.length; i++) {
            penalty[sol[i]] = n-i;
        }
    }
    int[] convertToSequence(double[] sol){
        int[] seq = new int[n];
        for (int i = 0; i < sol.length; i++) {
            seq[n-(int)sol[i]] = i;
        }
        return seq;
    }
    double evaluate(boolean first, boolean[] eval, boolean ls, int[][] indi, final double[] fitness, double best){
        for (int i = 0; i < indi.length; i++) {
            if (!eval[i]){
                if (first) assignRandomPenalty();
                else convertToPenalty(indi[i]);
                if (i >= indi.length/2) unidirection = false;
                else unidirection = true;
                fitness[i] = constructSchedule();
                for (int j = 0; j < indi[i].length; j++) {
                    indi[i][j] = sequence.get(j);
                }
                if (ls) fitness[i] = LS1(maxStepLS, (int)fitness[i],getOrder(sequence));
                if (best>fitness[i]) {
                    best=fitness[i];
                    findTime = (System.currentTimeMillis()-runningTime)/1000;
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
            if (bestFit > fitness[k]) {
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
    int findIndex (int key, int[] sol){
        for (int i = 0; i < sol.length; i++) {
            if (sol[i] == key) return i;
        }
        return -1;
    }
    public static void writeResult(String filename, String results, boolean append){
        try{
            // Create file
            FileWriter fstream = new FileWriter(filename,append);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(results);
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
        System.err.println("Error: " + e.getMessage());
        }
    }
}
