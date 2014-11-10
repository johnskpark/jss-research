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
 * $Id: DblDistribution.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $
 *******************************************************************************/
package jasima.core.random.continuous;

import java.util.Random;

import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Returns an arbitrarily distributed random number stream. Its distribution is
 * determined by an arbitrary {@link RealDistribution}.
 * 
 * @author Torsten Hildebrandt <hil@biba.uni-bremen.de>
 * @version 
 *          "$Id: DblDistribution.java 74 2013-01-08 17:31:49Z THildebrandt@gmail.com $"
 */
public class DblDistribution extends DblStream {

	private static final long serialVersionUID = -157283852135250753L;

	private RealDistribution distribution;

	public DblDistribution() {
		this(null, null, null);
	}

	public DblDistribution(RealDistribution distribution) {
		this(null, null, distribution);
	}

	public DblDistribution(Random random, RealDistribution distribution) {
		this(random, null, distribution);
	}

	public DblDistribution(String name, RealDistribution distribution) {
		this(null, name, distribution);
	}

	public DblDistribution(Random random, String name,
			RealDistribution distribution) {
		super();
		setRndGen(random);
		setDistribution(distribution);
		setName(name);
	}

	public RealDistribution getDistribution() {
		return distribution;
	}

	/**
	 * Sets the continuous distribution to use.
	 */
	public void setDistribution(RealDistribution distribution) {
		this.distribution = distribution;
	}

	@Override
	public double nextDbl() {
		return distribution.inverseCumulativeProbability(rndGen.nextDouble());
	}

	@Override
	public String toString() {
		return "DblDistribution(" + String.valueOf(distribution) + ')';
	}

}