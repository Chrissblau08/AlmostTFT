package view;

import controller.ViewController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Player;
import model.Unit;

import java.util.List;

public class ShopView extends VBox {

    private Label goldLabel;              // Anzeige für das aktuelle Gold
    private HBox unitDisplayArea;         // HBox zur Anzeige der Einheiten nebeneinander
    private Button buyButton;             // Button zum Kauf der ausgewählten Einheit
    private Button sellButton;
    private Button refreshButton;
    private Button btn_purchaseXp;
    private int selectedIndex = -1;       // Index der ausgewählten Einheit
    private ViewController viewController;

    private ImageView playerHpImageView;  // Neues ImageView für Spieler-HP

    private ImageView winStreak;
    private ImageView loseStreak;
    private Label winStreakLabel;
    private Label loseStreakLabel;


    private Label hpLabel;                // Label zur Anzeige der HP
    private Label XpLabelProgress;       // Xp Label
    private Label XpLabelLevel;
    private HBox mainLayout;

    public ShopView(ViewController viewController, int GameSessionID) {
        this.viewController = viewController;
        // Layout-Grundeinstellungen
        setSpacing(10);
        setAlignment(Pos.CENTER);

        // Gold-Anzeige erstellen
        goldLabel = new Label("Gold: ");

        // Player-HP-Bereich
        StackPane playerHpPane = new StackPane();
        if (GameSessionID == 0) {
            playerHpImageView = new ImageView(new Image(getClass().getResourceAsStream("/sprites/Pikachu.png")));
            playerHpPane.setStyle("-fx-border-color: red; -fx-border-width: 5px; -fx-background-color: white;");
        } else {
            playerHpImageView = new ImageView(new Image(getClass().getResourceAsStream("/sprites/Pikachu2.png")));
            playerHpPane.setStyle("-fx-border-color: blue; -fx-border-width: 5px; -fx-background-color: white;");
        }
        playerHpImageView.setFitWidth(100);
        playerHpImageView.setFitHeight(100);
        playerHpPane.getChildren().add(playerHpImageView);

        XpLabelLevel = new Label("Level: 1");
        XpLabelProgress = new Label("XP: 0/4");
        hpLabel = new Label("HP: 100");
        VBox playerHpBox = new VBox(5, XpLabelProgress, XpLabelLevel, playerHpPane, hpLabel);
        playerHpBox.setAlignment(Pos.CENTER);

        // Einheiten-Anzeige
        unitDisplayArea = new HBox(15);
        unitDisplayArea.setAlignment(Pos.CENTER);

        // Buttons für Refresh und Xp kaufen
        VBox buttonBox = new VBox();
        btn_purchaseXp = new Button("Purchase XP");
        btn_purchaseXp.setPrefSize(100, 75);
        refreshButton = new Button("Refresh");
        refreshButton.setPrefSize(100, 75);
        buttonBox.getChildren().addAll(btn_purchaseXp, refreshButton);

        // Buttons für Shop-Interaktionen
        buyButton = new Button("Kaufen");
        sellButton = new Button("Verkaufen");

        HBox menu = new HBox(buyButton, sellButton);
        menu.setAlignment(Pos.CENTER);

        // Shop-Aktionen verbinden
        buyButton.setOnAction(_ -> {
            if (getSelectedUnitIndex() != -1) {
                viewController.buyUnitOnIndex(getSelectedUnitIndex());
                selectedIndex = -1;
            }
        });

        sellButton.setOnAction(_ -> {
            viewController.sellUnit();
        });

        refreshButton.setOnAction(_ -> {
            viewController.refreshShop();
        });

        btn_purchaseXp.setOnAction(_ -> {
            viewController.purchaseXp();
        });

        //Winstreak/Losestreak
        StackPane streaks = new StackPane();
        winStreak = new ImageView(new Image(getClass().getResourceAsStream("/sprites/winstreak.gif")));
        loseStreak = new ImageView(new Image(getClass().getResourceAsStream("/sprites/losestreak.gif")));

        winStreak.setFitWidth(50);
        winStreak.setFitHeight(50);
        loseStreak.setFitWidth(50);
        loseStreak.setFitHeight(50);

        streaks.getChildren().add(winStreak);
        streaks.getChildren().add(loseStreak);

        winStreakLabel = new Label("Wins: 0");
        loseStreakLabel = new Label("Loses: 0");
        VBox streaksVBox = new VBox(10, winStreakLabel, winStreak, loseStreakLabel, loseStreak);
        streaksVBox.setAlignment(Pos.CENTER);

        // Hauptlayout erstellen
        mainLayout = new HBox(20, streaksVBox, playerHpBox, buttonBox, new VBox(goldLabel, unitDisplayArea, menu));
        mainLayout.setAlignment(Pos.CENTER);

        // Komponenten zum Hauptlayout hinzufügen
        getChildren().add(mainLayout);
    }


    /**
     * Setzt die angezeigten Einheiten im Shop.
     * Jede Einheit wird als eigene Karte mit Namen, Kosten und einem Bild dargestellt.
     *
     * @param units Eine Liste der verfügbaren Einheiten
     */
    public void setAvailableUnits(List<Unit> units) {
        unitDisplayArea.getChildren().clear();  // Zuerst alles leeren
        selectedIndex = -1;

        // Füge die verfügbaren Einheiten hinzu
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);

            // Normale Karte für eine verfügbare Einheit
            ImageView unitSprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/" + unit.getId() + ".png")));
            unitSprite.setFitWidth(50);
            unitSprite.setFitHeight(50);

            // Text für Name und Kosten
            Text unitName = new Text(unit.getName());
            Text unitCost = new Text("Kosten: " + unit.getCost());

            // VBox für Einheit-Details erstellen
            VBox unitCard = new VBox(5, unitSprite, unitName, unitCost);
            unitCard.setAlignment(Pos.CENTER);
            unitCard.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-border-radius: 5;");

            // Ereignis, um die Auswahl der Einheit anzuzeigen
            final int index = i;
            unitCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    selectedIndex = index;
                    highlightSelectedUnit(index);
                }
                else if (event.getButton() == MouseButton.SECONDARY)
                {
                    viewController.showInfoOfUnit(index, "Shop");
                }
            });

            unitDisplayArea.getChildren().add(unitCard);
        }

        // Wenn weniger als 5 Einheiten vorhanden sind, fügen wir Platzhalter hinzu
        for (int i = units.size(); i < 5; i++) {
            VBox placeholderCard = new VBox(10);
            placeholderCard.setPrefSize(75,115);
            placeholderCard.setAlignment(Pos.CENTER);
            placeholderCard.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-border-radius: 5; -fx-background-color: lightgray;");

            // Text, dass es sich um eine leere/platzierte Einheit handelt
            Text placeholderText = new Text("");
            placeholderText.setStyle("-fx-font-weight: bold; -fx-fill: gray;");
            placeholderCard.getChildren().add(placeholderText);

            unitDisplayArea.getChildren().add(placeholderCard);
        }
    }


    /**
     * Hebt die ausgewählte Einheit hervor.
     *
     * @param index Der Index der ausgewählten Einheit
     */
    private void highlightSelectedUnit(int index) {
        for (int i = 0; i < unitDisplayArea.getChildren().size(); i++) {
            VBox unitCard = (VBox) unitDisplayArea.getChildren().get(i);
            if (i == index) {
                unitCard.setStyle("-fx-border-color: blue; -fx-padding: 10; -fx-border-radius: 5;");
            } else {
                unitCard.setStyle("-fx-border-color: gray; -fx-padding: 10; -fx-border-radius: 5;");
            }
        }
    }

    /**
     * Aktualisiert das angezeigte Gold.
     *
     * @param gold Der aktuelle Goldbestand
     */
    public void setGold(int gold) {
        goldLabel.setText("Gold: " + gold);
    }

    /**
     * Aktualisiert die angezeigten HP des Spielers.
     *
     * @param hp Die aktuellen HP
     */
    public void setPlayerHp(int hp) {
        hpLabel.setText("HP: " + hp);
    }

    /**
     * Gibt den Index der ausgewählten Einheit zurück.
     *
     * @return Der Index der ausgewählten Einheit, oder -1, wenn keine Auswahl getroffen wurde.
     */
    public int getSelectedUnitIndex() {
        return selectedIndex;
    }

    /**
     * Setzt die Xp-Values
     * @param progress
     * @param level
     */
    public void setXpVals(String progress, int level) {
        XpLabelProgress.setText("XP: " + progress);
        XpLabelLevel.setText("Level: " + level);
    }

    public void setStreaks(int winStreak, int loseStreak){
        winStreakLabel.setText("Wins: " + winStreak);
        loseStreakLabel.setText("Loses: " + loseStreak);
    }
}
