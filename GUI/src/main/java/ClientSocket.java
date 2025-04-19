import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

// Classes used to send and receive messages.
public class ClientSocket {
    public void sendMessages(Socket serverSocket, String userMessage){
        try{
            // Creates an output stream to the server.
            PrintWriter output = new PrintWriter(serverSocket.getOutputStream(), true);
            // Sends the message to the server.
            output.println(userMessage);
        } catch (Exception ignored){
        }
    }
    public void readMessages(Socket serverSocket, ArrayList<String> messageArray, ArrayList<String> memberArray, ArrayList<String> memberNameArray){
        try {
            // Creates an input stream to the server.
            BufferedReader input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            // Creates and starts a new thread that creates a new ReceiveMessages object.
            Thread receiveThread = new Thread(new ReceiveMessages(input, messageArray, memberArray, memberNameArray));
            receiveThread.start();

        }catch (Exception ignored){
        }
    }
}

