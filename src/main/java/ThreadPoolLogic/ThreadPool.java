package ThreadPoolLogic;

import controller.GameController;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

public class ThreadPool {
    private final GameController gameController;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    public final AtomicInteger runningTasks; // Anzahl der gerade laufenden Tasks
    private final int FXThreads = 1;
    private final List<Future<?>> submittedTasks; // Liste der submitted Tasks

    public ThreadPool(GameController gameController, int maxThreads) {
        this.gameController = gameController;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.scheduler = Executors.newScheduledThreadPool(FXThreads); // Ein Thread für GUI-Updates
        this.runningTasks = new AtomicInteger(0);
        this.submittedTasks = new ArrayList<>();
    }

    /**
     * Führt eine Aufgabe im Thread-Pool aus und speichert das Future-Objekt.
     */
    public void submitTask(UnitTask task) {
        runningTasks.incrementAndGet(); // Zähle die laufenden Tasks hoch
        Future<?> future = executorService.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (runningTasks.decrementAndGet() == 0) {
                    cleanupCompletedTasks();
                    triggerAllTasksCompletedEvent();
                }
            }
        });

        synchronized (submittedTasks) {
            submittedTasks.add(future); // Speichere das Future-Objekt
        }
    }

    /**
     * Entfernt abgeschlossene Tasks aus der Liste.
     */
    public void cleanupCompletedTasks() {
        synchronized (submittedTasks) {
            submittedTasks.removeIf(Future::isDone); // Entferne alle erledigten Tasks
        }
        System.out.println("Abgeschlossene Tasks wurden bereinigt.");
    }

    /**
     * Löst das Event aus, wenn alle Aufgaben abgeschlossen sind.
     */
    private void triggerAllTasksCompletedEvent() {
        try {
            System.out.println("Alle Aufgaben abgeschlossen!");
            gameController.roundEnded(this);
        } catch (Exception e) {
            System.err.println("Fehler beim Auslösen von roundEnded: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Beendet nur die submitteten Tasks und blockiert keine neuen Tasks.
     */
    public void cancelSubmittedTasks() {
        synchronized (submittedTasks) {
            for (Future<?> future : submittedTasks) {
                if (!future.isDone()) {
                    future.cancel(true); // Versuche, die Task abzubrechen
                }
            }
        }
        System.out.println("Alle submitteten Tasks wurden abgebrochen.");
    }

    /**
     * Beendet den Thread-Pool und den Scheduler.
     */
    public void shutdown() {
        // Stoppe den ExecutorService
        executorService.shutdownNow(); // Unterbricht laufende Tasks
        scheduler.shutdownNow(); // Unterbricht Scheduler-Threads

        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Executor konnte nicht sauber beendet werden.");
            }
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Scheduler konnte nicht sauber beendet werden.");
            }
        } catch (InterruptedException e) {
            System.out.println("Shutdown unterbrochen.");
            Thread.currentThread().interrupt();
        }
        System.out.println("ThreadPool wurde beendet.");
    }

    /**
     * Gibt die Anzahl der aktuell laufenden Tasks zurück.
     */
    public int getRunningTaskCount() {
        return runningTasks.get();
    }

    /**
     * Gibt die Anzahl der eingereichten Tasks zurück.
     */
    public int getSubmittedTaskCount() {
        synchronized (submittedTasks) {
            return submittedTasks.size();
        }
    }

    /**
     * Plant eine Aufgabe, die nach einer Verzögerung ausgeführt wird.
     */
    public void scheduleTask(UnitTask task, long delay, TimeUnit unit) {
        scheduler.schedule(() -> submitTask(task), delay, unit);
    }
}
