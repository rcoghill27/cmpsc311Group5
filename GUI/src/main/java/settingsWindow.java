import javax.swing.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class settingsWindow {
    // Initializes variables
    private JComboBox themeComboBox;
    private JCheckBox fullScreenCheckbox;
    private JButton backButton;
    private JButton applyButton;
    private JPanel settingsPanel;
    public settingsWindow(JFrame appWindow, JFrame settingsWindow){
        // Sets contents of the drop-down menu.
        themeComboBox.addItem("Dark Mode");
        themeComboBox.addItem("Light Mode");
        themeComboBox.addItem("Monokai");
        themeComboBox.addItem("Arc Orange");
        themeComboBox.addItem("High Contrast");
        // Creates an object of the ChatFileWriter class.
        ChatFileWriter writer = new ChatFileWriter();
        String fullscreen;
        String readFile = writer.readSettingsFile();
        if (readFile == null){
            String newSettings =  (new FlatDarkLaf().toString() + "," + "0" );
            fullscreen = "0";
        } else {

            // Reads from settings file to find if the screen is fullscreen.
            fullscreen = readFile.split(",")[1];

        }
            if (fullscreen.equals("1")) {
                // Sets the checkbox if the app is fullscreen.
                fullScreenCheckbox.setSelected(true);
            }
            // Retrieves the current theme of the window.
            String theme = UIManager.getLookAndFeel().toString();

            // Sets the currently selected item in the dropdown to the current theme.
            if (theme.equals(new FlatDarkLaf().toString())) {
                themeComboBox.setSelectedItem("Dark Mode");
            } else if (theme.equals(new FlatLightLaf().toString())) {
                themeComboBox.setSelectedItem("Light Mode");
            } else if (theme.equals(new FlatArcDarkOrangeIJTheme().toString())) {
                themeComboBox.setSelectedItem("Arc Orange");
            } else if (theme.equals(new FlatMonokaiProIJTheme().toString())) {
                themeComboBox.setSelectedItem("Monokai");
            } else {
                themeComboBox.setSelectedItem("High Contrast");
            }


        // Action listener for the apply button.
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Maximizes the screen if the box is checked.
                if(fullScreenCheckbox.isSelected()){
                    appWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                // If the box is not checked, resets the size of the window.
                else {
                    appWindow.setSize(700, 500);
                    appWindow.setLocationRelativeTo(null);
                    appWindow.setVisible(false);
                    settingsWindow.setVisible(false);
                }
                // Changes the theme based on the selected item in the dropdown menu.
                try {
                    if (Objects.equals(themeComboBox.getSelectedItem(), "Dark Mode")) {
                            FlatDarkLaf.setup();
                            UIManager.setLookAndFeel(new FlatDarkLaf());

                    } else if (Objects.equals(themeComboBox.getSelectedItem(), "Light Mode")) {
                            FlatLightLaf.setup();
                            UIManager.setLookAndFeel(new FlatLightLaf());

                    } else if (Objects.equals(themeComboBox.getSelectedItem(), "Monokai")) {
                            FlatMonokaiProIJTheme.setup();
                            UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());

                    } else if (Objects.equals(themeComboBox.getSelectedItem(), "High Contrast")) {
                            FlatHighContrastIJTheme.setup();
                            UIManager.setLookAndFeel(new FlatHighContrastIJTheme());

                    } else if (Objects.equals(themeComboBox.getSelectedItem(), "Arc Orange")) {
                            FlatArcDarkOrangeIJTheme.setup();
                            UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());

                    }


                // Resets the content pane of the app window to the new theme.
                appWindow.setContentPane(new ipScreen(appWindow).getPanel());
                // Centers the window and makes it visible.
                appWindow.setLocationRelativeTo(null);
                appWindow.setVisible(true);
                } catch (Exception ignored){}
                // Sets the int to 1 if the checkbox is true and 0 if not.
                int fullScreenInt;
                if (fullScreenCheckbox.isSelected()){
                    fullScreenInt = 1;
                }
                else{
                    fullScreenInt = 0;
                }
                // Writes the currents settings to the file.
                writer.writeSettingsFile(fullScreenInt, UIManager.getLookAndFeel().toString());

                settingsWindow.dispose();

            }
        });

        // Action listener that closes the settings window.
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsWindow.dispose();
            }
        });
    }
    public JPanel getPanel(){
        return this.settingsPanel;
    }


}
