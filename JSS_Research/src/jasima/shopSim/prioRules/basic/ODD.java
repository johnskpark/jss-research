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
package jasima.shopSim.prioRules.basic;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * This class implements the Operation Due Date rule. The idea to use due dates
 * of operations instead of job due dates originates from a paper by Kanet and
 * Hayya (1982).
 * 
 * @author Christoph Pickardt, 2011-11-15
 * @version "$Id$"
 */
public class ODD extends PR {

	private static final long serialVersionUID = -2691388850642748038L;

	@Override
	public double calcPrio(PrioRuleTarget j) {
		return -j.getCurrentOperationDueDate();
	}

}
