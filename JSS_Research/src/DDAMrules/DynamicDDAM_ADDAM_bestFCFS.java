/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */

package DDAMrules;

import SmallStatistics.SmallStatistics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class DynamicDDAM_ADDAM_bestFCFS {
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
    public static double[][][][][][] simDDAM(boolean writeJobInfo, boolean writeReport, int rep) throws FileNotFoundException, IOException {
            SmallStatistics result = new SmallStatistics();
            String detailedReport = "\n dist s m u MPEA MPE ML STDL MT MAL MF PercentT \n";
            double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
            int[] numbeOfMachines = {4,5,6,10,20};
            String[] lowers = {"miss","full"};
            String[] dists = {"expo","erlang2","uniform"};
            double[][][][][][] detailedTest = new double [dists.length][lowers.length][numbeOfMachines.length][utilisation.length][8][rep];
            //*
            outerLoop:
            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (double u : utilisation){
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
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,lower,m,u,u,meanTime,distribution,param,1000,5000);
                    //SampleArray sa = new SampleArray(20); 
                    //set dispatching rule
                    Machine.priorityType PT = Machine.priorityType.FCFS;
                    jspDynamic.setPriorityType(PT);
                    jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                    jspDynamic.setNonDelayFactor(0.0);
                    //////////////////////////////////////////////
                    jspDynamic.setNextArrivalTime();
                    while (!jspDynamic.isStop()) {
                    if (jspDynamic.isNextArrivalEvent()) {
                        Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                        newjob.extraInfo+=jspDynamic.getNumberofJobs();
                        //Dynamic DDAMs
                        double N = jspDynamic.getNumberofJobs();
                        double ug = jspDynamic.getAverageNumberOfOperations();
                        double up = jspDynamic.getMeanOperationProcessingTime();
                        double lamda = jspDynamic.getArrivalRate();
                        double TOT = newjob.getTotalProcessingTime();
                        double NO = newjob.getNumberOperations();
                        double Nq = jspDynamic.getTotalNumberOfJobInQueue(newjob.getReleaseTime());
                        double TAPR = jspDynamic.getTotalAverageProcessingTime_Route(newjob);
                        double TLOT = jspDynamic.getLeftoverProcessingTime_Route(newjob);
                        double TQWL = jspDynamic.getQueueWorkLoad_Route(newjob);
                        double SAR = jspDynamic.getMovingAverageArrivalRate();
                        double TRWL = jspDynamic.getTotalRemainingWorkload_Route(newjob);
                        double TSAPR = jspDynamic.getTotalSampleAverageProcessingTime_Route(newjob);
                        double TSOTR = jspDynamic.getAverageSampledOTRatio_Route(newjob);
                        
                        double cond1 = N-(TQWL+TQWL-div((TQWL+TOT+TLOT+div(TQWL,SAR)+TRWL-TAPR*SAR),div(TQWL,SAR)))-(TLOT+TQWL*TSAPR);
                        double x = TQWL-div((div(0.84095263,TRWL-TAPR*SAR+TOT+TLOT)+TRWL-TAPR*SAR+TOT+TQWL),TAPR);
                        double cond2 = 2*x-N-TQWL;
                        
                        if (cond1>=0){
                            newjob.assignDuedate(TOT+TQWL+TLOT);
                        } else{
                            if (cond2>=0){
                                newjob.assignDuedate(TOT+TQWL);
                            } else {
                                newjob.assignDuedate(TQWL-2*TSOTR-div(TLOT,TLOT+TAPR)+TOT+TLOT);
                            }
                        }
                        
                        //newjob.assignDuedate(TOT+TQWL+TLOT);
                        jspDynamic.setNextArrivalTime();
                        } else {
                            jspDynamic.unplanAll();
                            do {
                                int nextMachine = jspDynamic.nextMachine();
                                if (nextMachine<0) 
                                    break;
                                Machine M = jspDynamic.machines[nextMachine];
                                jspDynamic.calculatePriority(M);
                                if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                                    Job J = M.completeJob();
                                    if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                                    else {
                                        jspDynamic.removeJobFromSystem(J);
                                        if (writeJobInfo&&!jspDynamic.isWarmUp()) System.out.println(J.toString());
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
                    result.add(mpea);
                    MPEA.add(mpea);
                    detailedTest[a][b][c][d][0][ds]=mpea;
                    MPE.add(jspDynamic.getMPE());
                    detailedTest[a][b][c][d][1][ds]=jspDynamic.getMPE();
                    ML.add(jspDynamic.getMeanLateness());
                    detailedTest[a][b][c][d][2][ds]=jspDynamic.getMeanLateness();
                    STDL.add(jspDynamic.getStdLateness());
                    detailedTest[a][b][c][d][3][ds]=jspDynamic.getStdLateness();
                    MT.add(jspDynamic.getMeantardiness());
                    detailedTest[a][b][c][d][4][ds]=jspDynamic.getMeantardiness();
                    MAL.add(jspDynamic.getMAE());
                    detailedTest[a][b][c][d][5][ds]=jspDynamic.getMAE();
                    MF.add(jspDynamic.getMeanFlowtime());
                    detailedTest[a][b][c][d][6][ds]=jspDynamic.getMeanFlowtime();
                    PercentT.add(jspDynamic.getPercentTardiness());
                    detailedTest[a][b][c][d][7][ds]=jspDynamic.getPercentTardiness();
                }
                if (writeReport){
                    String setting = dist + " " + s + " " + m + " " + u + " ";
                    detailedReport+=setting + MPEA.getAverage() + " " + MPE.getAverage() + " " +
                            ML.getAverage() + " " + STDL.getAverage() + " " + MT.getAverage() + " " +
                            MAL.getAverage() + " " + MF.getAverage() + " " + PercentT.getAverage() + "\n";
                }
                
            }}}}
                System.out.println("Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + detailedReport);
                return detailedTest;
    }
    public static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    public static double div(double a, double b){
        if (b!=0) return a/b;
        else return 0;
    }
}