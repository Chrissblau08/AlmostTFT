package test;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Game;

public class Start extends Application
{

    @Override
    public void start(Stage stage) throws Exception {
        new Game();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
