// src/main/java/com/BankAdvisor/BankOverlay.java
package com.BankAdvisor;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.widgets.InterfaceID.BANK_INVENTORY_ITEM_SPRITE_ID; // Assuming this is correct

public class BankOverlay extends Overlay {
    private final Client client;
    private final BankLayoutManager bankLayoutManager;
    private final BankAdvisorPlugin plugin; // Needed to access config/settings if any

    @Inject
    public BankOverlay(Client client, BankLayoutManager bankLayoutManager, BankAdvisorPlugin plugin) {
        this.client = client;
        this.bankLayoutManager = bankLayoutManager;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        // Add a menu entry to open the panel, if you don't already have one
        // setMovable(true); // Allow panel movement if desired
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Check if the bank interface is open
        Widget bankWidget = client.getWidget(InterfaceID.BANK);
        if (bankWidget == null || bankWidget.isHidden()) {
            return null;
        }

        // Get the container holding the bank items
        Widget bankContainer = client.getWidget(InterfaceID.BANK, WidgetInfo.BANK_INVENTORY_ITEM_CONTAINER);
        if (bankContainer == null || bankContainer.isHidden()) {
            return null;
        }

        // Iterate over all items in the bank container
        for (Widget itemWidget : bankContainer.getNestedChildren()) {
            if (itemWidget.getType() == Widget.WidgetType.GRAPHIC && itemWidget.getModelId() == 0 && itemWidget.getItemId() == -1) {
                // Skip empty slots or placeholder widgets
                continue;
            }

            // Get ItemComposition for the item
            // Use the BankLayoutManager's helper method to safely get ItemComposition
            // This assumes getItemCompositionFromWidget is correctly implemented in BankLayoutManager
            ItemComposition itemComposition = bankLayoutManager.getItemCompositionFromWidget(itemWidget).orElse(null);

            if (itemComposition != null) {
                BankLayoutTab tab = bankLayoutManager.getTabForItem(itemComposition);

                if (tab != null) {
                    Color tabColor = tab.getColor();
                    // If it's the uncategorized tab and has no specific color, use a default like gray
                    if (tab.isUncategorized() && tab.getColor() == null) {
                        tabColor = Color.GRAY;
                    } else if (tab.isUncategorized() && tab.getColor() != null) {
                         // User may have set a color for Uncategorized
                         tabColor = tab.getColor();
                    }

                    // Draw highlight around the item
                    renderHighlight(graphics, itemWidget, tabColor);

                    // Draw tab number
                    renderTabNumber(graphics, itemWidget, tab.getNumber());
                }
            }
        }

        return null; // No specific overlay dimension needed for this type of rendering
    }

    private void renderHighlight(Graphics2D graphics, Widget itemWidget, Color color) {
        if (color == null) {
            color = Color.WHITE; // Default to white if no color is set
        }
        // Create a rectangle matching the item's bounds
        Rectangle bounds = new Rectangle(itemWidget.getCanvasLocation()[0], itemWidget.getCanvasLocation()[1], itemWidget.getWidth(), itemWidget.getHeight());

        // Draw the highlight. Adjust stroke for visibility.
        graphics.setColor(color);
        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2)); // Make the border thicker
        graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics.setStroke(originalStroke); // Restore original stroke
    }

    private void renderTabNumber(Graphics2D graphics, Widget itemWidget, int tabNumber) {
        // Position the number, e.g., in the top-left corner of the item widget
        Point location = itemWidget.getCanvasLocation();
        if (location == null) {
            return;
        }

        // Use a font that's readable
        Font font = new Font("Arial", Font.BOLD, 12);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE); // Or a color that contrasts well with your highlights
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = String.valueOf(tabNumber);
        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();

        // Draw a small background for the number to ensure readability
        int padding = 2;
        int bgX = location[0] + padding;
        int bgY = location[1] + padding;
        graphics.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black background
        graphics.fillRect(bgX, bgY, textWidth + padding * 2, textHeight);

        // Draw the text
        graphics.setColor(Color.WHITE); // Text color
        graphics.drawString(text, bgX + padding, bgY + metrics.getAscent());
    }
}
