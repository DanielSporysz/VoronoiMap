package analysis;

public class OpenEdge extends StraightLine {
    //Points separated by this OpenEdge
    public Point point1;
    public Point point2;

    public OpenEdge(Point point1, Point point2) {
        // Site is used in getting intersection when the straight is vertical
        this.point1 = point1;
        this.point2 = point2;

        isVertical = (point1.y == point2.y) ? true : false;
        Point midPoint = new Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
        this.site = midPoint;

        if (isVertical) {
            m = b = 0;
        } else {
            m = -1.0 / ((point1.y - point2.y) / (point1.x - point2.x));
            b = midPoint.y - m * midPoint.x;
        }
    }
}
