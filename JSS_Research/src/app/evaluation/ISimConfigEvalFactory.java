package app.evaluation;

import java.io.IOException;

import org.w3c.dom.Element;

import app.simConfig.SimConfigFactory;

public interface ISimConfigEvalFactory extends SimConfigFactory {

	public void loadConfig(Element doc) throws IOException;

}
