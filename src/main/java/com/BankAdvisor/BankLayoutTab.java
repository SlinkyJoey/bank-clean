package com.BankAdvisor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BankLayoutTab {

    private int number;
    private String name;
    private Color color;
    private List<String> keywords;
    private List<String> actionTriggers; // right-click actions that match this tab e.g. "Eat", "Wear"
    private boolean set;

    // Constructor for built-in tabs with action triggers
    public BankLayoutTab(int number, String name, Color color,
                         List<String> keywords, List<String> actionTriggers) {
        this.number = number;
        this.name = name;
        this.color = color;
        this.keywords = new ArrayList<>(keywords);
        this.actionTriggers = new ArrayList<>(actionTriggers);
        this.set = false;
    }

    // Constructor for user-created tabs (no action triggers needed)
    public BankLayoutTab(int number, String name, Color color, List<String> keywords) {
        this(number, name, color, keywords, new ArrayList<>());
    }

    // Copy constructor
    public BankLayoutTab(BankLayoutTab other) {
        this.number = other.number;
        this.name = other.name;
        this.color = other.color;
        this.keywords = new ArrayList<>(other.keywords);
        this.actionTriggers = new ArrayList<>(other.actionTriggers != null ? other.actionTriggers : new ArrayList<>());
        this.set = other.set;
    }

    // Returns true if the item name contains any keyword for this tab
    public boolean matches(String itemName) {
        String lower = itemName.toLowerCase().trim();
        for (String keyword : keywords) {
            if (lower.contains(keyword.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    // Returns true if any of the item's right-click actions match this tab's triggers
    // e.g. "Eat" matches a Food tab, "Wear" matches a Gear tab
    public boolean matchesActions(String[] actions) {
        if (actions == null || actionTriggers == null || actionTriggers.isEmpty()) return false;
        for (String action : actions) {
            if (action == null) continue;
            for (String trigger : actionTriggers) {
                if (action.equalsIgnoreCase(trigger)) return true;
            }
        }
        return false;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public List<String> getActionTriggers() { return actionTriggers; }

    public boolean isSet() { return set; }
    public void setSet(boolean set) { this.set = set; }
}
