package app.simConfig.holthausConfig;

import java.io.IOException;

import org.w3c.dom.Element;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.SimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	@Override
	public void loadConfig(Element doc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean rotatesSeed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		// TODO Auto-generated method stub
		return null;
	}

}
