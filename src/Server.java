import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/*Server will handle multiple client requests
 * */
public class Server implements Runnable {
    public Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int no;

    private Server(Socket socket, int no) throws IOException {
        this.socket = socket;
        this.no = no;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) throws IOException {
        int portNumber = 8000;
        int peerNo = 0;
        System.out.println("Waiting for the client to connect...");
        ServerSocket serverSocket = new ServerSocket(portNumber);
        // Create thread for every new connection
        Socket clientSocket;
        while (true) {
            clientSocket = serverSocket.accept();
            peerNo++;
            Server obj = new Server(clientSocket, peerNo);
            Thread runnableThread = new Thread(obj);
            runnableThread.start();
        }
    }

    @Override
    public void run() {
        System.out.println("Peer" + no + " is connected");

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
                            this.sendIDList();
                        } catch (IOException e) {
                            System.out.println("Error while sending file list");
                        }
                        break;
                    case "GET_CHUNK":
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
                e.printStackTrace();
            }
            printWriter.close();
        }


    }

    private void sendIDList() throws IOException {
        try {

            Map<Integer, Integer> map = new HashMap<>();
            map.put(1, 100);
            map.put(2, 100);
            map.put(3, 12);

            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(map);
            objectOutputStream.flush();
            System.out.println("ID List sent to peer" + no);

        } catch (NullPointerException | FileNotFoundException exception) {
            System.out.println(exception);
        }
    }
}
