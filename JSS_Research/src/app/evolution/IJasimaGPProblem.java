package app.evolution;

import app.simConfig.AbsSimConfig;

public interface IJasimaGPProblem {

	public AbsSimConfig getSimConfig(); // TODO this is definitely required.

	public int getNumInds();

}
