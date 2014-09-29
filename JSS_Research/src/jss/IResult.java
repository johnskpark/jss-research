package jss;

import java.util.List;

public interface IResult {

	/**
	 * TODO javadoc.
	 * @param action
	 */
	public void addAction(Action action);

	/**
	 * TODO javadoc.
	 * @return
	 */
	public List<Action> getActions();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getMakespan();

	/**
	 * TODO javadoc.
	 * @return
	 */
	public double getTWT();
}
