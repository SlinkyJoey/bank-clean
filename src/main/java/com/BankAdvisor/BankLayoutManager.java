// src/main/java/com/BankAdvisor/BankLayoutManager.java
package com.BankAdvisor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.RuneScapeObjectToItemComposition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class BankLayoutManager {

    private static final String CONFIG_KEY_LAYOUT = "banklayout";
    private static final String PRESET_STANDARD_MAIN = "Standard Main";
    private static final String PRESET_IRONMAN = "Ironman";
    private static final String PRESET_SKILLER = "Skiller";
    private static final String UNCATEGORIZED_TAB_NAME = "Uncategorized";

    private BankLayoutPreset activePreset = new BankLayoutPreset();

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager; // Assuming ItemManager is available for ItemComposition

    // Gson instance with a custom Color type adapter
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorUtil.ColorAdapter())
            .enableComplexMapKeySerialization()
            .create();

    // Placeholder for RuneScapeObjectToItemComposition if you use it
    // @Inject
    // private RuneScapeObjectToItemComposition objectToItemComposition;

    public BankLayoutPreset getActivePreset() {
        return activePreset;
    }

    public void loadPreset(String presetName) {
        // In a real scenario, you'd load presets from resources or a config file.
        // For now, we'll hardcode a few and rely on loaded config.
        BankLayoutPreset preset = null;
        if (PRESET_STANDARD_MAIN.equals(presetName)) {
            preset = createStandardMainPreset();
        } else if (PRESET_IRONMAN.equals(presetName)) {
            preset = createIronmanPreset();
        } else if (PRESET_SKILLER.equals(presetName)) {
            preset = createSkillerPreset();
        } else {
            // Attempt to load from config if it's not a built-in
            String jsonConfig = client.getConfiguration(CONFIG_KEY_LAYOUT);
            if (jsonConfig != null && !jsonConfig.isEmpty()) {
                Type type = new TypeToken<BankLayoutPreset>() {}.getType();
                try {
                    preset = gson.fromJson(jsonConfig, type);
                    // Ensure loaded preset has an Uncategorized tab if missing
                    ensureUncategorizedTabExists(preset);
                } catch (Exception e) {
                    // Handle exception, maybe log it
                    preset = createDefaultPreset(); // Fallback
                }
            } else {
                preset = createDefaultPreset(); // Fallback to default if no config
            }
        }

        if (preset != null) {
            this.activePreset = preset;
            // Ensure the active preset always has an Uncategorized tab
            ensureUncategorizedTabExists(this.activePreset);
            sortTabsByNumber(this.activePreset); // Sort tabs by their number
        }
    }

    private void ensureUncategorizedTabExists(BankLayoutPreset preset) {
        if (preset == null || preset.getTabs() == null) {
            preset = new BankLayoutPreset(); // Should not happen if createDefaultPreset is robust
        }
        boolean uncategorizedExists = preset.getTabs().stream()
                .anyMatch(BankLayoutTab::isUncategorized);

        if (!uncategorizedExists) {
            // Find the highest tab number and add the new tab with the next number
            int nextNumber = preset.getTabs().stream()
                    .mapToInt(BankLayoutTab::getNumber)
                    .max()
                    .orElse(0) + 1;
            BankLayoutTab uncategorizedTab = new BankLayoutTab(nextNumber, UNCATEGORIZED_TAB_NAME, Color.GRAY);
            preset.getTabs().add(uncategorizedTab);
            sortTabsByNumber(preset); // Re-sort after adding
        }
    }

    private void sortTabsByNumber(BankLayoutPreset preset) {
        if (preset != null && preset.getTabs() != null) {
            preset.getTabs().sort(Comparator.comparingInt(BankLayoutTab::getNumber));
        }
    }

    public void saveActivePreset() {
        // Ensure Uncategorized tab is present before saving
        ensureUncategorizedTabExists(this.activePreset);
        sortTabsByNumber(this.activePreset);
        String json = gson.toJson(this.activePreset);
        client.setConfiguration(CONFIG_KEY_LAYOUT, json);
    }

    // Placeholder methods for creating built-in presets
    private BankLayoutPreset createStandardMainPreset() {
        BankLayoutPreset preset = new BankLayoutPreset();
        preset.setName(PRESET_STANDARD_MAIN);
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Food", Color.RED));
        tabs.get(0).getKeywords().addAll(Arrays.asList("Tuna", "Shark", "Karambwan", "Manta ray", "Anglerfish", "Brew"));
        tabs.get(0).getKeywords().addAll(Arrays.asList("Potato with butter", "Cooked meat", "Cooked fish")); // Basic cooked food

        tabs.add(new BankLayoutTab(2, "Potions", Color.ORANGE));
        tabs.get(1).getKeywords().addAll(Arrays.asList("Saradomin brew", "Super restore", "Prayer potion", "Ranging potion", "Magic potion", "Stamina potion", "Antidote", "Vile blowpipe", "Sanfew serum", "Energy potion", "Attack potion", "Strength potion", "Defence potion", "Agility potion", "Farming potion", "Fishing potion", "Hunter potion", "Mining potion", "Rune crafting potion", "Thieving potion", "Vigor potion"));

        tabs.add(new BankLayoutTab(3, "Ammunition", Color.YELLOW));
        tabs.get(2).getKeywords().addAll(Arrays.asList("Dragon arrow", "Rune arrow", "Broad bolt", "Dragon bolt", "Onyx bolt", "Ruby bolt", "Broad arrow", "Amethyst arrow", "Amethyst bolt"));

        tabs.add(new BankLayoutTab(4, "Runes", Color.CYAN));
        tabs.get(3).getKeywords().addAll(Arrays.asList("Air rune", "Water rune", "Earth rune", "Fire rune", "Mind rune", "Body rune", "Death rune", "Chaos rune", "Rune essence", "Pure essence", "Cosmic rune", "Nature rune", "Law rune", "Blood rune", "Soul rune", "Astral rune", "Wrath rune", "Our([]| )ble rune", "Steam rune", "Lava rune", "Mud rune"));

        tabs.add(new BankLayoutTab(5, "Gear - Melee", Color.GREEN));
        tabs.get(4).getKeywords().addAll(Arrays.asList("Dragon dagger", "Abyssal whip", "Dragon scimitar", "Ghrazi ghoul", "Ghazi ghoul", "Scythe", "Blade of saeldor", "Dragon hasta", "Tzhaar-ket-om", "Dragon mace", "Dragon longsword", "Dragon battleaxe", "Ghazi ghoul", "Ghazi ward", "Ghazi platebody", "Ghazi platelegs", "Ghazi helm", "Ghazi boots", "Ghazi gloves", "Ghazi cape", "Ghazi amulet", "Ghazi ring", "Ghazi shield", "Ghazi defender")); // Example melee gear
        tabs.get(4).getActions().add("Wield"); // Add "Wield" as an action trigger for gear

        tabs.add(new BankLayoutTab(6, "Gear - Ranged", Color.BLUE));
        tabs.get(5).getKeywords().addAll(Arrays.asList("Magic shortbow", "Toxic blowpipe", "Dragon crossbow", "Armadyl crossbow", "Odium ward", "Malediction ward", "God d'hide", "Karil's leather", "Black d'hide", "Blue d'hide", "Green d'hide", "Red d'hide", "Adamant dragon bolts", "Rune crossbow", "Toxic staff of the dead"));
        tabs.get(5).getActions().add("Wield");

        tabs.add(new BankLayoutTab(7, "Gear - Magic", Color.MAGENTA));
        tabs.get(6).getKeywords().addAll(Arrays.asList("Occult necklace", "Mage's book", "Seers ring", "Tormented bracelet", "Ancestral hat", "Ancestral robe top", "Ancestral robe bottom", "Void mage helm", "Imbued god cape", "Arcane spirit shield", "Master wand", "Staff of air", "Staff of water", "Staff of earth", "Staff of fire", "Kodai wand", "Toxic staff of the dead"));
        tabs.get(6).getActions().add("Wield");

        tabs.add(new BankLayoutTab(8, "Skilling - Mining/Smithing", Color.PINK));
        tabs.get(7).getKeywords().addAll(Arrays.asList("Pickaxe", "Ore", "Bar", "Hammer", "Anvil", "Smithing", "Coal", "Iron", "Mithril", "Adamant", "Rune", "Gold", "Silver", "Bronze", "Steel"));

        tabs.add(new BankLayoutTab(9, "Skilling - Fishing", Color.CYAN.darker()));
        tabs.get(8).getKeywords().addAll(Arrays.asList("Fishing rod", "Net", "Harpoon", "Bait", "Lure", "Fish", "Shrimp", "Sardine", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Meagren", "Monkfish", "Shark", "Sea turtle", "Manta ray", "Dark crab", "Anglerfish", "Karambwanji", "Raw", "Fish"));
        tabs.get(8).getKeywords().addAll(Arrays.asList("Big net", "Cage", "Harpoon"));

        tabs.add(new BankLayoutTab(10, "Skilling - Woodcutting", Color.ORANGE.darker()));
        tabs.get(9).getKeywords().addAll(Arrays.asList("Axe", "Hatchet", "Logs", "Teak", "Mahogany", "Yew", "Magic", "Redwood", "Stump"));

        tabs.add(new BankLayoutTab(11, "Quest Items", Color.LIGHT_GRAY));
        // Keywords for quest items are harder to generalize. Might need specific item names.

        tabs.add(new BankLayoutTab(12, "Miscellaneous", Color.DARK_GRAY));
        // For items that don't fit anywhere else.

        preset.setTabs(tabs);
        return preset;
    }

    private BankLayoutPreset createIronmanPreset() {
        BankLayoutPreset preset = new BankLayoutPreset();
        preset.setName(PRESET_IRONMAN);
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Food", Color.RED));
        tabs.get(0).getKeywords().addAll(Arrays.asList("Tuna", "Shark", "Karambwan", "Manta ray", "Anglerfish", "Brew"));

        tabs.add(new BankLayoutTab(2, "Potions", Color.ORANGE));
        tabs.get(1).getKeywords().addAll(Arrays.asList("Saradomin brew", "Super restore", "Prayer potion", "Ranging potion", "Magic potion", "Stamina potion", "Antidote", "Vile blowpipe", "Sanfew serum", "Energy potion", "Attack potion", "Strength potion", "Defence potion", "Agility potion", "Farming potion", "Fishing potion", "Hunter potion", "Mining potion", "Rune crafting potion", "Thieving potion", "Vigor potion"));

        tabs.add(new BankLayoutTab(3, "Ammunition", Color.YELLOW));
        tabs.get(2).getKeywords().addAll(Arrays.asList("Dragon arrow", "Rune arrow", "Broad bolt", "Dragon bolt", "Onyx bolt", "Ruby bolt", "Broad arrow", "Amethyst arrow", "Amethyst bolt"));

        tabs.add(new BankLayoutTab(4, "Runes", Color.CYAN));
        tabs.get(3).getKeywords().addAll(Arrays.asList("Air rune", "Water rune", "Earth rune", "Fire rune", "Mind rune", "Body rune", "Death rune", "Chaos rune", "Rune essence", "Pure essence", "Cosmic rune", "Nature rune", "Law rune", "Blood rune", "Soul rune", "Astral rune", "Wrath rune", "Our([]| )ble rune", "Steam rune", "Lava rune", "Mud rune"));

        tabs.add(new BankLayoutTab(5, "Gear - Melee", Color.GREEN));
        tabs.get(4).getKeywords().addAll(Arrays.asList("Dragon dagger", "Abyssal whip", "Dragon scimitar", "Ghrazi ghoul", "Ghazi ghoul", "Scythe", "Blade of saeldor", "Dragon hasta", "Tzhaar-ket-om", "Dragon mace", "Dragon longsword", "Dragon battleaxe", "Ghazi ghoul", "Ghazi ward", "Ghazi platebody", "Ghazi platelegs", "Ghazi helm", "Ghazi boots", "Ghazi gloves", "Ghazi cape", "Ghazi amulet", "Ghazi ring", "Ghazi shield", "Ghazi defender"));
        tabs.get(4).getActions().add("Wield");

        tabs.add(new BankLayoutTab(6, "Gear - Ranged", Color.BLUE));
        tabs.get(5).getKeywords().addAll(Arrays.asList("Magic shortbow", "Toxic blowpipe", "Dragon crossbow", "Armadyl crossbow", "Odium ward", "Malediction ward", "God d'hide", "Karil's leather", "Black d'hide", "Blue d'hide", "Green d'hide", "Red d'hide", "Adamant dragon bolts", "Rune crossbow", "Toxic staff of the dead"));
        tabs.get(5).getActions().add("Wield");

        tabs.add(new BankLayoutTab(7, "Gear - Magic", Color.MAGENTA));
        tabs.get(6).getKeywords().addAll(Arrays.asList("Occult necklace", "Mage's book", "Seers ring", "Tormented bracelet", "Ancestral hat", "Ancestral robe top", "Ancestral robe bottom", "Void mage helm", "Imbued god cape", "Arcane spirit shield", "Master wand", "Staff of air", "Staff of water", "Staff of earth", "Staff of fire", "Kodai wand", "Toxic staff of the dead"));
        tabs.get(6).getActions().add("Wield");

        tabs.add(new BankLayoutTab(8, "Skilling", Color.PINK));
        tabs.get(7).getKeywords().addAll(Arrays.asList("Pickaxe", "Ore", "Bar", "Hammer", "Anvil", "Smithing", "Coal", "Iron", "Mithril", "Adamant", "Rune", "Gold", "Silver", "Bronze", "Steel", "Fishing rod", "Net", "Harpoon", "Bait", "Lure", "Fish", "Shrimp", "Sardine", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Meagren", "Monkfish", "Shark", "Sea turtle", "Manta ray", "Dark crab", "Anglerfish", "Karambwanji", "Raw", "Fish", "Axe", "Hatchet", "Logs", "Teak", "Mahogany", "Yew", "Magic", "Redwood", "Stump"));

        tabs.add(new BankLayoutTab(9, "Quest Items", Color.LIGHT_GRAY));

        tabs.add(new BankLayoutTab(10, "Miscellaneous", Color.DARK_GRAY));

        preset.setTabs(tabs);
        return preset;
    }

    private BankLayoutPreset createSkillerPreset() {
        BankLayoutPreset preset = new BankLayoutPreset();
        preset.setName(PRESET_SKILLER);
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Mining", Color.RED));
        tabs.get(0).getKeywords().addAll(Arrays.asList("Pickaxe", "Ore", "Coal", "Iron", "Mithril", "Adamant", "Rune", "Gold", "Silver", "Bronze", "Steel", "Essence", "Pure essence", "Rune essence"));

        tabs.add(new BankLayoutTab(2, "Smithing", Color.ORANGE));
        tabs.get(1).getKeywords().addAll(Arrays.asList("Hammer", "Anvil", "Bar", "Smithing", "Platebody", "Platelegs", "Helm", "Kite shield", "Battleaxe", "Longsword", "Scimitar", "Dagger", "Mace", "Spear", "Hatchet", "Pickaxe", "Shield", "Two handed sword"));

        tabs.add(new BankLayoutTab(3, "Fishing", Color.YELLOW));
        tabs.get(2).getKeywords().addAll(Arrays.asList("Fishing rod", "Net", "Harpoon", "Bait", "Lure", "Fish", "Shrimp", "Sardine", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Meagren", "Monkfish", "Shark", "Sea turtle", "Manta ray", "Dark crab", "Anglerfish", "Karambwanji", "Raw", "Cooked", "Fish"));
        tabs.get(2).getKeywords().addAll(Arrays.asList("Big net", "Cage", "Harpoon"));

        tabs.add(new BankLayoutTab(4, "Cooking", Color.GREEN));
        tabs.get(3).getKeywords().addAll(Arrays.asList("Cooking ingredient", "Raw", "Cooked", "Pie", "Cake", "Stew", "Curry", "Soup", "Pizza", "Dough", "Doughnut", "Wine", "Beer", "Ale"));

        tabs.add(new BankLayoutTab(5, "Woodcutting", Color.BLUE));
        tabs.get(4).getKeywords().addAll(Arrays.asList("Axe", "Hatchet", "Logs", "Teak", "Mahogany", "Yew", "Magic", "Redwood", "Stump", "Stairs", "Wood"));

        tabs.add(new BankLayoutTab(6, "Firemaking", Color.MAGENTA));
        tabs.get(5).getKeywords().addAll(Arrays.asList("Tinderbox", "Logs", "Teak logs", "Mahogany logs", "Yew logs", "Magic logs", "Redwood logs", "Fire"));

        tabs.add(new BankLayoutTab(7, "Runecraft", Color.CYAN));
        tabs.get(6).getKeywords().addAll(Arrays.asList("Rune essence", "Pure essence", "Air rune", "Water rune", "Earth rune", "Fire rune", "Mind rune", "Body rune", "Death rune", "Chaos rune", "Cosmic rune", "Nature rune", "Law rune", "Blood rune", "Soul rune", "Astral rune", "Wrath rune", "Our([]| )ble rune", "Steam rune", "Lava rune", "Mud rune", "Elemental", "Body", "Mind", "Chaos", "Death", "Nature", "Law", "Blood", "Soul", "Astral", "Wrath", "Our([]| )ble", "Steam", "Lava", "Mud"));

        tabs.add(new BankLayoutTab(8, "Herblore", Color.PINK.darker()));
        tabs.get(7).getKeywords().addAll(Arrays.asList("Mortar", "Pestle", "Vial", "Empty vial", "Ground", "Unpowered orb", "Limpwurt root", "Clean", "Herb", "Unfinished potion", "Vial of water", "Harralander", "Ardougne", "Avantoe", "Bloodweed", "Cadant-ibus", "Dwarf weed", "Eye of newt", "Guthix balance", "Irit leaf", "Janger root", "Kardinal", "Keldagrim", "Lantadyme", "Limpwurt root", "Marrentill", "Mint", "Mithril", "Mushroom", "Nettle", "Ogre blood", "Ogre-flame", "Ranarr weed", "Red spiders' eggs", "Rotten tomato", "Snape grass", "Snapdragon", "Super", "Tarromin", "Torstol", "Twisted", "Unfinished", "Vile", "Wine", "Yellow", "Yellow spice"));
        tabs.get(7).getKeywords().addAll(Arrays.asList("Herb", "Potion", "Oil", "Poison"));

        tabs.add(new BankLayoutTab(9, "Crafting", Color.CYAN.darker()));
        tabs.get(8).getKeywords().addAll(Arrays.asList("Chisel", "Dyes", "Thread", "Needle", "Leather", "Hide", "Wool", "Clue", "Unfinished", "Gem", "Cut", "Uncut", "Bolt", "Arrow", "Dart", "String", "Unstrung", "Bow", "Shield", "Ring", "Amulet", "Bracelet", "Earrings", "Tiara", "Rod", "Belt", "Boots", "Gloves", "Hat", "Mask", "Helm", "Body", "Legs", "Cape", "Scarf", "Robe"));

        tabs.add(new BankLayoutTab(10, "Construction", Color.GRAY));
        tabs.get(9).getKeywords().addAll(Arrays.asList("Plank", "Mahogany plank", "Teak plank", "Oak plank", "Steel bar", "Iron bar", "Gold bar", "Marble", "Limestone brick", "Clay", "Hammer", "Saw", "Nails", "Garden", "Gnome", "Bench", "Altar", "Table", "Chair", "Door", "Window", "Wardrobe", "Bookshelf"));

        tabs.add(new BankLayoutTab(11, "Farming", Color.GREEN.darker()));
        tabs.get(10).getKeywords().addAll(Arrays.asList("Trellis", "Seed", "Allotment", "Horticulture", "Flower", "Herb patch", "Fruit tree", "Farming equipment", "Watering can", "Rake", "Spade", "Gardening gloves", "Magic secateurs", "Gnomon", "Plant pot", "Compost", "Super compost", "Ultra compost", "Fertilizer"));

        tabs.add(new BankLayoutTab(12, "Hunter", Color.PINK));
        tabs.get(11).getKeywords().addAll(Arrays.asList("Box trap", "Net", "Snare", "Deadfall trap", "Rabbit", "Chinchompa", "Grenwall", "Dragon", "Bird snares", "Butterfly net", "Butterfly", "Agility", "Amulet", "Charm"));

        tabs.add(new BankLayoutTab(13, "Miscellaneous", Color.DARK_GRAY));

        preset.setTabs(tabs);
        return preset;
    }

    private BankLayoutPreset createDefaultPreset() {
        // This will be the default preset when no config is found or an error occurs.
        // It should contain the "Uncategorized" tab by default.
        BankLayoutPreset preset = new BankLayoutPreset();
        preset.setName("Default");
        List<BankLayoutTab> tabs = new ArrayList<>();
        tabs.add(new BankLayoutTab(1, UNCATEGORIZED_TAB_NAME, Color.GRAY));
        preset.setTabs(tabs);
        return preset;
    }

    /**
     * Finds the appropriate BankLayoutTab for a given item.
     * It first tries to match keywords, then falls back to actions.
     * If no match is found, it returns the "Uncategorized" tab.
     *
     * @param itemComposition The ItemComposition of the item.
     * @return The BankLayoutTab that matches the item, or the "Uncategorized" tab.
     */
    public BankLayoutTab getTabForItem(ItemComposition itemComposition) {
        if (itemComposition == null) {
            return null; // Or a default tab if appropriate
        }

        // Iterate through all tabs to find a match
        for (BankLayoutTab tab : activePreset.getTabs()) {
            // Skip the Uncategorized tab in this initial matching phase
            if (tab.isUncategorized()) {
                continue;
            }
            if (tab.matches(itemComposition)) {
                return tab;
            }
        }

        // If no tab matched, return the Uncategorized tab
        // We need to ensure the Uncategorized tab is always available in activePreset.tabs
        return activePreset.getTabs().stream()
                .filter(BankLayoutTab::isUncategorized)
                .findFirst()
                .orElseGet(() -> {
                    // This case should ideally not happen if ensureUncategorizedTabExists is working
                    // but as a safeguard, we create and return a new Uncategorized tab.
                    // Note: This new tab won't be automatically added to the activePreset's list
                    // or saved, so it's a temporary fallback.
                    System.err.println("CRITICAL ERROR: Uncategorized tab not found in active preset!");
                    return new BankLayoutTab(-1, UNCATEGORIZED_TAB_NAME, Color.GRAY); // Temporary tab
                });
    }

    // You'll need to implement this based on your actual ItemManager and Client usage
    // This is a placeholder to get ItemComposition from a widget child ID.
    // You'll likely use clientThread.invokeLater to access RuneLite client APIs safely.
    public Optional<ItemComposition> getItemCompositionFromWidget(Widget widget) {
        if (widget == null) {
            return Optional.empty();
        }
        // Assuming widget child ID can be used to get item ID.
        // This part might need adjustment based on how your BankOverlay gets item IDs.
        int itemId = widget.getItemId(); // This is often how you get item ID from a widget

        if (itemId == -1) { // -1 usually means no item
            return Optional.empty();
        }

        // Use ItemManager to get ItemComposition.
        // The itemManager.getItemComposition(itemId) call needs to be on the client thread.
        // So, we'll wrap it in a clientThread.invokeLater if needed, or call it from the right context.
        // For simplicity here, assuming itemManager is already configured to work correctly.
        // If itemManager is not injected or accessible, you'd need to use client.cacheItemComposition(itemId).
        return Optional.ofNullable(itemManager.getItemComposition(itemId));
    }

    // Method to add a new tab to the active preset
    public void addTab(BankLayoutTab newTab) {
        if (activePreset != null && activePreset.getTabs() != null) {
            // Assign the next available tab number
            int nextNumber = activePreset.getTabs().stream()
                    .mapToInt(BankLayoutTab::getNumber)
                    .max()
                    .orElse(0) + 1;
            newTab.setNumber(nextNumber);
            activePreset.getTabs().add(newTab);
            sortTabsByNumber(activePreset); // Keep tabs sorted
            saveActivePreset(); // Save after adding
        }
    }

    // Method to remove a tab
    public void removeTab(BankLayoutTab tabToRemove) {
        if (activePreset != null && activePreset.getTabs() != null) {
            activePreset.getTabs().remove(tabToRemove);
            // Re-number remaining tabs to maintain sequential order
            int currentNumber = 1;
            for (BankLayoutTab tab : activePreset.getTabs()) {
                if (!tab.isUncategorized()) { // Don't re-number Uncategorized tab if it's special
                    tab.setNumber(currentNumber++);
                }
            }
            sortTabsByNumber(activePreset); // Ensure sorting after re-numbering
            saveActivePreset(); // Save after removing
        }
    }
}
