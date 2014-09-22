package jss.test;

import static org.junit.Assert.assertEquals;
import jss.problem.IMachine;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MachineTimerTest {
//
//	private static final double EPSILON = 0.0001;
//
//	private final double timeLapse = 100;
//
//	@Rule
//	public JUnitRuleMockery context = new JUnitRuleMockery();
//
//	@Mock
//	private IMachine mockMachine = context.mock(IMachine.class);
//
//	private MachineTimer machineTimer;
//
//	@Before
//	public void setup() {
//		machineTimer = new MachineTimer();
//
//		machineTimer.addMachine(mockMachine);
//	}
//
//	@Test
//	public void machineTimer_InitialisationTest() {
//		assertEquals(machineTimer.getCurrentTime(), 0, EPSILON);
//	}
//
//	@Test
//	public void machineTimer_TimeShiftTest() {
//		setMachineExpectations();
//		machineTimer.shiftTime(timeLapse);
//
//		assertEquals(machineTimer.getCurrentTime(), timeLapse, EPSILON);
//	}
//
//	// TODO: more tests here.
//
//	private void setMachineExpectations() {
//		context.checking(new Expectations() {{
//			oneOf (mockMachine).updateStatus();
//		}});
//	}
}
