package wamsPlugin;

import PamUtils.PamCalendar;
import PamguardMVC.PamDataUnit;

public class WAMSDivisionDataUnit extends PamDataUnit<WAMSDivisionDataUnit, WAMSDivisionDataUnit> {

	/** the detector being monitored */
	private String detector;
	
	/** the start of the current day, in milliseconds epoch.  Should correspond to 00:00:00 am */
	private long startDay;
	
	/** the number of time divisions each day */
	private int numDiv;
	
	/** the detection count for each time division */
	private Integer[] count;
	
//	/** the time (in milliseconds) of the start of each division */
//	private long[] divStartTimes;
	
	/** the time (in milliseconds) of the end of each division */
	private long[] divEndTimes;
	
	
	public WAMSDivisionDataUnit(long timeMilliseconds, long startDay, int numDiv) {
		super(timeMilliseconds);
		this.startDay = startDay;
		this.numDiv = numDiv;
		calcDivisions();
	}

	public void calcDivisions() {
		count = new Integer[numDiv];
//		divEndTimes = new long[numDiv];
//		long millisPerDiv = PamCalendar.millisPerDay/numDiv;
//		for (int i=0; i<numDiv; i++) {
//			divEndTimes[i] = startDay + (i+1)*millisPerDiv-1;
//		}
		divEndTimes = WAMSDivisionDataUnit.calcDivEndTimes(startDay, numDiv);
	}
	
	public static long[] calcDivEndTimes(long startOfDay, int numDiv) {
		long[] divEndTimes = new long[numDiv];
		long millisPerDiv = PamCalendar.millisPerDay/numDiv;
		for (int i=0; i<numDiv; i++) {
			divEndTimes[i] = startOfDay + (i+1)*millisPerDiv-1;
		}
		return divEndTimes;
	}
	
	public void resetCount(long startDay, int numDiv) {
		this.startDay = startDay;
		this.numDiv = numDiv;
		calcDivisions();
	}
	
	/**
	 * Set the number of detections in a single time division
	 * @param div the division/index to change (remember it starts at 0)
	 * @param count the number of detection counts
	 */
	public void setACount(int div, Integer count) {
		if (div<this.count.length) {
			this.count[div] = count;
		} else {
			System.out.println("Error - trying to access a time division beyond the number in the table");
		}
	}
	
	/**
	 * Add a count to the value that is currently in the array
	 * 
	 * @param div the division/index to change (remember it starts at 0)
	 * @param count the number of detection counts to add
	 */
	public void addACount(int div, Integer count) {
		if (div<this.count.length) {
			if (this.count[div]==null) {
				this.count[div] = count;
			} else {
				this.count[div] += count;
			}
		} else {
			System.out.println("Error - trying to access a time division beyond the number in the table");
		}
	}
	
	/**
	 * Set the detections in all time divisions at once
	 * 
	 * @param count[] an array containing the detections for each time division
	 */
	public void setAllCounts(Integer[] count) {
		if (count.length==numDiv) {
			this.count = count;
		} else {
			System.out.println("Error - the number of time divisions in the passed arguement does not match the number of time divisions in the table");
		}
	}

	public String getDetector() {
		return detector;
	}

	public void setDetector(String detector) {
		this.detector = detector;
	}

	public Integer getACount(int div) {
		return count[div];
	}


	public Integer[] getCount() {
		return count;
	}


	public long[] getDivEndTimes() {
		return divEndTimes;
	}

	public void setDivEndTimes(long[] divEndTimes) {
		this.divEndTimes = divEndTimes;
	}

	public long getStartDay() {
		return startDay;
	}

	public int getNumDiv() {
		return numDiv;
	}

	@Override
	public int getDatabaseIndex() {
		return super.getDatabaseIndex();
	}

	@Override
	public void setDatabaseIndex(int databaseIndex) {
		super.setDatabaseIndex(databaseIndex);
	}

	/**
	 * @return
	 */
	public Integer getDynamicCount() {
		return null;
	}


}
