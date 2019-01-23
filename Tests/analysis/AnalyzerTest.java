package analysis;

import org.junit.jupiter.api.Test;
import terrain.TerrainContour;
import terrain.TerrainData;
import terrain.TerrainObject;
import terrain.TerrainPoint;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalyzerTest {

    private TerrainData data;

    public AnalyzerTest() {
        data = new TerrainData();

        ArrayList<TerrainPoint> points = new ArrayList<>();
        points.add(new TerrainPoint(-10, 10));
        points.add(new TerrainPoint(10, 10));
        points.add(new TerrainPoint(10, -10));
        points.add(new TerrainPoint(-10, -10));

        data.contour = new TerrainContour(points);
    }

    @Test
    void analyzeTerrain() {
        //given
        ArrayList<TerrainObject> points = new ArrayList<>();
        points.add(new TerrainObject("test", new TerrainPoint(-5, 0)));
        points.add(new TerrainObject("test", new TerrainPoint(5, 0)));
        data.majorObjects = points;

        //When
        Analyzer analyzer = new Analyzer();
        analyzer.analyzeTerrain(data);

        //Then
        assertEquals(2, analyzer.points.size());
        assertEquals(true, analyzer.isContourComplete(analyzer.points.get(0).edges));
        assertEquals(true, analyzer.isContourComplete(analyzer.points.get(1).edges));
        assertEquals(true, analyzer.pointIsInsideContour(analyzer.points.get(0).edges, analyzer.points.get(0)));
        assertEquals(false, analyzer.pointIsInsideContour(analyzer.points.get(0).edges, analyzer.points.get(1)));
        assertEquals(true, analyzer.pointIsInsideContour(analyzer.points.get(1).edges, analyzer.points.get(1)));
        assertEquals(false, analyzer.pointIsInsideContour(analyzer.points.get(1).edges, analyzer.points.get(0)));
    }

    @Test
    void isContourComplete() {
        //Given
        ArrayList<Edge> squareIncomplete = new ArrayList<>();
        ArrayList<Edge> squareComplete = new ArrayList<>();
        squareIncomplete.add(new Edge(new Point(-10, 10), new Point(10, 10)));
        squareIncomplete.add(new Edge(new Point(10, 10), new Point(10, -10)));
        squareIncomplete.add(new Edge(new Point(10, -10), new Point(-10, -10)));
        squareComplete.addAll(squareIncomplete);
        squareComplete.add(new Edge(new Point(-10, -10), new Point(-10, 10)));
        Analyzer analyzer = new Analyzer();

        //Then
        assertEquals(false, analyzer.isContourComplete(squareIncomplete));
        assertEquals(true, analyzer.isContourComplete(squareComplete));
    }

}