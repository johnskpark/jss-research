package app.evolution.coop.fitness;

import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public class DCFKozaFitness extends KozaFitness {

	private double prevPerfFitness;
	private double perfFitness;

	private double prevActRate;
	private double actRate;

	private double weight;

	public void addSystemPerformance(final EvolutionState state, final double perf) {
		prevPerfFitness = perfFitness;
		perfFitness = perf;
	}

	public void addActivationRate(final EvolutionState state, final double rate) {
		prevActRate = actRate;
		actRate = rate;
	}

	@Override
	@Deprecated
	public void setStandardizedFitness(final EvolutionState state, final double _f) {
		super.setStandardizedFitness(state, _f);
	}


}
