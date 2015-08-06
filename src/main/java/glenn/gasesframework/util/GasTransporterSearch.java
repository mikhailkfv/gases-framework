package glenn.gasesframework.util;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasInterface;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.api.block.IGasTransporter;
import glenn.gasesframework.api.gastype.GasType;
import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyVec;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class GasTransporterSearch
{
	private static final IVec[] offsets = new IVec[]{
		new IVec(0, -1, 0),
		new IVec(0, 1, 0),
		new IVec(0, 0, -1),
		new IVec(0, 0, 1),
		new IVec(-1, 0, 0),
		new IVec(1, 0, 0)
	};
	
	public static class End
	{
		public final GasTransporterBranch branch;
		public final IVec endPosition;
		public final ForgeDirection endDirection;
		
		protected End(GasTransporterBranch branch, IVec endPosition, ForgeDirection endDirection)
		{
			this.branch = branch;
			this.endPosition = endPosition;
			this.endDirection = endDirection;
		}
	}
	
	public static class ReceptorSearch extends GasTransporterSearch
	{
		public final ArrayList<End> looseEnds = new ArrayList<End>();
		public final ArrayList<End> ends = new ArrayList<End>();
		
		public ReceptorSearch(World world, int x, int y, int z, int maxDepth)
		{
			search(world, x, y, z, maxDepth);
		}

		@Override
		protected void inspectConnection(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec connectionPos, ForgeDirection direction)
		{
			Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
			if(IGasReceptor.class.isAssignableFrom(directionBlock.getClass()))
			{
				IGasReceptor gasReceptor = (IGasReceptor)directionBlock;
				GasType carriedType = block.getCarriedType(world, blockPos.x, blockPos.y, blockPos.z);
				if(gasReceptor.canReceiveGas(world, connectionPos.x, connectionPos.y, connectionPos.z, direction.getOpposite(), carriedType))
				{
					ends.add(new End(branch, connectionPos, direction));
				}
			}
		}

		@Override
		protected void inspectLooseEnd(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec looseConnectionPos, ForgeDirection direction)
		{
			GasType carriedType = block.getCarriedType(world, blockPos.x, blockPos.y, blockPos.z);
			if(GasesFrameworkAPI.canFillWithGas(world, looseConnectionPos.x, looseConnectionPos.y, looseConnectionPos.z, carriedType))
			{
				looseEnds.add(new End(branch, looseConnectionPos, direction));
			}
		}
	}
	
	public static class PropellorSearch extends GasTransporterSearch
	{
		public final ArrayList<End> propellors = new ArrayList<End>();
		
		public PropellorSearch(World world, int x, int y, int z, int maxDepth)
		{
			search(world, x, y, z, maxDepth);
		}

		@Override
		protected void inspectConnection(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec connectionPos, ForgeDirection direction)
		{
			Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
			
			if(directionBlock instanceof IGasPropellor)
			{
				IGasPropellor gasPropellor = (IGasPropellor)directionBlock;
				
				if(gasPropellor.getPressureFromSide(world, connectionPos.x, connectionPos.y, connectionPos.z, direction.getOpposite()) > 0)
				{
					propellors.add(new End(branch, connectionPos, direction.getOpposite()));
				}
			}
		}

		@Override
		protected void inspectLooseEnd(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec looseConnectionPos, ForgeDirection direction)
		{

		}
	}
	
	public GasTransporterBranch root;
	
	protected void search(World world, int x, int y, int z, int maxDepth)
	{
		final HashMap<KeyVec, GasTransporterBranch> branchMap = new HashMap<KeyVec, GasTransporterBranch>();
    	ArrayList<GasTransporterBranch> top = new ArrayList<GasTransporterBranch>();
    	
    	if(world.getBlock(x, y, z) instanceof IGasTransporter)
    	{
	    	root = GasTransporterBranch.makeRoot(x, y, z, branchMap);
	    	top.add(root);
    	}
    	
    	for(int depth = 1; depth <= maxDepth && !top.isEmpty(); depth++)
    	{
    		ArrayList<GasTransporterBranch> newTop = new ArrayList<GasTransporterBranch>();
    		
    		for(GasTransporterBranch branch : top)
    		{
				IVec pos = branch.getPosition();
	    		IGasTransporter block = (IGasTransporter)world.getBlock(pos.x, pos.y, pos.z);
	    		
    			int connectionCount = 0;
    			int lastConnection = -1;
    			for(int i = 0; i < 6; i++)
    			{
    				ForgeDirection direction = ForgeDirection.getOrientation(i);
    				GasTransporterBranch connection = branch.connections[i];
    				IVec connectionPos = pos.added(offsets[i]);
    				if(connection == null)
    				{
    					Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
    					if(directionBlock instanceof IGasTransporter)
    					{
    						connection = new GasTransporterBranch(depth, branchMap, connectionPos);
    						newTop.add(connection);
    					}
    					else
    					{
    						inspectConnection(branch, world, pos, block, connectionPos, direction);
    					}
    					
    					if(directionBlock instanceof IGasInterface)
    					{
    						IGasInterface gasInterface = (IGasInterface)directionBlock;
    						if (gasInterface.connectToPipe(world, connectionPos.x, connectionPos.y, connectionPos.z, direction.getOpposite()))
    						{
							    connectionCount++;
							    lastConnection = i;
    						}
    					}
    				}
    				else
    				{
    					connectionCount++;
						lastConnection = i;
    				}
    			}
    			
    			if(connectionCount == 1)
    			{
    				ForgeDirection direction = ForgeDirection.getOrientation(lastConnection).getOpposite();
    				IVec looseConnectionPos = pos.added(new IVec(direction.offsetX, direction.offsetY, direction.offsetZ));
    				
    				inspectLooseEnd(branch, world, pos, block, looseConnectionPos, direction);
    			}
    		}
    		
    		top = newTop;
    	}
	}
	
	protected abstract void inspectConnection(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec connectionPos, ForgeDirection direction);
	protected abstract void inspectLooseEnd(GasTransporterBranch branch, World world, IVec blockPos, IGasTransporter block, IVec looseConnectionPos, ForgeDirection direction);
}