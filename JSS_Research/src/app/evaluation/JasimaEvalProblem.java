package app.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import app.IWorkStationListener;
import app.node.INode;
import app.node.NodeData;
import app.priorityRules.TrackedPR;
import app.simConfig.ExperimentGenerator;
import app.simConfig.SimConfig;
import app.util.RuleParser;
import jasima.core.experiment.Experiment;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public class JasimaEvalProblem {

	public static final String EVALUATION_XSD = "jss_evaluation.xsd";

	public static final String XML_SOLVER_BASE = "solverConfig";
	public static final String XML_SOLVER_CLASS = "solverClass";
	public static final String XML_SOLVER_FILE = "solverFile";

	public static final String XML_RULE_NUM = "ruleNum";
	public static final String XML_RULE_FILE = "ruleFile";

	public static final String XML_DATASET_BASE = "datasetConfig";
	public static final String XML_DATASET_CLASS = "datasetClass";

	public static final String XML_REFERENCE_BASE = "refConfig";
	public static final String XML_REFERENCE_RULE = "refRule";
	public static final String XML_REFERENCE_TRACKING = "refTracking";
	public static final String XML_REFERENCE_NUM_JOBS = "numJobsThreshold";
	public static final String XML_REFERENCE_NUM_SAMPLES = "numSamples";
	public static final String XML_REFERENCE_SEED = "seed";

	public static final String XML_FITNESS_BASE = "fitnessConfig";
	public static final String XML_FITNESS_CLASS = "fitnessClass";

	public static final String XML_LISTENER_BASE = "listenerConfig";
	public static final String XML_LISTENER_CLASS = "listenerClass";

	public static final String XML_OUTPUT_BASE = "outputConfig";
	public static final String XML_OUTPUT_FILE = "outputFile";

	private Map<String, List<AbsEvalPriorityRule>> solversMap = new HashMap<String, List<AbsEvalPriorityRule>>();
	private RuleParser parser = new RuleParser();

	private SimConfig simConfig;

	private List<IJasimaEvalFitness> fitnesses = new ArrayList<>();
	private List<IWorkStationListener> listeners = new ArrayList<>();
	private Map<String, IWorkStationListener> listenerMap = new HashMap<>();
	private List<PR> referenceRules = new ArrayList<>();

	private String outputCsv = null;

	/**
	 * Instantiate a new instance of the evaluation procedure.
	 * @param xmlFilename
	 * @throws Exception
	 */
	public JasimaEvalProblem(String xmlFilename) throws Exception {
		validateXml(xmlFilename);
		loadConfiguration(xmlFilename);
	}

	// Check the xml against the xsd schema file.
	private void validateXml(String xmlFilename) throws Exception {
		try {
			Source xmlFile = new StreamSource(new File(xmlFilename));

			URL schemaFile = JasimaEvalProblem.class.getResource(EVALUATION_XSD);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();

			validator.validate(xmlFile);
		} catch (SAXException ex) {
			throw new Exception("Validation failed for " + xmlFilename + " because of " + ex.getLocalizedMessage());
		}
	}

	// Load in the configurations for the solvers, dataset and the performance measure.
	private void loadConfiguration(String xmlFilename) throws Exception {
		File xmlFile = new File(xmlFilename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		loadListeners(doc);
		loadSolvers(doc);
		loadDataset(doc);
		loadReference(doc);
		loadFitnesses(doc);
		loadOutput(doc);
	}

	// Load in the solvers from the XML configuration.
	private void loadSolvers(Document doc) throws Exception {
		System.out.println("Solver: loading solvers.");

		NodeList nList = doc.getElementsByTagName(XML_SOLVER_BASE);

		System.out.println("Solver: " + nList.getLength() + " solvers detected.");

		for (int i = 0; i < nList.getLength(); i++) {
			Element solverBase = (Element) nList.item(i);

			// Get the solver definition.
			String solverClassStr = solverBase.getElementsByTagName(XML_SOLVER_CLASS).item(0).getTextContent();

			Class<?> retrievedClass = Class.forName(solverClassStr);
			if (retrievedClass.isAssignableFrom(AbsEvalPriorityRule.class)) {
				throw new XMLStreamException("Solver must be of type JSSEvalSolver");
			}

			@SuppressWarnings("unchecked")  // The checks right above.
			Class<? extends AbsEvalPriorityRule> solverClass =
					(Class<? extends AbsEvalPriorityRule>) retrievedClass;

			System.out.println("Solver: loading solver: " + solverClass.getSimpleName());

			// Load any additional files.
			NodeList ruleFileNodeList = solverBase.getElementsByTagName(XML_SOLVER_FILE);

			if (ruleFileNodeList.getLength() != 0) {
				Element ruleBase = (Element) ruleFileNodeList.item(0);

				String ruleFilename = ruleBase.getElementsByTagName(XML_RULE_FILE).item(0).getTextContent();

				System.out.println("Solver: detected rule file. Reading from rule file: " + ruleFilename);

				List<AbsEvalPriorityRule> solvers = loadRuleFile(solverClass, ruleFilename);
				solversMap.put(ruleFilename, solvers);
			} else {
				System.out.println("Solver: no rule file detected. Loading a static solver.");

				List<AbsEvalPriorityRule> solvers = loadStaticSolvers(solverClass);
				solversMap.put(solverClassStr, solvers);
			}
		}

		System.out.println("Solver: loading complete.");
	}

	// Load in the rule from the specified file.
	private List<AbsEvalPriorityRule> loadRuleFile(Class<? extends AbsEvalPriorityRule> solverClass,
			String ruleFilename) throws Exception {
		InputStream fileStream = new FileInputStream(new File(ruleFilename));
		InputStreamReader fileReader = new InputStreamReader(fileStream);
		BufferedReader reader = new BufferedReader(fileReader);

		List<AbsEvalPriorityRule> solvers = new ArrayList<AbsEvalPriorityRule>();

		String ruleString;
		while ((ruleString = reader.readLine()) != null) {
			String[] split = ruleString.split(",");

			JasimaEvalConfig config = new JasimaEvalConfig();
			config.setSeed(Integer.parseInt(split[0]));

			NodeData data = new NodeData();
			data.setWorkStationListeners(listenerMap);
			config.setNodeData(data);

			List<INode> roots = new ArrayList<INode>();
			for (int i = 1; i < split.length; i++) {
				roots.add(parser.getRuleFromString(split[i]));
			}
			config.setRules(roots);

			AbsEvalPriorityRule solver = solverClass.newInstance();
			solver.setConfiguration(config);

			solvers.add(solver);
		}

		reader.close();

		return solvers;
	}

	// Load in a preset rule (e.g. SPT, FIFO).
	private List<AbsEvalPriorityRule> loadStaticSolvers(Class<? extends AbsEvalPriorityRule> solverClass)
			throws Exception {
		List<AbsEvalPriorityRule> solvers = new ArrayList<AbsEvalPriorityRule>();

		AbsEvalPriorityRule solver = solverClass.newInstance();
		solver.setConfiguration(new JasimaEvalConfig());

		solvers.add(solver);

		return solvers;
	}

	// Load in the dataset from the XML configuration.
	private void loadDataset(Document doc) throws Exception {
		System.out.println("SimConfig: loading simulator.");

		NodeList nList = doc.getElementsByTagName(XML_DATASET_BASE);

		Node datasetNode = nList.item(0);
		Element datasetBase = (Element) datasetNode;

		Class<?> datasetClass = Class.forName(datasetBase
				.getElementsByTagName(XML_DATASET_CLASS)
				.item(0)
				.getTextContent());

		ISimConfigEvalFactory factory;
		factory = (ISimConfigEvalFactory) datasetClass.newInstance();
		factory.loadConfig(datasetBase);

		simConfig = factory.generateSimConfig();

		System.out.println("SimConfig: loading complete.");
	}

	// Load in the reference rule from the XML configuration.
	private void loadReference(Document doc) throws Exception {
		System.out.println("Reference: loading reference rules.");

		NodeList nList = doc.getElementsByTagName(XML_REFERENCE_BASE);

		System.out.println("Reference: " + nList.getLength() + " reference rules detected.");

		for (int i = 0; i < nList.getLength(); i++) {
			Element refBase = (Element) nList.item(i);

			Class<?> refClass = Class.forName(refBase
					.getElementsByTagName(XML_REFERENCE_RULE)
					.item(0)
					.getTextContent());

			PR refRule = (PR) refClass.newInstance();

			NodeList nTrack = refBase.getElementsByTagName(XML_REFERENCE_TRACKING);
			if (nTrack.getLength() != 0) {
				Element trackBase = (Element) nTrack.item(0);

				int numJobThreshold = Integer.parseInt(trackBase.getElementsByTagName(XML_REFERENCE_NUM_JOBS).item(0).getTextContent());
				int numSamples = Integer.parseInt(trackBase.getElementsByTagName(XML_REFERENCE_NUM_SAMPLES).item(0).getTextContent());
				long seed = Integer.parseInt(trackBase.getElementsByTagName(XML_REFERENCE_SEED).item(0).getTextContent());

				TrackedPR trackedRefRule = new TrackedPR(refRule, numJobThreshold, numSamples, seed);
				referenceRules.add(trackedRefRule);
			} else {
				referenceRules.add(refRule);
			}
		}

		System.out.println("Reference: loading complete.");
	}

	// Load in the performance measure from the XML configuration.
	private void loadFitnesses(Document doc) throws Exception {
		System.out.println("Fitness: loading fitnesses.");

		NodeList nList = doc.getElementsByTagName(XML_FITNESS_BASE);

		for (int i = 0; i < nList.getLength(); i++) {
			Element fitnessBase = (Element) nList.item(i);

			Class<?> fitnessClass = Class.forName(fitnessBase
					.getElementsByTagName(XML_FITNESS_CLASS)
					.item(0)
					.getTextContent());

			System.out.println("Fitness: loading fitness: " + fitnessClass.getSimpleName());

			fitnesses.add((IJasimaEvalFitness) fitnessClass.newInstance());
		}

		System.out.println("Fitness: loading complete.");
	}

	// Load in the workstation listener from the XML configuration (Optional).
	private void loadListeners(Document doc) throws Exception {
		System.out.println("Listener: loading listeners.");

		NodeList nList = doc.getElementsByTagName(XML_LISTENER_BASE);

		System.out.println("Listener: " + nList.getLength() + " listeners detected.");

		for (int i = 0; i < nList.getLength(); i++) {
			Element listenerBase = (Element) nList.item(i);

			Class<?> listenerClass = Class.forName(listenerBase
					.getElementsByTagName(XML_LISTENER_CLASS)
					.item(0)
					.getTextContent());

			IWorkStationListenerEvalFactory factory;
			factory = (IWorkStationListenerEvalFactory) listenerClass.newInstance();
			factory.loadConfig(listenerBase);

			IWorkStationListener listener = factory.generateWorkStationListener();

			listeners.add(listener);
			listenerMap.put(listener.getClass().getSimpleName(), listener);
		}

		System.out.println("Listener: loading complete.");
	}

	// Load in the output file name from the XML configuration.
	private void loadOutput(Document doc) throws Exception {
		System.out.println("Output: loading output.");

		NodeList nList = doc.getElementsByTagName(XML_OUTPUT_BASE);

		Node outputNode = nList.item(0);
		Element outputBase = (Element) outputNode;

		outputCsv = outputBase
				.getElementsByTagName(XML_OUTPUT_FILE)
				.item(0)
				.getTextContent();

		System.out.println("Output: the output file: " + outputCsv);

		System.out.println("Ouptut: loading complete.");
	}

	/**
	 * Evaluate the rule over the dataset using the given performance measure.
	 */
	public void evaluate() throws Exception {
		System.out.println("Evaluating rules.");

		PrintStream output = new PrintStream(new File(outputCsv));

		// Print out the headers.
		output.print("RuleFile,RuleSeed,TestSet,InstanceNum");

		for (IJasimaEvalFitness fitness : fitnesses) {
			output.printf(",%s", fitness.getHeaderName());
		}

		output.println();

		for (String ruleFilename : solversMap.keySet()) {
			List<AbsEvalPriorityRule> solvers = solversMap.get(ruleFilename);

			System.out.println("Evaluation: evaluating " + ruleFilename + ". Number of rules: " + solvers.size() + ", Number of instances: " + simConfig.getNumConfigs());

			for (AbsEvalPriorityRule solver : solvers) {
				for (int i = 0; i < simConfig.getNumConfigs(); i++) {
					output.printf("%s,%d,%s,%d", ruleFilename, solver.getSeed(), simConfig.getClass().getSimpleName(), i);

					Experiment experiment = getExperiment(solver, i);
					experiment.runExperiment();

					for (IJasimaEvalFitness fitness : fitnesses) {
						String result = fitness.getRelevantResult(solver, experiment.getResults());
						output.printf(",%s", result);
					}

					output.println();

					for (IWorkStationListener listener : listeners) {
						listener.clear();
					}
				}

				simConfig.reset();
			}

			System.out.println("Evaluation: " + ruleFilename + " evaluation complete.");
		}

		output.close();

		System.out.println("Evaluation complete.");
	}

	// TODO

	private Experiment getExperiment(AbsEvalPriorityRule rule, int index) {
		JobShopExperiment experiment = ExperimentGenerator.getExperiment(simConfig, rule, index);

		for (IWorkStationListener listener : listeners) {
			experiment.addMachineListener(listener);
		}

		return experiment;
	}

}
