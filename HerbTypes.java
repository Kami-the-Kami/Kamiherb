package com.example.KamiHerb;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

public enum HerbTypes
{
    //SMOKE_RUNES(ItemID.SMOKE_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
    //LAVA_RUNES(ItemID.LAVA_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
    //STEAM_RUNES(ItemID.STEAM_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN);
    SNAPDRAGON(ItemID.GRIMY_SNAPDRAGON, ItemID.GRIMY_SNAPDRAGON + 1, ItemID.SNAPDRAGON_SEED),
    RANARR(ItemID.GRIMY_RANARR_WEED, ItemID.GRIMY_RANARR_WEED + 1, ItemID.RANARR_SEED);


    @Getter(AccessLevel.PACKAGE)
    private final int notedHerbID;

    @Getter(AccessLevel.PACKAGE)
    private final int grimyHerbID;

    @Getter(AccessLevel.PACKAGE)
    private final int seedID;


    HerbTypes(final int grimyHerbID, final int notedHerbID, final int seedID)
    {
        this.grimyHerbID = grimyHerbID;
        this.notedHerbID = notedHerbID;
        this.seedID = seedID;
    }
}



/*
package net.runelite.client.plugins.icombinationrunecrafter;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

public enum RunecraftingTypes
{
	SMOKE_RUNES(ItemID.SMOKE_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
	LAVA_RUNES(ItemID.LAVA_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
	STEAM_RUNES(ItemID.STEAM_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN);

	@Getter(AccessLevel.PACKAGE)
	private final int createdRuneID;

	@Getter(AccessLevel.PACKAGE)
	private final int materialRuneID;

	@Getter(AccessLevel.PACKAGE)
	private final int talismanID;


	RunecraftingTypes(final int createdRuneID, final int materialRuneID, final int talismanID)
	{
		this.createdRuneID = createdRuneID;
		this.materialRuneID = materialRuneID;
		this.talismanID = talismanID;
	}
}
 */