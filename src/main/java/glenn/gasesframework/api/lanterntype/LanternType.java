package glenn.gasesframework.api.lanterntype;

import glenn.gasesframework.api.ItemKey;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;

public class LanternType
{
	private static final HashMap<ItemKey, LanternType> lanternTypesByItemIn = new HashMap<ItemKey, LanternType>();
	private static final HashMap<String, LanternType> lanternTypesByName = new HashMap<String, LanternType>();
	
	/**
	 * Is this lantern type {@link glenn.gasesframework.api.GasesFrameworkAPI#registerLanternType(LanternType) registered}?
	 */
	public boolean isRegistered = false;
	/**
	 * The lantern block associated with this lantern type.
	 * Is set when the lantern type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerLanternType(LanternType) registered}.
	 */
	public Block block;
	/**
	 * A name for this lantern type. Must be unique.
	 */
	public final String name;
	/**
	 * The level of light emitted by this lantern in an interval from 0.0f to 1.0f.
	 */
	public final float lightLevel;
	/**
	 * The name of the texture that will be used inside the lantern.
	 */
	public final String textureName;
	/**
	 * The item given from this lantern.
	 */
	public ItemKey itemOut;
	/**
	 * The lantern type this lantern will transform into if it expires. Can be null.
	 */
	public final LanternType expirationLanternType;
	/**
	 * The rate at which this lantern will expire. Smaller numbers mean quicker expiration. If <= 0, this lantern type will never expire.
	 */
	public final int expirationRate;
	/**
	 * The items that can be inserted into a lantern to create this type.
	 */
	private final HashSet<ItemKey> itemIn = new HashSet<ItemKey>();
	
	/**
	 * Get a lantern type from an item inserted into the lantern.
	 * @param item
	 * @param itemDamage
	 * @return
	 */
	public static LanternType getLanternTypeByItemIn(ItemKey itemMapping)
	{
		return lanternTypesByItemIn.get(itemMapping);
	}
	
	/**
	 * Get a lantern type from its name.
	 * @param name
	 * @return
	 */
	public static LanternType getLanternTypeByName(String name)
	{
		return lanternTypesByName.get(name);
	}
	
	/**
	 * Get an array of all lantern types, registered or not.
	 * @return
	 */
	public static LanternType[] getAllLanternTypes()
	{
		LanternType[] res = new LanternType[lanternTypesByName.size()];
		int i = 0;
		for(LanternType value : lanternTypesByName.values())
		{
			res[i++] = value;
		}
		return res;
	}
	
	/**
	 * Creates a new lantern type. Lantern types must be {@link glenn.gasesframework.api.GasesFrameworkAPI#registerLanternType(LanternType) registered}.
	 * @param name - An unique name for this lantern type.
	 * @param lightLevel - The level of light emitted by this lantern type in an interval from 0.0f to 1.0f.
	 * @param textureName - The name of the texture displayed inside the lantern.
	 * @param itemOut - The item given from this lantern. NOTE: Not the necessarily same as the item placed in the lantern.
	 * @param expirationLanternType - The lantern type this lantern will transform into if it expires. Can be null.
	 * @param expirationRate - The rate at which this lantern will expire. Smaller numbers mean quicker expiration. If <= 0, this lantern type will never expire.
	 */
	public LanternType(String name, float lightLevel, String textureName, ItemKey itemOut, LanternType expirationLanternType, int expirationRate)
	{
		this.name = name;
		this.lightLevel = lightLevel;
		this.textureName = textureName;
		this.itemOut = itemOut;
		this.expirationLanternType = expirationLanternType;
		this.expirationRate = expirationRate;
		
		map();
	}
	
	private void map()
	{
		LanternType prev = getLanternTypeByName(name);
		if(prev == null)
		{
			lanternTypesByName.put(name, this);
		}
		else
		{
			throw new RuntimeException("A lantern type with name " + name + " attempted to override a lantern type with the same name");
		}
	}
	
	/**
	 * Sets the item to treat the item given from this lantern type as something used to create a lantern of this type.
	 * This is common for non-gas lanterns.
	 * @return
	 */
	public LanternType setInOut()
	{
		return addItemIn(itemOut);
	}
	
	/**
	 * Adds an item that can be inserted into a lantern to create a lantern of this type. Must be unique to all lantern types.
	 * @param item
	 * @return
	 */
	public LanternType addItemIn(ItemKey item)
	{
		if(!lanternTypesByItemIn.containsKey(item))
		{
			lanternTypesByItemIn.put(item, this);
			itemIn.add(item);
		}
		else
		{
			throw new RuntimeException("A lantern type with name " + name + " attempted to override itemIn " + item);
		}
		return this;
	}
	
	/**
	 * Returns true if this item can be placed in a lantern to create this type.
	 * @param item
	 * @return
	 */
	public boolean accepts(ItemKey item)
	{
		return itemIn.contains(item);
	}
	
	/**
	 * Get a list of items that can be placed in a lantern to create a lantern of this type.
	 * @return
	 */
	public ItemKey[] getAllAcceptedItems()
	{
		ItemKey[] res = new ItemKey[itemIn.size()];
		int i = 0;
		for(ItemKey item : itemIn)
		{
			res[i++] = item;
		}
		return res;
	}
	
	/**
	 * This method is called upon lantern block construction when the lantern type is {@link glenn.gasesframework.api.GasesFrameworkAPI#registerLanternType(LanternType) registered}.
	 * @return
	 */
	public Block tweakLanternBlock(Block block)
	{
		return block;
	}
	
	public boolean expires()
	{
		return expirationRate > 0;
	}
	
	/**
	 * Get the unlocalized name of the lantern.
	 * @return "gf_lantern." + name;
	 */
	public String getUnlocalizedName()
	{
		return "gf_lantern." + name;
	}
}