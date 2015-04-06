package app.evolution.ensemble.tracker;

import jasima.core.statistics.SummaryStat;
import app.evolution.IJasimaGPProblem;
import app.evolution.ensemble.IJasimaEnsembleTracker;

public abstract class AbsEnsembleTracker implements IJasimaEnsembleTracker {

	private static final int SAMPLE_SIZE = 2000;

	private IJasimaGPProblem problem;

	protected int getNumIgnore() {
		return problem.getSimConfig().getNumIgnore();
	}

	protected boolean shouldSample(int jobFinished) {
		return (jobFinished >= problem.getSimConfig().getNumIgnore()) &&
				(jobFinished < problem.getSimConfig().getNumIgnore() + getSampleSize());
	}

	protected int getSampleSize() {
		return SAMPLE_SIZE;
	}

	protected IJasimaGPProblem getProblem() {
		return problem;
	}

	@Override
	public void setProblem(IJasimaGPProblem problem) {
		this.problem = problem;
	}

	@Override
	public double getDiversityMeasure(SummaryStat stat) {
		return stat.mean();
	}

}
