package app.evaluation;

import org.w3c.dom.Document;

import app.simConfig.SimConfigFactory;

public interface ISimConfigEvalFactory extends SimConfigFactory {

	public void loadConfig(Document doc);

}
