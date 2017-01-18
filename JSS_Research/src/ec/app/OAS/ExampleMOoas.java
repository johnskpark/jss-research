package ec.app.OAS;

/* Copyright 2009-2012 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */

import ec.app.QCSP.Core.*;
import java.io.IOException;
import java.text.DecimalFormat;
import jmetal.base.Algorithm;
import jmetal.base.Variable;
import jsp.LocalSearchJSPFramework;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.EpsilonMOEA;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Demonstrates how a new problem is defined and used within the MOEA
 * Framework.
 */
public class ExampleMOoas {

	/**
	 * Implementation of the DTLZ2 function.
	 */
    public static class OASproblem extends AbstractProblem {
        public static double[][] initInd;
        public static int nInit = 0;
        public static int count = 0;
        static OASUniform oas;
        /**
         * Constructs a new instance of the DTLZ2 function, defining it
         * to include 11 decision variables and 2 objectives.
         */
        public OASproblem() {
            super(oas.n+1, 2);
        }

        /**
         * Constructs a new solution and defines the bounds of the decision
         * variables.
         */
        @Override
        public Solution newSolution() {
                Solution solution = new Solution(getNumberOfVariables(),
                                getNumberOfObjectives());

                for (int i = 0; i < getNumberOfVariables(); i++) {
                    solution.setVariable(i, new RealVariable(0.0, 1.0));
                }
                return solution;
        }

        /**
         * Extracts the decision variables from the solution, evaluates the
         * Rosenbrock function, and saves the resulting objective value back to
         * the solution.
         */
        @Override
        public void evaluate(Solution solution) {
            double[] x = EncodingUtils.getReal(solution);
            if (count<nInit) {
                x = initInd[count];
                for (int i = 0; i < getNumberOfVariables(); i++) {
                    EncodingUtils.setReal(solution, x);
                }
                count++;
            }
            boolean ls = false; int objIndex = -1;
            //if (oas.rnd.nextDouble()<-1.05) {
            //    ls = true; objIndex = oas.rnd.nextInt(2);
            //}
            double[] r = oas.generateMOSchedule(x,ls,objIndex);
            double[] obj = {-r[0],r[1]};
            solution.setObjectives(obj);
            //System.out.println(-obj[0] + " " + obj[1]);
        }
    }
    public ExampleMOoas(int ps, OASUniform oas){
        popsize = ps;
        OASproblem.oas = oas;
        OASproblem.initInd = new double[ps][oas.n];
        OASproblem.count = 0;
    }
    public static int popsize = 500;
    public static void main(String[] args) throws IOException {
        test();
        //solve(true,222);
    }
    public static void test() throws IOException{
        int[] tNN = {100,50,25}; int[] tTTao = {1,3,5,7,9}; int[] tRR = {1,3,5,7,9}; int[] tINS = {1,2,3,4,5,6,7,8,9,10};
        //int[] tNN = {100}; int[] tTTao = {9}; int[] tRR = {9}; int[] tINS = {1};
        OASUniform[][][][] test = new OASUniform[tNN.length][tTTao.length][tRR.length][tINS.length];
        int REP = 1;
        long[][][][][] TIME = new long[tNN.length][tTTao.length][tRR.length][tINS.length][REP];
        String result = "";
        for (int i = 0; i < test.length; i++) {
            for (int j = 0; j < test[i].length; j++) {
                for (int k = 0; k < test[i][j].length; k++) {
                    for (int l = 0; l < test[i][j][k].length; l++) {
                        test[i][j][k][l] = new OASUniform(1, tNN[i], tTTao[j], tRR[k], tINS[l]);
                        for (int r = 0; r < REP; r++) {
                            long runningTime = System.currentTimeMillis();
                            ExampleMOoas oas = new ExampleMOoas(500, test[i][j][k][l]);
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
        System.out.println(result);
    }
    public static NondominatedPopulation solve(boolean newone, int seed) throws IOException {
        //System.out.println("====================================================");
        if (newone) {
            OASproblem.oas = new OASUniform(111,5,5,5,1);
            OASproblem.count = 0;
        }
        Executor ex = new Executor();

        ex.withProblemClass(OASproblem.class);
        ex.withAlgorithm("NSGAII");
        ex.withMaxEvaluations(100000);
        ex.withProperty("populationSize", popsize); //("pmx.rate", 1.0) ("insertion.rate", 0.3) ("swap.rate", 0.3)
        ex.withProperty("tournament", 2);
        PRNG.setSeed(seed);
        NondominatedPopulation result = ex.run();
        //display the results
        //for (Solution solution : result) {   System.out.print(-solution.getObjective(0));      System.out.print(' ');            System.out.println(solution.getObjective(1));        }
        //System.out.println("====================================================");
        return result;
    }
}