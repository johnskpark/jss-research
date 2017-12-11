package app.evolution.niched.statistics;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;

public class JasimaNichedStatistics extends SimpleStatistics {

	// TODO this needs to calculate what individuals are what in the archive. 

	@Override
    public void postEvaluationStatistics(final EvolutionState state) {
    	// TODO write down the after generation statistics here. 
		
		// TODO this part also needs to carry out the niching procedure where it sets the individual's fitnesses. 
    }

	@Override
    public void finalStatistics(final EvolutionState state, final int result) {
    	// TODO write down the final statistics here. 
    }
	
}
