import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket {
    public void connectToServer(String ipAddress, int port){
        boolean connecting = true;
        while(connecting){
            try(Socket serverSocket = new Socket()){
                InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
                serverSocket.connect();
            }
        }
    }
}
