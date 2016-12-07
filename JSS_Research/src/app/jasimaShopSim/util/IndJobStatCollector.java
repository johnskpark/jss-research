package app.jasimaShopSim.util;

import java.util.HashMap;
import java.util.Map;

import jasima.core.simulation.Simulation;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.util.ShopListenerBase;

public class IndJobStatCollector extends ShopListenerBase {

	private static final long serialVersionUID = -4011992602302111428L;
 
	private Map<Integer, Double> releaseDate;
	private Map<Integer, Double> completionTime;
	private Map<Integer, Double> flowtime;
	private Map<Integer, Double> tardiness;

	@Override
	protected void init(Simulation sim) {
		releaseDate = new HashMap<Integer, Double>();
		completionTime = new HashMap<Integer, Double>();
		flowtime = new HashMap<Integer, Double>();
		tardiness = new HashMap<Integer, Double>();
	}

	@Override
	protected void jobFinished(JobShop shop, Job j) {
		if (!shouldCollect(j))
			return;

		int num = j.getJobNum();
		
		double relDate = j.getRelDate();
		double compTime = shop.simTime();
		releaseDate.put(num, relDate);
		completionTime.put(num, compTime);

		double ft = compTime - relDate;
		flowtime.put(num, ft);

		double late = compTime - j.getDueDate();
		double tard = Math.max(late, 0);
		tardiness.put(num, tard);
	}

	@Override
	public void produceResults(Simulation sim, Map<String, Object> res) {
		res.put("jobReleaseDate", releaseDate);
		res.put("jobCompletionTime", completionTime);
		res.put("jobFlowtime", flowtime);
		res.put("jobTardiness", tardiness);
	}

	@Override
	public String toString() {
		return "IndJobStatCollector";
	}

}
