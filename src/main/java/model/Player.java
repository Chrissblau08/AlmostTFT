package model;

import java.util.ArrayList;
import java.util.List;

public class Player {


    public int health;
    public int level;
    public int xp;
    public int gold;
    public int winStreak;
    public int lossStreak;
    private final int playerID;

    private List<Unit> unitsOnField;
    private List<Unit> unitsOnBank;

    public static final int[] LEVEL_UP_XP_REQUIREMENTS = {0, 2, 4, 6, 10, 20, 36, 48, 72, 84};


    public Player(int playerID, int initalHealth, int initalLevel, int initalXP, int initalGold){
        this.unitsOnField = new ArrayList<>();
        this.unitsOnBank = new ArrayList<>();
        this.playerID = playerID;
        this.health = initalHealth;
        this.level = initalLevel;
        this.xp = initalXP;
        this.gold = initalGold;
    }

    public Player(Player other) {
        this.playerID = other.playerID;
        this.health = other.health;
        this.level = other.level;
        this.xp = other.xp;
        this.gold = other.gold;
        this.unitsOnField = new ArrayList<>(other.unitsOnField);
        this.unitsOnBank = new ArrayList<>(other.unitsOnBank);
    }


    // Getter for Player ID
    public int getPlayerID() {
        return playerID;
    }

    // Getters and Setters for Player attributes
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        System.out.println("Player"  + this.playerID + " has reached level: " + this.level);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getLossStreak() {
        return lossStreak;
    }

    public void setLossStreak(int lossStreak) {
        this.lossStreak = lossStreak;
    }


    // Method to add XP and handle level up
    public void addXP(int xpToAdd) {
        this.xp += xpToAdd;
        while (level < LEVEL_UP_XP_REQUIREMENTS.length && this.xp >= LEVEL_UP_XP_REQUIREMENTS[level]) {
            this.xp -= LEVEL_UP_XP_REQUIREMENTS[level];
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        System.out.println("Player " + playerID + " leveled up to level " + level);
    }

    // Method to add a unit to the bank
    public void addUnitToBank(Unit unit) {
        if (unitsOnBank.size() < 10) { // Limit to 10 units
            unitsOnBank.add(unit);
        } else {
            System.out.println("Cannot add more units to the bank. Limit reached.");
        }
    }

    // Method to remove a unit from the bank and add it to the field
    public void moveUnitFromBankToField(Unit unit) {
        if (unitsOnBank.contains(unit)) {
            unitsOnBank.remove(unit);
            unitsOnField.add(unit);
        } else {
            throw new IllegalArgumentException("Unit not found in bank");
        }
    }

    // Method to get units from the bank
    public List<Unit> getBankUnits() {
        return unitsOnBank;
    }

    // Method to get the count of units in the bank
    public int getBankUnitsCount() {
        return unitsOnBank.size();
    }

    // Method to get units on the field
    public List<Unit> getUnitsOnField() {
        return unitsOnField;
    }


    public void decreaseHealth(int i) {
        health -= i;
    }
}
