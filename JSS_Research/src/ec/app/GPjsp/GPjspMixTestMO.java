/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.GPjsp;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.simple.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsp.Job;
import jsp.JSPFramework;
import jsp.Machine;

public class GPjspMixTestMO extends GPProblem implements SimpleProblemForm {
    public static String fitness = "";
    public static String objective = "";

    public JSPData input;

    public JSPFramework[] jspTrainning = new JSPFramework[105];

    public Object clone(){
        GPjspMixTestMO newobj = (GPjspMixTestMO) (super.clone());
        newobj.input = (JSPData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
                for (int i = 0; i < jspTrainning.length; i++) {
                    jspTrainning[i] = new JSPFramework();
                try {
                    jspTrainning[i].getJSPdata(i*2 + 1);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GPjspMixTestMO.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(GPjspMixTestMO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (JSPData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, JSPData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            double[] objectives = ((MultiObjectiveFitness) ind.fitness).getObjectives();
            int hits = 0;
            SmallStatistics[] result = new SmallStatistics[2];
            result[0]= new SmallStatistics();
            result[1]= new SmallStatistics();
            //*
            for (int instance = 0; instance < jspTrainning.length; instance++){
            //start evaluate schedule
                input.abJSP = jspTrainning[instance];
                jspTrainning[instance].reset();
                int N = jspTrainning[instance].getNumberofOperations();
                jspTrainning[instance].initilizeSchedule();
                int nScheduledOp = 0;
                //choose the next machine to be schedule
                while (nScheduledOp<N){
                    Machine M = jspTrainning[instance].Machines[jspTrainning[instance].nextMachine()];
                    input.machine = M;

                    jspTrainning[instance].setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
                    // determine priority of jobs in queue
                    ((GPIndividual)ind).trees[0].child.eval(
                        state,threadnum,input,stack,((GPIndividual)ind),this);
                    ///////////////////////////////////////
                    jspTrainning[instance].sortJobInQueue(M);
                    Job J = M.completeJob();
                    if (!J.isCompleted()) jspTrainning[instance].Machines[J.getCurrentMachine()].joinQueue(J);
                    nScheduledOp++;
                }
                    result[0].add(jspTrainning[instance].getDevLBCmax());
                    result[1].add(jspTrainning[instance].getDevREFTotalWeightedTardiness());
                if (jspTrainning[instance].getDevLBCmax()==0) hits++;
            }

            //objectives[0] = (float)(jspTrainning[0].getTotalWeightedTardiness());
            //objectives[1] = (float)(jspTrainning[0].getMeanC());
            objectives[0] = (float)(result[0].getAverage());
            objectives[1] = (float)(result[1].getAverage());
            ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);

            ind.evaluated = true;
            }
     }
        public void finishEvaluating(final EvolutionState state, final int threadnum)
        {
            if (state.generation == 50) {
                MultiObjectiveStatistics myMOStat = (MultiObjectiveStatistics) state.statistics;
                //myMOStat.myFinalStatistic(state, threadnum,this,threadnum, 2, 105);
            }
        }
        public String getTestPerformance(final EvolutionState state, final int threadnum, Individual ind, int startIndex, int nInstances){
                JSPFramework[] jspTesting = new JSPFramework[nInstances];
                for (int i = 0; i < jspTesting.length; i++) {
                    jspTesting[i] = new JSPFramework();
                    try {
                        jspTesting[i].getJSPdata(i*2 + startIndex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GPjspMixTestMO.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GPjspMixTestMO.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                int hits = 0;
                SmallStatistics[] result = new SmallStatistics[2];
                result[0]= new SmallStatistics();
                result[1]= new SmallStatistics();
                for (int instance = 0; instance < jspTesting.length; instance++){
                //start evaluate schedule
                    input.abJSP = jspTesting[instance];
                    jspTesting[instance].reset();
                    int N = jspTesting[instance].getNumberofOperations();
                    jspTesting[instance].initilizeSchedule();
                    int nScheduledOp = 0;
                    //choose the next machine to be schedule
                    while (nScheduledOp<N){
                        Machine M = jspTesting[instance].Machines[jspTesting[instance].nextMachine()];
                        input.machine = M;

                        jspTesting[instance].setScheduleStrategy(Machine.scheduleStrategy.HYBRID);
                        // determine priority of jobs in queue
                        ((GPIndividual)ind).trees[0].child.eval(
                            state,threadnum,input,stack,((GPIndividual)ind),this);
                        ///////////////////////////////////////
                        jspTesting[instance].sortJobInQueue(M);
                        Job J = M.completeJob();
                        if (!J.isCompleted()) jspTesting[instance].Machines[J.getCurrentMachine()].joinQueue(J);
                        nScheduledOp++;
                    }
                        result[0].add(jspTesting[instance].getDevLBCmax());
                        result[1].add(jspTesting[instance].getDevREFTotalWeightedTardiness());
                    if (jspTesting[instance].getDevLBCmax()==0) hits++;
                }
                return result[0].getAverage() + " " + result[1].getAverage();
        }
}

