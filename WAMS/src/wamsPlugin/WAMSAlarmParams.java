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

import PamModel.parametermanager.PamParameterSet;
import PamguardMVC.dataSelector.DataSelectParams;

/**
 * @author mo55
 *
 */
public class WAMSAlarmParams extends DataSelectParams implements Cloneable {

	public static final long serialVersionUID = 1L;
	
	/** if true, alarm triggers on raw counts; if false, alarm triggers on difference between raw counts and mean counts for that time division */
	private boolean triggerOnRaw = true;
	
	public boolean isTriggeringOnRaw() {
		return triggerOnRaw;
	}

	public void setTriggerOnRaw(boolean triggerOnRaw) {
		this.triggerOnRaw = triggerOnRaw;
	}

	@Override
	public WAMSAlarmParams clone()  {
		try {
			return (WAMSAlarmParams) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public PamParameterSet getParameterSet() {
		PamParameterSet ps = PamParameterSet.autoGenerate(this);
		return ps;
	}

}
