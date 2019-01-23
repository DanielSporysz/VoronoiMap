package terrain;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class TerrainObject {
    private TerrainPoint position;
    private boolean isInhibitiant = false;
    private int inhibitiants = 0;
    private String type;
    private Image image;

    private Map<String, Byte> byteMap = new HashMap<>();
    private Map<String, Short> shortMap = new HashMap<>();
    private Map<String, Integer> integerMap = new HashMap<>();
    private Map<String, Long> longMap = new HashMap<>();
    private Map<String, Float> floatMap = new HashMap<>();
    private Map<String, Double> doubleMap = new HashMap<>();
    private Map<String, Boolean> booleanMap = new HashMap<>();
    private Map<String, Character> characterMap = new HashMap<>();
    private Map<String, String> stringMap = new HashMap<>();

    public TerrainObject(String type, TerrainPoint position) {
        this.type = type;
        this.position = position;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isInhibitiant() {
        return isInhibitiant;
    }

    public void setIsInhibitiant(boolean inhibitiant) {
        isInhibitiant = inhibitiant;
    }

    public int getInhibitiants() {
        return inhibitiants;
    }

    public void setInhibitiants(int inhibitiants) {
        this.inhibitiants = inhibitiants;
    }

    public String getType() {
        return type;
    }

    public TerrainPoint getPosition() {
        return position;
    }

    public void setPosition(TerrainPoint position) {
        this.position = position;
    }

    public Map<String, Byte> getByteMap() {
        return byteMap;
    }

    public Map<String, Short> getShortMap() {
        return shortMap;
    }

    public Map<String, Integer> getIntegerMap() {
        return integerMap;
    }

    public Map<String, Long> getLongMap() {
        return longMap;
    }

    public Map<String, Float> getFloatMap() {
        return floatMap;
    }

    public Map<String, Double> getDoubleMap() {
        return doubleMap;
    }

    public Map<String, Boolean> getBooleanMap() {
        return booleanMap;
    }

    public Map<String, Character> getCharacterMap() {
        return characterMap;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }
}
