package app.evolution.multilevel;

import ec.Breeder;
import ec.EvolutionState;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.util.Parameter;
import ec.util.ThreadPool;

// In multilevel selection, the subpopulations represent the groups,
// in which individuals can be a part of.
public class MultilevelSelectionBreeder extends Breeder {

    public static final String P_ELITE = "elite";
    public static final String P_ELITE_FRAC = "elite-fraction";
    public static final String P_REEVALUATE_ELITES = "reevaluate-elites";
    public static final String P_SEQUENTIAL_BREEDING = "sequential";
    public static final String P_CLONE_PIPELINE_AND_POPULATION = "clone-pipeline-and-population";
    /** An array[subpop] of the number of elites to keep for that subpopulation */
    public int[] elite;
    public double[] eliteFrac;
    public boolean[] reevaluateElites;
    public boolean sequentialBreeding;
    public boolean clonePipelineAndPopulation;
    public Population backupPopulation = null;

    public static final int NOT_SET = -1;

    public ThreadPool pool = new ThreadPool();

    public boolean usingElitism(int subpopulation) {
    	return (elite[subpopulation] > 0 ) || (eliteFrac[subpopulation] > 0);
    }

    public int numElites(EvolutionState state, int subpopulation) {
    	if (elite[subpopulation] != NOT_SET) {
    		return elite[subpopulation];
    	}
    	else if (eliteFrac[subpopulation] == 0) {
    		return 0; // no elites
    	}
    	else if (eliteFrac[subpopulation] != NOT_SET) {
    		return (int) Math.max(Math.floor(state.population.subpops[subpopulation].individuals.length * eliteFrac[subpopulation]), 1.0);  // AT LEAST 1 ELITE
    	}
    	else {
    		state.output.warnOnce("Elitism error (SimpleBreeder).  This shouldn't be able to happen.  Please report.");
    		return 0;  // this shouldn't happen
    	}
    }

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
    	Parameter p = new Parameter(Initializer.P_POP).push(Population.P_SIZE);
    	int size = state.parameters.getInt(p,null,1);  // if size is wrong, we'll let Population complain about it -- for us, we'll just make 0-sized arrays and drop out.

    	eliteFrac = new double[size];
    	elite = new int[size];
    	for(int i = 0; i < size; i++) {
    		eliteFrac[i] = elite[i] = NOT_SET;
    	}
    	reevaluateElites = new boolean[size];

    	sequentialBreeding = state.parameters.getBoolean(base.push(P_SEQUENTIAL_BREEDING), null, false);
    	if (sequentialBreeding && (size == 1)) { // uh oh, this can't be right
    		state.output.fatal("The Breeder is breeding sequentially, but you have only one population.", base.push(P_SEQUENTIAL_BREEDING));
    	}

    	clonePipelineAndPopulation =state.parameters.getBoolean(base.push(P_CLONE_PIPELINE_AND_POPULATION), null, true);
    	if (!clonePipelineAndPopulation && (state.breedthreads > 1)) { // uh oh, this can't be right
    		state.output.fatal("The Breeder is not cloning its pipeline and population, but you have more than one thread.", base.push(P_CLONE_PIPELINE_AND_POPULATION));
    	}

    	int defaultSubpop = state.parameters.getInt(new Parameter(Initializer.P_POP).push(Population.P_DEFAULT_SUBPOP), null, 0);
    	for(int x=0;x<size;x++) {
    		// get elites
    		if (state.parameters.exists(base.push(P_ELITE).push(""+x),null)) {
    			if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null)) {
    				state.output.error("Both elite and elite-frac specified for subpouplation " + x + ".", base.push(P_ELITE_FRAC).push(""+x), base.push(P_ELITE_FRAC).push(""+x));
    			} else {
    				elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+x),null,0);
    				if (elite[x] < 0) {
    					state.output.error("Elites for subpopulation " + x + " must be an integer >= 0", base.push(P_ELITE).push(""+x));
    				}
    			}
    		} else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+x),null)) {
    			eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+x),null,0.0, 1.0);
    			if (eliteFrac[x] < 0.0) {
    				state.output.error("Elite Fraction of subpopulation " + x + " must be a real value between 0.0 and 1.0 inclusive", base.push(P_ELITE_FRAC).push(""+x));
    			}
    		} else if (defaultSubpop >= 0) {
    			if (state.parameters.exists(base.push(P_ELITE).push(""+defaultSubpop),null)) {
    				elite[x] = state.parameters.getIntWithDefault(base.push(P_ELITE).push(""+defaultSubpop),null,0);
    				if (elite[x] < 0) {
    					state.output.warning("Invalid default subpopulation elite value.");  // we'll fail later
    				}
    			} else if (state.parameters.exists(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null)) {
    				eliteFrac[x] = state.parameters.getDoubleWithMax(base.push(P_ELITE_FRAC).push(""+defaultSubpop),null,0.0, 1.0);
    				if (eliteFrac[x] < 0.0) {
    					state.output.warning("Invalid default subpopulation elite-frac value.");  // we'll fail later
    				}
    			} else { // elitism is 0
    				elite[x] = 0;
    			}
    		} else { // elitism is 0
    			elite[x] = 0;
    		}

    		// get reevaluation
    		if (defaultSubpop >= 0 && !state.parameters.exists(base.push(P_REEVALUATE_ELITES).push(""+x),null)) {
    			reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+defaultSubpop), null, false);
    			if (reevaluateElites[x]) {
    				state.output.warning("Elite reevaluation not specified for subpopulation " + x + ".  Using values for default subpopulation " + defaultSubpop + ": " + reevaluateElites[x]);
    			}
    		} else {
    			reevaluateElites[x] = state.parameters.getBoolean(base.push(P_REEVALUATE_ELITES).push(""+x), null, false);
    		}
    	}

    	state.output.exitIfErrors();
    }

	@Override
	public Population breedPopulation(final EvolutionState state) {
		// In multilevel selection, breeding occurs in two stages.
		// The first stage breeds the groups and individuals so
		// so that there are double the numbers of groups and
		// individuals in the intermediate population. Afterwards,
		// half the groups and individuals are removed from the
		// intermediate population to obtain the next generation.

		Population newPop = null;
		if (clonePipelineAndPopulation) {
			newPop = (Population) state.population.emptyClone();
		} else {
			if (backupPopulation == null) {
				backupPopulation = (Population) state.population.emptyClone();
			}
			newPop = backupPopulation;
			newPop.clear();
			backupPopulation = state.population;  // swap in
		}

	    // load elites into top of newpop
		// loadElites(state, newPop);

		Subpopulation[] interSubpops = new Subpopulation[state.population.subpops.length];


		return null;
	}

	private Population breedIntermediatePopulation(final EvolutionState state, final Subpopulation[] subpops) {
		// TODO Auto-generated method stub
		// Breed the subpopulations

		return null;
	}

	private Population breedFinalPopulation(final EvolutionState state, final Subpopulation[] subpops) {
		// TODO Auto-generated method stub
		return null;
	}

}
