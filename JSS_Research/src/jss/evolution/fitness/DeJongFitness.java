package jss.evolution.fitness;

import java.util.List;

import jss.IProblemInstance;
import jss.evolution.IMOFitness;
import jss.problem.Statistics;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;

public class DeJongFitness implements IMOFitness {

	private static final long serialVersionUID = -8270954617791582445L;

	/**
	 * TODO javadoc.
	 */
	public DeJongFitness() {
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
	}

	@Override
	public void loadDataset(List<IProblemInstance> problems) {
	}

	@Override
	public double[] getFitness(Statistics stats) {
		// TODO
		return null;
	}

	@Override
	public void setFitness(final EvolutionState state,
			final Individual ind,
			final Statistics stats) {
		GPIndividual gpInd = (GPIndividual) ind;

		double[] newObjectives = new double[2];
		newObjectives[0] = stats.getAverageTWT();
		newObjectives[1] = 0; // TODO the size of the individual

		((MultiObjectiveFitness)ind.fitness).setObjectives(state, newObjectives);
	}

}
