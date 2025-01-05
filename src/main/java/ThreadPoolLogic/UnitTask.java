package ThreadPoolLogic;

import javafx.application.Platform;
import model.UnitsUtil;
import model.Player;
import model.Unit;
import view.BattleView;

/**
 * UnitTask handles the behavior of a single unit in the game during battle.
 * The unit alternates between moving and attacking based on its target's position.
 */
public class UnitTask implements Runnable {
    private final Player currentPlayer;
    private final Unit unit;
    private final BattleView FXcontroller;
    private final UnitsUtil unitsUtil;

    /**
     * Constructor for UnitTask.
     *
     * @param unit the unit associated with this task
     * @param battleView the BattleView for GUI updates
     * @param unitsUtil the utility class for unit operations
     * @param player the player controlling the unit
     */
    public UnitTask(Unit unit, BattleView battleView, UnitsUtil unitsUtil, Player player) {
        this.unit = unit;
        this.FXcontroller = battleView;
        this.unitsUtil = unitsUtil;
        this.currentPlayer = player;
    }

    /**
     * Executes the task for the unit. The unit searches for a target, moves towards it if out of range,
     * and attacks it if within range. If the unit or its target dies, the task ends.
     */
    @Override
    public void run() {
        unitsUtil.setNewTarget(unit, currentPlayer);
        FXcontroller.addTextToTextBox("Unit: " + unit.getName() + " hat als Ziel diese Unit: " + unitsUtil.getTarget(unit).getName() + " gewählt!");

        while (unit.getHp() > 0) {
            if (unit.getHp() <= 0) {
                Platform.runLater(() -> FXcontroller.removeUnit(unit));
                Thread.currentThread().interrupt();
                break;
            }
            if (unitsUtil.getTarget(unit) == null || unitsUtil.getTarget(unit).getHp() <= 0) {
                unitsUtil.setNewTarget(unit, currentPlayer);
            }

            if (unitsUtil.getTarget(unit) == null) {
                break;
            }

            double distance = unitsUtil.calculateDistance(unit, unitsUtil.getTarget(unit));

            if (distance <= unit.getAttackReach()) {
                try {
                    long pauseDuration = (long) Math.max(100, 1000 - (unit.getAttackSpeed() * 10));
                    Thread.sleep(pauseDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                FXcontroller.addTextToTextBox("Unit: " + unit.getName() + " greift die Unit: " + unitsUtil.getTarget(unit).getName() + " an!");
                unitsUtil.attack(unit, currentPlayer);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                FXcontroller.addTextToTextBox("Unit: " + unit.getName() + " läuft auf " + unitsUtil.getTarget(unit).getName() + " zu!");
                unitsUtil.move(unit, currentPlayer);
            }

            Platform.runLater(() -> FXcontroller.update(unit));



            if (unit.getHp() <= 0) {
                Platform.runLater(() -> FXcontroller.removeUnit(unit));
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
