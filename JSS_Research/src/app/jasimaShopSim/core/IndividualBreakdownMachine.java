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
			if (state != MachineState.DOWN) {
				workStation.currMachine = IndividualBreakdownMachine.this;
				workStation.depart();
				workStation.currMachine = null;
			} else {
				// If the machine has broken down, then reschedule the departure time,
				// as the job has not finished yet.
				assert state == MachineState.DOWN;

				// procFinished is the time when machine is repaired in this case.
				JobShop shop = workStation.shop();
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
		// TODO this needs to be modified if there are jobs currently stuck on the machine.
		if (state != MachineState.DOWN) {
			throw new IllegalStateException("Only a machine in state DOWN can be activated.");
		}
		if (curJob != null) {
			// TODO if the job was stuck on the machine during the break down.
			// workStation.activated(this); will select a new job to be processed on the machine.
			// Therefore, we can't use that function.
		} else {
			state = MachineState.IDLE;
			procFinished = -1.0d;
			procStarted = -1.0d;
			procProgress = -1.0d;
			procRemaining = -1.0d;

			workStation.activated(this);

			downReason = null;
		}
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

				procProgress = shop.simTime() - procStarted;
				procRemaining = procFinished - shop.simTime();

			} else {
				assert state == MachineState.IDLE;

				procProgress = -1.0d;
				procRemaining = -1.0d;

				curJob = null;
			}

			// In breakdown, procStarted is the breakdown time and procFinished is
			// the time when the machine is repaired.
			procStarted = shop.simTime();
			procFinished = shop.simTime();
			state = MachineState.DOWN;
			this.downReason = downReason;
			workStation.takenDown(this);
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
