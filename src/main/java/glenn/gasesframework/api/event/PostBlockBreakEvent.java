package glenn.gasesframework.api.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * An event that is fired on the Minecraft Forge event bus after a block has been successfully broken.
 */
public class PostBlockBreakEvent extends Event
{
    public final int x;
    public final int y;
    public final int z;
    public final World world;
    public final Block previousBlock;
    public final int previousBlockMetadata;
    public final EntityPlayerMP player;

    public PostBlockBreakEvent(int x, int y, int z, World world, Block previousBlock, int previousBlockMetadata, EntityPlayerMP player)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.previousBlock = previousBlock;
        this.previousBlockMetadata = previousBlockMetadata;
        this.player = player;
    }
}
