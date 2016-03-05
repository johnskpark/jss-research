package app.simConfig.hildebrandtConfig;

import java.io.IOException;

import org.w3c.dom.Element;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.SimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	@Override
	public SimConfig generateSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public void loadConfig(Element doc) throws IOException {
		// TODO Auto-generated method stub

	}

}
