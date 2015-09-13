package glenn.gasesframework.common.tileentity;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.common.block.BlockGasFurnace;
import glenn.moddingutils.blockrotation.BlockRotation;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityGasFurnace extends TileEntity implements ISidedInventory
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

	private final int smokeEmissionInterval;
	private final int maxFuelLevel;
	private final int temperaturePerFuel;
	private final int temperatureFalloff;
	
	public int fuelLevel;

	/** The number of ticks that the current item has been cooking for */
	public int cookTime = 0;
	public int temperature = 0;
	public int smokeTimer = 0;
	private String invName;
	
	public int prevStage;
	
	private static final int maxTemperature = 8000;
	
	public TileEntityGasFurnace(int smokeEmissionInterval, int maxFuelLevel, int temperaturePerFuel, int temperatureFalloff)
	{
		this.smokeEmissionInterval = smokeEmissionInterval;
		this.maxFuelLevel = maxFuelLevel;
		this.temperaturePerFuel = temperaturePerFuel;
		this.temperatureFalloff = temperatureFalloff;
	}

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
	public ItemStack getStackInSlot(int slotIndex)
	{
		return this.furnaceItemStacks[slotIndex];
	}
	
	public static SpecialFurnaceRecipe getSpecialFurnaceRecipe(ItemStack itemStack)
	{
		for(SpecialFurnaceRecipe recipe : specialFurnaceRecipes)
		{
			if(recipe.is(itemStack)) return recipe;
		}
		
		return null;
	}
	
	public int getCurrentItemCookTime()
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
	public ItemStack decrStackSize(int slotIndex, int amount)
	{
		if (this.furnaceItemStacks[slotIndex] != null)
		{
			ItemStack itemstack;

			if (this.furnaceItemStacks[slotIndex].stackSize <= amount)
			{
				itemstack = this.furnaceItemStacks[slotIndex];
				this.furnaceItemStacks[slotIndex] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.furnaceItemStacks[slotIndex].splitStack(amount);

				if (this.furnaceItemStacks[slotIndex].stackSize == 0)
				{
					this.furnaceItemStacks[slotIndex] = null;
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
	public ItemStack getStackInSlotOnClosing(int slotIndex)
	{
		if (this.furnaceItemStacks[slotIndex] != null)
		{
			ItemStack itemstack = this.furnaceItemStacks[slotIndex];
			this.furnaceItemStacks[slotIndex] = null;
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
	public void setInventorySlotContents(int slotIndex, ItemStack itemStack)
	{
		this.furnaceItemStacks[slotIndex] = itemStack;

		if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit())
		{
			itemStack.stackSize = this.getInventoryStackLimit();
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
	public void setGuiDisplayName(String invName)
	{
		this.invName = invName;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);
		NBTTagList slotsTagList = tagCompound.getTagList("Items", 10);
		this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < slotsTagList.tagCount(); ++i)
		{
			NBTTagCompound slotCompound = slotsTagList.getCompoundTagAt(i);
			byte b0 = slotCompound.getByte("Slot");

			if (b0 >= 0 && b0 < this.furnaceItemStacks.length)
			{
				this.furnaceItemStacks[b0] = ItemStack.loadItemStackFromNBT(slotCompound);
			}
		}

		this.fuelLevel = tagCompound.getShort("BurnTime");
		this.cookTime = tagCompound.getShort("CookTime");
		this.temperature = tagCompound.getShort("CookSpeed");
		
		if (tagCompound.hasKey("SmokeTimer"))
		{
			this.smokeTimer = tagCompound.getShort("SmokeTimer");
		}

		if (tagCompound.hasKey("CustomName"))
		{
			this.invName = tagCompound.getString("CustomName");
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		tagCompound.setShort("BurnTime", (short)this.fuelLevel);
		tagCompound.setShort("CookTime", (short)this.cookTime);
		tagCompound.setShort("CookSpeed", (short)this.temperature);
		tagCompound.setShort("SmokeTimer", (short)this.smokeTimer);
		NBTTagList slotsTagList = new NBTTagList();

		for (int i = 0; i < this.furnaceItemStacks.length; ++i)
		{
			if (this.furnaceItemStacks[i] != null)
			{
				NBTTagCompound slotCompound = new NBTTagCompound();
				slotCompound.setByte("Slot", (byte)i);
				this.furnaceItemStacks[i].writeToNBT(slotCompound);
				slotsTagList.appendTag(slotCompound);
			}
		}

		tagCompound.setTag("Items", slotsTagList);

		if (this.hasCustomInventoryName())
		{
			tagCompound.setString("CustomName", this.invName);
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
	public int getCookProgressScaled(int scale)
	{
		return this.cookTime * scale / (getCurrentItemCookTime() * 1000);
	}

	/**
	 * Returns true if the furnace is currently burning
	 */
	public boolean isBurning()
	{
		return this.temperature > 0;
	}
	
	public void handleSmoke()
	{
		if(isBurning() && smokeTimer++ > 100)
		{
			BlockRotation rotation = BlockRotation.getRotation(getBlockMetadata());
			ForgeDirection pushDirection = rotation.rotateInverse(ForgeDirection.NORTH);
			
			int x = xCoord + pushDirection.offsetX;
			int y = yCoord + pushDirection.offsetY;
			int z = zCoord + pushDirection.offsetZ;

			BlockGasFurnace block = (BlockGasFurnace)getBlockType();
			int pressure = block.getPressureFromSide(worldObj, xCoord, yCoord, zCoord, pushDirection);
			if (GasesFramework.implementation.pushGas(worldObj, worldObj.rand, x, y, z, GasesFramework.gasTypeSmoke, pushDirection, pressure))
			{
				smokeTimer = 0;
			}
		}
	}
	
	public int getStage()
	{
		return (int)Math.ceil(4.0D * temperature / maxTemperature);
	}

	/**
	 * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
	 * ticks and creates a new spawn inside its implementation.
	 */
	@Override
	public void updateEntity()
	{
		int stage = getStage();
		boolean dirty = stage != prevStage;
		
		burnFuel();

		if (!this.worldObj.isRemote)
		{
			cook();

			if (dirty)
			{
				BlockGasFurnace block = (BlockGasFurnace)getBlockType();
				block.updateFurnaceBlockState(stage, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}
		else
		{
			if (dirty)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
		
		if(!this.canSmelt())
		{
			this.cookTime = 0;
		}

		prevStage = stage;
	}
	
	private void burnFuel()
	{
		this.temperature -= temperatureFalloff;
		if (temperature < 0)
		{
			temperature = 0;
		}
		
		if (fuelLevel > 0)
		{
			if (temperature + temperaturePerFuel <= maxTemperature)
			{
				temperature += temperaturePerFuel;
				fuelLevel--;
			}
		}
	}
	
	private void cook()
	{
		handleSmoke();
		
		if (!isChoked() && this.canSmelt())
		{
			this.cookTime += this.temperature;

			if (this.cookTime >= getCurrentItemCookTime() * 1000)
			{
				this.cookTime -= getCurrentItemCookTime() * 1000;
				this.smeltItem();
			}
		}
	}
	
	public boolean isChoked()
	{
		return smokeTimer > 150;
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
		
		SpecialFurnaceRecipe recipe = getSpecialFurnaceRecipe(this.furnaceItemStacks[0]);
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
				SpecialFurnaceRecipe recipe = getSpecialFurnaceRecipe(this.furnaceItemStacks[0]);
				
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

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer)
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : entityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemStack)
	{
		return slotIndex == 0;
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return side == 0 ? slots_bottom : slots_top_sides;
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canInsertItem(int slotIndex, ItemStack itemStack, int side)
	{
		return this.isItemValidForSlot(slotIndex, itemStack);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side)
	{
		return slotIndex != 0;
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
	
	public int getFuelStored()
	{
		return fuelLevel;
	}
	
	public int getMaxFuelStored()
	{
		return maxFuelLevel;
	}
}
