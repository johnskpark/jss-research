package jss.problem.static_problem.taillard_dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticDataset;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;
import jss.util.TaillardRandom;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TaillardDataset extends StaticDataset {

	private static final String TALLIARD_DATASET = "jss_taillard.csv";

	private static final int TRAINING_SMALL_MACHINE_NUM = 15;
	private static final int TRAINING_SMALL_JOB_NUM = 15;
	private static final int TRAINING_MEDIUM_MACHINE_NUM = 30;
	private static final int TRAINING_MEDIUM_JOB_NUM = 20;
	private static final int TRAINING_LARGE_MACHINE_NUM = 100;
	private static final int TRAINING_LARGE_JOB_NUM = 20;

	private static final Map<ProblemSize, MachineJobPair> PROBLEM_SIZE_MAP = new HashMap<ProblemSize, MachineJobPair>();

	private static final int MIN_PROCESSING_TIME = 1;
	private static final int MAX_PROCESSING_TIME = 99;

	private List<RawInstance> rawInstances = new ArrayList<RawInstance>();
	private List<StaticInstance> problemInstances = new ArrayList<StaticInstance>();

	/**
	 * TODO javadoc.
	 */
	public TaillardDataset() {
		// Read the .csv file.
		readFile();

		// Convert from raw .csv file.
		generateDataset();

		// TODO docs.
		todoNameHere();
	}

	private void readFile() {
		InputStream inputStream = TaillardDataset.class.getResourceAsStream(TALLIARD_DATASET);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inputStreamReader);

		try {
			String line = reader.readLine(); // Skip the first line (header).
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				rawInstances.add(readLine(line));
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private RawInstance readLine(String line) {
		Scanner sc = new Scanner(line);
		sc.useDelimiter(",");

		RawInstance rawData = new RawInstance();
		rawData.numJobs = sc.nextInt();
		rawData.numMachines = sc.nextInt();
		rawData.timeSeed = sc.nextLong();
		rawData.machineSeed = sc.nextLong();
		rawData.upperBound = sc.nextDouble();
		rawData.lowerBound = sc.nextDouble();

		sc.close();
		return rawData;
	}

	private void generateDataset() {
		for (RawInstance rawInstance : rawInstances) {
			problemInstances.add(rawToStatic(rawInstance));
		}
	}

	// Look at the paper "Benchmarks for basic scheduling problems" for
	// the pseudocode details.
	private StaticInstance rawToStatic(RawInstance rawInstance) {
		TaillardRandom timeRand = new TaillardRandom(rawInstance.timeSeed);
		TaillardRandom machineRand = new TaillardRandom(rawInstance.machineSeed);

		StaticInstance instance = new StaticInstance();

		StaticMachine[] machines = new StaticMachine[rawInstance.numMachines];
		for (int i = 0; i < rawInstance.numMachines; i++) {
			machines[i] = new StaticMachine(instance);
			instance.addMachine(machines[i]);
		}

		StaticJob[] jobs = new StaticJob[rawInstance.numJobs];
		for (int j = 0; j < rawInstance.numJobs; j++) {
			jobs[j] = new StaticJob();
			instance.addJob(jobs[j]);
		}

		// Set the machine processing order of jobs.
		for (int j = 0; j < rawInstance.numJobs; j++) {

			// Initialise the initial processing order.
			int[] processingOrder = new int[rawInstance.numMachines];
			for (int i = 0; i < rawInstance.numMachines; i++) {
				processingOrder[i] = i;
			}

			// Apply random swaps to the processing order.
			for (int i = 0; i < rawInstance.numMachines; i++) {
				int newIndex = uniformDistribution(i, rawInstance.numMachines - 1, machineRand);
				int temp = processingOrder[i];
				processingOrder[i] = processingOrder[newIndex];
				processingOrder[newIndex] = temp;
			}

			// Offer the machines up to the job.
			for (int i = 0; i < rawInstance.numMachines; i++) {
				jobs[j].offerMachine(machines[processingOrder[i]]);
			}

			// Set the processing time for each machine on the job.
			for (int i = 0; i < rawInstance.numMachines; i++) {
				int processingTime = uniformDistribution(MIN_PROCESSING_TIME, MAX_PROCESSING_TIME, timeRand);
				jobs[j].setProcessingTime(machines[processingOrder[i]], processingTime);
			}
		}

		instance.setUpperBound(rawInstance.upperBound);
		instance.setLowerBound(rawInstance.lowerBound);

		return instance;
	}

	private void todoNameHere() {
		MachineJobPair smallPair = new MachineJobPair();
		smallPair.numMachines = TRAINING_SMALL_MACHINE_NUM;
		smallPair.numJobs = TRAINING_SMALL_JOB_NUM;
		PROBLEM_SIZE_MAP.put(ProblemSize.SMALL_PROBLEM_SIZE, smallPair);

		MachineJobPair mediumPair = new MachineJobPair();
		smallPair.numMachines = TRAINING_MEDIUM_MACHINE_NUM;
		smallPair.numJobs = TRAINING_MEDIUM_JOB_NUM;
		PROBLEM_SIZE_MAP.put(ProblemSize.MEDIUM_PROBLEM_SIZE, mediumPair);

		MachineJobPair largePair = new MachineJobPair();
		smallPair.numMachines = TRAINING_LARGE_MACHINE_NUM;
		smallPair.numJobs = TRAINING_LARGE_JOB_NUM;
		PROBLEM_SIZE_MAP.put(ProblemSize.LARGE_PROBLEM_SIZE, largePair);
	}

	private int uniformDistribution(int min, int max, TaillardRandom rand) {
		return (int) Math.floor(min + rand.nextDouble() * (max - min + 1));
	}

	@Override
	public List<IProblemInstance> getProblems() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		List<IProblemInstance> training = new ArrayList<IProblemInstance>();
		for (IProblemInstance problem : problemInstances) {
			int machineSize = problem.getMachines().size();
			int jobSize = problem.getJobs().size();

			// TODO placeholder. Need a way to parameterise this later.
			MachineJobPair pair = PROBLEM_SIZE_MAP.get(problemSize);
			if (machineSize == pair.numMachines && jobSize == pair.numJobs) {
				training.add(problem);
			}
		}

		// Remove half of the problem instances
		for (int i = 0; i < training.size() / 2; i++) {
			training.remove(training.size() - 1);
		}

		return training;
	}

	@Override
	public List<IProblemInstance> getTest() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	@Override
	public List<Double> getUpperBounds() {
		List<Double> upperBounds = new ArrayList<Double>();
		for (RawInstance raw : rawInstances) {
			upperBounds.add(raw.upperBound);
		}
		return upperBounds;
	}

	@Override
	public List<Double> getLowerBounds() {
		List<Double> lowerBounds = new ArrayList<Double>();
		for (RawInstance raw : rawInstances) {
			lowerBounds.add(raw.lowerBound);
		}
		return lowerBounds;
	}

	private class RawInstance {
		int numJobs;
		int numMachines;
		long timeSeed;
		long machineSeed;
		double upperBound;
		double lowerBound;
	}

	private class MachineJobPair {
		int numMachines;
		int numJobs;
	}
}
