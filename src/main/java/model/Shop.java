package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shop {

    private List<Unit> availableUnits;
    private UnitPool unitPool;
    private Player player;
    private Random random;

    public Shop(Player player, UnitPool unitPool) {
        this.player = player;
        this.unitPool = unitPool;
        this.random = new Random();
        this.availableUnits = new ArrayList<>();
        refreshShop();
    }

    public void refreshShop() {
        availableUnits.clear();
        for (int i = 0; i < 5; i++) {
            availableUnits.add(getRandomUnit());
        }
    }

    // Get a random unit based on player level
    private Unit getRandomUnit() {
        int level = player.getLevel();
        // Define the probabilities (percentages) as arrays for each level
        int[][] probabilities = {
                {100, 0, 0, 0, 0},  // Level 1
                {100, 0, 0, 0, 0},  // Level 2
                {75, 25, 0, 0, 0},  // Level 3
                {55, 30, 15, 0, 0}, // Level 4
                {45, 33, 20, 2, 0}, // Level 5
                {30, 40, 25, 5, 0}, // Level 6
                {19, 30, 40, 10, 1}, // Level 7
                {18, 25, 32, 22, 3}, // Level 8
                {15, 20, 25, 30, 10}, // Level 9
                {5, 10, 20, 40, 25}, // Level 10
                {1, 2, 12, 50, 35}  // Level 11
        };

        int[] currentLevelProb = probabilities[level - 1];
        int rnd = random.nextInt(100) + 1;

        int cumulativeProbability = 0;
        for (int cost = 1; cost <= 5; cost++) {
            cumulativeProbability += currentLevelProb[cost - 1];
            if (rnd <= cumulativeProbability) {
                return unitPool.getRandomUnitByCost(cost);
            }
        }

        // Fallback case
        return unitPool.getRandomUnit();
    }

    public List<Unit> getAvailableUnits() {
        return availableUnits;
    }

    // Method to buy a unit
    public boolean buyUnit(int unitId) {
        for (Unit unit : availableUnits) {
            if (unit.getId() == unitId) {
                boolean removed = unitPool.removeUnitById(unitId);
                if (removed) {
                    //player.addUnitToBank(unit);
                    availableUnits.remove(unit);
                    return true;
                }
            }
        }
        return false;
    }
}