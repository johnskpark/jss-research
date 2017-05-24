package app.evaluation.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import app.node.INode;
import app.util.RuleParser;

public class TreeTrimmer {

	private List<Long> seeds;
	private List<INode[]> rules;

	private RuleParser parser = new RuleParser();

	public TreeTrimmer(String ruleFile) throws IOException {
		readRuleFile(ruleFile);

	}

	private void readRuleFile(String ruleFile) throws IOException {
		FileReader fReader = new FileReader(new File(ruleFile));
		BufferedReader reader = new BufferedReader(fReader);

		String line;
		while ((line = reader.readLine()) != null) {
			String[] split = line.split(",");

			String seed = split[0];
			INode[] ruleComponents = new INode[split.length - 1];

			for (int i = 1; i < split.length; i++) {
				ruleComponents[i-1] = parser.getRuleFromString(split[i]);
			}

			seeds.add(Long.parseLong(seed));
			rules.add(ruleComponents);
		}

		reader.close();
	}

	public void trimTree() {
		for (int i = 0; i < seeds.size(); i++) {
			long seed = seeds.get(i);
			INode[] rule = rules.get(i);

			for (int j = 0; j < rule.length; j++) {
				// TODO need to implement this later.
			}
		}
	}

	public static void main(String[] args) {
		try {
			TreeTrimmer trimmer = new TreeTrimmer(args[0]);
			trimmer.trimTree();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}
