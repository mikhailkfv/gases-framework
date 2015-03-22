package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.common.block.BlockGasFurnace;

import java.util.ArrayList;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGasFurnace extends TileEntity implements ISidedInventory
{

	public static class SpecialFurnaceRecipe
	{
		public final ItemStack ingredient;
		public final ItemStack result;
		public final int cookTime;
		
		public SpecialFurnaceRecipe(ItemStack ingredient, ItemStack result, int cookTime)
		{
			this.ingredient = ingredient;
			this.result = result;
			this.cookTime = cookTime;
		}
		
		public boolean is(ItemStack itemStack)
		{
			return itemStack.getItem() == ingredient.getItem() & itemStack.getItemDamage() == ingredient.getItemDamage() & itemStack.stackSize >= ingredient.stackSize;
		}
	}
	
    private static final int[] slots_top_sides = new int[] {0};
    private static final int[] slots_bottom = new int[] {1};
    
    public static final ArrayList<SpecialFurnaceRecipe> specialFurnaceRecipes = new ArrayList<SpecialFurnaceRecipe>();

    /**
     * The ItemStacks that hold the items currently being used in the furnace
     */
    private ItemStack[] furnaceItemStacks = new ItemStack[2];

    /** The number of ticks that the furnace will keep burning */
    public int furnaceBurnTime;

    /** The number of ticks that the current item has been cooking for */
    public int furnaceCookTime = 0;
    public int furnaceCookSpeed = 0;
    public int smokeTimer = 0;
    private String invName;
    
    public int prevStage;
    
    
    public static final int maxFurnaceBurnTime = 1000;
    public static final int maxFurnaceCookSpeed = 8000;

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return this.furnaceItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.furnaceItemStacks[par1];
    }
    
    public static SpecialFurnaceRecipe getSpecialFurnaceRecipe(ItemStack itemStack)
    {
    	for(SpecialFurnaceRecipe recipe : specialFurnaceRecipes)
    	{
    		if(recipe.is(itemStack)) return recipe;
    	}
    	
    	return null;
    }
    
    public int getCurrentItemBurnTime()
    {
    	if(this.furnaceItemStacks[0] == null)
    	{
    		return 200;
    	}
    	
    	SpecialFurnaceRecipe recipe = getSpecialFurnaceRecipe(this.furnaceItemStacks[0]);
    	
    	if(recipe != null)
    	{
    		return recipe.cookTime;
    	}
    	else
    	{
    		return 200;
    	}
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack itemstack;

            if (this.furnaceItemStacks[par1].stackSize <= par2)
            {
                itemstack = this.furnaceItemStacks[par1];
                this.furnaceItemStacks[par1] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.furnaceItemStacks[par1].splitStack(par2);

                if (this.furnaceItemStacks[par1].stackSize == 0)
                {
                    this.furnaceItemStacks[par1] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.furnaceItemStacks[par1] != null)
        {
            ItemStack itemstack = this.furnaceItemStacks[par1];
            this.furnaceItemStacks[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.furnaceItemStacks[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.invName : "container.gasFurnace";
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    @Override
    public boolean hasCustomInventoryName()
    {
        return this.invName != null && this.invName.length() > 0;
    }

    /**
     * Sets the custom display name to use when opening a GUI linked to this tile entity.
     */
    public void setGuiDisplayName(String par1Str)
    {
        this.invName = par1Str;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
        this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.furnaceItemStacks.length)
            {
                this.furnaceItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.furnaceBurnTime = par1NBTTagCompound.getShort("BurnTime");
        this.furnaceCookTime = par1NBTTagCompound.getShort("CookTime");
        this.furnaceCookSpeed = par1NBTTagCompound.getShort("CookSpeed");
        
        if (par1NBTTagCompound.hasKey("SmokeTimer"))
        {
            this.smokeTimer = par1NBTTagCompound.getShort("SmokeTimer");
        }

        if (par1NBTTagCompound.hasKey("CustomName"))
        {
            this.invName = par1NBTTagCompound.getString("CustomName");
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BurnTime", (short)this.furnaceBurnTime);
        par1NBTTagCompound.setShort("CookTime", (short)this.furnaceCookTime);
        par1NBTTagCompound.setShort("CookSpeed", (short)this.furnaceCookSpeed);
        par1NBTTagCompound.setShort("SmokeTimer", (short)this.smokeTimer);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.furnaceItemStacks.length; ++i)
        {
            if (this.furnaceItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.furnaceItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        par1NBTTagCompound.setTag("Items", nbttaglist);

        if (this.hasCustomInventoryName())
        {
            par1NBTTagCompound.setString("CustomName", this.invName);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    public int getCookProgressScaled(int par1)
    {
        return this.furnaceCookTime * par1 / (getCurrentItemBurnTime() * 1000);
    }

    /**
     * Returns true if the furnace is currently burning
     */
    public boolean isBurning()
    {
        return this.furnaceCookSpeed > 0;
    }
    
    public void handleSmoke()
    {
    	if(isBurning() && smokeTimer++ > 100)
    	{
    		if(GasesFrameworkAPI.fillWithGas(worldObj, worldObj.rand, xCoord, yCoord + 1, zCoord, GasesFrameworkAPI.gasTypeSmoke))
    		{
    			smokeTimer = 0;
    		}
    		else
    		{
    			Block blockAbove = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
    			if(blockAbove instanceof IGasReceptor)
    			{
    				if(((IGasReceptor)blockAbove).receiveGas(worldObj, xCoord, yCoord + 1, zCoord, ForgeDirection.DOWN, GasesFrameworkAPI.gasTypeSmoke)) smokeTimer = 0;
    			}
    		}
    	}
    }
    
    public int getStage()
    {
    	return (int)Math.ceil(4.0D * furnaceCookSpeed / maxFurnaceCookSpeed);
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void updateEntity()
    {
    	int stage = getStage();
    	
        boolean flag1 = false;

        if (!this.worldObj.isRemote)
        {
        	handleSmoke();
        	
            if (smokeTimer <= 150 && this.canSmelt())
            {
                this.furnaceCookTime += this.furnaceCookSpeed;

                if (this.furnaceCookTime >= getCurrentItemBurnTime() * 1000)
                {
                    this.furnaceCookTime -= getCurrentItemBurnTime() * 1000;
                    this.smeltItem();
                    flag1 = true;
                }
            }

            if (prevStage != stage)
            {
                flag1 = true;
                BlockGasFurnace.updateFurnaceBlockState(stage, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }
        
        if(!this.canSmelt())
        {
        	this.furnaceCookTime = 0;
        }

        if (this.furnaceBurnTime > 0)
        {
            --this.furnaceBurnTime;
        	this.furnaceCookSpeed += GasesFramework.configurations.other_gasFurnaceHeatingSpeed;
        }
        else
        {
        	this.furnaceCookSpeed -= GasesFramework.configurations.other_gasFurnaceHeatingSpeed;
        }
        
        if(this.furnaceCookSpeed < 0)
        {
        	this.furnaceCookSpeed = 0;
        }
        else if(this.furnaceCookSpeed > maxFurnaceCookSpeed)
        {
        	this.furnaceCookSpeed = maxFurnaceCookSpeed;
        }

        if (flag1)
        {
            this.markDirty();
        }

        if (prevStage != stage)
        {
        	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        
        prevStage = stage;
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    public boolean canSmelt()
    {
        if (this.furnaceItemStacks[0] == null)
        {
            return false;
        }
        
        ItemStack itemstack = null;
        
    	SpecialFurnaceRecipe recipe = this.getSpecialFurnaceRecipe(this.furnaceItemStacks[0]);
    	if(recipe != null)
    	{
    		itemstack = recipe.result;
    	}
        
        if(itemstack == null)
        {
        	itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
        }
        
        if (itemstack != null)
        {
        	if (this.furnaceItemStacks[1] == null) return true;
	        if (!this.furnaceItemStacks[1].isItemEqual(itemstack)) return false;
	        int result = furnaceItemStacks[1].stackSize + itemstack.stackSize;
	        return (result <= getInventoryStackLimit() && result <= itemstack.getMaxStackSize());
        }
        
        return false;
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
            
            if(itemstack == null)
            {
            	SpecialFurnaceRecipe recipe = this.getSpecialFurnaceRecipe(this.furnaceItemStacks[0]);
            	
            	itemstack = recipe.result;
            	
            	if (this.furnaceItemStacks[1] == null)
                {
                    this.furnaceItemStacks[1] = itemstack.copy();
                }
                else if (this.furnaceItemStacks[1].isItemEqual(itemstack))
                {
                    furnaceItemStacks[1].stackSize += itemstack.stackSize;
                }

                this.furnaceItemStacks[0].stackSize -= recipe.ingredient.stackSize;
                
                if (this.furnaceItemStacks[0].stackSize <= 0)
                {
                    this.furnaceItemStacks[0] = null;
                }
            }
            else
            {
            	if (this.furnaceItemStacks[1] == null)
                {
                    this.furnaceItemStacks[1] = itemstack.copy();
                }
                else if (this.furnaceItemStacks[1].isItemEqual(itemstack))
                {
                    furnaceItemStacks[1].stackSize += itemstack.stackSize;
                }

                --this.furnaceItemStacks[0].stackSize;

                if (this.furnaceItemStacks[0].stackSize <= 0)
                {
                    this.furnaceItemStacks[0] = null;
                }
            }
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return par1 == 0;
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int par1)
    {
        return par1 == 0 ? slots_bottom : slots_top_sides;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canInsertItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return this.isItemValidForSlot(par1, par2ItemStack);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    @Override
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3)
    {
        return par1 != 0;
    }
    
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound localNBTTagCompound = new NBTTagCompound();
		writeToNBT(localNBTTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 5, localNBTTagCompound);
	}
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
    	readFromNBT(packet.func_148857_g());
    }
}
