/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */

package DDAMrules;

import TwoWaySchedulingPolicy.*;
import SmallStatistics.SmallStatistics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class DynamicDDAMregression {
    /**
     * @param args the command line arguments
     */
    public static double meanTime = 1;
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                            55013,84005,54972,80531,45414,25675,
                            79032,14882,17423,2798,77874,3805,
                            21671,51204,85187,76476,12363,92832,
                            36503,25237,26178,13614,50288,26279
                            };
    //public static double[] utilisation = {0.7,0.8,0.9};
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    /*
    double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
    int[] numbeOfMachines = {4,5,6,10,20};
    String[] lowers = {"miss","full"};
    String[] dists = {"expo","erlang2","uniform"};
    */
    public static double[][][][][][] simDDAM(double uu, int mmm, String ll, String dd, String rule, boolean writeJobInfo, boolean writeReport, int rep, int warm, int end, ArrayList x, ArrayList y, double[] coff) throws FileNotFoundException, IOException {
            SmallStatistics[] result = new SmallStatistics[3];
            for (int i = 0; i < result.length; i++) {
                result[i] = new SmallStatistics();
            }
            Machine.b = 2;
            Machine.k = 2;
            String detailedReport = "\n dist s m u Cmax TWT MAPE MPE ML STDL MT MAL MF PercentT \n";
            double[] utilisation = {uu};
            int[] numbeOfMachines = {mmm};
            String[] lowers = {ll};
            String[] dists = {dd};
            double[][][][][][] detailedTest = new double [dists.length][lowers.length][numbeOfMachines.length][utilisation.length][10][rep];
            //*
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){
                SmallStatistics Cmax = new SmallStatistics();
                SmallStatistics TWT = new SmallStatistics();
                SmallStatistics MPEA = new SmallStatistics();
                SmallStatistics MPE = new SmallStatistics();
                SmallStatistics ML = new SmallStatistics();
                SmallStatistics STDL = new SmallStatistics();
                SmallStatistics MT = new SmallStatistics();
                SmallStatistics MAL = new SmallStatistics();
                SmallStatistics MF = new SmallStatistics();
                SmallStatistics PercentT = new SmallStatistics();
                for (int ds = 0; ds < rep; ds++) {
                    int lower = 0;
                    String distribution ="";
                    double param = -1;
                    if ("miss".equals(s)) lower = 1; else lower = m;
                    if ("expo".equals(dist)){
                        distribution = "erlang";
                        param = 1;
                    } else if ("erlang2".equals(dist)) {
                        distribution = "erlang";
                        param = 2;
                    } else if ("uniform".equals(dist)) {
                        distribution = "uniform";
                        param = 0.5;                        
                    }
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,lower,m,u,u,meanTime,distribution,param,warm,end);
                    //SampleArray sa = new SampleArray(20); 
                    //set dispatching rule
                    Machine.priorityType PT = Machine.priorityType.CR;
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    jspDynamic.setNonDelayFactor(0.0);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                    if (jspDynamic.isNextArrivalEvent()) {
                        Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                        newjob.extraInfo+=jspDynamic.getNumberofJobs();
                        double estimatedFlowTime = 0;
                        for (int i = 0; i < newjob.getNumberOperations(); i++) {
                            int mm = newjob.getKthMachine(i);
                            double APR = jspDynamic.getMachines()[mm].getAverageProcessingTimeinQueue(newjob.getReleaseTime());
                            double M = m;
                            double NJQ = jspDynamic.getMachines()[mm].getNumberofJobInQueue(newjob.getReleaseTime());
                            double N = jspDynamic.getNumberofJobs();
                            double NO = newjob.getNumberOperations();
                            double OT = newjob.getKthOperationProcessingTime(i);
                            double PEF = estimatedFlowTime;
                            double RWL = jspDynamic.getMachines()[mm].getRemainingWorkload();
                            double SAPR = jspDynamic.getMachines()[mm].getSampleAverageProcessingTime();
                            double SAR = jspDynamic.getMovingAverageArrivalRate();
                            double SAW = jspDynamic.getMachines()[mm].getSampleAverageWaitingTime();
                            double SER = jspDynamic.getMovingAverageErrorDD();
                            double SL = jspDynamic.getMovingAverageJobLength();
                            double TAPR = jspDynamic.getTotalAverageProcessingTime_Route(newjob);
                            double TAW = jspDynamic.getTotalAverageWaiting_Route(newjob);
                            double TOT = newjob.getTotalProcessingTime();
                            double LOT = jspDynamic.getMachines()[mm].getLeftoverTimetoProcessCurrentJob(newjob.getReleaseTime());
                            double CPOT = jspDynamic.getMachines()[mm].getCompletedPartialTimeCurrentJob(newjob.getReleaseTime());
                            double QWL = jspDynamic.getMachines()[mm].getQueueWorkload(newjob.getReleaseTime());
                            double OTR = jspDynamic.getMachines()[mm].getOTRatio(newjob.getKthOperationProcessingTime(i));
                            double SOTR = jspDynamic.getMachines()[mm].getSampledOTRatio(newjob.getKthOperationProcessingTime(i));
                            
                            estimatedFlowTime+=QWL + LOT + OT;                   
                        }
                        double JQ = jspDynamic.getTotalNumberofJobQueue_Route(newjob);
                        double JR = jspDynamic.getTotalNumberofJob_Route(newjob);
                        double TR = jspDynamic.getTotalTimeJob_Route(newjob);
                        double JO = jspDynamic.getNumberofJobs() - JR;
                        double TO = jspDynamic.getTotalTimeJob() - TR;
                        double n =  newjob.getNumberOperations();
                        double p = newjob.getTotalProcessingTime();
                        double N = jspDynamic.getNumberofJobs();
                        double ug = jspDynamic.getAverageNumberOfOperations();
                        double up = jspDynamic.getMeanOperationProcessingTime();
                        double lamda = jspDynamic.getArrivalRate();
                        double TOT = newjob.getTotalProcessingTime();
                        double NO = newjob.getNumberOperations();
                        double Nq = jspDynamic.getTotalNumberOfJobInQueue(newjob.getReleaseTime());
                        double TAPR = jspDynamic.getTotalAverageProcessingTime_Route(newjob);
                        double SL = jspDynamic.getMovingAverageJobLength();
                        double TLOT = jspDynamic.getLeftoverProcessingTime_Route(newjob);
                        double TQWL = jspDynamic.getQueueWorkLoad_Route(newjob);
                        //newjob.assignDuedate(estimatedFlowTime);
                        
                        //newjob.assignDuedate(coff[0]*TOT); newjob.extraInfo= TOT + ""; //TWK
                        //newjob.assignDuedate(coff[0]*NO); newjob.extraInfo= NO + ""; //NOP
                        //newjob.assignDuedate(coff[0]*TOT + coff[1]*JQ); newjob.extraInfo= TOT + " " + JQ; //JOQ
                        newjob.assignDuedate(coff[0]*TOT+coff[1]*N); newjob.extraInfo= TOT + " " + N; //JIS
                        //newjob.assignDuedate(coff[0]*TOT+coff[1]*TQWL); newjob.extraInfo= TOT + " " + TQWL; //WIQ
                        //newjob.assignDuedate(coff[0]*TOT + coff[1]*JQ + coff[2]*TLOT + coff[3]*TQWL + coff[4]*NO); newjob.extraInfo= TOT + " " + JQ + " " + TLOT + " " + TQWL + " " + NO;
                        //newjob.assignDuedate(coff[0]*TOT + coff[1]*TLOT + coff[2]*TQWL); newjob.extraInfo= TOT + " " + TLOT + " " + TQWL;

                        jspDynamic.setNextArrivalTime();
                        } else {
                            jspDynamic.unplanAll();
                            do {
                                int nextMachine = jspDynamic.nextMachine();
                                if (nextMachine<0) 
                                    break;
                                Machine M = jspDynamic.machines[nextMachine];
                                //* input tule
                                jspDynamic.setInitalPriority(M);
                                // determine priority of jobs in queue
                                jspDynamic.calculatePriority(M);
                                if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                                    Job J = M.completeJob();
                                    if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                                    else {
                                        jspDynamic.removeJobFromSystem(J);
                                        if (writeJobInfo&&!jspDynamic.isWarmUp()) {
                                            //System.out.println(J.toString());
                                            StringTokenizer ss = new StringTokenizer(J.extraInfo, " ");
                                            int n =  ss.countTokens();
                                            double[] xx = new double[n];
                                            for (int i = 0; i < n; i++) xx[i] = Double.parseDouble(ss.nextToken());
                                            x.add(xx);
                                            y.add(J.getReadyTime()-J.getReleaseTime());
                                        }
                                    }
                                } else 
                                    M.plan();
                            } while(true);
                        }
                    }
                    double mpea = jspDynamic.getMAPE();
                    if (mpea < 0.0 || mpea == Double.POSITIVE_INFINITY || Double.isNaN(mpea))
                        break outerLoop;
                    
                    int a =-1;
                    for (int j = 0; j < dists.length; j++) {
                        if (dist.equals(dists[j])) 
                            a=j;
                    }
                    int b=-1;
                    for (int j = 0; j < lowers.length; j++) {
                        if (s.equals(lowers[j])) b=j;
                    }
                    int c=-1;
                    for (int j = 0; j < numbeOfMachines.length; j++) {
                        if (m==numbeOfMachines[j]) c=j;
                    }
                    int d =-1;
                    for (int j = 0; j < utilisation.length; j++) {
                        if (u==utilisation[j]) d=j;
                    }
                    result[0].add(jspDynamic.getCmax());
                    result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
                    result[2].add(mpea);
                    Cmax.add(jspDynamic.getCmax());
                    detailedTest[a][b][c][d][0][ds]=jspDynamic.getCmax();
                    TWT.add(jspDynamic.getNormalisedTotalWeightedTardiness());
                    detailedTest[a][b][c][d][1][ds]=jspDynamic.getNormalisedTotalWeightedTardiness();
                    MPEA.add(mpea);
                    detailedTest[a][b][c][d][2][ds]=mpea;
                    MPE.add(jspDynamic.getMPE());
                    detailedTest[a][b][c][d][3][ds]=jspDynamic.getMPE();
                    ML.add(jspDynamic.getMeanLateness());
                    detailedTest[a][b][c][d][4][ds]=jspDynamic.getMeanLateness();
                    STDL.add(jspDynamic.getStdLateness());
                    detailedTest[a][b][c][d][5][ds]=jspDynamic.getStdLateness();
                    MT.add(jspDynamic.getMeantardiness());
                    detailedTest[a][b][c][d][6][ds]=jspDynamic.getMeantardiness();
                    MAL.add(jspDynamic.getMAE());
                    detailedTest[a][b][c][d][7][ds]=jspDynamic.getMAE();
                    MF.add(jspDynamic.getMeanFlowtime());
                    detailedTest[a][b][c][d][8][ds]=jspDynamic.getMeanFlowtime();
                    PercentT.add(jspDynamic.getPercentTardiness());
                    detailedTest[a][b][c][d][9][ds]=jspDynamic.getPercentTardiness();
                }
            }}}}
            if (writeReport){
                for (int m = 0; m < rep; m++) {
                    System.out.print(detailedTest[0][0][0][0][0][m]+",");
                }
            }
            return detailedTest;
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
        if (b!=0) return a/b;
        else return 1;
    }
    public static double IF(double a, double b, double c){
        if (a>=0){
            return b;
        }else{
            return c;
        }
    }
}