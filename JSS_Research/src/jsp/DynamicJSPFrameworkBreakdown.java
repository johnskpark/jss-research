/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 *
 * Include the sub-routines to control processes in dynamic JSP
 *
 * This can be considered as a simulation model of JSP
 */

package jsp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import SmallStatistics.SmallStatistics;

    public class DynamicJSPFrameworkBreakdown extends AbstractJSPFramework
    {
    	public static final int ARRIVAL_EVENT = 0;
    	public static final int READY_EVENT = 1;
    	public static final int DEACTIVATE_EVENT = 2;

        // random factor declaration
        private cern.jet.random.engine.RandomEngine engineJob;
        private cern.jet.random.AbstractDistribution interArrivalTime;
        private cern.jet.random.AbstractDistribution jobWeight;

        private double[] weightDistribution = {0.2, 0.6, 0.2};
        private int[] weightValues = {1, 2, 4};
        private int[] nOperationsBound = new int[2];

        private cern.jet.random.AbstractDistribution distProcessingTime;
        private cern.jet.random.AbstractDistribution orderMachine;
        private cern.jet.random.AbstractDistribution distBottleneckProcessingTime;
        private cern.jet.random.AbstractDistribution distJobLength;

        private boolean discreteProcessingTime = false;
        public static boolean revisit = false;
        public static boolean equalWeightProbability = false;
        // jsp stats, variables and parameters
        public ArrayList<Job> jobs = new ArrayList<Job>();
        public Machine[] machines;
        private int idCount = 0;
        private double arrivalRate = 0.0;
        private double nextArrivalTime = 0;
        private double nextPlannedEarliestReadyTime = 0;
        private double duedateFactor = 1.3;
        private double meanOperationTime = 1;
        private int nOp;
        private int nMachine = 6;
        private int CriticalMachineID;
        private int CurrentBottleneckID;

        private SampleArray arrivalFrequency = new SampleArray(100);
        private SampleArray lengthFrequency = new SampleArray(20);
        private SampleArray errorDDFrequency = new SampleArray(20);

        //scheduling rules
        private Machine.priorityType PT;
        private Machine.scheduleStrategy SS;
        private double nonDelayFactor; // from 0 to 1; 0 is similar to nonDelay schedule,; 1 is similar to active schedule

        //aggregate statistics
        private double warmUpCondition = -1;
        private double stopCondition = 1;
        private double timeToStartCollectStat = -1;
        private int throughput = 0;
        private double percentTardiness = 0;
        private double totalWeightedTardiness = 0;
        private double totalABSDDError = 0;
        private double totalPercentageABSDDError = 0;
        private double estimateError = 0;
        private double totalDDError = 0;
        private double totalPercentageDDError = 0;

        // machine breakdowns
        private double breakdownRate = 0.0;
        private double mRepair = 0.0;
        private double[] machineDeactivateTimes;
        private double[] machineActivateTimes;
        private double nextEarliestDeactivateTime = Double.POSITIVE_INFINITY;
        private double nextEarliestActivateTime = Double.POSITIVE_INFINITY;
        private int nextEarliestDeactivateMachine = -1;
        private int nextEarliestActivateMachine = -1;

        private cern.jet.random.engine.RandomEngine engineBreakdown;
        private cern.jet.random.AbstractDistribution interBreakdownTime;
        private cern.jet.random.AbstractDistribution distRepairTime;

        private SmallStatistics flowtime = new SmallStatistics();
        private SmallStatistics tardiness = new SmallStatistics();
        private SmallStatistics lateness = new SmallStatistics();

        /*
         * constructor
         */
        public DynamicJSPFrameworkBreakdown(int seedJob, int numberMachine,
        		double arrivalRate, int warmUpThreshold, int stopThreshold,
                int seedBreakdown, double breakdownLevel, double meanRepair) {
//        	super(seedJob, numberMachine, arrivalRate, warmUpThreshold, stopThreshold);

            this.nMachine = numberMachine;
            this.engineJob = new cern.jet.random.engine.MersenneTwister(seedJob);
            this.arrivalRate = arrivalRate;
            //engineArrival = new cern.jet.random.engine.MersenneTwister(seedArrival);
            this.interArrivalTime = new cern.jet.random.Gamma(1, arrivalRate, engineJob);
            this.jobWeight = new cern.jet.random.Empirical(weightDistribution, cern.jet.random.Empirical.NO_INTERPOLATION, engineJob);
            this.machines = new Machine[nMachine];

            for (int i = 0; i < nMachine; i++) {
            	this.machines[i] = new Machine(i, 0);
            }

            this.warmUpCondition = warmUpThreshold;
            this.stopCondition = stopThreshold;

            // machine breakdown
        	this.machineDeactivateTimes = new double[nMachine];
        	this.machineActivateTimes = new double[nMachine];
            for (int i = 0; i < nMachine; i++) {
            	this.machineDeactivateTimes[i] = 0;
            	this.machineActivateTimes[i] = 0;
            }
            this.engineBreakdown = new cern.jet.random.engine.MersenneTwister(seedBreakdown);
            this.breakdownRate = getBreakdownRate(breakdownLevel, meanRepair);
            this.mRepair = meanRepair;

            if (!Double.isInfinite(breakdownRate)) {
            	this.interBreakdownTime = new cern.jet.random.Gamma(1, breakdownRate, engineBreakdown);
            } else {
            	this.interBreakdownTime = new cern.jet.random.Uniform(Double.MAX_VALUE, Double.MAX_VALUE, engineBreakdown);
            }
            this.distRepairTime = new cern.jet.random.Gamma(1, mRepair, engineBreakdown);
        }
        /*
         * complex constructor
         */
        public DynamicJSPFrameworkBreakdown(int seedJob, int numberMachine, int lowerBound,
	            int upperBound, double utilisation, double bottleneckUtilisation,
	            double meanTime, String prDis, double p,
	            int warmUpThreshold, int stopThreshold,
	            int seedBreakdown, double breakdownLevel, double meanRepair) {
//        	super(seedJob, numberMachine, lowerBound,
//        			upperBound, utilisation, bottleneckUtilisation,
//        			meanTime, prDis, p,
//        			warmUpThreshold, stopThreshold);

        	this.meanOperationTime = meanTime;
        	this.nMachine = numberMachine;
        	this.nOperationsBound[0] = lowerBound;
        	this.nOperationsBound[1] = upperBound;
        	this.engineJob = new cern.jet.random.engine.MersenneTwister(seedJob);
            // your favourite distribution goes here
            // job length distribution
            this.distJobLength = new cern.jet.random.Uniform(nOperationsBound[0], nOperationsBound[1], engineJob);
            // arrival process distribution
            this.arrivalRate = getArrivalRate(utilisation, nMachine, lowerBound, upperBound, meanOperationTime);
            double mArrivalRate = arrivalRate * 0.5 * (upperBound + lowerBound) / nMachine;
            this.interArrivalTime = new cern.jet.random.Gamma(1, arrivalRate, engineJob);

            // processing time distribution
            if ("erlang".equals(prDis)) {
            	this.distProcessingTime = new cern.jet.random.Gamma(p, mArrivalRate * p / utilisation, engineJob);
            	this.distBottleneckProcessingTime = new cern.jet.random.Gamma(p, mArrivalRate * p / bottleneckUtilisation, engineJob);
            } //mean = p/p2
            else if ("uniform".equals(prDis)) {
            	this.distProcessingTime = new cern.jet.random.Uniform(p, 2 * meanOperationTime - p, engineJob);
            	this.distBottleneckProcessingTime = new cern.jet.random.Uniform(p, 2 * meanOperationTime - p, engineJob); //2*meanOperationTime-p,
            } else if ("duniform".equals(prDis)) {
            	this.distProcessingTime = new cern.jet.random.Uniform(p, 2 * meanOperationTime - p, engineJob);
            	this.distBottleneckProcessingTime = new cern.jet.random.Uniform(p, 2 * meanOperationTime - p, engineJob); //2*meanOperationTime-p,
            	this.discreteProcessingTime = true;
            }//mean = 0.5*(p+p2)
            else {
            	this.distProcessingTime = new cern.jet.random.Gamma(1, mArrivalRate / utilisation, engineJob);
            	this.distBottleneckProcessingTime = new cern.jet.random.Gamma(1, mArrivalRate / bottleneckUtilisation, engineJob);
            }
            if (revisit){
            	this.orderMachine = new cern.jet.random.Uniform(0,nMachine-1,engineJob);
            }

            // job weight distribution
            if (!equalWeightProbability) {
            	this.jobWeight = new cern.jet.random.Empirical(weightDistribution, cern.jet.random.Empirical.NO_INTERPOLATION, engineJob);
            } else {
            	this.jobWeight = new cern.jet.random.Uniform(1,9,engineJob);
            }

            this.machines = new Machine[nMachine];
            for (int i = 0; i < nMachine; i++) {
            	this.machines[i] = new Machine(i, 0);
            }

            this.warmUpCondition = warmUpThreshold;
            this.stopCondition = stopThreshold;

            // machine breakdown distribution
        	this.machineDeactivateTimes = new double[nMachine];
        	this.machineActivateTimes = new double[nMachine];
            for (int i = 0; i < nMachine; i++) {
            	this.machineDeactivateTimes[i] = 0;
            	this.machineActivateTimes[i] = 0;
            }
            this.engineBreakdown = new cern.jet.random.engine.MersenneTwister(seedBreakdown);
            this.breakdownRate = getBreakdownRate(breakdownLevel, meanRepair);
            this.mRepair = meanRepair;

            if (!Double.isInfinite(breakdownRate)) {
            	this.interBreakdownTime = new cern.jet.random.Gamma(1, 1/breakdownRate, engineBreakdown);
            } else {
            	this.interBreakdownTime = new cern.jet.random.Uniform(Double.MAX_VALUE, Double.MAX_VALUE, engineBreakdown);
            }
            this.distRepairTime = new cern.jet.random.Gamma(1, 1/mRepair, engineBreakdown);
        }
        /*
         * calculate the corresponding arrival rate
         */
        private double getArrivalRate(double utilisation, double N, double l, double u, double mu) {
            //double rate = utilisation*(u-l+1)*N/(0.5*Math.pow(u+1, 2)-0.5*u-0.5-0.5*l*l+0.5*l)/mu;
            double rate = utilisation * N / (mu * 0.5 * (u + l));
            return rate;
        }
        /*
         * calculating the corresponding breakdown rate
         */
        private double getBreakdownRate(double breakdownLevel, double meanRepair) {
        	double rate = meanRepair / breakdownLevel - meanRepair;
        	return rate;
        }
        /*
         * return current arrival rate
         */
        public double getArrivalRate() {
            return arrivalRate;
        }
        /*
         * return the average number of operations
         */
        public double getAverageNumberOfOperations(){
            return 0.5*(nOperationsBound[0]+nOperationsBound[1]);
        }
        /*
         * return average operation processing time
         */
        public double getMeanOperationProcessingTime(){
            return meanOperationTime;
        }
        /*
         * get random number
         */
        public double getRandomNumber(){
            return engineJob.nextDouble();
        }
        /*
         * change utilisation level
         */
        public void setUtilisation(double u){
            arrivalRate = getArrivalRate(u, nMachine, nOperationsBound[0], nOperationsBound[1], meanOperationTime);
            interArrivalTime = new cern.jet.random.Gamma(1, arrivalRate, engineJob);
        }
        /*
         * Generate random non-recirculated job
         */
        public Job generateRandomJob(double arrivalTime) {
            //record the arrival history
            arrivalFrequency.add(arrivalTime);
            //generate random route
            long[] randomRoute = new long[distJobLength.nextInt()];
            lengthFrequency.add(randomRoute.length);
            //SRSWOR algorithm ~ O(n)
            if (!revisit) {
                cern.jet.random.sampling.RandomSampler.sample(randomRoute.length, nMachine, randomRoute.length, 0, randomRoute, 0, engineJob);
                shuffleArray(randomRoute, engineJob);
            } else {
                randomRoute[0] = orderMachine.nextInt();
                for (int i = 1; i < randomRoute.length; i++) {
                    do {
                        randomRoute[i] = orderMachine.nextInt();
                    } while(randomRoute[i] == randomRoute[i-1]);
                }
            }
            //generate random processing time
            double[] randomProcessingTime = new double[randomRoute.length];
            for (int i = 0; i < randomRoute.length; i++) {
                if (!discreteProcessingTime){
                    if ((int)randomRoute[i]!=0) randomProcessingTime[i] = distProcessingTime.nextDouble();
                    else randomProcessingTime[i] = distBottleneckProcessingTime.nextDouble();
                } else {
                    if ((int)randomRoute[i]!=0) randomProcessingTime[i] = distProcessingTime.nextInt();
                    else randomProcessingTime[i] = distBottleneckProcessingTime.nextInt();
                }
                machines[(int)randomRoute[i]].updateNewArrivingJob(randomProcessingTime[i]);
            }
            Job newjob ;
            if (!equalWeightProbability) newjob= new Job(idCount++, randomRoute.length, randomRoute, randomProcessingTime, duedateFactor, arrivalTime, weightValues[(int)(jobWeight.nextDouble()*weightValues.length)]);
            else {
                newjob= new Job(idCount++, randomRoute.length, randomRoute, randomProcessingTime, duedateFactor, arrivalTime, jobWeight.nextDouble());
                newjob.fWeight = jobWeight.nextDouble();
            }
            jobs.add(newjob);
            machines[newjob.getCurrentMachine()].joinQueue(newjob);
            return newjob;
        }
        /*
         * repair the machine that has been deactivated
         */
        public void repairMachine(int m) {
        	machines[m].repairMachine();
        }
        /*
         * shuffle numbers in an array (Knuth algorithm) ~ O(n)
         */
        private void shuffleArray(long[] randomRoute,cern.jet.random.engine.RandomEngine engine) {
            cern.jet.random.Uniform.staticSetRandomEngine(engine);
            for (int j = 0; j < randomRoute.length-1; j++) {
                int r = cern.jet.random.Uniform.staticNextIntFromTo(j, randomRoute.length-1);
                long temp = randomRoute[r];
                randomRoute[r] = randomRoute[j];
                randomRoute[j] = temp;
            }
            //show the generated sequence --------
            //cern.colt.list.LongArrayList numbers = new cern.colt.list.LongArrayList(randomRoute.length);
            //numbers.elements(randomRoute);
            //System.out.println("**" + numbers.toString());
            //------------------------------------
        }
        /*
         * return machines used in the shop
         */
        public Machine[] getMachines(){
            return machines;
        }
        /*
         * return total number of jobs in queue of each machines
         */
        public double getTotalNumberOfJobInQueue(double currentTime){
            double Nq = 0;
            for (int i = 0; i < machines.length; i++) {
                Nq+=machines[i].getNumberofJobInQueue(currentTime);
            }
            return Nq;
        }
        /*
         * get next earliest ready time
         */
        public double getNextEarliestReadyTime(){
            nextPlannedEarliestReadyTime = Double.POSITIVE_INFINITY;
            for (int i = 0; i < machines.length; i++) {
                if (!machines[i].getQueue().isEmpty() && nextPlannedEarliestReadyTime > machines[i].getPlannedStartTimeNextOperation())
                    nextPlannedEarliestReadyTime = machines[i].getPlannedStartTimeNextOperation();
            }
            return nextPlannedEarliestReadyTime;
        }
        /*
         * set next deactivate time and allocate it to a random machine
         */
        public double setNextDeactivateTime(int mIndex) {
        	// TODO probably not in this code, but the distribution doesn't really match.
        	double newDTime = machineActivateTimes[mIndex] + interBreakdownTime.nextDouble();

        	machineDeactivateTimes[mIndex] = newDTime;
        	machines[mIndex].setDeactivateTime(newDTime);

        	if (mIndex == nextEarliestDeactivateMachine) {
        		// Reschedule the next deactivate time
        		getEarliestDeactivationTimeAndMachine();
        	}

        	return machineDeactivateTimes[mIndex];
        }
        /*
         * get next earliest machine deactivation time
         */
        public double getNextEarliestDeactivateTime() {
        	if (nextEarliestDeactivateMachine == -1) {
            	getEarliestDeactivationTimeAndMachine();
        	}

        	return nextEarliestDeactivateTime;
        }
        /*
         * get the machine earliest deactivation occurs at
         */
        public int getNextEarliestDeactivateMachine() {
        	if (nextEarliestDeactivateMachine == -1) {
            	getEarliestDeactivationTimeAndMachine();
        	}

        	return nextEarliestDeactivateMachine;
        }
        /*
         * find earliest deactivation time and machine
         */
        private void getEarliestDeactivationTimeAndMachine() {
        	nextEarliestDeactivateTime = Double.POSITIVE_INFINITY;

        	for (int i = 0; i < machineDeactivateTimes.length; i++) {
        		if (nextEarliestDeactivateTime > machineDeactivateTimes[i]) {
        			nextEarliestDeactivateTime = machineDeactivateTimes[i];
        			nextEarliestDeactivateMachine = i;
        		}
        	}
        }
        /*
         * set next activate time and allocate it to a random machine
         */
        public double setNextActivateTime(int mIndex) {
        	double newATime = machineDeactivateTimes[mIndex] + distRepairTime.nextDouble();

        	machineActivateTimes[mIndex] = newATime;
        	machines[mIndex].setActivateTime(newATime);

        	if (mIndex == nextEarliestActivateMachine) {
        		// Reschedule the next activate time
        		getEarliestActivationTimeAndMachine();
        	}

        	return machineActivateTimes[mIndex];
        }
        /*
         * get next earliest machine reactivation time
         */
        public double getNextEarliestActivateTime() {
        	if (nextEarliestActivateMachine == -1) {
            	getEarliestActivationTimeAndMachine();
        	}

        	return nextEarliestActivateTime;
        }
        /*
         * get the machine earliest reactivation occurs at
         */
        public int getNextEarliestActivateMachine() {
        	if (nextEarliestActivateMachine == -1) {
            	getEarliestActivationTimeAndMachine();
        	}

        	return nextEarliestActivateMachine;
        }
        /*
         * find earliest reactivation time and machine
         */
        private void getEarliestActivationTimeAndMachine() {
        	nextEarliestActivateTime = Double.POSITIVE_INFINITY;

        	for (int i = 0; i < machineActivateTimes.length; i++) {
        		if (nextEarliestActivateTime > machineActivateTimes[i]) {
        			nextEarliestActivateTime = machineActivateTimes[i];
        			nextEarliestActivateMachine = i;
        		}
        	}
        }
        /*
         * set next arrival time
         */
        public double setNextArrivalTime() {
            nextArrivalTime += interArrivalTime.nextDouble();
            return nextArrivalTime;
        }
        /*
         * get next arrival time
         */
        public double getNextArrivalTime() {
            return nextArrivalTime;
        }
        /*
         * next event is arrival?
         */
        public boolean isNextArrivalEvent(){
            if (jobs.isEmpty()) return true;
            return nextArrivalTime <= getNextEarliestReadyTime() &&
            		nextArrivalTime <= getNextEarliestDeactivateTime() &&
            		nextArrivalTime <= getNextEarliestActivateTime();
        }

        public int getNextEventType() {
        	if (jobs.isEmpty()) {
        		if (nextArrivalTime < getNextEarliestDeactivateTime()) {
            		return ARRIVAL_EVENT;
        		} else {
        			return DEACTIVATE_EVENT;
        		}
        	} else {
        		double nextReadyTime = getNextEarliestReadyTime();
        		double nextDeactivateTime = getNextEarliestDeactivateTime();

        		if (nextArrivalTime <= nextDeactivateTime &&
        				nextArrivalTime <= nextReadyTime) {
            		// Arrival takes precedence
        			return ARRIVAL_EVENT;
        		} else if (nextDeactivateTime <= nextArrivalTime &&
        				nextDeactivateTime <= nextReadyTime) {
        			// Followed by breakdown
        			return DEACTIVATE_EVENT;
        		} else {
        			// Followed by machine ready.
            		return READY_EVENT;
        		}
        	}
        }

        /*
         * unplanned the schedule at all machine to handle the new arriving jobs
         */
        public void unplanAll(){
            for (int i = 0; i < machines.length; i++) {
                machines[i].unplan();
            }
        }
        /*
         * remove complete job from the system
         */
        public void removeJobFromSystem(Job job) {
            if (!isWarmUp()) {
                double flow = job.getReadyTime() - job.getReleaseTime();
                flowtime.add(flow);
                lateness.add(job.getReadyTime() - job.getDuedate());
                tardiness.add(maxPlus(job.getReadyTime() - job.getDuedate()));
                if (job.getReadyTime() > job.getDuedate()) {
                    percentTardiness++;
                    totalWeightedTardiness += job.getWeight() * maxPlus(job.getReadyTime() - job.getDuedate());
                }
                throughput++;
                totalABSDDError += Math.abs(job.getReadyTime() - job.getDuedate());
                totalPercentageABSDDError += Math.abs(job.getReadyTime() - job.getDuedate()) / flow;
                totalPercentageDDError += (job.getReadyTime() - job.getDuedate()) / flow;
                estimateError += Math.abs(job.getFinishTime() - job.getReadyTime());
            }
            errorDDFrequency.add(Math.abs(job.getReadyTime() - job.getDuedate()));
            jobs.remove(job);
            for (int i = 0; i < job.getNumberOperations(); i++) {
                machines[job.getMachineIndexOf(i)].updateLeavingJob(job.getProcessingTimeOf(i));
            }
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
                if (!machines[i].getQueue().isEmpty() && !machines[i].isPlanned) {
                    if (minComplete > machines[i].getEarliestCompletionTime()) {
                        minComplete = machines[i].getEarliestCompletionTime();
                        nextMachine = i;
                    }
                }

                if (maxRemainingWorkload < machines[i].getRemainingWorkload()) {
                    maxRemainingWorkload = machines[i].getRemainingWorkload();
                    CriticalMachineID = machines[i].getID();
                }

                if (maxQueueWorkload < machines[i].getQueueWorkload()) {
                    maxQueueWorkload = machines[i].getQueueWorkload();
                    CurrentBottleneckID = machines[i].getID();
                }
            }
            return nextMachine;
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
         * return true if critical machine is idle
         */
        public double getCriticalMachineIdleness(){
            return (machines[CriticalMachineID].getWorkLoadRatio());
        }
        /*
         * return total ADRES estimated waiting time
         */
        public double getTotalADRESWaitingTime(Job j){
            double totalADRESwaiting = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalADRESwaiting+=machines[m].updateADRES();
            }
            return totalADRESwaiting;
        }
        /*
         * get the total average operation time ratio on the route of job j
         */
        public double getAverageOTRatio_Route(Job j){
            double averageOTR = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                averageOTR+=machines[m].getOTRatio(j.getKthOperationProcessingTime(i));
            }
            return averageOTR/(double)j.getNumberOperations();
        }
        /*
         * get the total average expected operation time ratio on the route of job j
         */
        public double getAverageSampledOTRatio_Route(Job j){
            double averageOTR = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                averageOTR+=machines[m].getSampledOTRatio(j.getKthOperationProcessingTime(i));
            }
            return averageOTR/(double)j.getNumberOperations();
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getTotalAverageWaiting_Route(Job j){
            double totalwait = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalwait+=machines[m].getSampleAverageWaitingTime();
            }
            return totalwait;
        }
        /*
         * get number of job route
         */
        public double getTotalNumberofJob_Route(Job j){
            double totaljob = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totaljob+=machines[m].getNumberofJobOnMachine(j.getReleaseTime());
            }
            return totaljob;
        }
        /*
         * get number of job_queue route
         */
        public double getTotalNumberofJobQueue_Route(Job j){
            double totaljob = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totaljob+=machines[m].getNumberofJobInQueue(j.getReleaseTime());
            }
            return totaljob;
        }
        /*
         * get total time of job_queue route
         */
        public double getTotalTimeJobQueue_Route(Job j){
            double totaltimejob = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totaltimejob+=machines[m].getQueueWorkload(j.getReleaseTime());
            }
            return totaltimejob;
        }
        /*
         * get total time of job on machine route
         */
        public double getTotalTimeJob_Route(Job j){
            double totaltimejob = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totaltimejob+=machines[m].getWorkload(j.getReleaseTime());
            }
            return totaltimejob;
        }
        /*
         * get total time of job on machine
         */
        public double getTotalTimeJob(){
            double totaltimejob = 0;
            for (int i = 0; i < machines.length; i++) {
                totaltimejob += machines[i].getQueueWorkload();
            }
            return totaltimejob;
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getTotalAverageProcessingTime_Route(Job j){
            double totalprocessing = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalprocessing+=machines[m].getSampleAverageProcessingTime();
            }
            return totalprocessing;
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getLeftoverProcessingTime_Route(Job j){
            double totalprocessing = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalprocessing+=machines[m].getLeftoverTimetoProcessCurrentJob(j.getReleaseTime());
            }
            return totalprocessing;
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getQueueWorkLoad_Route(Job j){
            double totalQWL = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalQWL+=machines[m].getQueueWorkload(j.getReleaseTime());
            }
            return totalQWL;
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getTotalSampleAverageProcessingTime_Route(Job j){
            double totalSAPRL = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalSAPRL+=machines[m].getSampleAverageProcessingTime();
            }
            return totalSAPRL;
        }
        /*
         * get the total average waiting time of machines on the route of job j
         */
        public double getTotalRemainingWorkload_Route(Job j){
            double totalRWL = 0;
            for (int i = 0; i < j.getNumberOperations(); i++) {
                int m = j.getKthMachine(i);
                totalRWL+=machines[m].getRemainingWorkload();
            }
            return totalRWL;
        }
        /*
         * calculate priority
         */
        public void calculatePriority(Machine M){
            M.calculatePriority(PT , SS, nonDelayFactor ,machines, CriticalMachineID, CurrentBottleneckID);
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
            //Collections.sort(M.getQueue());
            M.findHighestPriorityJob(M.getQueue());
        }
        /*
         * return the number of jobs in the system
         */
        public double getNumberofJobs(){
            return jobs.size();
        }
        /*
         * return the moving average arrival rate
         */
        public double getMovingAverageArrivalRate(){
            return arrivalFrequency.getEventFrequency();
        }
        /*
         * return the moving average length of job
         */
        public double getMovingAverageJobLength(){
            return lengthFrequency.getAverage();
        }
        /*
         * return the moving average error of due-date
         */
        public double getMovingAverageErrorDD(){
            return errorDDFrequency.getAverage();
        }
        /*
         * get throughput
         */
        public String getThroughput(){
            return ""+throughput;
        }
        /*
         * get percent tardiness
         */
        public double getPercentTardiness(){
            return percentTardiness/throughput;
        }
        /*
         * get total weighted tardiness
         */
        public double getNormalisedTotalWeightedTardiness(){
            double averageWeight = 0;
            for (int i = 0; i < weightDistribution.length; i++) {
                averageWeight += weightDistribution[i]*weightValues[i];
            }
            return totalWeightedTardiness/(throughput*nMachine*meanOperationTime*averageWeight);
        }
        /*
         * get total weighted tardiness
         */
        public double getTotalWeightedTardiness(){
            return totalWeightedTardiness/(throughput*5);
        }
        /*
         * print utilisation of machine
         */
        public void printMachinesUtilisation(){
            String strUtilisations = "||";
            for (int i = 0; i < machines.length; i++) {
                 strUtilisations += machines[i].getUtilisation(timeToStartCollectStat) + "||";
            }
            System.out.println(strUtilisations);
        }
        /*
         * print average utilisation of machines
         */
        public void printAverageMachinesUtilisation(){
            double avgUtilisations = 0;
            for (int i = 0; i < machines.length; i++) {
                 avgUtilisations += machines[i].getUtilisation(timeToStartCollectStat);
            }
            System.out.println("||" + avgUtilisations/machines.length + "||");
        }
        /*
         * print flow-time statistics
         */
        public void printFlowTimeStat(){
            System.out.println(flowtime.getAverage() +"+/-" + flowtime.getUnbiasedStandardDeviation());
        }
        /*
         * get makespan
         */
        public double getCmax(){
            return flowtime.getMax();
        }
        /*
         * get mean flowtime
         */
        public double getMeanFlowtime(){
            return flowtime.getAverage();
        }
        /*
         * return mean lateness
         */
        public double getMeanLateness(){
            return lateness.getAverage();
        }
        /*
         * return lateness standard deviation
         */
        public double getStdLateness(){
            return lateness.getUnbiasedStandardDeviation();
        }
        /*
         * return mean sqaure lateness
         */
        public double getMeanSquareLateness(){
            return lateness.getMeanSquare();
        }
        /*
         * return mean tardiness
         */
        public double getMeantardiness(){
            return tardiness.getAverage();
        }
        /*
         * return mean tardiness
         */
        public double getMaxTardiness(){
            return tardiness.getMax();
        }
        /*
         * return mean absolute error for duedate assingment
         */
        public double getMAE(){
            return totalABSDDError/throughput;
        }
        /*
         * return mean percentage absolute error for dudate assignment
         */
        public double getMAPE(){
            return totalPercentageABSDDError/throughput;
        }
        /*
         * get mean esitmated error
         */
        public double getMAEE(){
            return estimateError/throughput;
        }
        /*
         * return mean percentage error for dudate assignment
         */
        public double getMPE(){
            return totalPercentageDDError/throughput;
        }
        /*
         * return the actual inter breakdown times for the machines
         */
        public double getBreakdownRate() {
        	return breakdownRate;
        }
        /*
         * return the actual mean repair times for the machines
         */
        public double getMeanRepairTimes() {
        	return mRepair;
        }
        /*
         * return the average inter breakdown times for the machines
         */
        public double getAvgInterBreakdownTimes() {
        	double sum = 0.0;
        	for (int i = 0; i < nMachine; i++) {
        		sum += machines[i].getSampleAvgInterBreakdownTimes();
        	}
        	return sum / nMachine;
        }
        /*
         * return the average repair times for the machines
         */
        public double getAvgRepairTimes() {
        	double sum = 0.0;
        	for (int i = 0; i < nMachine; i++) {
        		sum += machines[i].getSampleAvgRepairTimes();
        	}
        	return sum / nMachine;
        }
        /*
         * return the average deactivation for the machines
         */
        public double getAvgDeactivation() {
        	double sum = 0.0;
        	for (int i = 0; i < nMachine; i++) {
        		sum += machines[i].numDeactivated();
        	}
        	return sum / nMachine;
        }
        /*
         * return the average disruption for the machines
         */
        public double getAvgDisruption() {
        	double sum = 0.0;
        	for (int i = 0; i < nMachine; i++) {
        		sum += machines[i].numDisruption();
        	}
        	return sum / nMachine;
        }
        /*
         * print tardiness statistics
         */
        public void printTardinessStat(){
            System.out.println(tardiness.getAverage() +"+/-" + tardiness.getUnbiasedStandardDeviation());
        }
        public String getDueDateStatistic(){
            return "MAPE "+getMAPE()+" MPE "+getMPE()+" ML "+getMeanLateness()+" STDL "+getStdLateness()
                          +" MT "+getMeantardiness()+" MAL "+ getMAE() +" MF "+getMeanFlowtime()
                          +" %Tardy "+getPercentTardiness();
        }
        /*
         * print out the final schedule and related statistics
         */
        public void print() throws FileNotFoundException{
            PrintWriter outputStream  = new PrintWriter("JSP.out");
            for (int i = 0; i < nMachine; i++) {
                outputStream.println(machines[i].toString());
            }
            outputStream.close();
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
         * check stopping condition for the simulation
         */
        public boolean isStop(){
            return throughput >= stopCondition;
        }
        /*
         * check warm-up condition
         */
        public boolean isWarmUp(){
            if (idCount < warmUpCondition) return true;
            else if (idCount >= warmUpCondition && timeToStartCollectStat == -1) {
                timeToStartCollectStat = nextArrivalTime;
                for (int i = 0; i < machines.length; i++)machines[i].stopWarmUp();
            }
            return false;
        }
}
