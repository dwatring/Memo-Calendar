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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

	/**
	 * METHOD DOCUMENTATION AVAILABLE AT https://github.com/dwatring/Memo-Calendar/wiki
	 */

public class MemoCalendar extends CalendarData {
	int maxMemoSize = 255; //MOVE TO CONFIG
	JFrame mainFrame;
	JPanel calOpPanel;
	JButton todayBut;
	JLabel todayLab;
	JButton lYearBut;
	JButton lMonBut;
	JLabel curMMYYYYLab;
	JButton nMonBut;
	JButton nYearBut;
	JPanel calPanel;
	JButton[] weekDaysName;
	JPanel infoPanel;
	JLabel infoClock;
	JPanel memoPanel;
	JLabel selectedDate;
	JTextArea memoArea;
	JScrollPane memoAreaSP;
	JPanel memoSubPanel;
	JButton saveBut;
	JButton delBut;
	JButton clearBut;
	JPanel frameBottomPanel;
	JLabel bottomInfo = new JLabel("Welcome to Memo Calendar!");
	JButton[][] dateButs = new JButton[CALENDAR_HEIGHT][CALENDAR_WIDTH];
	MemoCalendar.ListenForCalOpButtons lForCalOpButtons = new MemoCalendar.ListenForCalOpButtons();
	MemoCalendar.listenForDateButs lForDateButs = new MemoCalendar.listenForDateButs();
	final String[] WEEK_DAY_NAME = { "SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT" };
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
		setCurrentDate();
		initializeGUI(this);
		focusToday();
		MemoCalendar.ThreadConrol threadCnl = new MemoCalendar.ThreadConrol();
		threadCnl.start();
		readMemo();
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
	
	private void showCal() {
		
		int[] memoDates = getDatesFromDB(this);
		int p = 0;
		while(memoDates[p] != 0){
			p++;
		}
		int[] newDates = new int[p];
		for(int x=0;x<p;x++){
			newDates[x] = memoDates[x];
		}
		JLabel todayMark = new JLabel("<html><font color=green>*</html>");
		for (int i = 0; i < CALENDAR_HEIGHT; i++) {
			for (int j = 0; j < CALENDAR_WIDTH; j++) {
				this.dateButs[i][j].removeAll();
				String fontColor = "black";
				if (j == 0)
					fontColor = "red";
					else if (j == 6)
					fontColor = "blue";
				boolean contains = false;
				for(int x=0;x<newDates.length;x++){
					if(this.calDates[i][j] == newDates[x]){
						contains = true;
						break;
					}
					else contains = false;
				}
				if(contains == true) {
					this.dateButs[i][j]
					.setText("<html><font color=" + fontColor + "><u>" + this.calDates[i][j] + "</u></font></html>");
				}
				else{
					this.dateButs[i][j]
					.setText("<html><font color=" + fontColor + ">" + this.calDates[i][j] + "</font></html>");
				}
				if ((this.month == this.today.get(2)) && (this.year == this.today.get(1))
						&& (this.calDates[i][j] == this.today.get(5))) {
					this.dateButs[i][j].add(todayMark);
					this.dateButs[i][j].setToolTipText("Today");
				}
				if (this.calDates[i][j] == 0)
					this.dateButs[i][j].setVisible(false);
				else this.dateButs[i][j].setVisible(true);
			}
		}
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
			MemoCalendar.this.curMMYYYYLab.setText("<html><table width=100><tr><th><font size=5>"
					+ (MemoCalendar.this.month + 1 < 10 ? "&nbsp;" : "") + (MemoCalendar.this.month + 1) + " / "
					+ MemoCalendar.this.year + "</th></tr></table></html>");
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
					"<Html><font size=3>" + (MemoCalendar.this.month + 1) + "/" + MemoCalendar.this.day + "/"
							+ MemoCalendar.this.year + "&nbsp;(" + dDayString + ")</html>");

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

	public static void initializeGUI(MemoCalendar memo){
		memo.mainFrame = new JFrame("Memo Calendar - Derek-watring.com");
		memo.mainFrame.setDefaultCloseOperation(3);
		memo.mainFrame.setSize(700, 400);
		memo.mainFrame.setLocationRelativeTo(null);
		memo.mainFrame.setResizable(false);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(memo.mainFrame);
		} catch (Exception e) {
			memo.bottomInfo.setText("ERROR : LookAndFeel setting failed");
		}
		memo.calOpPanel = new JPanel();
		memo.todayBut = new JButton("Today");
		memo.todayBut.setToolTipText("Today");
		memo.todayBut.addActionListener(memo.lForCalOpButtons);
		memo.todayLab = new JLabel(memo.today.get(2) + 1 + "/" + memo.today.get(5) + "/" + memo.today.get(1));
		memo.lYearBut = new JButton("<<");
		memo.lYearBut.setToolTipText("Previous Year");
		memo.lYearBut.addActionListener(memo.lForCalOpButtons);
		memo.lMonBut = new JButton("<");
		memo.lMonBut.setToolTipText("Previous Month");
		memo.lMonBut.addActionListener(memo.lForCalOpButtons);
		memo.curMMYYYYLab = new JLabel(
				"<html><table width=100><tr><th><font size=5>" + (memo.month + 1 < 10 ? "&nbsp;" : "")
						+ (memo.month + 1) + " / " + memo.year + "</th></tr></table></html>");
		memo.nMonBut = new JButton(">");
		memo.nMonBut.setToolTipText("Next Month");
		memo.nMonBut.addActionListener(memo.lForCalOpButtons);
		memo.nYearBut = new JButton(">>");
		memo.nYearBut.setToolTipText("Next Year");
		memo.nYearBut.addActionListener(memo.lForCalOpButtons);
		memo.calOpPanel.setLayout(new GridBagLayout());
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
		memo.calOpPanel.add(memo.todayBut, calOpGC);
		calOpGC.gridwidth = 3;
		calOpGC.gridx = 2;
		calOpGC.gridy = 1;
		memo.calOpPanel.add(memo.todayLab, calOpGC);
		calOpGC.anchor = 10;
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 1;
		calOpGC.gridy = 2;
		memo.calOpPanel.add(memo.lYearBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 2;
		calOpGC.gridy = 2;
		memo.calOpPanel.add(memo.lMonBut, calOpGC);
		calOpGC.gridwidth = 2;
		calOpGC.gridx = 3;
		calOpGC.gridy = 2;
		memo.calOpPanel.add(memo.curMMYYYYLab, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 5;
		calOpGC.gridy = 2;
		memo.calOpPanel.add(memo.nMonBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 6;
		calOpGC.gridy = 2;
		memo.calOpPanel.add(memo.nYearBut, calOpGC);

		memo.calPanel = new JPanel();
		memo.weekDaysName = new JButton[CALENDAR_WIDTH];
		for (int i = 0; i < CALENDAR_WIDTH; i++) {
			memo.weekDaysName[i] = new JButton(memo.WEEK_DAY_NAME[i]);
			memo.weekDaysName[i].setBorderPainted(false);
			memo.weekDaysName[i].setContentAreaFilled(false);
			memo.weekDaysName[i].setForeground(Color.WHITE);
			if (i == 0) {
				memo.weekDaysName[i].setBackground(new Color(200, 50, 50));
			} else if (i == 6) {
				memo.weekDaysName[i].setBackground(new Color(50, 100, 200));
			} else {
				memo.weekDaysName[i].setBackground(new Color(150, 150, 150));
			}
			memo.weekDaysName[i].setOpaque(true);
			memo.weekDaysName[i].setFocusPainted(false);
			memo.calPanel.add(memo.weekDaysName[i]);
		}
		for (int i = 0; i < CALENDAR_HEIGHT; i++) {
			for (int j = 0; j < CALENDAR_WIDTH; j++) {
				memo.dateButs[i][j] = new JButton();
				memo.dateButs[i][j].setBorderPainted(false);
				memo.dateButs[i][j].setContentAreaFilled(false);
				memo.dateButs[i][j].setBackground(Color.WHITE);
				memo.dateButs[i][j].setOpaque(true);
				memo.dateButs[i][j].addActionListener(memo.lForDateButs);
				memo.calPanel.add(memo.dateButs[i][j]);
			}
		}
		memo.calPanel.setLayout(new GridLayout(0, 7, 2, 2));
		memo.calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		memo.showCal();

		memo.infoPanel = new JPanel();
		memo.infoPanel.setLayout(new BorderLayout());
		memo.infoClock = new JLabel("", 4);
		memo.infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		memo.infoPanel.add(memo.infoClock, "North");
		memo.selectedDate = new JLabel("<Html><font size=3>" + (memo.today.get(2) + 1) + "/" + memo.today.get(5) + "/"
				+ memo.today.get(1) + "&nbsp;(Today)</html>", 2);
		memo.selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		memo.memoPanel = new JPanel();
		memo.memoPanel.setBorder(BorderFactory.createTitledBorder("Memo"));
		memo.memoArea = new JTextArea();
		memo.memoArea.setLineWrap(true);
		memo.memoArea.setWrapStyleWord(true);
		memo.memoAreaSP = new JScrollPane(memo.memoArea, 22, 31);
		memo.memoSubPanel = new JPanel();
		memo.saveBut = new JButton("Save");
		memo.saveBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				String memoText = memo.memoArea.getText();
				if (memoText.length() > 200){
					memo.bottomInfo.setText("Cannot store more than "+memo.maxMemoSize+" characters");
				}
				else{
					String str = memo.memoArea.getText();
					DBConnect.setContentData(memo, str);
					memo.bottomInfo.setText("Data saved to database");
				}
				memo.showCal();
			}
		});
		memo.clearBut = new JButton("Clear");
		memo.clearBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				memo.memoArea.setText(null);
				memo.bottomInfo.setText("Text area cleared");
			}
		});
		memo.memoSubPanel.add(memo.saveBut);
		memo.memoSubPanel.add(memo.clearBut);
		memo.memoPanel.setLayout(new BorderLayout());
		memo.memoPanel.add(memo.selectedDate, "North");
		memo.memoPanel.add(memo.memoAreaSP, "Center");
		memo.memoPanel.add(memo.memoSubPanel, "South");

		JPanel frameSubPanelWest = new JPanel();
		Dimension calOpPanelSize = memo.calOpPanel.getPreferredSize();
		calOpPanelSize.height = 90;
		memo.calOpPanel.setPreferredSize(calOpPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(memo.calOpPanel, "North");
		frameSubPanelWest.add(memo.calPanel, "Center");

		JPanel frameSubPanelEast = new JPanel();
		Dimension infoPanelSize = memo.infoPanel.getPreferredSize();
		infoPanelSize.height = 65;
		memo.infoPanel.setPreferredSize(infoPanelSize);
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(memo.infoPanel, "North");
		frameSubPanelEast.add(memo.memoPanel, "Center");

		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 410;
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);

		memo.frameBottomPanel = new JPanel();
		memo.frameBottomPanel.add(memo.bottomInfo);

		memo.mainFrame.setLayout(new BorderLayout());
		memo.mainFrame.add(frameSubPanelWest, "West");
		memo.mainFrame.add(frameSubPanelEast, "Center");
		memo.mainFrame.add(memo.frameBottomPanel, "South");
		memo.mainFrame.setVisible(true);

	}
}
