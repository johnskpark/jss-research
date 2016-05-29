package app.evaluation;

import app.IMultiRule;
import app.node.INode;

public interface IJasimaEvalPriorityRule extends IMultiRule<INode> {

	public void setConfiguration(JasimaEvalConfig config);

}
