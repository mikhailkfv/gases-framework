package glenn.gasesframework.api.reaction.environment;

import java.util.Random;

import glenn.gasesframework.api.PartialGasStack;
import net.minecraft.item.ItemStack;

public interface IReactionEnvironment
{
	PartialGasStack getA();

	void setA(PartialGasStack a);

	void igniteA();

	void dropItem(ItemStack itemstack);

	void explode(float power, boolean isFlaming, boolean isSmoking);

	Random getRandom();

	void playSound(String name, float volume, float pitch);
}
