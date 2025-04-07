import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket {
    public Socket connectToServer(JFrame appWindow, String ipAddress, int port){
        boolean connecting = true;
        int connectionAttempts = 0;
        while(connecting){
            try(Socket serverSocket = new Socket()){
                InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
                serverSocket.connect(socketAddress, 5000);
                return serverSocket;
            } catch (IOException e){
                connectionAttempts += 1;
            }
            if (connectionAttempts == 5){
                connecting = false;
                JOptionPane.showMessageDialog(appWindow, "Could not connect to chat room.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    public void sendMessages(Socket serverSocket, String userMessage){
        boolean readingMessages = true;
        try {
            PrintWriter output = new PrintWriter(serverSocket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            Thread receiveThread = new Thread(new ReceiveMessages(input));
            receiveThread.start();
            String message;
            while (readingMessages) {
                message = userMessage;
                if (message.equals("exit")){
                    readingMessages = false;
                }
                output.println(message);
            }
        }catch (Exception ignored){

        }
    }
}
class ReceiveMessages implements Runnable{
    private BufferedReader input;
    public ReceiveMessages(BufferedReader input){
        this.input = input;
    }
    public void run(){
        try{
            String serverMessage;
            while((serverMessage = String.valueOf(input.readLine())) != null){
                System.out.println(serverMessage);
            }
        } catch (IOException e){
            System.out.println("Error receiving message.");
        }
    }
}
