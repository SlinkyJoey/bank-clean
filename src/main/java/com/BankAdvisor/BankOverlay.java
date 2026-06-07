package com.BankAdvisor;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class BankOverlay extends Overlay {

    private static final int BANK_INTERFACE_ID = 12;
    private static final int BANK_ITEM_CONTAINER_CHILD = 13;

    private final Client client;
    private final ItemManager itemManager;
    private final BankLayoutManager layoutManager;

    @Inject
    public BankOverlay(Client client, ItemManager itemManager,
                       BankLayoutManager layoutManager) {
        this.client = client;
        this.itemManager = itemManager;
        this.layoutManager = layoutManager;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget bankContainer = client.getWidget(BANK_INTERFACE_ID, BANK_ITEM_CONTAINER_CHILD);
        if (bankContainer == null || bankContainer.isHidden()) {
            return null;
        }

        BankLayoutPreset preset = layoutManager.getActivePreset();
        if (preset == null) return null;

        Widget[] items = bankContainer.getDynamicChildren();
        if (items == null) return null;

        ItemContainer bankItems = client.getItemContainer(95);
        if (bankItems == null) return null;

        for (int i = 0; i < items.length; i++) {
            Widget itemWidget = items[i];
            if (itemWidget == null || itemWidget.isHidden()) continue;

            Item item = bankItems.getItem(i);
            if (item == null || item.getId() == -1) continue;

            String itemName = itemManager.getItemComposition(item.getId()).getName();
            if (itemName == null || itemName.equals("null")) continue;

            BankLayoutTab tab = preset.getTabForItem(itemName);
            if (tab == null) continue;

            Rectangle bounds = itemWidget.getBounds();
            graphics.setColor(new Color(
                tab.getColor().getRed(),
                tab.getColor().getGreen(),
                tab.getColor().getBlue(),
                60
            ));
            graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            graphics.setColor(tab.getColor());
            graphics.setFont(new Font("SansSerif", Font.BOLD, 10));
            graphics.drawString(
                String.valueOf(tab.getNumber()),
                bounds.x + 2,
                bounds.y + 10
            );
        }

        return null;
    }
}
