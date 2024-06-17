package wamsPlugin;

import java.util.ListIterator;

import PamUtils.PamCalendar;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class WAMSDivisionDataBlock extends PamDataBlock<WAMSDivisionDataUnit> {

	public WAMSDivisionDataBlock(Class unitClass, String dataName, PamProcess parentProcess, int channelMap) {
		super(unitClass, dataName, parentProcess, channelMap);
//		setNaturalLifetimeMillis((int) (PamCalendar.millisPerDay/((WAMSControl) ((WAMSProcess) parentProcess).getPamControlledUnit()).getWamsParams().getNumDiv()*2));
//		setNaturalLifetimeMillis(1000*60*60*2);
	}

	/**
	 * Searches through the data units to try and find one that has already been created for the
	 * day passed into this method.  The 'day' should be in milliseconds epoch time corresponding
	 * to 00:00:00.  If one does not already exist, it is created using the number of time divisions
	 * numDiv (as long as dontCreateNew=false).
	 * 
	 * @param startOfDay The day to search for (in millis, equal to 00:00:00 of the desired day)
	 * @param numDiv The number of time divisions to divide the day into
	 * @param dontCreateNew if true, this method will not create a new data unit if a suitable one is not found
	 * in the datablock or database.  Instead, it will return null
	 * 
	 * @return a new or existing data unit, or null if one cannot be found and dontCreateNew = true
	 */
	public WAMSDivisionDataUnit getDataUnitForThisDay(long startOfDay, int numDiv, boolean dontCreateNew) {
		
		// if there's is no associated database logger, exit immediately
		if (this.getLogging() == null) return null;
		
		// initialize return variable
		WAMSDivisionDataUnit aUnit;
		
		// FIRST, check if it already exists in the data units within this datablock.  If so,
		// pass that one back.  The assumption here is that if it's in the datablock, it must already be
		// in the database as well
		ListIterator<WAMSDivisionDataUnit> iterator = getListIterator(0);
		while (iterator.hasNext()) {
			aUnit = iterator.next();
			if (aUnit.getStartDay()==startOfDay && aUnit.getNumDiv()==numDiv) {
				return aUnit;
			}
		}
		
		// If it's not already in the datablock, check if this day already exists in the database.  If it does, create a data unit
		// from that row and pass that back
		if (((WAMSSummaryLogger) this.getLogging()).getNumDiv()==numDiv) {
			aUnit = ((WAMSSummaryLogger) this.getLogging()).checkForSpecificDataUnit(startOfDay);
			if (aUnit!=null) {
				this.addOldPamData(aUnit);
				return aUnit;
			}
		}
		
		// if we've gotten to this point, create a new data unit unless we're instructed not to
		if (dontCreateNew) {
			return null;
		}
		aUnit = new WAMSDivisionDataUnit(PamCalendar.getTimeInMillis(), startOfDay, numDiv);
		this.addPamData(aUnit);
		return aUnit;
	}

	@Override
	public void updatePamData(WAMSDivisionDataUnit pamDataUnit, long updateTimeMillis) {
		super.updatePamData(pamDataUnit, updateTimeMillis);
	}

	@Override
	public void remove() {
		super.remove();
	}

	@Override
	public synchronized void clearAll() {
		super.clearAll();
	}

	@Override
	public synchronized boolean remove(WAMSDivisionDataUnit aDataUnit) {
		return super.remove(aDataUnit);
	}

	@Override
	public synchronized boolean remove(WAMSDivisionDataUnit aDataUnit, boolean clearDatabase) {
		return super.remove(aDataUnit, clearDatabase);
	}

	@Override
	protected synchronized int removeOldUnitsT(long currentTimeMS) {
		return super.removeOldUnitsT(currentTimeMS);
	}

	@Override
	protected synchronized int removeOldUnitsS(long mastrClockSample) {
		return super.removeOldUnitsS(mastrClockSample);
	}

	@Override
	protected void removedDataUnit(WAMSDivisionDataUnit pamUnit) {
		super.removedDataUnit(pamUnit);
	}

	

}
