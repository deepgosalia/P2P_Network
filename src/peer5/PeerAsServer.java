package peer5;

import fileOwner.ChunkObj;
import fileOwner.ChunkStatus;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class PeerAsServer implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int clientNo = 1;
    private int ownPort = -1;


    ConcurrentHashMap<Integer, ChunkStatus> peerList;
    public PeerAsServer(){

    }
    public PeerAsServer(Socket socket,int no,ConcurrentHashMap<Integer, ChunkStatus> peerList) throws IOException{
        this.socket = socket;
        this.peerList = peerList;
        this.clientNo = no;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void main(int ownPort, int clientNo, ConcurrentHashMap<Integer, ChunkStatus> peerList) throws IOException {
        ServerSocket serverSocket = new ServerSocket(ownPort);

        while (true){
            socket = serverSocket.accept();
            PeerAsServer peerAsServer = new PeerAsServer(socket,clientNo,peerList);
            Thread runnableThread = new Thread(peerAsServer);
            runnableThread.start();
        }

    }
    @Override
    public void run() {

         //5
        System.out.println("Peer neighbour no " + clientNo + " is connected");
        try {
            //Send ID list to the server
            while (true) {
                String input = null;
                input = bufferedReader.readLine();
                switch (input) {
                    case "GET_ID_LIST":
                        this.sendIDList();
                        System.out.println("Sending ID list to peer");
//                        try {
//                            printWriter.println("OK");
//                            printWriter.flush(); // Send ack
//                            //System.out.println("Sending ID list to peer");
//                            this.sendIDList();
//                        } catch (IOException e) {
//
//                            System.out.println("Error while sending file list");
//
//                        }

                        break;

                    case "GET_CHUNKS":
                        this.sendChunks();
//                        try {
//                            printWriter.println("READY");
//                            System.out.println("Server [READY] to send chunks");
//                            printWriter.flush();
//
//                        }catch (Exception e){
//                            System.out.println("Error while sending data");
//                        }

                        break;
                    // TODO
                }

//break;
            }
        } catch (Exception e) {
            System.out.println("Peer" + clientNo + " Disconnected");
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
                System.out.println("All requested chunks sent to peer"+clientNo);
                break;
            }

            String[] input = request.split(":"); // TODO what if the command is invalid
            String dir = new File(".").getCanonicalPath();



            String path = dir + "\\src\\peer5\\" + input[1] + "." + "bin";
            byte[] array = Files.readAllBytes(Paths.get(path));
            ChunkObj chunk = new ChunkObj(array);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(chunk);
            objectOutputStream.flush();






//
//            File fileUpload = new File(dir + "\\src\\peer5\\" + input[1] + "." + "bin"); // TODO change to db
//            byte[] byteData = new byte[(int) fileUpload.length()];
//            FileInputStream fileInputStream = new FileInputStream(fileUpload);
//            fileInputStream.read(byteData,0,byteData.length);
//            OutputStream os = this.socket.getOutputStream();
//            os.write(byteData,0,byteData.length);
//            os.flush();
            System.out.println("Sent Chunk:["+input[1]+"] to peer"+clientNo);

        }

    }
    private void sendIDList() throws IOException {
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(peerList);
            objectOutputStream.flush();

            // System.out.println("ID List shared with peer" + clientNo);

        } catch (NullPointerException | FileNotFoundException exception) {
            //System.out.println(exception);
        }
    }

}
