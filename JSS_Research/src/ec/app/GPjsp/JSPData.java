/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */


package ec.app.GPjsp;
import ec.gp.*;
import jsp.AbstractJSPFramework;
import jsp.Job;
import jsp.Machine;
import jsp.Operation;

public class JSPData extends GPData {

	private static final long serialVersionUID = 4349673019358621978L;

	public boolean satisfyCONDITION = false;
	public int attributeThreshold; //10%, 20%,...,100%
	public double attributeValue;
	public double tempVal = 0;
	public double partialEstimatedFlowtime =0;
	public double constant;
	public int k = -1;
	public Operation op;
	public Job job;
	public Machine machine;
	public AbstractJSPFramework abJSP;
	public JSPVariables stat = new JSPVariables();
	public double improve = -1;
	public double maxJobTotal = -1;
	public double maxMachineTotal = -1;
	public double obj = -1;
	public double bestObj = -1;
	public boolean firstIteration = true;

	@Override
	public void copyTo(final GPData gpd) {
		//((jspData)gpd).x = x;
	}
}


