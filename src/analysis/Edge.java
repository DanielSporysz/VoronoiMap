package analysis;

import terrain.TerrainPoint;

public class Edge extends StraightLine implements Comparable<Edge> {

    public Point start;
    public Point end;

    //Used when checking if the edge contains a point
    private final double THRESHOLD = .00001;

    public Edge(Point start, Point end) {
        this.start = start;
        this.end = end;
        this.site = start;

        isVertical = start.x == end.x;

        if (isVertical) {
            m = b = 0;
        } else {
            m = (end.y - start.y) / (end.x - start.x);
            b = start.y - start.x * m;
        }

    }

    public Edge(TerrainPoint start, TerrainPoint end) {
        this(new Point(start.x, start.y), new Point(end.x, end.y));
    }

    @Override
    public int compareTo(Edge o) {
        if (start.compareTo(o.start) == 0 && end.compareTo(o.end) == 0
                || end.compareTo(o.start) == 0 && start.compareTo(o.end) == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    public boolean contains(Point point) {
        if (isVertical && Math.abs(this.site.x - point.x) < THRESHOLD || Math.abs(point.x * m + b - point.y) < THRESHOLD) {
            if (point.x >= start.x && point.x <= end.x || point.x >= end.x && point.x <= start.x) {
                return point.y >= start.y && point.y <= end.y || point.y >= end.y && point.y <= start.y;
            }
        }
        return false;
    }

    public Point getClosestPointTo(Point point) {
        Double normalM;
        Double normalB;

        if (isVertical) {
            normalM = 0d;
            normalB = point.y;
        } else {
            normalM = -1 / this.m;
            normalB = point.y - point.x * normalM;
        }

        StraightLine normal = new StraightLine() {
        };
        normal.m = normalM;
        normal.b = normalB;
        if (normalM.isInfinite()) {
            normal.isVertical = true;
            normal.site = point;
        }

        Point intersection = this.getIntersectionPoint(normal);

        if (intersection != null && this.contains(intersection)) {
            if (point.getDistanceTo(start) < point.getDistanceTo(end)) {
                return point.getDistanceTo(start) < point.getDistanceTo(intersection) ? start : intersection;
            } else {
                return point.getDistanceTo(end) < point.getDistanceTo(intersection) ? end : intersection;
            }
        } else {
            return point.getDistanceTo(start) < point.getDistanceTo(end) ? start : end;
        }

    }

    public Point[] getPoints() {
        Point[] points = new Point[2];

        points[0] = start;
        points[1] = end;

        return points;
    }
}
