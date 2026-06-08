package com.BankAdvisor;

import net.runelite.client.ui.PluginPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BankAdvisorPanel extends PluginPanel {
    private static final Color TEAL = new Color(0, 212, 184);
    private static final Color BG = new Color(6, 14, 28);
    private static final Color BG_CARD = new Color(12, 22, 40);
    private static final Color BG_INPUT = new Color(20, 35, 60);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color TEXT_DIM = new Color(130, 130, 130);
    private static final Color BORDER = new Color(30, 50, 80);
    private static final Color DELETE_RED = new Color(220, 90, 90);

    private static final Color MELEE_COLOR = new Color(220, 80, 80);
    private static final Color RANGED_COLOR = new Color(80, 180, 100);
    private static final Color MAGIC_COLOR = new Color(100, 149, 237);

    private final BankLayoutManager layoutManager;
    private final Runnable onLayoutChanged;

    private JComboBox<String> templateSelector;
    private JLabel currentLayoutLabel;
    private JPanel tabListPanel;
    private JTextArea scanReportArea;
    private JButton scanButton;

    private List<String> lastUncategorizedItems = new ArrayList<>();

    public BankAdvisorPanel(BankLayoutManager layoutManager, Runnable onLayoutChanged) {
        super(false);
        this.layoutManager = layoutManager;
        this.onLayoutChanged = onLayoutChanged;

        setBackground(BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildTabList());
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);

        JLabel title = new JLabel("Bank Advisor");
        title.setForeground(TEAL);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(2, 0, 6, 0));
        header.add(title);

        currentLayoutLabel = new JLabel();
        currentLayoutLabel.setForeground(TEXT_DIM);
        currentLayoutLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        currentLayoutLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshCurrentLayoutLabel();
        header.add(currentLayoutLabel);
        header.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel templateLabel = new JLabel("Apply template (replaces layout)");
        templateLabel.setForeground(TEXT_DIM);
        templateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        templateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(templateLabel);

        templateSelector = new JComboBox<>();
        templateSelector.setBackground(BG_INPUT);
        templateSelector.setForeground(TEXT);
        templateSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
        templateSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        templateSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (BankLayoutPreset preset : layoutManager.getAllPresets()) {
            templateSelector.addItem(preset.getName());
        }
        header.add(templateSelector);
        header.add(Box.createRigidArea(new Dimension(0, 4)));

        JButton applyButton = styledButton("Apply Template", TEAL);
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        applyButton.addActionListener(e -> applyTemplate());
        header.add(applyButton);
        header.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel exportImportRow = new JPanel();
        exportImportRow.setLayout(new BoxLayout(exportImportRow, BoxLayout.X_AXIS));
        exportImportRow.setBackground(BG);
        exportImportRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportImportRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JButton exportButton = styledButton("Export", TEAL);
        exportButton.addActionListener(e -> exportLayout());
        exportImportRow.add(exportButton);
        exportImportRow.add(Box.createRigidArea(new Dimension(6, 0)));

        JButton importButton = styledButton("Import", TEAL);
        importButton.addActionListener(e -> importLayout());
        exportImportRow.add(importButton);
        exportImportRow.add(Box.createHorizontalGlue());

        header.add(exportImportRow);
        header.add(Box.createRigidArea(new Dimension(0, 10)));

        scanButton = styledButton("Scan Open Bank", TEAL);
        scanButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        scanButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        scanButton.addActionListener(e -> scanOpenBank());
        header.add(scanButton);
        header.add(Box.createRigidArea(new Dimension(0, 8)));

        scanReportArea = new JTextArea();
        scanReportArea.setEditable(false);
        scanReportArea.setLineWrap(true);
        scanReportArea.setWrapStyleWord(true);
        scanReportArea.setBackground(BG_INPUT);
        scanReportArea.setForeground(TEXT);
        scanReportArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        scanReportArea.setBorder(new EmptyBorder(4, 4, 4, 4));
        scanReportArea.setText("Open your bank and click Scan Open Bank.");
        scanReportArea.setRows(8);

        JScrollPane scanScroll = new JScrollPane(scanReportArea);
        scanScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scanScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scanScroll.setPreferredSize(new Dimension(220, 120));
        scanScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        header.add(scanScroll);
        header.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton assignButton = styledButton("Assign Uncategorized", TEAL);
        assignButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        assignButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        assignButton.addActionListener(e -> openAssignUncategorized());
        header.add(assignButton);
        header.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel tabsHeading = new JLabel("Tabs");
        tabsHeading.setForeground(TEAL);
        tabsHeading.setFont(new Font("SansSerif", Font.BOLD, 12));
        tabsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(tabsHeading);
        header.add(Box.createRigidArea(new Dimension(0, 4)));

        JButton addTabButton = styledButton("+ Add Tab", TEAL);
        addTabButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addTabButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        addTabButton.addActionListener(e -> addTab());
        header.add(addTabButton);
        header.add(Box.createRigidArea(new Dimension(0, 8)));

        return header;
    }

    private void refreshCurrentLayoutLabel() {
        BankLayoutPreset active = layoutManager.getActivePreset();
        String name = active != null && active.getName() != null ? active.getName() : "Default";
        currentLayoutLabel.setText("Current layout: " + name);
    }

    private void scanOpenBank() {
        if (scanButton != null) {
            scanButton.setText("Scanning...");
            scanButton.setEnabled(false);
        }

        layoutManager.scanOpenBankAsync(
                result -> {
                    String report = result.toReportText();
                    lastUncategorizedItems = result.getUncategorizedItems();

                    if (scanReportArea != null) {
                        scanReportArea.setText(report);
                        scanReportArea.setCaretPosition(0);
                    }

                    if (scanButton != null) {
                        scanButton.setText("Scan Open Bank");
                        scanButton.setEnabled(true);
                    }

                    showReportDialog("Bank Advisor Scan", report);
                },
                throwable -> {
                    String errorReport = "Bank Advisor Scan failed.\n\n"
                            + throwable.getClass().getSimpleName()
                            + ": "
                            + throwable.getMessage()
                            + "\n\nCheck the console for the full error.";

                    if (scanReportArea != null) {
                        scanReportArea.setText(errorReport);
                        scanReportArea.setCaretPosition(0);
                    }

                    if (scanButton != null) {
                        scanButton.setText("Scan Open Bank");
                        scanButton.setEnabled(true);
                    }

                    throwable.printStackTrace();
                    showReportDialog("Bank Advisor Scan Failed", errorReport);
                }
        );
    }

    private void showReportDialog(String title, String text) {
        JTextArea dialogText = new JTextArea(text);
        dialogText.setEditable(false);
        dialogText.setLineWrap(true);
        dialogText.setWrapStyleWord(true);
        dialogText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        dialogText.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(dialogText);
        scrollPane.setPreferredSize(new Dimension(440, 360));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void applyTemplate() {
        String selected = (String) templateSelector.getSelectedItem();
        if (selected == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Replace your current layout with the \"" + selected + "\" template?\n"
                        + "Your current edits will be lost.",
                "Apply Template",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            layoutManager.setActivePreset(selected);
            refreshCurrentLayoutLabel();
            rebuildTabList();
            onLayoutChanged.run();
        }
    }

    private void exportLayout() {
        String json = layoutManager.exportActivePresetJson();

        JTextArea area = new JTextArea(json, 18, 40);
        area.setLineWrap(true);
        area.setWrapStyleWord(false);
        area.setCaretPosition(0);
        area.selectAll();

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(460, 380));

        JOptionPane.showMessageDialog(
                this,
                sp,
                "Export Layout — copy this JSON",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void importLayout() {
        JTextArea area = new JTextArea(18, 40);
        area.setLineWrap(true);
        area.setWrapStyleWord(false);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(460, 380));

        int res = JOptionPane.showConfirmDialog(
                this,
                sp,
                "Import Layout — paste JSON, replaces current",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        boolean ok = layoutManager.importPresetJson(area.getText());
        if (ok) {
            refreshCurrentLayoutLabel();
            rebuildTabList();
            onLayoutChanged.run();
            JOptionPane.showMessageDialog(this, "Layout imported.", "Import", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not import — the JSON was empty or invalid.",
                    "Import Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openAssignUncategorized() {
        if (lastUncategorizedItems == null || lastUncategorizedItems.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Run \"Scan Open Bank\" first — then I'll list every item that has no tab.",
                    "Assign Uncategorized",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        BankLayoutPreset preset = layoutManager.getActivePreset();
        List<BankLayoutTab> assignable = new ArrayList<>();
        if (preset != null && preset.getTabs() != null) {
            for (BankLayoutTab t : preset.getTabs()) {
                if (!t.isUncategorized()) {
                    assignable.add(t);
                }
            }
        }

        if (assignable.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "You have no tabs to assign items to. Add a tab first.",
                    "Assign Uncategorized",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] options = new String[assignable.size() + 1];
        options[0] = "— skip —";
        for (int i = 0; i < assignable.size(); i++) {
            options[i + 1] = assignable.get(i).getNumber() + " " + assignable.get(i).getName();
        }

        JPanel listHolder = new JPanel();
        listHolder.setLayout(new BoxLayout(listHolder, BoxLayout.Y_AXIS));

        List<String> items = new ArrayList<>(lastUncategorizedItems);
        List<JComboBox<String>> combos = new ArrayList<>();

        for (String itemName : items) {
            JPanel row = new JPanel(new BorderLayout(6, 0));
            row.setBorder(new EmptyBorder(2, 6, 2, 6));

            JLabel label = new JLabel(itemName);
            label.setPreferredSize(new Dimension(180, 22));
            row.add(label, BorderLayout.CENTER);

            JComboBox<String> combo = new JComboBox<>(options);
            combo.setPreferredSize(new Dimension(150, 22));
            row.add(combo, BorderLayout.EAST);

            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            combos.add(combo);
            listHolder.add(row);
        }

        JScrollPane scroll = new JScrollPane(listHolder);
        scroll.setPreferredSize(new Dimension(430, 430));

        int res = JOptionPane.showConfirmDialog(
                this,
                scroll,
                "Assign Uncategorized Items",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        int assigned = 0;
        for (int i = 0; i < items.size(); i++) {
            int sel = combos.get(i).getSelectedIndex();
            if (sel <= 0) {
                continue;
            }
            BankLayoutTab target = assignable.get(sel - 1);
            String keyword = items.get(i);
            if (!target.getKeywords().contains(keyword)) {
                target.getKeywords().add(keyword);
                assigned++;
            }
        }

        if (assigned > 0) {
            layoutManager.saveActivePreset();
            rebuildTabList();
            onLayoutChanged.run();
        }

        JOptionPane.showMessageDialog(
                this,
                "Assigned " + assigned + " item(s) to tabs.",
                "Assign Uncategorized",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void addTab() {
        BankLayoutTab newTab = new BankLayoutTab(
                99, "New Tab", TEAL, new ArrayList<>(), new ArrayList<>()
        );

        if (showTabDialog(newTab, "Add Tab", false)) {
            layoutManager.addTab(newTab);
            rebuildTabList();
            onLayoutChanged.run();
        }
    }

    private void editTab(BankLayoutTab tab) {
        if (showTabDialog(tab, "Edit Tab", true)) {
            layoutManager.saveActivePreset();
            rebuildTabList();
            onLayoutChanged.run();
        }
    }

    private void deleteTab(BankLayoutTab tab) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete tab \"" + tab.getName() + "\"?",
                "Delete Tab",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            layoutManager.removeTab(tab);
            rebuildTabList();
            onLayoutChanged.run();
        }
    }

    private boolean showTabDialog(BankLayoutTab tab, String dialogTitle, boolean showNumber) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JTextField nameField = new JTextField(tab.getName() == null ? "" : tab.getName());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JSpinner numberSpinner = new JSpinner(
                new SpinnerNumberModel(Math.max(1, tab.getNumber()), 1, 99, 1)
        );

        final Color[] chosenColor = { tab.getColor() == null ? Color.GRAY : tab.getColor() };
        JButton colorButton = new JButton("Choose colour");
        colorButton.setBackground(chosenColor[0]);
        colorButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        colorButton.addActionListener(e -> {
            Color picked = JColorChooser.showDialog(this, "Tab colour", chosenColor[0]);
            if (picked != null) {
                chosenColor[0] = picked;
                colorButton.setBackground(picked);
            }
        });

        JCheckBox gearTabBox = new JCheckBox("Gear tab — auto-sort equipment by style", tab.isGearTab());

        JTextArea keywordsArea = new JTextArea(joinLines(tab.getKeywords()), 8, 22);
        keywordsArea.setLineWrap(false);
        JScrollPane keywordsScroll = new JScrollPane(keywordsArea);

        JTextArea actionsArea = new JTextArea(joinLines(tab.getActions()), 3, 22);
        actionsArea.setLineWrap(false);
        JScrollPane actionsScroll = new JScrollPane(actionsArea);

        form.add(new JLabel("Tab name"));
        form.add(nameField);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        if (showNumber) {
            form.add(new JLabel("Tab number"));
            form.add(numberSpinner);
            form.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        form.add(new JLabel("Colour"));
        form.add(colorButton);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(gearTabBox);
        JLabel gearHint = new JLabel("<html><i>When on, equipable items are sorted into"
                + "<br>Melee / Ranged / Magic from their stats."
                + "<br>Non-gear items still use keywords below.</i></html>");
        gearHint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        form.add(gearHint);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Keywords (one per line)"));
        JLabel keywordHint = new JLabel("<html><i>Leading space (e.g. \" rune\") matches anywhere;"
                + "<br>no space matches whole words only.</i></html>");
        keywordHint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        form.add(keywordHint);
        form.add(keywordsScroll);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Equip/use actions (one per line)"));
        JLabel actionHint = new JLabel("<html><i>e.g. Wear, Wield, Eat, Drink, Equip.</i></html>");
        actionHint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        form.add(actionHint);
        form.add(actionsScroll);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                dialogTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String newName = nameField.getText().trim();
        if (!newName.isEmpty()) {
            tab.setName(newName);
        }

        if (showNumber) {
            tab.setNumber((Integer) numberSpinner.getValue());
        }

        tab.setColor(chosenColor[0]);
        tab.setKeywords(splitLines(keywordsArea.getText()));
        tab.setActions(splitLines(actionsArea.getText()));

        tab.setGearTab(gearTabBox.isSelected());
        if (gearTabBox.isSelected()) {
            ensureGearSections(tab);
        }

        return true;
    }

    private void ensureGearSections(BankLayoutTab tab) {
        addSectionIfMissing(tab, "Melee", MELEE_COLOR, 1);
        addSectionIfMissing(tab, "Ranged", RANGED_COLOR, 2);
        addSectionIfMissing(tab, "Magic", MAGIC_COLOR, 3);
        tab.getSections().sort(Comparator.comparingInt(BankLayoutSection::getRow));
    }

    private void addSectionIfMissing(BankLayoutTab tab, String name, Color color, int row) {
        for (BankLayoutSection s : tab.getSections()) {
            if (name.equalsIgnoreCase(s.getName())) {
                return;
            }
        }
        tab.getSections().add(new BankLayoutSection(name, row, color, new ArrayList<>(), new ArrayList<>()));
    }

    private void openSectionManager(BankLayoutTab tab) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner);
        dialog.setTitle("Sections — " + tab.getName());
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel listHolder = new JPanel();
        listHolder.setLayout(new BoxLayout(listHolder, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listHolder);
        scroll.setPreferredSize(new Dimension(380, 340));
        dialog.add(scroll, BorderLayout.CENTER);

        JButton addBtn = new JButton("+ Add Section");
        addBtn.addActionListener(e -> {
            BankLayoutSection s = new BankLayoutSection(
                    "New Section", nextSectionRow(tab), TEAL, new ArrayList<>(), new ArrayList<>()
            );
            if (showSectionDialog(s, "Add Section")) {
                tab.getSections().add(s);
                tab.getSections().sort(Comparator.comparingInt(BankLayoutSection::getRow));
                layoutManager.saveActivePreset();
                rebuildSectionList(listHolder, tab);
                rebuildTabList();
            }
        });
        dialog.add(addBtn, BorderLayout.SOUTH);

        rebuildSectionList(listHolder, tab);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void rebuildSectionList(JPanel listHolder, BankLayoutTab tab) {
        listHolder.removeAll();

        List<BankLayoutSection> sections = tab.getSections();
        if (sections.isEmpty()) {
            JLabel none = new JLabel("No sections yet.");
            none.setBorder(new EmptyBorder(8, 8, 8, 8));
            listHolder.add(none);
        } else {
            for (BankLayoutSection section : sections) {
                JPanel row = new JPanel(new BorderLayout(6, 0));
                row.setBorder(new EmptyBorder(4, 6, 4, 6));

                JLabel label = new JLabel(
                        section.getRow() + ". " + section.getName()
                                + "  (" + section.getKeywords().size() + " kw)"
                );
                row.add(label, BorderLayout.CENTER);

                JPanel btns = new JPanel();
                btns.setLayout(new BoxLayout(btns, BoxLayout.X_AXIS));

                JButton edit = new JButton("Edit");
                edit.addActionListener(e -> {
                    if (showSectionDialog(section, "Edit Section")) {
                        tab.getSections().sort(Comparator.comparingInt(BankLayoutSection::getRow));
                        layoutManager.saveActivePreset();
                        rebuildSectionList(listHolder, tab);
                        rebuildTabList();
                    }
                });

                JButton del = new JButton("Delete");
                del.addActionListener(e -> {
                    int c = JOptionPane.showConfirmDialog(
                            listHolder,
                            "Delete section \"" + section.getName() + "\"?",
                            "Delete Section",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (c == JOptionPane.YES_OPTION) {
                        tab.getSections().remove(section);
                        layoutManager.saveActivePreset();
                        rebuildSectionList(listHolder, tab);
                        rebuildTabList();
                    }
                });

                btns.add(edit);
                btns.add(Box.createRigidArea(new Dimension(4, 0)));
                btns.add(del);

                row.add(btns, BorderLayout.EAST);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                listHolder.add(row);
            }
        }

        listHolder.revalidate();
        listHolder.repaint();
    }

    private boolean showSectionDialog(BankLayoutSection section, String dialogTitle) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JTextField nameField = new JTextField(section.getName() == null ? "" : section.getName());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JSpinner rowSpinner = new JSpinner(
                new SpinnerNumberModel(Math.max(1, section.getRow()), 1, 99, 1)
        );

        final Color[] chosenColor = { section.getColor() == null ? TEAL : section.getColor() };
        JButton colorButton = new JButton("Choose colour");
        colorButton.setBackground(chosenColor[0]);
        colorButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        colorButton.addActionListener(e -> {
            Color picked = JColorChooser.showDialog(this, "Section colour", chosenColor[0]);
            if (picked != null) {
                chosenColor[0] = picked;
                colorButton.setBackground(picked);
            }
        });

        JTextArea keywordsArea = new JTextArea(joinLines(section.getKeywords()), 8, 22);
        keywordsArea.setLineWrap(false);
        JScrollPane keywordsScroll = new JScrollPane(keywordsArea);

        JTextArea actionsArea = new JTextArea(joinLines(section.getActions()), 3, 22);
        actionsArea.setLineWrap(false);
        JScrollPane actionsScroll = new JScrollPane(actionsArea);

        form.add(new JLabel("Section name"));
        form.add(nameField);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Sort order (row)"));
        form.add(rowSpinner);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Colour"));
        form.add(colorButton);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Keywords (one per line)"));
        JLabel keywordHint = new JLabel("<html><i>Leading space matches anywhere;"
                + "<br>no space matches whole words only.</i></html>");
        keywordHint.setFont(new Font("SansSerif", Font.PLAIN, 10));
        form.add(keywordHint);
        form.add(keywordsScroll);
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        form.add(new JLabel("Equip/use actions (one per line)"));
        form.add(actionsScroll);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                dialogTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String newName = nameField.getText().trim();
        if (!newName.isEmpty()) {
            section.setName(newName);
        }

        section.setRow((Integer) rowSpinner.getValue());
        section.setColor(chosenColor[0]);
        section.setKeywords(splitLines(keywordsArea.getText()));
        section.setActions(splitLines(actionsArea.getText()));

        return true;
    }

    private int nextSectionRow(BankLayoutTab tab) {
        int max = 0;
        for (BankLayoutSection s : tab.getSections()) {
            max = Math.max(max, s.getRow());
        }
        return max + 1;
    }

    private String joinLines(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join("\n", values);
    }

    private List<String> splitLines(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) {
            return out;
        }

        for (String raw : text.split("\r?\n", -1)) {
            if (raw.trim().isEmpty()) {
                continue;
            }
            out.add(raw);
        }
        return out;
    }

    private JPanel buildTabList() {
        tabListPanel = new JPanel();
        tabListPanel.setLayout(new BoxLayout(tabListPanel, BoxLayout.Y_AXIS));
        tabListPanel.setBackground(BG);
        populateTabList();
        return tabListPanel;
    }

    private void populateTabList() {
        tabListPanel.removeAll();

        BankLayoutPreset preset = layoutManager.getActivePreset();
        if (preset != null && preset.getTabs() != null) {
            for (BankLayoutTab tab : preset.getTabs()) {
                tabListPanel.add(buildTabCard(tab));
                tabListPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }

        tabListPanel.revalidate();
        tabListPanel.repaint();
    }

    private JPanel buildTabCard(BankLayoutTab tab) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 3, 0, 0, safeColor(tab.getColor())),
                new EmptyBorder(6, 8, 6, 8)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, tab.isUncategorized() ? 64 : 140));

        JLabel title = new JLabel(tab.getNumber() + "  " + tab.getName());
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        String countText = "Keywords: " + tab.getKeywords().size() + " | Sections: " + tab.getSections().size();
        if (tab.isGearTab()) {
            countText += "  • auto gear";
        }
        JLabel keywordCount = new JLabel(countText);
        keywordCount.setForeground(TEXT_DIM);
        keywordCount.setFont(new Font("SansSerif", Font.PLAIN, 10));
        keywordCount.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(keywordCount);

        if (tab.isUncategorized()) {
            JLabel note = new JLabel("Auto-managed fallback");
            note.setForeground(TEXT_DIM);
            note.setFont(new Font("SansSerif", Font.ITALIC, 10));
            note.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(Box.createRigidArea(new Dimension(0, 2)));
            card.add(note);
            return card;
        }

        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setBackground(BG_CARD);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setBorder(new EmptyBorder(6, 0, 0, 0));

        JButton editButton = styledButton("Edit", TEAL);
        editButton.addActionListener(e -> editTab(tab));
        buttonRow.add(editButton);

        buttonRow.add(Box.createRigidArea(new Dimension(6, 0)));

        JButton deleteButton = styledButton("Delete", DELETE_RED);
        deleteButton.addActionListener(e -> deleteTab(tab));
        buttonRow.add(deleteButton);

        buttonRow.add(Box.createHorizontalGlue());
        card.add(buttonRow);

        JButton sectionsButton = styledButton("Sections (" + tab.getSections().size() + ")", TEAL);
        sectionsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        sectionsButton.addActionListener(e -> openSectionManager(tab));
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(sectionsButton);

        return card;
    }

    private Color safeColor(Color color) {
        return color == null ? Color.GRAY : color;
    }

    public void rebuildTabList() {
        SwingUtilities.invokeLater(() -> {
            populateTabList();
            refreshCurrentLayoutLabel();
        });
    }

    private JButton styledButton(String text, Color fg) {
        JButton btn = new JButton(text);
        btn.setForeground(fg);
        btn.setBackground(BG_INPUT);
        btn.setBorder(new EmptyBorder(4, 8, 4, 8));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        return btn;
    }
}