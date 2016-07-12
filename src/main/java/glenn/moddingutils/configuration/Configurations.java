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
 * An abstract class that uses java reflection to automate the configuration
 * setting and getting.
 * 
 * @author Glenn
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
				if (i != length - 1)
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
	protected @interface AbstractConfig
	{

	}

	@Retention(RetentionPolicy.RUNTIME)
	protected @interface DelegateConfig
	{
		public String delegateFor();
	}

	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ConfigField
	{
		public String name() default "";

		public String comment() default "";

		public String defaultValue() default "";

		public int autoReset() default -1;
	}

	private class ConfigFieldSettings
	{
		public Field field;

		public String name = "";
		public String comment = "";
		public String defaultValue = "";
		public Boolean autoReset = false;

		public ConfigFieldSettings(Field field, Object categoryObject)
		{
			AbstractConfig abstractConfigField = field.getAnnotation(AbstractConfig.class);
			if (abstractConfigField == null)
			{
				apply(field, categoryObject);
			}
		}

		public boolean isValid()
		{
			return field != null && name.length() > 0;
		}

		private void apply(Field field, Object categoryObject)
		{
			this.field = field;

			ConfigField configField = field.getAnnotation(ConfigField.class);
			DelegateConfig delegateConfig = field.getAnnotation(DelegateConfig.class);

			if (delegateConfig != null)
			{
				try
				{
					Field delegatedField = categoryObject.getClass().getField(delegateConfig.delegateFor());
					apply(delegatedField, categoryObject);
				} catch (Exception e)
				{
					FMLLog.severe("Could not find delegated field %s for delegate field %s. The field must exist and be accessible.", delegateConfig.delegateFor(), field.getName());
				}
			}

			if (configField != null)
			{
				if (configField.name().length() > 0)
				{
					this.name = configField.name();
				}
				if (configField.comment().length() > 0)
				{
					this.comment = configField.comment();
				}
				if (configField.defaultValue().length() > 0)
				{
					this.defaultValue = configField.defaultValue();
				}
				if (configField.autoReset() != -1)
				{
					this.autoReset = configField.autoReset() > 0;
				}
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ConfigCategory
	{
		public String name() default "";

		public String comment() default "";
	}

	private class ConfigCategorySettings
	{
		public Field field;

		public String name = "";
		public String comment = "";

		public ConfigCategorySettings(Field field, Object categoryObject)
		{
			AbstractConfig abstractConfigField = field.getAnnotation(AbstractConfig.class);
			if (abstractConfigField == null)
			{
				apply(field, categoryObject);
			}
		}

		public boolean isValid()
		{
			return field != null && name.length() > 0;
		}

		private void apply(Field field, Object categoryObject)
		{
			this.field = field;

			ConfigCategory configCategory = field.getAnnotation(ConfigCategory.class);
			DelegateConfig delegateConfig = field.getAnnotation(DelegateConfig.class);

			if (delegateConfig != null)
			{
				try
				{
					Field delegatedField = categoryObject.getClass().getField(delegateConfig.delegateFor());
					apply(delegatedField, categoryObject);
				} catch (Exception e)
				{
					FMLLog.severe("Could not find delegated field %s for delegate field %s. The field must exist and be accessible.", delegateConfig.delegateFor(), field.getName());
				}
			}

			if (configCategory != null)
			{
				if (configCategory.name().length() > 0)
				{
					this.name = configCategory.name();
				}
				if (configCategory.comment().length() > 0)
				{
					this.comment = configCategory.comment();
				}
			}
		}
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
	}

	protected void setConfigFieldsAndCategories(ConfigPath basePath, Object categoryObject)
	{
		for (Field field : categoryObject.getClass().getDeclaredFields())
		{
			ConfigField configField = field.getAnnotation(ConfigField.class);
			if (configField != null)
			{
				ConfigFieldSettings settings = new ConfigFieldSettings(field, categoryObject);
				if (settings.isValid())
				{
					instantiateConfigField(basePath, settings, categoryObject);
				}
				else
				{
					FMLLog.severe("Config field '%s' has invalid settings", settings.name);
				}
			}

			ConfigCategory configCategory = field.getAnnotation(ConfigCategory.class);
			if (configCategory != null)
			{
				ConfigCategorySettings settings = new ConfigCategorySettings(field, categoryObject);
				if (settings.isValid())
				{
					instantiateConfigCategory(basePath, settings, categoryObject);
				}
				else
				{
					FMLLog.severe("Config category '%s' has invalid settings", settings.name);
				}
			}
		}
	}

	protected void instantiateConfigCategory(ConfigPath basePath, ConfigCategorySettings settings, Object categoryObject)
	{
		ConfigPath subPath = basePath.getSubPath(settings.name);
		try
		{
			Object subCategoryObject = settings.field.getType().newInstance();
			settings.field.set(categoryObject, subCategoryObject);
			if (settings.comment.length() > 0)
			{
				innerConfig.addCustomCategoryComment(subPath.toString(), settings.comment);
			}
			setConfigFieldsAndCategories(subPath, subCategoryObject);
		} catch (Exception e)
		{
			throw new RuntimeException("Failed to parse category " + subPath + ".", e);
		}
	}

	protected void instantiateConfigField(ConfigPath basePath, ConfigFieldSettings settings, Object categoryObject)
	{
		ConfigPath subPath = basePath.getSubPath(settings.name);
		String category = subPath.getCategory();
		try
		{
			Class<?> clazz = settings.field.getType();
			ConfigurationsValueParser parser = ConfigurationsValueParser.getParser(clazz);

			if (parser != null)
			{
				try
				{
					parser.parse(settings.defaultValue, clazz);
				} catch (Exception e)
				{
					FMLLog.severe("Failed to parse the default value '%s' of configuration field %s in config file %s", settings.defaultValue, subPath.toString(), innerConfig.getConfigFile().getAbsolutePath());
				}

				Property property = innerConfig.get(category, subPath.getName(), settings.defaultValue, settings.comment, parser.propertyType);
				String stringValue = property.getString();

				try
				{
					Object value = parser.parse(stringValue, clazz);
					settings.field.set(categoryObject, value);
				} catch (Exception e)
				{
					FMLLog.warning("Failed to parse the value '%s' of configuration field %s in config file %s", stringValue, subPath.toString(), innerConfig.getConfigFile().getAbsolutePath());
				}

				if (settings.autoReset)
				{
					property.set(settings.defaultValue);
				}
			}
			else if (clazz.isArray())
			{
				Class<?> componentClazz = clazz.getComponentType();
				parser = ConfigurationsValueParser.getParser(componentClazz);
				if (parser != null)
				{
					String[] defaultValues = settings.defaultValue.split("\n");
					String[] stringValues = innerConfig.getStringList(subPath.getName(), category, defaultValues, settings.comment);
					Object valuesArray = Array.newInstance(componentClazz, stringValues.length);
					settings.field.set(categoryObject, valuesArray);
					for (int i = 0; i < stringValues.length; i++)
					{
						try
						{
							Object value = parser.parse(stringValues[i], componentClazz);
							Array.set(valuesArray, i, value);
						} catch (Exception e)
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
		} catch (Exception e)
		{
			FMLLog.warning("%s Failed to set value for configuration field %s", e.toString(), subPath.toString());
		}
	}
}
