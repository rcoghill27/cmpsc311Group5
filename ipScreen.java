import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ipScreen {
    private JList rememberedRoomsJList;
    private JTextField displayTextField;
    private JTextField ipTextField;
    private JTextField portTextField;
    private JButton settingsButton;
    private JButton exitButton;
    private JButton enterButton;
    private JPanel ipPanel;
    private final FileWriter writer = new FileWriter();

    public ipScreen(JFrame appWindow) {
        //ArrayList<RememberedRoom> rooms = writer.readRememberedRoomFile();

        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String displayName = displayTextField.getText();
                String ipAddress = ipTextField.getText();
                String port = portTextField.getText();
                RememberedRoom newRoom = new RememberedRoom();
                //newRoom.setDisplayName(displayName);
                //newRoom.setIpAddress(ipAddress);
                //newRoom.setPort(port);
                //writer.writeRememberedRoomFile();
                appWindow.setVisible(false);
                ChatWindow chatWindow = new ChatWindow(appWindow);
                appWindow.setContentPane(chatWindow.getPanel());
                appWindow.pack();
                appWindow.setSize(1000,700);
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
            }
        });
    }

    public JPanel getPanel(){
        return this.ipPanel;
    }
}
