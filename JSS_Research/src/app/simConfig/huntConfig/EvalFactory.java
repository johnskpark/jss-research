package app.simConfig.huntConfig;

import org.w3c.dom.Document;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.DynamicSimConfig;
import app.simConfig.SimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	private DynamicSimConfig simConfig = null;
	private long initialSeed;

	@Override
	public void loadConfig(Document doc) {
		// TODO need to implement.
	}

	@Override
	public SimConfig generateSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
