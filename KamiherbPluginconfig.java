package com.example.KamiHerb;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigTitleSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Title;

@ConfigGroup("KamiherbPluginconfig")

public interface KamiherbPluginconfig extends Config
{
	@ConfigSection(
			keyName = "delayConfig",
			name = "Sleep Delay Configuration",
			description = "Configure how the bot handles sleep delays",
			position = 0
	)
	default boolean delayConfig()
	{
		return false;
	}

	@Range(
			min = 0,
			max = 550
	)
	@ConfigItem(
			keyName = "sleepMin",
			name = "Sleep Min",
			description = "",
			position = 1,
			section = "delayConfig"
	)
	default int sleepMin()
	{
		return 60;
	}

	@Range(
			min = 0,
			max = 550
	)
	@ConfigItem(
			keyName = "sleepMax",
			name = "Sleep Max",
			description = "",
			position = 2,
			section = "delayConfig"
	)
	default int sleepMax()
	{
		return 350;
	}

	@Range(
			min = 0,
			max = 550
	)
	@ConfigItem(
			keyName = "sleepTarget",
			name = "Sleep Target",
			description = "",
			position = 3,
			section = "delayConfig"
	)
	default int sleepTarget()
	{
		return 120;
	}

	@Range(
			min = 0,
			max = 550
	)
	@ConfigItem(
			keyName = "sleepDeviation",
			name = "Sleep Deviation",
			description = "",
			position = 4,
			section = "delayConfig"
	)
	default int sleepDeviation()
	{
		return 150;
	}

	@ConfigItem(
			keyName = "sleepWeightedDistribution",
			name = "Sleep Weighted Distribution",
			description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
			position = 5,
			section = "delayConfig"
	)
	default boolean sleepWeightedDistribution()
	{
		return false;
	}

	@ConfigSection(
			keyName = "delayTickConfig",
			name = "Game Tick Configuration",
			description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
			position = 10
	)
	default boolean delayTickConfig()
	{
		return false;
	}

	@Range(
			min = 0,
			max = 10
	)
	@ConfigItem(
			keyName = "tickDelayMin",
			name = "Game Tick Min",
			description = "",
			position = 11,
			section = "delayTickConfig"
	)
	default int tickDelayMin()
	{
		return 0;
	}

	@Range(
			min = 0,
			max = 10
	)
	@ConfigItem(
			keyName = "tickDelayMax",
			name = "Game Tick Max",
			description = "",
			position = 12,
			section = "delayTickConfig"
	)
	default int tickDelayMax()
	{
		return 2;
	}

	@Range(
			min = 0,
			max = 10
	)
	@ConfigItem(
			keyName = "tickDelayTarget",
			name = "Game Tick Target",
			description = "",
			position = 13,
			section = "delayTickConfig"
	)
	default int tickDelayTarget()
	{
		return 0;
	}

	@Range(
			min = 0,
			max = 10
	)
	@ConfigItem(
			keyName = "tickDelayDeviation",
			name = "Game Tick Deviation",
			description = "",
			position = 14,
			section = "delayTickConfig"
	)
	default int tickDelayDeviation()
	{
		return 2;
	}

	@ConfigItem(
			keyName = "tickDelayWeightedDistribution",
			name = "Game Tick Weighted Distribution",
			description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
			position = 15,
			section = "delayTickConfig"
	)
	default boolean tickDelayWeightedDistribution()
	{
		return false;
	}

	@ConfigTitleSection(
			keyName = "instructionsTitle",
			name = "Instructions",
			description = "",
			position = 16
	)
	default Title instructionsTitle()
	{
		return new Title();
	}

	@ConfigItem(
			keyName = "instruction",
			name = "",
			description = "Instructions. Don't enter anything into this field",
			position = 20,
			titleSection = "instructionsTitle"
	)
	default String instruction()
	{
		return "Farms Ranarrs or Snaps ";
	}

	@ConfigItem(
			keyName = "getHerbType",
			name = "Herb Type",
			description = "Choose which Herb to plant and harvest",
			position = 24
	)

	default HerbTypes getHerbType()
	{
		return HerbTypes.SNAPDRAGON;
	}


	@ConfigItem(
			keyName = "farmingGuild",
			name = "Use Farming Guild",
			description = "",
			position = 25
	)
	default boolean farmingGuild()
	{
		return false;
	}


	@ConfigItem(
			keyName = "xeric",
			name = "Use xeric's talisman",
			description = "",
			position = 26
	)
	default boolean xeric()
	{
		return false;
	}


	@ConfigItem(
			keyName = "trollStronghold",
			name = "Use troll stronghold",
			description = "",
			position = 27
	)
	default boolean trollStronghold()
	{
		return false;
	}

	@ConfigItem(
			keyName = "weiss",
			name = "Use weiss",
			description = "",
			position = 28
	)
	default boolean weiss()
	{
		return false;
	}

	@ConfigItem(
			keyName = "harmony",
			name = "Use harmony",
			description = "",
			position = 32
	)
	default boolean harmony()
	{
		return false;
	}

	@ConfigItem(
			keyName = "ardyCape",
			name = "Use ardy cape",
			description = "",
			position = 29
	)
	default boolean ardyCape()
	{
		return false;
	}

	@ConfigItem(
			keyName = "fallyRing",
			name = "Use fally ring",
			description = "",
			position = 30
	)
	default boolean fallyRing()
	{
		return false;
	}

	@ConfigItem(
			keyName = "stopSeeds",
			name = "Stop when out of seeds",
			description = "",
			position = 31
	)
	default boolean stopSeeds()
	{
		return false;
	}


	@ConfigItem(
			keyName = "logout",
			name = "Log out when out of items",
			description = "",
			position = 40
	)
	default boolean logout()
	{
		return false;
	}

	@ConfigItem(
			keyName = "startButton",
			name = "Start/Stop",
			description = "Test button that changes variable value",
			position = 50
	)
	default Button startButton()
	{
		return new Button();
	}
}