package app.evolution.ensemble.priorityRules;

public class MVPSPriorityRule extends MajorityVotingPriorityRule {

	private static final long serialVersionUID = -264234337172951194L;

	public MVPSPriorityRule() {
		super();

		AbsGPEnsemblePriorityRule tieBreaker = new PrioritySumVotingPriorityRule();
		tieBreaker.setTrackerValue(trackerValue); // Share the tracker value.

		setTieBreaker(tieBreaker);
	}

}
