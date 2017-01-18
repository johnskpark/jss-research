/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsp;

import java.util.Random;

/**
 *
 * @author nguyensu
 */
public class SA {
    private double InitialTemp = 500;
    private double TempFactor = 0.99;
    private double MinPercent = 0.02;
    private double SizeFactor = 2;
    private double PercentAccept = -1;
    private double T = InitialTemp;
    private Random R;
    public SA(Random r,double initTemp){
        R=r;
        if (initTemp>0) InitialTemp=initTemp;
    }
    public boolean isAcceptSA(double newObj,double oldObj){
        if (newObj<=oldObj){
            return true;
        }
        else  {
            double x = Math.exp(-(newObj-oldObj)/T);
            if (R.nextDouble()<Math.exp(-(newObj-oldObj)/T))
                return true;
        }
        return false;
    }
    public void updateTemp(int iter){
        if (iter%SizeFactor==0){
            T*=TempFactor;
        }
    }
}
