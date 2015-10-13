package ec.multilevel_new;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;

public class MLSCoopPopulation {

	private IMLSCoopEntity[] allEntities;
	private MLSSubpopulation[] groups;
	private MLSGPIndividual[] individuals;

	// Lists have to be used here instead of a set. This is due to the fact that
	// an individual's hashcode depends on its memory address, which is not consistent.
	private List<Individual> groupedIndividuals;
	private List<Individual> ungroupedIndividuals;

	private int numGroups;
	private int numIndividuals;

	private int numMetaGroups;
	private int numMetaInds;

	public MLSCoopPopulation(int numMetaGroups, int numMetaInds) {
		allEntities = new IMLSCoopEntity[numMetaGroups + numMetaInds];
		groups = new MLSSubpopulation[numMetaGroups];
		individuals = new MLSGPIndividual[numMetaInds];

		groupedIndividuals = new ArrayList<Individual>();
		ungroupedIndividuals = new ArrayList<Individual>();

		this.numMetaGroups = numMetaGroups;
		this.numMetaInds = numMetaInds;
	}

	public void addGroup(MLSSubpopulation group) {
		allEntities[numGroups + numIndividuals] = group;
		groups[numGroups++] = group;

		// Update the grouped and ungrouped individuals list.
		for (Individual ind : group.individuals) {
			groupedIndividuals.add(ind);
			ungroupedIndividuals.remove(ind);
		}
	}

	public void addIndividual(MLSGPIndividual ind) {
		allEntities[numGroups + numIndividuals] = ind;
		individuals[numIndividuals++] = ind;

		// Update the ungrouped individuals list, if the individual is not already grouped.
		if (!groupedIndividuals.contains(ind)) {
			ungroupedIndividuals.add(ind);
		}
	}

	public int getNumEntities() {
		return numGroups + numIndividuals;
	}

	public int getNumGroups() {
		return numGroups;
	}

	public int getNumIndividuals() {
		return numIndividuals;
	}

	public IMLSCoopEntity[] getAllEntities() {
		return allEntities;
	}

	public MLSSubpopulation[] getGroups() {
		return groups;
	}

	public MLSGPIndividual[] getIndividuals() {
		return individuals;
	}

	public MLSGPIndividual[] getUngroupedIndividuals() {
		MLSGPIndividual[] result = new MLSGPIndividual[ungroupedIndividuals.size()];

		ungroupedIndividuals.toArray(result);

		return result;
	}

	public void clear() {
		allEntities = new IMLSCoopEntity[numMetaGroups + numMetaInds];
		groups = new MLSSubpopulation[numMetaGroups];
		individuals = new MLSGPIndividual[numMetaInds];
	}

}
