package main.java.me.avankziar.mavec.spigot.cmd.valueentry;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class ARGValueEntryRemove extends ArgumentModule
{
	private MAVEC plugin;
	
	public ARGValueEntryRemove(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String valuelable = args[2];
		String othername = args[3];
		String reason = "/";
		if(args.length >= 5)
		{
			reason = args[4];
		}
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
		int count = 0;
		if(reason.equals("/"))
		{
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ?", uuid.toString(), valuelable);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ?", uuid.toString(), valuelable);
		} else
		{
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `intern_reason` = ?", uuid.toString(), valuelable, reason);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `intern_reason` = ?", uuid.toString(), valuelable, reason);
		}
		sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Remove.Remove")
				.replace("%ve%", valuelable)
				.replace("%player%", othername)
				.replace("%reason%", reason)
				.replace("%count%", String.valueOf(count))
				));
		return;
	}
}