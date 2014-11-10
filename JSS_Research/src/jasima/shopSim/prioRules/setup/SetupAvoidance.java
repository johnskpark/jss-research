/*******************************************************************************
 * Copyright (c) 2010-2013 Torsten Hildebrandt and jasima contributors
 *
 * This file is part of jasima, v1.0.
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
 *
 * $Id: SetupAvoidance.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.shopSim.prioRules.setup;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * 
 * @author Torsten Hildebrandt <hil@biba.uni-bremen.de>
 * @version "$Id: SetupAvoidance.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $"
 */
public class SetupAvoidance extends PR {

	public SetupAvoidance() {
		super();
	}

	@Override
	public double calcPrio(PrioRuleTarget j) {
		if (getOwner().currMachine.setupState == j.getCurrentOperation().setupState)
			return +1;
		else
			return -1;
	}

	@Override
	public String getName() {
		return "SA";
	}

}
