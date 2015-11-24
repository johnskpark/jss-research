package app.listener.hunt;

import org.w3c.dom.Document;

import app.IWorkStationListener;
import app.evaluation.IWorkStationListenerEvalFactory;

public class HuntListenerEvalFactory implements IWorkStationListenerEvalFactory {

	// FIXME remove this later down the line.
	public static final int DEFAULT_MAX_SIZE = 5;

	private int maxSize = DEFAULT_MAX_SIZE;

	@Override
	public void loadConfig(Document doc) {
		// FIXME Does nothing for now, but allow it to
		// load the max size later down the line.
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		return new HuntListener(maxSize);
	}

}
