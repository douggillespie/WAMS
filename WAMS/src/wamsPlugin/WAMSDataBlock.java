package wamsPlugin;

import java.util.ListIterator;

import PamUtils.PamCalendar;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import PamguardMVC.debug.Debug;

public class WAMSDataBlock extends PamDataBlock<WAMSDataUnit> {

	public WAMSDataBlock(Class unitClass, String dataName, PamProcess parentProcess, int channelMap) {
		super(unitClass, dataName, parentProcess, channelMap);
	}

	/**
	 * Check to see if this data block already contains a data unit with the start/end times given.  If so, adds
	 * the passed count to the existing count.  If not, creates a new data unit.
	 * 
	 * @param timeInMillis
	 * @param startOfInterval
	 * @param endOfInterval
	 * @param count
	 * @return
	 */
	public WAMSDataUnit generateDataUnit(long timeInMillis, long startOfInterval, long endOfInterval, int count) {
		
		// initialize return variable
		WAMSDataUnit aUnit;
		
		// FIRST, check if it already exists in the data units within this datablock.  If so,
		// pass that one back.  The assumption here is that if it's in the datablock, it must already be
		// in the database as well
		ListIterator<WAMSDataUnit> iterator = getListIterator(0);
		while (iterator.hasNext()) {
			aUnit = iterator.next();
			if (aUnit.getStartTime()==startOfInterval && aUnit.getEndTime()==endOfInterval) {
				int newCount = aUnit.getCount() + count;
				aUnit.setCount(newCount);
				return aUnit;
			}
		}
		
		// if we've gotten to this point, create a new data unit
		aUnit = new WAMSDataUnit(timeInMillis, startOfInterval, endOfInterval, count);
		Debug.out.println("Creating new wamsDataUnit for time period " + PamCalendar.formatDateTime2(startOfInterval) + " to " + PamCalendar.formatDateTime2(endOfInterval));
		return aUnit;
	}

}
