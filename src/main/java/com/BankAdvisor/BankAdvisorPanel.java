package com.BankAdvisor;

import net.runelite.client.ui.PluginPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class BankAdvisorPanel extends PluginPanel {
    private static final Color TEAL = new Color(0, 212, 184);
    private static final Color BG = new Color(6, 14, 28);
    private static final Color BG_CARD = new Color(12, 22, 40);
    private static final Color BG_INPUT = new Color(20, 35, 60);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color TEXT_DIM = new Color(130, 130, 130);
    private static final Color BORDER = new Color(30, 50, 80);

    private final BankLayoutManager layoutManager;
    private final Runnable onLayoutChanged;

    private JComboBox<String> presetSelector;
    private JPanel tabListPanel;

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
        title.setBorder(new EmptyBorder(2, 0, 8, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(title);

        JLabel presetLabel = new JLabel("Layout preset");
        presetLabel.setForeground(TEXT_DIM);
        presetLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        presetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(presetLabel);

        presetSelector = new JComboBox<>();
        presetSelector.setBackground(BG_INPUT);
        presetSelector.setForeground(TEXT);
        presetSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
        presetSelector.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        presetSelector.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (BankLayoutPreset preset : layoutManager.getAllPresets()) {
            presetSelector.addItem(preset.getName());
        }

        if (layoutManager.getActivePreset() != null) {
            presetSelector.setSelectedItem(layoutManager.getActivePreset().getName());
        }

        presetSelector.addActionListener(e -> switchPreset());

        header.add(presetSelector);
        header.add(Box.createRigidArea(new Dimension(0, 10)));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(sep);
        header.add(Box.createRigidArea(new Dimension(0, 8)));

        return header;
    }

    private void switchPreset() {
        String selected = (String) presetSelector.getSelectedItem();
        if (selected == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Switch to \"" + selected + "\"?\nYour current edits will be replaced.",
            "Switch Preset",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            layoutManager.setActivePreset(selected);
            rebuildTabList();
            onLayoutChanged.run();
        }
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

        JButton addTabBtn = styledButton("+ Add Tab", TEAL);
        addTabBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addTabBtn.addActionListener(e -> addNewTab());
        tabListPanel.add(addTabBtn);

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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(BG_CARD);
        cardHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel numBadge = new JLabel(" " + tab.getNumber() + " ");
        numBadge.setForeground(BG);
        numBadge.setBackground(safeColor(tab.getColor()));
        numBadge.setOpaque(true);
        numBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        numBadge.setBorder(new EmptyBorder(1, 4, 1, 4));

        JLabel nameLabel = new JLabel(tab.getName());
        nameLabel.setForeground(TEXT);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setBorder(new EmptyBorder(0, 6, 0, 0));

        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftSide.setBackground(BG_CARD);
        leftSide.add(numBadge);
        leftSide.add(nameLabel);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightSide.setBackground(BG_CARD);

        JToggleButton setBtn = new JToggleButton("Done");
        setBtn.setSelected(tab.isSet());
        setBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        setBtn.setForeground(tab.isSet() ? TEAL : TEXT_DIM);
        setBtn.setBackground(BG_INPUT);
        setBtn.setBorder(new EmptyBorder(2, 6, 2, 6));
        setBtn.setFocusPainted(false);
        setBtn.addActionListener(e -> {
            tab.setSet(setBtn.isSelected());
            setBtn.setForeground(tab.isSet() ? TEAL : TEXT_DIM);
            layoutManager.saveActivePreset();
            onLayoutChanged.run();
        });

        JButton colorBtn = new JButton();
        colorBtn.setBackground(safeColor(tab.getColor()));
        colorBtn.setPreferredSize(new Dimension(16, 16));
        colorBtn.setBorder(BorderFactory.createLineBorder(BORDER));
        colorBtn.setFocusPainted(false);
        colorBtn.setToolTipText("Change colour");
        colorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose tab colour", safeColor(tab.getColor()));
            if (chosen != null) {
                tab.setColor(chosen);
                rebuildTabList();
                layoutManager.saveActivePreset();
                onLayoutChanged.run();
            }
        });

        JButton deleteBtn = styledButton("\u2715", new Color(180, 60, 60));
        deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 10));
        deleteBtn.setPreferredSize(new Dimension(22, 22));
        deleteBtn.setToolTipText("Delete tab");
        deleteBtn.setEnabled(!tab.isUncategorized());
        deleteBtn.addActionListener(e -> {
            layoutManager.removeTab(tab);
            rebuildTabList();
            onLayoutChanged.run();
        });

        rightSide.add(setBtn);
        rightSide.add(colorBtn);
        rightSide.add(deleteBtn);

        cardHeader.add(leftSide, BorderLayout.WEST);
        cardHeader.add(rightSide, BorderLayout.EAST);
        card.add(cardHeader);

        JTextField nameField = new JTextField(tab.getName());
        styleTextField(nameField);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 11));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(dimLabel("Tab name:"));
        card.add(nameField);

        nameField.addActionListener(e -> {
            tab.setName(nameField.getText().trim());
            nameLabel.setText(tab.getName());
            layoutManager.saveActivePreset();
            onLayoutChanged.run();
        });

        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(dimLabel("Keywords:"));

        JPanel keywordsPanel = new JPanel();
        keywordsPanel.setLayout(new BoxLayout(keywordsPanel, BoxLayout.Y_AXIS));
        keywordsPanel.setBackground(BG_CARD);
        keywordsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String keyword : new ArrayList<>(tab.getKeywords())) {
            keywordsPanel.add(buildKeywordRow(keyword, tab, keywordsPanel));
        }

        card.add(keywordsPanel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JPanel addRow = new JPanel(new BorderLayout(4, 0));
        addRow.setBackground(BG_CARD);
        addRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        addRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField addField = new JTextField();
        styleTextField(addField);

        JButton addKwBtn = styledButton("+", TEAL);
        addKwBtn.setPreferredSize(new Dimension(26, 26));

        Runnable addKeyword = () -> {
            String keyword = addField.getText().trim().toLowerCase();
            if (!keyword.isEmpty() && !tab.getKeywords().contains(keyword)) {
                tab.getKeywords().add(keyword);
                keywordsPanel.add(buildKeywordRow(keyword, tab, keywordsPanel));
                keywordsPanel.revalidate();
                keywordsPanel.repaint();
                addField.setText("");
                layoutManager.saveActivePreset();
                onLayoutChanged.run();
            }
        };

        addField.addActionListener(e -> addKeyword.run());
        addKwBtn.addActionListener(e -> addKeyword.run());

        addRow.add(addField, BorderLayout.CENTER);
        addRow.add(addKwBtn, BorderLayout.EAST);
        card.add(addRow);

        return card;
    }

    private JPanel buildKeywordRow(String keyword, BankLayoutTab tab, JPanel keywordsPanel) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel kwLabel = new JLabel(keyword);
        kwLabel.setForeground(TEXT_DIM);
        kwLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton removeBtn = new JButton("\u00d7");
        removeBtn.setForeground(new Color(180, 60, 60));
        removeBtn.setBackground(BG_CARD);
        removeBtn.setBorder(null);
        removeBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        removeBtn.setPreferredSize(new Dimension(18, 18));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            tab.getKeywords().remove(keyword);
            keywordsPanel.remove(row);
            keywordsPanel.revalidate();
            keywordsPanel.repaint();
            layoutManager.saveActivePreset();
            onLayoutChanged.run();
        });

        row.add(kwLabel, BorderLayout.CENTER);
        row.add(removeBtn, BorderLayout.EAST);

        return row;
    }

    private void addNewTab() {
        BankLayoutPreset preset = layoutManager.getActivePreset();
        if (preset == null) {
            return;
        }

        BankLayoutTab newTab = new BankLayoutTab(0, "New Tab", new Color(150, 150, 150), new ArrayList<>());
        layoutManager.addTab(newTab);
        rebuildTabList();
        onLayoutChanged.run();
    }

    private Color safeColor(Color color) {
        return color == null ? Color.GRAY : color;
    }

    public void rebuildTabList() {
        SwingUtilities.invokeLater(this::populateTabList);
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

    private void styleTextField(JTextField field) {
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(2, 4, 2, 4)
        ));
        field.setFont(new Font("SansSerif", Font.PLAIN, 11));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JLabel dimLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}
