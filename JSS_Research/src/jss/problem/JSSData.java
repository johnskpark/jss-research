package jss.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jss.ProblemInstance;
import ec.EvolutionState;
import ec.gp.GPData;
import ec.util.Parameter;

/**
 * A Job Shop Scheduling problem dataset containing multiple problem instances.
 *
 * The JSSData requires the child classes to initialise the factory that generates
 * the problem in the setup() method, afterwards, the initialise() method will then
 * explicitly generate the problems from the factory.
 *
 * TODO for now, this is explicitly used to evolve the rules. Make this
 * a little more abstract in the future.
 *
 * @author parkjohn
 *
 */
public abstract class JSSData extends GPData {

	private List<IProblemInstance> problems = new ArrayList<IProblemInstance>();

	@Override
	public void copyTo(final GPData gpd) {
		JSSData data = (JSSData)gpd;
		data.setProblems(problems);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		doSetup(state, base);

		IProblemFactory factory = getProblemFactory();
		for (IProblemInstance inst : factory) {
			problems.add(inst);
		}
	}

	/**
	 * Setup that is used by the child classes to instantiate additional
	 * parameters.
	 * @param state TODO
	 * @param base TODO
	 */
	protected abstract void doSetup(EvolutionState state, Parameter base);

	/**
	 * Get the factory which generates the list of problems.
	 * @return TODO
	 */
	protected abstract IProblemFactory getProblemFactory();

	/**
	 * Getter method for the problems in the dataset.
	 * @return the list of problems in the dataset.
	 */
	public List<IProblemInstance> getProblems() {
		return problems;
	}

	/**
	 * Setter method for the problems in the dataset.
	 * @param p the list of problems to set to in the dataset.
	 */
	protected void setProblems(List<IProblemInstance> p) {
		this.problems = p;
	}

	@Override
	public Object clone() {
		JSSData clone = (JSSData)super.clone();
		clone.setProblems(new ArrayList<IProblemInstance>(problems));
		return clone;
	}

}
