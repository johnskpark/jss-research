package jss;

import java.util.ArrayList;
import java.util.List;

import jss.problem.IMachine;

/**
 * Universal timer used by all of the machines. When solving the problem,
 * this timer will be incremented to simulate the machines processing the
 * jobs. Callback is used to update the status of the machines after the
 * time is modified.
 *
 * @author parkjohn
 *
 */
public class MachineTimer {

	private List<IMachine> machines = new ArrayList<IMachine>();

	private double currentTime;

	/**
	 * Default initialisation of the machine timer.
	 * @param machine
	 */
	public MachineTimer() {
		currentTime = 0;
	}

	/**
	 * Add the machine to the machine timer.
	 * @param machine
	 */
	public void addMachine(IMachine machine) {
		machines.add(machine);
	}

	/**
	 * Shifts time forward by the specified amount, and update the
	 * statuses of the machines on whether they are processing a job
	 * or not.
	 * @param time amount the time is shifted by
	 */
	public void shiftTime(double time) {
		setCurrentTime(getCurrentTime() + time);

		for (IMachine machine : machines) {
			machine.updateStatus();
		}
	}

	/**
	 * Get the current time.
	 * @return
	 */
	public double getCurrentTime() {
		return currentTime;
	}

	/**
	 * Set the current time.
	 * @param time
	 */
	protected void setCurrentTime(double time) {
		currentTime = time;
	}
}
