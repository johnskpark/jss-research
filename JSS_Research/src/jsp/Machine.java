/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * This file includes sub-routines for machine class in JSP
 */

package jsp;

import java.util.ArrayList;

public class Machine {
    public enum priorityType {cr_spt,FCFS, SPT, SPTtwkr, SPTtwk, LPT,LRM, NINQ, WINQ,
    WLCtwkr, WLBtwkr, LSO, MWKR, MOPR, SWLNMtwkr, SWKR, STC,
    EDD,S_RPT,WSPT,MS,CR,MDD,CONV,ATC,ERD,S_OPN,W_CR_SPT,W_SRPT_SPT,MOD};
    public enum scheduleStrategy {SEMIACTIVE, ACTIVE, NONDELAY, HYBRID}

    private int id = -1;
    //static parameters
    private int nOperations = -1;
    private double aggregateWorkLoad = -1; // total processing time on the machine given a set of jobs
    //dynamic parameters
    public static double b = -1;
    public static double k = -1;
    public boolean isPlanned = false;
    private ArrayList<Job> jobInQueue;
    private double queueWorkload = 0;
    private double remainingAggregateWorkLoad = -1;
    private int remainAggregateOperations = -1;
    private double releaseTime = -1;
    private double readyTime = 0;
    private double previousStartTime = -1;
    private double previousOT = -1;
    private double earliestCompletionTime = Double.POSITIVE_INFINITY;
    private double earliestStartTime = Double.POSITIVE_INFINITY;
    private int topPriorityIndex = 0;
    private double averageOperationPosition= -1;
    private SampleArray processingFrequency = new SampleArray(20);
    private SampleArray weightFrequency = new SampleArray(20);
    private SampleArray waitingFrequency = new SampleArray(20);
    private SampleArray leftoverRemaingTimeFrequency = new SampleArray(20);
    public scheduleStrategy tempScheduleStrategy = scheduleStrategy.NONDELAY;
    //statistics
    private boolean isWarmup = true;
    private int[] sequence;
    private double idleTime = 0;
    //param for ADRES
    private double lastError = 0;
    private double w_real = 0;
    private double w_estimated = 0;
    private double beta = 0.2;
    private double A = 1;
    private double S = 1;
    // param for breakdown and activation
    private double deactivateTime = 0;
    private double activateTime = 0;
    private double prevDTime = 0;
    private double prevATime = 0;
    private double sumInterDeactivationTimes = 0;
    private double sumRepairTimes = 0;
    private int numDeactivate = 0;
    private int numDisrupt = 0;

    //End param for ADRES;
    //sub-routine
    /*
     *Constructor - param: machine ID, number of operations to be processed,
     *total workload to be processed (total processing time), and ready time
    */
    public Machine(int ID, int nO, double sumWL, double readytime){
        id = ID;
        nOperations = nO;
        remainAggregateOperations = nOperations;
        aggregateWorkLoad = sumWL;
        remainingAggregateWorkLoad = aggregateWorkLoad;
        jobInQueue = new ArrayList<Job>();
        releaseTime = readytime;
        readyTime = releaseTime;
        sequence = new int[nOperations];
    }
    /*
     *Constructor - param: machine ID, number of operations to be processed,
     *total workload to be processed (total processing time), and ready time
    */
    public Machine(int ID, double readytime){
        id = ID;
        remainingAggregateWorkLoad = 0;
        jobInQueue = new ArrayList<Job>();
        releaseTime = readytime;
        readyTime = releaseTime;
        aggregateWorkLoad = 0;
    }
    /*
     * update information from new job
     */
    public void updateNewArrivingJob(double processingTime){
        nOperations++;
        remainAggregateOperations++;
        aggregateWorkLoad += processingTime;
        remainingAggregateWorkLoad += processingTime;
    }
    /*
     * update information from leaving job
     */
    public void updateLeavingJob(double processingTime){
        nOperations--;
        aggregateWorkLoad -= processingTime;
    }
    /*
     * return the id of the machine
     */
    public int getID(){
        return id;
    }
    /*
     * reset the machine statistics
     */
    public void reset(){
        jobInQueue = new ArrayList<Job>();
        queueWorkload = 0;
        readyTime = releaseTime;
        sequence = new int[nOperations];
        remainAggregateOperations = nOperations;
        remainingAggregateWorkLoad = aggregateWorkLoad;
        earliestCompletionTime = Double.POSITIVE_INFINITY;
        earliestStartTime = Double.POSITIVE_INFINITY;
    }
    /*
     * a plan to schedule operations on this machine has been considered
     */
    public void plan(){
        isPlanned = true;
    }
    /*
     * remove the plan to schedule operations on this machine
     */
    public void unplan(){
        isPlanned = false;
    }
    /*
     * include a job into the queue of this machine
     */
    public void joinQueue(Job job){
        jobInQueue.add(job);
        queueWorkload += job.getCurrentOperationProcessingTime();
        // TODO this needs to be modified based on the machine breakdown event.
        if (earliestCompletionTime > job.getEarliestCompletionTimeCurrentOperation(readyTime)) {
            earliestCompletionTime = job.getEarliestCompletionTimeCurrentOperation(readyTime);
        }

        if (earliestStartTime > job.getReadyTime()) {
            earliestStartTime = job.getReadyTime();
        }
    }
    /*
     * return the machine's queue
     */
    public ArrayList<Job> getQueue(){
        return jobInQueue;
    }
    /*
     * return average processing time in queue
     */
    public double getAverageProcessingTimeinQueue(double currentTime){
        if (jobInQueue.isEmpty()) return 0;
        return getQueueWorkload()/(double)jobInQueue.size();
    }
    /*
     * set the inital priority for jobs in queue
     */
    public void initialisePriority(scheduleStrategy sstr, double nonDelayFactor){
        //if (readyTime<earliestStartTime) readyTime=earliestStartTime;
        double earliestStart = max(readyTime, earliestStartTime);
        for (Job J:jobInQueue){
            double activeFactor = Double.NEGATIVE_INFINITY;//-readyTime - remainingAggregateWorkLoad;
            //check scheduling strategy
            if (sstr == scheduleStrategy.SEMIACTIVE) activeFactor = 0;
            if (sstr == scheduleStrategy.ACTIVE) {
                if (J.getReadyTime() < earliestCompletionTime) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.NONDELAY) {
                if (J.getReadyTime() <= earliestStart) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.HYBRID) {
                if (J.getReadyTime() <= earliestStart + nonDelayFactor * (earliestCompletionTime - earliestStart)) activeFactor = 0;
            }
            J.setPriority(activeFactor);
        }
    }
    /*
     * prioritize jobs in the queue, jobs with higher priority will be scheduled earlier
     */
    public void calculatePriority(priorityType pr, scheduleStrategy sstr, double nonDelayFactor, Machine[] mlist, int criticalID, int bottleneckID){
        //if (readyTime<earliestStartTime) {
        //    idleTime += earliestStartTime-readyTime;
        //    readyTime=earliestStartTime;
        //}
        double earliestStart = max(readyTime, earliestStartTime);
        for (Job J:jobInQueue){
            double activeFactor = Double.NEGATIVE_INFINITY;//-readyTime - remainingAggregateWorkLoad;
            //check scheduling strategy
            if (sstr == scheduleStrategy.SEMIACTIVE) activeFactor = 0;
            if (sstr == scheduleStrategy.ACTIVE) {
                if (J.getReadyTime() < earliestCompletionTime) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.NONDELAY) {
                if (J.getReadyTime()<=earliestStart) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.HYBRID) {
                if (J.getReadyTime()<=earliestStart+nonDelayFactor*(earliestCompletionTime-earliestStart)) activeFactor = 0;
            }
            //check dispatching rule
            if (pr == priorityType.FCFS) J.setPriority(activeFactor-J.getReadyTime());
            else if(pr == priorityType.SPT) J.setPriority(activeFactor-J.getCurrentOperationProcessingTime());
            else if(pr == priorityType.ERD) J.setPriority(activeFactor-J.getReleaseTime());
            else if(pr == priorityType.LRM) J.setPriority(activeFactor+J.getRemainingProcessingTime()-J.getCurrentOperationProcessingTime());
            else if(pr == priorityType.LPT) J.setPriority(activeFactor+J.getCurrentOperationProcessingTime());
            else if(pr == priorityType.LSO) J.setPriority(activeFactor+J.getNextOperationProcessingTime());
            else if(pr == priorityType.MWKR) J.setPriority(activeFactor+J.getRemainingProcessingTime());
            else if(pr == priorityType.SWKR) J.setPriority(activeFactor-J.getRemainingProcessingTime());
            else if(pr == priorityType.MOPR) J.setPriority(activeFactor+J.getNumberRemainingOperations());
            else if(pr == priorityType.NINQ) {
                if (J.getNextMachine()==-1) J.setPriority(activeFactor);
                else J.setPriority(activeFactor - (mlist[J.getNextMachine()].getQueue().size()));
            }
            else if(pr == priorityType.WINQ) {
                if (J.getNextMachine()==-1) J.setPriority(activeFactor);
                else J.setPriority(activeFactor - (mlist[J.getNextMachine()].getQueueWorkload()));
            }
            else if(pr == priorityType.SPTtwkr) J.setPriority(activeFactor-(J.getCurrentOperationProcessingTime() / J.getRemainingProcessingTime()));
            else if(pr == priorityType.SPTtwk) J.setPriority(activeFactor-(J.getCurrentOperationProcessingTime() / J.getTotalProcessingTime()));
            else if(pr == priorityType.STC) {
                double timetoC = J.getTimeToMachine(criticalID);
                if (timetoC == -1){
                    J.setPriority(activeFactor);
                }
                J.setPriority(activeFactor - timetoC);
            }
            else if(pr == priorityType.WLCtwkr) {
                double criticalPr = J.checkMachine(criticalID);
                if (criticalPr!= 0)
                    J.setPriority(activeFactor+J.getRemainingProcessingTime()/criticalPr +criticalPr);
                else J.setPriority(activeFactor+J.getRemainingProcessingTime());
            }
            else if(pr == priorityType.WLBtwkr) J.setPriority(activeFactor-(J.checkMachine(bottleneckID)/J.getRemainingProcessingTime()));
            else if(pr == priorityType.SWLNMtwkr) {
                if (J.getNextMachine()==-1) J.setPriority(activeFactor);
                else J.setPriority(activeFactor - (mlist[J.getNextMachine()].queueWorkload / J.getRemainingProcessingTime()));
            }
            else if (pr == priorityType.EDD) J.setPriority(activeFactor-J.getDuedate());
            else if (pr == priorityType.WSPT) J.setPriority(activeFactor+J.getWeight()/J.getCurrentOperationProcessingTime());
            else if (pr == priorityType.S_RPT) J.setPriority(activeFactor-((J.getDuedate()-readyTime-J.getRemainingProcessingTime())/J.getRemainingProcessingTime()));
            else if (pr == priorityType.MS) J.setPriority(activeFactor-(J.getDuedate()-readyTime-J.getRemainingProcessingTime()));
            else if (pr == priorityType.MDD) J.setPriority(activeFactor-(max(J.getDuedate(),readyTime+J.getRemainingProcessingTime())));
            else if (pr == priorityType.MOD) J.setPriority(activeFactor-(-max(J.getReleaseTime()+2*(J.getTotalProcessingTime()-J.getRemainingProcessingTime()+J.getCurrentOperationProcessingTime()),getReadyTime()+J.getCurrentOperationProcessingTime())));
            else if (pr == priorityType.CONV) J.setPriority(activeFactor+(J.getWeight()/J.getCurrentOperationProcessingTime())*maxPlus(1-maxPlus((J.getDuedate()-readyTime-J.getRemainingProcessingTime()))/(k*b*J.getRemainingProcessingTime())));
            else if (pr == priorityType.ATC) J.setPriority(activeFactor+(J.getWeight()/J.getCurrentOperationProcessingTime())*Math.exp(-maxPlus((J.getDuedate()-readyTime-J.getCurrentOperationProcessingTime()-(b+1)*(J.getRemainingProcessingTime()-J.getCurrentOperationProcessingTime()))/(k*getQueueWorkload()/jobInQueue.size()))));
            else if (pr == priorityType.cr_spt) J.setPriority(activeFactor+(-J.getCurrentOperationProcessingTime()-max(1,(((J.getDuedate() - readyTime)/J.getRemainingProcessingTime())))));
            else if (pr == priorityType.S_OPN) J.setPriority(activeFactor+(-(J.getDuedate() - (readyTime + J.getRemainingProcessingTime()))/(double)J.getNumberRemainingOperations()));
            else if (pr == priorityType.CR) J.setPriority(activeFactor+(-(J.getDuedate() - readyTime)/J.getRemainingProcessingTime()));
            else if (pr == priorityType.W_CR_SPT) J.setPriority(activeFactor+J.getWeight()/(J.getCurrentOperationProcessingTime()*max(1,(((J.getDuedate() - readyTime)/J.getRemainingProcessingTime())))));
            else if (pr == priorityType.W_SRPT_SPT) J.setPriority(activeFactor+J.getWeight()/(J.getCurrentOperationProcessingTime()*max(1,(((J.getDuedate() - readyTime-J.getRemainingProcessingTime())/J.getRemainingProcessingTime())))));
            //if (readyTime-J.getReleaseTime()>1.3*(6-J.getNumberRemainingOperations()))
                                //J.setPriority(activeFactor+100000/J.getReleaseTime());
        }
        //Collections.sort(jobInQueue);
        findHighestPriorityJob(jobInQueue);
    }
    /*
     * calculate priority based on existing ranks of operations
     */
    public void calculatePriorityBasedRank(scheduleStrategy sstr, double nonDelayFactor){
        //if (readyTime<earliestStartTime) {
        //    idleTime += earliestStartTime-readyTime;
        //    readyTime=earliestStartTime;
        //}
        double earliestStart = max(readyTime, earliestStartTime);
        for (Job J:jobInQueue){
            double activeFactor = Double.NEGATIVE_INFINITY;//-readyTime - remainingAggregateWorkLoad;
            //check scheduling strategy
            if (sstr == scheduleStrategy.SEMIACTIVE) activeFactor = 0;
            if (sstr == scheduleStrategy.ACTIVE) {
                if (J.getReadyTime() < earliestCompletionTime) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.NONDELAY) {
                if (J.getReadyTime()<=earliestStart) activeFactor = 0;
            }
            if (sstr == scheduleStrategy.HYBRID) {
                if (J.getReadyTime()<=earliestStart+nonDelayFactor*(earliestCompletionTime-earliestStart)) activeFactor = 0;
            }
            //check dispatching rule
            J.setPriority(activeFactor+(double)J.getCurrentOperationRank());
        }
        //Collections.sort(jobInQueue);
        findHighestPriorityJob(jobInQueue);
    }
    /*
     * find the job with highest
     */
    public void findHighestPriorityJob(ArrayList<Job> Queue){
        topPriorityIndex = 0;
        double maxPriority = Double.NEGATIVE_INFINITY;
        for (int i = 0; i< Queue.size(); i++){
            if (maxPriority<Queue.get(i).getPriority()) {
                maxPriority=Queue.get(i).getPriority();
                topPriorityIndex = i;
            }
        }
    }
    /*
     * find the job with highest high-level
     */
    public void findHighestPriorityJob_high(){
        findHighestPriorityJob(jobInQueue);
    }
    /*
     * get planned start time of the next operation
     */
    public double getPlannedStartTimeNextOperation(){
        if (!jobInQueue.isEmpty()) {
            findHighestPriorityJob(jobInQueue);
            Job job = jobInQueue.get(topPriorityIndex);
            if (readyTime < job.getReadyTime())
                return job.getReadyTime();
        }
        return readyTime;
    }
    /*
     * set the deactivation time of the machine
     */
    public void setDeactivateTime(double dTime) {
    	deactivateTime = dTime;
    }
    /*
     * get the deactivation time of the machine
     */
    public double getDeactivationTime() {
    	return deactivateTime;
    }
    /*
     * get the previous deactivation time of the machine
     */
    public double getPrevDeactivationTime() {
    	return prevDTime;
    }
    /*
     * set the activation time of the machine
     */
    public void setActivateTime(double aTime) {
    	activateTime = aTime;
    }
    /*
     * get the activation time of the machine
     */
    public double getActivationTime() {
    	return activateTime;
    }
    /*
     * get the previous activation time of the machine
     */
    public double getPrevActivationTime() {
    	return prevATime;
    }
    /*
     * finish a job and update machine's statistics
     */
    public Job completeJob() {
        Job job = jobInQueue.get(topPriorityIndex);

        // make adjustments based on machine breakdown here.
        double tempt = job.getReadyTime();
        remainAggregateOperations--;
        if (readyTime < job.getReadyTime()){
            if (!isWarmup) idleTime += job.getReadyTime()-readyTime;
            readyTime = job.getReadyTime();
        } else {
        	job.setReadyTime(readyTime);
        }
        previousStartTime = readyTime;

        double expectedProcessingTime = job.getCurrentOperationProcessingTime();
        double actualProcessingTime = job.getCurrentOperationProcessingTime();
        double repairTime = 0;
        if (expectedProcessingTime + readyTime >= deactivateTime) {
        	// add the repair time to the processing time
        	repairTime = activateTime - deactivateTime;
        	actualProcessingTime += repairTime;
        }

        queueWorkload -= expectedProcessingTime;
        remainingAggregateWorkLoad -= expectedProcessingTime;
        processingFrequency.add(expectedProcessingTime);
        weightFrequency.add(job.getWeight());
        previousOT = actualProcessingTime;
        readyTime += actualProcessingTime;
        waitingFrequency.add(readyTime - tempt);
        w_real = readyTime - tempt;
        jobInQueue.remove(topPriorityIndex);
        earliestCompletionTime = Double.POSITIVE_INFINITY;
        earliestStartTime = Double.POSITIVE_INFINITY;

        for (Job j : jobInQueue){
            if (earliestCompletionTime > j.getEarliestCompletionTimeCurrentOperation(readyTime)) {
                earliestCompletionTime = j.getEarliestCompletionTimeCurrentOperation(readyTime);
            }

            if (earliestStartTime > j.getReadyTime()) {
                earliestStartTime = j.getReadyTime();
            }
        }

        job.completeOperation();
        return job;
    }
    /*
     * finish a job and update machine's statistics
     */
    public Job completeJobLocalSearch(){
        Job job = jobInQueue.get(topPriorityIndex);

        // make adjustments based on machine breakdown here.
        double tempt = job.getReadyTime();
        remainAggregateOperations--;
        if (readyTime < job.getReadyTime()){
            if (!isWarmup) idleTime += job.getReadyTime()-readyTime;
            readyTime = job.getReadyTime();
        }
        else job.setReadyTime(readyTime);
        previousStartTime = readyTime;

        double expectedProcessingTime = job.getCurrentOperationProcessingTime();
        double actualProcessingTime = job.getCurrentOperationProcessingTime();
        double repairTime = 0;
        if (expectedProcessingTime + readyTime >= deactivateTime) {
        	// add the repair time to the processing time
        	repairTime = activateTime - deactivateTime;
        	actualProcessingTime += repairTime;
        }

        // this part here is different
        sequence[nOperations-remainAggregateOperations] = job.getID();
        job.setRankCurrentOperation(remainAggregateOperations);

        queueWorkload = queueWorkload - expectedProcessingTime + repairTime;
        remainingAggregateWorkLoad = remainingAggregateWorkLoad - expectedProcessingTime + repairTime;
        processingFrequency.add(expectedProcessingTime);
        weightFrequency.add(job.getWeight());
        previousOT = actualProcessingTime;
        readyTime += actualProcessingTime;
        waitingFrequency.add(readyTime - tempt);
        w_real = readyTime - tempt;
        jobInQueue.remove(topPriorityIndex);
        earliestCompletionTime = Double.POSITIVE_INFINITY;
        earliestStartTime = Double.POSITIVE_INFINITY;
        for (Job j:jobInQueue){
            if (earliestCompletionTime > j.getEarliestCompletionTimeCurrentOperation(readyTime) + repairTime) {
                earliestCompletionTime = j.getEarliestCompletionTimeCurrentOperation(readyTime) + repairTime;
            }

            if (earliestStartTime > j.getReadyTime()) {
                earliestStartTime = j.getReadyTime();
            }
        }
        job.completeOperation();
        return job;
    }
    /*
     * repair the deactivated machine and update machine statistics
     */
    public void repairMachine() {
    	sumInterDeactivationTimes += deactivateTime - prevATime;
    	sumRepairTimes += activateTime - deactivateTime;
    	numDeactivate++;
    	double repairTime = activateTime - deactivateTime;
    	if (deactivateTime < readyTime) {
        	// a job's already been started, and completeJob already handles part of the logic
    		// TODO this part is less than the number of times the machine deactivates, this is not good.
    		numDisrupt++;
    	} else {
    		// the machine is not currently processing a job, delay the machine's availability to after it is repaired
    		queueWorkload += repairTime;
    		remainingAggregateWorkLoad += repairTime;
    		readyTime = activateTime;
    	}
    	// TODO need to update earliest start and earliest completion times maybe? I dunno.

    	prevDTime = deactivateTime;
    	prevATime = activateTime;
    }
    /*
     * updating for ADRES
     */
    public double updateADRES(){
        lastError = w_real - w_estimated;
        A = beta*Math.abs(lastError)+(1-beta)*A;
        S = beta*lastError+(1-beta)*S;
        double alpha = Math.abs(S/A);
        w_estimated = alpha*w_real+(1-alpha)*w_estimated;
        return w_estimated;
    }
    /*
     * get remaining time to finish current job
     */
    public double getLeftoverTimetoProcessCurrentJob(double currentTime){
        if (readyTime<currentTime||previousStartTime==-1) return 0;
        else return previousOT - (currentTime - previousStartTime);
    }
    /*
     * get partial operation time of previous job
     */
    public double getCompletedPartialTimeCurrentJob(double currentTime){
        if (readyTime<currentTime||previousStartTime==-1) return 0;
        else return (currentTime - previousStartTime);
    }
    /*
     * return sample average processsing time
     */
    public double getSampleAverageProcessingTime(){
        return processingFrequency.getAverage();
    }
    /*
     * return sample average waiting time
     */
    public double getSampleAverageWaitingTime(){
        return waitingFrequency.getAverage();
    }
    /*
     * return the next earliest completion time on this machine
     */
    public double getEarliestCompletionTime(){
        return earliestCompletionTime;
    }
    /*
     * return the ready time of this machine
     */
    public double getReadyTime(){
        return readyTime;
    }
    /*
     * return number of job in queue
     */
    public double getNumberofJobInQueue(){
        return jobInQueue.size();
    }
    /*
     * return number of job in queue at a certain time
     */
    public double getNumberofJobInQueue(double currentTime){
        int nJQ = 0;
        for (Job J:jobInQueue) {
            if (J.getReadyTime()<currentTime) nJQ++;
        }
        return nJQ;
    }
    /*
     * return number of job at machine at a certain time
     */
    public double getNumberofJobOnMachine(double currentTime){
        int nJQ = 0;
        for (Job J:jobInQueue) {
            if (J.getReadyTime()<currentTime) nJQ++;
        }
        if (readyTime>currentTime) nJQ++;
        return nJQ;
    }
    /*
     * return the workload of this machine
     */
    public double getQueueWorkload(){
        return queueWorkload;
    }
    /*
     * return the workload of this machine at a certain time
     */
    public double getQueueWorkload(double currentTime){
        double QWL = 0;
        for (Job J:jobInQueue) {
            if (J.getReadyTime()<currentTime) QWL+=J.getCurrentOperationProcessingTime();
        }
        return QWL;
    }
    /*
     * return the workload of this machine at a certain time
     */
    public double getWorkload(double currentTime){
        double QWL = 0;
        for (Job J:jobInQueue) {
            if (J.getReadyTime()<currentTime) QWL+=J.getCurrentOperationProcessingTime();
        }
        if (readyTime>currentTime) QWL+=previousOT;
        return QWL;
    }
    /*
     * return the remaining workload
     */
    public double getRemainingWorkload(){
        return remainingAggregateWorkLoad;
    }
    /*
     * return the remaining operations
     */
    public double getRemainingNOP(){
        return remainAggregateOperations;
    }
    /*
     * return the pecentage of workload in the current queue from the remaining
     * workload
     */
    public double getWorkLoadRatio(){
        return queueWorkload/remainingAggregateWorkLoad;
    }
    /*
     * return the process of this machine from the available jobs
     */
    public double getMachineProgress(){
        return (aggregateWorkLoad-remainingAggregateWorkLoad)/aggregateWorkLoad;
    }
    /*
     * return static utilisation
     */
    public double getStaticUtilisation(){
        if (readyTime != 0) return (aggregateWorkLoad-remainingAggregateWorkLoad)/readyTime;
        return 0;
    }
    /*
     * return the proportion of critical workload in queue
     */
    public double getCritialRatioOfQueue(int CriticalMachineID){
        double criticalWorkload = 0;
        for (Job J: jobInQueue){
            if (J.checkMachine(CriticalMachineID) > 0)
                criticalWorkload += J.getCurrentOperationProcessingTime();
        }
        return criticalWorkload/queueWorkload;
    }
     /*
     * return the proportion of bottleneck workload in queue
     */
    public double getBottleNeckRatioOfQueue(int BottleneckMachineID){
        double BottleneckWorkload = 0;
        for (Job J: jobInQueue){
            if (J.checkMachine(BottleneckMachineID) > 0)
                BottleneckWorkload += J.getCurrentOperationProcessingTime();
        }
        return BottleneckWorkload/queueWorkload;
    }
    /*
     * get deviation of processing times of jobs in queue
     */
    public double getDeviationInQueue(){
        if (jobInQueue.size()==1) return 0;
        else {
            double minPr = Double.POSITIVE_INFINITY;
            double maxPr = Double.NEGATIVE_INFINITY;
            for (Job J: jobInQueue){
                if (minPr>J.getCurrentOperationProcessingTime()) minPr = J.getCurrentOperationProcessingTime();
                if (maxPr<J.getCurrentOperationProcessingTime()) maxPr = J.getCurrentOperationProcessingTime();
            }
            return minPr/maxPr;
        }
    }
    /*
     * get domination ratio of a given operation time to the operation times of jobs in queue
     * 0 mean that the given operation time is smaller than any operation time of jobs in queue
     * 1 mean that the give operation time is larger than any operation time of jobs in queue
     */
    public double getOTRatio(double ot){
        if (jobInQueue.size()<=1) return 0;
        double otRatio = 0;
        for (Job J: jobInQueue){
            if (ot>J.getCurrentOperationProcessingTime()) otRatio+=J.getCurrentOperationProcessingTime();
        }
        return otRatio/getQueueWorkload();
    }
    public double getOTRatio(double ot, double currentTime){
        if (jobInQueue.size()<=1) return 0;
        double otRatio = 0;
        for (Job J: jobInQueue){
            if (ot>J.getCurrentOperationProcessingTime()&&J.getReadyTime()<currentTime) otRatio+=J.getCurrentOperationProcessingTime();
        }
        return otRatio/getQueueWorkload();
    }
    /*
     * get domination ratio of a given remaining time to the remaining times of jobs in queue
     * 0 mean that the given remaining time is smaller than any remaining time of jobs in queue
     * 1 mean that the give remaining time is larger than any remaining time of jobs in queue
     */
    public double getRTRatio(double rt){
        if (jobInQueue.size()<=1) return 0;
        double rtRatio = 0;
        for (Job J: jobInQueue){
            if (rt>J.getRemainingProcessingTime()) rtRatio+=J.getCurrentOperationProcessingTime();
        }
        return rtRatio/getQueueWorkload();
    }
    /*
     * get domination ratio of a given weight to the weights of jobs in queue
     * 0 mean that the given weight is smaller than any weight of jobs in queue
     * 1 mean that the give weight is larger than any weight of jobs in queue
     */
    public double getWRatio(double w){
        if (jobInQueue.size()<=1) return 0;
        double wRatio = 0;
        for (Job J: jobInQueue){
            if (w<J.getWeight()) wRatio+=J.getCurrentOperationProcessingTime();
        }
        return wRatio/getQueueWorkload();
    }
    /*
     * get priority domiating ratio
     */
    public double getPriorityRatio(double pr, double t){
        if (jobInQueue.size()<=1) return 0;
        double rtRatio = 0;
        for (Job J: jobInQueue){
            if (pr < (J.getCurrentOperationProcessingTime()+max(1,(((J.getDuedate() - t)/J.getRemainingProcessingTime()))))) rtRatio+=J.getCurrentOperationProcessingTime();
        }
        return rtRatio/getQueueWorkload();
    }
    /*
     * get expected operation time ratio OT/ex
     */
    public double getSampledOTRatio(double ot){
        return processingFrequency.getDominatedRatio(ot);
    }
    /*
     * get expected weight ratio W/ex
     */
    public double getSampledWRatio(double w){
        return weightFrequency.getDominatedRatio(w);
    }
    /*
     * get utilisation of the machine
     */
    public double getUtilisation(double timeToCollectStat){
        return 1 - idleTime/(readyTime-timeToCollectStat);
    }
    /*
     * get the average inter breakdown times
     */
    public double getAvgInterBreakdownTimes() {
    	if (numDeactivate != 0) {
        	return sumInterDeactivationTimes / numDeactivate;
    	} else {
    		return 0;
    	}
    }
    /*
     * get the average repair times
     */
    public double getAvgRepairTimes() {
    	if (numDeactivate != 0) {
    		return sumRepairTimes / numDeactivate;
    	} else {
    		return 0;
    	}
    }
    /*
     * get number of times the machine is deactivated
     */
    public int numDeactivated() {
    	// TODO
    	System.out.println("deactivate: " + numDeactivate);

    	return numDeactivate;
    }
    /*
     * get number of times job processing was disrupted due to machine deactivation
     */
    public int numDisruption() {
    	// TODO
    	System.out.println("disrupt: " + numDisrupt);

    	return numDisrupt;
    }
    /*
     * print out machine statistics
     */
    public String toString(){
        String result = "[ " + id + " ] [ ";
        for (int i = 0; i < nOperations; i++) {
            result += sequence[i] + " ";
        }
        result += "] [ " + readyTime + " ]";
        return result;
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
    /*
     * stop warm-up period
     */
    public void stopWarmUp(){
        isWarmup = false;
    }
    /*
     * for local search explorer
     */
    public int[] getSequence(){
        return sequence;
    }
    public double getNumberOfOperation(){
        return nOperations;
    }
    public double getTotalProcessingTime(){
        return aggregateWorkLoad;
    }
    public void setAverageOperationPosition(double apo){
        averageOperationPosition = apo;
    }
    public double getAverageOperationPosition(){
        return averageOperationPosition;
    }
}
