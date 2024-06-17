package wamsPlugin;

import userDisplay.UserDisplayComponent;
import userDisplay.UserDisplayControl;
import userDisplay.UserDisplayProvider;

public class WAMSDisplayProvider implements UserDisplayProvider {

	private WAMSControl wamsControl;
	
	private WAMSDisplayPanel summaryPanel;

	public WAMSDisplayProvider(WAMSControl wamsControl) {
		this.wamsControl = wamsControl;
		summaryPanel = new WAMSDisplayPanel(wamsControl);
	}

	@Override
	public String getName() {
		return wamsControl.getUnitName() + " Summary Display";
	}

	@Override
	public UserDisplayComponent getComponent(UserDisplayControl userDisplayControl, String uniqueDisplayName) {
		return (summaryPanel);
	}

	@Override
	public Class getComponentClass() {
		return WAMSDisplayPanel.class;
	}

	@Override
	public int getMaxDisplays() {
		return 0;
	}

	@Override
	public boolean canCreate() {
		return true;
	}

	@Override
	public void removeDisplay(UserDisplayComponent component) {
		// TODO Auto-generated method stub

	}

	public void updateColumnHeaders() {
		summaryPanel.updateColumnHeaders();
	}

}
