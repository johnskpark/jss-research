/*******************************************************************************
 * Copyright (c) 2010-2015 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.2.
 *
 * jasima is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jasima is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jasima.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package jasima.shopSim.prioRules.upDownStream;

import jasima.shopSim.core.PrioRuleTarget;

/**
 * This class implements an extension of the IFTMinusUIT rule, developed by
 * Branke and Pickardt (2011).
 * <p>
 * The lookahead needs to be enabled in the simulation for this rule to work
 * properly.
 * 
 * @author Christoph Pickardt, 2011-11-15
 * @version 
 *          "$Id$"
 */
public class IFTMinusUITPlusNPT extends IFTMinusUIT {

	private static final long serialVersionUID = -1423420723665183647L;

	@Override
	public double calcPrio(PrioRuleTarget j) {
		return super.calcPrio(j) - PTPlusWINQPlusNPT.npt(j);
	}

}
