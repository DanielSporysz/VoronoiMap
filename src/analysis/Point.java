package analysis;

import java.util.ArrayList;

public class Point implements Comparable<Point> {

    public double x;
    public double y;

    public ArrayList<Edge> edges;
    public ArrayList<HalfEdge> halfEdges;
    public ArrayList<OpenEdge> openEdges;

    //Used when comparing Points
    private final double THRESHOLD = .00001;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;

        halfEdges = new ArrayList<>();
        edges = new ArrayList<>();
        openEdges = new ArrayList<>();
    }

    @Override
    public int compareTo(Point that) {
        if (Math.abs(this.x - that.x) < THRESHOLD && Math.abs(this.y - that.y) < THRESHOLD) {
            return 0;
        } else {
            if (Math.abs(this.y - that.y) < THRESHOLD) {
                return that.x > this.x ? -1 : 1;
            } else {
                return that.y > this.y ? 1 : -1;
            }
        }
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            Point point = (Point) o;
            return Double.compare(point.x, x) == 0 &&
                    Double.compare(point.y, y) == 0;
        }
    }

    public double getDistanceTo(Point target) {
        return Math.sqrt((this.x - target.x) * (this.x - target.x) + (this.y - target.y) * (this.y - target.y));
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void addHalfEdge(HalfEdge hf) {
        halfEdges.add(hf);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public void addOpenEdge(OpenEdge oe) {
        openEdges.add(oe);
    }

}