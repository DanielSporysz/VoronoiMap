package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/MainWindow.fxml")
        );

        primaryStage.setMinWidth(510);
        primaryStage.setMinHeight(350);
        Parent root = loader.load();
        primaryStage.setTitle("Super diagram Voronoi");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("SuperIcon.png"));
        primaryStage.show();

        MainWindowController controller = loader.getController();
        controller.initialiseInteractivePane();
    }

}