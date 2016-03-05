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
		NodeList datasetInstNodeList = doc.getElementsByTagName(XML_DATASET_INSTANCES);
		if (datasetInstNodeList.getLength() != 0) {
			String instances = datasetInstNodeList.item(0).getTextContent();

			simConfig = HuntSimConfigGenerator.getSimConfig(instances);
			if (simConfig == null) {
				new IOException("Unrecognised instances for the simulator. " + instances);
			}
		}

		NodeList datasetSeedNodeList = doc.getElementsByTagName(XML_DATASET_SEED);
		if (datasetSeedNodeList.getLength() != 0) {
			initialSeed = Long.parseLong(datasetSeedNodeList.item(0).getTextContent());
		} else {
			throw new IOException("Initial seed for the dataset must be provided.");
		}
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
