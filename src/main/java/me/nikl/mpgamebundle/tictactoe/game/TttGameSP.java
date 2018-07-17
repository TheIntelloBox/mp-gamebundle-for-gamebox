package me.nikl.mpgamebundle.tictactoe.game;

import me.nikl.gamebox.nms.NmsFactory;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.Permission;
import me.nikl.mpgamebundle.tictactoe.TicTacToe;
import me.nikl.mpgamebundle.tictactoe.TttLanguage;
import me.nikl.mpgamebundle.tictactoe.TttRules;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Niklas Eicker
 */
public class TttGameSP extends TttGame {
    private Player player;
    private boolean firstTurn = true;
    private String name = "NPC";
    private ItemStack npcHead;
    private int timeForNpcTurn = 3000;
    private double randomMoveProbability;

    public TttGameSP(TicTacToe ticTacToe, TttRules rules, Player player) {
        this.ticTacToe = ticTacToe;
        this.rules = rules;
        timePerTurn = rules.getTimePerTurn();
        paperOut = ticTacToe.getSheetBorderItem();
        paperIn = ticTacToe.getSheetInnerItem();
        language = (TttLanguage) ticTacToe.getGameLang();
        nmsUtility = NmsFactory.getNmsUtility();
        loadNpcHead();
        randomMoveProbability = rules.getRandomMoveProbability();
        this.inventory = ticTacToe.createInventory(54, this.language.PREFIX);
        this.player = player;
        player.openInventory(inventory);
        prepareInventory();
        timer = new GameTimer(this);
        timer.runTaskTimer(ticTacToe.getGameBox(), 3, 3);
        beginningTurn = System.currentTimeMillis();
        updateTitle();
    }

    private void loadNpcHead() {
        this.npcHead = ticTacToe.getNpcHead();
        ItemMeta meta = npcHead.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + name);
        npcHead.setItemMeta(meta);
    }

    @Override
    protected void updateTitle(int timeLeft) {
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(player, language.TITLE_YOUR_TURN.replace("%time%", String.valueOf(timeLeft)));
        } else {
            nmsUtility.updateInventoryTitle(player, language.TITLE_OTHERS_TURN.replace("%time%", String.valueOf(timeLeft)));
        }
    }

    @Override
    protected void onGaveUp() {
        gameOver();
        // NPC does not give up
        nmsUtility.updateInventoryTitle(player, language.TITLE_LOST);
        player.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
    }

    @Override
    protected void nextTurn() {
        firstTurn = !firstTurn;
        beginningTurn = System.currentTimeMillis();
    }

    @Override
    protected void getMarkerPair() {
        markerPair = ticTacToe.getRandomMarkerPair(player.getName(), name);
    }

    @Override
    protected void setHeads() {
        ItemStack head = ItemStackUtility.getPlayerHead(player.getName()).clone();
        ItemMeta meta = head.getItemMeta();
        meta.setLore(new ArrayList<>());
        head.setItemMeta(meta);
        inventory.setItem(1, head);
        inventory.setItem(7, npcHead);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ticTacToe.info("click");
        if (gameOver || !firstTurn) return;
        int gridSlot = toSmallGrid(event.getSlot());
        ticTacToe.info("gridSlot: " + gridSlot);
        if (gridSlot < 0) return;
        if (grid[gridSlot] != 0) return;
        grid[gridSlot] = 1;
        inventory.setItem(event.getSlot(), markerPair.getOne());
        ticTacToe.info("placed " + "1" + " in grid slot " + gridSlot);
        stonesPlaced ++;
        checkGameStatusAndNextTurn();
    }

    @Override
    protected void onDraw() {
        gameOver();
        nmsUtility.updateInventoryTitle(player, language.TITLE_DRAW);
        player.sendMessage(language.PREFIX + language.GAME_DRAW);
    }

    @Override
    protected void onGameWon() {
        gameOver();
        if (firstTurn) {
            nmsUtility.updateInventoryTitle(player, language.TITLE_WON);
            if (ticTacToe.getSettings().isEconEnabled()
                    && rules.getMoneyToWin() > 0
                    && !Permission.BYPASS_GAME.hasPermission(player, ticTacToe.getGameID())) {
                player.sendMessage(language.PREFIX + language.GAME_WON_MONEY.replace("%reward%", String.valueOf(rules.getMoneyToWin())));
            } else {
                player.sendMessage(language.PREFIX + language.GAME_WON);
            }
            ticTacToe.onGameWon(player, rules, 1);
        } else {
            nmsUtility.updateInventoryTitle(player, language.TITLE_LOST);
            player.sendMessage(language.PREFIX + language.GAME_LOSE);
        }
    }

    @Override
    public void onClose(UUID uuid) {
        if (gameOver) return;
        gameOver();
        player.sendMessage(language.PREFIX + language.GAME_GAVE_UP);
    }

    @Override
    protected void tick() {
        if (!firstTurn && (System.currentTimeMillis() - beginningTurn) > timeForNpcTurn) {
            npcMove();
        }
        super.tick();
    }

    private void npcMove() {
        if (random.nextInt(100) < randomMoveProbability) {
            ticTacToe.info("playing random");
            randomMove();
            return;
        }
        if (grid[4] == 0) {
            ticTacToe.info("take middle");
            place(4);
            return;
        }
        int slotToWin = findSlotToWin(false);
        if (slotToWin >= 0) {
            ticTacToe.info("win with " + slotToWin);
            place(slotToWin);
            return;
        }
        int slotToBlock = findSlotToWin(true);
        if (slotToBlock >= 0) {
            ticTacToe.info("block " + slotToBlock);
            place(slotToBlock);
            return;
        }
        // not perfect, but don't wont to spend too much time on the AI. It should be impossible to win against.
        ticTacToe.info("random, because nothing else to do");
        randomMove();
    }

    private int findSlotToWin(boolean player) {
        for (int slot = 0; slot < 9; slot++) {
            if (grid[slot] != 0) continue;
            grid[slot] = player?1:2;
            if (isWon()) {
                grid[slot] = 0;
                return slot;
            }
            grid[slot] = 0;
        }
        return -1;
    }

    private void place(int gridSlot) {
        grid[gridSlot] = 2;
        inventory.setItem(toInventory(gridSlot), markerPair.getTwo());
        ticTacToe.info("placed 2 in grid slot " + gridSlot);
        stonesPlaced ++;
        checkGameStatusAndNextTurn();
    }

    private void randomMove() {
        List<Integer> emptySlots = new ArrayList<>();
        for (int slot = 0; slot < 9; slot++) {
            ticTacToe.info("slot " + slot + " is " + grid[slot]);
            if (grid[slot] == 0) emptySlots.add(slot);
        }
        ticTacToe.info(emptySlots.toString());
        if (emptySlots.isEmpty()) {
            ticTacToe.info("full grid during NPC move!");
            onDraw();
            return;
        }
        int rand = random.nextInt(emptySlots.size());
        place(emptySlots.get(rand));
    }
}
