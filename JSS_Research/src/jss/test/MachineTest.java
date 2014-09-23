package jss.test;


/**
 * Tests for the machines.
 *
 * @author parkjohn
 *
 */
public class MachineTest {
//
//	private static final double EPSILON = 0.0001;
//
//	private final double timeLapse = 100;
//
//	@Rule
//	public JUnitRuleMockery context = new JUnitRuleMockery();
//
//	@Mock
//	private IJob mockJob = context.mock(IJob.class);
//
//	private MachineTimer machineTimer;
//	private IMachine machine;
//
//	@Before
//	public void setup() {
//		machineTimer = new MachineTimer();
//		machine = new Machine(machineTimer);
//	}
//
//	@Test
//	public void machine_InitialisationTest() {
//		assertTrue(machine.isAvailable());
//
//		assertEquals(machine.getCurrentJob(), null);
//		assertEquals(machine.getTimeAvailable(), 0, EPSILON);
//		assertEquals(machine.getProcessedJobs().size(), 0);
//	}
//
//	@Test
//	public void machine_JobBeingProcessedTest() {
//		setJobExpectations();
//		machine.processJob(mockJob);
//
//		assertFalse(machine.isAvailable());
//
//		assertEquals(machine.getCurrentJob(), mockJob);
//		assertEquals(machine.getTimeAvailable(), timeLapse, EPSILON);
//		assertEquals(machine.getProcessedJobs().size(), 0);
//	}
//
//	@Test
//	public void machine_JobFinishedProcessingTest() {
//		setJobExpectations();
//		machine.processJob(mockJob);
//
//		setMachineTimerExpectations();
//		machineTimer.shiftTime(timeLapse);
//
//		assertTrue(machine.isAvailable());
//
//		assertEquals(machine.getCurrentJob(), null);
//		assertEquals(machine.getTimeAvailable(), timeLapse, EPSILON);
//		assertEquals(machine.getProcessedJobs().size(), 1);
//		assertEquals(machine.getProcessedJobs().get(0), mockJob);
//	}
//
//	// TODO: more tests here.
//
//	private void setJobExpectations() {
//		context.checking(new Expectations() {{
//			oneOf (mockJob).getReleaseTime(); will(returnValue(0.0));
//			oneOf (mockJob).getSetupTime(machine); will(returnValue(0.0));
//			oneOf (mockJob).getProcessingTime(machine); will(returnValue(100.0));
//		}});
//	}
//
//	private void setMachineTimerExpectations() {
//		// TODO: write down the expectations here.
//	}
}
