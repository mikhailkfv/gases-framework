package glenn.gasesframework;

import java.io.File;

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
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.ConfigGasFurnaceRecipes;
import glenn.gasesframework.common.GasBottleTransposerHandler;
import glenn.gasesframework.common.GuiHandler;
import glenn.gasesframework.init.GFBlocks;
import glenn.gasesframework.common.configuration.GasesFrameworkMainConfigurations;
import glenn.gasesframework.common.entity.EntityDelayedExplosion;
import glenn.gasesframework.init.GFItems;
import glenn.gasesframework.common.pipetype.PipeTypeGlass;
import glenn.gasesframework.common.pipetype.PipeTypeIron;
import glenn.gasesframework.common.pipetype.PipeTypeWood;
import glenn.gasesframework.common.reaction.ReactionIgnition;
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
	
	public static final String MODID = GasesFrameworkAPI.OWNER;
	public static final String VERSION = "1.1.2";
	public static final String TARGETVERSION = GasesFrameworkAPI.TARGETVERSION;
	
	public static final GuiHandler guiHandler = new GuiHandler();
	public static SimpleNetworkWrapper networkWrapper = new SimpleNetworkWrapper(MODID);
	
	public static GasesFrameworkMainConfigurations configurations;
	
	public static final PipeType pipeTypeIron = new PipeTypeIron(0, "iron", true, "gasesframework:pipe_iron");
	public static final PipeType pipeTypeGlass = new PipeTypeGlass(1, "glass", false, "gasesframework:pipe_glass");
	public static final PipeType pipeTypeWood = new PipeTypeWood(2, "wood", true, "gasesframework:pipe_wood");
	
	public static final WorldGeneratorGasesFramework worldGenerator = new WorldGeneratorGasesFramework();

	public static GFItems items;
	public static GFBlocks blocks;

	private void initBlocksAndItems()
	{
		items = new GFItems();
		blocks = new GFBlocks();

		GasesFrameworkAPI.registerLanternType(GasesFrameworkAPI.lanternTypeEmpty, GasesFrameworkAPI.creativeTab);
		for(int i = 0; i < GasesFrameworkAPI.lanternTypesGas.length; i++)
		{
			GasesFrameworkAPI.registerLanternType(GasesFrameworkAPI.lanternTypesGas[i]);
		}
		
		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeAir);
		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeSmoke, GasesFrameworkAPI.creativeTab);
		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeFire, GasesFrameworkAPI.creativeTab);
		
		GasesFrameworkAPI.registerPipeType(pipeTypeIron);
		GasesFrameworkAPI.registerPipeType(pipeTypeGlass);
		GasesFrameworkAPI.registerPipeType(pipeTypeWood);
	}

	private void initRecipes()
	{
		ItemStack pipeIron = new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 1, 0);
		ItemStack pipeWood = new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 1, 2);

		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.lanternTypeEmpty.block, 4), "I", "G", 'I', Items.iron_ingot, 'G', Blocks.glass);
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24), "III", 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24, 1), "GGG", "III", "GGG", 'I', Items.iron_ingot, 'G', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24, 2), " D ", "LLL", " D ", 'L', Blocks.log, 'D', items.ductTape);
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
		GameRegistry.addRecipe(new ItemStack(items.ductTape, 32), "SSS", "SAS", "SSS", 'S', Items.string, 'A', items.adhesive);

		for(LanternType lanternType : LanternType.getAllLanternTypes())
		{
			if(lanternType != GasesFrameworkAPI.lanternTypeEmpty)
			{
				for(ItemKey itemIn : lanternType.getAllAcceptedItems())
				{
					if(itemIn.item != null)
					{
						GameRegistry.addShapelessRecipe(new ItemStack(lanternType.block), new ItemStack(GasesFrameworkAPI.lanternTypeEmpty.block), new ItemStack(itemIn.item, 1, itemIn.damage));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerMessage(MessageGasEffects.Handler.class, MessageGasEffects.class, 0);
		proxy.registerMessage(MessageSetTransposerMode.Handler.class, MessageSetTransposerMode.class, 1);
		proxy.registerMessage(MessageSetBlockGasTypeFilter.Handler.class, MessageSetBlockGasTypeFilter.class, 2);
		proxy.registerMessage(MessageDuctTapeGag.Handler.class, MessageDuctTapeGag.class, 3);
		
		GasesFrameworkAPI.implementation = new Implementation();
		
		GasesFrameworkAPI.creativeTab = new CreativeTabs("tabGasesFramework")
		{
			public Item getTabIconItem()
			{
				return Item.getItemFromBlock(GasesFrameworkAPI.lanternTypeEmpty.block);
			}
		};
		
		configurations = new GasesFrameworkMainConfigurations(event.getSuggestedConfigurationFile());
		ConfigGasFurnaceRecipes.load(new File(event.getModConfigurationDirectory().getAbsolutePath() + "/gasesframework_GasFurnaceRecipes.json"));
		initBlocksAndItems();
		
		GasesFrameworkAPI.registerIgnitionBlock(Blocks.torch);
		GasesFrameworkAPI.registerIgnitionBlock(Blocks.fire);
		GasesFrameworkAPI.registerIgnitionBlock(GasesFrameworkAPI.gasTypeFire.block);
		
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
		
		GasesFrameworkAPI.registerReaction(new ReactionIgnition());
		
		GasesFrameworkAPI.registerGasTransposerHandler(new GasBottleTransposerHandler());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		
		GasesFrameworkWaila.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		for(String s : configurations.gases.ignition.addedBlocks)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) GasesFrameworkAPI.registerIgnitionBlock(block);
		}
		
		for(String s : configurations.gases.ignition.removedBlocks)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) GasesFrameworkAPI.registerIgnitionItem(item);
		}
		
		for(String s : configurations.gases.ignition.addedItems)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) GasesFrameworkAPI.unregisterIgnitionBlock(block);
		}
		
		for(String s : configurations.gases.ignition.removedItems)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) GasesFrameworkAPI.unregisterIgnitionItem(item);
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