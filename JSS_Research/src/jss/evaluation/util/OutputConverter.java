package jss.evaluation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputConverter {

	private List<CsvOutput> outputs = new ArrayList<CsvOutput>();

	public OutputConverter() {
	}

	public void readDirectory(String dirName) throws IOException {
		File dir = new File(dirName);

		if (!dir.exists() || !dir.isDirectory()) {
			throw new IOException("Is not a directory");
		}

		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".csv")) {
				readFile(f);
			}
		}
	}

	public void readFile(String fileName) throws IOException {
		readFile(new File(fileName));
	}

	private void readFile(File file) throws IOException {
		FileReader fReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fReader);

		String line;
		while ((line = reader.readLine()) != null) {
			String[] split = line.split(",");

			CsvOutput output = new CsvOutput();
			output.name = split[0];
			output.seed = Integer.parseInt(split[1]);

			for (int i = 2; i < split.length; i++) {
				output.results.add(Double.parseDouble(split[i]));
			}
		}

		reader.close();
	}

	private class CsvOutput {
		String name;
		int seed;
		List<Double> results;
	}

	public static void main(String [] args) {
		try {
			OutputConverter converter = new OutputConverter();

			String dir = args[0];
			converter.readDirectory(dir);

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
