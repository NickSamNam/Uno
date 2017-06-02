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
        try {
            this.socket = socket;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    List<Point> accessiblePoints = (List<Point>) objectInputStream.readObject();
                    List<Point> sources = (List<Point>) objectInputStream.readObject();
                    Point target = (Point) objectInputStream.readObject();

                    float startTime = System.nanoTime();
                    BreadthFirstSearch bfs = new BreadthFirstSearch(accessiblePoints, target);
                    Map<Point, List<Point>> paths = new HashMap<>(sources.size());
                    for (Point source : sources) {
                        paths.put(source, bfs.findPath(source));
                    }
                    float endTime = System.nanoTime();

                    dataOutputStream.writeFloat((endTime - startTime) * 1000000000);
                    objectOutputStream.writeObject(paths);
                }
            } catch (EOFException | SocketException e) {
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