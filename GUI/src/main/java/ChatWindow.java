import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow {
    private JPanel backgroundPanel;
    private JTextPane textPane1;
    private JList list1;
    private JTextField textField1;
    private JButton sendButton;
    private JButton disconnectButton;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel portLabel;

    public ChatWindow(JFrame appWindow, String displayName, String ipAddress, String port) {
        nameLabel.setText(displayName);
        addressLabel.setText(ipAddress);
        portLabel.setText(port);
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appWindow.setVisible(false);
                appWindow.setContentPane(new ipScreen(appWindow).getPanel());
                appWindow.pack();
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
            }
        });
    }

    public JPanel getPanel(){
        return this.backgroundPanel;
    }
}
