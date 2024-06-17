package wamsPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.DBControlUnit;
import generalDatabase.PamConnection;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;

public class WAMSSummaryLogger extends SQLLogging {

	public static final int STRING_LENGTH = 40;

	private PamTableDefinition tableDefinition;
	
	private PamTableItem detector, startTime;
	
	private String startTimeHeader = "StartTimeUTC";
	
	private int numDiv;
	
	private PamTableItem[] divCounts;
	

	public WAMSSummaryLogger(PamDataBlock pamDataBlock, int numDiv) {
		super(pamDataBlock);
		this.numDiv = numDiv;
		setCanView(true);
		
		// create the table definition. 
		tableDefinition = new PamTableDefinition(pamDataBlock.getLoggingName()+"_"+String.valueOf(numDiv)+"Divisions", UPDATE_POLICY_OVERWRITE);

		// add additional table items not included in PamDetectionLogging 
		tableDefinition.addTableItem(detector = new PamTableItem("Detector", Types.CHAR, STRING_LENGTH));
		tableDefinition.addTableItem(startTime = new PamTableItem(startTimeHeader, Types.TIMESTAMP));

		// set up the number of time divisions
		divCounts = new PamTableItem[numDiv];
		for (int i=0; i<numDiv; i++) {
			tableDefinition.addTableItem(divCounts[i] = 
					new PamTableItem("Div" + String.valueOf(i), Types.INTEGER));
		}
		
		tableDefinition.setUseCheatIndexing(false);
        setTableDefinition(tableDefinition);
	}

	/**
	 * Load the table with values from the PamDataUnit
	 */
	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {

		WAMSDivisionDataUnit wdu = (WAMSDivisionDataUnit) pamDataUnit;
		Integer[] countsPerDiv = wdu.getCount();
		detector.setValue(wdu.getDetector());
		startTime.setValue(sqlTypes.getTimeStamp(wdu.getStartDay()));
		for (int i=0; i<numDiv; i++) {
			divCounts[i].setValue(countsPerDiv[i]);
		}
	}
	
	/**
     * Create a new WAMSSummaryDataUnit and fill it with values from the database
     *
     * @param timeMilliseconds parameter from the database
     * @param databaseIndex database index
     * @return PamDataUnit the new data unit
     */
	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		WAMSDivisionDataUnit wdu = new WAMSDivisionDataUnit(timeMilliseconds,SQLTypes.millisFromTimeStamp(startTime.getValue()),numDiv);

        /* set the logging database index */
        wdu.setDatabaseIndex(databaseIndex);
        
        /* put the values from the database into the WAMSDataUnit object */
        wdu.setDetector(detector.getDeblankedStringValue());
		for (int i=0; i<numDiv; i++) {
			wdu.setACount(i, divCounts[i].getIntegerObject());
		}
        
        return wdu;
	}

	/**
	 * Return the number of time divisions associated with this database table
	 * @return
	 */
	public int getNumDiv() {
		return numDiv;
	}
	
	/**
	 * Look in the database table for an entry with a specific start time.  If found, create a new
	 * data unit and populate it with the data from that entry.  If not found, return null.
	 * 
	 * @param startOfDay the start date (in millis) to search for
	 * @return if an entry with the start date is found, a data unit is passed back populted with
	 * the values from that entry.  If no entry is found, null is returned
	 */
	public WAMSDivisionDataUnit checkForSpecificDataUnit(long startOfDay) {
		PamConnection con = DBControlUnit.findConnection();
		if (con==null) return null;

		WAMSDivisionDataUnit wsdu = null;
		
		// create an sql statement for the detections, get the results and loop through the rows
		String sqlStr = String.format("SELECT * FROM %s WHERE %s = %s", 
				tableDefinition.getTableName(),
				startTimeHeader,
				con.getSqlTypes().formatDBDateTimeQueryString(startOfDay));
//		System.out.println(sqlStr);

		// Execute the statement
		try {
			Statement stmt = con.getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			
			while(result.next()) {
				transferDataFromResult(con.getSqlTypes(), result);
				wsdu = (WAMSDivisionDataUnit) createDataUnit(con.getSqlTypes(), this.getLastTime(), this.getLastLoadIndex());
				wsdu.setUID(this.getLastLoadUID());
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error retrieving WAMS summary data from database");
			e.printStackTrace();
		}
		
		return wsdu;
	}

	public String getStartTimeHeader() {
		return startTimeHeader;
	}



}
