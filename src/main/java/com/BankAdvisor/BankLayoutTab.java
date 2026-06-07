package com.BankAdvisor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.runelite.api.ItemComposition;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
public class BankLayoutTab {
    private int number;
    private String name;
    private Color color;
    private List<String> keywords = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private boolean enabled = true;
    private boolean set = false;

    public BankLayoutTab() {
    }

    public BankLayoutTab(int number, String name, Color color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    public BankLayoutTab(int number, String name, Color color, List<String> keywords) {
        this.number = number;
        this.name = name;
        this.color = color;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
    }

    public BankLayoutTab(int number, String name, Color color, List<String> keywords, List<String> actions) {
        this.number = number;
        this.name = name;
        this.color = color;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
        this.actions = actions == null ? new ArrayList<>() : new ArrayList<>(actions);
    }

    public BankLayoutTab(BankLayoutTab other) {
        this.number = other.number;
        this.name = other.name;
        this.color = other.color;
        this.keywords = other.keywords == null ? new ArrayList<>() : new ArrayList<>(other.keywords);
        this.actions = other.actions == null ? new ArrayList<>() : new ArrayList<>(other.actions);
        this.enabled = other.enabled;
        this.set = other.set;
    }

    public boolean matches(ItemComposition item) {
        if (item == null) {
            return false;
        }

        return matches(item.getName());
    }

    public boolean matches(String itemName) {
        if (itemName == null || keywords == null || !enabled) {
            return false;
        }

        for (String keyword : keywords) {
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
        if (itemActions == null || actions == null || !enabled) {
            return false;
        }

        for (String wantedAction : actions) {
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

    public boolean isUncategorized() {
        return "Uncategorized".equalsIgnoreCase(name);
    }
}
