/*
 *  PAMGUARD - Passive Acoustic Monitoring GUARDianship.
 * To assist in the Detection Classification and Localisation
 * of marine mammals (cetaceans).
 *
 * Copyright (C) 2006
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */



package wamsPlugin;

import PamUtils.PamCalendar;
import PamguardMVC.PamDataUnit;
import PamguardMVC.debug.Debug;

/**
 * @author mo55
 *
 * This data unit simply keeps track of the number of counts in the current division.  Unlike the WAMSDivisionDataUnit, it is updated dynamically and
 * not just at the end of the division or when PAMGuard processing stops.  This data unit is intended for displays and alarms, and is not logged to
 * a database or binary file.
 */
public class WAMSDivCounterDataUnit extends PamDataUnit<WAMSDivCounterDataUnit, WAMSDivCounterDataUnit> {
	
	/**
	 * @param timeMilliseconds
	 */
	public WAMSDivCounterDataUnit(long timeMilliseconds) {
		super(timeMilliseconds);
	}

	private int currentCount;

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
		this.setTimeMilliseconds(PamCalendar.getTimeInMillis());
	}
	
	public void incCurrentCount() {
		this.currentCount++;
		this.setTimeMilliseconds(PamCalendar.getTimeInMillis());
		
//		Debug.out.println("Count is now " + String.valueOf(currentCount));
	}
	

}
