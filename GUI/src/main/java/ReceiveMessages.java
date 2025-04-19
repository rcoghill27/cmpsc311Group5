import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReceiveMessages implements Runnable{
    private final BufferedReader input;
    private final ArrayList<String> memberNameArray;
    private final ArrayList<String> messageArray;
    private final ArrayList<String> memberArray;
    public ReceiveMessages(BufferedReader input,ArrayList<String> messageArray, ArrayList<String> memberArray, ArrayList<String> memberNameArray){
        // Default constructor for the class.
        this.input = input;
        this.messageArray = messageArray;
        this.memberArray = memberArray;
        this.memberNameArray = memberNameArray;
    }
    public void run(){
        try{
            String serverMessage;
            // Loop to continuously read through messages received from the server.
            while((serverMessage = String.valueOf(input.readLine())) != null){
                // Splits the message at the designated separator.
                String[] messageSplit = serverMessage.split("&#");
                // Adds it to the messageArray to be displayed in the chat window.
                if(messageSplit[0].equals(": Message")){
                    messageArray.add(messageSplit[1]);
                }
                // Adds it the memberNameArray and to memberArray to trigger list update.
                else if(messageSplit[0].equals(": DisplayName")){

                    boolean nameFound = false;
                    for (String name : memberNameArray){
                        if (name.equals(messageSplit[1])){
                            nameFound = true;
                        }
                    }
                    if (!nameFound){
                        memberArray.add(messageSplit[1]);
                        memberNameArray.add(messageSplit[1]);
                    }

                }
                // Removes the name from the member list and adds it memberArray to trigger list update.
                else if(messageSplit[0].equals(": RemoveMember")){
                    memberArray.add(messageSplit[1]);
                    memberNameArray.remove(messageSplit[1]);
                }
            }
        } catch (IOException ignored){
        }
    }
}

