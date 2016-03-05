package app.simConfig.taillardConfig;

import java.io.IOException;

import org.w3c.dom.Element;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.SimConfig;
import app.simConfig.StaticSimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	private StaticSimConfig simConfig;

	@Override
	public void loadConfig(Element doc) throws IOException {
		// No setup required.
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (simConfig == null) {
			simConfig = new TaillardSimConfig();
		}
		return simConfig;
	}

}
