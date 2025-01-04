package test;

import controller.GameController;
import controller.ShopController;
import controller.ViewController;
import model.Player;
import model.Unit;
import model.UnitPool;

import java.util.List;
import java.util.Random;
/*
public class Main {
    public static void main(String[] args) {
        // Initialize UnitPool with the path to the JSON file
        UnitPool unitPool = new UnitPool("src/main/java/files/characters.json");
        ViewController viewController = new ViewController();
        GameController gameController = new GameController(unitPool, viewController);

        // Set up players and their shops
        gameController.setPlayersAndShops();
        gameController.outputPlayerValues();

        for (int i = 0; i < 2; i++) {
            ShopController shopController = gameController.getShopControllers()[i];
            Player player = gameController.getPlayers()[i];

            while (true) {
                shopController.displayShop();
                // Attempt to buy a unit
                if (!purchaseRandomUnit(shopController, player)) {
                    System.out.println("Purchase failed. Not enough units available or insufficient gold.");
                    break;
                }
                displayPlayerBank(player, i);

                // Attempt to buy XP until it fails
                boolean xpPurchaseSuccess = true;
                while (xpPurchaseSuccess) {
                    xpPurchaseSuccess = shopController.purchaseXP();
                    if (!xpPurchaseSuccess) {
                        System.out.println("XP purchase failed. Stopping further actions for player " + (i + 1) + ".");
                        break;
                    }
                    shopController.refreshShop();
                    shopController.displayShop();
                }
            }

            // Try buying units until the bench is full
            while (player.getBankUnitsCount() < 10 && player.getGold() > 10) {
                if (!purchaseRandomUnit(shopController, player)) {
                    System.out.println("Purchase failed. Not enough units available or insufficient gold.");
                }
            }
            displayPlayerBank(player, i);

            // Attempt to buy an additional unit after the bench is full
            if (!purchaseRandomUnit(shopController, player)) {
                System.out.println("Purchase unsuccessful. Player " + (i + 1) + "'s bank is full or insufficient gold.");
            }
            displayPlayerBank(player, i);
        }
    }

    private static boolean purchaseRandomUnit(ShopController shopController, Player player) {
        // Check if any units are available for purchase
        List<Unit> availableUnits = shopController.getAvailableUnits();
        if (availableUnits.isEmpty()) {
            return false;
        }

        // Select a random unit ID
        Random random = new Random();
        Unit unitToBuy = availableUnits.get(random.nextInt(availableUnits.size()));
        int unitId = unitToBuy.getId();

        boolean purchaseSuccessful = shopController.buyUnit(unitId);
        return purchaseSuccessful;
    }

    private static void displayPlayerBank(Player player, int playerIndex) {
        // Print out the player's bank
        System.out.println("Player " + (playerIndex + 1) + " bank:");
        for (Unit unit : player.getBankUnits()) {
            System.out.println("Unit ID: " + unit.getId() + ", Name: " + unit.getName() + " , Cost:" + unit.getCost());
        }
    }
}

 */