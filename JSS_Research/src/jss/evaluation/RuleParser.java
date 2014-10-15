package jss.evaluation;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jss.evaluation.node.INode;
import jss.evaluation.node.NodeAnnotation;
import jss.evaluation.node.NodeUtil;
import jss.evaluation.node.basic.ERCRandom;
import jss.node.NodeDefinition;

import org.reflections.Reflections;

/**
 * TODO javadoc.
 *
 * @author John Park
 *
 */
public class RuleParser {

	private static final String BASE_NODE_DIR = "jss.evaluation.node";

	private Map<String, NodeChildrenNumPair> nodeMap = new HashMap<String, NodeChildrenNumPair>();

	public RuleParser() {
		// Load up all of the nodes that will be used for the parsing.
		loadNodes();
	}

	// TODO docs.
	private void loadNodes() {
		try {
			Reflections reflections = new Reflections(BASE_NODE_DIR);

			Set<Class<? extends INode>> subTypes = reflections.getSubTypesOf(INode.class);

			for (Class<? extends INode> subType : subTypes) {
				if (subType.isAnnotationPresent(NodeAnnotation.class)) {
					NodeDefinition nodeDefinition = NodeUtil.getNodeDefinition(subType);

					NodeChildrenNumPair pair = new NodeChildrenNumPair();
					pair.nodeClass = subType;
					pair.childrenNum = nodeDefinition.numChildren();

					nodeMap.put(nodeDefinition.toString(), pair);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	// TODO docs.
	public INode getRuleFromString(String ruleString) {
		return new SubParser(ruleString).parse();
	}

	private class SubParser {
		private String input;
		private int index = 0;

		public SubParser(String input) {
			this.input = input;
		}

		public INode parse() {
			ignoreWhitespace();

			if (input.charAt(index) == '(') {
				// Parse the non-terminal node.
				match("(");

				String token = readToken();
				if (!nodeMap.containsKey(token)) {
					throw new RuntimeException("Unrecognised token " + token); // TODO
				}

				NodeChildrenNumPair pair = nodeMap.get(token);

				// Parse the child nodes.
				Object[] children = new INode[pair.childrenNum];
				for (int i = 0; i < pair.childrenNum; i++) {
					children[i] = parse();
				}

				match(")");

				return generateNewNode(pair, children);
			} else {
				// Parse the terminal node.
				String token = readToken();
				if (nodeMap.containsKey(token)) {
					NodeChildrenNumPair pair = nodeMap.get(token);

					return generateNewNode(pair);
				} else if (token.matches("[\\d]+(\\.[\\d]+)?")) {
					return new ERCRandom(Double.parseDouble(token));
				} else {
					throw new RuntimeException("Unrecognised token " + token); // TODO
				}
			}

		}

		private INode generateNewNode(NodeChildrenNumPair pair, Object... children) {
			try {
				Class<?>[] parameterTypes = new Class<?>[pair.childrenNum];
				for (int i = 0; i < pair.childrenNum; i++) {
					parameterTypes[i] = INode.class;
				}

				Constructor<? extends INode> constructor = pair.nodeClass.getConstructor(parameterTypes);

				return constructor.newInstance(children);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		private void ignoreWhitespace() {
			while (index < input.length() &&
					Character.isWhitespace(input.charAt(index))) {
				index++;
			}
		}

		private String readToken() {
			int startIndex = index;
			while (index < input.length() &&
					!Character.isWhitespace(input.charAt(index)) &&
					input.charAt(index) != ')') {
				index++;
			}
			return input.substring(startIndex, index);
		}

		private void match(String match) {
			if (!input.substring(index).startsWith(match)) {
				throw new RuntimeException("You done goofed from RuleParser");
			}
			index += match.length();
		}
	}

	private class NodeChildrenNumPair {
		Class<? extends INode> nodeClass;
		int childrenNum;
	}

}
