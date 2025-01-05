package alert;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class LoserAlert extends Alert
{

    public LoserAlert(int PlayerID) {
        super(AlertType.INFORMATION);

        setTitle("Spielentscheidung");
        setHeaderText("TFT 1v1 - Simplified Version");

        if(PlayerID == 1)
            getDialogPane().setContent(new Label("Du hast Verloren!" + " Mimique"));
        else{
            getDialogPane().setContent(new Label("Du hast Verloren!" + " Pikachu"));
        }
    }
}
