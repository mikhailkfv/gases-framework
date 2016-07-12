package glenn.moddingutils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Class that checks for a version update. */
public class UpdateChecker implements Runnable
{
	/**
	 * A Gson struct for version messages coming from the server.
	 */
	private static class VersionMessage
	{
		public int id = -1;
		public int timesToShow = 1;
		public int importance = 1;
		public String[] minecraftVersions = new String[0];
		public String[] modVersions = new String[0];
		public String displayName = "";
		public String[] description = new String[0];
		public String modVersion = "";
		public String URL = "";
	}

	/**
	 * A Gson struct for locally stored message view counts.
	 */
	private static class MessageCounter
	{
		public int id = -1;
		public int timesToShow = 1;
		public int timesShown = 0;

		public MessageCounter(int id, int timesToShow)
		{
			this.id = id;
			this.timesToShow = timesToShow;
		}

		public boolean shouldAlwaysShow()
		{
			return timesToShow == -1;
		}

		public boolean shouldShow()
		{
			return shouldAlwaysShow() || timesShown < timesToShow;
		}
	}

	private final Gson gson = new Gson();
	private final LinkedList<VersionMessage> relevantMessages = new LinkedList<VersionMessage>();
	private File messageCounterFile;
	private ArrayList<MessageCounter> counters;
	private boolean requestValid = false;

	private final String urlString;
	private final String fullModName;
	private final String modID;
	private final String modVersion;
	private final String minecraftVersion;

	/**
	 * Create a new UpdateChecker. Will asynchronously check for updates on the
	 * server and display them to the player when they open a world. This must
	 * be registered on the Minecraft Forge EVENT_BUS.
	 * 
	 * @param url
	 * @param fullModName
	 * @param modID
	 * @param modVersion
	 * @param minecraftVersion
	 */
	public UpdateChecker(String url, String fullModName, String modID, String modVersion, String minecraftVersion)
	{
		this.urlString = url;
		this.fullModName = fullModName;
		this.modID = modID;
		this.modVersion = modVersion;
		this.minecraftVersion = minecraftVersion;

		Thread async = new Thread(this);
		async.start();
	}

	/** Fires when a player joins a world. */
	@SubscribeEvent
	public void onEntityJoinedWorld(EntityJoinWorldEvent event)
	{
		if (requestValid && event.world.isRemote && event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;

			LinkedList<IChatComponent> messageQueue = new LinkedList<IChatComponent>();
			for (VersionMessage message : relevantMessages)
			{
				printMessage(message, messageQueue);
			}

			for (IChatComponent component : messageQueue)
			{
				player.addChatMessage(component);
			}
			messageQueue.clear();

			try
			{
				writeMessageCounterFile();
			} catch (IOException e)
			{

			}
		}
	}

	private File createMetaDirectory() throws FileNotFoundException
	{
		File metaDirectory = new File(Minecraft.getMinecraft().mcDataDir.getPath() + "/config/" + fullModName);
		if (!metaDirectory.exists())
		{
			metaDirectory.mkdirs();
		}

		File readMeFile = new File(metaDirectory.getPath() + "/readMe.txt");
		if (readMeFile.exists())
		{
			readMeFile.delete();
		}
		PrintWriter writer = new PrintWriter(readMeFile);
		writer.print(String.format("This is a folder used by %1$s, a Minecraft mod you have installed.%n" + "This folder, or the contents of this folder, should not be removed or altered as long as %1$s is installed.%n" + "The folder is only used as a small local data store to save data between sessions.%n" + "To disable the use of this folder, disable the update checker in the configurations of %1$s", fullModName));
		writer.close();

		return metaDirectory;
	}

	private ArrayList<MessageCounter> readMessageCounterFile() throws FileNotFoundException, IOException
	{
		ArrayList<MessageCounter> result = null;
		if (messageCounterFile.exists())
		{
			FileReader reader = new FileReader(messageCounterFile);
			result = gson.fromJson(reader, new TypeToken<ArrayList<MessageCounter>>()
			{
			}.getType());
			reader.close();

			if (result == null)
			{
				result = new ArrayList<MessageCounter>();
			}
		}

		if (result == null)
		{
			result = new ArrayList<MessageCounter>();
		}

		return result;
	}

	private void writeMessageCounterFile() throws IOException
	{
		if (messageCounterFile.exists())
		{
			messageCounterFile.createNewFile();
		}

		FileWriter writer = new FileWriter(messageCounterFile);
		gson.toJson(counters, writer);
		writer.close();
	}

	private ArrayList<VersionMessage> getVersionMessages(URL url) throws IOException
	{
		HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();

		httpsConnection.setReadTimeout(10000);
		httpsConnection.setRequestMethod("POST");
		httpsConnection.setRequestProperty("User-Agent", "Minecraft");
		httpsConnection.setDoOutput(true);

		DataOutputStream out = new DataOutputStream(httpsConnection.getOutputStream());
		out.writeBytes("modID=" + modID + "&minecraftVersion=" + minecraftVersion + "&modVersion=" + modVersion);
		out.flush();
		out.close();

		InputStreamReader in = new InputStreamReader(httpsConnection.getInputStream());
		ArrayList<VersionMessage> versionMessages = gson.fromJson(in, new TypeToken<ArrayList<VersionMessage>>()
		{
		}.getType());
		in.close();

		if (versionMessages != null)
		{
			return versionMessages;
		}
		else
		{
			return new ArrayList<VersionMessage>();
		}
	}

	private boolean isValid(VersionMessage message)
	{
		if (message != null)
		{
			if (message.id == -1)
				return false;
			if (message.timesToShow == 0)
				return false;
			if (!versionInRanges(minecraftVersion, message.minecraftVersions) || !versionInRanges(modVersion, message.modVersions))
				return false;

			return true;
		}
		else
		{
			return false;
		}
	}

	/** Processes the version message into a chat message. */
	private void processVersionMessage(VersionMessage message)
	{
		if (isValid(message))
		{
			MessageCounter counter = null;
			for (int i = 0; i < counters.size(); i++)
			{
				MessageCounter c = counters.get(i);
				if (c != null && c.id == message.id)
				{
					counter = c;
					break;
				}
			}

			if (counter == null)
			{
				counter = new MessageCounter(message.id, message.timesToShow);
				counters.add(counter);
			}
			else if (counter.timesToShow != message.timesToShow)
			{
				counter.timesToShow = message.timesToShow;
				counter.timesShown = 0;
			}

			relevantMessages.add(message);
		}
	}

	private void printMessage(VersionMessage message, LinkedList<IChatComponent> messageQueue)
	{
		MessageCounter counter = null;
		for (int i = 0; i < counters.size(); i++)
		{
			MessageCounter c = counters.get(i);
			if (c != null && c.id == message.id)
			{
				counter = c;
				break;
			}
		}

		if (counter != null && counter.shouldShow())
		{
			boolean anythingPrinted = false;

			if (message.displayName != null && message.displayName.length() > 0)
			{
				anythingPrinted = true;
				messageQueue.add(new ChatComponentText(message.displayName).setChatStyle(getImportanceStyle(message.importance).setBold(true)));
			}

			if (message.description != null)
			{
				for (String desc : message.description)
				{
					if (desc == null)
						continue;
					anythingPrinted = true;
					messageQueue.add(new ChatComponentText(desc).setChatStyle(getImportanceStyle(message.importance)));
				}
			}

			if (message.URL != null && message.URL.length() > 0)
			{
				anythingPrinted = true;
				ChatComponentText component = new ChatComponentText(message.URL);
				ChatStyle style = component.getChatStyle();
				style.setUnderlined(true);
				style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, message.URL));
				messageQueue.add(component);
			}

			if (anythingPrinted)
			{
				messageQueue.add(new ChatComponentText(""));

				ChatStyle style = new ChatStyle().setColor(EnumChatFormatting.YELLOW);
				counter.timesShown++;
				if (!counter.shouldAlwaysShow())
				{
					int timesLeft = counter.timesToShow - counter.timesShown;
					if (timesLeft == 0)
					{
						messageQueue.add(new ChatComponentText("This message will not be shown again.").setChatStyle(style));
					}
					else if (timesLeft == 1)
					{
						messageQueue.add(new ChatComponentText("This message will be shown one more time.").setChatStyle(style));
					}
					else if (timesLeft > 1)
					{
						messageQueue.add(new ChatComponentText("This message will be shown " + timesLeft + " more times.").setChatStyle(style));
					}
				}

				messageQueue.add(new ChatComponentText("This update checker can be disabled in the configurations.").setChatStyle(style));
			}
		}
	}

	/** Handles color based on "importance" of the update. */
	private ChatStyle getImportanceStyle(int importance)
	{
		switch (importance)
		{
			case 0:
				return new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
			case 1:
				return new ChatStyle().setColor(EnumChatFormatting.BLUE);
			case 2:
				return new ChatStyle().setColor(EnumChatFormatting.DARK_RED);
		}

		return new ChatStyle();
	}

	private boolean versionInRanges(String version, String[] ranges)
	{
		if (ranges == null || ranges.length == 0)
		{
			return true;
		}

		boolean result = false;

		for (int i = 0; i < ranges.length; i++)
		{
			result ^= versionInRange(version, ranges[i]);
		}

		return result;
	}

	private boolean versionInRange(String version, String range)
	{
		if (range == null || range.length() == 0)
			return true;

		int iVersion = versionStringToInt(version);

		if (range.startsWith("<="))
		{
			return iVersion <= versionStringToInt(range.substring(2));
		}
		else if (range.startsWith(">="))
		{
			return iVersion >= versionStringToInt(range.substring(2));
		}
		else if (range.startsWith("<"))
		{
			return iVersion < versionStringToInt(range.substring(1));
		}
		else if (range.startsWith(">"))
		{
			return iVersion > versionStringToInt(range.substring(1));
		}

		String[] split = range.split("-");
		if (split.length == 2)
		{
			int iRange1 = versionStringToInt(split[0]);
			int iRange2 = versionStringToInt(split[1]);
			return (iVersion <= iRange1 & iVersion >= iRange2) | (iVersion >= iRange1 & iVersion <= iRange2);
		}

		return iVersion == versionStringToInt(range);
	}

	private int versionStringToInt(String version)
	{
		int versionScore = 0;
		int multiplier = 1000 * 1000 * 1000;
		String[] splitVersion = version.split("\\.");

		for (int i = 0; i < splitVersion.length; i++, multiplier /= 1000)
		{
			versionScore += Integer.parseInt(splitVersion[i]) * multiplier;
		}

		return versionScore;
	}

	@Override
	public void run()
	{
		try
		{
			File metaDirectory = createMetaDirectory();
			messageCounterFile = new File(metaDirectory.getPath() + "/messageCounter.json");
			counters = readMessageCounterFile();
			ArrayList<VersionMessage> messages = getVersionMessages(new URL(urlString));

			for (VersionMessage message : messages)
			{
				processVersionMessage(message);
			}

			writeMessageCounterFile();

			requestValid = true;
		} catch (Exception e)
		{
			FMLLog.warning(fullModName + " failed to check for update messages (" + e.toString() + ")");
		}
	}
}