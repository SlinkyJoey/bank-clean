// src/main/java/com/BankAdvisor/BankLayoutTab.java
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

    // Default constructor for Gson
    public BankLayoutTab() {
    }

    public BankLayoutTab(int number, String name, Color color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    public boolean matches(ItemComposition item) {
        // Check keywords first
        for (String keyword : keywords) {
            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(item.getName()).find()) {
                return true;
            }
        }
        // Fallback to actions
        for (String action : actions) {
            // Check if the item has this action.
            // Note: This is a simplified check. RuneLite's ItemComposition doesn't directly expose actions easily.
            // A more robust solution might involve checking item IDs against known action-performing items.
            // For now, we'll assume actions are listed and items that *can* perform them will match.
            // A better approach for actions might be to look at the widget item's available actions.
            // For this example, we'll keep it simple and rely on keywords primarily.
            // If you have a way to get actions for an ItemComposition, implement it here.
        }
        return false;
    }

    // New method to check if this tab is the "Uncategorized" tab
    public boolean isUncategorized() {
        return "Uncategorized".equals(this.name);
    }
}
