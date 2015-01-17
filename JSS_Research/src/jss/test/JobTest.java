package jss.test;

import jss.IMachine;
import jss.problem.static_problem.StaticJob;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class JobTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private IMachine mockMachine1;
	private IMachine mockMachine2;

	@Test
	public void staticJobTest_GetterSetter() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		double releaseTime = 2;
		double[] processingTimes = new double[]{5, 2};
		double[] setupTimes = new double[]{3, 4};
		double dueDates = 7;
		double penalties = 1.1;

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		job.setReadyTime(releaseTime);

		Assert.assertEquals(job.getReadyTime(), releaseTime, EPSILON);

		job.setProcessingTime(mockMachine1, processingTimes[0]);
		job.setProcessingTime(mockMachine2, processingTimes[1]);

		Assert.assertEquals(job.getProcessingTime(mockMachine1), processingTimes[0], EPSILON);
		Assert.assertEquals(job.getProcessingTime(mockMachine2), processingTimes[1], EPSILON);

		job.setSetupTime(mockMachine1, setupTimes[0]);
		job.setSetupTime(mockMachine2, setupTimes[1]);

		Assert.assertEquals(job.getSetupTime(mockMachine1), setupTimes[0], EPSILON);
		Assert.assertEquals(job.getSetupTime(mockMachine2), setupTimes[1], EPSILON);

		job.setDueDate(dueDates);

		Assert.assertEquals(job.getDueDate(), dueDates, EPSILON);

		job.setPenalty(penalties);

		Assert.assertEquals(job.getPenalty(), penalties, EPSILON);
	}

	@Test
	public void staticJobTest_VisitMachine() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		Assert.assertEquals(job.getCurrentMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertFalse(job.isCompleted());

		job.startedProcessingOnMachine(mockMachine1, 0);
		job.finishProcessingOnMachine();

		Assert.assertEquals(job.getCurrentMachine(), mockMachine2);
		Assert.assertFalse(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertTrue(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertFalse(job.isCompleted());

		job.startedProcessingOnMachine(mockMachine2, 0);
		job.finishProcessingOnMachine();

		Assert.assertNull(job.getCurrentMachine());
		Assert.assertFalse(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertTrue(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertFalse(job.isCompleted());
	}

	@Test
	public void staticJobTest_Reset() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		Assert.assertEquals(job.getCurrentMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertFalse(job.isCompleted());

		job.startedProcessingOnMachine(mockMachine1, 0);
		job.finishProcessingOnMachine();
		job.startedProcessingOnMachine(mockMachine2, 0);
		job.finishProcessingOnMachine();

		Assert.assertNull(job.getCurrentMachine());
		Assert.assertFalse(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertEquals(job.getCurrentMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getCurrentMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getCurrentMachine()));
		Assert.assertFalse(job.isCompleted());
	}

	@Test
	public void staticJobTestFail_SetProcessingTime() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class);

		double processingTime = 5;

		try {
			job.setProcessingTime(mockMachine1, processingTime);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void staticJobTestFail_SetSetupTime() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class);

		double setupTime = 3;

		try {
			job.setSetupTime(mockMachine1, setupTime);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void staticJobTestFail_VisitMachine() {
		StaticJob job = new StaticJob();

		IMachine machine1 = context.mock(IMachine.class, "Machine1");
		IMachine machine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(machine1);
		job.offerMachine(machine2);

		try {
			job.startedProcessingOnMachine(machine2, 0);

			Assert.fail();
		} catch (RuntimeException e) {
		}
	}
}
