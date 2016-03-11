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
		System.out.println("SimConfig: loading Taillard dataset.");

		// No setup required.

		System.out.println("SimConfig: Taillard dataset loading complete.");
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
