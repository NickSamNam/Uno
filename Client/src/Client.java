import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Created by snick on 8-5-2017.
 */
public class Client implements GUI.OnDataSubmissionListener, WindowListener {
    private GUI gui;
    public static void main(String[] args) {
        new Client();
    }

    public final static String HOSTNAME = "server.aftersoft.nl";
    public final static int PORT = 8080;
    private Socket socket;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private DataInputStream dataInputStream = null;

    public Client() {
        gui = new GUI(this, this);
        try {
            socket = new Socket(HOSTNAME, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            System.out.println("\nConnected to: " + socket);
            new Thread(new DataReceiver()).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTest() {
        try {
            String messageOut = "test";
            objectOutputStream.writeUTF(messageOut);
            objectOutputStream.flush();
            System.out.println("\nOut to: " + socket + "\n" + messageOut);

            String messageIn = objectInputStream.readUTF();
            System.out.println("\nIn from: " + socket + "\n" + messageIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendData(List<Point> accessiblePoints, List<Point> sources, Point target) {
        try {
            objectOutputStream.writeObject(accessiblePoints);
            objectOutputStream.writeObject(sources);
            objectOutputStream.writeObject(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            dataInputStream.close();
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
        } catch (IOException ioE) {
            ioE.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    private class DataReceiver implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    float calcTime = dataInputStream.readFloat();
                    Map<Point, List<Point>> paths = (Map<Point, List<Point>>) objectInputStream.readObject();
                    gui.processData(calcTime, paths);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}