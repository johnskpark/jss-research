package app.evaluation;

public class Evaluate {

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
