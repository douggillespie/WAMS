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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;

/**
 * @author mo55
 *
 */
public class WAMSAlarmDialog extends PamDialog {

	private static final long serialVersionUID = 1L;
	
	private static WAMSAlarmDialog singleInstance;
	private WAMSAlarmParams wamsAlarmParams;
	
	private JRadioButton rawCount, diffCount;
	private ButtonGroup buttonGroup;
	
	private WAMSAlarmDialog(Window parentFrame) {
		super(parentFrame, "WAMS Alarm Options", true);
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder("Trigger On ..."));
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new PamGridBagContraints();
		addComponent(mainPanel, rawCount = new JRadioButton("Raw Counts"), c);
		c.gridy++;
		addComponent(mainPanel, diffCount = new JRadioButton("Counts above Mean"), c);
		buttonGroup = new ButtonGroup();
		buttonGroup.add(rawCount);
		buttonGroup.add(diffCount);
		setDialogComponent(mainPanel);
	}
	
	public static WAMSAlarmParams showDialog(Window frame, WAMSAlarmParams wamsAlarmParams) {
		if (singleInstance == null || singleInstance.getOwner() != frame) {
			singleInstance = new WAMSAlarmDialog(frame);
		}
		singleInstance.wamsAlarmParams = wamsAlarmParams.clone();
		singleInstance.setParams();
		singleInstance.setVisible(true);
		return singleInstance.wamsAlarmParams;
	}

	private void setParams() {
		if (wamsAlarmParams.isTriggeringOnRaw()) {
			rawCount.setSelected(true);
		} else {
			diffCount.setSelected(true);
		}
	}

	@Override
	public boolean getParams() {
		wamsAlarmParams.setTriggerOnRaw(rawCount.isSelected());
		return true;
	}

	@Override
	public void cancelButtonPressed() {
		wamsAlarmParams = null;
	}

	@Override
	public void restoreDefaultSettings() {
		wamsAlarmParams = new WAMSAlarmParams();
		setParams();
	}

}
