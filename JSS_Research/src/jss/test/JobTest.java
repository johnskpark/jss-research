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
		double[] dueDates = new double[]{7, 8};
		double[] penalties = new double[]{1.1, 1.0};

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		job.setReleaseTime(releaseTime);

		Assert.assertEquals(job.getReleaseTime(), releaseTime, EPSILON);

		job.setProcessingTime(mockMachine1, processingTimes[0]);
		job.setProcessingTime(mockMachine2, processingTimes[1]);

		Assert.assertEquals(job.getProcessingTime(mockMachine1), processingTimes[0], EPSILON);
		Assert.assertEquals(job.getProcessingTime(mockMachine2), processingTimes[1], EPSILON);

		job.setSetupTime(mockMachine1, setupTimes[0]);
		job.setSetupTime(mockMachine2, setupTimes[1]);

		Assert.assertEquals(job.getSetupTime(mockMachine1), setupTimes[0], EPSILON);
		Assert.assertEquals(job.getSetupTime(mockMachine2), setupTimes[1], EPSILON);

		job.setDueDate(mockMachine1, dueDates[0]);
		job.setDueDate(mockMachine2, dueDates[1]);

		Assert.assertEquals(job.getDueDate(mockMachine1), dueDates[0], EPSILON);
		Assert.assertEquals(job.getDueDate(mockMachine2), dueDates[1], EPSILON);

		job.setPenalty(mockMachine1, penalties[0]);
		job.setPenalty(mockMachine2, penalties[1]);

		Assert.assertEquals(job.getPenalty(mockMachine1), penalties[0], EPSILON);
		Assert.assertEquals(job.getPenalty(mockMachine2), penalties[1], EPSILON);
	}

	@Test
	public void staticJobTest_VisitMachine() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		Assert.assertEquals(job.getNextMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
		Assert.assertFalse(job.isCompleted());

		job.processedOnMachine(mockMachine1);

		Assert.assertEquals(job.getNextMachine(), mockMachine2);
		Assert.assertFalse(mockMachine1.equals(job.getNextMachine()));
		Assert.assertTrue(mockMachine2.equals(job.getNextMachine()));
		Assert.assertFalse(job.isCompleted());

		job.processedOnMachine(mockMachine2);

		Assert.assertNull(job.getNextMachine());
		Assert.assertFalse(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertTrue(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
		Assert.assertFalse(job.isCompleted());
	}

	@Test
	public void staticJobTest_Reset() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		Assert.assertEquals(job.getNextMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
		Assert.assertFalse(job.isCompleted());

		job.processedOnMachine(mockMachine1);
		job.processedOnMachine(mockMachine2);

		Assert.assertNull(job.getNextMachine());
		Assert.assertFalse(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertEquals(job.getNextMachine(), mockMachine1);
		Assert.assertTrue(mockMachine1.equals(job.getNextMachine()));
		Assert.assertFalse(mockMachine2.equals(job.getNextMachine()));
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
	public void staticJobTestFail_SetDueDate() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class);

		double dueDate = 7;

		try {
			job.setDueDate(mockMachine1, dueDate);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void staticJobTestFail_SetPenalty() {
		StaticJob job = new StaticJob();

		mockMachine1 = context.mock(IMachine.class);

		double penalty = 1.1;

		try {
			job.setDueDate(mockMachine1, penalty);
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
			job.processedOnMachine(machine2);

			Assert.fail();
		} catch (RuntimeException e) {
		}
	}
}
