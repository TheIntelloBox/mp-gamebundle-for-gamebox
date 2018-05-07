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

    public TicTacToe(GameBox gameBox) {
        super(gameBox, GameBundle.TIC_TAC_TOE);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {
        loadMarkers();
    }

    private void loadMarkers() {
        if (!config.isConfigurationSection("markers")) loadDefaultMarkers();
        ConfigurationSection markersSection = config.getConfigurationSection("markers");
        for (String key : config.getKeys(false)) {
            if (!markersSection.isConfigurationSection(key)) continue;
            ItemStack marker = ItemStackUtility.loadItem(markersSection.getConfigurationSection(key));
            if (marker == null) continue;
            markers.add(marker);
        }
        if (markers.size() < 2) loadDefaultMarkers();
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
