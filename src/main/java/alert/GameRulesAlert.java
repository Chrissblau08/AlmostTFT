package alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class GameRulesAlert extends Alert {

    public GameRulesAlert() {
        super(AlertType.INFORMATION);  // Setzt den Dialogtyp auf INFORMATION
        setTitle("Game Rules");
        setHeaderText("TFT 1v1 - Simplified Version");

        // HTML-Inhalt mit Markup
        String guide = """
            <html>
                <head><style>body { font-family: Arial, sans-serif; font-size: 14px; }</style></head>
                <body>
                    <h2>Game Rules for TFT - 1v1 Mode Without PvE, Items, or Carousel</h2>
                    
                    <h3>1. Introduction</h3>
                    <p>Welcome to the simplified version of Teamfight Tactics (TFT)! In this game, two players face off in a strategic battle. The goal is to defeat your opponent's units and reduce their health points to zero.</p>

                    <h3>2. Game Components</h3>
                    <ul>
                        <li><strong>Players:</strong> Two players, each starting with 100 health points.</li>
                        <li><strong>Gold:</strong> Players receive 5 gold at the start of each round, with additional bonuses for win or lose streaks.</li>
                        <li><strong>Units:</strong> Various units available for purchase using gold. Each unit has:
                            <ul>
                                <li>Name</li>
                                <li>Class (passive, has no effect in this version)</li>
                                <li>HP (health points)</li>
                                <li>Attack (damage per attack)</li>
                                <li>Defense (damage reduction)</li>
                                <li>Attack Speed</li>
                                <li>Attack Reach</li>
                                <li>Cost</li>
                            </ul>
                        </li>
                        <li><strong>Board:</strong> A 3x4 grid for each player where units are placed.</li>
                    </ul>

                    <h3>3. Game Flow</h3>
                    <p>The game consists of several rounds. Each round has the following phases:</p>

                    <h4>3.1 Recruitment Phase</h4>
                    <ul>
                        <li><strong>Earn gold:</strong> Both players receive 5 gold. Additional income:
                            <ul>
                                <li>Win streak (3+ wins): +1 gold</li>
                                <li>Lose streak (3+ losses): +1 gold</li>
                            </ul>
                        </li>
                        <li><strong>Buy units:</strong> Players can buy units from a shared shop. Each unit costs gold.
                            <ul>
                                <li>Units can be sold to regain 50% of their cost.</li>
                            </ul>
                        </li>
                        <li><strong>Place units:</strong> Purchased units can be placed on the board. A maximum of 6 units may be on the board at the same time.</li>
                    </ul>

                    <h4>3.2 Battle Phase</h4>
                    <p>Units fight automatically. The attack order is determined by each unit's attack speed.</p>
                    <p><strong>Damage calculation:</strong><br>
                    Damage = Attack - Defense<br>
                    If attack is less than or equal to defense, at least 1 damage is always inflicted.</p>
                    <p>The round ends when all units of one player are defeated.</p>

                    <h4>3.3 Damage Calculation</h4>
                    <p>The losing player takes damage based on the number of surviving enemy units:
                        <ul>
                            <li>Damage = Number of surviving enemy units.</li>
                        </ul>
                    </p>
                    <p>If a player's health drops to zero, they lose the game.</p>

                    <h3>4. Gameplay Mechanics</h3>
                    <h4>4.1 Gold Management</h4>
                    <ul>
                        <li>Players must plan strategically when to spend gold on units and when to save it.</li>
                        <li>Saving generates interest: For every 10 gold saved, the player earns 1 additional gold per round (maximum of 5 gold interest at 50 gold).</li>
                    </ul>

                    <h4>4.2 Board Strategy</h4>
                    <ul>
                        <li>Units have different ranges:
                            <ul>
                                <li>Melee units should be placed in the front rows.</li>
                                <li>Ranged units and supports can be placed in the back rows.</li>
                            </ul>
                        </li>
                    </ul>

                    <h3>5. Winning Conditions</h3>
                    <p>The game ends when one player's health points reach zero. The other player is declared the winner.</p>

                    <h3>6. Tips for Victory</h3>
                    <ul>
                        <li>Save gold: Use interest to gain more resources over time.</li>
                        <li>Optimize positioning: Arrange your units to make the best use of their attack ranges.</li>
                    </ul>
                </body>
            </html>
        """;

        // WebView für HTML-Inhalte
        WebView webView = new WebView();
        webView.getEngine().loadContent(guide);


        // Füge den ScrollPane direkt zum DialogPane hinzu (kein zusätzlicher VBox)
        getDialogPane().setContent(webView);

        // Setze die Größe des Dialogs
        getDialogPane().setPrefSize(640, 480);
    }
}
