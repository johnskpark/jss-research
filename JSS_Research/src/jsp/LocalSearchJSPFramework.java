/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * Include the sub-routines to control processes in static JSP
 * 
 */

package jsp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;
import java.util.StringTokenizer;

    public class LocalSearchJSPFramework extends AbstractJSPFramework
    {
        private Random R;
        private double duedateFactor = 1.3;
        private int indexInstance;
        private int nOp;
        private int nJob;
        private int nMachine;
        public Job[] Jobs;
        public Machine[] Machines;
        private int CriticalMachineID;
        private int CurrentBottleneckID;
        private double TotalShopWorkload = 0;
        private double maxTotalJobWorkload = Double.NEGATIVE_INFINITY;
        private double maxTotalMachineWorkload = Double.NEGATIVE_INFINITY;
        private double refObjectiveValue = -1;
        public String instanceName = "";
        //scheduling rules
        Machine.priorityType PT;
        Machine.scheduleStrategy SS;
        double nonDelayFactor; // from 0 to 1; 0 is similar to nonDelay schedule,; 1 is similar to active schedule
        /*
         * return machines used in the shop
         */
        public Machine[] getMachines(){
            return Machines;
        }
        /*
         * reset the JSP environment
         */
        public void reset(){
            for (int i = 0; i < nJob; i++) {
                Jobs[i].reset();
            }
            for (int i = 0; i < nMachine; i++) {
                Machines[i].reset();
            }
        }
        /*
         * get JSP data by intance index
         */
        public void getJSPdata(int index) throws FileNotFoundException, IOException{
            indexInstance = index;
            getJSPdata(JSPInstanceDatabase.Instances[indexInstance]);
            instanceName = JSPInstanceDatabase.Instances[indexInstance];
        }
        /*
         * get Index of an instance from its name
         */
        public int getIndex(String instanceName){
            for (int i = 0; i < JSPInstanceDatabase.Instances.length; i++) {
                if (instanceName == null ? JSPInstanceDatabase.Instances[i] == null : instanceName.equals(JSPInstanceDatabase.Instances[i])){
                    return i;
                }
            }
            return -1;
        }
        /*
         * get JSP data by instance name
         */
        public void getJSPdata(String filename) throws FileNotFoundException, IOException{
            indexInstance = getIndex(filename);//Arrays.binarySearch(JSPInstanceDatabase.Instances, filename);
            String dirpath="/prob/";
            String inputFileName  = dirpath + filename + ".prb";
            InputStream in = getClass().getResourceAsStream(inputFileName);
            InputStreamReader inputFileReader = new InputStreamReader(in);
            BufferedReader inputStream   = new BufferedReader(inputFileReader);
            String inLine=inputStream.readLine();
            if (!filename.startsWith("DMU")) inLine=inputStream.readLine();
            StringTokenizer str= new StringTokenizer(inLine," \t");
            nJob =Integer.parseInt(str.nextToken());
            nMachine = Integer.parseInt(str.nextToken());
            //setup data
            int[][] MachineJobOrder = new int[nMachine][nJob];
            double[][] processingtime = new double[nJob][nMachine];
            double[][] processingtimeJM = new double[nJob][nMachine];
            int[][] route = new int[nJob][nMachine];
            double[] TotalProcessingTime_Machine = new double[nMachine];
            double[] job_release = new double[nJob];
            double[] job_duedate = new double[nJob];
            int j=0; // j: job index
            while (inputStream.ready()) {
                inLine=inputStream.readLine();
                str= new StringTokenizer(inLine," \t");
                for (int i=0;i<nMachine;i++){ //i: machine index;
                    route[j][i]=Integer.parseInt(str.nextToken());
                    processingtime[j][i]=Double.parseDouble(str.nextToken());
                    processingtimeJM[j][route[j][i]]=processingtime[j][i];
                    MachineJobOrder[route[j][i]][j]=i;
                    TotalProcessingTime_Machine[route[j][i]]+= processingtime[j][i];
                    TotalShopWorkload += processingtime[j][i];
                    nOp++;
                }
                j++;
            }
            Jobs = new Job[nJob];
            for (int i = 0; i < nJob; i++) {
                double weight = 0;
                if (i<0.2*nJob) weight = 4;
                else if (i<0.8*nJob) weight = 2;
                else weight = 1;
                Jobs[i] = new Job(i,nMachine, route[i], processingtime[i], job_release[0], job_duedate[i],duedateFactor,weight);
                if (maxTotalJobWorkload<Jobs[i].getTotalProcessingTime()) maxTotalJobWorkload=Jobs[i].getTotalProcessingTime();
            }
            Machines = new Machine[nMachine];
            for (int i = 0; i < nMachine; i++) {
                Machines[i] = new Machine(i, nJob, TotalProcessingTime_Machine[i], 0);
                if (maxTotalMachineWorkload<TotalProcessingTime_Machine[i]) maxTotalMachineWorkload=TotalProcessingTime_Machine[i];
            }
            inputFileReader.close();
            //getRefsolution();
            //refObjectiveValue = getTotalWeightedTardiness();
            //if (refObjectiveValue==0) System.out.println("zero reference objective value");
        }
        /*
         * find reference solution (based on some simple dispatching rule)
         */
        public void getRefsolution(Machine.priorityType pt){
            int N = getNumberofOperations();
            initilizeSchedule();
            int nScheduledOp = 0;
            PT = pt;
            //choose the next machine to be schedule
            while (nScheduledOp<N){
                Machine M = Machines[nextMachine()];
                setPriorityType(PT);
                setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);
                setNonDelayFactor(0.0);
                calculatePriority(M);
                Job J = M.completeJobLocalSearch();
                if (!J.isCompleted()) Machines[J.getCurrentMachine()].joinQueue(J);
                nScheduledOp++;
            }
        }
        /*
         * find reference solution (based on some simple dispatching rule)
         */
        public void getScheduleFromRankMaTrix(){
            int N = getNumberofOperations();
            initilizeSchedule();
            int nScheduledOp = 0;
            //choose the next machine to be schedule
            while (nScheduledOp<N){
                Machine M = Machines[nextMachine()];
                setPriorityType(PT);
                setScheduleStrategy(Machine.scheduleStrategy.ACTIVE);
                calculatePriorityBasedRank(M);
                Job J = M.completeJobLocalSearch();
                if (!J.isCompleted()) Machines[J.getCurrentMachine()].joinQueue(J);
                nScheduledOp++;
            }
        }        
        /*
         * set priority type
         */
        public void setPriorityType(Machine.priorityType pt){
            PT = pt;
        }
        /*
        * set schedule strategy
        */
        public void setScheduleStrategy(Machine.scheduleStrategy ss){
            SS = ss;
        }
        /*
        * set non delay factor
        */
        public void setNonDelayFactor(double nd){
            nonDelayFactor = nd;
        }
        /*
        * get number of operations
        */
        public int getNumberofOperations(){
            return nOp;
        }
        /*
         * return the ID of critical machine
         */
        public int getCriticalMachineID(){
            return CriticalMachineID;
        }
         /*
         * return the ID of bottleneck machine
         */
        public int getBottleneckMachineID(){
            return CurrentBottleneckID;
        }
       /*
        * initilize schedule
        */
        public void initilizeSchedule(){
            //release jobs
            for (int i = 0; i < nJob; i++) {
                Machines[Jobs[i].getCurrentMachine()].joinQueue(Jobs[i]);
            }
        }
        /*
         * return true if critical machine is idle
         */
        public double getCriticalMachineIdleness(){
            return (Machines[CriticalMachineID].getWorkLoadRatio());
        }
        /*
         * calculate priority
         */
        public void calculatePriority(Machine M){
            M.calculatePriority(PT , SS, nonDelayFactor ,Machines, CriticalMachineID, CurrentBottleneckID);
        }
        /*
         * calculate priority
         */
        public void calculatePriorityBasedRank(Machine M){
            M.calculatePriorityBasedRank(SS, nonDelayFactor);
        }        
        /*
         * set the inital priority for jobs in queue
         */
        public void setInitalPriority(Machine M){
            M.initialisePriority(SS, nonDelayFactor);
        }
        /*
         * sort the jobs in the queue of the machine based on their priority
         */
        public void sortJobInQueue(Machine M){
            M.findHighestPriorityJob_high();
        }
        /*
         * return the next Machine to be scheduled
         */
        public int nextMachine(){
            double minComplete = Double.POSITIVE_INFINITY;
            double maxRemainingWorkload = Double.NEGATIVE_INFINITY;
            double maxQueueWorkload = Double.NEGATIVE_INFINITY;
            int nextMachine = -1;
            for (int i = 0; i < nMachine; i++) {
                if (!Machines[i].getQueue().isEmpty()){
                    if (minComplete>Machines[i].getEarliestCompletionTime()){
                        minComplete=Machines[i].getEarliestCompletionTime();
                        nextMachine = i;
                    }
                }
                if (maxRemainingWorkload<Machines[i].getRemainingWorkload())
                {
                    maxRemainingWorkload = Machines[i].getRemainingWorkload();
                    CriticalMachineID = Machines[i].getID();
                }
                if (maxQueueWorkload<Machines[i].getQueueWorkload())
                {
                    maxQueueWorkload = Machines[i].getQueueWorkload();
                    CurrentBottleneckID = Machines[i].getID();
                }
            }

            return nextMachine;
        }
        /*
         * return the makespan of the schedule
         */
        public double getCmax(){
            double Cmax = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < nMachine; i++) {
                if (Cmax<Machines[i].getReadyTime()) Cmax = Machines[i].getReadyTime();
            }
            return Cmax;
        }
        /*
         * return the mean makespan of the schedule
         */
        public double getMeanC(){
            double meanC = 0;
            for (int i = 0; i < nJob; i++) {
                meanC += Jobs[i].getReadyTime();
            }
            return meanC/nJob;
        }
        /*
         * return the total weighted tardiness of the schedule
         */
        public double getTotalWeightedTardiness(){
            double twt = 0;
            for (int i = 0; i < nJob; i++) {
                double wt = 0;
                if (Jobs[i].getReadyTime() > Jobs[i].getDuedate())
                    wt = Jobs[i].getReadyTime() - Jobs[i].getDuedate();
                twt += Jobs[i].getWeight()*wt;
            }
            return twt;
        }
        /*
         * get lower bound (Cmax) of the problem
         */
        public double getLowerBoundCmax(){
            return JSPInstanceDatabase.BestKnownCmax[indexInstance];
        }
        /*
         * max total job processing time
         */
        public double getMaxTotalJobProcessingTime(){
            return maxTotalJobWorkload;
        }
        /*
         * max total workload of machine
         */
        public double getMaxTotalMachineProcessingTime(){
            return maxTotalMachineWorkload;
        }
        /*
         * get deviation from lower bond (Cmax - LB)/LB
         */
        public double getDevLBCmax(){
            return (getCmax()-getLowerBoundCmax())/getLowerBoundCmax();
        }
        public double getDevLBCmax(double obj){
            return (obj-getLowerBoundCmax())/getLowerBoundCmax();
        }        
        /*
         * get deviation from ref objective value (Cmax - refO)/refO
         */
        public double getDevREFTotalWeightedTardiness(){
            return (getTotalWeightedTardiness()-refObjectiveValue)/refObjectiveValue;
        }
        /*
         * print out the final schedule and related statistics
         */
        public void print() throws FileNotFoundException{
            PrintWriter outputStream  = new PrintWriter("JSP.out");
            for (int i = 0; i < nMachine; i++) {
                outputStream.println(Machines[i].toString());
            }
            outputStream.close();
        }
        /*
         * print machine sequence
         */
        public void printMachineSequence(){
            for (int j = 0; j < Machines.length; j++) {
                System.out.println(Machines[j].toString());       
            }
        }
        /*
         * print job/operations ranks
         */
        public void printJobRanks(){
            for (int i = 0; i < Jobs.length; i++) {
                System.out.println(Jobs[i].printOperationRanks());
            }
        }
        /*
         * set job/operations ranks
         */
        public void setJobRanks(double[] ranks){
            int count = 0;
            int N = Jobs.length*Machines.length;
            for (int i = 0; i < Jobs.length; i++) {
                for (int j = 0; j < Jobs[i].getNumberOperations(); j++) {
                    Jobs[i].getOperations()[j].setRank(ranks[count]);
                    count++;
                }
            }
        }
        /*
         * max of two numbers
         */
        private double max(double a, double b){
            if (a>b) return a;
            else return b;
        }
        /*
         * get maxPlus
         */
        private double maxPlus(double a){
            if (a>0) return a;
            else return 0;
        }
        // = == = = = = = = = = = =design for local search explorer = = = = == = = == = = = =
        /*
         * find final completed job
         */
        public Job getFinalCompletedJob(){
            double maxTime = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            for (int i = 0; i < Jobs.length; i++) {
                if (maxTime<Jobs[i].getReadyTime()) {
                    maxTime=Jobs[i].getReadyTime();
                    maxIndex = i;
                }
            }
            return Jobs[maxIndex];
        }
        public Job getJob(int id){
            return Jobs[id];
        }
        public Job getHighestPriorityJob(){
            double maxPriority = Double.NEGATIVE_INFINITY;
            int maxIndex = -1;
            for (int i = 0; i < Jobs.length; i++) {
                if (maxPriority<Jobs[i].tempPriority) {
                    maxPriority=Jobs[i].tempPriority;
                    maxIndex = i;
                }
            }
            return Jobs[maxIndex];
        }        
        public void storeBestRank(){
            for (int i = 0; i < Jobs.length; i++) {
                Jobs[i].storeBestRank();
            }
        }
        public void restoreBestRank(){
            for (int i = 0; i < Jobs.length; i++) {
                Jobs[i].restoreBestRank();
            }
        }        
    }
