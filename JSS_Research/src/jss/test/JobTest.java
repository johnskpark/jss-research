package jss.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jss.BasicJob;
import jss.IJob;
import jss.IMachine;

import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for the jobs.
 *
 * @author parkjohn
 *
 */
public class JobTest {

	private static final double EPSILON = 0.0001;

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private IMachine mockMachine = context.mock(IMachine.class);

	@Test
	public void basicJob_InitialisationTest() {
		IJob basicJob = new BasicJob(1, 2, 3, 4);

		assertEquals(basicJob.getReleaseTime(), 1, EPSILON);
		assertEquals(basicJob.getProcessingTime(mockMachine), 2, EPSILON);
		assertEquals(basicJob.getSetupTime(mockMachine), 3, EPSILON);
		assertEquals(basicJob.getDueDate(mockMachine), 4, EPSILON);
	}

	@Test
	public void basicJob_VisitMachineTest() {
		IJob basicJob = new BasicJob(1, 2, 3, 4);

		assertTrue(basicJob.isProcessable(mockMachine));

		basicJob.visitMachine(mockMachine);

		assertFalse(basicJob.isProcessable(mockMachine));
	}

	// TODO: more tests here.

}
