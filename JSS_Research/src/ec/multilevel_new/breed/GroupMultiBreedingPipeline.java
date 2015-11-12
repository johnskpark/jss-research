package ec.multilevel_new.breed;

import ec.EvolutionState;
import ec.Individual;
import ec.multilevel_new.MLSDefaults;
import ec.multilevel_new.MLSGroupBreedingPipeline;
import ec.multilevel_new.MLSGroupBreedingSource;
import ec.multilevel_new.MLSGroupSelection;
import ec.util.Parameter;

public class GroupMultiBreedingPipeline extends MLSGroupBreedingPipeline {

	private static final long serialVersionUID = -4147099919720814921L;
	
	public static final String P_NUMSOURCES = "num-sources";
	public static final String P_SOURCE = "source";
	public static final String P_PROB = "prob";

	public static final String P_GEN_MAX = "generate-max";
	public static final String P_MULTIBREED = "multibreed";

	public static final int DYNAMIC_SOURCES = 0;

	public static final int NOT_SET = -1;
	public static final double NO_PROBABILITY = 0.0;

	// TODO this is used in sourcesAreProperForm() in BreedingPipeline.
	private Parameter myBase;
	
	private int maxGeneratable;
	private boolean generateMax;

	private MLSGroupBreedingSource[] sources;
	private double[] probs;

	public Parameter defaultBase() {
		return MLSDefaults.base().push(P_MULTIBREED);
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		myBase = base;

		Parameter def = defaultBase();

		// Load in the sources.
		int numSources = state.parameters.getInt(base.push(P_NUMSOURCES), def.push(P_NUMSOURCES), 1);
		if (numSources == 0) {
			state.output.fatal("Breeding pipeline num-sources value must be > 0",
					base.push(P_NUMSOURCES),
					def.push(P_NUMSOURCES));
		}

		sources = new MLSGroupBreedingSource[numSources];

		double total = 0.0;
		
		for (int i = 0; i < sources.length; i++) {
			Parameter p = base.push(P_SOURCE).push("" + i);
			Parameter d = def.push(P_SOURCE).push("" + i);

			sources[i] = (MLSGroupBreedingSource) state.parameters.getInstanceForParameter(p, d, MLSGroupBreedingPipeline.class);
			if (!(sources[i] instanceof MLSGroupBreedingPipeline)) {
				state.output.error("Group breeding source #" + i + "is not a MLSGroupBreedingPipeline");
			}
			
			sources[i].setup(state, p);

			if (!state.parameters.exists(p.push(P_PROB), d.push(P_PROB))) {
				probs[i] = NO_PROBABILITY;
			} else {
				probs[i] = state.parameters.getDouble(p.push(P_PROB), d.push(P_PROB), NO_PROBABILITY);
				if (probs[i] < 0.0) {
					state.output.error("Group breeding source #" + i + "\'s probability must be a double floating point value >= 0.0, or empty, which represents NO_PROABILITY.", base.push(P_PROB), def.push(P_PROB));
				}
			}

			state.output.exitIfErrors();
			
			total += probs[i];
		}
		
		// Check for zero probability. 
		if (total == 0.0) {
			state.output.warning("GroupMultiBreedingPipeline's children all have zero probabilities. Please check to ensure that this is not an error. The probabilities will be treated as a uniform distribution", base);
		}
		
		// Normalise the probabilities.
		normaliseProbabilities(probs);
	}

	@Override
	public void prepareToProduce(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishProducing(EvolutionState state, int thread) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns the number of sources that make up the group multibreeding pipeline.
	 */
	public int numSources() {
		return sources.length;
	}
	
	/**
	 * Returns the minimum number of children produced by the sources.
	 */
	public int minChildProduction() {
		if (sources.length == 0) { 
			return 0; 
		}
		
		int min = sources[0].typicalIndsProduced();
		for (int i = 1; i < sources.length; i++) {
			min = Math.min(min, sources[i].typicalIndsProduced());
		}
		return min;
	}
	
	/**
	 * Returns the maximum number of children produced by the sources.
	 */
	public int maxChildProduction() {
		if (sources.length == 0) {
			return 0;
		}
		
		int max = sources[0].typicalIndsProduced();
		for (int i = 1; i < sources.length; i++) {
			max = Math.max(max, sources[i].typicalIndsProduced());
		}
		return max;
	}
	
	@Override
	public int typicalIndsProduced() {
		return getMaxGeneratable();
	}
	
	protected int getMaxGeneratable() {
		if (maxGeneratable == NOT_SET) {
			maxGeneratable = maxChildProduction();
		}
		return maxGeneratable;
	}

	@Override
	public int produce(final EvolutionState state, 
			final int thread) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int produce(final int min, 
			final int max, 
			final int start, 
			final EvolutionState state,
			final int thread) {
		MLSGroupBreedingSource s = sources[pickRandom(probs, state.random[thread].nextDouble())];
		int total;
		
		if (generateMax) {
			int n = getMaxGeneratable();
			if (n < min) { n = min; }
			if (n > max) { n = max; }
			
			total = s.produce(min, max, start, state, thread);
		} else {
			total = s.produce(min, max, start, state, thread);
		}
		
		// Clone if necessary.
		if (s instanceof MLSGroupSelection) {
			for (int i = start; i < total + start; i++) {
				// TODO 
//				inds[i] = (Individual) inds[i].clone();
			}
		}
		
		return total;
	}
	
	private static void normaliseProbabilities(double[] probs) {
		// Find the total sum of the probabilities.
		double total = 0.0;
		for (double prob : probs) {
			total += prob;
		}

		if (total == 0.0) {
			// Use a uniform distribution. The probabilities should be zero.
			for (int i = 0; i < probs.length; i++) {
				probs[i] = 1.0 / probs.length;
			}
		} else {
			// Normalise the probabilities. 
			for (int i = 0; i < probs.length; i++) {
				probs[i] = 1.0 * probs[i] / total;
			}
		}
	}
	
	private static int pickRandom(double[] probs, double rand) {
		double accumProb = 0.0;
		
		for (int i = 0; i < probs.length; i++) {
			accumProb += probs[i];
			
			if (rand < accumProb) {
				return i;
			}
		}
		
		return probs.length - 1;
	}
	
	@Override
	public Object clone() {
		GroupMultiBreedingPipeline breedingPipeline = (GroupMultiBreedingPipeline) super.clone();
		
		breedingPipeline.sources = new MLSGroupBreedingPipeline[sources.length];
		
		for (int i = 0; i < sources.length; i++) {
			if (i == 0 || sources[i] != sources[i - 1]) {
				breedingPipeline.sources[i] = (MLSGroupBreedingPipeline) sources[i].clone();
			}
		}
		
		return breedingPipeline;
	}

}
