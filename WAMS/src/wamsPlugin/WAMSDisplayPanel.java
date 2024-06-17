package wamsPlugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import PamController.PamSettingManager;
import PamUtils.PamCalendar;
import PamguardMVC.PamDataUnit;
import PamguardMVC.PamObservable;
import PamguardMVC.PamObserver;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import pamViewFX.fxNodes.PamBorderPane;
import pamViewFX.fxNodes.pamChart.PamLineChart;
import pamViewFX.fxNodes.pamDialogFX.PamJFXPanel;
import userDisplay.UserDisplayComponent;

public class WAMSDisplayPanel implements UserDisplayComponent {
	
	private WAMSControl wamsControl;
	
	private JPanel mainPanel;

	private String uniqueName;

	private WAMSSummaryTable wamsSummaryTable;
	
	private WAMSSummaryTable wamsMeanTable;

	private JPanel meanDisplayInnerPanel;
	
	private SummaryChartFX summaryChart;
	
	private VBox holder;

	private PamJFXPanel dlgContent;
	
	private SummaryObs summaryObserver;
	
	private MeanObs meanObserver;

	
	public WAMSDisplayPanel(WAMSControl wamsControl) {
		this.wamsControl = wamsControl;
		wamsSummaryTable = new WAMSSummaryTable(wamsControl.getWamsProcess(),wamsControl.getWamsProcess().getSummaryDataBlock(),"Summary Detection Table","Start Date");
		wamsMeanTable = new WAMSSummaryTable(wamsControl.getWamsProcess(),wamsControl.getWamsProcess().getHistoryManager().getWamsMeanDataBlock(),"Mean Detection Table","Cur Date");
		
		
		// Attempt using GridBagLayout as the LayoutManager - Works well, except the Mean Detection panel doesn't size properly
		// by itself.  I have to force a minimum size in order to show the first row.  Can't figure out why this is
		mainPanel = new JPanel();
		GridBagLayout gbl_SummaryTableDisplay = new GridBagLayout();
		gbl_SummaryTableDisplay.columnWidths = new int[]{0, 0};
		gbl_SummaryTableDisplay.rowHeights = new int[]{0, 0, 0, 0};
		gbl_SummaryTableDisplay.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_SummaryTableDisplay.rowWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};	// the 0.0 in the second index means the Mean Detections panel doesn't resize (what we want)
		mainPanel.setLayout(gbl_SummaryTableDisplay);
		
		JPanel summaryDisplayPanel = new JPanel();
		summaryDisplayPanel.setBorder(new TitledBorder(null, "Summary of Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_summaryDisplayPanel = new GridBagConstraints();
		gbc_summaryDisplayPanel.insets = new Insets(10, 5, 5, 5);
		gbc_summaryDisplayPanel.fill = GridBagConstraints.BOTH;
		gbc_summaryDisplayPanel.gridx = 0;
		gbc_summaryDisplayPanel.gridy = 0;
		mainPanel.add(summaryDisplayPanel, gbc_summaryDisplayPanel);
		GridBagLayout gbl_summaryDisplayPanel = new GridBagLayout();
		gbl_summaryDisplayPanel.columnWidths = new int[]{0, 0};
		gbl_summaryDisplayPanel.rowHeights = new int[]{0, 0};
		gbl_summaryDisplayPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_summaryDisplayPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		summaryDisplayPanel.setLayout(gbl_summaryDisplayPanel);
		
		GridBagConstraints gbc_summaryDisplayScroller = new GridBagConstraints();
		gbc_summaryDisplayScroller.insets = new Insets(5, 5, 5, 5);
		gbc_summaryDisplayScroller.fill = GridBagConstraints.BOTH;
		gbc_summaryDisplayScroller.gridx = 0;
		gbc_summaryDisplayScroller.gridy = 0;
		summaryDisplayPanel.add(wamsSummaryTable.getComponent(), gbc_summaryDisplayScroller);
		
		JPanel meanDisplayPanel = new JPanel();
		meanDisplayPanel.setBorder(new TitledBorder(null, "Mean Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_meanDisplayPanel = new GridBagConstraints();
		gbc_meanDisplayPanel.insets = new Insets(5, 5, 5, 5);
		gbc_meanDisplayPanel.fill = GridBagConstraints.BOTH;
		gbc_meanDisplayPanel.gridx = 0;
		gbc_meanDisplayPanel.gridy = 1;
		mainPanel.add(meanDisplayPanel, gbc_meanDisplayPanel);
		GridBagLayout gbl_meanDisplayPanel = new GridBagLayout();
		gbl_meanDisplayPanel.columnWidths = new int[]{0, 0};
		gbl_meanDisplayPanel.rowHeights = new int[]{0, 0};
		gbl_meanDisplayPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_meanDisplayPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		meanDisplayPanel.setLayout(gbl_meanDisplayPanel);
		
		meanDisplayInnerPanel = new JPanel();
		GridBagConstraints gbc_meanDisplayInnerPanel = new GridBagConstraints();
		gbc_meanDisplayInnerPanel.insets = new Insets(5, 5, 5, 5);
		gbc_meanDisplayInnerPanel.anchor = GridBagConstraints.NORTH;
		gbc_meanDisplayInnerPanel.fill = GridBagConstraints.BOTH;
		gbc_meanDisplayInnerPanel.gridx = 0;
		gbc_meanDisplayInnerPanel.gridy = 0;
		meanDisplayPanel.add(meanDisplayInnerPanel, gbc_meanDisplayInnerPanel);
		meanDisplayInnerPanel.setLayout(new BorderLayout(0, 0));
		meanDisplayInnerPanel.add(wamsMeanTable.getComponent(), BorderLayout.CENTER);
		
		// don't know why I need this - GridBagLayout should be resizing automatically when DataBlockTableView.DataObs.update calls
		// blockTableModel.fireTableDataChanged().  But it doesn't - all it shows is the header row of the table.  
		double scaling = PamSettingManager.getInstance().getCurrentDisplayScaling();
		int heightToUse = (int) (42 * scaling);
		meanDisplayInnerPanel.setMinimumSize(new Dimension(1, heightToUse)); 
		
		JPanel meanChartPanel = new JPanel();
		GridBagConstraints gbc_meanChartPanel = new GridBagConstraints();
		gbc_meanChartPanel.insets = new Insets(5, 5, 10, 5);
		gbc_meanChartPanel.fill = GridBagConstraints.BOTH;
		gbc_meanChartPanel.gridx = 0;
		gbc_meanChartPanel.gridy = 2;
		mainPanel.add(meanChartPanel, gbc_meanChartPanel);
		meanChartPanel.setLayout(new BorderLayout(0, 0));
		meanChartPanel.add(createJFXPane(), BorderLayout.CENTER);
		summaryObserver = new SummaryObs();
		meanObserver = new MeanObs();
		wamsControl.getWamsProcess().getSummaryDataBlock().addObserver(summaryObserver);
		wamsControl.getWamsProcess().getHistoryManager().getWamsMeanDataBlock().addObserver(meanObserver);

		
		
		
		
		// Attempt using BorderLayout as the LayoutManager - Summary of Detection panels is resizing with the window (good),
		// but the Mean Detections panel is WAY too tall and I can't seem to force it smaller
//		mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
//		
//		JPanel tablePanel = new JPanel();
//		mainPanel.add(tablePanel);
//		tablePanel.setLayout(new BorderLayout(0, 10));
//		tablePanel.setBorder(new EmptyBorder(10, 5, 0, 5));
//		
//		JPanel summaryDisplayPanel = new JPanel();
//		tablePanel.add(summaryDisplayPanel);
//		summaryDisplayPanel.setBorder(new TitledBorder(null, "Summary of Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		summaryDisplayPanel.setLayout(new BorderLayout(0, 0));
//		
//		JPanel innerSummaryPanel = new JPanel();
//		innerSummaryPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		summaryDisplayPanel.add(innerSummaryPanel, BorderLayout.CENTER);
//		innerSummaryPanel.setLayout(new BorderLayout(0, 0));
//		innerSummaryPanel.add(wamsSummaryTable.getComponent(), BorderLayout.CENTER);
//		
//		JPanel meanDisplayPanel = new JPanel();
//		tablePanel.add(meanDisplayPanel, BorderLayout.SOUTH);
//		meanDisplayPanel.setBorder(new TitledBorder(null, "Mean Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		meanDisplayPanel.setLayout(new BorderLayout(0, 0));
//		
//		JPanel innerMeanPanel = new JPanel();
//		innerMeanPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		meanDisplayPanel.add(innerMeanPanel, BorderLayout.NORTH);
//		innerMeanPanel.setLayout(new BorderLayout(0, 0));
//		innerMeanPanel.add(wamsMeanTable.getComponent(), BorderLayout.NORTH);
//		meanDisplayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
//		innerMeanPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
//		wamsMeanTable.getComponent().setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
//		
//		JPanel meanChartPanel = new JPanel();
//		mainPanel.add(meanChartPanel);
//		meanChartPanel.setLayout(new BorderLayout(0, 0));
//		JTextArea dummText = new JTextArea(10, 1);
//		meanChartPanel.add(dummText, BorderLayout.CENTER);

	}
	
	/**
	 * Create the jfx panel 
	 * @return
	 */
	private JFXPanel createJFXPane(){

		//this has to be called in order to initialise the FX toolkit. Otherwise will crash if no other 
		//FX has been called. 
		dlgContent = new PamJFXPanel();

//		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(()->{
//			holder = new VBox();
//			holder.getChildren().add(summaryChart);
//			dlgContent.setRoot(holder);
			summaryChart = new SummaryChartFX();
			dlgContent.setRoot(summaryChart);
			try {
				URL res = getClass().getResource("/resources/chartStyle.css");
				if (res != null) {
					dlgContent.getScene().getStylesheets().add(res.toExternalForm());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		return dlgContent; 
	}


	@Override
	public Component getComponent() {
		return mainPanel;
	}

	@Override
	public void openComponent() {
	}

	@Override
	public void closeComponent() {
	}

	@Override
	public void notifyModelChanged(int changeType) {
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	@Override
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public String getFrameTitle() {
		return wamsControl.getUnitName() + " Summary Display";
	}

	public void updateColumnHeaders() {
		wamsSummaryTable.updateColumnHeaders();
		wamsMeanTable.updateColumnHeaders();
	}

	/**
	 * Inner class that does all of the work.  This uses a lot of JavaFX objects, and so
	 * before we create it the FX Toolkit needs to have been initialized.  That's why we
	 * couldn't simply declare the FX fields (like NumberAxis and LineChart) in the outer
	 * class - they would have been created before the constructor was run, and therefore before
	 * the FX Toolkit had been initialized
	 * 
	 * @author mo55
	 *
	 */
	public class SummaryChartFX extends PamBorderPane {

		/** a list of the data series to be shown on the chart */
		private ArrayList<XYChart.Series<Number, Number>> seriesList = new ArrayList<XYChart.Series<Number, Number>>();

		/** the x axis */
		private NumberAxis xAxis = new NumberAxis();

		/** the y axis */
		private NumberAxis yAxis = new NumberAxis();

		/** the chart object */
		private PamLineChart<Number,Number> lineChart;

		XYChart.Series<Number, Number> summaryData;
		
		XYChart.Series<Number, Number> meanData;

//		Runnable javaFXThread;

		/**
		 * Main Constructor
		 * 
		 * @param chartTitle The title of the chart - can be null
		 */
		private SummaryChartFX() {
			this.setCenter(createGraph());
		}

		private LineChart<Number, Number> createGraph() {
			xAxis.setAutoRanging(false);
			yAxis.setAutoRanging(true);
			summaryData = new XYChart.Series<Number, Number>();
			summaryData.setName("Last 24 Hours");
			meanData = new XYChart.Series<Number, Number>();
			meanData.setName("Mean Detections");
			lineChart = new PamLineChart<Number,Number>(xAxis,yAxis);
			lineChart.setAnimated(false);
			lineChart.getData().clear();
			lineChart.getData().add(summaryData);
			lineChart.getData().add(meanData);
			meanData.getNode().setStyle("-fx-stroke-dash-array: 2 12 12 2;");
//			summaryData.getNode().setStyle("-fx-stroke: steelblue; -fx-background: steelblue;");
//			meanData.getNode().setStyle("-fx-stroke-dash-array: 2 12 12 2; -fx-stroke: orange; -fx-background: orange;");
			xAxis.setTickLabelFormatter(new StringConverter<Number>() {
				@Override
				public String toString(Number object) {
					String dateTime;
					dateTime = PamCalendar.formatDateTime2(object.longValue());
					return dateTime;
				}
				@Override
				public Number fromString(String string) {
					long dateTime;
					dateTime = PamCalendar.millisFromDateTimeString(string, true);
					return dateTime;
				}
			});

			return lineChart;
		}

		/**
		 * Add a data series to the chart.  Note that to make it all a little easier, any y values that are null are replaced with 0
		 * 
		 * @param divList The x-values, division end times
		 * @param counts The number of counts in each division
		 */
		public void addSummaryData(long[] divList, Integer[] counts) {
			Platform.runLater(()->{
				summaryData.getData().clear();
				for (int i=0; i<divList.length; i++) {
					int newCounts;
					if (counts[i]==null) {
						newCounts=0;
					} else {
						newCounts = counts[i];
					}
					summaryData.getData().add(new XYChart.Data<Number, Number>(divList[i], newCounts));
				}
				xAxis.setLowerBound(divList[0]);
				xAxis.setUpperBound(divList[divList.length-1]);
				double tick = ((double) (divList[divList.length-1]-divList[0]))/(wamsControl.getWamsParams().getNumDiv()-1);
				xAxis.setTickUnit(tick);
			});
		}
		
		/**
		 * Add a data series to the chart.  Note that to make it all a little easier, any y values that are null are replaced with 0
		 * 
		 * @param divList The x-values, division end times
		 * @param numCounts	The number of counts in each division
		 */
		public void addMeanData(long[] divList, Integer[] numCounts) {
			Platform.runLater(()->{
				meanData.getData().clear();
				for (int i=0; i<divList.length; i++) {
					int newCounts;
					if (numCounts[i]==null) {
						newCounts=0;
					} else {
						newCounts = numCounts[i];
					}
					meanData.getData().add(new XYChart.Data<Number, Number>(divList[i], newCounts));
				}
				xAxis.setLowerBound(divList[0]);
				xAxis.setUpperBound(divList[divList.length-1]);
				double tick = ((double) (divList[divList.length-1]-divList[0]))/(wamsControl.getWamsParams().getNumDiv()-1);
				xAxis.setTickUnit(tick);
			});
		}
	}
	
	/**
	 * Inner class to watch for changes to the Summary Data Block
	 * 
	 * @author mo55
	 *
	 */
	public class SummaryObs implements PamObserver {

		@Override
		public long getRequiredDataHistory(PamObservable o, Object arg) {
			return 0;
		}

		@Override
		public void removeObservable(PamObservable o) {
		}

		@Override
		public void setSampleRate(float sampleRate, boolean notify) {
		}

		@Override
		public void noteNewSettings() {
		}

		@Override
		public String getObserverName() {
			return "WAMS Summary Data Observer";
		}

		@Override
		public void masterClockUpdate(long milliSeconds, long sampleNumber) {
		}

		@Override
		public PamObserver getObserverObject() {
			return this;
		}

		@Override
		public void addData(PamObservable observable, PamDataUnit pamDataUnit) {
			WAMSDivisionDataUnit data = (WAMSDivisionDataUnit) pamDataUnit;
			summaryChart.addSummaryData(data.getDivEndTimes(), data.getCount());
		}

		@Override
		public void updateData(PamObservable observable, PamDataUnit pamDataUnit) {
		}
		
		@Override
		public void receiveSourceNotification(int type, Object object) {
			// don't do anything by default
		}


	}
	
	/**
	 * Inner class to watch for changes to the Mean Summary Data Block
	 * 
	 * @author mo55
	 *
	 */
	public class MeanObs implements PamObserver {

		@Override
		public long getRequiredDataHistory(PamObservable o, Object arg) {
			return 0;
		}

		@Override
		public void removeObservable(PamObservable o) {
		}

		@Override
		public void setSampleRate(float sampleRate, boolean notify) {
		}

		@Override
		public void noteNewSettings() {
		}

		@Override
		public String getObserverName() {
			return "WAMS Mean Summary Data Observer";
		}

		@Override
		public void masterClockUpdate(long milliSeconds, long sampleNumber) {
		}

		@Override
		public PamObserver getObserverObject() {
			return this;
		}

		@Override
		public void addData(PamObservable observable, PamDataUnit pamDataUnit) {
			WAMSDivisionDataUnit data = (WAMSDivisionDataUnit) pamDataUnit;
			summaryChart.addMeanData(data.getDivEndTimes(), data.getCount());
		}

		@Override
		public void updateData(PamObservable observable, PamDataUnit pamDataUnit) {
		}
		
		@Override
		public void receiveSourceNotification(int type, Object object) {
			// don't do anything by default
		}


	}

	public WAMSSummaryTable getWamsSummaryTable() {
		return wamsSummaryTable;
	}
}
