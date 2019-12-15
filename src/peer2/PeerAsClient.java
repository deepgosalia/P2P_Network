package peer2;

import fileOwner.ChunkObj;
import fileOwner.ChunkStatus;
import fileOwner.MetaFile;
import peer1.FileJoin;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeerAsClient implements Runnable {
    private PrintWriter printWriter = null;
    private BufferedReader bufferedReader = null;
    private int serverPortNumber = -1;
    private String hostName = "localHost";
    private Socket socket;
    private int ownID = 1;
    int count = 0;
    private ConcurrentHashMap<Integer, ChunkStatus> peerList;

    private Map<String, String> mapFileMeta;
    public PeerAsClient(int serverPortNumber, ConcurrentHashMap<Integer, ChunkStatus> map, Map<String, String> mapFileMeta) throws IOException {
        this.serverPortNumber = serverPortNumber;
        this.peerList = map;
        this.mapFileMeta = mapFileMeta;
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
        boolean receivedAll = false;
        count = peerList.size();
        while (true) {
            if (count>=Integer.parseInt(mapFileMeta.get("FILE_CHUNKS")) && !receivedAll) {
                System.out.println("Received all chunks");
                receivedAll = true;
<<<<<<< HEAD

                try {
                    FileJoin.main("\\src\\peer2\\");
                } catch (IOException e) {
                    //e.printStackTrace();
=======
                try {
                    FileJoin f = new FileJoin(true);
                    f.main("\\src\\peer2\\");
                } catch (IOException e) {
//                    e.printStackTrace();
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38
                }
                //break;
            }
            if(!receivedAll){
            List<Integer> diff = null;
            System.out.println("Requesting ID list from the peer");
            printWriter.println("GET_ID_LIST");
            ConcurrentHashMap<Integer, ChunkStatus> list = null;
            try {
                list = getIDListFromPeer();
                diff = compareList(list);
            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
            }


<<<<<<< HEAD

=======
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38
//            String ack = null;
//            try {
//
//                ack = bufferedReader.readLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println(ack);
//            if (ack !=null && !ack.equals("ERR")) {
//                try {
//                    //System.out.println("Received ID list from peer");
//                    ConcurrentHashMap<Integer, ChunkStatus> list = getIDListFromPeer();
//                    diff = compareList(list);
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                //break; // TODO remove this while dealing with chunk
//            }

            if (diff != null) {
                System.out.println("Requesting Chunks from peer");
                printWriter.println("GET_CHUNKS");
//                try {
//                    ack = bufferedReader.readLine();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                try {
                    requestChunks(diff);
<<<<<<< HEAD
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
=======
                } catch (IOException e) {
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38
                    //e.printStackTrace();
                }
//                if (!ack.equals("NOT_READY")) {
//
//                    //break; // TODO remove this while dealing with chunk
//                }
            } else {
                //System.out.println("ID List is same");
            }
//    break;
        }

        }
    }

    private void requestChunks(List<Integer> diff) throws IOException, ClassNotFoundException, InterruptedException {  //TODO edge case where you end here
        for (Integer m : diff) {
            if(!peerList.get(m).received){
                System.out.println("Requesting chunk ["+ m+"] from peer");
                printWriter.println("CHUNK:"+m);

                String dir = new File(".").getCanonicalPath();
                String fileName = m + ".bin";
                File fileDownload = new File(dir + "\\src\\peer2\\" + fileName);  // TODO remove this hardcode



                InputStream is = socket.getInputStream();
<<<<<<< HEAD
                ObjectInputStream objectInputStream = new ObjectInputStream(is);
                Object temp = objectInputStream.readObject();
                ChunkObj obj = (ChunkObj) temp;
                byte[] arr = obj.chunk;

                //Write file
                FileOutputStream os = new FileOutputStream(fileDownload);
                os.write(arr);
                os.flush();
                os.close();




//                byte[] uploadData = new byte[peerList.get(m).size];
//                InputStream is = socket.getInputStream();
//                is.read(uploadData,0,uploadData.length);
//                FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
//                fileOutputStream.write(uploadData,0,uploadData.length);
//                fileOutputStream.flush();
//                fileOutputStream.close();
=======
                is.read(uploadData,0,uploadData.length);
                FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                fileOutputStream.write(uploadData,0,uploadData.length);
                fileOutputStream.flush();
                fileOutputStream.close();
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38
                peerList.get(m).received = true;
                count++;
                System.out.println("Received chunk ["+ m+"] from Peer");

            }
        }
        printWriter.println("CLOSE");
        printWriter.flush();
//        System.out.println(bufferedReader.readLine());
//        System.exit(1);
    }
    private ConcurrentHashMap<Integer, ChunkStatus> getIDListFromPeer() throws IOException, ClassNotFoundException {
        List<Integer> list = new ArrayList<>();
        ConcurrentHashMap<Integer, ChunkStatus> tempMap;
        InputStream is = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(is);
        Object temp = objectInputStream.readObject();
        tempMap = (ConcurrentHashMap) temp;
        System.out.println("Received all the IDs from peer");

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
