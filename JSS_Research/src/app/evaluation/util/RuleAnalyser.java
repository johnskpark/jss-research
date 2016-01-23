package app.evaluation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class RuleAnalyser {

	public RuleAnalyser() {
		// TODO 
	}
	
	public void readDirectory(String dirName) throws IOException {
		File dir = new File(dirName);

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			for (File f : files) {
				if (f.getName().endsWith("4op.txt") || f.getName().endsWith("8op.txt")) {
					readRuleFile(f);
				}
			}
		}
	}
	
	public void readRuleFile(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);

		String outputName = file.getName().replaceAll("(4op|8op).txt", "$1_analysis.csv");
		PrintStream output = new PrintStream(new File(outputName));

		output.println("seed,numRules,,numNodes1,...,numNodes_numRules");
		
		int maxNumRules = Integer.MIN_VALUE;
		List<String> seedList = new ArrayList<String>();
		List<Integer> numRulesList = new ArrayList<Integer>();
		List<Integer[]> numNodesList = new ArrayList<Integer[]>();
		int count = 0;

		String line;
		while((line = reader.readLine()) != null) {
			String[] split = line.split(",");

			String seed = split[0];
			int numRules = split.length - 1;
			
			Integer[] numNodes = new Integer[numRules];
			for (int i = 1; i < split.length; i++) {
				String strippedRule = split[i].replaceAll("[()]", "");

				numNodes[i-1] = strippedRule.split("[\\s]+").length;
			}

			maxNumRules = Math.max(maxNumRules, numRules);
			seedList.add(seed);
			numRulesList.add(numRules);
			numNodesList.add(numNodes);
			count++;
		}

		for (int i = 0; i < count; i++) {
			output.printf("%s,%d,", seedList.get(i), numRulesList.get(i));
			
			Integer[] numNodes = numNodesList.get(i);
			for (int j = 0; j < maxNumRules; j++) {
				output.printf(",%d", (j < numNodes.length) ? numNodes[j] : 0);
			}
			
			output.println();
		}
		
		reader.close();
		output.close();
	}
	
	public static void main(String[] args) {
		RuleAnalyser analyser = new RuleAnalyser();
		String directory = args[0];
		
		try {
			analyser.readDirectory(directory);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
}
