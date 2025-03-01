package controller;

import alert.LoserAlert;
import alert.WinnerAlert;
import javafx.util.Pair;
import listener.ViewEventListener;
import model.Player;
import model.Unit;
import utility.GameState;
import view.BankView;
import view.GameWindow;

import java.util.ArrayList;
import java.util.List;

public class ViewController implements ViewEventListener {

    private Player currentPlayer;
    private GameController gameController;
    public GameWindow gameWindow;

    private List<Pair<Integer, String>> selectionList = new ArrayList<>();

    public ViewController(int GameSessionID) {
        this.gameWindow = new GameWindow(this, GameSessionID);
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
    public void onBankInfoAction(int index, String flag) {
        gameController.showInfoOfUnit(index, currentPlayer, flag);
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
                gameController.moveBoardToBank(firstIndex, secondIndex, currentPlayer);
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

    public void showInfoOfUnit(int UnitIndex, String flag)
    {
        gameController.showInfoOfUnit(UnitIndex, currentPlayer, flag);
    }

    public void setAlert(String Flag, int playerID) {
        if(Flag.equals("Loss"))
        {
            LoserAlert alert = new LoserAlert(playerID);
            alert.showAndWait();
        }
        else
        {
            WinnerAlert alert = new WinnerAlert(playerID);
            alert.showAndWait();
        }
    }

    public void sellUnit() {
        BankView bank = gameWindow.getBankView();
        int SelectedIndex = bank.getClickedIndex();

        if(SelectedIndex == -1) return;

        gameController.sellUnit(currentPlayer, SelectedIndex);
    }
}
