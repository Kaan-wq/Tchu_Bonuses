package ch.epfl.tchu.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MenuTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        InitialMenuCreator.menuCreator(primaryStage, "null");
    }
}
