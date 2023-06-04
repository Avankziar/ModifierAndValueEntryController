package main.java.me.avankziar.mavec.spigot.assistance;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class BackgroundTask
{
	private static MAVEC plugin;
	
	public BackgroundTask(MAVEC plugin)
	{
		BackgroundTask.plugin = plugin;
		initUpdateTask();
	}
	
	public void initUpdateTask()
	{
		int mulp = plugin.getYamlHandler().getConfig().getInt("DeleteOldDataTask.RunInSeconds", 60);
		if(mulp <= 0)
		{
			mulp = 60;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getOnlinePlayers())
				{
					long now = System.currentTimeMillis();
					UUID uuid = player.getUniqueId();
					int m = plugin.getMysqlHandler().getCount(MysqlHandler.Type.MODIFIER,
							"`player_uuid` = ? AND `duration` > ? AND duration < ?",
							uuid.toString(), 0, now);
					if(m > 0)
					{
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
								"`player_uuid` = ? AND `duration` > ? AND duration < ?",
								uuid.toString(), 0, now);
						plugin.getModifier().update(uuid);
					}
					int v = plugin.getMysqlHandler().getCount(MysqlHandler.Type.VALUEENTRY,
							"`player_uuid` = ? AND `duration` > ? AND duration < ?",
							uuid.toString(), 0, now);
					if(v > 0)
					{
						plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
								"`player_uuid` = ? AND `duration` > ? AND duration < ?",
								uuid.toString(), 0, now);
					}
				}
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 20L*mulp);
	}
}
