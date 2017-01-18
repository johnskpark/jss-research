/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */
//expo miss 4 0.6 0.1485372184142967 0.009792231244458 0.34785054866869575 2.122423092560315 0.7648807523902023 1.1819109561117087 6.267943670293197 0.37559506880182375
package DDAMrules;

import SmallStatistics.SmallStatistics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class Journal_DynamicDDAM_ODDAM_bestFCFS {
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
            double[] utilisation = {0.9};
            int[] numbeOfMachines = {6};
            String[] lowers = {"full"};
            String[] dists = {"erlang2"};
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
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,lower,m,u-0.05,u,meanTime,distribution,param,1000,5000);
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
                            
                            double cond1=0;
                            cond1 = (SAR+2*LOT-PEF);
                            if (PEF+OT-PEF*SAPR*QWL>=0){
                                cond1 = cond1*div(div(OT,N),(APR+SAPR*LOT-PEF)*(OT+QWL));
                            } else cond1 = cond1*(QWL*0.2542787+SAR);
                            double cond2=0;
                            double tempt = 0;
                            if (cond1>=0){
                                cond2 = (div(OT,N)-OT+LOT+SAPR*LOT-PEF);
                                cond2 = (cond2*(SAR+2*LOT-PEF)+(LOT-PEF))*PEF*SAPR*LOT*QWL;
                                if (cond2>=0){
                                    tempt = OT+QWL+LOT;
                                } else tempt = OT + QWL;
                            } else tempt = OT + QWL;
                            estimatedFlowTime+=tempt;
                            //estimatedFlowTime += QWL + OT + LOT;
                        }
                        newjob.assignDuedate(estimatedFlowTime);
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
                    detailedTest[a][b][c][d][7][ds]=100*jspDynamic.getPercentTardiness();
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
        else return 1;
    }
}