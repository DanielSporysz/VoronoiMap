package gui;

import IO.ErrorLog;
import IO.TerrainFile;
import analysis.Analyzer;
import analysis.Edge;
import analysis.Point;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import terrain.TerrainData;
import terrain.TerrainObject;
import terrain.TerrainPoint;
import terrain.TerrainTemplate;

import java.io.File;
import java.util.*;

public class MainWindowController {

    public AnchorPane anchorPane;
    public Pane paneForInteractivePane;
    public Text xyTextForDisplayingInteractivePanePosition;
    public MenuBar menuBar;
    public VBox vboxForHoldingTools;
    public MenuItem saveFileQuitMenu;
    public MenuItem saveFileMenu;
    public Button chooseImageButton;
    public Button showObjectsButton;
    public Button showSummaryObjectButton;
    public Button showPeopleButton;
    public Button addContourButton;
    public Button addPointButton;
    public Button deletePointContourButton;
    public Button centerMap;
    public Slider volumeSlider;
    private TerrainData terrainData;
    private TerrainFile terrainFile;
    private InteractivePane interactivePane;
    private Analyzer analyzer;
    private Stack<Pair<String, TerrainPoint>> undoStack = new Stack<>();
    private double posX;
    private double posY;
    private int areaID;
    private MediaPlayer audio;

    void initialiseInteractivePane() {
        interactivePane = new InteractivePane();
        paneForInteractivePane.getChildren().addAll(interactivePane);

        InteractivePaneHandlers sceneGestures = new InteractivePaneHandlers(interactivePane);
        paneForInteractivePane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        paneForInteractivePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        paneForInteractivePane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        paneForInteractivePane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && analyzer != null) {
                posX = sceneGestures.getClickCoord().getKey();
                posY = sceneGestures.getClickCoord().getValue();
                if (analyzer.pointIsInsideContour(analyzer.terrainContour, new Point(posX, posY))) {
                    buttonsDisable(false);
                    areaID = findArea(posX, posY);
                    if (event.getClickCount() == 2) {
                        String name = getNameMajorPoint();
                        if (name != null) {
                            TerrainObject terrainObject = new TerrainObject(name, new TerrainPoint(posX, posY));
                            terrainData.getMajorObjects().add(terrainObject);
                            undoStack.add(new Pair<>("addpoint", new TerrainPoint(posX, posY)));
                            analyze();
                        }
                    }
                } else {
                    if (event.getClickCount() == 2) {
                        Edge closest = analyzer.getClosestTerrainEdge(new Point(posX, posY));
                        int i = 0;
                        int endID = 0;
                        for (TerrainPoint terrainPoint : terrainData.getContour().points) {
                            if (terrainPoint.x == closest.end.x && terrainPoint.y == closest.end.y) {
                                endID = i;
                                break;
                            }
                            i++;
                        }
                        terrainData.getContour().points.add(endID, new TerrainPoint(posX, posY));
                        undoStack.add(new Pair<>("addcontour", new TerrainPoint(posX, posY)));
                        analyze();
                    }
                    buttonsDisable(true);
                }
            }
        });

        KeyCombination undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination read = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        KeyCombination save = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination quit = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);

        anchorPane.setOnKeyPressed(event -> {
            if (undo.match(event)) {
                undo();
            } else if (read.match(event)) {
                openFile();
            } else if (save.match(event) && terrainData != null) {
                saveFile();
            } else if (quit.match(event)) {
                quit();
            } else if (event.getCode() == KeyCode.DELETE) {
                deleteElement();
            }
        });

        sceneGestures.setXYText(xyTextForDisplayingInteractivePanePosition);

        volumeSlider.valueProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> audio.setVolume(volumeSlider.getValue() / 100));
        Media music = new Media(this.getClass().getResource("/Theme.mp3").toExternalForm());
        audio = new MediaPlayer(music);
        audio.setVolume(0.5);
        audio.setCycleCount(0);
        audio.play();
    }

    private void analyze() {
        analyzer = new Analyzer();
        analyzer.analyzeTerrain(terrainData);

        TerrainAdapter adapter = new TerrainAdapter();
        adapter.updateScene(interactivePane, terrainData, analyzer);
    }

    private void undo() {
        if (!undoStack.empty()) {
            Pair<String, TerrainPoint> undoAction = undoStack.pop();
            if (undoAction.getKey().equals("addcontour")) {
                int i = 0;
                for (TerrainPoint terrainPoint : terrainData.getContour().getPoints()) {
                    if (terrainPoint.x == undoAction.getValue().x && terrainPoint.y == undoAction.getValue().y) {
                        break;
                    }
                    i++;
                }
                terrainData.getContour().getPoints().remove(i);
            } else if (undoAction.getKey().equals("removecontour")) {
                Edge closest = analyzer.getClosestTerrainEdge(new Point(undoAction.getValue().x, undoAction.getValue().y));
                int i = 0;
                int endID = 0;
                for (TerrainPoint terrainPoint : terrainData.getContour().points) {
                    if (terrainPoint.x == closest.end.x && terrainPoint.y == closest.end.y) {
                        endID = i;
                        break;
                    }
                    i++;
                }
                terrainData.getContour().getPoints().add(endID, new TerrainPoint(undoAction.getValue().x, undoAction.getValue().y));
            } else if (undoAction.getKey().equals("addpoint")) {
                int i = 0;
                for (TerrainObject terrainObject : terrainData.getMajorObjects()) {
                    if (terrainObject.getPosition().x == undoAction.getValue().x && terrainObject.getPosition().y == undoAction.getValue().y) {
                        break;
                    }
                    i++;
                }
                terrainData.getMajorObjects().remove(i);
            } else if (undoAction.getKey() != null) {
                terrainData.getMajorObjects().add(new TerrainObject(undoAction.getKey(), new TerrainPoint(undoAction.getValue().x, undoAction.getValue().y)));
            }
            analyze();
        }
    }

    private int findArea(double x, double y) {
        double minDist = Double.MAX_VALUE;
        int id = 0, i = 0;
        for (TerrainObject terrainObject : terrainData.getMajorObjects()) {
            double pointX = terrainObject.getPosition().x;
            double pointY = terrainObject.getPosition().y;

            double dist = Math.sqrt(Math.pow(x - pointX, 2) + Math.pow(y - pointY, 2));
            if (dist < minDist) {
                minDist = dist;
                id = i;
            }
            i++;
        }
        return id;
    }

    public void openFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz plik do wczytania");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
        );
        File file = chooser.showOpenDialog(anchorPane.getScene().getWindow());

        if (file != null) {
            terrainFile = new TerrainFile();
            terrainData = terrainFile.readFile(file);
            List<ErrorLog> errorList = terrainFile.getErrorLog();
            if (errorList.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błędy wczytywania");
                alert.setHeaderText("Podczas wczytywania napotkano błędy.");

                Label label = new Label("Błędy: ");
                TextArea textArea = new TextArea();
                for (ErrorLog error : errorList) {
                    String exceptionText = error.toString();
                    textArea.appendText(exceptionText);
                }

                textArea.setEditable(false);
                textArea.setWrapText(true);

                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);

                alert.showAndWait();
            }
            addPointButton.setDisable(false);
            addContourButton.setDisable(false);
            saveFileMenu.setDisable(false);
            saveFileQuitMenu.setDisable(false);
            deletePointContourButton.setDisable(false);
            analyze();
        }
    }

    public boolean saveFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz miejsce zapisania");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
        );
        File file = chooser.showSaveDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            terrainFile.saveFile(file, terrainData);
            return true;
        } else {
            return false;
        }
    }

    public void saveFileAndQuit() {
        if (saveFile()) {
            quit();
        }
    }

    public void quit() {
        Platform.exit();
    }

    public void addContour() {
        Pair<Double, Double> coord = getCoordinates(true);
        if (coord != null) {
            if (!analyzer.pointIsInsideContour(analyzer.terrainContour, new Point(coord.getKey(), coord.getValue()))) {
                Edge closest = analyzer.getClosestTerrainEdge(new Point(coord.getKey(), coord.getValue()));
                int i = 0;
                int endID = 0;
                for (TerrainPoint terrainPoint : terrainData.getContour().getPoints()) {
                    if (terrainPoint.x == closest.end.x && terrainPoint.y == closest.end.y) {
                        endID = i;
                        break;
                    }
                    i++;
                }
                terrainData.getContour().addContourPoint(endID, new TerrainPoint(coord.getKey(), coord.getValue()));
                undoStack.add(new Pair<>("addcontour", new TerrainPoint(coord.getKey(), coord.getValue())));
                analyze();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błąd dodawania punktu konturu");
                alert.setContentText("Podane koordynaty znajdują się wewnątrz konturu mapy.");
                alert.showAndWait();
            }
        }
    }

    public void addPoint() {
        Pair<Double, Double> coord = getCoordinates(false);
        if (coord != null) {
            if (analyzer.pointIsInsideContour(analyzer.terrainContour, new Point(coord.getKey(), coord.getValue()))) {
                String name = getNameMajorPoint();
                if (name != null) {
                    TerrainObject terrainObject = new TerrainObject(name, new TerrainPoint(coord.getKey(), coord.getValue()));
                    terrainData.getMajorObjects().add(terrainObject);
                    undoStack.add(new Pair<>("addpoint", new TerrainPoint(coord.getKey(), coord.getValue())));
                    analyze();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Błąd dodawania punktu kluczowego");
                alert.setContentText("Podane koordynaty znajdują się poza konturem mapy.");
                alert.showAndWait();
            }
        }
    }

    private String getNameMajorPoint() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Nazwa punktu");

        ButtonType okButton = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);
        dialog.getDialogPane().lookupButton(okButton).setDisable(true);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Nazwa punktu");

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 32) {
                name.setText(oldValue);
            }
            if (name.getText().isEmpty()) {
                dialog.getDialogPane().lookupButton(okButton).setDisable(true);
            } else {
                dialog.getDialogPane().lookupButton(okButton).setDisable(false);
            }
        });

        gridPane.add(name, 0, 0);
        dialog.getDialogPane().setContent(gridPane);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return name.getText();
            } else {
                return null;
            }
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private Pair<Double, Double> getCoordinates(boolean isCountour) {
        Dialog<Pair<Double, Double>> dialog = new Dialog<>();
        if (isCountour) {
            dialog.setTitle("Nowy kontur");
        } else {
            dialog.setTitle("Nowy punkt szczególny");
        }

        ButtonType okButton = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(okButton).setDisable(true);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 10, 10, 10));

        TextField posX = new TextField();
        posX.setPromptText("Pozycja X");
        TextField posY = new TextField();
        posY.setPromptText("Pozycja Y");

        posX.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                posX.setText(oldValue);
            }
            if (!posX.getText().isEmpty() && !posY.getText().isEmpty()) {
                dialog.getDialogPane().lookupButton(okButton).setDisable(false);
            } else {
                dialog.getDialogPane().lookupButton(okButton).setDisable(true);
            }
        });

        posY.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                posY.setText(oldValue);
            }
            if (!posX.getText().isEmpty() && !posY.getText().isEmpty()) {
                dialog.getDialogPane().lookupButton(okButton).setDisable(false);
            } else {
                dialog.getDialogPane().lookupButton(okButton).setDisable(true);
            }
        });

        gridPane.add(posX, 0, 0);
        gridPane.add(posY, 1, 0);

        dialog.getDialogPane().setContent(gridPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(Double.parseDouble(posX.getText()), Double.parseDouble(posY.getText()));
            } else {
                return null;
            }
        });

        Optional<Pair<Double, Double>> result = dialog.showAndWait();
        if (result.isPresent()) {
            return dialog.getResult();
        } else {
            return null;
        }
    }

    public void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Wybierz grafikę do wczytania");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif")
        );
        File file = (chooser.showOpenDialog(anchorPane.getScene().getWindow()));
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            terrainData.getMajorObjects().get(areaID).setImage(image);
            analyze();
        }
    }

    private void buttonsDisable(boolean state) {
        showPeopleButton.setDisable(state);
        showSummaryObjectButton.setDisable(state);
        showObjectsButton.setDisable(state);
        chooseImageButton.setDisable(state);
        deletePointContourButton.setDisable(false);
    }

    public void showObjects() {
        List<TerrainObject> listOfObject = analyzer.getObjectsFor(terrainData, areaID);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statystyki");
        alert.setHeaderText(terrainData.getMajorObjects().get(areaID).getType());
        StringBuilder sb = new StringBuilder();
        for (TerrainObject terrainObject : listOfObject) {
            sb.append(terrainObject.getType());
            TerrainTemplate terrainTemplate = terrainData.getTemplateObject().get(terrainObject.getType());
            for (Pair<String, String> pair : terrainTemplate.getVariablesInside()) {
                String variableName = pair.getKey();
                String variableType = pair.getValue();
                if (variableType.matches("Byte|byte")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getByteMap().get(variableName));
                } else if (variableType.matches("Short|short")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getShortMap().get(variableName));
                } else if (variableType.matches("Int|int")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getIntegerMap().get(variableName));
                } else if (variableType.matches("Long|long")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getLongMap().get(variableName));
                } else if (variableType.matches("Float|float")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getFloatMap().get(variableName));
                } else if (variableType.matches("Double|double")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getDoubleMap().get(variableName));
                } else if (variableType.matches("Bool|bool")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getBooleanMap().get(variableName));
                } else if (variableType.matches("Char|char")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getCharacterMap().get(variableName));
                } else if (variableType.matches("String|string")) {
                    sb.append(" ").append(variableName).append(": ").append(terrainObject.getStringMap().get(variableName));
                }
            }
            sb.append("\r\n");
        }
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    public void showSummaryObject() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statystyka zbiorcza");
        alert.setHeaderText(terrainData.getMajorObjects().get(areaID).getType());
        Map<String, Integer> objectsCount = new HashMap<>();
        for (TerrainObject terrainObject : analyzer.getObjectsFor(terrainData, areaID)) {
            if (!objectsCount.containsKey(terrainObject.getType())) {
                objectsCount.put(terrainObject.getType(), 1);
            } else {
                objectsCount.put(terrainObject.getType(), objectsCount.get(terrainObject.getType()) + 1);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String templateName : objectsCount.keySet()) {
            sb.append(" ").append(templateName).append(": ").append(objectsCount.get(templateName)).append(" ");
        }
        alert.setContentText(sb.toString());

        alert.showAndWait();
    }

    public void showPeople() {
        List<TerrainObject> listOfObject = analyzer.getObjectsFor(terrainData, areaID);
        StringBuilder sb = new StringBuilder();
        int summaryAmount = 0;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Liczba ludności");
        alert.setHeaderText(terrainData.getMajorObjects().get(areaID).getType());
        for (TerrainObject terrainObject : listOfObject) {
            if (terrainObject.isInhibitiant()) {
                summaryAmount += terrainObject.getInhibitiants();
                sb.append(terrainObject.getType()).append(": ").append(terrainObject.getInhibitiants());
            }
            sb.append("\r\n");
        }
        sb.append("Sumaryczna liczba ludności: ").append(summaryAmount);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    public void centerMap() {
        interactivePane.setTranslateX(0);
        interactivePane.setTranslateY(0);

        interactivePane.setScale(1);
    }

    public void deleteElement() {
        if (analyzer.pointIsInsideContour(analyzer.terrainContour, new Point(posX, posY)) && areaID >= 0) {
            undoStack.add(new Pair<>(terrainData.getMajorObjects().get(areaID).getType(), new TerrainPoint(terrainData.getMajorObjects().get(areaID).getPosition().x, terrainData.getMajorObjects().get(areaID).getPosition().y)));
            terrainData.getMajorObjects().remove(areaID);
            areaID = -1;
            buttonsDisable(true);
            deletePointContourButton.setDisable(true);
        } else {
            double minDist = Double.MAX_VALUE;
            int id = 0;
            int i = 0;
            for (TerrainPoint terrainPoint : terrainData.getContour().getPoints()) {
                double x = terrainPoint.x;
                double y = terrainPoint.y;
                double dist = Math.sqrt(Math.pow(x - posX, 2) + Math.pow(y - posY, 2));
                if (dist < minDist) {
                    minDist = dist;
                    id = i;
                }
                i++;
            }
            undoStack.add(new Pair<>("removecontour", new TerrainPoint(terrainData.getContour().getPoints().get(id).x, terrainData.getContour().getPoints().get(id).y)));
            terrainData.getContour().points.remove(id);
        }
        analyze();
    }

    public void startMusic() {
        audio.play();
    }

    public void pauseMusic() {
        audio.pause();
    }

    public void stopMusic() {
        audio.stop();
    }

    public void developerList() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        ButtonType donate = new ButtonType("Wyślij napiwek");
        alert.setTitle("O programie");
        alert.setHeaderText("Twórcy");
        Text developers = new Text("  Daniel Sporysz & Patryk Zaniewski  ");
        developers.setFont(Font.font(null, 13));

        alert.getDialogPane().setContent(developers);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(0, donate);
        alert.showAndWait();
    }
}