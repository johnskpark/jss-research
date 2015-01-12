package jss.test;

import jss.IJob;
import jss.IMachine;
import jss.ISubscriptionHandler;
import jss.problem.static_problem.StaticMachine;

import org.jmock.Expectations;
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
public class MachineTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private ISubscriptionHandler mockHandler;
	private IMachine machine;
	private IJob mockJob1;
	private IJob mockJob2;

	private double releaseTime;
	private double processingTime;
	private double setupTime;

	@Test
	public void staticMachineTest_ProcessJob() {
		mockHandler = context.mock(ISubscriptionHandler.class);
		machine = new StaticMachine(mockHandler);
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		final double completionTime = releaseTime + setupTime + processingTime;

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getReadyTime(), 0, EPSILON);

		context.checking(new Expectations() {{
			oneOf(mockJob1).startedProcessingOnMachine(machine);
			oneOf(mockJob1).getReadyTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
			oneOf(mockJob1).finishProcessingOnMachine();
		}});

		machine.processJob(mockJob1, 0);

		Assert.assertEquals(machine.getCurrentJob(), mockJob1);
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertFalse(machine.isAvailable());
		Assert.assertEquals(machine.getReadyTime(), completionTime, EPSILON);
	}

	@Test
	public void staticMachineTest_FinishJob() {
		mockHandler = context.mock(ISubscriptionHandler.class);
		machine = new StaticMachine(mockHandler);
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		final double completionTime = releaseTime + setupTime + processingTime;

		context.checking(new Expectations() {{
			oneOf(mockJob1).startedProcessingOnMachine(machine);
			oneOf(mockJob1).getCurrentMachine(); will(returnValue(null));
			oneOf(mockJob1).getReadyTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
			oneOf(mockJob1).finishProcessingOnMachine();
		}});

		machine.processJob(mockJob1, 0);

		context.checking(new Expectations() {{
			oneOf(mockHandler).sendMachineFeed(machine, completionTime);
		}});

		machine.updateStatus(completionTime);

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().size() == 1);
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getReadyTime(), completionTime, EPSILON);

		Assert.assertEquals(machine.getProcessedJobs().get(0), mockJob1);
	}

	// TODO need test for processing two jobs sequentially

	@Test
	public void staticMachineTest_Reset() {
		mockHandler = context.mock(ISubscriptionHandler.class);
		machine = new StaticMachine(mockHandler);
		mockJob1 = context.mock(IJob.class);

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		final double completionTime = releaseTime + setupTime + processingTime;

		context.checking(new Expectations() {{
			oneOf(mockJob1).startedProcessingOnMachine(machine);
			oneOf(mockJob1).getCurrentMachine(); will(returnValue(null));
			oneOf(mockJob1).getReadyTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
			oneOf(mockJob1).finishProcessingOnMachine();
		}});

		machine.processJob(mockJob1, 0);

		context.checking(new Expectations() {{
			oneOf(mockHandler).sendMachineFeed(machine, completionTime);
		}});

		machine.updateStatus(completionTime);

		machine.reset();

		Assert.assertNull(machine.getCurrentJob());
		Assert.assertTrue(machine.getProcessedJobs().isEmpty());
		Assert.assertTrue(machine.isAvailable());
		Assert.assertEquals(machine.getReadyTime(), 0, EPSILON);
	}

	@Test
	public void staticMachineTestFail_ProcessJob() {
		mockHandler = context.mock(ISubscriptionHandler.class);
		machine = new StaticMachine(mockHandler);
		mockJob1 = context.mock(IJob.class, "Job1");
		mockJob2 = context.mock(IJob.class, "Job2");

		releaseTime = 0.0;
		processingTime = 5.0;
		setupTime = 0.0;

		context.checking(new Expectations() {{
			oneOf(mockJob1).startedProcessingOnMachine(machine);
			oneOf(mockJob1).getCurrentMachine(); will(returnValue(null));
			oneOf(mockJob1).getReadyTime(); will(returnValue(releaseTime));
			oneOf(mockJob1).getProcessingTime(machine); will(returnValue(processingTime));
			oneOf(mockJob1).getSetupTime(machine); will(returnValue(setupTime));
			oneOf(mockJob1).finishProcessingOnMachine();
		}});

		machine.processJob(mockJob1, 0);

		try {
			machine.processJob(mockJob2, 0);
			Assert.fail();
		} catch (RuntimeException e) {
		}

	}

}
