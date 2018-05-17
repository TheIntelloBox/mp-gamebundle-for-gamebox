package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.nms.NmsFactory;
import me.nikl.gamebox.nms.NmsUtility;
import me.nikl.gamebox.utility.ItemStackUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

/**
 * @author Niklas Eicker
 */
public class TttGame {
    private Random random = new Random();
    private NmsUtility nmsUtility;
    private TicTacToe ticTacToe;
    private Player playerOne;
    private Player playerTwo;
    private Inventory inventory;
    private TttLanguage language;
    private TicTacToe.MarkerPair markerPair;
    private ItemStack paperOut;
    private ItemStack paperIn;
    private int stonesPlaced = 0;
    private Integer[] grid = new Integer[9];
    private GameTimer timer;
    private long beginningTurn;
    private boolean firstTurn = false;
    private int timePerTurn = 10;
    private boolean gameOver = false;
    private TttRules rules;

    public TttGame(TicTacToe ticTacToe, TttRules rules, Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.ticTacToe = ticTacToe;
        this.rules = rules;
        paperOut = ticTacToe.getSheetBorderItem();
        paperIn = ticTacToe.getSheetInnerItem();
        language = (TttLanguage) ticTacToe.getGameLang();
        nmsUtility = NmsFactory.getNmsUtility();
        this.inventory = ticTacToe.createInventory(54, this.language.PREFIX);
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

    private void updateTitle() {
        updateTitle(getTimeLeftInSeconds());
    }

    private void updateTitle(int timeLeft) {
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_YOUR_TURN.replace("%time%", String.valueOf(timeLeft)));
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_OTHERS_TURN.replace("%time%", String.valueOf(timeLeft)));
        } else {
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_YOUR_TURN.replace("%time%", String.valueOf(timeLeft)));
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_OTHERS_TURN.replace("%time%", String.valueOf(timeLeft)));
        }
    }

    private void onGaveUp() {
        gameOver();
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_LOST);
            playerOne.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_WON);
            playerTwo.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP);
        } else {
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_LOST);
            playerTwo.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_WON);
            playerOne.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP);
        }
    }

    void nextTurn() {
        firstTurn = !firstTurn;
        beginningTurn = System.currentTimeMillis();
    }

    private void prepareInventory() {
        for (int i = 0; i < 9; i++) grid[i] = 0;
        markerPair = ticTacToe.getRandomMarkerPair(playerOne.getName(), playerTwo.getName());
        inventory.setItem(0, markerPair.getOne());
        inventory.setItem(8, markerPair.getTwo());
        inventory.setItem(1, ItemStackUtility.getPlayerHead(playerOne.getName()));
        inventory.setItem(7, ItemStackUtility.getPlayerHead(playerTwo.getName()));
        for (int row = 1; row < 6; row ++) {
            for (int column = 2; column < 7; column ++) {
                int index = row*9 + column;
                if (toSmallGrid(index) < 0) {
                    inventory.setItem(index, paperOut);
                } else {
                    inventory.setItem(index, paperIn);
                }
            }
        }
    }

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
        if (isWon()) {
            onGameWon();
        } else {
            nextTurn();
            updateTitle();
        }
    }

    void onGameWon() {
        gameOver();
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_WON);
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_LOST);
        } else {
            nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_WON);
            nmsUtility.updateInventoryTitle(playerOne, language.TITLE_LOST);
        }
    }

    private void gameOver() {
        timer.cancel();
        gameOver = true;
    }

    private boolean isPlayerOne(InventoryInteractEvent event) {
        return playerOne.getUniqueId().equals(event.getWhoClicked().getUniqueId());
    }

    private boolean isPlayerTwo(InventoryInteractEvent event) {
        return playerTwo.getUniqueId().equals(event.getWhoClicked().getUniqueId());
    }

    private boolean isWon() {
        if (stonesPlaced < 3) return false;
        for (int i = 0; i < 3; i++) {
            if (grid[i] != 0 && grid[i].equals(grid[i + 3]) && grid[i + 3].equals(grid[i + 6])) return true;
            if (grid[i] != 0 && grid[i].equals(grid[i + 1]) && grid[i + 1].equals(grid[i + 2])) return true;
        }
        if (grid[0] != 0 && grid[0].equals(grid[4]) && grid[4].equals(grid[8])) return true;
        if (grid[6] != 0 && grid[6].equals(grid[4]) && grid[4].equals(grid[2])) return true;
        return false;
    }

    private int toSmallGrid(int inventorySlot) {
        int row = inventorySlot / 9;
        int column = inventorySlot % 9;
        if (column < 3 || column > 5) return -1;
        if (row < 2 || row > 4) return -1;
        return (row - 2)*3 + column - 3;
    }

    private int toInventory(int gridSlot) {
        int row = gridSlot / 3;
        int column = gridSlot % 3;
        return (row + 3)*9 + column + 3;
    }

    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
    }

    public void tick() {
        int timeLeft = getTimeLeftInSeconds();
        if (timeLeft < 1) {
            // ToDo: game over (gave up)
            if (rules.isLoseOnTimeOver()) {
                onGaveUp();
                return;
            }
            nextTurn();
        }
        updateTitle(timeLeft);
    }

    private int getTimeLeftInSeconds() {
        return ((int)(beginningTurn + timePerTurn * 1000 - System.currentTimeMillis())/1000);
    }
}
