/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * This file includes sub-routines for Operation class in JSP
 */

package jsp;

public class Operation {
    private int Machine = -1;
    private double processingTime = -1;
    private double startTime = -1;
    private double finishTime = -1;
    private double waitingTime = -1;
    private double bestWaitingTime = -1;
    private double rank = -1;
    private double bestRank = -1;
    public double tempPriority = -1;
    
    public Operation(int M, double pr){
        Machine = M;
        processingTime=pr;
    }
    public void reset()  {
        startTime = -1;
        finishTime = -1;
        tempPriority = -1;
    }
    public void setScheduleTime(double readyTime){
        startTime = readyTime;
        finishTime = readyTime+processingTime;
    }
    public void setRank(double RANK){
        rank = RANK;
    }  
    public void setWait(double wait){
        waitingTime = wait;
    }
    public double getWait(){
        return waitingTime;
    }
    public double getProcessingTime(){
        return processingTime;
    }
    public int getMachine(){
        return Machine;
    }
    public double getRank(){
        return rank;
    }
    public double getStartTime(){
        return startTime;
    }
    public double getFinishTime(){
        return finishTime;
    }
    /*
     * for local search explorer
     */
    public void upRank(int r){
        rank+=r+0.5;
    }
    public void downRank(int r){
        rank-=r+0.5;
    }
    public void assignRank(double newrank){
        rank += newrank;
    }
    public void storeBestRank(){
        bestRank = rank;
    }
    public void restoreBestRank(){
        rank = bestRank;
    }
    public void resetRecord(){
        waitingTime = -1;
    }
    public void shakeRecordWait(cern.jet.random.AbstractDistribution standardGaussian, double maxWaiting){
        waitingTime = Utility.truncatedGaussianMutation(waitingTime, 0, maxWaiting, standardGaussian);
    }
    public void storeBestWaitingTime(){
        bestWaitingTime = waitingTime;
    }
    public void restoreBestWaitingTime(){
        waitingTime = bestWaitingTime;
    }
}
