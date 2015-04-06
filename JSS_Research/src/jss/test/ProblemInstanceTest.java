package jss.test;

import jss.IMachine;
import jss.problem.static_problem.StaticInstance;
import jss.problem.static_problem.StaticJob;
import jss.problem.static_problem.StaticMachine;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class ProblemInstanceTest {

	private StaticInstance problem;
	private StaticMachine[] machines;
	private StaticJob[] jobs;

	@Test
	public void staticInstanceTest_Initialisation() {
		problem = new StaticInstance();

		Assert.assertTrue(problem.getJobs().isEmpty());
		Assert.assertTrue(problem.getMachines().isEmpty());
		Assert.assertTrue(problem.getEventHandlers().isEmpty());
	}

	@Test
	public void staticInstanceTest_AddMachines() {
		problem = new StaticInstance();

		machines = new StaticMachine[]{new StaticMachine(0, null),
				new StaticMachine(1, null)};

		for (StaticMachine machine : machines) {
			problem.addMachine(machine);
		}

		Assert.assertTrue(problem.getJobs().isEmpty());
		Assert.assertEquals(2, problem.getMachines().size());
		Assert.assertEquals(2, problem.getEventHandlers().size());

		for (IMachine machine : problem.getMachines()) {
			Assert.assertEquals(machines[machine.getId()], machine);
		}
	}

	@Test
	public void staticInstanceTest_AddJobs() {
		problem = new StaticInstance();

		jobs = new StaticJob[]{new StaticJob(0),
				new StaticJob(1)};

		for (StaticJob job : jobs) {
			job.offerMachine(new StaticMachine(0, null));
			problem.addJob(job);
		}

		Assert.assertEquals(2, problem.getJobs().size());
		Assert.assertTrue(problem.getMachines().isEmpty());
		Assert.assertEquals(2, problem.getEventHandlers().size());

		for (int i = 0; i < problem.getJobs().size(); i++) {
			Assert.assertEquals(jobs[i], problem.getJobs().get(i));
		}
	}


}
