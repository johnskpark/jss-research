package app.simConfig.mbConfig4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import app.simConfig.DynamicBreakdownSimConfig;

public class MB4SimConfigGenerator {

	private static final String V_CATEGORY_ALL = "all";

	private static final String REGEX = "\\(([^)]+)\\)[,]?";
	private static final double EPSILON = 0.001;

	private MB4SimConfigGenerator() {
	}

	public static final DynamicBreakdownSimConfig getSimConfig(String instances) {
		// Match all brackets.
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(instances);

		m.find(); String rtfStr = m.group().replaceAll(REGEX, "$1");
		m.find(); String blStr = m.group().replaceAll(REGEX, "$1");
		m.find(); String ddfStr = m.group().replaceAll(REGEX, "$1");
		m.find(); String sanjStr = m.group().replaceAll(REGEX, "$1");

		List<Double> repairTimeFactors = convertToVariables(rtfStr, MB4SimConfig.NUM_REPAIR_TIME_FACTORS);
		List<Double> breakdownLevels = convertToVariables(blStr, MB4SimConfig.NUM_BREAKDOWN_LEVELS);
		List<Integer> dueDateFactors = convertToVariables(ddfStr, MB4SimConfig.NUM_DUE_DATE_FACTORS)
				.stream()
				.map(x -> x.intValue())
				.collect(Collectors.toList());
		List<Integer> stopAfterNumJobs = convertToVariables(sanjStr, MB4SimConfig.NUM_STOP_AFTER_NUM_JOBS)
				.stream()
				.map(x -> x.intValue())
				.collect(Collectors.toList());

		return new MB4SimConfig(repairTimeFactors, breakdownLevels, dueDateFactors, stopAfterNumJobs);
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
