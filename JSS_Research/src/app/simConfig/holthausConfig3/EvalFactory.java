package app.simConfig.holthausConfig3;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.evaluation.ISimConfigEvalFactory;
import app.simConfig.DynamicBreakdownSimConfig;
import app.simConfig.SimConfig;

public class EvalFactory implements ISimConfigEvalFactory {

	public static final String XML_DATASET_INSTANCES = "datasetInstances";
	public static final String XML_DATASET_SEED = "datasetSeed";

	private DynamicBreakdownSimConfig simConfig = null;
	private long initialSeed;
	private boolean initialSeedSet = false;

	@Override
	public void loadConfig(Element doc) throws IOException {
		System.out.println("SimConfig: loading modified Holthaus simulator.");

		NodeList datasetInstNodeList = doc.getElementsByTagName(XML_DATASET_INSTANCES);
		if (datasetInstNodeList.getLength() != 0) {
			String instances = datasetInstNodeList.item(0).getTextContent();

			simConfig = Holthaus3SimConfigGenerator.getSimConfig(instances);
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

		System.out.println("SimConfig: modified Holthaus simulator loading complete.");
	}

	@Override
	public boolean rotatesSeed() {
		return false;
	}

	@Override
	public SimConfig generateSimConfig() {
		if (!initialSeedSet) {
			simConfig.setJobSeed(initialSeed);
			simConfig.setMachineSeed(initialSeed);
			initialSeedSet = true;
		}

		return simConfig;
	}

}
