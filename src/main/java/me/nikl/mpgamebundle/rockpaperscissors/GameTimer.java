package me.nikl.mpgamebundle.rockpaperscissors;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Niklas Eicker
 */
public class GameTimer extends BukkitRunnable {
    private RpsGame game;

    public GameTimer(RpsGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.tick();
    }
}
