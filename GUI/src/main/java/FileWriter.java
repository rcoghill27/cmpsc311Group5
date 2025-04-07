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
        ArrayList<RememberedRoom> roomList = new ArrayList<>();
        try{

            File inputFile = new File("rememberedRooms.txt");
            FileInputStream inputStream = new FileInputStream(inputFile);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            boolean running = true;
            while(running){
                RememberedRoom room = (RememberedRoom) objectStream.readObject();
                System.out.println("Read room in loop: " + room);
                if (room != null) {
                    roomList.add(room);
                }
                else{
                    running = false;
                }
            }
            System.out.println("Room List:" + roomList);
            return roomList;
        } catch (EOFException ignored) {

        } catch (Exception ex){
            System.out.println("It broke");
        }
        return roomList;
    }
}
