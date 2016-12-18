package app.evaluation;

import java.io.IOException;

import org.w3c.dom.Element;

import app.JasimaWorkStationListenerFactory;

public interface JasimaWorkStationListenerEvalFactory extends JasimaWorkStationListenerFactory {

	public void loadConfig(Element doc) throws IOException;

}
