import java.io.*;
import java.util.ArrayList;

public class FileWriter {
    public void writeRememberedRoomFile(ArrayList<RememberedRoom> roomList) {
        try {
            File file = new File("rememberedRooms.txt");
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            for (RememberedRoom room : roomList) {
                objectStream.writeObject(room);
            }
            fileStream.close();
            objectStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<RememberedRoom> readRememberedRoomFile(){
        try{
            File inputFile = new File("rememberedRooms.txt");
            FileInputStream inputStream = new FileInputStream(inputFile);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            boolean running = true;
            ArrayList<RememberedRoom> roomList = new ArrayList<>();
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
