package v1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class PeerAsServer implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int clientNo = 1;
    private int ownPort = -1;

    ConcurrentHashMap<Integer,ChunkStatus> peerList;
    public PeerAsServer(){

    }
    public PeerAsServer(Socket socket,int no,ConcurrentHashMap<Integer,ChunkStatus> peerList) throws IOException{
        this.socket = socket;
        this.peerList = peerList;
        this.clientNo = no;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void main(int ownPort, int clientNo, ConcurrentHashMap<Integer,ChunkStatus> peerList) throws IOException {
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
        System.out.println("Peer neighbour no " + clientNo + " is connected");
        try {
            //Send ID list to the server
            while (true) {
                String input = null;
                input = bufferedReader.readLine();
                switch (input) {
                    case "GET_ID_LIST":
                        try {
                            printWriter.println("OK");
                            printWriter.flush(); // Send ack
                            System.out.println("Sending ID list to peer");
                            this.sendIDList();
                        } catch (IOException e) {
                            System.out.println("Error while sending file list");
                        }
                        break;

                    case "GET_CHUNKS":
                        try {
                            printWriter.println("READY");
                            System.out.println("Server [READY] to send chunks");
                            printWriter.flush();
                            this.sendChunks();
                        }catch (Exception e){
                            System.out.println("Error while sending data");
                        }
                        break;
                    // TODO
                }


            }
        } catch (Exception e) {
            System.out.println("Peer" + clientNo + " Disconnected");
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
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
            String dir = new java.io.File(".").getCanonicalPath();
            File fileUpload = new File(dir + "\\src\\" + input[1] + "." + "bin"); // TODO change to db
            byte[] byteData = new byte[(int) fileUpload.length()];
            FileInputStream fileInputStream = new FileInputStream(fileUpload);
            fileInputStream.read(byteData);
            OutputStream os = this.socket.getOutputStream();
            os.write(byteData);
            os.flush();
            System.out.println("Sent Chunk:["+input[1]+"] to peer"+clientNo);

        }

    }
    private void sendIDList() throws IOException {
        try {
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(peerList);
            objectOutputStream.flush();
            System.out.println("ID List shared with peer" + clientNo);

        } catch (NullPointerException | FileNotFoundException exception) {
            System.out.println(exception);
        }
    }

}
