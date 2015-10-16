package app.evolution.ensemble.eurogp;

import jasima.core.util.Pair;
import app.evolution.IJasimaGPProblem;
import app.evolution.IJasimaTracker;
import app.evolution.JasimaGPProblem;
import ec.gp.GPIndividual;

public class EuroGPTracker implements IJasimaTracker {

	private EuroGPProblem problem;

	@Override
	public void setProblem(IJasimaGPProblem problem) {
		this.problem = (EuroGPProblem) problem;
	}

	public void setProblem(JasimaGPProblem problem) {
		// TODO
	}

	@Override
	public Pair<GPIndividual, Double>[] getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
