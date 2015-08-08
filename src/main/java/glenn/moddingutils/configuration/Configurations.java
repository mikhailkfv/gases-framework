package glenn.moddingutils.configuration;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLLog;
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
			Class<?> clazz = field.getType();
			ConfigurationsValueParser parser = ConfigurationsValueParser.getParser(clazz);
			
			if (parser != null)
			{
				try
				{
					parser.parse(defaultValue, clazz);
				}
				catch (Exception e)
				{
					FMLLog.severe("Failed to parse the default value '%s' of configuration field %s in config file %s", defaultValue, subPath.toString(), innerConfig.getConfigFile().getAbsolutePath());
				}
				
				Property property = innerConfig.get(category, name, defaultValue, comment, parser.propertyType);
				String stringValue = property.getString();

				try
				{
					Object value = parser.parse(stringValue, clazz);
					field.set(categoryObject, value);
				}
				catch (Exception e)
				{
					FMLLog.warning("Failed to parse the value '%s' of configuration field %s in config file %s", stringValue, subPath.toString(), innerConfig.getConfigFile().getAbsolutePath());
				}

				if (configField.autoReset())
				{
					property.set(defaultValue);
				}
			}
			else if (clazz.isArray())
			{
				Class<?> componentClazz = clazz.getComponentType();
				parser = ConfigurationsValueParser.getParser(componentClazz);
				if (parser != null)
				{
					String[] defaultValues = defaultValue.split("\n");
					String[] stringValues = innerConfig.getStringList(name, category, defaultValues, comment);
					Object valuesArray = Array.newInstance(componentClazz, stringValues.length);
					field.set(categoryObject, valuesArray);
					for (int i = 0; i < stringValues.length; i++)
					{
						try
						{
							parser.parse(defaultValues[i], componentClazz);
						}
						catch (Exception e)
						{
							FMLLog.severe("Failed to parse the default value '%s' of configuration field %s[%d] in config file %s", defaultValue, subPath.toString(), i, innerConfig.getConfigFile().getAbsolutePath());
						}

						try
						{
							Object value = parser.parse(stringValues[i], componentClazz);
							Array.set(valuesArray, i, value);
						}
						catch (Exception e)
						{
							FMLLog.warning("Failed to parse the value '%s' of configuration field %s[%d] in config file %s", stringValues[i], subPath.toString(), i, innerConfig.getConfigFile().getAbsolutePath());
						}
					}
				}
			}
			
			if (parser == null)
			{
				throw new RuntimeException(String.format("Could not parse ConfigField of type %s. You must register a ConfigurationsValueParser.", clazz));
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString() + " Failed to set value for configuration field " + subPath);
		}
	}
	
	protected abstract void onLoaded();
}