public class RememberedRoom {
    String displayName;
    String ipAddress;
    int port;
    void setDisplayName(String name){this.displayName = name;}
    void setIpAddress(String address){this.ipAddress = address;}
    void setPort(String port){this.port = Integer.parseInt(port);}
    String getDisplayName(){return this.displayName;}
    String getIpAddress(){return this.ipAddress;}
    int getPort(){return this.port;}
}
