package jss;


/**
 * Generates an action from the input. TODO elaborate.
 *
 * @author parkjohn
 *
 */
public interface ActionHandler {

	/**
	 * Get the action for the particular machine, given a problem instance.
	 * @return
	 */
	public Action getAction(IMachine machine, IProblemInstance problem);

}
