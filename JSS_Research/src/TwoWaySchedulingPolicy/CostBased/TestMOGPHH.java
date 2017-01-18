/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */

package TwoWaySchedulingPolicy.CostBased;

import SmallStatistics.SmallStatistics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import jsp.DynamicJSPFramework;
import jsp.Job;
import jsp.Machine;

public class TestMOGPHH {
    /**
     * @param args the command line arguments
     */
    public static double meanTime = 25;
    public static int[] SimSeed = {2734,72734,	72605,12628,20029,1991,
                        55013,84005,54972,80531,45414,25675,
                        79032,14882,17423,2798,77874,3805,
                        21671,51204,85187,76476,12363,92832,
                        36503,25237,26178,13614,50288,26279,
    70226,66382,52542,98151,83655,67162,39059,16816,92994,71343,35203,10876,79203,74695,66278,33801,72288,90659,51540,35071
    };
    //public static double[] utilisation = {0.7,0.8,0.9};
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    /*
    double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
    int[] numbeOfMachines = {4,5,6,10,20};
    String[] lowers = {"miss","full"};
    String[] dists = {"expo","erlang2","uniform"};
    */
    public static void main(String[] arg) throws FileNotFoundException, IOException{
        String[] rules = {"EVO"};
        TestMOGPHH.simDDAM(rules,true, true, 5, 500, 2000);
    }
    public static double[][][][][][][] simDDAM(String[] rules, boolean writeJobInfo, boolean writeReport, int rep, int warm, int end) throws FileNotFoundException, IOException {
            String detailedReport = "\n dist s m u MF Cmax PercentT MT Tmax \n";
            double[] utilisation = {0.8,0.9};
            double[] allowance = {3,5,7};
            int[] numbeOfMachines = {10};
            String[] lowers = {"miss"};
            String[] dists = {"duniform"};
            double[] Mean = {25};
            double[][][][][][][] detailedTest = new double [rules.length][Mean.length][numbeOfMachines.length][utilisation.length][allowance.length][5][rep];
            //*
                SmallStatistics ACmax = new SmallStatistics();
                SmallStatistics AMT = new SmallStatistics();
                SmallStatistics ATmax = new SmallStatistics();
                SmallStatistics AMF = new SmallStatistics();
                SmallStatistics APercentT = new SmallStatistics();
            outerLoop:

            for (String dist : dists){for (String s : lowers){ for (int m : numbeOfMachines){ for (String rule : rules){ for (double mean : Mean){  for (double u : utilisation){ 
                SmallStatistics Cmax = new SmallStatistics();
                SmallStatistics MT = new SmallStatistics();
                SmallStatistics Tmax = new SmallStatistics();
                SmallStatistics MF = new SmallStatistics();
                SmallStatistics PercentT = new SmallStatistics();

                String detailStat ="";
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
                    } else if ("duniform".equals(dist)) {
                        distribution = "duniform";
                        param = 1;
                    }
                    meanTime = mean;
                    DynamicJSPFramework.revisit = true;
                    //DynamicJSPFramework.equalWeightProbability = true;
                    DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],m,2,14,u,u,meanTime,distribution,param,warm,end);
                    //SampleArray sa = new SampleArray(20); 
                    //set dispatching rule
                    Machine.priorityType PT = Machine.priorityType.SPT;
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
                            double estimate = ((SOTR * (SOTR * OT)) + OT) + (0.04672965 + OT);
                            estimatedFlowTime+=estimate;
                            newjob.getOperations()[i].setWait(correctValue(estimate,OT));
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
                        

                        //newjob.assignDuedate(al*TOT);
                        newjob.assignDuedate(allowance[(int)(allowance.length*jspDynamic.getRandomNumber())]*newjob.getTotalProcessingTime());
                        newjob.setFinishTime(newjob.getReleaseTime()+estimatedFlowTime);
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
                                if (M.getQueue().size()>1){
                                    for (Job J:M.getQueue()) {
                                        double RJ = J.getReadyTime();
                                        double rJ = J.getReleaseTime();
                                        double RO = J.getNumberRemainingOperations();
                                        double RT = J.getRemainingProcessingTime();
                                        double PR = J.getCurrentOperationProcessingTime();
                                        double W = J.getWeight();
                                        double DD = J.getDuedate();
                                        double RM = M.getReadyTime();
                                        double MP = M.getMachineProgress();
                                        double SJ = J.getDuedate() - (M.getReadyTime() + J.getRemainingProcessingTime());
                                        double CWR = M.getCritialRatioOfQueue(jspDynamic.getCriticalMachineID());
                                        double CMI = jspDynamic.getCriticalMachineIdleness();
                                        double WR = M.getWorkLoadRatio();
                                        double BWR = M.getBottleNeckRatioOfQueue(jspDynamic.getBottleneckMachineID());
                                        double DJ = M.getDeviationInQueue();
                                        double WINQ = J.getWorkloadNextQueue(jspDynamic.getMachines());
                                        double FDD = J.getReleaseTime()+J.getTotalProcessingTime()-RT+PR;
                                        double WT = max(J.getReadyTime() - M.getReadyTime(), 0 );
                                        double EFT = J.getFinishTime()-J.getReleaseTime();
                                        double EWT = J.getCurrentOperationWaitingTime();
                                        double ENWT = J.getNextOperationWaitingTime();
                                        double NPR = J.getNextOperationProcessingTime();
                                        double APR = M.getQueueWorkload()/M.getNumberofJobInQueue();
                                        if (min((RM + WT) , DD)==0) System.out.println("sfdf");
                                        double x = 0;
                                        //double r1 = (((IF(SJ, RJ, max(PR , WT)) + (max(RO , RT) + (RJ / IF(SJ, PR, rJ)))) - WINQ) + (((max(RO , RT) + IF(SJ, IF(SJ, PR, rJ), rJ)) + (-1*(IF(SJ, PR, rJ)) + IF(SJ, DD / PR, rJ))) - min(SJ , (WINQ * min(PR , WINQ))))) - Abs((rJ - RT) +1*min(min(SJ , IF(SJ, PR, rJ)) , (rJ - RT)));
                                        //double r2 = (-rJ - SJ + max(RO , RT) + (((((RJ / PR) + max(RO , RT)) + max(PR , max(RO , RT))) + (-PR - RT)) - 0.8968051)) - Abs(IF(min(SJ , WINQ), WINQ, DD / PR) +Abs(min(SJ , WINQ)));
                                        //double r3 = ((max(RM , (Abs(min(WT , SJ)) * (RT * PR))) / PR) / Abs(PR + RO)) / Abs(max(((PR * max(RT , max(APR , SJ))) * (PR + WINQ)) , div(((Abs(PR) * (RT * PR)) * DD) , Abs(min(WT , (SJ / APR))))));
                                        //double r4 = div((max((PR * APR) , (Abs(min(WT , SJ)) * WINQ)) / PR) , WINQ) / Abs(max(((PR * PR) * (max(Abs(RT) , max(APR , SJ)) + min(WT , (SJ / APR)))) , div(((PR * WINQ) * DD) , Abs(min(WT , (SJ / APR))))));
                                        //double r5 = (((((RT / rJ) + rJ) / max(min(DD , SJ) , RT)) - min(-(IF(SJ, RJ, NPR) / (SJ + WINQ)) , DD)) + (-WINQ + (-RO - min(min(SJ , WINQ) , rJ)))) + ((max(SJ , rJ) + ((IF(SJ, RJ, -RO) / PR) - (rJ + max((WINQ + PR) , 0.37063625)))) - NPR);
                                        //double r6 = Abs((((RJ / SJ) / PR) / PR) / max(APR , WINQ)) * Abs(((((SJ / APR) - SJ) / min(RT , SJ)) * min(RT , SJ)) / min(((RJ / SJ) * (RJ / SJ)) , RT));
                                        //double r7 = ((SJ / APR) + (-(min(min(RT , PR) , rJ) - (min(PR , SJ) - min(WINQ , SJ))) - min(RT , SJ))) + ((RT + (((RJ / SJ) / PR) * min(RT , SJ))) + (WINQ - max(APR , WINQ)));
                                        //double r8 = Abs((((RJ / SJ) / PR) / PR) / min((min(PR , (RJ / SJ)) - (SJ / rJ)) , (DD - WINQ))) * Abs(((PR / (min(PR , RM) + WINQ)) * min(RT , SJ)) / min((RT * min(PR , (RJ / SJ))) , RT));
                                        //double r9 = ((((-rJ - div(-rJ , (SJ - RT))) - (WINQ + (0.5583345 + PR))) - max(Abs(-RT +rJ) , -rJ)) - ((SJ - max(Abs(SJ - RT) , SJ)) - (-rJ / min((RM + WT) , DD)))) + RT - 1 - SJ + DD - APR * PR - WINQ - 3*(0.5583345 + PR);
                                        //double r10 = max((WT-2*SJ + 0.563716 - APR * PR) , ((Abs(IF(PR + SJ, RM / PR, 2*SJ - WT)) / max(PR + 0.024362229 , max(SJ , RT))) / Abs(max(-APR + WINQ , APR) / (RM / PR))));
//duniform miss 10 0.85         706.4659408565201 2310.7853270208943 0.3948841581308568 72.73660400558101 678.027372166783
//duniform miss 10 0.85         665.7556726604303 2334.885706786335 0.349029054660297 56.72837705046707 737.249260098178

                                        //String rule = "RR";
                                        if ("SPT".equals(rule)) J.addPriority(-PR);
                                        else if("CR_SPT".equals(rule)) J.addPriority(-(DD-RM)/RT-PR);
                                        else if("CR".equals(rule)) J.addPriority(-(DD-RM)/RT);
                                        else if("EDD".equals(rule)) J.addPriority(-DD);
                                        else if("FDD".equals(rule)) J.addPriority(-FDD);
                                        else if("FIFO".equals(rule)) J.addPriority(-RJ);
                                        else if("LIFO".equals(rule)) J.addPriority(RJ);
                                        else if("LPT".equals(rule)) J.addPriority(PR);
                                        else if("AVPRO".equals(rule)) J.addPriority(J.getTotalProcessingTime()/J.getNumberOperations());
                                        else if("LWKR".equals(rule)) J.addPriority(-RT);
                                        else if("LWKR_SPT".equals(rule)) J.addPriority(-RT-PR);
                                        else if("MOD".equals(rule)) J.addPriority(-max(J.getReleaseTime()+2*(J.getTotalProcessingTime()-RT+PR),RM+PR));
                                        else if("MOPNR".equals(rule)) J.addPriority(RO);
                                        else if("MWKR".equals(rule)) J.addPriority(RT);
                                        else if("NPT".equals(rule)) J.addPriority(-J.getNextOperationProcessingTime());
                                        else if("OPFSLK_PT".equals(rule)) J.addPriority(max(RM+PR-FDD,0)/PR);
                                        else if("PW".equals(rule)) J.addPriority(-max(J.getReadyTime() - M.getReadyTime(), 0 ));
                                        else if("SL".equals(rule)) J.addPriority(-min(SJ,0));
                                        else if("Slack".equals(rule)) J.addPriority(-SJ);
                                        else if("Slack_OPN".equals(rule)) J.addPriority(-IF(SJ,SJ/RO,SJ*RO));
                                        else if("Slack_RPT_SPT".equals(rule)) J.addPriority(-SJ/RT-PR);
                                        else if("SPT_PW".equals(rule)) J.addPriority(-PR-max(J.getReadyTime() - M.getReadyTime(), 0 ));
                                        else if("SPT_PW_FDD".equals(rule)) J.addPriority(-PR-max(J.getReadyTime() - M.getReadyTime(), 0 ) - FDD);
                                        else if("WINQ".equals(rule)) J.addPriority(-WINQ);
                                        else if("PT_WINQ".equals(rule)) J.addPriority(-PR-WINQ);
                                        else if("2PT_WINQ_NPT".equals(rule)) J.addPriority(-2*PR-WINQ-J.getNextOperationProcessingTime());
                                        else if("PT_WINQ_SL".equals(rule)) J.addPriority(-PR-WINQ-min(DD-RM-RT,0));
                                        else if("PT_WINQ_NPT_WSL".equals(rule)) J.addPriority(-PR-WINQ-J.getNextOperationProcessingTime()-min(DD-WINQ-RM-RT,0));
                                        else if("ATC".equals(rule)) J.addPriority((1/PR)*Math.exp(-maxPlus((DD-RM-PR-(2+1)*(RT-PR))/(3*M.getQueueWorkload()/M.getNumberofJobInQueue()))));
                                        else if("COVERT".equals(rule)) J.addPriority((1/PR)*maxPlus(1-maxPlus((DD-RM-RT))/(2*2*RT)));
                                        else if("RR".equals(rule)) J.addPriority(-((DD-RM-RT)*Math.exp(-u)*PR/RT+Math.exp(u)*PR+1*J.getNextOperationProcessingTime()));
                                        else if("EVO".equals(rule)) J.addPriority(x);
                                        else
                                            System.out.println("Wroooooooooooooooooooooooooong");

                                    }
                                    jspDynamic.sortJobInQueue(M);
                                }
                                 //*/
                                //jspDynamic.calculatePriority(M);
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
                    
                    Cmax.add(jspDynamic.getCmax());
                    MF.add(jspDynamic.getMeanFlowtime());
                    PercentT.add(jspDynamic.getPercentTardiness());
                    MT.add(jspDynamic.getMeantardiness());
                    Tmax.add(jspDynamic.getMaxTardiness());
                    ACmax.add(jspDynamic.getCmax());
                    AMF.add(jspDynamic.getMeanFlowtime());
                    APercentT.add(jspDynamic.getPercentTardiness());
                    AMT.add(jspDynamic.getMeantardiness());
                    ATmax.add(jspDynamic.getMaxTardiness());
                    detailStat += jspDynamic.getMeanFlowtime()+","+jspDynamic.getCmax() +","+ jspDynamic.getPercentTardiness()+","+jspDynamic.getMeantardiness()+","+jspDynamic.getMaxTardiness()+",\n";
                    //jspDynamic.printAverageMachinesUtilisation();
                }
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
                int e =-1;
                for (int j = 0; j < allowance.length; j++) {
                    //if (al==allowance[j]) e=j;
                }
                int f =-1;
                for (int j = 0; j < Mean.length; j++) {
                    if (mean==Mean[j]) f=j;
                }
                int g =-1;
                for (int j = 0; j < rules.length; j++) {
                    if (                rule == null ? rules[j] == null : rule.equals(rules[j])) g=j;
                }
                //System.out.println("double[] stat"+rule+"_M"+(int)mean+"U"+(int)(100*u)+"A"+(int)al+"= {" + detailStat+"};");
                //System.out.println("stat["+g+"]["+f+"]["+d+"]["+e+"] =" + "OneDArrayToTwoDArray(" +"stat"+rule+"_M"+(int)mean+"U"+(int)(100*u)+"A"+(int)al+",30,5)"+";");
                if (writeReport){
                    String setting = dist + " " + s + " " + m + " " + u + " ";
                    detailedReport+=setting + "\t" +  MF.getAverage() + " " + Cmax.getAverage()+ " " + PercentT.getAverage() + " " + MT.getAverage() + " " +
                            Tmax.getAverage() + "\n";
                    System.out.println(setting + "\t" +  MF.getAverage() + " " + Cmax.getAverage()+ " " + PercentT.getAverage() + " " + MT.getAverage() + " " +
                            Tmax.getAverage() + "\n");
                }
                
                        }
                    }
                
                        }
                    }
                }
            }
                System.out.println(detailedReport);
                System.out.println("Training Results \t" +  AMF.getAverage() + " " + ACmax.getAverage()+ " " + APercentT.getAverage() + " " + AMT.getAverage() + " " +
                            ATmax.getAverage() + "\n");
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
    public static double Abs(double a){
        return Math.abs(a);
    }
    public static double IF(double a, double b, double c){
        if (a>=0){
            return b;
        }else{
            return c;
        }
    }
    private static double maxPlus(double a){
        if (a>0) return a;
        else return 0;
    }
    private static double correctValue(double val, double default_val){
        if (val < 0.0 || val > 99999 || Double.isNaN(val)){
            return default_val;
        }
        return val;
    }
}
//Fitness: [701.4847 12038.6875 0.0624012 131.96783 10462.446 25295.465 min]
//R=0 S=0.22155089735661831
//Tree 0:
//PR * ((rJ * (((-1*)(Abs(SJ)) - If(DD / SJ, SJ, EWT + PR)) - (If(SJ, WINQ, RJ) Min ENWT))) - (EFT * WINQ))
//Tree 1:
//((RWL / RWL) + (N + (((SOTR * (APR + OT)) + OT) + OT))) * ((SOTR * SOTR) + OT)