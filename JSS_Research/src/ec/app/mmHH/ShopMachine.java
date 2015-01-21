/*
  Machine in the job shop
 */
package ec.app.mmHH;

public class ShopMachine
{
	private int machineID; //
	private double readyTime; // The readytime (i.e. time available to start processing)
	private int numJobQueue; // Number of jobs in the queue at the machine
	private int[] jobQueue; // array of references to jobs in the job array representing the jobs currently in the queue at machine.
	private int current; // queue index of current job
	private double[][] mon; // for recording queue information
	private int monCount;
	private double currIdle;
	private double currAve;
	private double twk;

	private double[] waitTime = new double[5];
	private int waitCount;

	// Constructor
	public ShopMachine(int id, int qSize)
	{
		setMachineID(id);
		setReadyTime(0.0);
		setNumJobQueue(0);
		setJobQueue(new int[qSize]);
		int ob = qSize*5;
		mon = new double[ob][2];
		monCount = 0;
		waitCount=0;
		currIdle=0.0;
		currAve=0.0;
		twk = 0.0;
	}

	// Get and Sets for ShopMachine variables
	public int getMachineID() {
		return machineID;
	}

	public void setMachineID(int machineID) {
		this.machineID = machineID;
	}

	public double getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(double readyTime) {
		this.readyTime = readyTime;
	}

	public int getNumJobQueue() {
		return numJobQueue;
	}

	public void setNumJobQueue(int numJobQueue) {
		this.numJobQueue = numJobQueue;
	}

	public int[] getJobQueue() {
		return jobQueue;
	}

	public void setJobQueue(int[] jobQueue) {
		this.jobQueue = jobQueue;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	// Increment and Decrement count of jobs in the queue
	public void incrNumJobQueue(){
		numJobQueue++;
	}

	public void decrNumJobQueue(){
		numJobQueue--;
	}

	public void addToQueue(int jobref){
		jobQueue[numJobQueue]=jobref;
		numJobQueue++;
	}

	// Returns the id of job in position index of this machine's queue.
	public int getJob(int index){
		return jobQueue[index];
	}

	public void removeJob(int index){
		if(index<numJobQueue-1) {
			for(int i = index; i < numJobQueue-1; i++){
				jobQueue[i]= jobQueue[i+1];
			}
		}
		jobQueue[numJobQueue-1] = -1;
		numJobQueue = numJobQueue-1;
	}

	// The following are for calculating the time-average and the idletime of the machine.
	/*public void observe(double obs){
		mon[monCount][0] = obs;
		mon[monCount][1] = numJobQueue;
		if(monCount==0)
		   currIdle=currIdle+mon[0][0];
		if(monCount>1){
			currAve = currAve + (mon[monCount][0]-mon[monCount-1][0])*mon[monCount-1][1];
			if(mon[monCount-1][1]==0){
				currIdle = currIdle + (mon[monCount][0]-mon[monCount-1][0]);
			}
		}
		monCount++;
	}

	public double timeAverage(){
		if(monCount>0)
			return currAve/mon[monCount-1][0];
		else
			return 0;
	}

	public double idleTime(){
		if(monCount>0){
			if(monCount>1)
				return currIdle/mon[monCount-1][0];
			else
				return currIdle;
		}

		else
			return 0;
	}*/

	/*public void observe(double obs){
		mon[monCount][0] = obs;
		mon[monCount][1] = numJobQueue;
		monCount++;
	}*/

	public void observe(double obs){
		mon[monCount][0] = obs;
		mon[monCount][1] = numJobQueue;

		// update the current idle and time-average
		if(monCount>0){
			if(mon[monCount-1][1]==0){
				currIdle = currIdle + (obs-mon[monCount-1][0]);
			}
			currAve = currAve + (mon[monCount][0]-mon[monCount-1][0])*mon[monCount-1][1];

		}
		monCount++;
	}

	public double timeAverage(){
		if(monCount>0){
			return currAve/mon[monCount-1][0];
		}
		else{
			return 0.0;
		}
	}

	public double idleTime(){

		if(monCount>0){
			return currIdle/mon[monCount-1][0];
		}
		else{
			return 0.0;
		}
	}

	/*public double timeAverage(){
		currAve = 0.0;
		if(monCount>0){
			for(int i=1; i<monCount; i++){
				currAve = currAve + (mon[i][0]-mon[i-1][0])*mon[i-1][1];
			}
			return currAve/mon[monCount-1][0];
		}
		else{
			return 0.0;
		}
	}

	public double idleTime(){
		currIdle = 0.0;
		if(monCount>0){
			currIdle=currIdle+mon[0][0];
			for(int i=1; i<monCount; i++){
				if(mon[i-1][1]==0)
					currIdle=currIdle+(mon[i][0]-mon[i-1][0]);
			}
			return currIdle/mon[monCount-1][0];
		}
		else{
			return 0.0;
		}
	}*/




	// Records an observed wait time of a job in this queue
	public void obsWait(double opWait){
		waitTime[waitCount%waitTime.length] = opWait;
		waitCount++;
	}

	// Reports the average wait time of the last xx jobs at this machine
	public double getAveWait(){
		double sum = 0.0;
		for(int i=0; i<waitTime.length; i++){
			sum = sum + waitTime[i];
		}
		return (sum/waitTime.length);
	}

	public void setTWK(double twk){
		this.twk = twk;
	}

	public double getTWK(){
		return twk;
	}
}



