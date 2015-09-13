package glenn.gasesframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.IGasesFramework;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasTransporter;
import glenn.gasesframework.api.filter.GasTypeFilter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;
import glenn.gasesframework.api.lanterntype.LanternType;
import glenn.gasesframework.api.mechanical.IGasTransposerHandler;
import glenn.gasesframework.api.pipetype.PipeType;
import glenn.gasesframework.client.render.RenderBlockGasTypeFilter;
import glenn.gasesframework.common.block.BlockGas;
import glenn.gasesframework.common.block.BlockGasPipe;
import glenn.gasesframework.common.block.BlockLantern;
import glenn.gasesframework.common.entity.EntityDelayedExplosion;
import glenn.gasesframework.common.item.ItemGasPipe;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import glenn.gasesframework.common.tileentity.TileEntityGasTransposer;
import glenn.gasesframework.network.message.MessageSetBlockGasTypeFilter;
import glenn.gasesframework.util.GasTransporterIterator;
import glenn.gasesframework.util.GasTransporterSearch;
import glenn.moddingutils.IVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Implementation implements IGasesFramework
{
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
		TileEntityGasFurnace.specialFurnaceRecipes.add(new TileEntityGasFurnace.SpecialFurnaceRecipe(ingredient, result, cookTime));
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

	@Override
	public void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
		GasesFramework.networkWrapper.sendToDimension(new MessageSetBlockGasTypeFilter(x, y, z, side, filter), world.provider.dimensionId);
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
		return GasesFramework.configurations.gases.explosionFactor;
	}

	/**
	 * Get the amount of smoke to be generated by fires.
	 * @return
	 */
	@Override
	public int getFireSmokeAmount()
	{
		return GasesFramework.configurations.gases.smoke.fireSmokeAmount;
	}

	/**
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * @return
	 */
	@Override
	public int getRenderedGasTypeFilterBlockRenderType()
	{
		return RenderBlockGasTypeFilter.RENDER_ID;
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
				lanternType.addItemIn(new ItemKey(GasesFramework.items.gasBottle, type.gasID));
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
				GasesFramework.worldGenerator.registerGasWorldGenType(type, dimensionName);
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