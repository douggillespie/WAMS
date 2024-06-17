package wamsPlugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import PamController.PamController;
import PamController.PamViewParameters;
import PamDetection.LocContents;
import PamDetection.PamDetection;
import PamUtils.FileParts;
import PamUtils.PamCalendar;
import PamView.dialog.PamDialog;
import PamView.dialog.SourcePanel;
import PamguardMVC.PamDataBlock;
import alarm.AlarmDataUnit;
import generalDatabase.DBControlUnit;
import generalDatabase.PamConnection;
import generalDatabase.SQLTypes;
import javafx.util.StringConverter;
import reportWriter.Report;
import reportWriter.ReportChart;
import reportWriter.ReportFactory;
import reportWriter.ReportSection;

public class WAMSParamsDialog extends PamDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This dialog, as a singleton
	 */
	static private WAMSParamsDialog singleInstance;
	
	/**
	 * local copy of parameters
	 */
	private WAMSParameters wamsParams;
	
	private WAMSProcess wamsProcess;
	
	private SourcePanel detectorSourcePanel;

	private JTabbedPane tabbedPane;
	
	private JTextField startAngle;
	
	private JTextField endAngle;

	private JTextField timeInt;

	private JCheckBox angleVetoEnable;

	private JTextField reportStartDate;

	private JTextField reportEndDate;
	
	private JPanel alarmListPanel;

	private ArrayList<JCheckBox> alarmList;
	
	private JTextField outputDirTxt;
	
	private JButton outputDirectoryButton;

	private JTextField templateTxt;

	private JButton templateButton;

	private JRadioButton rdbtnPamTime;

	private JRadioButton rdbtnLocalPC;

	private JButton reportButton;

//	private JTextField numDivTxt;
	
	private JTextField txtCSVOutputFolder;

	private JButton btnCSVFolder;

	private JTextField startEndWithin;

	private JTextField minGap;

	private JCheckBox chkBoxUseHarmDet;
	
	// the factors of 24*60=1440 minutes in a day, to figure out how many intervals to use
	private int[] dailyIntervals = {2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 18, 20, 24, 30, 32, 36,
			40, 45, 48, 60, 72, 80, 90, 96, 120, 144, 160, 180, 240, 288, 360, 480, 720};

	/**
	 * Private constructor
	 * @param parentFrame
	 */
	private WAMSParamsDialog(Window parentFrame) {
		super(parentFrame, "WAMS Parameters", true);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
		// **********************************************************************
		// Source Data tab
		// **********************************************************************
		JPanel sourceTab = new JPanel();
		tabbedPane.addTab("Source Data", null, sourceTab, null);
		GridBagLayout gbl_sourceTab = new GridBagLayout();
		sourceTab.setLayout(gbl_sourceTab);
		
//		GridBagConstraints c = new GridBagConstraints();
//		c.anchor = GridBagConstraints.PAGE_START;
//		c.insets = new Insets(10,2,0,2);
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.anchor = GridBagConstraints.BASELINE_LEADING;
//        c.gridwidth = 1;
//		c.gridx = 0;
//		c.gridy = 0;

		// Detector Source
		JPanel sourcePanel = new JPanel(new BorderLayout());
		sourcePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sourcePanel.setBorder(new TitledBorder(null, "Source Detector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_sourcePanel = new GridBagConstraints();
		gbc_sourcePanel.anchor = GridBagConstraints.NORTH;
		gbc_sourcePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourcePanel.insets = new Insets(10, 5, 5, 0);
		gbc_sourcePanel.gridx = 0;
		gbc_sourcePanel.gridy = 0;
		sourceTab.add(sourcePanel, gbc_sourcePanel);
		detectorSourcePanel = new SourcePanel(this, PamDetection.class, false, true);
		sourcePanel.add(BorderLayout.CENTER, detectorSourcePanel.getPanel());
		detectorSourcePanel.addSelectionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				timeInt.setEnabled(true);
				boolean hasBearing = detectorSourcePanel.getSource().getLocalisationContents().hasLocContent(LocContents.HAS_BEARING);
				angleVetoEnable.setSelected(hasBearing);
				angleVetoEnable.setEnabled(hasBearing);
				startAngle.setEnabled(hasBearing);
				endAngle.setEnabled(hasBearing);
			}
		});
		
		// Time Interval
		JPanel timePanel = new JPanel();
		timePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout flowLayout_1 = (FlowLayout) timePanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		timePanel.setBorder(new TitledBorder(null, "Time Interval", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_timePanel = new GridBagConstraints();
		gbc_timePanel.anchor = GridBagConstraints.NORTH;
		gbc_timePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_timePanel.insets = new Insets(10, 5, 5, 0);
		gbc_timePanel.gridx = 0;
		gbc_timePanel.gridy = 1;
		sourceTab.add(timePanel, gbc_timePanel);
		
		JLabel lblTimeInt = new JLabel("Time Interval");
		timePanel.add(lblTimeInt);
		
		timeInt = new JTextField();
		timeInt.setHorizontalAlignment(SwingConstants.LEFT);
		timePanel.add(timeInt);
		timeInt.setColumns(10);
		
		JLabel lblMinutes = new JLabel("minutes");
		timePanel.add(lblMinutes);
		
		// Angle Vetos
		JPanel anglePanel = new JPanel();
		anglePanel.setBorder(new TitledBorder(null, "Angle Veto", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_anglePanel = new GridBagConstraints();
		gbc_anglePanel.fill = GridBagConstraints.BOTH;
		gbc_anglePanel.insets = new Insets(10, 5, 5, 0);
		gbc_anglePanel.gridx = 0;
		gbc_anglePanel.gridy = 2;
		sourceTab.add(anglePanel, gbc_anglePanel);
		GridBagLayout gbl_anglePanel = new GridBagLayout();
		anglePanel.setLayout(gbl_anglePanel);
		String angleMess = "<html><div WIDTH=250>Ignore any detections with a bearing that falls within the range given below.  " +
				"Note that the range should be between -180\u00B0 and 180\u00B0, with 0\u00B0 in the direction of the " +
				"primary axis and increasing counter-clockwise.</div></html>";
		JLabel lblAngleNote = new JLabel(angleMess);
		GridBagConstraints gbc_lblAngleNote = new GridBagConstraints();
		gbc_lblAngleNote.fill = GridBagConstraints.BOTH;
		gbc_lblAngleNote.insets = new Insets(5, 5, 0, 0);
		gbc_lblAngleNote.gridx = 0;
		gbc_lblAngleNote.gridy = 0;
		anglePanel.add(lblAngleNote, gbc_lblAngleNote);

		
		JPanel useAngleSubPanel = new JPanel();
		FlowLayout fl_useAngleSubPanel = (FlowLayout) useAngleSubPanel.getLayout();
		fl_useAngleSubPanel.setAlignment(FlowLayout.LEFT);
		useAngleSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints gbc_useAngleSubPanel = new GridBagConstraints();
		gbc_useAngleSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_useAngleSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_useAngleSubPanel.insets = new Insets(5, 5, 0, 0);
		gbc_useAngleSubPanel.gridx = 0;
		gbc_useAngleSubPanel.gridy = 1;
		anglePanel.add(useAngleSubPanel, gbc_useAngleSubPanel);
		
		JLabel lblNewLabel = new JLabel("Use Angle Veto");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		useAngleSubPanel.add(lblNewLabel);
		
		angleVetoEnable = new JCheckBox("");
		useAngleSubPanel.add(angleVetoEnable);
		angleVetoEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startAngle.setEnabled(angleVetoEnable.isSelected());
				endAngle.setEnabled(angleVetoEnable.isSelected());
			}
		});
		JPanel startAngSubPanel = new JPanel();
		startAngSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout fl_startAngSubPanel = (FlowLayout) startAngSubPanel.getLayout();
		fl_startAngSubPanel.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_startAngSubPanel = new GridBagConstraints();
		gbc_startAngSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_startAngSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_startAngSubPanel.insets = new Insets(5, 5, 0, 0);
		gbc_startAngSubPanel.gridx = 0;
		gbc_startAngSubPanel.gridy = 2;
		anglePanel.add(startAngSubPanel, gbc_startAngSubPanel);
		
		JLabel lblStartAng = new JLabel("Start Angle");
		startAngSubPanel.add(lblStartAng);
		
		startAngle = new JTextField();
		startAngSubPanel.add(startAngle);
		startAngle.setColumns(10);
		
		JLabel lblDeg1 = new JLabel("\u00B0");
		startAngSubPanel.add(lblDeg1);
		
		JPanel endAngleSubPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) endAngleSubPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		endAngleSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints gbc_endAngleSubPanel = new GridBagConstraints();
		gbc_endAngleSubPanel.weighty = 1.0;
		gbc_endAngleSubPanel.insets = new Insets(5, 5, 0, 0);
		gbc_endAngleSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_endAngleSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_endAngleSubPanel.gridx = 0;
		gbc_endAngleSubPanel.gridy = 3;
		anglePanel.add(endAngleSubPanel, gbc_endAngleSubPanel);
		
		JLabel lblEndAngle = new JLabel("End Angle  ");
		lblEndAngle.setHorizontalAlignment(SwingConstants.LEFT);
		endAngleSubPanel.add(lblEndAngle);
		
		endAngle = new JTextField();
		endAngleSubPanel.add(endAngle);
		endAngle.setColumns(10);
		
		JLabel lblDeg2 = new JLabel("\u00B0");
		endAngleSubPanel.add(lblDeg2);
		
		// harmonic detector
		JPanel harmonicsPanel = new JPanel();
		harmonicsPanel.setBorder(new TitledBorder(null, "Harmonics Detector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_harmonicsPanel = new GridBagConstraints();
		gbc_harmonicsPanel.insets = new Insets(10, 5, 5, 0);
		gbc_harmonicsPanel.fill = GridBagConstraints.BOTH;
		gbc_harmonicsPanel.gridx = 0;
		gbc_harmonicsPanel.gridy = 3;
		sourceTab.add(harmonicsPanel, gbc_harmonicsPanel);
		GridBagLayout gbl_harmonicsPanel = new GridBagLayout();
		harmonicsPanel.setLayout(gbl_harmonicsPanel);
		
		JLabel lblHarmDet = new JLabel("<html><div WIDTH=250>Attempt to recognize harmonics and only count them as a single detection.</div></html>");
		GridBagConstraints gbc_lblHarmDet = new GridBagConstraints();
		gbc_lblHarmDet.fill = GridBagConstraints.BOTH;
		gbc_lblHarmDet.insets = new Insets(5, 5, 5, 0);
		gbc_lblHarmDet.gridx = 0;
		gbc_lblHarmDet.gridy = 0;
		harmonicsPanel.add(lblHarmDet, gbc_lblHarmDet);
		
		JPanel useHarmDetSubPanel = new JPanel();
		FlowLayout fl_useHarmDetSubPanel = (FlowLayout) useHarmDetSubPanel.getLayout();
		fl_useHarmDetSubPanel.setAlignment(FlowLayout.LEFT);
		useHarmDetSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints gbc_useHarmDetSubPanel = new GridBagConstraints();
		gbc_useHarmDetSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_useHarmDetSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_useHarmDetSubPanel.insets = new Insets(5, 5, 5, 0);
		gbc_useHarmDetSubPanel.gridx = 0;
		gbc_useHarmDetSubPanel.gridy = 1;
		harmonicsPanel.add(useHarmDetSubPanel, gbc_useHarmDetSubPanel);
		
		JLabel lblUseHarmonicsDetector = new JLabel("Use Harmonics Detector");
		lblUseHarmonicsDetector.setHorizontalAlignment(SwingConstants.LEFT);
		useHarmDetSubPanel.add(lblUseHarmonicsDetector);
		
		chkBoxUseHarmDet = new JCheckBox("");
		useHarmDetSubPanel.add(chkBoxUseHarmDet);
		chkBoxUseHarmDet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startEndWithin.setEnabled(chkBoxUseHarmDet.isSelected());
				minGap.setEnabled(chkBoxUseHarmDet.isSelected());
			}
		});
		
		JPanel startEndSubPanel = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) startEndSubPanel.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_startEndSubPanel = new GridBagConstraints();
		gbc_startEndSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_startEndSubPanel.insets = new Insets(5, 5, 0, 0);
		gbc_startEndSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_startEndSubPanel.gridx = 0;
		gbc_startEndSubPanel.gridy = 2;
		harmonicsPanel.add(startEndSubPanel, gbc_startEndSubPanel);
		
		JLabel lblStartEnd = new JLabel("Start/End within");
		startEndSubPanel.add(lblStartEnd);
		
		startEndWithin = new JTextField();
		startEndSubPanel.add(startEndWithin);
		startEndWithin.setColumns(7);
		
		JLabel lblMs = new JLabel("ms of each other");
		startEndSubPanel.add(lblMs);
		
		JPanel minGapSubPanel = new JPanel();
		FlowLayout flowLayout2 = (FlowLayout) minGapSubPanel.getLayout();
		flowLayout2.setAlignment(FlowLayout.LEFT);
		minGapSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints gbc_minGapSubPanel = new GridBagConstraints();
		gbc_minGapSubPanel.insets = new Insets(5, 5, 0, 0);
		gbc_minGapSubPanel.anchor = GridBagConstraints.NORTH;
		gbc_minGapSubPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_minGapSubPanel.gridx = 0;
		gbc_minGapSubPanel.gridy = 3;
		harmonicsPanel.add(minGapSubPanel, gbc_minGapSubPanel);
		
		JLabel lblMinGap = new JLabel("Min Gap of");
		lblEndAngle.setHorizontalAlignment(SwingConstants.LEFT);
		minGapSubPanel.add(lblMinGap);
		
		minGap = new JTextField();
		minGap.setColumns(7);
		minGapSubPanel.add(minGap);
		
		JLabel lblMinGap2 = new JLabel("ms between detections");
		minGapSubPanel.add(lblMinGap2);
		
		// dummy space at the bottom
		JPanel dummyPanel = new JPanel();
		GridBagConstraints gbc_dummyPanel = new GridBagConstraints();
		gbc_dummyPanel.weighty = 1.0;
		gbc_dummyPanel.fill = GridBagConstraints.BOTH;
		gbc_dummyPanel.gridx = 0;
		gbc_dummyPanel.gridy = 4;
		sourceTab.add(dummyPanel, gbc_dummyPanel);
		
		
		// **********************************************************************
		// Alarms Tab
		// 2020/08/13 Tab Hidden.  If the user was monitoring an Alarm within the
		// WAMS plugin while at the same time using the Alarm Module to monitor one
		// of the WAMS output data blocks (i.e. they were observers of each other),
		// PAMGuard would get into an infinite loop when setting parameters and
		// throw a StackOverflow exception.  Error occurred during setSampleRate
		// method and masterClockUpdate method.
		// **********************************************************************
		JPanel alarmTab = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) alarmTab.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
//		tabbedPane.addTab("Alarms", null, alarmTab, null);
		
		JPanel alarmPanel = new JPanel();
		alarmTab.add(alarmPanel);
		GridBagLayout gbl_alarmPanel = new GridBagLayout();
		gbl_alarmPanel.columnWeights = new double[]{0.0};
		gbl_alarmPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		alarmPanel.setLayout(gbl_alarmPanel);
		
		JLabel lblNewLabel_1 = new JLabel("Select which Alarms to monitor");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.weightx = 1.0;
		gbc_lblNewLabel_1.insets = new Insets(10, 5, 5, 0);
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		alarmPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		alarmListPanel = new JPanel();
		GridBagConstraints gbc_alarmBox1 = new GridBagConstraints();
		gbc_alarmBox1.insets = new Insets(10, 10, 0, 0);
		gbc_alarmBox1.gridx = 0;
		gbc_alarmBox1.gridy = 1;
		addComponent(alarmPanel, alarmListPanel, gbc_alarmBox1);
		
		
		// **********************************************************************
		// Summary Tab
		// **********************************************************************
		JPanel summaryTab = new JPanel();
		tabbedPane.addTab("Output", null, summaryTab, null);
		GridBagLayout gbl_summaryTab = new GridBagLayout();
		gbl_summaryTab.columnWeights = new double[]{1.0};
		gbl_summaryTab.rowWeights = new double[]{0.0, 0.0, 1.0};
		summaryTab.setLayout(gbl_summaryTab);
		
//		JPanel summaryPanel = new JPanel();
//		summaryPanel.setBorder(new TitledBorder(null, "Time Divisions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		GridBagConstraints gbc_summaryPanel = new GridBagConstraints();
//		gbc_summaryPanel.fill = GridBagConstraints.HORIZONTAL;
//		gbc_summaryPanel.anchor = GridBagConstraints.NORTH;
//		gbc_summaryPanel.insets = new Insets(10, 5, 5, 0);
//		gbc_summaryPanel.gridx = 0;
//		gbc_summaryPanel.gridy = 0;
//		summaryTab.add(summaryPanel, gbc_summaryPanel);
//		GridBagLayout gbl_summaryPanel = new GridBagLayout();
//		gbl_summaryPanel.columnWidths = new int[]{0, 0, 0};
//		gbl_summaryPanel.rowHeights = new int[]{0, 0, 0};
//		gbl_summaryPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
//		gbl_summaryPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//		summaryPanel.setLayout(gbl_summaryPanel);
//		
//		JLabel lblSummaryNote = new JLabel("<html><div WIDTH=250>Enter the number of time divisions to split each day into." +
//				"  A column for each division will be created in the database Summary table, and will be populated with the number of detections in that period." +
//				"  The first division (Div0) will start at the beginning of the current day (00:00:00) and the last division will end at the end of the current day" +
//				" (23:59:59).<br>\r\nTo specify an hourly summary, for example, enter 24 in the box below.<br>\r\nNOTE: in order to remain consistent with the" +
//				" time keeping throughout PAMGuard, all times are based on a UTC time zone.</div></html>");
//		GridBagConstraints gbc_lblSummaryNote = new GridBagConstraints();
//		gbc_lblSummaryNote.insets = new Insets(0, 0, 5, 0);
//		gbc_lblSummaryNote.gridwidth = 2;
//		gbc_lblSummaryNote.gridx = 0;
//		gbc_lblSummaryNote.gridy = 0;
//		summaryPanel.add(lblSummaryNote, gbc_lblSummaryNote);
//		
//		JLabel lblTimeDiv = new JLabel("Time Divisions: ");
//		GridBagConstraints gbc_lblTimeDiv = new GridBagConstraints();
//		gbc_lblTimeDiv.insets = new Insets(10, 0, 0, 5);
//		gbc_lblTimeDiv.anchor = GridBagConstraints.EAST;
//		gbc_lblTimeDiv.gridx = 0;
//		gbc_lblTimeDiv.gridy = 1;
//		summaryPanel.add(lblTimeDiv, gbc_lblTimeDiv);
//		
//		numDivTxt = new JTextField();
//		GridBagConstraints gbc_txtNumDiv = new GridBagConstraints();
//		gbc_txtNumDiv.fill = GridBagConstraints.HORIZONTAL;
//		gbc_txtNumDiv.gridx = 1;
//		gbc_txtNumDiv.gridy = 1;
//		summaryPanel.add(numDivTxt, gbc_txtNumDiv);
//		numDivTxt.setColumns(10);
		
		JPanel csvOutput = new JPanel();
		csvOutput.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "CSV Output Folder", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_csvOutput = new GridBagConstraints();
		gbc_csvOutput.anchor = GridBagConstraints.NORTH;
		gbc_csvOutput.insets = new Insets(10, 5, 5, 0);
		gbc_csvOutput.fill = GridBagConstraints.BOTH;
		gbc_csvOutput.gridx = 0;
		gbc_csvOutput.gridy = 1;
		summaryTab.add(csvOutput, gbc_csvOutput);
		GridBagLayout gbl_csvOutput = new GridBagLayout();
		gbl_csvOutput.columnWeights = new double[]{0};
		gbl_csvOutput.rowWeights = new double[]{0.0, 0.0, 0.0};
		gbl_csvOutput.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_csvOutput.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		csvOutput.setLayout(gbl_csvOutput);
		
		txtCSVOutputFolder = new JTextField();
		txtCSVOutputFolder.setColumns(10);
		GridBagConstraints gbc_txtCSVOutputFolder = new GridBagConstraints();
		gbc_txtCSVOutputFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCSVOutputFolder.insets = new Insets(0, 0, 5, 0);
		gbc_txtCSVOutputFolder.gridx = 0;
		gbc_txtCSVOutputFolder.gridy = 1;
		csvOutput.add(txtCSVOutputFolder, gbc_txtCSVOutputFolder);
		
		btnCSVFolder = new JButton("Select Folder");
		GridBagConstraints gbc_btnCSVFolder = new GridBagConstraints();
		gbc_btnCSVFolder.anchor = GridBagConstraints.EAST;
		gbc_btnCSVFolder.insets = new Insets(0, 10, 5, 0);
		gbc_btnCSVFolder.gridx = 0;
		gbc_btnCSVFolder.gridy = 2;
		btnCSVFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
	            selectCSVFolder();
			}
		});
		csvOutput.add(btnCSVFolder, gbc_btnCSVFolder);
		
		JLabel lblCSVOutputNot = new JLabel("<html><div WIDTH=250>Historical mean values are stored in a csv file, to allow continuity when changing" +
				" databases.  The csv filename is WAMSMeanValues_divXX, where XX is replaced with the number of time divisions selected" + 
				" above.<br><br>\r\nSpecify below the folder to store the csv file in:<br></div></html>");
		GridBagConstraints gbc_lblCSVOutputNot = new GridBagConstraints();
		gbc_lblCSVOutputNot.insets = new Insets(0, 0, 5, 0);
		gbc_lblCSVOutputNot.gridx = 0;
		gbc_lblCSVOutputNot.gridy = 0;
		csvOutput.add(lblCSVOutputNot, gbc_lblCSVOutputNot);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		summaryTab.add(panel, gbc_panel);
		

		// **********************************************************************
		// Report Tab
		// **********************************************************************
		JPanel reportTab = new JPanel();
		tabbedPane.addTab("Reports", null, reportTab, null);
		GridBagLayout gbl_reportTab = new GridBagLayout();
		reportTab.setLayout(gbl_reportTab);
		
		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Output Folder and Template", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_outputPanel = new GridBagConstraints();
		gbc_outputPanel.anchor = GridBagConstraints.NORTH;
		gbc_outputPanel.insets = new Insets(10, 5, 5, 0);
		gbc_outputPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputPanel.gridx = 0;
		gbc_outputPanel.gridy = 0;
		reportTab.add(outputPanel, gbc_outputPanel);
		GridBagLayout gbl_outputPanel = new GridBagLayout();
		outputPanel.setLayout(gbl_outputPanel);
		
		JLabel lblOutputDir = new JLabel("Output Folder");
		GridBagConstraints gbc_lblOutputDir = new GridBagConstraints();
		gbc_lblOutputDir.anchor = GridBagConstraints.WEST;
		gbc_lblOutputDir.insets = new Insets(10, 5, 0, 5);
		gbc_lblOutputDir.gridx = 0;
		gbc_lblOutputDir.gridy = 0;
		outputPanel.add(lblOutputDir, gbc_lblOutputDir);
		
		outputDirTxt = new JTextField();
		GridBagConstraints gbc_outputDirTxt = new GridBagConstraints();
		gbc_outputDirTxt.gridwidth = 2;
		gbc_outputDirTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputDirTxt.insets = new Insets(0, 10, 0, 5);
		gbc_outputDirTxt.gridx = 0;
		gbc_outputDirTxt.gridy = 1;
		outputPanel.add(outputDirTxt, gbc_outputDirTxt);
		outputDirTxt.setColumns(10);
		
		outputDirectoryButton = new JButton("Select Folder");
		GridBagConstraints gbc_outputDirectoryButton = new GridBagConstraints();
		gbc_outputDirectoryButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputDirectoryButton.anchor = GridBagConstraints.WEST;
		gbc_outputDirectoryButton.insets = new Insets(0, 10, 0, 5);
		gbc_outputDirectoryButton.gridx = 0;
		gbc_outputDirectoryButton.gridy = 2;
        outputDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
	            selectOutputFolder();
			}
		});
		outputPanel.add(outputDirectoryButton, gbc_outputDirectoryButton);
		
		JLabel lblTemplate = new JLabel("Template File (optional)");
		GridBagConstraints gbc_lblTemplate = new GridBagConstraints();
		gbc_lblTemplate.anchor = GridBagConstraints.WEST;
		gbc_lblTemplate.insets = new Insets(15, 5, 0, 5);
		gbc_lblTemplate.gridx = 0;
		gbc_lblTemplate.gridy = 3;
		outputPanel.add(lblTemplate, gbc_lblTemplate);
		
		templateTxt = new JTextField();
		GridBagConstraints gbc_templateTxt = new GridBagConstraints();
		gbc_templateTxt.insets = new Insets(0, 10, 0, 5);
		gbc_templateTxt.gridwidth = 2;
		gbc_templateTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_templateTxt.gridx = 0;
		gbc_templateTxt.gridy = 4;
		outputPanel.add(templateTxt, gbc_templateTxt);
		templateTxt.setColumns(10);
		
		templateButton = new JButton("Select Report Template");
		GridBagConstraints gbc_templateButton = new GridBagConstraints();
		gbc_templateButton.insets = new Insets(0, 10, 10, 5);
		gbc_templateButton.gridx = 0;
		gbc_templateButton.gridy = 5;
		templateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
	            selectTemplate();
			}
		});
		outputPanel.add(templateButton, gbc_templateButton);
		
		JButton clearTemplateButton = new JButton("Clear Template");
		GridBagConstraints gbc_clearTemplateButton = new GridBagConstraints();
		gbc_clearTemplateButton.insets = new Insets(0, 0, 10, 5);
		gbc_clearTemplateButton.gridx = 1;
		gbc_clearTemplateButton.gridy = 5;
		clearTemplateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				templateTxt.setText(null);
			}
		});
		outputPanel.add(clearTemplateButton, gbc_clearTemplateButton);
		
		JPanel reportTimePanel = new JPanel();
		reportTimePanel.setBorder(new TitledBorder(null, "Reporting Time", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_reportTimePanel = new GridBagConstraints();
		gbc_reportTimePanel.insets = new Insets(10, 5, 5, 0);
		gbc_reportTimePanel.anchor = GridBagConstraints.NORTH;
		gbc_reportTimePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_reportTimePanel.gridx = 0;
		gbc_reportTimePanel.gridy = 1;
		reportTab.add(reportTimePanel, gbc_reportTimePanel);
		GridBagLayout gbl_reportTimePanel = new GridBagLayout();
		gbl_reportTimePanel.columnWidths = new int[]{0, 0, 0};
		gbl_reportTimePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_reportTimePanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_reportTimePanel.rowWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		reportTimePanel.setLayout(gbl_reportTimePanel);
		
		JPanel reportTimeBtnPanel = new JPanel();
		GridBagConstraints gbc_reportTimeBtnPanel = new GridBagConstraints();
		gbc_reportTimeBtnPanel.gridwidth = 2;
		gbc_reportTimeBtnPanel.insets = new Insets(0, 5, 5, 0);
		gbc_reportTimeBtnPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_reportTimeBtnPanel.gridx = 0;
		gbc_reportTimeBtnPanel.gridy = 0;
		reportTimePanel.add(reportTimeBtnPanel, gbc_reportTimeBtnPanel);
		reportTimeBtnPanel.setLayout(new GridLayout(0, 2, 5, 5));
		
		JButton lastHourBtn = new JButton("Last Hour");
		reportTimeBtnPanel.add(lastHourBtn);
		lastHourBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				long end = currentCal.getTimeInMillis();
				long start = end - 1 * 60 * 60 * 1000;
				populateReportTime(start,end);
			}
		});
		
		JButton last12Btn = new JButton("Last 12 Hours");
		reportTimeBtnPanel.add(last12Btn);
		last12Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				long end = currentCal.getTimeInMillis();
				long start = end - 12 * 60 * 60 * 1000;
				populateReportTime(start,end);
			}
		});
		
		JButton last24Btn = new JButton("24 Hours");
		reportTimeBtnPanel.add(last24Btn);
		last24Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				long end = currentCal.getTimeInMillis();
				long start = end - PamCalendar.millisPerDay;
				populateReportTime(start,end);
			}
		});
		
		JButton yesterdayBtn = new JButton("Yesterday");
		reportTimeBtnPanel.add(yesterdayBtn);
		yesterdayBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				currentCal.set(Calendar.HOUR, 0);
				currentCal.set(Calendar.HOUR_OF_DAY, 0);
				currentCal.set(Calendar.MINUTE, 0);
				currentCal.set(Calendar.SECOND, 0);
				currentCal.set(Calendar.MILLISECOND, 0);
				long end = currentCal.getTimeInMillis();
				long start = end - PamCalendar.millisPerDay;
				populateReportTime(start,end);
			}
		});
		
		JButton thisMonthBtn = new JButton("This Month");
		reportTimeBtnPanel.add(thisMonthBtn);
		thisMonthBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				currentCal.set(Calendar.HOUR, 0);
				currentCal.set(Calendar.HOUR_OF_DAY, 0);
				currentCal.set(Calendar.MINUTE, 0);
				currentCal.set(Calendar.SECOND, 0);
				currentCal.set(Calendar.MILLISECOND, 0);
				long end = currentCal.getTimeInMillis();
				int day = currentCal.getActualMinimum(Calendar.DAY_OF_MONTH);
				currentCal.set(Calendar.DAY_OF_MONTH, day);
				long start = currentCal.getTimeInMillis();
				populateReportTime(start,end);
			}
		});
		
		JButton lastMonthBtn = new JButton("Last Month");
		reportTimeBtnPanel.add(lastMonthBtn);
		lastMonthBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Calendar currentCal = getCorrectCalendar();
				currentCal.set(Calendar.HOUR, 0);
				currentCal.set(Calendar.HOUR_OF_DAY, 0);
				currentCal.set(Calendar.MINUTE, 0);
				currentCal.set(Calendar.SECOND, 0);
				currentCal.set(Calendar.MILLISECOND, 0);
				currentCal.add(Calendar.MONTH, -1); 
				int day = currentCal.getActualMinimum(Calendar.DAY_OF_MONTH);
				currentCal.set(Calendar.DAY_OF_MONTH, day);
				long start = currentCal.getTimeInMillis();
				day = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH);
				currentCal.set(Calendar.DAY_OF_MONTH, day);
				currentCal.add(Calendar.DAY_OF_MONTH, 1); 
				long end = currentCal.getTimeInMillis();
				populateReportTime(start,end);
			}
		});
		
		JLabel lblStart = new JLabel("Start");
		GridBagConstraints gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblStart.insets = new Insets(5, 10, 5, 5);
		gbc_lblStart.gridx = 0;
		gbc_lblStart.gridy = 1;
		reportTimePanel.add(lblStart, gbc_lblStart);
		
		reportStartDate = new JTextField();
		GridBagConstraints gbc_reportStartDate = new GridBagConstraints();
		gbc_reportStartDate.insets = new Insets(0, 0, 5, 0);
		gbc_reportStartDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_reportStartDate.gridx = 1;
		gbc_reportStartDate.gridy = 1;
		reportTimePanel.add(reportStartDate, gbc_reportStartDate);
		reportStartDate.setColumns(10);
		reportStartDate.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				enableReportButton();
			}
			
			private void enableReportButton() {
				if (reportStartDate.getText().isEmpty() || reportEndDate.getText().isEmpty()) {
					reportButton.setEnabled(false);
				} else {
					reportButton.setEnabled(true);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableReportButton();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				enableReportButton();
			}
		});
		
		JLabel lblEnd = new JLabel("End");
		GridBagConstraints gbc_lblEnd = new GridBagConstraints();
		gbc_lblEnd.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblEnd.insets = new Insets(5, 10, 0, 5);
		gbc_lblEnd.gridx = 0;
		gbc_lblEnd.gridy = 2;
		reportTimePanel.add(lblEnd, gbc_lblEnd);
		
		reportEndDate = new JTextField();
		GridBagConstraints gbc_reportEndDate = new GridBagConstraints();
		gbc_reportEndDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_reportEndDate.gridx = 1;
		gbc_reportEndDate.gridy = 2;
		reportTimePanel.add(reportEndDate, gbc_reportEndDate);
		reportEndDate.setColumns(10);
		reportEndDate.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				enableReportButton();
			}
			
			private void enableReportButton() {
				if (reportStartDate.getText().isEmpty() || reportEndDate.getText().isEmpty()) {
					reportButton.setEnabled(false);
				} else {
					reportButton.setEnabled(true);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableReportButton();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				enableReportButton();
			}
		});
		
		
		JPanel utcPanel = new JPanel();
		GridBagConstraints gbc_utcPanel = new GridBagConstraints();
		gbc_utcPanel.gridwidth = 2;
		gbc_utcPanel.insets = new Insets(0, 0, 0, 5);
		gbc_utcPanel.fill = GridBagConstraints.BOTH;
		gbc_utcPanel.gridx = 0;
		gbc_utcPanel.gridy = 3;
		reportTimePanel.add(utcPanel, gbc_utcPanel);
		
		JLabel lblTimesGivenIn = new JLabel("Times refer to");
		lblTimesGivenIn.setHorizontalAlignment(SwingConstants.CENTER);
		utcPanel.add(lblTimesGivenIn);
		ButtonGroup buttonGroup = new ButtonGroup();
		rdbtnPamTime = new JRadioButton("Pamguard Clock");
		buttonGroup.add(rdbtnPamTime);
		utcPanel.add(rdbtnPamTime);
		rdbtnPamTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reportStartDate.setText("");
				reportEndDate.setText("");
			}
		});
		rdbtnLocalPC = new JRadioButton("Local PC Time");
		buttonGroup.add(rdbtnLocalPC);
		utcPanel.add(rdbtnLocalPC);
		rdbtnLocalPC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reportStartDate.setText("");
				reportEndDate.setText("");
			}
		});
		
		reportButton = new JButton("Generate Report");
		reportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				generateReportButtonPress();
			}
		});
		reportButton.setEnabled(false);
		GridBagConstraints gbc_btnGenerateReport = new GridBagConstraints();
		gbc_btnGenerateReport.ipady = 10;
		gbc_btnGenerateReport.fill = GridBagConstraints.BOTH;
		gbc_btnGenerateReport.insets = new Insets(15, 10, 15, 10);
		gbc_btnGenerateReport.gridx = 0;
		gbc_btnGenerateReport.gridy = 2;
		reportTab.add(reportButton, gbc_btnGenerateReport);

		
        // set the dialog component focus
		setDialogComponent(tabbedPane);

	}
	
	protected Calendar getCorrectCalendar() {
		Calendar currentCal;
		if (rdbtnPamTime.isSelected()) {
			currentCal = PamCalendar.getCalendarDate();
		} else {
			currentCal = Calendar.getInstance();
			currentCal.setTimeInMillis(System.currentTimeMillis());
		}
		return currentCal;
	}

	protected void populateReportTime(long start, long end) {
		if (rdbtnPamTime.isSelected()) {
			reportStartDate.setText(PamCalendar.formatDateTime2(start));
			reportEndDate.setText(PamCalendar.formatDateTime2(end));
		}
		else {
			reportStartDate.setText(PamCalendar.formatLocalDateTime2(start));
			reportEndDate.setText(PamCalendar.formatLocalDateTime2(end));
		}
	}

	
    protected void selectOutputFolder() {
        String currDir = outputDirTxt.getText();
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select output directory...");
        fileChooser.setFileHidingEnabled(true);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (currDir != null) fileChooser.setSelectedFile(new File(currDir));
		int state = fileChooser.showOpenDialog(outputDirTxt);
		if (state == JFileChooser.APPROVE_OPTION) {
			currDir = fileChooser.getSelectedFile().getAbsolutePath();
		}
        outputDirTxt.setText(currDir);
    }
    
    protected void selectCSVFolder() {
        String currDir = txtCSVOutputFolder.getText();
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select output directory...");
        fileChooser.setFileHidingEnabled(true);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (currDir != null) fileChooser.setSelectedFile(new File(currDir));
		int state = fileChooser.showOpenDialog(txtCSVOutputFolder);
		if (state == JFileChooser.APPROVE_OPTION) {
			currDir = fileChooser.getSelectedFile().getAbsolutePath();
		}
		txtCSVOutputFolder.setText(currDir);
    }
    
	
    protected void selectTemplate() {
        String currFile = templateTxt.getText();
		JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select template file for Reports...");
        fileChooser.setFileHidingEnabled(true);
        fileChooser.setApproveButtonText("Select");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (currFile != null) fileChooser.setSelectedFile(new File(currFile));
		int state = fileChooser.showOpenDialog(outputDirTxt);
		if (state == JFileChooser.APPROVE_OPTION) {
			currFile = fileChooser.getSelectedFile().getAbsolutePath();
		}
		templateTxt.setText(currFile);
    }
	
    protected void generateReportButtonPress() {
		PamConnection con = DBControlUnit.findConnection();
		if (con == null) {
			return;
		}
		
		// pop up a message dialog to let the user know that we're working on the report
		final ReportInfoDialog dialog = new ReportInfoDialog(this);
		
		// generate the report in a separate thread
		SwingWorker<Void,Void> worker = new SwingWorker<Void,Void>()
		{
		    @Override
		    protected Void doInBackground()
		    {
		        generateReport(dialog);
		        return null;
		    }
		 
		    @Override
		    protected void done()
		    {
		        dialog.dispose();
		    }
		};
		worker.execute();
		dialog.setVisible(true);
    }
    
    
    private void generateReport(ReportInfoDialog dialog) {
		
		// prepare the variables
		PamConnection con = DBControlUnit.findConnection();
		WAMSLogger wamsLogger = (WAMSLogger) wamsProcess.getWAMSDataBlock().getLogging();
		boolean textInUTC = false;
		if (rdbtnPamTime.isSelected()) {
			textInUTC = true;
		}
		long dataStart = PamCalendar.millisFromDateTimeString(reportStartDate.getText(),textInUTC);
		long dataEnd = PamCalendar.millisFromDateTimeString(reportEndDate.getText(),textInUTC);
		PamViewParameters pvp = new PamViewParameters(dataStart, dataEnd);
		pvp.useAnalysisTime = false;
		boolean detectionDataFound = false;
		boolean alarmDataFound = false;

		// create an sql statement for the detections, get the results and loop through the rows
		String sqlStr = String.format("SELECT * FROM %s %s AND %s != ''", 
				wamsLogger.getTableDefinition().getTableName(),
				pvp.getSelectClause(con.getSqlTypes()),
				wamsLogger.getDetectorHeader());
		
		// if the user is giving us a local PC time and not the Pamguard Clock time, we want to query the
		// PCLocalTime column instead of the UTC column
//		if (rdbtnLocalPC.isSelected()) {
//			String tempSql = sqlStr.replace("UTC", "PCLocalTime");
//			sqlStr = tempSql;
//		}
//		System.out.println(sqlStr);
		String detectorName = null;
		double[] endTimeList = new double[1];	// initialize to dummy size to prevent error later
		double[] countList = new double[1];	// initialize to dummy size to prevent error later
		int rowCount = 0;
		int totalCounts = 0;
		dialog.setBarValue(5);
		
		// Execute the statement and just count all the rows
		try {
			Statement stmt = con.getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			
			while(result.next()) {
				rowCount++;
			}
			
			if (rowCount>0) {
				detectionDataFound = true;
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error retrieving WAMS table detection data from database");
			e.printStackTrace();
		}
			
		// Execute the statement a second time in order to extract the data
		dialog.setBarValue(10);
		if (detectionDataFound) {
			try {
				Statement stmt = con.getConnection().createStatement();
				ResultSet result = stmt.executeQuery(sqlStr);

				// loop through the results and store the end time and corresponding counts
				int idx=0;
				endTimeList = new double[rowCount];
				countList = new double[rowCount];
				while (result.next()) {

					// if this is the first row, store the detector name
					if (idx==0) {
						detectorName = result.getString(wamsLogger.getDetectorHeader());
					}

					// store the values from the current database row 
					endTimeList[idx] = SQLTypes.millisFromTimeStamp(result.getTimestamp(wamsLogger.getEndHeader()));
					countList[idx] = result.getInt(wamsLogger.getCountHeader());
					totalCounts+=countList[idx];
					idx++;

				}
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Error retrieving WAMS table detection data from database");
				e.printStackTrace();
			}
		}
		dialog.setBarValue(25);
		
		// create an sql statement for the alarms, get the results and loop through the rows
		String tempSql = sqlStr.replace(wamsLogger.getDetectorHeader(), wamsLogger.getAlarmHeader());
		sqlStr = tempSql;

//		System.out.println(sqlStr);
		String[] alarmName = new String[1];
		double[] alarmTimeList = new double[1];	// initialize to dummy size to prevent error later
		double[] scoreList = new double[1];	// initialize to dummy size to prevent error later
		int numAlarms = 0;
		
		// Execute the statement and just count all the rows
		try {
			Statement stmt = con.getConnection().createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			
			while(result.next()) {
				numAlarms++;
			}
			
			if (numAlarms>0) {
				alarmDataFound=true;
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error retrieving WAMS table alarm data from database");
			e.printStackTrace();
		}
		dialog.setBarValue(30);
			
		// Execute the statement a second time in order to extract the data
		if (alarmDataFound) {
			try {
				Statement stmt = con.getConnection().createStatement();
				ResultSet result = stmt.executeQuery(sqlStr);

				// loop through the results and store the end time and corresponding counts
				int idx=0;
				alarmName = new String[rowCount];
				alarmTimeList = new double[rowCount];
				scoreList = new double[rowCount];
				while (result.next()) {

					// store the values from the current database row 
					alarmName[idx] = result.getString(wamsLogger.getAlarmHeader());
					alarmTimeList[idx] = SQLTypes.millisFromTimeStamp(result.getTimestamp(wamsLogger.getStartHeader()));
					scoreList[idx] = result.getInt(wamsLogger.getCountHeader());
					idx++;
				}
				stmt.close();
			} catch (SQLException e) {
				System.out.println("Error retrieving WAMS table alarm data from database");
				e.printStackTrace();
			}
		}
		dialog.setBarValue(40);
		
		// otherwise, format the data and send it to the report generator
		Report myReport = ReportFactory.createReport("WAMS Activity Report");
		
		// Detector Section
		ReportSection aSection = new ReportSection("Detector Summary");
		aSection.addSectionText(String.format("Detector Name: %s",detectorName));
		aSection.addSectionText(String.format("Summary From: %s", reportStartDate.getText()));
		aSection.addSectionText(String.format("Summary To: %s", reportEndDate.getText()));
		aSection.addSectionText(String.format("Total Number of Detections: %d", totalCounts));
		if (!detectionDataFound) {
			aSection.addSectionText("    No detection data was recorded during this period");
		}
		myReport.addSection(aSection);

		if (detectionDataFound) {
			aSection = new ReportSection();
			ReportChart countsChart = new ReportChart(String.format("%s Activity - %s to %s", detectorName, reportStartDate.getText(), reportEndDate.getText()));
			countsChart.addSeries(detectorName, endTimeList, countList);
			countsChart.getXAxisObject().setTickLabelFormatter(new StringConverter<Number>() {
				@Override
				public String toString(Number object) {
					String dateTime;
					if (rdbtnPamTime.isSelected()) {
						dateTime = PamCalendar.formatDateTime2(object.longValue());
					} else {
						dateTime = PamCalendar.formatLocalDateTime2(object.longValue());
					}
					return dateTime;
				}
				@Override
				public Number fromString(String string) {
					long dateTime;
					if (rdbtnPamTime.isSelected()) {
						dateTime = PamCalendar.millisFromDateTimeString(string, true);
					} else {
						dateTime = PamCalendar.millisFromDateTimeString(string, false);
					}
					return dateTime;
				}
			});
			countsChart.getXAxisObject().setAutoRanging(false);
			countsChart.getXAxisObject().setLowerBound(dataStart);
			countsChart.getXAxisObject().setUpperBound(dataEnd);
			double interval = (dataEnd-dataStart)/5;
			countsChart.getXAxisObject().setTickUnit(interval);
			BufferedImage chartImage = countsChart.getImage();
			aSection.setImage(chartImage);
			myReport.addSection(aSection);
		}
		dialog.setBarValue(50);
		
		// Alarm Section
		aSection = new ReportSection("Alarm Summary");
		aSection.addSectionText(String.format("Summary From: %s", reportStartDate.getText()));
		aSection.addSectionText(String.format("Summary To: %s", reportEndDate.getText()));
		aSection.addSectionText(String.format("Total Number of Alarms: %d", numAlarms));
		if (!alarmDataFound) {
			aSection.addSectionText("    No alarms were recorded during this period");
		}
		myReport.addSection(aSection);
		
		if (alarmDataFound) {
			aSection = new ReportSection();
			for (int i=0; i<numAlarms; i++) {
				String correctedTime;
				if (rdbtnLocalPC.isSelected()) {
					correctedTime = PamCalendar.formatLocalDateTime2((long) alarmTimeList[i]);
				} else {
					correctedTime = PamCalendar.formatDateTime2((long) alarmTimeList[i]);
				}
				
				aSection.addSectionText(String.format("    Alarm: %s, Time: %s, Score: %d",
						alarmName[i].trim(),
						correctedTime,
						(int) scoreList[i]));
			}
			myReport.addSection(aSection);
		}
		dialog.setBarValue(60);
		
		// Generate report
		String startOfReport = PamCalendar.formatFileDateTime(dataStart,false);
		String endOfReport = PamCalendar.formatFileDateTime(dataEnd,false);
		if (rdbtnLocalPC.isSelected()) {
			startOfReport = PamCalendar.formatLocalFileDateTime(dataStart);
			endOfReport = PamCalendar.formatLocalFileDateTime(dataEnd);
		}
		String filename = outputDirTxt.getText() +
				FileParts.getFileSeparator() + 
				"WAMSReport_" + 
				startOfReport +
				"-" +
				endOfReport +
				".docx";
		ReportFactory.convertReportToDocx(myReport, filename, templateTxt.getText());
//		String filename = "C:\\Users\\mo55\\Documents\\Work\\NMMF Project\\myFirstGeneratedReport_docx4jDefaults.docx";
//		ReportFactory.convertReportToDocx(myReport, "D:\\Work\\QAM Module\\myFirstGeneratedReport_templateDefaults.docx", "D:\\Work\\QAM Module\\QAMTemplate.docx");
		dialog.setBarValue(90);

		ReportFactory.openReportInWordProcessor(filename);

	}
    
	/**
	 * Create the dialog, if it doesn't already exist, and display it
	 * 
	 * @param parentFrame The parent frame
	 * @param wamsProcess 
	 * @param wamsParams The parameters to start with
	 * 
	 * @return the same parameters that were passed in
	 */
	public static WAMSParameters showDialog(Frame parentFrame, 
            WAMSProcess wamsProcess, WAMSParameters wamsParams) {
		if (singleInstance == null || singleInstance.getParent() != parentFrame) {
			singleInstance = new WAMSParamsDialog(parentFrame);
		}
		singleInstance.wamsProcess = wamsProcess;
        singleInstance.wamsParams = wamsParams.clone();
		singleInstance.setParams();
		singleInstance.setVisible(true);
		return singleInstance.wamsParams;
	}

	/**
	 * Load the dialog with the parameters
	 */
	private void setParams() {
		detectorSourcePanel.setSource(wamsParams.getSourceDetector());
		angleVetoEnable.setSelected(wamsParams.isVetoAngles());
		startAngle.setText(String.valueOf(wamsParams.getAnglesToVeto()[0]));
		endAngle.setText(String.valueOf(wamsParams.getAnglesToVeto()[1]));
		startAngle.setEnabled(wamsParams.isVetoAngles());
		endAngle.setEnabled(wamsParams.isVetoAngles());
		timeInt.setText(String.valueOf(wamsParams.getTimeIntervalMinutes()));
		chkBoxUseHarmDet.setSelected(wamsParams.isUsingHarmDet());
		startEndWithin.setText(String.valueOf(wamsParams.getStartEndTime()));
		minGap.setText(String.valueOf(wamsParams.getMinGap()));
		startEndWithin.setEnabled(wamsParams.isUsingHarmDet());
		minGap.setEnabled(wamsParams.isUsingHarmDet());
//		numDivTxt.setText(String.valueOf(wamsParams.getNumDiv()));
		outputDirTxt.setText(wamsParams.getReportFolder());
		templateTxt.setText(wamsParams.getTemplateFile());
		txtCSVOutputFolder.setText(wamsParams.getCsvOutputFolder());
		
		generateAlarmList();
		if (!alarmList.isEmpty() && !wamsParams.getAlarmList().isEmpty()) {
			for (JCheckBox anExistingAlarm : alarmList) {
				if (wamsParams.getAlarmList().contains(anExistingAlarm.getText())) {
					anExistingAlarm.setSelected(true);
				} else {
					anExistingAlarm.setSelected(false);
				}
			}
			
		}
		
		// if there is no detector selected, disable all
		boolean hasBearing = false;
		if (detectorSourcePanel.getSource() == null) {
			timeInt.setEnabled(false);
		} 
		else {
			hasBearing = detectorSourcePanel.getSource().getLocalisationContents().hasLocContent(LocContents.HAS_BEARING);
		}
		if (!hasBearing) {
			angleVetoEnable.setSelected(false);
		}
		angleVetoEnable.setEnabled(hasBearing);
		startAngle.setEnabled(angleVetoEnable.isSelected());
		endAngle.setEnabled(angleVetoEnable.isSelected());
		rdbtnPamTime.setSelected(wamsParams.isUsingUTC());
		rdbtnLocalPC.setSelected(!wamsParams.isUsingUTC());
	}

	private void generateAlarmList() {
		alarmListPanel.removeAll();
		alarmListPanel.setLayout(new BoxLayout(alarmListPanel, BoxLayout.Y_AXIS));
		
    	// get list of all alarmDataBlocks
    	ArrayList<PamDataBlock> allAlarmDataBlocks = PamController.getInstance().getDataBlocks(AlarmDataUnit.class, false);
    	alarmList = new ArrayList<JCheckBox>();
    	if (allAlarmDataBlocks.isEmpty()) {
    		JLabel noAlarms = new JLabel("(No Alarm Modules have been created)");
//    		addComponent(alarmListPanel, noAlarms, c);
    		alarmListPanel.add(noAlarms);
    	}
    	else {
    		for (int i=0; i<allAlarmDataBlocks.size(); i++) {
    			alarmList.add(new JCheckBox(allAlarmDataBlocks.get(i).getDataName()));
    		}
    		for (JCheckBox anAlarm : alarmList) {
//    			addComponent(alarmListPanel, anAlarm, c);
//    			c.gridy++;
    			alarmListPanel.add(anAlarm);
    		}
    	}
	}

	/**
	 * Take the user selections from the dialog and load them
	 * into a WAMSParameters object to pass back
	 */
	@Override
	public boolean getParams() {
		wamsParams.setSourceDetector(detectorSourcePanel.getSourceName());
		wamsParams.setVetoAngles(angleVetoEnable.isSelected());
		int[] angleVeto = new int[2];
		angleVeto[0] = Integer.valueOf(startAngle.getText());
		angleVeto[1] = Integer.valueOf(endAngle.getText());
		wamsParams.setAnglesToVeto(angleVeto);
		wamsParams.setUsingHarmDet(chkBoxUseHarmDet.isSelected());
		wamsParams.setStartEndTime(Integer.valueOf(startEndWithin.getText()));
		wamsParams.setMinGap(Integer.valueOf(minGap.getText()));
		
		// figure out the best time interval to use, and set the two params
		int userInt = Integer.valueOf(timeInt.getText());
		int closestInt = dailyIntervals[nearestInList(userInt, dailyIntervals)];
		wamsParams.setTimeIntervalMinutes(closestInt);
		wamsParams.setNumDiv(24*60/closestInt);
		
		
		ArrayList<String> newAlarmList = new ArrayList<String>();
		for (JCheckBox alarm : alarmList) {
			if (alarm.isSelected()) {
				newAlarmList.add(alarm.getText());
			}
		}
		wamsParams.setAlaarmList(newAlarmList);
		wamsParams.setReportFolder(outputDirTxt.getText());
		wamsParams.setCsvOutputFolder(txtCSVOutputFolder.getText());
		if (templateTxt.getText()=="") {
			wamsParams.setTemplateFile(null);
		} else {
			wamsParams.setTemplateFile(templateTxt.getText());
		}
		wamsParams.setUsingUTC(rdbtnPamTime.isSelected());
		return true;
	}
	
	/**
	 * Find the number in a list that is closest to the desired value, and return the index position
	 * (copied from PamUtils version 2.01.03f, so that this plugin can be used with older versions of PAMGuard)
	 * 
	 * @param val the desired value
	 * @param list the list of numbers to test against
	 * @return the index position of the closest value (NOT the value itself)
	 */
	public int nearestInList(int val, int[] list) {
		int distance = Math.abs(list[0] - val);
		int idx = 0;
		for(int c = 1; c < list.length; c++){
		    int cdistance = Math.abs(list[c] - val);
		    if(cdistance < distance){
		        idx = c;
		        distance = cdistance;
		    }
		}
		return idx;
	}
	


	@Override
	public void cancelButtonPressed() {
		wamsParams = null;
	}

	@Override
	public void restoreDefaultSettings() {
		wamsParams = new WAMSParameters();
		setParams();
	}
	
	private class ReportInfoDialog extends PamDialog {
		
		JProgressBar bar;

		public ReportInfoDialog(Window parentFrame) {
			super(parentFrame, "Generating Report", false);
			getOkButton().setVisible(false);
			getCancelButton().setVisible(false);
			JPanel p = new JPanel();
//			p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
			p.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(15, 15, 0, 15);
			JLabel l = new JLabel("Please Wait - Generating Report");
			p.add(l,gbc);
			bar = new JProgressBar();
			bar.setIndeterminate(true);
			bar.setStringPainted(true);
			gbc.gridy++;
			gbc.insets = new Insets(5, 15, 15, 15);
			p.add(bar,gbc);

			setDialogComponent(p);
		}

		@Override
		public boolean getParams() {
			return false;
		}

		@Override
		public void cancelButtonPressed() {
		}

		@Override
		public void restoreDefaultSettings() {
		}
		
		public void setBarValue(int val) {
			bar.setValue(val);
		}
		
	}

}
