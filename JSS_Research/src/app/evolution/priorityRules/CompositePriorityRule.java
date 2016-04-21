package app.evolution.priorityRules;

import java.util.List;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import ec.Individual;
import ec.gp.GPIndividual;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

public class CompositePriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = 6402065211367618261L;

	private Individual[] individuals;

	private PrioRuleTarget[] entries;
	private double[] priorities;
	private int[] selectedIndices;

	public CompositePriorityRule() {
		super();
		selectedIndices = null;
	}

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		super.setConfiguration(config);

		individuals = config.getIndividuals();
	}

	@Override
	public Individual[] getIndividuals() {
		return individuals;
	}

	@Override
	public void beforeCalc(PriorityQueue<?> q) {
		super.beforeCalc(q);

		// Clear the current selected index.
		clear();

		// Calculate the priorities using the first individual.
		entries = new PrioRuleTarget[q.size()];
		priorities = new double[q.size()];

		double minPriority = Double.POSITIVE_INFINITY;
		double maxPriority = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < q.size(); i++) {
			PrioRuleTarget entry = q.get(i);
			entries[i] = entry;

			data.setPrioRuleTarget(entry);

			GPIndividual ind = (GPIndividual) individuals[0];
			ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

			priorities[i] = data.getPriority();
			minPriority = Math.min(minPriority, priorities[i]);
			maxPriority = Math.max(maxPriority, priorities[i]);
		}

		// Determine which tree to use using the normalised priorities.
		selectedIndices = new int[q.size()];

		for (int i = 0; i < q.size(); i++) {
			priorities[i] = (priorities[i] - minPriority) / (maxPriority - minPriority);

			selectedIndices[i] = (int) (priorities[i] * (individuals.length - 1)) + 1;
		}
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		data.setPrioRuleTarget(entry);

		int index = indexOf(entry);

		GPIndividual ind = (GPIndividual) individuals[index];
		ind.trees[0].child.eval(state, threadnum, data, null, ind, null);

		return data.getPriority();
	}

	private int indexOf(PrioRuleTarget entry) {
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].equals(entry)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public List<PrioRuleTarget> getEntryRankings() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void clear() {
		entries = null;
		priorities = null;
		selectedIndices = null;
	}

}
