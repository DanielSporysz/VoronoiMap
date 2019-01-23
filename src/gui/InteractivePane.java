package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

public class InteractivePane extends Pane {

    private static DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    InteractivePane() {
        setPrefSize(0, 0);

        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    double getScale() {
        return myScale.get();
    }

    void setScale(double scale) {
        myScale.set(scale);
    }
}