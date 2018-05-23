package me.nikl.mpgamebundle.tictactoe.game;

import me.nikl.gamebox.nms.NmsUtility;
import me.nikl.mpgamebundle.tictactoe.TicTacToe;
import me.nikl.mpgamebundle.tictactoe.TttLanguage;
import me.nikl.mpgamebundle.tictactoe.TttRules;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.UUID;

/**
 * @author Niklas Eicker
 */
public abstract class TttGame {
    protected Random random = new Random();
    protected NmsUtility nmsUtility;
    protected TicTacToe ticTacToe;
    protected Inventory inventory;
    protected TttLanguage language;
    protected TicTacToe.MarkerPair markerPair;
    protected ItemStack paperOut;
    protected ItemStack paperIn;
    protected int stonesPlaced = 0;
    protected Integer[] grid = new Integer[9];
    protected GameTimer timer;
    protected long beginningTurn;
    protected int timePerTurn;
    protected boolean gameOver = false;
    protected TttRules rules;

    protected void updateTitle() {
        updateTitle(getTimeLeftInSeconds());
    }

    abstract protected void updateTitle(int timeLeft);

    abstract protected void onGaveUp();

    abstract protected void nextTurn();

    protected void prepareInventory() {
        for (int i = 0; i < 9; i++) grid[i] = 0;
        getMarkerPair();
        inventory.setItem(0, markerPair.getOne());
        inventory.setItem(8, markerPair.getTwo());
        setHeads();
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

    protected abstract void getMarkerPair();

    protected abstract void setHeads();

    public abstract void onClick(InventoryClickEvent event);

    protected void checkGameStatusAndNextTurn() {
        if (isWon()) {
            onGameWon();
        } else if(stonesPlaced > 8) {
            onDraw();
        } else {
            nextTurn();
            updateTitle();
        }
    }

    protected abstract void onDraw();

    protected abstract void onGameWon();

    protected void gameOver() {
        timer.cancel();
        gameOver = true;
    }

    protected boolean isWon() {
        if (stonesPlaced < 3) return false;
        for (int i = 0; i < 3; i++) {
            if (grid[i] != 0 && grid[i].equals(grid[i + 3]) && grid[i + 3].equals(grid[i + 6])) return true;
            if (grid[i*3] != 0 && grid[i*3].equals(grid[i*3 + 1]) && grid[i*3 + 1].equals(grid[i*3 + 2])) return true;
        }
        if (grid[0] != 0 && grid[0].equals(grid[4]) && grid[4].equals(grid[8])) return true;
        if (grid[6] != 0 && grid[6].equals(grid[4]) && grid[4].equals(grid[2])) return true;
        return false;
    }

    protected int toSmallGrid(int inventorySlot) {
        int row = inventorySlot / 9;
        int column = inventorySlot % 9;
        if (column < 3 || column > 5) return -1;
        if (row < 2 || row > 4) return -1;
        return (row - 2)*3 + column - 3;
    }

    protected int toInventory(int gridSlot) {
        int row = gridSlot / 3;
        int column = gridSlot % 3;
        return (row + 2)*9 + column + 3;
    }

    public abstract void onClose(UUID uuid);

    protected void tick() {
        if (gameOver) return;
        int timeLeft = getTimeLeftInSeconds();
        if (timeLeft < 1) {
            // ToDo: game over (gave up)
            if (rules.isLoseOnTimeOver()) {
                onGaveUp();
                return;
            }
            nextTurn();
        }
        updateTitle();
    }

    private int getTimeLeftInSeconds() {
        return ((int)(beginningTurn + timePerTurn * 1000 - System.currentTimeMillis())/1000);
    }
}
