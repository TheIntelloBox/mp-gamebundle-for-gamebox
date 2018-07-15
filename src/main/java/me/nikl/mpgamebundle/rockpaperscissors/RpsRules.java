package me.nikl.mpgamebundle.rockpaperscissors;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * @author Niklas Eicker
 */
public class RpsRules extends GameRuleRewards {
    private int numberOfRounds;
    private boolean looseOnTimeOver;

    public RpsRules(String key, boolean saveStats, SaveType saveType, double cost, double moneyToWin, int tokenToWin, boolean looseOnTimeOver, int numberOfRounds) {
        super(key, saveStats, saveType, cost, moneyToWin, tokenToWin);
        this.numberOfRounds = numberOfRounds;
        this.looseOnTimeOver = looseOnTimeOver;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public boolean isLooseOnTimeOver() {
        return looseOnTimeOver;
    }
}
