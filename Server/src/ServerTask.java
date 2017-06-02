import java.awt.Point;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snick on 10-5-2017.
 */
public class ServerTask implements Runnable {
    private Socket socket;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    private DataOutputStream dataOutputStream = null;

    public ServerTask(Socket socket) {
        System.out.println("Opening streams");
        try {
            this.socket = socket;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Streams opened");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    System.out.println("\nAwaiting data reception");
                    List<Point> accessiblePoints = (List<Point>) objectInputStream.readObject();
                    List<Point> sources = (List<Point>) objectInputStream.readObject();
                    Point target = (Point) objectInputStream.readObject();
                    System.out.println("Data received");

                    System.out.println("Calculating path(s)");
                    float startTime = System.nanoTime();
                    BreadthFirstSearch bfs = new BreadthFirstSearch(accessiblePoints, target);
                    Map<Point, List<Point>> paths = new HashMap<>(sources.size());
                    for (Point source : sources) {
                        paths.put(source, bfs.findPath(source));
                    }
                    float endTime = System.nanoTime();
                    System.out.println("Path(s) calculated");

                    System.out.println("Sending data");
                    dataOutputStream.writeFloat((endTime - startTime) * 1000000000);
                    objectOutputStream.writeObject(paths);
                    System.out.println("Data sent");
                }
            } catch (EOFException | SocketException e) {
                e.printStackTrace();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    objectInputStream.close();
                    objectOutputStream.close();
                    dataOutputStream.close();
                    socket.close();
                    System.out.println("\nClient disconnected: " + socket.toString());
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