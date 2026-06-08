package com.BankAdvisor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Singleton
public class BankLayoutManager {
    private static final String CONFIG_GROUP = "bankadvisor";
    private static final String CONFIG_KEY_LAYOUT = "banklayout";

    private static final String PRESET_STANDARD_MAIN = "Standard Main";
    private static final String PRESET_IRONMAN = "Ironman";
    private static final String PRESET_SKILLER = "Skiller";
    private static final String UNCATEGORIZED_TAB_NAME = "Uncategorized";

    private BankLayoutPreset activePreset = createDefaultPreset();

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .create();

    private enum CombatStyle {
        MELEE("Melee"),
        RANGED("Ranged"),
        MAGIC("Magic");

        private final String sectionName;

        CombatStyle(String sectionName) {
            this.sectionName = sectionName;
        }

        String getSectionName() {
            return sectionName;
        }
    }

    public BankLayoutPreset getActivePreset() {
        return activePreset;
    }

    public void loadSavedOrDefaultPreset() {
        BankLayoutPreset preset = loadPresetFromConfig();

        if (preset == null || preset.getTabs() == null || preset.getTabs().isEmpty()) {
            preset = getBuiltInPresetCopy(PRESET_STANDARD_MAIN);
        }

        ensureUncategorizedTabExists(preset);
        sortTabsByNumber(preset);
        activePreset = preset;
    }

    public void loadPreset(String presetName) {
        BankLayoutPreset preset;

        if (PRESET_STANDARD_MAIN.equals(presetName)) {
            preset = getBuiltInPresetCopy(PRESET_STANDARD_MAIN);
        } else if (PRESET_IRONMAN.equals(presetName)) {
            preset = getBuiltInPresetCopy(PRESET_IRONMAN);
        } else if (PRESET_SKILLER.equals(presetName)) {
            preset = getBuiltInPresetCopy(PRESET_SKILLER);
        } else {
            preset = loadPresetFromConfig();
        }

        if (preset == null) {
            preset = createDefaultPreset();
        }

        ensureUncategorizedTabExists(preset);
        sortTabsByNumber(preset);
        activePreset = preset;
    }

    private BankLayoutPreset loadPresetFromConfig() {
        String jsonConfig = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_LAYOUT);

        if (jsonConfig == null || jsonConfig.isBlank()) {
            return null;
        }

        try {
            Type type = new TypeToken<BankLayoutPreset>() {
            }.getType();

            return gson.fromJson(jsonConfig, type);
        } catch (Exception e) {
            return null;
        }
    }

    private BankLayoutPreset getBuiltInPresetCopy(String presetName) {
        return BankLayoutPreset.getBuiltInPresets()
                .stream()
                .filter(preset -> presetName.equals(preset.getName()))
                .findFirst()
                .map(BankLayoutPreset::copy)
                .orElseGet(this::createDefaultPreset);
    }

    private void ensureUncategorizedTabExists(BankLayoutPreset preset) {
        if (preset == null) {
            return;
        }

        if (preset.getTabs() == null) {
            preset.setTabs(new ArrayList<>());
        }

        boolean uncategorizedExists = preset.getTabs()
                .stream()
                .anyMatch(BankLayoutTab::isUncategorized);

        if (!uncategorizedExists) {
            int nextNumber = preset.getTabs()
                    .stream()
                    .mapToInt(BankLayoutTab::getNumber)
                    .max()
                    .orElse(0) + 1;

            preset.getTabs().add(new BankLayoutTab(nextNumber, UNCATEGORIZED_TAB_NAME, Color.GRAY));
        }
    }

    private void sortTabsByNumber(BankLayoutPreset preset) {
        if (preset != null && preset.getTabs() != null) {
            preset.getTabs().sort(Comparator.comparingInt(BankLayoutTab::getNumber));
        }
    }

    public void saveActivePreset() {
        if (activePreset == null) {
            activePreset = createDefaultPreset();
        }

        ensureUncategorizedTabExists(activePreset);
        sortTabsByNumber(activePreset);

        String json = gson.toJson(activePreset);
        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_LAYOUT, json);
    }

    private BankLayoutPreset createDefaultPreset() {
        List<BankLayoutTab> tabs = new ArrayList<>();
        tabs.add(new BankLayoutTab(1, UNCATEGORIZED_TAB_NAME, Color.GRAY));
        return new BankLayoutPreset("Default", tabs);
    }

    public BankLayoutTab getTabForItem(ItemComposition itemComposition) {
        if (itemComposition == null) {
            return null;
        }

        if (activePreset == null) {
            activePreset = createDefaultPreset();
        }

        ensureUncategorizedTabExists(activePreset);

        BankLayoutTab matchedTab = activePreset.getTabForItem(itemComposition);

        // If keywords/actions matched nothing, route equipable combat gear to a gear tab.
        if (matchedTab == null && classifyGear(itemComposition.getId()) != null) {
            for (BankLayoutTab tab : activePreset.getTabs()) {
                if (tab.isGearTab()) {
                    matchedTab = tab;
                    break;
                }
            }
        }

        if (matchedTab != null) {
            return matchedTab;
        }

        return activePreset.getTabs()
                .stream()
                .filter(BankLayoutTab::isUncategorized)
                .findFirst()
                .orElseGet(() -> new BankLayoutTab(-1, UNCATEGORIZED_TAB_NAME, Color.GRAY));
    }

    public BankLayoutSection getSectionForItem(BankLayoutTab tab, ItemComposition itemComposition) {
        if (tab == null || itemComposition == null) {
            return null;
        }

        if (tab.isGearTab()) {
            CombatStyle style = classifyGear(itemComposition.getId());
            if (style != null) {
                for (BankLayoutSection section : tab.getSections()) {
                    if (style.getSectionName().equalsIgnoreCase(section.getName())) {
                        return section;
                    }
                }
            }
        }

        return tab.getSectionForItem(itemComposition);
    }

    /**
     * Classifies an equipable item into a combat style using its equipment stats.
     * Returns null for non-equipable items, or equipable items with no combat
     * signal at all (e.g. skilling outfits, pure teleport items) so they fall
     * back to keyword matching.
     */
    private CombatStyle classifyGear(int itemId) {
        ItemStats stats = itemManager.getItemStats(itemId);
        if (stats == null || !stats.isEquipable()) {
            return null;
        }

        ItemEquipmentStats eq = stats.getEquipment();
        if (eq == null) {
            return null;
        }

        int meleeAtk = Math.max(eq.getAstab(), Math.max(eq.getAslash(), eq.getAcrush()));
        int rangeAtk = eq.getArange();
        int mageAtk = eq.getAmagic();

        int strBonus = eq.getStr();
        int rangeStr = eq.getRstr();
        int mageDmg = (int) eq.getMdmg();

        // Primary signal: dominant positive attack bonus (catches all weapons).
        int bestAtk = Math.max(meleeAtk, Math.max(rangeAtk, mageAtk));
        if (bestAtk > 0) {
            if (meleeAtk == bestAtk) {
                return CombatStyle.MELEE;
            }
            if (rangeAtk == bestAtk) {
                return CombatStyle.RANGED;
            }
            return CombatStyle.MAGIC;
        }

        // Secondary signal: strength / ranged strength / magic damage
        // (catches armour with no attack bonus, e.g. Bandos, Masori, ancestral).
        if (strBonus > 0 || rangeStr > 0 || mageDmg > 0) {
            if (strBonus > 0 && strBonus >= rangeStr && strBonus >= mageDmg) {
                return CombatStyle.MELEE;
            }
            if (rangeStr > 0 && rangeStr >= mageDmg) {
                return CombatStyle.RANGED;
            }
            if (mageDmg > 0) {
                return CombatStyle.MAGIC;
            }
        }

        // Tertiary signal: defensive bonuses, for tank / prayer gear with no
        // offence (e.g. Proselyte, Justiciar, defensive shields).
        int meleeDef = Math.max(eq.getDstab(), Math.max(eq.getDslash(), eq.getDcrush()));
        int rangeDef = eq.getDrange();
        int mageDef = eq.getDmagic();

        int bestDef = Math.max(meleeDef, Math.max(rangeDef, mageDef));
        if (bestDef > 0) {
            if (meleeDef == bestDef) {
                return CombatStyle.MELEE;
            }
            if (rangeDef == bestDef) {
                return CombatStyle.RANGED;
            }
            return CombatStyle.MAGIC;
        }

        // No combat signal at all — let keyword sections handle it.
        return null;
    }

    public Optional<ItemComposition> getItemCompositionFromWidget(Widget widget) {
        if (widget == null) {
            return Optional.empty();
        }

        int itemId = widget.getItemId();
        if (itemId <= 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(itemManager.getItemComposition(itemId));
    }

    public void scanOpenBankAsync(Consumer<BankScanResult> onSuccess, Consumer<Throwable> onError) {
        clientThread.invokeLater(() -> {
            try {
                BankScanResult result = scanOpenBankOnClientThread();
                SwingUtilities.invokeLater(() -> onSuccess.accept(result));
            } catch (Throwable throwable) {
                SwingUtilities.invokeLater(() -> onError.accept(throwable));
            }
        });
    }

    private BankScanResult scanOpenBankOnClientThread() {
        BankScanResult result = new BankScanResult();

        Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankContainer == null || bankContainer.isHidden()) {
            return result;
        }

        Widget[] children = bankContainer.getDynamicChildren();
        if (children == null || children.length == 0) {
            children = bankContainer.getNestedChildren();
        }

        if (children == null) {
            return result;
        }

        for (Widget itemWidget : children) {
            if (itemWidget == null || itemWidget.isHidden() || itemWidget.getItemId() <= 0) {
                continue;
            }

            Optional<ItemComposition> itemCompositionOptional = getItemCompositionFromWidget(itemWidget);
            if (itemCompositionOptional.isEmpty()) {
                continue;
            }

            ItemComposition itemComposition = itemCompositionOptional.get();
            String itemName = itemComposition.getName();

            result.incrementTotalItems();

            BankLayoutTab tab = getTabForItem(itemComposition);
            if (tab == null || tab.isUncategorized()) {
                result.incrementUncategorizedItems();
                result.incrementTab(UNCATEGORIZED_TAB_NAME, itemName);
                result.addUncategorizedExample(itemName);
                result.addUncategorizedItem(itemName);
                continue;
            }

            result.incrementMatchedItems();
            result.incrementTab(tab.getNumber() + " " + tab.getName(), itemName);

            BankLayoutSection section = getSectionForItem(tab, itemComposition);
            if (section == null) {
                result.incrementSection(tab.getNumber() + " " + tab.getName() + " / No Section", itemName);
                result.addNoSectionExample(itemName);
            } else {
                result.incrementSection(tab.getNumber() + " " + tab.getName() + " / " + section.getName(), itemName);
            }
        }

        return result;
    }

    public void addTab(BankLayoutTab newTab) {
        if (newTab == null) {
            return;
        }

        if (activePreset == null) {
            activePreset = createDefaultPreset();
        }

        if (activePreset.getTabs() == null) {
            activePreset.setTabs(new ArrayList<>());
        }

        int nextNumber = activePreset.getTabs()
                .stream()
                .mapToInt(BankLayoutTab::getNumber)
                .max()
                .orElse(0) + 1;

        newTab.setNumber(nextNumber);
        activePreset.getTabs().add(newTab);
        sortTabsByNumber(activePreset);
        saveActivePreset();
    }

    public void removeTab(BankLayoutTab tabToRemove) {
        if (tabToRemove == null || tabToRemove.isUncategorized()) {
            return;
        }

        if (activePreset == null || activePreset.getTabs() == null) {
            return;
        }

        activePreset.getTabs().remove(tabToRemove);

        int currentNumber = 1;
        for (BankLayoutTab tab : activePreset.getTabs()) {
            if (!tab.isUncategorized()) {
                tab.setNumber(currentNumber++);
            }
        }

        ensureUncategorizedTabExists(activePreset);
        sortTabsByNumber(activePreset);
        saveActivePreset();
    }

    public BankLayoutPreset[] getAllPresets() {
        return BankLayoutPreset.getBuiltInPresets().toArray(new BankLayoutPreset[0]);
    }

    public void setActivePreset(String selected) {
        loadPreset(selected);
        saveActivePreset();
    }

    public String exportActivePresetJson() {
        if (activePreset == null) {
            activePreset = createDefaultPreset();
        }
        return gson.toJson(activePreset);
    }

    public boolean importPresetJson(String json) {
        if (json == null || json.isBlank()) {
            return false;
        }

        try {
            Type type = new TypeToken<BankLayoutPreset>() {
            }.getType();

            BankLayoutPreset imported = gson.fromJson(json, type);
            if (imported == null || imported.getTabs() == null || imported.getTabs().isEmpty()) {
                return false;
            }

            ensureUncategorizedTabExists(imported);
            sortTabsByNumber(imported);
            activePreset = imported;
            saveActivePreset();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}