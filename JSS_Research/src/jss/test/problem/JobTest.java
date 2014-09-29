package jss.test.problem;

import jss.IMachine;
import jss.evolution.sample.BasicJob;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Test;

public class JobTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private IMachine mockMachine1;
	private IMachine mockMachine2;

	@Test
	public void basicJobTest_GetterSetter() {
		BasicJob job = new BasicJob();

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
	public void basicJobTest_VisitMachine() {
		BasicJob job = new BasicJob();

		IMachine machine1 = context.mock(IMachine.class, "Machine1");
		IMachine machine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(machine1);
		job.offerMachine(machine2);

		Assert.assertEquals(job.getNextMachine(), machine1);
		Assert.assertTrue(job.isProcessable(machine1));
		Assert.assertFalse(job.isProcessable(machine2));
		Assert.assertFalse(job.isCompleted());

		job.visitMachine(machine1);

		Assert.assertEquals(job.getNextMachine(), machine2);
		Assert.assertFalse(job.isProcessable(machine1));
		Assert.assertTrue(job.isProcessable(machine2));
		Assert.assertFalse(job.isCompleted());

		job.visitMachine(machine2);

		Assert.assertNull(job.getNextMachine());
		Assert.assertFalse(job.isProcessable(machine1));
		Assert.assertFalse(job.isProcessable(machine2));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertTrue(job.isProcessable(machine1));
		Assert.assertFalse(job.isProcessable(machine2));
		Assert.assertFalse(job.isCompleted());
	}

	@Test
	public void basicJobTest_Reset() {
		BasicJob job = new BasicJob();

		mockMachine1 = context.mock(IMachine.class, "Machine1");
		mockMachine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(mockMachine1);
		job.offerMachine(mockMachine2);

		Assert.assertEquals(job.getNextMachine(), mockMachine1);
		Assert.assertTrue(job.isProcessable(mockMachine1));
		Assert.assertFalse(job.isProcessable(mockMachine2));
		Assert.assertFalse(job.isCompleted());

		job.visitMachine(mockMachine1);
		job.visitMachine(mockMachine2);

		Assert.assertNull(job.getNextMachine());
		Assert.assertFalse(job.isProcessable(mockMachine1));
		Assert.assertFalse(job.isProcessable(mockMachine2));
		Assert.assertTrue(job.isCompleted());

		job.reset();

		Assert.assertEquals(job.getNextMachine(), mockMachine1);
		Assert.assertTrue(job.isProcessable(mockMachine1));
		Assert.assertFalse(job.isProcessable(mockMachine2));
		Assert.assertFalse(job.isCompleted());
	}

	@Test
	public void basicJobTestFail_SetProcessingTime() {
		BasicJob job = new BasicJob();

		mockMachine1 = context.mock(IMachine.class);

		double processingTime = 5;

		try {
			job.setProcessingTime(mockMachine1, processingTime);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void basicJobTestFail_SetSetupTime() {
		BasicJob job = new BasicJob();

		mockMachine1 = context.mock(IMachine.class);

		double setupTime = 3;

		try {
			job.setSetupTime(mockMachine1, setupTime);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void basicJobTestFail_SetDueDate() {
		BasicJob job = new BasicJob();

		mockMachine1 = context.mock(IMachine.class);

		double dueDate = 7;

		try {
			job.setDueDate(mockMachine1, dueDate);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void basicJobTestFail_SetPenalty() {
		BasicJob job = new BasicJob();

		mockMachine1 = context.mock(IMachine.class);

		double penalty = 1.1;

		try {
			job.setDueDate(mockMachine1, penalty);
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void basicJobTestFail_VisitMachine() {
		BasicJob job = new BasicJob();

		IMachine machine1 = context.mock(IMachine.class, "Machine1");
		IMachine machine2 = context.mock(IMachine.class, "Machine2");

		job.offerMachine(machine1);
		job.offerMachine(machine2);

		try {
			job.visitMachine(machine2);

			Assert.fail();
		} catch (RuntimeException e) {
		}
	}
}
