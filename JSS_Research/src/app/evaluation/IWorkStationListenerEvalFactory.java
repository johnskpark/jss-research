package app.evaluation;

import org.w3c.dom.Document;

import app.listener.IWorkStationListenerFactory;

public interface IWorkStationListenerEvalFactory extends IWorkStationListenerFactory {

	public void loadConfig(Document doc);

}