package jss.evolution.sample;

import java.util.ArrayList;
import java.util.List;

import jss.evolution.ITracker;

public class PriorityTracker implements ITracker {

	private List<Double> priorities = new ArrayList<Double>();

	public PriorityTracker() {
	}

	public List<Double> getPriorities() {
		return priorities;
	}

	public void clear() {
		priorities = new ArrayList<Double>();
	}

}
