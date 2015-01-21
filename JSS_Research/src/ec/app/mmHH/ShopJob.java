/*
  Class of jobs in the shop.

 */
package ec.app.mmHH;

public class ShopJob
{
	int arrID; // the job ID
	double release; // arrival time of job into the shop i.e. the release time
	double[] opProc; // the processing time required for each operation
	int[] opMach; //  the machine required to process each operation
	int numOps; // Number of operations in the job

	public int getNumOps() {
		return numOps;
	}
	public void setNumOps(int numOps) {
		this.numOps = numOps;
	}

	double duedate; // Job duedate - for dynamic case
	double weight; // Job weight

	int currentOp; // Current Operation
	double[] opComplete; // completion time of each operation?

	double priority; // Evaluated priority of the current operation on the job by dispatching rule

	public ShopJob(   )
	{
		// arrID = ;
		// release = ;
		// opProc = new double[numMach];
		currentOp = 0;
	}

	/* Constructor used for static jobs */
	public ShopJob(int arrID, double release, int[] opMach, double[] opProc, int numOps  )
	{
		this.arrID = arrID;
		this.release = release;
		this.opProc = opProc;
		this.opMach = opMach;
		this.numOps = numOps;

		this.currentOp = 0;
		this.opComplete = new double[this.opProc.length];
	}

	/* Constructor used for dynamic jobs */
	public ShopJob(int arrID, double release, int[] opMach, double[] opProc, int numOps, double weight, double duedate )
	{
		this.arrID = arrID;
		this.release = release;
		this.opProc = opProc;
		this.opMach = opMach;
		this.numOps = numOps;
		this.weight = weight;
		this.duedate = duedate;

		this.currentOp = 0;
		this.opComplete = new double[this.opProc.length];
	}

	// Getter and Setter methods of ShopJob variables.
	public int getArrID() {
		return arrID;
	}

	public void setArrID(int arrID) {
		this.arrID = arrID;
	}

	public double getRelease() {
		return release;
	}

	public void setRelease(double release) {
		this.release = release;
	}

	public double[] getOpProc() {
		return opProc;
	}

	public void setOpProc(double opProc1, double opProc2) {
		this.opProc[0] = opProc1;
		this.opProc[1] = opProc2;
	}

	public int[] getOpMach() {
		return opMach;
	}

	public void setOpMach(int opM1, int opM2) {
		this.opMach[0] = opM1;
		this.opMach[1] = opM2;
	}

	public int getCurrOpMach() {
		return this.opMach[currentOp];
	}

	public double getDuedate() {
		return duedate;
	}

	public void setDuedate(double duedate) {
		this.duedate = duedate;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double[] getOpComplete() {
		return opComplete;
	}

	public void setOpComplete(double[] opComplete) {
		this.opComplete = opComplete;
	}

	public void setOpComplete(int op, double opComplete) {
		this.opComplete[op] = opComplete;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public int getCurrentOp() {
		return currentOp;
	}

	public void setCurrentOp(int currentOp) {
		this.currentOp = currentOp;
	}

	public void incrCurrentOp() {
		this.currentOp++;
	}

    public double getTotalPR(){
    	double tot = 0;
    	for(int i=0; i < numOps; i++)
    		tot = tot + opProc[i];
    	return tot;
    }


	// Methods for terminals

	// Returns the processing time of the current operation (i.e. the operation job is queuing to have processed)
	public double getPR() {
		return opProc[currentOp];
	}

	// Returns the processing time of the next operation, or 0 if there is no next operation
	public double getNextPR() {
		if(currentOp+1<numOps)
			return opProc[currentOp+1];
		else
			return 0.0;
	}


	// Returns the processing time remaining on the job
	public double getRT() {
		double totalTime = 0.0;
		int i = 0;
		for(i=currentOp; i < numOps ;i++)
		{
			totalTime = totalTime + opProc[i];
		}
		return totalTime;
	}

	// Returns the remaining number of operations on the job
	public double getRO() {
		return (numOps - currentOp);
	}

	// Returns the ready time of the operation
	public double getRJ() {
		double ready = 0.0;
		if(currentOp == 0){
			ready = release;
		}
		else{
			ready = opComplete[currentOp-1];
		}
		return ready;
	}

}



