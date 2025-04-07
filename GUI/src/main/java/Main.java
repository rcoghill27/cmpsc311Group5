import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame appWindow = new JFrame("Chat Room");
        appWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ipScreen ipScreen = new ipScreen(appWindow);
        appWindow.setContentPane(ipScreen.getPanel());
        appWindow.pack();
        appWindow.setLocationRelativeTo(null);
        //appWindow.setSize(1000,700);
        appWindow.setVisible(true);
    }
}
