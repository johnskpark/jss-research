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
import java.util.Random;

public class TestLocalSearch {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    static Random rnd = new  Random(222);
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework

        int hits = 0;
        SmallStatistics[] result = new SmallStatistics[2];
        result[0] = new SmallStatistics();
        result[1] = new SmallStatistics();
        for (int ds = 0; ds < 2; ds++) {
            LocalSearchJSPFramework[] jspTesting = new LocalSearchJSPFramework[105];
            for (int i = 0; i < jspTesting.length; i++) {
                jspTesting[i] = new LocalSearchJSPFramework();
                //jspTesting[i].getJSPdata(i*2 + ds + 1);
                jspTesting[i].getJSPdata(81);
            }
           //DMU - instances (1-80)//la - instances (81-120)
            //mt - instances (121/-123)//orb - instances (124-133)//ta -instances (134-173)
            //////////////////////////////////////////////

            for (int i = 0; i < jspTesting.length; i++) {
                int[] x = new int[jspTesting[i].Jobs.length*jspTesting[i].Machines.length];
                for (int j = 0; j < x.length; j++) {
                    x[j] = j;
                }
                //jspTesting[i].setJobRanks(x);
                jspTesting[i].getScheduleFromRankMaTrix();
                System.out.println(jspTesting[i].instanceName + " --> Cmax " + jspTesting[i].getCmax() + " --> TWT " + jspTesting[i].getTotalWeightedTardiness());

                jspTesting[i].printMachineSequence();
                jspTesting[i].printJobRanks();
                System.out.println(" --> Cmax " + jspTesting[i].getCmax());
                jspTesting[i].storeBestRank();
                //move here
                Job job = jspTesting[i].getFinalCompletedJob();
                Operation op = job.getKthOperation(0);
                op.upRank(3);
                System.out.println("***********************************************************");
                jspTesting[i].reset();
                jspTesting[i].getScheduleFromRankMaTrix();

                jspTesting[i].printMachineSequence();
                jspTesting[i].printJobRanks();
                System.out.println(" --> Cmax " + jspTesting[i].getCmax());
                job = jspTesting[i].getFinalCompletedJob();
                op = job.getLargestWaitingTime();
                op.upRank(1);                
                System.out.println("***********************************************************");

                break;
            }
                break;
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
}