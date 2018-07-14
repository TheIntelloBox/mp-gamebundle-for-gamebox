package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameLanguage;

/**
 * @author Niklas Eicker
 */
public class TttLanguage extends GameLanguage {
    public String TITLE_WON, TITLE_LOST, TITLE_YOUR_TURN, TITLE_OTHERS_TURN, TITLE_DRAW;
    public String GAME_PAYED, GAME_NOT_ENOUGH_MONEY, GAME_WON_MONEY, GAME_WON_MONEY_GAVE_UP, GAME_WON, GAME_LOSE, GAME_GAVE_UP, GAME_OTHER_GAVE_UP,
            GAME_TOO_SLOW, GAME_WON_MONEY_TOO_SLOW, GAME_WON_TOO_SLOW, GAME_DRAW;

    public TttLanguage(Game game) {
        super(game);
    }

    @Override
    protected void loadMessages() {
        getInvTitles();
        getGameMessages();
    }

    private void getInvTitles() {
        this.TITLE_WON = getString("inventoryTitles.won");
        this.TITLE_LOST = getString("inventoryTitles.lost");
        this.TITLE_DRAW = getString("inventoryTitles.draw");
        this.TITLE_YOUR_TURN = getString("inventoryTitles.yourTurn");
        this.TITLE_OTHERS_TURN = getString("inventoryTitles.othersTurn");
    }

    private void getGameMessages() {
        this.GAME_PAYED = getString("game.econ.payed");
        this.GAME_NOT_ENOUGH_MONEY = getString("game.econ.notEnoughMoney");
        this.GAME_WON_MONEY = getString("game.econ.wonMoney");
        this.GAME_WON_MONEY_GAVE_UP = getString("game.econ.wonMoneyGaveUp");
        this.GAME_WON_MONEY_TOO_SLOW = getString("game.econ.wonMoneyTooSlow");
        this.GAME_WON = getString("game.won");
        this.GAME_LOSE = getString("game.lost");
        this.GAME_DRAW = getString("game.draw");
        this.GAME_GAVE_UP = getString("game.gaveUp");
        this.GAME_OTHER_GAVE_UP = getString("game.otherGaveUp");
        this.GAME_TOO_SLOW = getString("game.tooSlow");
        this.GAME_WON_TOO_SLOW = getString("game.otherTooSlow");
        this.GAME_HELP = getStringList("gameHelp");
    }
}
