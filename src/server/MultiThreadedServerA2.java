package server;

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.*;
import utils.DBConnection;

/**
 * Server class which only registered students can access. The server creates a
 * new thread for each client. Invalid logins will result in an appropriate
 * message being sent to the client, and the socket and the client window will
 * close.
 * 
 * @author Aaron Mooney Date Created: 26/10/2018 Last Modified: 11/11/2018
 *
 */

public class MultiThreadedServerA2 extends JFrame {

	// Auto generated serial version ID
	private static final long serialVersionUID = -8090852805990309056L;

	private JTextArea jta;
	private DBConnection conn = new DBConnection();

	/**
	 * Main method which creates an instance of the server.
	 * 
	 * @param args
	 *            - unused
	 */
	public static void main(String[] args) {
		new MultiThreadedServerA2();
	}

	/**
	 * Constructor
	 */
	public MultiThreadedServerA2() {
		// GUI layout
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(405, 250, -403, -248);
		getContentPane().add(scrollPane);

		jta = new JTextArea();
		jta.setBounds(-1, 0, 483, 253);
		getContentPane().add(jta);

		setTitle("Server");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); // It is necessary to show the frame here!

		// Create a server socket
		ServerSocket serverSocket = null;
		try {
			// Initialize the socket
			serverSocket = new ServerSocket(8000);

			// Add initialization message to server view
			jta.append("Server started at " + new Date() + '\n');

			while (true) {
				// Listen for a connection request
				Socket socket = serverSocket.accept();

				// Start client on a new thread
				myClient c = new myClient(socket);
				c.start();

			}
		} catch (IOException ex) {
			try {
				if (null != serverSocket)
					serverSocket.close();
			} catch (IOException e) {
				System.err.println("Exception occured in constructor for class 'MultiThreadedServerA2'");
				e.printStackTrace();
			}
			System.err.println("Exception occured in constructor for class 'MultiThreadedServerA2'");
			ex.printStackTrace();
		}
	}

	/**
	 * Thread class which handles communication with the client and validates users.
	 * 
	 * @author Aaron Mooney Date Created: 26/10/2018 Last Modified: 11/11/2018
	 *
	 */
	private class myClient extends Thread {
		// The socket the client is connected through
		private Socket socket;
		// The ip address of the client
		private InetAddress address;
		// The input and output streams to the client
		private DataInputStream inputFromClient;
		private DataOutputStream outputToClient;

		/**
		 * Constructor
		 * 
		 * @param socket
		 * @throws IOException
		 */
		public myClient(Socket socket) {
			// Assign socket and address
			this.socket = socket;
			address = socket.getInetAddress();
			// Declare & Initialize input/output streams
			try {
				inputFromClient = new DataInputStream(socket.getInputStream());
				outputToClient = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.err.println("Exception occured in constructor for class 'myClient'");
				e.printStackTrace();
			}
		}

		/**
		 * The method that runs when the thread starts
		 * 
		 * @return nothing
		 */
		public void run() {
			try {
				// Read student ID from client
				String stdId = inputFromClient.readUTF();

				// Search to see if stdID exists in the database
				ResultSet rs = conn.getItem(stdId);
				String name = "";
				String surname = "";
				if (rs.next()) {
					// If user exists then send an appropriate login message to the client
					// Also, send a true boolean to flag the user as logged in.
					name = rs.getString("FNAME");
					surname = rs.getString("SNAME");
					outputToClient
							.writeUTF("Welcome " + name + " " + surname + ". You are now connected to the server.\n");
					outputToClient.flush();
					outputToClient.writeBoolean(true);
					outputToClient.flush();

					// Attach a message to the server text view to see which user has logged in.
					jta.append(name + " " + surname + " " + address + " Has joined the server.\n");
				} else {
					// If the user does not exist then send an appropriate message and sent a false
					// boolean.
					outputToClient.writeUTF("Sorry " + stdId + ". You are not a registered student. Bye.");
					outputToClient.flush();
					outputToClient.writeBoolean(false);
					outputToClient.flush();

					// Close the socket
					socket.close();
				}
				while (true) {

					if (!socket.isClosed()) {
						// Listen for client input
						String clientInput = inputFromClient.readUTF();

						// Create a message to be added to the text view
						String message = name + " " + surname + " " + address + ": " + clientInput + "\n";

						// Also send this message back to the client so they can see what they have
						// sent.
						outputToClient.writeUTF(message);
						outputToClient.flush();
						jta.append(message);
					}
				}
			} catch (Exception e) {
				System.err.println(e + " on " + socket);
				e.printStackTrace();
			}
		}
	}
}
