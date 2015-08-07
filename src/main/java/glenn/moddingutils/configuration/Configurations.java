package glenn.moddingutils.configuration;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.base.Strings;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * An abstract class that uses java reflection to automate the configuration setting and getting.
 * @author Glenn
 *
 */
public abstract class Configurations
{
	private static class ConfigPath
	{
		private final String[] path;
		
		public ConfigPath()
		{
			this(new String[0]);
		}
		
		public ConfigPath(String path)
		{
			this(path.split("\\."));
		}
		
		private ConfigPath(String[] path)
		{
			this.path = path;
		}
		
		public ConfigPath getSubPath(ConfigPath relativePath)
		{
			ArrayList<String> newPathList = new ArrayList<String>();
			for (int i = 0; i < path.length; i++)
			{
				newPathList.add(path[i]);
			}
			for (int i = 0; i < relativePath.path.length; i++)
			{
				newPathList.add(relativePath.path[i]);
			}
			String[] newPath = new String[newPathList.size()];
			newPathList.toArray(newPath);
			return new ConfigPath(newPath);
		}
		
		public ConfigPath getSubPath(String relativePath)
		{
			return getSubPath(new ConfigPath(relativePath));
		}
		
		private String join(int length)
		{
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < length; i++)
			{
				builder.append(path[i]);
				if(i != length - 1)
				{
					builder.append('.');
				}
			}
			
			return builder.toString();
		}
		
		public String toString()
		{
			return join(path.length);
		}
		
		public String getName()
		{
			if (path.length > 0)
			{
				return path[path.length - 1];
			}
			else
			{
				return "";
			}
		}
		
		public String getCategory()
		{
			return join(path.length - 1);
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ConfigField
	{
		public String path();
		public String comment() default "";
		public String defaultValue() default "";
		public boolean autoReset() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ConfigCategory
	{
		public String path();
		public String comment() default "";
	}
	
	public final Configuration innerConfig;
	
	public Configurations(File configurationsFile)
	{
		this(configurationsFile, "");
	}
	
	public Configurations(File configurationsFile, String baseCategory)
	{
		this.innerConfig = new Configuration(configurationsFile);
		innerConfig.load();
		
		setConfigFieldsAndCategories(new ConfigPath(), this);
		
		innerConfig.save();
		
		onLoaded();
	}
	
	protected void setConfigFieldsAndCategories(ConfigPath basePath, Object categoryObject)
	{
		for(Field field : categoryObject.getClass().getFields())
		{
			ConfigField configField = field.getAnnotation(ConfigField.class);
			if(configField != null)
			{
				instantiateConfigField(basePath, field, configField, categoryObject);
			}
			
			ConfigCategory configCategory = field.getAnnotation(ConfigCategory.class);
			if (configCategory != null)
			{
				instantiateConfigCategory(basePath, field, configCategory, categoryObject);
			}
		}
	}
	
	protected void instantiateConfigCategory(ConfigPath basePath, Field field, ConfigCategory configCategory, Object categoryObject)
	{
		ConfigPath subPath = basePath.getSubPath(configCategory.path());
		try
		{
			Object subCategoryObject = field.getType().newInstance();
			field.set(categoryObject, subCategoryObject);
			if (configCategory.comment().length() > 0)
			{
				innerConfig.addCustomCategoryComment(subPath.toString(), configCategory.comment());
			}
			setConfigFieldsAndCategories(subPath, subCategoryObject);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to parse category " + subPath + ".", e);
		}
	}
	
	protected void instantiateConfigField(ConfigPath basePath, Field field, ConfigField configField, Object categoryObject)
	{
		ConfigPath subPath = basePath.getSubPath(configField.path());
		String category = subPath.getCategory();
		String name = subPath.getName();
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
					throw new RuntimeException("Invalid default value for field " + subPath);
				}
				Property property = innerConfig.get(category, name, def, comment);
				fieldValue = new Integer(property.getInt(def));
				if(configField.autoReset()) property.set(def);
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
					throw new RuntimeException("Invalid default value for field " + subPath);
				}
				Property property = innerConfig.get(category, name, def, comment);
				fieldValue = new Float((float)property.getDouble(def));
				if(configField.autoReset()) property.set(def);
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
					throw new RuntimeException("Invalid default value for field " + subPath);
				}
				Property property = innerConfig.get(category, name, def, comment);
				fieldValue = new Double(property.getDouble(def));
				if(configField.autoReset()) property.set(def);
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
					throw new RuntimeException("Invalid default value for field " + subPath);
				}
				Property property = innerConfig.get(category, name, def, comment);
				fieldValue = new Boolean(property.getBoolean(def));
				if(configField.autoReset()) property.set(def);
			}
			else if(c == String.class)
			{
				Property property = innerConfig.get(category, name, defaultValue, comment);
				fieldValue = innerConfig.get(category, name, defaultValue, comment).getString();
				if(configField.autoReset()) property.set(defaultValue);
			}
			else if(c.isArray())
			{
				Class<?> arrayC = c.getComponentType();
				String[] defStrings = defaultValue.equals("") ? new String[0] : defaultValue.split("\n");
				String[] stringValues = innerConfig.getStringList(name, category, defStrings, comment);
				
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
				field.set(categoryObject, fieldValue);
			}
			else
			{
				throw new RuntimeException("Failed to read config field " + subPath + " because of unknown value type " + c.getName());
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString() + " Failed to set value for configuration field " + subPath);
		}
	}
	
	protected abstract void onLoaded();
}