package analysis;

public abstract class StraightLine {

    public double m, b;
    public boolean isVertical;

    // Used to calculations of intersection when the straight is vertical
    protected Point site;

    public Point getIntersectionPoint(StraightLine that) {
        if (that == null) {
            throw new IllegalArgumentException("Received null pointer at Edge. Cannot calculate interaction point.");
        }

        Double x, y;
        if (that.isVertical && this.isVertical) {
            return null;
        } else if (this.m == that.m && this.b != that.b && this.isVertical == that.isVertical) {
            return null; // no intersection
        } else if (this.isVertical) {
            x = site.x;
            y = that.m * x + that.b;
        } else if (that.isVertical) {
            x = that.site.x;
            y = this.m * x + this.b;
        } else {
            x = (that.b - this.b) / (this.m - that.m);
            y = m * x + b;
        }

        if (x.isNaN() || y.isNaN()) {
            return null;
        }
        return new Point(x, y);
    }
}
