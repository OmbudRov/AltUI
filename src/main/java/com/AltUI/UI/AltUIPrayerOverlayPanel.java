package com.AltUI.UI;

import com.AltUI.AltUIPlugin;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class AltUIPrayerOverlay extends Overlay {

    public int Currentprayer;
    public int Prayer;
    public int threshold;

    public Color backgroundColor;
    public Color foregroundColor;
    public Color flashingColor;
    public Color foregroundLowColor;
    public Color foregroundOffColor;

    public boolean displayPrayer;
    public boolean displayOutline;

    private int counter = 0;

    public int gameWidth = 100;

    public int barHeight = 25;

    public int flashInterval = 10;

    private final AltUIPlugin plugin;

    @Inject
    public AltUIPrayerOverlay(AltUIPlugin plugin, int Currentprayer, int Prayer, int threshold, Color backgroundColor, Color foregroundColor, Color foregroundLowColor, Color foregroundOffColor, Color flashingColor) {
        super(plugin);
        this.Currentprayer = Currentprayer;
        this.Prayer = Prayer;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.flashingColor = flashingColor;
        this.foregroundLowColor = foregroundLowColor;
        this.foregroundOffColor = foregroundOffColor;
        this.threshold = threshold;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setDragTargetable(false);
        setBounds(new Rectangle(0,0,gameWidth,barHeight));
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        int rightSideDisplayWidth = displayPrayer ? 50 : 0;
        counter++;

        graphics.setColor(backgroundColor);
        if(Currentprayer <= threshold && counter % flashInterval <= flashInterval/2) {
            graphics.setColor(flashingColor);
        }
        graphics.fillRect(0,0,gameWidth,barHeight);

        graphics.setColor(foregroundColor);

        if(plugin.isPrayerOff()) {
            graphics.setColor(foregroundOffColor);
        } else if(Currentprayer <= threshold) {
            graphics.setColor(foregroundLowColor);
        }
        graphics.fillRect(0,0,(gameWidth - rightSideDisplayWidth) * Currentprayer / Prayer ,barHeight);

        if(displayPrayer) {
            graphics.setColor(foregroundColor);
            graphics.fillRect(gameWidth - rightSideDisplayWidth + 1, 0, rightSideDisplayWidth - 1, barHeight);

            String letsWrite = Currentprayer + "/" + Prayer;
            FontMetrics metrics = graphics.getFontMetrics();
            graphics.setColor(new Color(backgroundColor.getRGB())); //get rid of alpha
            graphics.drawString(letsWrite, gameWidth - (rightSideDisplayWidth / 2) - metrics.stringWidth(letsWrite) / 2, barHeight / 2 + metrics.getAscent() / 2);
        }

        if(displayOutline) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, barHeight - 2, gameWidth, 2);
        }


        return new Dimension(gameWidth,barHeight);
    }

}
