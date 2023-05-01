package main.java.me.avankziar.mavec.spigot.cmd.modifier;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import main.java.me.avankziar.ifh.general.modifier.ModifierType;
import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.assistance.MatchApi;
import main.java.me.avankziar.mavec.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import net.md_5.bungee.api.chat.ClickEvent;

public class ARGModifierAdd extends ArgumentModule
{
	private MAVEC plugin;
	
	public ARGModifierAdd(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String modifier = args[2];
		String othername = args[3];
		String type = args[4];
		String value = args[5];
		double d = 0.0;
		String modiValue = args[6];
		String dur = args[7];
		String internReason = args[8];
		long duration = -1;
		String reason = "";
		ModifierType modit = ModifierType.ADDITION;
		if(!plugin.getModifier().isRegistered(modifier))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Add.IsNotRegistered")));
			return;
		}
		OfflinePlayer other = Bukkit.getPlayer(othername);
		if(other == null || !other.hasPlayedBefore())
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		final UUID uuid = other.getUniqueId();
		String server = null;
		String world = null;
		if(type.startsWith("server"))
		{
			String[] sp = type.split(":");
			if(sp.length != 2)
			{
				sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
						ClickEvent.Action.RUN_COMMAND, MAVEC.infoCommand));
				return;
			}
			server = sp[1];
		} else if(type.startsWith("world"))
		{
			String[] sp = type.split(":");
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
			type = "global";
		}
		if(!MatchApi.isDouble(value))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoDouble")
					.replace("%value%", value)));
			return;
		}
		d = Double.parseDouble(value);
		try
		{
			modit = ModifierType.valueOf(modiValue);
		} catch(Exception e)
		{
			sender.spigot().sendMessage(ChatApi.clickEvent(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
					ClickEvent.Action.RUN_COMMAND, MAVEC.infoCommand));
			return;
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
		plugin.getModifier().addFactor(uuid, modifier, d, modit, reason, internReason, server, world, duration);
		if(duration < 0)
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Add.AddedPermanent")
					.replace("%mod%", modifier)
					.replace("%player%", othername)
					.replace("%type%", type)
					.replace("%formula%", modit.toString())
					.replace("%value%", value)
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		} else
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Add.AddedTemporary")
					.replace("%mod%", modifier)
					.replace("%player%", othername)
					.replace("%type%", type)
					.replace("%formula%", modit.toString())
					.replace("%value%", value)
					.replace("%duration%", TimeHandler.getRepeatingTime(duration, "dd-HH:mm"))
					.replace("%internreason%", internReason)
					.replace("%reason%", reason)
					));
		}
		return;
	}
}