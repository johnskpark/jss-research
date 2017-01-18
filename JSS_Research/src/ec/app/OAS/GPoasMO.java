/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.OAS;
import SmallStatistics.SmallStatistics;
import ec.util.*;
import ec.*;
import ec.app.OAS.ExampleMOoas.OASproblem;
import ec.gp.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatisticsSu;
import ec.simple.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import org.moeaframework.core.NondominatedPopulation;


public class GPoasMO extends GPProblem implements SimpleProblemForm {
    int[] NN = {100}; int[] TTao = {1,5,9}; int[] RR = {1,5,9}; int[] INS = {1,2,3,4,5};
    public oasData input;
    OASUniform[][][][] train;
    public Object clone(){
        GPoasMO newobj = (GPoasMO) (super.clone());
        newobj.input = (oasData)(input.clone());
        return newobj;
    }

    public void setup(final EvolutionState state,final Parameter base) {
        // very important, remember this
        super.setup(state,base);
            //input training data;
        train = new OASUniform[NN.length][TTao.length][RR.length][INS.length];
        for (int i = 0; i < train.length; i++) {
            for (int j = 0; j < train[i].length; j++) {
                for (int k = 0; k < train[i][j].length; k++) {
                    for (int l = 0; l < train[i][j][k].length; l++) {
                        try {
                            train[i][j][k][l] = new OASUniform(1, NN[i], TTao[j], RR[k], INS[l]);
                        } catch (IOException ex) {
                            Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        try {
            train[0][0][0][0].getRefSolution();
        } catch (IOException ex) {
            Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
        }
        // set up our input -- don't want to use the default base, it's unsafe here
        input = (oasData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, oasData.class);
        input.setup(state,base.push(P_DATA));
    }

    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
                double[] objectives = ((MultiObjectiveFitness)ind.fitness).getObjectives();
                SmallStatistics[] result = new SmallStatistics[2];
                for (int i = 0; i < result.length; i++) {
                    result[i] = new SmallStatistics();
                }
                for (int i = 0; i < train.length; i++) {
                    for (int j = 0; j < train[i].length; j++) {
                        for (int k = 0; k < train[i][j].length; k++) {
                            for (int l = 0; l < train[i][j][k].length; l++) {
                                train[i][j][k][l].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                                for (int m = 0; m < ((GPIndividual) ind).trees.length; m++) {
                                    double[] obj = train[i][j][k][l].generateActiveGPSchedule(0);
                                    result[0].add(-obj[0]); result[1].add(obj[1]);
                                }
                            }
                        }

                    }
                }
                for (int i = 0; i < objectives.length; i++) {
                    objectives[i] = (float) result[i].getAverage();
                }
                ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
                ind.evaluated = true;
                //System.out.print("|");
            }
     }
    public void finishEvaluating(final EvolutionState state, final int threadnum)
    {
        System.out.println("*");
        if (state.generation == state.numGenerations-1) {
            MultiObjectiveStatisticsSu myMOStat = (MultiObjectiveStatisticsSu) state.statistics;
            try {
                myMOStat.myFinalStatistic(state, threadnum, this, threadnum, 2, 105);
            } catch (IOException ex) {
                Logger.getLogger(GPoasMO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public String getTestPerformance(final EvolutionState state, final int threadnum, Individual[] ind, int startIndex, int nInstances) throws IOException{
        int[] tNN = {100,50,25}; int[] tTTao = {1,3,5,7,9}; int[] tRR = {1,3,5,7,9}; int[] tINS = {1,2,3,4,5,6,7,8,9,10}; //10,15,20,25,50,
        //int[] tNN = {25}; int[] tTTao = {1,3}; int[] tRR = {1,3}; int[] tINS = {1,2}; //10,15,20,25,50,
        OASUniform[][][][] test = new OASUniform[tNN.length][tTTao.length][tRR.length][tINS.length];
        for (int i = 0; i < test.length; i++) {
            for (int j = 0; j < test[i].length; j++) {
                for (int k = 0; k < test[i][j].length; k++) {
                    for (int l = 0; l < test[i][j][k].length; l++) {
                        try {
                            test[i][j][k][l] = new OASUniform(1, tNN[i], tTTao[j], tRR[k], tINS[l]);
                        } catch (IOException ex) {
                            Logger.getLogger(GPoas.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        int REP = 1;
        long[][][][][] TIME = new long[tNN.length][tTTao.length][tRR.length][tINS.length][REP];
        String result = "";
        for (int i = 0; i < test.length; i++) {
            for (int j = 0; j < test[i].length; j++) {
                for (int k = 0; k < test[i][j].length; k++) {
                    for (int l = 0; l < test[i][j][k].length; l++) {
                        for (int r = 0; r < REP; r++) {
                            String instName  = "*"+tNN[i]+"*"+tTTao[j]+"*"+tRR[k]+"*"+tINS[l]+"*";
                            System.out.println(instName + "====================================================");
                            ExampleMOoas oas = new ExampleMOoas(500, test[i][j][k][l]);
                            long runningTime = System.currentTimeMillis();
                            SolutionSet myset = new  SolutionSet(ind.length);
                            OASproblem.nInit = 0;
                            for (int x = 0; x < ind.length; x++) {
                                test[i][j][k][l].setupGPIndividual((GPIndividual) ind[x],input, threadnum, state, stack, this);
                                double[] obj = test[i][j][k][l].generateActiveGPSchedule(0);
                                boolean thesame = false;
                                if (myset.size()==0) thesame = false;
                                for (int ref = 0; ref < myset.size(); ref++) {
                                    for (int o = 0; o < obj.length; o++) {
                                        if (myset.get(ref).getObjective(o)!=obj[o]){
                                            break;
                                        }
                                        thesame = true;
                                    }
                                    if (thesame) {
                                        break;
                                    }
                                }
                                if (!thesame) {
                                    Solution sol = new Solution(2);
                                    for (int o = 0; o<2;o++) sol.setObjective(o, obj[o]);
                                    myset.add(sol);
                                    OASproblem.initInd[OASproblem.nInit] = test[i][j][k][l].priority;
                                    OASproblem.nInit++;
                                }
                            }
                            //printNDset(myset);
                            NondominatedPopulation pareto = ExampleMOoas.solve(false,r);
                            String paretoStr = "";
                            for (org.moeaframework.core.Solution solution : pareto) {
                                paretoStr += -solution.getObjective(0) + " " + solution.getObjective(1) + " ";
                            }
                            TIME[i][j][k][l][r] = (System.currentTimeMillis()-runningTime)/1000;
                            result += "Instance " + tNN[i] + "/" + tTTao[j] + "/" + tRR[k] + "/" + tINS[l] + "/"  + r + "/"  + ": "  + TIME[i][j][k][l][r] + "\n";
                            result += paretoStr + "\n";
                        }
                    }
                }
            }
        }
        return result;
    }
    public void printNDset (SolutionSet myset){
        for (int i = 0; i < myset.size(); i++) {
            for (int j = 0; j < 2; j++) {
                if (j==0) System.out.print(myset.get(i).getObjective(j) + " ");
                else System.out.print(myset.get(i).getObjective(j) + " ");
            }
            System.out.println("");
        }
    }
}
/*
    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)
    {
        if (!ind.evaluated)  // don't bother reevaluating
            {
                double[] objectives = ((MultiObjectiveFitness)ind.fitness).getObjectives();
                SmallStatistics[] result = new SmallStatistics[2];
                result[0] = new SmallStatistics(); result[1] = new SmallStatistics();
                for (int i = 0; i < train.length; i++) {
                    for (int j = 0; j < train[i].length; j++) {
                        for (int k = 0; k < train[i][j].length; k++) {
                            for (int l = 0; l < train[i][j][k].length; l++) {
                                train[i][j][k][l].setupGPIndividual((GPIndividual) ind,input, threadnum, state, stack, this);
                                for (int m = 0; m < ((GPIndividual) ind).trees.length; m++) {
                                    double[] obj = train[i][j][k][l].generateMOGPSchedule(m);
                                    for (int g = 0; g < result.length; g++) {
                                        //System.out.println(obj[g] + " ");
                                        if (g==0) result[g].add(-obj[g]);
                                        else result[g].add(obj[g]);
                                    }
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < objectives.length; i++) {
                    objectives[i] = (float) result[i].getAverage();
                }
                ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
                ind.evaluated = true;
                //System.out.print("|");
            }
     }
 *
 */