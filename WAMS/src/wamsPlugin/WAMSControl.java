package wamsPlugin;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamSettingManager;
import PamController.PamSettings;
import userDisplay.UserDisplayControl;

public class WAMSControl extends PamControlledUnit implements PamSettings {

    /**
     * Unit Name
     */
	public static final String unitType = "NMMF WAMS";
	
	/**
	 * WAMS Process
	 */
	private WAMSProcess wamsProcess;
	
	/**
	 * WAMS parameters
	 */
	private WAMSParameters wamsParams;
	
	private WAMSDisplayProvider wamsDisplayProvider;
	
	
	public WAMSControl(String unitName) {
		super(unitType, unitName);
		wamsParams = new WAMSParameters();
        PamSettingManager.getInstance().registerSettings(this);
        addPamProcess(wamsProcess = new WAMSProcess(this));
        
        // need to set summary end times before we create the table, or else the column names aren't available and the tables will be blank
		wamsProcess.setSummaryEndTimes(WAMSDivisionDataUnit.calcDivEndTimes(0, wamsParams.getNumDiv()));
		UserDisplayControl.addUserDisplayProvider(wamsDisplayProvider = new WAMSDisplayProvider(this));
		
		// now that the table is created, give WAMSProcess a direct link to it so that it can force an update whenever the dynamic counter changes
		wamsProcess.setSummaryTable(((WAMSDisplayPanel) wamsDisplayProvider.getComponent(null, null)).getWamsSummaryTable()); 
	}

    @Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem(getUnitName() + " Parameters");
		menuItem.addActionListener(new SetParameters(parentFrame));
		return menuItem;
	}

	public WAMSParameters getWamsParams() {
		return wamsParams;
	}

	public void setWamsParams(WAMSParameters wamsParams) {
		this.wamsParams = wamsParams;
	}

	public WAMSProcess getWamsProcess() {
		return wamsProcess;
	}

	public WAMSDisplayProvider getWamsDisplayProvider() {
		return wamsDisplayProvider;
	}

	@Override
	public Serializable getSettingsReference() {
		return wamsParams;
	}

	@Override
	public long getSettingsVersion() {
		return WAMSParameters.getSerialVersionUID();
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		this.wamsParams = ((WAMSParameters) pamControlledUnitSettings.getSettings()).clone();
 		return true;
	}
	
	@Override
	public void notifyModelChanged(int changeType) {
		switch(changeType) {
		case PamController.INITIALIZATION_COMPLETE:
			// get the historical mean data, either from a file or from the database
			wamsProcess.getHistoricalData();	
			wamsProcess.calcOldSummaryData();
			wamsProcess.calcTodaysData(true);
			
			// run initialization to update the table with the historical data, but don't create any new data units if they
			// don't already exist.  The problem is the UID manager hasn't synch'd up the UID's with the database yet, so a new data unit at this
			// stage will have a UID of 1
			wamsProcess.initializeParams(true);
			
			// repaint the table display
			wamsDisplayProvider.updateColumnHeaders();
			break;
		}
	}


	@Override
	public void pamClose() {
		
		// save the current means data to the file
		wamsProcess.saveHistoricalData();
		
		// perform the regular close operations
		super.pamClose();
	}


	class SetParameters implements ActionListener {

		Frame parentFrame;

		public SetParameters(Frame parentFrame) {
			this.parentFrame = parentFrame;
		}

		public void actionPerformed(ActionEvent e) {
			WAMSParameters newParams = WAMSParamsDialog.showDialog(parentFrame, wamsProcess, wamsParams);
			if (newParams != null) {
				boolean resetDivs = false;
				if (newParams.getNumDiv() != wamsParams.getNumDiv()) resetDivs=true;
				wamsParams = newParams.clone();
				
				// if the number of divisions has changed, run through all of the initialization routines
				if (resetDivs) {
					wamsProcess.testNumDivs(); // check right away to see if the number of divisions has changed
					wamsProcess.getHistoricalData();	
					wamsProcess.calcOldSummaryData();
					wamsProcess.calcTodaysData(false);
					wamsProcess.initializeParams(false);
					wamsDisplayProvider.updateColumnHeaders(); // repaint the table display
				}
			}
		}
	}
}
