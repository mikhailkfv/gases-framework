package glenn.gasesframework.init;

import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.common.block.BlockGasTransposer;
import glenn.gasesframework.common.block.BlockInfiniteGasDrain;
import glenn.gasesframework.common.block.BlockInfiniteGasPump;
import glenn.gasesframework.common.block.BlockIronGasCollector;
import glenn.gasesframework.common.block.BlockIronGasDynamo;
import glenn.gasesframework.common.block.BlockIronGasFurnace;
import glenn.gasesframework.common.block.BlockIronGasPump;
import glenn.gasesframework.common.block.BlockIronGasTank;
import glenn.gasesframework.common.block.BlockWoodGasCollector;
import glenn.gasesframework.common.block.BlockWoodGasDynamo;
import glenn.gasesframework.common.block.BlockWoodGasFurnace;
import glenn.gasesframework.common.block.BlockWoodGasPump;
import glenn.gasesframework.common.block.BlockWoodGasTank;
import net.minecraft.block.Block;

public class GFBlocks
{
	public final Block ironGasPump = new BlockIronGasPump().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_ironGasPump").setBlockTextureName("gasesframework:pump_iron");
	public final Block woodGasPump = new BlockWoodGasPump().setHardness(2.5F).setStepSound(Block.soundTypeWood).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_woodGasPump").setBlockTextureName("gasesframework:pump_wood");
	public final Block gasTank = new BlockIronGasTank().setHardness(3.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_ironGasTank").setBlockTextureName("gasesframework:tank_iron");
	public final Block woodGasTank = new BlockWoodGasTank().setHardness(3.5F).setStepSound(Block.soundTypeWood).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_woodGasTank").setBlockTextureName("gasesframework:tank_wood");
	public final Block gasCollector = new BlockIronGasCollector().setHardness(2.5F).setStepSound(Block.soundTypeStone).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_ironGasCollector").setBlockTextureName("gasesframework:collector_iron");
	public final Block woodGasCollector = new BlockWoodGasCollector().setHardness(2.5F).setStepSound(Block.soundTypeWood).setCreativeTab(GasesFramework.creativeTab).setBlockName("gf_woodGasCollector").setBlockTextureName("gasesframework:collector_wood");
	public final Block gasFurnaceIdle = new BlockIronGasFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypeStone).setBlockName("gf_ironGasFurnaceIdle").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:gas_furnace_iron");
	public final Block gasFurnaceActive = new BlockIronGasFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypeStone).setLightLevel(0.25F).setBlockName("gf_ironGasFurnaceActive").setBlockTextureName("gasesframework:gas_furnace_iron");
	public final Block woodGasFurnaceIdle = new BlockWoodGasFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypeWood).setBlockName("gf_woodGasFurnaceIdle").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:gas_furnace_wood");
	public final Block woodGasFurnaceActive = new BlockWoodGasFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypeWood).setLightLevel(0.25F).setBlockName("gf_woodGasFurnaceActive").setBlockTextureName("gasesframework:gas_furnace_wood");
	public final Block infiniteGasPump = new BlockInfiniteGasPump().setHardness(-1.0F).setBlockName("gf_infiniteGasPump").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:pump_infinite");
	public final Block infiniteGasDrain = new BlockInfiniteGasDrain().setHardness(-1.0F).setBlockName("gf_infiniteGasDrain").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:drain");
	public final Block gasTransposer = new BlockGasTransposer().setHardness(2.5F).setBlockName("gf_gasTransposer").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:transposer");
	public final Block ironGasDynamo = new BlockIronGasDynamo().setHardness(2.5F).setBlockName("gf_ironGasDynamo").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:gas_dynamo_iron");
	public final Block woodGasDynamo = new BlockWoodGasDynamo().setHardness(2.5F).setBlockName("gf_woodGasDynamo").setCreativeTab(GasesFramework.creativeTab).setBlockTextureName("gasesframework:gas_dynamo_wood");

	public GFBlocks()
	{
		for (Field field : getClass().getFields())
		{
			try
			{
				GameRegistry.registerBlock((Block) field.get(this),
						field.getName());
			}
			catch (IllegalAccessException e)
			{}
		}
	}
}
