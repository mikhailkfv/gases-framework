package glenn.gasesframework.util;

import java.util.HashMap;

import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyVec;

public class GasTransporterBranch
{
	private static final IVec[] offsets = new IVec[]{
		new IVec(0, -1, 0),
		new IVec(0, 1, 0),
		new IVec(0, 0, -1),
		new IVec(0, 0, 1),
		new IVec(-1, 0, 0),
		new IVec(1, 0, 0)
	};
	
	private static final IVec[] reverseOffsets = new IVec[]{
		new IVec(0, 1, 0),
		new IVec(0, -1, 0),
		new IVec(0, 0, 1),
		new IVec(0, 0, -1),
		new IVec(1, 0, 0),
		new IVec(-1, 0, 0)
	};
	
	private static final int[] reverseIndices = new int[]{
		1, 0, 3, 2, 5, 4
	};
	
	private static class Root extends GasTransporterBranch
	{
		public final IVec pos;
		
		public Root(HashMap<KeyVec, GasTransporterBranch> branchMap, IVec pos)
		{
			super(0, branchMap, pos);
			this.pos = pos;
		}
		
		@Override
		protected void getPosition(IVec pos)
		{
			pos.set(this.pos);
		}
	}
	
	public final int depth;
	public final GasTransporterBranch[] connections;
	
	protected GasTransporterBranch(int depth, GasTransporterBranch[] connections)
	{
		this.depth = depth;
		this.connections = connections;
		
		for(int i = 0; i < 6; i++)
		{
			if(connections[i] != null)
			{
				connections[i].connections[reverseIndices[i]] = this;
			}
		}
	}
	
	public GasTransporterBranch(int depth, HashMap<KeyVec, GasTransporterBranch> branchMap, IVec pos)
	{
		this(depth, getConnectionsFromMap(branchMap, pos));
		branchMap.put(new KeyVec(pos), this);
	}
	
	private static GasTransporterBranch[] getConnectionsFromMap(HashMap<KeyVec, GasTransporterBranch> branchMap, IVec pos)
	{
		GasTransporterBranch[] connections = new GasTransporterBranch[6];
		
		for(int i = 0; i < 6; i++)
		{
			connections[i] = branchMap.get(new KeyVec(pos.added(offsets[i])));
		}
		
		return connections;
	}
	
	protected void getPosition(IVec pos)
	{
		for(int i = 0; i < 6; i++)
		{
			if(connections[i] != null && connections[i].depth < depth)
			{
				connections[i].getPosition(pos);
				pos.add(reverseOffsets[i]);
				return;
			}
		}
		throw new RuntimeException("Attempted to get position of invalid branch!");
	}
	
	public static GasTransporterBranch makeRoot(int x, int y, int z, HashMap<KeyVec, GasTransporterBranch> branchMap)
	{
		GasTransporterBranch root = new Root(branchMap, new IVec(x, y, z));
		return root;
	}
	
	public IVec getPosition()
	{
		IVec pos = new IVec();
		getPosition(pos);
		return pos;
	}
}