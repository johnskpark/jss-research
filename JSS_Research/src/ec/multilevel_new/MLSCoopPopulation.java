package ec.multilevel_new;

public class MLSCoopPopulation {

	private IMLSCoopEntity[] allEntities;
	private MLSSubpopulation[] groups;
	private MLSGPIndividual[] individuals;

	private int numGroups;
	private int numIndividuals;

	private int numMetaGroups;
	private int numMetaInds;

	public MLSCoopPopulation(int numMetaGroups, int numMetaInds) {
		allEntities = new IMLSCoopEntity[numMetaGroups + numMetaInds];
		groups = new MLSSubpopulation[numMetaGroups];
		individuals = new MLSGPIndividual[numMetaInds];

		this.numMetaGroups = numMetaGroups;
		this.numMetaInds = numMetaInds;
	}

	public void addGroup(MLSSubpopulation group) {
		allEntities[numGroups + numIndividuals] = group;
		groups[numGroups++] = group;
	}

	public void addIndividual(MLSGPIndividual ind) {
		allEntities[numGroups + numIndividuals] = ind;
		individuals[numIndividuals++] = ind;
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

	public void clear() {
		allEntities = new IMLSCoopEntity[numMetaGroups + numMetaInds];
		groups = new MLSSubpopulation[numMetaGroups];
		individuals = new MLSGPIndividual[numMetaInds];
	}

}
