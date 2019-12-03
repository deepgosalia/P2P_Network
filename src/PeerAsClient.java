import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class PeerAsClient implements Runnable{
    private PrintWriter printWriter = null;
    private BufferedReader bufferedReader = null;
    private int serverPortNumber = -1;
    private String hostName = "localHost";
    private Socket socket;
    private int ownID = 1;
    private ConcurrentHashMap<Integer,ChunkStatus> map;
    public PeerAsClient(int serverPortNumber,ConcurrentHashMap<Integer,ChunkStatus> map) throws IOException {
        this.serverPortNumber = serverPortNumber;
        this.map = map;
    }


    @Override
    public void run() {
        // Establish connection with the Server
        while (true) {
            // Connect to Uploading Neighbour
            try {
                socket = new Socket(hostName, serverPortNumber);
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Peer" +ownID+" is connected to the uploading neighbour");
                break;
            } catch (Exception e) {
                System.out.println("Retrying to establish connection with uploading neighbour");
                //break;  //TODO add bufferedReader to get another portnumber from the user
            }
        }

        // request for chunks and save it
        while (true){
            // get id list
            
            compareList();
            // compare

            // request missing

        }
    }
}
