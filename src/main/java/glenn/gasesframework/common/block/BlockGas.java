package glenn.gasesframework.common.block;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.IGFRegistry;
import glenn.gasesframework.api.PartialGasStack;
import glenn.gasesframework.api.block.ISample;
import glenn.gasesframework.api.MaterialGas;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.reaction.BlockReaction;
import glenn.gasesframework.api.reaction.EntityReaction;
import glenn.gasesframework.api.reaction.GasReaction;
import glenn.gasesframework.client.render.RenderBlockGas;

import java.util.Random;

import glenn.gasesframework.common.reaction.ReactionCommonIgnition;
import glenn.gasesframework.common.reaction.environment.WorldBlockReactionEnvironment;
import glenn.gasesframework.common.reaction.environment.WorldEntityReactionEnvironment;
import glenn.gasesframework.common.reaction.environment.WorldGasReactionEnvironment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * To allow a gas to flow in pipes, create a {@link glenn.gasesframework.common.block.BlockGasPipe BlockGasPipe} for the gas.
 * @author Glenn
 */
public class BlockGas extends Block implements ISample
{
	public GasType type;
	
	public boolean disableUpdate = false;

	//								   Ring 0	   Ring 1		Ring 2
	private static final int[] ringsX = {1, 0, -1, 0, 1, -1, -1, 1, 2, 0, -2, 0};
	private static final int[] ringsZ = {0, 1, 0, -1, 1, 1, -1, -1, 0, 2, 0, -2};
	
	private static final int[] ringX = {0, 0, -1, 1, 0, 0};
	private static final int[] ringY = {-1, 1, 0, 0, 0, 0};
	private static final int[] ringZ = {0, 0, 0, 0, -1, 1};

	//								   Ring 0	   Ring 1		Ring 2
	private static final int[] movesX = {1, 0, -1, 0, 1, -1, -1, 1, 1, 0, -1, 0};
	private static final int[] movesZ = {0, 1, 0, -1, 1, 1, -1, -1, 0, 1, 0, -1};
	
	/**
	 * Constructs a new gas block.
	 * @param type
	 */
	public BlockGas(GasType type)
	{
		super(MaterialGas.INSTANCE);
		this.type = type;
		
		setBlockName("gas_" + type.name);
		setTickRandomly(true);
		disableStats();
		setHardness(0.0F);
		setLightOpacity(type.opacity);
		setLightLevel(type.lightLevel);
		setCreativeTab(type.creativeTab);
		
		setBlockTextureName(type.textureName);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return type.getUnlocalizedName();
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k)
	{
		int volume = 16 - world.getBlockMetadata(i, j, k);
		double minY = type.getMinY(world, i, j, k, volume);
		double maxY = type.getMaxY(world, i, j, k, volume);

		//return AxisAlignedBB.getAABBPool().getAABB((double)i, (double)j + minY, (double)k, (double)i + 1.0D, (double)j + maxY, (double)k + 1.0D);
		return AxisAlignedBB.getBoundingBox((double)i, (double)j + minY, (double)k, (double)i + 1.0D, (double)j + maxY, (double)k + 1.0D);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		EntityReaction[] reactions = GasesFramework.registry.getRegisteredEntityReactions(type);
		if (reactions.length > 0)
		{
			WorldEntityReactionEnvironment environment = new WorldEntityReactionEnvironment(world, x, y, z, entity);
			for (EntityReaction reaction : reactions)
			{
				reaction.react(environment);
			}
		}
	}

	/**
	 * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
	 * when first determining what to render.
	 */
	@Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return type.color;
	}

	@Override
	public int getRenderColor(int metadata)
	{
		return type.color;
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	/**
	 * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
	 * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
	 */
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns whether this block is collideable based on the arguments passed in \n@param par1 block metaData \n@param
	 * par2 whether the player right-clicked while holding a boat
	 */
	@Override
	public boolean canCollideCheck(int par1, boolean par2)
	{
		return par2 && par1 == 0;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return false;
	}

	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int j, int k, int side)
	{
		int xDirection = side == 4 ? 1 : (side == 5 ? -1 : 0);
		int yDirection = side == 0 ? 1 : (side == 1 ? -1 : 0);
		int zDirection = side == 2 ? 1 : (side == 3 ? -1 : 0);
		
		int volume = 16 - blockAccess.getBlockMetadata(i, j, k);
		Block directionBlock = blockAccess.getBlock(i + xDirection, j + yDirection, k + zDirection);
		int directionBlockMetadata = blockAccess.getBlockMetadata(i + xDirection, j + yDirection, k + zDirection);
		
		if(side == 1)
		{
			if(directionBlock instanceof BlockGas)
			{
				double maxY = ((BlockGas)directionBlock).type.getMaxY(blockAccess, i, j, k, directionBlockMetadata);
				
				return maxY - 1.0D != type.getMinY(blockAccess, i, j, k, volume);
			} else
			{
				return true;
			}
		}
		else if(side == 0)
		{
			if(directionBlock instanceof BlockGas)
			{
				double minY = ((BlockGas)directionBlock).type.getMinY(blockAccess, i, j, k, directionBlockMetadata);
				
				return minY != type.getMaxY(blockAccess, i, j, k, volume) - 1.0D ;
			} else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	/**
	 * Returns the indent to a block at the given coordinate. Should only be used by the renderer to avoid side clipping.
	 * @param par1IBlockAccess
	 * @param par2
	 * @param par3
	 * @param par4
	 * @return
	 */
	public final double sideIndent(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
	{
		return par1IBlockAccess.getBlock(par2, par3, par4).isOpaqueCube() ? 0.001D : 0.0D;
	}

	/**
	 * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
	 * cleared to be reused)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
	{
		return null;
	}

	/**
	 * The type of render function that is called for this block
	 */
	@Override
	public int getRenderType()
	{
		return RenderBlockGas.RENDER_ID;
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}
	
	/**
	 * Called whenever a gas {@link ReactionCommonIgnition reacts}
	 * with a block registered with {@link IGFRegistry#registerIgnitionBlock(Block)}.
	 * @param world
	 * @param i
	 * @param j
	 * @param k
	 * @param random
	 * @param metadata
	 * @return
	 */
	public boolean onFire(World world, int i, int j, int k, Random random, int metadata)
	{
		if(type.combustibility.explosionPower > 0.0F)
		{
			if (!world.isRemote)
			{
				float totalPower = 0.0f;
				float totalSqPower = 0.0f;
				float avgX = 0.0f, avgY = 0.0f, avgZ = 0.0f;
				
				for (int localX = -1; localX <= 1; localX++)
				{
					int x = localX + i;
					for (int localY = -1; localY <= 1; localY++)
					{
						int y = localY + j;
						for (int localZ = -1; localZ <= 1; localZ++)
						{
							int z = localZ + k;
							GasType type = GasesFramework.implementation.getGasType(world, x, y, z);
							if (type != null && type.combustibility.explosionPower > 0.0f)
							{
								float power = (16.0F - world.getBlockMetadata(x, y, z)) / 32.0F + 0.20F;
								
								totalPower += power;
								totalSqPower += power * power;
								avgX += localX * power;
								avgY += localY * power;
								avgZ += localZ * power;

								world.setBlockToAir(x, y, z);
							}
						}
					}
				}
				
				if (totalPower > 0.0f)
				{
					avgX = avgX / totalPower + i + 0.5f;
					avgY = avgY / totalPower + j + 0.5f;
					avgZ = avgZ / totalPower + k + 0.5f;
					
					float power = (float)Math.sqrt(totalSqPower) * 0.75f;
					
					GasesFramework.implementation.spawnDelayedExplosion(world, avgX, avgY, avgZ, 5, GasesFramework.configurations.gases.explosionFactor * power, true, false);
				}
			}
			return true;
		}
		else if(type.combustibility.fireSpreadRate >= 0)
		{
			GasesFramework.implementation.placeGas(world, i, j, k, GasesFramework.gasTypeFire, 16 - metadata);

			return true;
		}

		return false;
	}
	
	/**
	 * Used internally only. Mixes the indices of items of the same value in a sorted array
	 * @param rand
	 * @param valueList
	 * @param indexList
	 */
	private void mixEqualSortedValues(Random rand, int[] valueList, int[] indexList)
	{
		for(int i = 0; i < valueList.length;)
		{
			int value = valueList[i];
			for(int j = 1; j + i <= valueList.length; j++)
			{
				if(j + i >= valueList.length || valueList[i + j] != value)
				{
					int amountOfSwaps = (j - 1) * (j - 1) * 2;
					for(int k = 0; k < amountOfSwaps; k++)
					{
						int pos1 = rand.nextInt(j);
						int pos2 = rand.nextInt(j);
						if(pos1 != pos2)
						{
							int temp = indexList[i + pos1];
							indexList[i + pos1] = indexList[i + pos2];
							indexList[i + pos2] = temp;
						}
					}
					
					i += j;
					break;
				}
			}
		}
	}
	
	/**
	 * Used internally only. Fills an array with randomly placed unique indices with boundaries
	 * @param rand
	 * @param indices
	 * @param length
	 * @param start
	 * @param arrayTranslate
	 */
	private void fillArrayWithIndices(Random rand, int[] indices, int length, int start, int arrayTranslate)
	{
		for(int i = 0; i < length; i++)
		{
			while(true)
			{
				int index = arrayTranslate + rand.nextInt(length);
				if(indices[index] == 0)
				{
					indices[index] = start + i;
					break;
				}
			}
		}
	}

	/**
	 * Gets the delay in ticks needed before the block should update again based on its density. Used internally only.
	 * @return
	 */
	protected int getDelayForUpdateByDensity()
	{
		if(type.density > 0)
		{
			return (int)(128.0F / (float)type.density);
		}
		else if(type.density < 0)
		{
			return (int)(-128.0F / (float)type.density);
		}
		else
		{
			return 8;
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if(y > GasesFramework.configurations.gases.maxHeight)
		{
			world.setBlock(x, y, z, Blocks.air);
			return;
		}
		
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
		{
			int bx = x + direction.offsetX;
			int by = y + direction.offsetY;
			int bz = z + direction.offsetZ;

			PartialGasStack b = GasesFramework.implementation.getGas(world, bx, by, bz);
			if (b != null)
			{
				GasReaction[] reactions = GasesFramework.registry.getRegisteredGasReactions(type);
				if (reactions.length > 0)
				{
					WorldGasReactionEnvironment environment = new WorldGasReactionEnvironment(world, x, y, z, bx, by, bz);
					for (GasReaction reaction : reactions)
					{
						reaction.react(environment);
					}
				}
			}
			else
			{
				BlockReaction[] reactions = GasesFramework.registry.getRegisteredBlockReactions(type);
				if (reactions.length > 0)
				{
					WorldBlockReactionEnvironment environment = new WorldBlockReactionEnvironment(world, x, y, z, bx, by, bz);
					for (BlockReaction reaction : reactions)
					{
						reaction.react(environment);
					}
				}
			}
		}

		{
			Block replacementBlock = world.getBlock(x, y, z);
			if (replacementBlock != this)
			{
				if (replacementBlock instanceof BlockGas)
				{
					replacementBlock.updateTick(world, x, y, z, random);
				}
				return;
			}
		}

		type.preTick(world, x, y, z, random);
		
		//For technical reasons, metadata is a reverse representation of how much gas there is inside a block
		int metadata = 16 - world.getBlockMetadata(x, y, z) - type.getDissipation(world, x, y, z, random);
		boolean requiresTick = type.dissipationRate > 0;
		
		if(metadata <= 0)
		{
			world.setBlockToAir(x, y, z);
			type.onDissipated(world, x, y, z);
			return;
		}
		
		//If density is 0, the block will behave very differently.
		if(type.density == 0)
		{
			//The gas will flow out from its position, but will priorify blocks around with the least amount of gas, and especially air. It will not flow into other blocks.
			int[] metadataList = new int[6];
			int[] priorityList = new int[6];
			int totalFlow = 0;
			int prevMetadata = metadata;

			for(int i = 0; i < 6; i++)
			{
				int xDirection = ringX[i];
				int yDirection = ringY[i];
				int zDirection = ringZ[i];
				
				Block direction2Block = world.getBlock(x + xDirection, y + yDirection, z + zDirection);
				int direction2BlockMetadata = 16 - world.getBlockMetadata(x + xDirection, y + yDirection, z + zDirection);
				
				if(type.canFlowHere(world, x + xDirection, y + yDirection, z + zDirection, prevMetadata))
				{
					direction2BlockMetadata = -1;
					totalFlow += 8;
				}
				else if(direction2Block != this)
				{
					direction2BlockMetadata = 17;
				}
				else if(direction2BlockMetadata < metadata - 1)
				{
					totalFlow += (metadata - direction2BlockMetadata) / 2;
				}

				for(int j = 0; j < 6; j++)
				{
					if(metadataList[j] <= direction2BlockMetadata & j != i)
					{
						continue;
					}

					for(int k = 4; k >= j; k--)
					{
						metadataList[k + 1] = metadataList[k];
						priorityList[k + 1] = priorityList[k];
					}

					metadataList[j] = direction2BlockMetadata;
					priorityList[j] = i;

					break;
				}
			}
			
			mixEqualSortedValues(random, metadataList, priorityList);

			for(int i = 0; i < 6 & metadata > 1; i++)
			{
				int j = priorityList[i];
				int direction2BlockMetadata = metadataList[i];
				int xDirection = ringX[j];
				int yDirection = ringY[j];
				int zDirection = ringZ[j];

				if(direction2BlockMetadata != 17)
				{
					if(direction2BlockMetadata == -1)
					{
						int flow = prevMetadata / 2;
						int transaction = flow * 16 / (totalFlow + 8);
						
						if(transaction < 1) transaction = 1;
						world.setBlock(x + xDirection, y + yDirection, z + zDirection, this, 16 - transaction, 3);
						requiresTick = false;
						metadata -= transaction;
					} else if(direction2BlockMetadata < 16 & direction2BlockMetadata + 1 < metadata)
					{
						int flow = (prevMetadata - direction2BlockMetadata) / 2;
						int transaction = flow * 16 / (totalFlow + 8);

						if(transaction < 1) transaction = 1;
						world.setBlockMetadataWithNotify(x + xDirection, y + yDirection, z + zDirection, 16 - direction2BlockMetadata - transaction, 3);
						requiresTick = false;
						metadata -= transaction;
					}
				}
			}
			
			//Remember to set the new metadata for the gas block.
			if(metadata > 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, 16 - metadata, 3);
			} else
			{
				world.setBlockToAir(x, y, z);
			}
			
			if(requiresTick || type.requiresNewTick(world, x, y, z, random))
			{
				world.scheduleBlockUpdate(x, y, z, this, 10);
			}
			
			type.postTick(world, x, y, z, random);
			
			return;
		}

		int yDirection = type.density > 0 ? -1 : 1;
		Block directionBlock = world.getBlock(x, y + yDirection, z);
		int directionBlockMetadata = 16 - world.getBlockMetadata(x, y + yDirection, z);
		Block reverseDirectionBlock = world.getBlock(x, y - yDirection, z);
		int reverseDirectionBlockMetadata = 16 - world.getBlockMetadata(x, y - yDirection, z);
		
		if(type.canFlowHere(world, x, y + yDirection, z, metadata))
		{
			//If the block in the direction can be flowed into forcefully, it will only move in this direction.
			if(metadata > 0)
			{
				world.setBlock(x, y + yDirection, z, this, 16 - metadata, 3);
			}
			world.setBlockToAir(x, y, z);
		}
		else
		{
			if(directionBlock != this && directionBlock instanceof BlockGas)
			{
				//If the block in the direction is another gas, it will swap the position of the gases according to their densities.
				int directionBlockDensity = ((BlockGas)directionBlock).type.density;

				if((type.density > 0 & type.density > directionBlockDensity) | (type.density < 0 & type.density < directionBlockDensity))
				{
					world.setBlock(x, y, z, directionBlock, 16 - directionBlockMetadata, 3);
					world.setBlock(x, y + yDirection, z, this, 16 - metadata, 3);
					return;
				}
			}
			
			//If the block in the direction is the same gas, it will attempt to fill the other gas block.
			if(directionBlock == this)
			{
				if(directionBlockMetadata < 16)
				{
					if(directionBlockMetadata + metadata < 16)
					{
						world.setBlockToAir(x, y, z);
						world.setBlockMetadataWithNotify(x, y + yDirection, z, 16 - directionBlockMetadata - metadata, 3);

						return;
					} else
					{
						world.setBlockMetadataWithNotify(x, y, z, 32 - directionBlockMetadata - metadata, 3);
						world.setBlockMetadataWithNotify(x, y + yDirection, z, 0, 3);
					}

					metadata -= 16 - directionBlockMetadata;
				}
			}
			
			//If the block in the opposite direction is this gas, it will take the contents of the other block to fill itself
			if(reverseDirectionBlock == this)
			{
				metadata += reverseDirectionBlockMetadata;
			}
			
			//The gas will flow out from its position, but will prioritize blocks around with the least amount of gas, and especially air. It will not flow into other blocks.
			int[] metadataList = new int[4];
			int[] priorityList = new int[4];
			int surroundingAirBlocks = 0;
			int totalFlow = 0;
			int prevMetadata = metadata;
			
			for(int i = 0; i < 4; i++)
			{
				int xDirection = ringsX[i];
				int zDirection = ringsZ[i];

				Block direction2Block = world.getBlock(x + xDirection, y, z + zDirection);
				int direction2BlockMetadata = 16 - world.getBlockMetadata(x + xDirection, y, z + zDirection);

				if(type.canFlowHere(world, x + xDirection, y, z + zDirection, prevMetadata))
				{
					direction2BlockMetadata = -1;
					surroundingAirBlocks++;
					totalFlow += 8;
				} else if(direction2Block != this)
				{
					direction2BlockMetadata = 17;
				}
				else if(direction2BlockMetadata < metadata - 1)
				{
					int flow = (metadata - direction2BlockMetadata) / 2;
					if(direction2BlockMetadata + flow > 16)
					{
						flow = 16 - direction2BlockMetadata;
					}
					totalFlow += flow;
				}

				for(int j = 0; j < 4; j++)
				{
					if(metadataList[j] <= direction2BlockMetadata & j != i)
					{
						continue;
					}

					for(int k = 2; k >= j; k--)
					{
						metadataList[k + 1] = metadataList[k];
						priorityList[k + 1] = priorityList[k];
					}

					metadataList[j] = direction2BlockMetadata;
					priorityList[j] = i;

					break;
				}
			}
			
			mixEqualSortedValues(random, metadataList, priorityList);
			
			//If this block is too small to spread properly, it will attempt to flow along the surface to a gap to be able to move further.
			//Closer gaps are prioritized.
			if(metadata < surroundingAirBlocks + 2)
			{
				int[] indices = new int[ringsX.length];
				this.fillArrayWithIndices(random, indices, 4, 0, 0);
				this.fillArrayWithIndices(random, indices, 4, 4, 4);
				this.fillArrayWithIndices(random, indices, 4, 8, 8);
				for(int i : indices)
				{
					int x0 = x + ringsX[i];
					int z0 = z + ringsZ[i];

					if(type.canFlowHere(world, x0, y, z0, prevMetadata))
					{
						Block direction3Block = world.getBlock(x0, y + yDirection, z0);

						if(type.canFlowHere(world, x0, y + yDirection, z0, prevMetadata) || (direction3Block == this && metadata - world.getBlockMetadata(x0, y + yDirection, z0) <= 0))
						{
							if(i >= 8)
							{
								if(!type.canFlowHere(world, x + movesX[i], y, z + movesZ[i], prevMetadata))
								{
									continue;
								}
							}
							else if(i >= 4)
							{
								if(!type.canFlowHere(world, x + movesX[i], y, z, prevMetadata) && !type.canFlowHere(world, x, y, z + movesZ[i], prevMetadata))
								{
									continue;
								}
							}

							world.setBlockToAir(x, y, z);
							if(metadata > 0)
							{
								world.setBlock(x + movesX[i], y, z + movesZ[i], this, 16 - metadata, 3);
							}
							return;
						}
					}
				}
			}
			
			//If the previous did not happen, the block will then disperse.
			for(int i = 0; i < 4 & metadata > 1; i++)
			{
				int j = priorityList[i];
				int direction2BlockMetadata = metadataList[i];
				int xDirection = ringsX[j];
				int zDirection = ringsZ[j];

				if(direction2BlockMetadata != 17)
				{
					if(direction2BlockMetadata == -1)
					{
						int flow = prevMetadata / 2;
						int transaction = flow * 16 / (totalFlow + 8);
						
						if(direction2BlockMetadata + transaction > 16)
						{
							transaction = 16 - direction2BlockMetadata;
						}
						else if(transaction < 1)
						{
							transaction = 1;
						}
						world.setBlock(x + xDirection, y, z + zDirection, this, 16 - transaction, 3);
						requiresTick = false;
						metadata -= transaction;
					}
					else if(direction2BlockMetadata < 16 & direction2BlockMetadata + 1 < metadata)
					{
						int flow = (prevMetadata - direction2BlockMetadata) / 2;
						int transaction = flow * 16 / (totalFlow + 8);

						if(direction2BlockMetadata + transaction > 16)
						{
							transaction = 16 - direction2BlockMetadata;
						}
						else if(transaction < 1)
						{
							transaction = 1;
						}
						world.setBlockMetadataWithNotify(x + xDirection, y, z + zDirection, 16 - direction2BlockMetadata - transaction, 3);
						requiresTick = false;
						metadata -= transaction;
					}
				}
			}
			
			//Finalizing. Setting the metadata for both this block and the block in the opposite direction to make sure the gases are finite.
			if(metadata > 16)
			{
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
				if(reverseDirectionBlock == this)
				{
					world.setBlockMetadataWithNotify(x, y - yDirection, z, 32 - metadata, 3);
				}
			} else
			{
				if(reverseDirectionBlock == this)
				{
					world.setBlockToAir(x, y - yDirection, z);
				}

				if(metadata > 0)
				{
					world.setBlockMetadataWithNotify(x, y, z, 16 - metadata, 3);
				} else
				{
					world.setBlockToAir(x, y, z);
				}
			}
		}
		
		//If this gas requires a new tick, it will schedule one.
		if(requiresTick || type.requiresNewTick(world, x, y, z, random))
		{
			world.scheduleBlockUpdate(x, y, z, this, getDelayForUpdateByDensity());
		}
		
		type.postTick(world, x, y, z, random);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		if(disableUpdate) return;
		
		int delay = getDelayForUpdateByDensity();
		if(delay <= 0)
		{
			this.updateTick(par1World, par2, par3, par4, par1World.rand);
		}
		else
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this, delay);
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor blockID
	 */
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
	{
		onBlockAdded(par1World, par2, par3, par4);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		onFire(world, x, y, z, world.rand, world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean func_149698_L()
	{
		return false;
	}
	
	/**
	 * Returns whether this block will combust normally.
	 * @return
	 */
	public boolean canCombustNormally()
	{
		return this.type.combustibility.burnRate >= Combustibility.FLAMMABLE.burnRate;
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		type.randomDisplayTick(world, x, y, z, random);
	}

	@Override
	public GasType sampleInteraction(World world, int x, int y, int z, GasType in, ForgeDirection side)
	{
		return type;
	}
}
