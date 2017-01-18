/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

package ec.app.QCSP.Core;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.util.Ranking;

/**
*
* @author nguyensu
*/
public class EnumerateMOqcsp {
    public static void main (String [] args) throws IOException {
        int nObj = 2;
        DecimalFormat df2 = new DecimalFormat( "000" );
        QCSPmo qcsp = new QCSPmo(QCSPmo.dataset[0] + df2.format(1 + 1) + ".txt");
        qcsp.LOCAL_MODE = true;
        qcsp.unidirection = false;
        SolutionSet myset = new  SolutionSet(1000);
        int count = 0; int n = 0;
        for (Iterator i = new Permute(Permute.set(qcsp.n)); i.hasNext(); ) {
            n++;
            final String [] a = (String []) i.next();
            qcsp.convertToPenalty(convertToIntArray(a));
            qcsp.constructSchedule();
            double[] obj = qcsp.obj;
            boolean thesame = false;
            if (myset.size()==0) thesame = false;
            for (int j = 0; j < myset.size(); j++) {
                thesame = true;
                for (int o = 0; o < obj.length; o++) {
                    if (myset.get(j).getObjective(o)!=obj[o]){
                        thesame = false;
                        break;
                    }
                }
                if (thesame) {
                    break;
                }
            }

            if (!thesame) {
                Solution sol = new Solution(nObj);
                for (int o = 0; o<nObj;o++) sol.setObjective(o, obj[o]);
                myset.add(sol);
                count++;
                if (count>=100){
                    Ranking rank = new Ranking(myset); myset.clear();
                    for (int j = 0; j < rank.getSubfront(0).size(); j++) {
                        myset.add(rank.getSubfront(0).get(j));
                    }
                    count=0;
                }
            }
        }
        Ranking rank = new Ranking(myset);
        myset = rank.getSubfront(0);
        printNDset(myset);
        System.out.println("nEval = " + n +", Done!!!!");
    }
    public static int[] convertToIntArray(String[] s){
        int[] n = new int[s.length];
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(s[i]);
        }
        return n;
    }
    public static void printNDset(SolutionSet set){
        for (int i = 0; i < set.size(); i++) {
            for (int j = 0; j < set.get(i).numberOfObjectives(); j++) {
                System.out.print(set.get(i).getObjective(j) + " ");
            }
            System.out.println("");
        }
    }
}
