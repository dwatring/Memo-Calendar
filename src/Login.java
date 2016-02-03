import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;

	protected static String username;
	protected static String password;
	
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	
	/**
	 * Create the frame.
	 */
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(123, 117, 188, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(123, 156, 188, 27);
		contentPane.add(passwordField);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(44, 117, 69, 27);
		contentPane.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(44, 156, 69, 27);
		contentPane.add(lblPassword);
		
		JLabel lblLoading = new JLabel("");
		lblLoading.setForeground(Color.RED);
		lblLoading.setText("Loading...");
		lblLoading.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoading.setBounds(10, 223, 414, 27);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				contentPane.add(lblLoading);
				Login.username = passwordField.getText();
				System.out.println(username);
				Login.password = textField.getText();
				if(username.length() != 0 && password.length() != 0){
					new DBConnect();
					new MemoCalendar();
					setVisible(false);
				}else{
					lblLoading.setText("Please enter a username and password");
				}
			}
		});
		btnNewButton.setBounds(172, 195, 89, 23);
		contentPane.add(btnNewButton);

		JTextPane txtpnMemoCalendarLogin = new JTextPane();
		txtpnMemoCalendarLogin.setEditable(false);
		txtpnMemoCalendarLogin.setFont(new Font("HelveticaNeueLT Pro 35 Th", Font.PLAIN, 35));
		txtpnMemoCalendarLogin.setText("Memo Calendar Login");
		txtpnMemoCalendarLogin.setBackground(UIManager.getColor("Button.background"));
		txtpnMemoCalendarLogin.setBounds(56, 33, 322, 42);
		contentPane.add(txtpnMemoCalendarLogin);
		
		JLabel lblDoNotUse = new JLabel("Do not use your personal user/pass! I can see these values currently...");
		lblDoNotUse.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblDoNotUse.setHorizontalAlignment(SwingConstants.CENTER);
		lblDoNotUse.setForeground(Color.RED);
		lblDoNotUse.setBounds(66, 83, 304, 23);
		contentPane.add(lblDoNotUse);
	}
}
