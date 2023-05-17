package main.java.me.avankziar.mavec.spigot.cmd.valueentry;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import main.java.me.avankziar.ifh.general.valueentry.ValueType;
import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.assistance.MatchApi;
import main.java.me.avankziar.mavec.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import net.md_5.bungee.api.chat.ClickEvent;

public class ARGValueEntryAdd extends ArgumentModule
{
	private MAVEC plugin;
	
	public ARGValueEntryAdd(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String valuelable = args[2];
		String othername = args[3];
		String type = args[4];
		String dimension = args[5];
		String value = args[6];
		String dur = args[7];
		String internReason = args[8];
		long duration = -1;
		String reason = "";
		ValueType vt = ValueType.BOOLEAN;
		if(!plugin.getValueEntry().isRegistered(valuelable))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Add.IsNotRegistered")));
			return;
		}
		OfflinePlayer other = Bukkit.getPlayer(othername);
		if(other == null || !other.hasPlayedBefore())
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPlayerExist")));
			return;
		}
		final UUID uuid = other.getUniqueId();
		try
		{
			vt = ValueType.valueOf(type);
		} catch(Exception e)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Add.ValueTypeNotCorrect")
					.replace("%value%", type)));
			return;
		}
		String server = null;
		String world = null;
		if(dimension.startsWith("server"))
		{
			String[] sp = dimension.split(":");
			if(sp.length != 2)
			{
				sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, MAVEC.infoCommand));
				return;
			}
			server = sp[1];
		} else if(dimension.startsWith("world"))
		{
			String[] sp = dimension.split(":");
			if(sp.length != 3)
			{
				sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, MAVEC.infoCommand));
				return;
			}
			server = sp[1];
			world = sp[2];
		} else
		{
			dimension = "global";
		}
		if(MatchApi.isLong(dur))
		{
			duration = Long.parseLong(dur);
		} else
		{
			duration = TimeHandler.getRepeatingTimeShort(dur);
		}
		if(duration == 0)
		{
			duration = -1;
		}
		for (int i = 9; i < args.length; i++) 
        {
			reason += args[i];
			if(i < (args.length-1))
			{
				reason += " ";
			}
        }
		if(reason.isBlank())
		{
			reason = "/";
		}
		boolean boo = plugin.getValueEntry().modifyValueEntry(uuid, valuelable, value, vt, internReason, reason, server, world, duration);
		if(!boo)
		{
			plugin.getValueEntry().addValueEntry(uuid, valuelable, value, vt, internReason, reason, server, world, duration);
		}
		if(duration < 0)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Add.AddedPermanent")
					.replace("%ve%", valuelable)
					.replace("%player%", othername)
					.replace("%type%", vt.toString())
					.replace("%dimension%", dimension)
					.replace("%value%", value)
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Add.AddedTemporary")
					.replace("%ve%", valuelable)
					.replace("%player%", othername)
					.replace("%type%", vt.toString())
					.replace("%dimension%", dimension)
					.replace("%value%", value)
					.replace("%duration%", TimeHandler.getRepeatingTime(duration, "dd-HH:mm"))
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		}
		return;
	}
}
