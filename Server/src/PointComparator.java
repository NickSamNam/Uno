import java.awt.*;
import java.util.Comparator;

/**
 * Created by snick on 15-5-2017.
 */
public class PointComparator implements Comparator<Point> {
    @Override
    public int compare(Point point1, Point point2) {
        return (point1.x+point1.y)-(point2.x+point2.y);
    }
}
