package jss.test;

import jss.IProblemInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticInstance;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class ProblemInstanceTest {

	private IProblemInstance problem;

	@Test
	public void basicInstanceTest_GetterSetter() {
		problem = new StaticInstance();

		Assert.assertTrue(problem.getJobs().isEmpty());
		Assert.assertTrue(problem.getMachines().isEmpty());
		Assert.assertTrue(problem.getEventHandlers().isEmpty());
	}

	private class FakeBasicJob extends StaticJob {

	}
}
