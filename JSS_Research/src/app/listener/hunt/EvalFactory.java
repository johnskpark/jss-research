package app.listener.hunt;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.IWorkStationListener;
import app.evaluation.IWorkStationListenerEvalFactory;

public class EvalFactory implements IWorkStationListenerEvalFactory {

	public static final String XML_MAX_SIZE = "listenerSize";

	private HuntListener listener = null;
	private int maxSize;

	@Override
	public void loadConfig(Element doc) throws IOException {
		NodeList listenerNodeList = doc.getElementsByTagName(XML_MAX_SIZE);
		if (listenerNodeList.getLength() != 0) {
			maxSize = Integer.parseInt(listenerNodeList.item(0).getTextContent());
		} else {
			throw new IOException("No maximum size specified for the listener.");
		}
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new HuntListener(maxSize);
		}
		return listener;
	}

}
