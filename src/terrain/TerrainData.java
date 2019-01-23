package terrain;

import java.util.List;
import java.util.Map;

public class TerrainData {
    public TerrainContour contour;
    public List<TerrainObject> majorObjects;
    public Map<String, TerrainTemplate> templateObject;
    public List<TerrainObject> objects;

    public List<TerrainObject> getObjects() {
        return objects;
    }

    public void setObjects(List<TerrainObject> objects) {
        this.objects = objects;
    }

    public Map<String, TerrainTemplate> getTemplateObject() {
        return templateObject;
    }

    public void setTemplateObject(Map<String, TerrainTemplate> templateObject) {
        this.templateObject = templateObject;
    }

    public TerrainContour getContour() {
        return contour;
    }

    public void setContour(TerrainContour contour) {
        this.contour = contour;
    }

    public List<TerrainObject> getMajorObjects() {
        return majorObjects;
    }

    public void setMajorObjects(List<TerrainObject> majorObjects) {
        this.majorObjects = majorObjects;
    }
}
