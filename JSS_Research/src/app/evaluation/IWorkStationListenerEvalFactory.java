package app.evaluation;

import org.w3c.dom.Document;

import app.IWorkStationListenerFactory;

public interface IWorkStationListenerEvalFactory extends IWorkStationListenerFactory {

	public void loadConfig(Document doc);

}
