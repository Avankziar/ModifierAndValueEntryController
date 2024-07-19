package main.java.me.avankziar.mavec.spigot.ifh;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler.Type;
import main.java.me.avankziar.mavec.spigot.objects.Modification;
import main.java.me.avankziar.mavec.spigot.objects.Modifier;
import main.java.me.avankziar.mavec.spigot.objects.ModifierBaseValue;
import me.avankziar.ifh.general.modifier.ModificationType;
import me.avankziar.ifh.general.modifier.ModifierType;

public class ModifierProvider implements me.avankziar.ifh.general.modifier.Modifier
{
	private MAVEC plugin;
	private static ArrayList<Modification> registeredMod = new ArrayList<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, Double>> modPerUUIDSUM = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, Double>> modPerUUIDMUL = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, Double>> modPerUUIDPerServerSUM = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, Double>> modPerUUIDPerServerMUL = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, LinkedHashMap<String, Double>>> modPerUUIDPerServerPerWorldSUM = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, LinkedHashMap<String, LinkedHashMap<String, Double>>> modPerUUIDPerServerPerWorldMUL = new LinkedHashMap<>();
	
	public ModifierProvider(MAVEC plugin)
	{
		this.plugin = plugin;
		if(registeredMod.isEmpty())
		{
			init();
		}
	}
	
	public void init()
	{
		ArrayList<Modification> bmlist = Modification.convert(plugin.getMysqlHandler()
				.getFullList(MysqlHandler.Type.MODIFICATION, "`id`", "1"));
		registeredMod.addAll(bmlist);
		MAVEC.log.info(bmlist.size()+" Modification are registered!");
	}
	
	public void join(UUID uuid)
	{
		ArrayList<Modifier> modilist = Modifier.convert(
				plugin.getMysqlHandler().getFullList(MysqlHandler.Type.MODIFIER, "`id` ASC",
						"`player_uuid` = ?", uuid.toString()));
		ArrayList<String> modil = new ArrayList<>();
		LinkedHashMap<String, Double> summap = new LinkedHashMap<>();
		LinkedHashMap<String, Double> mulmap = new LinkedHashMap<>();
		LinkedHashMap<String, Double> summapserver = new LinkedHashMap<>();
		LinkedHashMap<String, Double> mulmapserver = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, Double>> summapworld = new LinkedHashMap<>();
		LinkedHashMap<String, LinkedHashMap<String, Double>> mulmapworld = new LinkedHashMap<>();
		long now = System.currentTimeMillis();
		String server = plugin.getAdministration() != null ? plugin.getAdministration().getSpigotServerName() 
				: plugin.getYamlHandler().getConfig().getString("ServerName");
		for(Modifier modi : modilist)
		{
			String modis = modi.getModificationName();
			modil.add(modis);
			if(modi.getDuration() > 0 && modi.getDuration() < now)
			{
				plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER, "`id` = ?", modi.getID());
			}
			switch(modi.getType())
			{
			case ADDITION:
				if(modi.getServer() != null && modi.getServer().equals(server))
				{
					if(modi.getWorld() != null)
					{
						//world
						LinkedHashMap<String, Double> summ = new LinkedHashMap<>();
						if(summapworld.containsKey(modi.getWorld()))
						{
							summ = summapworld.get(modi.getWorld());
						} 
						summ.put(modis, summ.containsKey(modis) ? summ.get(modis) + modi.getValue() : modi.getValue());
						summapworld.put(modi.getWorld(), summ);
						break;
					}
					//server
					summapserver.put(modis, summapserver.containsKey(modis) ? summapserver.get(modis) + modi.getValue() : modi.getValue());
					break;
				}
				//global
				summap.put(modis, summap.containsKey(modis) ? summap.get(modis) + modi.getValue() : modi.getValue());
				break;
			case MULTIPLICATION:
				if(modi.getServer() != null && modi.getServer().equals(server))
				{
					if(modi.getWorld() != null)
					{
						//world
						LinkedHashMap<String, Double> mulm = new LinkedHashMap<>();
						if(mulmapworld.containsKey(modi.getWorld()))
						{
							mulm = summapworld.get(modi.getWorld());
						}
						
						mulm.put(modis, mulm.containsKey(modis) ? mulm.get(modis) + modi.getValue() : modi.getValue());
						mulmapworld.put(modi.getWorld(), mulm);
						break;
					}
					//server
					mulmapserver.put(modis, mulmapserver.containsKey(modis) ? mulmapserver.get(modis) + modi.getValue() : modi.getValue());
					break;
				}
				//global
				mulmap.put(modis, mulmap.containsKey(modis) ? mulmap.get(modis) + modi.getValue() : modi.getValue());
				break;
			}
		}
		modPerUUIDSUM.put(uuid, summap);
		modPerUUIDMUL.put(uuid, mulmap);
		modPerUUIDPerServerSUM.put(uuid, summapserver);
		modPerUUIDPerServerMUL.put(uuid, mulmapserver);
		modPerUUIDPerServerPerWorldSUM.put(uuid, summapworld);
		modPerUUIDPerServerPerWorldMUL.put(uuid, mulmapworld);
	}
	
	public void quit(UUID uuid)
	{
		modPerUUIDSUM.remove(uuid);
		modPerUUIDMUL.remove(uuid);
		modPerUUIDPerServerSUM.remove(uuid);
		modPerUUIDPerServerMUL.remove(uuid);
		modPerUUIDPerServerPerWorldSUM.remove(uuid);
		modPerUUIDPerServerPerWorldMUL.remove(uuid);
	}
	
	public double getSumValue(UUID uuid, String bonusMalusName, String server, String world)
	{
		double d = 0.0;
		if(server != null)
		{
			if(world != null)
			{
				if(modPerUUIDPerServerPerWorldSUM.containsKey(uuid) 
						&& modPerUUIDPerServerPerWorldSUM.get(uuid).containsKey(world))
				{
					d += modPerUUIDPerServerPerWorldSUM.get(uuid).get(world).containsKey(bonusMalusName)
							? modPerUUIDPerServerPerWorldSUM.get(uuid).get(world).get(bonusMalusName) : 0;
				}			
			}
			d += modPerUUIDPerServerSUM.containsKey(uuid) ?
					(modPerUUIDPerServerSUM.get(uuid).containsKey(bonusMalusName) ? modPerUUIDPerServerSUM.get(uuid).get(bonusMalusName) : 0)
					: 0;
		}
		d += modPerUUIDSUM.containsKey(uuid) ?
				(modPerUUIDSUM.get(uuid).containsKey(bonusMalusName) ? modPerUUIDSUM.get(uuid).get(bonusMalusName) : 0)
				: 0;
		return d;
	}
	
	public double getMulltiplyValue(UUID uuid, String bonusMalusName, String server, String world)
	{
		double d = 0.0;
		if(server != null)
		{
			if(world != null)
			{
				if(modPerUUIDPerServerPerWorldMUL.containsKey(uuid) 
						&& modPerUUIDPerServerPerWorldMUL.get(uuid).containsKey(world)
						&& modPerUUIDPerServerPerWorldMUL.get(uuid).get(world).containsKey(bonusMalusName))
				{
					d += modPerUUIDPerServerPerWorldMUL.get(uuid).get(world).get(bonusMalusName);
				}				
			}
			if(modPerUUIDPerServerMUL.containsKey(uuid)
					&& modPerUUIDPerServerMUL.get(uuid).containsKey(bonusMalusName))
			{
				d += modPerUUIDPerServerMUL.get(uuid).get(bonusMalusName);
			}
		}
		if(modPerUUIDMUL.containsKey(uuid)
				&& modPerUUIDMUL.get(uuid).containsKey(bonusMalusName))
		{
			d += modPerUUIDMUL.get(uuid).get(bonusMalusName);
		}
		return d == 0.0 ? 1.0 : d;
	}
	
	public ArrayList<Modification> getRegisteredMod()
	{
		return registeredMod;
	}
	
	public LinkedHashMap<String, Double> getRegisteredValues(UUID uuid, String world, boolean additionOrMultiplication, String levelType)
	{
		switch(levelType)
		{
		default:
		case "global":
			if(additionOrMultiplication)
			{
				return modPerUUIDSUM.get(uuid);
			} else
			{
				return modPerUUIDMUL.get(uuid);
			}
		case "server":
			if(additionOrMultiplication)
			{
				return modPerUUIDPerServerSUM.get(uuid);
			} else
			{
				return modPerUUIDPerServerMUL.get(uuid);
			}
		case "world":
			if(additionOrMultiplication)
			{
				LinkedHashMap<String, LinkedHashMap<String, Double>> map = modPerUUIDPerServerPerWorldSUM.get(uuid);
				return map.get(world);
			} else
			{
				LinkedHashMap<String, LinkedHashMap<String, Double>> map = modPerUUIDPerServerPerWorldMUL.get(uuid);
				return map.get(world);
			}
		}
	}

	public boolean isRegistered(String modificationName)
	{
		for(Modification mod : registeredMod)
		{
			if(mod.getInternName().equals(modificationName))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean register(String modificationName, String displayName,
			ModificationType type,
			String...explanation)
	{
		if(isRegistered(modificationName))
		{
			return false;
		}
		if(modificationName == null || displayName == null
				|| type == null)
		{
			return false;
		}
		Modification mod = new Modification(modificationName, displayName, type, explanation);
		plugin.getMysqlHandler().create(MysqlHandler.Type.MODIFICATION, mod);
		registeredMod.add(mod);
		return true;
	}
	
	public ArrayList<Modification> getRegistered()
	{
		ArrayList<Modification> list = registeredMod;
		return list;
	}
	
	public me.avankziar.ifh.general.modifier.objects.Modification getRegisteredModification(String internName)
	{
		for(Modification mod : registeredMod)
		{
			if(mod.getInternName().equals(internName))
			{
				return mod.getModification();
			}
		}
		return null;
	}
	
	public ArrayList<me.avankziar.ifh.general.modifier.objects.Modification> getRegisteredModification()
	{
		ArrayList<me.avankziar.ifh.general.modifier.objects.Modification> list = new ArrayList<>();
		for(Modification mod : registeredMod)
		{
			list.add(mod.getModification());
		}
		return list;
	}
	
	public ArrayList<me.avankziar.ifh.general.modifier.objects.Modification>  getRegisteredModification(ModificationType type)
	{
		ArrayList<me.avankziar.ifh.general.modifier.objects.Modification> list = new ArrayList<>();
		for(Modification mod : registeredMod)
		{
			if(mod.getType() == type)
			{
				list.add(mod);
			}
		}
		return list;
	}
	
	public void remove(@Nullable UUID uuid, @Nullable String modificationName, @Nullable String internReason)
	{
		if(uuid != null && modificationName != null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ?",
					uuid.toString(), modificationName, internReason);
			update();
		} else if(uuid == null && modificationName != null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`modification_name` = ? AND `intern_reason` = ?",
					modificationName, internReason);
			update();
		} else if(uuid != null && modificationName == null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `intern_reason` = ?",
					uuid.toString(), internReason);
			update();
		} else if(uuid != null && modificationName != null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ?",
					uuid.toString(), modificationName);
			update();
		} else if(uuid == null && modificationName == null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`intern_reason` = ?",
					internReason);
			update();
		} else if(uuid == null && modificationName != null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`modification_name` = ?",
					modificationName);
			update();
		} else if(uuid != null && modificationName == null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ?",
					uuid.toString());
			update();
		}
	}
	
	public void remove(UUID uuid, String modificationName, String internReason,
			@Nullable String server, @Nullable String world)
	{
		if(uuid != null && modificationName != null && internReason != null && server != null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), modificationName, internReason, server, world);
			update();
		} else if(uuid != null && modificationName != null && internReason != null && server == null && world != null)
		{
			remove(uuid, modificationName, internReason);
		} else if(uuid != null && modificationName != null && internReason != null && server != null && world == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ?",
					uuid.toString(), modificationName, internReason, server);
			update();
		}
	}
	
	public void remove(@Nullable String server, @Nullable String world)
	{
		if(server != null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`server` = ? AND `world` = ?",
					server, world);
			update();
		} else if(server != null && world == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`server` = ?",
					server);
			update();
		} else if(server == null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.MODIFIER,
					"`world` = ?",
					world);
			update();
		}
	}
	
	public boolean hasModifier(UUID uuid, String modificationName, @Nullable String internReason,
			@Nullable String server, @Nullable String world)
	{
		if(internReason != null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ?", 
					uuid.toString(), modificationName, internReason, server);
		} else if(internReason != null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), modificationName, internReason, server, world);
		} else if(internReason == null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `server` = ?",
					uuid.toString(), modificationName, server);
		} else if(internReason == null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), modificationName, server, world);
		} else if(internReason == null && (server == null && world == null)
				|| server == null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.MODIFIER,
					"`player_uuid` = ? AND `modification_name` = ?",
					uuid.toString(), modificationName);
		}
		return false;
	}
	
	public double getLastBaseValue(final UUID uuid, final String modificationName)
	{
		ModifierBaseValue modbv = (ModifierBaseValue) plugin.getMysqlHandler().getData(MysqlHandler.Type.MODIFIERBASEVALUE,
				"`player_uuid` = ? AND `modification_name` = ?",
				uuid.toString(), modificationName);
		if(modbv == null)
		{
			return 1.0;
		} else
		{
			return modbv.getLastBaseValue();
		}
	}
	
	public LinkedHashMap<String, Double> getPlayerGlobalModifier(UUID uuid, ModifierType type)
	{
		LinkedHashMap<String, Double> map = (type == ModifierType.ADDITION ? modPerUUIDSUM.get(uuid) : modPerUUIDMUL.get(uuid));
		return map;
	}
	
	public LinkedHashMap<String, Double> getPlayerServerModifier(UUID uuid, ModifierType type)
	{
		LinkedHashMap<String, Double> map = (type == ModifierType.ADDITION ? modPerUUIDPerServerSUM.get(uuid) : modPerUUIDPerServerMUL.get(uuid));
		return map;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Double>> getPlayerWorldModifier(UUID uuid, ModifierType type)
	{
		LinkedHashMap<String, LinkedHashMap<String, Double>> map = (type == ModifierType.ADDITION ?
				modPerUUIDPerServerPerWorldSUM.get(uuid) : modPerUUIDPerServerPerWorldMUL.get(uuid));
		return map;
	}
	
	public double getResult(UUID uuid, double baseValue, String bonusMalusName)
	{
		return getResult(uuid, baseValue, bonusMalusName, null, null);
	}
	
	public double getResult(final UUID uuid, final double baseValue, final String modificationName, String server, String world)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				ModifierBaseValue bmbv = (ModifierBaseValue) plugin.getMysqlHandler().getData(MysqlHandler.Type.MODIFIERBASEVALUE,
						"`player_uuid` = ? AND `modification_name` = ?",
						uuid.toString(), modificationName);
				if(bmbv == null)
				{
					bmbv = new ModifierBaseValue(uuid, modificationName, baseValue);
					plugin.getMysqlHandler().create(MysqlHandler.Type.MODIFIERBASEVALUE, bmbv);
				} else
				{
					bmbv.setLastBaseValue(baseValue);
					plugin.getMysqlHandler().updateData(MysqlHandler.Type.MODIFIERBASEVALUE, bmbv,
							"`player_uuid` = ? AND `modification_name` = ?",
							uuid.toString(), modificationName);
				}
			}
		}.runTaskLaterAsynchronously(plugin, 10L);
		if(!isRegistered(modificationName) || !hasModifier(uuid, modificationName, null, server, world))
		{
			return baseValue;
		}
		return (baseValue + getSumValue(uuid, modificationName, server, world)) * getMulltiplyValue(uuid, modificationName, server, world);
	}
	
	public void addFactor(UUID uuid, String modificationName,
			double value, ModifierType modifierType, String internReason, String displayReason,
			String server, String world,
			Long duration)
	{
		Modifier modi = null;
		if(hasModifier(uuid, modificationName, internReason, server, world))
		{
			if(server != null && world == null)
			{
				modi = (Modifier) plugin.getMysqlHandler().getData(MysqlHandler.Type.MODIFIER,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ?", 
						uuid.toString(), modificationName, internReason, server);
			} else if(server != null && world != null)
			{
				modi = (Modifier) plugin.getMysqlHandler().getData(MysqlHandler.Type.MODIFIER,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?",
						uuid.toString(), modificationName, internReason, server, world);
			} else
			{
				modi = (Modifier) plugin.getMysqlHandler().getData(MysqlHandler.Type.MODIFIER,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ?",
						uuid.toString(), modificationName, internReason);
			}
			modi.setDisplayReason(displayReason);
			if(duration < 0 && modi.getDuration() > 0)
			{
				modi.setDuration(-1);
			} else if(duration > 0 && modi.getDuration() > 0)
			{
				long dur = modi.getDuration() - System.currentTimeMillis();
				if(dur > 0)
				{
					dur = dur + duration;
				} else
				{
					dur = duration;
				}
				modi.setDuration(dur+System.currentTimeMillis());
			} else
			{
				modi.setDuration(duration+System.currentTimeMillis());
			}
			if(server != null && world == null)
			{
				plugin.getMysqlHandler().updateData(Type.MODIFIER, modi,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ?", 
						uuid.toString(), modificationName, internReason, server);
			} else if(server != null && world != null)
			{
				plugin.getMysqlHandler().updateData(Type.MODIFIER, modi,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?",
						uuid.toString(), modificationName, internReason, server, world);
			} else
			{
				plugin.getMysqlHandler().updateData(Type.MODIFIER, modi,
						"`player_uuid` = ? AND `modification_name` = ? AND `intern_reason` = ?",
						uuid.toString(), modificationName, internReason);
			}
		} else
		{
			modi = new Modifier(0, uuid, modificationName, modifierType,
					value, internReason, displayReason, server, world,
					duration != null ? (duration.longValue() > 0 ? duration.longValue()+System.currentTimeMillis() : -1) : -1);
			plugin.getMysqlHandler().create(MysqlHandler.Type.MODIFIER, modi);
		}
		update(uuid);
	}
	
	public void update()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			final UUID uuid = player.getUniqueId();
			update(uuid);
		}
	}
	
	public void update(UUID uuid)
	{
		if(Bukkit.getPlayer(uuid) == null)
		{
			return;
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				quit(uuid);
				join(uuid);
			}
		}.runTaskLaterAsynchronously(plugin, 2L);
	}
}