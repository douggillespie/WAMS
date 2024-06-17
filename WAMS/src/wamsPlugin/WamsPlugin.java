package wamsPlugin;

import PamModel.PamDependency;
import PamModel.PamPluginInterface;

public class WamsPlugin implements PamPluginInterface {

	String jarFile;

	@Override
	public String getClassName() {
		return "wamsPlugin.WAMSControl";
	}

	@Override
	public String getDefaultName() {
		return "NMMF WAMS";
	}

	@Override
	public String getDescription() {
		return "NMMF WAMS";
	}

	@Override
	public String getMenuGroup() {
		return "Utilities";
	}

	@Override
	public String getToolTip() {
		return "Summarize detector and alarm activity over specific periods of time.";
	}

	@Override
	public PamDependency getDependency() {
		return null;
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 1;
	}

	@Override
	public int getNInstances() {
		return 1;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public String getHelpSetName() {
		return "help/WAMSHelp.hs";
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "National Marine Mammal Foundation";
	}

	@Override
	public String getContactEmail() {
		return "brittany.jones@nmmpfoundation.org";
	}

	@Override
	public String getVersion() {
		return "2.2.1";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.01.15ff";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.02.02";
	}

	@Override
	public String getAboutText() {
		String desc = "The NMMF WAMS plugin provides a simple way to monitor a detector and/or multiple alarms, " +
				"and generate reports summarizing activity over specific time periods.  Information is saved to " +
				"the database and can be displayed in table and chart form on User Display modules";
		return desc;
	}

	/* (non-Javadoc)
	 * @see PamModel.PamPluginInterface#allowedModes()
	 */
	@Override
	public int allowedModes() {
		return PamPluginInterface.ALLMODES;
	}

}
