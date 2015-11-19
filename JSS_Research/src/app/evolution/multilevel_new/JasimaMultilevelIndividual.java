package app.evolution.multilevel_new;

import app.evolution.JasimaGPIndividual;
import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.gp.GPTree;
import ec.multilevel_new.IMLSCoopEntity;
import ec.multilevel_new.MLSCoopCombiner;

public class JasimaMultilevelIndividual extends JasimaGPIndividual implements IMLSCoopEntity {

	private static final long serialVersionUID = -1819545258972420086L;

	@Override
	public Fitness getFitness() {
		return fitness;
	}

	@Override
	public Individual[] getIndividuals() {
		return new Individual[]{this};
	}

	@Override
	public IMLSCoopEntity combine(final EvolutionState state, final IMLSCoopEntity other) {
		return MLSCoopCombiner.COOP_COMBINER.combine(state, this, other);
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
