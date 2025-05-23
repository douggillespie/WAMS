package wamsPlugin;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import PamController.PamFolders;
import PamUtils.PamCalendar;
import PamUtils.TxtFileUtils;
import generalDatabase.DBControlUnit;
import generalDatabase.PamConnection;
import generalDatabase.SQLTypes;

/**
 * A class that takes care of everything to do with the historical means data.  Includes loading/saving the text file,
 * calculating means from the database, and updating current data
 * 
 * @author mo55
 *
 */
public class WAMSHistoryManager {
	
	/** The WAMSProcess object creating this */
	private WAMSProcess wamsProcess;
	
	/** Output data block for mean summary data */
	private WAMSDivisionDataBlock wamsMeanDataBlock;
	
	/** Summary Mean data unit - holds the mean values being used right now (not all the historical counts) */
	private WAMSDivisionDataUnit wamsMeanDataUnit;
	
	/** the csv file containing the summary mean data */
	private String summaryMeanFile;
	
	/** the historical counts, loaded from the file.  This contains Long values because the first column is a date */
	private ArrayList<ArrayList<Long>> historicalCounts;
	
	/** a hashmap mapping the date to the row in the historicalCounts array */
	private HashMap<Long, Integer> dateToRow;
	

	public WAMSHistoryManager(WAMSProcess wamsProcess) {
		this.wamsProcess = wamsProcess;
		
		// create the data block containing the mean summary data
		wamsMeanDataBlock = new WAMSDivisionDataBlock(WAMSDivisionDataUnit.class, wamsProcess.getWamsControl().getUnitName()+" Historical Mean", wamsProcess, 0);
		wamsMeanDataUnit = new WAMSDivisionDataUnit(PamCalendar.getTimeInMillis(), 0, wamsProcess.getWamsControl().getWamsParams().getNumDiv());
		wamsMeanDataBlock.addPamData(wamsMeanDataUnit);
		summaryMeanFile = wamsProcess.getWamsControl().getWamsParams().getCsvOutputFolder() + 
				File.separator + 
				"WAMSMeanValues_div" + 
				String.valueOf(wamsProcess.getWamsControl().getWamsParams().getNumDiv()) +
				".csv";
		
		// set the natural lifetime of the summary data block to be 5 time divisions
//		wamsMeanDataBlock.setNaturalLifetimeMillis((int) (PamCalendar.millisPerDay/wamsControl.getWamsParams().getNumDiv()*15));
		wamsMeanDataBlock.setNaturalLifetimeMillis(Integer.MAX_VALUE);
		
		// initialize the hashmap
		dateToRow = new HashMap<Long,Integer>();
	}
	
	
	
	public WAMSDivisionDataBlock getWamsMeanDataBlock() {
		return wamsMeanDataBlock;
	}

	/**
	 * Update the counts in a single time div
	 * 
	 * @param div the time division to update
	 * @param counts the counts to add
	 */
	public void updateDiv(long theDate, int div, Integer counts) {
		Long theCount = null;
		if (counts!=null) theCount = counts.longValue();
		int theRow = getRowFromDate(theDate);
		historicalCounts.get(theRow).set(div+1, theCount);	// offset by +1 because first column is date
		calculateSingleMean(div);
	}
	
	public Long getDivCount(long theDate, int div) {
		int theRow = getRowFromDate(theDate);
		return historicalCounts.get(theRow).get(div+1);	// offset by +1 because first column is date
	}



	/**
	 * Calculate the mean for a single division.  Use this after updating one of the divisions in historicalCounts, instead
	 * of wasting time recalculating the means for every division using calculateMeans()
	 * 
	 * @param div The time division to update
	 */
	private void calculateSingleMean(int div) {
		int numDays = historicalCounts.size();
		int totalCounts = 0;
		int totalRows = 0;
		for (int row=0; row<numDays; row++) {
			if (historicalCounts.get(row).get(div+1)!=null) {	// offset by 1 to take into account the date in the first column, and only include if non-null
				totalCounts += historicalCounts.get(row).get(div+1);	// offset by 1 to take into account the date in the first column
				totalRows++;
			}
		}
		Integer meanVal = null;
		if (totalRows>0) meanVal = totalCounts/totalRows;
		wamsMeanDataUnit.setACount(div, meanVal);
		wamsMeanDataBlock.updatePamData(wamsMeanDataUnit,System.currentTimeMillis());
	}
	
	/**
	 * Return the mean value for a specific time division
	 * 
	 * @param div The time division to check
	 * 
	 * @return the mean count for that time division, or 0 if the mean count is null
	 */
	public int getSingleMean(int div) {
		Integer count = wamsMeanDataUnit.getACount(div);
		if (count==null) count=0;
		return count;
	}
	
	private void setSummaryMeanFile() {
		summaryMeanFile = wamsProcess.getWamsControl().getWamsParams().getCsvOutputFolder() + 
				File.separator + 
				"WAMSMeanValues_div" + 
				String.valueOf(wamsProcess.getWamsControl().getWamsParams().getNumDiv()) +
				".csv";
	}
	
	/**
	 * Save the mean data to a file.  Any null values are replaced with NaN
	 */
	public void saveHistoricalCounts() {
		ArrayList<String> dataToWrite = new ArrayList<String>();
		int numDivs = historicalCounts.get(0).size();
		int numDays = historicalCounts.size();
		
		String countValsString;
		for (int i=0; i<numDays; i++) {
			countValsString = "*" + PamCalendar.formatDateTime2(historicalCounts.get(i).get(0), false);
			for (int j=1; j<numDivs; j++) {
				countValsString += ",";
				if (historicalCounts.get(i).get(j)==null) {
					countValsString += "NaN";
				}
				else {
					countValsString += String.valueOf(historicalCounts.get(i).get(j));
				}
			}
			dataToWrite.add(countValsString);
		}
		setSummaryMeanFile();
		TxtFileUtils.exportTxtData(summaryMeanFile, dataToWrite);
	}
	
	/**
	 * Load the historical means data, either from a file (preferred) or calculated from the database
	 */
	public void getHistoricalData() {
		resetDay(wamsProcess.calcStartOfDay(), wamsProcess.getWamsControl().getWamsParams().getNumDiv());
		setSummaryMeanFile();
		if ((new File(summaryMeanFile)).isFile()) {
			loadHistoricalCounts();
		}
		else {
			calculateCountsFromDB();
		}
		
		// quick check - if we weren't able to get any data from a file OR the database,
		// just create a historicalCounts list full of nulls
		if (historicalCounts==null || historicalCounts.isEmpty()) {
			ArrayList<Long> blankRow = createEmptyRow(wamsProcess.getWamsControl().getWamsParams().getNumDiv());
			blankRow.set(0, wamsProcess.calcStartOfDay());
			dateToRow.put(wamsProcess.calcStartOfDay(), 0);
			historicalCounts = new ArrayList<ArrayList<Long>>();
			historicalCounts.add(blankRow);	// adds a row of nulls, with today's date in the first column
		}
		else {
			calculateMeans();
		}
	}
	
	private int getRowFromDate(long theDate) {
		Integer theRow = dateToRow.get(theDate);
		if (theRow != null) return theRow;
		
		// if we've gotten here, we're on a new day so add a row to the end
		ArrayList<Long> blankRow = createEmptyRow(wamsProcess.getWamsControl().getWamsParams().getNumDiv());
		blankRow.set(0, theDate);
		historicalCounts.add(blankRow);
		dateToRow.put(theDate, historicalCounts.size()-1);
		return historicalCounts.size()-1;
	}
	
	/**
	 * Creates an ArrayList filled with null values.  The length is numDiv+1, because we
	 * need to add an extra column for the timestamp
	 * @param numDiv the number of time divisions
	 * @return
	 */
	private ArrayList<Long> createEmptyRow(int numDiv) {
		ArrayList<Long> emptyRow = new ArrayList<Long>(numDiv+1);
		for (int i=0; i<numDiv+1; i++) {
			emptyRow.add(null);
		}
		return emptyRow;
	}
	
	/**
	 * Load the historical means data from the file, and save to the internal array
	 */
	private void loadHistoricalCounts() {
		historicalCounts = new ArrayList<ArrayList<Long>>();
		setSummaryMeanFile();
		ArrayList<ArrayList<String>> strData = TxtFileUtils.importTxtDataToString(summaryMeanFile,true);
		if (strData==null) return; 

		for (int i=0; i<strData.size(); i++) {
			ArrayList<Long> convertedLine = new ArrayList<Long>(strData.get(i).size());

			for (int j=0; j<strData.get(i).size(); j++){
				String strCell = strData.get(i).get(j);
				
				if (j==0) {
					if (strCell.startsWith("*")) {
						strCell = strCell.substring(1);
					}
					long theDate = PamCalendar.millisFromDateTimeString(strCell, true);
					convertedLine.add(theDate);
					dateToRow.put(theDate, i);
				}
				else {
					if (strCell==null) {
						convertedLine.add(null);
					}
					else {
						convertedLine.add(Long.parseLong(strCell));
					}
				}
			}
			historicalCounts.add(convertedLine);
		}
	}
	
	/**
	 * Calculate the mean values from the database summary table
	 */
	private void calculateCountsFromDB() {
		
		PamConnection con = DBControlUnit.findConnection();
		if (con == null) {
			return;
		}
		
		historicalCounts = new ArrayList<ArrayList<Long>>();
		WAMSSummaryLogger wamsLogger = (WAMSSummaryLogger) wamsProcess.getSummaryDataBlock().getLogging();
		int numDiv = wamsProcess.getWamsControl().getWamsParams().getNumDiv();

		// create an sql statement for the detections, get the results and loop through the rows
		//			String sqlStr = String.format("SELECT AVG (%s) FROM %s",
		//					"Div" + String.valueOf(i),
		//					wamsLogger.getTableDefinition().getTableName());
		//			String sqlStr = String.format("SELECT %s FROM %s",
		//					"Div" + String.valueOf(i),
		//					wamsLogger.getTableDefinition().getTableName());
		String sqlStr = String.format("SELECT * FROM %s", wamsLogger.getTableDefinition().getTableName());
		//			Debug.out.println("Mean Div = " + String.valueOf(i) + ", " + sqlStr);

		// Execute the statement and just count all the rows
		try {
			Statement stmt = con.getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			int idx=0;

			while(result.next()) {
				ArrayList<Long> blankRow = createEmptyRow(numDiv);
				long theDate = SQLTypes.millisFromTimeStamp(result.getTimestamp(wamsLogger.getStartTimeHeader()));
				blankRow.set(0, theDate);
				dateToRow.put(theDate, idx);
				for (int i=0;i<numDiv; i++) {
					String colName = "Div" + String.valueOf(i);
					Object obj = result.getObject(colName);
					if (obj==null) {
						blankRow.set(i+1,null);
					}
					else {
						blankRow.set(i+1,(long) ((int) obj));
					}
				}
				historicalCounts.add(blankRow);
				idx++;
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error retrieving WAMS table detection data from database");
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate the means from the historical data and pass the values to the WAMSMeanDataUnit
	 */
	public void calculateMeans() {
		if (historicalCounts == null) {
			return;
		}
		int numDivs = historicalCounts.get(0).size();
		int numDays = historicalCounts.size();
		
		Integer[] meanVals = new Integer[numDivs-1];
		for (int div=1; div<numDivs; div++) {

			int totalCounts = 0;
			int totalRows = 0;
			for (int row=0; row<numDays; row++) {
				if (historicalCounts.get(row).get(div)!=null) {	// only count values that are non-null
					totalCounts += historicalCounts.get(row).get(div);
					totalRows++;
				}
			}

			if (totalRows==0) {	// if there weren't any rows to add, set the mean value as null
				meanVals[div-1] = null; // -1 because the first column is the start date, so we need to offset that
			}
			else {
				meanVals[div-1] = totalCounts/totalRows;  // -1 because the first column is the start date, so we need to offset that
			}
		}
		wamsMeanDataUnit.setAllCounts(meanVals);
		wamsMeanDataBlock.updatePamData(wamsMeanDataUnit,System.currentTimeMillis());
	}
	
	public void resetDay(long startOfDay, int numDiv) {
		wamsMeanDataUnit.resetCount(startOfDay, numDiv);
	}





}
