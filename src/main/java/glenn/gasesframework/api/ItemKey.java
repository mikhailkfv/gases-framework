package glenn.gasesframework.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemKey
{
	public final Item item;
	public final int damage;
	
	public ItemKey()
	{
		this(null, 0);
	}
	
	public ItemKey(Item item)
	{
		this(item, 0);
	}
	
	public ItemKey(Item item, int damage)
	{
		this.item = item;
		this.damage = damage;
	}
	
	public ItemKey(ItemStack itemStack)
	{
		if(itemStack != null)
		{
			this.item = itemStack.getItem();
			this.damage = 0;
		}
		else
		{
			this.item = null;
			this.damage = 0;
		}
	}
	
	public ItemStack itemStack()
	{
		if(item != null)
		{
			return new ItemStack(item, 1, damage);
		}
		else
		{
			return null;
		}
	}
	
	public int hashCode()
	{
    	int itemHash = item != null ? item.hashCode() : 0;
    	int damageHash = Integer.hashCode(damage);
    	return (itemHash + damageHash) * damageHash + itemHash;
    }

    public boolean equals(Object otherObject)
    {
    	if(otherObject instanceof ItemKey)
    	{
    		ItemKey other = (ItemKey)otherObject;
    		return this.item == other.item && this.damage == other.damage;
    	}
    	return false;
    }

    public String toString()
    { 
    	if(item != null)
    	{
    		return "(" + item.toString() + ":" + damage + ")"; 
    	}
    	else
    	{
    		return "(null:" + damage + ")";
    	}
    }
}