package view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import model.Unit;

public class InfoBarView extends GridPane {

    private ImageView unitImage;
    private Text nameText;
    private Text hpText;
    private Text attackText;
    private Text defenseText;
    private Text attackSpeedText;
    private Text attackReachText;
    private Text costText;
    private Text positionText;

    public InfoBarView() {
        // GridPane Einstellungen
        this.setHgap(10);
        this.setVgap(10);
        this.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-background-color: lightgray;");

        // ImageView für das Bild
        unitImage = new ImageView();
        unitImage.setFitWidth(100);
        unitImage.setFitHeight(100);
        this.add(unitImage, 0, 0, 2, 1); // Das Bild nimmt zwei Spalten ein

        // Attribute
        nameText = addRow("Name", 1);
        hpText = addRow("HP", 2);
        attackText = addRow("Attack", 3);
        defenseText = addRow("Defense", 4);
        attackSpeedText = addRow("Attack Speed", 5);
        attackReachText = addRow("Attack Reach", 6);
        costText = addRow("Cost", 7);
        positionText = addRow("Position", 8);

        // Leeren Zustand setzen
        clearUnitInfo();
    }

    private Text addRow(String label, int rowIndex) {
        Text labelText = new Text(label + ":");
        Text valueText = new Text();
        this.add(labelText, 0, rowIndex);
        this.add(valueText, 1, rowIndex);
        return valueText;
    }

    // Leeren Zustand anzeigen
    public void clearUnitInfo() {
        unitImage.setImage(null); // Entferne das Bild
        nameText.setText("");     // Leerer Text für Name
        hpText.setText("");
        attackText.setText("");
        defenseText.setText("");
        attackSpeedText.setText("");
        attackReachText.setText("");
        costText.setText("");
        positionText.setText("");
    }

    public void updateUnitInfo(Unit unit) {
        if (unit == null) {
            clearUnitInfo(); // Setze die Ansicht in den leeren Zustand
            return;
        }

        // Setze das Bild der Einheit
        Image image = new Image(getClass().getResourceAsStream("/sprites/" + unit.getId() + ".png")); // Bildpfad anpassen
        unitImage.setImage(image);

        // Aktualisiere die Textwerte
        nameText.setText(unit.getName());
        hpText.setText(String.valueOf(unit.getHp()));
        attackText.setText(String.valueOf(unit.getAttack()));
        defenseText.setText(String.valueOf(unit.getDefense()));
        attackSpeedText.setText(String.format("%.2f", unit.getAttackSpeed()));
        attackReachText.setText(String.valueOf(unit.getAttackReach()));
        costText.setText(String.valueOf(unit.getCost()));
        positionText.setText(String.format("(%d, %d)", unit.getPosX(), unit.getPosY()));
    }
}
