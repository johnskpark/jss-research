package app.tracker.sampler.breakdown;

import java.util.List;

import app.TrackedRuleBase;
import app.node.INode;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;
import app.tracker.DecisionEvent;
import app.tracker.JasimaExperimentTracker;
import app.tracker.sampler.SamplingPR;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
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
//		if (isWithinMeanTime(q) && numJobsInterrupted(q) != 0) {
//			System.out.printf("Num jobs: %d, num jobs interrupted: %d\n", q.size(), numJobsInterrupted(q));
//		}

		return isWithinMeanTime(q) && numJobsInterrupted(q) != 00;
	}

	private boolean isWithinMeanTime(PriorityQueue<?> q) {
		WorkStation machine = q.getWorkStation();

		double time = machine.shop().simTime();
		double deactivateTime = getDeactivateTime(machine);

		return (time + currentPT) >= deactivateTime;
	}

	private int numJobsInterrupted(PriorityQueue<?> q) {
		WorkStation machine = q.getWorkStation();

		double time = machine.shop().simTime();
		double deactivateTime = getDeactivateTime(machine);

		int numInterrupted = 0;
		for (int i = 0; i < q.size(); i++) {
			if (time + q.get(i).currProcTime() >= deactivateTime) {
				numInterrupted++;
			}
		}

		return numInterrupted;
	}

	protected DowntimeSource getDowntimeSource(WorkStation machine) {
		IndividualMachine indMachine = machine.machDat()[0];

		List<DowntimeSource> srcs = indMachine.getDowntimeSources();
		if (srcs != null && !srcs.isEmpty()) {
			return srcs.get(0);
		} else {
			return null;
		}
	}

	// Bunch of useful getters.

	protected double getDeactivateTime(WorkStation machine) {
		DowntimeSource downSrc = getDowntimeSource(machine);
		if (downSrc == null) {
			// Returns that machine will breakdown at time infinity.
			return Double.POSITIVE_INFINITY;
		}

		return downSrc.getDeactivateTime();
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
