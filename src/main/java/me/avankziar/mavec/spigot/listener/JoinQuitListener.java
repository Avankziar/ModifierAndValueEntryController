package main.java.me.avankziar.mavec.spigot.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.me.avankziar.mavec.spigot.MAVEC;

public class JoinQuitListener implements Listener
{
	private MAVEC plugin;
	
	public JoinQuitListener(MAVEC plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		plugin.getModifier().join(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		plugin.getModifier().quit(uuid);
	}
}