package analysis;

import java.util.Objects;

public class HalfEdge extends StraightLine implements Comparable<HalfEdge> {

    public Point outset;
    public Point growthVector;

    //Points separated by this HalfEdge
    private Point point1;
    private Point point2;

    public HalfEdge(Point outset, Point point1, Point point2, Point awayFromPoint) {
        this.outset = outset;
        // Site is used in getting intersection when the straight is vertical
        this.site = outset;
        this.point1 = point1;
        this.point2 = point2;

        isVertical = (point1.y == point2.y) ? true : false;
        Point midPoint = new Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);

        if (isVertical) {
            m = b = 0;
        } else {
            m = -1.0 / ((point1.y - point2.y) / (point1.x - point2.x));
            b = midPoint.y - m * midPoint.x;
        }

        growthVector = calculateGrowthVector(awayFromPoint);
    }

    @Override
    public int compareTo(HalfEdge o) {
        if (o.point1.compareTo(point1) == 0 || o.point1.compareTo(point2) == 0) {
            if (o.point2.compareTo(point1) == 0 || o.point2.compareTo(point2) == 0) {
                return 0;
            }
        }
        return -1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outset, point1, point2);
    }

    private Point calculateGrowthVector(Point awayFromPoint) {
        Point vector = null;
        if (isVertical) {
            vector = new Point(0, 1);
        } else {
            // '100' is just any number that will move the point away from Outset
            double x = outset.x + 100;
            double y = x * m + b;
            vector = new Point(x - outset.x, y - outset.y);
        }

        Point to1st = new Point(point1.x - outset.x, point1.y - outset.y);
        Point to3rd = new Point(awayFromPoint.x - outset.x, awayFromPoint.y - outset.y);

        double angle1toVector = VectorAnalyzer.getAngleBetween(vector, to1st);
        double angle3toVector = VectorAnalyzer.getAngleBetween(vector, to3rd);

        if (angle1toVector < angle3toVector) {
            return vector;
        } else {
            vector.x = -vector.x;
            vector.y = -vector.y;
            return vector;
        }
    }

}
