package glenn.gasesframework;

import java.io.File;

import glenn.gasesframework.api.GFAPI;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.ExtendedGasEffectsBase;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.ConfigGasFurnaceRecipes;
import glenn.gasesframework.common.CreativeTab;
import glenn.gasesframework.common.GasBottleTransposerHandler;
import glenn.gasesframework.common.GuiHandler;
import glenn.gasesframework.common.block.BlockGasPipe;
import glenn.gasesframework.common.gastype.GasTypeFire;
import glenn.gasesframework.common.reaction.ReactionBurnEntity;
import glenn.gasesframework.common.reaction.ReactionEntityIgnition;
import glenn.gasesframework.common.reaction.ReactionNoisyPeople;
import glenn.gasesframework.init.GFBlocks;
import glenn.gasesframework.common.configuration.GasesFrameworkMainConfigurations;
import glenn.gasesframework.common.entity.EntityDelayedExplosion;
import glenn.gasesframework.init.GFItems;
import glenn.gasesframework.common.pipetype.PipeTypeGlass;
import glenn.gasesframework.common.pipetype.PipeTypeIron;
import glenn.gasesframework.common.pipetype.PipeTypeWood;
import glenn.gasesframework.common.reaction.ReactionCommonIgnition;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasDrain;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasPump;
import glenn.gasesframework.common.tileentity.TileEntityIronGasCollector;
import glenn.gasesframework.common.tileentity.TileEntityIronGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityIronGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityIronGasPump;
import glenn.gasesframework.common.tileentity.TileEntityIronGasTank;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasCollector;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasPump;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasTank;
import glenn.gasesframework.common.worldgen.WorldGeneratorGasesFramework;
import glenn.gasesframework.network.message.MessageDuctTapeGag;
import glenn.gasesframework.network.message.MessageGasEffects;
import glenn.gasesframework.network.message.MessageSetBlockGasTypeFilter;
import glenn.gasesframework.network.message.MessageSetTransposerMode;
import glenn.gasesframework.waila.GasesFrameworkWaila;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * <b>Gases Framework</b>
 * <br>
 * Gases Framework provides support for simplified implementation of gases in Minecraft.
 * <br>
 * <br>
 * This piece of software is covered under the LGPL license. Redistribution and modification of this mod is permitted.
 * It would be nice if you let us know if you do redistribute/modify it: 
 * http://www.jamieswhiteshirt.com/minecraft/mods/gases/
 * @author Glenn
 * @author Trent
 *
 */
@Mod(modid = GasesFramework.MODID, name = "Gases Framework", version = GasesFramework.VERSION, dependencies="required-after:gasesFrameworkCore", acceptedMinecraftVersions = "[" + GasesFramework.TARGETVERSION + "]")
public class GasesFramework
{
	// The instance of your mod that Forge uses.
	@Instance(GasesFramework.MODID)
	public static GasesFramework instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "glenn.gasesframework.client.ClientProxy", serverSide = "glenn.gasesframework.server.ServerProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = GFAPI.OWNER;
	public static final String VERSION = "1.2.0";
	public static final String TARGETVERSION = GFAPI.TARGETVERSION;

	public static Logger logger;
	public static final GuiHandler guiHandler = new GuiHandler();
	public static SimpleNetworkWrapper networkWrapper = new SimpleNetworkWrapper(MODID);
	public static GasesFrameworkMainConfigurations configurations;
	public static final CreativeTabs creativeTab = new CreativeTab("tabGasesFramework");
	public static final WorldGeneratorGasesFramework worldGenerator = new WorldGeneratorGasesFramework();

	public static GFItems items;
	public static GFBlocks blocks;

	public static final PipeType pipeTypeIron = new PipeTypeIron(0, "iron", true, "gasesframework:pipe_iron");
	public static final PipeType pipeTypeGlass = new PipeTypeGlass(1, "glass", false, "gasesframework:pipe_glass");
	public static final PipeType pipeTypeWood = new PipeTypeWood(2, "wood", true, "gasesframework:pipe_wood");

	public static final GasType gasTypeSmoke = new GasType(true, 1, "smoke", 0x3F3F3F9F, 2, -16, Combustibility.NONE).setCreativeTab(creativeTab)
			.setEffectRate(ExtendedGasEffectsBase.EffectType.BLINDNESS, 4)
			.setEffectRate(ExtendedGasEffectsBase.EffectType.SUFFOCATION, 4)
			.setEffectRate(ExtendedGasEffectsBase.EffectType.SLOWNESS, 16);
	public static final GasType gasTypeFire = new GasTypeFire().setCreativeTab(creativeTab);

	public static final LanternType lanternTypeEmpty = new LanternType("empty", 0.0f, "gasesframework:lantern_empty", new ItemKey(), null, 0).setCreativeTab(creativeTab);
	public static final LanternType lanternTypeGasEmpty = new LanternType("gas_empty", 0.0f, "gasesframework:lantern_gas_empty", new ItemKey(Items.glass_bottle), lanternTypeEmpty, 0).setCreativeTab(creativeTab);
	public static final LanternType[] lanternTypesGas = new LanternType[] {
			lanternTypeGasEmpty,
			new LanternType("gas_1", 1.0f, "gasesframework:lantern_gas_1", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 1),
			new LanternType("gas_2", 1.0f, "gasesframework:lantern_gas_2", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 2),
			new LanternType("gas_3", 1.0f, "gasesframework:lantern_gas_3", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 3),
			new LanternType("gas_4", 1.0f, "gasesframework:lantern_gas_4", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 4),
			new LanternType("gas_5", 1.0f, "gasesframework:lantern_gas_5", new ItemKey(Items.glass_bottle), lanternTypeGasEmpty, 5)
	};



	public static final Implementation implementation = new Implementation();
	public static final Registry registry = new Registry();

	private void initBlocksAndItems()
	{
		items = new GFItems();
		blocks = new GFBlocks();

		registry.registerLanternType(lanternTypeEmpty);
		for (LanternType lanternType : lanternTypesGas)
		{
			registry.registerLanternType(lanternType);
		}
		
		registry.registerGasType(GFAPI.gasTypeAir.setCreativeTab(creativeTab));
		registry.registerGasType(gasTypeSmoke);
		registry.registerGasType(gasTypeFire);
		
		registry.registerPipeType(pipeTypeIron);
		registry.registerPipeType(pipeTypeGlass);
		registry.registerPipeType(pipeTypeWood);

		registry.getGasPipeBlock(GFAPI.gasTypeAir).setCreativeTab(creativeTab);
	}

	private void initRecipes()
	{
		BlockGasPipe pipeBlock = registry.getGasPipeBlock(GFAPI.gasTypeAir);
		ItemStack pipeIron = new ItemStack(pipeBlock, 1, 0);
		ItemStack pipeWood = new ItemStack(pipeBlock, 1, 2);

		GameRegistry.addRecipe(new ItemStack(registry.getLanternBlock(lanternTypeEmpty), 4), "I", "G", 'I', Items.iron_ingot, 'G', Blocks.glass);
		GameRegistry.addRecipe(new ItemStack(pipeBlock, 24), "III", 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(pipeBlock, 24, 1), "GGG", "III", "GGG", 'I', Items.iron_ingot, 'G', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(pipeBlock, 24, 2), " D ", "LLL", " D ", 'L', Blocks.log, 'D', items.ductTape);
		GameRegistry.addRecipe(new ItemStack(blocks.ironGasPump), " I ", "PRP", " I ", 'I', Items.iron_ingot, 'P', pipeIron, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(blocks.woodGasPump), " W ", "PDP", " W ", 'W', Blocks.planks, 'P', pipeWood, 'D', items.ductTape);
		GameRegistry.addRecipe(new ItemStack(blocks.gasCollector), " P ", "PUP", " P ", 'U', blocks.ironGasPump, 'P', pipeIron);
		GameRegistry.addRecipe(new ItemStack(blocks.woodGasCollector), "DPD", "PUP", "DPD", 'U', blocks.woodGasPump, 'P', pipeWood, 'D', items.ductTape);
		GameRegistry.addRecipe(new ItemStack(blocks.gasTank), "IPI", "P P", "IPI", 'I', Items.iron_ingot, 'P', pipeIron);
		GameRegistry.addRecipe(new ItemStack(blocks.woodGasTank), "WPW", "P P", "WPW", 'W', Blocks.planks, 'P', pipeWood);
		GameRegistry.addRecipe(new ItemStack(blocks.gasFurnaceIdle), " P ", "PFP", " P ", 'P', pipeIron, 'F', Blocks.furnace);
		GameRegistry.addRecipe(new ItemStack(blocks.woodGasFurnaceIdle), "DPD", "PLP", "DPD", 'P', pipeWood, 'L', Blocks.log, 'D', items.ductTape);
		GameRegistry.addRecipe(new ItemStack(blocks.ironGasDynamo), " R ", "RFR", " R ", 'R', Items.redstone, 'F', blocks.gasFurnaceIdle);
		GameRegistry.addRecipe(new ItemStack(blocks.woodGasDynamo), " R ", "RFR", " R ", 'R', Items.redstone, 'F', blocks.woodGasFurnaceIdle);
		GameRegistry.addRecipe(new ItemStack(blocks.gasTransposer), " P ", "PHP", " P ", 'P', pipeIron, 'H', Blocks.hopper);
		GameRegistry.addShapelessRecipe(new ItemStack(items.gasSamplerExcluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(items.gasSamplerIncluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 15));
		GameRegistry.addShapelessRecipe(new ItemStack(items.adhesive), new ItemStack(Items.water_bucket), Items.rotten_flesh, Items.sugar);
		GameRegistry.addShapelessRecipe(new ItemStack(items.adhesive), Items.potato, Items.wheat, Blocks.cobblestone);
		GameRegistry.addShapelessRecipe(new ItemStack(items.ductTape, 2), items.adhesive, Items.string, Items.string);
		GameRegistry.addRecipe(new ItemStack(items.ductTape, 32), "SSS", "SAS", "SSS", 'S', Items.string, 'A', items.adhesive);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		GFAPI.install(implementation, registry);

		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerMessage(MessageGasEffects.Handler.class, MessageGasEffects.class, 0);
		proxy.registerMessage(MessageSetTransposerMode.Handler.class, MessageSetTransposerMode.class, 1);
		proxy.registerMessage(MessageSetBlockGasTypeFilter.Handler.class, MessageSetBlockGasTypeFilter.class, 2);
		proxy.registerMessage(MessageDuctTapeGag.Handler.class, MessageDuctTapeGag.class, 3);

		configurations = new GasesFrameworkMainConfigurations(event.getSuggestedConfigurationFile());
		ConfigGasFurnaceRecipes.load(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/gasesframework_GasFurnaceRecipes.json"));
		initBlocksAndItems();
		
		registry.registerIgnitionBlock(Blocks.torch);
		registry.registerIgnitionBlock(Blocks.fire);
		registry.registerIgnitionBlock(registry.getGasBlock(gasTypeFire));
		
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		
		GameRegistry.registerWorldGenerator(worldGenerator, 10);

		initRecipes();

		GameRegistry.registerTileEntity(TileEntityIronGasPump.class, "gasPump");
		GameRegistry.registerTileEntity(TileEntityWoodGasPump.class, "woodGasPump");
		GameRegistry.registerTileEntity(TileEntityIronGasCollector.class, "gasCollector");
		GameRegistry.registerTileEntity(TileEntityWoodGasCollector.class, "woodGasCollector");
		GameRegistry.registerTileEntity(TileEntityIronGasTank.class, "gasTank");
		GameRegistry.registerTileEntity(TileEntityWoodGasTank.class, "woodGasTank");
		GameRegistry.registerTileEntity(TileEntityIronGasFurnace.class, "gasPoweredFurnace");
		GameRegistry.registerTileEntity(TileEntityWoodGasFurnace.class, "woodGasPoweredFurnace");
		GameRegistry.registerTileEntity(TileEntityInfiniteGasPump.class, "infiniteGasPump");
		GameRegistry.registerTileEntity(TileEntityInfiniteGasDrain.class, "infiniteGasDrain");
		GameRegistry.registerTileEntity(TileEntityGasTransposer.class, "gasTransposer");
		GameRegistry.registerTileEntity(TileEntityIronGasDynamo.class, "ironGasDynamo");
		GameRegistry.registerTileEntity(TileEntityWoodGasDynamo.class, "woodGasDynamo");
		
		EntityRegistry.registerModEntity(EntityDelayedExplosion.class, "delayedGasExplosion", 127, this, 20, 1, false);

		for (GasType gasType : registry.getRegisteredGasTypes())
		{
			if (gasType.combustibility.burnRate >= Combustibility.FLAMMABLE.burnRate)
			{
				registry.registerReaction(new ReactionCommonIgnition(), gasType);
				registry.registerReaction(new ReactionEntityIgnition(), gasType);
			}

			registry.registerReaction(new ReactionNoisyPeople(), gasType);

			if (gasType.combustibility != Combustibility.NONE)
			{
				registry.registerLanternInput(lanternTypesGas[gasType.combustibility.ordinal()], new ItemKey(items.gasBottle, gasType.gasID));
			}
		}
		registry.registerReaction(new ReactionBurnEntity(), gasTypeFire);

		registry.registerLanternInput(lanternTypeGasEmpty, new ItemKey(Items.glass_bottle));

		registry.registerGasTransposerHandler(new GasBottleTransposerHandler());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		
		GasesFrameworkWaila.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		for(String s : configurations.gases.ignition.addedBlocks)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) registry.registerIgnitionBlock(block);
		}
		
		for(String s : configurations.gases.ignition.removedBlocks)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) registry.registerIgnitionItem(item);
		}
		
		for(String s : configurations.gases.ignition.addedItems)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) registry.unregisterIgnitionBlock(block);
		}
		
		for(String s : configurations.gases.ignition.removedItems)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) registry.unregisterIgnitionItem(item);
		}
		
		if (configurations.blocks.woodGasFurnace.catchesFire)
		{
			Blocks.fire.setFireInfo(blocks.woodGasFurnaceIdle, 5, 5);
			Blocks.fire.setFireInfo(blocks.woodGasFurnaceActive, 5, 5);
		}
		
		if (configurations.blocks.woodGasDynamo.catchesFire)
		{
			Blocks.fire.setFireInfo(blocks.woodGasDynamo, 5, 5);
		}
	}
	
}