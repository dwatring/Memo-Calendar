import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;

	/**
	 * METHOD DOCUMENTATION AVAILABLE AT https://github.com/dwatring/Memo-Calendar/wiki
	 */

public class MemoCalendar extends CalendarData {
	int maxMemoSize = 255; //MOVE TO CONFIG
	JFrame mainFrame;
	JPanel calOpPanel;
	WebButton todayBut;
	JLabel todayLab;
	WebButton lYearBut;
	WebButton lMonBut;
	JLabel curMMYYYYLab;
	WebButton nMonBut;
	WebButton nYearBut;
	JPanel calPanel;
	WebButton[] weekDaysName;
	JPanel infoPanel;
	JLabel infoClock;
	JPanel memoPanel;	
	//JPanel calendarPanel;
	JLabel selectedDate;
	JTextArea memoArea;
	JScrollPane memoAreaSP;
	JPanel memoSubPanel;
	WebButton saveBut;
	WebButton delBut;
	WebButton clearBut;
	JPanel frameBottomPanel;
	TitledBorder memoBorder;
	JLabel bottomInfo = new JLabel("Welcome to Memo Calendar!");
	WebButton[][] dateButs = new WebButton[CALENDAR_HEIGHT][CALENDAR_WIDTH];
	MemoCalendar.ListenForCalOpButtons lForCalOpButtons = new MemoCalendar.ListenForCalOpButtons();
	MemoCalendar.listenForDateButs lForDateButs = new MemoCalendar.listenForDateButs();
	final String[] WEEK_DAY_NAME = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
	final String title = "Memo Calendar";
	final String ClrButMsg1 = "Text Area cleared";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Login login = new Login();
				login.setVisible(true);
			}
		});
	}

	public MemoCalendar() {
		System.out.println("Creating memo calendar");
		setCurrentDate();
		initializeGUI();
		System.out.println("TEST8");
		focusToday();
		System.out.println("TEST9");
		readMemo();
		System.out.println("TEST10");
		MemoCalendar.ThreadConrol threadCnl = new MemoCalendar.ThreadConrol();
		threadCnl.start();
	}
	
	public void initializeGUI(){
		initializeMainFrame();
		setCalenderOperationButtons();
		setCalendarPanel();
		setWeekdayButtons();
		setDateButtons();
		showCal();
		setInfoPanel();
		setMemoPanel(this);
		setSubPanels();
	}
	
	private void showCal() {
		String fontColor = "black";
		System.out.println("Displaying the calendar");
		WebLabel todayMark = new WebLabel("<html><font color=green>*</html>");
		int[] datesWithMemos = getDatesFromDB(this);
		int numDaysWithMemos = 0;
		while(datesWithMemos[numDaysWithMemos] != 0){
			numDaysWithMemos++;
		}
		int[] newDates = new int[numDaysWithMemos];
		for(int x=0;x<numDaysWithMemos;x++){
			newDates[x] = datesWithMemos[x];
		}
		for (int i = 0; i < CALENDAR_HEIGHT; i++) {
			for (int j = 0; j < CALENDAR_WIDTH; j++) {
				if (j == 0 || j == 6)
					fontColor = "#00BCD4";
				setDateButtonTextAndConditions(this, fontColor, newDates, i, j);
				if ((this.month == this.today.get(2)) && (this.year == this.today.get(1))
						&& (this.calDates[i][j] == this.today.get(5))) {
					this.dateButs[i][j].add(todayMark);
					this.dateButs[i][j].setToolTipText("Today");
				}
				if (this.calDates[i][j] == 0) {
					this.dateButs[i][j].setVisible(false);
				} else {
					this.dateButs[i][j].setVisible(true);
				}
			}
		}
	}
	
	private void initializeMainFrame(){
		mainFrame = new JFrame("Memo Calendar - Derek-watring.com");
		mainFrame.setDefaultCloseOperation(3);
		mainFrame.setSize(700, 400);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(false);
		try {
	    	WebLookAndFeel.install ();
		} catch (Exception e) {
			bottomInfo.setText("ERROR : LookAndFeel setting failed");
		}
	}
	
	private void setDateButtonTextAndConditions(MemoCalendar memo, String fontColor, int[] newDates, int i, int j){
		boolean contains = false;
		for(int x=0;x<newDates.length;x++){
			if(this.calDates[i][j] == newDates[x]){
				contains = true;
				break;
			}
			else contains = false;
		}
		if(contains == true) {
			memo.dateButs[i][j].setDrawBottom(true);
			
			memo.dateButs[i][j].setBackground(Login.accent);
			memo.dateButs[i][j].setText("<html><font color=" + fontColor + ">" + this.calDates[i][j] + "</font></html>");
		}
		else{
			memo.dateButs[i][j].setText("<html><font color=" + fontColor + ">" + this.calDates[i][j] + "</font></html>");
			memo.dateButs[i][j].setDrawBottom(false);
		}
	}
	
	private void focusToday() {
		if (this.today.get(7) == 1) {
			this.dateButs[this.today.get(4)][(this.today.get(7) - 1)].requestFocusInWindow();
		} else {
			this.dateButs[(this.today.get(4) - 1)][(this.today.get(7) - 1)].requestFocusInWindow();
		}
	}

	private void readMemo() {
		String memoAreaText = DBConnect.getContentData(MemoCalendar.this);
		this.memoArea.setText(memoAreaText);
	}
	
	private int[] getDatesFromDB(MemoCalendar calendar){
		return DBConnect.getDates(calendar);
	}
	
	private class ListenForCalOpButtons implements ActionListener {
		private ListenForCalOpButtons() {}
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == MemoCalendar.this.todayBut) {
				MemoCalendar.this.setCurrentDate();
				MemoCalendar.this.lForDateButs.actionPerformed(e);
				MemoCalendar.this.focusToday();
			} else if (e.getSource() == MemoCalendar.this.lYearBut) {
				MemoCalendar.this.moveMonth(-12);
			} else if (e.getSource() == MemoCalendar.this.lMonBut) {
				MemoCalendar.this.moveMonth(-1);
			} else if (e.getSource() == MemoCalendar.this.nMonBut) {
				MemoCalendar.this.moveMonth(1);
			} else if (e.getSource() == MemoCalendar.this.nYearBut) {
				MemoCalendar.this.moveMonth(12);
			}
			MemoCalendar.this.curMMYYYYLab.setText("<html><table width=100><tr><th><font size=5><font color=\"#FFFFFF\">"
					+ (MemoCalendar.this.month + 1 < 10 ? "&nbsp;" : "") + (MemoCalendar.this.month + 1) + " / "
					+ MemoCalendar.this.year + "</font></font></th></tr></table></html>");
			MemoCalendar.this.showCal();
		}
	}

	private class listenForDateButs implements ActionListener {
		private listenForDateButs() {}
		public void actionPerformed(ActionEvent e) {
			int k = 0;
			int l = 0;
			for (int i = 0; i < CALENDAR_HEIGHT; i++) {
				for (int j = 0; j < CALENDAR_WIDTH; j++) {
					if (e.getSource() == MemoCalendar.this.dateButs[i][j]) {
						k = i;
						l = j;
					}
				}
			}
			if ((k != 0) || (l != 0)) {
				MemoCalendar.this.day = MemoCalendar.this.calDates[k][l];
			}
			MemoCalendar.this.cal = new GregorianCalendar(MemoCalendar.this.year, MemoCalendar.this.month,
					MemoCalendar.this.day);

			String dDayString = new String();
			int dDay = (int) ((MemoCalendar.this.cal.getTimeInMillis() - MemoCalendar.this.today.getTimeInMillis())
					/ 1000L / 60L / 60L / 24L);
			if ((dDay == 0) && (MemoCalendar.this.cal.get(1) == MemoCalendar.this.today.get(1))
					&& (MemoCalendar.this.cal.get(2) == MemoCalendar.this.today.get(2))
					&& (MemoCalendar.this.cal.get(5) == MemoCalendar.this.today.get(5))) {
				dDayString = "Today";
			} else if (dDay >= 0) {
				dDayString = "Today+" + (dDay + 1);
			} else if (dDay < 0) {
				dDayString = "Today-" + dDay * -1;
			}
			MemoCalendar.this.selectedDate.setText(
					"<Html><font size=3><font color=\"#FFFFFF\">" + (MemoCalendar.this.month + 1) + "/" + MemoCalendar.this.day + "/"
							+ MemoCalendar.this.year + "&nbsp;(" + dDayString + ")</font></font></html>");

			MemoCalendar.this.readMemo();
		}
	}

	private class ThreadConrol extends Thread {
		private ThreadConrol() {}

		public void run() {
			boolean msgCntFlag = false;
			int num = 0;
			String curStr = new String();
			try {
				for (;;) {
					MemoCalendar.this.today = Calendar.getInstance();
					String amPm = MemoCalendar.this.today.get(9) == 0 ? "AM" : "PM";
					String hour;
					if (MemoCalendar.this.today.get(10) == 0) {
						hour = "12";
					} else {
						if (MemoCalendar.this.today.get(10) == 12) {
							hour = " 0";
						} else {
							hour = (MemoCalendar.this.today.get(10) < 10 ? " " : "") + MemoCalendar.this.today.get(10);
						}
					}
					String min = (MemoCalendar.this.today.get(12) < 10 ? "0" : "") + MemoCalendar.this.today.get(12);
					String sec = (MemoCalendar.this.today.get(13) < 10 ? "0" : "") + MemoCalendar.this.today.get(13);
					MemoCalendar.this.infoClock.setText(amPm + " " + hour + ":" + min + ":" + sec);
					sleep(1000L);
					String infoStr = MemoCalendar.this.bottomInfo.getText();
					if ((infoStr != " ") && ((!msgCntFlag) || (curStr != infoStr))) {
						num = 5;
						msgCntFlag = true;
						curStr = infoStr;
					} else if ((infoStr != " ") && (msgCntFlag)) {
						if (num > 0) {
							num--;
						} else {
							msgCntFlag = false;
							MemoCalendar.this.bottomInfo.setText(" ");
						}
					}
				}
			} catch (InterruptedException e) {
				System.out.println("Thread:Error");
			}
		}
	}
	
	public void setCalendarPanel(){
		calOpPanel.setLayout(new GridBagLayout());
		GridBagConstraints calOpGC = new GridBagConstraints();
		calOpGC.gridx = 1;
		calOpGC.gridy = 1;
		calOpGC.gridwidth = 2;
		calOpGC.gridheight = 1;
		calOpGC.weightx = 1.0D;
		calOpGC.weighty = 1.0D;
		calOpGC.insets = new Insets(5, 5, 0, 0);
		calOpGC.anchor = 17;
		calOpGC.fill = 0;
		calOpPanel.add(todayBut, calOpGC);
		calOpGC.gridwidth = 3;
		calOpGC.gridx = 2;
		calOpGC.gridy = 1;
		calOpPanel.add(todayLab, calOpGC);
		calOpGC.anchor = 10;
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 1;
		calOpGC.gridy = 2;
		calOpPanel.add(lYearBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 2;
		calOpGC.gridy = 2;
		calOpPanel.add(lMonBut, calOpGC);
		calOpGC.gridwidth = 2;
		calOpGC.gridx = 3;
		calOpGC.gridy = 2;
		calOpPanel.add(curMMYYYYLab, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 5;
		calOpGC.gridy = 2;
		calOpPanel.add(nMonBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 6;
		calOpGC.gridy = 2;
		calOpPanel.add(nYearBut, calOpGC);
		calPanel = new JPanel();
		calPanel.setBackground(Login.bg);
		calPanel.setLayout(new GridLayout(0, 7, 2, 2));
		calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	}
	
	public void setCalenderOperationButtons(){
		calOpPanel = new JPanel();
		calOpPanel.setBackground(Login.bg);
		todayBut = new WebButton("Today");
		todayBut.setToolTipText("Today");
		todayBut.addActionListener(lForCalOpButtons);
		todayLab = new JLabel(today.get(2) + 1 + "/" + today.get(5) + "/" + today.get(1));
		todayLab.setForeground(Color.WHITE);
		lYearBut = new WebButton("<<");
		lYearBut.setToolTipText("Previous Year");
		lYearBut.addActionListener(lForCalOpButtons);
		lMonBut = new WebButton("<");
		lMonBut.setToolTipText("Previous Month");
		lMonBut.addActionListener(lForCalOpButtons);
		curMMYYYYLab = new JLabel(
				"<html><table width=100><tr><th><font size=5><font color=\"#FFFFFF\">" + (month + 1 < 10 ? "&nbsp;" : "")
						+ (month + 1) + " / " + year + "</font></font></th></tr></table></html>");
		nMonBut = new WebButton(">");
		nMonBut.setToolTipText("Next Month");
		nMonBut.addActionListener(lForCalOpButtons);
		nYearBut = new WebButton(">>");
		nYearBut.setToolTipText("Next Year");
		nYearBut.addActionListener(lForCalOpButtons);
	}
	
	public void setWeekdayButtons(){
		weekDaysName = new WebButton[CALENDAR_WIDTH];
		for (int i = 0; i < CALENDAR_WIDTH; i++) {
			weekDaysName[i] = new WebButton(WEEK_DAY_NAME[i]);
			weekDaysName[i].setDrawLines(true, true, true, true);
			weekDaysName[i].setDrawSides(false, false, false, false);
			weekDaysName[i].setContentAreaFilled(false);
			weekDaysName[i].setForeground(Color.WHITE);
			if (i == 0 || i == 6) {
				weekDaysName[i].setBottomBgColor(Login.accent);
				weekDaysName[i].setTopBgColor(Login.accent);
			} else {
				weekDaysName[i].setBottomBgColor(new Color(150, 150, 150));
			}
			weekDaysName[i].setOpaque(true);
			weekDaysName[i].setFont(Login.loadFont("Oxygen-Regular.ttf", 12f));
			weekDaysName[i].setFocusPainted(false);
			weekDaysName[i].setDrawBottom(false);
			calPanel.add(weekDaysName[i]);
		}
	}
	
	public void setInfoPanel(){
		infoPanel = new JPanel();
		infoPanel.setBackground(Login.bg);
		infoPanel.setLayout(new BorderLayout());
		infoClock = new JLabel("", 4);
		infoClock.setBackground(Login.bg);
		infoClock.setForeground(Color.WHITE);
		infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		infoPanel.add(infoClock, "North");
		selectedDate = new JLabel("<Html><font size=3><font color=\"#FFFFFF\">" + (today.get(2) + 1) + "/" + today.get(5) + "/"
				+ today.get(1) + "&nbsp;(Today)</font></font></html>", 2);
		selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	}
	public void setMemoPanel(MemoCalendar memo){
		memoPanel = new JPanel();
		memoPanel.setBackground(Login.bg);
		memoPanel.setForeground(Color.WHITE);
		memoBorder = BorderFactory.createTitledBorder("Memo");
		memoBorder.setTitleColor(Color.WHITE);
		memoPanel.setBorder(memoBorder);
		memoArea = new JTextArea();
		memoArea.setLineWrap(true);
		memoArea.setWrapStyleWord(true);
		memoAreaSP = new JScrollPane(memoArea, 22, 31);
		memoSubPanel = new JPanel();
		memoSubPanel.setBackground(Login.bg);
		saveBut = new WebButton("Save");
		saveBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				String memoText = memoArea.getText();
				if (memoText.length() > 200){
					bottomInfo.setText("Cannot store more than "+maxMemoSize+" characters");
				}
				else{
					String str = memoArea.getText();
					DBConnect.setContentData(memo, str);
					bottomInfo.setText("Data saved to database");
				}
				showCal();
			}
		});
		clearBut = new WebButton("Clear");
		clearBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				memoArea.setText(null);
				bottomInfo.setText("Text area cleared");
			}
		});
		memoSubPanel.add(saveBut);
		memoSubPanel.add(clearBut);
		memoPanel.setLayout(new BorderLayout());
		memoPanel.add(selectedDate, "North");
		memoPanel.add(memoAreaSP, "Center");
		memoPanel.add(memoSubPanel, "South");
	}
	
	public void setSubPanels(){
		bottomInfo.setForeground(Color.WHITE);
		bottomInfo.setBackground(Login.bg);
		JPanel frameSubPanelWest = new JPanel();
		frameSubPanelWest.setBackground(Login.bg);
		Dimension calOpPanelSize = calOpPanel.getPreferredSize();
		calOpPanelSize.height = 90;
		calOpPanel.setPreferredSize(calOpPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(calOpPanel, "North");
		frameSubPanelWest.add(calPanel, "Center");

		JPanel frameSubPanelEast = new JPanel();
		frameSubPanelEast.setBackground(Login.bg);
		Dimension infoPanelSize = infoPanel.getPreferredSize();
		infoPanelSize.height = 65;
		infoPanel.setPreferredSize(infoPanelSize);
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(infoPanel, "North");
		frameSubPanelEast.add(memoPanel, "Center");

		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 410;
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);

		frameBottomPanel = new JPanel();
		frameBottomPanel.setBackground(Login.bg);
		frameBottomPanel.add(bottomInfo);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(frameSubPanelWest, "West");
		mainFrame.add(frameSubPanelEast, "Center");
		mainFrame.add(frameBottomPanel, "South");
		mainFrame.setVisible(true);
	}
	
	public void setDateButtons(){
		for (int i = 0; i < CALENDAR_HEIGHT; i++) {
			for (int j = 0; j < CALENDAR_WIDTH; j++) {
				this.dateButs[i][j] = new WebButton();
				this.dateButs[i][j].setAnimate(true);
				this.dateButs[i][j].setBorderPainted(false);
				this.dateButs[i][j].setContentAreaFilled(false);
				this.dateButs[i][j].setBackground(Color.WHITE);
				this.dateButs[i][j].setOpaque(true);
				this.dateButs[i][j].setDrawLines(true, true, true, true);
				this.dateButs[i][j].addActionListener(this.lForDateButs);
				this.dateButs[i][j].setDrawSides(false, false, false, false);
				this.dateButs[i][j].setDrawShade(false);
				this.dateButs[i][j].setFont(Login.loadFont("Oxygen-Regular.ttf", 12f)); //TODO move this function elsewhere
				this.calPanel.add(this.dateButs[i][j]);
			}
		}
	}
}
