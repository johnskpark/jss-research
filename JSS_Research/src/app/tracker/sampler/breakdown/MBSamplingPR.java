package app.tracker.sampler.breakdown;

import app.TrackedRuleBase;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.DecisionEvent;
import app.tracker.JasimaExperimentTracker;
import app.tracker.sampler.SamplingPR;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;

public class MBSamplingPR extends SamplingPR {

	private static final long serialVersionUID = 8494043176020775929L;

	private double currentPT;

	public MBSamplingPR(PR refRule, long s, JasimaExperimentTracker<?> t) {
		super(refRule, s, t);
	}

	@Override
	public void initRecordingRun(SimConfig config, int configIndex) {
		if (!(config instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("The experiment must be of DynamicBreakdownSimConfig.");
		}

		super.initRecordingRun(config, configIndex);

		DynamicBreakdownSimConfig breakdownConfig = (DynamicBreakdownSimConfig) config;
		currentPT = breakdownConfig.getProcTime(configIndex).getNumericalMean();
	}

	@Override
	public void initTrackedRun(SimConfig config, int configIndex) {
		if (!(config instanceof DynamicBreakdownSimConfig)) {
			throw new RuntimeException("The experiment must be of DynamicBreakdownSimConfig.");
		}

		super.initTrackedRun(config, configIndex);

		DynamicBreakdownSimConfig breakdownConfig = (DynamicBreakdownSimConfig) config;
		currentPT = breakdownConfig.getProcTime(configIndex).getNumericalMean();
	}

	@Override
	protected boolean isDecisionSampled(PriorityQueue<?> q) {
		return isWithinMeanTime(q) && isJobsInterrupted(q);
	}

	private boolean isWithinMeanTime(PriorityQueue<?> q) {
		WorkStation machine = q.getWorkStation();

		double time = machine.shop().simTime();
		double deactivateTime = machine.machDat()[0].downReason.getDeactivateTime();

		return (time + currentPT) >= deactivateTime;
	}

	private boolean isJobsInterrupted(PriorityQueue<?> q) {
		WorkStation machine = q.getWorkStation();

		double time = machine.shop().simTime();
		double deactivateTime = machine.machDat()[0].downReason.getDeactivateTime();

		int numInterrupted = 0;
		for (int i = 0; i < q.size(); i++) {
			if (time + q.get(i).currProcTime() >= deactivateTime) {
				numInterrupted++;
			}
		}

		return numInterrupted != 0;
	}

	@Override
	protected void sampleDecision(PriorityQueue<?> q) {
		DecisionEvent event = getDecisionEvent(q.get(0));

		if (isSampleRun()) {
			// Record this particular decision situation.
			getCurrentRecording().add(event);
		} else {
			 // Determine whether this belongs to one of the sampled events.
			if (getCurrentSample().contains(event)) {
				// Run it over the different rules.
				getTracker().addDispatchingDecision(q);

				for (TrackedRuleBase<INode> pr : getPriorityRules()) {
					prPriorityCalculation(pr, q);
				}
			}
		}
	}

}
