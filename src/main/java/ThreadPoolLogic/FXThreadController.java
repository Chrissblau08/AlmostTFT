package ThreadPoolLogic;

import javafx.application.Platform;
import model.Unit;

/**
 * Die Klasse FXThreadController steuert die Aktualisierung der GUI
 * innerhalb eines JavaFX-Anwendungsthreads.
 */
public class FXThreadController {

    private final Thread fxThread;

    /**
     * Konstruktor, der einen JavaFX-Thread initialisiert.
     *
     * @param fxThread Der Thread, der die JavaFX-Anwendung ausführt.
     */
    public FXThreadController(Thread fxThread) {
        this.fxThread = fxThread;
    }

    /**
     * Aktualisiert die Benutzeroberfläche.
     * Diese Methode sollte verwendet werden, um allgemeine GUI-Elemente
     * zu aktualisieren. Alle Änderungen an der GUI müssen im JavaFX-Thread
     * durchgeführt werden.
     */
    public void refreshGui() {
        Platform.runLater(() -> {
            System.out.println("GUI wird aktualisiert.");
            //todo
        });
    }

    /**
     * Fügt eine Einheit zur Anzeige im GUI-Pane hinzu.
     *
     * @param unit Die Einheit, die zur Benutzeroberfläche hinzugefügt werden soll.
     */
    public void updateUnitInPane(Unit unit) {
        Platform.runLater(() -> {
            System.out.println("Einheit hinzugefügt: " + unit);
            //todo
        });
    }

    /**
     * Entfernt eine Einheit aus dem GUI-Pane.
     *
     * @param unit Die Einheit, die aus der Benutzeroberfläche entfernt werden soll.
     */
    public void removeUnitInPane(Unit unit) {
        Platform.runLater(() -> {
            System.out.println("Einheit entfernt: " + unit);
            //todo
        });
    }
}
