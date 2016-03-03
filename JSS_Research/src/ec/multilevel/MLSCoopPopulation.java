package ec.multilevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.Individual;
import ec.Subpopulation;

public class MLSCoopPopulation {

	private List<IMLSCoopEntity> allEntities;
	private List<MLSSubpopulation> groups;
	private List<Individual> individuals;

	// Lists have to be used here instead of a set. This is due to the fact that
	// an individual's hashcode depends on its memory address, which is not consistent.

	// Determines whether the individual is part of a group in the population or not.
	private List<Individual> groupedIndividuals;
	private List<Individual> ungroupedIndividuals;

	private List<MLSSubpopulation> validGroups;
	private Map<Individual, List<MLSSubpopulation>> removedIndividuals;

	private int numMetaGroups;
	private int numMetaInds;

	public MLSCoopPopulation(int numMetaGroups, int numMetaInds) {
		this.numMetaGroups = numMetaGroups;
		this.numMetaInds = numMetaInds;

		allEntities = new ArrayList<IMLSCoopEntity>(numMetaGroups + numMetaInds);
		groups = new ArrayList<MLSSubpopulation>(numMetaGroups);
		individuals = new ArrayList<Individual>(numMetaInds);

		groupedIndividuals = new ArrayList<Individual>();
		ungroupedIndividuals = new ArrayList<Individual>();

		validGroups = new ArrayList<MLSSubpopulation>();
		removedIndividuals = new HashMap<Individual, List<MLSSubpopulation>>();
	}

	public void addGroup(MLSSubpopulation group) {
		allEntities.add(group);
		groups.add(group);

		for (Individual ind : group.individuals) {
			// Update the grouped and ungrouped individuals list.
			groupedIndividuals.add(ind);
			ungroupedIndividuals.remove(ind);

			// Update the valid groups list.
			if (individuals.contains(ind)) {
				addValidGroup(group);
			} else {
				if (!removedIndividuals.containsKey(ind)) {
					removedIndividuals.put(ind, new ArrayList<MLSSubpopulation>());
				}
				removedIndividuals.get(ind).add(group);
			}
		}
	}

	public void addIndividual(Individual ind) {
		if (!(ind instanceof IMLSCoopEntity)) {
			throw new IllegalArgumentException("Individual must implement IMLSCoopEntity. Individual's class: " + ind.getClass());
		}

		allEntities.add((IMLSCoopEntity) ind);
		individuals.add(ind);

		// Update the ungrouped individuals list, if the individual is not already grouped.
		if (!groupedIndividuals.contains(ind)) {
			ungroupedIndividuals.add(ind);
		}

		// Update the valid groups list.
		if (removedIndividuals.containsKey(ind)) {
			List<MLSSubpopulation> groups = removedIndividuals.get(ind);
			for (MLSSubpopulation group : groups) {
				addValidGroup(group);
			}
			removedIndividuals.remove(ind);
		}
	}

	protected void addValidGroup(MLSSubpopulation group) {
		if (!validGroups.contains(group)) {
			validGroups.add(group);
		}
	}

	public int getMaxGroupNum() {
		return numMetaGroups;
	}

	public int getMaxIndividualNum() {
		return numMetaInds;
	}

	public int getNumEntities() {
		return groups.size() + individuals.size();
	}

	public int getNumGroups() {
		return groups.size();
	}

	public int getNumIndividuals() {
		return individuals.size();
	}

	public IMLSCoopEntity getEntity(int index) {
		return allEntities.get(index);
	}

	public MLSSubpopulation getGroup(int index) {
		return groups.get(index);
	}

	public MLSSubpopulation getValidGroup(int index) {
		return validGroups.get(index);
	}

	public Individual getIndividual(int index) {
		return individuals.get(index);
	}

	public Individual[] getUngroupedIndividuals() {
		Individual[] result = new Individual[ungroupedIndividuals.size()];
		ungroupedIndividuals.toArray(result);

		return result;
	}

	public Individual[] getUnremovedIndividuals(Subpopulation subpop) {
		List<Individual> unremovedIndividuals = new ArrayList<Individual>();
		for (Individual ind : subpop.individuals) {
			if (!removedIndividuals.containsKey(ind)) {
				unremovedIndividuals.add(ind);
			}
		}

		Individual[] result = new Individual[unremovedIndividuals.size()];
		unremovedIndividuals.toArray(result);

		return result;
	}

	public MLSSubpopulation[] getValidGroups() {
		MLSSubpopulation[] result = new MLSSubpopulation[validGroups.size()];
		validGroups.toArray(result);

		return result;
	}

	public void clear() {
		allEntities.clear();
		groups.clear();
		individuals.clear();

		groupedIndividuals.clear();
		ungroupedIndividuals.clear();

		validGroups.clear();
		removedIndividuals.clear();
	}

}
