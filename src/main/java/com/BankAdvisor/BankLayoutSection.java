package com.BankAdvisor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BankLayoutSection {
    private String name;
    private int row;
    private Color color;
    private List<String> keywords = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private boolean enabled = true;

    public BankLayoutSection() {
    }

    public BankLayoutSection(String name, int row, Color color) {
        this.name = name;
        this.row = row;
        this.color = color;
    }

    public BankLayoutSection(String name, int row, Color color, List<String> keywords) {
        this.name = name;
        this.row = row;
        this.color = color;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
    }

    public BankLayoutSection(String name, int row, Color color, List<String> keywords, List<String> actions) {
        this.name = name;
        this.row = row;
        this.color = color;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
        this.actions = actions == null ? new ArrayList<>() : new ArrayList<>(actions);
    }

    public BankLayoutSection(BankLayoutSection other) {
        this.name = other.name;
        this.row = other.row;
        this.color = other.color;
        this.keywords = other.keywords == null ? new ArrayList<>() : new ArrayList<>(other.keywords);
        this.actions = other.actions == null ? new ArrayList<>() : new ArrayList<>(other.actions);
        this.enabled = other.enabled;
    }

    public boolean matches(String itemName) {
        if (itemName == null || !enabled) {
            return false;
        }

        for (String keyword : getKeywords()) {
            if (keyword == null || keyword.isBlank()) {
                continue;
            }

            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE)
                .matcher(itemName)
                .find()) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesActions(String[] itemActions) {
        if (itemActions == null || !enabled) {
            return false;
        }

        for (String wantedAction : getActions()) {
            if (wantedAction == null || wantedAction.isBlank()) {
                continue;
            }

            for (String itemAction : itemActions) {
                if (itemAction != null && itemAction.equalsIgnoreCase(wantedAction)) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public Color getColor() {
        return color;
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    public List<String> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords == null ? new ArrayList<>() : keywords;
    }

    public void setActions(List<String> actions) {
        this.actions = actions == null ? new ArrayList<>() : actions;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
