package glenn.gasesframework;

import glenn.gasesframework.api.IGasesFramework;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.reaction.Reaction;
import glenn.gasesframework.api.reaction.ReactionEmpty;
import glenn.gasesframework.api.type.GasType;
import glenn.gasesframework.api.type.GasTypeAir;
import glenn.gasesframework.api.type.GasTypeFire;
import glenn.gasesframework.block.BlockGas;
import glenn.gasesframework.block.BlockGasCollector;
import glenn.gasesframework.block.BlockGasFurnace;
import glenn.gasesframework.block.BlockGasPipe;
import glenn.gasesframework.block.BlockGasPump;
import glenn.gasesframework.block.BlockGasTank;
import glenn.gasesframework.block.BlockInfiniteGas;
import glenn.gasesframework.block.BlockLantern;
import glenn.gasesframework.block.BlockLanternEmpty;
import glenn.gasesframework.block.BlockLanternGas;
import glenn.gasesframework.block.BlockLanternGasEmpty;
import glenn.gasesframework.block.BlockLanternSpecial;
import glenn.gasesframework.client.render.RenderBlockGas;
import glenn.gasesframework.client.render.RenderBlockGasPipe;
import glenn.gasesframework.client.render.RenderBlockLantern;
import glenn.gasesframework.client.render.RenderBlockGasPump;
import glenn.gasesframework.client.render.RenderBlockGasTank;
import glenn.gasesframework.item.ItemGasBottle;
import glenn.gasesframework.item.ItemGasPipe;
import glenn.gasesframework.item.ItemGasSampler;
import glenn.gasesframework.tileentity.TileEntityGasCollector;
import glenn.gasesframework.tileentity.TileEntityGasFurnace;
import glenn.gasesframework.tileentity.TileEntityGasFurnace.SpecialFurnaceRecipe;
import glenn.gasesframework.tileentity.TileEntityInfiniteGas;
import glenn.gasesframework.tileentity.TileEntityPump;
import glenn.gasesframework.tileentity.TileEntityTank;
import glenn.gasesframework.util.QueuedLanternRecipe;
import glenn.moddingutils.Configurations.ItemRepresentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

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
@Mod(modid = "gasesFramework", name = "Gases Framework", version = GasesFramework.VERSION, dependencies="required-after:gasesFrameworkCore", acceptedMinecraftVersions = "[" + GasesFramework.TARGETVERSION + "]")
public class GasesFramework implements IGasesFramework
{
	// The instance of your mod that Forge uses.
	@Instance("gasesFramework")
	public static GasesFramework instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "glenn.gasesframework.client.ClientProxy", serverSide = "glenn.gasesframework.CommonProxy")
	public static CommonProxy proxy;
	public static final GuiHandler guiHandler = new GuiHandler();
	
	public static final String MODID = GasesFrameworkAPI.OWNER;
	public static final String VERSION = "1.1.0";
	public static final String TARGETVERSION = GasesFrameworkAPI.TARGETVERSION;
	
	public static GasesFrameworkMainConfigurations configurations;
	
	private static ArrayList<QueuedLanternRecipe> queuedLanternRecipes = new ArrayList<QueuedLanternRecipe>();
	
	private static Configuration config;
	
	
	public static Block gasPump;
	public static Block gasTank;
	public static Block gasCollector;
	public static Block gasFurnaceIdle;
	public static Block gasFurnaceActive;
	public static Block infGasBlock;
	
	public static BlockLantern lanternEmpty;
	public static BlockLantern lanternGasEmpty;
	public static BlockLantern lanternGas1;
	public static BlockLantern lanternGas2;
	public static BlockLantern lanternGas3;
	public static BlockLantern lanternGas4;
	public static BlockLantern lanternGas5;
	
	
	
	private void initBlocksAndItems()
	{
		GameRegistry.registerItem(GasesFrameworkAPI.gasBottle = (new ItemGasBottle()).setUnlocalizedName("gf_gasBottle").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:gas_bottle"), "gasBottle");

		GameRegistry.registerBlock(infGasBlock = new BlockInfiniteGas().setBlockName("gf_infGasBlock").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:infGasBlock"), "infGasBlock");
		
		GameRegistry.registerItem(GasesFrameworkAPI.gasSamplerIncluder = (new ItemGasSampler(false)).setUnlocalizedName("gf_gasSamplerIncluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler"), "gasSamplerIncluder");
		GameRegistry.registerItem(GasesFrameworkAPI.gasSamplerExcluder = (new ItemGasSampler(true)).setUnlocalizedName("gf_gasSamplerExcluder").setCreativeTab(GasesFrameworkAPI.creativeTab).setTextureName("gasesframework:sampler"), "gasSamplerExcluder");
		
		GameRegistry.registerBlock(gasPump = new BlockGasPump(true).setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_gasPump").setBlockTextureName("gasesframework:pump"), "gasPump");
		GameRegistry.registerBlock(gasTank = new BlockGasTank().setHardness(3.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_gasTank").setBlockTextureName("gasesframework:tank"), "gasTank");
		GameRegistry.registerBlock(gasCollector = new BlockGasCollector().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockName("gf_gasCollector").setBlockTextureName("gasesframework:collector"), "gasCollector");
		GameRegistry.registerBlock(gasFurnaceIdle = new BlockGasFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("gf_gasFurnace").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:gas_furnace"), "gasFurnaceIdle");
		GameRegistry.registerBlock(gasFurnaceActive = new BlockGasFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypeStone).setLightLevel(0.25F).setBlockName("gf_gasFurnaceWarm").setBlockTextureName("gasesframework:gas_furnace"), "gasFurnaceActive");

		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeAir);
		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeSmoke, GasesFrameworkAPI.creativeTab);
		GasesFrameworkAPI.registerGasType(GasesFrameworkAPI.gasTypeFire, GasesFrameworkAPI.creativeTab);
		
		GameRegistry.registerBlock(lanternEmpty = (BlockLantern)(new BlockLanternEmpty()).setBlockName("gf_lanternEmpty").setCreativeTab(GasesFrameworkAPI.creativeTab).setBlockTextureName("gasesframework:lantern_empty"), "lanternEmpty");
		GameRegistry.registerBlock(lanternGasEmpty = (BlockLantern)(new BlockLanternGasEmpty()).setBlockName("gf_lanternGasEmpty").setBlockTextureName("gasesframework:lantern_gas0"), "lanternGasEmpty");
		GameRegistry.registerBlock(lanternGas1 = (BlockLantern)(new BlockLanternGas(Combustibility.CONTROLLABLE)).setLightLevel(1.0F).setBlockName("gf_lanternGas1").setBlockTextureName("gasesframework:lantern_gas1"), "lanternGas1");
		GameRegistry.registerBlock(lanternGas2 = (BlockLantern)(new BlockLanternGas(Combustibility.FLAMMABLE)).setLightLevel(1.0F).setBlockName("gf_lanternGas2").setBlockTextureName("gasesframework:lantern_gas2"), "lanternGas2");
		GameRegistry.registerBlock(lanternGas3 = (BlockLantern)(new BlockLanternGas(Combustibility.HIGHLY_FLAMMABLE)).setLightLevel(1.0F).setBlockName("gf_lanternGas3").setBlockTextureName("gasesframework:lantern_gas3"), "lanternGas3");
		GameRegistry.registerBlock(lanternGas4 = (BlockLantern)(new BlockLanternGas(Combustibility.EXPLOSIVE)).setLightLevel(1.0F).setBlockName("gf_lanternGas4").setBlockTextureName("gasesframework:lantern_gas4"), "lanternGas4");
		GameRegistry.registerBlock(lanternGas5 = (BlockLantern)(new BlockLanternGas(Combustibility.HIGHLY_EXPLOSIVE)).setLightLevel(1.0F).setBlockName("gf_lanternGas5").setBlockTextureName("gasesframework:lantern_gas5"), "lanternGas5");
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		GasesFrameworkAPI.modInstance = instance;
		
		GasesFrameworkAPI.creativeTab = new CreativeTabs("tabGases")
		{
			public Item getTabIconItem()
			{
				return Item.getItemFromBlock(lanternEmpty);
			}
		};
		
		initBlocksAndItems();
		configurations = new GasesFrameworkMainConfigurations(event.getSuggestedConfigurationFile());
		try
		{
			File gasFurnaceRecipesFile = new File(event.getModConfigurationDirectory().getAbsolutePath() + "/gasesframework_GasFurnaceRecipes.json");
			
			if(!gasFurnaceRecipesFile.exists())
			{
				gasFurnaceRecipesFile.createNewFile();
				PrintWriter writer = new PrintWriter(gasFurnaceRecipesFile);
				writer.print(String.format("[%n\t%n]"));
				writer.close();
			}
			
			BufferedReader bf = new BufferedReader(new FileReader(gasFurnaceRecipesFile));
			String recipesJsonString = "";
			String line;
			while((line = bf.readLine()) != null)
			{
				recipesJsonString += line + "\n";
			}
			bf.close();
			
			Gson gson = new Gson();
			ArrayList<CustomGasFurnaceRecipe> recipes = gson.fromJson(recipesJsonString, new TypeToken<ArrayList<CustomGasFurnaceRecipe>>(){}.getType());
			
			for(CustomGasFurnaceRecipe recipe : recipes)
			{
				addSpecialFurnaceRecipe(recipe.input.getItemStack(), recipe.output.getItemStack(), recipe.time == 0 ? 200 : recipe.time, recipe.exp);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		GasesFrameworkAPI.registerIgnitionBlock(Blocks.torch);
		GasesFrameworkAPI.registerIgnitionBlock(Blocks.fire);
		GasesFrameworkAPI.registerIgnitionBlock(GasesFrameworkAPI.gasTypeFire.block);
		
		proxy.registerEventHandlers();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		
		GameRegistry.addRecipe(new ItemStack(lanternEmpty, 4), "I", "G", 'I', Items.iron_ingot, 'G', Blocks.glass);
		
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24), "III", 'I', Items.iron_ingot);
		GameRegistry.addRecipe(new ItemStack(GasesFrameworkAPI.gasTypeAir.pipeBlock, 24, 1), "GGG", "III", "GGG", 'I', Items.iron_ingot, 'G', Blocks.glass_pane);
		GameRegistry.addRecipe(new ItemStack(gasPump), " I ", "PRP", " I ", 'I', Items.iron_ingot, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock, 'R', Items.redstone);
		GameRegistry.addRecipe(new ItemStack(gasCollector), " P ", "PUP", " P ", 'U', gasPump, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock);
		GameRegistry.addRecipe(new ItemStack(gasTank), "IPI", "P P", "IPI", 'I', Items.iron_ingot, 'P', GasesFrameworkAPI.gasTypeAir.pipeBlock);
		GameRegistry.addRecipe(new ItemStack(gasFurnaceIdle), " I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.furnace);
		GameRegistry.addShapelessRecipe(new ItemStack(GasesFrameworkAPI.gasSamplerExcluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(GasesFrameworkAPI.gasSamplerIncluder), new ItemStack(Items.glass_bottle), new ItemStack(Items.dye, 1, 15));
		
		GameRegistry.registerTileEntity(TileEntityPump.class, "gasPump");
		GameRegistry.registerTileEntity(TileEntityInfiniteGas.class, "infiniteGas");
		GameRegistry.registerTileEntity(TileEntityGasCollector.class, "gasCollector");
		GameRegistry.registerTileEntity(TileEntityTank.class, "gasTank");
		GameRegistry.registerTileEntity(TileEntityGasFurnace.class, "gasPoweredFurnace");
		
		GasesFrameworkAPI.registerReaction(new ReactionIgnition());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
		
		for(QueuedLanternRecipe recipe : queuedLanternRecipes)
		{
			recipe.register();
		}
		
		for(String s : configurations.other_removedIgnitionBlocks)
		{
			Block block = Block.getBlockFromName(s);
			if(block != null) GasesFrameworkAPI.unregisterIgnitionBlock(block);
		}
		
		for(String s : configurations.other_removedIgnitionItems)
		{
			Item item = (Item)Item.itemRegistry.getObject(s);
			if(item != null) GasesFrameworkAPI.unregisterIgnitionItem(item);
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	/**
	 * Returns the reverse direction index of a direction index. Used by most blocks in Gases Framework.
	 */
	public static int reverseDirection(int direction)
	{
		return (direction / 2) * 2 + 1 - direction % 2;
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
	 * Ignore, used by the Gases Framework only.
	 * @param result
	 * @param ingredient
	 */
	public static void queueLanternRecipe(BlockLantern result, ItemStack ingredient)
	{
		queuedLanternRecipes.add(new QueuedLanternRecipe(result, ingredient));
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
		return configurations.gases_gasExplosionFactor;
	}
	
	/**
	 * Get the amount of smoke to be generated by fires.
	 * @return
	 */
	@Override
	public int getFireSmokeAmount()
	{
		return configurations.gases_fireSmokeAmount;
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
		}
		
		if(type.combustibility.lanternBlock != null)
		{
			GameRegistry.addShapelessRecipe(new ItemStack(type.combustibility.lanternBlock), new Object[]{new ItemStack(GasesFramework.lanternEmpty), new ItemStack(GasesFrameworkAPI.gasBottle, 1, type.gasID)});
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
	 * Register a non-expiring lantern that contains a certain item. This lantern can be created by right-clicking a lantern with the input in hand, or by crafting an empty lantern with the input.
	 * @param input - The item contained by this lantern.
	 * @param name - The block name for this lantern.
	 * @param lightLevel - The level of light emitted by this lantern from 0.0f to 1.0f.
	 * @param textureName - The texture name of the item displayed inside the lantern.
	 * @return
	 */
	@Override
	public Block registerLanternType(ItemStack input, String name, float lightLevel, String textureName)
	{
		return registerExpiringLanternType(0, input, input, null, name, lightLevel, textureName);
	}
	
	/**
	 * Register an expiring lantern that contains a certain item. This lantern can be created by right-clicking a lantern with the input in hand, or by crafting an empty lantern with the input.
	 * @param tickRate - The rate at which the lantern expires. Lower values will cause the lantern to expire more quickly.
	 * @param input - The item contained by this lantern.
	 * @param output - The item given when the item is removed.
	 * @param expirationBlock - The block this lantern will transform into when it expires. Ideally, this is another lantern block.
	 * @param name - The block name for this lantern.
	 * @param lightLevel - The level of light emitted by this lantern from 0.0f to 1.0f.
	 * @param textureName - The texture name of the item displayed inside the lantern.
	 * @return
	 */
	@Override
	public Block registerExpiringLanternType(int tickRate, ItemStack input, ItemStack output, Block expirationBlock, String name, float lightLevel, String textureName)
	{
		return GameRegistry.registerBlock(new BlockLanternSpecial(tickRate, input, output, expirationBlock).setLightLevel(lightLevel).setBlockName("lantern_" + name).setBlockTextureName(textureName), "lantern_" + name);
	}
	
	private class CustomGasFurnaceRecipe
	{
		public ItemRepresentation input;
		public ItemRepresentation output;
		public int time = 200;
		public int exp = 200;
	}
}