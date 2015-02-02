/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */


package ec.app.mmHH;
import ec.Fitness;
import ec.Individual;
import ec.simple.*;
import ec.EvolutionState;
import ec.util.*;

import java.io.*;

/*
 * SimpleFitness.java
 *
 * Created: Tue Aug 10 20:10:42 1999
 * By: Sean Luke
 */

/**
 * A simple default fitness, consisting of a single floating-point value
 * where fitness A is superior to fitness B if and only if A > B.
 * Fitness values may range from (-infinity,infinity) exclusive -- that is,
 * you may not have infinite fitnesses.
 *
 * <p>Some kinds of selection methods require a more stringent definition of
 * fitness.  For example, FitProportionateSelection requires that fitnesses
 * be non-negative (since it must place them into a proportionate distribution).
 * You may wish to restrict yourself to values in [0,1] or [0,infinity) in
 * such cases.
 *
 <p><b>Default Base</b><br>
 simple.fitness

 * @author Sean Luke
 * @version 1.0
 */

public class HHFitness extends Fitness
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected float fitness;
	protected boolean isIdeal;
	protected float testFitness;

	public Parameter defaultBase()
	{
		return SimpleDefaults.base().push(P_FITNESS);
	}

	/**
       Deprecated -- now redefined to set the fitness but ALWAYS say that it's not ideal.
       If you need to specify that it's ideal, you should use the new function
       setFitness(final EvolutionState state, float _f, boolean _isIdeal).
       @deprecated
	 */
	public void setFitness(final EvolutionState state, float _f)
	{
		setFitness(state,_f,false);
	}

	public void setFitness(final EvolutionState state, float _f, boolean _isIdeal)
	{
		// we now allow f to be *any* value, positive or negative
		if (_f == Float.POSITIVE_INFINITY || _f == Float.NEGATIVE_INFINITY || Float.isNaN(_f))
		{
			state.output.warning("Bad fitness: " + _f + ", setting to 0.");
			fitness = 0;
		}
		else fitness = _f;
		isIdeal = _isIdeal;
	}

	public double fitness()
	{
		return fitness;
	}

	public void setup(final EvolutionState state, Parameter base)
	{
		super.setup(state,base);  // unnecessary but what the heck
	}

	public boolean isIdealFitness()
	{
		return isIdeal;
	}

	public boolean equivalentTo(final Fitness _fitness)
	{
		return _fitness.fitness() == fitness();
	}

	public boolean betterThan(final Fitness _fitness)
	{
		return _fitness.fitness() > fitness();
	}

	public String fitnessToString()
	{
		return FITNESS_PREAMBLE + Code.encode(fitness());
	}

	public String fitnessToStringForHumans()
	{
		return FITNESS_PREAMBLE + fitness();
	}

	/** Presently does not decode the fact that the fitness is ideal or not */
	public void readFitness(final EvolutionState state,
			final LineNumberReader reader)
					throws IOException
					{
		setFitness(state, Code.readFloatWithPreamble(FITNESS_PREAMBLE, state, reader));

		/*
  int linenumber = reader.getLineNumber();
  String s = reader.readLine();
  if (s==null || s.length() < FITNESS_PREAMBLE.length()) // uh oh
  state.output.fatal("Reading Line " + linenumber + ": " +
  "Bad Fitness.");
  DecodeReturn d = new DecodeReturn(s, FITNESS_PREAMBLE.length());
  Code.decode(d);
  if (d.type!=DecodeReturn.T_FLOAT)
  state.output.fatal("Reading Line " + linenumber + ": " +
  "Bad Fitness.");
  setFitness(state,(float)d.d,false);
		 */
					}

	public void writeFitness(final EvolutionState state,
			final DataOutput dataOutput) throws IOException
			{
		dataOutput.writeFloat(fitness);
		dataOutput.writeBoolean(isIdeal);
		writeTrials(state, dataOutput);
			}

	public void readFitness(final EvolutionState state,
			final DataInput dataInput) throws IOException
			{
		fitness = dataInput.readFloat();
		isIdeal = dataInput.readBoolean();
		readTrials(state, dataInput);
			}

	public void setToMeanOf(EvolutionState state, Fitness[] fitnesses)
	{
		// this is not numerically stable.  Perhaps we should have a numerically stable algorithm for sums
		// we're presuming it's not a very large number of elements, so it's probably not a big deal,
		// since this function is meant to be used mostly for gathering trials together.
		double f = 0;
		boolean ideal = true;
		for(int i = 0; i < fitnesses.length; i++)
		{
			HHFitness fit = (HHFitness)(fitnesses[i]);
			f += fit.fitness;
			ideal = ideal && fit.isIdeal;
		}
		f /= fitnesses.length;
		fitness = (float)f;
		isIdeal = ideal;
	}

	/* For test evaluation */
	public void setTestFitness(final EvolutionState state, float _f, boolean _isIdeal)
	{
		// we now allow f to be *any* value, positive or negative
		if (_f == Float.POSITIVE_INFINITY || _f == Float.NEGATIVE_INFINITY || Float.isNaN(_f))
		{
			state.output.warning("Bad fitness: " + _f + ", setting to 0.");
			testFitness = 0;
		}
		else testFitness = _f;
	}

	public void setTestToMeanOf(EvolutionState state, Fitness[] fitnesses)
	{
		// this is not numerically stable.  Perhaps we should have a numerically stable algorithm for sums
		// we're presuming it's not a very large number of elements, so it's probably not a big deal,
		// since this function is meant to be used mostly for gathering trials together.
		double f = 0;
		boolean ideal = true;
		for(int i = 0; i < fitnesses.length; i++)
		{
			HHFitness fit = (HHFitness)(fitnesses[i]);
			f += fit.fitness;
			ideal = ideal && fit.isIdeal;
		}
		f /= fitnesses.length;
		testFitness = (float)f;
	}

	public float testFitness()
	{
		return testFitness;
	}

	public String testFitnessToString()
	{
		return "Test fitness: " + Code.encode(testFitness());
	}

	public String testFitnessToStringForHumans()
	{
		return "Test fitness: " + testFitness();
	}

	/** Should print the fitness out fashion pleasing for humans to read,
    with a verbosity of Output.V_NO_GENERAL.
	 */
	public void printTestFitnessForHumans(EvolutionState state, int log)
	{
		printTestFitnessForHumans( state, log, Output.V_NO_GENERAL);
	}

	/** Should print the fitness out fashion pleasing for humans to read,
    using state.output.println(...,verbosity,log).  The default version
    of this method calls fitnessToStringForHumans(), adds context (collaborators) if any,
    and printlns the resultant string.
    @deprecated Verbosity no longer has meaning
	 */
	public void printTestFitnessForHumans(EvolutionState state, int log,
			int verbosity)
	{
		String s = testFitnessToStringForHumans();
		if (context != null)
		{
			for(int i = 0; i < context.length; i++)
			{
				if (context[i] != null)
				{
					s += "\nCollaborator " + i + ": ";
					// temporarily de-link the context of the collaborator
					// to avoid loops
					Individual[] c = context[i].fitness.context;
					context[i].fitness.context = null;
					s += context[i].genotypeToStringForHumans();
					// relink
					context[i].fitness.context = c;
				}
				else // that's me!
				{
					// do nothing
				}
			}
		}
		state.output.println( s, verbosity, log);
	}

}

