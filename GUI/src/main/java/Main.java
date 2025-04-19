import javax.swing.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme;


public class Main {
    // Creates a static int for the size of the screen.
    static String fullscreen = "0";

    public static void main(String[] args) {
        // Creates an object of ChatFileWriter to read from the settings file.
        ChatFileWriter writer = new ChatFileWriter();
        // Reads the settings file
        String settings = writer.readSettingsFile();
        // If the file is empty, defaults to dark theme.
        if (settings == null){
            FlatLaf.setup(new FlatDarkLaf());
            try{
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception ignored){}
        }
        // Sets the window to the previous theme and size if settings file is not empty.
        else{
            // Splits the string read from the settings file into theme and fullscreen int
            String[] splitSettings = settings.split(",");
            // Sets the static variable to the read string from the file.
            fullscreen = splitSettings[1];

            // If-else structure that checks the stored string for theme and checks it to each of the available themes.
            try {
                // Uses the FlatLaf Library to change the theme of the window.
                if (splitSettings[0].equals(new FlatDarkLaf().toString())) {
                    FlatDarkLaf.setup();
                    UIManager.setLookAndFeel(new FlatDarkLaf());

                } else if (splitSettings[0].equals(new FlatLightLaf().toString())) {
                    FlatLightLaf.setup();
                    UIManager.setLookAndFeel(new FlatLightLaf());

                } else if (splitSettings[0].equals(new FlatMonokaiProIJTheme().toString())) {
                    FlatMonokaiProIJTheme.setup();
                    UIManager.setLookAndFeel(new FlatMonokaiProIJTheme());

                } else if (splitSettings[0].equals(new FlatHighContrastIJTheme().toString())) {
                    FlatHighContrastIJTheme.setup();
                    UIManager.setLookAndFeel(new FlatHighContrastIJTheme());

                } else if (splitSettings[0].equals(new FlatArcDarkOrangeIJTheme().toString())) {
                    FlatArcDarkOrangeIJTheme.setup();
                    UIManager.setLookAndFeel(new FlatArcDarkOrangeIJTheme());

                }
            } catch (Exception ignored){
            }
        }

        // Creates the JFrame for the GUI and sets the default close operation.
        JFrame appWindow = new JFrame("Chat App");
        appWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creates an object of the ipScreen class and sets the JFrame content to the main panel.
        try {
            ipScreen ipScreen = new ipScreen(appWindow);
            appWindow.setContentPane(ipScreen.getPanel());
            appWindow.pack();

            // Checks the variable pulled from settings and maximizes the screen and sets it visible.
            if (fullscreen.equals("1")) {
                appWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                appWindow.setSize(700, 500);
            }
            // Centers the window in the middle of the screen.
            appWindow.setLocationRelativeTo(null);
            appWindow.setVisible(true);
        } catch (Exception ignored){
        }
    }
}
