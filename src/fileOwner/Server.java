package fileOwner;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/*Server will handle multiple client requests
 * */
public class Server implements Runnable {
    public Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int no;
    public Map<Integer,Integer> idList;
    public MetaFile metaFile;
    public int chunkStart;
    public int chunkEnd;
    public static Map<Integer,Integer> ownerList;

    private Server(Socket socket, int no, int chunkStart, int chunkEnd, MetaFile mf, Map<Integer,Integer>ownerList) throws IOException {
        this.idList = ownerList;
        this.metaFile = mf;
        this.chunkEnd = chunkEnd;
        this.chunkStart = chunkStart;
        this.socket = socket;
        this.no = no;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 5000;
        int peerNo = 0;
        Splitter splitter = new Splitter();
        System.out.println("Dividing files into chunk");
        ownerList = new HashMap<>();
        MetaFile metaFile = splitter.main(ownerList);

        int[] chunkRange = getChunkRange(metaFile.nChunks);
        int minChunkSize = chunkRange[0];
        int remChunkSize = chunkRange[1];

        System.out.println("nChunks is " + metaFile.nChunks + " size is " + metaFile.size);
        System.out.println("Waiting for the client to connect...");
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // Create thread for every new connection
        Socket clientSocket;
        int nextRangeStart = 1;
        while (true) {
            int chunkStart;
            int chunkEnd;
            clientSocket = serverSocket.accept();
            peerNo++;
            if (peerNo == 5) { // TODO remove
                chunkStart = nextRangeStart;
                chunkEnd = (chunkStart - 1) + minChunkSize + remChunkSize;
                nextRangeStart = chunkEnd + 1;
            } else {
                chunkStart = nextRangeStart;  // 1
                chunkEnd = (chunkStart - 1) + minChunkSize; // 0 + 5
                nextRangeStart = chunkEnd + 1;
            }
            Server obj = new Server(clientSocket, peerNo, chunkStart, chunkEnd, metaFile,ownerList);
            Thread runnableThread = new Thread(obj);
            runnableThread.start();
        }
    }

    private static int[] getChunkRange(int n) {
        return new int[]{n / 5, n % 5};
    }

    @Override
    public void run() {
        System.out.println("Peer" + no + " is connected");
        System.out.println("Your chunk start is " + chunkStart + " end is " + chunkEnd);
        try {
            //Send ID list to the server
            while (true) {
                String input = null;

                input = bufferedReader.readLine();
                switch (input) {
                    case "GET_ID_LIST":
                        System.out.println("Sending ID list to peer");
                        this.sendIDList();
//                        try {
//                            printWriter.println("OK");
//                            printWriter.flush(); // Send ack
//                            System.out.println("Sending ID list to peer");
//                            this.sendIDList();
//                        } catch (IOException e) {
//                            System.out.println("Error while sending file list");
//                        }
                        break;
                    case "GET_META_FILE":
                        this.sendMetaFile();
//                        try {
//                            printWriter.println("OK");
//                            printWriter.flush();
//                            this.sendMetaFile();
//                            //this.sendIDList();
//                        }catch (Exception e){
//                            System.out.println("Error while sending data");
//                        }
                        break;
                    case "GET_CHUNKS":
                        System.out.println("Server [READY] to send chunks");
                        this.sendChunks();
//                        try {
//                            printWriter.println("READY");
//                            System.out.println("Server [READY] to send chunks");
//                            printWriter.flush();
//                            this.sendChunks();
//                        }catch (Exception e){
//                            System.out.println("Error while sending data");
//                        }
                        break;
                    // TODO
                }


            }
        } catch (Exception e) {
            System.out.println("Peer" + no + " Disconnected");
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            printWriter.close();
        }


    }

    private void sendChunks() throws IOException {
        String status = "OPEN";
        while (true){
            String request = bufferedReader.readLine();
            if (request.equals("CLOSE")){
                System.out.println("All requested chunks sent to peer"+no);
                break;
            }

            String[] input = request.split(":"); // TODO what if the command is invalid
            String dir = new File(".").getCanonicalPath();
           // String path = dir + "\\src\\fileOwner\\" + input[1] + "." + "bin";
            File fileUpload = new File(dir + "\\src\\fileOwner\\" + input[1] + "." + "bin");


<<<<<<< HEAD
            String path = dir + "\\src\\fileOwner\\" + input[1] + "." + "bin";
            byte[] array = Files.readAllBytes(Paths.get(path));
            ChunkObj chunk = new ChunkObj(array);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(chunk);
            objectOutputStream.flush();
=======
//            String path = dir + "\\src\\fileOwner\\" + input[1] + "." + "bin";
//            byte[] array = Files.readAllBytes(Paths.get(path));
//            ChunkObj chunk = new ChunkObj(array);
//            OutputStream outputStream = socket.getOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//            objectOutputStream.writeObject(chunk);
//            objectOutputStream.flush();
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38




<<<<<<< HEAD
//            byte[] byteData = new byte[(int) fileUpload.length()];
//            FileInputStream fileInputStream = new FileInputStream(fileUpload);
//            fileInputStream.read(byteData,0,byteData.length);
//            OutputStream os = this.socket.getOutputStream();
//            os.write(byteData,0,byteData.length);
//            os.flush();
=======
            byte[] byteData = new byte[(int) fileUpload.length()];
            FileInputStream fileInputStream = new FileInputStream(fileUpload);
            fileInputStream.read(byteData,0,byteData.length);
            OutputStream os = this.socket.getOutputStream();
            os.write(byteData,0,byteData.length);

            os.flush();
>>>>>>> 9f37baaaa59b986b849e7c4427d224c4612afa38
            System.out.println("Sent Chunk:["+input[1]+"] to peer"+no);

        }
    }

    private void sendMetaFile() throws IOException {
        try {


            Map<String, String> metaData = new HashMap<>();
            metaData.put("FILE_NAME",metaFile.fileName);
            metaData.put("FILE_EXT",metaFile.fileExtension);
            metaData.put("FILE_CHUNKS",Integer.toString(metaFile.nChunks));
            metaData.put("FILE_SIZE",Integer.toString(metaFile.size));


            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(metaData);
            objectOutputStream.flush();
            System.out.println("Meta file sent to peer" + no);

        } catch (NullPointerException | FileNotFoundException exception) {
            //System.out.println(exception);
        }
    }

    private void sendIDList() throws IOException {
        try {

            Map<Integer, ChunkStatus> map = new HashMap<>();
            for (int i = chunkStart; i <= chunkEnd; i++) {
                map.put(i, new ChunkStatus(idList.get(i),false));  // mark true for the chunk that the peer will receive TODO check
            }
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(map);
            objectOutputStream.flush();
            System.out.println("ID List sent to peer" + no);

        } catch (NullPointerException | FileNotFoundException exception) {
           // System.out.println(exception);
        }
    }
}
