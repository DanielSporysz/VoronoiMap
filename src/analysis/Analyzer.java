package analysis;

import terrain.TerrainData;
import terrain.TerrainObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class Analyzer {

    public ArrayList<Edge> terrainContour;
    public ArrayList<Point> points;

    private TreeSet<Edge> allEdges;

    private final double THRESHOLD = .00001;

    public void analyzeTerrain(TerrainData data) {
        //Data check and preparation
        allEdges = new TreeSet<>();
        terrainContour = importTerrainContour(data);
        if (!isContourComplete(terrainContour)) {
            System.err.println("Contour is not complete.");
            return;
        }
        points = importPoints(data);
        rejectPointsOutsideTheContour();
        ArrayList<PointsSet> sets = generatePointsSets();

        //Analysis
        if (points.isEmpty()) { // CASE 0: No data to analyze
            return;
        } else if (points.size() == 1) { // CASE 1: There is just one point thus the whole terrainContour is its field
            Point point = points.get(0);
            for (Edge edge : terrainContour) {
                point.addEdge(edge);
            }
        } else if (sets.isEmpty()) { // CASE 2: There are just 2 points
            createOpenEdges();
            closeOpenEdges();
        } else { // CASE 3: Typical data set
            analiseSets(sets);
            finishHalfEdges();
            fillToTerrainContour();

            if (allEdges.isEmpty()) { // CASE 4: All points are on the same line
                createOpenEdges();
                closeOpenEdges();
            }
        }
        cutToTerrainContour();
        fixContoursIfMissingParts();
        rearrangeEdgesOfPoints();
    }

    public boolean pointIsInsideContour(ArrayList<Edge> terrainContour, Point point) {

        if (terrainContour == null || point == null) {
            throw new IllegalArgumentException("Recived null pointer at ArrayList<Edge> or at Point.");
        }

        Edge vectorToMiddle = new Edge(point, getMiddlePoint(terrainContour));

        for (Edge edge : terrainContour) {
            Point intersectionPoint = edge.getIntersectionPoint(vectorToMiddle);

            if (intersectionPoint != null && edge.contains(intersectionPoint) && vectorToMiddle.contains(intersectionPoint)) {
                return false;
            }
        }

        return true;
    }

    public List<TerrainObject> getObjectsFor(TerrainData data, int pointIndex) {
        List<TerrainObject> objects = new ArrayList<>();

        if (points.isEmpty() && pointIndex >= points.size()) {
            return objects;
        }

        Point majorPoint = points.get(pointIndex);

        for (TerrainObject terrainObj : data.objects) {
            Point objConverted = new Point(terrainObj.getPosition().x, terrainObj.getPosition().y);
            if (pointIsInsideContour(terrainContour, objConverted) && pointIsInsideContour(majorPoint.edges, objConverted)) {
                objects.add(terrainObj);
            }
        }

        return objects;
    }

    public Edge getClosestTerrainEdge(Point point) {
        if (terrainContour == null) {
            throw new IllegalStateException("ArrayList<Edge> terrainContour has not been initialized.");
        } else if (terrainContour.isEmpty()) {
            throw new IllegalStateException("ArrayList<Edge> terrainContour is empty.");
        }

        Edge closestEdge = null;
        double shortestDistance = Double.MAX_VALUE;

        for (Edge edge : terrainContour) {
            Point closestPoint = edge.getClosestPointTo(point);
            if (point.getDistanceTo(closestPoint) < shortestDistance) {
                closestEdge = edge;
                shortestDistance = point.getDistanceTo(closestPoint);
            }
        }

        return closestEdge;
    }

    private void cutToTerrainContour() {
        for (Point point : points) {
            point.edges.addAll(terrainContour);

            // Splitting edges if the intersect with each other
            ArrayList<Point> intersectionPoints = new ArrayList<>();
            for (Edge edge1 : point.edges) {
                for (Edge edge2 : point.edges) {
                    Point intersection = edge1.getIntersectionPoint(edge2);

                    if (intersection != null && edge1.contains(intersection)
                            && intersection.compareTo(edge1.start) != 0 && intersection.compareTo(edge1.end) != 0) {
                        intersectionPoints.add(intersection);
                    }
                }
            }

            while (!intersectionPoints.isEmpty()) {
                Point intersection = intersectionPoints.get(0);
                intersectionPoints.remove(intersection);

                for (Edge edge : point.edges) {
                    if (edge.contains(intersection) && intersection.compareTo(edge.start) != 0 && intersection.compareTo(edge.end) != 0) {
                        point.edges.remove(edge);
                        point.edges.add(new Edge(edge.start, intersection));
                        point.edges.add(new Edge(intersection, edge.end));
                        break;
                    }
                }
            }

            //Removing Edges that being/end outside the contour
            ArrayList<Edge> toDelete = new ArrayList<>();
            for (Edge edge : point.edges) {
                if (pointIsCovered(point.edges, edge.start, point) || pointIsCovered(point.edges, edge.end, point)) {
                    toDelete.add(edge);
                }
            }

            point.edges.removeAll(toDelete);
        }
    }

    private boolean pointIsCovered(ArrayList<Edge> terrainContour, Point point, Point middlePoint) {
        if (terrainContour == null || point == null || middlePoint == null) {
            throw new IllegalArgumentException("Received null pointer.");
        }

        Edge vectorToMiddle = new Edge(point, middlePoint);

        for (Edge edge : terrainContour) {
            Point intersectionPoint = edge.getIntersectionPoint(vectorToMiddle);

            if (intersectionPoint != null && edge.contains(intersectionPoint) && vectorToMiddle.contains(intersectionPoint)
                    && intersectionPoint.compareTo(edge.start) != 0 && intersectionPoint.compareTo(edge.end) != 0) {
                return true;
            }
        }

        return false;
    }

    private void createOpenEdges() {
        Collections.sort(points);
        for (int i = 0; i < points.size() - 1; i++) {
            points.get(i).addOpenEdge(new OpenEdge(points.get(i), points.get(i + 1)));
            points.get(i + 1).addOpenEdge(new OpenEdge(points.get(i), points.get(i + 1)));
        }
    }

    private void closeOpenEdges() {
        for (Point point : points) {
            for (OpenEdge openEdge : point.openEdges) {
                Point start = null;
                Point end = null;

                for (Edge contour : terrainContour) {
                    Point intersection = contour.getIntersectionPoint(openEdge);
                    if (intersection != null && contour.contains(intersection)) {
                        if (start == null) {
                            start = intersection;
                        } else {
                            end = intersection;
                            break;
                        }
                    }
                }

                if (start != null && end != null) {
                    point.edges.add(new Edge(start, end));
                } else {
                    System.err.println("Point " + point + " has got unclosed OpenEdge.");
                }
            }
        }
    }

    protected boolean isContourComplete(ArrayList<Edge> contour) {
        if (contour == null) {
            throw new IllegalArgumentException("Recived null pointer at ArrayList<Edge>.");
        }

        if (contour.isEmpty()) {
            return false;
        } else {
            //Every edge has to have 2 neighbours
            for (Edge currentEdge : contour) {
                int nextEdgesCount = 0;

                for (Edge nextEdge : contour) {
                    if (currentEdge.compareTo(nextEdge) != 0) {
                        if (nextEdge.end.compareTo(currentEdge.end) == 0 || nextEdge.end.compareTo(currentEdge.start) == 0
                                || nextEdge.start.compareTo(currentEdge.end) == 0 || nextEdge.start.compareTo(currentEdge.start) == 0) {
                            nextEdgesCount++;
                        }
                    }
                }

                if (nextEdgesCount != 2) {
                    return false;
                }
            }
        }

        return true;
    }

    private void rejectPointsOutsideTheContour() {
        if (terrainContour == null || points == null) {
            throw new IllegalStateException("Data containers has not been initialised.");
        }

        ArrayList<Point> toDelete = new ArrayList<>();

        for (Point point : points) {
            if (!pointIsInsideContour(terrainContour, point)) {
                toDelete.add(point);
            }
        }

        points.removeAll(toDelete);
    }

    private Point getMiddlePoint(ArrayList<Edge> contour) {
        if (contour == null) {
            throw new IllegalArgumentException("Recived null pointer at ArrayList<Edge>.");
        } else if (contour.size() < 3) {
            throw new IllegalArgumentException("Countor contains less than 3 edges. It is not a contour.");
        }

        double x = 0;
        double y = 0;
        int n = 0;

        for (Edge edge : contour) {
            x += edge.start.x;
            x += edge.end.x;
            y += edge.start.y;
            y += edge.end.y;

            n += 2;
        }

        x = x / n;
        y = y / n;

        return new Point(x, y);
    }

    private ArrayList<Edge> importTerrainContour(TerrainData data) {
        if (data == null) {
            throw new IllegalArgumentException("Recived null pointer at TerrainData.");
        }

        ArrayList<Edge> cnt = new ArrayList<>();

        for (int i = 0; i < data.contour.points.size() - 1; i++) {
            cnt.add(new Edge(data.contour.points.get(i), data.contour.points.get(i + 1)));
        }
        cnt.add(new Edge(data.contour.points.get(data.contour.points.size() - 1), data.contour.points.get(0)));

        return cnt;
    }

    private ArrayList<Point> importPoints(TerrainData data) {
        ArrayList<Point> points = new ArrayList<>();
        for (TerrainObject obj : data.majorObjects) {
            points.add(new Point(obj.getPosition().x, obj.getPosition().y));
        }

        return points;
    }

    private ArrayList<PointsSet> generatePointsSets() {
        ArrayList<PointsSet> sets = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    sets.add(new PointsSet(points.get(i), points.get(j), points.get(k)));
                }
            }
        }

        return sets;
    }

    private void analiseSets(ArrayList<PointsSet> sets) {
        if (sets == null) {
            throw new IllegalArgumentException("Recived null pointer at ArrayList<PointsSet>.");
        }

        for (PointsSet set : sets) {
            Point p1 = set.point1;
            Point p2 = set.point2;
            Point p3 = set.point3;
            Point circleCenter;

            double W = 4 * (p2.x - p1.x) * (p2.y - p3.y) - 4 * (p2.x - p3.x) * (p2.y - p1.y);

            double Wx = 2 * (p2.y - p3.y) * (Math.pow(p2.x, 2) - Math.pow(p1.x, 2) + Math.pow(p2.y, 2) - Math.pow(p1.y, 2))
                    - 2 * (p2.y - p1.y) * (Math.pow(p2.x, 2) - Math.pow(p3.x, 2) + Math.pow(p2.y, 2) - Math.pow(p3.y, 2));

            double Wy = 2 * (p2.x - p1.x) * (Math.pow(p2.x, 2) - Math.pow(p3.x, 2) + Math.pow(p2.y, 2) - Math.pow(p3.y, 2))
                    - 2 * (p2.x - p3.x) * (Math.pow(p2.x, 2) - Math.pow(p1.x, 2) + Math.pow(p2.y, 2) - Math.pow(p1.y, 2));

            if (W > THRESHOLD || W < -THRESHOLD) {
                double xs = Wx / W;
                double ys = Wy / W;
                circleCenter = new Point(xs, ys);

                double r = Math.sqrt(Math.pow(p1.x - xs, 2) + Math.pow(p1.y - ys, 2));
                boolean emptyCircle = true;

                for (Point point : points) {
                    if (point.compareTo(p1) != 0 && point.compareTo(p2) != 0 && point.compareTo(p3) != 0) {
                        if (Double.compare(point.getDistanceTo(circleCenter), r) < 0) {
                            emptyCircle = false;
                            break;
                        }
                    }
                }

                if (emptyCircle) {
                    HalfEdge edge1 = new HalfEdge(circleCenter, p1, p2, p3);
                    HalfEdge edge2 = new HalfEdge(circleCenter, p1, p3, p2);
                    HalfEdge edge3 = new HalfEdge(circleCenter, p3, p2, p1);
                    p1.addHalfEdge(edge1);
                    p1.addHalfEdge(edge2);
                    p2.addHalfEdge(edge1);
                    p2.addHalfEdge(edge3);
                    p3.addHalfEdge(edge2);
                    p3.addHalfEdge(edge3);
                }
            }
        }
    }

    private void finishHalfEdges() {
        ArrayList<HalfEdge> toDelete;

        for (Point point : points) {
            toDelete = new ArrayList<>();

            for (int i = 0; i < point.halfEdges.size() - 1; i++) {
                for (int j = i + 1; j < point.halfEdges.size(); j++) {
                    if (point.halfEdges.get(i).compareTo(point.halfEdges.get(j)) == 0) {
                        Edge edge = new Edge(point.halfEdges.get(i).outset, point.halfEdges.get(j).outset);
                        point.addEdge(edge);
                        toDelete.add(point.halfEdges.get(i));
                        toDelete.add(point.halfEdges.get(j));

                        allEdges.add(edge);
                        break;
                    }
                }
            }

            point.halfEdges.removeAll(toDelete);
        }
    }

    private void fillToTerrainContour() {
        for (Point point : points) {
            for (HalfEdge hf : point.halfEdges) {
                for (Edge terrainEdge : terrainContour) {
                    Point intersection = terrainEdge.getIntersectionPoint(hf);

                    if (intersection != null && terrainEdge.contains(intersection)) {
                        Point growthVector = new Point(intersection.x - hf.outset.x, intersection.y - hf.outset.y);
                        if (VectorAnalyzer.getAngleBetween(hf.growthVector, growthVector) < THRESHOLD) {
                            Edge newEdge = new Edge(hf.outset, intersection);
                            point.edges.add(newEdge);
                            allEdges.add(newEdge);
                        }
                    }
                }
            }
        }
    }

    private void fixContoursIfMissingParts() {
        for (Point point : points) {
            ArrayList<Point> openPoints = new ArrayList<>();
            ArrayList<Edge> edgesWithoutNeighbour = new ArrayList<>();

            //Finding open edges
            for (Edge edge : point.edges) {
                if (!hasContinuationFromPoint(edge, edge.start, point.edges)) {
                    openPoints.add(edge.start);
                    edgesWithoutNeighbour.add(edge);
                }
                if (!hasContinuationFromPoint(edge, edge.end, point.edges)) {
                    openPoints.add(edge.end);
                    edgesWithoutNeighbour.add(edge);
                }
            }

            //Finishing open edges
            while (!edgesWithoutNeighbour.isEmpty()) {
                Edge toComplete1 = edgesWithoutNeighbour.get(0);
                Point openPoint1 = openPoints.get(0);
                Edge toComplete2 = null;
                Point openPoint2 = new Point(Double.MAX_VALUE / 2, Double.MAX_VALUE / 2);

                for (int i = 0; i < edgesWithoutNeighbour.size(); i++) {
                    Edge tmpE = edgesWithoutNeighbour.get(i);
                    Point tmpP = openPoints.get(i);

                    if (tmpE.compareTo(toComplete1) != 0 && openPoint1.getDistanceTo(tmpP) < openPoint1.getDistanceTo(openPoint2)) {
                        toComplete2 = tmpE;
                        openPoint2 = tmpP;
                    }
                }

                point.edges.add(new Edge(openPoint1, openPoint2));

                edgesWithoutNeighbour.remove(toComplete1);
                edgesWithoutNeighbour.remove(toComplete2);
                openPoints.remove(openPoint1);
                openPoints.remove(openPoint2);
            }
        }
    }

    private boolean hasContinuationFromPoint(Edge edge, Point continuationPoint, ArrayList<Edge> edges) {
        if (edge == null || continuationPoint == null || edges == null) {
            throw new IllegalArgumentException("Recived null pointer.");
        } else if (edges.isEmpty()) {
            throw new IllegalArgumentException("Recived ArrayList<Edge> container is empty.");
        }


        for (Edge next : edges) {
            if (next.compareTo(edge) != 0 && (next.start.compareTo(continuationPoint) == 0 || next.end.compareTo(continuationPoint) == 0)) {
                return true;
            }
        }

        return false;
    }

    private void rearrangeEdgesOfPoints() {
        if (points == null) {
            throw new IllegalStateException("Points container has not been initialized.");
        }

        if (points.isEmpty()) {
            return;
        }

        for (Point point : points) {
            if (point.edges == null) {
                throw new IllegalStateException("Point " + point + " has uninitialised edges container.");
            } else if (point.edges.isEmpty()) {
                throw new IllegalStateException("Point " + point + " has got no edges.");
            }

            ArrayList<Edge> correctOrder = new ArrayList<>();

            Edge currentEdge = point.edges.get(0);
            Point connectionPoint = currentEdge.start;
            correctOrder.add(currentEdge);
            Point breakPoint = connectionPoint;

            do {
                for (Edge nextEdge : point.edges) {
                    if (nextEdge.compareTo(currentEdge) != 0
                            && (nextEdge.start.compareTo(connectionPoint) == 0 || nextEdge.end.compareTo(connectionPoint) == 0)) {

                        currentEdge = nextEdge;

                        if (currentEdge.start.compareTo(connectionPoint) == 0) {
                            correctOrder.add(new Edge(connectionPoint, currentEdge.end));
                            connectionPoint = currentEdge.end;
                        } else {
                            correctOrder.add(new Edge(connectionPoint, currentEdge.start));
                            connectionPoint = currentEdge.start;
                        }

                        break;
                    }
                }
            } while (correctOrder.size() != point.edges.size() && breakPoint.compareTo(connectionPoint) != 0);

            point.edges = correctOrder;
        }
    }
}