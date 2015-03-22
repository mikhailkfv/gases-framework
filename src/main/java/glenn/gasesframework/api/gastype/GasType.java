package glenn.gasesframework.api.gastype;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.GasesFrameworkAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class GasType
{
	private static final GasType[] gasTypesByID = new GasType[256];
	private static final HashMap<String, GasType> gasTypesByName = new HashMap<String, GasType>();
	private static final HashMap<String, GasType> gasTypesByItem = new HashMap<String, GasType>();
	
	/**
	 * Is this gas type {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}?
	 */
	public boolean isRegistered = false;
	/**
	 * The gas block associated with this gas type.
	 * Is set when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 */
	public Block block;
	/**
	 * The gas pipe block associated with this gas type. Can be null if the gas type is {@link #isIndustrial industrial}.
	 * Is set when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 */
	public Block pipeBlock;
	/**
	 * Can this gas be used in pipe systems?
	 */
	public final boolean isIndustrial;
	/**
	 * The ID of this gas type. Must be unique. Limited to 0-255. Consult the Gases Framework documentation for unoccupied IDs.
	 */
	public final int gasID;
	/**
	 * A name for this gas type. Must be unique.
	 */
	public final String name;
	/**
	 * The color of this gas type, represented as an RGB hex.
	 */
	public final int color;
	/**
	 * The opacity of this gas type. A highly opaque gas will block more light.
	 */
	public final int opacity;
	/**
	 * The density of this gas type. A negative density will make a rising gas. A positive density will make a falling gas. If The density is 0, the gas will spread evenly in all directions.
	 */
	public final int density;
	
	/**
	 * The combustibility of this gas. See {@link glenn.gasesframework.api.Combustibility Combustibility}.
	 */
	public final Combustibility combustibility;
	/**
	 * How quickly this gas type will evaporate in the world. Higher values will decrease evaporation speed. If 0, it does not evaporate.
	 */
	public int evaporationRate = 0;
	/**
	 * The damage this gas deals upon touch.
	 */
	public float damage = 0.0f;
	
	/**
	 * The rate at which gas of this type will cause blindness.
	 */
	public int blindnessRate = 0;
	/**
	 * The rate at which gas of this type will cause suffocation. At higher values, the player will suffocate earlier and will take damage more frequently.
	 */
	public int suffocationRate = 0;
	/**
	 * How much this gas will gradually slow down the player.
	 */
	public int slownessRate = 0;
	/**
	 * The overlay image used when the player is inside the gas.
	 */
	public ResourceLocation overlayImage = GasesFrameworkAPI.gasOverlayImage;
	/**
	 * The name of the texture that will be used on the gas block. Default "gasesframework:gas"
	 */
	public String textureName = "gasesframework:gas";
	
	/**
	 * This field is a little buggy.
	 */
	private static final HashSet<String> noisyPeople = new HashSet<String>();
	private static final Random soundRandom = new Random();
	
	static
	{
		noisyPeople.add("cyanideepic");
		noisyPeople.add("dethridgecraft");
		noisyPeople.add("wyld");
		noisyPeople.add("crustymustard");
		noisyPeople.add("glenna");
		noisyPeople.add("trentv4");
		noisyPeople.add("username720");
	}
	
	/**
	 * Get a gas type by its gasID.
	 * @param gasID
	 * @return
	 */
	public static GasType getGasTypeByID(int gasID)
	{
		if(gasID >= 0 && gasID < gasTypesByID.length)
		{
			return gasTypesByID[gasID];
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Get a gas type by its name.
	 * @param name
	 * @return
	 */
	public static GasType getGasTypeByName(String name)
	{
		return gasTypesByName.get(name);
	}
	
	/**
	 * Get an array of all gas types, registered or not.
	 * @return
	 */
	public static GasType[] getAllTypes()
	{
		ArrayList<GasType> list = new ArrayList<GasType>();
		for(GasType type : gasTypesByID)
		{
			if(type != null)
			{
				list.add(type);
			}
		}
		
		GasType[] res = new GasType[list.size()];
		list.toArray(res);
		return res;
	}
	
	private void map()
	{
		GasType prev = getGasTypeByID(gasID);
		if(prev == null)
		{
			gasTypesByID[gasID] = this;
		}
		else
		{
			throw new RuntimeException("A gas type with name " + name + " attempted to override a gas type with name " + prev.name + " with gasID " + gasID);
		}
		
		prev = getGasTypeByName(name);
		if(prev == null)
		{
			gasTypesByName.put(name, this);
		}
		else
		{
			throw new RuntimeException("A gas type with name " + name + " attempted to override a gas type with the same name");
		}
	}
	
	/**
	 * Creates a new gas type. Gas types must be {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 * @param isIndustrial - Can this gas be used in pipe systems?
	 * @param gasID - The ID of this gas type. Must be unique. Limited to 0-255. Consult the Gases Framework documentation for unoccupied IDs.
	 * @param name - An unique name for the gas type.
	 * @param color - An RGBA representation of the color to be used by this gas.
	 * @param opacity - Higher values will increase the opacity of this gas. This will also affect how well light passes through it.
	 * @param density - A value determining how dense the gas will be relative to air.
	 * <ul><li><b>density > 0</b> Will produce a falling gas. Greater values means the gas will move faster.</li>
	 * <li><b>density < 0</b> Will produce a rising gas. Lower values means the gas will move faster.</li>
	 * <li><b>density = 0</b> Will produce a floating gas which will spread in all directions.</li></ul>
	 * @param combustibility - The grade of combustibility of this gas type.
	 */
	public GasType(boolean isIndustrial, int gasID, String name, int color, int opacity, int density, Combustibility combustibility)
	{
		this.isIndustrial = isIndustrial;
		this.gasID = gasID;
		this.name = name;
        this.color = color;
        this.opacity = opacity;
        this.density = density;
        this.combustibility = combustibility;
		
        map();
	}
	
	/**
	 * Sets the rates of gas effects on this gas type.
	 * @param blindness - How quickly the player will experience gradual blindness.
	 * @param suffocation - How quickly the player will suffocate in the gas, and how often {@link GasType#onBreathed(EntityLivingBase)} will be called
	 * @param slowness - How quickly the player will lose their movement speed inside the gas.
	 * @return
	 */
	public GasType setEffectRates(int blindness, int suffocation, int slowness)
    {
    	this.blindnessRate = blindness;
    	this.suffocationRate = suffocation;
    	this.slownessRate = slowness;
    	
    	if(blindnessRate <= 0) blindnessRate = -4;
    	if(suffocationRate <= 0) suffocationRate = -16;
    	if(slownessRate <= 0) slownessRate = -32;
    	
    	return this;
    }
	
	/**
	 * Set how much damage the gas will deal upon contact with the player.
	 * @param damage
	 * @return
	 */
	public GasType setDamage(float damage)
	{
		this.damage = damage;
		return this;
	}
	
	/**
	 * Set how quickly the gas can evaporate. Higher values will decrease evaporation speed.
	 * @param evaporation
	 * @return
	 */
	public GasType setEvaporationRate(int evaporation)
    {
    	this.evaporationRate = evaporation;
    	return this;
    }
	
	/**
	 * Set the texture name of the gas in block form.
	 * @param textureName
	 * @return
	 */
	public GasType setTextureName(String textureName)
	{
		this.textureName = textureName;
		return this;
	}
	
	/**
	 * Set the overlay image to be rendered when the player is inside the gas.
	 * @param overlayImage
	 * @return
	 */
	public GasType setOverlayImage(ResourceLocation overlayImage)
	{
		this.overlayImage = overlayImage;
		return this;
	}
	
	/**
	 * Get the bottled item. Is {@link GasesFramework#gasBottle} by default.
	 * @return
	 */
	public ItemStack getBottledItem()
	{
		return new ItemStack(GasesFrameworkAPI.gasBottle, 1, gasID);
	}
	
	/**
	 * This method is called upon gas block construction when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 * @return
	 */
	public Block tweakGasBlock(Block block)
	{
		return block;
	}
	
	/**
	 * This method is called upon gas pipe block construction when the gas type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerGasType(GasType) registered}.
	 * @return
	 */
	public Block tweakPipeBlock(Block block)
	{
		return block;
	}
	
	/**
	 * Apply effects onto an entity when breathed. A gas is breathed when the player runs out of air in their hidden air meter.
	 * How quickly this happens, and how frequently this method is called depends on this gas type's {@link GasType#suffocationRate}.
	 * @param entity
	 */
	public void onBreathed(EntityLivingBase entity)
	{
		entity.attackEntityFrom(GasesFrameworkAPI.asphyxiationDamageSource, 2.0F);
	}
	
	/**
	 * Called when an entity touches the gas in block form.
	 * @param entity
	 */
	public void onTouched(Entity entity)
	{
		if(damage > 0.0F & !(entity instanceof EntityItem))
    	{
    		entity.attackEntityFrom(DamageSource.generic, damage);
    	}
		
		if(entity instanceof EntityPlayer)
		{
			String displayName = ((EntityPlayer)entity).getDisplayName().toLowerCase();
			if(noisyPeople.contains(displayName) && soundRandom.nextInt(20) == 0)
			{
				entity.worldObj.playSoundAtEntity(entity, "mob.villager.idle", 1.0F, 0.75F + soundRandom.nextFloat() * 0.5F);
			}
		}
	}
	
	/**
	 * Called when a gas block of this type evaporates.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void onEvaporated(World world, int x, int y, int z)
	{
		
	}
	
	/**
	 * Called randomly on the client when the player is around a gas block of this type.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 */
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		
	}
	
	/**
	 * Is this gas visible?
	 */
	public boolean isVisible()
	{
		return (color & 0xff) != 0;
	}
	
	/**
     * Get the relative Y coordinate of the bottom side of the gas block.
     * @param blockAccess
     * @param x
     * @param y
     * @param z
     * @param metadata
     * @return
     */
	public double getMinY(IBlockAccess blockAccess, int x, int y, int z, int metadata)
    {
		if(density > 0)
    	{
    		return 0.0D;
    	}
    	else if(density < 0)
    	{
    		return (double)metadata / 16.0D;
    	}
    	else
    	{
    		if(blockAccess.getBlock(x, y - 1, z) == block)
    		{
    			return 0.0D;
    		}
    		boolean b = blockAccess.getBlock(x, y + 1, z) == block;
    		double d = (0.5D - (double)(16 - metadata) / 8.0D) * (b ? 2.0D : 1.0D);
    		return d < 0.0D ? 0.0D : d;
    	}
    }
	
	/**
     * Get the relative Y coordinate of the bottom side of the gas block.
     * @param blockAccess
     * @param x
     * @param y
     * @param z
     * @param metadata
     * @return
     */
	public double getMaxY(IBlockAccess blockAccess, int x, int y, int z, int metadata)
    {
		if(density > 0)
    	{
    		return 1.0D - (double)metadata / 16.0D;
    	}
    	else if(density < 0)
    	{
    		return 1.0D;
    	}
    	else
    	{
    		if(blockAccess.getBlock(x, y + 1, z) == block)
    		{
    			return 1.0D;
    		}
    		boolean b = blockAccess.getBlock(x, y - 1, z) == block;
    		double d = 1.0D - (0.5D - (double)(16 - metadata) / 8.0D) * (b ? 2.0D : 1.0D);
    		return d > 1.0D ? 1.0D : d;
    	}
    }
	
	/**
	 * Can a gas block of type 'type' forcefully flow into this type of gas?
	 * @param type - The type of gas attempting to flow into this one.
	 * @param thisMetadata - The metadata of this gas block.
	 * @param otherMetadata - The metadata of the other gas block.
	 * @return
	 */
	public boolean canBeDestroyedBy(GasType type, int thisMetadata, int otherMetadata)
	{
		return false;
	}
	
	/**
	 * Get the decay of a gas. This triggers every time the gas block ticks.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param random
	 * @return
	 */
    public int getGasDecay(World world, int x, int y, int z, Random random)
    {
    	return evaporationRate > 0 && random.nextInt(evaporationRate) == 0 ? 1 : 0;
    }
    
    /**
     * Called before a gas block of this type ticks.
     * @param world
     * @param x
     * @param y
     * @param z
     * @param random
     */
    public void preTick(World world, int x, int y, int z, Random random)
    {
    	
    }
    
    /**
     * Called after a gas block of this type ticks.
     * @param world
     * @param x
     * @param y
     * @param z
     * @param random
     */
    public void postTick(World world, int x, int y, int z, Random random)
    {
    	
    }
    
    /**
     * Called at the end of the gas block tick. If this returns true, a new tick is guaranteed.
     * @param world
     * @param x
     * @param y
     * @param z
     * @param random
     * @return
     */
    public boolean requiresNewTick(World world, int x, int y, int z, Random random)
    {
    	return false;
    }
	
	/**
	 * Get the overlay image to be renderer when the player is inside the gas.
	 * @return
	 */
	public ResourceLocation getOverlayImage()
	{
		return overlayImage;
	}
	
	/**
	 * Get the unlocalized name of the gas.
	 * @return "gf_gas." + name;
	 */
	public String getUnlocalizedName()
	{
		return "gf_gas." + name;
	}
}