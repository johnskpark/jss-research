/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsp;

/**
 *
 * @author nguyensu
 */
public class SampleArray {
    double[] values;
    int nextslot = 0;
    int n = 0;
    double total = 0;
    public SampleArray(int sampleSize) {
        values = new double[sampleSize];
    }
    public void add(double val){
        if (n<values.length) n++;
        total -= values[nextslot];
        values[nextslot] = val;
        total += val;
        if (nextslot==values.length-1) nextslot = 0;
        else nextslot++;
    }
    public double getAverage(){
        if (n<=1) return 0;
        return total/n;
    }
    public double getRange(){
        if (n != values.length) return values[nextslot-1]-values[0];
        else if (nextslot==0) return values[n-1]-values[0];
        else return values[nextslot-1]-values[nextslot];
    }
    public double getEventFrequency(){
        if (n<=1) return 0;
        return n/getRange();
    }
    public double getDominatedRatio(double ot){
        if (n==0) return 0;
        double otRatio = 0;
        for (int i = 0; i < values.length; i++) {
            if (ot>values[i]) otRatio+=values[i];  
        }
        return otRatio/total;
    }
}
