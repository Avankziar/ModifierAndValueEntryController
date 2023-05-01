package main.java.me.avankziar.mavec.spigot.cmd.modifier;

import java.io.IOException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;

public class ARGModifier extends ArgumentModule
{
	private MAVEC plugin;
	
	public ARGModifier(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.OtherCmd")));
		return;
	}
}