package app.evolution.ensemble.priorityRules;

import app.evolution.AbsGPPriorityRule;
import app.evolution.JasimaGPConfig;
import app.evolution.JasimaGPData;
import app.evolution.ensemble.IJasimaEnsembleTracker;
import app.evolution.ensemble.EnsembleTrackerValue;
import ec.EvolutionState;
import ec.gp.GPIndividual;

public abstract class AbsGPEnsemblePriorityRule extends AbsGPPriorityRule {

	private static final long serialVersionUID = -5421055412842763721L;

	protected EvolutionState state;
	protected GPIndividual[] individuals;
	protected int threadnum;

	protected JasimaGPData data;
	protected IJasimaEnsembleTracker tracker;

	protected EnsembleTrackerValue trackerValue = new EnsembleTrackerValue();

	@Override
	public void setConfiguration(JasimaGPConfig config) {
		state = config.getState();
		individuals = config.getIndividuals();
		threadnum = config.getThreadnum();
		data = config.getData();
		tracker = (IJasimaEnsembleTracker) config.getTracker();
	}

	protected void setTrackerValue(EnsembleTrackerValue trackerValue) {
		this.trackerValue = trackerValue;
	}

	// TODO do the priority calculation here.


}
