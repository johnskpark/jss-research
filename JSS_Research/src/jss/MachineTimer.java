package jss;

import java.util.ArrayList;
import java.util.List;

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

	private List<Machine> machines = new ArrayList<Machine>();

	private double currentTime = 0;

	/**
	 * FORKEN JAVADOCS RIGHT HERE RIGHT NOW.
	 * @param machine
	 */
	public MachineTimer() {

	}

	public MachineTimer(double startTime) {
		currentTime = startTime;
	}

	public void addMachine(Machine machine) {
		machines.add(machine);
	}

	/**
	 * TODO
	 * @param time
	 */
	public void shiftTime(double time) {
		currentTime += time;
	}

	/**
	 * TODO
	 * @return
	 */
	public double getCurrentTime() {
		return currentTime;
	}
}
