package jss.evolution.fitness;

import java.util.List;

import jss.IProblemInstance;
import jss.evolution.IMOFitness;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class MOMakespanTWTFitness implements IMOFitness {

	private static final long serialVersionUID = 1053629485918930948L;

	/**
	 * TODO javadoc.
	 */
	public MOMakespanTWTFitness() {
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double[] getFitness(Statistics stats) {
		return new double[]{stats.getAverageMakespan(), stats.getAverageTWT()};
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats) {
		double[] newObjectives = getFitness(stats);

		((MultiObjectiveFitness)ind.fitness).setObjectives(state, newObjectives);
	}

}
