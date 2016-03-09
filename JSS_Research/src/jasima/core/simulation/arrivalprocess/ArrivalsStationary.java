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
package jasima.core.simulation.arrivalprocess;

import jasima.core.random.continuous.DblDistribution;
import jasima.core.random.continuous.DblStream;

import java.util.Random;

/**
 * This class can be used to create a stationary arrival process which can be
 * described by a certain sequence of inter-arrival times (using, e.g.,
 * {@link DblDistribution}).
 * 
 * @see DblStream
 * 
 * @author Torsten Hildebrandt, 2012-08-07
 * @version 
 *          "$Id$"
 */
public class ArrivalsStationary extends ArrivalProcess {

	private static final long serialVersionUID = -7877781395872395477L;

	private DblStream interArrivalTimes = null;

	public ArrivalsStationary() {
		this(null);
	}

	public ArrivalsStationary(DblStream interArrivalTimes) {
		super();
		setInterArrivalTimes(interArrivalTimes);
	}

	@Override
	public void init() {
		super.init();

		if (interArrivalTimes != null)
			interArrivalTimes.init();
	}

	@Override
	public double nextDbl() {
		if (isFirst && isArrivalAtTimeZero()) {
			// state = state; // do nothing
		} else {
			state = state + interArrivalTimes.nextDbl();
		}
		isFirst = false;
		return state;
	}

	@Override
	public void setRndGen(Random rndGen) {
		super.setRndGen(rndGen);
		if (interArrivalTimes != null)
			interArrivalTimes.setRndGen(rndGen);
	}

	@Override
	public DblStream clone() throws CloneNotSupportedException {
		ArrivalsStationary c = (ArrivalsStationary) super.clone();
		if (interArrivalTimes != null)
			c.interArrivalTimes = interArrivalTimes.clone();
		return c;
	}

	public DblStream getInterArrivalTimes() {
		return interArrivalTimes;
	}

	public void setInterArrivalTimes(DblStream interArrivalTimes) {
		this.interArrivalTimes = interArrivalTimes;
	}

}
