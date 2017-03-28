/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */


package ec.app.GPjsp;
import SmallStatistics.SmallStatistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatisticsSu;
import ec.util.Parameter;
import jsp.DynamicJSPFrameworkBreakdown;
import jsp.Job;
import jsp.Machine;

public class DMOCCNSGA_MB_test extends GPjsp2WayMOCoevolveNSGA {

	private static final long serialVersionUID = -7145910495558748714L;

	public double meanTime = 1;
	public static String fitness = "";
	public static String objective = "";

	public JSPData input;

	public static final int[] SIM_JOB_SEED = {2734, 72734, 72605, 12628, 20029, 1991,
			55013, 84005, 54972, 80531, 45414, 25675,
			79032, 14882, 17423,  2798, 77874,  3805,
			21671, 51204, 85187, 76476, 12363, 92832,
			36503, 25237, 26178, 13614, 50288, 26279};

	public static final int[] SIM_BREAKDOWN_SEED = {2734, 72734, 72605, 12628, 20029, 1991,
			55013, 84005, 54972, 80531, 45414, 25675,
			79032, 14882, 17423,  2798, 77874,  3805,
			21671, 51204, 85187, 76476, 12363, 92832,
			36503, 25237, 26178, 13614, 50288, 26279};


	public Object clone() {
		DMOCCNSGA_MB_test newobj = (DMOCCNSGA_MB_test) (super.clone());
		newobj.input = (JSPData)(input.clone());
		return newobj;
	}

	public void setup(final EvolutionState state, final Parameter base) {
		// very important, remember this
		super.setup(state,base);

		// set up our input -- don't want to use the default base, it's unsafe here
		input = (JSPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSPData.class);
		input.setup(state,base.push(P_DATA));
	}

	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		if ((updateFitness[0] && !ind[0].evaluated) ||
				(updateFitness[1] && !ind[1].evaluated)) {
			if (ind.length != 2 ||
					!(ind[0] instanceof Coevolutionary2WayGPIndividual) ||
					!(ind[1] instanceof Coevolutionary2WayGPIndividual)) {
				state.output.error( "There should be two subpopulations, both with CoevolutionaryDoubleVectorIndividual." );
			}

			Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual) ind[0];
			Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual) ind[1];

			double[] utilisation = {0.8, 0.9};
			double[] breakdownLevel = {0.0};
			double[] meanRepair = {1};
			int[] numberOfMachines = {4, 6};
			String[] lowers = {"miss"};
			String[] dists = {"expo"};

			double[] objectives = new double[3];

			SmallStatistics[] result = new SmallStatistics[] {
					new SmallStatistics(), new SmallStatistics()
			};
			SmallStatistics resultDD = new SmallStatistics();

			StringBuilder detail = new StringBuilder();
			runExperiments(dists, lowers, numberOfMachines, utilisation,
					breakdownLevel, meanRepair, 1,
					(GPIndividual) ind1, (GPIndividual) ind2, state, threadnum,
					resultDD,
					result,
					detail);

			objectives[0] = result[0].getAverage();
			objectives[1] = result[1].getAverage();
			objectives[2] = resultDD.getAverage();

			for (int i = 0; i < 3; i++) {
				if (objectives[i]  < 0.0f || objectives[i] == Float.POSITIVE_INFINITY || Double.isNaN(objectives[i])) {
					objectives[i] = Float.POSITIVE_INFINITY;
				}
				if (i == 2 && objectives[2] > 3) {
					for (int j = 0; j < 3; j++) objectives[j]=1000000;
					break;
				}
			}

			//((MultiObjectiveFitness)ind[1].fitness).setObjectives(state, objectives);
			//ind[1].evaluated = true;
			//coevolve fitness update
			//double functionValue = objectives[0];

			MultiObjectiveFitness moFitness = new MultiObjectiveFitness();
			moFitness.setObjectives(false, objectives);
			if( updateFitness[0] )
			{
				if( moFitness.paretoDominates(((MultiObjectiveFitness)ind1.fitness)) )
				{
					((MultiObjectiveFitness)ind1.fitness).setObjectives(state, objectives);
					ind1.context = new Coevolutionary2WayGPIndividual[2];
					ind1.context[1] = ind2;
				}
			}
			if( updateFitness[1] )
			{
				if( moFitness.paretoDominates(((MultiObjectiveFitness)ind2.fitness)) )
				{
					((MultiObjectiveFitness)ind2.fitness).setObjectives(state, objectives);
					ind2.context = new Coevolutionary2WayGPIndividual[2];
					ind2.context[0] = ind1;
				}
			}
			//ind1.printTrees(state, threadnum);
			//System.out.println("|" + objectives[0] + " " + objectives[1] + " " + objectives[2]);
			System.out.print("|");// + ind1.trees[0].child.numNodes(GPNode.NODESEARCH_ALL) + "**" + ind2.trees[0].child.numNodes(GPNode.NODESEARCH_ALL));
		}
	}

	public String getTestPerformance(final EvolutionState state,
			final int threadnum,
			Individual[] ind) {
		if( ind.length != 2 ||
				( ! ( ind[0] instanceof Coevolutionary2WayGPIndividual ) ) ||
				( ! ( ind[1] instanceof Coevolutionary2WayGPIndividual ) ) )
		{
			state.output.error( "There should be two subpopulations, both with CoevolutionaryDoubleVectorIndividual." );
		}
		Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual)(ind[0]);
		Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual)(ind[1]);

		double[] utilisation = {0.65, 0.75, 0.85, 0.9};
		double[] breakdownLevel = {0.05};
		double[] meanRepair = {2.3, 3.3, 4.3};
		int[] numberOfMachines = {5, 10, 20};
		String[] lowers = {"miss", "full"};
		String[] dists = {"expo", "uniform"};

		SmallStatistics[] result = new SmallStatistics[] {
				new SmallStatistics(),
				new SmallStatistics()
		};
		SmallStatistics resultDD = new SmallStatistics();

		StringBuilder detail = new StringBuilder();
		runExperiments(dists, lowers, numberOfMachines, utilisation,
				breakdownLevel, meanRepair, 5,
				(GPIndividual) ind1, (GPIndividual) ind2, state, threadnum,
				resultDD,
				result,
				detail);

		//return detail + "\n" + result[0].getAverage() + " " + result[1].getAverage() + " " + resultDD.getAverage();
		return result[0].getAverage() + " " + result[1].getAverage() + " " + resultDD.getAverage();
	}

	private void runExperiments(String[] dists,
			String[] lowers,
			int[] numberOfMachines,
			double[] utilisation,
			double[] breakdownLevel,
			double[] meanRepair,
			int numDS,
			GPIndividual ind1,
			GPIndividual ind2,
			EvolutionState state,
			int threadnum,
			SmallStatistics resultDD,
			SmallStatistics[] result,
			StringBuilder detail) {
		outerLoop:
			for (String dist : dists) { for (String s : lowers) { for (int m : numberOfMachines) { for (double u : utilisation) { for (double bl : breakdownLevel) { for (double mr : meanRepair) {
				for (int ds = 0; ds < numDS; ds++) {
					int lower = 0;
					String distribution ="";
					double param = -1;

					if ("miss".equals(s)) {
						lower = 1;
					} else {
						lower = m;
					}

					if ("expo".equals(dist)) {
						distribution = "erlang";
						param = 1;
					} else if ("erlang2".equals(dist)) {
						distribution = "erlang";
						param = 2;
					} else if ("uniform".equals(dist)) {
						distribution = "uniform";
						param = 0.5;
					}

					DynamicJSPFrameworkBreakdown jspDynamic = new DynamicJSPFrameworkBreakdown(SIM_JOB_SEED[ds], m, lower,
							m, u, u, meanTime, distribution, param, 1000, 5000, SIM_BREAKDOWN_SEED[ds], bl, mr);
					input.abjsp = jspDynamic;

					//set dispatching rule
					Machine.priorityType PT = Machine.priorityType.CONV;
					jspDynamic.setPriorityType(PT);
					jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);

					//////////////////////////////////////////////
					jspDynamic.setNextArrivalTime();
					//set deactivations and activations for the machines
					for (int i = 0; i < m; i++) {
						jspDynamic.setNextDeactivateTime(i);
						jspDynamic.setNextActivateTime(i);
					}

					while (!jspDynamic.isStop()) {
						int event = jspDynamic.getNextEventType();
						if (event == DynamicJSPFrameworkBreakdown.ARRIVAL_EVENT) {
							//JOB newjob = jspDynamic.GenerateNonRecirculatedJob(jspDynamic.getNextArrivalTime());
							///*
							Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
							input.partialEstimatedFlowtime = 0;
							input.J = newjob;
							for (int i = 0; i < newjob.getNumberOperations(); i++) {
								input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob, i, input.partialEstimatedFlowtime);
								//calculcate parital flowtime
								input.tempVal = 0;
								input.k = i;
								((GPIndividual) ind2).trees[0].child.eval(state,
										threadnum,
										input,
										stack,
										(GPIndividual) ind2,
										this);
								input.partialEstimatedFlowtime += input.tempVal;
							}

							if (input.partialEstimatedFlowtime < 0.0 ||
									input.partialEstimatedFlowtime == Double.POSITIVE_INFINITY ||
									Double.isNaN(input.partialEstimatedFlowtime)) {
								resultDD = new SmallStatistics();
								break outerLoop;
							}

							newjob.assignDuedate(input.partialEstimatedFlowtime);
							jspDynamic.setNextArrivalTime();
							//*/
							//newjob.assignDuedate(1.3*newjob.getTotalProcessingTime());
							//jspDynamic.setNextArrivalTime();
						} else if (event == DynamicJSPFrameworkBreakdown.READY_EVENT) {
							jspDynamic.unplanAll();
							do {
								int nextMachine = jspDynamic.nextMachine();
								if (nextMachine < 0)
									break;
								Machine M = jspDynamic.machines[nextMachine];
								input.M = M;
								jspDynamic.setInitalPriority(M);
								// determine priority of jobs in queue
								if (M.getQueue().size() > 1) {
									((GPIndividual) ind1).trees[0].child.eval(
											state,
											threadnum,
											input,
											stack,
											(GPIndividual) ind1,
											this);
									for (Job J : M.getQueue()) {
										J.addPriority(J.tempPriority);
									}
									jspDynamic.sortJobInQueue(M);
								}

								if (M.getPlannedStartTimeNextOperation() <= jspDynamic.getNextArrivalTime() &&
										M.getPlannedStartTimeNextOperation() < M.getDeactivationTime()) {
									// job will start, but may be interrupted by machine deactivation

									Job J = M.completeJob();
									if (!J.isCompleted()) {
										jspDynamic.machines[J.getCurrentMachine()].joinQueue(J);
									} else {
										jspDynamic.removeJobFromSystem(J);
									}
								} else {
									M.plan();
								}
							} while(true);
						} else if (event == DynamicJSPFrameworkBreakdown.DEACTIVATE_EVENT) {
							int mIndex = jspDynamic.getNextEarliestDeactivateMachine();

							jspDynamic.repairMachine(mIndex);
							jspDynamic.setNextDeactivateTime(mIndex);
							jspDynamic.setNextActivateTime(mIndex);
						} else {
							throw new RuntimeException("Unrecognised type of event: " + event);
						}
					}

					double mape = jspDynamic.getMAPE();
					if (mape < 0.0 || mape == Double.POSITIVE_INFINITY || Double.isNaN(mape)) {
						break outerLoop;
					}

					resultDD.add(mape);
					result[0].add(jspDynamic.getCmax());
					result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
					detail.append(jspDynamic.getCmax() + " " + jspDynamic.getNormalisedTotalWeightedTardiness() + " " + mape + " ");
				}
			}}}}}}
	}

	public void preprocessPopulation(final EvolutionState state, Population pop, boolean[] prepareForFitnessAssessment, boolean countVictoriesOnly) {
		double[] objectives = {9999999, 9999999, 9999999};
		for (int i = 0; i < pop.subpops.length; i++) {
			for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
				if (!pop.subpops[i].individuals[j].evaluated) {
					((MultiObjectiveFitness)pop.subpops[i].individuals[j].fitness).setObjectives(state, objectives);
				}
			}
		}
	}

	public void postprocessPopulation(final EvolutionState state, Population pop, boolean[] prepareForFitnessAssessment, boolean countVictoriesOnly) {
		System.out.println("x");
		for (int i = 0; i < pop.subpops.length; i++) {
			for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
				pop.subpops[i].individuals[j].evaluated = true;
			}
		}

		if (state.generation == state.numGenerations-1) {
			MultiObjectiveStatisticsSu myMOStat = (MultiObjectiveStatisticsSu) state.statistics;
			myMOStat.myFinalStatisticCoevolveNSGA(state, 0, this);
			//FinalStatisticMO2Way.myFinalStatistic(state, threadnum,this,threadnum, 2, 105);
		}
	}

	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

/*
            double[] utilisation = {0.6,0.7,0.8,0.9,0.95};
            int[] numbeOfMachines = {4,5,6,10,20};
            String[] lowers = {"miss","full"};
            String[] dists = {"expo","erlang2","uniform"};
 */