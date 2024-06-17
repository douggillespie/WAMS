package wamsPlugin;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class WAMSParamsWindowBuilderTemplate extends JPanel {
	private JTextField startAngle;
	private JTextField endAngle;
	private JTextField timeInt;
	private JTextField outputDirTxt;
	private JTextField templateTxt;
	private JTextField reportStartDate;
	private JTextField reportEndDate;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField txtNumDiv;
	private JTextField txtCSVOutputFolder;
	private JTextField startEndWithin;
	private JTextField minGap;

	/**
	 * Create the panel.
	 */
	public WAMSParamsWindowBuilderTemplate() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel sourceTab = new JPanel();
		tabbedPane.addTab("Source Data", null, sourceTab, null);
		GridBagLayout gbl_sourceTab = new GridBagLayout();
		gbl_sourceTab.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_sourceTab.columnWeights = new double[]{1.0};
		sourceTab.setLayout(gbl_sourceTab);
		
		JPanel sourcePanel = new JPanel();
		sourcePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sourcePanel.setBorder(new TitledBorder(null, "Source Detector", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_sourcePanel = new GridBagConstraints();
		gbc_sourcePanel.anchor = GridBagConstraints.NORTH;
		gbc_sourcePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_sourcePanel.insets = new Insets(10, 5, 5, 0);
		gbc_sourcePanel.gridx = 0;
		gbc_sourcePanel.gridy = 0;
		sourceTab.add(sourcePanel, gbc_sourcePanel);
		sourcePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
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
		
		JPanel anglePanel = new JPanel();
		anglePanel.setBorder(new TitledBorder(null, "Angle Veto", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_anglePanel = new GridBagConstraints();
		gbc_anglePanel.anchor = GridBagConstraints.NORTH;
		gbc_anglePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_anglePanel.insets = new Insets(10, 5, 5, 0);
		gbc_anglePanel.gridx = 0;
		gbc_anglePanel.gridy = 2;
		sourceTab.add(anglePanel, gbc_anglePanel);
		GridBagLayout gbl_anglePanel = new GridBagLayout();
		anglePanel.setLayout(gbl_anglePanel);
		
		JLabel lblAngleNote = new JLabel("<html><div WIDTH=250>Ignore any detections with a bearing that falls within the range given below.  Note that the range should be between -180 deg and 180 deg, with 0 deg in the direction of the primary axis and increasing counter-clockwise.</div></html>");
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
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		useAngleSubPanel.add(chckbxNewCheckBox);
		
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
		
		JLabel lblDeg1 = new JLabel("degrees");
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
		
		JLabel lblDeg2 = new JLabel("degrees");
		endAngleSubPanel.add(lblDeg2);
		
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
		
		JCheckBox chkBoxUseHarmDet = new JCheckBox("");
		useHarmDetSubPanel.add(chkBoxUseHarmDet);
		
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
		
		JLabel lblMinGap = new JLabel("Min gap of");
		lblEndAngle.setHorizontalAlignment(SwingConstants.LEFT);
		minGapSubPanel.add(lblMinGap);
		
		minGap = new JTextField();
		minGap.setColumns(7);
		minGapSubPanel.add(minGap);
		
		JLabel lblMinGap2 = new JLabel("ms between detections");
		minGapSubPanel.add(lblMinGap2);
		
		JPanel dummyPanel = new JPanel();
		GridBagConstraints gbc_dummyPanel = new GridBagConstraints();
		gbc_dummyPanel.weighty = 1.0;
		gbc_dummyPanel.fill = GridBagConstraints.BOTH;
		gbc_dummyPanel.gridx = 0;
		gbc_dummyPanel.gridy = 4;
		sourceTab.add(dummyPanel, gbc_dummyPanel);
		
		JPanel alarmTab = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) alarmTab.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		tabbedPane.addTab("Alarms", null, alarmTab, null);
		
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
		
		JCheckBox alarmBox1 = new JCheckBox("New check box");
		GridBagConstraints gbc_alarmBox1 = new GridBagConstraints();
		gbc_alarmBox1.insets = new Insets(10, 10, 0, 0);
		gbc_alarmBox1.gridx = 0;
		gbc_alarmBox1.gridy = 1;
		alarmPanel.add(alarmBox1, gbc_alarmBox1);
		
		JCheckBox alarmBox3 = new JCheckBox("New check box");
		GridBagConstraints gbc_alarmBox3 = new GridBagConstraints();
		gbc_alarmBox3.insets = new Insets(0, 10, 0, 0);
		gbc_alarmBox3.gridx = 0;
		gbc_alarmBox3.gridy = 2;
		alarmPanel.add(alarmBox3, gbc_alarmBox3);
		
		JCheckBox alarmBox2 = new JCheckBox("New check box");
		GridBagConstraints gbc_alarmBox2 = new GridBagConstraints();
		gbc_alarmBox2.insets = new Insets(0, 10, 0, 0);
		gbc_alarmBox2.gridx = 0;
		gbc_alarmBox2.gridy = 3;
		alarmPanel.add(alarmBox2, gbc_alarmBox2);
		
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
		
		JButton clearTemplateButton = new JButton("Clear Template");
		GridBagConstraints gbc_clearTemplateButton = new GridBagConstraints();
		gbc_clearTemplateButton.insets = new Insets(0, 0, 0, 5);
		gbc_clearTemplateButton.gridx = 1;
		gbc_clearTemplateButton.gridy = 5;
		outputPanel.add(clearTemplateButton, gbc_clearTemplateButton);
		
		JButton templateButton = new JButton("Select Report Template");
		GridBagConstraints gbc_templateButton = new GridBagConstraints();
		gbc_templateButton.insets = new Insets(0, 10, 0, 5);
		gbc_templateButton.gridx = 0;
		gbc_templateButton.gridy = 5;
		outputPanel.add(templateButton, gbc_templateButton);
		
		templateTxt = new JTextField();
		GridBagConstraints gbc_templateTxt = new GridBagConstraints();
		gbc_templateTxt.insets = new Insets(0, 10, 0, 5);
		gbc_templateTxt.gridwidth = 2;
		gbc_templateTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_templateTxt.gridx = 0;
		gbc_templateTxt.gridy = 4;
		outputPanel.add(templateTxt, gbc_templateTxt);
		templateTxt.setColumns(10);
		
		JLabel lblTemplate = new JLabel("Template File (optional)");
		GridBagConstraints gbc_lblTemplate = new GridBagConstraints();
		gbc_lblTemplate.anchor = GridBagConstraints.WEST;
		gbc_lblTemplate.insets = new Insets(15, 5, 0, 5);
		gbc_lblTemplate.gridx = 0;
		gbc_lblTemplate.gridy = 3;
		outputPanel.add(lblTemplate, gbc_lblTemplate);
		
		JButton outputDirectoryButton = new JButton("Select Folder");
		GridBagConstraints gbc_outputDirectoryButton = new GridBagConstraints();
		gbc_outputDirectoryButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputDirectoryButton.anchor = GridBagConstraints.WEST;
		gbc_outputDirectoryButton.insets = new Insets(0, 10, 0, 5);
		gbc_outputDirectoryButton.gridx = 0;
		gbc_outputDirectoryButton.gridy = 2;
		outputPanel.add(outputDirectoryButton, gbc_outputDirectoryButton);
		
		outputDirTxt = new JTextField();
		GridBagConstraints gbc_outputDirTxt = new GridBagConstraints();
		gbc_outputDirTxt.gridwidth = 2;
		gbc_outputDirTxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_outputDirTxt.insets = new Insets(0, 10, 0, 5);
		gbc_outputDirTxt.gridx = 0;
		gbc_outputDirTxt.gridy = 1;
		outputPanel.add(outputDirTxt, gbc_outputDirTxt);
		outputDirTxt.setColumns(10);
		
		JLabel lblOutputDir = new JLabel("Output Folder");
		GridBagConstraints gbc_lblOutputDir = new GridBagConstraints();
		gbc_lblOutputDir.anchor = GridBagConstraints.WEST;
		gbc_lblOutputDir.insets = new Insets(10, 5, 0, 5);
		gbc_lblOutputDir.gridx = 0;
		gbc_lblOutputDir.gridy = 0;
		outputPanel.add(lblOutputDir, gbc_lblOutputDir);
		
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
		gbl_reportTimePanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_reportTimePanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_reportTimePanel.rowWeights = new double[]{1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
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
		
		JButton last12Btn = new JButton("Last 12 Hours");
		reportTimeBtnPanel.add(last12Btn);
		
		JButton last24Btn = new JButton("24 Hours");
		reportTimeBtnPanel.add(last24Btn);
		
		JButton yesterdayBtn = new JButton("Yesterday");
		reportTimeBtnPanel.add(yesterdayBtn);
		
		JButton thisMonthBtn = new JButton("This Month");
		reportTimeBtnPanel.add(thisMonthBtn);
		
		JButton lastMonthBtn = new JButton("Last Month");
		reportTimeBtnPanel.add(lastMonthBtn);
		
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
		
		JLabel lblEnd = new JLabel("End");
		GridBagConstraints gbc_lblEnd = new GridBagConstraints();
		gbc_lblEnd.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblEnd.insets = new Insets(5, 10, 5, 5);
		gbc_lblEnd.gridx = 0;
		gbc_lblEnd.gridy = 2;
		reportTimePanel.add(lblEnd, gbc_lblEnd);
		
		reportEndDate = new JTextField();
		GridBagConstraints gbc_reportEndDate = new GridBagConstraints();
		gbc_reportEndDate.insets = new Insets(0, 0, 5, 0);
		gbc_reportEndDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_reportEndDate.gridx = 1;
		gbc_reportEndDate.gridy = 2;
		reportTimePanel.add(reportEndDate, gbc_reportEndDate);
		reportEndDate.setColumns(10);
		
		JPanel utcPanel = new JPanel();
		GridBagConstraints gbc_utcPanel = new GridBagConstraints();
		gbc_utcPanel.gridwidth = 2;
		gbc_utcPanel.insets = new Insets(0, 0, 0, 5);
		gbc_utcPanel.fill = GridBagConstraints.BOTH;
		gbc_utcPanel.gridx = 0;
		gbc_utcPanel.gridy = 3;
		reportTimePanel.add(utcPanel, gbc_utcPanel);
		
		JLabel lblTimesGivenIn = new JLabel("Times Given in");
		lblTimesGivenIn.setHorizontalAlignment(SwingConstants.CENTER);
		utcPanel.add(lblTimesGivenIn);
		
		JRadioButton rdbtnLocalPC = new JRadioButton("Local PC Time");
		buttonGroup.add(rdbtnLocalPC);
		utcPanel.add(rdbtnLocalPC);
		
		JRadioButton rdbtnUtcTime = new JRadioButton("UTC Time");
		buttonGroup.add(rdbtnUtcTime);
		utcPanel.add(rdbtnUtcTime);
		
		JButton btnGenerateReport = new JButton("Generate Report");
		GridBagConstraints gbc_btnGenerateReport = new GridBagConstraints();
		gbc_btnGenerateReport.ipady = 10;
		gbc_btnGenerateReport.fill = GridBagConstraints.BOTH;
		gbc_btnGenerateReport.insets = new Insets(15, 10, 15, 10);
		gbc_btnGenerateReport.gridx = 0;
		gbc_btnGenerateReport.gridy = 2;
		reportTab.add(btnGenerateReport, gbc_btnGenerateReport);
		add(tabbedPane);
		
		JPanel summaryTab = new JPanel();
		tabbedPane.addTab("Summary and Mean", null, summaryTab, null);
		GridBagLayout gbl_summaryTab = new GridBagLayout();
		gbl_summaryTab.columnWeights = new double[]{1.0};
		gbl_summaryTab.rowWeights = new double[]{0.0, 0.0, 1.0};
		summaryTab.setLayout(gbl_summaryTab);
		
		JPanel summaryPanel = new JPanel();
		summaryPanel.setBorder(new TitledBorder(null, "Time Divisions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_summaryPanel = new GridBagConstraints();
		gbc_summaryPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_summaryPanel.anchor = GridBagConstraints.NORTH;
		gbc_summaryPanel.insets = new Insets(10, 5, 5, 0);
		gbc_summaryPanel.gridx = 0;
		gbc_summaryPanel.gridy = 0;
		summaryTab.add(summaryPanel, gbc_summaryPanel);
		GridBagLayout gbl_summaryPanel = new GridBagLayout();
		gbl_summaryPanel.columnWidths = new int[]{0, 0, 0};
		gbl_summaryPanel.rowHeights = new int[]{0, 0, 0};
		gbl_summaryPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_summaryPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		summaryPanel.setLayout(gbl_summaryPanel);
		
		JLabel lblSummaryNote = new JLabel("<html><div WIDTH=250>Enter the number of time divisions to split each day into." +
				"  A column for each division will be created in the database Summary table, and will be populated with the number of detections in each period." +
				"  The first division (Div0) will start at the beginning of the current day (00:00:00) and the last division will end at the end of the current day" +
				" (23:59:59).<br>\r\nTo specify an hourly summary, for example, enter <em>24</em> below.<br>\r\nNote that in order to remain consistent with the" +
				" time keeping throughout PAMGuard, all times will be based on a UTC time zone.<br><br></div></html>");
		GridBagConstraints gbc_lblSummaryNote = new GridBagConstraints();
		gbc_lblSummaryNote.insets = new Insets(0, 0, 5, 0);
		gbc_lblSummaryNote.gridwidth = 2;
		gbc_lblSummaryNote.gridx = 0;
		gbc_lblSummaryNote.gridy = 0;
		summaryPanel.add(lblSummaryNote, gbc_lblSummaryNote);
		
		JLabel lblTimeDiv = new JLabel("Time Divisions: ");
		GridBagConstraints gbc_lblTimeDiv = new GridBagConstraints();
		gbc_lblTimeDiv.insets = new Insets(0, 0, 0, 5);
		gbc_lblTimeDiv.anchor = GridBagConstraints.EAST;
		gbc_lblTimeDiv.gridx = 0;
		gbc_lblTimeDiv.gridy = 1;
		summaryPanel.add(lblTimeDiv, gbc_lblTimeDiv);
		
		txtNumDiv = new JTextField();
		GridBagConstraints gbc_txtNumDiv = new GridBagConstraints();
		gbc_txtNumDiv.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNumDiv.gridx = 1;
		gbc_txtNumDiv.gridy = 1;
		summaryPanel.add(txtNumDiv, gbc_txtNumDiv);
		txtNumDiv.setColumns(10);
		
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
		
		JButton btnCSVFolder = new JButton("Select Folder");
		GridBagConstraints gbc_btnCSVFolder = new GridBagConstraints();
		gbc_btnCSVFolder.anchor = GridBagConstraints.EAST;
		gbc_btnCSVFolder.insets = new Insets(0, 10, 5, 0);
		gbc_btnCSVFolder.gridx = 0;
		gbc_btnCSVFolder.gridy = 2;
		csvOutput.add(btnCSVFolder, gbc_btnCSVFolder);
		
		JLabel lblCSVOutputNot = new JLabel("<html><div WIDTH=250>Historical mean values are stored in a csv file, to allow continuity when changing databases.  The csv filename is blah_?div, where the ? character is replaced with the number of time divisions selected above.<br><br>\r\nSpecify below the folder to store the csv file in:<br></div></html>");
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
		
		// Layout of the Summary Display User Panel
		JPanel SummaryTableDisplay = new JPanel();
		tabbedPane.addTab("Summary Display", null, SummaryTableDisplay, null);
		GridBagLayout gbl_SummaryTableDisplay = new GridBagLayout();
		gbl_SummaryTableDisplay.columnWidths = new int[]{0, 0};
		gbl_SummaryTableDisplay.rowHeights = new int[]{0, 0, 0, 0};
		gbl_SummaryTableDisplay.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_SummaryTableDisplay.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		SummaryTableDisplay.setLayout(gbl_SummaryTableDisplay);
		
		JPanel summaryDisplayPanel2 = new JPanel();
		summaryDisplayPanel2.setBorder(new TitledBorder(null, "Summary of Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_summaryDisplayPanel2 = new GridBagConstraints();
		gbc_summaryDisplayPanel2.insets = new Insets(10, 5, 5, 5);
		gbc_summaryDisplayPanel2.fill = GridBagConstraints.BOTH;
		gbc_summaryDisplayPanel2.gridx = 0;
		gbc_summaryDisplayPanel2.gridy = 0;
		SummaryTableDisplay.add(summaryDisplayPanel2, gbc_summaryDisplayPanel2);
		GridBagLayout gbl_summaryDisplayPanel2 = new GridBagLayout();
		gbl_summaryDisplayPanel2.columnWidths = new int[]{0, 0};
		gbl_summaryDisplayPanel2.rowHeights = new int[]{0, 0};
		gbl_summaryDisplayPanel2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_summaryDisplayPanel2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		summaryDisplayPanel2.setLayout(gbl_summaryDisplayPanel2);
		
		JScrollPane summaryDisplayScroller = new JScrollPane();
		GridBagConstraints gbc_summaryDisplayScroller = new GridBagConstraints();
		gbc_summaryDisplayScroller.insets = new Insets(5, 5, 5, 5);
		gbc_summaryDisplayScroller.fill = GridBagConstraints.BOTH;
		gbc_summaryDisplayScroller.gridx = 0;
		gbc_summaryDisplayScroller.gridy = 0;
		summaryDisplayPanel2.add(summaryDisplayScroller, gbc_summaryDisplayScroller);
		
		JPanel meanDisplayPanel2 = new JPanel();
		meanDisplayPanel2.setBorder(new TitledBorder(null, "Mean Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_meanDisplayPanel2 = new GridBagConstraints();
		gbc_meanDisplayPanel2.insets = new Insets(5, 5, 5, 5);
		gbc_meanDisplayPanel2.fill = GridBagConstraints.BOTH;
		gbc_meanDisplayPanel2.gridx = 0;
		gbc_meanDisplayPanel2.gridy = 1;
		SummaryTableDisplay.add(meanDisplayPanel2, gbc_meanDisplayPanel2);
		GridBagLayout gbl_meanDisplayPanel2 = new GridBagLayout();
		gbl_meanDisplayPanel2.columnWidths = new int[]{0, 0};
		gbl_meanDisplayPanel2.rowHeights = new int[]{0, 0};
		gbl_meanDisplayPanel2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_meanDisplayPanel2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		meanDisplayPanel2.setLayout(gbl_meanDisplayPanel2);
		
		JPanel meanDisplayInnerPanel = new JPanel();
		GridBagConstraints gbc_meanDisplayInnerPanel = new GridBagConstraints();
		gbc_meanDisplayInnerPanel.insets = new Insets(5, 5, 5, 5);
		gbc_meanDisplayInnerPanel.anchor = GridBagConstraints.NORTH;
		gbc_meanDisplayInnerPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_meanDisplayInnerPanel.gridx = 0;
		gbc_meanDisplayInnerPanel.gridy = 0;
		meanDisplayPanel2.add(meanDisplayInnerPanel, gbc_meanDisplayInnerPanel);
		meanDisplayInnerPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel meanChartPanel2 = new JPanel();
		GridBagConstraints gbc_meanChartPanel2 = new GridBagConstraints();
		gbc_meanChartPanel2.insets = new Insets(5, 5, 10, 5);
		gbc_meanChartPanel2.fill = GridBagConstraints.BOTH;
		gbc_meanChartPanel2.gridx = 0;
		gbc_meanChartPanel2.gridy = 2;
		SummaryTableDisplay.add(meanChartPanel2, gbc_meanChartPanel2);
		meanChartPanel2.setLayout(new BorderLayout(0, 0));
		
		JPanel mainPanel = new JPanel();
		tabbedPane.addTab("New tab", null, mainPanel, null);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		JPanel tablePanel = new JPanel();
		mainPanel.add(tablePanel);
		tablePanel.setLayout(new BorderLayout(0, 10));
		
		JPanel summaryDisplayPanel = new JPanel();
		tablePanel.add(summaryDisplayPanel);
		summaryDisplayPanel.setBorder(new TitledBorder(null, "Summary of Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		summaryDisplayPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel innerSummaryPanel = new JPanel();
		innerSummaryPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		summaryDisplayPanel.add(innerSummaryPanel, BorderLayout.CENTER);
		innerSummaryPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel meanDisplayPanel = new JPanel();
		tablePanel.add(meanDisplayPanel, BorderLayout.SOUTH);
		meanDisplayPanel.setBorder(new TitledBorder(null, "Mean Detections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		meanDisplayPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel innerMeanPanel = new JPanel();
		innerMeanPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		meanDisplayPanel.add(innerMeanPanel, BorderLayout.CENTER);
		innerMeanPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel meanChartPanel = new JPanel();
		mainPanel.add(meanChartPanel);
		meanChartPanel.setLayout(new BorderLayout(0, 0));

	}
}
