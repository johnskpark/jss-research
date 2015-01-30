package jss.evolution.fitness;

import java.util.List;

import jss.IProblemInstance;
import jss.evolution.IGroupedFitness;
import jss.evolution.JSSGPCoopProblem;
import jss.evolution.statistic_data.PenaltyData;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopEnsembleFitness implements IGroupedFitness {

	private static final long serialVersionUID = -9127872949337138137L;

	public static final String P_RATIO = "ratio";

	public double diversityRatio;

	/**
	 * TODO javadoc.
	 */
	public CoopEnsembleFitness() {
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		try {
			diversityRatio = state.parameters.getInt(base.push(P_RATIO), null);
		} catch (NumberFormatException ex) {
			state.output.fatal(ex.getMessage());
		}
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double getFitness(Statistics stats, int index) {
		PenaltyData penaltyData = (PenaltyData) stats.getData(JSSGPCoopProblem.TRACKER_DATA);

		return stats.getAverageMakespanDeviation() * (1 + diversityRatio * penaltyData.getAveragePenalty(index));
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats,
			final int index) {
		double fitness = getFitness(stats, index);

		((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
	}

}
