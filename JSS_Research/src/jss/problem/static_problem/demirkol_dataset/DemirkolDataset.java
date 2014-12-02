package jss.problem.static_problem.demirkol_dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class DemirkolDataset implements IDataset {

	private static final String DEMIRKOL_DATASET = "dataset/DemirkolBenchmarksJobShop/";
	private static final String DEMIRKOL_FILE_SUFFIX = ".txt";
	private static final String DEMIRKOL_BOUNDS_FILE = "dmu_bounds.csv";

	private static final int TRAINING_SMALL_MACHINE_NUM = 15;
	private static final int TRAINING_SMALL_JOB_NUM = 15;
	private static final int TRAINING_MEDIUM_MACHINE_NUM = 20;
	private static final int TRAINING_MEDIUM_JOB_NUM = 30;
	private static final int TRAINING_LARGE_MACHINE_NUM = 20;
	private static final int TRAINING_LARGE_JOB_NUM = 100;

	private Map<String, StaticInstance> problemNameMap = new HashMap<String, StaticInstance>();
	private List<StaticInstance> problemInstances = new ArrayList<StaticInstance>();

	private List<IProblemInstance> smallInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> mediumInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> largeInstances = new ArrayList<IProblemInstance>();

	/**
	 * Construct a new instance of the Demirkol's dataset.
	 */
	public DemirkolDataset() {
		// Read the .txt files.
		readDirectory();

		// Generate the lists of the training sets.
		generateTrainingSets();
		
		// Read in the upper and lower bounds.
		readBounds();
	}

	private void readDirectory() {
		File datasetDir = new File(DEMIRKOL_DATASET);
		
		if (!datasetDir.exists() || !datasetDir.isDirectory()) {
			throw new RuntimeException("Demirkol dataset does not exist at directory \'dataset/DemirkolBenchmarksJobShop\'");
		}
		
		for (File dataFile : datasetDir.listFiles()) {
			if (!dataFile.getName().endsWith(DEMIRKOL_FILE_SUFFIX)) {
				continue;
			}
			
			try {
				FileReader fileReader = new FileReader(dataFile);
				BufferedReader reader = new BufferedReader(fileReader);
				
				String line = reader.readLine();
				int numJobs = Integer.parseInt(line.split("\\s+")[0]);
				int numMachines = Integer.parseInt(line.split("\\s+")[1]);
				
				StaticInstance problemInstance = new StaticInstance();
				
				List<StaticMachine> machines = new ArrayList<StaticMachine>();
				
				for (int machine = 0; machine < numMachines; machine++) {
					machines.add(new StaticMachine(problemInstance));
				}
				
				for (int j = 0; j < numJobs; j++) {
					line = reader.readLine();
					
					String[] split = line.trim().split("\\s+");
					
					StaticJob job = new StaticJob();
					
					for (int i = 0; i < numMachines; i++) {
						int machine = Integer.parseInt(split[2 * i]);
						double processingTime = Double.parseDouble(split[2 * i + 1]);
						
						job.offerMachine(machines.get(machine));
						job.setProcessingTime(machines.get(machine), processingTime);
					}
					
					problemInstance.addJob(job);
				}
				
				problemInstances.add(problemInstance);
				
				String problemInstanceName = dataFile.getName().replace(DEMIRKOL_FILE_SUFFIX, "");
				problemNameMap.put(problemInstanceName, problemInstance);
				
				reader.close();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	private void readBounds() {
		try {
			File boundsFile = new File(DEMIRKOL_DATASET + DEMIRKOL_BOUNDS_FILE);
			FileReader fileReader = new FileReader(boundsFile);
			BufferedReader reader = new BufferedReader(fileReader);
			
			String line = reader.readLine(); // Skip the first line (header).
			while ((line = reader.readLine()) != null) {
				String[] split = line.split(",");
				
				String problemInstanceName = split[0];
				double lowerBound = Double.parseDouble(split[3]);
				double upperBound = Double.parseDouble(split[4]);
				
				StaticInstance problemInstance = problemNameMap.get(problemInstanceName);
				problemInstance.setLowerBound(lowerBound);
				problemInstance.setUpperBound(upperBound);
			}
			
			reader.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private void generateTrainingSets() {
		for (IProblemInstance problem : problemInstances) {
			int machineSize = problem.getMachines().size();
			int jobSize = problem.getJobs().size();

			if (machineSize == TRAINING_SMALL_MACHINE_NUM && jobSize == TRAINING_SMALL_JOB_NUM) {
				smallInstances.add(problem);
			} else if (machineSize == TRAINING_MEDIUM_MACHINE_NUM && jobSize == TRAINING_MEDIUM_JOB_NUM) {
				mediumInstances.add(problem);
			} else if (machineSize == TRAINING_LARGE_MACHINE_NUM && jobSize == TRAINING_LARGE_JOB_NUM) {
				largeInstances.add(problem);
			}
		}

		int smallSize = smallInstances.size() / 2;
		for (int i = 0; i < smallSize; i++) {
			smallInstances.remove(smallInstances.size() - 1);
		}

		int mediumSize = mediumInstances.size() / 2;
		for (int i = 0; i < mediumSize; i++) {
			mediumInstances.remove(mediumInstances.size() - 1);
		}

		int largeSize = largeInstances.size() / 2;
		for (int i = 0; i < largeSize; i++) {
			largeInstances.remove(largeInstances.size() - 1);
		}
	}

	@Override
	public List<IProblemInstance> getProblems() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

	@Override
	public List<IProblemInstance> getTraining(ProblemSize problemSize) {
		switch (problemSize) {
		case SMALL_PROBLEM_SIZE: return smallInstances;
		case MEDIUM_PROBLEM_SIZE: return mediumInstances;
		case LARGE_PROBLEM_SIZE: return largeInstances;
		default: throw new RuntimeException("You done goofed");
		}
	}

	@Override
	public List<IProblemInstance> getTest() {
		return new ArrayList<IProblemInstance>(problemInstances);
	}

}
