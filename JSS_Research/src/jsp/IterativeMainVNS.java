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
import java.util.Arrays;

public class IterativeMainVNS {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");
    private static cern.jet.random.engine.RandomEngine engineJob;
    private static cern.jet.random.AbstractDistribution noise;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //loading JSP instance into JSPFramework
for (double b = 0.5; b < 0.51; b+=0.1) {
    for (double k = 4.40; k < 4.5; k+=0.5) {
        Machine.b = 0.5; //0.2 - ATCnon-delay
        Machine.k = 1.5; //1.4 - ATCnon delay
        int hits = 0;
        engineJob = new cern.jet.random.engine.MersenneTwister(99999);
        noise = new cern.jet.random.Normal(0, 1, engineJob);
        SmallStatistics[] result = new SmallStatistics[5];
        result[0] = new SmallStatistics();
        result[1] = new SmallStatistics();
        for (int ds = 0; ds < 2; ds++) {
            JSPFramework[] jspTesting = new JSPFramework[105];
            for (int i = 0; i < jspTesting.length; i++) {
                jspTesting[i] = new JSPFramework();
                jspTesting[i].getJSPdata(i*2 + 1);
            }
           //DMU - instances (1-80)//la - instances (81-120)
            //mt - instances (121/-123)//orb - instances (124-133)//ta -instances (134-173)
            //////////////////////////////////////////////
            for (int i = 0; i < jspTesting.length; i++) {
                double countEval = 0;
                double tempObj = Double.POSITIVE_INFINITY;
                double globalBest = Double.POSITIVE_INFINITY;
                boolean[] isApplied_Nk = new boolean[2]; //Arrays.fill(isApplied_Nk, Boolean.TRUE);
                int Nk = 0; // index of the Iterative dispatching rule to be used
                jspTesting[i].resetALL();
                boolean firstIteration = true;
                double countLargeStep = 0;
                do{
                    countEval++;
                    //start evaluate schedule
                    jspTesting[i].reset();
                    int N = jspTesting[i].getNumberofOperations();
                    jspTesting[i].initilizeSchedule();
                    int nScheduledOp = 0;

                //choose the next machine to be schedule
                    while (nScheduledOp<N){

                        Machine M = jspTesting[i].Machines[jspTesting[i].nextMachine()];

                        jspTesting[i].setScheduleStrategy(Machine.scheduleStrategy.HYBRID );
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
                            if (RWT == -1){
                                RWT = M.getQueueWorkload()/2.0;
                            }
                            //J.addPriority((W/PR)*Math.exp(-maxPlus((DD-RM-PR-(RT-PR+J.getRemainingWaitingTime(M)))/(3*M.getQueueWorkload()/M.getNumberofJobInQueue()))));  //iATC
                            //J.addPriority((PR*PR*0.614577*(-RM-RM/W)-RT*PR*RT/W)
                              //      -(RT*PR/(W-0.5214191)-RM/W*PR*0.614577+RT*PR/(W-0.5214191)*2*RM/W));
                            //J.addPriority(((W/PR)*((W/PR)/(RFT*RFT)))/(max(div((RFT-RT),(RWT/W)),IF(RFT/W-max(RFT-RT,DD),DD,RFT))+DD/RFT+RFT/W-max(RFT-RFT/W,DD))); //best TWT priorityIterative
                            if (Nk==0)
                                //J.addPriority(min(W/PR-RFT+min(RT,W/RFT),((min(RT,RNWT-RFT)/(RJ-min(RWT,RFT*0.067633785)))/(RJ-min(RWT,RFT*0.067633785))))/RFT);
                                J.addPriority(min(W/PR-RFT+min(RT,W/RFT),((div(min(RT,RNWT-RFT),(RJ-min(RWT,RFT*0.067633785))))/(RJ-min(RWT,RFT*0.067633785))))/RFT);
                            else
                                J.addPriority(min((((W/PR)/RFT)/(2*RNWT+max(RO,RFT)))/(PR+RNWT+max(RO,RFT)),((W/PR)/RFT)/PR)/RFT);
                        }
                        //jspTesting[i].calculatePriority(M);
                        jspTesting[i].sortJobInQueue(M);
                        Job J = M.completeJob();
                        if (!J.isCompleted()) jspTesting[i].Machines[J.getCurrentMachine()].joinQueue(J);
                        nScheduledOp++;
                    }
                    double currentObj = -100;
                    currentObj = jspTesting[i].getTotalWeightedTardiness();
                    if (tempObj > currentObj){
                        tempObj = currentObj;
                        jspTesting[i].recordSchedule();
                        Arrays.fill(isApplied_Nk, Boolean.FALSE);
                        //System.out.println("Improved!!!");
                    }
                    else {
                        isApplied_Nk[Nk] = true;
                        if (!isNextApplied(Nk, isApplied_Nk)) Nk = circleShift(Nk, isApplied_Nk.length);
                        else {
                            if (globalBest>tempObj) {
                                globalBest = tempObj;
                                jspTesting[i].storeBestRecordSchedule();
                            } jspTesting[i].restoreBestRecordSchedule();
                            if (countLargeStep<1) {
                                tempObj = Double.POSITIVE_INFINITY;
                                Arrays.fill(isApplied_Nk, Boolean.FALSE);
                                jspTesting[i].shakeRecordSchedule(noise, engineJob, globalBest);
                                countLargeStep++;
                            }
                            else break;
                        }
                    }
                    firstIteration = false;
                    
                } while(true);
                result[ds].add(jspTesting[i].getDevREFTotalWeightedTardiness(globalBest));
                if (jspTesting[i].getDevLBCmax()==0) hits++;
                System.out.println(jspTesting[i].instanceName + " & "+ globalBest + " & " + countEval);
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
    public static int circleShift(int current, int total){
            if (current+1==total) return 0;
            else return current+1;
        }
        public static boolean isNextApplied(int current, boolean[] set){
            if (current+1==set.length) return set[0];
            else return set[current+1];
        }
    double Cauchy (double formFactor, double median) {
	assert( formFactor > 0. );
	double u, v;
	do {
		 u = 2.0 * engineJob.nextDouble() - 1.0;
		 v = 2.0 * engineJob.nextDouble() - 1.0; }
	while (u*u+v*v>1.0 || (u==0.0&&v==0.0));
	if (u!=0)
		return (median + formFactor * (v/u));
	else
		return(median); }
}