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
package jasima.core.experiment;

import jasima.core.experiment.AbstractMultiExperiment.BaseExperimentCompleted;
import jasima.core.experiment.Experiment.ExpPrintEvent;
import jasima.core.experiment.Experiment.ExperimentEvent;
import jasima.core.util.observer.NotifierListener;

import java.io.Serializable;
import java.util.Map;

/**
 * This class can be used as a base class for experiment listeners. It delegates
 * all events of {@link Experiment} to separate methods.
 * 
 * @author Torsten Hildebrandt
 * @version 
 *          "$Id$"
 */
public abstract class ExperimentListenerBase implements
		NotifierListener<Experiment, ExperimentEvent>, Cloneable, Serializable {

	private static final long serialVersionUID = -3880665781275114403L;

	@Override
	public final void update(Experiment e, ExperimentEvent event) {
		if (event == Experiment.EXPERIMENT_STARTING) {
			starting(e);
		} else if (event == Experiment.EXPERIMENT_INITIALIZED) {
			initialized(e);
		} else if (event == Experiment.EXPERIMENT_BEFORE_RUN) {
			beforeRun(e);
		} else if (event == Experiment.EXPERIMENT_AFTER_RUN) {
			afterRun(e);
		} else if (event == Experiment.EXPERIMENT_DONE) {
			done(e);
		} else if (event == Experiment.EXPERIMENT_COLLECT_RESULTS) {
			produceResults(e, e.results);
		} else if (event == Experiment.EXPERIMENT_FINISHING) {
			finishing(e, e.results);
		} else if (event == Experiment.EXPERIMENT_FINISHED) {
			finished(e, e.getResults());
		} else if (event instanceof ExpPrintEvent) {
			print(e, (ExpPrintEvent) event);
		} else if (event instanceof BaseExperimentCompleted) {
			BaseExperimentCompleted evt = (BaseExperimentCompleted) event;
			multiExperimentCompletedTask(e, evt.experimentRun, evt.results);
		} else {
			handleOther(e, event);
		}
	}

	protected void handleOther(Experiment e, ExperimentEvent event) {
	}

	protected void print(Experiment e, ExpPrintEvent event) {
	}

	protected void starting(Experiment e) {
	}

	protected void initialized(Experiment e) {
	}

	protected void beforeRun(Experiment e) {
	}

	protected void afterRun(Experiment e) {
	}

	protected void done(Experiment e) {
	}

	protected void produceResults(Experiment e, Map<String, Object> res) {
	}

	protected void finishing(Experiment e, Map<String, Object> results) {
	}

	protected void finished(Experiment e, Map<String, Object> results) {
	}

	protected void multiExperimentCompletedTask(Experiment baseExp,
			Experiment runExperiment, Map<String, Object> runResults) {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
