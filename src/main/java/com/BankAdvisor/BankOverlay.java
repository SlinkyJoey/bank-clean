package com.BankAdvisor;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class BankOverlay extends Overlay {
    private final Client client;
    private final BankLayoutManager bankLayoutManager;

    @Inject
    public BankOverlay(Client client, BankLayoutManager bankLayoutManager) {
        this.client = client;
        this.bankLayoutManager = bankLayoutManager;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Widget bankContainer = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankContainer == null || bankContainer.isHidden()) {
            return null;
        }

        Widget[] children = bankContainer.getDynamicChildren();
        if (children == null || children.length == 0) {
            children = bankContainer.getNestedChildren();
        }

        if (children == null) {
            return null;
        }

        for (Widget itemWidget : children) {
            if (itemWidget == null || itemWidget.isHidden()) {
                continue;
            }

            int itemId = itemWidget.getItemId();
            if (itemId <= 0) {
                continue;
            }

            ItemComposition itemComposition = bankLayoutManager.getItemCompositionFromWidget(itemWidget).orElse(null);
            if (itemComposition == null) {
                continue;
            }

            BankLayoutTab tab = bankLayoutManager.getTabForItem(itemComposition);
            if (tab == null) {
                continue;
            }

            Color tabColor = tab.getColor();
            if (tabColor == null) {
                tabColor = Color.GRAY;
            }

            renderHighlight(graphics, itemWidget, tabColor);
            renderTabNumber(graphics, itemWidget, tab.getNumber());
        }

        return null;
    }

    private void renderHighlight(Graphics2D graphics, Widget itemWidget, Color color) {
        Point location = itemWidget.getCanvasLocation();
        if (location == null) {
            return;
        }

        int width = itemWidget.getWidth();
        int height = itemWidget.getHeight();

        if (width <= 0 || height <= 0) {
            width = 36;
            height = 32;
        }

        Rectangle bounds = new Rectangle(
                location.getX(),
                location.getY(),
                width,
                height
        );

        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 55);
        Color borderColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 230);

        graphics.setColor(fillColor);
        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(2.5f));
        graphics.setColor(borderColor);
        graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        graphics.setStroke(originalStroke);
    }

    private void renderTabNumber(Graphics2D graphics, Widget itemWidget, int tabNumber) {
        Point location = itemWidget.getCanvasLocation();
        if (location == null) {
            return;
        }

        String text = String.valueOf(tabNumber);

        graphics.setFont(new Font("Arial", Font.BOLD, 12));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics metrics = graphics.getFontMetrics();
        int padding = 3;
        int bgX = location.getX() + 1;
        int bgY = location.getY() + 1;
        int bgW = metrics.stringWidth(text) + padding * 2;
        int bgH = metrics.getHeight();

        graphics.setColor(new Color(0, 0, 0, 190));
        graphics.fillRoundRect(bgX, bgY, bgW, bgH, 6, 6);

        graphics.setColor(Color.WHITE);
        graphics.drawString(text, bgX + padding, bgY + metrics.getAscent());
    }
}