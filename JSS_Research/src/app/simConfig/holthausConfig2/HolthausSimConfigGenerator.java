package app.simConfig.holthausConfig2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import app.simConfig.DynamicSimConfig;

public class HolthausSimConfigGenerator {

	private static final double[] NUM_DUE_DATE_FACTORS = new double[]{4, 8};

	private static final String V_CATEGORY_ALL = "all";

	private static final String REGEX = "\\(([^)]+)\\)[,]?";
	private static final double EPSILON = 0.001;

	private HolthausSimConfigGenerator() {
	}

	public static final DynamicSimConfig getSimConfig(String instances) {
		// Match all brackets.
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(instances);

		m.find(); String ddfStr = m.group().replaceAll(REGEX, "$1");

		List<Integer> dueDateFactors = convertToVariables(ddfStr, NUM_DUE_DATE_FACTORS)
				.stream()
				.map(x -> x.intValue())
				.collect(Collectors.toList());

		return new HolthausSimConfig(dueDateFactors);
	}

	private static List<Double> convertToVariables(String varStr, double[] validVars) {
		List<Double> varsList = new ArrayList<Double>();

		for (String numStr : varStr.split(",")) {
			if (numStr.equals(V_CATEGORY_ALL)) {
				for (double var : validVars) {
					varsList.add(var);
				}

				return varsList;
			} else {
				double num = Double.parseDouble(numStr);

				boolean varFound = false;
				for (int i = 0; i < validVars.length && !varFound; i++) {
					if (num > validVars[i] - EPSILON && num < validVars[i] + EPSILON) {
						varsList.add(validVars[i]);
						varFound = true;
					}
				}

				if (!varFound) {
					throw new RuntimeException("Error in HolthausSimConfigGenerator.");
				}
			}
		}

		return varsList;
	}

}
