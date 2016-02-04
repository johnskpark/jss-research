package app.evolution.coop;

import java.util.HashMap;
import java.util.Map;

import app.evolution.IJasimaFitness;
import ec.EvolutionState;
import ec.Individual;

public abstract class JasimaCoopFitness implements IJasimaFitness<JasimaCoopIndividual> {

	private Map<Individual, IJasimaFitness<JasimaCoopIndividual>> individualFitnesses = new HashMap<Individual, IJasimaFitness<JasimaCoopIndividual>>();
	
	private Map<Individual, Boolean> updateFitness = new HashMap<Individual, Boolean>();
	private boolean shouldSetContext;
	
	public void loadIndividuals(final Individual[] inds) {
		for (Individual ind : inds) {
			if (!individualFitnesses.containsKey(ind)) {
				individualFitnesses.put(ind, getFitness());
			}
		}
	}
	
	protected abstract IJasimaFitness<JasimaCoopIndividual> getFitness();

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
	
	protected boolean shouldSetContext() {
		return shouldSetContext;
	}
	
	@Override
	public void setFitness(final EvolutionState state, final JasimaCoopIndividual ind) {
		IJasimaFitness<JasimaCoopIndividual> fitness = individualFitnesses.get(ind);
		
		fitness.setFitness(state, ind);
	}

//	public void setTrialFitness(final EvolutionState state,
//			final Individual[] inds,
//			final boolean[] updateFitness,
//			final boolean shouldSetContext);
//
//	public void setDiversityFitness(final EvolutionState state,
//			final Individual[] inds,
//			final boolean[] updateFitness);
//
//	public void setObjectiveFitness(final EvolutionState state,
//			final Individual[] inds);

}
