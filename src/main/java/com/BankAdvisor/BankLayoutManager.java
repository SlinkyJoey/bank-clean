package com.BankAdvisor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Singleton
public class BankLayoutManager {

    private static final String CONFIG_GROUP = "bankadvisor";
    private static final String CONFIG_KEY_ACTIVE = "activeLayout";

    private final ConfigManager configManager;

    private final Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Color.class, new ColorAdapter())
        .create();

    private List<BankLayoutPreset> allPresets;
    private BankLayoutPreset activePreset;

    @Inject
    public BankLayoutManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.allPresets = new ArrayList<>(BankLayoutPreset.getBuiltInPresets());
        loadActivePreset();
    }

    public BankLayoutPreset getActivePreset() { return activePreset; }
    public List<BankLayoutPreset> getAllPresets() { return allPresets; }

    public void setActivePreset(String presetName) {
        for (BankLayoutPreset preset : allPresets) {
            if (preset.getName().equals(presetName)) {
                activePreset = preset.copy();
                saveActivePreset();
                return;
            }
        }
        log.warn("Preset not found: {}", presetName);
    }

    public void saveActivePreset() {
        if (activePreset == null) return;
        try {
            String json = gson.toJson(activePreset);
            configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_ACTIVE, json);
        } catch (Exception e) {
            log.error("Failed to save active preset", e);
        }
    }

    private void loadActivePreset() {
        try {
            String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_ACTIVE);
            if (json != null && !json.isEmpty()) {
                activePreset = gson.fromJson(json, BankLayoutPreset.class);
                return;
            }
        } catch (Exception e) {
            log.warn("Could not load saved preset, using default", e);
        }
        activePreset = allPresets.get(0).copy();
    }

    private static class ColorAdapter
        implements com.google.gson.JsonSerializer<Color>,
                   com.google.gson.JsonDeserializer<Color> {

        @Override
        public com.google.gson.JsonElement serialize(Color src,
            java.lang.reflect.Type type,
            com.google.gson.JsonSerializationContext ctx) {
            return new com.google.gson.JsonPrimitive(src.getRGB());
        }

        @Override
        public Color deserialize(com.google.gson.JsonElement json,
            java.lang.reflect.Type type,
            com.google.gson.JsonDeserializationContext ctx) {
            return new Color(json.getAsInt(), true);
        }
    }
}
