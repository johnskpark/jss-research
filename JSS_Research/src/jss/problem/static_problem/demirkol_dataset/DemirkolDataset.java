package jss.problem.static_problem.demirkol_dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jss.IDataset;
import jss.IProblemInstance;
import jss.ProblemSize;
import jss.problem.static_problem.StaticInstance;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class DemirkolDataset implements IDataset {

	private static final String DEMIRKOL_DATASET = "DemirkolBenchmarksJobShop";
	private static final String DEMIRKOL_FILE_SUFFIX = ".txt";

	private static final int TRAINING_SMALL_MACHINE_NUM = 15;
	private static final int TRAINING_SMALL_JOB_NUM = 15;
	private static final int TRAINING_MEDIUM_MACHINE_NUM = 20;
	private static final int TRAINING_MEDIUM_JOB_NUM = 30;
	private static final int TRAINING_LARGE_MACHINE_NUM = 20;
	private static final int TRAINING_LARGE_JOB_NUM = 100;

	private List<StaticInstance> problemInstances = new ArrayList<StaticInstance>();

	private List<IProblemInstance> smallInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> mediumInstances = new ArrayList<IProblemInstance>();
	private List<IProblemInstance> largeInstances = new ArrayList<IProblemInstance>();

	/**
	 * Construct a new instance of the Demirkol's dataset.
	 */
	public DemirkolDataset() {
		// Read the .txt files.
		readFile();

		// Generate the lists of the training sets.
		generateTrainingSets();
	}

	private void readFile() {
		List<InputStream> inputStreams = (isRunningFromJar()) ? getStreamsFromJar() : getStreamsFromDir();
		
		readProblemInstances(inputStreams);
	}

	private boolean isRunningFromJar() {
		URL datasetURL = DemirkolDataset.class.getResource(DEMIRKOL_DATASET);

		return datasetURL.toString().startsWith("jar:");
	}

	// TODO fix up the exception handling sometime later.
	private List<InputStream> getStreamsFromDir() {
		try {
			List<InputStream> inputStreams = new ArrayList<InputStream>();
	
			File dir = new File(DemirkolDataset.class.getResource(DEMIRKOL_DATASET).toURI());
			for (File file : dir.listFiles()) {
				if (file.getName().endsWith(DEMIRKOL_FILE_SUFFIX)) {
					inputStreams.add(new FileInputStream(file));
				}
			}
			
			return inputStreams;
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	// The fark is this on about? Have a run of this at the university tomorrow to see what 
	// the heck a jar entry does.
	private List<InputStream> getStreamsFromJar() {
		// TODO
		
		JarFile jarFile = null;
		try {
			String path = new File(DemirkolDataset.class.getResource(DEMIRKOL_DATASET).getPath()).getParent().replaceAll("(!|file:\\\\)", "");
			System.out.println(path);

			jarFile = new JarFile(path);

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry je = entries.nextElement();
				if (je.getName().startsWith(".txt")) {
					System.out.println(je.getName());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				jarFile.close();
			} catch (Exception e) {
			}
		}
		
		return null;
	}
	
	private void readProblemInstances(List<InputStream> inputStreams) {
		// I don't like this idea of having multiple file streams open. Maybe try to find another 
		// way somehow.
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

	// TODO temp code.
	public static void main(String[] args) {
		DemirkolDataset dataset = new DemirkolDataset();
	}

}
