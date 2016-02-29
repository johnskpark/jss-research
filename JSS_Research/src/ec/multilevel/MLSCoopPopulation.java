package ec.multilevel;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;
import ec.Subpopulation;

public class MLSCoopPopulation {

	// TODO convert all of this into lists.
	private List<IMLSCoopEntity> allEntities;
	private List<MLSSubpopulation> groups;
	private List<Individual> individuals;

	// Lists have to be used here instead of a set. This is due to the fact that
	// an individual's hashcode depends on its memory address, which is not consistent.

	// Determines whether the individual is part of a group in the population or not.
	private List<Individual> groupedIndividuals;
	private List<Individual> ungroupedIndividuals;

	private int numMetaGroups;
	private int numMetaInds;

	public MLSCoopPopulation(int numMetaGroups, int numMetaInds) {
		this.numMetaGroups = numMetaGroups;
		this.numMetaInds = numMetaInds;

		clear();
	}

	public void addGroup(MLSSubpopulation group) {
		allEntities.add(group);
		groups.add(group);

		// Update the grouped and ungrouped individuals list.
		for (Individual ind : group.individuals) {
			groupedIndividuals.add(ind);
			ungroupedIndividuals.remove(ind);
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
			if (individuals.contains(ind)) {
				unremovedIndividuals.add(ind);
			}
		}

		Individual[] result = new Individual[unremovedIndividuals.size()];
		unremovedIndividuals.toArray(result);

		return result;
	}

	public void clear() {
		allEntities = new ArrayList<IMLSCoopEntity>(numMetaGroups + numMetaInds);
		groups = new ArrayList<MLSSubpopulation>(numMetaGroups);
		individuals = new ArrayList<Individual>(numMetaInds);

		groupedIndividuals = new ArrayList<Individual>();
		ungroupedIndividuals = new ArrayList<Individual>();
	}

}
