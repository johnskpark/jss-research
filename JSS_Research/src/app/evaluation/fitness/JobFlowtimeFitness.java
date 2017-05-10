package app.evaluation.fitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.jasimaShopSim.util.IndJobStatCollector;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

public class JobFlowtimeFitness implements IJasimaEvalFitness {

	public static final String JOB_RELEASE_DATE = "jobReleaseDate";
	public static final String JOB_COMPLETION_TIME = "jobCompletionTime";
	public static final String JOB_FLOWTIME = "jobFlowtime";
	public static final String JOB_TARDINESS = "jobTardiness";

	@Override
	public String getHeaderName() {
		String jobReleaseDate = "JobRelease";
		String jobCompletionTime = "JobCompletion";
		String jobFlowtime = "JobFlowtime";
		String jobTardiness = "JobTardiness";

		return String.format("%s,%s,%s,%s",
				jobReleaseDate,
				jobCompletionTime,
				jobFlowtime,
				jobTardiness);
	}

	@Override
	public boolean resultIsNumeric() {
		return false;
	}

	@Override
	public void beforeExperiment(final PR rule,
			final SimConfig simConfig,
			final JobShopExperiment experiment,
			final JasimaExperimentTracker<INode> tracker) {
		experiment.addShopListener(new IndJobStatCollector());
	}

	@Override
	public double getNumericResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		throw new UnsupportedOperationException("The output is not numeric!");
	}

	@Override
	public String getStringResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		String[] experimentResults = new String[] {
				getReleaseDateResult(rule, simConfig, configIndex, results, tracker),
				getCompletionTimeResult(rule, simConfig, configIndex, results, tracker),
				getFlowtimeResult(rule, simConfig, configIndex, results, tracker),
				getTardinessResult(rule, simConfig, configIndex, results, tracker)
		};

		String output = String.format("%s", experimentResults[0]);

		for (int i = 1; i < experimentResults.length; i++) {
			output += String.format(",%s", experimentResults[i]);
		}

		return output;
	}

	protected String getReleaseDateResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		@SuppressWarnings("unchecked")
		Map<Integer, Double> jobRelDate = (Map<Integer, Double>) results.get(JOB_RELEASE_DATE);

		List<Integer> jobNumList = new ArrayList<Integer>(jobRelDate.keySet());
		Collections.sort(jobNumList);

		StringBuilder jobRelDateStr = new StringBuilder();
		for (int i = 0; i < jobNumList.size(); i++) {
			int jobNum = jobNumList.get(i);
			double relDate = jobRelDate.get(jobNum);

			if (i != 0) {
				jobRelDateStr.append(",");
			}
			jobRelDateStr.append(String.format("%d:%.4f", jobNum, relDate));
		}

		return "\"" + jobRelDateStr.toString() + "\"";
	}

	protected String getCompletionTimeResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		@SuppressWarnings("unchecked")
		Map<Integer, Double> jobCompTime = (Map<Integer, Double>) results.get(JOB_COMPLETION_TIME);

		List<Integer> jobNumList = new ArrayList<Integer>(jobCompTime.keySet());
		Collections.sort(jobNumList);

		StringBuilder jobCompTimeStr = new StringBuilder();
		for (int i = 0; i < jobNumList.size(); i++) {
			int jobNum = jobNumList.get(i);
			double compTime = jobCompTime.get(jobNum);

			if (i != 0) {
				jobCompTimeStr.append(",");
			}
			jobCompTimeStr.append(String.format("%d:%.4f", jobNum, compTime));
		}

		return "\"" + jobCompTimeStr.toString() + "\"";
	}

	protected String getFlowtimeResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		@SuppressWarnings("unchecked")
		Map<Integer, Double> jobFlowtime = (Map<Integer, Double>) results.get(JOB_FLOWTIME);

		List<Integer> jobNumList = new ArrayList<Integer>(jobFlowtime.keySet());
		Collections.sort(jobNumList);

		StringBuilder jobFlowtimeStr = new StringBuilder();
		for (int i = 0; i < jobNumList.size(); i++) {
			int jobNum = jobNumList.get(i);
			double flowtime = jobFlowtime.get(jobNum);

			if (i != 0) {
				jobFlowtimeStr.append(",");
			}
			jobFlowtimeStr.append(String.format("%d:%.4f", jobNum, flowtime));
		}

		return "\"" + jobFlowtimeStr.toString() + "\"";
	}

	protected String getTardinessResult(final PR rule,
			final SimConfig simConfig,
			final int configIndex,
			final Map<String, Object> results,
			final JasimaExperimentTracker<INode> tracker) {
		@SuppressWarnings("unchecked")
		Map<Integer, Double> jobTardiness = (Map<Integer, Double>) results.get(JOB_TARDINESS);

		List<Integer> jobNumList = new ArrayList<Integer>(jobTardiness.keySet());
		Collections.sort(jobNumList);

		StringBuilder jobTardinessStr = new StringBuilder();
		for (int i = 0; i < jobNumList.size(); i++) {
			int jobNum = jobNumList.get(i);
			double Tardiness = jobTardiness.get(jobNum);

			if (i != 0) {
				jobTardinessStr.append(",");
			}
			jobTardinessStr.append(String.format("%d:%.4f", jobNum, Tardiness));
		}

		return "\"" + jobTardinessStr.toString() + "\"";
	}
}
