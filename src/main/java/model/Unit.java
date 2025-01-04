package model;

import java.util.UUID;

public class Unit {
    private transient UUID uuid;
    private int id;
    private String name;
    private String className;
    private int hp;
    private int attack;
    private int defense;
    private double attackSpeed;
    private int attackReach;
    private int cost;
    private int PosX, PosY;
    private int MaxHp;

    // Copy-Konstruktor
    public Unit(Unit other) {
        this.uuid = other.uuid;
        this.id = other.id;
        this.name = other.name;
        this.className = other.className;
        this.hp = other.hp;
        this.attack = other.attack;
        this.defense = other.defense;
        this.attackSpeed = other.attackSpeed;
        this.attackReach = other.attackReach;
        this.cost = other.cost;
        this.PosX = other.PosX;
        this.PosY = other.PosY;
        this.MaxHp = other.MaxHp;
    }


    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getAttackReach() {
        return attackReach;
    }

    public void setAttackReach(int attackReach) {
        this.attackReach = attackReach;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPosX() {
        return PosX;
    }

    public void setPosX(int posX) {
        PosX = posX;
    }

    public int getPosY() {
        return PosY;
    }

    public void setPosY(int posY) {
        PosY = posY;
    }

    public int getMaxHp() {
        return MaxHp;
    }

    public void setMaxHp(int maxHp) {
        MaxHp = maxHp;
    }

    public void resetForNextRound() {
        this.hp = this.MaxHp;
    }

    public void setUuid(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}