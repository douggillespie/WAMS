package wamsPlugin;

import PamguardMVC.DataUnitBaseData;
import PamguardMVC.PamDataUnit;

public class WAMSDataUnit extends PamDataUnit<WAMSDataUnit, WAMSDataUnit> {

	private String detector;
	
	private String alarm;
	
	private long startTime;
	
	private long endTime;
	
	private int count;
	
	public WAMSDataUnit(long timeMilliseconds, long startTime, long endTime, int count) {
		super(timeMilliseconds);
		this.startTime = startTime;
		this.endTime = endTime;
		this.count = count;
	}

	public String getDetector() {
		return detector;
	}

	public void setDetector(String detector) {
		this.detector = detector;
	}

	public String getAlarm() {
		return alarm;
	}

	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
