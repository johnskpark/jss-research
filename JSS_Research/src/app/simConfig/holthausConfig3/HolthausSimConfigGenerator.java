package app.simConfig.holthausConfig3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import app.simConfig.DynamicBreakdownSimConfig;

public class HolthausSimConfigGenerator {

	private static final double[] NUM_REPAIR_TIME_FACTORS = new double[]{1.5, 5.5, 10.5};
	private static final double[] NUM_BREAKDOWN_LEVELS = new double[]{0.0, 0.025, 0.05, 0.1, 0.15}; 

	private static final double[] NUM_DUE_DATE_FACTORS = new double[]{3, 5};

	private static final String V_CATEGORY_ALL = "all";

	private static final String REGEX = "\\(([^)]+)\\)[,]?";
	private static final double EPSILON = 0.001;

	private HolthausSimConfigGenerator() {
	}

	public static final DynamicBreakdownSimConfig getSimConfig(String instances) {
		// Match all brackets.
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(instances);

		m.find(); String rtfStr = m.group().replaceAll(REGEX, "$1");
		m.find(); String blStr = m.group().replaceAll(REGEX, "$1");
		m.find(); String ddfStr = m.group().replaceAll(REGEX, "$1");

		List<Double> repairTimeFactors = convertToVariables(rtfStr, NUM_REPAIR_TIME_FACTORS);
		List<Double> breakdownLevels = convertToVariables(blStr, NUM_BREAKDOWN_LEVELS);
		List<Integer> dueDateFactors = convertToVariables(ddfStr, NUM_DUE_DATE_FACTORS)
				.stream()
				.map(x -> x.intValue())
				.collect(Collectors.toList());

		return new HolthausSimConfig(repairTimeFactors, breakdownLevels, dueDateFactors);
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
