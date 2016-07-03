package glenn.gasesframework.api.gastype;

import glenn.gasesframework.api.Combustibility;
import glenn.gasesframework.api.ExtendedGasEffectsBase.EffectType;
import glenn.gasesframework.api.GFAPI;

import java.util.EnumMap;
import java.util.Random;

import glenn.gasesframework.api.IGFRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class GasType
{
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
	 * The color of this gas type, represented as RGBA hex.
	 */
	public final int color;
	/**
	 * The ability of the gas to stick together. Only applies to 0-density gases. Limited to 0-16. Higher values mean the gas will have a higher threshold of volume before spreading in midair.
	 */
	public final int cohesion;
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
	 * How quickly this gas type will dissipate in the world. Higher values will decrease dissipation speed. If 0, it does not dissipate.
	 */
	public int dissipationRate = 0;
	/**
	 * Will this gas type, when flowing, destroy loose blocks such as redstone, torches and lanterns?
	 */
	public boolean destroyLooseBlocks = false;
	/**
	 * The level of light this gas gives off from 0.0 to 1.0.
	 */
	public float lightLevel = 0.0f;
	/**
	 * The creative tab this gas type is bound to.
	 */
	public CreativeTabs creativeTab;
	
	/**
	 * The rate at which gas of this type will cause various gas effects.
	 */
	private final EnumMap<EffectType, Integer> effectRates = new EnumMap<EffectType, Integer>(EffectType.class);
	
	/**
	 * The overlay image used when the player is inside the gas.
	 */
	public ResourceLocation overlayImage = GFAPI.gasOverlayImage;
	/**
	 * The name of the texture that will be used on the gas block. Default "gasesframework:gas".
	 */
	public String textureName = "gasesframework:gas";

	/**
	 * Get the ID of a gas type with support for null.
	 * @param gasType The gas type
	 * @return The ID of the gas type, or -1 if null
	 */
	public static int getGasID(GasType gasType)
	{
		return gasType != null ? gasType.gasID : -1;
	}
	
	/**
	 * Create a new gas type. Gas types must be {@link IGFRegistry#registerGasType(GasType) registered}.
	 * @param isIndustrial Can this gas be used in pipe systems?
	 * @param gasID The ID of this gas type. Must be unique. Limited to 0-255. Consult the Gases Framework documentation for unoccupied IDs
	 * @param name An unique name for the gas type
	 * @param color An RGBA hex representation of the color to be used by this gas
	 * @param opacity Higher values will increase the opacity of this gas. This will also affect how well light passes through it
	 * @param density A value determining how dense the gas will be relative to air
	 * <ul><li><b>density > 0</b> Will produce a falling gas. Greater values means the gas will fall faster</li>
	 * <li><b>density < 0</b> Will produce a rising gas. Lower values means the gas will rise faster</li>
	 * <li><b>density = 0</b> Will produce a floating gas which will spread in all directions</li></ul>
	 * @param  cohesion The ability of the gas to stick together. Only applies to 0-density gases. Limited to 0-16. Higher values mean the gas 
	 * will have a higher threshold of volume before spreading in midair
	 * @param combustibility The grade of combustibility of this gas type
	 */
	public GasType(boolean isIndustrial, int gasID, String name, int color, int opacity, int density, int cohesion, Combustibility combustibility)
	{
		this.isIndustrial = isIndustrial;
		this.gasID = gasID;
		this.name = name;
        this.color = color;
        this.opacity = opacity;
        this.density = density;
        this.combustibility = combustibility;
        this.cohesion = cohesion;
	}
	
	/**
	 * Sets the rate of a gas effect on this gas type.
	 * @param effectType The effect type which is to be added to the gas
	 * @param value The rate at which this effect will be applied. Greater numbers trigger the effects more quickly
	 * @return this
	 */
	public GasType setEffectRate(EffectType effectType, int value)
    {
		this.effectRates.put(effectType, value);
    	
    	return this;
    }

	/**
	 * Get the rate of a gas effect on this gas type. All effect rates are 0 by default.
	 * @param effectType The effect type
	 * @return The rate of the effect
	 */
	public int getEffectRate(EffectType effectType)
	{
		Integer res = this.effectRates.get(effectType);
		if(res == null)
		{
			return 0;
		}
		else
		{
			return res;
		}
	}

	/**
	 * Set how quickly the gas can dissipate. Higher values will decrease dissipation speed.
	 * @param dissipationRate The dissipation rate
	 * @return this
	 */
	public GasType setDissipationRate(int dissipationRate)
    {
    	this.dissipationRate = dissipationRate;
    	return this;
    }

	/**
	 * Set the level of light this gas gives off from 0.0 to 1.0.
	 * @param lightLevel The light level
	 * @return this
	 */
	public GasType setLightLevel(float lightLevel)
	{
		this.lightLevel = lightLevel;
		return this;
	}

	/**
	 * Set the creative tab this gas is bound to.
	 * @param creativeTab The creative tab
	 * @return this
	 */
	public GasType setCreativeTab(CreativeTabs creativeTab)
	{
		this.creativeTab = creativeTab;
		return this;
	}
	
	/**
	 * Set the texture name of the gas in block form.
	 * @param textureName The texture name
	 * @return this
	 */
	public GasType setTextureName(String textureName)
	{
		this.textureName = textureName;
		return this;
	}
	
	/**
	 * Set the overlay image to be rendered when the player is inside the gas.
	 * Common overlay images are found in {@link GFAPI GFAPI}.
	 * @param overlayImage The overlay image
	 * @return this
	 */
	public GasType setOverlayImage(ResourceLocation overlayImage)
	{
		this.overlayImage = overlayImage;
		return this;
	}
	
	/**
	 * Set whether this gas type, when flowing, will destroy loose blocks such as redstone and torches.
	 * If true, the gas can destroy blocks with materials on the condition {@link net.minecraft.block.material.Material#getMaterialMobility() getMaterialMobility()} == 1. 
	 * If false, the gas can destroy blocks with materials on the condition {@link net.minecraft.block.material.Material#isReplaceable() isReplaceable()}.
	 * @param destroyLooseBlocks Destroy loose blocks
	 * @return this
	 */
	public GasType setDestroyLooseBlocks(boolean destroyLooseBlocks)
	{
		this.destroyLooseBlocks = destroyLooseBlocks;
		return this;
	}

	/**
	 * Apply effects onto an entity when breathed. A gas is breathed when the player runs out of air in their hidden air meter.
	 * How quickly this happens, and how frequently this method is called depends on this gas type's rate of suffocation.
	 * @param entity The entity that is breathing the gas
	 */
	public void onBreathed(EntityLivingBase entity)
	{
		entity.attackEntityFrom(GFAPI.asphyxiationDamageSource, 2.0F);
	}
	
	/**
	 * Called when a gas block of this type dissipates.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 */
	public void onDissipated(World world, int x, int y, int z)
	{}
	
	/**
	 * Called randomly on the client when the player is around a gas block of this type.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param random The random object
	 */
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{}
	
	/**
	 * Is this gas visible?
	 * @return True if this gas is visible
	 */
	public boolean isVisible()
	{
		return (color & 0xff) != 0;
	}
	
	/**
     * Get the relative Y coordinate of the bottom side of the gas block.
     * @param blockAccess The block access
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param volume The volume of the gas block
     * @return The relative minimum Y of the gas block
     */
	public double getMinY(IBlockAccess blockAccess, int x, int y, int z, int volume)
    {
		if(density > 0)
    	{
    		return 0.0D;
    	}
    	else if(density < 0)
    	{
    		return 1.0D - volume / 16.0D;
    	}
    	else
    	{
    		if(GFAPI.implementation.getGasType(blockAccess, x, y - 1, z) == this)
    		{
    			return 0.0D;
    		}
    		boolean b = GFAPI.implementation.getGasType(blockAccess, x, y + 1, z) == this;
    		double d = (0.5D - volume / 8.0D) * (b ? 2.0D : 1.0D);
    		return d < 0.0D ? 0.0D : d;
    	}
    }
	
	/**
     * Get the relative Y coordinate of the bottom side of the gas block.
     * @param blockAccess The block access
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param volume The volume of the gas block
     * @return The relative maximum Y of the gas block
     */
	public double getMaxY(IBlockAccess blockAccess, int x, int y, int z, int volume)
    {
		if(density > 0)
    	{
    		return volume / 16.0D;
    	}
    	else if(density < 0)
    	{
    		return 1.0D;
    	}
    	else
    	{
    		if(GFAPI.implementation.getGasType(blockAccess, x, y + 1, z) == this)
    		{
    			return 1.0D;
    		}
    		boolean b = GFAPI.implementation.getGasType(blockAccess, x, y - 1, z) == this;
    		double d = 1.0D - (0.5D - volume / 8.0D) * (b ? 2.0D : 1.0D);
    		return d > 1.0D ? 1.0D : d;
    	}
    }
	
	/**
	 * Can a gas block of type 'type' forcefully flow into this type of gas?
	 * @param thisVolume The volume of this gas block
	 * @param type The type of gas attempting to flow into this one
	 * @param otherVolume The volume of the other gas block
	 * @return True if the other gas can forcefully flow into this
	 */
	public boolean canBeDestroyedBy(int thisVolume, GasType type, int otherVolume)
	{
		return false;
	}
	
	/**
	 * Can this gas flow to this coordinate?
	 * @param world The world object
	 * @param x The X coordinate this gas can flow to
	 * @param y The Y coordinate this gas can flow to
	 * @param z The z coordinate this gas can flow to
	 * @param volume The volume of this gas block
	 * @return True if the gas can flow to the coordinate
	 */
	public boolean canFlowHere(World world, int x, int y, int z, int volume)
	{
		GasType otherGasType = GFAPI.implementation.getGasType(world, x, y, z);
		if(otherGasType != null)
		{
			return otherGasType.canBeDestroyedBy(GFAPI.implementation.getGasVolume(world, x, y, z), this, volume);
		}
		else
		{
			Material material = world.getBlock(x, y, z).getMaterial();
			if(this.destroyLooseBlocks)
			{
				return !material.isLiquid() && (material.isReplaceable() || material.getMaterialMobility() == 1);
			}
			else
			{
				return !material.isLiquid() && material.isReplaceable();
			}
		}
	}
	
	/**
	 * Get the dissipation of a gas. This triggers every time the gas block ticks. This is affected by the {@link #dissipationRate} of the GasType.
	 * @param world The world object
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param random The random object
	 * @return
	 */
    public int getDissipation(World world, int x, int y, int z, Random random)
    {
    	return dissipationRate > 0 && random.nextInt(dissipationRate) == 0 ? 1 : 0;
    }
    
    /**
     * Called before a gas block of this type ticks.
     * @param world The world object
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param random The random object
     */
    public void preTick(World world, int x, int y, int z, Random random)
    {}
    
    /**
     * Called after a gas block of this type ticks.
     * @param world The world object
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param random The random object
     */
    public void postTick(World world, int x, int y, int z, Random random)
    {}
    
    /**
     * Called at the end of the gas block tick. If true is returned, a new tick will happen.
     * @param world The world object
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param random The random object
     * @return True if another tick is needed
     */
    public boolean requiresNewTick(World world, int x, int y, int z, Random random)
    {
    	return false;
    }
	
	/**
	 * Get the overlay image to be rendered when the player is inside the gas.
	 * @return The overlay image
	 */
	public ResourceLocation getOverlayImage()
	{
		return overlayImage;
	}
	
	/**
	 * Get the unlocalized name of the gas.
	 * @return "gf_gas." + {@link #name};
	 */
	public String getUnlocalizedName()
	{
		return "gf_gas." + name;
	}
}