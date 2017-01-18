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

public class R3Cmax {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework
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
                jspTesting[i].reset();
                int N = jspTesting[i].getNumberofOperations();
                jspTesting[i].initilizeSchedule();
                int nScheduledOp = 0;
                Machine.priorityType PT = Machine.priorityType.CONV;
                //choose the next machine to be schedule
                while (nScheduledOp<N){
                    Machine M = jspTesting[i].Machines[jspTesting[i].nextMachine()];
                    int DIV = (int)(M.getDeviationInQueue()*9.99);
                    int CWR = (int)(M.getCritialRatioOfQueue(jspTesting[i].getCriticalMachineID())*9.99);
                    int CMI = (int)(jspTesting[i].getCriticalMachineIdleness()*9.99);
                    int MP = (int)(M.getMachineProgress()*9.99);
                    //*
                    jspTesting[i].setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
                    if (CWR>=9) {
                        jspTesting[i].setNonDelayFactor(0.06940693);
                    } else{
                        if (MP<=9){
                            jspTesting[i].setNonDelayFactor(0.12828052);
                        }
                    }

                    jspTesting[i].setInitalPriority(M);
                    for (Job J:M.getQueue()) {
                        double RJ = J.getReadyTime();
                        double RO = J.getNumberRemainingOperations();
                        double RT = J.getRemainingProcessingTime();
                        double PR = J.getCurrentOperationProcessingTime();
                        double W = J.getWeight();
                        double DD = J.getDuedate();
                        double RM = M.getReadyTime();
                        if (CWR>=9) {
                            J.addPriority((RJ+0.594845)*(RT+PR)/(W+PR));
                        } else{
                            J.addPriority((RT-PR)/(W+PR));
                        }
                    }
                    jspTesting[i].sortJobInQueue(M);
                    //*/
                    Job J = M.completeJob();
                    if (!J.isCompleted()) jspTesting[i].Machines[J.getCurrentMachine()].joinQueue(J);
                    nScheduledOp++;
                }
                result[ds].add(jspTesting[i].getDevLBCmax());
                if (jspTesting[i].getDevLBCmax()==0) hits++;
                System.out.println(jspTesting[i].instanceName +" & " +jspTesting[i].getCmax());
            }
        }
        //jsp.schedule();
        System.out.println("*************************************************************************");
        System.out.println("[ & " + formatter.format(result[0].getMin()) + " & "
                 + formatter.format(result[0].getAverage()) + " & "  + formatter.format(result[0].getMax()) +
                 " & " + formatter.format(result[1].getMin()) + " & "
                 + formatter.format(result[1].getAverage()) + " & "  + formatter.format(result[1].getMax()) + "]");
    }
}