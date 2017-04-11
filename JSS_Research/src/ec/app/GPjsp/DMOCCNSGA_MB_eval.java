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
import java.util.List;
import java.util.Map;

import SmallStatistics.AllStatistics;
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

	public static final int NOT_SET = -1;

	public static final String P_FILE = "input-file";
	public static final String P_INTERMEDIATE_FILE = "intermediate-file";
	public static final String P_APPROACH = "approach";
	public static final String P_SEED = "seed";
	public static final String P_USE_RUN_SEED = "use-run-seed";
	public static final String P_IND_RUN = "ind-run";

	public double meanTime = 1;
	public static String fitness = "";
	public static String objective = "";

	public String inputFile = null;
	public String intermediateFile = null;

	public String approach = null;
	public int seed = NOT_SET;
	public boolean useRunSeed = false;
	public int indRun = NOT_SET;

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
		// Very important, remember this.
		super.setup(state,base);

		// Set up our input -- Don't want to use the default base, it's unsafe here.
		input = (JSPData) state.parameters.getInstanceForParameterEq(base.push(P_DATA), null, JSPData.class);
		input.setup(state,base.push(P_DATA));

		// Setup the mandatory input and intermediate file.
		inputFile = state.parameters.getStringWithDefault(base.push(P_FILE), null, null);
		intermediateFile = state.parameters.getStringWithDefault(base.push(P_INTERMEDIATE_FILE), null, null);

		// Setup subsections of the file that will be read.
		approach = state.parameters.getStringWithDefault(base.push(P_APPROACH), null, null);
		seed = state.parameters.getIntWithDefault(base.push(P_SEED), null, NOT_SET);
		useRunSeed = state.parameters.getBoolean(base.push(P_USE_RUN_SEED), null, false);
		if (seed == NOT_SET && useRunSeed) {
			seed = state.parameters.getIntWithDefault(new Parameter(P_SEED).push("" + 0), null, NOT_SET);
		}
		indRun = state.parameters.getIntWithDefault(base.push(P_IND_RUN), null, NOT_SET);

		if (approach == null) {
			state.output.message("Specific approach not set. Iterating through all approaches.");
		} else {
			state.output.message("Specific approach set: " + approach);
		}

		if (seed == NOT_SET) {
			state.output.message("Specific seed not set. Iterating through all seeds.");
		} else {
			state.output.message("Specific seed set: " + seed);
		}

		if (indRun == NOT_SET) {
			state.output.message("Specific individual run not set. Iterating through all individual runs.");
		} else {
			state.output.message("Specific individual run: " + indRun);
		}
	}

	public void evaluate(final EvolutionState state,
			final Individual[] ind,
			final boolean[] updateFitness,
			final boolean countVictoriesOnly,
			final int[] subpops,
			final int threadnum) {
		// Do nothing on evolution runs.
	}

	public void writeToIntermediateFile(EvolutionState state) {
		try {
			System.out.println("Reading file...");
			LineNumberReader inputFileReader = new LineNumberReader(new FileReader(new File(inputFile)));
			PrintStream intermediateOutput = new PrintStream(new File(intermediateFile));

			String line;
			line = inputFileReader.readLine(); // Read in the header.
			while ((line = inputFileReader.readLine()) != null) {
				// Read in the individuals, and then output it in a format that is readable by ECJ.
				String[] split = line.split(",");

				String a = split[0];
				int s = Integer.parseInt(split[1]);
				int ir = Integer.parseInt(split[2]);

				String dr = split[3];
				String ddar = split[4];

				// Ignore any individuals that are not part of the filter.
				if ((approach == null || approach.equals(a)) &&
						(seed == NOT_SET || seed == s) &&
						(indRun == NOT_SET || indRun == ir)) {
					writeIndividualToFile(intermediateOutput, split[0], split[1], split[2], "0", dr);
					writeIndividualToFile(intermediateOutput, split[0], split[1], split[2], "1", ddar);
				}
			}

			System.out.println("Successfully read file...");
			inputFileReader.close();
			intermediateOutput.close();
		} catch (IOException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	public void writeIndividualToFile(PrintStream output,
			String approach,
			String seed,
			String archiveInd,
			String collabNum,
			String tree) {
		output.printf("%s,%s,%s,%s\n", approach, seed, archiveInd, collabNum);

		output.println("Evaluated: false");
		output.println("Fitness: [d|9999999| d|9999999| d|9999999|]");
		output.println("Rank: i0|");
		output.println("Sparsity: d|Infinity|");

		output.println("Tree 0:");
		output.println(" " + tree);
	}

	// This part doesn't disseminate between the different approaches, so make sure to separate out the approaches beforehand.
	public void readFromIntermediateFile(EvolutionState state) {
		try {
			LineNumberReader interFileReader = new LineNumberReader(new FileReader(new File(intermediateFile)));

			// Okay, this map thing is fucking up on me right now.
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

			state.population.subpops[0].individuals = new Individual[spMap.size()];
			state.population.subpops[1].individuals = new Individual[spMap.size()];

			int index = 0;
			for (Map.Entry<ArchiveRun, SchedulingPolicy> kv : spMap.entrySet()) {
				ArchiveRun ar = kv.getKey();
				SchedulingPolicy sp = kv.getValue();

				Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual) sp.drInd;
				Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual) sp.ddarInd;

				ind1.setApproach(ar.approach);
				ind1.setSeed(ar.seed);
				ind1.setRunNum(ar.runNum);

				ind2.setApproach(ar.approach);
				ind2.setSeed(ar.seed);
				ind2.setRunNum(ar.runNum);

				ind1.context = new Coevolutionary2WayGPIndividual[] {ind1, ind2};
				ind2.context = new Coevolutionary2WayGPIndividual[] {ind1, ind2};

				state.population.subpops[0].individuals[index] = ind1;
				state.population.subpops[1].individuals[index] = ind2;

				index++;
			}

		} catch (IOException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	public String getTrainingPerformance(final EvolutionState state,
			final int threadnum,
			Individual[] ind) {
		if( ind.length != 2 ||
				!(ind[0] instanceof Coevolutionary2WayGPIndividual) ||
				!(ind[1] instanceof Coevolutionary2WayGPIndividual)) {
			state.output.error("There should be two subpopulations, both with Coevolutionary2WayGPIndividual.");
		}
		Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual)(ind[0]);
		Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual)(ind[1]);

		double[] utilisation = {0.75, 0.85};
		double[] breakdownLevel = {0.05};
		double[] meanRepair = {2.3, 3.3};
		int[] numberOfMachines = {4, 6};
		int numDS = 1;
		String[] lowers = {"miss"};
		String[] dists = {"expo"};

		AllStatistics[] result = new AllStatistics[] {
				new AllStatistics(), // max flowtime
				new AllStatistics() // normalised TWT
		};
		AllStatistics resultDD = new AllStatistics(); // MAPE
		AllStatistics resultNF = new AllStatistics(); // negative flowtimes

		StringBuilder detail = new StringBuilder();
		runExperiments(dists, lowers, numberOfMachines, utilisation,
				breakdownLevel, meanRepair, numDS,
				(GPIndividual) ind1, (GPIndividual) ind2, state, threadnum,
				resultDD,
				resultNF,
				result,
				detail,
				false);

		List<Double> maxFlowtimes = result[0].getValues();
		List<Double> normTWTs = result[1].getValues();
		List<Double> mapes = resultDD.getValues();
		List<Double> negativeFlowtimes = resultNF.getValues();

		double[] objectives = new double[]{
				result[0].getAverage(),
				result[1].getAverage(),
				resultDD.getAverage()
		};

		// Set the fitnesses of the individual just for posterity (and so that the NullPointerException doesn't occur).
		MultiObjectiveFitness fitness1 = (MultiObjectiveFitness) ind1.fitness;
		MultiObjectiveFitness fitness2 = (MultiObjectiveFitness) ind2.fitness;

		fitness1.setObjectives(false, objectives);
		fitness2.setObjectives(false, objectives);

		ind1.evaluated = true;
		ind2.evaluated = true;

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mapes.size(); i++) {
			if (i != 0) {
				builder.append("\n");
			}

			double maxF = maxFlowtimes.get(i);
			double twt = normTWTs.get(i);
			double mape = mapes.get(i);
			double nf = negativeFlowtimes.get(i);

			builder.append(String.format("%s,%d,%d,%d,%f,%f,%f,%f",
					ind1.getApproach(),
					ind1.getSeed(),
					ind1.getRunNum(),
					i,
					maxF,
					twt,
					mape,
					nf));
		}

		return builder.toString();
	}

	public String getTestPerformance(final EvolutionState state,
			final int threadnum,
			Individual[] ind) {
		if( ind.length != 2 ||
				!(ind[0] instanceof Coevolutionary2WayGPIndividual) ||
				!(ind[1] instanceof Coevolutionary2WayGPIndividual)) {
			state.output.error("There should be two subpopulations, both with Coevolutionary2WayGPIndividual.");
		}
		Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual)(ind[0]);
		Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual)(ind[1]);

		double[] utilisation = {0.80, 0.9};
		double[] breakdownLevel = {0.05};
		double[] meanRepair = {2.3, 3.3, 4.3};
		int[] numberOfMachines = {5, 10};
		int numDS = 5;
		String[] lowers = {"miss", "full"};
		String[] dists = {"expo", "uniform"};

		AllStatistics[] result = new AllStatistics[] {
				new AllStatistics(), // max flowtime
				new AllStatistics() // normalised TWT
		};
		AllStatistics resultDD = new AllStatistics(); // MAPE
		AllStatistics resultNF = new AllStatistics(); // negative flowtimes

		StringBuilder detail = new StringBuilder();
		runExperiments(dists, lowers, numberOfMachines, utilisation,
				breakdownLevel, meanRepair, numDS,
				(GPIndividual) ind1, (GPIndividual) ind2, state, threadnum,
				resultDD,
				resultNF,
				result,
				detail,
				false);

		List<Double> maxFlowtimes = result[0].getValues();
		List<Double> normTWTs = result[1].getValues();
		List<Double> mapes = resultDD.getValues();
		List<Double> negativeFlowtimes = resultNF.getValues();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mapes.size(); i++) {
			if (i != 0) {
				builder.append("\n");
			}

			double maxF = maxFlowtimes.get(i);
			double twt = normTWTs.get(i);
			double mape = mapes.get(i);
			double nf = negativeFlowtimes.get(i);

			builder.append(String.format("%s,%d,%d,%d,%f,%f,%f,%f",
					ind1.getApproach(),
					ind1.getSeed(),
					ind1.getRunNum(),
					i,
					maxF,
					twt,
					mape,
					nf));
		}

		return builder.toString();
	}

	private class SchedulingPolicy {
		Individual drInd;
		Individual ddarInd;
	}

	private class ArchiveRun {
		String approach;
		int seed;
		int runNum;

		@Override
		public int hashCode() {
			return approach.hashCode() * 3 + seed * 5 + runNum * 7;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || o.getClass() != this.getClass()) {
				return false;
			}
			ArchiveRun other = (ArchiveRun) o;
			return this.approach.equals(other.approach) &&
					this.seed == other.seed &&
					this.runNum == other.runNum;
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
			SmallStatistics resultNF,
			SmallStatistics[] result,
			StringBuilder detail,
			boolean terminateOnInvalidPEF) {
		// TODO temp
		int count = 0;

		outerLoop:
			for (String dist : dists) { for (String s : lowers) { for (int m : numberOfMachines) { for (double u : utilisation) { for (double bl : breakdownLevel) { for (double mr : meanRepair) {
				for (int ds = 0; ds < numDS; ds++) {
//					if (count == 94) {
//						System.out.println("This is the problem instance with the large error value.");
//					}
//					System.out.printf("Problem instance index: %d\n", count);

					int lower = 0;
					String distribution ="";
					double param = -1;
					int numNegativeFlowtimes = 0;

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
					input.abJSP = jspDynamic;

					// Set the dispatching rule.
					Machine.priorityType PT = Machine.priorityType.CONV;
					jspDynamic.setPriorityType(PT);
					jspDynamic.setScheduleStrategy(Machine.scheduleStrategy.NONDELAY);

					//////////////////////////////////////////////
					jspDynamic.setNextArrivalTime();

					// Set the deactivations and activations for the machines.
					for (int i = 0; i < m; i++) {
						jspDynamic.setNextDeactivateTime(i);
						jspDynamic.setNextActivateTime(i);
					}

					while (!jspDynamic.isStop()) {
						int event = jspDynamic.getNextEventType();
						if (event == DynamicJSPFrameworkBreakdown.ARRIVAL_EVENT) {
							Job newjob = jspDynamic.generateRandomJob(jspDynamic.getNextArrivalTime());
							input.partialEstimatedFlowtime = 0;
							input.job = newjob;
							for (int i = 0; i < newjob.getNumberOperations(); i++) {
								input.stat.gatherStatFromJSPModel(jspDynamic, m , newjob, i, input.partialEstimatedFlowtime);

								// Calculate partial estimated flowtime.
								input.tempVal = 0;
								input.k = i;
								((GPIndividual) ind2).trees[0].child.eval(state,
										threadnum,
										input,
										stack,
										(GPIndividual) ind2,
										this);

								if (input.tempVal > 1000000) {
									System.out.printf("large PEF value: %d, %d, %d, %f\n", count, input.job.getID(), i, input.tempVal);
								}

								input.partialEstimatedFlowtime += input.tempVal;
							}

							if (input.partialEstimatedFlowtime < 0.0 ||
									input.partialEstimatedFlowtime == Double.POSITIVE_INFINITY ||
									Double.isNaN(input.partialEstimatedFlowtime)) {
								if (terminateOnInvalidPEF) {
									state.output.warning("Invalid estimated flowtime value: (" + input.partialEstimatedFlowtime + "). Soft exitting the simulation.");
									resultDD = new SmallStatistics();
									break outerLoop;
								}

								numNegativeFlowtimes++;
							}

							newjob.assignDuedate(input.partialEstimatedFlowtime);
							jspDynamic.setNextArrivalTime();
						} else if (event == DynamicJSPFrameworkBreakdown.READY_EVENT) {
							jspDynamic.unplanAll();
							do {
								int nextMachine = jspDynamic.nextMachine();
								if (nextMachine < 0)
									break;
								Machine M = jspDynamic.machines[nextMachine];
								input.machine = M;
								jspDynamic.setInitalPriority(M);

								// Determine priority of jobs in queue.
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
									// Job will start, but may be interrupted by machine deactivation.
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
						state.output.warning("Invalid MAPE value. Soft exitting simulation.");
						break outerLoop;
					}

					resultDD.add(mape);
					resultNF.add(numNegativeFlowtimes);
					result[0].add(jspDynamic.getCmax());
					result[1].add(jspDynamic.getNormalisedTotalWeightedTardiness());
					detail.append(jspDynamic.getCmax() + " " + jspDynamic.getNormalisedTotalWeightedTardiness() + " " + mape + " ");

					// TODO temp
					count++;
				}
			}}}}}}
	}

	public void preprocessPopulation(final EvolutionState state,
			Population pop,
			boolean[] prepareForFitnessAssessment,
			boolean countVictoriesOnly) {
		double[] objectives = {9999999, 9999999, 9999999};

		for (int i = 0; i < pop.subpops.length; i++) {
			for (int j = 0; j < pop.subpops[i].individuals.length; j++) {
				if (!pop.subpops[i].individuals[j].evaluated) {
					((MultiObjectiveFitness) pop.subpops[i].individuals[j].fitness).setObjectives(state, objectives);
				}
			}
		}
	}


	public void postprocessPopulation(final EvolutionState state,
			Population pop,
			boolean[] prepareForFitnessAssessment,
			boolean countVictoriesOnly) {
		System.out.println("x");

		if (state.generation == state.numGenerations-1) {
			MultiObjectiveStatisticsSu myMOStat = (MultiObjectiveStatisticsSu) state.statistics;
			myMOStat.fullEvaluationStatisticsCoevolveNSGA(state, 0, this);
		}
	}

	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
