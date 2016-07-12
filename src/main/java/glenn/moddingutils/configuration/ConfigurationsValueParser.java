package glenn.moddingutils.configuration;

import java.util.HashMap;

import net.minecraftforge.common.config.Property;

public abstract class ConfigurationsValueParser
{
	private static class IntParser extends ConfigurationsValueParser
	{
		public IntParser()
		{
			super(Property.Type.INTEGER);
		}

		@Override
		public Object parse(String value, Class<?> clazz)
		{
			return Integer.valueOf(Integer.parseInt(value));
		}
	}

	private static class FloatParser extends ConfigurationsValueParser
	{
		public FloatParser()
		{
			super(Property.Type.DOUBLE);
		}

		@Override
		public Object parse(String value, Class<?> clazz)
		{
			return Float.valueOf(Float.parseFloat(value));
		}
	}

	private static class DoubleParser extends ConfigurationsValueParser
	{
		public DoubleParser()
		{
			super(Property.Type.DOUBLE);
		}

		@Override
		public Object parse(String value, Class<?> clazz)
		{
			return Double.valueOf(Double.parseDouble(value));
		}
	}

	private static class BooleanParser extends ConfigurationsValueParser
	{
		public BooleanParser()
		{
			super(Property.Type.BOOLEAN);
		}

		@Override
		public Object parse(String value, Class<?> clazz)
		{
			return Boolean.valueOf(Boolean.parseBoolean(value));
		}
	}

	private static class StringParser extends ConfigurationsValueParser
	{
		public StringParser()
		{
			super(Property.Type.STRING);
		}

		@Override
		public Object parse(String value, Class<?> clazz)
		{
			return value;
		}
	}

	private static final HashMap<Class<?>, ConfigurationsValueParser> parsers = new HashMap<Class<?>, ConfigurationsValueParser>();

	static
	{
		registerParser(new IntParser(), int.class);
		registerParser(new FloatParser(), float.class);
		registerParser(new DoubleParser(), double.class);
		registerParser(new BooleanParser(), boolean.class);
		registerParser(new StringParser(), String.class);
	}

	public static void registerParser(ConfigurationsValueParser parser, Class<?> clazz)
	{
		parsers.put(clazz, parser);
	}

	public static ConfigurationsValueParser getStrictParser(Class<?> clazz)
	{
		return parsers.get(clazz);
	}

	public static ConfigurationsValueParser getParser(Class<?> clazz)
	{
		do
		{
			ConfigurationsValueParser parser = getStrictParser(clazz);
			if (parser != null)
			{
				return parser;
			}
		} while ((clazz = clazz.getSuperclass()) != null);

		return null;
	}

	public final Property.Type propertyType;

	public ConfigurationsValueParser(Property.Type propertyType)
	{
		this.propertyType = propertyType;
	}

	public abstract Object parse(String value, Class<?> clazz);
}