import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;

public class ipScreen {
    private JList rememberedRoomsJList;
    private JTextField displayTextField;
    private JFormattedTextField ipTextField;
    private JTextField portTextField;
    private JButton settingsButton;
    private JButton exitButton;
    private JButton enterButton;
    private JPanel ipPanel;
    private JButton joinButton;
    private JButton removeButton;
    private final FileWriter writer = new FileWriter();
    private ArrayList<RememberedRoom> rooms;

    public ipScreen(JFrame appWindow) {
        joinButton.setVisible(false);
        removeButton.setVisible(false);
        try {
            MaskFormatter ipMask = new MaskFormatter("##.##.###.###");
            //MaskFormatter ipMask = new MaskFormatter("###.###.#.###");
            ipMask.setPlaceholderCharacter('#');
            ipMask.install(ipTextField);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        try {
            rooms = writer.readRememberedRoomFile();
            if (rooms == null){
                rooms = new ArrayList<>();
            }
            else{
                String[] roomIPs = new String[rooms.size()];
                for(int i = 0; i < rooms.size(); i++){
                    roomIPs[i] = rooms.get(i).getIpAddress() + ":" + String.valueOf(rooms.get(i).getPort());
                }
                rememberedRoomsJList.setListData(roomIPs);
                rememberedRoomsJList.revalidate();
                rememberedRoomsJList.repaint();
                rememberedRoomsJList.updateUI();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String displayName = displayTextField.getText();
                String ipAddress = ipTextField.getText();
                String port = portTextField.getText();
                joinRoom(appWindow, displayName, ipAddress, port);
            }
        });
        rememberedRoomsJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                joinButton.setVisible(true);
                removeButton.setVisible(true);
                joinButton.setEnabled(true);
                removeButton.setEnabled(true);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appWindow.dispose();
            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = rememberedRoomsJList.getSelectedIndex();
                rooms.remove(index);
                String[] ipAddresses = new String[rooms.size()];
                writer.writeRememberedRoomFile(rooms);
                for(int i = 0; i < rooms.size(); i++){
                    ipAddresses[i] = rooms.get(i).getIpAddress() + ":" + rooms.get(i).getPort();
                }
                rememberedRoomsJList.setListData(ipAddresses);
                rememberedRoomsJList.repaint();
                rememberedRoomsJList.revalidate();
                rememberedRoomsJList.updateUI();
                if(rooms.size() == 0){
                    joinButton.setVisible(false);
                    removeButton.setVisible(false);
                }
                joinButton.setEnabled(false);
                removeButton.setEnabled(false);
            }
        });
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int room = rememberedRoomsJList.getSelectedIndex();
                RememberedRoom selectedRoom = rooms.get(room);
                joinRoom(appWindow, selectedRoom.getDisplayName(), selectedRoom.getIpAddress(), String.valueOf(selectedRoom.getPort()));
            }
        });
    }

    public JPanel getPanel(){
        return this.ipPanel;
    }
    private void joinRoom(JFrame appWindow, String displayName, String ipAddress, String port){

        if(displayName.equals("") || ipAddress.equals("###.###.#.###") || port.equals("")){
            JOptionPane.showMessageDialog(appWindow, "One of more fields is blank.\nPlease fill in all information.", "Missing Information", JOptionPane.WARNING_MESSAGE);
        }
        else {
            RememberedRoom newRoom = new RememberedRoom();
            newRoom.setDisplayName(displayName);
            newRoom.setIpAddress(ipAddress);
            newRoom.setPort(port);
            if (rooms.size() == 0) {
                rooms.add(newRoom);
            } else {
                boolean addRoom = true;
                for (RememberedRoom room : rooms) {
                    System.out.println(!(room.getIpAddress().equals(newRoom.getIpAddress()) && room.getPort() == newRoom.getPort()));
                    if ((room.getIpAddress().equals(newRoom.getIpAddress()) && room.getPort() == newRoom.getPort())) {
                        addRoom = false;
                    }
                }
                if (addRoom) {
                    rooms.add(newRoom);
                }
            }
            writer.writeRememberedRoomFile(rooms);

            //ClientSocket clientSocket = new ClientSocket();
            //Socket serverSocket = clientSocket.connectToServer(appWindow, ipAddress, Integer.valueOf(port));
            //System.out.println("Ip Screen : Is closed: " + serverSocket.isClosed());
            //if (serverSocket != null) {
            appWindow.setVisible(false);
            ChatWindow chatWindow = new ChatWindow(appWindow, displayName, ipAddress, port);
            appWindow.setContentPane(chatWindow.getPanel());
            appWindow.pack();
            appWindow.setSize(1000, 700);
            appWindow.setLocationRelativeTo(null);
            appWindow.setVisible(true);
            //}
            //else{
            // System.out.println("Error Connecting");
            //}
        }
    }
}

