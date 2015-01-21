/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.mmHH;
//import ec.*;
import ec.gp.*;


public class DoubleData extends GPData
    {
    /**
	 *
	 */
	private static final long serialVersionUID = -4857060393295277728L;
	/**
	 *
	 */

	public double x;    // return value

    public void copyTo(final GPData gpd)   // copy my stuff to another DoubleData
        { ((DoubleData)gpd).x = x; }
    }