package glenn.moddingutils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import cpw.mods.fml.common.FMLLog;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Uses reflection to automatically read from and write to the buffer.
 * @author Glenn
 *
 */
public abstract class AbstractSerialMessage extends AbstractMessage
{
	@Override
	public void toBytes(ByteBuf buffer)
	{
		for(Field field : getClass().getFields())
		{
			Class<?> c = field.getType();
			try
			{
				if(c == char.class)
				{
					buffer.writeChar(field.getChar(this));
				}
				else if(c == byte.class)
				{
					buffer.writeByte(field.getByte(this));
				}
				else if(c == short.class)
				{
					buffer.writeShort(field.getShort(this));
				}
				else if(c == int.class)
				{
					buffer.writeInt(field.getInt(this));
				}
				else if(c == long.class)
				{
					buffer.writeLong(field.getLong(this));
				}
				else if(c == boolean.class)
				{
					buffer.writeBoolean(field.getBoolean(this));
				}
				else if(c == float.class)
				{
					buffer.writeFloat(field.getFloat(this));
				}
				else if(c == double.class)
				{
					buffer.writeDouble(field.getDouble(this));
				}
				else if(c == String.class)
				{
					byte[] val = ((String)field.get(this)).getBytes();
					buffer.writeShort(val.length);
					buffer.writeBytes(val);
				}
				else if(c.isArray())
				{
					Class<?> arrayC = c.getComponentType();
					short arraySize = 0;
					ByteBuf valBuffer = Unpooled.buffer();
					
					if(arrayC == char.class)
					{
						char[] valArray = (char[])field.get(this);
						for(char val : valArray)
						{
							valBuffer.writeChar(val);
						}
					}
					else if(arrayC == byte.class)
					{
						byte[] valArray = (byte[])field.get(this);
						for(byte val : valArray)
						{
							valBuffer.writeByte(val);
						}
					}
					else if(arrayC == short.class)
					{
						short[] valArray = (short[])field.get(this);
						for(short val : valArray)
						{
							valBuffer.writeShort(val);
						}
					}
					else if(arrayC == int.class)
					{
						int[] valArray = (int[])field.get(this);
						for(int val : valArray)
						{
							valBuffer.writeInt(val);
						}
					}
					else if(arrayC == long.class)
					{
						long[] valArray = (long[])field.get(this);
						for(long val : valArray)
						{
							valBuffer.writeLong(val);
						}
					}
					else if(arrayC == boolean.class)
					{
						boolean[] valArray = (boolean[])field.get(this);
						for(boolean val : valArray)
						{
							valBuffer.writeBoolean(val);
						}
					}
					else if(arrayC == float.class)
					{
						float[] valArray = (float[])field.get(this);
						for(float val : valArray)
						{
							valBuffer.writeFloat(val);
						}
					}
					else if(arrayC == double.class)
					{
						double[] valArray = (double[])field.get(this);
						for(double val : valArray)
						{
							valBuffer.writeDouble(val);
						}
					}
					
					valBuffer.writeShort(arraySize);
					valBuffer.writeBytes(valBuffer);
				}
			}
			catch(Exception e)
			{
				FMLLog.severe("%s while writing packet %s.", e.toString(), getClass().getCanonicalName());
			}
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		for(Field field : getClass().getFields())
		{
			Class<?> c = field.getType();
			try
			{
				if(c == char.class)
				{
					field.setChar(this, buffer.readChar());
				}
				else if(c == byte.class)
				{
					field.setByte(this, buffer.readByte());
				}
				else if(c == short.class)
				{
					field.setShort(this, buffer.readShort());
				}
				else if(c == int.class)
				{
					field.setInt(this, buffer.readInt());
				}
				else if(c == long.class)
				{
					field.setLong(this, buffer.readLong());
				}
				else if(c == boolean.class)
				{
					field.setBoolean(this, buffer.readBoolean());
				}
				else if(c == float.class)
				{
					field.setFloat(this, buffer.readFloat());
				}
				else if(c == double.class)
				{
					field.setDouble(this, buffer.readDouble());
				}
				else if(c == String.class)
				{
					byte[] val = new byte[buffer.readShort()];
					buffer.readBytes(val);
					field.set(this, new String(val, "UTF-16"));
				}
				else if(c.isArray())
				{
					Class<?> arrayC = c.getComponentType();
					short arraySize = buffer.readShort();
					Object val = null;
					
					if(arrayC == char.class)
					{
						char[] valArray = new char[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readChar();
						}
					}
					else if(arrayC == byte.class)
					{
						byte[] valArray = new byte[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readByte();
						}
					}
					else if(arrayC == short.class)
					{
						short[] valArray = new short[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readShort();
						}
					}
					else if(arrayC == int.class)
					{
						int[] valArray = new int[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readInt();
						}
					}
					else if(arrayC == long.class)
					{
						long[] valArray = new long[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readLong();
						}
					}
					else if(arrayC == boolean.class)
					{
						boolean[] valArray = new boolean[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readBoolean();
						}
					}
					else if(arrayC == float.class)
					{
						float[] valArray = new float[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readFloat();
						}
					}
					else if(arrayC == double.class)
					{
						double[] valArray = new double[arraySize];
						for(int i = 0; i < arraySize; i++)
						{
							valArray[i] = buffer.readDouble();
						}
					}
					
					field.set(this, val);
				}
			}
			catch(Exception e)
			{
				FMLLog.severe("%s while writing packet %s.", e.toString(), getClass().getCanonicalName());
			}
		}
	}
}