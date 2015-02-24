package app.node;

public class NodeUtil {

	private NodeUtil() {
	}

	public static NodeDefinition getNodeDefinition(Class<? extends INode> nodeClass) {
		if (!nodeClass.isAnnotationPresent(NodeAnnotation.class)) {
			throw new RuntimeException("INode class must define annotation");
		}

		NodeAnnotation annotation = nodeClass.getAnnotation(NodeAnnotation.class);
		return annotation.node();
	}
}
