package glenn.gasesframework.network.packet;

import glenn.gasesframework.network.AbstractSerialPacket;
import net.minecraft.entity.player.EntityPlayer;

public class PacketGasEffects extends AbstractSerialPacket
{
	public int blindness, suffocation, slowness;
	
	public PacketGasEffects(int blindness, int suffocation, int slowness)
	{
		this.blindness = blindness;
		this.suffocation = suffocation;
		this.slowness = slowness;
	}
	
	public PacketGasEffects()
	{}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
		
	}

	@Override
	public void handleServerSide(EntityPlayer player)
	{
		
	}
}