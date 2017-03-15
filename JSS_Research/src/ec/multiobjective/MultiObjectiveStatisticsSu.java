/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ec.EvolutionState;
import ec.Individual;
import ec.app.GPjsp.Coevolutionary2WayGPIndividual;
import ec.app.GPjsp.DMOCCNSGA_MB_eval;
import ec.app.GPjsp.GPjsp2WayMO;
import ec.app.GPjsp.GPjsp2WayMOCoevolveNSGA;
import ec.app.GPjsp.GPjsp2WayMOCoevolveSPEA;
import ec.app.GPjsp.GPjspMOGPHH;
import ec.app.OAS.GPoasMO;
import ec.multiobjective.nsga2.HaDMOEA2MultiObjectiveFitness;
import ec.multiobjective.nsga2.NSGA2MultiObjectiveFitness;
import ec.multiobjective.spea2.SPEA2MultiObjectiveFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;

/*
 * MultiObjectiveStatistics.java
 *
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 *
 */

/*
 * MultiObjectiveStatistics are a SimpleStatistics subclass which overrides the finalStatistics
 * method to output the current Pareto Front in various ways:
 *
 * <ul>
 * <li><p>Every individual in the Pareto Front is written to the end of the statistics log.
 * <li><p>A summary of the objective values of the Pareto Front is written to stdout.
 * <li><p>The objective values of the Pareto Front are written in tabular form to a special
 * Pareto Front file specified with the parameters below.  This file can be easily read by
 * gnuplot or Excel etc. to display the Front (if it's 2D or perhaps 3D).
 *
 * <p>
 * <b>Parameters</b><br>
 * <table>
 * <tr>
 * <td valign=top><i>base</i>.<tt>front</tt><br>
 * <font size=-1>String (a filename)</font></td>
 * <td valign=top>(The Pareto Front file, if any)</td>
 * </tr>
 * </table>
 */

public class MultiObjectiveStatisticsSu extends MultiObjectiveStatistics
    {
    public static String TestResult = "";
    /** front file parameter */
    public static final String P_PARETO_FRONT_FILE = "front";

    /** The pareto front log */

    public static final int NO_FRONT_LOG = -1;

    public int frontLog;

    public void setup(final EvolutionState state, final Parameter base) {
    	super.setup(state,base);

    	File frontFile = state.parameters.getFile(base.push(P_PARETO_FRONT_FILE),null);

    	if (frontFile!=null) {
    		try {
    			frontLog = state.output.addLog(frontFile, !compress, compress);
    		} catch (IOException i) {
    			state.output.fatal("An IOException occurred while trying to create the log " + frontFile + ":\n" + i);
    		}
    	} else {
    		state.output.warning("No Pareto Front statistics file specified.", base.push(P_PARETO_FRONT_FILE));
    	}
    }


    public void myFinalStatistic(final EvolutionState state,
    		final int result,
    		GPjsp2WayMO gp,
    		int threadnum,
    		int startIndex,
    		int nInstances) {
    	// super.finalStatistics(state,result);
    	// I don't want just a single best fitness
    	int maxParetoSolution = 200;
    	state.output.println("\n\n\n PARETO FRONTS", statisticslog);
    	for (int s = 0; s < state.population.subpops.length; s++) {
    		MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness)(state.population.subpops[s].individuals[0].fitness);
    		state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

    		// build front
    		ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

    		// sort by objective[0]
    		Object[] sortedFront = front.toArray();
    		Individual[] newpop = new Individual[sortedFront.length];
    		for (int i = 0; i < newpop.length; i++) {
    			newpop[i] = (Individual) sortedFront[i];
    		}

    		if (newpop[0].fitness instanceof NSGA2MultiObjectiveFitness) {
    			assignSparsity((Individual[])newpop);
    		} else if (newpop[0].fitness instanceof HaDMOEA2MultiObjectiveFitness) {
    			assignSparsityHaDMOEA(state, newpop);
    		} else {
    			computeAuxiliaryData(state, newpop);
    		}

    		QuickSort.qsort(sortedFront, new SortComparator() {
    			public boolean lt(Object a, Object b) {
    				MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
    				MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

    				return fitnessA.temp > fitnessB.temp;
    			}

    			public boolean gt(Object a, Object b) {
    				MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
    				MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

    				return fitnessA.temp < fitnessB.temp;
    			}
    		});

    		if (sortedFront.length < maxParetoSolution) {
    			maxParetoSolution = sortedFront.length;
    		}

    		// print out header
    		state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
    		String message = "Ind";
    		int numObjectives = typicalFitness.getObjectives().length;
    		for(int i = 0; i < numObjectives; i++) {
    			message += ("\t" + "Objective " + i);
    		}

    		String[] names = typicalFitness.getAuxilliaryFitnessNames();
    		for(int i = 0; i < names.length; i++) {
    			message += ("\t" + names[i]);
    		}
    		state.output.message(message);

    		// write front to screen
    		for (int i = 0; i < maxParetoSolution; i++) {
    			Individual individual = (Individual) (sortedFront[i]);

    			double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();
    			String line = "" + i;
    			for (int f = 0; f < objectives.length; f++) {
    				line += ("\t" + objectives[f]);
    			}

    			double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
    			for(int f = 0; f < vals.length; f++) {
    				line += ("\t" + vals[f]);
    			}
    			//for testing (not in origial ECJ
    			TestResult+=  gp.getTestPerformance(state, threadnum, individual, startIndex, nInstances) + "\n";
    			////////////////////////////////
    			state.output.message(line);
    		}

    		// print out front to statistics log
    		for (int i = 0; i < maxParetoSolution; i++) {
    			((Individual)(sortedFront[i])).printIndividualForHumans(state, statisticslog);
    		}

    		// write short version of front out to disk
    		if (frontLog >= 0) {
    			if (state.population.subpops.length > 1) {
    				state.output.println("Subpopulation " + s, frontLog);
    			}

    			for (int i = 0; i < maxParetoSolution; i++) {
    				Individual ind = (Individual)(sortedFront[i]);
    				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
    				double[] objectives = mof.getObjectives();

    				String line = "";
    				for (int f = 0; f < objectives.length; f++) {
    					line += (objectives[f] + " ");
    				}
    				state.output.println(line, frontLog);
    			}
    		}
    	}
    	state.output.println("\n TestSet Result", frontLog);
    	state.output.println(TestResult, frontLog);
    }

    public void myFinalStatistic(final EvolutionState state,
    		final int result,
    		GPjspMOGPHH gp,
    		int threadnum,
    		int startIndex,
    		int nInstances) {
    	// super.finalStatistics(state,result);
    	// I don't want just a single best fitness
    	int maxParetoSolution = 200;
    	state.output.println("\n\n\n PARETO FRONTS", statisticslog);
    	for (int s = 0; s < state.population.subpops.length; s++) {
    		MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness) state.population.subpops[s].individuals[0].fitness;
    		state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

    		// build front
    		ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

    		// sort by objective[0]
    		Object[] sortedFront = front.toArray();
    		Individual[] newpop = new Individual[sortedFront.length];
    		for (int i = 0; i < newpop.length; i++) {
    			newpop[i] = (Individual) sortedFront[i];
    		}

    		if (newpop[0].fitness instanceof NSGA2MultiObjectiveFitness) {
    			assignSparsity((Individual[]) newpop);
    		} else if (newpop[0].fitness instanceof HaDMOEA2MultiObjectiveFitness) {
    			assignSparsityHaDMOEA(state, newpop);
    		} else {
    			computeAuxiliaryData(state, newpop);
    		}

    		QuickSort.qsort(sortedFront, new SortComparator() {
    			public boolean lt(Object a, Object b) {
    				MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
    				MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

    				return fitnessA.temp > fitnessB.temp;
    			}

    			public boolean gt(Object a, Object b) {
    				MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
    				MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

    				return fitnessA.temp < fitnessB.temp;
    			}
    		});

    		if (sortedFront.length < maxParetoSolution) {
    			maxParetoSolution = sortedFront.length;
    		}

    		// print out header
    		state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
    		String message = "Ind";
    		int numObjectives = typicalFitness.getObjectives().length;
    		for(int i = 0; i < numObjectives; i++) {
    			message += ("\t" + "Objective " + i);
    		}

    		String[] names = typicalFitness.getAuxilliaryFitnessNames();
    		for(int i = 0; i < names.length; i++) {
    			message += ("\t" + names[i]);
    		}
    		state.output.message(message);

    		// write front to screen
    		for (int i = 0; i < maxParetoSolution; i++) {
    			Individual individual = (Individual) (sortedFront[i]);

    			double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();
    			String line = "" + i;
    			for (int f = 0; f < objectives.length; f++) {
    				line += ("\t" + objectives[f]);
    			}

    			double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
    			for(int f = 0; f < vals.length; f++) {
    				line += ("\t" + vals[f]);
    			}
    			//for testing (not in origial ECJ
    			TestResult+=  gp.getTestPerformance(state, threadnum, individual, startIndex, nInstances) + "\n";
    			////////////////////////////////
    			state.output.message(line);
    		}

    		// print out front to statistics log
    		for (int i = 0; i < maxParetoSolution; i++) {
    			((Individual) sortedFront[i]).printIndividualForHumans(state, statisticslog);
    		}

    		// write short version of front out to disk
    		if (frontLog >= 0) {
    			if (state.population.subpops.length > 1) {
    				state.output.println("Subpopulation " + s, frontLog);
    			}

    			for (int i = 0; i < maxParetoSolution; i++) {
    				Individual ind = (Individual)(sortedFront[i]);
    				MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
    				double[] objectives = mof.getObjectives();

    				String line = "";
    				for (int f = 0; f < objectives.length; f++) {
    					line += (objectives[f] + " ");
    				}
    				state.output.println(line, frontLog);
    			}
    		}
    	}
    	state.output.println("\n TestSet Result", frontLog);
    	state.output.println(TestResult, frontLog);
    }

    public void myFinalStatistic(final EvolutionState state,
    		final int result,
    		GPoasMO gp,
    		int threadnum,
    		int startIndex,
    		int nInstances) throws IOException {
        // super.finalStatistics(state,result);
        // I don't want just a single best fitness
        int maxParetoSolution = 200;
        state.output.println("\n\n\n PARETO FRONTS", statisticslog);
        for (int s = 0; s < state.population.subpops.length; s++) {
            MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness) state.population.subpops[s].individuals[0].fitness;
            state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

            // build front
            ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

            // sort by objective[0]
            Object[] sortedFront = front.toArray();
            Individual[] newpop = new Individual[sortedFront.length];
            for (int i = 0; i < newpop.length; i++) {
                newpop[i] = (Individual) sortedFront[i];
            }

            if (newpop[0].fitness instanceof NSGA2MultiObjectiveFitness) {
            	assignSparsity((Individual[])newpop);
            } else if (newpop[0].fitness instanceof HaDMOEA2MultiObjectiveFitness) {
            	assignSparsityHaDMOEA(state, newpop);
            } else {
            	computeAuxiliaryData(state, newpop);
            }

            QuickSort.qsort(sortedFront, new SortComparator() {
            	public boolean lt(Object a, Object b) {
            		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
            		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

            		return fitnessA.temp > fitnessB.temp;
            	}

            	public boolean gt(Object a, Object b) {
            		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
            		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

            		return fitnessA.temp < fitnessB.temp;
            	}
            });

            if (sortedFront.length<maxParetoSolution) {
            	maxParetoSolution = sortedFront.length;
            }

            // print out header
            state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
            String message = "Ind";
            int numObjectives = typicalFitness.getObjectives().length;
            for(int i = 0; i < numObjectives; i++) {
                message += ("\t" + "Objective " + i);
            }

            String[] names = typicalFitness.getAuxilliaryFitnessNames();
            for(int i = 0; i < names.length; i++) {
                message += ("\t" + names[i]);
            }

            state.output.message(message);

            // write front to screen
            Individual[] ndSET = new Individual[min(maxParetoSolution,sortedFront.length)];
            for (int i = 0; i < maxParetoSolution; i++) {
            	Individual individual = (Individual) sortedFront[i];
            	ndSET[i] = (Individual) sortedFront[i];
            	double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();
            	String line = "" + i;
            	for (int f = 0; f < objectives.length; f++) {
            		line += ("\t" + objectives[f]);
            	}

            	double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
            	for(int f = 0; f < vals.length; f++) {
            		line += ("\t" + vals[f]);
            	}

            	////////////////////////////////
            	state.output.message(line);
            }

            //for testing (not in origial ECJ
            TestResult +=  gp.getTestPerformance(state, threadnum, ndSET, startIndex, nInstances) + "\n";

            // print out front to statistics log
            for (int i = 0; i < maxParetoSolution; i++) {
                ((Individual) sortedFront[i]).printIndividualForHumans(state, statisticslog);
            }

            // write short version of front out to disk
            if (frontLog >= 0) {
            	if (state.population.subpops.length > 1) {
            		state.output.println("Subpopulation " + s, frontLog);
            	}

            	for (int i = 0; i < maxParetoSolution; i++) {
            		Individual ind = (Individual) sortedFront[i];
            		MultiObjectiveFitness mof = (MultiObjectiveFitness) ind.fitness;
            		double[] objectives = mof.getObjectives();

            		String line = "";
            		for (int f = 0; f < objectives.length; f++) {
            			line += (objectives[f] + " ");
            		}
            		state.output.println(line, frontLog);
            	}
            }
        }
        state.output.println("\n TestSet Result", frontLog);
        state.output.println(TestResult, frontLog);
    }

    /** Logs the best individual of the run. */
    public void finalStatistics(final EvolutionState state, final int result) {

    }

    public void myFinalStatisticCoevolveSPEA(final EvolutionState state, final int result, GPjsp2WayMOCoevolveSPEA gp) {
        // super.finalStatistics(state,result);
        // I don't want just a single best fitness
        int maxParetoSolution = 200;
        Individual[] combinedInds;
        Individual[] sub1 = state.population.subpops[0].individuals;
        Individual[] sub2 = state.population.subpops[1].individuals;
        combinedInds = new Individual[sub1.length + sub2.length];
        System.arraycopy(sub2, 0, combinedInds, 0,  sub2.length);
        System.arraycopy(sub1, 0, combinedInds,  sub2.length, sub1.length);

        state.output.println("\n\n\n PARETO FRONTS", statisticslog);
        //for (int s = 0; s < state.population.subpops.length; s++)
        //{
        MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness)(combinedInds[0].fitness);
        state.output.println("\n\nPareto Front of Subpopulation ", statisticslog);

        // build front
        ArrayList front = typicalFitness.partitionIntoParetoFront(combinedInds, null, null);

        // sort by objective[0]
        Object[] sortedFront = front.toArray();
        Individual[] newpop = new Individual[sortedFront.length];
        for (int i = 0; i < newpop.length; i++) {
        	newpop[i] = (Individual) sortedFront[i];
        }

        //if (newpop[0].fitness instanceof NSGA2MultiObjectiveFitness) assignSparsity((Individual[])newpop);
        //else computeAuxiliaryData(state, newpop);
        //assignSparsity((Individual[])newpop);
        computeAuxiliaryData(state, newpop);
        //assignSparsity((Individual[])newpop);
        QuickSort.qsort(sortedFront, new SortComparator() {
        	public boolean lt(Object a, Object b) {
        		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
        		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

        		return fitnessA.temp > fitnessB.temp;
        	}

        	public boolean gt(Object a, Object b) {
        		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
        		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

        		return fitnessA.temp < fitnessB.temp;
        	}
        });

        if (sortedFront.length<maxParetoSolution) {
        	maxParetoSolution = sortedFront.length;
        }

        // print out header
        state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
        String message = "Ind";
        int numObjectives = typicalFitness.getObjectives().length;
        for (int i = 0; i < numObjectives; i++) {
        	message += ("\t" + "Objective " + i);
        }

        String[] names = typicalFitness.getAuxilliaryFitnessNames();
        for (int i = 0; i < names.length; i++) {
        	message += ("\t" + names[i]);
        }
        state.output.message(message);

        // write front to screen
        for (int i = 0; i < maxParetoSolution; i++) {
        	Individual individual = (Individual) sortedFront[i];
        	double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();

        	String line = "" + i;
        	for (int f = 0; f < objectives.length; f++) {
        		line += ("\t" + objectives[f]);
        	}

        	double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
        	for (int f = 0; f < vals.length; f++) {
        		line += ("\t" + vals[f]);
        	}
        	//for testing (not in origial ECJ
        	Individual[] ind = new Individual[2];
        	if (((Coevolutionary2WayGPIndividual) individual).context[1] != null) {
        		ind[0] = individual;
        		ind[1] = ((Coevolutionary2WayGPIndividual) individual).context[1];
        	} else {
        		ind[1] = individual;
        		ind[0] = ((Coevolutionary2WayGPIndividual) individual).context[0];
        	}
        	TestResult+=  gp.getTestPerformance(state, 0, ind) + "\n";
        	////////////////////////////////
        	state.output.message(line);
        }

        // print out front to statistics log
        for (int i = 0; i < maxParetoSolution; i++) {
        	((Individual) sortedFront[i]).printIndividualForHumans(state, statisticslog);
        }

        // write short version of front out to disk
        if (frontLog >= 0) {
        	if (state.population.subpops.length > 1) {
        		state.output.println("Subpopulation ", frontLog);
        	}

        	for (int i = 0; i < maxParetoSolution; i++) {
        		Individual ind = (Individual) sortedFront[i];
        		MultiObjectiveFitness mof = (MultiObjectiveFitness) ind.fitness;
        		double[] objectives = mof.getObjectives();

        		String line = "";
        		for (int f = 0; f < objectives.length; f++) {
        			line += (objectives[f] + " ");
        		}
        		state.output.println(line, frontLog);
        	}
        }
        //}
        state.output.println("\n TestSet Result", frontLog);
        state.output.println(TestResult, frontLog);
    }

    public void myFinalStatisticCoevolveNSGA(final EvolutionState state, final int result, GPjsp2WayMOCoevolveNSGA gp) {
        // super.finalStatistics(state,result);
        // I don't want just a single best fitness
        int maxParetoSolution = 200;
        Individual[] combinedInds;
        Individual[] sub1 = state.population.subpops[0].individuals;
        Individual[] sub2 = state.population.subpops[1].individuals;
        combinedInds = new Individual[sub1.length + sub2.length];
        System.arraycopy(sub2, 0, combinedInds, 0,  sub2.length);
        System.arraycopy(sub1, 0, combinedInds,  sub2.length, sub1.length);

        state.output.println("\n\n\n PARETO FRONTS", statisticslog);
        //for (int s = 0; s < state.population.subpops.length; s++)
        //{
        MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness) combinedInds[0].fitness;
        state.output.println("\n\nPareto Front of Subpopulation ", statisticslog);

        // build front
        ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.archive, null, null);

        // sort by objective[0]
        Object[] sortedFront = front.toArray();

        Individual[] newpop = new Individual[sortedFront.length];
        for (int i = 0; i < newpop.length; i++) {
        	newpop[i] = (Individual) sortedFront[i];
        }
        //if (newpop[0].fitness instanceof NSGA2MultiObjectiveFitness) assignSparsity((Individual[])newpop);
        //else computeAuxiliaryData(state, newpop);
        assignSparsity((Individual[])newpop);
        //computeAuxiliaryData(state, newpop);
        //assignSparsity((Individual[])newpop);
        QuickSort.qsort(sortedFront, new SortComparator() {
        	public boolean lt(Object a, Object b) {
        		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
        		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

        		return fitnessA.temp > fitnessB.temp;
        	}

        	public boolean gt(Object a, Object b) {
        		MultiObjectiveFitness fitnessA = (MultiObjectiveFitness) ((Individual) a).fitness;
        		MultiObjectiveFitness fitnessB = (MultiObjectiveFitness) ((Individual) b).fitness;

        		return fitnessA.temp < fitnessB.temp;
        	}
        });

        if (sortedFront.length<maxParetoSolution) {
        	maxParetoSolution = sortedFront.length;
        }

        // print out header
        state.output.message("Pareto Front Summary: " + sortedFront.length + " Individuals");
        String message = "Ind";
        int numObjectives = typicalFitness.getObjectives().length;
        for (int i = 0; i < numObjectives; i++) {
        	message += ("\t" + "Objective " + i);
        }

        String[] names = typicalFitness.getAuxilliaryFitnessNames();
        for (int i = 0; i < names.length; i++) {
        	message += ("\t" + names[i]);
        }

        state.output.message(message);

        // write front to screen
        for (int i = 0; i < maxParetoSolution; i++) {
        	Individual individual = (Individual) sortedFront[i];

        	double[] objectives = ((MultiObjectiveFitness) individual.fitness).getObjectives();
        	String line = "" + i;
        	for (int f = 0; f < objectives.length; f++) {
        		line += ("\t" + objectives[f]);
        	}

        	double[] vals = ((MultiObjectiveFitness) individual.fitness).getAuxilliaryFitnessValues();
        	for(int f = 0; f < vals.length; f++) {
        		line += ("\t" + vals[f]);
        	}

        	//for testing (not in origial ECJ
        	Individual[] ind = new Individual[2];
        	if (((Coevolutionary2WayGPIndividual)individual).context[1] != null) {
        		ind[0] = individual;
        		ind[1] = ((Coevolutionary2WayGPIndividual)individual).context[1];
        	} else {
        		ind[1] = individual;
        		ind[0] = ((Coevolutionary2WayGPIndividual)individual).context[0];
        	}
        	TestResult += gp.getTestPerformance(state, 0, ind) + "\n";
        	////////////////////////////////
        	state.output.message(line);
        }

        // print out front to statistics log
        for (int i = 0; i < maxParetoSolution; i++) {
        	((Individual) sortedFront[i]).printIndividualForHumans(state, statisticslog);
        }

        // write short version of front out to disk
        if (frontLog >= 0) {
        	if (state.population.subpops.length > 1) {
        		state.output.println("Subpopulation ", frontLog);
        	}

        	for (int i = 0; i < maxParetoSolution; i++) {
        		Individual ind = (Individual) sortedFront[i];
        		MultiObjectiveFitness mof = (MultiObjectiveFitness) ind.fitness;
        		double[] objectives = mof.getObjectives();

        		String line = "";
        		for (int f = 0; f < objectives.length; f++) {
        			line += (objectives[f] + " ");
        		}
        		state.output.println(line, frontLog);
        	}
        }
        //}
        state.output.println("\n TestSet Result", frontLog);
        state.output.println(TestResult, frontLog);
    }

    public void fullEvaluationStatisticsCoevolveNSGA(final EvolutionState state, final int result, DMOCCNSGA_MB_eval gp) {
    	// Callback to problem to overwrite the individuals in the evolution state.
    	gp.writeToIntermediateFile(state);
    	gp.readFromIntermediateFile(state);

        List<Coevolutionary2WayGPIndividual> subpop1 = Arrays.asList(state.population.subpops[0].individuals)
        		.stream()
        		.map(x -> (Coevolutionary2WayGPIndividual) x)
        		.sorted(new Coevolutionary2WayGPIndividualComparator())
        		.collect(Collectors.toList());
        List<Coevolutionary2WayGPIndividual> subpop2 = Arrays.asList(state.population.subpops[1].individuals)
        		.stream()
        		.map(x -> (Coevolutionary2WayGPIndividual) x)
        		.sorted(new Coevolutionary2WayGPIndividualComparator())
        		.collect(Collectors.toList());

        checkConstraints(state, subpop1, subpop2);

        int paretoSize = subpop1.size();

        state.output.message("Applying rules over training set");
        state.output.println(" Subpopulation", frontLog);

        // Sort based on the seed and the runs.

        for (int i = 0; i < paretoSize; i++) {
        	// Reevaluate the individuals over the training set.
        	Coevolutionary2WayGPIndividual individual = (Coevolutionary2WayGPIndividual) subpop1.get(i);

        	Individual[] inds = new Individual[2];
    		inds[0] = individual;
    		inds[1] = individual.context[1];

        	state.output.println(gp.getTrainingPerformance(state, 0, inds), frontLog);
        	state.output.message("Evaluated individual " + i + " on training");
        }

        state.output.message("Applying rules over test set");
        state.output.println("\n TestSet Result", frontLog);

        for (int i = 0; i < paretoSize; i++) {
        	// Reevaluate the individuals over the test set.
        	Coevolutionary2WayGPIndividual individual = (Coevolutionary2WayGPIndividual) subpop1.get(i);

        	Individual[] inds = new Individual[2];
    		inds[0] = individual;
    		inds[1] = individual.context[1];

    		state.output.println(gp.getTestPerformance(state, 0, inds), frontLog);
        	state.output.message("Evaluated individual " + i + " on test");
        }
    }

    private class Coevolutionary2WayGPIndividualComparator implements Comparator<Coevolutionary2WayGPIndividual> {

    	public int compare(Coevolutionary2WayGPIndividual ind1, Coevolutionary2WayGPIndividual ind2) {
    		int approachComp = ind1.getApproach().compareTo(ind2.getApproach());
    		if (approachComp != 0) {
    			return approachComp;
    		}

    		int seedComp = ind1.getSeed() - ind2.getSeed();
    		if (seedComp != 0) {
    			return seedComp;
    		}

    		return ind1.getRunNum() - ind2.getRunNum();
    	}
    }

    private void checkConstraints(final EvolutionState state, List<Coevolutionary2WayGPIndividual> subpop1, List<Coevolutionary2WayGPIndividual> subpop2) {
    	if (subpop1.size() != subpop2.size()) {
    		state.output.fatal("The two subpopulation's read from the file should be the same length as each other.");
    	}

    	int len = subpop1.size();
    	for (int i = 0; i < len; i++) {
    		Coevolutionary2WayGPIndividual ind1 = (Coevolutionary2WayGPIndividual) subpop1.get(i);
    		Coevolutionary2WayGPIndividual ind2 = (Coevolutionary2WayGPIndividual) subpop2.get(i);

    		if (!ind1.context[1].equals(ind2) || !ind2.context[0].equals(ind1)) {
    			state.output.fatal("The individual's collaborators do not match: " + i);
    		}
    	}
    }


    /**
     * Computes and assigns the sparsity values of a given front.
     */
    public void assignSparsity(Individual[] front) {
        int numObjectives = ((MultiObjectiveFitness) front[0].fitness).getObjectives().length;

        for (int i = 0; i < front.length; i++) {
            ((MultiObjectiveFitness) front[i].fitness).temp = 0;
        }

        for (int i = 0; i < numObjectives; i++) {
            final int o = i;
            // 1. Sort front by each objective.
            // 2. Sum the manhattan distance of an individual's neighbours over
            // each objective.
            // NOTE: No matter which objectives objective you sort by, the
            // first and last individuals will always be the same (they maybe
            // interchanged though). This is because a Pareto front's
            // objective values are strictly increasing/decreasing.
            ec.util.QuickSort.qsort(front, new SortComparator() {
            	public boolean lt(Object a, Object b) {
            		Individual i1 = (Individual) a;
            		Individual i2 = (Individual) b;
            		return (((MultiObjectiveFitness) i1.fitness).getObjective(o) < ((MultiObjectiveFitness) i2.fitness).getObjective(o));
            	}

            	public boolean gt(Object a, Object b) {
            		Individual i1 = (Individual) a;
            		Individual i2 = (Individual) b;
            		return (((MultiObjectiveFitness) i1.fitness).getObjective(o) > ((MultiObjectiveFitness) i2.fitness).getObjective(o));
            	}
            });

            // Compute and assign sparsity.
            // the first and last individuals are the sparsest.
            ((MultiObjectiveFitness) front[0].fitness).temp = Double.POSITIVE_INFINITY;
            if (front[0].fitness instanceof NSGA2MultiObjectiveFitness) {
                ((NSGA2MultiObjectiveFitness)front[0].fitness).sparsity = ((MultiObjectiveFitness) front[0].fitness).temp;
            }

            ((MultiObjectiveFitness) front[front.length - 1].fitness).temp = Double.POSITIVE_INFINITY;
            if (front[front.length - 1].fitness instanceof NSGA2MultiObjectiveFitness) {
                ((NSGA2MultiObjectiveFitness)front[front.length - 1].fitness).sparsity = ((MultiObjectiveFitness) front[front.length - 1].fitness).temp;
            }

            for (int j = 1; j < front.length - 1; j++) {
            	MultiObjectiveFitness f_j = (MultiObjectiveFitness) (front[j].fitness);
            	MultiObjectiveFitness f_jplus1 = (MultiObjectiveFitness) (front[j+1].fitness);
            	MultiObjectiveFitness f_jminus1 = (MultiObjectiveFitness) (front[j-1].fitness);

            	// store the NSGA2Sparsity in sparsity
            	f_j.temp += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (f_j.maxObjective[o] - f_j.minObjective[o]);
            	if (f_j instanceof NSGA2MultiObjectiveFitness) {
            		((NSGA2MultiObjectiveFitness)f_j).sparsity = f_j.temp;
            	}
            }
        }
    }

    public void assignSparsityHaDMOEA(EvolutionState state,Individual[] inds)
        {
        double[][] distances = calculateDistancesHadMOEA(inds);
        // calculate k value
        int kTH = (int) Math.sqrt(inds.length);  // note that the first element is k=1, not k=0
        for (int i = 0; i < inds.length; i++){
                ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).sparsity = 0;
                double harmonicCD = 0;
                for (int j = 0; j < kTH; j++) {
                    double distance_j = Math.sqrt(orderStatistics(distances[i], j+2, state.random[0]));
                    if (distance_j==0) {
                        harmonicCD=Double.POSITIVE_INFINITY;
                        break;
                    }
                    else harmonicCD += 1/distance_j;
                }

                ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).sparsity = (double)kTH/harmonicCD;
                ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).temp = ((HaDMOEA2MultiObjectiveFitness) inds[i].fitness).sparsity;
            }
        }
    public void computeAuxiliaryData(EvolutionState state, Individual[] inds)
        {
        double[][] distances = calculateDistances(state, inds);

        // For each individual calculate the strength
        for(int y=0;y<inds.length;y++)
            {
            // Calculate the node strengths
            int myStrength = 0;
            for(int z=0;z<inds.length;z++)
                if (((SPEA2MultiObjectiveFitness)inds[y].fitness).paretoDominates((MultiObjectiveFitness)inds[z].fitness))
                    myStrength++;
            ((SPEA2MultiObjectiveFitness)inds[y].fitness).strength = myStrength;
            } //For each individual y calculate the strength

        // calculate k value
        int kTH = (int) Math.sqrt(inds.length);  // note that the first element is k=1, not k=0

        // For each individual calculate the Raw fitness and kth-distance
        for(int y=0;y<inds.length;y++)
            {
            double fitness = 0;
            // Set SPEA2 raw fitness value for each individual

            SPEA2MultiObjectiveFitness indYFitness = ((SPEA2MultiObjectiveFitness)inds[y].fitness);

            // Density component

            // calc k-th nearest neighbor distance.
            // distances are squared, so we need to take the square root.
            double kthDistance = Math.sqrt(orderStatistics(distances[y], kTH, state.random[0]));

            // Set SPEA2 k-th NN distance value for each individual
            indYFitness.temp = - 1.0 / ( 2 + kthDistance);
            indYFitness.kthNNDistance = -indYFitness.temp;
            }
        }
    public void computeCoevolveAuxiliaryData(EvolutionState state, Individual[] inds)
        {
        double[][] distances = calculateDistances(state, inds);

        // For each individual calculate the strength
        for(int y=0;y<inds.length;y++)
            {
            // Calculate the node strengths
            int myStrength = 0;
            for(int z=0;z<inds.length;z++)
                if (((SPEA2MultiObjectiveFitness)inds[y].fitness).paretoDominates((MultiObjectiveFitness)inds[z].fitness))
                    myStrength++;
            ((SPEA2MultiObjectiveFitness)inds[y].fitness).strength = myStrength;
            } //For each individual y calculate the strength

        // calculate k value
        int kTH = (int) Math.sqrt(inds.length);  // note that the first element is k=1, not k=0

        // For each individual calculate the Raw fitness and kth-distance
        for(int y=0;y<inds.length;y++)
            {
            double fitness = 0;
            // Set SPEA2 raw fitness value for each individual

            SPEA2MultiObjectiveFitness indYFitness = ((SPEA2MultiObjectiveFitness)inds[y].fitness);

            // Density component

            // calc k-th nearest neighbor distance.
            // distances are squared, so we need to take the square root.
            double kthDistance = Math.sqrt(orderStatistics(distances[y], kTH, state.random[0]));
            int count = 1;
            int tempK = kTH;
            while ((int)tempK/2.0>=1){
                tempK=(int) ((double)tempK/2.0);
                count++;
                kthDistance += Math.sqrt(orderStatistics(distances[y], tempK, state.random[0]));
            }
            kthDistance = kthDistance/(double)count;
            // Set SPEA2 k-th NN distance value for each individual
            indYFitness.temp = - 1.0 / ( 2 + kthDistance);
            indYFitness.kthNNDistance = -indYFitness.temp;
            }
        }
        public void ComputeAverageDistance(EvolutionState state, Individual[] inds)
        {
        double[][] distances = calculateDistances(state, inds);

        // For each individual calculate the strength
        for(int y=0;y<inds.length;y++)
            {
                ((MultiObjectiveFitness)inds[y].fitness).temp = 0;
                // Calculate the node strengths
                int myStrength = 0;
                for(int z=0;z<inds.length;z++){
                    ((MultiObjectiveFitness)inds[y].fitness).temp += distances[y][z];
                }
                ((MultiObjectiveFitness)inds[y].fitness).temp /= (double)inds.length;
            }
        //For each individual y calculate the strength

        }
    public double[][] calculateDistances(EvolutionState state, Individual[] inds)
        {
        double[][] distances = new double[inds.length][inds.length];
        for(int y=0;y<inds.length;y++)
            {
            distances[y][y] = 0;
            for(int z=y+1;z<inds.length;z++)
                {
                distances[z][y] = distances[y][z] =
                    ((MultiObjectiveFitness)inds[y].fitness).
                    sumSquaredObjectiveDistance( (MultiObjectiveFitness)inds[z].fitness );
                }
            }
        return distances;
        }
    public double[][] calculateDistancesHadMOEA(Individual[] inds)
        {
        double[][] distances = new double[inds.length][inds.length];
        int nObj = ((HaDMOEA2MultiObjectiveFitness)inds[0].fitness).getNumObjectives();
        double[] min = new double[nObj];
        double[] max = new double[nObj];
        double[] range = new double[nObj];
        for (int i = 0; i < nObj; i++) {
            min[i] = Double.POSITIVE_INFINITY;
            max[i] = Double.NEGATIVE_INFINITY;
        }
        for (int i = 0; i < inds.length; i++) {
            for (int j = 0; j < nObj; j++) {
                if (((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j)<min[j]) min[j] = ((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j);
                if (((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j)>max[j]) max[j] = ((HaDMOEA2MultiObjectiveFitness)inds[i].fitness).getObjective(j);
            }
        }
        for (int i = 0; i < nObj; i++) {
            range[i] = max[i] - min[i];
        }
        for(int y=0;y<inds.length;y++)
            {
            distances[y][y] = 0;
            for(int z=y+1;z<inds.length;z++)
                {
                distances[z][y] = distances[y][z] =
                    ((HaDMOEA2MultiObjectiveFitness)inds[y].fitness).
                    sumSquaredObjectiveDistance_normalised( (HaDMOEA2MultiObjectiveFitness)inds[z].fitness,range );
                }
            }
        return distances;
        }

    /** Returns the kth smallest element in the array.  Note that here k=1 means the smallest element in the array (not k=0).
        Uses a randomized sorting technique, hence the need for the random number generator. */
    double orderStatistics(double[] array, int kth, MersenneTwisterFast rng)
        {
        return randomizedSelect(array, 0, array.length-1, kth, rng);
        }


    /* OrderStatistics [Cormen, p187]:
     * find the ith smallest element of the array between indices p and r */
    double randomizedSelect(double[] array, int p, int r, int i, MersenneTwisterFast rng)
        {
        if(p==r) return array[p];
        int q = randomizedPartition(array, p, r, rng);
        int k = q-p+1;
        if(i<=k)
            return randomizedSelect(array, p, q, i,rng);
        else
            return randomizedSelect(array, q+1, r, i-k,rng);
        }


    /* [Cormen, p162] */
    int randomizedPartition(double[] array, int p, int r, MersenneTwisterFast rng)
        {
        int i = rng.nextInt(r-p+1)+p;

        //exchange array[p]<->array[i]
        double tmp = array[i];
        array[i]=array[p];
        array[p]=tmp;
        return partition(array,p,r);
        }


    /* [cormen p 154] */
    int partition(double[] array, int p, int r)
        {
        double x = array[p];
        int i = p-1;
        int j = r+1;
        while(true)
            {
            do j--; while(array[j]>x);
            do i++; while(array[i]<x);
            if ( i < j )
                {
                //exchange array[i]<->array[j]
                double tmp = array[i];
                array[i]=array[j];
                array[j]=tmp;
                }
            else
                return j;
            }
        }
    int min(int a, int b){
        if (a<b) return a;
        else return b;
    }
}

