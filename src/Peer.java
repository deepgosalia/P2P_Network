import java.io.*;
import java.net.Socket;
import java.util.Map;

/*Client denotes a peer
 * */
public class Peer {
    private static Socket socket = null;

    private static int portFileOwner = 8000; // file owner  args[0]
    private static int portDownload = -1; // Server to download the file args[1]
    private static int portUpload = -1; // For uploading to server args[2]
    private static Map<Integer, Integer> map;  // to store ID list
    private static String hostName = "localhost";

    private static PrintWriter printWriter = null;
    private static BufferedReader bufferedReader = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Get correct portNumber  ask again if it is wrong
        while (true) {
            // Connect to FileOwner
            try {
                //portFileOwner = Integer.parseInt(args[0]);
                socket = new Socket(hostName, portFileOwner);
                printWriter = new PrintWriter(socket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println("Peer is connected to the file owner");
                break;
            } catch (Exception e) {
                System.out.println("Retrying to establish connection with file Owner");
                //break;  //TODO add bufferedReader to get another portnumber from the user
            }

        }

        boolean getIDList = false;
        boolean getChunk = false;
        while (true) {
            if (!getIDList) {
                System.out.println("Requesting ID list from the file owner");
                printWriter.println("GET_ID_LIST");
                String ack = bufferedReader.readLine();
                if (ack.equals("OK")) {
                    getIDListFromOwner();
                    getIDList = true;
                    break; // TODO remove this while dealing with chunk
                }
            }
            if (!getChunk) {

            }

            //TODO Should we close the connection with the file owner??

        }
        // Create two threads

        //Downloading thread (Client)
        PeerAsClient peerAsClient = new PeerAsClient(5001);
        Thread downloadingThread = new Thread(peerAsClient);
        downloadingThread.start();

        //Uploading Thread (Server)
        PeerAsServer peerAsServer = new PeerAsServer();
        peerAsServer.main(5002,1);


        while (true) {

        }


    }

    private static void getIDListFromOwner() throws IOException, ClassNotFoundException {
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object temp = objectInputStream.readObject();
        Map<Integer, Integer> map = (Map) temp;
        System.out.println("Received all the IDs from file Owner");
        for (Map.Entry<Integer, Integer> m : map.entrySet()) {
            System.out.println(m.getKey() + "->" + m.getValue());
        }
    }
}
