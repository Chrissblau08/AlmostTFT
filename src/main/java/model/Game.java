package model;

import ThreadPoolLogic.ThreadPool;
import controller.GameController;
import controller.ShopController;
import controller.ViewController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game
{
    private UnitPool unitPool;
    private ViewController viewControllers[];
    private GameController gameController;

    private ShopController[] shopControllers;
    private UnitsUtil unitsUtil;
    private ThreadPool threadPools[];
    private final int THREAD_POOL_SIZE = 10;
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 10;

    public Game() {
        initializeGame();
        configureShopAndPlayerView();
        startGame();
    }

    private void startGame()
    {
        gameController.startGame();
    }

    private void initializeGame() {
        // Lade Einheiten aus der Datei
        unitPool = new UnitPool("src/main/java/files/characters.json");

        // Erstelle View und GameController
        viewControllers = new ViewController[2];
        viewControllers[0] = new ViewController(0);
        viewControllers[1] = new ViewController(1);
        gameController = new GameController(unitPool, viewControllers);

        // Initialisiere das Spielfeld (Board), Spieler und deren Einheiten
        gameController.setPlayersAndShops();
        gameController.outputPlayerValues();

        Stage stage1 = new Stage();
        stage1.setScene(new Scene(viewControllers[0].gameWindow)); // Hier kannst du deine spezifische View oder Layout setzen
        stage1.show(); // Zeigt die erste Stage an

        // Zweite Stage und Scene
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(viewControllers[1].gameWindow)); // Eine andere View für die zweite Stage
        stage2.show();

        threadPools = new ThreadPool[2];
        threadPools[0] = new ThreadPool(gameController, THREAD_POOL_SIZE);
        threadPools[1] = new ThreadPool(gameController, THREAD_POOL_SIZE);
        gameController.setThreadPools(threadPools);

        unitsUtil = new UnitsUtil(gameController, BOARD_WIDTH, BOARD_HEIGHT, gameController.getPlayers());
        gameController.setGameForUnitTask(unitsUtil);
    }

    private void configureShopAndPlayerView() {
        shopControllers = new ShopController[2];
        shopControllers[0] = gameController.getShopControllers()[0];
        shopControllers[1] = gameController.getShopControllers()[1];

        // Update der Shop-Ansicht für beide Spieler
        viewControllers[0].gameWindow.updateShopView(shopControllers[0].getAvailableUnits());
        viewControllers[1].gameWindow.updateShopView(shopControllers[1].getAvailableUnits());

        // Setzen der aktuellen Spielerinformationen und Gold für beide Spieler
        viewControllers[0].setCurrentPlayer(gameController.getPlayers()[0]);
        viewControllers[0].setPlayerGold(gameController.getPlayers()[0].getGold());

        viewControllers[1].setCurrentPlayer(gameController.getPlayers()[1]);
        viewControllers[1].setPlayerGold(gameController.getPlayers()[1].getGold());

        gameController.updateGui();
    }
}
