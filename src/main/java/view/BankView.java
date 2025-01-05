package view;


import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import listener.ViewEventListener;
import model.Unit;

import java.util.List;

public class BankView extends HBox {
    private static final int MAX_SLOTS = 10; // Maximale Anzahl der Plätze
    private final StackPane[] slots; // Array von StackPane für die Slots
    private int clickedIndex = -1;
    private ViewEventListener eventListener;

    public BankView() {
        // Setze die Ausrichtung und den Abstand der VBox
        setAlignment(Pos.CENTER);
        setSpacing(10);

        // Initialisiere das Array für die Slots
        slots = new StackPane[MAX_SLOTS];

        // Erstelle Slots und füge sie zur BankView hinzu
        for (int i = 0; i < MAX_SLOTS; i++) {
            slots[i] = createSlot();

            final int index = i;
            slots[i].setOnMouseClicked(event -> {

                if (event.getButton() == MouseButton.PRIMARY) {
                    clickedIndex = index;
                    eventListener.onBankAction(clickedIndex, "Bank");
                }
                else if (event.getButton() == MouseButton.SECONDARY)
                {
                    eventListener.onBankInfoAction(index, "Bank");
                }
            });
            getChildren().add(slots[i]); // Füge jeden Slot zur VBox hinzu
        }
    }

    public void setEventListener(ViewEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Hebt den ausgewählten Slot hervor.
     *
     * @param index Der Index des auszuwählenden Slots
     */
    public void highlightSelectedUnit(int index) {
        for (int i = 0; i < slots.length; i++) {
            StackPane slot = slots[i];
            if (i == index) {
                slot.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: lightgray;");
            } else {
                slot.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightgray;");
            }
        }
    }

    /**
     * Setzt die Hervorhebung aller Slots zurück.
     */
    public void resetHighlight() {
        for (StackPane slot : slots) {
            slot.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightgray;");
        }
    }

    /**
     * Erstellt einen Slot für die Bank.
     * @return Ein StackPane, das als Slot dient.
     */
    private StackPane createSlot() {
        StackPane slot = new StackPane();
        slot.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: lightgray;"); // Stil für den Slot
        slot.setPrefSize(100, 100); // Größe des Slots
        return slot;
    }

    /**
     * Aktualisiert die Bankansicht mit einer Liste von Einheiten.
     * @param units Eine Liste von Einheiten, die in der Bank angezeigt werden sollen.
     */
    public void update(List<Unit> units) {
        // Leere die Slots
        for (StackPane slot : slots) {
            slot.getChildren().clear();
        }

        // Füge die Sprites für die Einheiten zu den Slots hinzu
        for (int i = 0; i < units.size() && i < MAX_SLOTS; i++) {
            Unit unit = units.get(i);
            ImageView unitSprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/" + unit.getId() + ".png")));
            //ImageView unitSprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/pikachu.png")));
            unitSprite.setFitWidth(80); // Breite des Sprites anpassen
            unitSprite.setFitHeight(80); // Höhe des Sprites anpassen
            slots[i].getChildren().add(unitSprite); // Füge das Sprite dem Slot hinzu
        }
    }
}
