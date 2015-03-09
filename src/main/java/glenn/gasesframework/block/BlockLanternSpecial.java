package glenn.gasesframework.block;

import glenn.gasesframework.GasesFramework;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockLanternSpecial extends BlockLantern
{
	public ItemStack containedItemIn;
	public ItemStack containedItemOut;
	public Block expirationBlock;
	
	/**
	 * Creates a new lantern which contains certain items and can eject another item in return.
	 * @param tickrate - The rate at which the lantern will burn out. Set to 0 for non-expiring lanterns.
	 * @param containedItemIn - The item/block which can be used with a lantern to create this lantern.
	 * @param containedItemOut - The item/block which will be ejected by this lantern. For instance, gas lanterns accept bottles of gas but eject empty bottles.
	 * @param expirationBlock - The block this lantern will become when expired or destroyed. If null, expirationBlock = this
	 */
	public BlockLanternSpecial(int tickrate, ItemStack containedItemIn, ItemStack containedItemOut, Block expirationBlock)
	{
		super(tickrate, containedItemIn);
		
		this.containedItemIn = containedItemIn;
		this.containedItemOut = containedItemOut;
		this.expirationBlock = expirationBlock == null ? this : expirationBlock;
	}

    @Override
	public Block getExpirationBlock()
	{
		return expirationBlock;
	}
	
    @Override
	public ItemStack getContainedItemOut()
	{
		return containedItemOut.copy();
	}
	
    @Override
	public ItemStack getContainedItemIn()
	{
		return containedItemIn.copy();
	}
}