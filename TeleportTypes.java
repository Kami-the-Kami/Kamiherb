package com.example.KamiHerb;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

public enum TeleportTypes
{
    //SMOKE_RUNES(ItemID.SMOKE_RUNE, ItemID.AIR_RUNE, ItemID.AIR_TALISMAN),
    //LAVA_RUNES(ItemID.LAVA_RUNE, ItemID.EARTH_RUNE, ItemID.EARTH_TALISMAN),
    //STEAM_RUNES(ItemID.STEAM_RUNE, ItemID.WATER_RUNE, ItemID.WATER_TALISMAN);
    Weiss(true, true),
    Troll(false, true),
    Neither(false, false);


    @Getter(AccessLevel.PACKAGE)
    private final boolean weiss;

    @Getter(AccessLevel.PACKAGE)
    private final boolean troll;



    TeleportTypes(final boolean weiss, final boolean troll)
    {
        this.weiss = weiss;
        this.troll = troll;
    }
}
