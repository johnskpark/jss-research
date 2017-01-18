package ec.app.QCSP.Core;

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

import java.io.IOException;
import java.text.DecimalFormat;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.problem.AbstractProblem;

/**
 * Demonstrates how a new problem is defined and used within the MOEA
 * Framework.
 */
public class ExampleMO {

	/**
	 * Implementation of the DTLZ2 function.
	 */
    public static class QCSPproblem extends AbstractProblem {
        public static int[][] initInd;
        public static int nInit = 0;
        public static QCSPmo qcsp;
        public static int count = 0;
        /**
         * Constructs a new instance of the DTLZ2 function, defining it
         * to include 11 decision variables and 2 objectives.
         */
        public QCSPproblem() {
            super(1, 2);
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
                    solution.setVariable(i, new Permutation(qcsp.n));
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
            int[] x = EncodingUtils.getPermutation(solution.getVariable(0));
            if (count<nInit) {
                x = initInd[count];
                solution.setVariable(0, new Permutation(x));
                count++;
            }
            qcsp.LOCAL_MODE = true;
            qcsp.convertToPenalty(x);
            qcsp.constructSchedule();
            solution.setObjectives(qcsp.obj);
            //System.out.println(solution.getObjective(0) + " " + solution.getObjective(1));
        }
    }
    public ExampleMO(int ps, QCSPmo qcsp){
        popsize = ps;
        QCSPproblem.qcsp = qcsp;
        QCSPproblem.initInd = new int[ps][qcsp.n];
    }
    public static int popsize = 500;
    public static void main(String[] args) throws IOException {
        solve(true);
    }
    public static void solve(boolean newone) throws IOException {
        System.out.println("====================================================");
        if (newone) {
            DecimalFormat df2 = new DecimalFormat( "000" );
            QCSPmo qcsp = new QCSPmo(QCSPmo.dataset[19] + df2.format(5 + 1) + ".txt");
            qcsp.LOCAL_MODE = true;
            qcsp.unidirection = false;
            QCSPmo.uncertainty = false;
            QCSPmo.noiseLevel = 0.3;
            QCSPproblem.qcsp = qcsp;
            QCSPproblem.count = 0;
        }
        Executor ex = new Executor();
        ex.withProblemClass(QCSPproblem.class);
        ex.withAlgorithm("NSGAII");
        ex.withMaxEvaluations(10000);
        ex.withProperty("populationSize", popsize);
        ex.withProperty("pmx.rate", 1.0);
        ex.withProperty("insertion.rate", 0.5);
        ex.withProperty("swap.rate", 0.5);
        PRNG.setSeed(222);
        NondominatedPopulation result = ex.run();
        //display the results
        for (Solution solution : result) {
            System.out.print(solution.getObjective(0));
            System.out.print(' ');
            System.out.println(solution.getObjective(1));
        }
        System.out.println("====================================================");
    }
}