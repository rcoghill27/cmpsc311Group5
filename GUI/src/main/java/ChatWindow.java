import com.formdev.flatlaf.FlatLightLaf;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
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
    private JLabel dateLabel;
    private JLabel currentTimeLabel;
    // ArrayLists for members and messages.
    private final ArrayList<String> messageArray = new ArrayList<>();
    private final ArrayList<String> memberArray = new ArrayList<>();
    private final ArrayList<String> memberNamesArray = new ArrayList<>();
    // Variables for socket and the ClientSocket class.
    private final Socket serverSocket;

    // Boolean variables for loops.
    private boolean reading = true;
    private boolean connectionEstablished = true;
    private boolean typing = false;
    private boolean notifications = false;



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
                if (connectionAttempts < 3 && connectionAttempts != 0){
                    JOptionPane.showMessageDialog(appWindow, "Connection Timed Out. Retrying in 5 seconds. Attempt (" + connectionAttempts + " of 3)", "Connection Failed", JOptionPane.ERROR_MESSAGE);
                }
                // If the connection fails three times, stops attempting to connect.
                else if (connectionAttempts >= 3) {
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
                    Thread.sleep(3000);
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
            dateLabel.setText(getDate());
            ChatFileWriter writer1 = new ChatFileWriter();
            String notificationsSetting = writer1.readSettingsFile().split(",")[2];
            if (notificationsSetting.equals("1")){
                notifications  = true;
            }
            else {
                notifications = false;
            }
            // Sends a message to the server to let it know the user's display name to update members list.
            sendServerMessages(serverSocket, "DisplayName&#" + displayName);
            // Initializes style attributes for the chat window.
            SimpleAttributeSet rightAlign = new SimpleAttributeSet();
            SimpleAttributeSet leftAlign = new SimpleAttributeSet();
            SimpleAttributeSet centerAlign = new SimpleAttributeSet();
            SimpleAttributeSet font = new SimpleAttributeSet();

            // Sets specific values to the attributes.
            StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
            StyleConstants.setAlignment(centerAlign, StyleConstants.ALIGN_CENTER);

            StyleConstants.setFontFamily(font, "Times New Roman");
            StyleConstants.setFontSize(font, 20);


            // Changes the color of the chat text, dependent on theme color scheme.
            if (UIManager.getLookAndFeel().toString().equals(new FlatLightLaf().toString())){
                StyleConstants.setForeground(font, Color.BLACK);
            }
            else {
                StyleConstants.setForeground(font, Color.WHITE);
            }

            // Creates an HTMLEditorKit to allow the textPane to be customized with different styles.
            HTMLEditorKit eKit = new HTMLEditorKit();
            chatTextPane.setEditorKit(eKit);
            HTMLDocument htmlDoc = (HTMLDocument) chatTextPane.getDocument();

            // Sets the font color, family, and size for the text.
            htmlDoc.setParagraphAttributes(0, htmlDoc.getLength(), font, false);

            // Sends a notification message that informs other users that the user has joined the chat.
            sendServerMessages(serverSocket, "Message&#Notification:" + displayName + " has joined the chat.");
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            } catch (Exception ignored){
            }
            BufferedReader finalInput = input;
            // Lambda expression for new thread to read messages.
            Thread readingThread = new Thread(() -> {
                try {

                    // Creates an object of the receive Messages class and calls the method to read. from the server.
                    ReceiveMessages receiver = new ReceiveMessages(finalInput, messageArray, memberArray, memberNamesArray);
                    receiver.read();
                }catch (Exception ignored){
                }
            });
            readingThread.start();



            // Creates a new thread with a lambda expression to read messages from the server and update the GUI
            Thread chatWindowThread = new Thread(() -> {
                // Initializes a string array.
                String[] messageSplit;

                // Loop for while the program is reading from the server.
                while (reading) {
                    // If the message has a new messages, the text pane is updated.
                    if (messageArray.size() != 0) {

                        // Iterates through the new messages and splits them at the colon.
                        for (int i = 0; i < messageArray.size(); i++) {

                            messageSplit = messageArray.get(i).split(":");

                            // Used to line wrap in the JTextPane because of HTML format.
                            // Checks to see if the message is over 70 characters
                            if (messageArray.get(i).length() > 70) {
                                // Finds how many times it needs to break the string to fit
                                int breakCount = messageArray.get(i).length() % 70;
                                // Creates a string builder to hold the broken up message.
                                StringBuilder lineBreaker = new StringBuilder();
                                // Loops through the string and breaks it up every 70 characters and adds a new line.
                                for (int j = 0 ; j < breakCount; j++) {
                                    int startInt = 70*j;
                                    int endInt = 70 + 70*j;

                                    if (endInt < messageSplit[messageSplit.length - 1].length()) {
                                        String wrappedMessage = messageSplit[messageSplit.length - 1].substring(startInt, endInt) + "\n";
                                        lineBreaker.append(wrappedMessage);
                                    }
                                    else{
                                        endInt = 70 + 70 * (j-1);
                                        lineBreaker.append(messageSplit[messageSplit.length - 1].substring(endInt, messageSplit[messageSplit.length -1].length()));
                                        break;
                                    }
                                }
                                // Sets the messageSplit entry to the newly formatted string.
                                messageSplit[messageSplit.length - 1] = lineBreaker.toString();
                                // Clears the string builder.
                                lineBreaker.delete(0, lineBreaker.length());
                            }

                            try {
                                // If the message was sent by the user, displays it on the right side of the screen.
                                if (messageSplit[0].equals(displayName)) {
                                    htmlDoc.insertString(0, messageSplit[1] + " :" + messageSplit[0] + "\n", rightAlign);

                                    // If the message is a notification about members leaving or joining, centers the test.
                                } else if (messageSplit[0].equals("Notification")) {
                                    htmlDoc.insertString(0, messageSplit[1] + "\n", centerAlign);
                                    if (messageSplit[1].equals("Server is shutting down.")){
                                        messageField.setEnabled(false);
                                        sendButton.setEnabled(false);
                                    }

                                } else if (messageSplit[0].equals("DM")){
                                    if (messageSplit[1].equals("@" +displayName.replaceAll(" ", ""))){
                                        htmlDoc.insertString(0, messageSplit[2] + ": " + messageSplit[3]+"\n", leftAlign);
                                        // Plays sound notification for direct message if the setting is on.
                                        if (notifications) {
                                            soundNotification();
                                        }
                                    }
                                    else if (messageSplit[2].equals(displayName)){
                                        htmlDoc.insertString(0, messageSplit[1] + " " + messageSplit[3] + " :" + messageSplit[2] + "\n", rightAlign);
                                    }

                                } else if(messageSplit[0].equals("TypingNotification")){
                                    typingLabel.setText(messageSplit[1] + " is typing...");

                                }else if(messageSplit[0].equals("RemoveTyping")){
                                    typingLabel.setText("");
                                }else {
                                    // Aligns all messages from other users on the left side of the screen.
                                    htmlDoc.insertString(0, messageArray.get(i) + "\n", leftAlign);
                                    // Plays sound notification for message from others to the chat room if the setting is on.
                                    if (notifications) {
                                        soundNotification();
                                    }
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
                        sendServerMessages(serverSocket, "DisplayName&#" + displayName);
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
                    // Sends a message to the server to notify members that the user has left.
                    sendServerMessages(serverSocket, "Message&#Notification:" + displayName + " has left the chat.");
                    // Sends a message to the server to remove the users name from the members list.
                    sendServerMessages(serverSocket, "RemoveMember&#" + displayName);


                    try {

                        // Closes the socket
                        serverSocket.close();
                        // Breaks the loop that is reading messages from the server.
                        reading = false;
                        timeThread.join();
                        memberListThread.join();
                        chatWindowThread.join();
                        readingThread.join();

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
                    sendMessage(displayName);
                }
            });

            messageField.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    // Listens for the caret to move in the text box to identify when the user is typing.
                    //typingLabel.setText(displayName + " is typing...");
                    if (!typing) {
                        sendServerMessages(serverSocket, "Message&#TypingNotification:" + displayName);
                    }
                    typing = true;
                }
            });
            // Listener in the text field that allows the user to enter a message by hitting the enter key instead of send button.
            messageField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendMessage(displayName);
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
        memberJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // If the user selects a name from the list, autofill the @"user" command and changes to focus from the JList to the text field
                if (memberJList.getSelectedValue() != null){
                    messageField.setText("@" + memberJList.getSelectedValue().toString().replaceAll(" ", "") + " ");
                    messageField.setFocusable(true);
                    messageField.grabFocus();
                    memberJList.clearSelection();
                }
            }
        });
        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                // If the user clicks off of the text field, it sends a message to indicate that they stopped typing.
                sendServerMessages(serverSocket, "Message&#RemoveTyping:" + displayName);
                typing = false;
            }
        });
    }

    public JPanel getPanel(){
        return this.backgroundPanel;
    }
    // Getter for connectionEstablished boolean.
    public boolean getConnected(){
        return this.connectionEstablished;
    }
    private void soundNotification(){
        try {
            // Creates a new thread to play the notification sound.
            Thread notificationSoundThread = new Thread(() -> {
                try {
                    // Uses the JavaZoom library to play a mp3 file.
                    Player player = new Player(new FileInputStream("YouveGotMail.mp3"));
                    player.play();
                } catch (Exception ignored) {
                }
            });
            // Starts the thread and then joins it to the main thread.
            notificationSoundThread.start();
            notificationSoundThread.join();
        } catch (Exception ignored){

        }
    }
    // Helper method used to send a message.
    private void sendMessage(String displayName){
        boolean isDirectMessage = false;
        String userMessage;
        // Determines if it is a direct message
        if (messageField.getText().charAt(0) == '@'){
            isDirectMessage = true;
        }
        int index = 0;
        if (isDirectMessage) {
            // Checks to see where the @Command ends and the message begins.
            for (int i = 0; i < messageField.getText().length(); i++){
                if (messageField.getText().charAt(i) == ' '){
                    index = i;
                    break;
                }
            }
            // Formats the message with the appropriate deliminators and the format of the message.
            userMessage = "Message&#DM:" + messageField.getText().substring(0, index) + ":" + displayName + ":(DM)" + messageField.getText().substring(index, messageField.getText().length());
        }
        else {
            // If the message is not a direct message, formats it in a standard way.
            // concatenates the message identifier, the users display name, and their message to a string.
            userMessage = "Message&#" + displayName + ": " + messageField.getText();
        }
        // Sends the message.
        // Calls the method the send the message to the server.
        sendServerMessages(serverSocket, userMessage);
        // Clears the text field and typing label.
        messageField.setText("");
        typingLabel.setText("");
        sendServerMessages(serverSocket, "Message&#RemoveTyping:" + displayName);
        typing = false;
    }
    public void sendServerMessages(Socket serverSocket, String userMessage){
        try{
            // Creates an output stream to the server.
            PrintWriter output = new PrintWriter(serverSocket.getOutputStream(), true);
            // Sends the message to the server.
            output.println(userMessage);
        } catch (Exception ignored){
        }
    }
    private String getDate(){
        LocalDate currDate = LocalDate.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return formattedDate.format(currDate);
    }
}
