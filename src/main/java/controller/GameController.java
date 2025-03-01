package controller;

import ThreadPoolLogic.ThreadPool;
import ThreadPoolLogic.UnitTask;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import utility.GameState;
import utility.Phase;
import view.BattleView;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GameController verwaltet die zentrale Spiellogik, einschließlich Spieler, Shop, Spielfeld,
 * Kampflogik und Kommunikation mit der Benutzeroberfläche.
 */
public class GameController {

    long sixtySecondsTimer = 30 * 1000L; // 60 Sekunden in Millisekunden
    BattleView battleView = new BattleView();
    private static final int PLAYER_COUNT = 2;
    private Player[] players = new Player[PLAYER_COUNT];
    private ShopController[] shopControllers = new ShopController[PLAYER_COUNT];
    private UnitPool unitPool;
    private final AtomicBoolean roundEndedCalled = new AtomicBoolean(false);
    private ThreadPool[] threadPools;
    private Map<UUID, int[]> unitPositions = new HashMap<>();

    private Phase phase = Phase.Shopping;

    private ViewController[] viewControllers;
    private UnitsUtil unitsUtil;
    private ThreadPool[] candidates = new ThreadPool[PLAYER_COUNT];

    /**
     * Konstruktor für Mehrspieler-Modus.
     *
     * @param unitPool       Der gemeinsame Pool aller Einheiten im Spiel.
     * @param viewControllers Die Ansichten, die mit dem Controller verknüpft sind (eine pro Spieler).
     */
    public GameController(UnitPool unitPool, ViewController[] viewControllers) {
        this.viewControllers = viewControllers;
        this.unitPool = unitPool;
        this.viewControllers[0].setGameController(this);
        this.viewControllers[1].setGameController(this);
        this.viewControllers[0].setCurrentPlayer(players[0]);
        this.viewControllers[1].setCurrentPlayer(players[1]);
        System.out.println();
        //todo besseren Platz dafür finden, hier ist es offen direkt vom Start
        //todo Fenster am Anfang derPhase starten und dannach schließen
        //todo zu der Shopping-Phase wechseln
        Stage stage = new Stage();
        stage.setScene(new Scene(battleView));
        stage.show();
    }

    /**
     * Initialisiert Spieler und ihre zugehörigen Shops.
     */
    public void setPlayersAndShops() {
        for (int i = 0; i < PLAYER_COUNT; i++) {
            players[i] = new Player(i, 100, 1, 0, 10); // Example initialization values
            shopControllers[i] = new ShopController(players[i], unitPool); // Initialize ShopController with Player and UnitPool
        }
    }

    /**
     * Gibt die Werte aller Spieler in der Konsole aus (Debug-Zwecke).
     */
    public void outputPlayerValues() {
        for (Player player : players) {
            System.out.println("Player ID: " + player.getPlayerID());
            System.out.println("Health: " + player.getHealth());
            System.out.println("Level: " + player.getLevel());
            System.out.println("XP: " + player.getXp());
            System.out.println("Gold: " + player.getGold());
            System.out.println("Win Streak: " + player.getWinStreak());
            System.out.println("Loss Streak: " + player.getLossStreak());
            System.out.println("---------------------------");
        }
    }

    /**
     * @return Das Array der Spieler im Spiel.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @return Die Shop-Controller, die den Spielern zugeordnet sind.
     */
    public ShopController[] getShopControllers() {
        return shopControllers;
    }

    /**
     * Kauft eine Einheit aus dem Shop eines Spielers basierend auf dem Index.
     *
     * @param index  Index der Einheit im Shop.
     * @param player Der Spieler, der die Einheit kaufen möchte.
     */
    public void buyUnitOnIndex(int index, Player player) {
        int PlayerID = player.getPlayerID();
        int UnitID = shopControllers[PlayerID].getAvailableUnits().get(index).getId();
        boolean successful = shopControllers[PlayerID].buyUnit(UnitID);

        if (successful) {
            checkforUpgrades(player);
            updateGui();
        }
    }

    private void checkforUpgrades(Player player) {
        if (phase == Phase.Battle) return;

        List<Unit> bank = player.getBankUnits();
        List<Unit> board = player.getUnitsOnField();

        // Kombiniere alle Einheiten
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(bank);
        allUnits.addAll(board);

        // Gruppen nach ID und Stern-Level
        HashMap<Integer, HashMap<Integer, List<Unit>>> groupedUnits = new HashMap<>();
        for (Unit unit : allUnits) {
            if (unit.getStarLevel() < 3) {
                groupedUnits
                        .computeIfAbsent(unit.getId(), k -> new HashMap<>())
                        .computeIfAbsent(unit.getStarLevel(), k -> new ArrayList<>())
                        .add(unit);
            }
        }

        // Iterative Verarbeitung
        boolean upgraded;
        do {
            upgraded = false;

            for (Map.Entry<Integer, HashMap<Integer, List<Unit>>> entry : groupedUnits.entrySet()) {
                int unitID = entry.getKey();
                HashMap<Integer, List<Unit>> starLevelGroups = entry.getValue();

                for (Map.Entry<Integer, List<Unit>> starEntry : starLevelGroups.entrySet()) {
                    int starLevel = starEntry.getKey();
                    List<Unit> units = starEntry.getValue();

                    if (units.size() >= 2) {

                        bank.removeIf(unit -> unit.getId() == unitID && unit.getStarLevel() == starLevel);
                        board.removeIf(unit -> unit.getId() == unitID && unit.getStarLevel() == starLevel);

                        Unit tmp = units.getFirst(); // Nimm die erste Einheit
                        tmp.starUpUnit();

                        // Neue Einheit zur Bank hinzufügen
                        bank.add(tmp);

                        // Gruppen nach Upgrade aktualisieren
                        groupedUnits.clear();
                        allUnits.clear();
                        allUnits.addAll(bank);
                        allUnits.addAll(board);

                        for (Unit updatedUnit : allUnits) {
                            if (updatedUnit.getStarLevel() < 3) {
                                groupedUnits
                                        .computeIfAbsent(updatedUnit.getId(), k -> new HashMap<>())
                                        .computeIfAbsent(updatedUnit.getStarLevel(), k -> new ArrayList<>())
                                        .add(updatedUnit);
                            }
                        }

                        upgraded = true;
                        break;
                    }
                }
                if (upgraded) break;
            }
        } while (upgraded);
    }


    /**
     * Aktualisiert das Shop-Angebot eines Spielers.
     *
     * @param currentPlayer Der Spieler, dessen Shop aktualisiert werden soll.
     */
    public void refreshShop(Player currentPlayer) {
        int PlayerID = currentPlayer.getPlayerID();
        shopControllers[PlayerID].refreshShop();
        updateGui();
    }

    /**
     * Verschiebt eine Einheit auf dem Spielfeld eines Spielers.
     *
     * @param firstSelectedIndex Die Startposition der Einheit.
     * @param secondSelection    Die Zielposition der Einheit.
     * @param currentPlayer      Der Spieler, der die Einheit verschiebt.
     */
    /**
     * Verschiebt eine Einheit auf dem Spielfeld eines Spielers.
     *
     * @param firstSelectedIndex Die Startposition der Einheit.
     * @param secondSelection    Die Zielposition der Einheit.
     * @param currentPlayer      Der Spieler, der die Einheit verschiebt.
     */
    public void moveBoardToBoard(int firstSelectedIndex, int secondSelection, Player currentPlayer) {
        System.out.println("First: " + firstSelectedIndex);
        System.out.println("Second: " + secondSelection);

        int playerID = currentPlayer.getPlayerID();
        List<Unit> board = players[playerID].getUnitsOnField();

        int[][] positions = {
                {firstSelectedIndex % 10, firstSelectedIndex / 10},
                {secondSelection % 10, secondSelection / 10}
        };

        List<Unit> unitsToMove = board.stream()
                .filter(unit -> (unit.getPosX() == positions[0][0] && unit.getPosY() == positions[0][1]) ||
                        (unit.getPosX() == positions[1][0] && unit.getPosY() == positions[1][1]))
                .toList();

        if (unitsToMove.isEmpty()) {
            return;
        }

        for (int i = 0; i < unitsToMove.size(); i++) {
            Unit unit = unitsToMove.get(i);
            int targetIndex = (i + 1) % 2; // Wechselt zwischen den zwei Positionen
            unit.setPosX(positions[targetIndex][0]);
            unit.setPosY(positions[targetIndex][1]);
        }

        updateGui();

        System.out.println("Moving units on the board");
    }


    /**
     * Verschiebt eine Einheit vom Spielfeld in die Bank eines Spielers.
     *
     * @param firstSelectedIndex Die Position der Einheit auf dem Spielfeld.
     * @param secondSelection    Die Zielposition in der Bank.
     * @param currentPlayer      Der Spieler, der die Einheit verschiebt.
     */
    public void moveBoardToBank(int firstSelectedIndex, int secondSelection, Player currentPlayer) {
        int PlayerID = currentPlayer.getPlayerID();

        List<Unit> board = players[PlayerID].getUnitsOnField();
        List<Unit> bank = players[PlayerID].getBankUnits();

        //if(firstSelectedIndex >= board.size()) return;

        int x = firstSelectedIndex % 10;
        int y = firstSelectedIndex / 10;

        Unit Utmp = null;
        for(Unit tmp : board)
        {
            if(tmp.getPosX() == x && tmp.getPosY() == y)
            {
                Utmp = tmp;
                board.remove(tmp);
                break;
            }
        }
        if(Utmp == null) return;

        Utmp.setPosX(-1);
        Utmp.setPosY(-1);
        //todo secondSelection als Index fürs Setzen von bank machen
        bank.add(Utmp);
        updateGui();

        System.out.println("Im trying to move board to bank");
    }

    /**
     * Verschiebt eine Einheit von der Bank auf das Spielfeld eines Spielers.
     *
     * @param firstSelectedIndex Die Position der Einheit auf dem Spielfeld.
     * @param secondSelection    Die Zielposition in der Bank.
     * @param currentPlayer      Der Spieler, der die Einheit verschiebt.
     */
    public void moveFromBankToBoard(int firstSelectedIndex, int secondSelection, Player currentPlayer) {
        int PlayerID = currentPlayer.getPlayerID();

        if(phase == Phase.Battle) return;

        List<Unit> board = players[PlayerID].getUnitsOnField();
        List<Unit> bank = players[PlayerID].getBankUnits();

        if(board.size() >= players[PlayerID].getLevel()) return;
        if(firstSelectedIndex > bank.size() - 1) return;

        int x = secondSelection % 10;
        int y = secondSelection / 10;

        if (board.stream().anyMatch(u -> u.getPosX() == x && u.getPosY() == y)) {
            return;
        }

        Unit tmp = bank.get(firstSelectedIndex);
        tmp.setPosX(x);
        tmp.setPosY(y);
        board.add(tmp);

        bank.remove(firstSelectedIndex);
        updateGui();
        System.out.println("Im trying to move bank to board");
    }

    /**
     * Verschiebt eine Einheit von der Bank auf die Bank eines Spielers.
     *
     * @param firstIndex        Die Position der Einheit auf dem Spielfeld.
     * @param secondIndex       Die Zielposition in der Bank.
     * @param currentPlayer     Der Spieler, der die Einheit verschiebt.
     */
    public void moveFromBankToBank(int firstIndex, int secondIndex, Player currentPlayer) {
        int PlayerID = currentPlayer.getPlayerID();
        List<Unit> bank = players[PlayerID].getBankUnits();

        if(firstIndex == secondIndex) return;
        if(firstIndex >= bank.size() || secondIndex >= bank.size()) return;

        Unit tmp = bank.get(firstIndex);
        bank.set(firstIndex, bank.get(secondIndex));
        bank.set(secondIndex, tmp);


        updateGui();
        System.out.println("Im trying to move bank to bank");
    }

    public void setSixtySecondsTimers()
    {
        Arrays.stream(viewControllers)
                .forEach(vc -> vc.gameWindow.setTotalDuration(sixtySecondsTimer));
    }

    public void startGame() {
        setSixtySecondsTimers();
    }

    public void setThreadPools(ThreadPool[] threadPools)
    {
        this.threadPools = threadPools;
    }

    /**
     * Startet die Battling-Phase, in der die Einheiten eines Spielers kämpfen.
     *
     * @param currentPlayer Der Spieler, der an der Reihe ist.
     */
    public void BattlingPhase(Player currentPlayer)
    {
        //roundEndedCalled.set(false);
        int playerID = currentPlayer.getPlayerID();

        // 1. Positionen speichern
        saveUnitPositions();

        // 2. Einheiten von Spieler 1 spiegeln
        if (playerID == 1) {
            flipPlayerUnits(players[playerID].getUnitsOnField());
        }

        List<Unit> boardPlayer = players[playerID].getUnitsOnField();

        int enemyIndex = (currentPlayer == players[0]) ? 0 : 1;

        synchronized (this) {
            if (currentPlayer.getUnitsOnField().isEmpty()) {
                roundEnded(threadPools[currentPlayer.getPlayerID()]);
                return;
            }
            if (players[enemyIndex].getUnitsOnField().isEmpty()) {
                roundEnded(threadPools[enemyIndex]);
                return;
            }
        }

        for (Unit unit : boardPlayer) {
            UnitTask playerTask = new UnitTask(
                    unit,
                    battleView,
                    unitsUtil,
                    players[playerID]
            );
            threadPools[playerID].submitTask(playerTask);
        }

        battleView.setTotalDuration(sixtySecondsTimer);
        phase = Phase.Battle;
        updateGui();
    }

    // Speichert die Positionen aller Einheiten
    private void saveUnitPositions() {
        unitPositions.clear();
        for (Player player : players) {
            for (Unit unit : player.getUnitsOnField()) {
                unitPositions.put(unit.getUuid(), new int[]{unit.getPosX(), unit.getPosY()});
            }
        }
    }

    // Spiegelt die Einheiten eines Spielers an der X-Achse
    private void flipPlayerUnits(List<Unit> units) {
        for (Unit unit : units) {
            int flippedY = BattleView.SIZE - 1 - unit.getPosY(); // X-Achse spiegeln
            unit.setPosY(flippedY);
        }
    }

    // Positionen nach der Runde wiederherstellen
    public void restoreUnitPositions() {
        for (Player player : players) {
            for (Unit unit : player.getUnitsOnField()) {
                int[] originalPosition = unitPositions.get(unit.getUuid());
                if (originalPosition != null) {
                    unit.setPosX(originalPosition[0]);
                    unit.setPosY(originalPosition[1]);
                }
            }
        }
    }

    public void setGameForUnitTask(UnitsUtil unitsUtil) {
        this.unitsUtil = unitsUtil;
    }

    public void roundEnded(ThreadPool loser) {
        synchronized (this) {
            if (!roundEndedCalled.compareAndSet(false, true)) {
                return;
            }

            try {
                ThreadPool winner = determineWinner(loser);

                int winnerIndex = (winner == threadPools[0]) ? 0 : 1;
                int loserIndex = (loser == threadPools[0]) ? 0 : 1;

                if (winner == null) {
                    int enemyIndex = (loser == threadPools[1]) ? 0 : 1;
                    System.out.println("Kein Gewinner: Beide Spieler haben keine Einheiten.");
                    handleHealthReduction(loserIndex);
                    handleHealthReduction(enemyIndex);

                    if(players[enemyIndex].level < 10) {
                        players[enemyIndex].addXP(2);
                    }
                    players[enemyIndex].gold += 3;
                    players[enemyIndex].setWinStreak(0);

                    if(players[loserIndex].level < 10) {
                        players[loserIndex].addXP(2);
                    }
                    players[loserIndex].gold += 3;
                    players[loserIndex].setWinStreak(0);

                    resetForNextRound();
                }
                else if(winner != loser)
                {
                    System.out.println("Gewinner wird ermittelt...");

                    // Gewinner- und Verliererlogik anwenden
                    handlePlayerRewards(winnerIndex, loserIndex);
                    handleHealthReduction(loserIndex);
                    cancelAllTasks();

                    resetForNextRound();
                }

            } finally
            {
                Player death = gameOver();

                if(death != null)
                {
                    Platform.runLater(() -> {
                        if(death == players[0] )
                        {
                            viewControllers[0].setAlert("Loss", 0);
                            viewControllers[1].setAlert("Win", 1);
                        }
                        else if (death == players[1])
                        {
                            viewControllers[1].setAlert("Loss", 1);
                            viewControllers[0].setAlert("Win", 0);
                        }
                    });
                    sixtySecondsTimer = Long.MAX_VALUE;
                }
            }
        }
    }

    // Gewinnerlogik
    private ThreadPool determineWinner(ThreadPool loser) {
        boolean player0HasUnits = !players[0].getUnitsOnField().isEmpty();
        boolean player1HasUnits = !players[1].getUnitsOnField().isEmpty();

        if(!player0HasUnits && !player1HasUnits)
            return null;

        if (!player0HasUnits)
            return threadPools[1];

        if (!player1HasUnits)
            return threadPools[0];


        for (ThreadPool pool : threadPools) {
            if (pool != loser) {
                return pool;
            }
        }

        return null;
    }


    // Methode für Spielerbelohnungen
    private void handlePlayerRewards(int winnerIndex, int loserIndex) {
        if(winnerIndex == -1 || loserIndex == -1) return;

        Player winner = players[winnerIndex];
        Player loser = players[loserIndex];

        // Gewinnen/Verlieren-Streaks
        winner.setWinStreak(winner.getWinStreak() + 1);
        winner.setLossStreak(0);

        loser.setLossStreak(loser.getLossStreak() + 1);
        loser.setWinStreak(0);

        caculateEarnings(winner);
        caculateEarnings(loser);
        // Belohnungen
        winner.gold += 6;
        loser.gold += 4;

        if(winner.level < 10) {
            winner.addXP(2);
        }
        if(winner.level < 10) {
            loser.addXP(2);
        }
    }

    // Methode zur Gesundheitsreduktion
    private void handleHealthReduction(int loserIndex) {
        players[loserIndex].decreaseHealth(10);
    }

    // Alle laufenden Aufgaben abbrechen
    private void cancelAllTasks() {
        for (ThreadPool threadPool : threadPools) {
            threadPool.cancelSubmittedTasks();
        }
    }

    // Vorbereitung für die nächste Runde
    private void resetForNextRound() {
        Platform.runLater(() -> {
            restoreUnitPositions();
            phase = Phase.Shopping;
            for (Player player : players) {
                player.getUnitsOnField().forEach(Unit::resetForNextRound);
                viewControllers[player.getPlayerID()].increaseLevel();
                refreshShop(player);
                checkforUpgrades(player);
            }

            clearBattleView();
            updateGui();
            setSixtySecondsTimers();
            roundEndedCalled.set(false);
        });
    }

    // GUI-Komponenten zurücksetzen
    private void clearBattleView() {
        battleView.getTextArea().clear();
        battleView.resetBattleView();
    }

    /**
     * Aktualisiert die GUI basierend auf dem aktuellen Spielzustand.
     */
    public void updateGui()
    {
        for(int i = 0; i < 2; i++)
        {
            int PlayerEnemyID = (i == 0) ? 1 : 0;

            if(phase == Phase.Shopping)
            {
                viewControllers[i].updateGameWindow(new GameState(
                        players[i].getBankUnits(),
                        shopControllers[i].getAvailableUnits(),
                        players[PlayerEnemyID].getBankUnits(),
                        players[i].getUnitsOnField(),
                        players[i].getGold(),
                        players[i].getXp() + "/" + Player.LEVEL_UP_XP_REQUIREMENTS[players[i].getLevel()],
                        players[i].getLevel(),
                        players[i].getHealth(),
                        i,
                        players[i].getWinStreak(),
                        players[i].getLossStreak()
                ));
            }
            else {
                viewControllers[i].updateGameWindow(new GameState(
                        players[i].getBankUnits(),
                        shopControllers[i].getAvailableUnits(),
                        players[PlayerEnemyID].getBankUnits(),
                        new ArrayList<Unit>(),
                        players[i].getGold(),
                        players[i].getXp() + "/" + Player.LEVEL_UP_XP_REQUIREMENTS[players[i].getLevel()],
                        players[i].getLevel(),
                        players[i].getHealth(),
                        i,
                        players[i].getWinStreak(),
                        players[i].getLossStreak()
                ));
            }

        }
    }

    /**
     * Kauft 4Xp für 4 Gold aus dem Shop
      * @param currentPlayer aktueller Spieler
     */
    public void purchaseXp(Player currentPlayer) {
        shopControllers[currentPlayer.getPlayerID()].purchaseXP();
        updateGui();
    }

    public void showInfoOfUnit(int unitIndex, Player player, String flag) {
        int PlayerID = player.getPlayerID();
        if(flag.equals("Shop"))
        {
            if(unitIndex >= shopControllers[PlayerID].getAvailableUnits().size())
                return;

            Unit InfoOfUnit = shopControllers[PlayerID].getAvailableUnits().get(unitIndex);
            viewControllers[PlayerID].gameWindow.showInfoOfUnit(InfoOfUnit);
        }
        else if(flag.equals("Bank"))
        {
            if(unitIndex >= players[PlayerID].getBankUnits().size())
                return;

            viewControllers[PlayerID].gameWindow.showInfoOfUnit(players[PlayerID].getBankUnits().get(unitIndex));
        }
        else if(flag.equals("Board"))
        {
            if(unitIndex >= 10*10)
                return;

            List<Unit> board = players[PlayerID].getUnitsOnField();

            int x = unitIndex % 10;
            int y = unitIndex / 10;

            Unit Utmp = null;
            for(Unit tmp : board)
            {
                if(tmp.getPosX() == x && tmp.getPosY() == y)
                {
                    Utmp = tmp;
                    break;
                }
            }
            if(Utmp == null) return;

            viewControllers[PlayerID].gameWindow.showInfoOfUnit(Utmp);
        }
    }

    public void sellUnit(Player currentPlayer, int selectedIndex) {
        if(selectedIndex >= currentPlayer.getBankUnitsCount())
            return;

        List<Unit> bankUnits = currentPlayer.getBankUnits();
        int PlayerID = currentPlayer.getPlayerID();

        if(shopControllers[PlayerID].sellUnit(bankUnits.get(selectedIndex).getId()))
        {
            System.out.println("Verkauf war erfolgreich!");
            updateGui();
        }
    }

    public Player gameOver() {
        for (Player player : players) {
            if (player.getHealth() <= 0)
                return player;
        }
        return null;
    }

    public void caculateEarnings(Player currentPlayer){
        if(currentPlayer == null) return;
        int currentGold = currentPlayer.getGold();

        //interest
        int interest = currentGold / 10;
        currentGold += interest;

        //streak
        int winstreak = currentPlayer.getWinStreak();
        int losestreak = currentPlayer.getLossStreak();

        currentGold = streaks(currentGold, winstreak);
        currentGold = streaks(currentGold, losestreak);

        currentPlayer.setGold(currentGold);
    }

    private int streaks(int currentGold, int streaks) {
        switch (streaks) {
            case 3: currentGold += 1; break;
            case 4: currentGold += 2; break;
            default:
                if (streaks >= 5) {
                    currentGold += 3;
                }
                break;
        }
        return currentGold;
    }
}