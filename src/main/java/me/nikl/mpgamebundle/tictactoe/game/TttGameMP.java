package me.nikl.mpgamebundle.tictactoe.game;

import me.nikl.gamebox.nms.NmsFactory;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.Permission;
import me.nikl.mpgamebundle.tictactoe.TicTacToe;
import me.nikl.mpgamebundle.tictactoe.TttLanguage;
import me.nikl.mpgamebundle.tictactoe.TttRules;
import me.nikl.mpgamebundle.tictactoe.game.GameTimer;
import me.nikl.mpgamebundle.tictactoe.game.TttGame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Niklas Eicker
 */
public class TttGameMP extends TttGame {
    private Player playerOne;
    private Player playerTwo;
    private boolean firstTurn = false;

    public TttGameMP(TicTacToe ticTacToe, TttRules rules, Player playerOne, Player playerTwo) {
        this.ticTacToe = ticTacToe;
        this.rules = rules;
        timePerTurn = rules.getTimePerTurn();
        paperOut = ticTacToe.getSheetBorderItem();
        paperIn = ticTacToe.getSheetInnerItem();
        language = (TttLanguage) ticTacToe.getGameLang();
        nmsUtility = NmsFactory.getNmsUtility();
        this.inventory = ticTacToe.createInventory(54, this.language.PREFIX);
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        playerOne.openInventory(inventory);
        playerTwo.openInventory(inventory);
        prepareInventory();
        timer = new GameTimer(this);
        timer.runTaskTimer(ticTacToe.getGameBox(), 3, 3);
        double randomNumber = random.nextDouble();
        if (randomNumber < 0.5) {
            firstTurn = true;
        }
        beginningTurn = System.currentTimeMillis();
        updateTitle();
    }

    @Override
    protected void updateTitle(int timeLeft) {
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_YOUR_TURN.replace("%time%", String.valueOf(timeLeft)));
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_OTHERS_TURN.replace("%time%", String.valueOf(timeLeft)));
        } else {
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_YOUR_TURN.replace("%time%", String.valueOf(timeLeft)));
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_OTHERS_TURN.replace("%time%", String.valueOf(timeLeft)));
        }
    }

    @Override
    protected void onGaveUp() {
        gameOver();
        if (firstTurn) {
            if (ticTacToe.getSettings().isEconEnabled()
                    && rules.getMoneyToWin() > 0
                    && !Permission.BYPASS_GAME.hasPermission(playerTwo, ticTacToe.getGameID())) {
                playerTwo.sendMessage(language.PREFIX + language.GAME_WON_MONEY_TOO_SLOW.replace("%reward%", String.valueOf(rules.getMoneyToWin())));
            } else {
                playerTwo.sendMessage(language.PREFIX + language.GAME_WON_TOO_SLOW);
            }
            playerOne.sendMessage(language.PREFIX + language.GAME_TOO_SLOW);
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_LOST);
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_WON);
        } else {
            if (ticTacToe.getSettings().isEconEnabled()
                    && rules.getMoneyToWin() > 0
                    && !Permission.BYPASS_GAME.hasPermission(playerOne, ticTacToe.getGameID())) {
                playerOne.sendMessage(language.PREFIX + language.GAME_WON_MONEY_TOO_SLOW.replace("%reward%", String.valueOf(rules.getMoneyToWin())));
            } else {
                playerOne.sendMessage(language.PREFIX + language.GAME_WON_TOO_SLOW);
            }
            playerTwo.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_LOST);
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_WON);
        }
    }

    @Override
    protected void nextTurn() {
        firstTurn = !firstTurn;
        beginningTurn = System.currentTimeMillis();
    }

    @Override
    protected void getMarkerPair() {
        markerPair = ticTacToe.getRandomMarkerPair(playerOne.getName(), playerTwo.getName());
    }

    @Override
    protected void setHeads() {
        ItemStack headOne = ItemStackUtility.getPlayerHead(playerOne.getName()).clone();
        ItemStack headTwo = ItemStackUtility.getPlayerHead(playerTwo.getName()).clone();
        ItemMeta meta = headOne.getItemMeta();
        meta.setLore(new ArrayList<>());
        headOne.setItemMeta(meta);
        meta = headTwo.getItemMeta();
        meta.setLore(new ArrayList<>());
        headTwo.setItemMeta(meta);
        inventory.setItem(1, headOne);
        inventory.setItem(7, headTwo);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ticTacToe.info("click");
        if (gameOver) return;
        int gridSlot = toSmallGrid(event.getSlot());
        ticTacToe.info("gridSlot: " + gridSlot);
        if (gridSlot < 0) return;
        if (firstTurn && !isPlayerOne(event)) return;
        if (!firstTurn && !isPlayerTwo(event)) return;
        ticTacToe.info("turn: " + firstTurn);
        if (grid[gridSlot] != 0) return;
        grid[gridSlot] = firstTurn?1:2;
        inventory.setItem(event.getSlot(), firstTurn?markerPair.getOne():markerPair.getTwo());
        ticTacToe.info("placed " + (firstTurn?"1":"2") + " in grid slot " + gridSlot);
        stonesPlaced ++;
        checkGameStatusAndNextTurn();
    }

    @Override
    protected void onDraw() {
        gameOver();
        nmsUtility.updateInventoryTitle(playerOne, language.TITLE_DRAW);
        nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_DRAW);
        playerOne.sendMessage(language.PREFIX + language.GAME_DRAW);
        playerTwo.sendMessage(language.PREFIX + language.GAME_DRAW);
    }

    @Override
    protected void onGameWon() {
        gameOver();
        Player winner = firstTurn?playerOne:playerTwo;
        Player loser = firstTurn?playerTwo:playerOne;

        if (ticTacToe.getSettings().isEconEnabled()
                && rules.getMoneyToWin() > 0
                && !Permission.BYPASS_GAME.hasPermission(winner, ticTacToe.getGameID())) {
            winner.sendMessage(language.PREFIX + language.GAME_WON_MONEY.replace("%reward%", String.valueOf(rules.getMoneyToWin())));
        } else {
            winner.sendMessage(language.PREFIX + language.GAME_WON);
        }
        loser.sendMessage(language.PREFIX + language.GAME_LOSE);
        nmsUtility.updateInventoryTitle(winner, language.TITLE_WON);
        nmsUtility.updateInventoryTitle(loser, language.TITLE_LOST);
        ticTacToe.onGameWon(winner, rules, 1);
    }

    private boolean isPlayerOne(InventoryInteractEvent event) {
        return playerOne.getUniqueId().equals(event.getWhoClicked().getUniqueId());
    }

    private boolean isPlayerTwo(InventoryInteractEvent event) {
        return playerTwo.getUniqueId().equals(event.getWhoClicked().getUniqueId());
    }

    @Override
    public void onClose(UUID uuid) {
        if (gameOver) return;
        gameOver();
        if (uuid.equals(playerOne.getUniqueId())) {
            if (ticTacToe.getSettings().isEconEnabled()
                    && rules.getMoneyToWin() > 0
                    && !Permission.BYPASS_GAME.hasPermission(playerTwo, ticTacToe.getGameID())) {
                playerTwo.sendMessage(language.PREFIX + language.GAME_WON_MONEY_GAVE_UP.replace("%loser%", playerOne.getName()).replace("%reward%", String.valueOf(rules.getMoneyToWin())));
            } else {
                playerTwo.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP.replace("%loser%", playerOne.getName()));
            }
            playerOne.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_WON);
        } else {
            if (ticTacToe.getSettings().isEconEnabled()
                    && rules.getMoneyToWin() > 0
                    && !Permission.BYPASS_GAME.hasPermission(playerOne, ticTacToe.getGameID())) {
                playerOne.sendMessage(language.PREFIX + language.GAME_WON_MONEY_GAVE_UP.replace("%loser%", playerTwo.getName()).replace("%reward%", String.valueOf(rules.getMoneyToWin())));
            } else {
                playerOne.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP.replace("%loser%", playerTwo.getName()));
            }
            playerTwo.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_WON);
        }
        ticTacToe.onGameWon(uuid.equals(playerOne.getUniqueId())?playerOne:playerTwo, rules, 1);
    }
}
