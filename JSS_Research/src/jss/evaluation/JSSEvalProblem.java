package jss.evaluation;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * TODO javadoc.
 *
 * @author John Park
 *
 */
public class JSSEvalProblem {

	private static final String EVALUATION_XSD = "jss_evaluation.xsd";

	// Will work similar to the EvolutionProblem. Insert rules and fitness,
	// and then get the output.

	public JSSEvalProblem(String xmlFilename) throws Exception {
		validateXml(xmlFilename);
		loadConfiguration(xmlFilename);
	}

	// Check the xml against the xsd schema file.
	private void validateXml(String xmlFilename) throws Exception {
		Source xmlFile = new StreamSource(new File(xmlFilename));

		URL schemaFile = JSSEvalProblem.class.getResource(EVALUATION_XSD);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();

		validator.validate(xmlFile);
	}

	private void loadConfiguration(String xmlFilename) {
		loadRules();
		loadDataset();
	}

	private void loadRules() {
		// TODO
	}

	private void loadDataset() {
		// TODO
	}

	public void evaluate(String outputCsv) {
		// TODO
	}

}
