package me.nikl.mpgamebundle;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.Module;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Niklas Eicker
 */
public class GameBundle extends JavaPlugin {
    public static final String TIC_TAC_TOE = "tictactoe";
    public static final String ROCK_PAPER_SCISSORS = "rockpaperscissors";
    public static final String TIC_TAC_TOE_SP = "tictactoesingle";
    private GameBox gameBox;

    @Override
    public void onEnable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("GameBox");
        if (plugin == null || !plugin.isEnabled()) {
            getLogger().warning(" GameBox was not found! Disabling Battleship...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        gameBox = (GameBox) plugin;
        new Module(gameBox, TIC_TAC_TOE
                , "me.nikl.mpgamebundle.tictactoe.TicTacToe"
                , this, TIC_TAC_TOE, "ttt");
        new Module(gameBox, TIC_TAC_TOE_SP
                , "me.nikl.mpgamebundle.tictactoe.TicTacToeSP"
                , this, TIC_TAC_TOE_SP, "ttts");
        new Module(gameBox, ROCK_PAPER_SCISSORS
                , "me.nikl.mpgamebundle.rockpaperscissors.RockPaperScissors"
                , this, ROCK_PAPER_SCISSORS, "rps");
    }
}
