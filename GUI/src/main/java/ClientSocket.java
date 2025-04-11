import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket {
    public Socket connectToServer(JFrame appWindow, String ipAddress, int port) {
        boolean connecting = true;
        int connectionAttempts = 0;
        while(connecting){
            try(Socket serverSocket = new Socket()){
                InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
                serverSocket.connect(socketAddress);
                System.out.println("Is closed: " + serverSocket.isClosed());
                //readMessages(serverSocket);

                return serverSocket;
            } catch (IOException e){
                connectionAttempts += 1;
                try {
                    Thread.sleep(5000);
                } catch (Exception ignored){

                }
                System.out.println("Failed to Connect: Retrying in 5 Seconds");
            }
            if (connectionAttempts == 5){
                connecting = false;
                JOptionPane.showMessageDialog(appWindow, "Could not connect to chat room.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    public void sendMessages(Socket serverSocket, String userMessage){
        try{
            PrintWriter output = new PrintWriter(serverSocket.getOutputStream());
            String message = userMessage;
            output.println(message);
            System.out.println(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void readMessages(Socket serverSocket){
        try {

            BufferedReader input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            Thread receiveThread = new Thread(new ReceiveMessages(input));
            receiveThread.start();
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
            e.printStackTrace();
        }
    }
}
