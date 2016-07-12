package glenn.gasesframework.client.render;

import org.lwjgl.opengl.GL11;
import glenn.gasesframework.common.tileentity.TileEntityGasDynamo;
import glenn.moddingutils.blockrotation.BlockRotation;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityGasDynamoRenderer extends TileEntitySpecialRenderer
{
	private ResourceLocation coil_inactiveTexture = new ResourceLocation("gasesframework:textures/blocks/gas_dynamo_coil_inactive.png");
	private ResourceLocation coil_activeTexture = new ResourceLocation("gasesframework:textures/blocks/gas_dynamo_coil_active.png");
	private Tessellator tessellator;

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double d0, double d1, double d2, float f)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d0, (float) d1, (float) d2);

		renderGasDynamoAt((TileEntityGasDynamo) tileEntity, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);

		GL11.glPopMatrix();
	}

	public void renderGasDynamoAt(TileEntityGasDynamo tileEntity, int i, int j, int k)
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		tessellator = Tessellator.instance;
		World world = tileEntity.getWorldObj();
		Block block = tileEntity.getBlockType();

		int metadata = tileEntity.getWorldObj().getBlockMetadata(i, j, k);
		BlockRotation rotation = BlockRotation.getRotation(metadata);
		ForgeDirection direction = rotation.rotateInverse(ForgeDirection.NORTH);
		int mixedBrightness = block.getMixedBrightnessForBlock(world, i + direction.offsetX, j + direction.offsetY, k + direction.offsetZ);
		int rotationX = rotation.pitch.getRotationDegrees();
		int rotationY = rotation.yaw.getRotationDegrees();

		double charge = (double) tileEntity.getEnergyStored(ForgeDirection.UNKNOWN) / tileEntity.getMaxEnergyStored(ForgeDirection.UNKNOWN);

		GL11.glPushMatrix();

		// Rotate around block center
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(rotationY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(rotationX, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		double minX = 2.0D / 16.0D;
		double maxX = 14.0D / 16.0D;
		double minY = 0.0D;
		double maxY = 1.0F;

		double centerX = maxX * (1.0f - charge) + minX * charge;

		bindTexture(coil_inactiveTexture);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(mixedBrightness);
		tessellator.setNormal(direction.offsetX, direction.offsetY, direction.offsetZ);
		tessellator.addVertexWithUV(minX, minY, 0.0D, minX, maxY);
		tessellator.addVertexWithUV(minX, maxY, 0.0D, minX, minY);
		tessellator.addVertexWithUV(centerX, maxY, 0.0D, centerX, minY);
		tessellator.addVertexWithUV(centerX, minY, 0.0D, centerX, maxY);
		tessellator.draw();

		bindTexture(coil_activeTexture);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(mixedBrightness);
		tessellator.setNormal(direction.offsetX, direction.offsetY, direction.offsetZ);
		tessellator.addVertexWithUV(centerX, minY, 0.0D, centerX, maxY);
		tessellator.addVertexWithUV(centerX, maxY, 0.0D, centerX, minY);
		tessellator.addVertexWithUV(maxX, maxY, 0.0D, maxX, minY);
		tessellator.addVertexWithUV(maxX, minY, 0.0D, maxX, maxY);
		tessellator.draw();

		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_LIGHTING);
	}
}