package wamsPlugin;

import PamUtils.PamCalendar;
import PamView.component.DataBlockTableView;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamObserver;

public class WAMSSummaryTable extends DataBlockTableView<WAMSDivisionDataUnit> {

	private String[] colNames;
	
	private String firstColName = "Start Date";
	
	private WAMSProcess wamsProcess;
	
	private int numDiv;
	
	private WAMSDivisionDataBlock summaryDataBlock;
	
	private PamObserver tableObserver;

	public WAMSSummaryTable(WAMSProcess wamsProcess, WAMSDivisionDataBlock summaryDataBlock, String title, String firstColName) {
		super(summaryDataBlock, title);
		this.wamsProcess = wamsProcess;
		this.summaryDataBlock = summaryDataBlock;
		this.firstColName = firstColName;
		generateColumnNames();
		findTableObserver();
	}
	
	/**
	 * 
	 */
	private void findTableObserver() {
		int numObs = summaryDataBlock.countObservers();
		for (int i=0; i<numObs; i++) {
			PamObserver anObserver = summaryDataBlock.getPamObserver(i);
			if (anObserver.getClass().getEnclosingClass().isAssignableFrom(DataBlockTableView.class)) {
				tableObserver = anObserver;
			}
		}
		
	}

	private void generateColumnNames() {
		long[] divEndTimes = wamsProcess.getSummaryEndTimes();
		if (divEndTimes==null) return;
		numDiv = divEndTimes.length;
		colNames = new String[numDiv+1];
		colNames[0] = firstColName;
		colNames[1] = "00:00-" + PamCalendar.formatDateTime2(divEndTimes[0],"HH:mm",false);
		for (int i=1; i<numDiv; i++) {
			colNames[i+1] = PamCalendar.formatDateTime2(divEndTimes[i-1]+1000,"HH:mm",false) + "-" + PamCalendar.formatDateTime2(divEndTimes[i],"HH:mm",false);
		}
		this.fireTableStructureChanged();
	}

	@Override
	public String[] getColumnNames() {
		return colNames;
	}
	
	@Override
	public Object getColumnData(WAMSDivisionDataUnit dataUnit, int columnIndex) {
		if (dataUnit == null || columnIndex >= dataUnit.getNumDiv()) {
			return null;
		}
		switch (columnIndex) {
		case 0:
			return PamCalendar.formatDate(dataUnit.getStartDay());
		default:
			Integer theCount;
//			if (columnIndex-1==wamsProcess.divToLog) {
//				theCount = dataUnit.getDynamicCount();
//			} else {
				theCount = dataUnit.getCount()[columnIndex-1];
//			}
			return theCount;
		}
	}

	public void updateColumnHeaders() {
		generateColumnNames();
	}

	@Override
	public String getToolTipText(WAMSDivisionDataUnit dataUnit, int columnIndex) {
		if (dataUnit == null || columnIndex==0) {
			String[] colNames = getColumnNames();
			if (colNames != null && colNames.length > columnIndex) {
				return colNames[columnIndex];
			}
			else {
				return null;
			}
		} else {
			String str = "<html>";
			long[] divEndTimes = wamsProcess.getSummaryEndTimes();
			if (divEndTimes!=null) {
				str += "<p> UTC Date: " + PamCalendar.formatDate(dataUnit.getStartDay()) + "</p>";
				if (columnIndex==1) {
					str += "<p>UTC Time: 00:00";
				} else {
					str += "<p>UTC Time: " + PamCalendar.formatDateTime2(divEndTimes[columnIndex-2]+1000,"HH:mm",false);
				}
				str += "-" + PamCalendar.formatDateTime2(divEndTimes[columnIndex-1],"HH:mm",false) + "</p>";
				str += "<p> Local Date: " + PamCalendar.formatLocalDateTime2(dataUnit.getStartDay(),"dd MMMM yyyy",false) + "</p>";
				if (columnIndex==1) {
					str += "<p>Local Time: " + PamCalendar.formatLocalDateTime2(dataUnit.getStartDay(),"HH:mm",false);
				} else {
					str += "<p>Local Time: " + PamCalendar.formatLocalDateTime2(divEndTimes[columnIndex-2]+1000,"HH:mm",false);
				}
				str += "-" + PamCalendar.formatLocalDateTime2(divEndTimes[columnIndex-1],"HH:mm",false) + "</p>";
			}
			str += "<p>Counts: " + dataUnit.getCount()[columnIndex-1] + "</p></html>";
			return str;
		}
	}
	
	public void forceTableUpdate() {
		tableObserver.addData(summaryDataBlock, null);
	}
	

}
