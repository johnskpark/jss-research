package app.evolution.coop;

import app.evolution.JasimaGPIndividual;
import ec.Individual;
import ec.gp.GPTree;

public class JasimaCoopIndividual extends JasimaGPIndividual {

	private static final long serialVersionUID = 7566878552263864266L;

	private Individual[] collaborators;

	public Individual[] getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Individual[] inds) {
		collaborators = inds;
	}

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
