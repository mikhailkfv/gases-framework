package glenn.gasesframework.common.worldgen;

import glenn.gasesframework.GasesFramework;
import glenn.gasesframework.api.gasworldgentype.GasWorldGenType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class WorldGeneratorGasesFramework implements IWorldGenerator
{
	private static class ChunkBlobs
	{
		private static class Pocket
		{
			private static class Blob
			{
				public final float x, y, z, w;
				
				public Blob(float x, float y, float z, float w)
				{
					this.x = x;
					this.y = y;
					this.z = z;
					this.w = w;
				}
			}
			
			private static final float[][][] preCalculatedDistances = new float[15][15][15];
			
			static
			{
				for(int x = 0; x < 15; x++)
				{
					for(int y = 0; y < 15; y++)
					{
						for(int z = 0; z < 15; z++)
						{
							preCalculatedDistances[x][y][z] = (float)Math.sqrt(x * x + y * y + z * z);
						}
					}
				}
			}
			
			private static float getApproximateDistance(float x, float y, float z)
			{
				return preCalculatedDistances[Math.abs(Math.round(x))][Math.abs(Math.round(y))][Math.abs(Math.round(z))];
			}
			
			public int minX, minY, minZ, maxX, maxY, maxZ;
			public final Blob[] blobs;
			
			public Pocket(Random random, int absoluteChunkX, int absoluteChunkZ, TypeValues type)
			{
				float pocketX = absoluteChunkX + random.nextFloat() * 16.0f;
				float pocketY = random.nextFloat() * (type.type.maxY - type.type.minY) + type.type.minY;
				float pocketZ = absoluteChunkZ + random.nextFloat() * 16.0f;
				
				int numBlobs = randomRound(type.averageBlobFrequency, random);
				
				blobs = new Blob[numBlobs];
				
				for(int blob = 0; blob < numBlobs; blob++)
				{
					float blobX, blobY, blobZ;
					do
					{
						blobX = random.nextFloat() * 2.0f - 1.0f;
						blobY = random.nextFloat() * 2.0f - 1.0f;
						blobZ = random.nextFloat() * 2.0f - 1.0f;
					}
					while(blobX * blobX + blobY * blobY + blobZ * blobZ > 1.0f);
					
					float blobRadius = (random.nextFloat() + 0.5f) * type.averageBlobRadius;
					
					Blob b = new Blob(blobX * type.blobSpread + pocketX, blobY * type.blobSpread + pocketY, blobZ * type.blobSpread + pocketZ, blobRadius);
					blobs[blob] = b;
					
					if(blob != 0)
					{
						minX = Math.min(minX, (int)Math.floor(b.x - b.w));
						maxX = Math.min(maxX, (int)Math.ceil(b.x + b.w));
						minY = Math.min(minY, (int)Math.floor(b.y - b.w));
						maxY = Math.min(maxY, (int)Math.ceil(b.y + b.w));
						minZ = Math.min(minZ, (int)Math.floor(b.z - b.w));
						maxZ = Math.min(maxZ, (int)Math.ceil(b.z + b.w));
					}
					else
					{
						minX = (int)Math.floor(b.x - b.w);
						maxX = (int)Math.ceil(b.x + b.w);
						minY = (int)Math.floor(b.y - b.w);
						maxY = (int)Math.ceil(b.y + b.w);
						minZ = (int)Math.floor(b.z - b.w);
						maxZ = (int)Math.ceil(b.z + b.w);
					}
				}
			}
			
			public void generate(int chunkMinX, int chunkMinZ, int chunkMaxX, int chunkMaxZ)
			{
				int minX = Math.max(this.minX, chunkMinX);
				int maxX = Math.min(this.maxX, chunkMaxX);
				int minZ = Math.max(this.minZ, chunkMinZ);
				int maxZ = Math.min(this.maxZ, chunkMaxZ);
				
				for(int x = minX; x < maxX; x++)
				{
					float xf = x;
					for(int y = minY; y < maxY; y++)
					{
						float yf = y;
						for(int z = minZ; z < maxZ; z++)
						{
							float zf = z;
							float score = 0.0f;
							
							for(Blob b : blobs)
							{
								float r = (b.w - getApproximateDistance(b.x - xf, b.y - yf, b.z - zf)) / b.w;
								
								if(r > 0.0f)
								{
									score += r;
								}
							}
							
							if(score >= 0.25f)
							{
								int volume = currentType.getPlacementVolume(currentWorld, x, y, z, score - 0.25f);
								if(volume > 0)
								{
									currentWorld.setBlock(x, y, z, currentType.gasType.block, 16 - volume, 2);
								}
							}
						}
					}
				}
			}
		}
		
		public final HashMap<String, Pocket[]> pocketsByType = new HashMap<String, Pocket[]>();
		
		public ChunkBlobs(int chunkX, int chunkZ, Collection<TypeValues> types)
		{
			Random random = new Random(currentWorld.getSeed() + new ChunkCoordIntPair(chunkX, chunkZ).hashCode() * currentWorld.provider.getDimensionName().hashCode());
			
			for(TypeValues type : types)
			{
				int numPockets = randomRound((random.nextFloat() + 0.5f) * type.type.generationFrequency * (type.type.maxY - type.type.minY) / 16.0f, random);
				Pocket[] pockets = new Pocket[numPockets];
				for(int pocket = 0; pocket < numPockets; pocket++)
				{
					pockets[pocket] = new Pocket(random, chunkX << 4, chunkZ << 4, type);
				}
				pocketsByType.put(type.type.name, pockets);
			}
		}
		
		public void generate(int chunkMinX, int chunkMinZ, int chunkMaxX, int chunkMaxZ, Collection<TypeValues> types)
		{
			for(TypeValues type : types)
			{
				currentType = type.type;
				Pocket[] pockets = pocketsByType.get(currentType.name);
				
				for(Pocket pocket : pockets)
				{
					pocket.generate(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ);
				}
			}
		}
	}
	
	private static class TypeValues
	{
		public final GasWorldGenType type;
		public final float averageBlobFrequency;
		public final float averagePocketRadius;
		public final float averageBlobRadius;
		public final float blobSpread;
		
		public TypeValues(GasWorldGenType type)
		{
			float r3 = (float)(type.averageVolume * 3.0D / (4.0D * Math.PI));
			
			this.type = type;
			this.averageBlobFrequency = 1.0f + (1.0f - type.evenness) * 9.0f;
			this.averagePocketRadius = (float)Math.cbrt(r3);
			this.averageBlobRadius = 1.5f * (float)Math.cbrt(type.averageVolume / averageBlobFrequency);
			this.blobSpread = 2.0f + averagePocketRadius * (1.0f - type.evenness);
		}
	}
	
	private final HashMap<String, HashMap<String, TypeValues>> typesByDimension = new HashMap<String, HashMap<String, TypeValues>>();
	private final IdentityHashMap<World, HashMap<ChunkCoordIntPair, ChunkBlobs>> chunkBlobsMapsByDimension = new IdentityHashMap<World, HashMap<ChunkCoordIntPair, ChunkBlobs>>();
	
	private final IdentityHashMap<World, HashMap<ChunkCoordIntPair, HashSet<String>>> chunkRetrogenDataByDimension = new IdentityHashMap<World, HashMap<ChunkCoordIntPair, HashSet<String>>>();
	private final IdentityHashMap<World, HashMap<ChunkCoordIntPair, HashSet<String>>> chunksToRetrogenByWorld = new IdentityHashMap<World, HashMap<ChunkCoordIntPair, HashSet<String>>>();
	
	private HashSet<String> getChunkRetrogenData(World world, ChunkCoordIntPair positionKey)
	{
		HashMap<ChunkCoordIntPair, HashSet<String>> chunkRetrogenData = getChunkRetrogenDataMap(world);
		HashSet<String> retrogenData = chunkRetrogenData.get(positionKey);
		if(retrogenData == null)
		{
			retrogenData = new HashSet<String>();
			chunkRetrogenData.put(positionKey, retrogenData);
		}
		return retrogenData;
	}
	
	private HashSet<String> tryGetChunkRetrogenData(World world, ChunkCoordIntPair positionKey)
	{
		HashMap<ChunkCoordIntPair, HashSet<String>> chunkRetrogenData = tryGetChunkRetrogenDataMap(world);
		if(chunkRetrogenData != null)
		{
			return chunkRetrogenData.get(positionKey);
		}
		return null;
	}
	
	private HashMap<ChunkCoordIntPair, HashSet<String>> getChunkRetrogenDataMap(World world)
	{
		HashMap<ChunkCoordIntPair, HashSet<String>> chunkRetrogenData = chunkRetrogenDataByDimension.get(world);
		if(chunkRetrogenData == null)
		{
			chunkRetrogenData = new HashMap<ChunkCoordIntPair, HashSet<String>>();
			chunkRetrogenDataByDimension.put(world, chunkRetrogenData);
		}
		return chunkRetrogenData;
	}
	
	private HashMap<ChunkCoordIntPair, HashSet<String>> tryGetChunkRetrogenDataMap(World world)
	{
		return chunkRetrogenDataByDimension.get(world);
	}
	
	private HashMap<ChunkCoordIntPair, HashSet<String>> getChunksToRetrogen(World world)
	{
		HashMap<ChunkCoordIntPair, HashSet<String>> chunksToRetrogen = chunksToRetrogenByWorld.get(world);
		if(chunksToRetrogen == null)
		{
			chunksToRetrogen = new HashMap<ChunkCoordIntPair, HashSet<String>>();
			chunksToRetrogenByWorld.put(world, chunksToRetrogen);
		}
		return chunksToRetrogen;
	}
	
	private HashMap<ChunkCoordIntPair, HashSet<String>> tryGetChunksToRetrogen(World world)
	{
		return chunksToRetrogenByWorld.get(world);
	}
	
	private void unmapChunkRetrogenData(World world, String dimensionKey, ChunkCoordIntPair positionKey)
	{
		HashMap<ChunkCoordIntPair, HashSet<String>> chunkRetrogenData = tryGetChunkRetrogenDataMap(world);
		if(chunkRetrogenData != null)
		{
			chunkRetrogenData.remove(positionKey);
		}
		
		HashMap<ChunkCoordIntPair, HashSet<String>> chunksToRetrogen = tryGetChunksToRetrogen(world);
		if(chunksToRetrogen != null)
		{
			chunksToRetrogen.remove(positionKey);
		}
	}
	
	private void tryQueueRetrogen(World world, String dimensionKey, ChunkCoordIntPair positionKey)
	{
		HashMap<String, TypeValues> types = typesByDimension.get(dimensionKey);
		if(types != null && types.size() > 0)
		{
			HashSet<String> previouslyGenerated = getChunkRetrogenData(world, positionKey);
			HashSet<String> toGenerate = new HashSet<String>();
			for(TypeValues type : types.values())
			{
				if(!previouslyGenerated.contains(type.type.name))
				{
					toGenerate.add(type.type.name);
				}
			}
			
			if(toGenerate.size() > 0)
			{
				HashMap<ChunkCoordIntPair, HashSet<String>> chunksToRetrogen = getChunksToRetrogen(world);
				chunksToRetrogen.put(positionKey, toGenerate);
			}
		}
	}
	
	private static GasWorldGenType currentType;
	private static World currentWorld;
	
    public WorldGeneratorGasesFramework()
    {
    	
    }
    
    public void registerGasWorldGenType(GasWorldGenType type, String dimension)
    {
    	String dimensionKey = dimension.toLowerCase();
    	
    	HashMap<String, TypeValues> typeValuesMap = typesByDimension.get(dimensionKey);
    	if(typeValuesMap == null)
    	{
    		typeValuesMap = new HashMap<String, TypeValues>();
    		typesByDimension.put(dimension, typeValuesMap);
    	}

    	TypeValues typeValues = new TypeValues(type);
    	if(!typeValuesMap.containsKey(typeValues.type.name))
    	{
    		typeValuesMap.put(typeValues.type.name, typeValues);
    	}
    	else
    	{
    		throw new RuntimeException("A gas world gen type was attempted registered to a dimension it was already registered to");
    	}
    }
    
    private static int randomRound(float f, Random random)
    {
    	float r = (float)Math.floor(f);
    	if(random.nextFloat() < f - r) r += 1.0f;
    	return (int)r;
    }

	@Override
	public synchronized void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		HashMap<String, TypeValues> typesMap = typesByDimension.get(world.provider.getDimensionName().toLowerCase());
		if(typesMap != null)
		{
			generate(chunkX, chunkZ, world, typesMap.values());
		}
	}
	
	private void generate(int chunkX, int chunkZ, World world, Collection<TypeValues> types)
	{
		currentWorld = world;
		
		String dimensionKey = world.provider.getDimensionName().toLowerCase();
		
		if(types != null && types.size() > 0)
		{
			int chunkMinX = chunkX << 4, chunkMinZ = chunkZ << 4;
			int chunkMaxX = chunkMinX + 16, chunkMaxZ = chunkMinZ + 16;
			
			HashMap<ChunkCoordIntPair, ChunkBlobs> chunkBlobsMap = chunkBlobsMapsByDimension.get(dimensionKey);
			if(chunkBlobsMap == null)
			{
				chunkBlobsMap = new HashMap<ChunkCoordIntPair, ChunkBlobs>();
				chunkBlobsMapsByDimension.put(world, chunkBlobsMap);
			}
			
			for(int x = chunkX - 1; x < chunkX + 1; x++)
			{
				for(int z = chunkZ - 1; z < chunkZ + 1; z++)
				{
					ChunkCoordIntPair positionKey = new ChunkCoordIntPair(x, z);
					ChunkBlobs chunkBlobs = chunkBlobsMap.get(positionKey);
					if(chunkBlobs == null)
					{
						chunkBlobs = new ChunkBlobs(x, z, types);
						chunkBlobsMap.put(positionKey, chunkBlobs);
					}
					
					chunkBlobs.generate(chunkMinX, chunkMinZ, chunkMaxX, chunkMaxZ, types);
					
					if(areChunksAroundChunkLoaded(world.getChunkProvider(), x, z))
					{
						chunkBlobsMap.remove(positionKey);
					}
				}
			}
			
			HashSet<String> retrogenData = getChunkRetrogenData(world, new ChunkCoordIntPair(chunkX, chunkZ));
			for(TypeValues type : types)
			{
				retrogenData.add(type.type.name);
			}
		}
	}
	
	private boolean areChunksAroundChunkLoaded(IChunkProvider chunkGenerator, int x, int z)
	{
		for(int x1 = x; x1 < x - 1; x1++)
		{
			for(int z1 = z; z1 < z - 1; z1++)
			{
				if(x1 != x && z1 != z && !chunkGenerator.chunkExists(x1, z1))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public synchronized void onChunkLoad(ChunkDataEvent.Load event)
	{
		if(!event.world.isRemote)
		{
			String dimensionKey = event.world.provider.getDimensionName().toLowerCase();
			
			Chunk chunk = event.getChunk();
			ChunkCoordIntPair positionKey = chunk.getChunkCoordIntPair();
			
			NBTTagCompound data = event.getData();
			NBTTagCompound gasWorldGenData = data.getCompoundTag("gasesFramework_worldGenData");
			NBTTagList previouslyGeneratedTypesList = gasWorldGenData.getTagList("generatedTypes", 8);
			
			HashSet<String> retrogenData = getChunkRetrogenData(event.world, positionKey);
			for(int i = 0; i < previouslyGeneratedTypesList.tagCount(); i++)
			{
				retrogenData.add(previouslyGeneratedTypesList.getStringTagAt(i));
			}
			
			if(GasesFramework.configurations.worldGeneration.retrogen.enabled)
			{
				tryQueueRetrogen(event.world, dimensionKey, positionKey);
			}
		}
	}
	
	public synchronized void onChunkSave(ChunkDataEvent.Save event)
	{
		if(!event.world.isRemote)
		{
			String dimensionKey = event.world.provider.getDimensionName().toLowerCase();
			HashMap<String, TypeValues> typesMap = typesByDimension.get(dimensionKey);
			if(typesMap != null)
			{
				Collection<TypeValues> types = typesMap.values();
				if(types != null)
				{
					Chunk chunk = event.getChunk();
					HashMap<ChunkCoordIntPair, HashSet<String>> chunkRetrogenData = tryGetChunkRetrogenDataMap(chunk.worldObj);
					if(chunkRetrogenData != null)
					{
						ChunkCoordIntPair positionKey = chunk.getChunkCoordIntPair();
						HashSet<String> retrogenData = chunkRetrogenData.get(positionKey);
						if(retrogenData != null)
						{
							NBTTagCompound data = event.getData();
							NBTTagCompound gasWorldGenData = data.getCompoundTag("gasesFramework_worldGenData");
							NBTTagList previouslyGeneratedTypesList = gasWorldGenData.getTagList("generatedTypes", 8);
							
							HashSet<String> previouslyGeneratedTypes = new HashSet<String>();
							for(int i = 0; i < previouslyGeneratedTypesList.tagCount(); i++)
							{
								previouslyGeneratedTypes.add(previouslyGeneratedTypesList.getStringTagAt(i));
							}
							
							for(String typeName : retrogenData)
							{
								if(!previouslyGeneratedTypes.contains(typeName))
								{
									previouslyGeneratedTypesList.appendTag(new NBTTagString(typeName));
								}
							}
							
							gasWorldGenData.setTag("generatedTypes", previouslyGeneratedTypesList);
							data.setTag("gasesFramework_worldGenData", gasWorldGenData);
						}
					}
				}
			}
		}
	}
	
	public synchronized void onChunkUnload(ChunkEvent.Unload event)
	{
		Chunk chunk = event.getChunk();
		String dimensionKey = event.world.provider.getDimensionName().toLowerCase();
		ChunkCoordIntPair positionKey = chunk.getChunkCoordIntPair();
		unmapChunkRetrogenData(chunk.worldObj, dimensionKey, positionKey);
	}
	
	public synchronized void onServerTick(ServerTickEvent event)
	{
		for(Map.Entry<World, HashMap<ChunkCoordIntPair, HashSet<String>>> chunksToRetrogen : chunksToRetrogenByWorld.entrySet())
		{
			World world = chunksToRetrogen.getKey();
			for(Map.Entry<ChunkCoordIntPair, HashSet<String>> chunkToRetrogen : chunksToRetrogen.getValue().entrySet())
			{
				String dimensionKey = world.provider.getDimensionName().toLowerCase();
				HashMap<String, TypeValues> types = typesByDimension.get(dimensionKey);
				if(types != null)
				{
					ChunkCoordIntPair positionKey = chunkToRetrogen.getKey();
					if(world.getChunkProvider().chunkExists(positionKey.chunkXPos, positionKey.chunkZPos))
					{
						HashSet<String> alreadyGenerated = getChunkRetrogenData(world, positionKey);
						ArrayList<TypeValues> toGenerate = new ArrayList<TypeValues>();
						
						for(String typeName : chunkToRetrogen.getValue())
						{
							toGenerate.add(types.get(typeName));
							alreadyGenerated.add(typeName);
						}
						
						generate(positionKey.chunkXPos, positionKey.chunkZPos, world, toGenerate);
						chunksToRetrogen.getValue().remove(positionKey);
						if(chunksToRetrogen.getValue().size() == 0)
						{
							chunksToRetrogenByWorld.remove(world);
						}
						
						return;
					}
				}
			}
		}
	}
}