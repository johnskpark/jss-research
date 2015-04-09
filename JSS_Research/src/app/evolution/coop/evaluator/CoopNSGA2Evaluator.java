package app.evolution.coop.evaluator;

import java.util.ArrayList;
import java.util.List;

import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.coevolve.MultiPopCoevolutionaryEvaluator;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.nsga2.NSGA2Breeder;
import ec.multiobjective.nsga2.NSGA2MultiObjectiveFitness;
import ec.util.Parameter;
import ec.util.SortComparator;

public class CoopNSGA2Evaluator extends MultiPopCoevolutionaryEvaluator {

	private static final long serialVersionUID = 5282696617392225149L;

    public int originalPopSize[];

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter p = new Parameter(Initializer.P_POP);
		int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
		Parameter p_subpop;
		originalPopSize = new int[subpopsLength];
		for (int i = 0; i < subpopsLength; i++) {
			p_subpop = p.push(Population.P_SUBPOP).push("" + i).push(Subpopulation.P_SUBPOPSIZE);
			originalPopSize[i] = state.parameters.getInt(p_subpop, null, 1);
		}
	}

	@Override
	public void evaluatePopulation(final EvolutionState state) {
		super.evaluatePopulation(state);

		// TODO right, it seems that the other subpopulations are being properly evaluated.
		for (int x = 0; x < state.population.subpops.length; x++) {
        	state.population.subpops[x].individuals = buildArchive(state, x);
        }
	}

	public Individual[] buildArchive(EvolutionState state, int subpop) {
		Individual[] dummy = new Individual[0];
		List<List<Individual>> ranks = assignFrontRanks(state.population.subpops[subpop]);

		List<Individual> newSubpopulation = new ArrayList<Individual>();
		int size = ranks.size();
		for (int i = 0; i < size; i++) {
			Individual[] rank = (Individual[]) ranks.get(i).toArray(dummy);
			assignSparsity(rank);
			if (rank.length + newSubpopulation.size() >= originalPopSize[subpop]) {
				// first sort the rank by sparsity
				ec.util.QuickSort.qsort(rank, new SortComparator() {
					public boolean lt(Object a, Object b) {
						Individual i1 = (Individual) a;
						Individual i2 = (Individual) b;
						return (((NSGA2MultiObjectiveFitness) i1.fitness).sparsity > ((NSGA2MultiObjectiveFitness) i2.fitness).sparsity);
					}

					public boolean gt(Object a, Object b) {
						Individual i1 = (Individual) a;
						Individual i2 = (Individual) b;
						return (((NSGA2MultiObjectiveFitness) i1.fitness).sparsity < ((NSGA2MultiObjectiveFitness) i2.fitness).sparsity);
					}
				});

				// then put the m sparsest individuals in the new population
				int m = originalPopSize[subpop] - newSubpopulation.size(); // TODO the bug's here.
				for(int j = 0 ; j < m; j++) {
					newSubpopulation.add(rank[j]);
				}

				// and bail
				break;
			} else {
				// dump in everyone
				for(int j = 0 ; j < rank.length; j++) {
					newSubpopulation.add(rank[j]);
				}
			}
		}

		Individual[] archive = (Individual[]) newSubpopulation.toArray(dummy);

		NSGA2Breeder breeder = (NSGA2Breeder) state.breeder;
		if (breeder.reevaluateElites[subpop]) {
			for (int i = 0; i < archive.length; i++) {
				archive[i].evaluated = false;
			}
		}

		return archive;
	}

	public List<List<Individual>> assignFrontRanks(Subpopulation subpop) {
		Individual[] inds = subpop.individuals;
		@SuppressWarnings("unchecked")
		List<List<Individual>> frontsByRank = MultiObjectiveFitness.partitionIntoRanks(inds);

		int numRanks = frontsByRank.size();
		for (int rank = 0; rank < numRanks; rank++) {
			List<Individual> front = (List<Individual>) frontsByRank.get(rank);
			int numInds = front.size();

			for (int ind = 0; ind < numInds; ind++) {
				NSGA2MultiObjectiveFitness fitness = (NSGA2MultiObjectiveFitness) front.get(ind).fitness;
				fitness.rank = rank;
			}
		}

		return frontsByRank;
	}

	public void assignSparsity(Individual[] front) {
		int numObjectives = ((NSGA2MultiObjectiveFitness) front[0].fitness).getObjectives().length;

		for (int i = 0; i < front.length; i++) {
			((NSGA2MultiObjectiveFitness) front[i].fitness).sparsity = 0;
		}

		for (int i = 0; i < numObjectives; i++) {
			final int o = i;
			ec.util.QuickSort.qsort(front, new SortComparator() {
				public boolean lt(Object a, Object b) {
					Individual i1 = (Individual) a;
					Individual i2 = (Individual) b;
					return (((NSGA2MultiObjectiveFitness) i1.fitness).getObjective(o) < ((NSGA2MultiObjectiveFitness) i2.fitness).getObjective(o));
				}

				public boolean gt(Object a, Object b) {
					Individual i1 = (Individual) a;
					Individual i2 = (Individual) b;
					return (((NSGA2MultiObjectiveFitness) i1.fitness).getObjective(o) > ((NSGA2MultiObjectiveFitness) i2.fitness).getObjective(o));
				}
			});

			((NSGA2MultiObjectiveFitness) front[0].fitness).sparsity = Double.POSITIVE_INFINITY;
			((NSGA2MultiObjectiveFitness) front[front.length - 1].fitness).sparsity = Double.POSITIVE_INFINITY;
			for (int j = 1; j < front.length - 1; j++) {
				NSGA2MultiObjectiveFitness f_j = (NSGA2MultiObjectiveFitness) (front[j].fitness);
				NSGA2MultiObjectiveFitness f_jplus1 = (NSGA2MultiObjectiveFitness) (front[j+1].fitness);
				NSGA2MultiObjectiveFitness f_jminus1 = (NSGA2MultiObjectiveFitness) (front[j-1].fitness);

				f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (f_j.maxObjective[o] - f_j.minObjective[o]);
			}
		}
	}

}
