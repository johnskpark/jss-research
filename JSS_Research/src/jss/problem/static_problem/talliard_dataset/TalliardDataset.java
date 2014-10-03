package jss.problem.static_problem.talliard_dataset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jss.IProblemInstance;
import jss.problem.static_problem.StaticDataset;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;
import jss.util.TalliardRandom;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class TalliardDataset extends StaticDataset {

	private static final String TALLIARD_DATASET = "jss_talliard.csv";

	private static int MIN_PROCESSING_TIME = 1;
	private static int MAX_PROCESSING_TIME = 99;

	private List<RawInstance> rawInstances = new ArrayList<RawInstance>();
	private List<StaticInstance> problemInstances = new ArrayList<StaticInstance>();

	/**
	 * TODO javadoc.
	 */
	public TalliardDataset() {
		// Read the .csv file.
		readFile();

		// Convert from raw .csv file.
		generateDataset();
	}

	private void readFile() {
		InputStream inputStream = TalliardDataset.class.getResourceAsStream(TALLIARD_DATASET);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inputStreamReader);

		try {
			String line = reader.readLine(); // Skip the first line (header).
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				System.out.println(line);
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
		TalliardRandom timeRand = new TalliardRandom(rawInstance.timeSeed);
		TalliardRandom machineRand = new TalliardRandom(rawInstance.machineSeed);

		StaticInstance instance = new StaticInstance();

		StaticMachine[] machines = new StaticMachine[rawInstance.numMachines];
		for (int i = 0; i < rawInstance.numMachines; i++) {
			machines[i] = new StaticMachine(instance);
		}

		StaticJob[] jobs = new StaticJob[rawInstance.numJobs];
		for (int j = 0; j < rawInstance.numJobs; j++) {
			jobs[j] = new StaticJob();
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
		}

		// Set the processing time of the jobs.
		for (int j = 0; j < rawInstance.numJobs; j++) {
			for (int i = 0; i < rawInstance.numMachines; i++) {
				int processingTime = uniformDistribution(MIN_PROCESSING_TIME, MAX_PROCESSING_TIME, timeRand);
				jobs[j].setProcessingTime(machines[i], processingTime);
			}
		}

		return instance;
	}

	private int uniformDistribution(int min, int max, TalliardRandom rand) {
		return (int) Math.floor(min + rand.nextDouble() * (max - min + 1));
	}

	@Override
	public List<IProblemInstance> getProblems() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<Double> getUpperBounds() {
		List<Double> upperBounds = new ArrayList<Double>();
		for (RawInstance raw : rawInstances) {
			upperBounds.add(raw.upperBound);
		}
		return upperBounds;
	}

	/**
	 * TODO javadoc.
	 * @return
	 */
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
}
