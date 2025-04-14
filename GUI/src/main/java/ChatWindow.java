import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ChatWindow {
    private JPanel backgroundPanel;
    private JTextPane textPane1;
    private JList list1;
    private JTextField messageField;
    private JButton sendButton;
    private JButton disconnectButton;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel portLabel;
    private Socket serverSocket;
    private final ClientSocket clientSocket = new ClientSocket();

    public ChatWindow(JFrame appWindow, String displayName, String ipAddress, String port) {
        //Socket socket = clientSocket.connectToServer(appWindow, ipAddress, Integer.valueOf(port));
        boolean connecting = true;
        int connectionAttempts = 0;

        try {
            serverSocket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, Integer.parseInt(port));
            serverSocket.connect(socketAddress);
            System.out.println("Is closed: " + serverSocket.isClosed());
            nameLabel.setText(displayName);
            addressLabel.setText(ipAddress);
            portLabel.setText(port);
            System.out.println(serverSocket);
            clientSocket.sendMessages(serverSocket, "DisplayName*%" + displayName);
            System.out.println("Chat Window, Is Closed: " + serverSocket.isClosed());
            ArrayList<String> messageArray = new ArrayList<>();

            clientSocket.readMessages(serverSocket, messageArray);
            //Thread chatWindowThread = new Thread();
        } catch (Exception e) {
            e.printStackTrace();

        }
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverSocket.close();
                } catch (Exception ignored){

                }
                appWindow.setVisible(false);
                appWindow.setContentPane(new ipScreen(appWindow).getPanel());
                appWindow.pack();
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userMessage = "Message*%" + displayName + ": " + messageField.getText();
                clientSocket.sendMessages(serverSocket, userMessage);
            }
        });

        messageField.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                System.out.println("Typing...");
            }
        });
    }

    public JPanel getPanel(){
        return this.backgroundPanel;
    }
}
