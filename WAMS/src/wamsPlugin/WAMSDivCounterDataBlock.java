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

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import alarm.AlarmCounterProvider;
import alarm.AlarmDataSource;

/**
 * @author mo55
 * 
 * Very basic data block that keeps track of the counts in the current division.  Use as a source
 * for an Alarm Module, because the other 2 data blocks don't update in real-time
 *
 */
public class WAMSDivCounterDataBlock extends PamDataBlock<WAMSDivCounterDataUnit> implements AlarmDataSource {

	private WAMSAlarmCounterProvider wamsAlarmCounterProvider;
	
	/**
	 * @param unitClass
	 * @param dataName
	 * @param parentProcess
	 * @param channelMap
	 */
	public WAMSDivCounterDataBlock(String dataName, WAMSProcess parentProcess, int channelMap) {
		super(WAMSDivCounterDataUnit.class, dataName, parentProcess, channelMap);
	}

	@Override
	public AlarmCounterProvider getAlarmCounterProvider() {
		if (wamsAlarmCounterProvider==null) {
			wamsAlarmCounterProvider = new WAMSAlarmCounterProvider((WAMSControl) this.getParentProcess().getPamControlledUnit());
		}
		
		return wamsAlarmCounterProvider;
	}


}
