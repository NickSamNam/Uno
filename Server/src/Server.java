import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by snick on 8-5-2017.
 */
public class Server {
    public static void main(String[] args) {
        new Server();
    }

    public final static String HOSTNAME = "192.168.1.24";
    public final static int PORT = 8080;
    ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName(HOSTNAME));
            System.out.println("Server running: " + serverSocket.toString());
            while (true) {
                connectClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClient() {
        try {
            Socket socket = serverSocket.accept();
            System.out.println("\nClient connected: " + socket.toString());
            new Thread(new ServerTask(socket)).start();
        } catch (IOException e) {
            System.out.println("Can't connect client: " +  e.getMessage());
        }
    }
}
