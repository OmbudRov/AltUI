package com.AltUI;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("AltUI")
public interface AltUIConfig extends Config
{
	@ConfigSection(
		name = "General",
		description = "Everything general",
		position = -1
	)
	String general = "general";

	@ConfigSection(
		name = "Prayer",
		description = "Related to the Prayer",
		position = 1
	)
	String Prayer = "Prayer";

	@ConfigSection(
		name = "Hitpoints",
		description = "Related to the Hitpoints",
		position = 1
	)
	String Hitpoints = "Hitpoints";


	@ConfigSection(
		name = "Screen Blasters",
		description = "you're gonna have to figure out what that one means ;)",
		position = 3
	)
	String screenBlasters = "screenBlasters";

	@ConfigSection(
		name = "Ear Blasters",
		description = "Blasts your ears with pertinent info",
		position = 4
	)
	String earBlasters = "earBlasters";

	@ConfigSection(
		name = "Nearby Drops",
		description = "Nearby drops pointer-outer",
		position = 4
	)
	String nearbyDrops = "nearbyDrops";

	@ConfigSection(
		name = "Tracker",
		description = "Tracks all accounts using the plugin at once",
		position = 5
	)
	String tracker = "tracker";

	@ConfigItem(
		keyName = "rsnDisplay",
		name = "RSN Display",
		description = "Whether or not to display the account's RSN",
		section = general
	)
	default boolean rsnDisplay() {
		return true;
	}



	@Range(
		min=1
	)
	@ConfigItem(
		keyName = "rsnFontSize",
		name = "RSN Font Size",
		description = "The size of the RSN",
		section = general
	)
	default int rsnFontSize() {
		return 50;
	}

	@ConfigItem(
		keyName = "flashInterval",
		name = "Flashing Interval",
		description = "How many frames it should be before a blood shard flasher or Prayer prayer bar flasher flashes. (Higher = less flashy, avoid seizures.) At 50 FPS, if a flashing interval of 50 will result in one cycle of flashing per second.",
		section = general
	)
	default int flashyInterval() { return 20; }

	@ConfigItem(
		keyName = "slotsLeft",
		name = "Show Slots Left",
		description = "Show number of item slots open",
		section = general
	)
	default boolean slotsLeft() {
		return true;
	}

	@Range(
		min=1
	)
	@ConfigItem(
		keyName = "slotsLeftFontSize",
		name = "Slots Left Font Size",
		description = "Font size for slots left display",
		section = general
	)
	default int slotsLeftFontSize() {
		return 50;
	}

	@ConfigItem(
		keyName = "sessionTracker",
		name = "Session Tracker",
		description = "Displays profit from the session",
		section = general
	)
	default boolean sessionTracker() {
		return true;
	}


	@ConfigItem(
		keyName = "PrayerBar",
		name = "Bar",
		description = "Whether to show the Prayer bar",
		section = Prayer,
		position = 0
	)
	default boolean PrayerBar() {
		return true;
	}

	@Range(
		max=99
	)
	@ConfigItem(
		keyName = "PrayerThreshold",
		name = "Threshold",
		description = "The threshold for the Prayer bar",
		section = Prayer,
		position = 1
	)
	default int PrayerThreshold() {
		return 5;
	}

	@ConfigItem(
		keyName = "PrayerSize",
		name = "Size",
		description = "The size for the Prayer bar",
		section = Prayer,
		position = 2
	)
	default int PrayerSize() {
		return 25;
	}

	@ConfigItem(
		keyName = "PrayerOutline",
		name = "Outline",
		description = "Whether or not to display the white outline",
		section = Prayer,
		position = 3
	)
	default boolean PrayerOutline() {
		return true;
	}

	@ConfigItem(
		keyName = "PrayerPrayer",
		name = "Display Prayer",
		description = "Whether or not to display prayer",
		section = Prayer,
		position = 4
	)
	default boolean PrayerPrayer() {
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName="PrayerBackground",
		name="Background",
		description = "The background for the Prayer bar",
		section = Prayer
	)
	default Color PrayerBackground() {
		return new Color(0,0,0,0);
	}

	@Alpha
	@ConfigItem(
		keyName="PrayerForeground",
		name="Foreground",
		description = "The foreground for the Prayer bar",
		section = Prayer
	)
	default Color PrayerForeground() {
		return new Color(38,63,62,0);
	}

	@Alpha
	@ConfigItem(
		keyName="PrayerForegroundOff",
		name="Foreground Off",
		description = "The foreground for the Prayer bar when off",
		section = Prayer
	)
	default Color PrayerForegroundOff() {
		return new Color(0, 5, 10,0);
	}


	@Alpha
	@ConfigItem(
		keyName="PrayerForegroundLow",
		name="Foreground Low",
		description = "The foreground for the Prayer bar when below threshold",
		section = Prayer
	)
	default Color PrayerForegroundLow() {
		return new Color(83,55,29,0);
	}

	@Alpha
	@ConfigItem(
		keyName = "PrayerFlashing",
		name="Flashing",
		description = "The flashing color for the Prayer bar",
		section = Prayer
	)
	default Color PrayerFlashing() {
		return new Color(192, 157,69,0);
	}

	@ConfigItem(
		keyName = "HitpointsBar",
		name = "Bar",
		description = "Whether to show the Hitpoints bar",
		section = Hitpoints,
		position = 0
	)
	default boolean HitpointsBar() {
		return true;
	}

	@Range(
		max=99
	)
	@ConfigItem(
		keyName = "HitpointsThreshold",
		name = "Threshold",
		description = "The threshold for the Hitpoints bar",
		section = Hitpoints,
		position = 1
	)
	default int HitpointsThreshold() {
		return 5;
	}

	@ConfigItem(
		keyName = "HitpointsSize",
		name = "Size",
		description = "The size for the Hitpoints bar",
		section = Hitpoints,
		position = 2
	)
	default int HitpointsSize() {
		return 25;
	}

	@ConfigItem(
		keyName = "HitpointsOutline",
		name = "Outline",
		description = "Whether or not to display the white outline",
		section = Hitpoints,
		position = 3
	)
	default boolean HitpointsOutline() {
		return true;
	}

	@ConfigItem(
		keyName = "HitpointsHitpoints",
		name = "Display Hitpoints",
		description = "Whether or not to display Hitpoints",
		section = Hitpoints,
		position = 4
	)
	default boolean Hitpoints() {
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName="HitpointsBackground",
		name="Background",
		description = "The background for the Hitpoints bar",
		section = Hitpoints
	)
	default Color HitpointsBackground() {
		return new Color(0,0,0,0);
	}

	@Alpha
	@ConfigItem(
		keyName="HitpointsForeground",
		name="Foreground",
		description = "The foreground for the Hitpoints bar",
		section = Hitpoints
	)
	default Color HitpointsForeground() {
		return new Color(38,63,62,0);
	}

	@Alpha
	@ConfigItem(
		keyName="HitpointsForegroundOff",
		name="Foreground Off",
		description = "The foreground for the Hitpoints bar when off",
		section = Hitpoints
	)
	default Color HitpointsForegroundOff() {
		return new Color(0, 5, 10,0);
	}


	@Alpha
	@ConfigItem(
		keyName="HitpointsForegroundLow",
		name="Foreground Low",
		description = "The foreground for the Hitpoints bar when below threshold",
		section = Hitpoints
	)
	default Color HitpointsForegroundLow() {
		return new Color(83,55,29,0);
	}

	@Alpha
	@ConfigItem(
		keyName = "HitpointsFlashing",
		name="Flashing",
		description = "The flashing color for the Hitpoints bar",
		section = Hitpoints
	)
	default Color HitpointsFlashing() {
		return new Color(192, 157,69,0);
	}


	@ConfigItem(
		keyName = "goBank",
		name="Go Bank",
		description = "Whether or not the inventory is full",
		section = screenBlasters
	)
	default boolean goBank() {
		return true;
	}

	@ConfigItem(
		keyName = "treasureNear",
		name="Treasure Near",
		description = "Whether or not there is red treasure near",
		section = screenBlasters
	)
	default boolean treasureNear() {
		return true;
	}

	@ConfigItem(
		keyName = "tooAFKIndicator",
		name = "Too AFK Indicator",
		description = "Tells if the account is not retaliating",
		section = screenBlasters
	)
	default boolean tooAFKIndicator() {
		return true;
	}


	@ConfigItem(
		keyName = "doNearbyDrops",
		name="Nearby Drops",
		description = "Whether or not to list nearby drops",
		section = nearbyDrops
	)
	default boolean doNearbyDrops() {
		return true;
	}

	@ConfigItem(
		keyName = "nearbyThreshold",
		name = "Valuable Threshold",
		description = "Threshold in traded value for nearby drops to be considered valuable",
		section = general
	)
	default int nearbyThreshold() {
		return 6000;
	}

	@ConfigItem(
		keyName = "nearbyBlacklist",
		name = "Valuable Blacklist",
		description = "Items to ignore when their stack value is over the Nearby Threshold (comma separated list of item names, not case sensitive, don't put spaces between commas)",
		section = general
	)
	default String nearbyBlacklist() {
		return "rune dagger,vampyre dust";
	}

	@Range(
		min=0
	)
	@ConfigItem(
		keyName = "nearbyRange",
		name = "Range",
		description = "The range, in tiles, to search for new nearby items in (maximum distance along an axis)",
		section = nearbyDrops
	)
	default int nearbyRange() { return 100; }

	@ConfigItem(
		keyName = "customBloodshard",
		name = "Shard EarBlaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\shard.wav' on windows",
		section = earBlasters
	)
	default boolean customBloodshard() {
		return true;
	}

	@ConfigItem(
		keyName = "customOnItsBoltTips",
		name = "Onyx EarBlaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\onyx.wav' on windows",
		section = earBlasters
	)
	default boolean customOnItsBoltTips() {
		return true;
	}

	@ConfigItem(
		keyName = "customYoink",
		name = "Yoink EarBlaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\yoink.wav' on windows",
		section = earBlasters
	)
	default boolean customYoink() {
		return true;
	}

	@ConfigItem(
		keyName = "customPrayer",
		name = "Prayer EarBlaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\prayer.wav' on windows",
		section = earBlasters
	)
	default boolean customPrayer() {
		return true;
	}

	@ConfigItem(
		keyName = "customLowHP",
		name = "Low HP Blaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\health.wav' on windows",
		section = earBlasters
	)
	default boolean customLowHP() {
		return true;
	}


	@ConfigItem(
		keyName = "customRegularDrops",
		name = "Regular Drop EarBlaster",
		description = "Place file in '%userprofile%\\.runelite\\qolting\\regularDrop.wav' on windows, only works for items over 'Valuable Threshold' threshold set in General!",
		section = earBlasters
	)
	default boolean customRegularDrops() {
		return true;
	}

	@Range(
		min = 1,
		max = 100
	)
	@ConfigItem(
		keyName = "numLoops",
		name = "Play Blasters (#)",
		description = "How many times to play an Ear Blaster",
		section = earBlasters,
		position = -3
	)
	default int loopBlasters() { return 1; }

	@ConfigItem(
		keyName = "infLoop",
		name = "Loop Shard/Onyx",
		description = "Loop the ear blaster until a very valuable item has been picked up [overrides 'Play Blasters (#)']",
		section = earBlasters,
		position = -2
	)
	default boolean loopUntil() { return false; }

	@ConfigItem(
		keyName = "useAccountTracker",
		name = "Use Account Tracker",
		description = "Whether or not to save info to the account tracker at %userprofile%/.runelite/qolting/ (for many functionalities of the plugin this is recommended)",
		section = tracker
	)
	default boolean useAccountTracker() { return true; }

	@ConfigItem(
		keyName = "launchAccountTracker",
		name = "Launch Account Tracker",
		description = "Opens the account tracker window with this client as its parent",
		section = tracker,
		position = -2
	)
	default boolean launchAccountTracker() { return false; }


	@ConfigItem(
		keyName = "alwaysOnTopTracker",
		name = "Always On Top",
		description = "Makes the account tracker always-on-top",
		section = tracker
	)
	default boolean alwaysOnTopTracker() { return false; }
}
