package model;

import controller.GameController;
import javafx.application.Platform;
import utility.Graph;

import java.util.List;
import java.util.Random;

public class UnitsUtil {
    GameController controller;
    Graph graph;
    Unit target;
    int widht, height;

    Player[] players;

    /**
     * Constructor for the UnitsUtil class.
     *
     * @param gameController the GameController instance
     * @param widht the width of the game field
     * @param height the height of the game field
     * @param players the array of players in the game
     */
    public UnitsUtil(GameController gameController, int widht, int height, Player[] players) {
        this.controller = gameController;
        this.graph = new Graph(widht, height);
        this.widht = widht;
        this.height = height;
        this.players = players;
    }

    /**
     * Sets a new target for the given unit from the enemy player's units on the field.
     *
     * @param sourceUnit the unit that needs a target
     * @param sourcePlayer the player who owns the source unit
     */
    public void setNewTarget(Unit sourceUnit, Player sourcePlayer) {
        if (sourceUnit.getHp() <= 0) return;

        List<Unit> units = getTargetPlayer(sourcePlayer).getUnitsOnField();

        if (!units.isEmpty()) {
            Unit unit = units.get(new Random().nextInt(units.size()));
            target = unit;
            log("New target set at (" + target.getPosX() + ", " + target.getPosY() + ")");
        }
    }

    /**
     * Moves the unit towards its current target.
     * If the target becomes invalid or unreachable, a new target is set.
     *
     * @param unit the unit to move
     * @param sourcePlayer the player who owns the unit
     */
    public void move(Unit unit, Player sourcePlayer) {
        if (unit.getHp() <= 0) {
            return;
        }

        List<int[]> path = graph.dijkstra(unit.getPosX(), unit.getPosY(), target.getPosX(), target.getPosY());

        if (!path.isEmpty()) {
            int[] nextPosition = path.getFirst();
            if (isPositionOccupied(nextPosition[0], nextPosition[1])) {
                setNewTarget(unit, sourcePlayer);
                return;
            }

            // Check if the next position is within the field bounds
            if (isWithinBounds(nextPosition)) {
                unit.setPosX(nextPosition[0]);
                unit.setPosY(nextPosition[1]);
                log("Moved to (" + unit.getPosX() + ", " + unit.getPosY() + ")");
            } else {
                setNewTarget(unit, sourcePlayer);
                move(unit, sourcePlayer);
            }
        }
    }

    /**
     * Checks if a given position is occupied by another unit.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return true if the position is occupied, false otherwise
     */
    private boolean isPositionOccupied(int x, int y) {
        for (Player player : players) {
            for (Unit unit : player.getUnitsOnField()) {
                if (unit.getPosX() == x && unit.getPosY() == y) {
                    return true; // A unit occupies this position
                }
            }
        }
        return false;
    }

    /**
     * Executes an attack from the source unit on its target.
     * If the target is defeated, it is removed from the game field.
     *
     * @param sourceUnit the attacking unit
     * @param currentPlayer the player who owns the source unit
     */
    public void attack(Unit sourceUnit, Player currentPlayer) {
        if (target != null && target.getHp() > 0 && sourceUnit != target) {
            log("Attacking target: " + target.getName() + " at (" + target.getPosX() + ", " + target.getPosY() + ")");
            takeDamage(target, sourceUnit.getAttack());
            if (target.getHp() <= 0) {
                log("Target " + target.getName() + " defeated!");
                //Platform.runLater(() -> controller.removeUnit(target, currentPlayer));
            }
        }
    }

    /**
     * Calculates and applies damage to the target, considering its armor.
     *
     * @param target the unit receiving damage
     * @param damage the raw damage dealt
     */
    public void takeDamage(Unit target, int damage) {
        int armor = target.getDefense();
        double damageMultiplier = 1.0 / (1.0 + (armor / 100.0));
        int effectiveDamage = (int) Math.round(damage * damageMultiplier);
        target.setHp(target.getHp() - effectiveDamage);
        log("Took " + damage + " damage, remaining HP: " + target.getHp());
        if (target.getHp() <= 0) {
            log("Died and marked as dead.");
        }
    }

    /**
     * Checks if a given position is within the field bounds.
     *
     * @param position an array containing the x and y coordinates
     * @return true if the position is within bounds, false otherwise
     */
    private boolean isWithinBounds(int[] position) {
        return position[0] >= 0 && position[0] < widht &&
                position[1] >= 0 && position[1] < height;
    }

    /**
     * Retrieves the enemy player for the given source player.
     *
     * @param sourcePlayer the player requesting the target
     * @return the enemy player
     */
    private Player getTargetPlayer(Player sourcePlayer) {
        return players[0] == sourcePlayer ? players[1] : players[0];
    }

    /**
     * Logs a message with thread information.
     *
     * @param message the message to log
     */
    private void log(String message) {
        System.out.println("[" + Thread.currentThread().getName() + "] " + ": " + message);
    }

    /**
     * Calculates the Euclidean distance between two units.
     *
     * @param unit1 the first unit
     * @param unit2 the second unit
     * @return the distance between the two units
     */
    public double calculateDistance(Unit unit1, Unit unit2) {
        int dx = unit1.getPosX() - unit2.getPosX();
        int dy = unit1.getPosY() - unit2.getPosY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets the current target of the unit.
     *
     * @return the target unit
     */
    public Unit getTarget() {
        return target;
    }
}
