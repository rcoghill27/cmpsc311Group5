
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

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
    private JComboBox ipDropDown;
    private final ChatFileWriter writer = new ChatFileWriter();
    private ArrayList<RememberedRoom> rooms;
    private final MaskFormatter ipMask1 = new MaskFormatter("##.##.###.###");
    private final MaskFormatter ipMask2 = new MaskFormatter("###.###.#.###");
    private final MaskFormatter ipMask3 = new MaskFormatter("##.##.##.###");

    private final MaskFormatter ipMask4 = new MaskFormatter("##.##.###.##");
    private final MaskFormatter ipMask5 = new MaskFormatter("##.##.##.##");
    public ipScreen(JFrame appWindow) throws ParseException {
        // Sets the buttons to invisible if nothing is selected.

        joinButton.setEnabled(false);
        joinButton.setVisible(false);
        removeButton.setVisible(false);

        // Sets the placeholders for the required characters and populates the dropdown menu with ip address format options.
        ipMask1.setPlaceholderCharacter('#');
        ipMask2.setPlaceholderCharacter('#');
        ipMask3.setPlaceholderCharacter('#');
        ipMask4.setPlaceholderCharacter('#');
        ipMask5.setPlaceholderCharacter('#');
        ipDropDown.addItem("##.##.###.###");
        ipDropDown.addItem("###.###.#.###");
        ipDropDown.addItem("##.##.##.###");
        ipDropDown.addItem("##.##.###.##");
        ipDropDown.addItem("##.##.##.##");


        // Sets the ip text field mask to the first one.
        ipTextField.setFont(new Font("Times New Roman", Font.PLAIN, 16 ));
        ipMask1.install(ipTextField);

        try {
            // Assigns the arraylist to the rooms read from the file.
            rooms = writer.readRememberedRoomFile();

            // if it is empty, creates a new array list.
            if (rooms == null){
                rooms = new ArrayList<>();
            }
            // If the arraylist is not empty, iterates through and updates list.
            else{
                // Creates a string array the size of the array list.
                String[] roomIPs = new String[rooms.size()];
                for(int i = 0; i < rooms.size(); i++){
                    // Adds the ip and port as a string to string array.
                    roomIPs[i] = rooms.get(i).getIpAddress() + ":" + String.valueOf(rooms.get(i).getPort());
                }
                // Sets the list entries and the font as well as updates the GUI.
                rememberedRoomsJList.setListData(roomIPs);
                rememberedRoomsJList.setFont(new Font("Times New Roman", Font.PLAIN, 16));
                rememberedRoomsJList.updateUI();
            }
        } catch (Exception ignored){
        }

        // Action listener for the enter button.
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Retrieves the text from the fields.
                String displayName = displayTextField.getText();
                String ipAddress = ipTextField.getText();
                String port = portTextField.getText();

                // Calls the join room function to join the room.
                joinRoom(appWindow, displayName, ipAddress, port);
            }
        });

        // Listener for if an item is selected in the list.
        rememberedRoomsJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Sets each of the associated buttons to enabled and visible.
                joinButton.setVisible(true);
                removeButton.setVisible(true);
                joinButton.setEnabled(true);
                removeButton.setEnabled(true);
            }
        });

        // Destroys the main window.
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appWindow.dispose();
            }
        });

        // Button listener to remove a remembered room
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gets the selected index from the JList
                int index = rememberedRoomsJList.getSelectedIndex();
                // Removes the item from the array of remembered room objects.
                rooms.remove(index);
                // Creates a new string array and populates it with the updated array information.
                String[] ipAddresses = new String[rooms.size()];
                writer.writeRememberedRoomFile(rooms);

                for(int i = 0; i < rooms.size(); i++){
                    ipAddresses[i] = rooms.get(i).getIpAddress() + ":" + rooms.get(i).getPort();
                }
                // Sets the list in the JList and updates the screen.
                rememberedRoomsJList.setListData(ipAddresses);
                rememberedRoomsJList.updateUI();

                // If the list is empty, makes the join and remove buttons invisible.
                if(rooms.size() == 0){
                    joinButton.setVisible(false);
                    removeButton.setVisible(false);
                }
                // Disables the buttons after a room is removed so prevent errors.
                joinButton.setEnabled(false);
                removeButton.setEnabled(false);
            }
        });

        // Action listener for joining a room.
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gets the selected room from the JList and sends the information from the object to the join room function.
                int room = rememberedRoomsJList.getSelectedIndex();
                RememberedRoom selectedRoom = rooms.get(room);
                joinRoom(appWindow, selectedRoom.getDisplayName(), selectedRoom.getIpAddress(), String.valueOf(selectedRoom.getPort()));
            }
        });


        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Creates a new JFrame for the settings menu.
                JFrame settingFrame = new JFrame("Settings");
                settingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                settingFrame.setContentPane(new settingsWindow(appWindow, settingFrame).getPanel());
                settingFrame.pack();
                settingFrame.setLocationRelativeTo(null);
                settingFrame.setVisible(true);

            }
        });
        // Creates an action listener
        ipDropDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Compares the selected item to a string and updates the text field with the appropriate mask.
                if (Objects.equals(ipDropDown.getSelectedItem(), "##.##.###.###")) {
                    ipMask1.install(ipTextField);
                    ipTextField.updateUI();
                }
                else if (Objects.equals(ipDropDown.getSelectedItem(),"###.###.#.###")) {
                    ipMask2.install(ipTextField);
                    ipTextField.updateUI();
                }
                else if (Objects.equals(ipDropDown.getSelectedItem(),"##.##.##.###")) {
                    ipMask3.install(ipTextField);
                    ipTextField.updateUI();
                }
                else if (Objects.equals(ipDropDown.getSelectedItem(),"##.##.###.##")) {
                    ipMask4.install(ipTextField);
                    ipTextField.updateUI();
                }
                else if (Objects.equals(ipDropDown.getSelectedItem(), "##.##.##.##")){
                    ipMask5.install(ipTextField);
                    ipTextField.updateUI();
                }


            }
        });
    }

    public JPanel getPanel(){
        return this.ipPanel;
    }

    // Method that changes windows when joining the room.
    private void joinRoom(JFrame appWindow, String displayName, String ipAddress, String port){

        // Checks the ipTextField to see if a full IP address was input.
        boolean incompleteIp = false;
        for (int i = 0; i < ipAddress.length(); i++){
            if (ipAddress.charAt(i) == '#'){
                incompleteIp = true;
            }
        }

        // Displays a popup if not all the information was filled in correctly.
        if(displayName.equals("") || incompleteIp || port.equals("")){
            JOptionPane.showMessageDialog(appWindow, "One of more fields is blank.\nPlease fill in all information.", "Missing Information", JOptionPane.WARNING_MESSAGE);
        }

        else {
            // Creates a new remembered room object with the entered information.
            RememberedRoom newRoom = new RememberedRoom();
            newRoom.setDisplayName(displayName);
            newRoom.setIpAddress(ipAddress);
            newRoom.setPort(port);
            // Adds the room to the array if the array is empty..
            if (rooms.size() == 0) {
                rooms.add(newRoom);
            } else {
                // Checks the array to see if the room is already in the array.
                boolean addRoom = true;
                for (RememberedRoom room : rooms) {
                    if ((room.getIpAddress().equals(newRoom.getIpAddress()) && room.getPort() == newRoom.getPort())) {
                        addRoom = false;
                    }
                }
                // If the room is not already in the array, adds the array.
                if (addRoom) {
                    rooms.add(newRoom);
                }
            }
            // Writes to the file for to save the rooms.
            writer.writeRememberedRoomFile(rooms);
            // Changes the window to the chatroom window.
            appWindow.setVisible(false);


            ChatWindow chatWindow = new ChatWindow(appWindow, displayName, ipAddress, port);
            // Checks to make sure the client connected to the server before it changes window.
            if (chatWindow.getConnected()) {
                appWindow.setContentPane(chatWindow.getPanel());
                appWindow.pack();
                appWindow.setSize(1000, 700);
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
            }

        }
    }
}

