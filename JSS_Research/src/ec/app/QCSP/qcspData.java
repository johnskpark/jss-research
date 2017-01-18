/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.QCSP;

import ec.gp.GPData;

/**
 *
 * @author nguyensu
 */
public class qcspData extends GPData{

    public double PR;
    public double HWL;
    public double D;
    public double LWL;
    public double LQC;
    public double DNQ;
    public double C;
    public double CNQ;
    public double NBQT;
    public double T;
    public double S;
    public double FT;
    public double B;
    public double Q;
    public double PT;
    public double W;
    public double tempVal = 0;
    
    public void updateData(double w,double b, double q, double pr, double hwl, double d, double lwl, double lqc, double dnq, double c,double cnq,double nbqt, double t, double s, double ft, double pt){
        B = b;
        Q = q;
        PR =pr;
        HWL = hwl;
        D = d;
        LWL = lwl;
        LQC = lqc;
        DNQ = dnq;
        C = c;
        CNQ = cnq;
        NBQT = nbqt;
        T = t;
        S = s;
        FT = ft;
        PT =pt;
        W = w;
    }
    @Override
    public void copyTo(GPData gpd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
