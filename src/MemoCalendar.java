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

public class MemoCalendar extends CalendarData {
	JFrame mainFrame;
	JPanel calOpPanel;
	JButton todayBut;
	JLabel todayLab;
	JButton lYearBut;
	JButton lMonBut;
	JLabel curMMYYYYLab;
	JButton nMonBut;
	JButton nYearBut;
	MemoCalendar.ListenForCalOpButtons lForCalOpButtons = new MemoCalendar.ListenForCalOpButtons();
	JPanel calPanel;
	JButton[] weekDaysName;
	JButton[][] dateButs = new JButton[6][7];
	MemoCalendar.listenForDateButs lForDateButs = new MemoCalendar.listenForDateButs();
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
		this.mainFrame = new JFrame("Memo Calendar - Derek-watring.com");
		this.mainFrame.setDefaultCloseOperation(3);
		this.mainFrame.setSize(700, 400);
		this.mainFrame.setLocationRelativeTo(null);
		this.mainFrame.setResizable(false);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this.mainFrame);
		} catch (Exception e) {
			this.bottomInfo.setText("ERROR : LookAndFeel setting failed");
		}
		this.calOpPanel = new JPanel();
		this.todayBut = new JButton("Today");
		this.todayBut.setToolTipText("Today");
		this.todayBut.addActionListener(this.lForCalOpButtons);
		this.todayLab = new JLabel(this.today.get(2) + 1 + "/" + this.today.get(5) + "/" + this.today.get(1));
		this.lYearBut = new JButton("<<");
		this.lYearBut.setToolTipText("Previous Year");
		this.lYearBut.addActionListener(this.lForCalOpButtons);
		this.lMonBut = new JButton("<");
		this.lMonBut.setToolTipText("Previous Month");
		this.lMonBut.addActionListener(this.lForCalOpButtons);
		this.curMMYYYYLab = new JLabel(
				"<html><table width=100><tr><th><font size=5>" + (this.month + 1 < 10 ? "&nbsp;" : "")
						+ (this.month + 1) + " / " + this.year + "</th></tr></table></html>");
		this.nMonBut = new JButton(">");
		this.nMonBut.setToolTipText("Next Month");
		this.nMonBut.addActionListener(this.lForCalOpButtons);
		this.nYearBut = new JButton(">>");
		this.nYearBut.setToolTipText("Next Year");
		this.nYearBut.addActionListener(this.lForCalOpButtons);
		this.calOpPanel.setLayout(new GridBagLayout());
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
		this.calOpPanel.add(this.todayBut, calOpGC);
		calOpGC.gridwidth = 3;
		calOpGC.gridx = 2;
		calOpGC.gridy = 1;
		this.calOpPanel.add(this.todayLab, calOpGC);
		calOpGC.anchor = 10;
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 1;
		calOpGC.gridy = 2;
		this.calOpPanel.add(this.lYearBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 2;
		calOpGC.gridy = 2;
		this.calOpPanel.add(this.lMonBut, calOpGC);
		calOpGC.gridwidth = 2;
		calOpGC.gridx = 3;
		calOpGC.gridy = 2;
		this.calOpPanel.add(this.curMMYYYYLab, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 5;
		calOpGC.gridy = 2;
		this.calOpPanel.add(this.nMonBut, calOpGC);
		calOpGC.gridwidth = 1;
		calOpGC.gridx = 6;
		calOpGC.gridy = 2;
		this.calOpPanel.add(this.nYearBut, calOpGC);

		this.calPanel = new JPanel();
		this.weekDaysName = new JButton[7];
		for (int i = 0; i < 7; i++) {
			this.weekDaysName[i] = new JButton(this.WEEK_DAY_NAME[i]);
			this.weekDaysName[i].setBorderPainted(false);
			this.weekDaysName[i].setContentAreaFilled(false);
			this.weekDaysName[i].setForeground(Color.WHITE);
			if (i == 0) {
				this.weekDaysName[i].setBackground(new Color(200, 50, 50));
			} else if (i == 6) {
				this.weekDaysName[i].setBackground(new Color(50, 100, 200));
			} else {
				this.weekDaysName[i].setBackground(new Color(150, 150, 150));
			}
			this.weekDaysName[i].setOpaque(true);
			this.weekDaysName[i].setFocusPainted(false);
			this.calPanel.add(this.weekDaysName[i]);
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				this.dateButs[i][j] = new JButton();
				this.dateButs[i][j].setBorderPainted(false);
				this.dateButs[i][j].setContentAreaFilled(false);
				this.dateButs[i][j].setBackground(Color.WHITE);
				this.dateButs[i][j].setOpaque(true);
				this.dateButs[i][j].addActionListener(this.lForDateButs);
				this.calPanel.add(this.dateButs[i][j]);
			}
		}
		this.calPanel.setLayout(new GridLayout(0, 7, 2, 2));
		this.calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		showCal();

		this.infoPanel = new JPanel();
		this.infoPanel.setLayout(new BorderLayout());
		this.infoClock = new JLabel("", 4);
		this.infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.infoPanel.add(this.infoClock, "North");
		this.selectedDate = new JLabel("<Html><font size=3>" + (this.today.get(2) + 1) + "/" + this.today.get(5) + "/"
				+ this.today.get(1) + "&nbsp;(Today)</html>", 2);
		this.selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		this.memoPanel = new JPanel();
		this.memoPanel.setBorder(BorderFactory.createTitledBorder("Memo"));
		this.memoArea = new JTextArea();
		this.memoArea.setLineWrap(true);
		this.memoArea.setWrapStyleWord(true);
		this.memoAreaSP = new JScrollPane(this.memoArea, 22, 31);
		readMemo();
		this.memoSubPanel = new JPanel();
		this.saveBut = new JButton("Save");
		this.saveBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				String memo = MemoCalendar.this.memoArea.getText();
				if (memo.length() > 200){
					MemoCalendar.this.bottomInfo.setText("Cannot store more than 200 characters");
				}
				else{
					String str = MemoCalendar.this.memoArea.getText();
					DBConnect.setContentData(MemoCalendar.this, str);
					MemoCalendar.this.bottomInfo.setText("Data saved to database");
				}
				MemoCalendar.this.showCal();
			}
		});
		this.clearBut = new JButton("Clear");
		this.clearBut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				MemoCalendar.this.memoArea.setText(null);
				MemoCalendar.this.bottomInfo.setText("Text area cleared");
			}
		});
		this.memoSubPanel.add(this.saveBut);
		this.memoSubPanel.add(this.clearBut);
		this.memoPanel.setLayout(new BorderLayout());
		this.memoPanel.add(this.selectedDate, "North");
		this.memoPanel.add(this.memoAreaSP, "Center");
		this.memoPanel.add(this.memoSubPanel, "South");

		JPanel frameSubPanelWest = new JPanel();
		Dimension calOpPanelSize = this.calOpPanel.getPreferredSize();
		calOpPanelSize.height = 90;
		this.calOpPanel.setPreferredSize(calOpPanelSize);
		frameSubPanelWest.setLayout(new BorderLayout());
		frameSubPanelWest.add(this.calOpPanel, "North");
		frameSubPanelWest.add(this.calPanel, "Center");

		JPanel frameSubPanelEast = new JPanel();
		Dimension infoPanelSize = this.infoPanel.getPreferredSize();
		infoPanelSize.height = 65;
		this.infoPanel.setPreferredSize(infoPanelSize);
		frameSubPanelEast.setLayout(new BorderLayout());
		frameSubPanelEast.add(this.infoPanel, "North");
		frameSubPanelEast.add(this.memoPanel, "Center");

		Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
		frameSubPanelWestSize.width = 410;
		frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);

		this.frameBottomPanel = new JPanel();
		this.frameBottomPanel.add(this.bottomInfo);

		this.mainFrame.setLayout(new BorderLayout());
		this.mainFrame.add(frameSubPanelWest, "West");
		this.mainFrame.add(frameSubPanelEast, "Center");
		this.mainFrame.add(this.frameBottomPanel, "South");
		this.mainFrame.setVisible(true);

		focusToday();

		MemoCalendar.ThreadConrol threadCnl = new MemoCalendar.ThreadConrol();
		threadCnl.start();
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
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				String fontColor = "black";
				if (j == 0)
					fontColor = "red";
					else if (j == 6)
					fontColor = "blue";
				for(int x=0;x<newDates.length;x++){
					if(this.calDates[i][j] == newDates[x]) {
						this.dateButs[i][j]
						.setText("<html><font color=" + fontColor + "><u>" + this.calDates[i][j] + "</u></font></html>");
					}
					else{
						this.dateButs[i][j]
						.setText("<html><font color=" + fontColor + ">" + this.calDates[i][j] + "</font></html>");
					}
				}
				JLabel todayMark = new JLabel("<html><font color=green>*</html>");
				this.dateButs[i][j].removeAll();
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
		private ListenForCalOpButtons() {
		}

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
		private listenForDateButs() {
		}

		public void actionPerformed(ActionEvent e) {
			int k = 0;
			int l = 0;
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 7; j++) {
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
		private ThreadConrol() {
		}

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
}
