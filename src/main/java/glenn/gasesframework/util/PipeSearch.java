package glenn.gasesframework.util;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.gasesframework.api.block.IGasPropellor;
import glenn.gasesframework.api.block.IGasReceptor;
import glenn.gasesframework.block.BlockGasPipe;
import glenn.gasesframework.block.BlockGasPump;
import glenn.gasesframework.tileentity.TileEntityPump;
import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyPair;
import glenn.moddingutils.KeyVec;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class PipeSearch
{
	private static final IVec[] offsets = new IVec[]{
		new IVec(0, 1, 0),
		new IVec(0, -1, 0),
		new IVec(1, 0, 0),
		new IVec(-1, 0, 0),
		new IVec(0, 0, 1),
		new IVec(0, 0, -1)
	};
	
	private static final int[] reverseIndices = new int[]{
		1, 0, 3, 2, 5, 4
	};
	
	public static class PipeEnd
	{
		public final PipeBranch branch;
		public final IVec endPosition;
		public final int endDirection;
		
		protected PipeEnd(PipeBranch branch, IVec endPosition, int endDirection)
		{
			this.branch = branch;
			this.endPosition = endPosition;
			this.endDirection = endDirection;
		}
	}
	
	public static class ReceptorSearch extends PipeSearch
	{
		public final ArrayList<PipeEnd> looseEnds = new ArrayList<PipeEnd>();
		public final ArrayList<PipeEnd> ends = new ArrayList<PipeEnd>();
		
		public ReceptorSearch(World world, int x, int y, int z, int maxDepth)
		{
			search(world, x, y, z, maxDepth);
		}

		@Override
		protected boolean inspectConnection(PipeBranch branch, World world, IVec connectionPos, BlockGasPipe pipeBlock, int direction)
		{
			Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
			if(IGasReceptor.class.isAssignableFrom(directionBlock.getClass()))
			{
				IGasReceptor gasReceptor = (IGasReceptor)directionBlock;
				if(gasReceptor.canReceiveGas(world, connectionPos.x, connectionPos.y, connectionPos.z, direction, pipeBlock.type))
				{
					ends.add(new PipeEnd(branch, connectionPos, direction));
				}
				
				return true;
			}
			
			return false;
		}

		@Override
		protected void inspectLooseEnd(PipeBranch branch, World world, IVec looseConnectionPos, BlockGasPipe pipeBlock, int direction)
		{
			if(GasesFrameworkAPI.canFillWithGas(world, looseConnectionPos.x, looseConnectionPos.y, looseConnectionPos.z, pipeBlock.type))
			{
				looseEnds.add(new PipeEnd(branch, looseConnectionPos, direction));
			}
		}
	}
	
	public static class PropellorSearch extends PipeSearch
	{
		public final ArrayList<PipeEnd> propellors = new ArrayList<PipeEnd>();
		
		public PropellorSearch(World world, int x, int y, int z, int maxDepth)
		{
			search(world, x, y, z, maxDepth);
		}

		@Override
		protected boolean inspectConnection(PipeBranch branch, World world, IVec connectionPos, BlockGasPipe pipeBlock, int direction)
		{
			Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
			
			if(IGasPropellor.class.isAssignableFrom(directionBlock.getClass()))
			{
				IGasPropellor gasPropellor = (IGasPropellor)directionBlock;
				
				if(gasPropellor.canPropelGasFromSide(world, connectionPos.x, connectionPos.y, connectionPos.z, GasesFramework.reverseDirection(direction)))
				{
					propellors.add(new PipeEnd(branch, connectionPos, GasesFramework.reverseDirection(direction)));
				}
			}
			
			return false;
		}
	}
	
	public PipeBranch root;
	
	protected void search(World world, int x, int y, int z, int maxDepth)
	{
		final HashMap<KeyVec, PipeBranch> branchMap = new HashMap<KeyVec, PipeBranch>();
    	ArrayList<PipeBranch> top = new ArrayList<PipeBranch>();
    	
    	if(world.getBlock(x, y, z) instanceof BlockGasPipe)
    	{
	    	root = PipeBranch.makeRoot(x, y, z, branchMap);
	    	top.add(root);
    	}
    	
    	for(int depth = 1; depth <= maxDepth && !top.isEmpty(); depth++)
    	{
    		ArrayList<PipeBranch> newTop = new ArrayList<PipeBranch>();
    		
    		for(PipeBranch branch : top)
    		{
				IVec pos = branch.getPosition();
	    		BlockGasPipe pipeBlock = (BlockGasPipe)world.getBlock(pos.x, pos.y, pos.z);
	    		
    			int connectionCount = 0;
    			int lastConnection = -1;
    			for(int i = 0; i < 6; i++)
    			{
    				PipeBranch connection = branch.connections[i];
    				IVec connectionPos = pos.added(offsets[i]);
    				if(connection == null)
    				{
    					Block directionBlock = world.getBlock(connectionPos.x, connectionPos.y, connectionPos.z);
    					if(directionBlock instanceof BlockGasPipe)
    					{
    						connection = new PipeBranch(depth, branchMap, connectionPos);
    						newTop.add(connection);
    					}
    					else if(inspectConnection(branch, world, connectionPos, pipeBlock, i))
    					{
    						connectionCount++;
    						lastConnection = i;
    					}
    				}
    				
    				if(connection != null)
    				{
    					connectionCount++;
						lastConnection = i;
    				}
    			}
    			
    			if(connectionCount == 1)
    			{
    				int direction = reverseIndices[lastConnection];
    				IVec looseConnectionPos = pos.added(offsets[direction]);
    				
    				inspectLooseEnd(branch, world, looseConnectionPos, pipeBlock, direction);
    			}
    		}
    		
    		top = newTop;
    	}
	}
	
	protected abstract boolean inspectConnection(PipeBranch branch, World world, IVec connectionPos, BlockGasPipe pipeBlock, int direction);
	protected void inspectLooseEnd(PipeBranch branch, World world, IVec looseConnectionPos, BlockGasPipe pipeBlock, int direction)
	{
		
	}
}