import com.sun.istack.internal.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by snick on 19-3-2017.
 */
public class BreadthFirstSearch {

    private List<Point> accessiblePoints;
    private Vertex target;
    private Map<Point, Vertex> visited;

    /**
     * Maps the distance from each point in accessiblePoints to the target point.
     *
     * @param accessiblePoints list of points available for search.
     * @param target           point to navigate to
     * @throws NullPointerException Target not within scope.
     */
    public BreadthFirstSearch(@NotNull List<Point> accessiblePoints, @NotNull Point target)
            throws NullPointerException {
        this.accessiblePoints = accessiblePoints;
        this.target = new Vertex(target);
        visited = new ConcurrentHashMap<>(accessiblePoints.size());
        bfs();
    }

    public List<Point> findPath(Point source) {
        return searchShortestPath(source);
    }


    /**
     * @param sources list of sources from which to find the route to the target
     * @return map with source as key and list of points of route as value
     */
    public Map<Point, List<Point>> findPaths(List<Point> sources) {
        ExecutorService executor = Executors.newFixedThreadPool(sources.size());
        Map<Point, List<Point>> paths = new ConcurrentHashMap(sources.size());
        Collections.sort(sources, new PointComparator());

        for (Point source : sources) {
            executor.execute(new SearchTask(source, paths));
        }

        while (paths.size() != sources.size());
        executor.shutdown();
        return paths;
    }

    /**
     * @param source point to start at
     * @return List containing the points between source and target, excludes source, includes target.
     * @throws NullPointerException Source not within scope.
     */
    private List<Point> searchShortestPath(@NotNull Point source) throws NullPointerException {
        Vertex src = visited.get(source);
        List<Point> path = new ArrayList<>();
        while (src != target) {
            src = src.getClosestChild();
            if (src == null)
                break;
            path.add(src.location);
        }
        return path;
    }

    private void bfs() {
        target.distance = 0;
        visited.put(target.location, target);

        RecursiveTask<Vertex> mappingTask = new MappingTask(target);
        ForkJoinPool pool = new ForkJoinPool();
        target = pool.invoke(mappingTask);
    }

    private class MappingTask extends RecursiveTask<Vertex> {
        private Vertex currentVertex;

        public MappingTask(Vertex currentVertex) {
            this.currentVertex = currentVertex;
        }

        @Override
        public Vertex compute() {
            ArrayList<RecursiveTask<Vertex>> childrenTasks = new ArrayList<>();
            for (Vertex childVertex : currentVertex.getAdjacentVertices()) {
                if (!visited.containsValue(childVertex)) {
                    visited.put(childVertex.location, childVertex);
                    childVertex.setDistanceRelativeToTarget();
                    RecursiveTask<Vertex> childTask = new MappingTask(childVertex);
                    childrenTasks.add(childTask);
                    childTask.fork();
                } else {
                    currentVertex.addChild(visited.get(childVertex.location));
                }
//                System.out.println("Parent: " + currentVertex.location + "\tChild: " + childVertex.location);
            }
            for (RecursiveTask<Vertex> childTask : childrenTasks) {
                currentVertex.addChild(childTask.join());
            }
            return currentVertex;
        }
    }

    private class Vertex implements Comparable<Vertex> {

        private Point location;
        private List<Vertex> children;
        private int distance;

        /**
         * Create new Vertex
         *
         * @param location    location of the vertex
         */
        public Vertex(Point location) {
            this.location = location;
            children = new CopyOnWriteArrayList<>();
        }

        /**
         * @return List of vertices connected to this vertex.
         */
        public List<Vertex> getAdjacentVertices() {
            List<Vertex> adj = new ArrayList<>();

            Point up = new Point(location.x, location.y + 1);
            Point down = new Point(location.x, location.y - 1);
            Point left = new Point(location.x - 1, location.y);
            Point right = new Point(location.x + 1, location.y);

            if (accessiblePoints.contains(up)) {
                adj.add(new Vertex(up));
            }
            if (accessiblePoints.contains(down)) {
                adj.add(new Vertex(down));
            }
            if (accessiblePoints.contains(left)) {
                adj.add(new Vertex(left));
            }
            if (accessiblePoints.contains(right)) {
                adj.add(new Vertex(right));
            }

            return adj;
        }

        /**
         * Add a child vertex to this vertex if the child is not already contained
         * @param child vertex to be added as child
         * @return true if added, false if not
         */
        public boolean addChild(Vertex child) {
            if (!children.contains(child)) {
                children.add(child);
                return true;
            }
            return false;
        }

        public Vertex getClosestChild() {
            Collections.sort(children);
            return children.get(0);
        }

        @Override
        public int compareTo(Vertex v) {
            return distance-v.distance;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Vertex) {
                if (this.location.equals(((Vertex) obj).location)) {
                    return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return location.hashCode();
        }

        public Point getLocation() {
            return location;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public void setDistanceRelativeToTarget() {
            distance = Math.abs(target.location.x-location.x) + Math.abs(target.location.y-location.y);
        }

        @Override
        public String toString() {
            return "Vertex[x=" + location.x + ",y=" + location.y + ",d=" + distance + "]";
        }
    }

    private class SearchTask implements Runnable {
        private Point source;
        private Map<Point, List<Point>> collector;

        public SearchTask(Point source, Map<Point, List<Point>> collector) {
            this.source = source;
            this.collector = collector;
        }

        @Override
        public void run() {
            collector.put(source, searchShortestPath(source));
        }
    }
}
