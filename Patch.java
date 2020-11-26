package com.example.KamiHerb;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.timetracking.farming.FarmingRegion;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.Produce;

@RequiredArgsConstructor(
        access = AccessLevel.PUBLIC
)
@Slf4j
@Getter
public class Patch
{
    @Setter(AccessLevel.PUBLIC)
    private String produceName;
    @Setter(AccessLevel.PUBLIC)
    private String produceState;
    @Setter(AccessLevel.PUBLIC)
    private int patchID;
    @Setter(AccessLevel.PUBLIC)
    private Boolean composted = false;
    @Setter(AccessLevel.PUBLIC)
    private Boolean depositedHerbs = false;
    @Setter(AccessLevel.PUBLIC)
    private Boolean depositedWeeds = false;

    public Boolean ProduceIsHerb()
    {
        if( getProduceName().equals("Ranarr") || getProduceName().equals("Snapdragon"))
            return true;
        else
            return false;
    }

    public Boolean needsWorkDone()
    {
        log.debug(String.format("produceName = %s, produceState = %s, patchID = %s", produceName,produceState,patchID));
        log.debug("ranarr getname is " + Produce.RANARR.getName());
        if (produceName == Produce.RANARR.getName() || produceName == Produce.SNAPDRAGON.getName())
        {
            log.debug("Produce matched name");

            if (produceState == "Done in x mins")
            {

                if(composted && depositedHerbs && depositedWeeds)
                {
                    log.debug("patch done");
                    return false;
                }
                else
                {
                    log.debug(String.format("patch needs more work, %s, %s, %s", composted,depositedHerbs,depositedWeeds));
                    return true;
                }
            }
        }
        return true;
    }
}
