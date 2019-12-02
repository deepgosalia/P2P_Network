import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerAsServer implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private int clientNo = 1;
    private int ownPort = -1;

    public PeerAsServer(){

    }
    public PeerAsServer(Socket socket,int no) throws IOException{
        this.socket = socket;
        this.clientNo = no;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream(), true);
    }

    public void main(int ownPort,int clientNo) throws IOException {
        ServerSocket serverSocket = new ServerSocket(ownPort);
        while (true){
            socket = serverSocket.accept();
            PeerAsServer peerAsServer = new PeerAsServer(socket,clientNo);
            Thread runnableThread = new Thread(peerAsServer);
            runnableThread.start();
        }

    }
    @Override
    public void run() {
        System.out.println("Peer neighbour no " + clientNo + " is connected");
        while (true){

        }
    }
}
