package test;

import ThreadPoolLogic.ThreadPool;
import controller.GameController;
import controller.ShopController;
import controller.ViewController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UnitsUtil;
import model.UnitPool;

public class RundenAblaufTest extends Application
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

    public RundenAblaufTest()
    {

    }



    @Override
    public void start(Stage stage) throws Exception {
        // Titel des Fensters setzen
        stage.setTitle("Game Simulation");

        // Initialisiere das Spiel und die zugehörigen Controller
        initializeGame(stage);
        configureShopAndPlayerView();
        //stage.show();
        // Starte das Spiel
        startGame();
        // Zeige das Fenster an
    }

    private void startGame()
    {
        gameController.startGame();
        //gameController.setSixtySecondsTimers();
    }

    private void initializeGame(Stage stage) {
        // Lade Einheiten aus der Datei
        unitPool = new UnitPool("src/main/java/files/characters.json");

        // Erstelle View und GameController
        viewControllers = new ViewController[2];
        viewControllers[0] = new ViewController();
        viewControllers[1] = new ViewController();
        gameController = new GameController(unitPool, viewControllers);

        // Initialisiere das Spielfeld (Board), Spieler und deren Einheiten
        gameController.setPlayersAndShops();
        gameController.outputPlayerValues();

        // Erstelle und konfiguriere das Game-Objekt


        Stage stage1 = new Stage();
        stage1.setScene(new Scene(viewControllers[0].gameWindow)); // Hier kannst du deine spezifische View oder Layout setzen
        stage1.show(); // Zeigt die erste Stage an

        // Zweite Stage und Scene
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(viewControllers[1].gameWindow)); // Eine andere View für die zweite Stage
        stage2.show(); // Zeigt die zweite Stage an

        // Initialisiere das Thread-Pool-Objekt mit festgelegter Größe
        threadPools = new ThreadPool[2];
        threadPools[0] = new ThreadPool(gameController, THREAD_POOL_SIZE);
        threadPools[1] = new ThreadPool(gameController, THREAD_POOL_SIZE);
        gameController.setThreadPools(threadPools);

        unitsUtil = new UnitsUtil(gameController, BOARD_WIDTH, BOARD_HEIGHT, gameController.getPlayers());
        gameController.setGameForUnitTask(unitsUtil);
    }

    private void configureShopAndPlayerView() {
        // Holen des ShopControllers für den ersten und zweiten Spieler
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
