package view;

import controller.GameController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Player;
import model.Unit;

import java.util.HashMap;
import java.util.UUID;

import static javafx.scene.paint.Color.*;

public class BattleView extends BorderPane {
    public static final int SIZE = 10;
    private final GridPane grid = new GridPane();
    private final HashMap<UUID, StackPane> unitSprites = new HashMap<>();
    private ProgressBar progressBar;
    private Timeline timeline;
    private double totalDuration;
    private TextArea textArea;

    public BattleView() {
        initializeGrid();
        setCenter(grid);
        setupProgressBar();
        setupTextBox();
    }

    private void setupTextBox() {
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(150);
        textArea.setWrapText(true);
        textArea.setScrollTop(Double.MAX_VALUE);
        setBottom(textArea);
    }

    public void addTextToTextBox(String text) {
        Platform.runLater(() -> {
            textArea.appendText(text + "\n");
            textArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void initializeGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Pane cell = createGridCell();
                grid.add(cell, col, row);
            }
        }
    }

    private Pane createGridCell() {
        Pane cell = new Pane();
        cell.setPrefSize(50, 50);
        Rectangle border = new Rectangle(50, 50, TRANSPARENT);
        border.setStroke(GRAY);
        cell.getChildren().add(border);
        return cell;
    }

    private void setupProgressBar() {
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        setAlignment(progressBar, Pos.CENTER);
        setTop(progressBar);
    }

    public void setTotalDuration(double duration) {
        this.totalDuration = duration;
        setupTimeline();
        timeline.play();
    }

    private void setupTimeline() {
        timeline = new Timeline(
                new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
                    double elapsedTime = 0;

                    @Override
                    public void handle(ActionEvent event) {
                        elapsedTime += 100;
                        double progress = elapsedTime / totalDuration;
                        progressBar.setProgress(progress);

                        if (elapsedTime >= totalDuration) {
                            timeline.stop();
                        }
                    }
                })
        );

        timeline.setCycleCount((int) (totalDuration / 100));
        timeline.setOnFinished(event -> System.out.println("Zeit abgelaufen!"));
    }

    private void updateProgressBar() {
        double elapsedTime = timeline.getCurrentTime().toMillis();
        double progress = Math.min(elapsedTime / totalDuration, 1.0);
        progressBar.setProgress(progress);
        if (progress >= 1.0) timeline.stop();
    }

    public void update(Unit unit, Player currentPlayer) {
        Platform.runLater(() -> {
            unitSprites.computeIfAbsent(unit.getUuid(), uuid -> createUnitPane(unit, currentPlayer));
            updateUnitPosition(unit);
        });
    }

    private StackPane createUnitPane(Unit unit, Player currentPlayer) {
        StackPane unitPane = new StackPane();
        unitPane.setPrefSize(50, 50);
        unitPane.getChildren().addAll(createHpBar(unit, currentPlayer), createSprite(unit));
        grid.add(unitPane, unit.getPosX(), unit.getPosY());
        return unitPane;
    }

    private ProgressBar createHpBar(Unit unit, Player currentPlayer) {
        ProgressBar hpBar = new ProgressBar(unit.getHp() / (double) unit.getMaxHp());
        if(currentPlayer.getPlayerID() == 0){
            hpBar.setStyle("-fx-accent: red;");
        }else {
            hpBar.setStyle("-fx-accent: blue;");
        }
        hpBar.setPrefWidth(40);
        hpBar.setTranslateY(20);
        return hpBar;
    }

    private StackPane createSprite(Unit unit) {
        // Erstelle das Sprite der Einheit
        ImageView sprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/" + unit.getId() + ".png")));
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);

        // Erstelle das StackPane, um Sprite und Sterne zu kombinieren
        StackPane spritePane = new StackPane();

        // Erstelle das StarLevel Image basierend auf dem Sternlevel der Einheit
        ImageView starLevelImage = null;
        if (unit.getStarLevel() == 2) {
            starLevelImage = createImageView("twostar");
        } else if (unit.getStarLevel() == 3) {
            starLevelImage = createImageView("threestar");
        }

        // Füge das Sprite der Einheit zum StackPane hinzu
        spritePane.getChildren().add(sprite);

        // Füge das StarLevel Image hinzu, wenn es existiert
        if (starLevelImage != null) {
            starLevelImage.setFitWidth(20);
            starLevelImage.setFitHeight(20);
            StackPane.setAlignment(starLevelImage, Pos.TOP_CENTER);  // Positioniere es oben in der Mitte
            spritePane.getChildren().add(starLevelImage);
        }



        return spritePane;
    }

    private void updateUnitPosition(Unit unit) {
        StackPane unitPane = unitSprites.get(unit.getUuid());
        if (unitPane != null && (GridPane.getRowIndex(unitPane) != unit.getPosY() || GridPane.getColumnIndex(unitPane) != unit.getPosX())) {
            grid.getChildren().remove(unitPane);
            grid.add(unitPane, unit.getPosX(), unit.getPosY());
        }
        updateUnitHP(unit);
    }

    public void updateUnitHP(Unit unit) {
        StackPane unitPane = unitSprites.get(unit.getUuid());
        if (unitPane != null) {
            ProgressBar hpBar = (ProgressBar) unitPane.getChildren().get(0);
            Platform.runLater(() -> hpBar.setProgress(unit.getHp() / (double) unit.getMaxHp()));
        }
    }

    public void removeUnit(Unit unit) {
        StackPane unitPane = unitSprites.remove(unit.getUuid());
        if (unitPane != null) grid.getChildren().remove(unitPane);
    }

    public void resetBattleView() {
        grid.getChildren().clear();
        unitSprites.clear();
        initializeGrid();
        if (timeline != null) timeline.stop();
        progressBar.setProgress(0);
    }

    public TextArea getTextArea() {
        return textArea;
    }

    private ImageView createImageView(String imageName) {
        return new ImageView(new Image(getClass().getResourceAsStream("/sprites/" + imageName + ".png")));
    }
}
