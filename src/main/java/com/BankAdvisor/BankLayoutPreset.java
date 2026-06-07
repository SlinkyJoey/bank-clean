package com.BankAdvisor;

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

    public BankLayoutTab getTabForItem(String itemName) {
        for (BankLayoutTab tab : tabs) {
            if (tab.matches(itemName)) {
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

        tabs.add(new BankLayoutTab(1, "Consumables", new Color(220, 60, 60),
            Arrays.asList("potion", "brew", "antifire", "restore", "bastion",
                "battlemage", "sanfew", "shark", "anglerfish", "manta ray",
                "dark crab", "karambwan", "monkfish", "lobster", "swordfish",
                "cooked", "tuna", "salmon", "pizza", "pie", "cake", "bread", "stew")));

        tabs.add(new BankLayoutTab(2, "Runes & Ammo", new Color(100, 149, 237),
            Arrays.asList(" rune", "rune (", "pure essence", "rune essence",
                "arrow", " bolt", " dart", "cannonball", "crystal shard")));

        tabs.add(new BankLayoutTab(3, "Gear", new Color(180, 120, 220),
            Arrays.asList("helm", "platebody", "chainbody", "platelegs", "plateskirt",
                "chaps", "dhide", "armour", "shield", "sword", "scimitar",
                "whip", "bow", "crossbow", "staff", "wand", "boots",
                "gloves", "cape", "amulet", "ring", "brace", "vamb",
                "gauntlet", "coif", "mask", "hood", "robe top", "robe bottom",
                "bandos", "armadyl", "ancestral", "torva", "masori",
                "void", "slayer helm", "berserker", "archers", "seers",
                "torture", "anguish", "occult", "fury", "dragon", "barrows", "blessed")));

        tabs.add(new BankLayoutTab(4, "Slayer", new Color(220, 140, 50),
            Arrays.asList("slayer", "cannon", "cannonball", "broad", "leaf-bladed",
                "bag of salt", "rock hammer", "fungicide", "mirror shield",
                "nose peg", "earmuff", "facemask", "enchanted gem",
                "slayer ring", "slayer cape", "black mask")));

        tabs.add(new BankLayoutTab(5, "Skilling", new Color(80, 180, 100),
            Arrays.asList("pickaxe", "axe", "hatchet", "fishing rod", "fly fishing",
                "lobster pot", "harpoon", "net", "tinderbox", "chisel",
                "hammer", "needle", "thread", "knife", "saw", "plank",
                "leather", "hide", " bar", " ore", "coal", "logs",
                "graceful", "rake", "spade", "seed dibber", "watering can", "secateurs")));

        tabs.add(new BankLayoutTab(6, "Herblore & Farming", new Color(100, 200, 100),
            Arrays.asList("grimy", "clean ", " herb", " seed", " seeds",
                "compost", "supercompost", "ultracompost", "vial",
                "pestle", "mortar", "unfinished", "snape grass",
                "eye of newt", "limpwurt", "red spiders", "white berries")));

        tabs.add(new BankLayoutTab(7, "Raw Materials", new Color(180, 160, 100),
            Arrays.asList(" logs", " ore", " bar", "gem", "sapphire", "emerald",
                "ruby", "diamond", "dragonstone", "onyx", "zenyte",
                "raw ", "feather", "bone", "hide")));

        tabs.add(new BankLayoutTab(8, "Quest & Misc", new Color(150, 150, 150),
            Arrays.asList("clue", "quest", "key", "scroll", "casket",
                "mysterious", "strange", "seal", "certificate",
                "ticket", "token", "holiday")));

        return new BankLayoutPreset("Standard Main", tabs);
    }

    private static BankLayoutPreset buildIronman() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Consumables", new Color(220, 60, 60),
            Arrays.asList("potion", "brew", "antifire", "restore",
                "shark", "anglerfish", "manta ray", "dark crab",
                "karambwan", "monkfish", "cooked")));

        tabs.add(new BankLayoutTab(2, "Runes & Ammo", new Color(100, 149, 237),
            Arrays.asList(" rune", "rune (", "pure essence", "rune essence",
                "arrow", " bolt", " dart", "cannonball")));

        tabs.add(new BankLayoutTab(3, "Gear", new Color(180, 120, 220),
            Arrays.asList("helm", "body", "legs", "shield", "sword", "bow",
                "staff", "boots", "gloves", "cape", "amulet", "ring",
                "armour", "barrows", "void", "dragon", "blessed")));

        tabs.add(new BankLayoutTab(4, "Skilling", new Color(80, 180, 100),
            Arrays.asList("pickaxe", "axe", "hatchet", "rod", "net", "logs",
                " ore", " bar", "coal", "leather", "hide", "plank",
                "tinderbox", "chisel", "hammer", "needle", "thread")));

        tabs.add(new BankLayoutTab(5, "Herblore & Farming", new Color(100, 200, 100),
            Arrays.asList("grimy", "clean ", " herb", " seed", " seeds",
                "compost", "vial", "unfinished")));

        tabs.add(new BankLayoutTab(6, "Drops & Materials", new Color(180, 160, 100),
            Arrays.asList("bones", "ashes", "hide", "gem", "sapphire", "emerald",
                "ruby", "diamond", "raw ", "feather", "clue", "ensouled")));

        tabs.add(new BankLayoutTab(7, "Quest & Misc", new Color(150, 150, 150),
            Arrays.asList("quest", "key", "scroll", "seal", "certificate",
                "ticket", "token", "strange", "mysterious")));

        return new BankLayoutPreset("Ironman", tabs);
    }

    private static BankLayoutPreset buildSkiller() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(1, "Food & Supplies", new Color(220, 60, 60),
            Arrays.asList("cooked", "shark", "lobster", "tuna", "salmon", "potion", "restore")));

        tabs.add(new BankLayoutTab(2, "Woodcutting", new Color(139, 90, 43),
            Arrays.asList("axe", "hatchet", " logs", "woodcutting")));

        tabs.add(new BankLayoutTab(3, "Mining & Smithing", new Color(120, 120, 140),
            Arrays.asList("pickaxe", " ore", " bar", "coal", "hammer", "mould")));

        tabs.add(new BankLayoutTab(4, "Fishing & Cooking", new Color(60, 120, 200),
            Arrays.asList("rod", "net", "harpoon", "pot", "raw ", "feather", "bait", "knife")));

        tabs.add(new BankLayoutTab(5, "Farming & Herblore", new Color(80, 180, 100),
            Arrays.asList(" seed", " seeds", "compost", "rake", "spade",
                "dibber", "watering", "grimy", "clean ", " herb", "vial", "unfinished")));

        tabs.add(new BankLayoutTab(6, "Crafting & Fletching", new Color(200, 160, 60),
            Arrays.asList("chisel", "needle", "thread", "leather", "hide",
                "gem", "sapphire", "emerald", "ruby", "diamond",
                "bowstring", "knife", "shaft", "unstrung")));

        tabs.add(new BankLayoutTab(7, "Magic & Runecrafting", new Color(100, 149, 237),
            Arrays.asList(" rune", "essence", "talisman", "tiara", "staff", "wand", "pouch")));

        tabs.add(new BankLayoutTab(8, "Misc", new Color(150, 150, 150),
            Arrays.asList("clue", "quest", "key", "scroll")));

        return new BankLayoutPreset("Skiller", tabs);
    }
}
