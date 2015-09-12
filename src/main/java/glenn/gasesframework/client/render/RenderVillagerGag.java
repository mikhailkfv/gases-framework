package glenn.gasesframework.client.render;

import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillagerGag extends RenderVillager
{
	private static final ResourceLocation texture = new ResourceLocation("gasesframework:textures/entity/villager_gag.png");

	@Override
	protected ResourceLocation getEntityTexture(EntityVillager entityVillager)
	{
		return texture;
	}
}
