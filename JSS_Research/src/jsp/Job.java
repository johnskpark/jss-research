/**
 *
 * @author Nguyen Su
 * Framework to develop heuristics for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * This file includes sub-routines of Job class in JSP
 */

package jsp;

public class Job implements Comparable{
    //static parameters
    private int id = -1;
    private int nOperations = -1;
    private Operation[] operations;
    private double releaseTime = -1;
    private double dueDate = -1;
    private double totalProcessingTime = 0;
    //dynamic parameters
    private double remainingProcessingTime = -1;
    private double completionTime = -1;
    private int currentOperation = -1;
    private double readyTime = -1;
    private double weight = -1;
    private boolean isFinished = false;
    private double Priority = Double.NEGATIVE_INFINITY;
    public double tempPriority;
    private double FinishTime = -1;
    private double bestFinishTime = -1;
    private double Lateness = 0;
    public String extraInfo="";
    public double fWeight = -1;
    /*
     * get job ID
     */
    public int getID(){
        return id;
    }
    /*
     * constructor, params: job ID, number of operations, processing order,
     * processing times, due date, and release time
     */
    public Job(int ID, int nO, int[] route, double[] processingtime, double duedate, double releasetime, double duedateFactor, double w){
        id = ID;
        nOperations=nO;
        operations = new Operation[nO];
        for (int i = 0; i < nOperations; i++) {
            operations[i] = new Operation(route[i], processingtime[i]);
            totalProcessingTime+=processingtime[i];
        }
        remainingProcessingTime = totalProcessingTime;
        FinishTime = totalProcessingTime;
        releaseTime = releasetime;
        dueDate = releasetime + duedateFactor*totalProcessingTime;
        Lateness = totalProcessingTime - dueDate;
        readyTime = releaseTime;
        currentOperation = 0;
        weight = w;
    }
    /*
     * constructor, params: job ID, number of operations, processing order,
     * processing times, due date, and release time
     */
    public Job(int ID, int nO, long[] route, double[] processingtime, double duedateFactor, double releasetime, double w){
        id = ID;
        nOperations=nO;
        operations = new Operation[nO];
        for (int i = 0; i < nOperations; i++) {
            operations[i] = new Operation((int)route[i], processingtime[i]);
            totalProcessingTime+=processingtime[i];
        }
        remainingProcessingTime = totalProcessingTime;
        releaseTime = releasetime;
        dueDate = releaseTime + duedateFactor*totalProcessingTime;
        readyTime = releaseTime;
        currentOperation = 0;
        weight = w;
    }
    /*
     * reset the jobs statistics
     */
    public void reset(){
        readyTime = releaseTime;
        currentOperation = 0;
        isFinished = false;
        for (int i = 0; i < nOperations; i++) {
            operations[i].reset();
        }
        remainingProcessingTime = totalProcessingTime;
        readyTime = releaseTime;
        Priority = Double.NEGATIVE_INFINITY;
    }
    /*
     * complete the current operation and update job's statistics
     */
    public void completeOperation(){
        operations[currentOperation].setScheduleTime(readyTime);
        remainingProcessingTime -= operations[currentOperation].getProcessingTime();
        readyTime += operations[currentOperation].getProcessingTime();
        currentOperation++;
        if (currentOperation==nOperations) isFinished = true;
    }
    /*
     * return True if the job has been completed
     */
    public boolean isCompleted(){
        return isFinished;
    }
    /*
     * check the existence of a given machine in the remaining operation of
     * this job and return the processing time of the opeartion processed on
     * that machine
     */
    public double checkMachine(int machineID){
        for (int i = currentOperation+1; i < nOperations; i++) {
            if (operations[i].getMachine()==machineID)
                return operations[i].getProcessingTime();
        }
        return 0;
    }
     /*
     * check the existence of a given machine in the remaining operation of
     * this job and return the processing time of the opeartion processed on
     * that machine
     */
    public double getTimeToMachine(int machineID){
        if (operations[currentOperation].getMachine()==machineID) return 0;
        double totalTime = operations[currentOperation].getProcessingTime();
        for (int i = currentOperation+1; i < nOperations; i++) {
            if (operations[i].getMachine()==machineID)
                return totalTime;
            totalTime += operations[i].getProcessingTime();
        }
        return Double.POSITIVE_INFINITY;
    }
    /*
     * get largest expected time to complete all operations
     */
    public double getLargestExpectedTimeToComplete(Machine[] M){
        double totalTime = M[operations[currentOperation].getMachine()].getQueueWorkload();
        for (int i = currentOperation+1; i < nOperations; i++) {
            totalTime += operations[i].getProcessingTime()+M[operations[i].getMachine()].getQueueWorkload();
        }
        return totalTime;
    }
    /*
     * get workload in the next queue
     */
    public double getWorkloadNextQueue(Machine[] M){
        if (currentOperation+1==nOperations) return 0;
        return M[operations[currentOperation+1].getMachine()].getQueueWorkload();
    }
    /*
     * return the remain processing time of the job (including the processing
     * time of the current operation
     */
    public double getRemainingProcessingTime(){
        return remainingProcessingTime;
    }
    /*
     * return the total processing time
     */
    public double getTotalProcessingTime(){
        return totalProcessingTime;
    }
    /*
     * return the processing time of the current operation
     */
    public double getCurrentOperationProcessingTime(){
        return operations[currentOperation].getProcessingTime();
    }
    /*
     * return rank of the current operation
     */
    public double getCurrentOperationRank(){
        return operations[currentOperation].getRank();
    }
    /*
     * return the processing time of kth operation
     */
    public double getKthOperationProcessingTime(int k){
        return operations[k].getProcessingTime();
    }
    /*
     * return the remaining processing time when the job at kth operation
     */
    public double getKthRemainProcessingTime(int k){
        double rt = operations[k].getProcessingTime();
        if (k+2==nOperations) return rt;
        else {
            for (int i = k+1; i < nOperations; i++) {
                rt+= operations[i].getProcessingTime();
            }
        }
        return rt;
    }

    /*
     * return the processing time of the next operation
     */
    public double getNextOperationProcessingTime(){
        if (currentOperation+1==nOperations) return 0;
        return operations[currentOperation+1].getProcessingTime();
    }
    /*
     * return the number of remaining operations
     */
    public int getNumberRemainingOperations(){
        return nOperations -currentOperation;
    }
    /*
     * get the position of a machine in this job (at which kth operation)
     */
    public int getKthPositionofMachine(int MachineID){
        for (int i = 0; i < operations.length; i++) {
            if (MachineID == operations[i].getMachine()) return i;
        }
        return -1;
    }
    /*
     * return the earliest completion time of the current operation
     */
    public double getEarliestCompletionTimeCurrentOperation(double machineReadyTime) {
        if (machineReadyTime>readyTime) return machineReadyTime+operations[currentOperation].getProcessingTime();
        return readyTime+operations[currentOperation].getProcessingTime();
    }
    /*
     * return the machine that process the current operation
     */
    public int getCurrentMachine(){
        return operations[currentOperation].getMachine();
    }
    /*
     * return the machine that process the kth operation
     */
    public int getKthMachine(int k){
        return operations[k].getMachine();
    }
    /*
     * return the machine for the next operation
     */
    public int getNextMachine(){
        if (currentOperation+1==nOperations) return 0;
        return operations[currentOperation+1].getMachine();
    }
    /*
     * set the ready time of the current operation
     */
    public void setReadyTime(double readytime){
        readyTime = readytime;
    }
    /*
     * set the due date of job
     */
    public double getDuedate(){
        return dueDate;
    }
    /*
     * assign due date for the job
     */
    public void assignDuedate(double estimateFlowTime){
        dueDate = releaseTime + estimateFlowTime;
    }
    /*
     * set the weight of job
     */
    public double getWeight(){
        return weight;
    }
    /*
     * get release time of the job (arrival time)
     */
    public double getReleaseTime(){
        return releaseTime;
    }
    /*
     * return the ready time of the current operation
     */
    public double getReadyTime(){
        return readyTime;
    }
    /*
     * set the priority of the job
     */
    public void setPriority(double pr){
        Priority = pr;
    }
    /*
     * set rank of the current operation (for move in local search)
     */
    public void setRankCurrentOperation(int RANK){
        operations[currentOperation].setRank(RANK);
    }
     /*
     * add value to the priority of the job
     */
    public void addPriority(double v){
        Priority += v;
    }
    /*
     * get the priority of the job
     */
    public double getPriority(){
        return Priority;
    }
    /*
     * get number of operations
     */
    public int getNumberOperations(){
        return nOperations;
    }
    /*
     * get the processing time of n^th operations
     */
    public double getProcessingTimeOf(int index){
        return operations[index].getProcessingTime();
    }
    /*
     * get the machine index of n^th operations
     */
    public int getMachineIndexOf(int index){
        return operations[index].getMachine();
    }
    /*
     * compator for to sort job based on their priority (used in Machine class)
     */
    public int compareTo(Object otherJob){
        Job tempJob = (Job)otherJob;
        if(this.getPriority() < tempJob.getPriority()){
            return 1;
        }else if(this.getPriority() > tempJob.getPriority()){
            return -1;
        }else{
            return 0;
        }
    }
    /*
     * print job info
     */
    public String toString(){
        return releaseTime + " " + nOperations + " " + totalProcessingTime + " " + extraInfo + " " + (readyTime-releaseTime) + " " + (dueDate-releaseTime);
    }
    /*
     * print job/operation rank
     */
    public String printOperationRanks(){
        String ranks = "{" + id + "} {";
        for (int i = 0; i < operations.length; i++) {
            ranks += operations[i].getRank() + " ";
        }
        return ranks+"} {" + readyTime + "}" ;
    }
    /*
     * for local search explorer
     */
    public double getMaxProccessingTime(){
        double maxPR = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < operations.length; i++) {
            double wait = operations[i].getProcessingTime();
            if (maxPR < wait) {
                maxPR=wait;
            }
        }
        return maxPR;
    }
    public double getMaxWaitingTime(){
        double maxWait = operations[0].getStartTime()-releaseTime;
        operations[0].setWait(maxWait);
        for (int i = 1; i < operations.length; i++) {
            double wait = operations[i].getStartTime() - operations[i-1].getFinishTime();
            operations[0].setWait(wait);
            if (maxWait<wait) {
                maxWait=wait;
            }
        }
        return maxWait;
    }
    public double getTotalWaitingTime(){
        double totalwait = 0;
        totalwait = operations[0].getStartTime() - releaseTime;
        for (int i = 1; i < operations.length; i++) {
            totalwait += operations[i].getStartTime()-operations[i].getFinishTime();
        }
        return totalwait;
    }
    public double getTotalRank(){
        double totalrank = 0;
        for (int i = 0; i < operations.length; i++) {
            totalrank += operations[i].getRank();
        }
        return totalrank;
    }
    public Operation[] getOperations(){
        return operations;
    }
    public Operation getSmallestRank(){
        double MinRank = Double.POSITIVE_INFINITY;
        double MinIndex = -1;
        for (int i = 0; i < operations.length; i++) {
            if (MinRank > operations[i].getRank()) {
                MinRank = operations[i].getRank();
                MinIndex = i;
            }
        }
        return operations[(int)MinIndex];
    }
    public Operation getLargestWaitingTime(){
        double maxWait = operations[0].getStartTime()-releaseTime;
        int maxWaitId = -1;
        for (int i = 1; i < operations.length; i++) {
            double wait = operations[i].getStartTime() - operations[i-1].getFinishTime();
            if (maxWait<wait) {
                maxWait=wait;
                maxWaitId=i;
            }
        }
        return operations[maxWaitId];
    }
    public Operation getHighestPriority(){
        double maxPriority = Double.NEGATIVE_INFINITY;
        int maxPriorityID = -1;
        for (int i = 0; i < operations.length; i++) {
            if (maxPriority<operations[i].tempPriority) {
                maxPriority=operations[i].tempPriority;
                maxPriorityID=i;
            }
        }
        return operations[maxPriorityID];
    }
    public Operation getOperationProcessMachine(int MachineID){
        return operations[getKthPositionofMachine(MachineID)];
    }
    public Operation getKthOperation(int operationID){
        return operations[operationID];
    }
    public void storeBestRank(){
        for (int i = 0; i < operations.length; i++) {
            operations[i].storeBestRank();
        }
    }
    public void restoreBestRank(){
        for (int i = 0; i < operations.length; i++) {
            operations[i].restoreBestRank();
        }
    }
    public void recordFinishTime(){
        FinishTime = readyTime;
    }
    public void shakeFinishTime(cern.jet.random.AbstractDistribution standardGaussian, double bestCmax){
        FinishTime = Utility.truncatedGaussianMutation(FinishTime, totalProcessingTime, bestCmax, standardGaussian);
    }
    public void recordLateness(){
        Lateness = readyTime - dueDate;
    }
    public double getFinishTime(){
        return FinishTime;
    }
    public double getLateness(){
        return Lateness;
    }
    public double getCurrentOperationWaitingTime(){
        return operations[currentOperation].getWait();
    }
    public double getNextOperationWaitingTime(){
        if (currentOperation+1==nOperations) return 0;
        return operations[currentOperation+1].getWait();
    }
    public void resetRecord(){
        FinishTime = totalProcessingTime;
        Lateness = totalProcessingTime - dueDate;
        for (int i = 0; i < operations.length; i++) {
            operations[i].resetRecord();
        }
    }
    public double getRemainingWaitingTime(Machine M){
        if (currentOperation+1==nOperations) return 0;
        else {
            double twait = 0;
            for (int i = currentOperation+1; i < nOperations; i++) {
                double wait=operations[i].getWait();
                if (wait==-1)
                    wait=M.getQueueWorkload()/2;
                twait +=wait;
            }
            return twait;
        }
    }
    public void storeBestFinishTime(){
        bestFinishTime = FinishTime;
    }
    public void restoreBestFinishTime(){
        FinishTime = bestFinishTime;
    }
    public void setFinishTime(double ft){
        FinishTime = ft;
    }
}
