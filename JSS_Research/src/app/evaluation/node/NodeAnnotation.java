package app.evaluation.node;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import app.node.NodeDefinition;

/**
 * TODO javadoc.
 *
 * @author parkjohn
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NodeAnnotation {

	public NodeDefinition node();

}
