package com.BankAdvisor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BankScanResult {
    private static final int MAX_EXAMPLES_PER_GROUP = 12;
    private static final int MAX_UNCATEGORIZED_ITEMS = 2000;

    private int totalItems;
    private int matchedItems;
    private int uncategorizedItems;

    private final Map<String, Integer> tabCounts = new TreeMap<>();
    private final Map<String, Integer> sectionCounts = new TreeMap<>();

    private final Map<String, List<String>> tabExamples = new TreeMap<>();
    private final Map<String, List<String>> sectionExamples = new TreeMap<>();
    private final List<String> uncategorizedExamples = new ArrayList<>();
    private final List<String> noSectionExamples = new ArrayList<>();

    // Full list of uncategorized item names (deduped) for the assign feature.
    private final List<String> uncategorizedItemsFull = new ArrayList<>();

    public void incrementTotalItems() {
        totalItems++;
    }

    public void incrementMatchedItems() {
        matchedItems++;
    }

    public void incrementUncategorizedItems() {
        uncategorizedItems++;
    }

    public void incrementTab(String tabName) {
        incrementTab(tabName, null);
    }

    public void incrementTab(String tabName, String itemName) {
        if (tabName == null || tabName.isBlank()) {
            tabName = "Unknown";
        }

        tabCounts.put(tabName, tabCounts.getOrDefault(tabName, 0) + 1);
        addExample(tabExamples, tabName, itemName);
    }

    public void incrementSection(String sectionName) {
        incrementSection(sectionName, null);
    }

    public void incrementSection(String sectionName, String itemName) {
        if (sectionName == null || sectionName.isBlank()) {
            sectionName = "No Section";
        }

        sectionCounts.put(sectionName, sectionCounts.getOrDefault(sectionName, 0) + 1);
        addExample(sectionExamples, sectionName, itemName);
    }

    public void addUncategorizedExample(String itemName) {
        addExample(uncategorizedExamples, itemName);
    }

    public void addNoSectionExample(String itemName) {
        addExample(noSectionExamples, itemName);
    }

    public void addUncategorizedItem(String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return;
        }
        if (uncategorizedItemsFull.contains(itemName)) {
            return;
        }
        if (uncategorizedItemsFull.size() >= MAX_UNCATEGORIZED_ITEMS) {
            return;
        }
        uncategorizedItemsFull.add(itemName);
    }

    public List<String> getUncategorizedItems() {
        return new ArrayList<>(uncategorizedItemsFull);
    }

    private void addExample(Map<String, List<String>> examplesByGroup, String groupName, String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return;
        }

        List<String> examples = examplesByGroup.computeIfAbsent(groupName, key -> new ArrayList<>());
        addExample(examples, itemName);
    }

    private void addExample(List<String> examples, String itemName) {
        if (itemName == null || itemName.isBlank()) {
            return;
        }

        if (examples.contains(itemName)) {
            return;
        }

        if (examples.size() >= MAX_EXAMPLES_PER_GROUP) {
            return;
        }

        examples.add(itemName);
    }

    public String toReportText() {
        StringBuilder report = new StringBuilder();

        report.append("Bank Advisor Scan\n");
        report.append("=================\n\n");

        report.append("Total items scanned: ").append(totalItems).append('\n');
        report.append("Matched items: ").append(matchedItems).append('\n');
        report.append("Uncategorized items: ").append(uncategorizedItems).append("\n\n");

        report.append("Tabs:\n");
        if (tabCounts.isEmpty()) {
            report.append("- None\n");
        } else {
            for (Map.Entry<String, Integer> entry : tabCounts.entrySet()) {
                report.append("- ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append('\n');
            }
        }

        report.append("\nSections:\n");
        if (sectionCounts.isEmpty()) {
            report.append("- None\n");
        } else {
            for (Map.Entry<String, Integer> entry : sectionCounts.entrySet()) {
                report.append("- ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append('\n');
            }
        }

        appendExampleList(report, "\nUncategorized examples:", uncategorizedExamples);
        appendExampleList(report, "\nNo-section examples:", noSectionExamples);

        report.append("\nSection examples:\n");
        if (sectionExamples.isEmpty()) {
            report.append("- None\n");
        } else {
            for (Map.Entry<String, List<String>> entry : sectionExamples.entrySet()) {
                report.append("\n")
                        .append(entry.getKey())
                        .append(":\n");

                for (String itemName : entry.getValue()) {
                    report.append("- ").append(itemName).append('\n');
                }
            }
        }

        return report.toString();
    }

    private void appendExampleList(StringBuilder report, String title, List<String> examples) {
        report.append(title).append('\n');

        if (examples.isEmpty()) {
            report.append("- None\n");
            return;
        }

        for (String itemName : examples) {
            report.append("- ").append(itemName).append('\n');
        }
    }
}