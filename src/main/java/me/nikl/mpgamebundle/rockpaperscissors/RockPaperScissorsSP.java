package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.game.GameSettings;
import me.nikl.mpgamebundle.GameBundle;
import me.nikl.mpgamebundle.rockpaperscissors.RockPaperScissors;
import me.nikl.mpgamebundle.rockpaperscissors.RpsManager;

/**
 * @author Niklas Eicker and TheIntelloBox for the single player
 */
public class RockPaperScissorsSP extends RockPaperScissors {

    RockPaperScissorsSP(GameBox gameBox) {
        super(gameBox, GameBundle.TIC_TAC_TOE_SP);
    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.SINGLE_PLAYER);
    }

    @Override
    public void loadGameManager() {
        gameManager = new TttManager(this, true);
    }
}
