package glenn.gasesframework.common;

import glenn.gasesframework.api.GasesFrameworkAPI;
import glenn.moddingutils.ItemRepresentation;

import java.io.BufferedReader;
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
			if(!file.exists())
			{
				file.createNewFile();
				PrintWriter writer = new PrintWriter(file);
				writer.print(String.format("[%n\t%n]"));
				writer.close();
			}
			
			Gson gson = new Gson();
			ArrayList<CustomGasFurnaceRecipe> recipes = gson.fromJson(new FileReader(file), new TypeToken<ArrayList<CustomGasFurnaceRecipe>>(){}.getType());
			
			for(CustomGasFurnaceRecipe recipe : recipes)
			{
				GasesFrameworkAPI.addSpecialFurnaceRecipe(recipe.input.getItemStack(), recipe.output.getItemStack(), recipe.time <= 0 ? 200 : recipe.time, recipe.exp);
			}
		}
		catch(IOException e)
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