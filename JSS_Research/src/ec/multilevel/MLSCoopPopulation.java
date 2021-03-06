package ec.multilevel;

import java.util.ArrayList;
import java.util.List;

import ec.Individual;
import ec.Subpopulation;
import ec.util.Pair;

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
	private List<Pair<Individual, List<MLSSubpopulation>>> removedIndividuals;

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
		removedIndividuals = new ArrayList<Pair<Individual, List<MLSSubpopulation>>>();
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
				Pair<Individual, List<MLSSubpopulation>> removedInd = getGroupsWithRemoved(ind);
				if (removedInd == null) {
					Pair<Individual, List<MLSSubpopulation>> newRemovedInd =
							new Pair<Individual, List<MLSSubpopulation>>(ind, new ArrayList<MLSSubpopulation>());
					newRemovedInd.i2.add(group);

					removedIndividuals.add(newRemovedInd);
				} else if (!removedInd.i2.contains(group)) {
						removedInd.i2.add(group);
				}
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
		Pair<Individual, List<MLSSubpopulation>> removedInd = getGroupsWithRemoved(ind);
		if (removedInd != null) {
			List<MLSSubpopulation> groups = removedInd.i2;
			for (MLSSubpopulation group : groups) {
				addValidGroup(group);
			}
			removedIndividuals.remove(removedInd);
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
			if (getGroupsWithRemoved(ind) == null) {
				unremovedIndividuals.add(ind);
			}
		}

		Individual[] result = new Individual[unremovedIndividuals.size()];
		unremovedIndividuals.toArray(result);

		return result;
	}

	private Pair<Individual, List<MLSSubpopulation>> getGroupsWithRemoved(Individual ind) {
		for (Pair<Individual, List<MLSSubpopulation>> removedInd : removedIndividuals) {
			if (removedInd.i1.equals(ind)) {
				return removedInd;
			}
		}
		return null;
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
