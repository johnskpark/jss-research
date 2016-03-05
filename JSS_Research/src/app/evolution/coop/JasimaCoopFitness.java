package app.evolution.coop;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaFitness;
import app.evolution.JasimaGPProblem;
import ec.EvolutionState;
import ec.Individual;

public abstract class JasimaCoopFitness implements IJasimaFitness<JasimaCoopIndividual> {

	private static final boolean DEFAULT_SHOULD_SET_CONTEXT = false;
	
	private JasimaGPProblem problem;
	
	private Individual[] individuals;
	private Map<Individual, IJasimaFitness<JasimaCoopIndividual>> individualFitnesses = new HashMap<Individual, IJasimaFitness<JasimaCoopIndividual>>();
	
	private Map<Individual, Boolean> updateFitness = new HashMap<Individual, Boolean>();
	private boolean shouldSetContext = DEFAULT_SHOULD_SET_CONTEXT;
	
	public void loadIndividuals(final Individual[] inds) {
		individuals = inds;
		
		for (Individual ind : inds) {
			if (!individualFitnesses.containsKey(ind)) {
				individualFitnesses.put(ind, getFitness(inds));
				individualFitnesses.get(ind).setProblem(getProblem());
			}
		}
	}
	
	protected abstract IJasimaFitness<JasimaCoopIndividual> getFitness(final Individual[] inds);

	public JasimaGPProblem getProblem() {
		return problem;
	}
	
	protected Individual[] getIndividuals() {
		return individuals;
	}

	protected boolean shouldSetContext() {
		return shouldSetContext;
	}
	
	public void setProblem(JasimaGPProblem problem) {
		this.problem = problem;
	}
	
	@Override
	public void accumulateFitness(final int index, final JasimaCoopIndividual ind, final Map<String, Object> results) {
		IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses.get(ind);
		
		fitness.accumulateFitness(index, ind, results);
	}

	// Set the update fitness somewhere here. 
	
	public void setUpdateConfiguration(final Individual[] inds, final boolean[] updateFitness, final boolean shouldSetContext) {
		for (int i = 0; i < inds.length; i++) {
			if (!this.updateFitness.containsKey(inds[i])) {
				this.updateFitness.put(inds[i], updateFitness[i]);
			} else if (!this.updateFitness.get(inds[i]) && updateFitness[i]) {
				this.updateFitness.put(inds[i], updateFitness[i]);
			}
		}
		
		this.shouldSetContext = shouldSetContext;
	}
	
	protected Map<Individual, Boolean> updateFitness() {
		return updateFitness;
	}
	
	@Override
	public void setFitness(final EvolutionState state, final JasimaCoopIndividual ind) {
		if (updateFitness.get(ind)) {
			IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses.get(ind);
		
			fitness.setFitness(state, ind);
		}
	}
	
	@Override
	public void clear() {
		individualFitnesses.clear();
		
		updateFitness.clear();
		shouldSetContext = DEFAULT_SHOULD_SET_CONTEXT;
	}

}
