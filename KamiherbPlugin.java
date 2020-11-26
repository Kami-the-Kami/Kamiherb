package com.example.KamiHerb;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.plugins.iutils.*;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.*;
import org.pf4j.Extension;
//import com.owain.chinbreakhandler.ChinBreakHandler;
import java.time.Instant;
import java.util.*;
import java.awt.Rectangle;

import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.widgets.WidgetID;

import static net.runelite.api.widgets.WidgetInfo.BANK_PIN_INSTRUCTION_TEXT;


import static net.runelite.client.plugins.iutils.iUtils.iterating;
import static com.example.KamiHerb.KamiHerbState.*;

import net.runelite.client.plugins.timetracking.*;


@Extension
@PluginDependency(iUtils.class)
@PluginDependency(TimeTrackingPlugin.class)
@PluginDescriptor(
        name = "Kami Herb",
        enabledByDefault = true,
        description = "Kami Herb",
        type = PluginType.SYSTEM
)


@Slf4j
public class KamiherbPlugin extends Plugin
{
    //------------------------
    boolean DO_FULL_DEBUG = true;
    //--------------------


    String homeDir = System.getProperty("user.home");
    HashMap<Integer, Integer[]> herbPatches = new HashMap<Integer, Integer[]>();
    Integer currentPatch = null;
    Integer harvestStage = 0;
    //HerbPatches patches = new HerbPatches();
    List<Integer> doneHerbPatches = new ArrayList<>();
    MenuEntry targetMenu;
    Instant botTimer;
    Player player;
    KamiHerbState state;
    KamiHerbState seedState;
    KamiHerbState necklaceState;
    LocalPoint beforeLoc = new LocalPoint(0, 0);
    GameObject bankChest;
    GameObject mysteriousRuins;
    GameObject fireAltar;
    GameObject herbPatch;
    Widget bankItem;
    Boolean weedSelected;
    Boolean herbSelected;

    InspectionResult lastInspection;

    Boolean workingOnPatch;
    Boolean ignorePatch;

    //	@Inject
//	private ChinBreakHandler chinBreakHandler;
    WidgetItem useableItem;
    Set<Integer> SKILL_NECKLACES = Set.of(ItemID.SKILLS_NECKLACE1, ItemID.SKILLS_NECKLACE2, ItemID.SKILLS_NECKLACE3, ItemID.SKILLS_NECKLACE4, ItemID.SKILLS_NECKLACE5, ItemID.SKILLS_NECKLACE6);
    Set<Integer> SPENT_NECKLACES = Set.of(ItemID.SKILLS_NECKLACE);
    List<Integer> REQUIRED_ITEMS = new ArrayList<>();

    //String[] keys = patches.getKeys();
    boolean startBot;
    boolean setTalisman;
    boolean outOfNecklaces;
    boolean outOfStaminaPots;
    boolean trollStronghold;
    boolean weiss;
    boolean farmingGuild;
    boolean xeric;
    boolean ardyCape;
    boolean fallyRing;
    long sleepLength;
    int tickLength;
    int timeout;
    int coinsPH;
    int beforeEssence;
    int totalEssence;
    int beforeMaterialRunes;
    int totalMaterialRunes;
    int beforeTalisman;
    int totalTalisman;
    int totalCraftedRunes;
    int beforeCraftedRunes;
    int currentCraftedRunes;
    int totalDuelRings;
    int totalNecklaces;
    int totalStaminaPots;
    int runesPH;
    int profitPH;
    int totalProfit;
    int runesCost;
    int essenceCost;
    int talismanCost;
    int duelRingCost;
    int necklaceCost;
    int staminaPotCost;
    int materialRuneCost;
    int herbTypeID;
    int herbSeedID;
    int herbNoteID;
    Integer nextLocation;
    Boolean haveSkillsNecklace;
    Boolean travelingToLocation;
    boolean readyForNextTele = true;
    String currentPatchStatus = "";
    Boolean currentPatchDone = null;
    Integer previousPatchID = null;
    Boolean seedSelected;
    Boolean bucketSelected;


    Patch Patch_CurrentPatch;
    // Injects our config
    @Inject
    private KamiherbPluginconfig config;
    @Inject
    private Client client;
    @Inject
    private iUtils utils;
    @Inject
    private NPCUtils npc;
    @Inject
    private MouseUtils mouse;
    @Inject
    private PlayerUtils playerUtils;
    @Inject
    private BankUtils bank;
    @Inject
    private InventoryUtils inventory;
    @Inject
    private InterfaceUtils interfaceUtils;
    @Inject
    private CalculationUtils calc;
    @Inject
    private MenuUtils menu;
    @Inject
    private ObjectUtils object;
    @Inject
    private ConfigManager configManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KamiHerbOverlay overlay;
    @Inject
    private WalkUtils walk;
    // @Inject
    //private TimeTrackingConfig timeTrackingConfig;
    //@Inject
    //private TimeTrackingPlugin timeTrackingPlugin;
    @Inject
    private FarmingTracker farmingTracker;


    // Provides our config
    @Provides
    KamiherbPluginconfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(KamiherbPluginconfig.class);
    }


    @Override
    protected void startUp()
    {
        //chinBreakHandler.registerPlugin(this);


    }


    @Override
    protected void shutDown()
    {
        resetVals();
        //chinBreakHandler.unregisterPlugin(this);
    }


    private void resetVals()
    {
        log.info("stopping KamiHerb plugin");
//		chinBreakHandler.stopPlugin(this);
        startBot = false;
        botTimer = null;
        overlayManager.remove(overlay);
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("KamiherbPluginconfig"))
        {
            return;
        }
        log.info("button {} pressed!", configButtonClicked.getKey());
        if (configButtonClicked.getKey().equals("startButton"))
        {
            if (!startBot)
            {
                startBot = true;
//				chinBreakHandler.startPlugin(this);
                botTimer = Instant.now();
                initCounters();
                state = null;
                seedState = null;
                targetMenu = null;
                necklaceState = null;
                readyForNextTele = true;
                nextLocation = null;
                haveSkillsNecklace = null;
                travelingToLocation = false;
                currentPatchDone = false;
                previousPatchID = null;
                ignorePatch = false;
                seedSelected = false;
                bucketSelected = false;

                lastInspection = null;
                trollStronghold = config.trollStronghold();
                weiss = config.weiss();
                farmingGuild = config.farmingGuild();
                xeric = config.xeric();
                ardyCape = config.ardyCape();
                fallyRing = config.fallyRing();
                workingOnPatch = false;
                weedSelected = false;
                herbSelected = false;

                herbTypeID = config.getHerbType().getGrimyHerbID();
                herbNoteID = config.getHerbType().getNotedHerbID();
                herbSeedID = config.getHerbType().getSeedID();
                REQUIRED_ITEMS = List.of(herbSeedID, ItemID.SEED_DIBBER, ItemID.SPADE, ItemID.BOTTOMLESS_COMPOST_BUCKET_22997, ItemID.RAKE, ItemID.ECTOPHIAL);

                //updatePrices();
                botTimer = Instant.now();
                overlayManager.add(overlay);


                herbPatches.put(8152, new Integer[]{54, 54}); //Ardy 46,54??
                herbPatches.put(8153, new Integer[]{37, 49}); //Mory
                herbPatches.put(8150, new Integer[]{58, 63}); //Fally 58 71??
                herbPatches.put(33979, new Integer[]{38, 54}); //Guild
                herbPatches.put(27115, new Integer[]{42, 38}); //Glade 37,49??
                herbPatches.put(8151, new Integer[]{61, 71}); //Cath 61,63??
                herbPatches.put(18816, new Integer[]{42, 54}); //Troll
                herbPatches.put(33176, new Integer[]{48, 46}); //Weiss 56,46

            }
            else
            {
                resetVals();
            }
        }
    }

    @Subscribe
    private void onConfigChange(ConfigChanged event)
    {

        if (!event.getGroup().equals("iCombinationRunecrafter"))
        {
            return;
        }
        //TODO: Do this later
		/*
		switch (event.getKey())
		{
			case "getEssence":
				herbSeedID = config.getHerbType().getSeedID();
				essenceCost = (essenceTypeID != ItemID.DAEYALT_ESSENCE) ?
						utils.getOSBItem(essenceTypeID).getOverall_average() : 0;
				break;
			case "getRunecraftingType":
				herbTypeID = config.getRunecraftingType().getCreatedRuneID();
				talismanID = config.getRunecraftingType().getTalismanID();
				materialRuneID = config.getRunecraftingType().getMaterialRuneID();
				break;
		}
		setTalisman = false;
		REQUIRED_ITEMS = List.of(talismanID, materialRuneID, essenceTypeID);
		updatePrices();
		*/
    }

    private void initCounters()
    {
        timeout = 0;
    }


    private long sleepDelay()
    {
        sleepLength = calc.randomDelay(config.sleepWeightedDistribution(), config.sleepMin(), config.sleepMax(), config.sleepDeviation(), config.sleepTarget());
        return sleepLength;
    }

    private int tickDelay()
    {
        tickLength = (int) calc.randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        log.info("tick delay for {} ticks", tickLength);
        return tickLength;
    }


    private Patch PatchIDToPatch(int inputPatch)
    {
        farmingTracker.loadCompletionTimes();
        net.runelite.client.plugins.timetracking.farming.FarmingWorld farmingWorld = new FarmingWorld();


        Patch outputPatch = new Patch();


        String state = "";
        String name = "";
        String produce = "";
        for (Map.Entry<Tab, Set<net.runelite.client.plugins.timetracking.farming.FarmingPatch>> tab : farmingWorld.getTabs().entrySet())
        {
            long maxCompletionTime = 0;
            boolean allUnknown = true;
            boolean allEmpty = true;


            if (tab.getKey().getName() != "Herb Patches")
                continue;
            for (FarmingPatch patch : tab.getValue())
            {
                name = patch.getRegion().getName();
                if (inputPatch != PatchNameToID(name))
                {
                    continue;
                }

                PatchPrediction prediction = farmingTracker.predictPatch(patch);


                long unixNow = Instant.now().getEpochSecond();
                switch (prediction.getCropState())
                {
                    case HARVESTABLE:
                        state = "Done";
                        break;
                    case GROWING:
                        if (prediction.getDoneEstimate() < unixNow)
                        {
                            state = "Done";
                        }
                        else
                        {
                            state = "Done in x mins";
                        }
                        break;
                    case DISEASED:
                        state = "Diseased";
                        break;
                    case DEAD:
                        state = "Dead";
                        break;
                    case EMPTY:
                        state = "Empty";
                        break;
                    case FILLING:
                        state = "Filling";
                        break;
                }
                produce = prediction.getProduce().getName();

            }


        }
        outputPatch.setProduceName(produce);
        outputPatch.setPatchID(inputPatch);
        outputPatch.setProduceState(state);

        return outputPatch;
    }

    private Map<String, String> GetHerbStates()
    {
        farmingTracker.loadCompletionTimes();

        log.debug("Entered get herb states");
        net.runelite.client.plugins.timetracking.farming.FarmingWorld farmingWorld = new FarmingWorld();

        Map<String, String> output = new HashMap<>();
        for (Map.Entry<Tab, Set<net.runelite.client.plugins.timetracking.farming.FarmingPatch>> tab : farmingWorld.getTabs().entrySet())
        {
            long maxCompletionTime = 0;
            boolean allUnknown = true;
            boolean allEmpty = true;


            String state = "";
            String name = "";
            if (tab.getKey().getName() != "Herb Patches")
                continue;
            for (FarmingPatch patch : tab.getValue())
            {
                name = patch.getRegion().getName();

                PatchPrediction prediction = farmingTracker.predictPatch(patch);


                long unixNow = Instant.now().getEpochSecond();
                switch (prediction.getCropState())
                {
                    case HARVESTABLE:
                        state = "Done";
                        break;
                    case GROWING:
                        if (prediction.getDoneEstimate() < unixNow)
                        {
                            state = "Done";
                        }
                        else
                        {
                            state = "Done in x mins";
                        }
                        break;
                    case DISEASED:
                        state = "Diseased";
                        break;
                    case DEAD:
                        state = "Dead";
                        break;
                    case EMPTY:
                        state = "Empty";
                        break;
                    case FILLING:
                        state = "Filling";
                        break;
                }
                if (prediction.getProduce() == Produce.WEEDS)
                    state = "WEEDS";

                output.put(name, state);
            }

        }

        return output;
    }


    private void teleport_fallyRing()
    {
        targetMenu = new MenuEntry("", "", 2, 57, -1,
                25362455, false);

        Widget ringWidget = client.getWidget(WidgetInfo.EQUIPMENT_RING);
        if (ringWidget != null)
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(ringWidget.getBounds(), sleepDelay());
        }
        else
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(new Point(0, 0), sleepDelay());
        }
    }

    private void teleport_FarmingGuild()
    {
        targetMenu = new MenuEntry("", "", 7, 1007, -1,
                25362448, false);

        Widget amuletWidget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
        if (amuletWidget != null)
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(amuletWidget.getBounds(), sleepDelay());
        }
        else
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(new Point(0, 0), sleepDelay());
        }
    }

    private void teleport_FishingGuild()
    {
        targetMenu = new MenuEntry("", "", 2, 57, -1,
                25362448, false);

        Widget amuletWidget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
        if (amuletWidget != null)
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(amuletWidget.getBounds(), sleepDelay());
        }
        else
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(new Point(0, 0), sleepDelay());
        }
    }

    private void teleport_Ardy3()
    {
        targetMenu = new MenuEntry("", "", 3, 57, -1,
                25362447, false);

        Widget _widget = client.getWidget(WidgetInfo.EQUIPMENT_CAPE);
        if (_widget != null)
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(_widget.getBounds(), sleepDelay());
        }
        else
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(new Point(0, 0), sleepDelay());
        }
    }

    private void teleport_Xeric()
    {
        targetMenu = new MenuEntry("", "", 3, 57, -1,
                25362448, false);

        Widget amuletWidget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
        if (amuletWidget != null)
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(amuletWidget.getBounds(), sleepDelay());
        }
        else
        {
            menu.setEntry(targetMenu);
            mouse.delayMouseClick(new Point(0, 0), sleepDelay());
        }
    }

    private void teleport_Ectophial()
    {
        WidgetItem tp_item = inventory.getWidgetItem(ItemID.ECTOPHIAL);

        MenuEntry entry = new MenuEntry("", "", ItemID.ECTOPHIAL, 33,
                tp_item.getIndex(), WidgetInfo.INVENTORY.getId(), true);
        tp_item.getCanvasBounds().getBounds();
        Rectangle rectangle = tp_item.getCanvasBounds().getBounds();
        utils.doActionGameTick(entry, rectangle, tickDelay());
    }

    private void teleport_Camelot()
    {
        WidgetItem tp_item = inventory.getWidgetItem(ItemID.CAMELOT_TELEPORT);

        MenuEntry entry = new MenuEntry("", "", ItemID.CAMELOT_TELEPORT, 33,
                tp_item.getIndex(), 9764864, true);
        tp_item.getCanvasBounds().getBounds();
        Rectangle rectangle = tp_item.getCanvasBounds().getBounds();
        utils.doActionGameTick(entry, rectangle, tickDelay());
    }

    private void teleport_Falador()
    {
        WidgetItem tp_item = inventory.getWidgetItem(ItemID.FALADOR_TELEPORT);

        MenuEntry entry = new MenuEntry("", "", ItemID.FALADOR_TELEPORT, 33,
                tp_item.getIndex(), 9764864, true);
        tp_item.getCanvasBounds().getBounds();
        Rectangle rectangle = tp_item.getCanvasBounds().getBounds();
        utils.doActionGameTick(entry, rectangle, tickDelay());
    }

    private void teleport_StonyBasalt()
    {
        WidgetItem tp_item = inventory.getWidgetItem(ItemID.STONY_BASALT);

        MenuEntry entry = new MenuEntry("", "", ItemID.STONY_BASALT, 33,
                tp_item.getIndex(), 9764864, true);
        tp_item.getCanvasBounds().getBounds();
        Rectangle rectangle = tp_item.getCanvasBounds().getBounds();
        utils.doActionGameTick(entry, rectangle, tickDelay());
    }

    private void teleport_IcyBasalt()
    {
        WidgetItem tp_item = inventory.getWidgetItem(ItemID.ICY_BASALT);

        MenuEntry entry = new MenuEntry("", "", ItemID.ICY_BASALT, 33,
                tp_item.getIndex(), 9764864, true);
        tp_item.getCanvasBounds().getBounds();
        Rectangle rectangle = tp_item.getCanvasBounds().getBounds();
        utils.doActionGameTick(entry, rectangle, tickDelay());
    }


    private KamiHerbState getItemState(Set<Integer> itemIDs)
    {
        if (inventory.containsItem(itemIDs))
        {
            useableItem = inventory.getWidgetItem(itemIDs);
            return ACTION_ITEM;
        }
        if (bank.containsAnyOf(itemIDs))
        {
            bankItem = bank.getBankItemWidgetAnyOf(itemIDs);
            return WITHDRAW_ITEM;
        }
        return OUT_OF_ITEM;
    }

	/*
	private boolean shouldSipStamina()
	{
		return (config.staminaPotion() && client.getVar(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) == 0) &&
				(client.getEnergy() <= (75 - calc.getRandomIntBetweenRange(0, 40)) ||
						(inventory.containsItem(STAMINA_POTIONS) && client.getEnergy() < 75));
	}
	 */

    //TODO: -------------------------------------------------------

    private KamiHerbState getRequiredItemState()
    {
        if ((!inventory.containsItem(herbSeedID) && !bank.contains(herbSeedID, 1))
            //||
            //(!inventory.containsItem(materialRuneID) && !bank.contains(materialRuneID, 26)) ||
            //(!inventory.containsItem(essenceTypeID) && !bank.contains(essenceTypeID, 10))
        )
        {
            bankItem = null;
            return OUT_OF_ITEM;
        }
        for (int itemID : REQUIRED_ITEMS)
        {
            if (!inventory.containsItem(itemID))
            {
                bankItem = bank.getBankItemWidget(itemID);
                int[] multiItems = {ItemID.ICY_BASALT, ItemID.STONY_BASALT, herbSeedID};
                for (int multiID : multiItems)
                {
                    if (multiID == itemID)
                        return WITHDRAW_ALL_ITEM;
                }
                return WITHDRAW_ITEM;
            }
        }
        return OUT_OF_ITEM;
    }


    private KamiHerbState getState()
    {
        if (timeout > 0)
        {
            playerUtils.handleRun(20, 30);
            return TIMEOUT;
        }
        if (iterating)
        {
            return ITERATING;
        }
        if (playerUtils.isMoving(beforeLoc) || player.getAnimation() == 714) //teleport animation
        {
            playerUtils.handleRun(20, 30);
            return MOVING;
        }


        int[] herbPatchIDs = new int[herbPatches.keySet().size()];
        int i = 0;
        for (Integer x : herbPatches.keySet())
            herbPatchIDs[i++] = x;


        herbPatch = object.findNearestGameObject(herbPatchIDs);

        if (herbPatch != null)
        {
            if (previousPatchID == null)
            {
                Patch_CurrentPatch = PatchIDToPatch(herbPatch.getId());
            }
            else if (herbPatch.getId() != previousPatchID)
            {
                previousPatchID = herbPatch.getId();
                Patch_CurrentPatch = PatchIDToPatch(herbPatch.getId());
            }

        }
        else
        {
            Patch_CurrentPatch = null;
        }
        //if current patch is the same as previous patch, dont change objects


        if (client.getWidget(WidgetID.BANK_PIN_GROUP_ID, BANK_PIN_INSTRUCTION_TEXT.getChildId()) != null)
        {
            return ENTER_PIN;
        }

        else if ((Patch_CurrentPatch != null && !Patch_CurrentPatch.getProduceState().equals("Done in x mins")
                && !Patch_CurrentPatch.getProduceState().equals("Diseased")
                || ((Patch_CurrentPatch != null && !Patch_CurrentPatch.ProduceIsHerb())
                ||inventory.containsItem(ItemID.WEEDS)
                || inventory.containsItem(herbTypeID))))
        {
            Debug(String.format("1- Entered main picking loop with Currentpatch = %s, produce state = %s, invi has weeds = %s, invi has a herb = %s",Patch_CurrentPatch.getPatchID(), Patch_CurrentPatch.getProduceState(),  inventory.containsItem(ItemID.WEEDS),inventory.containsItem(herbTypeID)));
            if (Patch_CurrentPatch.ProduceIsHerb())
            {
                Debug("2- Produce is herb");
                if (Patch_CurrentPatch.getProduceState().equals("Dead"))
                {
                    Debug("3- produce is dead");
                    lastInspection = null;
                    return REMOVE_DEAD;
                }
                else if (Patch_CurrentPatch.getProduceState().equals("Diseased"))
                {
                    Debug("4- produce is diseased");
                    return HANDLE_DISEASE;
                }
                else if (Patch_CurrentPatch.getProduceState().equals("Done"))
                {
                    Debug("5- produce is done");
                    return PICK_HERB;
                }
                else if (Patch_CurrentPatch.getProduceState().equals("Done in x mins"))
                {
                    Debug("6- produce will be done in x mins");
                    if (inventory.containsItem(ItemID.WEEDS))
                    {
                        Debug("7- inventory has weeds");
                        return DROP_WEEDS;
                    }
                    else if (inventory.containsItem(herbTypeID))
                    {
                        Debug("8- inventory has herb");
                        if (herbSelected)
                        {
                            Debug("9- note herb");
                            nextLocation = null;
                            return NOTE_HERBS;
                        }
                        else
                        {
                            Debug("10- select herb");
                            return SELECT_HERBS;
                        }

                    }
                }
            }
            else
            {
                Debug("11- produce is not herb");
                if (lastInspection == null)
                {
                    Debug("12- last inspection was null, inspecting");
                    return INSPECT;
                }
                else if (lastInspection.getIsWeeded())
                {
                    Debug("13- last inspection was weeded");
                    if (lastInspection.getIsTreated())
                    {
                        Debug("14- last inspection was treated");
                        if (lastInspection.getIsEmpty())
                        {
                            Debug("15- last inspection was empty");
                            if (seedSelected)
                            {
                                Debug("16- seed is selected, planting seed");
                                seedSelected = false;
                                lastInspection = null;
                                return PLANT_SEED;
                            }
                            else
                            {
                                Debug("17- selectng seed");
                                seedSelected = true;
                                return SELECT_SEED;
                            }

                        }
                    }
                    else
                    {
                        Debug("18- last inspeciton was not treated");
                        if (bucketSelected)
                        {
                            Debug("19- bucket selected, treating");
                            bucketSelected = false;
                            lastInspection = null;
                            return COMPOST_PATCH;
                        }
                        else
                        {
                            Debug("20- select bucket");
                            bucketSelected = true;
                            return SELECT_BUCKET;
                        }

                    }
                }
                else
                {
                    Debug("21- last inspection was not weeded, weeding");
                    lastInspection = null;
                    return RAKE_WEEDS;
                }


            }
            Debug("22- Reached end of main picking loop");

        }
        else// if (Patch_CurrentPatch == null)
        {
            //Debug(String.format("0) failed main picking loop with Currentpatch = %s, produce state = %s, invi has weeds = %s, invi has a herb = %s",Patch_CurrentPatch.getPatchID(), Patch_CurrentPatch.getProduceState(),  inventory.containsItem(ItemID.WEEDS),inventory.containsItem(herbTypeID)));
            if (nextLocation == null)
            {
                travelingToLocation = false;
                Debug("1) Next Location was Null");
                //if have not decided next location
                return NEXT_LOCATION;
            }
            else
            {
                Debug("2) Next location was not null : " + nextLocation);
                //decided next location
                if (nextLocation == 8152 || nextLocation == 33979)
                {
                    Debug("3) Next location was either 8152 or 33979");
                    //if next location is Ardy or Guild
                    if (!travelingToLocation)
                    {
                        Debug("4) traveling to location was false");
                        if (bank.isOpen() && (playerUtils.isItemEquipped(SKILL_NECKLACES) || inventory.containsItem(SKILL_NECKLACES)))
                        {
                            Debug("5) Need to close bank since bank is open, invi has or is equipped skills neck");
                            return CLOSE_BANK;
                        }
                        else
                        {
                            Debug("6) Bank was closed and no necklace found");
                            //if not moving to herb patch at Ardy or Guild
                            if (playerUtils.isItemEquipped(SKILL_NECKLACES))
                            {
                                Debug("7) Skills necklace ready to use");
                                //if skills necklace is ready to use
                                return USE_SKILLS_NECKLACE;
                            }
                            else if (inventory.containsItem(SKILL_NECKLACES))
                            {
                                Debug("8) need to equip skills necklace");
                                //if skills necklace is in inventory
                                return EQUIP_SKILLS_NECKLACE;
                            }
                            else if (playerUtils.isItemEquipped(SPENT_NECKLACES))
                            {
                                Debug("9) skills necklace ran out and is equipped");
                                //if spent necklace is equipped
                                return UNEQUIP_SKILLS_NECKLACE;
                            }
                            else
                            {
                                Debug("10) skills necklace not equipped");
                                bankChest = object.findNearestGameObject(ObjectID.BANK_BOOTH_25808);

                                NPC theWedge = npc.findNearestNpc(NpcID.THE_WEDGE);
                                log.debug("11) The wedge is " + theWedge);
                                //5517
                                if (theWedge == null && bankChest == null)
                                {
                                    Debug("12) Didnt find wedge or bankchest, going to camelot");
                                    return TELEPORT_CAMELOT;
                                }
                                else
                                {
                                    Debug("13) found bank chest or wedge");
                                    if (bankChest == null)
                                    {
                                        Debug("14) didnt find bank chest");
                                        return GO_TO_BANK;
                                    }
                                    else
                                    {
                                        Debug("15) found bank chest");
                                        if (bank.isOpen())
                                        {
                                            Debug("16) bank is open");
                                            if (inventory.containsItem(SPENT_NECKLACES))
                                            {
                                                Debug("17) have a spent necklace, need to deposit");
                                                //if a spent necklace is in invi
                                                return DEPOSIT_SKILLS_NECKLACE;
                                            }
                                            else
                                            {
                                                Debug("18) withdraw new necklace");
                                                //if no skills necklace available
                                                return WITHDRAW_SKILLS_NECKLACE;
                                            }
                                        }
                                        else
                                        {
                                            Debug("19) bank foudn but not open, open it");
                                            return OPEN_BANK;
                                        }
                                    }
                                }
                            }


                        }
                    }
                    else
                    {
                        Debug("20) Moving to patch location");
                        ignorePatch = false;
                        return GO_TO_LOCATION;
                        //is moving to herb patch location
                    }

                }
                else if (nextLocation == 27115)
                {
                    Debug("21) next location is 27115");
                    //if next location is Ardy or Guild
                    if (!travelingToLocation)
                    {
                        Debug("22) not moving to location");


                        //if not moving to herb patch at Ardy or Guild
                        if (playerUtils.isItemEquipped(Set.of(ItemID.XERICS_TALISMAN)))
                        {
                            Debug("23) xeric equipped and ready to use");
                            //if skills necklace is ready to use
                            return TELEPORT_KOUREND;
                        }
                        else if (inventory.containsItem(Set.of(ItemID.XERICS_TALISMAN)))
                        {
                            Debug("24) need to equip xeric");
                            //if skills necklace is in inventory
                            return EQUIP_XERIC;
                        }


                    }
                    else
                    {
                        Debug("25) moving to location");
                        ignorePatch = false;
                        return GO_TO_LOCATION;
                        //is moving to herb patch location
                    }
                }


                else
                {
                    Debug("26) going to a locaiton other than farming guild / ardy");
                    //going to unlimited teleport location
                    //maybe make sure we have the teleport in invi?
                    if (!travelingToLocation)
                    {
                        Debug("27) not moving to location, picking teleport");

                        travelingToLocation = true;
                        switch (nextLocation)
                        {
                            case 8152: //Ardy
                                return TELEPORT_ARDY;

                            case 8151://Cath
                                return TELEPORT_CAMELOT;

                            case 8150: //Fally
                                return TELEPORT_FALADOR;

                            case 33979: //Farming guild
                                return TELEPORT_FARMING_GUILD;

                            case 27115: //Kourend
                                return TELEPORT_KOUREND;

                            case 8153: //Mory
                                return TELEPORT_MORYTANIA;

                            case 18816: //Troll
                                return TELEPORT_TROLL;

                            case 33176: //Weiss
                                return TELEPORT_WEISS;
                        }
                    }
                    else
                    {
                        Debug("28) moving to location");
                        ignorePatch = false;
                        return GO_TO_LOCATION;
                    }

                }


            }


        }
        Debug("9999) reached end of function, bug");
        return OUT_OF_AREA;
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (!startBot/* || chinBreakHandler.isBreakActive(this)*/)
        {
            return;
        }
        player = client.getLocalPlayer();
        if (client != null && player != null && client.getGameState() == GameState.LOGGED_IN)
        {
            if (!client.isResized())
            {
                utils.sendGameMessage("KamiHerb - client must be set to resizable");
                startBot = false;
                return;


            }

            Map<String, String> herbPatchStatus = new HashMap<>();
            herbPatchStatus = GetHerbStates();



            if (!config.farmingGuild())
                herbPatchStatus.remove("Farming Guild");
            if (!config.xeric())
                herbPatchStatus.remove("Kourend");
            if (!config.trollStronghold())
                herbPatchStatus.remove("Troll Stronghold");
            if (!config.weiss())
                herbPatchStatus.remove("Weiss");
            if(!config.harmony())
                herbPatchStatus.remove("Harmony");


            doneHerbPatches.clear();
            for (String key : herbPatchStatus.keySet())
            {
                if (herbPatchStatus.get(key).equals("Done")
                        || herbPatchStatus.get(key).equals("Dead")
                        || herbPatchStatus.get(key).equals("WEEDS"))
                {
                    //log.debug("Adding " + key + "with id " + PatchNameToID(key) + " to done herb patches");
                    doneHerbPatches.add(PatchNameToID(key));
                }
            }


            state = getState();
            log.debug(state.name());



            switch (state)
            {
                case TIMEOUT:
                    timeout--;
                    break;
                case ITERATING:
                    break;
                case MOVING:
                    timeout = tickDelay();
                    break;

                case NEXT_LOCATION:
                    if (doneHerbPatches.size() == 0)
                    {
                        //Out of patches to do
                        log.debug("Out of patches to TP to");
                        nextLocation = null;
                    }
                    else
                    {

                        int nextPatch = doneHerbPatches.get(0);
                        log.debug("Next patch is : " + PatchIdToname(nextPatch) + "with ID :" + nextPatch);
                        //nextPatch = 8152;
                        nextLocation = nextPatch;
                    }
                    timeout = tickDelay() * 5;
                    break;
                case USE_SKILLS_NECKLACE:
                    if (nextLocation == 8152)
                    {
                        targetMenu = new MenuEntry("", "", 2,57, -1,
                                25362448, true);
                    }
                    else
                    {

                        targetMenu = new MenuEntry("", "", 7, 1007, -1,
                                25362448, true);
                    }

                    Widget ringWidget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(ringWidget.getBounds(), sleepDelay());
                    timeout = tickDelay() * 5;
                    travelingToLocation = true;
                    break;
                case EQUIP_SKILLS_NECKLACE:
                    WidgetItem currNeck = inventory.getWidgetItem(SKILL_NECKLACES);
                    targetMenu = new MenuEntry("Wear", "", currNeck.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(),
                            currNeck.getIndex(), 9764864, false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(currNeck.getCanvasBounds(), sleepDelay());
                    //WorldPoint walkerDest = new WorldPoint(2670, 3376, 0);
                    //walk.webWalk(walkerDest , 2, playerUtils.isMoving(beforeLoc), sleepDelay());
                    timeout = tickDelay();
                    break;
                case GO_TO_BANK:
                    WorldPoint walkerDest = new WorldPoint(2725, 3492, 0);
                    walk.webWalk(walkerDest, 1, playerUtils.isMoving(beforeLoc), sleepDelay());
                    timeout = tickDelay();
                    break;
                case WITHDRAW_SKILLS_NECKLACE:
                    bankItem = bank.getBankItemWidgetAnyOf(SKILL_NECKLACES);

                    targetMenu = new MenuEntry("Withdraw-1", "<col=ff9040>Skills necklace(6)</col>",
                            1, 57, bankItem.getIndex(),
                            786444, false);

                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(bankItem.getBounds(), sleepDelay());
                    timeout = tickDelay();
                    break;
                case UNEQUIP_SKILLS_NECKLACE:
                    targetMenu = new MenuEntry("Remove", "", 1, MenuOpcode.CC_OP.getId(), -1,
                            25362448, false);
                    Widget unequipWidget = client.getWidget(WidgetInfo.EQUIPMENT_AMULET);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(unequipWidget.getBounds(), sleepDelay());
                    timeout = tickDelay();
                    break;
                case DEPOSIT_SKILLS_NECKLACE:
                    WidgetItem invSpentNecklace = inventory.getWidgetItem(SPENT_NECKLACES);
                    targetMenu = new MenuEntry("Depoist-1", "", 2, MenuOpcode.CC_OP.getId(), invSpentNecklace.getIndex(),
                            983043, false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(invSpentNecklace.getCanvasBounds(), sleepDelay());
                    timeout = tickDelay();
                    break;

                case OPEN_BANK:
                    targetMenu = new MenuEntry("Bank", "<col=ffff>Bank booth", bankChest.getId(), 4,
                            bankChest.getSceneMinLocation().getX(), bankChest.getSceneMinLocation().getY(), false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(bankChest.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();
                    return;
                case CLOSE_BANK:
                    bank.close();
                    timeout = tickDelay();

                    break;
                case TELEPORT_CAMELOT:
                    teleport_Camelot();

                    timeout = tickDelay()* 5;
                    break;
                case TELEPORT_MORYTANIA:
                    teleport_Ectophial();
                    timeout = tickDelay() *5;
                    break;
                case TELEPORT_TROLL:
                    teleport_StonyBasalt();
                    timeout = tickDelay() * 5;
                    break;
                case TELEPORT_WEISS:
                    teleport_IcyBasalt();
                    timeout = tickDelay() * 5;
                    break;

                case TELEPORT_FALADOR:
                    if (config.fallyRing())
                    {
                        teleport_fallyRing();
                    }
                    else
                    {
                        teleport_Falador();
                    }
                    timeout = tickDelay() * 5;
                    break;

                case EQUIP_XERIC:
                    WidgetItem xericNeck = inventory.getWidgetItem(ItemID.XERICS_TALISMAN);
                    targetMenu = new MenuEntry("Wear", "", xericNeck.getId(), MenuOpcode.ITEM_SECOND_OPTION.getId(),
                            xericNeck.getIndex(), 9764864, false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(xericNeck.getCanvasBounds(), sleepDelay());
                    //WorldPoint walkerDest = new WorldPoint(2670, 3376, 0);
                    //walk.webWalk(walkerDest , 2, playerUtils.isMoving(beforeLoc), sleepDelay());
                    timeout = tickDelay() * 5;
                    break;
                case TELEPORT_KOUREND:
                    teleport_Xeric();
                    timeout = tickDelay() * 5;

                    break;
                case USE_TELEPORTER:

                    break;


                case GO_TO_LOCATION:
                    WorldPoint herbLocation = null;


                    switch (nextLocation)
                    {
                        case 8152: //Ardy
                            herbLocation = new WorldPoint(2664, 3375, 0);
                            break;

                        case 8151://Cath
                            herbLocation = new WorldPoint(2807, 3463, 0);
                            //TODO: House Teleport
                            //Pop Camelot stone
                            //Walk to herb patch
                            break;

                        case 8150: //Fally
                            herbLocation = new WorldPoint(3055, 3306, 0);
                            //If have RING
                            //TP via ring
                            //If NO ring
                            //Pop falador teleport
                            //walk to patch
                            break;

                        case 33979: //Farming guild
                            herbLocation = new WorldPoint(1237, 3728, 0);
                            //equip necklace
                            //pop necklace
                            break;

                        case 27115: //Kourend
                            herbLocation = new WorldPoint(1740, 3553, 0);
                            //equip necklace
                            //pop necklace
                            //walk to patch
                            break;

                        case 8153: //Mory
                            herbLocation = new WorldPoint(3608, 3532, 0);
                            //pop ecto
                            //walk to patch
                            break;

                        case 18816: //Troll
                            herbLocation = new WorldPoint(2830, 3693, 0);
                            //TODO: House teleport
                            //pop stony basalt
                            break;

                        case 33176: //Weiss
                            herbLocation = new WorldPoint(2846, 3935, 0);
                            //TODO: House teleport
                            //pop icy basalt
                            break;
                    }
                    walk.webWalk(herbLocation, 2, playerUtils.isMoving(beforeLoc), sleepDelay());
                    timeout = tickDelay();
                    currentPatchDone = false;
                    break;

                case RAKE_WEEDS:
                    targetMenu = new MenuEntry("Rake", "<col=ffff>Herb patch",
                            herbPatch.getId(), 3, herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), true);


                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay() * 5;
                    break;
                case PICK_HERB:
                    targetMenu = new MenuEntry("Pick", "<col=ffff>Herbs",
                            herbPatch.getId(), 3, herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), true);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();

                    break;
                case REMOVE_DEAD:
                    targetMenu = new MenuEntry("Clear", "<col=ffff>Dead herbs",
                            herbPatch.getId(), 3, herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), true);


                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();

                    break;
                case INSPECT:
                    targetMenu = new MenuEntry("Inspect", "",
                            herbPatch.getId(), 4, herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), true);


                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();
                    break;

                case SELECT_HERBS:
                    WidgetItem herb = inventory.getWidgetItem(herbTypeID);
                    targetMenu = new MenuEntry("Use", "Use", herbTypeID, MenuOpcode.ITEM_USE.getId(),
                            herb.getIndex(), 9764864, true);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herb.getCanvasBounds(), sleepDelay());
                    timeout = tickDelay();
                    herbSelected = true;

                    break;

                case NOTE_HERBS:
                    NPC leprechaun = npc.findNearestNpc(0, 757);
                    if (leprechaun == null)
                    {
                        log.debug("No little green boys around");
                        return;
                    }

                    String herbString = "<col=ff9040>Grimy snapdragon<col=ffffff> -> <col=ffff00>Tool Leprechaun";
                    if (herbSeedID == ItemID.RANARR_SEED)
                        herbString = "<col=ff9040>Grimy ranarr weed<col=ffffff> -> <col=ffff00>Tool Leprechaun";


                    targetMenu = new MenuEntry("Use", herbString,
                            leprechaun.getIndex(), MenuOpcode.ITEM_USE_ON_NPC.getId(),
                            0, 0, true);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(leprechaun.getConvexHull().getBounds(), sleepDelay());
                    herbSelected = false;
                    timeout = tickDelay();

                    break;
                case SELECT_SEED:
                    WidgetItem herbSeed = inventory.getWidgetItem(herbSeedID);
                    targetMenu = new MenuEntry("Use", "Use", herbSeedID, MenuOpcode.ITEM_USE.getId(),
                            herbSeed.getIndex(), 9764864, false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbSeed.getCanvasBounds(), sleepDelay());
                    timeout = tickDelay();
                    seedSelected = true;
                    break;
                case PLANT_SEED:
                    String seedString = "<col=ff9040>Snapdragon seed<col=ffffff> -> <col=ffff>Herb patch";
                    if (herbSeedID == ItemID.RANARR_SEED)
                        seedString = "<col=ff9040>Ranarr seed<col=ffffff> -> <col=ffff>Herb patch";
                    targetMenu = new MenuEntry("Use", seedString,
                            herbPatch.getId(), MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId(), herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), true);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();
                    seedSelected = false;
                    break;
                case SELECT_BUCKET:
                    WidgetItem fert = inventory.getWidgetItem(ItemID.BOTTOMLESS_COMPOST_BUCKET_22997);
                    targetMenu = new MenuEntry("Use", "Use", ItemID.BOTTOMLESS_COMPOST_BUCKET_22997, MenuOpcode.ITEM_USE.getId(),
                            fert.getIndex(), 9764864, true);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(fert.getCanvasBounds(), sleepDelay());
                    bucketSelected = true;
                    timeout = tickDelay();
                    break;
                case COMPOST_PATCH:
                    String fertString = "<col=ff9040>Bottomless compost bucket<col=ffffff> -> <col=ffff>Herbs";

                    targetMenu = new MenuEntry("Use", fertString,
                            herbPatch.getId(), MenuOpcode.ITEM_USE_ON_GAME_OBJECT.getId(), herbPatch.getSceneMinLocation().getX(),
                            herbPatch.getSceneMinLocation().getY(), false);
                    menu.setEntry(targetMenu);
                    mouse.delayMouseClick(herbPatch.getConvexHull().getBounds(), sleepDelay());
                    timeout = tickDelay();
                    bucketSelected = false;
                    break;
                case DROP_WEEDS:
                    if (inventory.containsItem(ItemID.WEEDS))
                        inventory.dropItems(Collections.singleton(ItemID.WEEDS), true, config.sleepMin(), config.sleepMax());
                    timeout = tickDelay();
                    break;
                case HANDLE_DISEASE:
                    break;

                /*


                 */
            }
            beforeLoc = player.getLocalLocation();
        }

    }


    private String PatchIdToname(int id)
    {
        switch (id)
        {
            case 8152:
                return "Ardougne";

            case 8151:
                return "Catherby";

            case 8150:
                return "Falador";

            case 33979:
                return "Farming Guild";

            case 27115:
                return "Kourend";

            case 8153:
                return "Morytania";

            case 18816:
                return "Troll Stronghold";

            case 33176:
                return "Weiss";

            default:
                return "DEFAULT";
        }
    }

    private int PatchNameToID(String s)
    {
        switch (s)
        {
            case "Ardougne":
                return 8152;

            case "Catherby":
                return 8151;

            case "Falador":
                return 8150;

            case "Farming Guild":
                return 33979;

            case "Kourend":
                return 27115;

            case "Morytania":
                return 8153;

            case "Troll Stronghold":
                return 18816;

            case "Weiss":
                return 33176;

            default:
                return -1;

        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        if (!startBot)
        {
            return;
        }
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            setTalisman = false;
            state = TIMEOUT;
            timeout = 2;
        }
    }
    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
            if (event.getType() == ChatMessageType.GAMEMESSAGE)
            {
                String message = event.getMessage();
                if (message.contains("herb patch"))
                {
                    InspectionResult result = new InspectionResult();
                    if (message.contains("The soil has been treated with ultracompost"))
                    {
                        result.setIsTreated(true);
                    }
                    else
                    {
                        result.setIsTreated(false);
                    }

                    if (message.contains("weeded"))
                    {
                        result.setIsWeeded(true);
                    }
                    else
                    {
                        result.setIsWeeded(false);
                    }

                    if (message.contains("patch is empty"))
                    {
                        result.setIsEmpty(true);
                    }
                    else
                    {
                        result.setIsEmpty(false);
                    }

                    lastInspection = result;
                }
            }
        }

        void Debug(String str)
        {
            if (DO_FULL_DEBUG)
            {
                log.debug(str);
            }
        }

    }


