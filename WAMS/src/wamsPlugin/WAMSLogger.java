package wamsPlugin;

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;

public class WAMSLogger extends SQLLogging {

	public static final int STRING_LENGTH = 40;

	private PamTableDefinition tableDefinition;
	
	private PamTableItem detector, alarm, startTime, endTime, count;
	
	private String detectorHeader = "Detector";
	
	private String alarmHeader = "Alarm";
	
	private String startHeader = "StartTimeUTC";
	
	private String endHeader = "EndTimeUTC";
	
	private String countHeader = "Count";

	public WAMSLogger(PamDataBlock pamDataBlock) {
		super(pamDataBlock);
		setCanView(true);
		
		// create the table definition. 
		tableDefinition = new PamTableDefinition(pamDataBlock.getLoggingName(), UPDATE_POLICY_WRITENEW);

		// add additional table items not included in PamDetectionLogging 
		tableDefinition.addTableItem(detector = new PamTableItem(detectorHeader, Types.CHAR, STRING_LENGTH));
		tableDefinition.addTableItem(alarm = new PamTableItem(alarmHeader, Types.CHAR, STRING_LENGTH));
		tableDefinition.addTableItem(startTime = new PamTableItem(startHeader, Types.TIMESTAMP));
		tableDefinition.addTableItem(endTime = new PamTableItem(endHeader, Types.TIMESTAMP));
		tableDefinition.addTableItem(count = new PamTableItem(countHeader, Types.INTEGER));

		tableDefinition.setUseCheatIndexing(true);
        setTableDefinition(tableDefinition);
	}

	/**
	 * Load the table with values from the PamDataUnit
	 */
	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {

		WAMSDataUnit wdu = (WAMSDataUnit) pamDataUnit;
		detector.setValue(wdu.getDetector());
		alarm.setValue(wdu.getAlarm());
		startTime.setValue(sqlTypes.getTimeStamp(wdu.getStartTime()));
		endTime.setValue(sqlTypes.getTimeStamp(wdu.getEndTime()));
		count.setValue(wdu.getCount());
	}

    /**
     * Create a new WAMSDataUnit and fill it with values from the database
     *
     * @param timeMilliseconds parameter from the database
     * @param databaseIndex database index
     * @return PamDataUnit the new data unit
     */
	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
        WAMSDataUnit wdu = new WAMSDataUnit(timeMilliseconds,startTime.getLongValue(),endTime.getLongValue(),count.getIntegerValue());

        /* set the logging database index */
        wdu.setDatabaseIndex(databaseIndex);
        
        /* put the values from the database into the WAMSDataUnit object */
        wdu.setDetector(detector.getDeblankedStringValue());
        wdu.setAlarm(alarm.getDeblankedStringValue());
        
        return wdu;
	}

	public String getDetectorHeader() {
		return detectorHeader;
	}

	public String getStartHeader() {
		return startHeader;
	}

	public String getEndHeader() {
		return endHeader;
	}

	public String getCountHeader() {
		return countHeader;
	}
	
	public String getAlarmHeader() {
		return alarmHeader;
	}
	
}
