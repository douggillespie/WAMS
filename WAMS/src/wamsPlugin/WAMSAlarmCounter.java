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

import java.awt.Window;
import java.io.Serializable;

import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamguardMVC.PamDataUnit;
import PamguardMVC.debug.Debug;
import alarm.AlarmControl;
import alarm.AlarmCounter;

/**
 * @author mo55
 *
 */
public class WAMSAlarmCounter extends AlarmCounter implements PamSettings {

	private WAMSProcess wamsProcess;
	private WAMSAlarmParams wamsAlarmParams = new WAMSAlarmParams();
	
	/**
	 * @param alarmControl
	 */
	public WAMSAlarmCounter(AlarmControl alarmControl, WAMSProcess wamsProcess) {
		super(alarmControl);
		this.wamsProcess = wamsProcess;
		PamSettingManager.getInstance().registerSettings(this);
	}

	@Override
	public double getValue(int countType, PamDataUnit dataUnit) {
		WAMSDivCounterDataUnit wamsDataUnit = (WAMSDivCounterDataUnit) dataUnit;
		int count = wamsDataUnit.getCurrentCount();
		if (!wamsAlarmParams.isTriggeringOnRaw()) {
			count -= wamsProcess.getHistoryManager().getSingleMean(wamsProcess.divToLog);
		}
		
//		Debug.out.println("Updating WAMS counter to " + String.valueOf(count));
		return count;
	}

	@Override
	public void resetCounter() {
	}

	@Override
	public boolean hasOptions() {
		return true;
	}

	@Override
	public boolean showOptions(Window parent) {
		WAMSAlarmParams newParams = WAMSAlarmDialog.showDialog(parent, wamsAlarmParams);
		if (newParams != null) {
			wamsAlarmParams = newParams.clone();
			return true;
		}
		return false;
	}

	@Override
	public String getUnitName() {
		return wamsProcess.getWamsControl().getUnitName();
	}

	@Override
	public String getUnitType() {
		return "WAMSAlarmParameters";
	}

	@Override
	public Serializable getSettingsReference() {
		return wamsAlarmParams;
	}

	@Override
	public long getSettingsVersion() {
		return wamsAlarmParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		this.wamsAlarmParams = ((WAMSAlarmParams) pamControlledUnitSettings.getSettings()).clone();
		return (wamsAlarmParams!=null);
	}

}
