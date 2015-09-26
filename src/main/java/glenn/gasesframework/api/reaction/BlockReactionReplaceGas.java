package glenn.gasesframework.api.reaction;

import glenn.gasesframework.api.PartialGasStack;
import glenn.gasesframework.api.gastype.GasType;
import glenn.gasesframework.api.reaction.environment.IBlockReactionEnvironment;
import net.minecraft.block.Block;

/**
 * Reaction for replacement of a gas against a specific block.
 */
public class BlockReactionReplaceGas extends BlockReaction
{
	public final GasType replacementGas;
	public final Block match;

	public BlockReactionReplaceGas(GasType replacementGas, Block match)
	{
		this.replacementGas = replacementGas;
		this.match = match;
	}

	@Override
	public void react(IBlockReactionEnvironment environment)
	{
		if (environment.getB() == match)
		{
			PartialGasStack a = environment.getA();
			environment.setA(new PartialGasStack(replacementGas, a.partialAmount));
		}
	}
}
