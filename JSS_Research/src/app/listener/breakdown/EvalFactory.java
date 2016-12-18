package app.listener.breakdown;

import java.io.IOException;

import org.w3c.dom.Element;

import app.JasimaWorkStationListener;
import app.evaluation.JasimaWorkStationListenerEvalFactory;

public class EvalFactory implements JasimaWorkStationListenerEvalFactory {

	private BreakdownListener listener = null;

	@Override
	public void loadConfig(Element doc) throws IOException {
		System.out.println("Listener: loading Hunt et al.'s listener.");

		// No setup required.

		System.out.println("Listener: Hunt et al. listener loading complete.");
	}

	@Override
	public JasimaWorkStationListener generateWorkStationListener() {
		if (listener == null) {
			listener = new BreakdownListener();
		}
		return listener;
	}

}
