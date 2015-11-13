package app.evolution;

import jasima.shopSim.core.PrioRuleTarget;

import java.util.List;

import app.dispatchingDecision.JasimaDispatchingDecision;
import ec.Individual;
import ec.util.Pair;

// TODO right, the tracker needs to be able to do the following:
// - Get the decision made by the sequencing rule on which job was selected to be processed.
// - Get the decisions made by the individual components that make up the sequencing rule.
// - Get the priorities assigned to each of the jobs by the individuals components.

// I'm wondering: Is there a better way to implement the ensemble rules for Jasima?
// Could I have something where the EnsemblePriorityRule is aggregated from BasicPriorityRule?

public interface IJasimaNewTracker {

	// TODO I could probably add something in here that deals with adding decisions.

	public void setProblem(JasimaGPProblem problem);

	public Pair<Individual, List<JasimaDispatchingDecision>> getResults();

	public void clear();

}
