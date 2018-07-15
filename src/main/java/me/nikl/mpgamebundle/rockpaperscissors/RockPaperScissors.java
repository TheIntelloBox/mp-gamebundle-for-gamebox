package me.nikl.mpgamebundle.rockpaperscissors;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameSettings;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.mpgamebundle.GameBundle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Niklas Eicker
 */
public class RockPaperScissors extends Game {
    private ItemStack rock, paper, scissors;
    private ItemStack indicatorWon, indicatorPlaceholder;

    public RockPaperScissors(GameBox gameBox) {
        super(gameBox, GameBundle.ROCK_PAPER_SCISSORS);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {
        loadPlayItems();
        loadIndicators();
    }

    private void loadIndicators() {
        if (!config.isConfigurationSection("indicatorWon")) {
            loadDefaultIndicatorWon();
        } else {
            indicatorWon = ItemStackUtility.loadItem(config.getConfigurationSection("indicatorWon"));
            if (indicatorWon == null) {
                info("Invalid 'indicatorWon' section found. Loading default...");
                loadDefaultIndicatorWon();
            }
        }
        if (!config.isConfigurationSection("indicatorPlaceholder")) {
            loadDefaultIndicatorPlaceholder();
        } else {
            indicatorPlaceholder = ItemStackUtility.loadItem(config.getConfigurationSection("indicatorPlaceholder"));
            if (indicatorPlaceholder == null) {
                info("Invalid 'indicatorPlaceholder' section found. Loading default...");
                loadDefaultIndicatorPlaceholder();
            }
        }
    }

    private void loadDefaultIndicatorPlaceholder() {
        this.indicatorPlaceholder = ItemStackUtility.getItemStack("stained_glass_pane:7");
        ItemMeta meta = indicatorPlaceholder.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA.toString());
        indicatorPlaceholder.setItemMeta(meta);
    }

    private void loadDefaultIndicatorWon() {
        this.indicatorWon = ItemStackUtility.getItemStack("stained_glass_pane:13");
        ItemMeta meta = indicatorWon.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Won");
        indicatorWon.setItemMeta(meta);
    }

    private void loadPlayItems() {
        if (!config.isConfigurationSection("rock")) {
            loadDefaultRock();
        } else {
            rock = ItemStackUtility.loadItem(config.getConfigurationSection("rock"));
            if (rock == null) {
                info("Invalid 'rock' section found. Loading default...");
                loadDefaultRock();
            }
        }
        if (!config.isConfigurationSection("paper")) {
            loadDefaultPaper();
        } else {
            paper = ItemStackUtility.loadItem(config.getConfigurationSection("paper"));
            if (paper == null) {
                info("Invalid 'paper' section found. Loading default...");
                loadDefaultPaper();
            }
        }
        if (!config.isConfigurationSection("scissors")) {
            loadDefaultScissors();
        } else {
            scissors = ItemStackUtility.loadItem(config.getConfigurationSection("scissors"));
            if (scissors == null) {
                info("Invalid 'scissors' section found. Loading default...");
                loadDefaultScissors();
            }
        }
    }

    private void loadDefaultScissors() {
        this.scissors = new ItemStack(Material.SHEARS);
        ItemMeta meta = scissors.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Scissors");
        scissors.setItemMeta(meta);
    }

    private void loadDefaultPaper() {
        this.paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Paper");
        paper.setItemMeta(meta);
    }

    private void loadDefaultRock() {
        this.rock = new ItemStack(Material.COBBLESTONE);
        ItemMeta meta = rock.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Rock");
        rock.setItemMeta(meta);
    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.TWO_PLAYER);
    }

    @Override
    public void loadLanguage() {
        gameLang = new RpsLanguage(this);
    }

    @Override
    public void loadGameManager() {
        gameManager = new RpsManager(this);
    }

    public ItemStack getRock() {
        return this.rock;
    }

    public ItemStack getPaper() {
        return this.paper;
    }

    public ItemStack getScissors() {
        return this.scissors;
    }

    public ItemStack getIndicatorWon() {
        return this.indicatorWon;
    }

    public ItemStack getIndicatorPlaceholder() {
        return this.indicatorPlaceholder;
    }
}
