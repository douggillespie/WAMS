package wamsPlugin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import PamController.PamFolders;
import alarm.AlarmDataBlock;


public class WAMSParameters implements Serializable, Cloneable {

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Source used for detector input
	 */
	private String sourceDetector;
	
	/**
	 * List of alarms to monitor
	 ************************** HOW TO CLONE THIS *********************************
	 */
	private ArrayList<String> alarmList = new ArrayList<String>();
	
	/**
	 * The time interval to use when counting.  Units are minutes.  Defaults to 1 hour
	 */
	private int timeIntervalMinutes = 60;

	/**
	 * Boolean indicating whether the user wants to veto some angles (true) or not (false)
	 */
	private boolean vetoAngles = false;
	
	/**
	 * Vector containing the veto angle.  Index 0 is the minimum angle, index 1 is the
	 * max angle.  If both are the same, there is no veto angle
	 */
	private int[] anglesToVeto = new int[2];

	/** Folder to save reports to */
	private String reportFolder = "C:\\";
	
	/** document to use as template */
	private String templateFile = "";
	
	/** boolean indicating whether report times should be UTC (true) or Local PC time (false) */
	private boolean usingUTC = true;
	
	/** the number of time divisions to use in the database table.  Defaults to 24 (hourly) */
	private int numDiv = 24;
	
	/** the folder to save the csv file containing historical means to */
	private String csvOutputFolder;
	
	/** whether or not to use the Harmonic Detector */
	private boolean usingHarmDet = false;
	
	/** the max overlap (in ms) of the start and end points of whistles to decide that they are harmonics */
	private int startEndTime = 100;
	
	/** the min gap (in ms) between detections to decide they are new and not harmonics */
	private int minGap = 100;
	
	public String getSourceDetector() {
		return sourceDetector;
	}

	public void setSourceDetector(String sourceDetector) {
		this.sourceDetector = sourceDetector;
	}

	public ArrayList<String> getAlarmList() {
		return alarmList;
	}
	
	public void setAlaarmList (ArrayList<String> newAlarmList) {
		alarmList = newAlarmList;
	}
	
//	public void addAlarm (String alarmToAdd) {
//		alarmList.add(alarmToAdd);
//	}
//	
//	public void removeAlarm (String alarmToRemove) {
//		alarmList.remove(alarmToRemove);
//	}
	
	public void setTimeIntervalMinutes(int minutes) {
		timeIntervalMinutes = minutes;
	}
	
	public int getTimeIntervalMinutes() {
		return timeIntervalMinutes;
	}

	public boolean isVetoAngles() {
		return vetoAngles;
	}

	public void setVetoAngles(boolean vetoAngles) {
		this.vetoAngles = vetoAngles;
	}

	public int[] getAnglesToVeto() {
		return anglesToVeto;
	}

	public void setAnglesToVeto(int[] anglesToVeto) {
		this.anglesToVeto = anglesToVeto;
	}

   public String getReportFolder() {
		return reportFolder;
	}

	public void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public boolean isUsingUTC() {
		return usingUTC;
	}

	public void setUsingUTC(boolean usingUTC) {
		this.usingUTC = usingUTC;
	}

	public int getNumDiv() {
		return numDiv;
	}

	public void setNumDiv(int numDiv) {
		this.numDiv = numDiv;
	}

	public String getCsvOutputFolder() {
		return csvOutputFolder;
	}

	public void setCsvOutputFolder(String csvOutputFolder) {
		this.csvOutputFolder = csvOutputFolder;
	}

	public boolean isUsingHarmDet() {
		return usingHarmDet;
	}

	public void setUsingHarmDet(boolean usingHarmDet) {
		this.usingHarmDet = usingHarmDet;
	}

	public int getStartEndTime() {
		return startEndTime;
	}

	public void setStartEndTime(int startEndTime) {
		this.startEndTime = startEndTime;
	}

	public int getMinGap() {
		return minGap;
	}

	public void setMinGap(int minGap) {
		this.minGap = minGap;
	}

	public static long getSerialVersionUID() {
        return serialVersionUID;
    }

	@Override
	/**
	 * clone the parameters
	 */
	protected WAMSParameters clone() {
		try {
			// if there is no valid output folder, set it here
			if (csvOutputFolder == null) {
				csvOutputFolder = PamFolders.getDefaultProjectFolder();
			}
			return (WAMSParameters) super.clone();
		}
		catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
