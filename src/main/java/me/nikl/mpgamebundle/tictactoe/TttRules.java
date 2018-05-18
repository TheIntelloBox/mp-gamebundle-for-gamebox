package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * @author Niklas Eicker
 */
public class TttRules extends GameRuleRewards {
    private boolean loseOnTimeOver;
    private int timePerTurn = 10;

    public TttRules(String key, boolean saveStats, SaveType saveType, double cost, double moneyToWin, int tokenToWin, int timePerTurn, boolean loseOnTimeOver) {
        super(key, saveStats, saveType, cost, moneyToWin, tokenToWin);
        this.loseOnTimeOver = loseOnTimeOver;
        this.timePerTurn = timePerTurn;
    }

    public boolean isLoseOnTimeOver() {
        return loseOnTimeOver;
    }

    public int getTimePerTurn() {
        return timePerTurn;
    }
}