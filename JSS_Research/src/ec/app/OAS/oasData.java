/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.OAS;

import ec.app.QCSP.*;
import ec.gp.GPData;

/**
 *
 * @author nguyensu
 */
public class oasData extends GPData{

    public double P;
    public double R;
    public double W;
    public double D;
    public double D_;
    public double E;
    public double S;
    public double SI;
    public double tempVal = 0;
    
    public void updateData(double p,double r, double w, double d, double d_, double e, double s, double si){
        P = p;
        R = r;
        W = w;
        D = d;
        D_= d_;
        E = e;
        S = s;
        SI = si;
    }
    @Override
    public void copyTo(GPData gpd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
