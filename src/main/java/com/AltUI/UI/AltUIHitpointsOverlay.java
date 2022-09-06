package com.AltUI.UI;

import com.AltUI.AltUIPlugin;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;

public class AltUIHitpointsOverlay extends OverlayPanel {

	public int CurrentHitpoints;
	public int Hitpoints;
	public int threshold;

	public Color backgroundColor;
	public Color foregroundColor;
	public Color flashingColor;
	public Color foregroundLowColor;
	public Color foregroundOffColor;

	public boolean displayHitpoints;
	public boolean displayOutline;

	private int counter = 0;

	public int gameWidth = 100;
	public int barDistance=0;

	public int barHeight = 25;

	public int flashInterval = 10;

	private final AltUIPlugin plugin;

	@Inject
	public AltUIHitpointsOverlay(AltUIPlugin plugin, int CurrentHitpoints, int Hitpoints, int threshold, Color backgroundColor, Color foregroundColor, Color foregroundLowColor, Color foregroundOffColor, Color flashingColor, int barDistance) {
		super(plugin);
		this.CurrentHitpoints = CurrentHitpoints;
		this.Hitpoints = Hitpoints;
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		this.flashingColor = flashingColor;
		this.foregroundLowColor = foregroundLowColor;
		this.foregroundOffColor = foregroundOffColor;
		this.threshold = threshold;
		this.plugin = plugin;
		this.barDistance=barDistance;

		setPosition(OverlayPosition.DYNAMIC);
		setBounds(new Rectangle(0,0,gameWidth,barHeight));
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY, "Clear","List"));
		setPosition(OverlayPosition.BOTTOM_LEFT);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	LayoutableRenderableEntity BarEntity = new LayoutableRenderableEntity() {
		//Figure out what to do with these three functions
		@Override
		public Rectangle getBounds(){return null;}
		@Override
		public void setPreferredLocation(Point position){}
		@Override
		public void setPreferredSize(Dimension dimension){}

		@Override
		public Dimension render(Graphics2D graphics) {
			int rightSideDisplayWidth = displayHitpoints ? 50 : 0;
			counter++;

			graphics.setColor(backgroundColor);
			if(CurrentHitpoints <= threshold && counter % flashInterval <= flashInterval/2) {
				graphics.setColor(flashingColor);
			}
			graphics.fillRect(0,barDistance,gameWidth,barHeight);

			graphics.setColor(foregroundColor);



			if(CurrentHitpoints <= threshold) {
				graphics.setColor(foregroundLowColor);
			}
			graphics.fillRect(0,barDistance,(gameWidth - rightSideDisplayWidth) * CurrentHitpoints / Hitpoints ,barHeight); //Shows a Filled Bar to represent current health out of total health

			if(displayHitpoints) {
				graphics.setColor(foregroundColor);
				graphics.fillRect(gameWidth - rightSideDisplayWidth + 1, barDistance, rightSideDisplayWidth - 1, barHeight);

				String letsWrite = CurrentHitpoints + "/" + Hitpoints;
				FontMetrics metrics = graphics.getFontMetrics();
				graphics.setColor(new Color(backgroundColor.getRGB())); //get rid of alpha
				graphics.drawString(letsWrite, gameWidth - (rightSideDisplayWidth / 2) - metrics.stringWidth(letsWrite) / 2, barDistance+(barHeight / 2 + metrics.getAscent() / 2));
			}
			if(displayOutline) {
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, barDistance+barHeight, gameWidth, 1);
			}
			return new Dimension(gameWidth,barHeight-7); //Genuinly cant figure out why i need to remove 7 from the bar height but it works
		}
	};
	@Override
	public Dimension render(Graphics2D graphics) {
		panelComponent.render(graphics);
		panelComponent.getChildren().add(BarEntity);//Panel that contains the health bar
		panelComponent.setBackgroundColor(new Color(0, 0, 0, 0));
		return super.render(graphics);
	}

}

