package jss.evolution;

import ec.gp.GPIndividual;
import ec.gp.GPTree;

/**
 * TODO javadoc.
 * @author parkjohn
 *
 */
public class JSSGPIndividual extends GPIndividual {

	private static final long serialVersionUID = -5218812585218446585L;

	@Override
	public String genotypeToStringForHumans() {
		return printTreesForCollaborators();
	}

	@Override
	public String genotypeToString() {
		return printTreesForCollaborators();
	}

	private String printTreesForCollaborators() {
		StringBuilder buf = new StringBuilder();

		for (int x = 0; x < trees.length; x++) {
			buf.append("Tree " + x + ": ");
			buf.append(printTreeForCollaborators(trees[0]));
		}

		return buf.toString();

	}

	// TODO rename for later.
	private String printTreeForCollaborators(GPTree tree) {
        switch (tree.printStyle) {
        case GPTree.PRINT_STYLE_C : return tree.child.makeCTree(true,
        			tree.printTerminalsAsVariablesInC,
        			tree.printTwoArgumentNonterminalsAsOperatorsInC);
        case GPTree.PRINT_STYLE_LATEX : return tree.child.makeLatexTree();
        case GPTree.PRINT_STYLE_DOT : return tree.child.makeGraphvizTree();
        case GPTree.PRINT_STYLE_LISP : return tree.child.makeLispTree();
        default : return tree.child.makeLispTree();
        }
	}
}
