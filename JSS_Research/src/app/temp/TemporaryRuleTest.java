package app.temp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.core.WorkStation.WorkStationEvent;
import jasima.shopSim.models.staticShop.StaticShopExperiment;
import jasima.shopSim.prioRules.basic.EDD;
import jasima.shopSim.prioRules.basic.SPT;
import jasima.shopSim.util.ExtendedJobStatCollector;

public class TemporaryRuleTest {

	public static void main(String[] args) {
		try {
			PR spt = new SPT();
			PR edd = new EDD();

			MachineListener sptL = new MachineListener("spt_output.txt");
			MachineListener eddL = new MachineListener("edd_output.txt");

			runExperiment(spt, sptL);
			runExperiment(edd, eddL);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void runExperiment(PR rule, MachineListener listener) throws IOException {
		StaticShopExperiment e = new StaticShopExperiment();

		e.setSequencingRule(rule);
		e.setShopListener(new NotifierListener[]{new ExtendedJobStatCollector()});
		e.addMachineListener(listener);
		e.setInstFileName("dataset/js06x06.txt");

		e.runExperiment();
		e.printResults();

		listener.printOutput(e.getResults());
	}

	private static class MachineListener implements NotifierListener<WorkStation, WorkStationEvent> {

		private String filename;
		private Map<Integer, Map<Integer, Operation>> opInProg = new HashMap<Integer, Map<Integer, Operation>>();

		private List<Operation> operations = new ArrayList<Operation>();

		public MachineListener(String name) {
			filename = name;
		}

		@Override
		public void update(WorkStation notifier, WorkStationEvent event) {
			if (event == WorkStation.WS_JOB_SELECTED) {
				operationStart(notifier);
			} else if (event == WorkStation.WS_JOB_COMPLETED){
				operationComplete(notifier);
			}
		}

		private void operationStart(WorkStation machine) {
			PrioRuleTarget entry = machine.justStarted;
			int j = entry.getJobNum();
			int m = machine.index();

			if (!opInProg.containsKey(j)) {
				opInProg.put(j, new HashMap<Integer, Operation>());
			}

			int id = entry.getTaskNumber();
			double arrivalTime = entry.getArriveTime();
			double startTime = entry.getShop().simTime();

			Operation op = new Operation();
			op.id = id;
			op.job = j;
			op.machine = m;
			op.startTime = startTime;
			op.dueDate = entry.getDueDate();
			op.weight = entry.getWeight();

			opInProg.get(j).put(id, op);
		}

		private void operationComplete(WorkStation machine) {
			PrioRuleTarget entry = machine.justCompleted;
			int id = entry.getTaskNumber();
			int job = entry.getJobNum();
			int m = machine.index();

			Operation op = opInProg.get(job).get(id);
			op.endTime = entry.getShop().simTime();

			operations.add(op);
		}

		public void printOutput(Map<String, Object> results) throws IOException {
			System.out.println("operations: " + operations.size());

			Collections.sort(operations, new Comparator<Operation>() {
				public int compare(Operation o1, Operation o2) {
					int diff = o1.job - o2.job;
					if (diff != 0) {
						return diff;
					}
					return o1.id - o2.id;
				}
			});

			PrintStream output = new PrintStream(new File(filename));

			output.println("job,op,machine,startTime,endTime,dueDate,weight");

			for (Operation op : operations) {
				output.printf("%d,%d,%d,%f,%f,%f,%f\n", op.job, op.id, op.machine, op.startTime, op.endTime, op.dueDate, op.weight);
			}

			output.close();
		}

	}

	private static class Operation {
		int id;
		int job;
		int machine;
		double startTime;
		double endTime;
		double dueDate;
		double weight;
	}

}
