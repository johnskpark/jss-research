package jss;


/**
 * Interface for rules that generate @see Action. IActionHandler is a
 * subcomponent of @see ISolver, where the solver uses the action handler to
 * generate the individual actions while it coordinates the overall solution
 * generation.
 *
 * @author parkjohn
 *
 */
public interface IActionHandler {

	/**
	 * Get the action for the particular machine, given a problem instance and
	 * the current time.
	 */
	public Action getAction(IMachine machine, IProblemInstance problem, double time);

}
