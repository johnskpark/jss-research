package jss.evaluation;

/**
 * TODO call this something else after I have things more formalised.
 * @author parkjohn
 *
 */
public class Main {

	public static void main(String[] args) {
		String xmlFilename = args[0];
		String outputCsv = args[1];

		try {
			JSSEvalProblem evalProblem;

			evalProblem = new JSSEvalProblem(xmlFilename);
			evalProblem.evaluate(outputCsv);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
