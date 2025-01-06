package view;

import alert.GameRulesAlert;
import controller.ViewController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Player;
import model.Unit;
import utility.GameState;

import java.util.List;

public class GameWindow extends BorderPane {
    private BoardView boardView;
    private ShopView shopView;
    private BankView bankView;
    private BankView bankViewEnemy;
    private ProgressBar progressBar;
    private Label roundCounter;
    private VBox topLayout;
    private ViewController viewController;
    private double totalDuration;
    private InfoBarView infoBarView;

    Timeline timeline;

    public GameWindow(ViewController viewController, int GameSessionID) {
        this.viewController = viewController;
        boardView = new BoardView();
        shopView = new ShopView(this.viewController, GameSessionID);
        bankView = new BankView();
        bankViewEnemy = new BankView();
        infoBarView = new InfoBarView();


        // GameWindow-Settings
        setPadding(new Insets(10));
        BorderPane.setMargin(boardView, new Insets(20, 0, 20, 270));

        // Eigenen Shop setzen
        //shopView.setAvailableUnits(units);

        // Top-Bereich mit Bankansicht und ProgressBar erstellen
        setTop(createTopPanel());

        // Setze das Spielfeld in die Mitte
        setCenter(boardView);

        setRight(infoBarView);

        // Layout für Shop und Spielerbank erstellen
        VBox bottomLayout = new VBox();
        bottomLayout.setSpacing(20);
        bottomLayout.getChildren().addAll(bankView, shopView);
        setBottom(bottomLayout);
    }

    private VBox createTopPanel() {
        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER_LEFT);

        Button helpButton = new Button("Help");
        helpButton.setOnAction(_Event -> {
            GameRulesAlert gameRulesAlert = new GameRulesAlert();
            gameRulesAlert.showAndWait();
        });


        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        setUpTimeLine(totalDuration);

        roundCounter = new Label("Runde: 1");

        HBox middlePanel = new HBox(10);
        middlePanel.setAlignment(Pos.CENTER);
        middlePanel.getChildren().addAll(roundCounter, progressBar);

        topPanel.getChildren().addAll(helpButton, middlePanel);
        topPanel.setSpacing(300);

        topLayout = new VBox(topPanel, bankViewEnemy);
        topLayout.setAlignment(Pos.CENTER);
        topLayout.setSpacing(10);

        return topLayout;
    }


    private void setUpTimeLine(double totalDuration) {
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
                            viewController.startPhase();
                        }
                    }
                })
        );

        timeline.setCycleCount((int) (totalDuration / 100));
        timeline.setOnFinished(event -> System.out.println("Zeit abgelaufen!"));
    }

    /**
     * Aktualisiert die verfügbaren Einheiten im Shop des Spielers.
     *
     * @param playerShopUnits Eine Liste der Einheiten, die im Shop des Spielers verfügbar sind.
     */
    public void updateShopView(List<Unit> playerShopUnits) {
        shopView.setAvailableUnits(playerShopUnits);
    }

    public void setPlayerGold(int gold) {
        shopView.setGold(gold);
    }

    public BankView getBankView() {
        return bankView;
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public void updateUnitOnBoard(Unit unit) {
        boardView.updateUnit(unit);
        boardView.updateUnitHP(unit);
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
        setUpTimeLine(totalDuration);
        timeline.play();
    }

    public void increaseLevel() {
        String text = roundCounter.getText().trim();
        int currentRound = Integer.parseInt(text.replace("Runde:", "").trim());
        roundCounter.setText("Runde: " + (currentRound + 1));
    }

    public void updateGameWindow(GameState gameState) {
        bankView.update(gameState.playerBench);

        if(!gameState.playerBoard.isEmpty())
            boardView.update(gameState.playerBoard);
        shopView.setAvailableUnits(gameState.shopUnits);
        shopView.setPlayerHp(gameState.playerHp);
        shopView.setXpVals(gameState.playerXp, gameState.playerLevel);
        shopView.setGold(gameState.playerGold);

        //todo -Bugs?
        bankViewEnemy.update(gameState.enemyBench);
    }

    public void showInfoOfUnit(Unit unit) {
        infoBarView.updateUnitInfo(unit);
    }
}
