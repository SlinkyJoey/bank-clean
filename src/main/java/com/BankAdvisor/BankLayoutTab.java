package com.BankAdvisor;

import net.runelite.api.ItemComposition;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class BankLayoutTab {
    private int number;
    private String name;
    private Color color;
    private List<String> keywords = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private List<BankLayoutSection> sections = new ArrayList<>();
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

    public BankLayoutTab(
            int number,
            String name,
            Color color,
            List<String> keywords,
            List<String> actions,
            List<BankLayoutSection> sections
    ) {
        this.number = number;
        this.name = name;
        this.color = color;
        this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords);
        this.actions = actions == null ? new ArrayList<>() : new ArrayList<>(actions);
        this.sections = sections == null ? new ArrayList<>() : new ArrayList<>(sections);
    }

    public BankLayoutTab(BankLayoutTab other) {
        this.number = other.number;
        this.name = other.name;
        this.color = other.color;
        this.keywords = other.keywords == null ? new ArrayList<>() : new ArrayList<>(other.keywords);
        this.actions = other.actions == null ? new ArrayList<>() : new ArrayList<>(other.actions);
        this.enabled = other.enabled;
        this.set = other.set;

        this.sections = new ArrayList<>();
        if (other.sections != null) {
            for (BankLayoutSection section : other.sections) {
                this.sections.add(new BankLayoutSection(section));
            }
        }
    }

    public boolean matches(ItemComposition item) {
        if (item == null) {
            return false;
        }

        return matches(item.getName());
    }

    public boolean matches(String itemName) {
        if (itemName == null || !enabled) {
            return false;
        }

        for (String keyword : getKeywords()) {
            if (keywordMatches(itemName, keyword)) {
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

    public BankLayoutSection getSectionForItem(ItemComposition itemComposition) {
        if (itemComposition == null) {
            return null;
        }

        String itemName = itemComposition.getName();
        String[] itemActions = itemComposition.getInventoryActions();

        for (BankLayoutSection section : getSections()) {
            if (section.matches(itemName)) {
                return section;
            }
        }

        for (BankLayoutSection section : getSections()) {
            if (section.matchesActions(itemActions)) {
                return section;
            }
        }

        return null;
    }

    private boolean keywordMatches(String itemName, String keyword) {
        if (itemName == null || keyword == null || keyword.isBlank()) {
            return false;
        }

        String normalizedItemName = itemName.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);

        /*
         * If the keyword intentionally contains leading/trailing spaces,
         * preserve old phrase behavior. This is useful for patterns like " rune"
         * so "Air rune" matches but "Rune scimitar" does not.
         */
        if (!keyword.equals(keyword.trim())) {
            return normalizedItemName.contains(normalizedKeyword);
        }

        normalizedKeyword = normalizedKeyword.trim();

        /*
         * Match whole words/phrases only.
         * This stops bad matches like:
         * - "ring" matching "watering"
         * - "bow" matching "bowl"
         */
        Pattern pattern = Pattern.compile(
                "(?i)(^|[^a-z0-9])" + Pattern.quote(normalizedKeyword) + "($|[^a-z0-9])"
        );

        return pattern.matcher(normalizedItemName).find();
    }

    public boolean isUncategorized() {
        return "Uncategorized".equalsIgnoreCase(name);
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
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

    public List<BankLayoutSection> getSections() {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isSet() {
        return set;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setSections(List<BankLayoutSection> sections) {
        this.sections = sections == null ? new ArrayList<>() : sections;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setSet(boolean set) {
        this.set = set;
    }
}