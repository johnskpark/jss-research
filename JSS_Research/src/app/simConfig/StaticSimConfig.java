package app.simConfig;

public abstract class StaticSimConfig implements SimConfig {

	public abstract int getNumJobs(int index);

	public abstract String getInstFileName(int index);

}
