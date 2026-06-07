package com.BankAdvisor;

import net.runelite.api.ItemComposition;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankLayoutPreset {
    private String name;
    private List<BankLayoutTab> tabs = new ArrayList<>();

    public BankLayoutPreset() {
    }

    public BankLayoutPreset(String name, List<BankLayoutTab> tabs) {
        this.name = name;
        this.tabs = tabs == null ? new ArrayList<>() : new ArrayList<>(tabs);
    }

    public BankLayoutPreset copy() {
        List<BankLayoutTab> copiedTabs = new ArrayList<>();

        if (tabs != null) {
            for (BankLayoutTab tab : tabs) {
                copiedTabs.add(new BankLayoutTab(tab));
            }
        }

        return new BankLayoutPreset(name, copiedTabs);
    }

    public BankLayoutTab getTabForItem(ItemComposition comp) {
        if (comp == null || tabs == null) {
            return null;
        }

        String itemName = comp.getName();
        String[] actions = comp.getInventoryActions();

        for (BankLayoutTab tab : tabs) {
            if (!tab.isUncategorized() && tab.matches(itemName)) {
                return tab;
            }
        }

        for (BankLayoutTab tab : tabs) {
            if (!tab.isUncategorized() && tab.matchesActions(actions)) {
                return tab;
            }
        }

        return null;
    }

    public BankLayoutSection getSectionForItem(ItemComposition comp) {
        BankLayoutTab tab = getTabForItem(comp);
        if (tab == null) {
            return null;
        }

        return tab.getSectionForItem(comp);
    }

    public String getName() {
        return name;
    }

    public List<BankLayoutTab> getTabs() {
        if (tabs == null) {
            tabs = new ArrayList<>();
        }
        return tabs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTabs(List<BankLayoutTab> tabs) {
        this.tabs = tabs == null ? new ArrayList<>() : tabs;
    }

    public static List<BankLayoutPreset> getBuiltInPresets() {
        List<BankLayoutPreset> presets = new ArrayList<>();
        presets.add(buildStandardMain());
        presets.add(buildIronman());
        presets.add(buildSkiller());
        return presets;
    }

    private static BankLayoutPreset buildStandardMain() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(
                1,
                "Consumables",
                new Color(220, 60, 60),
                Arrays.asList(
                        "potion", "brew", "antifire", "restore", "food",
                        "shark", "anglerfish", "manta ray", "dark crab",
                        "karambwan", "monkfish", "lobster", "swordfish",
                        "tuna", "salmon", "trout", "pizza", "pie", "cake",
                        "bread", "stew", "wine", "jug"
                ),
                Arrays.asList("Eat", "Drink"),
                Arrays.asList(
                        new BankLayoutSection(
                                "Potions",
                                1,
                                new Color(220, 120, 60),
                                Arrays.asList("potion", "brew", "restore", "antifire", "sanfew"),
                                Arrays.asList("Drink")
                        ),
                        new BankLayoutSection(
                                "Food",
                                2,
                                new Color(220, 60, 60),
                                Arrays.asList("shark", "anglerfish", "manta ray", "karambwan", "lobster", "swordfish", "tuna", "salmon", "trout", "pizza", "pie", "cake", "bread"),
                                Arrays.asList("Eat")
                        )
                )
        ));

        tabs.add(new BankLayoutTab(
                2,
                "Runes & Ammo",
                new Color(100, 149, 237),
                Arrays.asList(
                        " rune", "rune essence", "pure essence",
                        "arrow", " bolt", " dart", "cannonball", "javelin"
                ),
                new ArrayList<>(),
                Arrays.asList(
                        new BankLayoutSection(
                                "Runes",
                                1,
                                new Color(100, 149, 237),
                                Arrays.asList(" rune", "rune essence", "pure essence"),
                                new ArrayList<>()
                        ),
                        new BankLayoutSection(
                                "Ammo",
                                2,
                                new Color(230, 210, 90),
                                Arrays.asList("arrow", " bolt", " dart", "cannonball", "javelin"),
                                new ArrayList<>()
                        )
                )
        ));

        tabs.add(new BankLayoutTab(
                3,
                "Gear",
                new Color(180, 120, 220),
                Arrays.asList(
                        "helm", "body", "legs", "shield", "sword", "scimitar",
                        "whip", "bow", "crossbow", "staff", "wand", "boots",
                        "gloves", "cape", "amulet", "ring", "robe", "armour",
                        "barrows", "dragon", "black d'hide", "blessed", "void",
                        "bandos", "armadyl", "ancestral", "torva", "masori",
                        "fang", "rapier", "defender", "blowpipe", "trident"
                ),
                Arrays.asList("Wear", "Wield", "Equip", "Operate"),
                Arrays.asList(
                        new BankLayoutSection(
                                "Melee",
                                1,
                                new Color(220, 80, 80),
                                Arrays.asList(
                                        "whip", "scimitar", "sword", "fang", "rapier", "hasta", "lance",
                                        "defender", "bandos", "torva", "platebody", "platelegs", "melee",
                                        "berserker", "torture"
                                ),
                                Arrays.asList("Wield")
                        ),
                        new BankLayoutSection(
                                "Ranged",
                                2,
                                new Color(80, 180, 100),
                                Arrays.asList(
                                        "bow", "crossbow", "blowpipe", "ballista", "d'hide", "chaps",
                                        "armadyl", "masori", "archer", "anguish", "ranged"
                                ),
                                Arrays.asList("Wield")
                        ),
                        new BankLayoutSection(
                                "Magic",
                                3,
                                new Color(100, 149, 237),
                                Arrays.asList(
                                        "staff", "wand", "trident", "shadow", "robe", "ancestral",
                                        "ahrim", "mystic", "occult", "tormented", "mage", "magic"
                                ),
                                Arrays.asList("Wield")
                        ),
                        new BankLayoutSection(
                                "Jewelry",
                                4,
                                new Color(240, 210, 90),
                                Arrays.asList("amulet", "ring", "bracelet", "necklace", "cape", "gloves", "boots"),
                                Arrays.asList("Wear", "Equip")
                        )
                )
        ));

        tabs.add(new BankLayoutTab(
                4,
                "Skilling",
                new Color(80, 180, 100),
                Arrays.asList(
                        "pickaxe", "axe", "hatchet", "fishing rod", "harpoon",
                        "net", "tinderbox", "chisel", "hammer", "needle",
                        "thread", "knife", "saw", "plank", "rake", "spade",
                        "watering can", "secateurs"
                ),
                new ArrayList<>(),
                Arrays.asList(
                        new BankLayoutSection(
                                "Tools",
                                1,
                                new Color(80, 180, 100),
                                Arrays.asList("pickaxe", "axe", "hatchet", "fishing rod", "harpoon", "net", "tinderbox", "chisel", "hammer", "needle", "thread", "knife", "saw"),
                                new ArrayList<>()
                        ),
                        new BankLayoutSection(
                                "Farming",
                                2,
                                new Color(100, 200, 100),
                                Arrays.asList("rake", "spade", "watering can", "secateurs"),
                                new ArrayList<>()
                        )
                )
        ));

        tabs.add(new BankLayoutTab(
                5,
                "Herblore & Farming",
                new Color(100, 200, 100),
                Arrays.asList(
                        "grimy", "clean ", " herb", " seed", " seeds",
                        "compost", "vial", "unfinished", "snape grass",
                        "eye of newt", "limpwurt"
                )
        ));

        tabs.add(new BankLayoutTab(
                6,
                "Raw Materials",
                new Color(180, 160, 100),
                Arrays.asList(
                        " logs", " ore", " bar", "gem", "sapphire", "emerald",
                        "ruby", "diamond", "dragonstone", "onyx", "raw ",
                        "feather", "bone", "hide", "leather", "flax", "wool"
                )
        ));

        tabs.add(new BankLayoutTab(
                7,
                "Quest & Misc",
                new Color(150, 150, 150),
                Arrays.asList(
                        "clue", "quest", "key", "scroll", "casket",
                        "mysterious", "strange", "ticket", "token"
                )
        ));

        tabs.add(new BankLayoutTab(8, "Uncategorized", Color.GRAY));

        return new BankLayoutPreset("Standard Main", tabs);
    }

    private static BankLayoutPreset buildIronman() {
        BankLayoutPreset preset = buildStandardMain().copy();
        preset.setName("Ironman");
        return preset;
    }

    private static BankLayoutPreset buildSkiller() {
        List<BankLayoutTab> tabs = new ArrayList<>();

        tabs.add(new BankLayoutTab(
                1,
                "Food & Supplies",
                new Color(220, 60, 60),
                Arrays.asList("cooked", "shark", "lobster", "tuna", "salmon", "potion", "restore"),
                Arrays.asList("Eat", "Drink")
        ));

        tabs.add(new BankLayoutTab(
                2,
                "Woodcutting",
                new Color(139, 90, 43),
                Arrays.asList("axe", "hatchet", " logs", "woodcutting")
        ));

        tabs.add(new BankLayoutTab(
                3,
                "Mining & Smithing",
                new Color(120, 120, 140),
                Arrays.asList("pickaxe", " ore", " bar", "coal", "hammer", "mould")
        ));

        tabs.add(new BankLayoutTab(
                4,
                "Fishing & Cooking",
                new Color(60, 120, 200),
                Arrays.asList("rod", "net", "harpoon", "pot", "raw ", "feather", "bait", "knife")
        ));

        tabs.add(new BankLayoutTab(
                5,
                "Farming & Herblore",
                new Color(80, 180, 100),
                Arrays.asList(" seed", " seeds", "compost", "rake", "spade", "watering", "grimy", "clean ", " herb", "vial")
        ));

        tabs.add(new BankLayoutTab(
                6,
                "Crafting & Fletching",
                new Color(200, 160, 60),
                Arrays.asList("chisel", "needle", "thread", "leather", "hide", "gem", "bowstring", "knife", "shaft", "unstrung")
        ));

        tabs.add(new BankLayoutTab(
                7,
                "Magic & Runecrafting",
                new Color(100, 149, 237),
                Arrays.asList(" rune", "essence", "talisman", "tiara", "staff", "wand", "pouch")
        ));

        tabs.add(new BankLayoutTab(
                8,
                "Misc",
                new Color(150, 150, 150),
                Arrays.asList("clue", "quest", "key", "scroll")
        ));

        tabs.add(new BankLayoutTab(9, "Uncategorized", Color.GRAY));

        return new BankLayoutPreset("Skiller", tabs);
    }
}