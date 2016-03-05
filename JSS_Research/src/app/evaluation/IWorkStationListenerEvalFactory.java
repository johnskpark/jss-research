package app.evaluation;

import java.io.IOException;

import org.w3c.dom.Element;

import app.IWorkStationListenerFactory;

public interface IWorkStationListenerEvalFactory extends IWorkStationListenerFactory {

	public void loadConfig(Element doc) throws IOException;

}
