package terrain;

import javafx.util.Pair;

import java.util.ArrayList;

public class TerrainTemplate {
    private String name;
    private ArrayList<Pair<String, String>> variablesInside = new ArrayList<>();

    public TerrainTemplate(String name) {
        this.name = name;
    }

    public ArrayList<Pair<String, String>> getVariablesInside() {
        return variablesInside;
    }
}
