package app.jasimaShopSim.core;

import jasima.core.simulation.Event;
import jasima.shopSim.core.DowntimeSource;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.core.WorkStation;

public class IndividualBreakdownMachine extends IndividualMachine {

	public double procProgress;
	public double procRemaining;

	public IndividualBreakdownMachine(WorkStation workStation, int idx) {
		super(workStation, idx);
	}

	// called whenever an operation is finished
	private Event onDepart = new Event(0.0d, WorkStation.DEPART_PRIO) {
		@Override
		public void handle() {
			final JobShop shop = workStation.shop();

			// TODO why is task 0 being called again? what's so different about the two?
			// Some cunt is scheduling this earlier in advance, what the actual fuck?
			if (curJob != null && curJob.getJobNum() == 1801 && curJob.getTaskNumber() >= 5) {
				System.out.printf("indmachine ondepart: time: %f, task: %d, num ops: %d\n", shop.simTime(), curJob.getTaskNumber(), curJob.numOps());
			}

			if (state != MachineState.DOWN) {
				if (procFinished > shop.simTime()) {
					// If the machine has been fixed in between the job's processing time
					// duration, then the departure needs to be delayed.

					// procFinished is the new time that the job is delayed to in this case.
					double newDepartTime = procFinished;

					this.setTime(newDepartTime);
					shop.schedule(this);
				} else {
					// Standard procedure for job departure.
					workStation.currMachine = IndividualBreakdownMachine.this;
					workStation.depart();
					workStation.currMachine = null;
				}
			} else {
				// If the machine has broken down, then reschedule the departure time,
				// as the job has not finished yet.
				assert state == MachineState.DOWN;

				// procFinished is the time when machine is repaired in this case.
				double newDepartTime = procFinished + procRemaining;

				this.setTime(newDepartTime);
				shop.schedule(this);
			}
		}
	};

	@Override
	public Event onDepart() {
		return onDepart;
	}

	@Override
	public void activate() {
		if (state != MachineState.DOWN) {
			throw new IllegalStateException("Only a machine in state DOWN can be activated.");
		}

		if (curJob != null) {
			// The machine broke down while a job was in the middle of processing.
			// Update the procFinished to be the operation's completion time.
			state = MachineState.WORKING;
			procFinished = workStation.shop().simTime() + procRemaining;
			procStarted = -1.0d;
			procProgress = -1.0d;
			procRemaining = -1.0d;

			// We can't call workStation.activated(), since it calls selectAndStart().
			// Therefore, we need to do everything that workStation.activated() does,
			// except for the selectAndStart() part.
			workStation.activatedStillBusy(this);
		} else {
			// The machine did not breakdown while a job was in the middle of processing.
			// Continue as normal.
			state = MachineState.IDLE;
			procFinished = -1.0d;
			procStarted = -1.0d;
			procProgress = -1.0d;
			procRemaining = -1.0d;

			workStation.activated(this);
		}

		downReason = null;
	}

	@Override
	public void takeDown(final DowntimeSource downReason) {
		final JobShop shop = workStation.shop();

		if (state != MachineState.DOWN) {
			// If the machine's not down, then either it is currently processing a job or is idle.
			if (state == MachineState.WORKING) {
				// If the machine's currently processing a job, interrupt the job by setting the job's
				// procFinished time to be after the machine is repaired and the job is resumed.
				assert procFinished > shop.simTime();
				assert curJob != null;

				// In breakdown, procStarted is the breakdown time and procFinished is
				// the time when the machine is repaired.
				procProgress = shop.simTime() - procStarted;
				procRemaining = procFinished - shop.simTime();
				procStarted = shop.simTime();
				procFinished = shop.simTime();

				state = MachineState.DOWN;
				this.downReason = downReason;

				workStation.takenDownStillBusy(this);
			} else {
				assert state == MachineState.IDLE;

				// In breakdown, procStarted is the breakdown time and procFinished is
				// the time when the machine is repaired.
				procProgress = -1.0d;
				procRemaining = -1.0d;
				procStarted = shop.simTime();
				procFinished = shop.simTime();

				curJob = null;

				state = MachineState.DOWN;
				this.downReason = downReason;

				workStation.takenDown(this);
			}
		} else {
			// If the machine is currently down, then postpone the take down.
			assert procFinished > shop.simTime();

			shop.schedule(new Event(procFinished, WorkStation.TAKE_DOWN_PRIO) {
				@Override
				public void handle() {
					assert workStation.currMachine == null;
					workStation.currMachine = IndividualBreakdownMachine.this;
					takeDown(downReason);
					workStation.currMachine = null;
				}
			});
		}
	}

}
