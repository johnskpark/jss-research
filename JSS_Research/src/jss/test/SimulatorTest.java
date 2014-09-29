package jss.test;

import java.util.ArrayList;
import java.util.Arrays;

import jss.IEvent;
import jss.IEventHandler;
import jss.IProblemInstance;
import jss.Simulator;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
public class SimulatorTest {

	private static final double EPSILON = 0.001;

	private Mockery context = new JUnit4Mockery();

	private IProblemInstance mockProblem = context.mock(IProblemInstance.class);

	private double time1 = 0;
	private double time2 = 2;
	private double time3 = 5;

	private FakeEventHandler fakeHandler1 = new FakeEventHandler(time1);
	private FakeEventHandler fakeHandler2 = new FakeEventHandler(time3);
	private FakeEventHandler fakeHandler3 = new FakeEventHandler(time2);
	private FakeEventHandler fakeHandler4 = new FakeEventHandler(time1);

	private int numTrigger = 2;

	private double time4 = 7;
	private FakeEventHandler fakeHandler5 = new FakeEventHandler(time4);

	private double time5 = 3;
	private FakeEventHandler fakeHandler6 = new FakeEventHandler(time5);

	private IEvent mockEvent = context.mock(IEvent.class);

	private Expectations initExpectations;

	@Before
	public void setup() {
		initExpectations = new Expectations() {{
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(Arrays.asList(new IEventHandler[]{
					fakeHandler1,
					fakeHandler2,
					fakeHandler3,
					fakeHandler4
			})));
		}};
	}

	@Test
	public void simulatorTest_Init() {
		context.checking(initExpectations);

		Simulator simulator = new Simulator(mockProblem);

		Assert.assertTrue(simulator.hasEvent());
	}

	@Test
	public void simulatorTest_RunNoNewEvents() {
		context.checking(initExpectations);

		Simulator simulator = new Simulator(mockProblem);

		// Run the first event of the simulator
		context.checking(new Expectations() {{
			exactly(numTrigger).of(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertTrue(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time1, EPSILON);

		// Run the second event of the simulator
		context.checking(new Expectations() {{
			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertTrue(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time2, EPSILON);

		// Run the third event of the simulator
		context.checking(new Expectations() {{
			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertFalse(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time3, EPSILON);
	}

	@Test
	public void simulatorTest_RunNewEvent() {
		context.checking(initExpectations);

		Simulator simulator = new Simulator(mockProblem);

		context.checking(new Expectations() {{
			exactly(numTrigger).of(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));

			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));

			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(Arrays.asList(new IEventHandler[]{
					fakeHandler5
			})));
		}});

		simulator.triggerEvent();
		simulator.triggerEvent();
		simulator.triggerEvent();

		Assert.assertTrue(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time3, EPSILON);

		context.checking(new Expectations() {{
			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertFalse(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time4, EPSILON);
	}

	@Test
	public void simulatorTest_RunNewEventBeforeOld() {
		context.checking(initExpectations);

		Simulator simulator = new Simulator(mockProblem);

		context.checking(new Expectations() {{
			exactly(numTrigger).of(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));

			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(Arrays.asList(new IEventHandler[]{
					fakeHandler6
			})));
		}});

		simulator.triggerEvent();
		simulator.triggerEvent();

		Assert.assertTrue(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time2, EPSILON);

		context.checking(new Expectations() {{
			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertTrue(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time5, EPSILON);

		context.checking(new Expectations() {{
			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();

		Assert.assertFalse(simulator.hasEvent());
		Assert.assertEquals(simulator.getTime(), time3, EPSILON);
	}

	@Test
	public void simulatorTest_RunNewEventFail() {
		context.checking(initExpectations);

		Simulator simulator = new Simulator(mockProblem);

		context.checking(new Expectations() {{
			exactly(numTrigger).of(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));

			oneOf(mockEvent).trigger();
			oneOf(mockProblem).getEventHandlers();
			will(returnValue(new ArrayList<IEventHandler>()));
		}});

		simulator.triggerEvent();
		simulator.triggerEvent();

		try {
			context.checking(new Expectations() {{
				oneOf(mockEvent).trigger();
				oneOf(mockProblem).getEventHandlers();
				will(returnValue(Arrays.asList(new IEventHandler[]{
						fakeHandler6
				})));
			}});

			simulator.triggerEvent();
			Assert.fail();
		} catch (RuntimeException e) {
		}
	}

	private class FakeEventHandler implements IEventHandler {
		private double time;

		public FakeEventHandler(double time) {
			this.time = time;
		}

		@Override
		public boolean hasEvent() {
			return true;
		}

		@Override
		public IEvent getNextEvent() {
			return mockEvent;
		}

		@Override
		public double getNextEventTime() {
			return time;
		}

	}
}
