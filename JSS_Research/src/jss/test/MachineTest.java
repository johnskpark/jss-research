package jss.test;

import jss.IJob;
import jss.IMachine;
import jss.evolution.sample.BasicMachine;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Test;

public class MachineTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private IMachine machine;
	private IJob mockJob1;
	private IJob mockJob2;

	private double releaseTime;
	private double processingTime;
	private double setupTime;

	@Test
	public void basicMachineTest_ProcessJob() {
		machine = new BasicMachine();
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		double completionTime = releaseTime + setupTime + processingTime;

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getTimeAvailable(), 0, EPSILON);

		context.checking(new Expectations() {{
			oneOf(mockJob1).visitMachine(machine);
			oneOf(mockJob1).getReleaseTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
		}});

		machine.processJob(mockJob1);

		Assert.assertEquals(machine.getCurrentJob(), mockJob1);
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertFalse(machine.isAvailable());
		Assert.assertEquals(machine.getTimeAvailable(), completionTime, EPSILON);
	}

	@Test
	public void basicMachineTest_FinishJob() {
		machine = new BasicMachine();
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		double completionTime = releaseTime + setupTime + processingTime;

		context.checking(new Expectations() {{
			oneOf(mockJob1).visitMachine(machine);
			oneOf(mockJob1).getReleaseTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
		}});

		machine.processJob(mockJob1);
		machine.updateStatus(completionTime);

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().size() == 1);
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getTimeAvailable(), completionTime, EPSILON);

		Assert.assertEquals(machine.getProcessedJobs().get(0), mockJob1);
	}

	@Test
	public void basicMachineTest_Reset() {
		machine = new BasicMachine();
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		double completionTime = releaseTime + setupTime + processingTime;

		context.checking(new Expectations() {{
			oneOf(mockJob1).visitMachine(machine);
			oneOf(mockJob1).getReleaseTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
		}});

		machine.processJob(mockJob1);
		machine.updateStatus(completionTime);

		machine.reset();

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getTimeAvailable(), 0, EPSILON);
	}

	@Test
	public void basicMachineTestFail_ProcessJob() {
		machine = new BasicMachine();
		mockJob1 = context.mock(IJob.class, "Job1");
		mockJob2 = context.mock(IJob.class, "Job2");

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		context.checking(new Expectations() {{
			oneOf(mockJob1).visitMachine(machine);
			oneOf(mockJob1).getReleaseTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
		}});

		machine.processJob(mockJob1);

		try {
			machine.processJob(mockJob2);
			Assert.fail();
		} catch (RuntimeException e) {
		}

	}

}
