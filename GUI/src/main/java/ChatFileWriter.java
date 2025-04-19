import java.io.*;
import java.util.ArrayList;
// Class used to write to remembered rooms file and settings file.
public class ChatFileWriter {

    public void writeRememberedRoomFile(ArrayList<RememberedRoom> roomList) {
        try {
            // Creates a file from the file name
            File file = new File("rememberedRooms.txt");
            // Creates output streams for the file and objects.
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            // Iterates through array and writes each object to the file.
            for (RememberedRoom room : roomList) {
                objectStream.writeObject(room);
            }
            // Closes the streams
            fileStream.close();
            objectStream.close();
        } catch (Exception ignored) {
        }
    }
    // Reads from the remembered rooms file.
    public ArrayList<RememberedRoom> readRememberedRoomFile(){
        ArrayList<RememberedRoom> roomList = new ArrayList<>();

        try{
            // Crates the file and opens input streams.
            File inputFile = new File("rememberedRooms.txt");
            FileInputStream inputStream = new FileInputStream(inputFile);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);

            // Loops through the file and adds each object to the array.
            boolean running = true;
            while(running){
                RememberedRoom room = (RememberedRoom) objectStream.readObject();
                if (room != null) {
                    roomList.add(room);
                }
                else{
                    running = false;
                }
            }
            return roomList;
        } catch (Exception ignored) {
        }

        return roomList;
    }
    public void writeSettingsFile(int fullScreen, String theme){
        try {
            // Creates a file and file writer.
            File settingsFile = new File("settings.txt");
            FileWriter writer = new FileWriter(settingsFile);
            BufferedWriter buff = new BufferedWriter(writer);
            // Creates a single string with the theme and int for fullscreen split by a comma and writes it.
            buff.write(theme + "," + fullScreen);
            // Closes writers.
            buff.close();
            writer.close();
        } catch (Exception ignored){
        }

    }
    public String readSettingsFile(){
        try{
            // Reads the first line of the file which contains the settings.
            File settingsFile = new File("settings.txt");
            FileReader reader = new FileReader(settingsFile);
            BufferedReader buff = new BufferedReader(reader);
            return buff.readLine();
        } catch (Exception ignored){
        }
        return null;
    }
}
