package peer2;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*Client denotes a peer
 * */
public class Peer {
    private static Socket socket = null;

    private static int portFileOwner = 5000; // file owner  args[0]
    private static int peerAsServer = 5001;
    private static int peerAsClient = 5002;
    private static int portDownload = -1; // Server to download the file args[1]
    private static int portUpload = -1; // For uploading to server args[2]
    private static Map<Integer, ChunkStatus> mapIDList;  // to store ID list
    private static Map<String, String> mapFileMeta;
    private static String hostName = "localhost";

    static ConcurrentHashMap<Integer, ChunkStatus> peerList;
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
                    //break; // TODO remove this while dealing with chunk
                }
            }
            System.out.println("Requesting meta data of file");
            printWriter.println("GET_META_FILE");
            String ack = bufferedReader.readLine();
            if (ack.equals("OK")) {
                getMetaFile();
                //getIDList = true;
               // break; // TODO remove this while dealing with chunk
            }

            System.out.println("Requesting chunks from server");
            printWriter.println("GET_CHUNKS");
            ack = bufferedReader.readLine();
            if (ack.equals("READY")) {
                requestChunks();
                break; // TODO remove this while dealing with chunk
            }


            //TODO Should we close the connection with the file owner??

        }
        // Create two threads

        //Downloading thread (Client)
        PeerAsClient peerAsClient = new PeerAsClient(5001, peerList);
        Thread downloadingThread = new Thread(peerAsClient);
        downloadingThread.start();

        //Uploading Thread (Server)
        PeerAsServer peerAsServer = new PeerAsServer();
        peerAsServer.main(5002, 1,peerList);


        while (true) {

        }


    }

    private static void requestChunks() throws IOException {  //TODO edge case where you end here
        for (Map.Entry<Integer, ChunkStatus> m : mapIDList.entrySet()) {
            if(!m.getValue().received){
                System.out.println("Requesting chunk ["+ m.getKey()+"] from fileOwner");
                printWriter.println("CHUNK:"+m.getKey());

                String dir = new File(".").getCanonicalPath();
                String fileName = m.getKey() + ".bin";
                File fileDownload = new File(dir + "\\src\\db\\" + fileName);  // TODO remove this hardcode
                byte[] uploadData = new byte[m.getValue().size];
                InputStream is = socket.getInputStream();
                is.read(uploadData);
                FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                fileOutputStream.write(uploadData);
                fileOutputStream.flush();
                fileOutputStream.close();
                m.getValue().received = true;
                System.out.println("Received chunk ["+ m.getKey()+"] from fileOwner");

            }
        }
        printWriter.println("CLOSE");
        peerList = new ConcurrentHashMap<>(mapIDList);
        printWriter.flush();
    }

    private static void getMetaFile() throws IOException, ClassNotFoundException {
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object temp = objectInputStream.readObject();
        mapFileMeta = (Map) temp;
        System.out.println("Received meta file from file Owner");
        for (Map.Entry<String, String> m : mapFileMeta.entrySet()) {
            System.out.println(m.getKey() + "->" + m.getValue());
        }
        //System.out.println("Received meta file from the server ");  // TODO remove nchunks
    }

    private static void getIDListFromOwner() throws IOException, ClassNotFoundException {
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object temp = objectInputStream.readObject();
        mapIDList = (Map) temp;
        System.out.println("Received all the IDs from file Owner");
        for (Map.Entry<Integer, ChunkStatus> m : mapIDList.entrySet()) {
            System.out.println(m.getKey() + "->" + m.getValue());
        }
    }
}
