package app.evaluation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jasima.core.util.Pair;

public class RuleExtractor {

	private String seedName;
	
	private Map<String, List<String>> fileSeeds = new HashMap<String, List<String>>();
	
	public RuleExtractor(String seedName) {
		this.seedName = seedName;
	}
	
	public void readSeeds() throws IOException {
		FileReader fileReader = new FileReader(new File(seedName));
		BufferedReader reader = new BufferedReader(fileReader);
		
		String line;
		while ((line = reader.readLine()) != null) {
			String fileName = line.substring(0, line.indexOf(' '));
			List<String> seeds = Arrays.asList(line.substring(line.indexOf(' ')+1).split(","));
			
			fileSeeds.put(fileName, seeds);
		}
		
		reader.close();
	}
	
	public void readDirectory(String dirName) throws IOException {
		File dir = new File(dirName);

		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			
			for (File f : files) {
				String fileName = f.getName();
				
				if (fileSeeds.containsKey(fileName)) {
					readRuleFile(f, fileSeeds.get(fileName));
				}
			}
		}
	}
	
	public void readRuleFile(File file, List<String> seeds) throws IOException {
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);

		String outputName = file.getName().replaceAll("(4op|8op).txt", "$1_best.txt");
		PrintStream output = new PrintStream(new File(outputName));
		
		List<Pair<String, String[]>> rules = new ArrayList<Pair<String, String[]>>();

		String line;
		while((line = reader.readLine()) != null) {
			String seed = line.substring(0, line.indexOf(','));
			String[] inds = line.substring(line.indexOf(',')+1).split(",");
			
			if (seeds.contains(seed)) {
				rules.add(new Pair<String, String[]>(seed, inds));
				
				output.println(line);
			}
		}
		
		for (int i = 0; i < rules.size(); i++) {
			String seed = rules.get(i).a;
			
			for (int j = 0; j < rules.get(i).b.length; j++) {
				String indRule = String.format("%s%d,%s", seed, j, rules.get(i).b[j]);
				
				output.println(indRule);
			}
		}

		reader.close();
		output.close();
	}
	
	public static void main(String[] args) {
		String seeds = args[0];
		String directory = args[1];
		RuleExtractor extractor = new RuleExtractor(seeds);
		
		try {
			extractor.readSeeds();
			extractor.readDirectory(directory);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
}
