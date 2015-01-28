package jss.evaluation;

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

import jss.IDataset;
import jss.IProblemInstance;
import jss.IResult;
import jss.evaluation.node.INode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The "main" class that carries out the evaluation procedure for a set of
 * rules over a dataset.
 *
 * The rules, dataset and performance measure are loaded in using an XML file.
 * The syntax for the XML file is given by jss_evaluation.xsd.
 *
 * @author John Park
 *
 */
public class JSSEvalProblem {

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

	private static final int DEFAULT_SEED = 15;

	private Map<String, List<JSSEvalSolver>> solversMap = new HashMap<String, List<JSSEvalSolver>>();
	private IDataset dataset;
	private IFitness fitness;

	private RuleParser parser = new RuleParser();

	/**
	 * Instantiate a new instance of the evaluation procedure.
	 * @param xmlFilename
	 * @throws Exception
	 */
	public JSSEvalProblem(String xmlFilename) throws Exception {
		validateXml(xmlFilename);
		loadConfiguration(xmlFilename);
	}

	// Check the xml against the xsd schema file.
	private void validateXml(String xmlFilename) throws Exception {
		try {
			Source xmlFile = new StreamSource(new File(xmlFilename));

			URL schemaFile = JSSEvalProblem.class.getResource(EVALUATION_XSD);
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
	}

	// Load in the solvers from the XML configuration.
	private void loadSolvers(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_SOLVER_BASE);

		for (int i = 0; i < nList.getLength(); i++) {
			Element solverBase = (Element) nList.item(i);

			// Get the solver definition.
			String solverClassStr = solverBase.getElementsByTagName(XML_SOLVER_CLASS).item(0).getTextContent();

			Class<?> retrievedClass = Class.forName(solverClassStr);
			if (retrievedClass.isAssignableFrom(JSSEvalSolver.class)) {
				throw new XMLStreamException("Solver must be of type JSSEvalSolver");
			}

			@SuppressWarnings("unchecked")  // The checks right above.
			Class<? extends JSSEvalSolver> solverClass =
					(Class<? extends JSSEvalSolver>) retrievedClass;

			// Load any additional files.
			NodeList ruleFileNodeList = solverBase.getElementsByTagName(XML_SOLVER_FILE);

			if (ruleFileNodeList.getLength() != 0) {
				Element ruleBase = (Element) ruleFileNodeList.item(0);

				String ruleNumStr = ruleBase.getElementsByTagName(XML_RULE_NUM).item(0).getTextContent();
				String ruleFilename = ruleBase.getElementsByTagName(XML_RULE_FILE).item(0).getTextContent();

				int ruleNum = Integer.parseInt(ruleNumStr);

				List<JSSEvalSolver> solvers = loadRuleFile(solverClass, ruleNum, ruleFilename);
				solversMap.put(ruleFilename, solvers);
			} else {
				List<JSSEvalSolver> solvers = loadStaticSolvers(solverClass);

				solversMap.put(solverClassStr, solvers);
			}
		}

	}

	// Load in the rule from the specified file.
	private List<JSSEvalSolver> loadRuleFile(Class<? extends JSSEvalSolver> solverClass,
			int numRules,
			String ruleFilename) throws Exception {
		InputStream fileStream = new FileInputStream(new File(ruleFilename));
		InputStreamReader fileReader = new InputStreamReader(fileStream);
		BufferedReader reader = new BufferedReader(fileReader);

		List<JSSEvalSolver> solvers = new ArrayList<JSSEvalSolver>();

		String ruleString;
		while ((ruleString = reader.readLine()) != null) {
			String[] split = ruleString.split(",");

			JSSEvalConfiguration config = new JSSEvalConfiguration();
			config.setSeed(Integer.parseInt(split[0]));

			List<INode> roots = new ArrayList<INode>();
			for (int i = 0; i < numRules; i++) {
				roots.add(parser.getRuleFromString(split[i+1]));
			}
			config.setRules(roots);
			config.setRuleNum(numRules);

			JSSEvalSolver solver = solverClass.newInstance();
			solver.setConfiguration(config);

			solvers.add(solver);
		}

		reader.close();

		return solvers;
	}

	// Load in a preset rule (e.g. SPT, FIFO).
	private List<JSSEvalSolver> loadStaticSolvers(Class<? extends JSSEvalSolver> solverClass)
			throws Exception {
		List<JSSEvalSolver> solvers = new ArrayList<JSSEvalSolver>();

		JSSEvalSolver solver = solverClass.newInstance();
		solver.setConfiguration(new JSSEvalConfiguration());

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

		dataset = (IDataset)datasetClass.newInstance();
		dataset.setSeed(DEFAULT_SEED); // TODO temporary code.

		NodeList datasetFileNodeList = datasetBase.getElementsByTagName(XML_DATASET_FILE);
		if (datasetFileNodeList.getLength() != 0) {
			String filename = datasetFileNodeList.item(0).getTextContent();

			// TODO load the file into the dataset.
		}

		dataset.generateDataset();
	}

	// Load in the performance measure from the XML configuration.
	private void loadFitness(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_FITNESS_BASE);

		Node datasetNode = nList.item(0);
		Element datasetBase = (Element) datasetNode;

		Class<?> fitnessClass = Class.forName(datasetBase
				.getElementsByTagName(XML_FITNESS_CLASS)
				.item(0)
				.getTextContent());

		fitness = (IFitness)fitnessClass.newInstance();
	}

	/**
	 * Evaluate the rule over the dataset using the given performance measure.
	 * @param outputCsv The output CSV file to write the results to.
	 */
	public void evaluate(String outputCsv) throws Exception {
		PrintStream output = new PrintStream(new File(outputCsv));

		for (String ruleFilename : solversMap.keySet()) {
			List<JSSEvalSolver> solvers = solversMap.get(ruleFilename);

			for (JSSEvalSolver solver : solvers) {
				output.printf("%s,%d", ruleFilename, solver.getSeed());

				for (IProblemInstance problem : dataset.getProblems()) {
					IResult solution = solver.getSolution(problem);

					output.printf(",%f", fitness.getFitness(solution));
				}

				output.println();
			}
		}

		output.close();
	}

}
