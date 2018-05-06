package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.rules.GameRuleRewards;

/**
 * @author Niklas Eicker
 */
public class TttRules extends GameRuleRewards {
    public TttRules(String key, boolean saveStats, SaveType saveType, double cost, double moneyToWin, int tokenToWin) {
        super(key, saveStats, saveType, cost, moneyToWin, tokenToWin);
    }
}
