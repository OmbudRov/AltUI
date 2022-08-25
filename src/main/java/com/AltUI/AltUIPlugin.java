package com.AltUI;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import com.AltUI.AccountManager.AltUIAccountInfo;
import com.AltUI.AccountManager.AltUIAccountManager;
import com.AltUI.AccountManager.AltUIAccountManagerFrame;
import com.AltUI.UI.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;


import java.io.*;
import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
	name = "AltUI"
)
public class AltUIPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private AltUIConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private ConfigManager configManager;

	private AltUIAccountManager altUIAccountManager;

	private AltUIAccountManagerFrame currentManager = null;

	private File AltUIDirectory = new File(RuneLite.RUNELITE_DIR, "AltUI");

	private ArrayList<GroundItem> nearbyItems = new ArrayList<>();

	//Implement way to make the stacking of the bars dynamic using Size of the bars(if enabled) by passing the sum of Respective Bar and the other's bar size as an argument
	private AltUIPrayerOverlay AltUIPrayerPanel = null;
	protected int Pbar = 0;

	private AltUIHitpointsOverlay AltUIHitpointsPanel = null;
	protected int Hbar = 0;
	private AltUIRSNOverlay AltUIRSNOverlay = null;

	private AltUIShoutOverlay AltUIShoutPanel = null;
	private AltUINearbyPanel AltUINearbyPanel = null;


	private static final long CLIP_MTIME_UNLOADED = -2;
	private static final long CLIP_MTIME_BUILTIN = -1;

	public static final String LOCK_FILE = "AltUIDisableBLACKOUT.txt";


	public final File[] files = {
		new File(RuneLite.RUNELITE_DIR, "AltUI\\yoink.wav"),
		new File(RuneLite.RUNELITE_DIR, "AltUI\\shard.wav"),
		new File(RuneLite.RUNELITE_DIR, "AltUI\\onyx.wav"),
		new File(RuneLite.RUNELITE_DIR, "AltUI\\prayer.wav"),
		new File(RuneLite.RUNELITE_DIR, "AltUI\\health.wav"),
		new File(RuneLite.RUNELITE_DIR, "AltUI\\regularDrop.wav")
	};
	public final Clip[] clips = {
		null,
		null,
		null,
		null,
		null,
		null
	};
	private long[] lastClipMTime = {
		CLIP_MTIME_UNLOADED,
		CLIP_MTIME_UNLOADED,
		CLIP_MTIME_UNLOADED,
		CLIP_MTIME_UNLOADED,
		CLIP_MTIME_UNLOADED,
		CLIP_MTIME_UNLOADED
	};
	private final byte YOINK = 0;
	private final byte SHARD = 1;
	private final byte ONYX = 2;
	private final byte PRAYER = 3;
	private final byte HEALTH = 4;
	private final byte REGULAR_DROP = 5;

	int takingItem = 0;
	int ownLootTimer = 0;
	boolean playShardSoundNextTick = false;

	float timeElapsed = 0;

	private void updatePrayer()
	{
		if (!config.PrayerBar())
		{
			overlayManager.remove(AltUIPrayerPanel);
			return;
		}
		overlayManager.add(AltUIPrayerPanel);

		AltUIPrayerPanel.Prayer = client.getRealSkillLevel(Skill.PRAYER);
		AltUIPrayerPanel.Currentprayer = client.getBoostedSkillLevel(Skill.PRAYER);
		AltUIPrayerPanel.gameWidth = client.getViewportWidth();
	}

	private void updateHitpoints()
	{
		if (!config.HitpointsBar())
		{
			overlayManager.remove(AltUIHitpointsPanel);
			return;
		}
		overlayManager.add(AltUIHitpointsPanel);

		AltUIHitpointsPanel.Hitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
		AltUIHitpointsPanel.CurrentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
		AltUIHitpointsPanel.gameWidth = client.getViewportWidth();
	}

	private void updateRSN()
	{
		if (!config.rsnDisplay())
		{
			overlayManager.remove(AltUIRSNOverlay);
			return;
		}
		overlayManager.add(AltUIRSNOverlay);
	}

	private void updateShout()
	{
		boolean[] thingsToBeFussedAbout = new boolean[]{false, false, false};
		boolean any = false;
		if (config.tooAFKIndicator() && tooAFK())
		{
			thingsToBeFussedAbout[AltUIShoutOverlay.TOO_AFK] = true;
			any = true;
		}
		if (config.treasureNear() && shardNear())
		{
			thingsToBeFussedAbout[AltUIShoutOverlay.SHARD_NEARBY] = true;
			any = true;
		}
		if (config.goBank() && inventoryFull())
		{
			thingsToBeFussedAbout[AltUIShoutOverlay.GO_BANK] = true;
			any = true;
		}
		AltUIShoutPanel.thingsToBeFussedAbout = thingsToBeFussedAbout;
		AltUIShoutPanel.viewportWidth = client.getViewportWidth();
		AltUIShoutPanel.viewportHeight = client.getViewportHeight();
		if (any)
		{
			overlayManager.add(AltUIShoutPanel);
		}
		else
		{
			overlayManager.remove(AltUIShoutPanel);
		}
	}

	private void updateNearby()
	{
		if (!config.doNearbyDrops())
		{
			overlayManager.remove(AltUINearbyPanel);
			return;
		}
		overlayManager.add(AltUINearbyPanel);
		AltUINearbyPanel.nearbyItems = nearbyItems;
	}


	public boolean isDoorClosed(int worldX, int worldY)
	{
		LocalPoint coords = LocalPoint.fromWorld(client, worldX, worldY);
		return client.getScene().getTiles()[0][coords.getSceneX()][coords.getSceneY()].getWallObject() != null;
	}

	public String getItemName(int id)
	{
		return client.getItemDefinition(id).getName();
	}

	public int getItemPrice(int id)
	{
		return itemManager.getItemPrice(id);
	}

	public boolean inventoryFull()
	{
		ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY);
		if (invent == null)
		{
			return false;
		}
		if (invent.getItems().length < 28)
		{
			return false;
		}
		boolean full = true;
		for (Item i : invent.getItems())
		{
			if (i == null)
			{
				full = false;
				break;
			}
			if (i.getQuantity() == 0)
			{
				full = false;
			}
		}
		return full;
	}

	public boolean shardNear()
	{
		for (GroundItem i : nearbyItems)
		{
			if (i.id == ItemID.BLOOD_SHARD)
			{
				return true;
			}
		}
		return false;
	}

	public boolean tooAFK()
	{
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return false;
		}
		if (localPlayer.getPlayerComposition().getEquipmentId(KitType.TORSO) == ItemID.VYRE_NOBLE_TOP || localPlayer.getPlayerComposition().getEquipmentId(KitType.LEGS) == ItemID.VYRE_NOBLE_LEGS || localPlayer.getPlayerComposition().getEquipmentId(KitType.BOOTS) == ItemID.VYRE_NOBLE_SHOES)
		{
			return true;
		}
		if (client.getVarpValue(172) == 1)
		{
			return true;
		}
		return false;
	}

	public boolean isPrayerOff()
	{
		for (Prayer pray : Prayer.values())
		{
			if (client.isPrayerActive(pray))
			{
				return false;
			}
		}
		return true;
	}

	public boolean LowPrayer()
	{
		//this account
		if (client.getBoostedSkillLevel(Skill.PRAYER) <= config.PrayerThreshold())
		{
			return true;
		}
		//any account
		ArrayList<AltUIAccountInfo> info = altUIAccountManager.getAllAccountInfo();
		for (AltUIAccountInfo i : info)
		{
			if (i.prayer <= config.PrayerThreshold())
			{
				return true;
			}
		}
		return false;
	}

	public boolean LowHitpoints()
	{
		//this account
		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.HitpointsThreshold())
		{
			return true;
		}
		//any account
		ArrayList<AltUIAccountInfo> info = altUIAccountManager.getAllAccountInfo();
		for (AltUIAccountInfo i : info)
		{
			if (i.health <= config.HitpointsThreshold())
			{
				return true;
			}
		}
		return false;
	}

	public boolean ignoreItem(int id)
	{
		String name = getItemName(id);
		return ignoreItem(name);
	}

	public boolean ignoreItem(String name)
	{
		for (String s : config.nearbyBlacklist().split(","))
		{
			if (s.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}

	public String getRSN()
	{
		if (client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
		{
			return "?";
		}
		return client.getLocalPlayer().getName();
	}

	private void removeAllPanels()
	{
		overlayManager.remove(AltUIPrayerPanel);
		overlayManager.remove(AltUIHitpointsPanel);
		overlayManager.remove(AltUIRSNOverlay);
		overlayManager.remove(AltUIShoutPanel);
		overlayManager.remove(AltUINearbyPanel);

	}

	@Override
	protected void startUp()
	{
		if(config.PrayerBar())
			Pbar=config.PrayerSize();
		else
			Pbar=0;
		if(config.HitpointsBar())
			Pbar=config.HitpointsSize();
		else
			Hbar=0;
		altUIAccountManager = new AltUIAccountManager(AltUIDirectory);
		AltUIPrayerPanel = new AltUIPrayerOverlay(this, 0, 0, config.PrayerThreshold(), config.PrayerBackground(), config.PrayerForeground(), config.PrayerForegroundLow(), config.PrayerForegroundOff(), config.PrayerFlashing());
		AltUIHitpointsPanel = new AltUIHitpointsOverlay(this, 0, 0, config.HitpointsThreshold(), config.HitpointsBackground(), config.HitpointsForeground(), config.HitpointsForegroundLow(), config.HitpointsForegroundOff(), config.HitpointsFlashing(),Pbar);
		AltUIRSNOverlay = new AltUIRSNOverlay(this);
		AltUIShoutPanel = new AltUIShoutOverlay(this, client.getViewportWidth(), client.getViewportHeight());
		AltUINearbyPanel = new AltUINearbyPanel(this);
		updateConfig();

	}

	@Override
	protected void shutDown()
	{
		removeAllPanels();
	}

	private synchronized void playCustomSound(byte index, boolean justOnce)
	{
		File file = files[index];
		long currentMTime = file.exists() ? file.lastModified() : CLIP_MTIME_BUILTIN;
		if (clips[index] == null || currentMTime != lastClipMTime[index] || !clips[index].isOpen())
		{
			if (clips[index] != null)
			{
				clips[index].close();
			}

			try
			{
				clips[index] = AudioSystem.getClip();
			}
			catch (LineUnavailableException e)
			{
				lastClipMTime[index] = CLIP_MTIME_UNLOADED;
				log.warn("Unable to play notification", e);
				return;
			}

			lastClipMTime[index] = currentMTime;

			if (!tryLoadNotification(index))
			{
				return;
			}
		}
		clips[index].setMicrosecondPosition(0);
		// Using loop instead of start + setFramePosition prevents the clip
		// from not being played sometimes, presumably a race condition in the
		// underlying line driver
		clips[index].loop(justOnce ? 0 : Math.min(Math.max(0, config.loopBlasters() - 1), 100));
	}

	private boolean tryLoadNotification(byte index)
	{
		File file = files[index];
		if (file.exists())
		{
			try (InputStream fileStream = new BufferedInputStream(new FileInputStream(file));
				 AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream))
			{
				clips[index].open(sound);
				return true;
			}
			catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
			{
				log.warn("Unable to load notification sound", e);
			}
		}

		return false;
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked clicked)
	{
		if (clicked.getMenuOption().toLowerCase().contains("take"))
		{
			takingItem = 10;
		}
	}

	private void clearNearbyItems()
	{
		nearbyItems.clear();
		AltUINearbyPanel.nearbyItems.clear();
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked event)
	{
		if (event.getEntry().getMenuAction() == MenuAction.RUNELITE_OVERLAY &&
			event.getEntry().getTarget().equals("List") &&
			event.getEntry().getOption().equals("Clear"))
		{
			clearNearbyItems();
		}

	}

	private Item[] getInventoryList(ItemContainerChanged changed)
	{
		return ArrayUtils.addAll(changed.getItemContainer().getItems(), client.getItemContainer(InventoryID.EQUIPMENT).getItems());
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived)
	{
		for (ItemStack i : npcLootReceived.getItems())
		{
			if (i.getId() == ItemID.BLOOD_SHARD)
			{
				ownLootTimer = 10;
				break;
			}
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned itemSpawned)
	{
		TileItem item = itemSpawned.getItem();
		if (item.getId() == ItemID.BLOOD_SHARD && config.customBloodshard())
		{
			//For the yoink sounds, we'll play next tick to compare if it is the player's drop
			playShardSoundNextTick = true;
		}
		else if (item.getId() == ItemID.ONYX_BOLT_TIPS && config.customOnItsBoltTips())
		{
			playCustomSound(ONYX, config.loopUntil());
		}
		else if (config.customRegularDrops() && getItemPrice(item.getId()) * item.getQuantity() >= config.nearbyThreshold())
		{
			playCustomSound(REGULAR_DROP, false);
		}
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		if (Math.abs(itemSpawned.getTile().getWorldLocation().getX() - client.getLocalPlayer().getWorldLocation().getX()) > config.nearbyRange() ||
			Math.abs(itemSpawned.getTile().getWorldLocation().getY() - client.getLocalPlayer().getWorldLocation().getY()) > config.nearbyRange())
		{
			//item is outside the range of the config
			return;
		}

		boolean existing = false;
		nearbyItems.add(new GroundItem(item.getId(), item.getQuantity(), itemSpawned.getTile()));

	}

	@Subscribe
	public void onItemDespawned(ItemDespawned itemDespawned)
	{
		TileItem item = itemDespawned.getItem();

		for (GroundItem i : nearbyItems)
		{
			if (i.id == item.getId() && i.quantity == itemDespawned.getItem().getQuantity())
			{
				nearbyItems.remove(i);
				break;
			}
		}

	}

	@Subscribe
	public void onItemQuantityChanged(ItemQuantityChanged itemQuantityChanged)
	{
		TileItem item = itemQuantityChanged.getItem();

		for (GroundItem i : nearbyItems)
		{
			if (i.id == item.getId() && itemQuantityChanged.getOldQuantity() == i.quantity)
			{
				i.quantity = itemQuantityChanged.getNewQuantity();
				break;
			}
		}
	}

	public void updateConfig()
	{
		if(config.PrayerBar())
			Pbar=config.PrayerSize();
		else
			Pbar=0;
		AltUIPrayerPanel.backgroundColor = config.PrayerBackground();
		AltUIPrayerPanel.foregroundColor = config.PrayerForeground();
		AltUIPrayerPanel.flashingColor = config.PrayerFlashing();
		AltUIPrayerPanel.foregroundLowColor = config.PrayerForegroundLow();
		AltUIPrayerPanel.foregroundOffColor = config.PrayerForegroundOff();
		AltUIPrayerPanel.threshold = config.PrayerThreshold();
		AltUIPrayerPanel.barHeight = config.PrayerSize();
		AltUIPrayerPanel.displayOutline = config.PrayerOutline();
		AltUIPrayerPanel.displayPrayer = config.PrayerPrayer();
		AltUIPrayerPanel.flashInterval = config.flashyInterval();

		if(config.HitpointsBar())
			AltUIHitpointsPanel.barDistance=config.PrayerSize();
		else
			AltUIHitpointsPanel.barDistance=0;
		AltUIHitpointsPanel.backgroundColor = config.HitpointsBackground();
		AltUIHitpointsPanel.foregroundColor = config.HitpointsForeground();
		AltUIHitpointsPanel.flashingColor = config.HitpointsFlashing();
		AltUIHitpointsPanel.foregroundLowColor = config.HitpointsForegroundLow();
		AltUIHitpointsPanel.foregroundOffColor = config.HitpointsForegroundOff();
		AltUIHitpointsPanel.threshold = config.HitpointsThreshold();
		AltUIHitpointsPanel.barHeight = config.HitpointsSize();
		AltUIHitpointsPanel.displayOutline = config.HitpointsOutline();
		AltUIHitpointsPanel.displayHitpoints = config.Hitpoints();
		AltUIHitpointsPanel.flashInterval = config.flashyInterval();

		AltUIRSNOverlay.fontSize = config.rsnFontSize();
		AltUINearbyPanel.threshold = config.nearbyThreshold();

		AltUIShoutPanel.flashInterval = config.flashyInterval();

		if (currentManager != null)
		{
			currentManager.frame.setAlwaysOnTop(config.alwaysOnTopTracker());
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		updateConfig();

		if (configChanged.getKey().equals("launchAccountTracker"))
		{
			if (currentManager != null)
			{
				currentManager.close();
			}
			currentManager = new AltUIAccountManagerFrame(altUIAccountManager, config.alwaysOnTopTracker(), this);
			currentManager.update(config.PrayerThreshold(), config.nearbyThreshold());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		int state = gameStateChanged.getGameState().getState();
		if (state == GameState.HOPPING.getState() || state == GameState.LOGGING_IN.getState() || state == GameState.STARTING.getState())
		{
			clearNearbyItems();
		}
	}

	int previousPrayer = 0;
	int previousHealth = 0;

	public void updateStatusNoise()
	{

		if (config.customPrayer() && previousPrayer > config.PrayerThreshold() && client.getBoostedSkillLevel(Skill.PRAYER) <= config.PrayerThreshold())
		{
			playCustomSound(PRAYER, false);
		}
		if (config.customLowHP() && previousHealth > 50 && client.getBoostedSkillLevel(Skill.HITPOINTS) <= 49)
		{
			playCustomSound(HEALTH, false);
		}


		previousPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
		previousHealth = client.getBoostedSkillLevel(Skill.HITPOINTS);
	}


	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (config.useAccountTracker())
		{
			ArrayList<LootItem> items = new ArrayList<>();
			for (GroundItem item : nearbyItems)
			{
				items.add(new LootItem(item, this));
			}
		}
		if (currentManager != null)
		{
			currentManager.update(config.PrayerThreshold(), config.nearbyThreshold());
		}
		if (config.loopUntil())
		{
			for (GroundItem item : nearbyItems)
			{
				if (item.id == ItemID.BLOOD_SHARD && (clips[SHARD] == null || !clips[SHARD].isRunning()))
				{
					playCustomSound(SHARD, true);
					break;
				}
				if (item.id == ItemID.ONYX_BOLT_TIPS && (clips[ONYX] == null || !clips[ONYX].isRunning()))
				{
					playCustomSound(ONYX, true);
					break;
				}
			}
		}
		else
		{
			if (playShardSoundNextTick)
			{
				if (ownLootTimer == 0 && config.customYoink())
				{
					playCustomSound(YOINK, false);
				}
				else if (config.customBloodshard())
				{
					playCustomSound(SHARD, false);
				}
				playShardSoundNextTick = false;
			}
		}

		updateStatusNoise();
		updatePrayer();
		updateRSN();
		updateShout();
		updateNearby();
		updateHitpoints();

		takingItem = Math.max(0, takingItem - 1);
		ownLootTimer = Math.max(0, ownLootTimer - 1);
	}

	@Provides
	AltUIConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AltUIConfig.class);
	}
}
