package com.BankAdvisor;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
    name = "Bank Advisor",
    description = "Overlay and panel for organising your bank by tab",
    tags = {"bank", "sort", "organise", "tabs", "layout"}
)
public class BankAdvisorPlugin extends Plugin {

    @Inject private ClientToolbar clientToolbar;
    @Inject private OverlayManager overlayManager;
    @Inject private BankLayoutManager layoutManager;
    @Inject private BankOverlay bankOverlay;

    private BankAdvisorPanel panel;
    private NavigationButton navButton;

    @Override
    protected void startUp() {
        panel = new BankAdvisorPanel(layoutManager, () -> {});

        BufferedImage icon = new BufferedImage(25, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0, 212, 184));
        g.fillRoundRect(1, 1, 23, 23, 6, 6);
        g.setColor(new Color(6, 14, 28));
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("B", 7, 18);
        g.dispose();

        navButton = NavigationButton.builder()
            .tooltip("Bank Advisor")
            .icon(icon)
            .priority(5)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);
        overlayManager.add(bankOverlay);
        log.info("Bank Advisor started");
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
        overlayManager.remove(bankOverlay);
        panel = null;
        log.info("Bank Advisor stopped");
    }

    @Provides
    BankAdvisorConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BankAdvisorConfig.class);
    }
}
