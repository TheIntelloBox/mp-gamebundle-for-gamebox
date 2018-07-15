package me.nikl.mpgamebundle.rockpaperscissors;

import me.nikl.gamebox.GameBoxSettings;
import me.nikl.gamebox.nms.NmsFactory;
import me.nikl.gamebox.nms.NmsUtility;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Niklas Eicker
 */
public class RpsGame {
    protected static RockPaperScissors rockPaperScissors;
    private Player playerOne;
    private Player playerTwo;
    private RpsRules rules;
    private Random random = new Random();
    private boolean firstTurn = true;
    protected Inventory inventory;
    protected RpsLanguage language;
    private int numberOfRounds;
    private int winsOne = 0, winsTwo = 0;
    private long stateEndTimeStamp;
    private Status status = Status.CHOOSE;
    private ChosenIcon firstIcon, secondIcon;
    private int chooseTime = 10000;
    private int waitTime = 5000;
    private String currentFirstTitle, currentSecondTitle;
    private NmsUtility nmsUtility = NmsFactory.getNmsUtility();
    private boolean firstPlaySounds, secondPlaySounds;
    private Sound wonSound = Sound.VILLAGER_YES, lostSound = Sound.VILLAGER_NO, drawSound = Sound.VILLAGER_IDLE;
    private GameTimer timer = new GameTimer(this);
    // animation
    private List<Integer> animationSteps = new ArrayList<>();
    private int currentAnimationStep = -1;

    public RpsGame(RockPaperScissors rockPaperScissors, RpsRules rule, Player player, Player player1) {
        this.rules = rule;
        RpsGame.rockPaperScissors = rockPaperScissors;
        this.inventory = rockPaperScissors.createInventory(54, "");
        this.playerOne = player;
        this.playerTwo = player1;
        this.language = (RpsLanguage) rockPaperScissors.getGameLang();
        this.numberOfRounds = rule.getNumberOfRounds();
        firstPlaySounds = rockPaperScissors.getGameBox().getPluginManager().getPlayer(playerOne.getUniqueId()).isPlaySounds() && GameBoxSettings.playSounds;
        secondPlaySounds = rockPaperScissors.getGameBox().getPluginManager().getPlayer(playerTwo.getUniqueId()).isPlaySounds() && GameBoxSettings.playSounds;
        prepareInventory();
        if (random.nextDouble() < 0.5) {
            firstTurn = false;
        }
        stateEndTimeStamp = System.currentTimeMillis() + chooseTime;
        currentFirstTitle = language.TITLE_CHOOSE;
        currentSecondTitle = language.TITLE_CHOOSE;
        playerOne.openInventory(inventory);
        playerTwo.openInventory(inventory);
        updateTitle();
        timer.runTaskTimer(rockPaperScissors.getGameBox(), 4, 4);
    }

    private void prepareInventory() {
        setHeads();
        placeIndicators();
        toggleIconSelection(true);
    }

    private void toggleIconSelection(boolean show) {
        if (show) {
            inventory.setItem(30, rockPaperScissors.getRock());
            inventory.setItem(31, rockPaperScissors.getPaper());
            inventory.setItem(32, rockPaperScissors.getScissors());
        } else {
            inventory.setItem(30, null);
            inventory.setItem(31, null);
            inventory.setItem(32, null);
        }
    }

    private void placeIndicators() {
        for (int row = 1; row < numberOfRounds/2 + numberOfRounds%2 + 1; row ++) {
            inventory.setItem(row * 9 + 1, rockPaperScissors.getIndicatorPlaceholder());
            inventory.setItem(row * 9 + 7, rockPaperScissors.getIndicatorPlaceholder());
        }
    }

    private void updateTitle() {
        if (playerTwo == null || playerOne == null) return;
        String time = String.valueOf((stateEndTimeStamp - System.currentTimeMillis())/1000);
        nmsUtility.updateInventoryTitle(playerTwo, currentSecondTitle.replace("%time%", time));
        nmsUtility.updateInventoryTitle(playerOne, currentFirstTitle.replace("%time%", time));
    }

    protected void setHeads() {
        ItemStack head = ItemStackUtility.getPlayerHead(playerOne.getName()).clone();
        ItemStack headTwo = ItemStackUtility.getPlayerHead(playerTwo.getName()).clone();
        ItemMeta meta = head.getItemMeta();
        meta.setLore(new ArrayList<>());
        head.setItemMeta(meta);
        meta = headTwo.getItemMeta();
        meta.setLore(new ArrayList<>());
        headTwo.setItemMeta(meta);
        inventory.setItem(1, head);
        inventory.setItem(7, headTwo);
    }

    public void onClick(InventoryClickEvent event) {
        if (status != Status.CHOOSE) return;
        if (event.getRawSlot() < 30 || event.getRawSlot() > 32) return;
        boolean firstPlayer = event.getWhoClicked().getUniqueId().equals(playerOne.getUniqueId());
        if ((firstPlayer && firstIcon != null) || (!firstPlayer && secondIcon != null)) return;
        if (firstPlayer) {
            firstIcon = ChosenIcon.fromSlot(event.getRawSlot());
        } else {
            secondIcon = ChosenIcon.fromSlot(event.getRawSlot());
        }
        String time = String.valueOf((stateEndTimeStamp - System.currentTimeMillis())/1000);
        if (firstIcon != null && secondIcon != null) {
            status = Status.WAIT;
            toggleIconSelection(false);
            displayChosenIcons();
            stateEndTimeStamp = System.currentTimeMillis() + waitTime;
            if (isDraw()) {
                playSound(drawSound);
                currentFirstTitle = language.TITLE_NEXT_ROUND_DRAW;
                currentSecondTitle = language.TITLE_NEXT_ROUND_DRAW;
                updateTitle();
                firstIcon = null;
                secondIcon = null;
                return;
            } else {
                currentFirstTitle = language.TITLE_NEXT_ROUND;
                currentSecondTitle = language.TITLE_NEXT_ROUND;
                updateTitle();
            }
            if (firstWon()) {
                winsOne ++;
                animateWin(true);
                playSound(true, wonSound);
                playSound(false, lostSound);
            } else {
                winsTwo ++;
                animateWin(false);
                playSound(true, lostSound);
                playSound(false, wonSound);
            }
            if ((winsTwo + winsOne) == rules.getNumberOfRounds()) {
                onGameOver();
            }
            firstIcon = null;
            secondIcon = null;
        } else {
            if (firstPlayer) {
                currentFirstTitle = language.TITLE_WAIT;
            } else {
                currentSecondTitle = language.TITLE_WAIT;
            }
            updateTitle();
        }
    }

    private void onGameOver() {
        status = Status.OVER;
        if (winsOne == winsTwo) {
            onDraw();
            return;
        }
        boolean firstWon = winsOne>winsTwo;
        nmsUtility.updateInventoryTitle(firstWon?playerOne:playerTwo, language.TITLE_WON);
        nmsUtility.updateInventoryTitle(firstWon?playerTwo:playerOne, language.TITLE_LOST);
        rockPaperScissors.onGameWon(firstWon?playerOne:playerTwo, rules, 1);
    }

    private void onDraw() {
        nmsUtility.updateInventoryTitle(playerTwo, language.TITLE_DRAW);
        nmsUtility.updateInventoryTitle(playerOne, language.TITLE_DRAW);
    }

    protected void tick() {
        if (!animationSteps.isEmpty()) {
            if (currentAnimationStep > 0) inventory.setItem(currentAnimationStep, null);
            currentAnimationStep = animationSteps.remove(animationSteps.size() - 1);
            inventory.setItem(currentAnimationStep, rockPaperScissors.getIndicatorWon());
            if (animationSteps.isEmpty()) currentAnimationStep = -1;
        } else {
            if (status == Status.OVER) timer.cancel();
        }
        updateTitle();
        if (status == Status.WAIT && System.currentTimeMillis() > stateEndTimeStamp) {
            status = Status.CHOOSE;
            inventory.setItem(21, null);
            inventory.setItem(23, null);
            stateEndTimeStamp = System.currentTimeMillis() + chooseTime;
            currentFirstTitle = language.TITLE_CHOOSE;
            currentSecondTitle = language.TITLE_CHOOSE;
            toggleIconSelection(true);
            return;
        }
        if (status == Status.CHOOSE && System.currentTimeMillis() > stateEndTimeStamp) {
            status = Status.OVER;
            currentFirstTitle = language.TITLE_LOST;
            currentSecondTitle = language.TITLE_LOST;
            updateTitle();
        }
    }

    private void animateWin(boolean firstWon) {
        int destinationSlot = (firstWon?1:7) + 9*(firstWon?winsOne:winsTwo);
        if (destinationSlot > inventory.getSize()) {
            rockPaperScissors.info("Animation out of inventory!");
            return;
        }
        animationSteps.clear();
        animationSteps.add(destinationSlot);
        if (destinationSlot/9 < 3) {
            animationSteps.add(firstWon?20:24);
        } else if (destinationSlot/9 == 3) {
            animationSteps.add(firstWon?29:33);
        } else {
            animationSteps.add(firstWon?38:42);
        }
        animationSteps.add(31 + (firstWon?-1:1));
        animationSteps.add(31);
        animationSteps.add(22);
    }

    private boolean firstWon() {
        return firstIcon.winsAgainst(secondIcon);
    }

    private boolean isDraw() {
        return firstIcon == secondIcon;
    }

    private void displayChosenIcons() {
        inventory.setItem(21, firstIcon.getItem());
        inventory.setItem(23, secondIcon.getItem());
    }

    public void onClose(InventoryCloseEvent inventoryCloseEvent) {
        if (status == Status.OVER || playerTwo == null || playerOne == null) return;
        boolean firstQuit = inventoryCloseEvent.getPlayer().getUniqueId().equals(playerOne.getUniqueId());

        if (firstQuit) {
            playerTwo.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP.replace("%loser%", playerOne.getName()));
            playerOne = null;
        } else {
            playerOne.sendMessage(language.PREFIX + language.GAME_OTHER_GAVE_UP.replace("%loser%", playerTwo.getName()));
            playerTwo = null;
        }
        status = Status.OVER;
        rockPaperScissors.onGameWon(firstQuit?playerTwo:playerOne, rules, 1);
    }

    private enum Status {
        CHOOSE, WAIT, OVER
    }

    private enum ChosenIcon {
        PAPER, ROCK, SCISSORS;

        public static ChosenIcon fromSlot(int slot) {
            if (slot == 30) return ROCK;
            if (slot == 31) return PAPER;
            if (slot == 32) return SCISSORS;
            throw new IllegalArgumentException("Passed slot does not contain an icon!");
        }

        public ItemStack getItem() {
            switch (this) {
                case ROCK:
                    return rockPaperScissors.getRock();
                case PAPER:
                    return rockPaperScissors.getPaper();
                case SCISSORS:
                    return rockPaperScissors.getScissors();
                default:
                    throw new RuntimeException("Unknown item");
            }
        }

        public boolean winsAgainst(ChosenIcon icon) {
            switch (icon) {
                case SCISSORS:
                    return this == ROCK;
                case PAPER:
                    return this == SCISSORS;
                case ROCK:
                    return this == PAPER;
            }
            return false;
        }
    }

    private void playSound(boolean first, Sound sound) {
        if (!(first?firstPlaySounds:secondPlaySounds)) return;
        Player player = first?playerOne:playerTwo;
        player.playSound(player.getLocation(), sound.bukkitSound(), 0.5f, 0.5f);
    }

    private void playSound(Sound sound) {
        playSound(true, sound);
        playSound(false, sound);
    }
}
