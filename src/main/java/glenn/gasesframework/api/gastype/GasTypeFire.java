package glenn.gasesframework.api.gastype;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.MaterialGas;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class GasTypeFire extends GasType
{
	public GasTypeFire()
	{
		super(false, 11, "fire", 0xFFFFFFFF, 0, 0, Combustibility.NONE);
		setOverlayImage(GasesFrameworkAPI.fireOverlayImage);
		setDissipationRate(2);
		setTextureName("gasesframework:gas_fire");
		setDestroyLooseBlocks(true);
	}
	
	/**
	 * This method is called upon gas block construction when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 * @return
	 */
	@Override
	public Block tweakGasBlock(Block block)
	{
		block.setLightLevel(0.5f);
		return super.tweakGasBlock(block);
	}
	
	/**
	 * Called when an entity touches the gas in block form.
	 * @param entity
	 */
    @Override
	public void onTouched(Entity entity)
	{
    	super.onTouched(entity);
		entity.setFire(5);
	}
    
    /**
	 * Called when a gas block of this type evaporates.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
    @Override
    public void onDissipated(World world, int x, int y, int z)
    {
    	if(!world.isRemote)
    	{
			if(Blocks.fire.canPlaceBlockAt(world, x, y, z))
			{
				world.setBlock(x, y, z, Blocks.fire);
			}
			else if(GasesFrameworkAPI.getFireSmokeAmount() > 0 && world.rand.nextInt(4) == 0)
			{
				world.setBlock(x, y, z, GasesFrameworkAPI.gasTypeSmoke.block, 16 - GasesFrameworkAPI.getFireSmokeAmount(), 3);
			}
    	}
    }
    
    /**
	 * Called randomly on the client when the player is around a gas block of this type.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
    @Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
    	if(random.nextInt(12) == 0)
		{
			world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
		}
    	
    	int metadata = world.getBlockMetadata(x, y, z);
    	double minY = this.getMinY(world, x, y, z, metadata);
    	double maxY = this.getMaxY(world, x, y, z, metadata);
    	
    	if(random.nextFloat() < maxY - minY)
    	{
    		double xd = x + random.nextDouble();
    		double yd = y + minY + random.nextDouble() * (maxY - minY);
    		double zd = z + random.nextDouble();
    		
    		world.spawnParticle("largesmoke", xd, yd, zd, 0.0D, 0.0D, 0.0D);
    	}
	}
}