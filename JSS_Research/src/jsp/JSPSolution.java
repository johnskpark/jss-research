/**
 *
 * @author Nguyen Su
 * Framework to develop new dispatching rules for The Job-Shop Scheduling Problem
 * School of Engineering and Computer Science
 * Victoria University of Wellington, New Zealand
 */

package jsp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author nguyensu
 */
public class JSPSolution {
    public int[][] schedule;
    public int[][] schedule_job;
    public int[] EndMachine;
    public double[] JobCompletionTime;
    public double Cmax;
    public boolean[][] connectPreviousOp;
    public ArrayList<int[]> cp;
    public int nBlock=-1;
    public boolean isEvaluated = false;
    public int[] reverseMove=new int[3];
    public int perturbBlock = -1;
    public int perturbPos = -1;
    public double previousHomeCmax=-1;
    public double distancetoReferenceSolution = -1;
    public JSPSolution(boolean localBestSol){
        if (localBestSol) Cmax = Double.POSITIVE_INFINITY;
        reverseMove[0]=-1;
    }
    public JSPSolution(){
        reverseMove[0]=-1;
    }
    public void resetPerturb(){
        perturbBlock = -1;
        perturbPos = -1;
    }
    public void setReverseMove(int m, int u, int mode){
        reverseMove[0]=m;
        reverseMove[1]=schedule[m][u];
        reverseMove[2]=schedule[m][u-mode];
    }
    public boolean matchReverseMove(int m, int u, int mode){
        if (m==reverseMove[0]){
            if (schedule[m][u]==reverseMove[1]&&schedule[m][u-mode]==reverseMove[2])
                return true;
        }
        return false;
    }
    public boolean updateIfImprove(JSPSolution newSol){
       boolean isImproved = newSol.isBetter(this);
       if (isImproved) {
           newSol.copyto(this);
           this.resetPerturb();
       }
       return isImproved;
    }
    public boolean updateIfNotImprove(JSPSolution newSol){
       boolean isImproved = newSol.isBetter(this);
       if (!isImproved) {
           newSol.copyto(this);
           this.resetPerturb();
       }
       return !isImproved;
    }
    public void forceUpdate(JSPSolution newSol){
       newSol.copyto(this);
       this.resetPerturb();
    }
    public boolean isBetter(JSPSolution newSol){
        return Cmax<newSol.Cmax;
    }
    public boolean isEqual(JSPSolution newSol){
        return Cmax==newSol.Cmax;
    }
    public void copyScheduleto(JSPSolution newSol){
        newSol.schedule=copy2DArray(schedule);
        newSol.Cmax=Cmax;
    }
    public void copyto(JSPSolution newSol){
        newSol.schedule=copy2DArray(schedule);
        newSol.schedule_job=copy2DArray(schedule_job);
        newSol.EndMachine = copyArray(EndMachine);
        newSol.JobCompletionTime = copyArray(JobCompletionTime);
        newSol.connectPreviousOp=copy2DArray(connectPreviousOp);
        newSol.Cmax = Cmax;
        newSol.nBlock = nBlock;
        if (cp!=null) newSol.cp=copyArrayList(cp);
        newSol.isEvaluated=isEvaluated;
        newSol.reverseMove=copyArray(reverseMove);
    }
    private ArrayList<int[]> copyArrayList(ArrayList<int[]> a){
        ArrayList<int[]> b = new ArrayList<int[]>();
        for (int i = 0; i < a.size(); i++) {
            int[] arr = new int[a.get(i).length];
            arr = Arrays.copyOf(a.get(i), a.get(i).length);
            b.add(arr);
        }
        return b;
    }
    private boolean[][] copy2DArray(boolean[][] a){
        boolean[][] b=new boolean[a.length][];
        for (int m = 0; m < a.length; m++) {
            b[m] = new boolean[a[m].length];
            b[m] = Arrays.copyOf(a[m], a[m].length);
        }
        return b;
    }
    private int[][] copy2DArray(int[][] a){
        int[][] b=new int[a.length][];
        for (int m = 0; m < a.length; m++) {
            b[m] = new int[a[m].length];
            b[m] = Arrays.copyOf(a[m], a[m].length);
        }
        return b;
    }
    private int[] copyArray(int[] a){
        return Arrays.copyOf(a, a.length);
    }
    private double[] copyArray(double[] a){
        return Arrays.copyOf(a, a.length);
    }
    public boolean checkPredAdjacent(int machine,int pos){
        return !connectPreviousOp[machine][pos];
    }
    public boolean checkSucAdjacent(int machine,int pos){
        return !connectPreviousOp[machine][pos+1];
    }
    public boolean compareCompletionTimeWithMJob(int machine,int pos, int relativeIndex){ //return true if its lateness is greater than other jobs on the same machine given a relative index distance
        return JobCompletionTime[schedule[machine][pos]]<JobCompletionTime[schedule[machine][pos+relativeIndex]];
    }
}
