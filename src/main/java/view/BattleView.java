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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Unit;

import java.util.HashMap;
import java.util.UUID;

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
        Rectangle border = new Rectangle(50, 50, Color.TRANSPARENT);
        border.setStroke(Color.GRAY);
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

    public void update(Unit unit) {
        Platform.runLater(() -> {
            unitSprites.computeIfAbsent(unit.getUuid(), uuid -> createUnitPane(unit));
            updateUnitPosition(unit);
        });
    }

    private StackPane createUnitPane(Unit unit) {
        StackPane unitPane = new StackPane();
        unitPane.setPrefSize(50, 50);
        unitPane.getChildren().addAll(createHpBar(unit), createSprite());
        grid.add(unitPane, unit.getPosX(), unit.getPosY());
        return unitPane;
    }

    private ProgressBar createHpBar(Unit unit) {
        ProgressBar hpBar = new ProgressBar(unit.getHp() / (double) unit.getMaxHp());
        hpBar.setPrefWidth(40);
        hpBar.setTranslateY(20);
        return hpBar;
    }

    private ImageView createSprite() {
        ImageView sprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/pikachu.png")));
        sprite.setFitWidth(40);
        sprite.setFitHeight(40);
        return sprite;
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
}
