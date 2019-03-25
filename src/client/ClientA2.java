package client;

import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Client class where the student enters their student number to the server for
 * authentication. On successful login, the client can send messages to the
 * server. On unsuccessful login the socket is closed and the client window will
 * close.
 * 
 * @author Aaron Mooney Date Created: 26/10/2018 Last Modified: 11/11/2018
 *
 */

public class ClientA2 extends JFrame {

	// Auto generated serial version ID
	private static final long serialVersionUID = -4732121244357976644L;

	// JFrame components
	private JTextField jtf = new JTextField();
	private JTextArea jta;
	private JButton btnSend;
	private JPanel panel;
	private JPanel login;
	private JTextField stdNumber;

	// Logged in boolean
	private Boolean loggedIn;

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	/**
	 * Main method which creates a new client instance
	 * 
	 * @param args
	 *            - Unused
	 * @return nothing
	 */
	public static void main(String[] args) {
		new ClientA2();
	}

	/**
	 * Constructor
	 */
	public ClientA2() {
		// Initialize login to false
		loggedIn = false;

		// GUI layout
		getContentPane().setLayout(null);

		login = new JPanel();
		login.setBounds(0, 0, 482, 253);
		getContentPane().add(login);
		login.setLayout(null);

		stdNumber = new JTextField();
		stdNumber.setBounds(153, 121, 188, 22);
		login.add(stdNumber);
		stdNumber.setColumns(10);

		JLabel stdNumberLbl = new JLabel("Please enter your student number");
		stdNumberLbl.setBounds(152, 92, 224, 16);
		login.add(stdNumberLbl);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(196, 171, 97, 25);
		login.add(btnLogin);

		panel = new JPanel();
		panel.setVisible(false);
		panel.setBounds(0, 0, 482, 253);
		getContentPane().add(panel);
		panel.setLayout(null);
		JLabel lblMessage = new JLabel("Message:");
		lblMessage.setBounds(0, 229, 69, 16);
		panel.add(lblMessage);
		jtf.setBounds(75, 221, 310, 32);
		panel.add(jtf);
		jtf.setHorizontalAlignment(JTextField.LEFT);

		btnSend = new JButton("Send");
		btnSend.setBounds(385, 221, 97, 32);
		panel.add(btnSend);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 5, 482, 211);
		panel.add(scrollPane);

		jta = new JTextArea();
		scrollPane.setViewportView(jta);
		btnSend.addActionListener(new Listener());
		btnLogin.addActionListener(new Listener());

		// Add validations
		// only allow numeric values
		KeyAdapter numericsOnly = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar()) && !(e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
					JOptionPane optionPane = new JOptionPane(new JLabel("Invalid input. Please enter a numerical value",JLabel.CENTER));
				    JDialog dialog = optionPane.createDialog("");
				    dialog.setLocationRelativeTo(login);
				    dialog.setModal(true);
				    dialog.setVisible(true);
					e.consume();
				}
			}
		};
		// restrict student number input to numeric values
		stdNumber.addKeyListener(numericsOnly);

		jtf.addActionListener(new Listener()); // Register listener

		setTitle("Client");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		// Create a socket
		Socket socket = null;
		try {
			// Assign socket to connect to the server
			socket = new Socket("localhost", 8000);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		} catch (IOException ex) {
			System.err.println("Exception occured in constructor for class 'ClientA2'");
			jta.append(ex.toString() + '\n');

			// close the socket
			if (null != socket)
				try {
					socket.close();
				} catch (IOException e) {
					System.err.println("Exception occured in constructor for class 'ClientA2'");
					e.printStackTrace();
				}
		}
	}

	/**
	 * Listener class which communicates with the server and listens for client
	 * actions
	 * 
	 * @author Aaron Mooney Date Created: 26/10/2018 Last Modified: 11/11/2018
	 *
	 */
	private class Listener implements ActionListener {

		/**
		 * Action Performed method which determines which performs tasks depending on
		 * the button pressed in the GUI.
		 * 
		 * @param e
		 *            - The action performed
		 * @return nothing
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Initialise a response string
				String response = "";

				String action = e.getActionCommand();
				// Check if logged in and handle sending/receiving messages to/from the server
				if (loggedIn) {
					switch (action) {
					case "Send":
						toServer.writeUTF(jtf.getText().toString());
						toServer.flush();
						jtf.setText("");
						break;
					}
					String message = fromServer.readUTF();
					jta.append(message);
				}

				// If not logged in then try and authenticate
				switch (action) {
				case "Login":
					Boolean userExists = false;

					// Read student id from input
					String id = stdNumber.getText().trim();
					toServer.writeUTF(id);
					toServer.flush();

					// assign server response
					response = fromServer.readUTF();
					userExists = fromServer.readBoolean();

					// If a user exists with that student number then log them in and switch from
					// login panel to client panel
					if (userExists) {
						loggedIn = true;
						login.setVisible(false);
						panel.setVisible(true);
					} else {
						// If user does not exist then show an error dialog and close the window
						JOptionPane optionPane = new JOptionPane(new JLabel(response,JLabel.CENTER));
					    JDialog dialog = optionPane.createDialog("");
					    dialog.setLocationRelativeTo(login);
					    dialog.setModal(true);
					    dialog.setVisible(true);
						System.exit(0);
					}
				}
				// Display the response on the client window
				jta.append(response);
			} catch (IOException ex) {
				System.err.println("Exception occured in method 'actionPerformed' in class 'Listener'");
				ex.printStackTrace();
			}
		}
	}
}