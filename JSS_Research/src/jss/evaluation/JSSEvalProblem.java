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
 * TODO javadoc.
 *
 * @author John Park
 *
 */
public class JSSEvalProblem {

	public static final String EVALUATION_XSD = "jss_evaluation.xsd";

	public static final String XML_RULE_BASE = "ruleConfig";
	public static final String XML_RULE_CLASS = "ruleClass";
	public static final String XML_RULE_NUM = "ruleNum";
	public static final String XML_RULE_FILE = "ruleFile";

	public static final String XML_DATASET_BASE = "datasetConfig";
	public static final String XML_DATASET_CLASS = "datasetClass";
	public static final String XML_DATASET_FILE = "datasetFile";

	private Map<String, List<JSSEvalSolver>> solversMap = new HashMap<String, List<JSSEvalSolver>>();
	private IDataset dataset;

	private RuleParser parser = new RuleParser();

	/**
	 * TODO javadoc.
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

	// TODO docs.
	private void loadConfiguration(String xmlFilename) throws Exception {
		File xmlFile = new File(xmlFilename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		doc.getDocumentElement().normalize();

		loadRules(doc);
		loadDataset(doc);
	}

	// TODO docs.
	private void loadRules(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_RULE_BASE);

		for (int i = 0; i < nList.getLength(); i++) {
			Node ruleNode = nList.item(i);
			Element ruleBase = (Element) ruleNode;

			// Parse the node list
			String solverClassStr = ruleBase.getElementsByTagName(XML_RULE_CLASS).item(0).getTextContent();
			String ruleNumStr = ruleBase.getElementsByTagName(XML_RULE_NUM).item(0).getTextContent();
			String ruleFilename = ruleBase.getElementsByTagName(XML_RULE_FILE).item(0).getTextContent();

			Class<?> retrievedClass = Class.forName(solverClassStr);
			if (retrievedClass.isAssignableFrom(JSSEvalSolver.class)) {
				throw new RuntimeException("You done goofed"); // TODO
			}

			@SuppressWarnings("unchecked")  // The checks right above.
			Class<? extends JSSEvalSolver> solverClass =
					(Class<? extends JSSEvalSolver>) retrievedClass;

			int ruleNum = Integer.parseInt(ruleNumStr);

			loadRuleFile(solverClass, ruleNum, ruleFilename);
		}

	}

	// TODO docs.
	private void loadRuleFile(Class<? extends JSSEvalSolver> solverClass,
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

			JSSEvalSolver solver = solverClass.newInstance();
			solver.setConfiguration(config);

			solvers.add(solver);
		}

		solversMap.put(ruleFilename, solvers);

		reader.close();
	}

	// TODO docs.
	private void loadDataset(Document doc) throws Exception {
		NodeList nList = doc.getElementsByTagName(XML_DATASET_BASE);

		Node datasetNode = nList.item(0);
		Element datasetBase = (Element) datasetNode;

		Class<?> datasetClass = Class.forName(datasetBase
				.getElementsByTagName(XML_DATASET_CLASS)
				.item(0)
				.getTextContent());

		dataset = (IDataset)datasetClass.newInstance();

		NodeList datasetFileNodeList = datasetBase.getElementsByTagName(XML_DATASET_FILE);
		if (datasetFileNodeList.getLength() != 0) {
			String filename = datasetFileNodeList.item(0).getTextContent();

			// TODO load the file into the dataset.
		}
	}

	/**
	 * TODO javadoc.
	 * @param outputCsv
	 */
	public void evaluate(String outputCsv) throws Exception {
		PrintStream output = new PrintStream(new File(outputCsv));

		for (String ruleFilename : solversMap.keySet()) {
			List<JSSEvalSolver> solvers = solversMap.get(ruleFilename);

			for (JSSEvalSolver solver : solvers) {
				output.printf("%s,%d", ruleFilename, solver.getSeed());

				for (IProblemInstance problem : dataset.getProblems()) {
					IResult solution = solver.getSolution(problem);

					output.printf(",%f", solution.getMakespan());
				}

				output.println();
			}
		}

		output.close();
	}

}
