/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.app.TwoDStripPacking;

import ec.gp.GPData;

/**
 *
 * @author nguyensu
 */
public class twoDStripData extends GPData{

    public double pWidth = -1;
    public double pHeight = -1;
    public double pArea = -1;
    public double sHeight = -1;
    public double sWidthLeft = -1;
    public double shWidth = -1;
    public double shHeight = -1;
    public double tempVal = 0;
    public double penalty = 0;
    public double previousX = 0;
    public double previousY = 0;

    public void updateData(double pW, double pH, double sH, double sWL, double shW, double shH,double b,double pX, double pY){
        pWidth = pW;
        pHeight = pH;
        pArea = pW*pH;
        sHeight = sH;
        sWidthLeft = sWL;
        shWidth = shW;
        shHeight = shH;
        penalty = b;
        previousX = pX;
        previousY = pY;
    }
    @Override
    public void copyTo(GPData gpd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
