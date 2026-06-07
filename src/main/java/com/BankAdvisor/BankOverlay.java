package com.BankAdvisor;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class BankOverlay extends Overlay {

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
        // Use the current RuneLite API single-int widget lookup
        Widget bankContainer = client.getWidget(InterfaceID.Bankmain.ITEMS);
        if (bankContainer == null || bankContainer.isHidden()) {
            return null;
        }

        BankLayoutPreset preset = layoutManager.getActivePreset();
        if (preset == null) return null;

        Widget[] items = bankContainer.getDynamicChildren();
        if (items == null) return null;

        for (Widget itemWidget : items) {
            if (itemWidget == null || itemWidget.isHidden()) continue;

            // Get item ID directly from the widget
            int itemId = itemWidget.getItemId();
            if (itemId == -1) continue;

            // Get full ItemComposition — needed for both name and inventory actions
            ItemComposition comp = itemManager.getItemComposition(itemId);
            if (comp == null) continue;

            String itemName = comp.getName();
            if (itemName == null || itemName.equals("null")) continue;

            // Match using keywords first, then action-based fallback
            BankLayoutTab tab = preset.getTabForItem(comp);
            if (tab == null) continue;

            Rectangle bounds = itemWidget.getBounds();
            if (bounds == null) continue;

            // Draw coloured highlight
            graphics.setColor(new Color(
                tab.getColor().getRed(),
                tab.getColor().getGreen(),
                tab.getColor().getBlue(),
                60
            ));
            graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            // Draw tab number in top-left corner
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
