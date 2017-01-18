/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.GPjsp.LocalSearch;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.GPjsp.jspData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsp.Job;
import jsp.JSPFramework;
import jsp.LocalSearchJSPFramework;
import jsp.Machine;

public class GPjspLocalSearch extends GPProblem implements SimpleProblemForm {
    public static String fitness = "";
    public static String objective = "";
    //public static MACHINE.priorityType[] pts = {MACHINE.priorityType.FCFS,MACHINE.priorityType.SPT,MACHINE.priorityType.LPT,MACHINE.priorityType.LRM};
    public static Machine.priorityType[] pts = {Machine.priorityType.W_CR_SPT};
    public int MaxSteps = 100;
    public jspData input;
    
    public LocalSearchJSPFramework[] jspTrainning = new LocalSearchJSPFramework[1];
    public Object clone(){
        GPjspLocalSearch newobj = (GPjspLocalSearch) (super.clone());
        newobj.input = (jspData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
                for (int i = 0; i < jspTrainning.length; i++) {
                    jspTrainning[i] = new LocalSearchJSPFramework();
                try {
                    jspTrainning[i].getJSPdata(i*2 + 99);
                    //jspTrainning[i].getJSPdata(212);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GPjspLocalSearch.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GPjspLocalSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (jspData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, jspData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state, 
        final Individual ind, 
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            double dummyNumber = 0;
            int hits = 0;
            SmallStatistics result = new SmallStatistics();
            //*
            for (int instance = 0; instance < jspTrainning.length; instance++){
                for (Machine.priorityType pt:pts) {
                //initialise
                jspTrainning[instance].reset();
                jspTrainning[instance].getRefsolution(pt);
                jspTrainning[instance].storeBestRank();
                double bestObj = jspTrainning[instance].getTotalWeightedTardiness();
                input.abjsp = jspTrainning[instance];
                int count = 0;
                do {
                    //modify the operation rank
                    for (Job job:jspTrainning[instance].Jobs){
                        input.J = job;
                        for (int i = 0; i < input.J.getNumberOperations(); i++) {
                            input.O = input.J.getKthOperation(i);
                            //move selection
                            ((GPIndividual)ind).trees[0].child.eval(
                                state,threadnum,input,stack,((GPIndividual)ind),this);                        
                        }
                    }
                    jspTrainning[instance].reset();
                    jspTrainning[instance].getScheduleFromRankMaTrix();
                    double obj = jspTrainning[instance].getTotalWeightedTardiness();
                    // acceptance criteria
                    input.obj = obj; input.bestObj = bestObj; 
                    input.maxJobTotal = jspTrainning[instance].getMaxTotalJobProcessingTime();
                    input.maxMachineTotal = jspTrainning[instance].getMaxTotalMachineProcessingTime();
                    
                    if (bestObj>obj) {
                        bestObj = obj;
                        jspTrainning[instance].storeBestRank(); 
                    } else {
                        jspTrainning[instance].restoreBestRank(); 
                        jspTrainning[instance].reset();
                        jspTrainning[instance].getScheduleFromRankMaTrix();
                        //break;
                    }
                    count++;
                    if (jspTrainning[instance].getDevLBCmax(bestObj)==0) 
                        break;
                    if (count==MaxSteps) break;
                }while(true);
                //System.out.println(count);
                //if (objective.equals("Cmax"))
                    result.add(jspTrainning[instance].getTotalWeightedTardiness());
                    //result.add(bestObj);
                //else if (objective.equals("TWT")) {
                  //  result.add(jspTrainning[instance].getDevREFTotalWeightedTardiness());
                   // dummyNumber = 100;
               // }
                if (jspTrainning[instance].getDevLBCmax()==0) hits++;
            }}
            //*/
            //System.out.println("Cmax " + jsp.getCmax());
            // the fitness better be KozaFitness!
            KozaFitnessOriginal f = ((KozaFitnessOriginal)ind.fitness);
            //if (fitness.equals("avg")) f.setStandardizedFitness(state,(float)(dummyNumber+result.getAverage()));
            //else if (fitness.equals("max")) f.setStandardizedFitness(state,(float)(dummyNumber+result.getMax()));

            f.setStandardizedFitness(state,(float)(dummyNumber+result.getAverage()));
            f.min = result.getMin();
            f.average = result.getAverage();
            f.max = result.getMax();
            f.hits = hits;

            ind.evaluated = true;
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            if (state.generation == 50) {
                Individual best_i;
                SimpleShortStatistics stats = (SimpleShortStatistics) state.statistics;
                /*
                best_i = state.population.subpops[0].individuals[0];
                for (int y = 0; y < state.population.subpops[0].individuals.length; y++) {
                    // best individual
                    if (state.population.subpops[0].individuals[y].fitness.betterThan(best_i.fitness)) {
                        best_i = state.population.subpops[0].individuals[y];
                    }
                }
                 //*/

                best_i = stats.getBestSoFar()[0];
                //best_i.evaluated=false;
                KozaFitnessOriginal.results+= "\n" + getTestPerformance(state, threadnum, best_i, 82, 10);   //la
                //f.results+= "\n" + getTestPerformance(state, threadnum, best_i, 1, 80);    //dmu
                //f.results+= "\n" + getTestPerformance(state, threadnum, best_i, 121, 3);   //mt
                //f.results+= "\n" + getTestPerformance(state, threadnum, best_i, 124, 10);  //orb
            }
        }
        private String getTestPerformance(final EvolutionState state, final int threadnum, Individual best_i, int startIndex, int nInstances){
                LocalSearchJSPFramework[] jspTesting = new LocalSearchJSPFramework[nInstances];
                for (int i = 0; i < jspTesting.length; i++) {
                    jspTesting[i] = new LocalSearchJSPFramework();
                    try {
                        jspTesting[i].getJSPdata(i*2 + startIndex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GPjspLocalSearch.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GPjspLocalSearch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                jspTrainning = jspTesting;
                int hits = 0;
                SmallStatistics result = new SmallStatistics();
            for (int instance = 0; instance < jspTrainning.length; instance++){
                for (Machine.priorityType pt:pts) {
                //initialise
                jspTrainning[instance].reset();
                jspTrainning[instance].getRefsolution(pt);
                jspTrainning[instance].storeBestRank();
                double bestObj = jspTrainning[instance].getCmax();
                input.abjsp = jspTrainning[instance];
                int count = 0;
                do {
                    //modify the operation rank
                    for (Job job:jspTrainning[instance].Jobs){
                        input.J = job;
                        for (int i = 0; i < input.J.getNumberOperations(); i++) {
                            input.O = input.J.getKthOperation(i);
                            //move selection
                            ((GPIndividual)best_i).trees[0].child.eval(
                                state,threadnum,input,stack,((GPIndividual)best_i),this);                        
                        }
                    }
                    jspTrainning[instance].reset();
                    jspTrainning[instance].getScheduleFromRankMaTrix();
                    double obj = jspTrainning[instance].getCmax();
                    // acceptance criteria
                    input.obj = obj; input.bestObj = bestObj; 
                    input.maxJobTotal = jspTrainning[instance].getMaxTotalJobProcessingTime();
                    input.maxMachineTotal = jspTrainning[instance].getMaxTotalMachineProcessingTime();
                    if (bestObj>obj) {
                        bestObj = obj;
                        jspTrainning[instance].storeBestRank(); 
                    } else {
                        jspTrainning[instance].restoreBestRank(); 
                        jspTrainning[instance].reset();
                        jspTrainning[instance].getScheduleFromRankMaTrix();
                        //break;
                    }
                    count++;
                    if (jspTrainning[instance].getDevLBCmax(bestObj)==0) 
                        break;
                    if (count==MaxSteps) break;
                }while(true);
                //System.out.println(count);
                //if (objective.equals("Cmax"))
                    result.add(jspTrainning[instance].getDevLBCmax(bestObj));
                    //result.add(bestObj);
                //else if (objective.equals("TWT")) {
                  //  result.add(jspTrainning[instance].getDevREFTotalWeightedTardiness());
                   // dummyNumber = 100;
               // }
                if (jspTrainning[instance].getDevLBCmax()==0) hits++;
            }}
                return "Performance on test set: [Average DEV = " + result.getAverage() +
                        ", Min DEV = " + result.getMin() + ", Max DEV = " +
                        result.getMax() + ", Optimal hits = " + hits + "]";
        }
}

