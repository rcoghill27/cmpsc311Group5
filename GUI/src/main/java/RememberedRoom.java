import java.io.Serial;
import java.io.Serializable;

public class RememberedRoom implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String displayName;
    private String ipAddress;
    private int port;
    public void setDisplayName(String name){this.displayName = name;}
    public void setIpAddress(String address){this.ipAddress = address;}
    public void setPort(String port){this.port = Integer.parseInt(port);}
    public String getDisplayName(){return this.displayName;}
    public String getIpAddress(){return this.ipAddress;}
    int getPort(){return this.port;}
    public String toString(){
        return ipAddress + ":" + port;
    }
}
