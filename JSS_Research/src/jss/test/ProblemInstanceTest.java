package jss.test;

import jss.IProblemInstance;
import jss.evolution.sample.BasicInstance;
import jss.evolution.sample.BasicJob;

import org.junit.Assert;
import org.junit.Test;

public class ProblemInstanceTest {

	private IProblemInstance problem;

	@Test
	public void basicInstanceTest_GetterSetter() {
		problem = new BasicInstance();

		Assert.assertTrue(problem.getJobs().isEmpty());
		Assert.assertTrue(problem.getMachines().isEmpty());
		Assert.assertTrue(problem.getEventHandlers().isEmpty());
	}

	private class FakeBasicJob extends BasicJob {

	}
}
