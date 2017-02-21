/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */


package ec.app.GPjsp;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import SmallStatistics.SmallStatistics;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Species;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatisticsSu;
import ec.util.Parameter;
import jsp.DynamicJSPFrameworkBreakdown;
import jsp.Job;
import jsp.Machine;

public class DMOCCNSGA_MB_eval extends GPjsp2WayMOCoevolveNSGA {

	private static final long serialVersionUID = -7145910495558748714L;

	public static final String P_FILE = "input-file";
	public static final String P_INTERMEDIATE_FILE = "intermediate-file";

	public double meanTime = 1;
	public static String fitness = "";
	public static String objective = "";

	public String inputFile = null;
	public String intermediateFile = null;

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
		DMOCCNSGA_MB newobj = (DMOCCNSGA_MB) (super.clone());
		newobj.input = (JSPData)(input.clone());
		return newobj;
	}

	public void setup(final EvolutionState state, final Parameter base) {
		// very important, remember this
		super.setup(state,base);

		// set up our input -- don't want to use the default base, it's unsafe here
		input = (JSPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSPData.class);
		input.setup(state,base.push(P_DATA));

		inputFile = state.parameters.getStringWithDefault(base.push(P_FILE), null, null);
		intermediateFile = state.parameters.getStringWithDefault(base.push(P_INTERMEDIATE_FILE), null, null);
	}

	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// TODO prematurely end the program here.


	}

	private void writeToIntermediateFile(final EvolutionState state) {
		try {
			System.out.println("Reading file...");
			LineNumberReader inputFileReader = new LineNumberReader(new FileReader(new File(inputFile)));
			PrintStream intermediateOutput = new PrintStream(new File(intermediateFile));

			String line;
			line = inputFileReader.readLine(); // Read in the header.
			while ((line = inputFileReader.readLine()) != null) {
				// Read in the individuals, and then output it in a format that is readable by ECJ.
				String[] split = line.split(",");

				String dr = split[3];
				String ddar = split[4];

				writeIndividualToFile(intermediateOutput, split[0], split[1], split[2], "0", dr);
				writeIndividualToFile(intermediateOutput, split[0], split[1], split[2], "1", ddar);
			}

			System.out.println("Successfully read file...");
			inputFileReader.close();
			intermediateOutput.close();
		} catch (IOException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	// TODO these need to go into the multiobjective side of things.
	private void writeIndividualToFile(PrintStream output,
			String approach,
			String seed,
			String archiveInd,
			String collabNum,
			String tree) {
		output.printf("%s,%s,%s,%s\n", approach, seed, archiveInd, collabNum);

		output.println("Evaluated: false");
		output.println("Fitness: [d|9999999| d|9999999| d|9999999|]");
		output.println("Rank: i0|");
		output.println("Spartiy: d|Infinity|");

		output.println("Tree 0:");
		output.println(" " + tree);
	}

	// TODO need to figure out how to disseminate between the different approaches, the different runs, etc.
	private void readFromIntermediateFile(final EvolutionState state) {
		try {
			LineNumberReader interFileReader = new LineNumberReader(new FileReader(new File(intermediateFile)));

			Map<ArchiveRun, SchedulingPolicy> spMap = new HashMap<ArchiveRun, SchedulingPolicy>();

			String headerLine;
			while ((headerLine = interFileReader.readLine()) != null) {
				String[] indInfo = headerLine.split(",");

				ArchiveRun ar = new ArchiveRun() {{
					approach = indInfo[0];
					seed = Integer.parseInt(indInfo[1]);
					runNum = Integer.parseInt(indInfo[2]);
				}};

				int subpop = Integer.parseInt(indInfo[3]);
				Species species = state.population.subpops[subpop].species;
				Individual ruleInd = species.newIndividual(state, interFileReader);

				if (!spMap.containsKey(ar)) {
					spMap.put(ar, new SchedulingPolicy());
				}

				SchedulingPolicy sp = spMap.get(ar);
				if (subpop == 0) {
					sp.drInd = ruleInd;
				} else if (subpop == 1) {
					sp.ddarInd = ruleInd;
				} else {
					state.output.fatal("There should only be two subpopulations in the DMOCC approach, instead got subpopulation " + subpop);
				}
			}

			// TODO write to the evolutionstate.
		} catch (IOException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	// TODO need to make this homogeneous for all individuals, but this is going to be a bit tricky.
	public String getTestPerformance(final EvolutionState state,
			final int threadnum,
			Individual[] ind) {
		writeToIntermediateFile(state);
		readFromIntermediateFile(state);

//			System.out.println("Getting individual 1...");
//			Individual IND = state.population.subpops[0].individuals[0];
//
//			System.out.println("Printing original individual to log...");
//			IND.printIndividual(state, ((SimpleStatistics) state.statistics).statisticslog);
//
//			System.out.println("Reading individual from file...");
//			IND.readIndividual(state, inputFileReader);
//
//			System.out.println("Printing read individual to log...");
//			IND.printIndividual(state, ((SimpleStatistics) state.statistics).statisticslog);
//
//
//			System.out.println("Getting individual 2...");
//			Individual IND2 = state.population.subpops[0].individuals[1];
//
//			System.out.println("Printing original individual to log...");
//			IND2.printIndividual(state, ((SimpleStatistics) state.statistics).statisticslog);
//
//			System.out.println("Reading individual from file...");
//			IND2.readIndividual(state, inputFileReader);
//
//			System.out.println("Printing read individual to log...");
//			IND2.printIndividual(state, ((SimpleStatistics) state.statistics).statisticslog);
		return null;
	}

	private class SchedulingPolicy {
		Individual drInd;
		Individual ddarInd;
	}

	private class ArchiveRun {
		String approach;
		int seed;
		int runNum;

		public int hashCode() {
			return approach.hashCode() * 3 + seed * 5 + runNum * 7;
		}
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
			myMOStat.fullEvaluationStatisticsCoevolveNSGA(state, 0, this);
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