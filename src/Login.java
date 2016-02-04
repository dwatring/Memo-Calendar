import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.FontFormatException;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Color;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	protected static String username;
	protected static String password;
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private int width = 450;
	private int height = 300;
	
	/**
	 * METHOD DOCUMENTATION AVAILABLE AT https://github.com/dwatring/Memo-Calendar/wiki
	 */
	
	public Login() {
		
		/**
		 * Basic frame setup
		 */
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setSize(width, height);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception ex) {
		}
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/**
		 * Initialize the font
		 */
		
		InputStream is = Login.class.getResourceAsStream("Oxygen-Regular.ttf");
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException ex) {}
		catch (IOException ex){}
		
		/**
		 * Add contents to the pane
		 */
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(44, 117, 69, 27);
		contentPane.add(lblUsername);
		
		textField = new JTextField();
		textField.setBounds(123, 117, 188, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(44, 156, 69, 27);
		contentPane.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(123, 156, 188, 27);
		contentPane.add(passwordField);
		
		JLabel statusText = new JLabel("");
		statusText.setForeground(Color.RED);
		statusText.setText("");
		statusText.setHorizontalAlignment(SwingConstants.CENTER);
		statusText.setBounds(10, 223, 414, 27);
		contentPane.add(statusText);
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				statusText.setText("Loading...");
				Login.password = passwordField.getText();
				Login.username = textField.getText().toLowerCase();
				//TODO add check for user/pass too long for config
				if(username.length() != 0 && password.length() != 0){
					new DBConnect();
						if(DBConnect.isPasswordCorrect(password) == false){
							statusText.setText("Password does not match this username");
						}
						else{
							statusText.setText("Logging in...");
							new MemoCalendar();
							setVisible(false);
						}
				}else
					statusText.setText("Please enter a username and password");
			}
		});
		loginButton.setBounds(172, 195, 89, 23);
		contentPane.add(loginButton);

		JTextPane titlePane = new JTextPane();
		Font title = font.deriveFont(30f);
		titlePane.setEditable(false);
		titlePane.setFont(title);
		titlePane.setText("Memo Calendar Login");
		titlePane.setBackground(UIManager.getColor("Button.background"));
		titlePane.setBounds(70, 33, 322, 42);
		contentPane.add(titlePane);
		
		JLabel warningText = new JLabel("Do not use your personal user/pass! These values are not hidden.");
		warningText.setFont(new Font("Tahoma", Font.PLAIN, 9));
		warningText.setHorizontalAlignment(SwingConstants.CENTER);
		warningText.setForeground(Color.RED);
		warningText.setBounds(66, 83, 304, 23);
		contentPane.add(warningText);
	}
}
