package glenn.moddingutils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

/**
 * An abstract class that uses java reflection to automate the configuration setting and getting.
 * @author Glenn
 *
 */
public abstract class Configurations
{
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ConfigField
	{
		public String path();
		public String comment() default "";
		public String defaultValue() default "";
		public boolean autoReset() default false;
	}
	
	public class ItemRepresentation
	{
		public String name;
		public int metadata = 0;
		public int amount = 1;
		
		public ItemStack getItemStack()
		{
			return new ItemStack((Item)Item.itemRegistry.getObject(name), amount == 0 ? 1 : amount, metadata);
		}
	}
	
	public Configurations(File configurationsFile)
	{
		Configuration config = new Configuration(configurationsFile);
		config.load();
		
		for(Field field : getClass().getFields())
		{
			ConfigField configField = field.getAnnotation(ConfigField.class);
			if(configField != null)
			{
				String fieldPath = configField.path();
				int lastDot = fieldPath.lastIndexOf('.');
				String category = lastDot != -1 ? fieldPath.substring(0, lastDot) : "other";
				String name = fieldPath.substring(lastDot + 1);
				String comment = configField.comment();
				
				String defaultValue = configField.defaultValue();
				
				try
				{
					Class<?> c = field.getType();
					Object fieldValue = null;
					
					if(c == int.class)
					{
						int def = 0;
						try
						{
							def = Integer.parseInt(defaultValue);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Invalid default value for field " + fieldPath);
						}
						fieldValue = new Integer(config.get(category, name, def, comment).getInt(def));
					}
					else if(c == float.class)
					{
						float def = 0.0f;
						try
						{
							def = Float.parseFloat(defaultValue);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Invalid default value for field " + fieldPath);
						}
						fieldValue = new Float(Float.parseFloat(config.get(category, name, def, comment).getString()));
					}
					else if(c == double.class)
					{
						double def = 0.0D;
						try
						{
							def = Double.parseDouble(defaultValue);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Invalid default value for field " + fieldPath);
						}
						fieldValue = new Double(Double.parseDouble(config.get(category, name, def, comment).getString()));
					}
					else if(c == boolean.class)
					{
						boolean def = false;
						try
						{
							def = Boolean.parseBoolean(defaultValue);
						}
						catch(Exception e)
						{
							throw new RuntimeException("Invalid default value for field " + fieldPath);
						}
						fieldValue = new Boolean(config.get(category, name, def, comment).getBoolean(def));
					}
					else if(c == String.class)
					{
						fieldValue = config.get(category, name, defaultValue, comment).getString();
					}
					else if(c.isArray())
					{
						Class<?> arrayC = c.getComponentType();
						String[] defStrings = defaultValue.equals("") ? new String[0] : defaultValue.split("\n");
						String[] stringValues = config.getStringList(name, category, defStrings, comment);
						
						if(arrayC == Integer.class)
						{
							int[] typeValues = new int[stringValues.length];
							for(int i = 0; i < stringValues.length; i++)
							{
								try
								{
									typeValues[i] = Integer.parseInt(stringValues[i]);
								}
								catch(Exception e)
								{
									
									typeValues[i] = 0;
								}
							}
							fieldValue = typeValues;
						}
						else if(arrayC == Float.class)
						{
							float[] typeValues = new float[stringValues.length];
							for(int i = 0; i < stringValues.length; i++)
							{
								try
								{
									typeValues[i] = Float.parseFloat(stringValues[i]);
								}
								catch(Exception e)
								{
									typeValues[i] = 0.0f;
								}
							}
							fieldValue = typeValues;
						}
						else if(arrayC == Double.class)
						{
							double[] typeValues = new double[stringValues.length];
							for(int i = 0; i < stringValues.length; i++)
							{
								try
								{
									typeValues[i] = Double.parseDouble(stringValues[i]);
								}
								catch(Exception e)
								{
									typeValues[i] = 0.0D;
								}
							}
							fieldValue = typeValues;
						}
						else if(arrayC == Boolean.class)
						{
							boolean[] typeValues = new boolean[stringValues.length];
							for(int i = 0; i < stringValues.length; i++)
							{
								try
								{
									typeValues[i] = Boolean.parseBoolean(stringValues[i]);
								}
								catch(Exception e)
								{
									typeValues[i] = false;
								}
							}
							fieldValue = typeValues;
						}
						else if(arrayC == String.class)
						{
							fieldValue = stringValues;
						}
					}
					
					if(fieldValue != null)
					{
						field.set(this, fieldValue);
					}
					else
					{
						throw new RuntimeException("Failed to read config field " + fieldPath + " because of unknown value type " + c.getName());
					}
				}
				catch(Exception e)
				{
					System.out.println(e.toString() + " Failed to set value for configuration field " + fieldPath);
				}
			}
		}
		
		config.save();
		
		onLoaded();
	}
	
	protected abstract void onLoaded();
}