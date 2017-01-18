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

public class Main {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework
for (double b = 0.5; b < 0.51; b+=0.1) {
    for (double k = 4.40; k < 4.5; k+=0.5) {
        Machine.b = 2.5; //0.2 - ATCnon-delay
        Machine.k = 2.5; //1.4 - ATCnon delay
        int hits = 0;
        SmallStatistics[] result = new SmallStatistics[2];
        result[0] = new SmallStatistics();
        result[1] = new SmallStatistics();
        for (int ds = 0; ds < 1; ds++) {
            JSPFramework[] jspTesting = new JSPFramework[100];
            for (int i = 0; i < jspTesting.length; i++) {
                jspTesting[i] = new JSPFramework();
                jspTesting[i].getJSPdata(i*1 + ds + 1);
            }
           //DMU - instances (1-80)//la - instances (81-120)
            //mt - instances (121/-123)//orb - instances (124-133)//ta -instances (134-173)
            //////////////////////////////////////////////
            for (int i = 0; i < jspTesting.length; i++) {
                jspTesting[i].reset();
                int N = jspTesting[i].getNumberofOperations();
                jspTesting[i].initilizeSchedule();
                int nScheduledOp = 0;

                //choose the next machine to be schedule
                while (nScheduledOp<N){

                    Machine M = jspTesting[i].Machines[jspTesting[i].nextMachine()];

                    jspTesting[i].setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
                    jspTesting[i].setPriorityType(Machine.priorityType.LRM);
                        double DJ = M.getDeviationInQueue();
                        double A = (0.6135312665606205 - 0.8407726040274823);
                        double B = DJ;
                        jspTesting[i].setNonDelayFactor(Math.abs(A)/(Math.abs(A)+Math.abs(B)));
                        jspTesting[i].setNonDelayFactor(0.3);
                    /*
                    jspTesting[i].setInitalPriority(M);
                    for (JOB J:M.getQueue()) {
                        double RJ = J.getReadyTime();
                        double RO = J.getNumberRemainingOperations();
                        double RT = J.getRemainingProcessingTime();
                        double PR = J.getCurrentOperationProcessingTime();
                        double W = J.getWeight();
                        double DD = J.getDuedate();
                        double RM = M.getReadyTime();
                        double PTav;
                        if (M.getNumberofJobInQueue()>0) PTav = M.getQueueWorkload()/M.getNumberofJobInQueue();
                        else PTav = 0;
                        double AR = max(J.getReadyTime() - M.getReadyTime(), 0 );
                        double WINQ = J.getWorkloadNextQueue(jspTesting[i].getMachines());
                        double FDD = J.getReleaseTime()+J.getTotalProcessingTime()-RT+PR;

                        //J.addPriority((PR*PR*0.614577*(-RM-RM/W)-RT*PR*RT/W)
                          //      -(RT*PR/(W-0.5214191)-RM/W*PR*0.614577+RT*PR/(W-0.5214191)*2*RM/W));
                        J.addPriority(1);
                    }
                    jspTesting[i].sortJobInQueue(M);
                    //*/
                    jspTesting[i].calculatePriority(M);
                    Job J = M.completeJob();
                    if (!J.isCompleted()) jspTesting[i].Machines[J.getCurrentMachine()].joinQueue(J);
                    nScheduledOp++;
                }
                result[ds].add(jspTesting[i].getDevLBCmax());
                if (jspTesting[i].getDevLBCmax()==0) hits++;
                System.out.println(jspTesting[i].instanceName +" & $" +(int)jspTesting[i].getCmax()+"$");
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
        private static double  POS(double a){
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