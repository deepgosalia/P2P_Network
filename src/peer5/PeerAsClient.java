package peer5;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PeerAsClient implements Runnable {
    private PrintWriter printWriter = null;
    private BufferedReader bufferedReader = null;
    private int serverPortNumber = -1;
    private String hostName = "localHost";
    private Socket socket;
    private int ownID = 1;
    private ConcurrentHashMap<Integer, ChunkStatus> peerList;

    public PeerAsClient(int serverPortNumber, ConcurrentHashMap<Integer, ChunkStatus> map) throws IOException {
        this.serverPortNumber = serverPortNumber;
        this.peerList = map;
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
                System.out.println("Peer" + ownID + " is connected to the uploading neighbour");
                break;
            } catch (Exception e) {
                System.out.println("Retrying to establish connection with uploading neighbour");
                //break;  //TODO add bufferedReader to get another portnumber from the user
            }
        }

        // request for chunks and save it
        while (true) {
            List<Integer> diff = null;
            System.out.println("Requesting ID list from the file owner");
            printWriter.println("GET_ID_LIST");
            String ack = null;
            try {
                ack = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ack.equals("OK")) {
                try {
                    System.out.println("Received ID list from peer");
                    ConcurrentHashMap<Integer, ChunkStatus> list = getIDListFromPeer();
                    diff = compareList(list);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                //break; // TODO remove this while dealing with chunk
            }

            if (diff!=null){
                System.out.println("Requesting Chunks from peer");
                printWriter.println("GET_CHUNKS");
                try {
                    ack = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ack.equals("READY")) {
                    try {
                        requestChunks(diff);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break; // TODO remove this while dealing with chunk
                }
            }


        }
    }

    private void requestChunks(List<Integer> diff) throws IOException {  //TODO edge case where you end here
        for (Integer m : diff) {
            if(!peerList.get(m).received){
                System.out.println("Requesting chunk ["+ m+"] from fileOwner");
                printWriter.println("CHUNK:"+m);

                String dir = new File(".").getCanonicalPath();
                String fileName = m + ".bin";
                File fileDownload = new File(dir + "\\src\\db\\" + fileName);  // TODO remove this hardcode
                byte[] uploadData = new byte[peerList.get(m).size];
                InputStream is = socket.getInputStream();
                is.read(uploadData);
                FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                fileOutputStream.write(uploadData);
                fileOutputStream.flush();
                fileOutputStream.close();
                peerList.get(m).received = true;
                System.out.println("Received chunk ["+ m+"] from Peer");

            }
        }
        printWriter.println("CLOSE");
        printWriter.flush();
    }
    private ConcurrentHashMap<Integer, ChunkStatus> getIDListFromPeer() throws IOException, ClassNotFoundException {
        List<Integer> list = new ArrayList<>();
        ConcurrentHashMap<Integer, ChunkStatus> tempMap;
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object temp = objectInputStream.readObject();
        tempMap = (ConcurrentHashMap) temp;
        System.out.println("Received all the IDs from file Owner");

        return tempMap;
    }

    private List<Integer> compareList(ConcurrentHashMap<Integer, ChunkStatus> list) {
        List<Integer> diff = new ArrayList<>();
        for (ConcurrentHashMap.Entry<Integer, ChunkStatus> m : list.entrySet()) {
            int key = m.getKey();
            ChunkStatus value = m.getValue();
            if (!peerList.containsKey(key)) {
                diff.add(key);
                peerList.put(key, new ChunkStatus(value.size, false));
            }
        }
        return diff;
    }
}
