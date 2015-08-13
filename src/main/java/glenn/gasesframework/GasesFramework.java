package glenn.gasesframework;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.IGasesFramework;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasTransporter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.common.CommonProxy;
import glenn.gasesframework.common.ConfigGasFurnaceRecipes;
import glenn.gasesframework.common.GasBottleTransposerHandler;
import glenn.gasesframework.common.GuiHandler;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.common.block.BlockGasCollector;
import glenn.gasesframework.common.block.BlockGasDynamo;
import glenn.gasesframework.common.block.BlockGasFurnace;
import glenn.gasesframework.common.block.BlockGasPipe;
import glenn.gasesframework.common.block.BlockGasPump;
import glenn.gasesframework.common.block.BlockGasTank;
import glenn.gasesframework.common.block.BlockGasTransposer;
import glenn.gasesframework.common.block.BlockInfiniteGasDrain;
import glenn.gasesframework.common.block.BlockInfiniteGasPump;
import glenn.gasesframework.common.block.BlockIronGasCollector;
import glenn.gasesframework.common.block.BlockIronGasDynamo;
import glenn.gasesframework.common.block.BlockIronGasPump;
import glenn.gasesframework.common.block.BlockLantern;
import glenn.gasesframework.common.block.BlockWoodGasCollector;
import glenn.gasesframework.common.block.BlockWoodGasDynamo;
import glenn.gasesframework.common.block.BlockWoodGasPump;
import glenn.gasesframework.common.configuration.GasesFrameworkMainConfigurations;
import glenn.gasesframework.common.entity.EntityDelayedExplosion;
import glenn.gasesframework.common.item.ItemGasBottle;
import glenn.gasesframework.common.item.ItemGasPipe;
import glenn.gasesframework.common.item.ItemGasSampler;
import glenn.gasesframework.common.pipetype.PipeTypeGlass;
import glenn.gasesframework.common.pipetype.PipeTypeIron;
import glenn.gasesframework.common.pipetype.PipeTypeWood;
import glenn.gasesframework.common.reaction.ReactionIgnition;
import glenn.gasesframework.common.tileentity.TileEntityGasCollector;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace.SpecialFurnaceRecipe;
import glenn.gasesframework.common.tileentity.TileEntityDirectionalGasPropellor;
import glenn.gasesframework.common.tileentity.TileEntityGasTank;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasDrain;
import glenn.gasesframework.common.tileentity.TileEntityInfiniteGasPump;
import glenn.gasesframework.common.tileentity.TileEntityIronGasCollector;
import glenn.gasesframework.common.tileentity.TileEntityIronGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityIronGasPump;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasCollector;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasDynamo;
import glenn.gasesframework.common.tileentity.TileEntityWoodGasPump;
import glenn.gasesframework.common.worldgen.WorldGeneratorGasesFramework;
import glenn.gasesframework.network.message.MessageGasEffects;
import glenn.gasesframework.network.message.MessageSetTransposerMode;
import glenn.gasesframework.util.GasTransporterIterator;
import glenn.gasesframework.util.GasTransporterSearch;
import glenn.gasesframework.waila.GasesFrameworkWaila;
import glenn.moddingutils.IVec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
import cpw.mods.fml.relauncher.Side;

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
public class GasesFramework implements IGasesFramework
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
	
	public static Block ironGasPump;
	public static Block woodGasPump;
	public static Block gasTank;
	public static Block ironGasCollector;
	public static Block woodGasCollector;
	public static Block gasFurnaceIdle;
	public static Block gasFurnaceActive;
	public static Block infiniteGasPump;
	public static Block infiniteGasDrain;
	public static Block gasTransposer;
	public static Block ironGasDynamo;
	public static Block woodGasDynamo;
	
	private void initBlocksAndItems()
	{
		GameRegistry.registerItem(GasesFrameworkAPI.gasBottle = (new ItemGasBottle()).setUnlocalizedName("gf_gasBottle").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:gas_bottle"), "gasBottle");

		
		GameRegistry.registerItem(GasesFrameworkAPI.gasSamplerIncluder = (new ItemGasSampler(false)).setUnlocalizedName("gf_gasSamplerIncluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler"), "gasSamplerIncluder");
		GameRegistry.registerItem(GasesFrameworkAPI.gasSamplerExcluder = (new ItemGasSampler(true)).setUnlocalizedName("gf_gasSamplerExcluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler"), "gasSamplerExcluder");
		
		GameRegistry.registerBlock(ironGasPump = new BlockIronGasPump().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_ironGasPump").setBlockTextureName("gasesframework:pump_iron"), "gasPump");
		GameRegistry.registerBlock(woodGasPump = new BlockWoodGasPump().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_woodGasPump").setBlockTextureName("gasesframework:pump_wood"), "woodGasPump");
		GameRegistry.registerBlock(gasTank = new BlockGasTank().setHardness(3.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_gasTank").setBlockTextureName("gasesframework:tank"), "gasTank");
		GameRegistry.registerBlock(ironGasCollector = new BlockIronGasCollector().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_ironGasCollector").setBlockTextureName("gasesframework:collector_iron"), "gasCollector");
		GameRegistry.registerBlock(woodGasCollector = new BlockWoodGasCollector().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_woodGasCollector").setBlockTextureName("gasesframework:collector_wood"), "woodGasCollector");
		GameRegistry.registerBlock(gasFurnaceIdle = new BlockGasFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("gf_gasFurnace").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:gas_furnace"), "gasFurnaceIdle");
		GameRegistry.registerBlock(gasFurnaceActive = new BlockGasFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypeStone).setLightLevel(0.25F).setBlockName("gf_gasFurnaceWarm").setBlockTextureName("gasesframework:gas_furnace"), "gasFurnaceActive");
		GameRegistry.registerBlock(infiniteGasPump = new BlockInfiniteGasPump().setHardness(-1.0F).setBlockName("gf_infiniteGasPump").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:pump_infinite"), "infiniteGasPump");
		GameRegistry.registerBlock(infiniteGasDrain = new BlockInfiniteGasDrain().setHardness(-1.0F).setBlockName("gf_infiniteGasDrain").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:drain"), "infiniteGasDrain");
		GameRegistry.registerBlock(gasTransposer = new BlockGasTransposer().setHardness(2.5F).setBlockName("gf_gasTransposer").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:transposer"), "gasTransposer");
		GameRegistry.registerBlock(ironGasDynamo = new BlockIronGasDynamo().setHardness(2.5F).setBlockName("gf_ironGasDynamo").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:gas_dynamo_iron"), "ironGasDynamo");
		GameRegistry.registerBlock(woodGasDynamo = new BlockWoodGasDynamo().setHardness(2.5F).setBlockName("gf_woodGasDynamo").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:gas_dynamo_wood"), "woodGasDynamo");
		
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
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerMessage(MessageGasEffects.Handler.class, MessageGasEffects.class, 0);
		proxy.registerMessage(MessageSetTransposerMode.Handler.class, MessageSetTransposerMode.class, 1);
		
		GasesFrameworkAPI.modInstance = instance;
		
		GasesFrameworkAPI.creativeTab = new CreativeTabs("tabGases")
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
		
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.lanternTypeEmpty.block, 4), "I", "G", 'I', Items.iron_ingot, 'G', Blocks.glass);
		
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24), "III", 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24, 1), "GGG", "III", "GGG", 'I', Items.iron_ingot, 'G', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(ironGasPump), " I ", "PRP", " I ", 'I', Items.iron_ingot, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(ironGasCollector), " P ", "PUP", " P ", 'U', ironGasPump, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock);
		GameRegistry.addRecipe(new ItemStack(gasTank), "IPI", "P P", "IPI", 'I', Items.iron_ingot, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock);
		GameRegistry.addRecipe(new ItemStack(gasFurnaceIdle), " I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.furnace);
		GameRegistry.addRecipe(new ItemStack(gasTransposer), " P ", "PHP", " P ", 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock, 'H', Blocks.hopper);
		GameRegistry.addShapelessRecipe(new ItemStack(GasesFrameworkAPI.gasSamplerExcluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(GasesFrameworkAPI.gasSamplerIncluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 15));
		
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
		
		GameRegistry.registerTileEntity(TileEntityIronGasPump.class, "gasPump");
		GameRegistry.registerTileEntity(TileEntityWoodGasPump.class, "woodGasPump");
		GameRegistry.registerTileEntity(TileEntityIronGasCollector.class, "gasCollector");
		GameRegistry.registerTileEntity(TileEntityWoodGasCollector.class, "woodGasCollector");
		GameRegistry.registerTileEntity(TileEntityGasTank.class, "gasTank");
		GameRegistry.registerTileEntity(TileEntityGasFurnace.class, "gasPoweredFurnace");
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
	}
	
	/**
	 * Adds a special furnace recipe which can be used in a gas furnace. Special furnace recipes are notably different in the way the stack size of what is smelted matters.
	 * A special gas furnace recipe will always be prioritized before an ordinary furnace recipe.
	 * @param ingredient - The item to be smelted. Can have a stack size larger than 1.
	 * @param result - The result of the smelting action.
	 * @param cookTime - The time it takes to complete the smelting action. Default is 200.
	 */
	@Override
	public void addSpecialFurnaceRecipe(ItemStack ingredient, ItemStack result, int cookTime, int exp)
	{
		TileEntityGasFurnace.specialFurnaceRecipes.add(new SpecialFurnaceRecipe(ingredient, result, cookTime));
	}
	
	/**
	 * Returns true if this block coordinate can be filled with a unit of gas.
	 * If this returns true, {@link glenn.gasesframework.GasesFramework#fillWithGas(World, Random, int, int, int, GasType) fillWithGas(World,Random,int,int,int,GasType)} will also return true.
	 * @param world - The world object
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	@Override
	public boolean canFillWithGas(World world, int x, int y, int z, GasType type)
	{
		if(type == GasesFrameworkAPI.gasTypeAir)
		{
			Block block = world.getBlock(x, y, z);
			if(block instanceof BlockGas)
			{
				return world.getBlockMetadata(x, y, z) > 0;
			}
			else
			{
				return !block.isOpaqueCube();
			}
		}
		
		int firstBlockCapacity = fillCapacity(world, x, y, z, type);
		
		if(firstBlockCapacity >= 16)
		{
			return true;
		}
		else if(firstBlockCapacity >= 0)
		{
			int capacity = 0;
			int[] sideCapacity = new int[6];
			for(int side = 0; side < 6; side++)
			{
		    	if((side == 0 & type.density > 0) | (side == 1 & type.density < 0))
		    	{
		    		sideCapacity[side] = -1;
		    	}
		    	else
		    	{
					int xDirection = x + (side == 4 ? 1 : (side == 5 ? -1 : 0));
			    	int yDirection = y + (side == 0 ? 1 : (side == 1 ? -1 : 0));
			    	int zDirection = z + (side == 2 ? 1 : (side == 3 ? -1 : 0));
			    	
			    	if((sideCapacity[side] = fillCapacity(world, xDirection, yDirection, zDirection, type)) > 0) capacity += sideCapacity[side];
		    	}
			}
			
			return capacity + firstBlockCapacity >= 16;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Try to fill this block coordinate with a unit of gas. If necessary, this method will spread the gas outwards.
	 * The result of this method can be predetermined with {@link glenn.gasesframework.GasesFramework#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type - The gas type that can or cannot be filled here.
	 * @return
	 */
	@Override
	public boolean fillWithGas(World world, Random random, int x, int y, int z, GasType type)
	{
		if(type == GasesFrameworkAPI.gasTypeAir)
		{
			Block block = world.getBlock(x, y, z);
			if(block instanceof BlockGas)
			{
				return world.getBlockMetadata(x, y, z) > 0;
			}
			else
			{
				return !block.isOpaqueCube();
			}
		}
		
		int firstBlockCapacity = fillCapacity(world, x, y, z, type);
		
		if(firstBlockCapacity >= 16)
		{
			fill(world, x, y, z, type, 16);
			return true;
		}
		else if(firstBlockCapacity >= 0)
		{
			int capacity = 0;
			int[] sideCapacity = new int[6];
			for(int side = 0; side < 6; side++)
			{
		    	if((side == 0 & type.density > 0) | (side == 1 & type.density < 0))
		    	{
		    		sideCapacity[side] = -1;
		    	}
		    	else
		    	{
					int xDirection = x + (side == 4 ? 1 : (side == 5 ? -1 : 0));
			    	int yDirection = y + (side == 0 ? 1 : (side == 1 ? -1 : 0));
			    	int zDirection = z + (side == 2 ? 1 : (side == 3 ? -1 : 0));
			    	
			    	if((sideCapacity[side] = fillCapacity(world, xDirection, yDirection, zDirection, type)) > 0) capacity += sideCapacity[side];
		    	}
			}
			
			if(capacity + firstBlockCapacity < 16)
			{
				return false;
			}
			
			fill(world, x, y, z, type, firstBlockCapacity);
			
			int fill = 0;
			int[] sideFill = new int[6];
			for(int side = 0; side < 6; side++)
			{
				if(sideCapacity[side] != -1) fill += (sideFill[side] = (sideCapacity[side] * (16 - firstBlockCapacity)) / capacity);
			}
			
			while(fill < (16 - firstBlockCapacity))
			{
				int side = random.nextInt(6);
				if(sideFill[side] < sideCapacity[side] & sideCapacity[side] != -1)
				{
					sideFill[side]++;
					fill++;
				}
			}
			
			for(int side = 0; side < 6; side++)
			{
				int xDirection = x + (side == 4 ? 1 : (side == 5 ? -1 : 0));
		    	int yDirection = y + (side == 0 ? 1 : (side == 1 ? -1 : 0));
		    	int zDirection = z + (side == 2 ? 1 : (side == 3 ? -1 : 0));
		    	
		    	fill(world, xDirection, yDirection, zDirection, type, sideFill[side]);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private static void fill(World world, int x, int y, int z, GasType type, int amount)
	{
		if(amount <= 0) return;
		
		Block block = world.getBlock(x, y, z);
		if(block == type.block)
		{
			int newMetadata = 16 - world.getBlockMetadata(x, y, z) + amount;
			world.setBlockMetadataWithNotify(x, y, z, 16 - newMetadata, 3);
		}
		else
		{
			world.setBlock(x, y, z, type.block, 16 - amount, 3);
		}
	}
	
	private static int fillCapacity(World world, int x, int y, int z, GasType type)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGas)
		{
			if(block == type.block)
			{
				return world.getBlockMetadata(x, y, z);
			}
			else
			{
				return -1;
			}
		}
		else if(block instanceof BlockLiquid)
		{
			return -1;
		}
		else
		{
			return block.isReplaceable(world, x, y, z) ? 16 : -1;
		}
	}
	
	/**
	 * Place a gas block of the specified type with a specific volume ranging from 0 to 16.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param volume
	 * @return
	 */
	@Override
	public void placeGas(World world, int x, int y, int z, GasType type, int volume)
	{
		if(volume > 0)
		{
			if(volume > 16) volume = 16;
			world.setBlock(x, y, z, type.block, 16 - volume, 3);
		}
	}

	/**
	 * Pump gas into an IGasTransporter or an IGasReceptor with a certain direction and pressure.
	 * If the block is an IGasTransporter, the gas will be pumped as far as the pressure allows it.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param direction
	 * @param pressure
	 * @return Whether the pumping action succeeded or not.
	 */
	public boolean pumpGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		Block block = world.getBlock(x, y, z);
		if (block instanceof IGasTransporter)
		{
			GasTransporterSearch.ReceptorSearch search = new GasTransporterSearch.ReceptorSearch(world, x, y, z, pressure);
		    
			boolean isSearchingLooseEnds = !search.looseEnds.isEmpty();
		    ArrayList<GasTransporterSearch.End> listToSearch = (ArrayList<GasTransporterSearch.End>)(isSearchingLooseEnds ? search.looseEnds : search.ends).clone();
		    Collections.shuffle(listToSearch, random);
		    
		    for(GasTransporterSearch.End end : listToSearch)
		    {
			    IVec branchPos = end.branch.getPosition();
			    IGasTransporter sourceBlock = (IGasTransporter)world.getBlock(branchPos.x, branchPos.y, branchPos.z);
			    GasType sourceBlockType = sourceBlock.getCarriedType(world, x, y, z);
			    boolean hasPushed = false;
			    
			    if(isSearchingLooseEnds)
			    {
				    hasPushed = GasesFrameworkAPI.fillWithGas(world, random, end.endPosition.x, end.endPosition.y, end.endPosition.z, sourceBlockType);
			    }
			    else
			    {
				    IGasReceptor receptor = (IGasReceptor)world.getBlock(end.endPosition.x, end.endPosition.y, end.endPosition.z);
				    hasPushed = receptor.receiveGas(world, end.endPosition.x, end.endPosition.y, end.endPosition.z, end.endDirection.getOpposite(), sourceBlockType);
			    }
			    
			    if(hasPushed)
			    {
				    GasTransporterIterator.DescendingGasTransporterIterator iterator = new GasTransporterIterator.DescendingGasTransporterIterator(end.branch);
				    GasTransporterIterator.Iteration iteration;
				    while((iteration = iterator.narrowNext(random)) != null)
				    {
					    IGasTransporter receptorBlock = (IGasTransporter)world.getBlock(iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z);
					    IGasTransporter giverBlock = (IGasTransporter)world.getBlock(iteration.currentPosition.x, iteration.currentPosition.y, iteration.currentPosition.z);
					    GasType transferredType = giverBlock.getCarriedType(world, iteration.currentPosition.x, iteration.currentPosition.y, iteration.currentPosition.z);
					    
					    receptorBlock = receptorBlock.setCarriedType(world, iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z, transferredType);
					    receptorBlock.handlePressure(world, random, iteration.previousPosition.x, iteration.previousPosition.y, iteration.previousPosition.z, end.branch.depth + 1);
				    }
				    
				    IGasTransporter receptorBlock = (IGasTransporter)block;
				    receptorBlock = receptorBlock.setCarriedType(world, x, y, z, type);
				    Block kek = world.getBlock(x, y, z);
				    receptorBlock.handlePressure(world, random, x, y, z, end.branch.depth + 1);
				    
				    
				    return true;
			    }
		    }
		    
		    return false;
		}
		else if (block instanceof IGasReceptor)
		{
			IGasReceptor receptorBlock = (IGasReceptor)block;
			return receptorBlock.receiveGas(world, x, y, z, direction.getOpposite(), type);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Push gas to a coordinate with a certain direction and pressure.
	 * If the block is an IGasTransporter or IGasReceptor, {@link glenn.gasesframework.api.IGasesFramework#pumpGas(World,int,int,int,GasType,ForgeDirection,int) pumpGas(World,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link glenn.gasesframework.api.IGasesFramework#fillWithGas(World,int,int,int,GasType) fillWithGas(World,int,int,int,GasType)} is returned.
	 * @param world
	 * @param random
	 * @param x
	 * @param y
	 * @param z
	 * @param type
	 * @param direction
	 * @param pressure
	 * @return Whether the pushing action succeeded or not.
	 */
	public boolean pushGas(World world, Random random, int x, int y, int z, GasType type, ForgeDirection direction, int pressure)
	{
		Block block = world.getBlock(x, y, z);
		if (block instanceof IGasTransporter || block instanceof IGasReceptor)
		{
			return pumpGas(world, random, x, y, z, type, direction, pressure);
		}
		else
		{
			return fillWithGas(world, random, x, y, z, type);
		}
	}
	
	/**
	 * If gas exists at this location, it will be ignited.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
	@Override
	public void ignite(World world, int x, int y, int z, Random random)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGas)
		{
			((BlockGas)block).onFire(world, x, y, z, random, world.getBlockMetadata(x, y, z));
		}
	}

	@Override
	public void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking)
	{
        EntityDelayedExplosion explosionEntity = new EntityDelayedExplosion(world, 5, power, false, true);
        explosionEntity.setPosition(x, y, z);
        
        world.spawnEntityInWorld(explosionEntity);
	}
	
	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public GasType getGasType(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGas)
		{
			return ((BlockGas)block).type;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Gets the gas type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public GasType getGasPipeType(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGasPipe)
		{
			return ((BlockGasPipe)block).type;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public int getGasVolume(World world, int x, int y, int z)
	{
		return 16 - world.getBlockMetadata(x, y, z);
	}

	/**
	 * Get the global multiplier for gas explosion power.
	 * @return
	 */
	@Override
	public float getGasExplosionPowerFactor()
	{
		return configurations.gases.explosionFactor;
	}
	
	/**
	 * Get the amount of smoke to be generated by fires.
	 * @return
	 */
	@Override
	public int getFireSmokeAmount()
	{
		return configurations.gases.smoke.fireSmokeAmount;
	}
	
	/**
	 * Registers a gas type. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @return The gas block registered for this type, if any.
	 */
	@Override
	public Block registerGasType(GasType type)
	{
		if(type.isRegistered)
		{
			throw new RuntimeException("Gas type named " + type.name + " was attempted registered while it was already registered.");
		}
		
		if(type != GasesFrameworkAPI.gasTypeAir)
		{
			type.block = GameRegistry.registerBlock(type.tweakGasBlock(new BlockGas(type)), "gas_" + type.name);
			if(type.combustibility.fireSpreadRate >= 0 | type.combustibility.explosionPower > 0.0F)
			{
				Blocks.fire.setFireInfo(type.block, 1000, 1000);
			}
		}
		if(type.isIndustrial)
		{
			type.pipeBlock = GameRegistry.registerBlock(type.tweakPipeBlock(new BlockGasPipe(type)), ItemGasPipe.class, "gasPipe_" + type.name);
		
			LanternType lanternType = GasesFrameworkAPI.lanternTypesGas[type.combustibility.burnRate];
			if(lanternType != GasesFrameworkAPI.lanternTypeGasEmpty)
			{
				lanternType.addItemIn(new ItemKey(GasesFrameworkAPI.gasBottle, type.gasID));
			}
		}
		
		type.isRegistered = true;
		return type.block;
	}
	
	/**
	 * Registers a gas type and places the gas block on a creative tab. This involves creating and registering the blocks necessary for a gas type.
	 * @param type
	 * @param creativeTab
	 * @return The gas block registered for this type, if any.
	 */
	@Override
	public Block registerGasType(GasType type, CreativeTabs creativeTab)
	{
		Block result = registerGasType(type);
		if(result != null)
		{
			result.setCreativeTab(creativeTab);
		}
		return result;
	}
	
	/**
	 * Registers a lantern type. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @return The lantern block registered for this type, if any.
	 */
	@Override
	public Block registerLanternType(LanternType type)
	{
		if(type.isRegistered)
		{
			throw new RuntimeException("Lantern type named " + type.name + " was attempted registered while it was already registered.");
		}
		
		type.block = GameRegistry.registerBlock(type.tweakLanternBlock(new BlockLantern(type)), "lantern_" + type.name);
		
		type.isRegistered = true;
		
		return type.block;
	}
	
	/**
	 * Registers a lantern type and places the lantern block on a creative tab. This involves creating and registering the blocks necessary for a lantern type.
	 * @param type
	 * @param creativeTab
	 * @return The lantern block registered for this type, if any.
	 */
	@Override
	public Block registerLanternType(LanternType type, CreativeTabs creativeTab)
	{
		Block result = registerLanternType(type);
		if(result != null)
		{
			result.setCreativeTab(creativeTab);
		}
		return result;
	}
	
	/**
	 * Registers a gas world generator for generation in certain dimensions.
	 * @param type
	 */
	@Override
	public void registerGasWorldGenType(GasWorldGenType type, String[] dimensionNames)
	{
		if(type.generationFrequency > 0.0f)
		{
			for(String dimensionName : dimensionNames)
			{
				worldGenerator.registerGasWorldGenType(type, dimensionName);
			}
		}
	}

	/**
	 * Registers a gas transposer handler.
	 * @param handler
	 */
	@Override
	public void registerGasTransposerHandler(IGasTransposerHandler handler)
	{
		TileEntityGasTransposer.registerHandler(handler);
	}

	/**
	 * Registers a pipe type.
	 * @param type
	 */
	public void registerPipeType(PipeType type)
	{
		
	}
}