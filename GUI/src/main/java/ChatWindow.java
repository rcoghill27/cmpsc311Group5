import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.LocalTime;

public class ChatWindow {
    // Swing GUI variables.
    private JPanel backgroundPanel;
    private JTextPane chatTextPane;
    private JList memberJList;
    private JTextField messageField;
    private JButton sendButton;
    private JButton disconnectButton;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JLabel typingLabel;
    private JLabel newMessagesLabel;
    private JLabel currentTimeLabel;
    // ArrayLists for members and messages.
    private final ArrayList<String> messageArray = new ArrayList<>();
    private final ArrayList<String> memberArray = new ArrayList<>();
    private final ArrayList<String> memberNamesArray = new ArrayList<>();
    // Variables for socket and the ClientSocket class.
    private final Socket serverSocket;
    private final ClientSocket clientSocket = new ClientSocket();

    // Boolean variables for loops.
    private boolean reading = true;
    private boolean connectionEstablished = true;

    public ChatWindow(JFrame appWindow, String displayName, String ipAddress, String port) {
        // Variables for connecting to the server.
        boolean connecting = true;
        int connectionAttempts = 0;
        // Creates a new empty socket.
        serverSocket = new Socket();
        // Creates a new address with the given ip and port.
        InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, Integer.parseInt(port));
        // Attempts to connect to the server socket.
        while (connecting) {
            try {
                // If the connection fails at least once, a popup appears to inform the user.
                if (connectionAttempts < 5 && connectionAttempts != 0){
                    JOptionPane.showMessageDialog(appWindow, "Connection Timed Out. Retrying in 5 seconds. Attempt (" + connectionAttempts + " of 5)", "Connection Failed", JOptionPane.ERROR_MESSAGE);
                }
                // If the connection fails five times, stops attempting to connect.
                else if (connectionAttempts >= 5) {
                    connecting = false;
                }
                // Line that connects to the ip and port given.
                serverSocket.connect(socketAddress);
                // breaks the loop.
                connecting = false;

            } catch (IOException ex) {
                // Adds one to the amount of attempts to connect to the server.
                connectionAttempts += 1;
                try {
                    // makes the thread sleep to allow time before reconnection attempt in case server was busy.
                    Thread.sleep(5000);
                } catch (Exception ignored) {
                }
            } catch (Exception ignored) {
            }
        }
        // Conditional statement for a successful connection to the server.
        if (serverSocket.isConnected()) {

            // Sets labels for user to view their display name, connected ip address, and port.
            nameLabel.setText(displayName);
            addressLabel.setText(ipAddress);
            portLabel.setText(port);
            // Sends a message to the server to let it know the user's display name to update members list.
            clientSocket.sendMessages(serverSocket, "DisplayName&#" + displayName);
            // Initializes style attributes for the chat window.
            SimpleAttributeSet rightAlign = new SimpleAttributeSet();
            SimpleAttributeSet leftAlign = new SimpleAttributeSet();
            SimpleAttributeSet centerAlign = new SimpleAttributeSet();
            SimpleAttributeSet fontColor = new SimpleAttributeSet();
            SimpleAttributeSet fontFamily = new SimpleAttributeSet();
            SimpleAttributeSet fontSize = new SimpleAttributeSet();

            // Sets specific values to the attributes.
            StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
            StyleConstants.setAlignment(centerAlign, StyleConstants.ALIGN_CENTER);
            StyleConstants.setFontFamily(fontFamily, "Times New Roman");
            StyleConstants.setFontSize(fontSize, 20);

            // Changes the color of the chat text, dependent on theme color scheme.
            if (UIManager.getLookAndFeel().toString().equals(new FlatLightLaf().toString())){
                StyleConstants.setForeground(fontColor, Color.BLACK);
            }
            else {
                StyleConstants.setForeground(fontColor, Color.WHITE);
            }

            // Creates an HTMLEditorKit to allow the textPane to be customized with different styles.
            HTMLEditorKit eKit = new HTMLEditorKit();
            chatTextPane.setEditorKit(eKit);
            HTMLDocument htmlDoc = (HTMLDocument) chatTextPane.getDocument();

            // Sets the font color, family, and size for the text.
            htmlDoc.setParagraphAttributes(0, htmlDoc.getLength(), fontColor, false);
            htmlDoc.setParagraphAttributes(0, htmlDoc.getLength(), fontFamily, false);
            htmlDoc.setParagraphAttributes(0, htmlDoc.getLength(), fontSize, false);

            // Sends a notification message that informs other users that the user has joined the chat.
            clientSocket.sendMessages(serverSocket, "Message&#Notification:" + displayName + " has joined the chat.");

            // Creates a new thread with a lambda expression to read messages from the server and update the GUI
            Thread chatWindowThread = new Thread(() -> {
                // Initializes a string array.
                String[] messageSplit;
                // Calls the method to read messages from the server and sends it the current socket as well as arrays.
                clientSocket.readMessages(serverSocket, messageArray, memberArray, memberNamesArray);
                // Loop for while the program is reading from the server.
                while (reading) {
                    // If the message has an new messages, the text pane is updated.
                    if (messageArray.size() != 0) {
                        // Iterates through the new messages and splits them at the colon.
                        for (int i = 0; i < messageArray.size(); i++) {
                            messageSplit = messageArray.get(i).split(":");

                            try {
                                // If the message was sent by the user, displays it on the right side of the screen.
                                if (messageSplit[0].equals(displayName)) {
                                    htmlDoc.insertString(0, messageSplit[1] + ":" + messageSplit[0] + "\n", rightAlign);
                                    // If the message is a notification about members leaving or joining, centers the test.
                                } else if (messageSplit[0].equals("Notification")) {
                                    htmlDoc.insertString(0, messageSplit[1] + "\n", centerAlign);
                                    // Aligns all messages from other users on the left side of the screen.
                                } else {
                                    htmlDoc.insertString(0, messageArray.get(i) + "\n", leftAlign);
                                }

                            } catch (Exception ignored) {
                            }
                        }
                        // Updates the text pane for the user to see.
                        chatTextPane.updateUI();
                        // Clears read messages from the message array
                        messageArray.clear();
                    }
                    // Arbitrary line of code that runs to allow conditional statements to execute.
                    // Simply checks to the validity of the text pane in the window.
                    chatTextPane.isValid();
                }
            });
            // Starts the thread.
            chatWindowThread.start();

            // Creates a new thread to update the members list in the GUI.
            Thread memberListThread = new Thread(() -> {
                while (reading) {
                    // Arbitrary line of code to make the loop run.
                    // Loop does not work if it only contains conditional statements.
                    memberJList.isValid();

                    // Checks the size of the member array to see if an update has occurred.
                    if (memberArray.size() != 0) {
                        clientSocket.sendMessages(serverSocket, "DisplayName&#" + displayName);
                        // Takes the names from the memberNamesArray and creates a string array.
                        String[] memberNames = new String[memberNamesArray.size()];

                        // Copies each name in the ArrayList into the string array.
                        for (int i = 0; i < memberNamesArray.size(); i++) {
                            memberNames[i] = memberNamesArray.get(i);
                        }
                        // Sets the JList content.
                        memberJList.setListData(memberNames);

                        // Clears the member array of the change.
                        memberArray.clear();
                    }
                }
            });
            // Starts the thread.
            memberListThread.start();

            // New thread that updates the clock that is located in the gui.
            Thread timeThread = new Thread(() -> {
                while (reading) {
                    // Gets the current time.
                    LocalTime currentTime = LocalTime.now();
                    // Formats it to hours and minutes.
                    DateTimeFormatter hoursAndMinutes = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = currentTime.format(hoursAndMinutes);
                    // Sets the label to the time.
                    currentTimeLabel.setText("Time: " + formattedTime);
                }
            });
            // Starts the thread.
            timeThread.start();

            // Action listener for the disconnect button.
            disconnectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Sends a message to the server to remove the users name from the members list.
                    clientSocket.sendMessages(serverSocket, "RemoveMember&#" + displayName);
                    // Sends a message to the server to notify members that the user has left.
                    clientSocket.sendMessages(serverSocket, "Message&#Notification:" + displayName + " has left the chat.");

                    try {
                        // Closes the socket
                        serverSocket.close();
                        // Breaks the loop that is reading messages from the server.
                        reading = false;

                        // Changes the window from the chat window to the ip entry screen.
                        appWindow.setVisible(false);
                        appWindow.setContentPane(new ipScreen(appWindow).getPanel());
                        appWindow.pack();
                        appWindow.setLocationRelativeTo(null);
                        appWindow.setVisible(true);

                    } catch (Exception ignored) {
                    }

                }
            });

            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // concatenates the message identifier, the users display name, and their message to a string.
                    String userMessage = "Message&#" + displayName + ": " + messageField.getText();
                    // Calls the method the send the message to the server.
                    clientSocket.sendMessages(serverSocket, userMessage);
                    // Clears the text field and typing label.
                    messageField.setText("");
                    typingLabel.setText("");
                }
            });

            messageField.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    // Listens for the caret to move in the text box to identify when the user is typing.
                    typingLabel.setText(displayName + " is typing...");
                    // TODO Allow all users to see who is typing: currently only works locally for the program user.
                }
            });
            // Listener in the text field that allows the user to enter a message by hitting the enter key instead of send button.
            messageField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String userMessage = "Message&#" + displayName + ": " + messageField.getText();
                        clientSocket.sendMessages(serverSocket, userMessage);
                        messageField.setText("");
                        typingLabel.setText("");
                    }
                }
            });
        }
        // Conditional statement for if the client fails to connect to the server.
        else{
            try {
                // Changes the GUI back to the ipScreen for the user to join a room.
                appWindow.setVisible(false);
                ipScreen returnScreen = new ipScreen(appWindow);
                appWindow.setContentPane(returnScreen.getPanel());
                appWindow.pack();
                appWindow.setSize(700,500);
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
                // Sets the connectionEstablished variable to false for the ipScreen to use.
                connectionEstablished = false;
                // Displays a popup letting the user know that the client could not connect.
                JOptionPane.showMessageDialog(appWindow, "Failed to Connect to Server.", "Failed Connection", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ignored){}
        }
    }

    public JPanel getPanel(){
        return this.backgroundPanel;
    }
    // Getter for connectionEstablished boolean.
    public boolean getConnected(){
        return this.connectionEstablished;
    }
}
