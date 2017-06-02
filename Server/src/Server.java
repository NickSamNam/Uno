import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by snick on 8-5-2017.
 */
public class Server {
    public static void main(String[] args) {
        new Server();
    }

    public final static int PORT = 8080;
    ServerSocket serverSocket;

    public Server() {
        System.out.println("Setting up server");
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server running: " + serverSocket.toString());
            while (true) {
                connectClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClient() {
        System.out.println("\n\nWaiting for client");
        try {
            Socket socket = serverSocket.accept();
            System.out.println("\nClient connected: " + socket.toString());
            new Thread(new ServerTask(socket)).start();
        } catch (IOException e) {
            System.out.println("Can't connect client: " +  e.getMessage());
        }
    }
}
