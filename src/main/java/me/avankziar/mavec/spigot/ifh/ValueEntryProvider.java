package main.java.me.avankziar.mavec.spigot.ifh;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;

import main.java.me.avankziar.ifh.general.valueentry.ValueType;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler.Type;
import main.java.me.avankziar.mavec.spigot.objects.ValueEntry;
import main.java.me.avankziar.mavec.spigot.objects.ValueLable;

public class ValueEntryProvider implements main.java.me.avankziar.ifh.general.valueentry.ValueEntry
{
	private MAVEC plugin;
	private static ArrayList<ValueLable> registeredVL = new ArrayList<>();
	
	public ValueEntryProvider()
	{
		this.plugin = MAVEC.getPlugin();
		if(registeredVL.isEmpty())
		{
			init();
		}
	}
	
	public void init()
	{
		ArrayList<ValueLable> clist = ValueLable.convert(plugin.getMysqlHandler()
				.getFullList(MysqlHandler.Type.VALUELABLE, "`id`", "1"));
		registeredVL.addAll(clist);
		MAVEC.log.info(clist.size()+" ValueLable are registered!");
	}
	
	public boolean isRegistered(String valueLableName)
	{
		for(ValueLable c : registeredVL)
		{
			if(c.getInternName().equals(valueLableName))
			{
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<ValueLable> getRegisteredVL()
	{
		return registeredVL;
	}
	
	public boolean register(String valueLableName, String displayName,
			String...explanation)
	{
		if(isRegistered(valueLableName))
		{
			return false;
		}
		if(valueLableName == null || displayName == null)
		{
			return false;
		}
		ValueLable c = new ValueLable(valueLableName, displayName,
				explanation);
		plugin.getMysqlHandler().create(MysqlHandler.Type.VALUELABLE, c);
		registeredVL.add(c);
		return true;
	}
	
	public main.java.me.avankziar.ifh.general.valueentry.objects.ValueLable getRegisteredValueLable(String valueLableName)
	{
		for(ValueLable c : registeredVL)
		{
			if(c.getInternName().equals(valueLableName))
			{
				return c.getValueLable();
			}
		}
		return null;
	}
	
	public ArrayList<main.java.me.avankziar.ifh.general.valueentry.objects.ValueLable> getRegisteredValueLable()
	{
		ArrayList<main.java.me.avankziar.ifh.general.valueentry.objects.ValueLable> list = new ArrayList<>();
		for(ValueLable c : registeredVL)
		{
			list.add(c.getValueLable());
		}
		return list;
	}
	
	public void remove(@Nullable UUID uuid, @Nullable String valueLableName, @Nullable String internReason)
	{
		if(uuid != null && valueLableName != null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `intern_reason` = ?",
					uuid.toString(), valueLableName, internReason);
		} else if(uuid == null && valueLableName != null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`valuelable_name` = ? AND `intern_reason` = ?",
					valueLableName, internReason);
		} else if(uuid != null && valueLableName == null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `intern_reason` = ?",
					uuid.toString(), internReason);
		} else if(uuid != null && valueLableName != null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ?",
					uuid.toString(), valueLableName);
		} else if(uuid == null && valueLableName == null && internReason != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`intern_reason` = ?",
					internReason);
		} else if(uuid == null && valueLableName != null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`valuelable_name` = ?",
					valueLableName);
		} else if(uuid != null && valueLableName == null && internReason == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ?",
					uuid.toString());
		}
	}
	
	public void remove(UUID uuid, String valueLableName, String internReason,
			@Nullable String server, @Nullable String world)
	{
		if(server != null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), valueLableName, internReason, server, world);
		} else if(server != null && world == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `intern_reason` = ? AND `server` = ?",
					uuid.toString(), valueLableName, internReason, server);
		} else if(server == null)
		{
			remove(uuid, valueLableName, internReason);
		}
	}
	
	public void remove(@Nullable String server, @Nullable String world)
	{
		if(server != null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`server` = ? AND `world` = ?",
					server, world);
		} else if(server != null && world == null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`server` = ?",
					server);
		} else if(server == null && world != null)
		{
			plugin.getMysqlHandler().deleteData(MysqlHandler.Type.VALUEENTRY,
					"`world` = ?",
					world);
		}
	}
	
	public boolean hasValueEntry(UUID uuid, String valueEntryName, ValueType type, @Nullable String internReason, 
			@Nullable String server, @Nullable String world)
	{
		if(internReason != null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `intern_reason` = ? AND `server` = ?"
					, uuid.toString(), valueEntryName, type.toString(), internReason, server);
		} else if(internReason != null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `intern_reason` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), valueEntryName, internReason, server, world);
		} else if(internReason == null && server != null && world == null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ?"
					, uuid.toString(), valueEntryName, server);
		} else if(internReason == null && server != null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), valueEntryName, server, world);
		} else if(internReason == null && (server == null && world == null)
				|| server == null && world != null)
		{
			return plugin.getMysqlHandler().exist(MysqlHandler.Type.VALUEENTRY,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ?"
					, uuid.toString(), valueEntryName);
		}
		return false;
	}
	
	public Boolean getBooleanValueEntry(UUID uuid, String valuelableName, @Nullable String server, @Nullable String world)
	{
		if(!isRegistered(valuelableName))
		{
			return null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.BOOLEAN, null, server, world))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), valuelableName, ValueType.BOOLEAN, server, world);
			return ve != null ? Boolean.valueOf(ve.getValue()) : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.BOOLEAN, null, server, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ?"
					, uuid.toString(), valuelableName, ValueType.BOOLEAN, server, null);
			return ve != null ? Boolean.valueOf(ve.getValue()) : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.BOOLEAN, null, null, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ?"
					, uuid.toString(), valuelableName, ValueType.BOOLEAN, null, null);
			return ve != null ? Boolean.valueOf(ve.getValue()) : null;
		}
		return null;
	}
	
	public Double getNumberValueEntry(UUID uuid, String valuelableName, @Nullable String server, @Nullable String world)
	{
		if(!isRegistered(valuelableName))
		{
			return null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.NUMBER, null, server, world))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), valuelableName, ValueType.NUMBER, server, world);
			return ve != null ? Double.valueOf(ve.getValue()) : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.NUMBER, null, server, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ?"
					, uuid.toString(), valuelableName, ValueType.NUMBER, server, null);
			return ve != null ? Double.valueOf(ve.getValue()) : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.NUMBER, null, null, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ?"
					, uuid.toString(), valuelableName, ValueType.NUMBER, null, null);
			return ve != null ? Double.valueOf(ve.getValue()) : null;
		}
		return null;
	}
	
	public String getTextValueEntry(UUID uuid, String valuelableName, @Nullable String server, @Nullable String world)
	{
		if(!isRegistered(valuelableName))
		{
			return null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.TEXT, null, server, world))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?"
					, uuid.toString(), valuelableName, ValueType.TEXT, server, world);
			return ve != null ? ve.getValue() : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.TEXT, null, server, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ?"
					, uuid.toString(), valuelableName, ValueType.TEXT, server, null);
			return ve != null ? ve.getValue() : null;
		}
		if(hasValueEntry(uuid, valuelableName, ValueType.TEXT, null, null, null))
		{
			ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ?"
					, uuid.toString(), valuelableName, ValueType.TEXT, null, null);
			return ve != null ? ve.getValue() : null;
		}
		return null;
	}
	
	public boolean addValueEntry(UUID uuid, String valuelableName,
			String value, ValueType type,
			String internReason, String displayReason,
			String server, String world,
			Long duration)
	{
		if(hasValueEntry(uuid, valuelableName, type, null, server, world))
		{
			return false;
		}
		ValueEntry ve = new ValueEntry(0, uuid, valuelableName, value, type,
				internReason, displayReason,
				server, world,
				duration != null ? (duration.longValue() > 0 ? duration.longValue()+System.currentTimeMillis() : -1) : -1);
		plugin.getMysqlHandler().create(MysqlHandler.Type.VALUEENTRY, ve);
		return true;
	}
	
	public boolean modifyValueEntry(UUID uuid, String valuelableName,
			String value, ValueType type,
			@Nullable String internReason, @Nullable String displayReason,
			@Nullable String server, @Nullable String world,
			Long duration)
	{
		if(hasValueEntry(uuid, valuelableName, type, null, server, world))
		{
			return false;
		}
		ValueEntry ve = (ValueEntry) plugin.getMysqlHandler().getData(Type.VALUEENTRY,
				"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?",
				uuid.toString(), valuelableName, type.toString(), server, world);
		if(ve == null)
		{
			return false;
		}
		ve.setValue(value);
		if(internReason != null)
		{
			ve.setInternReason(internReason);
		}
		if(displayReason != null)
		{
			ve.setDisplayReason(displayReason);
		}
		if(duration != null)
		{
			ve.setDuration(duration);
		}
		if(server != null && world != null)
		{
			plugin.getMysqlHandler().updateData(Type.VALUEENTRY, ve,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), valuelableName, type.toString(), server, world);
		} else if(server != null && world == null)
		{
			plugin.getMysqlHandler().updateData(Type.VALUEENTRY, ve,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ?",
					uuid.toString(), valuelableName, type.toString(), server);
		} else if((server == null && world == null) || (server == null && world != null))
		{
			plugin.getMysqlHandler().updateData(Type.VALUEENTRY, ve,
					"`player_uuid` = ? AND `valuelable_name` = ? AND `valueentry_type` = ? AND `server` = ? AND `world` = ?",
					uuid.toString(), valuelableName, type.toString());
		}
		return false;
	}
}