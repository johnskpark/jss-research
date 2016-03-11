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
		System.out.println("Listener: loading Hunt et al.'s listener.");

		NodeList listenerNodeList = doc.getElementsByTagName(XML_MAX_SIZE);
		if (listenerNodeList.getLength() != 0) {
			maxSize = Integer.parseInt(listenerNodeList.item(0).getTextContent());
			System.out.println("Listener: maximum query size set for listener: " + maxSize);
		} else {
			throw new IOException("Listner: no maximum query size specified for the listener.");
		}

		System.out.println("Listener: Hunt et al. listener loading complete.");
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new HuntListener(maxSize);
		}
		return listener;
	}

}
