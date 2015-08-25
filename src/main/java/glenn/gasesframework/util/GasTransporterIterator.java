package glenn.gasesframework.util;

import glenn.moddingutils.IVec;

import java.util.ArrayList;
import java.util.Random;

public abstract class GasTransporterIterator
{
	private static final IVec[] offsets = new IVec[]{
		new IVec(0, -1, 0),
		new IVec(0, 1, 0),
		new IVec(0, 0, -1),
		new IVec(0, 0, 1),
		new IVec(-1, 0, 0),
		new IVec(1, 0, 0)
	};
	
	public static class AscendingGasTransporterIterator extends GasTransporterIterator
	{
		public AscendingGasTransporterIterator(GasTransporterBranch begin)
		{
			super(begin);
		}
		
		@Override
		protected boolean validate(GasTransporterBranch previous, GasTransporterBranch next)
		{
			return previous.depth < next.depth;
		}
	}
	
	public static class DescendingGasTransporterIterator extends GasTransporterIterator
	{
		public DescendingGasTransporterIterator(GasTransporterBranch begin)
		{
			super(begin);
		}
		
		@Override
		protected boolean validate(GasTransporterBranch previous, GasTransporterBranch next)
		{
			return previous.depth > next.depth;
		}
	}
	
	public static class Iteration
	{
		public final GasTransporterBranch previous;
		public final IVec previousPosition;
		public final GasTransporterBranch current;
		public final IVec currentPosition;
		public final int direction;
		
		public Iteration(GasTransporterBranch previous, IVec previousPosition, GasTransporterBranch current, IVec currentPosition, int direction)
		{
			this.previous = previous;
			this.previousPosition = previousPosition;
			this.current = current;
			this.currentPosition = currentPosition;
			this.direction = direction;
		}
	}
	
	protected final GasTransporterBranch begin;
	private ArrayList<Iteration> currentQueue;
	private int currentIndex = 0;
	private ArrayList<Iteration> nextQueue = new ArrayList<Iteration>();
	
	protected GasTransporterIterator(GasTransporterBranch begin)
	{
		this.begin = begin;
		
		iterate(begin, begin.getPosition());
		swapQueue();
	}
	
	public Iteration next()
	{
		Iteration iteration = get(currentIndex++);
		
		if(currentIndex == currentQueue.size())
		{
			swapQueue();
		}
		
		return iteration;
	}
	
	public Iteration narrowNext(Random random)
	{
		Iteration iteration = get(currentQueue.size() > 1 ? random.nextInt(currentQueue.size()) : 0);
		
		swapQueue();
		
		return iteration;
	}
	
	private Iteration get(int index)
	{
		if(!currentQueue.isEmpty())
		{
			Iteration iteration = currentQueue.get(index);
			iterate(iteration.current, iteration.currentPosition);
			return iteration;
		}
		else
		{
			return null;
		}
	}
	
	private void swapQueue()
	{
		currentIndex = 0;
		currentQueue = nextQueue;
		nextQueue = new ArrayList<Iteration>();
	}
	
	private void queue(Iteration iteration)
	{
		if(!nextQueue.contains(iteration)) nextQueue.add(iteration);
	}
	
	private void iterate(GasTransporterBranch previous, IVec previousPosition)
	{
		for(int i = 0; i < 6; i++)
		{
			GasTransporterBranch next = previous.connections[i];
			if(next != null)
			{
				if(validate(previous, next))
				{
					queue(new Iteration(previous, previousPosition, next, previousPosition.added(offsets[i]), i));
				}
			}
		}
	}
	
	protected abstract boolean validate(GasTransporterBranch previous, GasTransporterBranch next);
}