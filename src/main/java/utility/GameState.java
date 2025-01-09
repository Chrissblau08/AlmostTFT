package utility;

import model.Unit;

import java.util.ArrayList;
import java.util.List;

public class GameState
{
    public final List<Unit> playerBench = new ArrayList<>();
    public final List<Unit> enemyBench = new ArrayList<>();
    public final List<Unit> playerBoard = new ArrayList<>();
    public final List<Unit> shopUnits = new ArrayList<>();
    public final int playerHp;
    public final int playerGold;
    public final int playerLevel;
    public final String playerXp;
    public final int PlayerID;
    public final int winStreak;
    public final int loseStreak;


    public GameState(List<Unit> playerBench, List<Unit> shopUnits, List<Unit> enemyBench,
                     List<Unit> playerBoard, int playerGold, String playerXp, int playerLevel, int playerHp, int PlayerID, int winStreak, int loseStreak)
    {
        this.playerBench.addAll(playerBench);
        this.shopUnits.addAll(shopUnits);
        this.enemyBench.addAll(enemyBench);
        this.playerBoard.addAll(playerBoard);
        this.playerGold = playerGold;
        this.playerXp = playerXp;
        this.playerLevel = playerLevel;
        this.playerHp = playerHp;
        this.PlayerID = PlayerID;
        this.winStreak = winStreak;
        this.loseStreak = loseStreak;
    }
}
