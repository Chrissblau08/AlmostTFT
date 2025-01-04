package controller;

import model.Player;
import model.Shop;
import model.Unit;
import model.UnitPool;

import java.util.List;

public class ShopController {

    private Shop shop;
    private Player player;

    public ShopController(Player player, UnitPool unitPool) {
        this.player = player;
        this.shop = new Shop(player, unitPool);
    }

    public void refreshShop() {
        shop.refreshShop();
        System.out.println("Shop refreshed for player " + player.getPlayerID());
    }

    public List<Unit> getAvailableUnits() {
        return shop.getAvailableUnits();
    }

    public boolean purchaseXP() {
        if (player.getGold() >= 4) {
            player.setGold(player.getGold() - 4);
            player.addXP(4);
            return true;
        } else {
            System.out.println("Not enough gold to buy XP.");
            return false;
        }
    }

    public boolean buyUnit(int unitId) {
        if (isBenchFull()) {
            System.out.println("Cannot buy more units. Player's bank is full.");
            return false;
        }

        Unit unitToBuy = shop.getAvailableUnits().stream()
                .filter(unit -> unit.getId() == unitId)
                .findFirst()
                .orElse(null);

        if (unitToBuy == null) {
            System.out.println("Unit not found in shop.");
            return false;
        }

        if (!canAffordUnit(unitToBuy)) {
            System.out.println("Not enough gold to buy this unit.");
            return false;
        }

        // Deduct the price from the player's gold and attempt to add the unit in a single step
        player.setGold(player.getGold() - unitToBuy.getCost());
        boolean shopCanSell = shop.buyUnit(unitId);

        if (shopCanSell) {
            player.addUnitToBank(unitToBuy);
            System.out.println("Player " + player.getPlayerID() + " successfully purchased unit: " + unitToBuy.getName());
            return true;
        } else {
            System.out.println("Shop failed to sell unit.");
            // Refund the player if the shop couldn't sell the unit
            player.setGold(player.getGold() + unitToBuy.getCost());
            return false;
        }
    }

    public void displayShop() {
        System.out.println("Shop for player " + player.getPlayerID() + ":");
        for (Unit unit : shop.getAvailableUnits()) {
            System.out.println(unit.getName() + " " + unit.getCost());
        }
    }

    public boolean isBenchFull() {
        return player.getBankUnitsCount() >= 10;
    }

    public boolean canAffordUnit(Unit unit) {
        return player.getGold() >= unit.getCost();
    }
}