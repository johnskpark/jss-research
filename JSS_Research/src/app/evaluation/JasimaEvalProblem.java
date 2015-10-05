package app.evaluation;

import jasima.core.experiment.Experiment;
import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import app.listener.AbsWorkStationListener;
import app.node.INode;
import app.simConfig.AbsSimConfig;
import app.util.RuleParser;

public class JasimaEvalProblem {

	public static final String EVALUATION_XSD = "jss_evaluation.xsd";

	public static final String XML_SOLVER_BASE = "solverConfig";
	public static final String XML_SOLVER_CLASS = "solverClass";
	public static final String XML_SOLVER_FILE = "solverFile";

	public static final String XML_RULE_NUM = "ruleNum";
	public static final String XML_RULE_FILE = "ruleFile";

	public static final String XML_DATASET_BASE = "datasetConfig";
	public static final String XML_DATASET_CLASS = "datasetClass";
	public static final String XML_DATASET_FILE = "datasetFile";

	public static final String XML_FITNESS_BASE = "fitnessConfig";
	public static final String XML_FITNESS_CLASS = "fitnessClass";

	public static final String XML_LISTENER_BASE = "listenerConfig";
	public static final String XML_LISTENER_CLASS = "listenerClass";

	public static final String XML_OUTPUT_BASE = "outputConfig";
	public static final String XML_OUTPUT_FILE = "outputFile";

	private static final int DEFAULT_SEED = 15;

	private Map<String, List<AbsEvalPriorityRule>> solversMap = new HashMap<String, List<AbsEvalPriorityRule>>();
	private AbsSimConfig simConfig;
	private IJasimaEvalFitness fitness;

	private RuleParser parser = new RuleParser();

	private AbsWorkStationListener workstationListener;

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

		loadSolvers(doc);
		loadDataset(doc);
		loadFitness(doc);
		loadListener(doc);
		loadOutput(doc);
	}

	// Load in the solvers from the XML configuration.
	private void loadSolvers(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_SOLVER_BASE);

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

			// Load any additional files.
			NodeList ruleFileNodeList = solverBase.getElementsByTagName(XML_SOLVER_FILE);

			if (ruleFileNodeList.getLength() != 0) {
				Element ruleBase = (Element) ruleFileNodeList.item(0);

				String ruleFilename = ruleBase.getElementsByTagName(XML_RULE_FILE).item(0).getTextContent();

				List<AbsEvalPriorityRule> solvers = loadRuleFile(solverClass, ruleFilename);
				solversMap.put(ruleFilename, solvers);
			} else {
				List<AbsEvalPriorityRule> solvers = loadStaticSolvers(solverClass);

				solversMap.put(solverClassStr, solvers);
			}
		}

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
		NodeList nList = doc.getElementsByTagName(XML_DATASET_BASE);

		Node datasetNode = nList.item(0);
		Element datasetBase = (Element) datasetNode;

		Class<?> datasetClass = Class.forName(datasetBase
				.getElementsByTagName(XML_DATASET_CLASS)
				.item(0)
				.getTextContent());

		simConfig = (AbsSimConfig) datasetClass.newInstance();

		NodeList datasetFileNodeList = datasetBase.getElementsByTagName(XML_DATASET_FILE);
		if (datasetFileNodeList.getLength() != 0) {
			String filename = datasetFileNodeList.item(0).getTextContent();

			loadDatasetFile(filename);
		}
	}

	private void loadDatasetFile(String filename) throws IOException {
		// FIXME add in the functionality to add in files.
	}

	// Load in the performance measure from the XML configuration.
	private void loadFitness(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_FITNESS_BASE);

		Node fitnessNode = nList.item(0);
		Element fitnessBase = (Element) fitnessNode;

		Class<?> fitnessClass = Class.forName(fitnessBase
				.getElementsByTagName(XML_FITNESS_CLASS)
				.item(0)
				.getTextContent());

		fitness = (IJasimaEvalFitness) fitnessClass.newInstance();
	}

	// Load in the workstation listener from the XML configuration (Optional).
	private void loadListener(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_LISTENER_BASE);

		if (nList.getLength() != 0) {
			Node listenerNode = nList.item(0);
			Element listenerBase = (Element) listenerNode;

			Class<?> listenerClass = Class.forName(listenerBase
					.getElementsByTagName(XML_LISTENER_CLASS)
					.item(0)
					.getTextContent());

			workstationListener = (AbsWorkStationListener) listenerClass.newInstance();
		}
	}

	// Load in the output file name from the XML configuration.
	private void loadOutput(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_OUTPUT_BASE);

		Node outputNode = nList.item(0);
		Element outputBase = (Element) outputNode;

		outputCsv = outputBase
				.getElementsByTagName(XML_OUTPUT_FILE)
				.item(0)
				.getTextContent();
	}

	/**
	 * Evaluate the rule over the dataset using the given performance measure.
	 */
	public void evaluate() throws Exception {
		PrintStream output = new PrintStream(new File(outputCsv));

		for (String ruleFilename : solversMap.keySet()) {
			List<AbsEvalPriorityRule> solvers = solversMap.get(ruleFilename);

			for (AbsEvalPriorityRule solver : solvers) {
				output.printf("%s,%d", ruleFilename, solver.getSeed());
				simConfig.setSeed(DEFAULT_SEED); // FIXME temporary code.

				for (int i = 0; i < simConfig.getNumConfigs(); i++) {
					Experiment experiment = getExperiment(solver, i);

					experiment.runExperiment();

					output.printf(",%f", fitness.getRelevantResult(experiment.getResults()));
				}

				output.println();
			}
		}

		output.close();
	}

	@SuppressWarnings("unchecked")
	private Experiment getExperiment(AbsEvalPriorityRule rule, int index) {
		DynamicShopExperiment experiment = new DynamicShopExperiment();

		experiment.setInitialSeed(simConfig.getLongValue());
		experiment.setNumMachines(simConfig.getNumMachines(index));
		experiment.setUtilLevel(simConfig.getUtilLevel(index));
		experiment.setDueDateFactor(simConfig.getDueDateFactor(index));
		experiment.setWeights(simConfig.getWeight(index));
		experiment.setOpProcTime(simConfig.getMinOpProc(index), simConfig.getMaxOpProc(index));
		experiment.setNumOps(simConfig.getMinNumOps(index), simConfig.getMaxNumOps(index));

		BasicJobStatCollector statCollector = new BasicJobStatCollector();
		statCollector.setIgnoreFirst(simConfig.getNumIgnore());

		experiment.setShopListener(new NotifierListener[]{statCollector});
		experiment.addMachineListener(workstationListener);
		experiment.setStopAfterNumJobs(simConfig.getStopAfterNumJobs());
		experiment.setSequencingRule(rule);
		experiment.setScenario(DynamicShopExperiment.Scenario.JOB_SHOP);

		return experiment;
	}

	// Main.
	public static void main(String[] args) {
		String xmlFilename = args[0];

		try {
			JasimaEvalProblem evalProblem;

			evalProblem = new JasimaEvalProblem(xmlFilename);
			evalProblem.evaluate();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}
