package app.listener.breakdown;

import java.io.IOException;

import org.w3c.dom.Element;

import app.IWorkStationListener;
import app.evaluation.IWorkStationListenerEvalFactory;

public class EvalFactory implements IWorkStationListenerEvalFactory {

	private BreakdownListener listener = null;

	@Override
	public void loadConfig(Element doc) throws IOException {
		System.out.println("Listener: loading Hunt et al.'s listener.");

		// No setup required.

		System.out.println("Listener: Hunt et al. listener loading complete.");
	}

	@Override
	public IWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new BreakdownListener();
		}
		return listener;
	}

}
