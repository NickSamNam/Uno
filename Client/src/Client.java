import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by snick on 8-5-2017.
 */
public class Client {
    public static void main(String[] args) {
        new Client().sendTest();
    }

    public final static String HOSTNAME = "networkprogramming.aftersoft.nl";
    public final static int PORT = 8080;
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public Client() {
        try {
            socket = new Socket(HOSTNAME, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTest() {
        try {
            outputStream.writeUTF("test");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}