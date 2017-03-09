package ec.app.GPjsp;
import ec.*;
import ec.gp.GPIndividual;

/** This class exists solely to print out the DoubleVectorIndividual and its collaborators in
    a nice way for statistics purposes. */

public class Coevolutionary2WayGPIndividual extends GPIndividual {

	public Coevolutionary2WayGPIndividual[] context;
	boolean dontPrintContext = false;

	// Used for re-evaluation of rules.
	private String approach;
	private int seed;
	private int runNum;

	public void printIndividualForHumans(EvolutionState state, int log) {
		super.printIndividualForHumans(state, log);

		if (!dontPrintContext && context != null) {
			for(int i = 0; i < context.length; i++) {
				if (context[i] != null) {
					state.output.println("--Collaborator " + i + ":", log);

					// this is a hack but it should be fine because printing
					// individuals for humans is essentially always single-threaded
					context[i].dontPrintContext = true;
					context[i].printIndividualForHumans(state, log);
					context[i].dontPrintContext = false;
				}
			}
		}
	}

	public void setApproach(String approach) {
		this.approach = approach;
	}

	public String getApproach() {
		return approach;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public int getSeed() {
		return seed;
	}

	public void setRunNum(int runNum) {
		this.runNum = runNum;
	}

	public int getRunNum() {
		return runNum;
	}
}