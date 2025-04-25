import java.io.Serial;
import java.io.Serializable;

// Class used to store past rooms.
public class RememberedRoom implements Serializable {
    // Makes object serializable to store in the file.
    @Serial
    private static final long serialVersionUID = 1L;
    private String displayName;
    private String ipAddress;
    private int port;

    // Setters for fields.
    public void setDisplayName(String name){this.displayName = name;}
    public void setIpAddress(String address){this.ipAddress = address;}
    public void setPort(String port){this.port = Integer.parseInt(port);}

    // Getters for fields.
    public String getDisplayName(){return this.displayName;}
    public String getIpAddress(){return this.ipAddress;}
    int getPort(){return this.port;}

    // toString method.
    public String toString(){
        return ipAddress + ":" + port;
    }
}
