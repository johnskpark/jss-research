package test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import jasima.core.util.observer.NotifierListener;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;
import jasima.shopSim.core.WorkStation;
import jasima.shopSim.models.dynamicShop.DynamicShopExperiment;
import jasima.shopSim.util.BasicJobStatCollector;
import jasima.shopSim.util.WorkStationListenerBase;

public class JasimaTrackingTest {

	public static void main(String[] args) {
		Random rand = new Random(15);

		// Okay, apparently the sequencing rules are cloned across all the machines on the shop floor.
		// The best way to do this is to share a class that stores all of the relevant information... Or
		// What I can do is to have a class that gets the values of the relevant terminals, along with the job
		// and the workstation.

		// Now the tricky part is to figure out how to do this part autonomously, while incorporating
		// existing code. How would I do this cleanly? So if it's job related, then its fine.
		// Otherwise, if machine or shop floor related, then you're fucked.
		// So when I read these in as decision scenarios, then how do I interpret them properly?

		DynamicShopExperiment e = new DynamicShopExperiment();
		TrackedSPT spt = new TrackedSPT();

		e.setNumMachines(10);
		e.setInitialSeed(rand.nextLong());
		e.setShopListener(new NotifierListener[]{new BasicJobStatCollector()}); // The basic job stat collector collects informations about the experiment.
		e.setSequencingRule(spt);

//		ResultSaver saver = new ResultSaver();
//		saver.setSaveSubExperiments(true);
//		e.addNotifierListener(saver);

		e.runExperiment();
		e.printResults();

		// SummaryStat stat = (SummaryStat) e.getResults().get("tardiness");

		// Testing job's operation details.
		List<Job> jobs = spt.getJobs();
		WorkStation machine = spt.getMachine();

		List<Integer> expectedTaskNum = spt.getTaskNums();
		List<Integer> actualTaskNum = jobs.stream().map(x -> x.getTaskNumber()).collect(Collectors.toList());

		for (int i = 0; i < expectedTaskNum.size(); i++) {
			int expected = expectedTaskNum.get(i);
			int actual = actualTaskNum.get(i);

			if (expected != actual) {
				throw new RuntimeException("Mismatch of job's task number at job " + i + ", expected: " + expected + ", actual: " + actual);
			}
		}

		// Testing job's machine order.
		List<Integer[]> expectedMachines = spt.getMachines();
		List<Integer[]> actualMachines = jobs.stream().map(x -> getMachineIndices(x)).collect(Collectors.toList());

		for (int i = 0; i < expectedMachines.size(); i++) {
			Integer[] expected = expectedMachines.get(i);
			Integer[] actual = actualMachines.get(i);

			if (expected.length != actual.length) {
				throw new RuntimeException("Mismatch of job's number of machines at job " + i + ", expected: " + expected + ", actual: " + actual);
			}

			for (int j = 0; j < expected.length; j++) {
				if (expected[j] != actual[j]) {
					throw new RuntimeException("Mismatch of job's machine at job " + i + ", expected: " + expected + ", actual: " + actual);
				}
			}
		}

		//
		PriorityQueue<Job> newQ = new PriorityQueue<Job>(machine);
		for (Job j : jobs) {
			newQ.add(j);
		}
		spt.beforeCalc(newQ);

		List<Double> expectedPriorities = spt.getPriorities();
		List<Double> actualPriorities = jobs.stream().map(x -> spt.calcPrio(x)).collect(Collectors.toList());

		for (int i = 0; i < expectedPriorities.size(); i++) {
			double expected = expectedPriorities.get(i);
			double actual = actualPriorities.get(i);

			if (expected != actual) {
				throw new RuntimeException("Mismatch of job's priority at job " + i + ", expected: " + expected + ", actual: " + actual);
			}
		}

		// So this fails.
		double expectedSimTime = spt.getSimTime();
		double actualSimTime = jobs.get(0).getShop().simTime();

		if (expectedSimTime != actualSimTime) {
			throw new RuntimeException("Mismatch of sim time, expected: " + expectedSimTime + ", actual: " + actualSimTime);
		}
	}

	private static Integer[] getMachineIndices(Job j) {
		Integer[] m = new Integer[j.numOpsLeft()];

		for (int i = j.getTaskNumber(); i < j.numOps(); i++) {
			m[i-j.getTaskNumber()] = j.getOps()[i].machine.index();
		}

		return m;
	}

	private static class MachineListener extends WorkStationListenerBase {

		@Override
		protected void operationStarted(WorkStation m,
				PrioRuleTarget justStarted,
				int oldSetupState,
				int newSetupState,
				double setupTime) {
			// TODO
		}

	}

}
