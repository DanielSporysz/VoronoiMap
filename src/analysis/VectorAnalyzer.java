package analysis;

public class VectorAnalyzer {

    public static double getAngleBetween(Point v1, Point v2) {
        double angle1 = getAngleToHorizon(v1);
        double angle2 = getAngleToHorizon(v2);

        if (Math.abs(angle1 - angle2) > 180) {
            return 360 - Math.abs(angle1 - angle2);
        } else {
            return Math.abs(angle1 - angle2);
        }
    }

    public static double getAngleToHorizon(Point vector) {
        double angle = 180.0 / Math.PI * Math.atan2(vector.y, vector.x);

        if (angle < 0) {
            angle = 360 + angle;
        }

        return angle;
    }

}
