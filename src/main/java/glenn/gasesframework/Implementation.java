package glenn.gasesframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.IGasesFrameworkImplementation;
import glenn.gasesframework.api.ItemKey;
import glenn.gasesframework.api.PartialGasStack;
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
import glenn.gasesframework.common.entity.EntityDelayedExplosion;
import glenn.gasesframework.common.tileentity.TileEntityGasFurnace;
import glenn.gasesframework.network.message.MessageSetBlockGasTypeFilter;
import glenn.gasesframework.util.GasTransporterIterator;
import glenn.gasesframework.util.GasTransporterSearch;
import glenn.moddingutils.IVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Implementation implements IGasesFrameworkImplementation
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
	 * If this returns true, {@link glenn.gasesframework.api.IGasesFrameworkImplementation#fillWithGas(World, Random, int, int, int, GasType) fillWithGas(World,Random,int,int,int,GasType)} will also return true.
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
	 * The result of this method can be predetermined with {@link glenn.gasesframework.api.IGasesFrameworkImplementation#canFillWithGas(World, int, int, int, GasType) canFillWithGas(World,int,int,int,GasType)}.
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

	private void fill(World world, int x, int y, int z, GasType type, int amount)
	{
		if(amount <= 0) return;

		Block block = world.getBlock(x, y, z);
		if(block == GasesFramework.registry.getGasBlock(type))
		{
			int newMetadata = 16 - world.getBlockMetadata(x, y, z) + amount;
			world.setBlockMetadataWithNotify(x, y, z, 16 - newMetadata, 3);
		}
		else
		{
			placeGas(world, x, y, z, type, amount);
		}
	}

	private int fillCapacity(World world, int x, int y, int z, GasType type)
	{
		Block block = world.getBlock(x, y, z);
		if(block instanceof BlockGas)
		{
			if(block == GasesFramework.registry.getGasBlock(type))
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
	 * Place a gas block.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param gasStack
	 */
	@Override
	public void placeGas(World world, int x, int y, int z, PartialGasStack gasStack)
	{
		placeGas(world, x, y, z, gasStack.gasType, gasStack.partialAmount);
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
			BlockGas gasBlock = GasesFramework.registry.getGasBlock(type);
			if (gasBlock != null)
			{
				world.setBlock(x, y, z, gasBlock, 16 - volume, 3);
			}
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
				boolean hasPushed;

				if(isSearchingLooseEnds)
				{
					hasPushed = fillWithGas(world, random, end.endPosition.x, end.endPosition.y, end.endPosition.z, sourceBlockType);
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
	 * If the block is an IGasTransporter or IGasReceptor,
	 * {@link glenn.gasesframework.api.IGasesFrameworkImplementation#pumpGas(World,Random,int,int,int,GasType,ForgeDirection,int) pumpGas(World,Random,int,int,int,GasType,ForgeDirection,int)} is returned.
	 * Else, {@link glenn.gasesframework.api.IGasesFrameworkImplementation#fillWithGas(World,Random,int,int,int,GasType) fillWithGas(World,Random,int,int,int,GasType)} is returned.
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
	 * Place a pipe block of the specified type containing a gas.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param pipeType
	 * @param gasType
	 */
	@Override
	public void placePipe(World world, int x, int y, int z, PipeType pipeType, GasType gasType)
	{
		world.setBlock(x, y, z, GasesFramework.registry.getGasPipeBlock(gasType), pipeType.pipeID, 3);
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
	 * Spawn a delayed explosion in the world.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param delay
	 * @param power
	 * @param isFlaming
	 * @param isSmoking
	 */
	@Override
	public void spawnDelayedExplosion(World world, double x, double y, double z, int delay, float power, boolean isFlaming, boolean isSmoking)
	{
		EntityDelayedExplosion explosionEntity = new EntityDelayedExplosion(world, 5, power, false, true);
		explosionEntity.setPosition(x, y, z);

		world.spawnEntityInWorld(explosionEntity);
	}

	/**
	 * Sent a filter update packet for {@link glenn.gasesframework.api.block.IGasTypeFilter IGasTypeFilter} blocks to clients.
	 * This will call {@link glenn.gasesframework.api.block.IGasTypeFilter#setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter) setFilter(IBlockAccess,int,int,int,ForgeDirection,GasTypeFilter)}.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param side
	 * @param filter
	 */
	@Override
	public void sendFilterUpdatePacket(World world, int x, int y, int z, ForgeDirection side, GasTypeFilter filter)
	{
		GasesFramework.networkWrapper.sendToDimension(
				new MessageSetBlockGasTypeFilter(x, y, z, side, filter),
				world.provider.dimensionId);
	}

	/**
	 * Get a PartialGasStack at the location. If the block is air, a full stack of air is returned. Else, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public PartialGasStack getGas(IBlockAccess blockAccess, int x, int y, int z)
	{
		Block block = blockAccess.getBlock(x, y, z);
		if (block instanceof BlockGas)
		{
			return new PartialGasStack(((BlockGas)block).type, 16 - blockAccess.getBlockMetadata(x, y, z));
		}
		else if (block == Blocks.air)
		{
			return new PartialGasStack(GasesFrameworkAPI.gasTypeAir, 16);
		}

		return null;
	}

	/**
	 * Gets the gas type of the gas block at the location, if any. If no gas block is present, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public GasType getGasType(IBlockAccess blockAccess, int x, int y, int z)
	{
		Block block = blockAccess.getBlock(x, y, z);
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
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public GasType getGasTypeInPipe(IBlockAccess blockAccess, int x, int y, int z)
	{
		Block block = blockAccess.getBlock(x, y, z);
		if (block instanceof BlockGasPipe)
		{
			return ((BlockGasPipe)block).type;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the pipe type of the gas pipe block at the location, if any. If no gas pipe block is present, null is returned.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public PipeType getPipeType(IBlockAccess blockAccess, int x, int y, int z)
	{
		Block block = blockAccess.getBlock(x, y, z);
		if (block instanceof BlockGasPipe)
		{
			return ((BlockGasPipe)block).getPipeType(blockAccess, x, y, z);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets the volume of a gas block ranging from 1 to 16.
	 * @param blockAccess
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	@Override
	public int getGasVolume(IBlockAccess blockAccess, int x, int y, int z)
	{
		return 16 - blockAccess.getBlockMetadata(x, y, z);
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
	 * Get the block rendering ID for blocks that implement {@link glenn.gasesframework.api.block.IRenderedGasTypeFilter IRenderedGasTypeFilter}.
	 * @return
	 */
	@Override
	public int getRenderedGasTypeFilterBlockRenderType()
	{
		return RenderBlockGasTypeFilter.RENDER_ID;
	}
}
