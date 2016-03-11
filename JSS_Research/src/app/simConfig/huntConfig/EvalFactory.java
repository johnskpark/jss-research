package app.simConfig.huntConfig;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.DynamicSimConfig;
import app.simConfig.SimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	public static final String XML_DATASET_INSTANCES = "datasetInstances";
	public static final String XML_DATASET_SEED = "datasetSeed";

	private DynamicSimConfig simConfig = null;
	private long initialSeed;
	private boolean initialSeedSet = false;

	@Override
	public void loadConfig(Element doc) throws IOException {
		System.out.println("SimConfig: loading Hunt et al. simulator.");

		NodeList datasetInstNodeList = doc.getElementsByTagName(XML_DATASET_INSTANCES);
		if (datasetInstNodeList.getLength() != 0) {
			String instances = datasetInstNodeList.item(0).getTextContent();

			simConfig = HuntSimConfigGenerator.getSimConfig(instances);
			if (simConfig != null) {
				System.out.println("SimConfig: configuration loaded for simulator: " + simConfig.getClass().getSimpleName());
			} else {
				throw new IOException("SimConfig: unrecognised configuration for the simulator. " + instances);
			}
		} else {
			throw new IOException("SimConfig: no instances specified for the simulator.");
		}

		NodeList datasetSeedNodeList = doc.getElementsByTagName(XML_DATASET_SEED);
		if (datasetSeedNodeList.getLength() != 0) {
			initialSeed = Long.parseLong(datasetSeedNodeList.item(0).getTextContent());
			System.out.println("SimConfig: initial seed set for simulator: " + initialSeed);
		} else {
			throw new IOException("SimConfig: initial seed for the dataset must be provided.");
		}

		System.out.println("SimConfig: Hunt et al. simulator loading complete.");
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (!initialSeedSet) {
			simConfig.setSeed(initialSeed);
			initialSeedSet = true;
		}

		return simConfig;
	}

}
