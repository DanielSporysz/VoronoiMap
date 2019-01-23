package terrain;

import java.util.List;

public class TerrainContour {
    public List<TerrainPoint> points;

    public TerrainContour(List<TerrainPoint> points) {
        this.points = points;
    }

    public List<TerrainPoint> getPoints() {
        return points;
    }

    public void addContourPoint(int pos, TerrainPoint terrainPoint) {
        points.add(pos, terrainPoint);
    }
}
