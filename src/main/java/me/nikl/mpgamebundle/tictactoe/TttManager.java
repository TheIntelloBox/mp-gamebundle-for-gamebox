package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.data.toplist.SaveType;
import me.nikl.gamebox.game.exceptions.GameStartException;
import me.nikl.gamebox.game.manager.EasyManager;
import me.nikl.gamebox.game.rules.GameRule;
import me.nikl.mpgamebundle.tictactoe.game.TttGame;
import me.nikl.mpgamebundle.tictactoe.game.TttGameMP;
import me.nikl.mpgamebundle.tictactoe.game.TttGameSP;
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
public class TttManager extends EasyManager {
    private Map<String, TttRules> gameTypes = new HashMap<>();
    private Map<UUID, TttGame> games = new HashMap<>();
    private GameBox gameBox;
    private TicTacToe ticTacToe;
    private TttLanguage language;
    private boolean againstNPC = false;

    public TttManager(TicTacToe ticTacToe) {
        this.ticTacToe = ticTacToe;
        this.gameBox = ticTacToe.getGameBox();
        this.language = (TttLanguage) ticTacToe.getGameLang();
    }

    public TttManager(TicTacToe ticTacToe, boolean againstNPC) {
        this(ticTacToe);
        this.againstNPC = againstNPC;
    }

    @Override
    public boolean isInGame(UUID uuid) {
        return games.keySet().contains(uuid);
    }

    @Override
    public void startGame(Player[] players, boolean b, String... strings) throws GameStartException {
        TttRules rule = gameTypes.get(strings[0]);
        if (rule == null) {
            throw new GameStartException(GameStartException.Reason.ERROR);
        }
        if (!againstNPC) {
            boolean firstCanPlay = ticTacToe.payIfNecessary(players[0], rule.getCost(), false);
            boolean secondCanPlay = ticTacToe.payIfNecessary(players[1], rule.getCost(), false);
            if (!firstCanPlay && !secondCanPlay) {
                players[0].sendMessage(language.PREFIX + language.GAME_NOT_ENOUGH_MONEY);
                players[1].sendMessage(language.PREFIX + language.GAME_NOT_ENOUGH_MONEY);
                throw new GameStartException(GameStartException.Reason.NOT_ENOUGH_MONEY);
            }
            if (!firstCanPlay) {
                players[0].sendMessage(language.PREFIX + language.GAME_NOT_ENOUGH_MONEY);
                throw new GameStartException(GameStartException.Reason.NOT_ENOUGH_MONEY_FIRST_PLAYER);
            }
            if (!secondCanPlay) {
                players[1].sendMessage(language.PREFIX + language.GAME_NOT_ENOUGH_MONEY);
                throw new GameStartException(GameStartException.Reason.NOT_ENOUGH_MONEY_SECOND_PLAYER);
            }
        }
        ticTacToe.payIfNecessary(players, rule.getCost());

        if (!againstNPC) {
            TttGameMP game = new TttGameMP(ticTacToe, rule, players[0], players[1]);
            games.put(players[1].getUniqueId(), game);
            games.put(players[0].getUniqueId(), game);
        } else {
            games.put(players[0].getUniqueId(), new TttGameSP(ticTacToe, rule, players[0]));
        }
    }

    @Override
    public void removeFromGame(UUID uuid) {
        TttGame game = games.get(uuid);
        if (game == null) return;
        game.onClose(uuid);
        games.remove(uuid);
    }

    @Override
    public void loadGameRules(ConfigurationSection buttonSec, String buttonID) {
        double cost = buttonSec.getDouble("cost", 0.);
        double reward = buttonSec.getDouble("reward", 0.);
        int tokens = buttonSec.getInt("tokens", 0);
        int timePerTurn = buttonSec.getInt("timePerTurn", 10);
        boolean saveStats = buttonSec.getBoolean("saveStats", false);
        boolean loseOnTimeOver = true;
        TttRules rules = new TttRules(buttonID, saveStats, SaveType.WINS, cost, reward, tokens, timePerTurn, loseOnTimeOver);
        int randomMoveProbability = buttonSec.getInt("randomMoveProbability", 0);
        if (randomMoveProbability > 100 || randomMoveProbability < 0) randomMoveProbability = 0;
        rules.setRandomMoveProbability(randomMoveProbability);
        gameTypes.put(buttonID, rules);
    }

    @Override
    public Map<String, ? extends GameRule> getGameRules() {
        return gameTypes;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        TttGame game = games.get(inventoryClickEvent.getWhoClicked().getUniqueId());
        if (game == null) return;
        game.onClick(inventoryClickEvent);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        removeFromGame(inventoryCloseEvent.getPlayer().getUniqueId());
    }
}
