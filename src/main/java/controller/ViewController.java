package controller;

import javafx.util.Pair;
import listener.ViewEventListener;
import model.Player;
import model.Unit;
import utility.GameState;
import view.GameWindow;

import java.util.ArrayList;
import java.util.List;

public class ViewController implements ViewEventListener {

    private Player currentPlayer;
    private GameController gameController;
    public GameWindow gameWindow;

    private List<Pair<Integer, String>> selectionList = new ArrayList<>();

    public ViewController() {
        this.gameWindow = new GameWindow(this);
        gameWindow.getBoardView().setEventListener(this);
        gameWindow.getBankView().setEventListener(this);
    }

    public void buyUnitOnIndex(int selectedUnitIndex) {
        System.out.println("I want to buy Unit on Index " + selectedUnitIndex);
        gameController.buyUnitOnIndex(selectedUnitIndex, currentPlayer);
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void updateGameWindow(GameState gameState)
    {
        gameWindow.updateGameWindow(gameState);
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayerGold(int gold) {
        gameWindow.setPlayerGold(gold);
    }

    public void refreshShop() {
        gameController.refreshShop(currentPlayer);
    }

    // Behandlung der Zellenauswahl für das Spielfeld
    @Override
    public void onBoardCellSelected(int index, String source) {
        handleSelection(index, source, "Board");
    }

    // Behandlung einer Bank-Aktion
    @Override
    public void onBankAction(int bankItemId, String source) {
        handleSelection(bankItemId, source, "Bank");
    }

    @Override
    public void onBankInfoAction(int index) {
        gameController.showInfoOfUnit(index, currentPlayer);
    }

    private void handleSelection(int index, String source, String viewType) {
        // Überprüfen, ob zwei Elemente ausgewählt sind
        if (selectionList.size() == 2) {
            resetSelection();
        }

        // Auswahl hinzufügen
        selectionList.add(new Pair<>(index, source));

        // Einheit im richtigen View hervorheben
        if (viewType.equals("Board")) {
            gameWindow.getBoardView().highlightSelectedUnit(index);
        } else if (viewType.equals("Bank")) {
            gameWindow.getBankView().highlightSelectedUnit(index);
        }

        // Ausführen, wenn zwei Elemente ausgewählt sind
        if (selectionList.size() == 2) {
            executeMove();
        }
    }

    private void executeMove() {
        if (selectionList.size() == 2) {
            int firstIndex = selectionList.get(0).getKey();
            int secondIndex = selectionList.get(1).getKey();

            String source = selectionList.get(0).getValue();
            String target = selectionList.get(1).getValue();

            if (source.equals("Board") && target.equals("Board")) {
                gameController.moveBoardToBoard(firstIndex, secondIndex, currentPlayer);
            } else if (source.equals("Board") && target.equals("Bank")) {
                gameController.moveToBank(firstIndex, secondIndex, currentPlayer);
            } else if (source.equals("Bank") && target.equals("Board")) {
                gameController.moveFromBankToBoard(firstIndex, secondIndex, currentPlayer);
            } else if (source.equals("Bank") && target.equals("Bank")) {
                gameController.moveFromBankToBank(firstIndex, secondIndex, currentPlayer);
            }
            resetSelection();
        }
    }

    private void resetSelection() {
        // Beide Views zurücksetzen
        gameWindow.getBoardView().resetHighlight();
        gameWindow.getBankView().resetHighlight();
        selectionList.clear();
        System.out.println("Auswahl zurückgesetzt");
    }

    public void updateUnitOnBoard(Unit unit)
    {
        gameWindow.updateUnitOnBoard(unit);
    }

    public void startPhase() {
        gameController.BattlingPhase(currentPlayer);
    }

    public void increaseLevel() {
        gameWindow.increaseLevel();
    }

    public void purchaseXp() {
        gameController.purchaseXp(currentPlayer);
    }

    public void showInfoOfUnit(int UnitIndex)
    {
        gameController.showInfoOfUnit(UnitIndex, currentPlayer);
    }
}
