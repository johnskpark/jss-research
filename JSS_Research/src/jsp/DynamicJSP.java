/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */

package jsp;

import SmallStatistics.SmallStatistics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DynamicJSP {

    /**
     * @param args the command line arguments
     */
    public static int[] SimSeed = {2734, 72734, 72605, 12628, 20029, 1991,
                            55013, 84005, 54972, 80531, 45414, 25675,
                            79032, 14882, 17423,  2798, 77874,  3805,
                            21671, 51204, 85187, 76476, 12363, 92832,
                            36503, 25237, 26178, 13614, 50288, 26279};

    public static double[] utilisation = {0.5, 0.6, 0.7, 0.8, 0.9};
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework
        SmallStatistics[] result = new SmallStatistics[2];
        result[0] = new SmallStatistics();
        result[1] = new SmallStatistics();
        for (int ds = 0; ds < 30; ds++) {
            for (double u : utilisation){
            DynamicJSPFramework jspDynamic = new DynamicJSPFramework(SimSeed[ds],6,6,6,u,u,1,"erlang",1,50000,100000);

            //set dispatching rule
            Machine.b = 0.5; //0.2 - ATCnon-delay
            Machine.k = 1.5; //1.4 - ATCnon delay
            Machine.priorityType PT = Machine.priorityType.ATC;
            jspDynamic.setPriorityType(PT);
            jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
            jspDynamic.setNonDelayFactor(0.3);
            //////////////////////////////////////////////
            jspDynamic.setNextArrivalTime();
            while (!jspDynamic.isStop()) {
                if (jspDynamic.isNextArrivalEvent()) {
                    Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
                    newjob.assignDuedate(1.3*newjob.getTotalProcessingTime());
                    jspDynamic.setNextArrivalTime();
                } else {
                    jspDynamic.unplanAll();
                    do {
                        int nextMachine = jspDynamic.nextMachine();
                        if (nextMachine<0)
                            break;
                        Machine M = jspDynamic.machines[nextMachine];
/* R1_Cmax
                        int DIV = (int)(M.getDeviationInQueue()*9.99);
                        int CWR = (int)(M.getCritialRatioOfQueue(jspDynamic.getCriticalMachineID())*9.99);
                        int CMI = (int)(jspDynamic.getCriticalMachineIdleness()*9.99);
                        int WR = (int)(M.getWorkLoadRatio()*9.99);

                        if (CMI>=1) {
                            if (CWR>=2){
                                if (CWR>=8){
                                    if (CMI<=0){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                        jspDynamic.setNonDelayFactor(0.39363864);
                                    }else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.13117726);
                                    }
                                }
                                else{
                                    if (DIV<=2){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.SPT);
                                        jspDynamic.setNonDelayFactor(0.19862764);
                                    }
                                    else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.10229393);
                                    }
                                }
                            }
                            else {
                                if (CMI<=0){
                                    jspDynamic.setPriorityType(MACHINE.priorityType.SPT);
                                    jspDynamic.setNonDelayFactor(0.19862764);
                                }
                                else {
                                    if (CWR>=1){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.10229393);
                                    }
                                    else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.13117726);
                                    }
                                }
                            }
                        }
                        else{
                            if (CWR>=1){
                                if (CWR>=8){
                                    jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                    jspDynamic.setNonDelayFactor(0.014232537);
                                }
                                else{
                                    if (DIV<=2){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.SPT);
                                        jspDynamic.setNonDelayFactor(0.19862764);
                                    }
                                    else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.13117726);
                                    }
                                }
                            }
                            else{
                                if (CWR>=8){
                                    if (CMI<=0){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                        jspDynamic.setNonDelayFactor(0.014232537);
                                    }
                                    else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LPT);
                                        jspDynamic.setNonDelayFactor(0.83020926);
                                    }
                                }
                                else{
                                    if (DIV<=1){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.SPT);
                                        jspDynamic.setNonDelayFactor(0.19862764);
                                    }
                                    else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LRM);
                                        jspDynamic.setNonDelayFactor(0.10229393);
                                    }
                                }
                            }
                        }
                        jspDynamic.calculatePriority(M);
                        //*/

/* R2_Cmax
                        jspDynamic.setInitalPriority(M);
                        for (JOB J:M.getQueue()) {
                            double r = J.getReleaseTime();
                            double RJ = J.getReadyTime();
                            double RO = J.getNumberRemainingOperations();
                            double RT = J.getRemainingProcessingTime();
                            double PR = J.getCurrentOperationProcessingTime();
                            double W = J.getWeight();
                            double DD = J.getDuedate();
                            double RM = M.getReadyTime();
                            if (RM-r>1.3*(6-RO))
                                J.addPriority(100000/r);
                            else J.addPriority((PR+RM)*W/PR-(RJ+RM)*PR/RT+RT+DD/(PR+RM)*RT*W/(PR*PR)+RT/PR*(RJ+RM));
                            //else J.addPriority(-PR);
                        }
                        jspDynamic.sortJobInQueue(M);
                         //*/

/* R3_Cmax
                        int DIV = (int)(M.getDeviationInQueue()*9.99);
                        int CWR = (int)(M.getCritialRatioOfQueue(jspDynamic.getCriticalMachineID())*9.99);
                        int CMI = (int)(jspDynamic.getCriticalMachineIdleness()*9.99);
                        int MP = (int)(M.getMachineProgress()*9.99);
                        //*
                        jspDynamic.setScheduleStrategy(MACHINE.scheduleStrategy.HYBRID);
                        if (CWR>=9) {
                            jspDynamic.setNonDelayFactor(0.06940693);
                        } else{
                            if (MP<=9){
                                jspDynamic.setNonDelayFactor(0.12828052);
                            }
                        }

                        jspDynamic.setInitalPriority(M);
                        for (JOB J:M.getQueue()) {
                            double r = J.getReleaseTime();
                            double RJ = J.getReadyTime();
                            double RO = J.getNumberRemainingOperations();
                            double RT = J.getRemainingProcessingTime();
                            double PR = J.getCurrentOperationProcessingTime();
                            double W = J.getWeight();
                            double DD = J.getDuedate();
                            double RM = M.getReadyTime();
                            //double x = 0;
                            //if ((RM>r+1.5*(6-RO-1))&&(true))
                            //if (true)
                            //if (RM-RJ>2)
                                //J.addPriority(10000/(RJ/RM));
                            //else{
                                if (CWR>=9) {
                                    J.addPriority((RJ+0.594845)*(RT+PR)/(W+PR));
                                } else{
                                    J.addPriority((RT-PR)/(W+PR));
                                }
                                //J.addPriority((-r));
                            //}
                        }
                        jspDynamic.sortJobInQueue(M);
                         //*/

/* R1_TWT
                        int DIV = (int)(M.getDeviationInQueue()*9.99);
                        int CWR = (int)(M.getCritialRatioOfQueue(jspDynamic.getCriticalMachineID())*9.99);
                        int CMI = (int)(jspDynamic.getCriticalMachineIdleness()*9.99);
                        int BWR = (int)(M.getBottleNeckRatioOfQueue(jspDynamic.getBottleneckMachineID())*9.99);
                        int MP = (int)(M.getMachineProgress()*9.99);

                        if (DIV>=8){
                            if (BWR>=9){
                                if (DIV<=8){
                                    jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                    jspDynamic.setNonDelayFactor(0.42680368);
                                }else{
                                    if (MP<=0){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                        jspDynamic.setNonDelayFactor(0.43658486);
                                    } else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LPT);
                                        jspDynamic.setNonDelayFactor(0.36472374);
                                    }
                                }
                            }else{
                                jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                jspDynamic.setNonDelayFactor(0.06532474);
                            }
                        }else{
                            if (BWR>=2){
                                if (DIV<=2){
                                    jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                    jspDynamic.setNonDelayFactor(0.43658486);
                                }else{
                                    if (DIV<=2){
                                        jspDynamic.setPriorityType(MACHINE.priorityType.LPT);
                                        jspDynamic.setNonDelayFactor(0.36472374);
                                    }else{
                                        jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                        jspDynamic.setNonDelayFactor(0.38936377);
                                    }
                                }
                            }else{
                                if (DIV<=2){
                                    jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                    jspDynamic.setNonDelayFactor(0.43658486);
                                }else{
                                    jspDynamic.setPriorityType(MACHINE.priorityType.WSPT);
                                    jspDynamic.setNonDelayFactor(0.18058573);
                                }
                            }
                        }

                        jspDynamic.calculatePriority(M);
                        //*/

/* R2_TWT
                        jspDynamic.setInitalPriority(M);
                        for (JOB J:M.getQueue()) {
                            double RJ = J.getReadyTime();
                            double RO = J.getNumberRemainingOperations();
                            double RT = J.getRemainingProcessingTime();
                            double PR = J.getCurrentOperationProcessingTime();
                            double W = J.getWeight();
                            double DD = J.getDuedate();
                            double RM = M.getReadyTime();
                            J.addPriority((PR*PR*0.614577*(-RM-RM/W)-RT*PR*RT/W)
                                -(RT*PR/(W-0.5214191)-RM/W*PR*0.614577+RT*PR/(W-0.5214191)*2*RM/W));
                        }
                        jspDynamic.sortJobInQueue(M);
                         //*/

//* R3_TWT
                        int DIV = (int)(M.getDeviationInQueue()*9.99);
                        if (DIV<=4) jspDynamic.setNonDelayFactor(0.3309732);
                        else jspDynamic.setNonDelayFactor(0.16265061);
                        jspDynamic.setInitalPriority(M);
                        for (Job J:M.getQueue()) {
                            double RJ = J.getReadyTime();
                            double RO = J.getNumberRemainingOperations();
                            double RT = J.getRemainingProcessingTime();
                            double PR = J.getCurrentOperationProcessingTime();
                            double W = J.getWeight();
                            double DD = J.getDuedate();
                            double RM = M.getReadyTime();
                            if (DIV<=4)
                                J.addPriority(-1*DD*PR/W);
                            else
                                J.addPriority(-1*(DD/W)*(RT/W));
                        }
                        jspDynamic.sortJobInQueue(M);
                         //*/

                        //jspDynamic.calculatePriority(M);
                        if (M.getPlannedStartTimeNextOperation()<=jspDynamic.getNextArrivalTime()){
                            Job J = M.completeJob();
                            if (!J.isCompleted()) jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
                            else jspDynamic.removeJobFromSystem(J);
                        } else
                            M.plan();
                    } while(true);
                    //System.out.println("!!");
                }
            }
            //System.out.println("Throughput: " + jspDynamic.getThroughput());
            //System.out.println("%Tardiness: " + jspDynamic.getPercentTardiness());
            System.out.print(jspDynamic.getNormalisedTotalWeightedTardiness() + " ");
            //System.out.print(jspDynamic.getCmax() + " ");
            //jspDynamic.printMachinesUtilisation();
            //jspDynamic.printFlowTimeStat();
            //jspDynamic.printTardinessStat();

            }
            System.out.println("");
        }
        //jsp.schedule();
        System.out.println("*************************************************************************");
        System.out.println("[ & " + formatter.format(result[0].getMin()) + " & "
                 + formatter.format(result[0].getAverage()) + " & "  + formatter.format(result[0].getMax()) +
                 " & " + formatter.format(result[1].getMin()) + " & "
                 + formatter.format(result[1].getAverage()) + " & "  + formatter.format(result[1].getMax()) + "]");
    }
}