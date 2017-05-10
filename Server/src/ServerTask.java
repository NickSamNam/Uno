import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by snick on 10-5-2017.
 */
public class ServerTask implements Runnable {
    private Socket socket;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public ServerTask(Socket socket) {
        try {
            this.socket = socket;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(socket + "\t" + inputStream.readUTF());
            } catch (EOFException | SocketException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    System.out.println("Client disconnected: " + socket.toString());
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    return;
                }
            }
        }
    }
}