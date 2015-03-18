package glenn.gasesframework.util;

import glenn.moddingutils.IVec;
import glenn.moddingutils.KeyPair;

import java.util.ArrayList;
import java.util.Random;

public abstract class PipeBranchIterator
{
	private static final IVec[] offsets = new IVec[]{
		new IVec(0, -1, 0),
		new IVec(0, 1, 0),
		new IVec(0, 0, -1),
		new IVec(0, 0, 1),
		new IVec(-1, 0, 0),
		new IVec(1, 0, 0)
	};
	
	public static class AscendingPipeBranchIterator extends PipeBranchIterator
	{
		public AscendingPipeBranchIterator(PipeBranch begin)
		{
			super(begin);
		}
		
		@Override
		protected boolean validate(PipeBranch previous, PipeBranch next)
		{
			return previous.depth < next.depth;
		}
	}
	
	public static class DescendingPipeBranchIterator extends PipeBranchIterator
	{
		public DescendingPipeBranchIterator(PipeBranch begin)
		{
			super(begin);
		}
		
		@Override
		protected boolean validate(PipeBranch previous, PipeBranch next)
		{
			return previous.depth > next.depth;
		}
	}
	
	public static class Iteration
	{
		public final PipeBranch previous;
		public final IVec previousPosition;
		public final PipeBranch current;
		public final IVec currentPosition;
		public final int direction;
		
		public Iteration(PipeBranch previous, IVec previousPosition, PipeBranch current, IVec currentPosition, int direction)
		{
			this.previous = previous;
			this.previousPosition = previousPosition;
			this.current = current;
			this.currentPosition = currentPosition;
			this.direction = direction;
		}
	}
	
	protected final PipeBranch begin;
	private ArrayList<Iteration> currentQueue;
	private int currentIndex = 0;
	private ArrayList<Iteration> nextQueue = new ArrayList<Iteration>();
	
	protected PipeBranchIterator(PipeBranch begin)
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
	
	private void iterate(PipeBranch previous, IVec previousPosition)
	{
		for(int i = 0; i < 6; i++)
		{
			PipeBranch next = previous.connections[i];
			if(next != null)
			{
				if(validate(previous, next))
				{
					queue(new Iteration(previous, previousPosition, next, previousPosition.added(offsets[i]), i));
				}
			}
		}
	}
	
	protected abstract boolean validate(PipeBranch previous, PipeBranch next);
}