/*

 */


package ec.app.mmHH;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;


public class MultiMachineDJSEnsemble extends MultiMachineDJS
{

	public static final String P_GROUP_SIZE = "group_size";
	public static final String P_ITER = "iteration";

	private int groupSize = 1;
	private int numIterations = 1;

	public Map<Individual, Individual[][]> evalGroups = new HashMap<Individual, Individual[][]>();

	private Individual[] bestGroup = null;
	private double bestGroupFitness = Double.MAX_VALUE;

	private Individual[] bestGroupOfGeneration = null;
	private double bestGroupOfGenerationFitness = Double.MAX_VALUE;

	public void setup(final EvolutionState state, final Parameter base)
	{
		super.setup(state, base);

		groupSize = state.parameters.getInt(base.push(P_GROUP_SIZE), null);
		numIterations = state.parameters.getInt(base.push(P_ITER), null);
	}

	@Override
	public void prepareToEvaluate(final EvolutionState state, final int threadnum) {
		evalGroups.clear();
		bestGroupOfGeneration = null;
		bestGroupOfGenerationFitness = Double.MAX_VALUE;

		Individual[] inds = state.population.subpops[0].individuals;

		for (int i = 0; i < inds.length; i++) {
			Individual[][] evalGroup = new Individual[numIterations][groupSize];

			List<GPIndividual> remainingInds = new ArrayList<GPIndividual>(inds.length-1);
			for (Individual ind : inds) {
				if (!ind.equals(inds[i])) remainingInds.add((GPIndividual) ind);
			}

			Collections.shuffle(remainingInds, new Random(state.random[threadnum].nextLong()));

			int index = 0;
			for (int iteration = 0; iteration < numIterations; iteration++) {
				evalGroup[iteration][0] = (GPIndividual) inds[i];
				for (int count = 1; count < groupSize; count++) {
					evalGroup[iteration][count] = remainingInds.get(index);
					index++;
				}
			}

			evalGroups.put(inds[i], evalGroup);
		}
	}

	/* Evaluates the individual and sets its fitness
  /* Calls simulation method? how many simulations for each rule? same seed at every generation?   */
	public void evaluate(final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum)
	{
		if(!ind.evaluated) // don't bother reevaluating
		{
			Individual[][] evalGroup = evalGroups.get(ind);

			double sumFitness = 0;

			for (int iteration = 0; iteration < evalGroup.length; iteration++) {
				Individual[] ensemble = evalGroup[iteration];

				//state.output.systemMessage(""+numMachines+" cs:"+changeSeed+" 1tree?"+oneTree+" size"+size);

				/* THIS IS USED FOR ....
				 * double[] util = {0.85, 0.85, 0.88, 0.88, 0.92, 0.92, 0.95, 0.95};
				double[] mu = {25.0, 25.0, 25.0, 25.0, 25.0, 25.0, 25.0, 25.0};
				double[][] ddt = {{3,5,7}, {2,4,6}, {3,5,7}, {2,4,6}, {3,5,7}, {2,4,6}, {3,5,7}, {2,4,6}};
				int[] seeds = {18725838,  794921487, 106345483, 960062340, 6831611, 8519399, 4825101, 1524843};*/

				numOps=10;
				double[] util = {0.85,0.95};
				double[] mu = {25.0, 25.0};
				double[][] ddt = {{3,5,7}, {3,5,7}};
				int[] seeds = {18725838,  794921487};

				double fitness = 0;
				int[] newSeeds = new int[seeds.length];
				int k = (state.generation-(state.generation%10));
				for(int m=0; m<newSeeds.length;m++){
					newSeeds[m] = seeds[m]+167854072*k;
				}

				for(int i=0;i<seeds.length;i++){
					double[] results;
					if(changeseed==10){
						//state.output.systemMessage("newseeds: "+newSeeds[i]);
						results = simulate(state, ensemble, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, newSeeds[i], ddt[i],0);
					}
					else{
						//state.output.systemMessage("seeds: "+seeds[i]);
						results = simulate(state, ensemble, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, seeds[i], ddt[i],0);
					}
					fitness = fitness + results[0] / util[i];
				}

				seeds[0] = 106345483;
				seeds[1] = 960062340;
				numOpsConstant=0;
				for(int m=0; m<newSeeds.length;m++){
					newSeeds[m] = seeds[m]+167854072*k;
				}

				for(int i=0;i<seeds.length;i++){
					double[] results;
					if(changeseed==10){
						//state.output.systemMessage("newseeds: "+newSeeds[i]);
						results = simulate(state, ensemble, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, newSeeds[i], ddt[i],0);
					}
					else{
						//state.output.systemMessage("seeds: "+seeds[i]);
						results = simulate(state, ensemble, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, seeds[i], ddt[i],0);
					}
					fitness = fitness + results[0] / util[i];
				}
				numOpsConstant=1;

				// TODO need to add in the penalty.
				fitness = fitness / ((double) seeds.length);
				sumFitness += fitness;

				if (fitness < bestGroupFitness) {
					bestGroup = ensemble;
					bestGroupFitness = fitness;
				}

				if (fitness < bestGroupOfGenerationFitness) {
					bestGroupOfGeneration = ensemble;
					bestGroupOfGenerationFitness = fitness;
				}
			}

			((HHFitness)ind.fitness).setFitness(state,(float)(sumFitness/((double)(evalGroup.length))),false);
			if(VERB==1)  state.output.systemMessage("Fitness: "+(sumFitness/((double)(evalGroup.length))));

			ind.evaluated = true; // individual has been evaluated
		}
	}


	public void createJobs(final EvolutionState state,
			final Individual[] inds,
			final int subpopulation,
			final int threadnum,
			double util,
			DIST dist,
			double mu,
			double param2,
			int seed,
			double[] ddt,
			int test)
	{
		// Set up Machines
		machines = new ShopMachine[numMachines];
		for(int i=0; i<numMachines; i++){
			machines[i] = new ShopMachine(i, Qsize); //create machines
			machines[i].observe(0.0);
		}

		// Set up Simulation - set up random number generator
		int simSeed = seed;
		MersenneTwisterFast simuRandom = new MersenneTwisterFast(simSeed);

		int njobs; // the number of jobs in the system at the moment? that have arrived?

		// Set up array for jobs
		jobs = new ShopJob[size]; // Jobs
		currentTime = 0.0;

		// Calculate arrival rate lambda
		double lambda;
		if(numOpsConstant==1){
			lambda = (util/mu)*((double)numMachines/(double)numOps);
		}
		else{
			// The number of operations per job varies between 2 and numMachines.
			double mean = (double)(numMachines)/2.0+1.0; // the mean number of operations in each job.
			lambda = (util/mu)*((double)numMachines/mean);
		}
		//state.output.systemMessage("util/mu="+util/mu+", numMach/numops="+(double)numMachines/(double)numOps+", Lambda: "+lambda);

		int jobNumOps = numOps;

		// Generate Jobs and add to queue of machine for their first operation
		// parameter for exponential distribution of processing times.
		for(njobs=0; njobs < size; njobs++){
			currentTime += ((-1.0)*Math.log(simuRandom.nextDouble(false,false)))/lambda;
			//state.output.systemMessage(""+currentTime+" "+njobs);
			// Dynamic Job Shop - arrivals according to a Poisson process
			// Exponential inter-arrival times.
			double release = currentTime;

			// Generates the weight for the job.
			double weight;
			double w = simuRandom.nextDouble(true, true);
			if(test < 24){
				if(w < 0.20)
					weight = 1.0;
				else if(w < 0.80)
					weight = 2.0;
				else
					weight = 4.0;
			}
			else{
				if(w < 0.20)
					weight = 1.0;
				else if(w < 0.70)
					weight = 2.0;
				else if(w < 0.2)
					weight = 4.0;
				else
					weight = 8.0;
			}

			double duedate;
			double h; // The tightness parameter for duedate assigment (make this variable?)

			if(numOpsConstant==0){
				jobNumOps = simuRandom.nextInt(numMachines-1)+2;
			}


			//int numOps = simuRandom.nextInt(numMachines); // The number of ops in the job
			double[] opProc = new double[jobNumOps];
			int[] opMach = new int[jobNumOps];

			boolean[] picked = new boolean[numMachines];
			for(int i=0; i<jobNumOps; i++){
				picked[i] = false;
			}

			double totalProc = 0.0;


			// For each operation...
			for(int i=0; i<jobNumOps; i++){
				// Find the machine for the next operation
				int next = simuRandom.nextInt(numMachines);
				while(picked[next]==true){
					next = simuRandom.nextInt(numMachines);
				}

				opMach[i] = next;
				picked[next] = true;

				// Find the processing time for the next operation
				// opProc[i] = (-1*Math.log(simuRandom.nextDouble(false,false)))/mu[next];

				switch (dist) {
				case UNIFORM:
				{
					opProc[i] = simuRandom.nextInt((int)(2*mu-1))+1;
					break;
				}
				case NEGBIN:
				{
					// mu and param2 = n
					double prob = param2/(param2 +mu);
					double x = 0.0;
					for(int y=0; y < param2; y++){
						x = x + Math.floor((Math.log(simuRandom.nextDouble())/Math.log(1-prob)));
					}
					opProc[i] = x;
					break;
				}
				case EXPONENTIAL:
				{
					opProc[i] = (((-1.0)*Math.log(simuRandom.nextDouble(false,false)))*mu)+0.1;
					break;
				}
				case GEOMETRIC:
				{
					opProc[i] = Math.ceil((Math.log(simuRandom.nextDouble())/Math.log(1-(1/mu))));
					if(opProc[i]==0) state.output.systemMessage(" "+opProc[i]);
					break;
				}
				default:
					break;
				}


				//state.output.systemMessage(" "+next+" :"+opProc[i]);
				totalProc += opProc[i];
			}
			//state.output.systemMessage(" -- ");
			double k = simuRandom.nextDouble();
			int c = 1;
			int num = ddt.length;
			while(c/num < k)
				c = c+1;
			h=ddt[c-1];
			duedate = release + h*totalProc;
			jobs[njobs] = new ShopJob(njobs, release, opMach, opProc, jobNumOps, weight, duedate);

		}
	}

	/* Simulation method:
  /* Rule currently being evaluated is used to set priorities of (all) jobs
  /* throughout the simulation of the job shop.*/
	public double[] simulate(final EvolutionState state,
			final Individual[] inds,
			final int subpopulation,
			final int threadnum,
			double util,
			DIST dist,
			double mu,
			double param2,
			int seed,
			double[] ddt,
			int test)
	{
		if(VERB==1) state.output.systemMessage("Warmup: "+warmup+", nmeasure: "+nmeasure+", queuesize: "+size);
		Qsize = size;
		createJobs(state,inds,subpopulation,threadnum,util,dist,mu,param2,seed,ddt,test);
		if(VERB==1) state.output.systemMessage("jobs created");

		// Static Job Shop Objective : minimize total weighted tardiness
		double totalWeightTard = 0.0;
		double penalty = 0.0;
		numProcessed = 0;

		currentJob = jobs[0];
		currentMachine = machines[0];
		currentTime = 0.0; //reset clock now we are ready to start the simulation
		int njobs = 0;
		int collected = 0; //how many of the nmeasure jobs have finished processing.

		int nmeas = nmeasure;
		//if(test==0)
		//	nmeas = nmeasureTRAIN;
		//else
		//	nmeas = nmeasure;


		// run through until we have warmed up and had the necessary number of finished jobs.
		while(collected < nmeas){
			double nextMachReady = nextMachineReady();
			double nextArrival;
			if(njobs < jobs.length){
				nextArrival = jobs[njobs].getRelease();
			}
			else{
				if(VERB==1)  state.output.systemMessage("Ineffective rule assigned large TWT");
				for(int j=0; j<machines.length;j++){
					if(test>0){
						state.output.systemMessage("Test"+test+" M"+j+". tA: "+machines[j].timeAverage());
						state.output.systemMessage("Test"+test+" M"+j+". UT: "+(1-machines[j].idleTime()));
					}
				}
				return new double[]{10000000, 10000000}; // rule is bad at scheduling and tonnes of jobs have arrived before the nmeas jobs after warmup were processed.
			}

			if(nextMachReady < nextArrival)
			{
				// Find next machine to be ready for new job
				nextMachine();

				/*if(currentMachine.getNumJobQueue()==0){
					for(int j=currentMachine.getMachineID(); j < numMachines; j++){
						if(Math.abs(machines[j].getReadyTime()-currentMachine.getReadyTime())< 0.0001)
							if(machines[j].getNumJobQueue()>0)
								currentMachine=machines[j];
					}
				}*/


				if(currentTime>0.0 && currentMachine.getReadyTime()>= currentTime){
					//state.output.systemMessage(""+currentMachine.getJob(currentMachine.getCurrent())); //HERE
					currentJob = jobs[currentMachine.getJob(currentMachine.getCurrent())];

					// Finish processing - remove job from queue and move it to next op
					if(VERB==1)  state.output.systemMessage("Job "+currentJob.getArrID()+" op finishes at Machine "+currentMachine.getMachineID()+" at "+currentTime);
					currentTime = currentMachine.getReadyTime();
					currentJob.incrCurrentOp();
					//state.output.systemMessage("Job "+currentJob.getArrID()+", remop#"+currentJob.getRO());



					currentMachine.removeJob(currentMachine.getCurrent());
					currentMachine.observe(currentTime);
					//	if(VERB==1)  state.output.systemMessage("Time-Average # in queue at Machine "+currentMachine.getMachineID()+": "+currentMachine.timeAverage());
					//	if(VERB==1)  state.output.systemMessage("prop Idle time queue at Machine "+currentMachine.getMachineID()+": "+currentMachine.idleTime());

					// Remaining operations - job needs to move queues.
					if(currentJob.getRO() > 0){
						machines[currentJob.getCurrOpMach()].addToQueue(currentJob.getArrID());
						if(VERB==1)  state.output.systemMessage("Added job "+currentJob.getArrID()+" to queue at Machine "+currentJob.getCurrOpMach());
					}
					// otherwise job has been finished!
					else{
						if(VERB==1)  state.output.systemMessage("Job " +currentJob.getArrID()+" finished at " + currentTime+".");
						numProcessed++;
						//state.output.systemMessage("	numProc"+numProcessed);
						//state.output.systemMessage("		ID:"+currentJob.getArrID());
						if(currentJob.getArrID()>=warmup && currentJob.getArrID()<(warmup+nmeas)){
							totalWeightTard += currentJob.getWeight()*Math.max(0.0, currentTime - currentJob.getDuedate());
							penalty += 0.0; // TODO add the penalty factor.
							collected++;
							//state.output.systemMessage("		ID:"+currentJob.getArrID()+"  in range collected:"+collected);
						}
					}
				}

				// If there are jobs remaining in the queue
				if(currentMachine.getNumJobQueue()>0){
					// Find and start processing next job
					evaluateQueue(state, inds, subpopulation, threadnum);
				}
			}
			else if(njobs < size)
				// new job arrives before either machine is ready for a new job
			{

				if(VERB==1)  {if(njobs>warmup+nmeas) state.output.systemMessage("new arrival "+currentTime);}
				currentTime = jobs[njobs].getRelease();
				machines[jobs[njobs].getCurrOpMach()].addToQueue(njobs);
				if(VERB==1){
					if(jobs[njobs].getNumOps()==2) state.output.systemMessage("Clock: "+currentTime+", Job "+njobs+", m"+jobs[njobs].getOpMach()[0]+": "+jobs[njobs].getOpProc()[0]+", m"+jobs[njobs].getOpMach()[1]+": "+jobs[njobs].getOpProc()[1]);
					else if(jobs[njobs].getNumOps()==2) state.output.systemMessage("Clock: "+currentTime+",Job "+njobs+", m"+jobs[njobs].getOpMach()[0]+": "+jobs[njobs].getOpProc()[0]+".");
				}
				if(VERB==1)  state.output.systemMessage("Added job "+njobs+" to queue at Machine "+1);
				machines[jobs[njobs].getCurrOpMach()].observe(currentTime);
				njobs++;
				//	if(VERB==1)  state.output.systemMessage("Time-Average # in queue at Machine "+jobs[njobs].getCurrOpMach()+": "+machines[jobs[njobs].getCurrOpMach()].timeAverage());
				//	if(VERB==1)  state.output.systemMessage("prop Idle time queue at Machine "+jobs[njobs].getCurrOpMach()+": "+machines[jobs[njobs].getCurrOpMach()].idleTime());
			}

		}
		if(VERB==1)  state.output.systemMessage("Total Weighted Tardiness: "+totalWeightTard);
		if(VERB==1)  state.output.systemMessage("Clock: "+currentTime);
		if(VERB==1)  state.output.systemMessage("");

		/*double maxm = (-1)*MAX;
		for(int k=0;k<jobs.length; k++){
			if(jobs)
		}*/
		for(int j=0; j<machines.length;j++){
			if(test>0){
				state.output.systemMessage("Test"+test+" M"+j+". tA: "+Math.round(machines[j].timeAverage()*10000.0)/10000.0);
				state.output.systemMessage("Test"+test+" M"+j+". UT: "+Math.round((1-machines[j].idleTime())*10000.0)/10000.0);
				// state.output.systemMessage("M"+j+". tA: "+machines[j].timeAverage()+", UT: "+(1-machines[j].idleTime()));
			}
		}
		//state.output.systemMessage("Total Weighted Tardiness: "+totalWeightTard);
		if(VERB==1)  state.output.systemMessage("");
		if(test>0) state.output.systemMessage("Final Clock: "+currentTime);
		return new double[]{totalWeightTard, penalty};
	}


	/* Evaluates all jobs in the queue at the machine using the current dispatching rule.
	 * The job with the highest priority is selected to be evaluated next.
	 * Returns the position of the highest priority job in the queue.*/
	public void evaluateQueue(final EvolutionState state,
			final Individual[] inds,
			final int subpopulation,
			final int threadnum)
	{
		int j;
		int maxPosition = 0;
		double max = (-1)*MAX;

		double minPR=0;
		int tie = 0;

		if(VERB==1) {
			for (Individual ind : inds) ind.printIndividualForHumans(state, 1);
		}
		//if((numMachEvol<numMachines) && (currentMachine.getMachineID()<numMachEvol)) state.output.systemMessage("Machine "+currentMachine.getMachineID()+" evolving rule");
		//else 			state.output.systemMessage("Machine "+currentMachine.getMachineID()+" EDD");


		double[][] priorities = new double[inds.length][currentMachine.getNumJobQueue()];

		for(j=0; j < currentMachine.getNumJobQueue(); j++){

			DoubleData rd = new DoubleData();

			currentJob = jobs[currentMachine.getJob(j)];

			for (int k=0; k < inds.length; k++) {
				if((numMachEvol<numMachines)){
					if(currentMachine.getMachineID()<numMachEvol){
						((GPIndividual)inds[k]).trees[0].child.eval(state,threadnum,rd,stack,((GPIndividual)inds[k]),this);
						priorities[k][j] = rd.x;
					}
					else{
						//currentJob.setPriority(j); //FIFO
						//currentJob.setPriority(-1*currentJob.getDuedate()); // EDD
						priorities[k][j] = -1*((double)(currentJob.getPR()))/currentJob.getWeight(); // WSPT
					}
				}
				else{
					((GPIndividual)inds[k]).trees[0].child.eval(state,threadnum,rd,stack,((GPIndividual)inds[k]),this);
					priorities[k][j] = rd.x;
				}
			}

			/*if(numTrees==1){
				((GPIndividual)ind).trees[0].child.eval(state,threadnum,rd,stack,((GPIndividual)ind),this);
			}
			else{
				((GPIndividual)ind).trees[currentMachine.getMachineID()].child.eval(state,threadnum,rd,stack,((GPIndividual)ind),this);
			}*/
		}

		for (j=0; j < priorities.length; j++) {
			double maxPriority = Double.NEGATIVE_INFINITY;
			for (int k=0; k < priorities[j].length; k++) {
				if (priorities[j][k] > maxPriority) {
					maxPriority = priorities[j][k];
				}
			}

			double sumPriority = 0.0;
			for (int k=0; k < priorities[j].length; k++) {
				sumPriority += Math.exp(priorities[j][k] - maxPriority);
			}

			double bestPriority = Double.NEGATIVE_INFINITY;
			int bestIndex = -1;
			for (int k=0; k < priorities[j].length; k++) {
				double normalisedPriority = Math.exp(priorities[j][k] - maxPriority - Math.log(sumPriority));
				if (normalisedPriority > bestPriority) {
					bestPriority = normalisedPriority;
					bestIndex = k;
				}
			}

			currentJob = jobs[currentMachine.getJob(bestIndex)];
			currentJob.setPriority(currentJob.getPriority() + bestPriority);
			//state.output.systemMessage(" "+currentJob.getPriority());

			if (max<currentJob.getPriority()) {
				max = currentJob.getPriority();
				maxPosition = bestIndex;
				minPR = currentJob.getPR();
				tie = 0;
			} else if (Math.abs(max-currentJob.getPriority()) < 0.0001) {
				if (currentJob.getPR() < minPR) {
					max = currentJob.getPriority();
					maxPosition = bestIndex;
					minPR = currentJob.getPR();
				}
				tie++;
			}
		}

		currentMachine.setCurrent(maxPosition);
		currentJob = jobs[currentMachine.getJob(maxPosition)];

		double wait = currentTime-currentJob.getRJ();
		double ready = currentTime+currentJob.getPR();

		currentJob.setOpComplete(currentJob.getCurrentOp(), ready);

		currentMachine.setReadyTime(ready);

		currentMachine.obsWait(wait);

		if(VERB==1) state.output.systemMessage("Job "+currentJob.getArrID()+" op starts at Machine "+currentMachine.getMachineID()+" at "+currentTime);
	}


	// updates the total work in queue for each of the machines in the shop, as well as setting the average.
	public void updateTWK(){
		double totalTWK = 0.0;
		for(int i=0; i < numMachines; i++){
			double total = 0.0;
			int nj = machines[i].getNumJobQueue();

			for(int j=0; j < nj; j++){
				total = total + jobs[machines[i].getJob(j)].getPR();
			}
			machines[i].setTWK(total);
			totalTWK = totalTWK + total;
		}
		TWKav = totalTWK/((double)(numMachines));
	}


	/* Returns the machine which is ready next. */
	public void nextMachine()
	{
		double min = MAX;
		int minMach = -1;
		for(int i=0; i < numMachines; i++){
			if(machines[i].getNumJobQueue()>0 ){
				if(machines[i].getReadyTime() < min ){
					min = machines[i].getReadyTime() ;
					minMach = i;
				}
			}
		}
		if(min >= currentTime){
			currentTime = min;
			currentMachine = machines[minMach];
		}
		else{
			currentMachine = machines[minMach];
			currentMachine.setCurrent(0);
		}
	}

	/* Returns the minimum ready time of the machines. */
	public double nextMachineReady()
	{
		double min = MAX;
		for(int i=0; i < numMachines; i++){
			if(machines[i].getNumJobQueue()>0 ){
				if(machines[i].getReadyTime() < min ){
					min = machines[i].getReadyTime() ;
				}
			}
		}
		return min;
	}


	/* "Reevaluates" an individual, for the purpose of printing out
    interesting facts about the individual in the context of the
    Problem, and logs the results.  This might be called to print out
    facts about the best individual in the population, for example.  */

	public void describe(
			final EvolutionState state,
			final Individual ind,
			final int subpopulation,
			final int threadnum,
			final int log)
	{
		long startTime = System.currentTimeMillis();
		int simCount = 1;

		// Rajendran and Holthaus 1999
		// 10 Machines
		// 10 ops or "missing ops" i.e. numOps \in {2,3,4,5,6,7,8,9,10}
		double[] util = {0.80, 0.85, 0.90, 0.95};
		double[] mu = {25.0, 25.0, 25.0, 25.0};//,
		double[][] ddt = {{4},{6},{8}};

		numOps = 10;

		int[] seeds = {66157202, 1455390, 4338497, 7808008, 9558216, 5193882, 2028920, 4164487, 6235324, 4287392, 5981541, 7465431, 6235324};
		double value = 0;
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				double sim = simulate(state, ind, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value = value + sim;
				state.output.message("Test " +simCount+" Fitness: " + Math.round(sim*100.0)/100.0);
				simCount++;
			}
		}
		numOpsConstant = 0;
		int[] seeds2 = {2702111, 8090781, 4804228, 5675939, 9506898, 3106570, 1332915, 2736016, 3974059, 8351905, 6612731, 8978166};
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				double sim = simulate(state, ind, subpopulation, threadnum, util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value = value + sim;
				state.output.message("Test " +simCount+" Fitness: " + Math.round(sim*100.0)/100.0);
				simCount++;
			}
		}

		state.output.systemMessage("Testing time (ms): "+(System.currentTimeMillis()-startTime));

		state.output.systemMessage("================================================================");
		state.output.systemMessage("Extreme Testing");
		startTime = System.currentTimeMillis();

		numOps = 10;
		numOpsConstant = 1;

		double paramN = 5.0;
		double[][] ddt2 = {{4,6,8},{3,5,7},{3,4,5}};

		int[] seeds3 = {5852060, 7492786, 5248371, 9695503, 5020477, 6917852, 3844242, 5149016, 8019679, 8740548, 7686878, 6098854};
		value = 0;
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				double sim = simulate(state, ind, subpopulation, threadnum, util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value = value + sim;
				state.output.message("Test " +simCount+" Fitness: " + Math.round(sim*100.0)/100.0);
				simCount++;
			}
		}
		numOpsConstant = 0;
		int[] seeds4 = {7033856, 8443495, 9287616, 9181720, 5734468, 3143314, 9755635, 5864038, 8199550, 6430259, 9482495, 5230788};
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				double sim = simulate(state, ind, subpopulation, threadnum, util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value = value + sim;
				state.output.message("Test " +simCount+" Fitness: " + Math.round(sim*100.0)/100.0);
				simCount++;
			}
		}

		if(VERB==1)  state.output.systemMessage("Test fitness: "+(value/(double)(simCount)));

		state.output.message("Change seed ="+changeseed);
		state.output.message(""+((HHFitness)ind.fitness).testFitnessToStringForHumans());
		state.output.systemMessage("Extreme testing time (ms): "+(System.currentTimeMillis()-startTime));



		String s = seed+"dot.txt";
		File f = new File(s);
		try{PrintStream ps = new PrintStream(s);
		((GPIndividual)ind).trees[0].printStyle = 1;
		((GPIndividual)ind).trees[0].printTreeForHumans(ps, 0);
		ps.close();}

		catch(FileNotFoundException ex){

		}
		String s2 = seed+"final.txt";
		File f2 = new File(s2);
		try{PrintStream ps2 = new PrintStream(s2);
		((GPIndividual)ind).trees[0].printStyle = 0;
		((GPIndividual)ind).trees[0].printTreeForHumans(ps2, 0);
		ps2.close();}

		catch(FileNotFoundException ex){

		}



		((GPIndividual)ind).trees[0].printStyle = 1;
		((GPIndividual)ind).trees[0].printTreeForHumans(state, log, 0);
		//((GPIndividual)ind).trees[1].printStyle = 1;
		//((GPIndividual)ind).trees[1].printTreeForHumans(state, log, 0);
		//((GPIndividual)ind).trees[2].printStyle = 1;
		//((GPIndividual)ind).trees[2].printTreeForHumans(state, log, 0);

	}


}