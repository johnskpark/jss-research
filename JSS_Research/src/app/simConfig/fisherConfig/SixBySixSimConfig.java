package app.simConfig.fisherConfig;

import app.simConfig.StaticSimConfig;
import jasima.core.experiment.Experiment;
import jasima.shopSim.models.staticShop.StaticShopExperiment;

public class SixBySixSimConfig extends StaticSimConfig {

	public Experiment getExperiment() {
		StaticShopExperiment e = new StaticShopExperiment();
		e.setInstFileName("js06x06.txt");
		return e;
	}

}
