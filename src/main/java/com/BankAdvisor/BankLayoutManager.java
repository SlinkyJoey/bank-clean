package com.BankAdvisor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    private final Gson gson = new GsonBuilder()
        .enableComplexMapKeySerialization()
        .create();

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
        if (matchedTab != null) {
            return matchedTab;
        }

        return activePreset.getTabs()
            .stream()
            .filter(BankLayoutTab::isUncategorized)
            .findFirst()
            .orElseGet(() -> new BankLayoutTab(-1, UNCATEGORIZED_TAB_NAME, Color.GRAY));
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
}
