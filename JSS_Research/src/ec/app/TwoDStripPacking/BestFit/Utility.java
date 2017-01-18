/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.TwoDStripPacking.BestFit;

/**
 *
 * @author nguyensu
 */
public class Utility {
    public static double truncatedGaussianMutation(double current, double lower, double upper, cern.jet.random.AbstractDistribution standardGaussian){
        double stdev = 0.1*(upper-lower);
        return min(max(lower,current + stdev*standardGaussian.nextDouble()),upper);
    }   
    private static double max(double a, double b){
        if (a>b) return a;
        else return b;
    }
    private static double min(double a, double b){
        if (a<b) return a;
        else return b;
    }
}
