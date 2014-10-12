package jss.evaluation.node;

import jss.node.NodeDefinition;

public class NodeUtil {

	private NodeUtil() {
	}

	public static NodeDefinition getNodeDefinition(Class<? extends INode> nodeClass) {
		if (nodeClass.isAnnotationPresent(NodeAnnotation.class)) {
			throw new RuntimeException("You done goofed"); // TODO
		}

		NodeAnnotation annotation = nodeClass.getAnnotation(NodeAnnotation.class);
		return annotation.node();
	}
}
