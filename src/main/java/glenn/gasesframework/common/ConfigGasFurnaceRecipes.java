package glenn.gasesframework.common;

import com.google.gson.JsonParseException;
import glenn.gasesframework.GasesFramework;
import glenn.moddingutils.ItemRepresentation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.FMLLog;

public class ConfigGasFurnaceRecipes
{
	public static void load(File file)
	{
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
				PrintWriter writer = new PrintWriter(file);
				writer.print(String.format("[%n\t%n]"));
				writer.close();
			}

			try
			{
				Gson gson = new Gson();
				ArrayList<CustomGasFurnaceRecipe> recipes = gson.fromJson(new FileReader(file), new TypeToken<ArrayList<CustomGasFurnaceRecipe>>(){}.getType());
				for (CustomGasFurnaceRecipe recipe : recipes)
				{
					GasesFramework.registry.registerGasFurnaceRecipe(recipe.input.getMatcherItemStack(), recipe.output.getRealItemStack(), recipe.time <= 0 ? 200 : recipe.time, recipe.exp);
				}
			} catch (JsonParseException e)
			{
				FMLLog.warning("JSON error in custom gas furnace recipe configuration file (%s)", e.toString());
			}

		} catch (IOException e)
		{
			FMLLog.warning("Could not read custom gas furnace recipe configuration file (%s)", e.toString());
		}
	}

	private class CustomGasFurnaceRecipe
	{
		public ItemRepresentation input;
		public ItemRepresentation output;
		public int time = 200;
		public int exp = 200;
	}
}