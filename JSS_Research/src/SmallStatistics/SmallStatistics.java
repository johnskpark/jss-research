/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SmallStatistics;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author nguyensu
 */
public class SmallStatistics {

    NumberFormat formatter = new DecimalFormat("#0.000");
    //track experiment
    public int f;
    public int p;
    public int r;
    public int seed;
    //end track
    private double sum = 0;
    private double sumQ = 0;
    private double max = Double.NEGATIVE_INFINITY;
    private double min = Double.POSITIVE_INFINITY;

    public SmallStatistics(){

    }

    private double count=0;

    public void reset() {
        sum = 0;
        sumQ = 0;
        max = Double.NEGATIVE_INFINITY;
        min = Double.POSITIVE_INFINITY;
        count=0;
    }

    public void add(double val) {
        sum += val;
        sumQ += val * val;
        count ++;
        if (val < min) { min=val; }
        if (val > max) { max=val; }
    }

    public double getLength() {
        return count;
    }

    public double getAverage() {
        return sum / count;
    }

    public double getMeanSquare() {
        return sumQ / count;
    }

    public double getVariance() {
        return sumQ / count - Math.pow(sum / count, 2);
    }

    public double getUnBiasedVariance() {
        return getVariance() * count / (count - 1);
    }

    public double getUnbiasedStandardDeviation() {
        return Math.sqrt(getUnBiasedVariance());
    }

    public double getCV() {
        return getUnbiasedStandardDeviation() / getAverage();
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getQuickStat() {
        String stat = "";
        stat = "$" + formatter.format(getAverage()) + " \\pm " + formatter.format(getUnbiasedStandardDeviation()) + "$";
        return stat;
    }

}
