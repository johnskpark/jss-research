/*

 */


package ec.app.mmHH;
import ec.util.*;
import ec.app.mmHH.MultiMachineDJS.DIST;
import ec.gp.*;
import ec.simple.*;
import ec.EvolutionState;
import ec.Individual;

public class MMDJSBenchmark extends GPProblem implements SimpleProblemForm
{
	private static final long serialVersionUID = 1;

	public static ShopJob currentJob;
	public static ShopMachine currentMachine;

	public static int numMachines = 10;
	public static int numOps = 4;

	public static int warmup = 500; // warm up period for dynamic job shop
	public static int nmeasure = 2000; // number of jobs we want to calculate objective function from
	public static int size = 100000;   // number of jobs we want to create
	public static int Qsize = 100000; // queue capacity at each machine

	public static int numProcessed; // Number of jobs completely processed.

	public static ShopJob[] jobs;
	public static ShopMachine[] machines;

	public static int numOpsConstant=1;

	public static int VERB = 0;
	public static final double MAX = Double.MAX_VALUE;

	public static double currentTime;

	public static enum RULE { WSPT, FIFO, MS, EDD, ATC, wCOVERT};
	public static RULE rule = RULE.MS;

	public double TWKav;

	/* "Reevaluates" an individual, for the purpose of printing out
    interesting facts about the individual in the context of the
    Problem, and logs the results.  This might be called to print out
    facts about the best individual in the population, for example.  */

	public static void main(String[] args)
	{
		long startTime = System.currentTimeMillis();
		double[][] results = new double[50][6];


		// N
		double[] util = {0.80, 0.85, 0.90, 0.95};
		double[] mu = {25.0, 25.0, 25.0, 25.0};//,
		double[][] ddt = {{4},{6},{8}};

		numOps = 10;
		int[] seeds = {66157202, 1455390, 4338497, 7808008, 9558216, 5193882, 2028920, 4164487, 6235324, 4287392, 5981541, 7465431, 6235324};
		double[] value = new double[6];
		double[] time = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

		int simCount = 0;

		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				System.out.println("Objective: MS");
				startTime = System.currentTimeMillis();
				rule = RULE.MS;
				double sim1 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[0] = value[0] + sim1;
				results[simCount][0]=sim1;
				time[0] = time[0] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: WSPT");
				startTime = System.currentTimeMillis();
				rule = RULE.WSPT;
				double sim2 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[1] = value[1] + sim2;
				results[simCount][1]=sim2;
				time[1] = time[1] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: FIFO");
				startTime = System.currentTimeMillis();
				rule = RULE.FIFO;
				double sim3 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[2] = value[2] + sim3;
				results[simCount][2]=sim3;
				time[2] = time[2] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: EDD");
				startTime = System.currentTimeMillis();
				rule = RULE.EDD;
				double sim4 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[3] = value[3] + sim4;
				results[simCount][3]=sim4;
				time[3] = time[3] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.ATC;
				System.out.println("Objective: ATC");
				double sim5 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[4] = value[4] + sim5;
				results[simCount][4]=sim5;
				time[4] = time[4] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.wCOVERT;
				System.out.println("Objective: wCOVERT");
				double sim6 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds[((i*ddt.length)+j)], ddt[j], simCount);
				value[5] = value[5] + sim6;
				results[simCount][5]=sim6;
				time[5] = time[5] + (System.currentTimeMillis()-startTime);

				simCount++;
			}
		}

		numOpsConstant = 0;
		int[] seeds2 = {2702111, 8090781, 4804228, 5675939, 9506898, 3106570, 1332915, 2736016, 3974059, 8351905, 6612731, 8978166};
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){
				System.out.println("Objective: MS");
				startTime = System.currentTimeMillis();
				rule = RULE.MS;
				double sim1 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value[0] = value[0] + sim1;
				results[simCount][0]=sim1;
				time[0] = time[0] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: WSPT");
				startTime = System.currentTimeMillis();
				rule = RULE.WSPT;
				double sim2 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value[1] = value[1] + sim2;
				results[simCount][1]=sim2;
				time[1] = time[1] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: FIFO");
				startTime = System.currentTimeMillis();
				rule = RULE.FIFO;
				double sim3 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value[2] = value[2] + sim3;
				results[simCount][2]=sim3;
				time[2] = time[2] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: EDD");
				startTime = System.currentTimeMillis();
				rule = RULE.EDD;
				double sim4 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				System.out.println(""+sim4);
				value[3] = value[3] + sim4;
				results[simCount][3]=sim4;
				time[3] = time[3] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.ATC;
				System.out.println("Objective: ATC");
				double sim5 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value[4] = value[4] + sim5;
				results[simCount][4]=sim5;
				time[4] = time[4] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.wCOVERT;
				System.out.println("Objective: wCOVERT");
				double sim6 = simulate(util[i], DIST.UNIFORM, mu[i], 0.0, seeds2[(i*ddt.length)+j], ddt[j], simCount);
				value[5] = value[5] + sim6;
				results[simCount][5]=sim6;
				time[5] = time[5] + (System.currentTimeMillis()-startTime);

				simCount++;
			}
		}
		System.out.println("================================================================");
		System.out.println("Extreme Testing");

		numOps = 10;
		numOpsConstant = 1;
		double paramN = 5.0;
		double[][] ddt2 = {{4,6,8},{3,5,7},{3,4,5}};

		int[] seeds3 = {5852060, 7492786, 5248371, 9695503, 5020477, 6917852, 3844242, 5149016, 8019679, 8740548, 7686878, 6098854};
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){

				System.out.println("Objective: MS");
				startTime = System.currentTimeMillis();
				rule = RULE.MS;
				double sim1 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[0] = value[0] + sim1;
				results[simCount][0]=sim1;
				time[0] = time[0] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: WSPT");
				startTime = System.currentTimeMillis();
				rule = RULE.WSPT;
				double sim2 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[1] = value[1] + sim2;
				results[simCount][1]=sim2;
				time[1] = time[1] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: FIFO");
				startTime = System.currentTimeMillis();
				rule = RULE.FIFO;
				double sim3 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[2] = value[2] + sim3;
				results[simCount][2]=sim3;
				time[2] = time[2] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: EDD");
				startTime = System.currentTimeMillis();
				rule = RULE.EDD;
				double sim4 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[3] = value[3] + sim4;
				results[simCount][3]=sim4;
				time[3] = time[3] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.ATC;
				System.out.println("Objective: ATC");
				double sim5 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[4] = value[4] + sim5;
				results[simCount][4]=sim5;
				time[4] = time[4] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.wCOVERT;
				System.out.println("Objective: wCOVERT");
				double sim6= simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds3[(i*ddt.length)+j], ddt2[j], simCount);
				value[5] = value[5] + sim6;
				results[simCount][5]=sim6;
				time[5] = time[5] + (System.currentTimeMillis()-startTime);

				simCount++;
			}
		}
		numOpsConstant = 0;
		int[] seeds4 = {7033856, 8443495, 9287616, 9181720, 5734468, 3143314, 9755635, 5864038, 8199550, 6430259, 9482495, 5230788};
		for(int i=0;i<util.length;i++){
			for(int j=0; j< ddt.length; j++){

				System.out.println("Objective: MS");
				startTime = System.currentTimeMillis();
				rule = RULE.MS;
				double sim1 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[0] = value[0] + sim1;
				results[simCount][0]=sim1;
				time[0] = time[0] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: WSPT");
				startTime = System.currentTimeMillis();
				rule = RULE.WSPT;
				double sim2 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[1] = value[1] + sim2;
				results[simCount][1]=sim2;
				time[1] = time[1] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: FIFO");
				startTime = System.currentTimeMillis();
				rule = RULE.FIFO;
				double sim3 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[2] = value[2] + sim3;
				results[simCount][2]=sim3;
				time[2] = time[2] + (System.currentTimeMillis()-startTime);

				System.out.println("Objective: EDD");
				startTime = System.currentTimeMillis();
				rule = RULE.EDD;
				double sim4 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[3] = value[3] + sim4;
				results[simCount][3]=sim4;
				time[3] = time[3] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.ATC;
				System.out.println("Objective: ATC");
				double sim5 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[4] = value[4] + sim5;
				results[simCount][4]=sim5;
				time[4] = time[4] + (System.currentTimeMillis()-startTime);

				startTime = System.currentTimeMillis();
				rule = RULE.wCOVERT;
				System.out.println("Objective: wCOVERT");
				double sim6 = simulate(util[i], DIST.GEOMETRIC, mu[i], paramN, seeds4[(i*ddt.length)+j], ddt2[j], simCount);
				value[5] = value[5] + sim6;
				results[simCount][5]=sim6;
				time[5] = time[5] + (System.currentTimeMillis()-startTime);

				simCount++;

			}
		}

		System.out.println(" & WPST & FIFO & MS & EDD & ATC & WCOVERT\\");
		for(int i=0; i < simCount; i++){
			System.out.println("P"+i+" & "+results[i][1]+" & "+results[i][2]+" & "+results[i][0]+" & "+results[i][3]+" & "+results[i][4]+" & "+results[i][5]+"\\");
		}
		System.out.println("Average  & "+(value[1]/simCount)+" & "+(value[2]/simCount)+" & "+(value[0]/simCount)+" & "+(value[3]/simCount)+" & "+(value[4]/simCount)+" & "+(value[5]/simCount)+"\\");
		System.out.println("Test Time  & "+time[1]+" & "+time[2]+" & "+time[0]+" & "+time[3]+" & "+time[4]+" & "+time[5]+"\\");

		/*	//

	//	double[] util = {0.82, 0.82, 0.82, 0.82, 0.97, 0.97, 0.97, 0.97};
	//	double[] mu = {25.0, 25.0, 50.0, 50.0, 25.0, 25.0, 50.0, 50.0};//,
	//	double[][] ddt = {{3,5,7},{4,6,8},{3,5,7},{4,6,8},{3,5,7},{4,6,8},{3,5,7},{4,6,8}};
	//	int[] seeds = {6941802, 3256619, 6568285, 9750797, 3379814, 3135511, 7776464, 1439564};// , 5662436, 9308862, 7291505, 9153816, 5050059, 6751375};//, 8445678, 1453193, 4877436};
	//  double[][] results = new double[util.length+2][6];


		System.out.println("Objective: MS");
		double value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			results[i][0]=sim;
			System.out.println("Test " +i+" Fitness: " + sim);
		}
        results[util.length][0]= (value/((double)(util.length)));
        results[util.length+1][0] = (System.currentTimeMillis()-startTime);

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");

		startTime = System.currentTimeMillis();
		rule = RULE.WSPT;
		System.out.println("Objective: WSPT");
		value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			System.out.println("Test " +i+" Fitness: " + sim);
			results[i][1]=sim;
		}

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");
        results[util.length][1]= (value/((double)(util.length)));
        results[util.length+1][1] = (System.currentTimeMillis()-startTime);

        startTime = System.currentTimeMillis();
		rule = RULE.FIFO;
		System.out.println("Objective: FIFO");
		value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			System.out.println("Test " +i+" Fitness: " + sim);
			results[i][2]=sim;
		}

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");
        results[util.length][2]= (value/((double)(util.length)));
        results[util.length+1][2] = (System.currentTimeMillis()-startTime);

        startTime = System.currentTimeMillis();
        rule = RULE.EDD;
		System.out.println("Objective: EDD");
		value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			System.out.println("Test " +i+" Fitness: " + sim);
			results[i][3]=sim;
		}

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");
        results[util.length][3]= (value/((double)(util.length)));
        results[util.length+1][3] = (System.currentTimeMillis()-startTime);


        startTime = System.currentTimeMillis();
        rule = RULE.ATC;
		System.out.println("Objective: ATC");
		value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			System.out.println("Test " +i+" Fitness: " + sim);
			results[i][4]=sim;
		}

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");
        results[util.length][4]= (value/((double)(util.length)));
        results[util.length+1][4] = (System.currentTimeMillis()-startTime);


        startTime = System.currentTimeMillis();
        rule = RULE.wCOVERT;
		System.out.println("Objective: wCOVERT");
		value = 0;
		for(int i=0;i<util.length;i++){
			double sim = simulate( util[i], mu[i], seeds[i], ddt[i]);
			value = value + sim;
			System.out.println("Test " +i+" Fitness: " + sim);
			results[i][5]=sim;
		}

		if(VERB==0)  System.out.println("Test fitness: "+(value/((double)(util.length))));
		System.out.println("Test Fitness: " + (value/((double)(util.length))));
		System.out.println("Testing time (ms): "+(System.currentTimeMillis()-startTime)+"ms");
        results[util.length][5]= (value/((double)(util.length)));
        results[util.length+1][5] = (System.currentTimeMillis()-startTime);
		 */

	}


	public static void createJobs(
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
					if(opProc[i]==0) System.out.println(""+opProc[i]);
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


	/*public static void createJobs(
			double util,
			double mu,
			int seed,
			double[] ddt)
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
		double lambda = (util/mu)*((double)numMachines/(double)numOps);
		//state.output.systemMessage("util/mu="+util/mu+", numMach/numops="+(double)numMachines/(double)numOps+", Lambda: "+lambda);

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
			if(w < 0.20)
				weight = 1.0;
			else if(w < 0.80)
				weight = 2.0;
			else
				weight = 4.0;

			double duedate;
			double h = 1.3; // The tightness parameter for duedate assigment (make this variable?)

			//int numOps = simuRandom.nextInt(numMachines); // The number of ops in the job
			double[] opProc = new double[numOps];
			int[] opMach = new int[numOps];

			boolean[] picked = new boolean[numMachines];
			for(int i=0; i<numOps; i++){
				picked[i] = false;
			}

			double totalProc = 0.0;

			// For each operation...
			for(int i=0; i<numOps; i++){
				// Find the machine for the next operation
				int next = simuRandom.nextInt(numMachines);
				while(picked[next]==true){
					next = simuRandom.nextInt(numMachines);
				}

				opMach[i] = next;
				picked[next] = true;

				// Find the processing time for the next operation
				// opProc[i] = (-1*Math.log(simuRandom.nextDouble(false,false)))/mu[next];
				opProc[i] = simuRandom.nextInt(2*(int)mu-1)+1;
				if(opProc[i]==0) System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH");

				//state.output.systemMessage(" "+next+" :"+opProc[i]);
				totalProc += opProc[i];
			}
			//state.output.systemMessage(" -- ");
			double k = simuRandom.nextDouble();
			if(k < (1.0/3.0))
				h=ddt[0];
			else if(k < 0.67)
				h=ddt[1];
			else
				h=ddt[2];
			duedate = release + h*totalProc;

			jobs[njobs] = new ShopJob(njobs, release, opMach, opProc, numOps, weight, duedate);

		}
	}*/

	public static double simulate(
			double util,
			DIST dist,
			double mu,
			double param2,
			int seed,
			double[] ddt,
			int test)
	{

		if(VERB==1)System.out.println("Warmup: "+warmup+", nmeasure: "+nmeasure+", queuesize: "+size);
		Qsize = size;
		createJobs(util,dist,mu,param2,seed,ddt,test);
		if(VERB==1)System.out.println("jobs created");


		// Static Job Shop Objective : minimize total weighted tardiness
		double totalWeightTard = 0.0;
		numProcessed = 0;

		currentJob = jobs[0];
		currentMachine = machines[0];
		currentTime = 0.0; //reset clock now we are ready to start the simulation
		int njobs = 0;
		int collected = 0; //how many of the nmeasure jobs have finished processing.

		// run through until we have warmed up and had the necessary number of finished jobs.
		while(collected < nmeasure){
			double nextMachReady = nextMachineReady();
			double nextArrival;
			if(njobs < jobs.length){
				nextArrival = jobs[njobs].getRelease();
			}
			else{
				if(VERB==1) System.out.println("Ineffective rule assigned large TWT");
				for(int j=0; j<machines.length;j++){
					System.out.println("M"+j+". tA: "+machines[j].timeAverage()+", UT: "+(1-machines[j].idleTime()));
				}
				return 10000000; // rule is bad at scheduling and tonnes of jobs have arrived before the nmeasure jobs after warmup were processed.
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
					if(VERB==1) System.out.println("Job "+currentJob.getArrID()+" op finishes at Machine "+currentMachine.getMachineID()+" at "+currentTime);
					currentTime = currentMachine.getReadyTime();
					currentJob.incrCurrentOp();
					//state.output.systemMessage("Job "+currentJob.getArrID()+", remop#"+currentJob.getRO());



					currentMachine.removeJob(currentMachine.getCurrent());
					currentMachine.observe(currentTime);
					//	if(VERB==1) System.out.println("Time-Average # in queue at Machine "+currentMachine.getMachineID()+": "+currentMachine.timeAverage());
					//	if(VERB==1) System.out.println("prop Idle time queue at Machine "+currentMachine.getMachineID()+": "+currentMachine.idleTime());

					// Remaining operations - job needs to move queues.
					if(currentJob.getRO() > 0){
						machines[currentJob.getCurrOpMach()].addToQueue(currentJob.getArrID());
						if(VERB==1) System.out.println("Added job "+currentJob.getArrID()+" to queue at Machine "+currentJob.getCurrOpMach());
					}
					// otherwise job has been finished!
					else{
						if(VERB==1) System.out.println("Job " +currentJob.getArrID()+" finished at " + currentTime+".");
						numProcessed++;
						//state.output.systemMessage("	numProc"+numProcessed);
						//state.output.systemMessage("		ID:"+currentJob.getArrID());
						if(currentJob.getArrID()>=warmup && currentJob.getArrID()<(warmup+nmeasure)){
							totalWeightTard += currentJob.getWeight()*Math.max(0.0, currentTime - currentJob.getDuedate());
							collected++;
							//state.output.systemMessage("		ID:"+currentJob.getArrID()+"  in range collected:"+collected);
						}
					}
				}

				// If there are jobs remaining in the queue
				if(currentMachine.getNumJobQueue()>0){
					// Find and start processing next job
					evaluateQueue();
				}
			}
			else if(njobs < size)
				// new job arrives before either machine is ready for a new job
			{

				if(VERB==1)  {if(njobs>warmup+nmeasure)System.out.println("new arrival "+currentTime);}
				currentTime = jobs[njobs].getRelease();
				machines[jobs[njobs].getCurrOpMach()].addToQueue(njobs);
				if(VERB==1){
					if(jobs[njobs].getNumOps()==2)System.out.println("Clock: "+currentTime+", Job "+njobs+", m"+jobs[njobs].getOpMach()[0]+": "+jobs[njobs].getOpProc()[0]+", m"+jobs[njobs].getOpMach()[1]+": "+jobs[njobs].getOpProc()[1]);
					else if(jobs[njobs].getNumOps()==2)System.out.println("Clock: "+currentTime+",Job "+njobs+", m"+jobs[njobs].getOpMach()[0]+": "+jobs[njobs].getOpProc()[0]+".");
				}
				if(VERB==1) System.out.println("Added job "+njobs+" to queue at Machine "+1);
				machines[jobs[njobs].getCurrOpMach()].observe(currentTime);
				njobs++;
				//	if(VERB==1) System.out.println("Time-Average # in queue at Machine "+jobs[njobs].getCurrOpMach()+": "+machines[jobs[njobs].getCurrOpMach()].timeAverage());
				//	if(VERB==1) System.out.println("prop Idle time queue at Machine "+jobs[njobs].getCurrOpMach()+": "+machines[jobs[njobs].getCurrOpMach()].idleTime());
			}

		}
		if(VERB==1) System.out.println("Total Weighted Tardiness: "+totalWeightTard);
		if(VERB==1) System.out.println("Clock: "+currentTime);
		if(VERB==1) System.out.println("");

		/*double maxm = (-1)*MAX;
		for(int k=0;k<jobs.length; k++){
			if(jobs)
		}*/
		for(int j=0; j<machines.length;j++){
			System.out.println("M"+j+". tA: "+machines[j].timeAverage()+", UT: "+(1-machines[j].idleTime()));
			//System.out.println("M"+j+". tA: "+machines[j].timeAverage()+", UT: "+(1-machines[j].idleTime()));
		}
		//state.output.systemMessage("Total Weighted Tardiness: "+totalWeightTard);
		if(VERB==1) System.out.println("");
		return totalWeightTard;
	}


	/* Evaluates all jobs in the queue at the machine using the current dispatching rule.
	 * The job with the highest priority is selected to be evaluated next.
	 * Returns the position of the highest priority job in the queue.*/
	public static void evaluateQueue()
	{
		int j;

		switch (rule) {
		case WSPT:
		{
			// Calculate weighted shortest processing time for each job in queue
			int minPosition = 0;
			double min = MAX;
			double minPR = 0;
			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				currentJob = jobs[currentMachine.getJob(j)];
				currentJob.setPriority((double)(currentJob.getPR())/currentJob.getWeight());
				if((min>currentJob.getPriority())==true){
					min = currentJob.getPriority();
					minPosition = j;
					minPR = currentJob.getPR();
				}
				if(Math.abs(min-currentJob.getPriority())<0.00001){
					if(currentJob.getPR() < minPR){
						min = currentJob.getPriority();
						minPosition = j;
						minPR = currentJob.getPR();
					}
				}
			}
			currentMachine.setCurrent(minPosition);
			currentJob = jobs[currentMachine.getJob(minPosition)];
			break;
		}

		case FIFO:
		{
			// FIFO just takes the first job in the queue
			currentMachine.setCurrent(0);
			currentJob = jobs[currentMachine.getJob(0)];
			break;
		}

		case MS:
		{
			// Calculate minimum slack for each job in queue
			// MS = duedate - remaining time - readytime of machine
			int minPosition = -1;
			double min = MAX;
			double minPR = 0;
			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				currentJob = jobs[currentMachine.getJob(j)];
				currentJob.setPriority(currentJob.getDuedate()-currentJob.getRT()-currentMachine.getReadyTime());
				if((min>currentJob.getPriority())==true){
					min = currentJob.getPriority();
					minPosition = j;
					minPR = currentJob.getPR();
				}
				if(Math.abs(min-currentJob.getPriority())<0.00001){
					if(currentJob.getPR() < minPR){
						min = currentJob.getPriority();
						minPosition = j;
						minPR = currentJob.getPR();
					}
				}
			}
			currentMachine.setCurrent(minPosition);
			currentJob = jobs[currentMachine.getJob(minPosition)];
			break;
		}

		case EDD:
		{
			// Calculate due date for each job in queue
			int minPosition = -1;
			double min = MAX;
			double minPR = 0;
			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				currentJob = jobs[currentMachine.getJob(j)];
				currentJob.setPriority(currentJob.getDuedate());
				if((min>currentJob.getPriority())==true){
					min = currentJob.getPriority();
					minPosition = j;
					minPR = currentJob.getPR();
				}
				if(Math.abs(min-currentJob.getPriority())<0.00001){
					if(currentJob.getPR() < minPR){
						min = currentJob.getPriority();
						minPosition = j;
						minPR = currentJob.getPR();
					}
				}
			}
			currentMachine.setCurrent(minPosition);
			currentJob = jobs[currentMachine.getJob(minPosition)];
			break;
		}
		case ATC:
		{
			// Calculate minimum slack for each job in queue
			// MS = duedate - remaining time - readytime of machine
			int maxPosition = -1;
			double max = (-1)*MAX;
			double minPR = 0;
			double p = 0; //average processing time of waiting jobs
			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				p = p + jobs[currentMachine.getJob(j)].getPR();
			}
			p = p/((double)currentMachine.getNumJobQueue());

			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				currentJob = jobs[currentMachine.getJob(j)];
				double p1 = currentJob.getWeight()/currentJob.getPR();
				double p2 = currentJob.getDuedate() - currentTime - currentJob.getPR();
				double p3 = 2.0 * (currentJob.getRT()-currentJob.getPR());
				double p4 = 3.0 * p;
				double result = p1*Math.exp((-1.0)*Math.max(0.0,((p2-p3)/p4)));
				currentJob.setPriority(result);
				if((max<currentJob.getPriority())==true){
					max = currentJob.getPriority();
					maxPosition = j;
					minPR = currentJob.getPR();
				}
				if(Math.abs(max-currentJob.getPriority())<0.00001){
					if(currentJob.getPR() < minPR){
						max = currentJob.getPriority();
						maxPosition = j;
						minPR = currentJob.getPR();
					}
				}
			}
			currentMachine.setCurrent(maxPosition);
			currentJob = jobs[currentMachine.getJob(maxPosition)];
			break;
		}
		case wCOVERT:
		{
			// Calculate minimum slack for each job in queue
			// MS = duedate - remaining time - readytime of machine
			int maxPosition = -1;
			double max = (-1)*MAX;
			double minPR = 0;
			double p = 0; //average processing time of waiting jobs
			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				p = p + jobs[currentMachine.getJob(j)].getPR();
			}
			p = p/((double)currentMachine.getNumJobQueue());

			for(j=0; j < currentMachine.getNumJobQueue(); j++){
				currentJob = jobs[currentMachine.getJob(j)];
				double p1 = currentJob.getWeight()/currentJob.getPR();
				double p2 = Math.max(0.0,(currentJob.getDuedate() - currentTime - currentJob.getRT()));
				double p3 = 2.0 * (2.0*currentJob.getTotalPR());
				double result = p1*Math.max(0.0,(1.0-p2/p3));
				currentJob.setPriority(result);

				if((max<currentJob.getPriority())==true){
					max = currentJob.getPriority();
					maxPosition = j;
					minPR = currentJob.getPR();
				}
				if(Math.abs(max-currentJob.getPriority())<0.00001){
					if(currentJob.getPR() < minPR){
						max = currentJob.getPriority();
						maxPosition = j;
						minPR = currentJob.getPR();
					}
				}
			}
			currentMachine.setCurrent(maxPosition);
			currentJob = jobs[currentMachine.getJob(maxPosition)];
			break;
		}
		default:
			break;
		}
		double ready = currentTime+currentJob.getPR();
		currentJob.setOpComplete(currentJob.getCurrentOp(), ready);
		currentMachine.setReadyTime(ready);

		if(VERB==1) System.out.println("Job "+currentJob.getArrID()+" op starts at Machine "+currentMachine.getMachineID()+" at "+currentTime);
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
	public static void nextMachine()
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
	public static double nextMachineReady()
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



	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO Auto-generated method stub

	}




}