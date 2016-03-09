/*******************************************************************************
 * Copyright (c) 2010-2015 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.2.
 *
 * jasima is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jasima is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jasima.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package jasima.shopSim.util;

import jasima.core.simulation.Simulation;
import jasima.core.util.AbstractResultSaver;
import jasima.shopSim.core.IndividualMachine;
import jasima.shopSim.core.Job;
import jasima.shopSim.core.JobShop;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.WorkStation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Produces a detailed trace of all events of a {@link JobShop} in a text file.
 * Creating this file is rather slow, so this class is mainly useful for
 * debugging purposes.
 * 
 * @author Torsten Hildebrandt, 2012-08-24
 * @version 
 *          "$Id$"
 */
public class TraceFileProducer extends ShopListenerBase {

	private static final long serialVersionUID = -4595308395912967961L;

	// parameters

	private String fileName;

	// used during run

	private PrintWriter log;

	private String name;

	public TraceFileProducer() {
		super();
	}

	public TraceFileProducer(String fileName) {
		this();

		setFileName(fileName);
	}

	protected WorkStationListenerBase createWSListener() {
		return new WorkStationListenerBase() {
			@Override
			protected void arrival(WorkStation m, Job j) {
				if (!j.isFuture()) {
					print(m.shop().simTime() + "\tarrives_at\t" + j + "\t" + m
							+ "\t" + (m.numBusy() == 0 ? "IDLE" : "PROCESSING")
							+ "\t" + (m.queue.size() - 1));
				}
			}

			@Override
			protected void activated(WorkStation ws, IndividualMachine m) {
				print(ws.shop().simTime()
						+ "\tbecomes_available\t"
						+ m.toString()
						+ "\t"
						+ ws.queue.size()
						+ (m.downReason == null ? "" : "\t"
								+ String.valueOf(m.downReason)));
			}

			@Override
			protected void deactivated(WorkStation ws, IndividualMachine m) {
				print(ws.shop().simTime()
						+ "\tunavailable\t"
						+ m.toString()
						+ "\t"
						+ ws.queue.size()
						+ (m.downReason == null ? "" : "\t"
								+ String.valueOf(m.downReason)));
			}

			@Override
			protected void operationStarted(WorkStation m,
					PrioRuleTarget jobOrBatch, int oldSetupState,
					int newSetupState, double setTime) {
				if (jobOrBatch == null) {
					print(m.shop().simTime() + "\tkeeping_idle\t"
							+ m.currMachine.toString() + "\t" + jobOrBatch);
				} else {
					for (int i = 0; i < jobOrBatch.numJobsInBatch(); i++)
						print(m.shop().simTime() + "\tstart_processing\t"
								+ m.currMachine.toString() + "\t"
								+ jobOrBatch.job(i) + "\t" + "\t"
								+ m.queue.size());
					// shop.log().debug(
					// shop.simTime + "\tstart_processing\t" + machName + "\t"
					// + batch + "\t" + "\t" + queue.size());
					if (oldSetupState != newSetupState) {
						print(m.shop().simTime() + "\tsetup\t"
								+ m.currMachine.toString() + "\t"
								+ m.setupStateToString(oldSetupState) + "\t"
								+ m.setupStateToString(newSetupState) + "\t"
								+ setTime);
					}
				}
			}

			@Override
			protected void operationCompleted(WorkStation m,
					PrioRuleTarget jobOrBatch) {
				// shop.log().debug(
				// shop.simTime + "\tfinished_processing\t" + machName + "\t"
				// + b);
				for (int i = 0; i < jobOrBatch.numJobsInBatch(); i++)
					print(m.shop().simTime() + "\tfinished_processing\t"
							+ m.currMachine.toString() + "\t"
							+ jobOrBatch.job(i));
			}
		};
	}

	@Override
	protected void jobReleased(JobShop shop, Job j) {
		print(shop.simTime() + "\tenter_system\t" + j);
	}

	@Override
	protected void jobFinished(JobShop shop, Job j) {
		print(shop.simTime() + "\tleave_system\t" + j);
	}

	@Override
	protected void simEnd(Simulation sim) {
		print(sim.simTime() + "\tsim_end");

		log.close();
		log = null;
	}

	@Override
	protected void init(Simulation sim) {
		createLogFile(sim);
	}

	@Override
	protected void simStart(Simulation sim) {
		print(sim.simTime() + "\tsim_start");

		JobShop shop = (JobShop) sim;
		shop.installMachineListener(createWSListener(), false);
	}

	// @Override
	// protected void print(Simulation sim, SimPrintEvent event) {
	// print(sim.simTime() + "\tprint\t" + event.getMessage());
	// }

	protected void print(String line) {
		log.println(line);
	}

	private void createLogFile(Simulation sim) {
		try {
			name = getFileName();
			if (name == null) {
				// create some default name
				name = "jasimaTrace"
						+ new SimpleDateFormat("_yyyyMMdd_HHmmss")
								.format(new Date());
				// don't overwrite existing
				name = AbstractResultSaver.findFreeFile(name, ".txt") + ".txt";
			}
			log = new PrintWriter(new BufferedWriter(new FileWriter(name)),
					true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		String n = getFileName();
		if (n == null)
			n = name;
		return getClass().getSimpleName() + "(" + n + ")";
	}

	// getter/setter for parameter below

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
