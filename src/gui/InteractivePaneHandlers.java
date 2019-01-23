package gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.text.DecimalFormat;

class InteractivePaneHandlers {

    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .1d;

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    private Pair<Double, Double> clickCoord;
    private Text xyText;
    private InteractivePane pane;

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {
            if (event.isSecondaryButtonDown()) {
                mouseAnchorX = event.getSceneX();
                mouseAnchorY = event.getSceneY();

                translateAnchorX = pane.getTranslateX();
                translateAnchorY = pane.getTranslateY();
            } else if (event.isPrimaryButtonDown()) {
                double x = (event.getSceneX() / pane.getScale() - pane.getTranslateX() / pane.getScale());
                double y = (event.getSceneY() / pane.getScale() - pane.getTranslateY() / pane.getScale() - 53 / pane.getScale());
                DecimalFormat dc = new DecimalFormat("#.#");
                x = Double.parseDouble((dc.format(x)).replace(",", "."));
                y = Double.parseDouble((dc.format(y)).replace(",", "."));
                xyText.setText("x: " + x + " y: " + y);
                clickCoord = new Pair<>(x, y);
            }

            event.consume();
        }

    };
    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            if (!event.isSecondaryButtonDown())
                return;

            pane.setTranslateX(translateAnchorX + event.getSceneX() - mouseAnchorX);
            pane.setTranslateY(translateAnchorY + event.getSceneY() - mouseAnchorY);

            event.consume();
        }
    };
    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {
            double delta = 1.2;
            double scale = pane.getScale();

            if (event.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);
            pane.setScale(scale);

            event.consume();
        }
    };

    InteractivePaneHandlers(InteractivePane pane) {
        this.pane = pane;
    }

    private static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }

    Pair<Double, Double> getClickCoord() {
        return clickCoord;
    }

    EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    void setXYText(Text text) {
        xyText = text;
    }
}