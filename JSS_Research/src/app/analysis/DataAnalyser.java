package app.analysis;

import org.xml.sax.SAXException;

//Right, what do I need to do with the evaluation procedure?
//
//So the process goes:
//- Evaluate the rules and get the output results.
//- Shrink the files by their seeds.
//- Get the best rule within the shrinked file over its performance over a particular set and output specific details of the particular rule.
//- Get the number of members that make up each of the rules in the shrinked file and output that into a file.
//- Get a list of comparisons between the shrinked files over each instance, and how they perform against each other.
//- And more stuff as I go along.
//
//- I need a method of inputting multiple datasets that the rules can be evaluated over simultaneously, each outputting a results file.
//- I need a way of retaining the common 30 seeds from the results file for analysis.
//- I need another script that will read the values from the results files, and gives the average and standard deviation for each instance and the overall average and standard deviation.
//- I need a script that compares the
//- I need a way of grouping the evolution times together into a single csv file as follows:
//
//seed,time
//...,...
//NB: if you want to have a vector in a .csv file, then use double quotes, e.g.,
//1,2,5,3,"2,3",1 will have 5 columns, where "2,3" is a single column.

public class DataAnalyser {

	public static final String ANALYSIS_XSD = "jss_analysis.xsd";

	public DataAnalyser(String xmlFilename) throws Exception {
		validateXml(xmlFilename);
		loadConfiguration(xmlFilename);
	}

	private void validateXml(String xmlFilename) throws Exception {
//		try {
//
//		} catch (SAXException ex) {
//		}
	}

	private void loadConfiguration(String xmlFilename) throws Exception {
		// TODO
	}

	// Carries out the main data processing.
	public void analyse() throws Exception {

	}

}
