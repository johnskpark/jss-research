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
 * $Id: SSPT.java 73 2013-01-08 17:16:19Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.shopSim.prioRules.setup;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;

/**
 * This class implements the Shortest Setup and Processing Time rule, developed
 * by Wilbrecht and Prescott (1969), which selects the job that can be completed
 * the quickest, see also Pickardt and Branke (2012).
 * 
 * @author Christoph Pickardt, 2011-11-15
 * @version $Id: SSPT.java 73 2013-01-08 17:16:19Z THildebrandt@gmail.com $
 */
public class SSPT extends PR {

	@Override
	public double calcPrio(PrioRuleTarget job) {
		return -(getOwner().getSetupMatrix()[getOwner().currMachine.setupState][job
				.getCurrentOperation().setupState] + job.getCurrentOperation().procTime);
	}

}
