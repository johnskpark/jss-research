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

public class IterativeMain {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework
for (double b = 0.5; b < 0.51; b+=0.1) {
    for (double k = 4.40; k < 4.5; k+=0.5) {
        Machine.b = 0.5; //0.2 - ATCnon-delay
        Machine.k = 1.5; //1.4 - ATCnon delay
        int hits = 0;
        SmallStatistics[] result = new SmallStatistics[2];
        result[0] = new SmallStatistics();
        result[1] = new SmallStatistics();
        for (int ds = 0; ds < 2; ds++) {
            JSPFramework[] jspTesting = new JSPFramework[105];
            for (int i = 0; i < jspTesting.length; i++) {
                jspTesting[i] = new JSPFramework();
                jspTesting[i].getJSPdata(i*2 + ds + 1);
            }
           //DMU - instances (1-80)//la - instances (81-120)
            //mt - instances (121/-123)//orb - instances (124-133)//ta -instances (134-173)
            //////////////////////////////////////////////
            for (int i = 0; i < jspTesting.length; i++) {
                double tempObj = Double.POSITIVE_INFINITY;
                jspTesting[i].resetALL();
                boolean firstIteration = true;
                do{
                    //start evaluate schedule
                    jspTesting[i].reset();
                    int N = jspTesting[i].getNumberofOperations();
                    jspTesting[i].initilizeSchedule();
                    int nScheduledOp = 0;

                //choose the next machine to be schedule
                    while (nScheduledOp<N){

                        Machine M = jspTesting[i].Machines[jspTesting[i].nextMachine()];

                        jspTesting[i].setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
                        jspTesting[i].setPriorityType(Machine.priorityType.ATC);
                        jspTesting[i].setNonDelayFactor(0.3);
                        //*
                        jspTesting[i].setInitalPriority(M);
                        for (Job J:M.getQueue()) {
                            double RJ = J.getReadyTime();
                            double RO = J.getNumberRemainingOperations();
                            double RT = J.getRemainingProcessingTime();
                            double PR = J.getCurrentOperationProcessingTime();
                            double W = J.getWeight();
                            double DD = J.getDuedate();
                            double RM = M.getReadyTime();
                            double RWT = J.getCurrentOperationWaitingTime();
                            double RFT = J.getFinishTime();
                            double RNWT = J.getNextOperationWaitingTime();
                            int nextMachine = J.getNextMachine();

                            if (nextMachine==-1){
                                RNWT=0;
                            } else {
                                RNWT = J.getNextOperationWaitingTime();
                                if (RNWT == -1){
                                RNWT = jspTesting[i].getMachines()[nextMachine].getQueueWorkload()/2.0;
                                }
                            }

                            //J.addPriority((W/PR)*Math.exp(-maxPlus((DD-RM-PR-(RT-PR+J.getRemainingWaitingTime(M)))/(3*M.getQueueWorkload()/M.getNumberofJobInQueue()))));  //iATC
                            //J.addPriority((PR*PR*0.614577*(-RM-RM/W)-RT*PR*RT/W)
                              //      -(RT*PR/(W-0.5214191)-RM/W*PR*0.614577+RT*PR/(W-0.5214191)*2*RM/W));
                            J.addPriority(((W/PR)*((W/PR)/(RFT*RFT)))/(max(div((RFT-RT),(RWT/W)),IF(RFT/W-max(RFT-RT,DD),DD,RFT))+DD/RFT+RFT/W-max(RFT-RFT/W,DD)));
                        }
                        //jspTesting[i].calculatePriority(M);
                        jspTesting[i].sortJobInQueue(M);
                        Job J = M.completeJob();
                        if (!J.isCompleted()) jspTesting[i].Machines[J.getCurrentMachine()].joinQueue(J);
                        nScheduledOp++;
                    }
                    double currentObj = -100;
                    //currentObj = jspTrainning[instance].getCmax();
                    currentObj = jspTesting[i].getTotalWeightedTardiness();
                    if (tempObj > currentObj){
                        //if (tempObj!=Double.POSITIVE_INFINITY) System.out.println("Yeah");
                        tempObj = currentObj;
                    }
                    else {
                        break;
                    }
                    jspTesting[i].recordSchedule();
                    firstIteration = false;
                } while(true);
                result[ds].add(jspTesting[i].getDevREFTotalWeightedTardiness(tempObj));
                if (jspTesting[i].getDevLBCmax()==0) hits++;
                System.out.println(jspTesting[i].instanceName + " & "+ tempObj);
            }
        }
        //jsp.schedule();
        //*
        System.out.println("*************************************************************************");
        System.out.println("[ & " + formatter.format(result[0].getMin()) + " & "
                 + formatter.format(result[0].getAverage()) + " & "  + formatter.format(result[0].getMax()) +
                 " & " + formatter.format(result[1].getMin()) + " & "
                 + formatter.format(result[1].getAverage()) + " & "  + formatter.format(result[1].getMax()) + "]");
         //*/
        System.out.print(""+formatter.format(result[0].getAverage()) + " ");
    }
    System.out.println("");
}
    }

    /*
     * get maxPlus
     */
    private static double  maxPlus(double a){
        if (a>0) return a;
        else return 0;
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
}