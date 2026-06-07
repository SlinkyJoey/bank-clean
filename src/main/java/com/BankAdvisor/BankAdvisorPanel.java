package com.BankAdvisor;

import net.runelite.client.ui.PluginPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

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
    private JTextArea scanReportArea;
    private JButton scanButton;

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
        title.setBorder(new EmptyBorder(2, 0, 8, 0));
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
        header.add(Box.createRigidArea(new Dimension(0, 8)));

        return header;
    }

    private void scanOpenBank() {
        if (scanButton != null) {
            scanButton.setText("Scanning...");
            scanButton.setEnabled(false);
        }

        layoutManager.scanOpenBankAsync(
                result -> {
                    String report = result.toReportText();

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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel title = new JLabel(tab.getNumber() + "  " + tab.getName());
        title.setForeground(TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);

        JLabel keywordCount = new JLabel("Keywords: " + tab.getKeywords().size() + " | Sections: " + tab.getSections().size());
        keywordCount.setForeground(TEXT_DIM);
        keywordCount.setFont(new Font("SansSerif", Font.PLAIN, 10));
        keywordCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(keywordCount);

        return card;
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
}