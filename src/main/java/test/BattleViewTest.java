package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.BattleView;

public class BattleViewTest extends Application
{


    @Override
    public void start(Stage stage) throws Exception
    {
        BattleView battleView = new BattleView();

        stage.setScene(new Scene(battleView)); // Hier kannst du deine spezifische View oder Layout setzen
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
