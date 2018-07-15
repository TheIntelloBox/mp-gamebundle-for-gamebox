package me.nikl.mpgamebundle.rockpaperscissors;

import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.exceptions.GameStartException;
import me.nikl.gamebox.game.manager.EasyManager;
import me.nikl.gamebox.game.rules.GameRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Niklas Eicker
 */
public class RpsManager extends EasyManager {
    private RockPaperScissors rockPaperScissors;
    private RpsLanguage rpsLanguage;
    private Map<String, RpsRules> gameTypes = new HashMap<>();
    private Map<UUID, RpsGame> games = new HashMap<>();

    public RpsManager(RockPaperScissors rockPaperScissors) {
        this.rockPaperScissors = rockPaperScissors;
        this.rpsLanguage = (RpsLanguage) rockPaperScissors.getGameLang();
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return games.keySet().contains(uuid);
    }

    @Override
    public void startGame(Player[] players, boolean b, String... strings) throws GameStartException {
        RpsRules rule = gameTypes.get(strings[0]);
        if (rule == null) {
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        rockPaperScissors.payIfNecessary(players, rule.getCost());

        RpsGame game = new RpsGame(rockPaperScissors, rule, players[0], players[1]);
        games.put(players[1].getUniqueId(), game);
        games.put(players[0].getUniqueId(), game);
    }

    @Override
    public void removeFromGame(UUID uuid) {
        RpsGame game = games.get(uuid);
        if (game == null) return;
        game.onClose(uuid);
        games.remove(uuid);
    }

    @Override
    public void loadGameRules(ConfigurationSection buttonSec, String buttonID) {
        double cost = buttonSec.getDouble("cost", 0.);
        double reward = buttonSec.getDouble("reward", 0.);
        int tokens = buttonSec.getInt("tokens", 0);
        boolean saveStats = buttonSec.getBoolean("saveStats", false);
        boolean loseOnTimeOver = true;
        int rounds = buttonSec.getInt("rounds", 5);
        gameTypes.put(buttonID, new RpsRules(buttonID, saveStats, SaveType.WINS, cost, reward, tokens, loseOnTimeOver, rounds));
    }

    @Override
    public Map<String, ? extends GameRule> getGameRules() {
        return gameTypes;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        RpsGame game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if (game == null) return;
        game.onClick(inventoryClickEvent);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());
    }
}
