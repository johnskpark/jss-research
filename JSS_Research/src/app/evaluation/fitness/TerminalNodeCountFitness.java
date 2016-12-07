package app.evaluation.fitness;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.evaluation.IJasimaEvalFitness;
import app.node.INode;
import app.simConfig.SimConfig;
import app.tracker.JasimaExperimentTracker;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;

// FIXME finish sometime later.
public class TerminalNodeCountFitness implements IJasimaEvalFitness {

	@Override
	public String getHeaderName() {
		return "TerminalNodeCount";
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
		// Do nothing.
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
		Map<String, Integer> nodeCountMap = new HashMap<String, Integer>();

		// TODO

		List<NodeCount> nodeCount = new ArrayList<NodeCount>();
		for (String node : nodeCountMap.keySet()) {
			nodeCount.add(new NodeCount(node, nodeCountMap.get(node)));
		}

		Collections.sort(nodeCount);

		String output = "\"";
//		for (int i = 0; i )

		return output;
	}

	private class NodeCount implements Comparable<NodeCount> {
		private String node;
		private int count;

		public NodeCount(String node, int count) {
			this.node = node;
			this.count = count;
		}

		public String getNode() {
			return node;
		}

		public int getCount() {
			return count;
		}

		public int compareTo(NodeCount other) {
			int diff = other.count - this.count;
			if (diff != 0) {
				return diff;
			} else {
				return this.node.compareTo(other.node);
			}
		}
	}

}
