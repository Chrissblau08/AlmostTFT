package ThreadPoolLogic;

import controller.GameController;
import controller.ViewController;
import javafx.application.Platform;
import model.Player;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBoardController
{
    private static final Logger logger = Logger.getLogger(MyBoardController.class.getName());
    private static final int PHASE_DURATION = 30; // Dauer jeder Phase in Sekunden

    private final GameController gameController;
    private Player[] players;
    private ViewController viewController;
    private int round = 0;
    private boolean isCombatPhase = true;

    private final ExecutorService phaseExecutor;

    public MyBoardController(GameController gameController) {
        this.gameController = gameController;
        this.phaseExecutor = Executors.newFixedThreadPool(3); // 3 Threads für die Phasen

        logger.info("MyBoardController initialisiert");
    }

    public void startGame() {
        while (true) {
            try {
                startPhaseThreads();  // Starte die Threads für jede Phase
                round++;  // Nächste Runde
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Fehler beim Ausführen der Spielphasen", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void startPhaseThreads() throws InterruptedException {
        // Starte und überwache die Einkaufs-, Kampf- und Rundenend-Threads nacheinander
        phaseExecutor.submit(this::runCombatPhase);
        phaseExecutor.submit(this::runEndRoundPhase);

        // Warten, bis alle Phasen abgeschlossen sind (30 Sekunden pro Phase)
        phaseExecutor.awaitTermination(PHASE_DURATION * 3L, TimeUnit.SECONDS);
    }


    private void runCombatPhase() {
        try {
            logger.info("Kampfphase für Runde " + round);
            isCombatPhase = true;
            // Logik für die Kampfphase (z.B. Einheiten kämpfen)
            Platform.runLater(this::refreshGui);
            Thread.sleep(TimeUnit.SECONDS.toMillis(PHASE_DURATION));
            isCombatPhase = false;
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Kampfphase unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }

    private void runEndRoundPhase() {
        try {
            logger.info("Ende der Runde " + round);
            // Logik für das Ende der Runde (z.B. Gold verdienen, HP verwalten)
            Platform.runLater(this::refreshGui);
            Thread.sleep(TimeUnit.SECONDS.toMillis(PHASE_DURATION));
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Endrunde unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }

    private void refreshGui() {
        logger.info("GUI aktualisieren.");
        // GUI-Elemente aktualisieren
    }
}
