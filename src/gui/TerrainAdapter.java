package gui;

import analysis.Analyzer;
import analysis.Edge;
import analysis.Point;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import terrain.TerrainData;
import terrain.TerrainObject;

public class TerrainAdapter {
    public void updateScene(InteractivePane pane, TerrainData data, Analyzer analyzer) {
        pane.getChildren().clear();

        if (analyzer.terrainContour != null && !analyzer.terrainContour.isEmpty()) {
            for (Edge edge : analyzer.terrainContour) {
                Point[] points = edge.getPoints();
                Line line = new Line(points[0].x, points[0].y, points[1].x, points[1].y);
                line.setStrokeWidth(.2f);
                pane.getChildren().addAll(line);
                line.setStroke(Color.RED);
            }
        }

        int i = 0;
        for (Point point : analyzer.points) {
            Polygon filling = new Polygon();

            if (point.edges != null && !point.edges.isEmpty()) {
                for (Edge edge : point.edges) {
                    filling.getPoints().addAll(edge.start.x, edge.start.y);
                    filling.getPoints().addAll(edge.end.x, edge.end.y);
                }
            }

            if (data.getMajorObjects().get(i).getImage() != null) {
                ImagePattern imagePattern = new ImagePattern(data.getMajorObjects().get(i).getImage());
                filling.setFill(imagePattern);
            } else {
                filling.setFill(Color.color(Math.random(), Math.random(), Math.random(), 0.85d));
            }

            filling.setStroke(Color.BLACK);
            pane.getChildren().add(filling);
            i++;
        }

        for (Point object : analyzer.points) {
            Circle circle1 = new Circle(1);
            circle1.setTranslateX(object.x);
            circle1.setTranslateY(object.y);
            circle1.setStroke(Color.RED);
            //circle1.setFill(Color.RED.deriveColor(1, 1, 1, 0.5));
            circle1.setFill(Color.RED);
            pane.getChildren().addAll(circle1);

        }
    }
}