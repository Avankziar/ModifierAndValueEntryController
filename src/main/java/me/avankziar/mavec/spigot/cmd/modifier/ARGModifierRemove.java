package main.java.me.avankziar.mavec.spigot.cmd.modifier;

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

public class ARGModifierRemove extends ArgumentModule
{
	private MAVEC plugin;
	
	public ARGModifierRemove(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		String bonusmalus = args[2];
		String othername = args[3];
		String reason = "/";
		if(args.length >= 5)
		{
			reason = args[4];
		}
		if(!plugin.getModifier().isRegistered(bonusmalus))
		{
			sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Add.IsNotRegistered")));
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
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ?", uuid.toString(), bonusmalus);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ?", uuid.toString(), bonusmalus);
		} else
		{
			count = plugin.getMysqlHandler().getCount(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ?", uuid.toString(), bonusmalus, reason);
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ?", uuid.toString(), bonusmalus, reason);
		}
		plugin.getModifier().update();
		sender.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Remove.Remove")
				.replace("%mod%", bonusmalus)
				.replace("%player%", othername)
				.replace("%reason%", reason)
				.replace("%count%", String.valueOf(count))
				));
		return;
	}
}