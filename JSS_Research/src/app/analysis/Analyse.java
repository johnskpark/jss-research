package app.analysis;

public class Analyse {

	public static void main(String[] args) {
		String xmlFilename = args[0];

		try {
			DataAnalyser analyser;

			analyser = new DataAnalyser(xmlFilename);
			analyser.analyse();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
