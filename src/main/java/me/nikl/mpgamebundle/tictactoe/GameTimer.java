package me.nikl.mpgamebundle.tictactoe;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Niklas Eicker
 */
public class GameTimer extends BukkitRunnable {
    private TttGame game;

    public GameTimer(TttGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.tick();
    }
}
