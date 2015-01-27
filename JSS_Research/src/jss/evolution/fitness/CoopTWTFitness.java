package jss.evolution.fitness;

import java.util.List;

import ec.EvolutionState;
import ec.util.Parameter;
import jss.IProblemInstance;
import jss.evolution.ISimpleFitness;
import jss.evolution.JSSGPGroupedProblem;
import jss.evolution.statistic_data.PenaltyData;
import jss.problem.Statistics;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class CoopTWTFitness implements ISimpleFitness {

	private static final long serialVersionUID = 3149457776389815117L;

	public static final String P_RATIO = "ratio";

	public double diversityRatio;

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
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
	public double getFitness(Statistics stats) {
		double twt = stats.getAverageTWT();
		double penalty = ((PenaltyData) stats.getData(JSSGPGroupedProblem.TRACKER_DATA)).getAveragePenalty(0);

		return twt * (1.0 + diversityRatio * penalty);
	}

}
