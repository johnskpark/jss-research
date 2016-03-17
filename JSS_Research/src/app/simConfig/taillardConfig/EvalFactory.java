package app.simConfig.taillardConfig;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.SimConfig;
import app.simConfig.StaticSimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	public static final String XML_DATASET_INSTANCES = "datasetInstances";
	
	private StaticSimConfig simConfig;

	@Override
	public void loadConfig(Element doc) throws IOException {
		System.out.println("SimConfig: loading Taillard dataset.");

		NodeList datasetInstNodeList = doc.getElementsByTagName(XML_DATASET_INSTANCES);
		if (datasetInstNodeList.getLength() != 0) {
			String instances = datasetInstNodeList.item(0).getTextContent();

			simConfig = TaillardSimConfigGenerator.getSimConfig(instances);
			if (simConfig != null) {
				System.out.println("SimConfig: configuration loaded for simulator: " + simConfig.getClass().getSimpleName());
			} else {
				throw new IOException("SimConfig: unrecognised configuration for the simulator. " + instances);
			}
		} else {
			throw new IOException("SimConfig: no instances specified for the simulator.");
		}

		System.out.println("SimConfig: Taillard dataset loading complete.");
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		return simConfig;
	}

}
