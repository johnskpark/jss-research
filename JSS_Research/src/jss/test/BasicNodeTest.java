package jss.test;

import java.util.Arrays;
import java.util.Set;

import jss.IJob;
import jss.IMachine;
import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.evaluation.node.basic.ERCRandom;
import jss.evolution.JSSGPData;
import jss.evolution.node.basic.OpAddition;
import jss.evolution.node.basic.OpConditional;
import jss.evolution.node.basic.OpDivision;
import jss.evolution.node.basic.OpMultiplication;
import jss.evolution.node.basic.OpSubtraction;
import jss.evolution.node.basic.ScoreDueDate;
import jss.evolution.node.basic.ScoreJobReadyTime;
import jss.evolution.node.basic.ScoreMachineReadyTime;
import jss.evolution.node.basic.ScorePenalty;
import jss.evolution.node.basic.ScoreProcessingTime;
import jss.evolution.node.basic.ScoreRemainingOperation;
import jss.evolution.node.basic.ScoreRemainingTime;
import jss.evolution.node.basic.ScoreSetupTime;
import jss.node.NodeDefinition;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import ec.gp.GPNode;

public class BasicNodeTest {

	private static final double EPSILON = 0.001;
	
	private Mockery context = new JUnit4Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};

	private IMachine mockMachine;
	private IJob mockJob;
	
	@Before
	public void setup() {
		// Generate the instances of the expectations.
		// TODO
	}
	
	// Tests for evaluation nodes.
	
	@Test
	public void evaluationNode_AnnotationPresent() {
		try {
			Reflections reflections = new Reflections();
			Set<Class<? extends INode>> evalNodes = reflections.getSubTypesOf(INode.class);
			
			for (Class<? extends INode> evalNode : evalNodes) {
				Assert.assertTrue(evalNode.isAnnotationPresent(NodeAnnotation.class));
			}
		} catch (Exception ex) {
			Assert.fail();
		}
	}
	
	@Test
	public void evaluationNode_ConstructorPresent() {
		try {
			Reflections reflections = new Reflections();
			
			for (Class<? extends INode> evalNode : reflections.getSubTypesOf(INode.class)) {
				if (evalNode != ERCRandom.class) {
					NodeDefinition nodeDef = evalNode.getAnnotation(NodeAnnotation.class).node();
					
					Class<?>[] constParams = new Class<?>[nodeDef.numChildren()];
					Arrays.fill(constParams, INode.class);
					
					evalNode.getConstructor(constParams);
				}
			}
		} catch (Exception ex) {
			Assert.fail();
		}
	}
	
	// TODO more tests for evaluation.
	
	// Tests for evolution nodes.
	
	private JSSGPData data;
	
	private GPNode mockGPNode1;
	private GPNode mockGPNode2;
	private GPNode mockGPNode3;
	
	@Test
	public void processingTimeTest() {
		mockMachine = context.mock(IMachine.class);
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);
		data.setMachine(mockMachine);
		
		context.checking(new Expectations() {{
			oneOf(mockJob).getProcessingTime(mockMachine); will(returnValue(5.0));
		}});
		
		ScoreProcessingTime node = new ScoreProcessingTime();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}

	@Test
	public void dueDateTest() {
		mockMachine = context.mock(IMachine.class);
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);
		data.setMachine(mockMachine);
		
		context.checking(new Expectations() {{
			oneOf(mockJob).getDueDate(mockMachine); will(returnValue(5.0));
		}});
		
		ScoreDueDate node = new ScoreDueDate();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void jobReadyTimeTest() {
		mockMachine = context.mock(IMachine.class);
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);
		data.setMachine(mockMachine);
		
		context.checking(new Expectations() {{
			oneOf(mockJob).getReadyTime(mockMachine); will(returnValue(5.0));
		}});
		
		ScoreJobReadyTime node = new ScoreJobReadyTime();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}

	@Test
	public void setupTimeTest() {
		mockMachine = context.mock(IMachine.class);
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);
		data.setMachine(mockMachine);
		
		context.checking(new Expectations() {{
			oneOf(mockJob).getSetupTime(mockMachine); will(returnValue(5.0));
		}});
		
		ScoreSetupTime node = new ScoreSetupTime();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}

	@Test
	public void penaltyTest() {
		mockMachine = context.mock(IMachine.class);
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);
		data.setMachine(mockMachine);
		
		context.checking(new Expectations() {{
			oneOf(mockJob).getPenalty(mockMachine); will(returnValue(5.0));
		}});
		
		ScorePenalty node = new ScorePenalty();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void remainingTimeTest() {
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);

		context.checking(new Expectations() {{
			oneOf(mockJob).getRemainingTime(); will(returnValue(5.0));
		}});
		
		ScoreRemainingTime node = new ScoreRemainingTime();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(5.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void remainingOperationTest() {
		mockJob = context.mock(IJob.class);
		
		data = new JSSGPData();
		data.setJob(mockJob);

		context.checking(new Expectations() {{
			oneOf(mockJob).getRemainingOperation(); will(returnValue(2));
		}});
		
		ScoreRemainingOperation node = new ScoreRemainingOperation();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(2, data.getPriority(), EPSILON);
	}
	
	@Test
	public void machineReadyTimeTest() {
		mockMachine = context.mock(IMachine.class);
		
		data = new JSSGPData();
		data.setMachine(mockMachine);

		context.checking(new Expectations() {{
			oneOf(mockMachine).getReadyTime(); will(returnValue(0.0));
		}});
		
		ScoreMachineReadyTime node = new ScoreMachineReadyTime();
		node.eval(null, 0, data, null, null, null);
		
		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void additionTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
		}});
		
		OpAddition node = new OpAddition();
		node.children = new GPNode[2];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void conditionalTest_IfTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		mockGPNode3 = context.mock(GPNode.class, "GPNode3");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode3).eval(null, 0, data, null, null, null);
		}});
		
		OpConditional node = new OpConditional();
		node.children = new GPNode[3];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.children[2] = mockGPNode3;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void conditionalTest_ElseTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		mockGPNode3 = context.mock(GPNode.class, "GPNode3");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode3).eval(null, 0, data, null, null, null);
		}});
		
		OpAddition node = new OpAddition();
		node.children = new GPNode[3];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.children[2] = mockGPNode3;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void divisionTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
		}});
		
		OpDivision node = new OpDivision();
		node.children = new GPNode[2];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void multiplicationTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
		}});
		
		OpMultiplication node = new OpMultiplication();
		node.children = new GPNode[2];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
	@Test
	public void subtractionTest() {
		mockGPNode1 = context.mock(GPNode.class, "GPNode1");
		mockGPNode2 = context.mock(GPNode.class, "GPNode2");
		
		data = new JSSGPData();
		
		context.checking(new Expectations() {{
			oneOf(mockGPNode1).eval(null, 0, data, null, null, null);
			oneOf(mockGPNode2).eval(null, 0, data, null, null, null);
		}});
		
		OpSubtraction node = new OpSubtraction();
		node.children = new GPNode[2];
		node.children[0] = mockGPNode1;
		node.children[1] = mockGPNode2;
		node.eval(null, 0, data, null, null, null);

		Assert.assertEquals(0.0, data.getPriority(), EPSILON);
	}
	
}
