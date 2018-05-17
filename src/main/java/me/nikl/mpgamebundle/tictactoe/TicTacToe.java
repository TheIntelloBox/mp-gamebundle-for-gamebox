package me.nikl.mpgamebundle.tictactoe;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameSettings;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.mpgamebundle.GameBundle;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Niklas Eicker
 */
public class TicTacToe extends Game {
    private List<ItemStack> markers = new ArrayList<>();
    private Random random = new Random();
    private ItemStack sheetInnerItem;
    private ItemStack sheetBorderItem;

    public TicTacToe(GameBox gameBox) {
        super(gameBox, GameBundle.TIC_TAC_TOE);
    }

    @Override
    public void onDisable() {

    }

    private void setDefaultSheetBorderItem() {
        ItemMeta meta;
        sheetBorderItem = ItemStackUtility.getItemStack("paper");
        sheetBorderItem.setAmount(1);
        meta = sheetBorderItem.getItemMeta();
        meta.setDisplayName("");
        sheetBorderItem.setItemMeta(meta);
    }

    private void setDefaultSheetInnerItem() {
        ItemMeta meta;
        sheetInnerItem = ItemStackUtility.getItemStack("paper");
        sheetInnerItem.setAmount(1);
        meta = sheetInnerItem.getItemMeta();
        meta.setDisplayName("Click to draw");
        sheetInnerItem.setItemMeta(meta);
    }

    @Override
    public void init() {
        loadSheetItems();
        loadMarkers();
    }

    private void loadSheetItems() {
        if (!config.isConfigurationSection("sheetBorder")) {
            info("No 'sheetBorder' section found. Loading default...");
            setDefaultSheetBorderItem();
        } else {
            sheetBorderItem = ItemStackUtility.loadItem(config.getConfigurationSection("sheetBorder"));
            if (sheetBorderItem == null) {
                info("Invalid 'sheetBorder' section found. Loading default...");
                setDefaultSheetBorderItem();
            }
        }
        if (!config.isConfigurationSection("sheetInner")) {
            info("No 'sheetInner' section found. Loading default...");
            setDefaultSheetInnerItem();
        } else {
            sheetInnerItem = ItemStackUtility.loadItem(config.getConfigurationSection("sheetInner"));
            if (sheetInnerItem == null) {
                info("Invalid 'sheetInner' section found. Loading default...");
                setDefaultSheetInnerItem();
            }
        }
    }

    private void loadMarkers() {
        if (!config.isConfigurationSection("markers")) {
            info("No 'markers' section found. Loading default...");
            loadDefaultMarkers();
            return;
        }
        ConfigurationSection markersSection = config.getConfigurationSection("markers");
        for (String key : markersSection.getKeys(false)) {
            if (!markersSection.isConfigurationSection(key)) continue;
            ItemStack marker = ItemStackUtility.loadItem(markersSection.getConfigurationSection(key));
            if (marker == null) {
                info("Invalid marker '" + key + "' found. Skipping...");
                continue;
            }
            markers.add(marker);
        }
        if (markers.size() < 2) {
            info("Less then two valid markers found. Loading default...");
            loadDefaultMarkers();
        }
    }

    private void loadDefaultMarkers() {
        // ToDo: load default from the default config file
        ItemStack markerOne = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
        ItemMeta meta = markerOne.getItemMeta();
        meta.setDisplayName("%player%");
        markerOne.setItemMeta(meta);
        ItemStack markerTwo = new ItemStack(Material.STAINED_GLASS, 1, (short) 7);
        meta = markerTwo.getItemMeta();
        meta.setDisplayName("%player%");
        markerTwo.setItemMeta(meta);
        markers.add(markerOne);
        markers.add(markerTwo);
    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.TWO_PLAYER);
    }

    @Override
    public void loadLanguage() {
        gameLang = new TttLanguage(this);
    }

    @Override
    public void loadGameManager() {
        gameManager = new TttManager(this);
    }

    public MarkerPair getRandomMarkerPair(String nameOne, String nameTwo) {
        int indexOne = random.nextInt(markers.size());
        int indexTwo = random.nextInt(markers.size());
        while (indexOne == indexTwo) {
            indexTwo = random.nextInt(markers.size());
        }
        ItemStack one = markers.get(indexOne).clone();
        ItemMeta meta = one.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("%player%", nameOne));
        one.setItemMeta(meta);
        ItemStack two = markers.get(indexTwo).clone();
        meta = two.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("%player%", nameTwo));
        two.setItemMeta(meta);
        return new MarkerPair(one, two);
    }

    public ItemStack getSheetBorderItem() {
        return sheetBorderItem;
    }

    public ItemStack getSheetInnerItem() {
        return sheetInnerItem;
    }

    public class MarkerPair {
        private ItemStack one;
        private ItemStack two;

        public MarkerPair(ItemStack one, ItemStack two) {
            this.one = one;
            this.two = two;
        }

        public ItemStack getOne() {
            return this.one;
        }

        public ItemStack getTwo() {
            return this.two;
        }
    }
}
