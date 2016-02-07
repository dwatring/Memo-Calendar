import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.FontFormatException;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;

import javax.swing.SwingConstants;

import java.awt.Color;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	protected static String username;
	protected static String password;
	
	private JPanel contentPane;
	private int width = 450;
	private int height = 300;
	static Color bg = new Color(96, 125, 139);
	static Color accent = new Color(0, 188, 212);
	
	/**
	 * AVAILABLE AT https://github.com/dwatring/Memo-Calendar/
	 */
	
	public Login() {
		
		/**
		 * Basic frame setup
		 */
		DBConnect.connectToDB();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(width, height);
		this.setLocationRelativeTo(null);
	    try 
	    {
	    	WebLookAndFeel.install ();
	    } 
	    catch (Exception e) 
	    {
	      e.printStackTrace();
	    }
		contentPane = new JPanel();
		contentPane.setBackground(bg);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/**
		 * Initialize fonts
		 */
		Font title = loadFont("Nexa_Light.otf", 38f);
		
		/**
		 * Add contents to the pane
		 */
		createStatusText();
		createUsernameField();
		createPasswordField();
		createLoginButton(contentPane);
		createWarningText();
		createTitleText(title);
	}
	
	public static Font loadFont(String name, float size){
		InputStream is = Login.class.getResourceAsStream("/resources/"+name);
		Font title = null;
		try {
			title = Font.createFont(Font.TRUETYPE_FONT, is);
			title = title.deriveFont(size);
		} catch (FontFormatException ex) {ex.printStackTrace();
		}
		catch (IOException ex){ex.printStackTrace();}
		return title;
	}
	
	public void createUsernameField(){
        final WebTextField textField = new WebTextField ( 15 );
        textField.setInputPrompt ("Username");
        textField.setInputPromptFont (new Font("Helvetica Neue", Font.PLAIN, 12));
        textField.setBounds(123, 117, 188, 27);
        textField.setHideInputPromptOnFocus ( false );
        contentPane.add(textField);
	}
	
	public void createPasswordField(){
        final WebPasswordField passwordField = new WebPasswordField ( 15 );
        passwordField.setInputPrompt ("Password");
		passwordField.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        passwordField.setBounds(123, 156, 188, 27);
        passwordField.setHideInputPromptOnFocus ( false );
        contentPane.add(passwordField);
	}
	
	public void createStatusText(){
		JLabel statusText = new JLabel("");
		statusText.setForeground(accent);
		statusText.setBackground(bg);
		statusText.setText("");
		statusText.setHorizontalAlignment(SwingConstants.CENTER);
		statusText.setBounds(10, 223, 414, 27);
		contentPane.add(statusText);
	}
	
	public void createTitleText(Font title){
		JTextPane titleText = new JTextPane();
		titleText.setForeground(Color.WHITE);
		titleText.setFont(title);
		titleText.setEditable(false);
		titleText.setText("Memo Calendar Login");
		titleText.setBackground(bg);
		titleText.setBounds(22, 23, 402, 54);
		contentPane.add(titleText);
	}
	
	public void createWarningText(){
		JLabel warningText = new JLabel("Do not use your personal user/pass! These values are not hidden.");
		warningText.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
		warningText.setHorizontalAlignment(SwingConstants.CENTER);
		warningText.setForeground(accent);
		warningText.setBounds(10, 75, 414, 23);
		contentPane.add(warningText);
	}	
	
	public void createLoginButton(JPanel contentPane){
		JButton loginButton = new JButton("Login");
		loginButton.setBackground(bg);
		loginButton.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
		loginButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				WebPasswordField passwordField = (WebPasswordField) contentPane.findComponentAt(124, 157);
				WebTextField textField = (WebTextField) contentPane.findComponentAt(124, 118);
				JLabel statusText = (JLabel) contentPane.findComponentAt(11, 224);
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
	}
}
