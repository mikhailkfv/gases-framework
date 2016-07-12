package glenn.gasesframework.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPlayerGag extends RendererLivingEntity
{
	private static final ResourceLocation texture = new ResourceLocation("gasesframework:textures/entity/player_gag.png");

	public RenderPlayerGag()
	{
		super(new ModelBiped(0.0F), 0.5F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float f)
	{
		float f1 = 0.9375F;
		GL11.glScalef(f1, f1, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entityPlayer)
	{
		return texture;
	}
}
