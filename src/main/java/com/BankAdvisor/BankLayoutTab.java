package com.BankAdvisor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BankLayoutTab {

    private int number;
    private String name;
    private Color color;
    private List<String> keywords;
    private boolean set;

    public BankLayoutTab(int number, String name, Color color, List<String> keywords) {
        this.number = number;
        this.name = name;
        this.color = color;
        this.keywords = new ArrayList<>(keywords);
        this.set = false;
    }

    public BankLayoutTab(BankLayoutTab other) {
        this.number = other.number;
        this.name = other.name;
        this.color = other.color;
        this.keywords = new ArrayList<>(other.keywords);
        this.set = other.set;
    }

    public boolean matches(String itemName) {
        String lower = itemName.toLowerCase().trim();
        for (String keyword : keywords) {
            if (lower.contains(keyword.toLowerCase().trim())) {
                return true;
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

    public boolean isSet() { return set; }
    public void setSet(boolean set) { this.set = set; }
}
