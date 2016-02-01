package app.test.nodeTest;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;

import app.evolution.JasimaGPData;
import app.evolution.node.SingleLineGPNode;
import app.node.INode;
import app.node.NodeAnnotation;
import app.node.NodeData;
import app.node.NodeDefinition;
import app.node.basic.ERCRandom;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class NodeTest {

	private static final double RANGE_OF_ERROR = 0.001;

	private static final String TERMINAL_PREFIX = "Score";
	private static final String NON_TERMINAL_PREFIX = "Op";

	private Reflections reflections;

	private long seed = 15;
	private Random rand;

	@Before
	public void setup() {
		reflections = new Reflections();

		rand = new Random(seed);
	}

	// Tests for the evolution nodes.

	// TODO come up with appropriate tests here whenever I need to.

	// Tests for the evaluation nodes.

	@Test
	public void evaluationNode_AnnotationPresent() {
		try {
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
			Set<Class<? extends INode>> evalNodes = reflections.getSubTypesOf(INode.class);

			for (Class<? extends INode> evalNode : evalNodes) {
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

	@Test
	public void evaluationNode_ERCPresent() {
		// TODO
	}

	@Test
	public void evaluationNode_HasEvolutionNodeEquivalent() {
		try {
			Set<Class<? extends INode>> evalNodes = reflections.getSubTypesOf(INode.class);
			Set<Class<? extends SingleLineGPNode>> evolNodes = reflections.getSubTypesOf(SingleLineGPNode.class);

			System.out.println(evalNodes.size() + ", " + evolNodes.size());

			Map<String, Class<? extends INode>> evalNodeMap = new HashMap<String, Class<? extends INode>>();
			for (Class<? extends INode> evalNode : evalNodes) {
				String name = evalNode.getSimpleName();

				evalNodeMap.put(name, evalNode);
			}

			for (Class<? extends SingleLineGPNode> evolNode : evolNodes) {
				String name = evolNode.getSimpleName();

				if (!evalNodeMap.containsKey(name)) {
					Assert.fail("Evaluation nodes not contain a specific evolution node: " + name);
				}
			}
		} catch (Exception ex) {
			Assert.fail();
		}
	}

	// TODO need to have standardised method of testing trees, and outputting values.

	private NodeData nodeData;
	private JasimaGPData gpData;

	private int maxChildNum = 3;

	private INode[] fakeINodes;
	private GPNode[] fakeGPNodes;

	@Test
	public void basicTerminals_EqualOutput() {
		// TODO
	}

	@Test
	public void basicNonTerminals_EqualOutput() {
		try {
			Set<Class<? extends INode>> evalNodes = reflections.getSubTypesOf(INode.class);
			Set<Class<? extends SingleLineGPNode>> evolNodes = reflections.getSubTypesOf(SingleLineGPNode.class);

			List<Class<? extends INode>> evalNonTerminals = evalNodes.stream()
					.filter(node -> node.getSimpleName().startsWith(NON_TERMINAL_PREFIX)).collect(Collectors.toList());
			List<Class<? extends SingleLineGPNode>> evolNonTerminals = evolNodes.stream()
					.filter(node -> node.getSimpleName().startsWith(NON_TERMINAL_PREFIX)).collect(Collectors.toList());

			int evalSize = evalNonTerminals.size();
			int evolSize = evolNonTerminals.size();

			// Assertion.
			Assert.assertEquals(String.format("Sizes do not match: eval: %d, evol: %d", evalSize, evolSize), evalSize, evolSize);

			Collections.sort(evalNonTerminals, new Comparator<Class<?>>() {
				public int compare(Class<?> o1, Class<?> o2) {
					return o1.getSimpleName().compareTo(o2.getSimpleName());
				};
			});

			Collections.sort(evolNonTerminals, new Comparator<Class<?>>() {
				public int compare(Class<?> o1, Class<?> o2) {
					return o1.getSimpleName().compareTo(o2.getSimpleName());
				};
			});

			// Initialise the fake nodes and data.
			nodeData = new NodeData();
			gpData = new JasimaGPData();

			fakeINodes = new FakeINode[maxChildNum];
			fakeGPNodes = new FakeGPNode[maxChildNum];

			for (int i = 0; i < maxChildNum; i++) {
				double value = rand.nextDouble();

				fakeINodes[i] = new FakeINode(value);
				fakeGPNodes[i] = new FakeGPNode(value);
			}

			// Initialise their constructors.
			for (int i = 0; i < evolSize; i++) {
				Class<? extends INode> evalNodeClass = evalNonTerminals.get(i);
				Class<? extends SingleLineGPNode> evolNodeClass = evolNonTerminals.get(i);

				// Initialise the evaluation node.
				NodeDefinition nodeDef = evalNodeClass.getAnnotation(NodeAnnotation.class).node();

				Class<?>[] constParams = new Class<?>[nodeDef.numChildren()];
				Arrays.fill(constParams, INode.class);
				Constructor<? extends INode> constructor = evalNodeClass.getConstructor(constParams);

				Object[] evalNodeParams = Arrays.copyOf(fakeINodes, nodeDef.numChildren());
				INode evalNode = constructor.newInstance(evalNodeParams);

				// Initialise the evolution node.
				GPNode evolNode = evolNodeClass.newInstance();
				evolNode.children = Arrays.copyOf(fakeGPNodes, evolNode.expectedChildren());

				// Get the evaluation value.
				double evalValue = evalNode.evaluate(nodeData);

				// Get the evolution value.
				evolNode.eval(null, 0, gpData, null, null, null);
				double evolValue = gpData.getPriority();

				Assert.assertEquals(String.format("The outputs do not match! Eval node: %s, Evol node: %s", evalNode.toString(), evolNode.toString()), evalValue, evolValue, RANGE_OF_ERROR);
			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			Assert.fail();
		}
	}

	@Test
	public void hildebrandtTerminals_EqualOutput() {
		// TODO
	}

	@Test
	public void hildebrandtNonTerminals_EqualOutput() {
		// TODO
	}

	@Test
	public void huntTerminals_EqualOutput() {
		// TODO
	}

	@Test
	public void huntNonTerminals_EqualOutput() {
		// TODO
	}

	// Fake INodes.
	private class FakeINode implements INode {
		private double value;

		public FakeINode(double value) {
			this.value = value;
		}

		public int getChildrenNum() {
			return 1;
		}

		public double evaluate(NodeData data) {
			return value;
		}
	}

	// Fake GPNodes.
	private class FakeGPNode extends GPNode {

		private static final long serialVersionUID = 1007379263738622226L;

		private double value;

		public FakeGPNode(double value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value + "";
		}

		@Override
		public void eval(final EvolutionState state,
				final int thread,
				final GPData input,
				final ADFStack stack,
				final GPIndividual individual,
				final Problem problem) {
			JasimaGPData data = (JasimaGPData)input;
			data.setPriority(value);
		}
	}

}
