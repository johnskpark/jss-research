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
 * $Id: WBPT.java 73 2013-01-08 17:16:19Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.shopSim.prioRules.setup;

import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.core.PriorityQueue;

/**
 * This class implements the Weighted Batch Processing Time rule, developed by
 * Raman et al. (1989), which is an additive combination of MMS and SPT, see
 * also Pickardt and Branke (2012).
 * 
 * @author Christoph Pickardt, 2011-11-15
 * @version $Id: WBPT.java 73 2013-01-08 17:16:19Z THildebrandt@gmail.com $
 */
public class WBPT extends MMS {

	@Override
	public double calcPrio(PrioRuleTarget job) {
		if (arrivesTooLate(job))
			return PriorityQueue.MIN_PRIO;

		double marginalSetup = getOwner().getSetupMatrix()[getOwner().currMachine.setupState][job
				.getCurrentOperation().setupState]
				/ jobsPerFamily.get("" + job.getCurrentOperation().setupState);

		return -(marginalSetup + job.getCurrentOperation().procTime);
	}

}
