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
    DataInputStream inputStream;
    DataOutputStream outputStream;

    public ServerTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    System.out.println(socket + "\t" + inputStream.readUTF());
                } catch (EOFException | SocketException e) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
                System.out.println("Client disconnected: " + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
