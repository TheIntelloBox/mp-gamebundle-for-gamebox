package me.nikl.mpgamebundle.rockpaperscissors;

import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameLanguage;

/**
 * @author Niklas Eicker
 */
public class RpsLanguage extends GameLanguage {
    public String TITLE_WON, TITLE_LOST, TITLE_WAIT, TITLE_CHOOSE, TITLE_NEXT_ROUND, TITLE_NEXT_ROUND_DRAW, TITLE_DRAW;
    public String GAME_PAYED, GAME_NOT_ENOUGH_MONEY, GAME_WON_MONEY, GAME_WON_MONEY_GAVE_UP, GAME_WON, GAME_LOSE, GAME_GAVE_UP, GAME_OTHER_GAVE_UP,
            GAME_TOO_SLOW, GAME_WON_MONEY_TOO_SLOW, GAME_WON_TOO_SLOW;

    public RpsLanguage(Game game) {
        super(game);
    }

    @Override
    protected void loadMessages() {
        getInvTitles();
        getGameMessages();
    }

    private void getInvTitles() {
        this.TITLE_WON = getString("inventoryTitles.won");
        this.TITLE_DRAW = getString("inventoryTitles.draw");
        this.TITLE_LOST = getString("inventoryTitles.lost");
        this.TITLE_WAIT = getString("inventoryTitles.wait");
        this.TITLE_CHOOSE = getString("inventoryTitles.choose");
        this.TITLE_NEXT_ROUND = getString("inventoryTitles.nextRound");
        this.TITLE_NEXT_ROUND_DRAW = getString("inventoryTitles.nextRoundDraw");
    }

    private void getGameMessages() {
        this.GAME_PAYED = getString("game.econ.payed");
        this.GAME_NOT_ENOUGH_MONEY = getString("game.econ.notEnoughMoney");
        this.GAME_WON_MONEY = getString("game.econ.wonMoney");
        this.GAME_WON_MONEY_GAVE_UP = getString("game.econ.wonMoneyGaveUp");
        this.GAME_WON_MONEY_TOO_SLOW = getString("game.econ.wonMoneyTooSlow");
        this.GAME_WON = getString("game.won");
        this.GAME_LOSE = getString("game.lost");
        this.GAME_GAVE_UP = getString("game.gaveUp");
        this.GAME_OTHER_GAVE_UP = getString("game.otherGaveUp");
        this.GAME_TOO_SLOW = getString("game.tooSlow");
        this.GAME_WON_TOO_SLOW = getString("game.otherTooSlow");
        this.GAME_HELP = getStringList("gameHelp");
    }
}
