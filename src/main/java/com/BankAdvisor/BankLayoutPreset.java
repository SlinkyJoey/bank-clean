package com.BankAdvisor;

import net.runelite.api.ItemComposition;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankLayoutPreset {

    private String name;
    private List<BankLayoutTab> tabs;

    public BankLayoutPreset(String name, List<BankLayoutTab> tabs) {
        this.name = name;
        this.tabs = new ArrayList<>(tabs);
    }

    public BankLayoutPreset copy() {
        List<BankLayoutTab> copiedTabs = new ArrayList<>();
        for (BankLayoutTab tab : tabs) {
            copiedTabs.add(new BankLayoutTab(tab));
        }
        return new BankLayoutPreset(this.name, copiedTabs);
    }

    // Find the tab for an item using its full ItemComposition.
    // Keywords are checked first (specific matches take priority),
    // then action triggers as a fallback (catches any equippable/consumable
    // item not covered by keywords — works for all future items too).
    public BankLayoutTab getTabForItem(ItemComposition comp) {
        String itemName = comp.getName();
        String[] actions = comp.getInventoryActions();

        // 1. Keyword matching first — specific named items go to the right tab
        //    (e.g. "pickaxe" goes to Skilling, not Gear, even though it has Wield action)
        for (BankLayoutTab tab : tabs) {
            if (tab.matches(itemName)) {
                return tab;
            }
        }

        // 2. Action-based fallback — catches anything equippable/consumable
        //    not covered by keywords (new items, obscure gear, etc.)
        for (BankLayoutTab tab : tabs) {
            if (tab.matchesActions(actions)) {
                return tab;
            }
        }

        return null;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<BankLayoutTab> getTabs() { return tabs; }

    public static List<BankLayoutPreset> getBuiltInPresets() {
        List<BankLayoutPreset> presets = new ArrayList<>();
        presets.add(buildStandardMain());
        presets.add(buildIronman());
        presets.add(buildSkiller());
        return presets;
    }

    private static BankLayoutPreset buildStandardMain() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        // Tab 1: Consumables — keywords cover named items, actions catch anything new
        tabs.add(new BankLayoutTab(1, "Consumables", new Color(220, 60, 60),
            Arrays.asList(
                "potion", "brew", "antifire", "restore", "bastion",
                "battlemage", "sanfew", "shark", "anglerfish", "manta ray",
                "dark crab", "karambwan", "monkfish", "lobster", "swordfish",
                "cooked", "tuna", "salmon", "trout", "pizza", "pie",
                "cake", "bread", "stew", "wine", "jug", "shrimps",
                "sardine", "herring", "anchovies", "mackerel", "bass",
                "kebab", "potato"
            ),
            Arrays.asList("Eat", "Drink") // fallback: anything you eat or drink
        ));

        // Tab 2: Runes & Ammo — all stackable, name-based only
        tabs.add(new BankLayoutTab(2, "Runes & Ammo", new Color(100, 149, 237),
            Arrays.asList(
                " rune", "rune (", "pure essence", "rune essence",
                "arrow", " bolt", " dart", "cannonball", "crystal shard",
                "javelin", "ballista"
            ),
            new ArrayList<>() // no action fallback needed — runes don't have special actions
        ));

        // Tab 3: Gear — keywords cover common gear names,
        // Wear/Wield/Equip fallback catches everything else equippable
        tabs.add(new BankLayoutTab(3, "Gear", new Color(180, 120, 220),
            Arrays.asList(
                "helm", "platebody", "chainbody", "platelegs", "plateskirt",
                "chaps", "dhide", "armour", "shield", "sword", "scimitar",
                "whip", "bow", "crossbow", "wand", "boots",
                "gloves", "cape", "amulet", "ring", "brace", "vamb",
                "gauntlet", "coif", "mask", "hood", "robe top", "robe bottom",
                "bandos", "armadyl", "ancestral", "torva", "masori",
                "void", "slayer helm", "berserker", "archers", "seers",
                "torture", "anguish", "occult", "fury", "dragon", "barrows",
                "blessed", "blowpipe", "trident", "scythe", "sang", "fang",
                "rapier", "hasta", "lance", "ballista", "atlatl"
            ),
            Arrays.asList("Wear", "Wield", "Equip", "Operate") // catch ALL equippable items
        ));

        // Tab 4: Slayer — keywords only, slayer gear also caught by Gear tab via Wear/Wield
        tabs.add(new BankLayoutTab(4, "Slayer", new Color(220, 140, 50),
            Arrays.asList(
                "slayer", "cannon", "broad", "leaf-bladed",
                "bag of salt", "rock hammer", "fungicide", "mirror shield",
                "nose peg", "earmuff", "facemask", "enchanted gem",
                "slayer ring", "black mask", "dwarf multicannon",
                "cannonball", "ice cooler"
            ),
            new ArrayList<>()
        ));

        // Tab 5: Skilling — keywords MUST come before Gear in the tab list so
        // pickaxes/axes match Skilling via keyword before Gear's Wield trigger
        tabs.add(new BankLayoutTab(5, "Skilling", new Color(80, 180, 100),
            Arrays.asList(
                "pickaxe", "axe", "hatchet", "fishing rod", "fly fishing",
                "lobster pot", "harpoon", "net", "tinderbox", "chisel",
                "hammer", "needle", "thread", "knife", "saw", "plank",
                "graceful", "rake", "spade", "seed dibber", "watering can",
                "secateurs", "bull roarer", "pestle and mortar",
                "crafting cape", "smithing cape", "mining cape"
            ),
            new ArrayList<>()
        ));

        // Tab 6: Herblore & Farming
        tabs.add(new BankLayoutTab(6, "Herblore & Farming", new Color(100, 200, 100),
            Arrays.asList(
                "grimy", "clean ", " herb", " seed", " seeds",
                "compost", "supercompost", "ultracompost", "vial",
                "pestle", "mortar", "unfinished", "snape grass",
                "eye of newt", "limpwurt", "red spiders", "white berries",
                "noxifer", "golovanova", "logavano"
            ),
            new ArrayList<>()
        ));

        // Tab 7: Raw Materials
        tabs.add(new BankLayoutTab(7, "Raw Materials", new Color(180, 160, 100),
            Arrays.asList(
                " logs", " ore", " bar", "gem", "sapphire", "emerald",
                "ruby", "diamond", "dragonstone", "onyx", "zenyte",
                "opal", "jade", "topaz", "raw ", "feather", "bone",
                "hide", "leather", "flax", "wool", "ball of wool",
                "molten glass", "bucket of sand", "soda ash"
            ),
            new ArrayList<>()
        ));

        // Tab 8: Quest & Misc
        tabs.add(new BankLayoutTab(8, "Quest & Misc", new Color(150, 150, 150),
            Arrays.asList(
                "clue", "quest", "key", "scroll", "casket",
                "mysterious", "strange", "seal", "certificate",
                "ticket", "token", "holiday", "ensouled",
                "imbued", "jar of"
            ),
            new ArrayList<>()
        ));

        return new BankLayoutPreset("Standard Main", tabs);
    }

    private static BankLayoutPreset buildIronman() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Consumables", new Color(220, 60, 60),
            Arrays.asList(
                "potion", "brew", "antifire", "restore",
                "shark", "anglerfish", "manta ray", "dark crab",
                "karambwan", "monkfish", "cooked"
            ),
            Arrays.asList("Eat", "Drink")
        ));

        tabs.add(new BankLayoutTab(2, "Runes & Ammo", new Color(100, 149, 237),
            Arrays.asList(" rune", "rune (", "pure essence", "rune essence",
                "arrow", " bolt", " dart", "cannonball"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(3, "Gear", new Color(180, 120, 220),
            Arrays.asList(
                "helm", "body", "legs", "shield", "sword", "bow",
                "staff", "boots", "gloves", "cape", "amulet", "ring",
                "armour", "barrows", "void", "dragon", "blessed"
            ),
            Arrays.asList("Wear", "Wield", "Equip", "Operate")
        ));

        tabs.add(new BankLayoutTab(4, "Skilling", new Color(80, 180, 100),
            Arrays.asList("pickaxe", "axe", "hatchet", "rod", "net",
                " ore", " bar", "coal", "leather", "hide", "plank",
                "tinderbox", "chisel", "hammer", "needle", "thread"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(5, "Herblore & Farming", new Color(100, 200, 100),
            Arrays.asList("grimy", "clean ", " herb", " seed", " seeds",
                "compost", "vial", "unfinished"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(6, "Drops & Materials", new Color(180, 160, 100),
            Arrays.asList("bones", "ashes", "hide", "gem", "sapphire", "emerald",
                "ruby", "diamond", "raw ", "feather", "clue", "ensouled"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(7, "Quest & Misc", new Color(150, 150, 150),
            Arrays.asList("quest", "key", "scroll", "seal", "certificate",
                "ticket", "token", "strange", "mysterious"),
            new ArrayList<>()
        ));

        return new BankLayoutPreset("Ironman", tabs);
    }

    private static BankLayoutPreset buildSkiller() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Food & Supplies", new Color(220, 60, 60),
            Arrays.asList("cooked", "shark", "lobster", "tuna", "salmon", "potion", "restore"),
            Arrays.asList("Eat", "Drink")
        ));

        tabs.add(new BankLayoutTab(2, "Woodcutting", new Color(139, 90, 43),
            Arrays.asList("axe", "hatchet", " logs", "woodcutting"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(3, "Mining & Smithing", new Color(120, 120, 140),
            Arrays.asList("pickaxe", " ore", " bar", "coal", "hammer", "mould"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(4, "Fishing & Cooking", new Color(60, 120, 200),
            Arrays.asList("rod", "net", "harpoon", "pot", "raw ", "feather", "bait", "knife"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(5, "Farming & Herblore", new Color(80, 180, 100),
            Arrays.asList(" seed", " seeds", "compost", "rake", "spade",
                "dibber", "watering", "grimy", "clean ", " herb", "vial", "unfinished"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(6, "Crafting & Fletching", new Color(200, 160, 60),
            Arrays.asList("chisel", "needle", "thread", "leather", "hide",
                "gem", "sapphire", "emerald", "ruby", "diamond",
                "bowstring", "knife", "shaft", "unstrung"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(7, "Magic & Runecrafting", new Color(100, 149, 237),
            Arrays.asList(" rune", "essence", "talisman", "tiara", "staff", "wand", "pouch"),
            new ArrayList<>()
        ));

        tabs.add(new BankLayoutTab(8, "Misc", new Color(150, 150, 150),
            Arrays.asList("clue", "quest", "key", "scroll"),
            new ArrayList<>()
        ));

        return new BankLayoutPreset("Skiller", tabs);
    }
}
