package wamsPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingWorker;

import PamController.PamController;
import PamController.PamViewParameters;
import PamDetection.LocContents;
import PamDetection.LocalisationInfo;
import PamModel.PamModel;
import PamUtils.PamCalendar;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamProcess;
import PamguardMVC.debug.Debug;
import alarm.AlarmDataUnit;
import generalDatabase.DBControlUnit;
import generalDatabase.PamConnection;

public class WAMSProcess extends PamProcess {
	
	/** link to the PAMControlledUnit */
	private WAMSControl wamsControl;
	
	/** Output data block - contains records of each detection sent to the WAMS plugin */
	private WAMSDataBlock wamsOutputDataBlock;
	
	/** Output data block for summary data */
	private WAMSDivisionDataBlock wamsSummaryOutput;
	
	/** Summary data unit currently being populated with data */
	private WAMSDivisionDataUnit wamsTodaysSummaryDataUnit;
	
	/** The historical data manager */
	private WAMSHistoryManager historyManager;
	
	/** The Source Detector data block */
	private PamDataBlock sourceDataBlock;
	
	/** The timer */
	private Timer countTimer;
	
	/** The start of the current time interval (milliseconds timestamp) */
	private long startOfInterval;
	
	/** The end of the current time interval (milliseconds timestamp) */
	private long endOfInterval;

	/** The number of detections counted */
	private int count;

	/** boolean indicating that we've already stopped - prevents the last count from being stored in the database multiple times */
	private boolean alreadyStopped = true;
	
	/** boolean indicating whether we want to (or are able to) veto certain angles */
	private boolean checkAngles = false;
	
	/** the start of the current day, in millis epoch time */
	private long startOfDay;

	/** a list of the end times of each time division, in millis */
	private long[] summaryEndTimes;
	
	/** the current time division being logged to */
	int divToLog=0;
	
	/** data block that just holds the current count - used as a source for the Alarm module */
	private WAMSDivCounterDataBlock wamsDivCounterDataBlock;

	/** data unit that just holds the current count - used as a source for the Alarm module */
	private WAMSDivCounterDataUnit wamsDivCounterDataUnit;
	
	private ArrayList<Long> detectStarts;
	
	private ArrayList<Long> detectEnds;

	private WAMSSummaryTable wamsSummaryTable;
	
	public WAMSProcess(WAMSControl pamControlledUnit) {
		super(pamControlledUnit, null);
		this.wamsControl = pamControlledUnit;
		
		// create the output data block
		wamsOutputDataBlock = new WAMSDataBlock(WAMSDataUnit.class, wamsControl.getUnitName(), this, 0);
		wamsOutputDataBlock.SetLogging(new WAMSLogger(wamsOutputDataBlock));
		addOutputDataBlock(wamsOutputDataBlock);
		
		// create the summary data block
		wamsSummaryOutput = new WAMSDivisionDataBlock(WAMSDivisionDataUnit.class, wamsControl.getUnitName()+" Summary", this, 0);
		wamsSummaryOutput.SetLogging(new WAMSSummaryLogger(wamsSummaryOutput, wamsControl.getWamsParams().getNumDiv()));
		addOutputDataBlock(wamsSummaryOutput);
		
		// set the natural lifetime of the summary data block to be 5 time divisions
//		wamsSummaryOutput.setNaturalLifetimeMillis((int) (PamCalendar.millisPerDay/wamsControl.getWamsParams().getNumDiv()*15));
		wamsSummaryOutput.setNaturalLifetimeMillis(Integer.MAX_VALUE);
		wamsSummaryOutput.setClearAtStart(false); // tells PAMGuard not to clear out the data units every time PAMGuard starts processing
		
		// create the historical data manager
		historyManager = new WAMSHistoryManager(this);
		
		// create the array lists to hold the start/end times of prev detections
		detectStarts = new ArrayList<Long>();
		detectEnds = new ArrayList<Long>();
		
		// create the output data block that just holds the current count
		wamsDivCounterDataBlock = new WAMSDivCounterDataBlock(wamsControl.getUnitName()+" Div Count", this, 0);
		wamsDivCounterDataBlock.addPamData(wamsDivCounterDataUnit = new WAMSDivCounterDataUnit(0));
		addOutputDataBlock(wamsDivCounterDataBlock);
	}
	
	public WAMSControl getWamsControl() {
		return wamsControl;
	}

	public WAMSDataBlock getWAMSDataBlock() {
		return wamsOutputDataBlock;
	}
	
	public WAMSDivisionDataBlock getSummaryDataBlock() {
		return wamsSummaryOutput;
	}
	
	public WAMSDivisionDataUnit getWamsTodaysSummaryDataUnit() {
		return wamsTodaysSummaryDataUnit;
	}

	public long[] getSummaryEndTimes() {
		return summaryEndTimes;
	}
	
	public void setSummaryEndTimes(long[] endTimes) {
		this.summaryEndTimes = endTimes;
	}

	public WAMSHistoryManager getHistoryManager() {
		return historyManager;
	}
	
	public void setSummaryTable(WAMSSummaryTable wamsSummaryTable) {
		this.wamsSummaryTable = wamsSummaryTable;
	}

	/**
	 * <p>Initialize all of the parameters, including the historical data so that the table is accurate.  Data from today can be
	 * included with the initialization or not, depending on the passed parameter.</p>  
	 * <p>If the boolean dontCreateNew is set to true, then this routine will try to include today's data unless it doesn't exist (e.g.
	 * if PAMGuard is running today for the first time).  If the boolean is set to false, a new data unit will be created if 
	 * necessary.  Typically we would want a new data unit created if it doesn't exist (dontCreateNew=false), the exception being
	 * if PAMGuard has just started up and this is being called from WAMSControl because an INITIALIZATION_COMPLETE
	 * notification has been sent.  That notification is sent before the UID Manager has a chance to synch up the latest UID value with
	 * the database.  A new data unit, in that case, would have a UID of 1.</p>
	 * 
	 *  @param dontCreateNew boolean indicating whether or not to create a new data unit if a suitable one isn't found in the database already
	 */
	public void initializeParams(boolean dontCreateNew) {
		// if the number of divisions in the summary table have changed, create a whole new logger
		testNumDivs();
		
		// set up the current summary data unit
        startOfDay = calcStartOfDay();
        
        // do a quick check here of the historical data already in the summary data block.  If the data unit dates are later than
        // the current startOfDay, get rid of them.  This happens because when PAMGuard starts, the date is set to the current date/time
        // and the calcOldSummaryData populates the table with info from the previous 5 days.  But if we are analyzing a file with an
        // old date, that doesn't become the PAMGuard clock date/time until the user hits Start.
		synchronized (this) {
			boolean updateList = false;
			ListIterator<WAMSDivisionDataUnit> it = wamsSummaryOutput.getListIterator(0);
			while (it.hasNext()) {
				WAMSDivisionDataUnit dataUnit = it.next();
				if (dataUnit.getStartDay() > startOfDay) {
					it.remove();
					updateList = true;
				}
			}
			if (updateList) {
//				calcOldSummaryData();
			}
		}
        
//        // get a data unit for today and set the divToLog field to the current div
		wamsTodaysSummaryDataUnit=wamsSummaryOutput.getDataUnitForThisDay(startOfDay, wamsControl.getWamsParams().getNumDiv(),dontCreateNew);
		if (wamsTodaysSummaryDataUnit!=null) {
			summaryEndTimes = wamsTodaysSummaryDataUnit.getDivEndTimes();
			for (divToLog=0; divToLog<wamsControl.getWamsParams().getNumDiv(); divToLog++) {
				if (PamCalendar.getTimeInMillis()<summaryEndTimes[divToLog]) {
					break;
				}
			}
		}
		
		// do a special check here to see if the 
		
		// clear the counts flag
		count = 0;
		
		// reset the day used to calculate means, to make sure that everything
		// is updated properly.  Then recalculate the mean values based on the most recent data
		historyManager.resetDay(startOfDay, wamsControl.getWamsParams().getNumDiv());
		historyManager.calculateMeans();
		
		// resize the tables?
		wamsControl.getWamsDisplayProvider().getComponent(null, null).getComponent().setVisible(false);
		wamsControl.getWamsDisplayProvider().getComponent(null, null).getComponent().setVisible(true);
	}

	
	public void calcTodaysData(boolean dontCreateNew) {
		// set up the current summary data unit
        startOfDay = calcStartOfDay();
        
       // if we're including today's data, go through the database table and try to summarize up until this time division
		wamsTodaysSummaryDataUnit=wamsSummaryOutput.getDataUnitForThisDay(startOfDay, wamsControl.getWamsParams().getNumDiv(),dontCreateNew);
		if (wamsTodaysSummaryDataUnit!=null) {
			summaryEndTimes = wamsTodaysSummaryDataUnit.getDivEndTimes();
			for (divToLog=0; divToLog<wamsControl.getWamsParams().getNumDiv(); divToLog++) {
				if (PamCalendar.getTimeInMillis()>=summaryEndTimes[divToLog]) {
					logSummaryDivInNewStream(divToLog);
				} else {
					logSummaryDivInNewStream(divToLog);	// one extra, so that we include the time division that we are currently in
					break;
				}
			}
		}
	}
	
	/**
	 * Calculate the millis that represent the start of today (00:00:00)
	 * @return
	 */
	public long calcStartOfDay() {
		// try to figure out the parameters of the summary timer.  If the current number of divisions doesn't match what is
        // already in the database, start a new table
		Calendar startCalendar = PamCalendar.getCalendarDate();
        startCalendar.set(Calendar.HOUR, 0);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        return startCalendar.getTimeInMillis();
	}
	
	public void calcOldSummaryData() {
		// set up the summary data unit for 5 days ago
		int numOfDays = 5;
		long prevDay = calcStartOfDay()-numOfDays*PamCalendar.millisPerDay;

		// go through the database table and try to summarize the days data.
		for (int i=0; i<numOfDays; i++) {
			wamsTodaysSummaryDataUnit=wamsSummaryOutput.getDataUnitForThisDay(prevDay, wamsControl.getWamsParams().getNumDiv(), true);
			if (wamsTodaysSummaryDataUnit!=null) {
				summaryEndTimes = wamsTodaysSummaryDataUnit.getDivEndTimes();
//				historyManager.setRowInHistoricalData(prevDay);
				for (divToLog=0; divToLog<wamsControl.getWamsParams().getNumDiv(); divToLog++) {
					logSummaryDiv(divToLog);
				}
			}
			prevDay+=PamCalendar.millisPerDay;
		}
	}

	
	/**
	 * Compare the number of divisions in the logger with the number of divisions in the params.  If they are different,
	 * create a new logger, clear the output data block of any existing data, and check for different historical data
	 */
	public void testNumDivs() {
		if (((WAMSSummaryLogger) wamsSummaryOutput.getLogging()).getNumDiv() != wamsControl.getWamsParams().getNumDiv()) {
			wamsSummaryOutput.SetLogging(new WAMSSummaryLogger(wamsSummaryOutput, wamsControl.getWamsParams().getNumDiv()));
			wamsSummaryOutput.getLogging().reCheckTable();	// make sure new table is created if needed
			wamsSummaryOutput.clearAll();
			getHistoricalData();
		}
	}

	
	/*
     * Subscribe to the detector data block
     */
    public void prepareProcess() {
        super.prepareProcess();

        // subscribe to the detector
        sourceDataBlock = PamController.getInstance().getDataBlockByLongName(wamsControl.getWamsParams().getSourceDetector());
		setParentDataBlock(sourceDataBlock);
		
		// subscribe to the alarm, if any
		// 2021-06-04 commented out to prevent infinite loops from occurring when using an older psfx that had subscribed to
		// an alarm
//		ArrayList<String> alarms = wamsControl.getWamsParams().getAlarmList();
//		if (!alarms.isEmpty()) {
//	        Boolean multithread = PamModel.getPamModel().isMultiThread();
//			for (String anAlarm : alarms) {
//				PamDataBlock alarmDataBlock = PamController.getInstance().getDataBlock(AlarmDataUnit.class, anAlarm);
//				alarmDataBlock.addObserver(this, multithread);
//			}
//		}
		if (sourceDataBlock == null) {
			return;
		}
		// decide if we should be checking angles
		if (sourceDataBlock.getLocalisationContents().hasLocContent(LocContents.HAS_BEARING) && wamsControl.getWamsParams().isVetoAngles()) {
			checkAngles = true;
		} else {
			checkAngles = false;
		}
    }
        
	@Override
	public void pamStart() {
		alreadyStopped = false;
		
		// initialize the parameters - done here, so that if PAMGuard has been idle for awhile we don't lose the summary of previous
		// data.  Also, this will automatically take into account changes to the parameters and a change in day
		this.initializeParams(false);
		
        // calculate the time of the first logging event (round up to the next
        // nth minute, where n is the time step specified in the WAMSParameters object)
		// and set a timer to log at that first event, and then every n minutes
		// afterwards
		// Note that ideally we'd simply set a Timer object here, but we can't.  PAMGuard is not necessarily processing in
		// real-time, but the Timer object always is.  We want the timer to run on PAMGuard time, which
		// might be real-time if our source is a daq card, but might instead be faster than real-time
		// if our source is a wav file.
		// Instead, create a timer than polls PAMGuard time every 100 ms, and then compare that
		// time to when we want to log.
//        calcStartAndEndTimes(PamCalendar.getTimeInMillis());
		Calendar startCalendar = PamCalendar.getCalendarDate();
        startOfInterval = startCalendar.getTimeInMillis();
        int unroundedMinutes = startCalendar.get(Calendar.MINUTE);
        int timeStep = wamsControl.getWamsParams().getTimeIntervalMinutes();
        int mod = unroundedMinutes % timeStep;
        startCalendar.add(Calendar.MINUTE, mod == 0 ? timeStep : timeStep - mod);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        endOfInterval = startCalendar.getTimeInMillis();
        
		// create the timer task.  This checks to see if we've hit the next logging time
        TimeCheck countTask = new TimeCheck();
        countTimer = new Timer();
        countTimer.scheduleAtFixedRate(countTask, 0, 100);	// run the timer every 100 ms, to continuously poll PAMGuard's time
	}
	
	/**
	 * Calculate the start and end times for the time period that would include the passed time.
	 * 
	 * NOT USED AT THE MOMENT!  This basically duplicated the summary table.  Instead, keep the start/end times
	 * as they were calculated before, based on the actual start and end times of the files.  That way you
	 * can use this table to go back and figure out how many detections there were in each file.  To get the total
	 * number of detections in a time period, use the Summary Table instead.
	 * 
	 * @param refTime the time around which the start and end times should be calculated
	 */
	public void calcStartAndEndTimes (long refTime) {
		Calendar theCalendar = PamCalendar.getCalendarDate(refTime);
        int unroundedMinutes = theCalendar.get(Calendar.MINUTE);
        int timeStep = wamsControl.getWamsParams().getTimeIntervalMinutes();
        int mod = unroundedMinutes % timeStep;
        theCalendar.add(Calendar.MINUTE, mod == 0 ? timeStep : timeStep - mod);
        theCalendar.set(Calendar.SECOND, 0);
        theCalendar.set(Calendar.MILLISECOND, 0);
        endOfInterval = theCalendar.getTimeInMillis();
        startOfInterval = endOfInterval - wamsControl.getWamsParams().getTimeIntervalMinutes()*60*1000;
	}
	
	/**
	 * Inner class, to check whether it's time to update the tables in the database
	 * 
	 * @author mo55
	 *
	 */
	public class TimeCheck extends TimerTask {
		
		int delayTime = 1000;
		
		public TimeCheck() {
		}
		
		@Override
		public void run() {
			
			// if we've passed the end of the current time interval, log the counts.  Note that
			// we've added a small delay here, so that we can be sure all detections have been
			// properly logged to the database
			if (PamCalendar.getTimeInMillis()>=endOfInterval+delayTime) {
				Debug.out.println("calling logCounts from timer run");
				logCounts();
			}
			
			// if we've passed the end of the current summary time division, summarize the activity.  Note that
			// we've added a slightly longer delay here than above, because we might be using data from the
			// table above and want to make sure it's complete first
			if (PamCalendar.getTimeInMillis()>=summaryEndTimes[divToLog]+delayTime*5) {
				
				// if we haven't just finished up the last time division of the day, take our time logging the summary
				if (divToLog<summaryEndTimes.length-1) {
					logSummaryDivInNewStream(divToLog);
					divToLog++;
				}
				
				// if we have just finished the last time division of the day, log before doing anything else and then
				// initialize the params.  If we set the summary log to start in it's own thread (as above), we run
				// the risk of the initializeParams setting a new date first and the summary data getting saved to the wrong place
				else {
					logSummaryDiv(divToLog);
					divToLog++;
					initializeParams(false);
				}
				
				// reset the dynamic counter
				wamsDivCounterDataUnit.setCurrentCount(0);
				wamsDivCounterDataBlock.notifyObservers(wamsDivCounterDataUnit);
				wamsTodaysSummaryDataUnit.setACount(divToLog, 0);
				wamsSummaryTable.forceTableUpdate();
			}
		}
	}

    /**
     * Detector has made a detection - increase the count variable
     */
    @Override
    public void newData (PamObservable o, PamDataUnit arg) {
    	
    	// if this is a detection, update the counter
    	if (o==sourceDataBlock ) {
			boolean skipThisDetection = false;
    		
    		// check angles, if that's what the user wants
    		if (checkAngles) {
    			
    			// if we don't have a localisation for this detection, exit immediately
    			if (arg.getLocalisation() == null) {
    				return;
    			}
    			
    			//get the horizontal angle: copied from the click detector BT display 
    			// THIS NEXT PART COMES UP WITH AN ANGLE RELATIVE TO NORTH - WOULD WORK
    			// FINE IF THERE IS A GPS SIGNAL, BUT MAY BE CONFUSING OTHERWISE.  BETTER
    			// TO JUST GET THE ANGLE RELATIVE TO THE PRIMARY AXIS, AND LET THE USER
    			// FIGURE IT OUT
//    			double angle = 0;
//    			GpsData oll;
//    			switch(arg.getLocalisation().getSubArrayType()) {
//    			case ArrayManager.ARRAY_TYPE_NONE:
//    			case ArrayManager.ARRAY_TYPE_POINT:
//    				return;
//    			case ArrayManager.ARRAY_TYPE_LINE:
//    				double[] surfaceAngle = arg.getLocalisation().getPlanarAngles();
//    				angle = Math.toDegrees(surfaceAngle[0]);
//    				break;
//    			case ArrayManager.ARRAY_TYPE_PLANE:
//    			case ArrayManager.ARRAY_TYPE_VOLUME:
//    				PamVector[] vecs = null;
//    				vecs = arg.getLocalisation().getRealWorldVectors();
//    				if (vecs == null || vecs.length < 1) {
//    					return;
//    				}
//    				angle = Math.toDegrees(PamVector.vectorToSurfaceBearing(vecs[0]));
//    				oll = arg.getOriginLatLong(false);
//    				if (oll != null) {
//    					angle -= oll.getHeading();
//    				}
//    				break;
//    			default:
//    				return;
//    			}
//    			angle = PamUtils.constrainedAngle(angle, 180.00001);
    			
    			LocalisationInfo unitLocalisationFlags = arg.getLocalisation().getLocContents();
    			double[] angles = arg.getLocalisation().getPlanarAngles();
    			int[] anglesToVeto = wamsControl.getWamsParams().getAnglesToVeto();
    			int nBearings = 0;
    			skipThisDetection = false;
    			if (angles != null && unitLocalisationFlags.hasLocContent(LocContents.HAS_BEARING)) {
    				nBearings = angles.length;
    			
    				// compare to the angle veto range defined by the user.  Set the flag
    				// if the horizontal angle falls outside of the range.  Do this for each bearing
    				// (in the case of a linear array, if either of the right/left bearing falls into the
    				// veto zone, don't log the detection)
    				for (int i = 0; i < nBearings; i++) {
						Debug.out.printf("WMD UID %d has angle %3.1f radians (%3.1f deg), checking against range %d to %d \n", arg.getUID(), angles[i], Math.toDegrees(angles[i]), anglesToVeto[0], anglesToVeto[1]);
						
//						if (Math.toDegrees(angles[i]) >= anglesToVeto[0] &&
//    							Math.toDegrees(angles[i]) <= anglesToVeto[1]) {

						// use algorithm from this website: https://math.stackexchange.com/questions/1044905/simple-angle-between-two-angles-of-circle
						// takes care of the problem when the circle wraps around from 180 deg to -180 deg
						int detAngDelta = (int) (Math.toDegrees(angles[i]) - anglesToVeto[0]);
						int endAngleDelta = anglesToVeto[1] - anglesToVeto[0];
						if (detAngDelta < 0) detAngDelta+=360;
						if (endAngleDelta < 0) endAngleDelta+=360;
						if (detAngDelta <= endAngleDelta) {
    						skipThisDetection = true;
    						Debug.out.printf("*** Skipping angle %3.1f radians (%3.1f deg) because it falls outside of range %d to %d \n", angles[i], Math.toDegrees(angles[i]), anglesToVeto[0], anglesToVeto[1]);
    						break;
						}
    				}

    			}
    		}
    		
    		// if we're trying to avoid harmonics, do a simple check
    		if (wamsControl.getWamsParams().isUsingHarmDet()) {
    			int minDiff = wamsControl.getWamsParams().getStartEndTime(); // min number of milliseconds between detection starts to consider them different (e.g. NOT harmonic)
    			int maxDiff = wamsControl.getWamsParams().getMinGap();	// max number of milliseconds between end of one detection and start of the next, before we discard the old one
    			
    			
    			// get the start and end of the current detection
    			long startSample = arg.getTimeMilliseconds();
    			long endSample = (long) (startSample + arg.getDurationInMilliseconds());
    			
    			// cycle through all of the detections we're currently holding, checking to see if the start and end values line up within minDiff samples
    			// while we're at it, discard any detections that are more the maxDiff samples in the past
    			if (detectStarts.size()>0) {
    				ArrayList<Long> startsToRemove = new ArrayList<Long>();
    				ArrayList<Long> endsToRemove = new ArrayList<Long>();
		    		for (int i=0; i<detectStarts.size(); i++) {
		    			if ( (Math.abs(detectStarts.get(i)-startSample) < minDiff ) &&
		    				 (Math.abs(detectEnds.get(i)-endSample) < minDiff )) {
		    				skipThisDetection = true;
//		    				Debug.out.println("Found harmonic - start time = " + PamCalendar.formatDateTime2(startSample) +
//		    						" end time = " + PamCalendar.formatDateTime2(endSample));
		    			}
		    			
		    			if (startSample - detectEnds.get(i) > maxDiff) {
		    				startsToRemove.add(detectStarts.get(i));
		    				endsToRemove.add(detectEnds.get(i));
		    			}
		    		}
		    		
		    		// remove any detection that are too old
		    		detectStarts.removeAll(startsToRemove);
		    		detectEnds.removeAll(endsToRemove);
    			}
    			
    			detectStarts.add(startSample);
    			detectEnds.add(endSample);
    		}
    		
    		// if we're not skipping this detection, update the counter
    		if (!skipThisDetection) {
    			synchronized (this) {
//    				Debug.out.println("Counting detection - start time = " + PamCalendar.formatDateTime2(arg.getTimeMilliseconds()));
    				count++;
    				wamsDivCounterDataUnit.incCurrentCount();
    				wamsDivCounterDataBlock.notifyObservers(wamsDivCounterDataUnit);
					wamsTodaysSummaryDataUnit.setACount(divToLog, wamsDivCounterDataUnit.getCurrentCount());
					wamsSummaryTable.forceTableUpdate();
    			}
    		}
    	}
    	
    	// if this is an alarm, log it in the database
    	else {
    		logAlarm(arg);
    	}
    }
    
    
	/**
	 * Log the current count and reset the counter
	 */
	private synchronized void logCounts() {
		WAMSDataUnit wdu = wamsOutputDataBlock.generateDataUnit(PamCalendar.getTimeInMillis(),startOfInterval,endOfInterval,count);
//		WAMSDataUnit wdu = new WAMSDataUnit(PamCalendar.getTimeInMillis(),startOfInterval,endOfInterval,count);
		Debug.out.println("logging " + String.valueOf(count) + " for time period " + PamCalendar.formatDateTime2(startOfInterval) + " to " + PamCalendar.formatDateTime2(endOfInterval));
		wdu.setDetector(wamsControl.getWamsParams().getSourceDetector());
		wamsOutputDataBlock.addPamData(wdu);
		
		count=0;
		startOfInterval = endOfInterval;
		endOfInterval+=wamsControl.getWamsParams().getTimeIntervalMinutes()*60*1000;
//		calcStartAndEndTimes(endOfInterval);
	}

	private void logAlarm(PamDataUnit arg) {
		AlarmDataUnit alarm = (AlarmDataUnit) arg;
		int alarmState = alarm.getCurrentStatus();
		
		// record alarm if alarm status = 1 (amber) or 2 (red)
		if (alarmState>0) {
			WAMSDataUnit wdu = new WAMSDataUnit(PamCalendar.getTimeInMillis(),
					alarm.getLastStateTime()[alarmState],
					alarm.getLastStateTime()[alarmState],
					(int) alarm.getCurrentScore());
			wdu.setAlarm(alarm.getParentDataBlock().getDataName());
			wamsOutputDataBlock.addPamData(wdu);
		}
	}
	

	/**
	 * Query the database for detections within the time division div, and update
	 * the summary output data unit.  Because we're not sure how long this might take
	 * (and there isn't really a hurry for it) run this in a separate thread
	 * 
	 * @param div the time division to summarize
	 */
	private void logSummaryDivInNewStream(int div) {
		PamConnection con = DBControlUnit.findConnection();
		if (con == null) {
			return;
		}
		
		// generate the report in a separate thread
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>()
		{
		    @Override
		    protected Void doInBackground()
		    {
		    	logSummaryDiv(div);
		    	
//				// reset the dynamic counter
//				wamsDivCounterDataUnit.setCurrentCount(0);
//				wamsDivCounterDataBlock.notifyObservers(wamsDivCounterDataUnit);
//				wamsTodaysSummaryDataUnit.setACount(div, 0);
//				wamsSummaryTable.forceTableUpdate();

		        return null;
		    }
		};
		worker.execute();
	}
	
	/**
	 * Query the database for detections within the time division div, and update
	 * the summary output data unit.
	 * 
	 * @param div the time division to summarize
	 */
	private synchronized void logSummaryDiv(int div) {
		
		PamConnection con = DBControlUnit.findConnection();
		if (con == null) {
			return;
		}
		WAMSLogger wamsLogger = (WAMSLogger) this.getWAMSDataBlock().getLogging();
		long dataEnd = wamsTodaysSummaryDataUnit.getDivEndTimes()[div];
		long dataStart = dataEnd-(PamCalendar.millisPerDay/wamsControl.getWamsParams().getNumDiv())+1;
		PamViewParameters pvp = new PamViewParameters(dataStart, dataEnd);
		pvp.useAnalysisTime = false;

		// create an sql statement for the detections, get the results and loop through the rows
		String sqlStr = String.format("SELECT * FROM %s WHERE %s >= %s AND %s <= %s AND %s != ''", 
				wamsLogger.getTableDefinition().getTableName(),
				wamsLogger.getStartHeader(),
				con.getSqlTypes().formatDBDateTimeQueryString(pvp.getRoundedViewStartTime()),
				wamsLogger.getEndHeader(),
				con.getSqlTypes().formatDBDateTimeQueryString(pvp.getRoundedViewEndTime()),
				wamsLogger.getDetectorHeader());
//		Debug.out.println("Summary, Div = " + div + ", " + sqlStr);
		
		Integer counts=0;
		int numRows = 0;
		
		// Execute the statement and just count all the rows
		try {
			Statement stmt = con.getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			
			while(result.next()) {
				counts += result.getInt(wamsLogger.getCountHeader());
				numRows++;
//				Debug.out.println("     Div = " + div + " Counts = "+counts);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error retrieving WAMS table detection data from database");
			e.printStackTrace();
		}
		if (numRows==0) counts=null;	// if no rows were returned from the query, set the value to null
		
		// if nothing was returned from the database, just return. This can happen if we've changed database
		// partway through the day.  It's empty, but wamsTodaysSummaryDataUnit and the historyManager are
		// both full of information. We don't want to overwrite, so just return
		if (counts==null) return;
		
		// if we're at this point, log what was read from the database and update.  Also force the dynamic
		// counter to match the database count
		wamsTodaysSummaryDataUnit.setACount(div, counts);
		wamsSummaryOutput.updatePamData(wamsTodaysSummaryDataUnit,System.currentTimeMillis());
		historyManager.updateDiv(wamsTodaysSummaryDataUnit.getStartDay(), div, counts);
		wamsDivCounterDataUnit.setCurrentCount(counts);
		wamsDivCounterDataBlock.notifyObservers(wamsDivCounterDataUnit);
	}

	@Override
	public void pamStop() {
		// save the count at the current time, and stop the timer
		endOfInterval=PamCalendar.getTimeInMillis();
		if (!alreadyStopped) {	// only log this final count if we haven't already
			try {
				Thread.sleep(1000);	// pause for a second - sometimes the WMD lags behind and sends out a few more detections
			} catch (InterruptedException ex) {}
			Debug.out.println("calling logCounts from pamStop");
			logCounts();
			logSummaryDiv(divToLog);
			alreadyStopped=true;
		}
		if (countTimer!=null) {
			countTimer.cancel();
		}
	}

	public void getHistoricalData() {
		historyManager.getHistoricalData();
	}

	public void saveHistoricalData() {
		historyManager.saveHistoricalCounts();
	}


}
