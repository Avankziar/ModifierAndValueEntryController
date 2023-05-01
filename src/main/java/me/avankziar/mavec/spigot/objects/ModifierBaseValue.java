package main.java.me.avankziar.mavec.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import main.java.me.avankziar.mavec.spigot.database.MysqlHandable;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class ModifierBaseValue implements MysqlHandable
{
	private UUID uuid;
	private String modificationName;
	private double lastbasevalue;
	
	public ModifierBaseValue()
	{}
	
	public ModifierBaseValue(UUID uuid, String modificationName, double lastbasevalue)
	{
		setUuid(uuid);
		setModificationName(modificationName);
		setLastBaseValue(lastbasevalue);
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public void setUuid(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getModificationName()
	{
		return modificationName;
	}

	public void setModificationName(String modificationName)
	{
		this.modificationName = modificationName;
	}

	public double getLastBaseValue()
	{
		return lastbasevalue;
	}

	public void setLastBaseValue(double lastbasevalue)
	{
		this.lastbasevalue = lastbasevalue;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `modification_name`, `last_base_value`) " 
					+ "VALUES("
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uuid.toString());
	        ps.setString(2, modificationName);
	        ps.setDouble(3, lastbasevalue);
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(MysqlHandler.QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String tablename, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + tablename
				+ "` SET `player_uuid` = ?, `modification_name` = ?, `last_base_value` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uuid.toString());
	        ps.setString(2, modificationName);
	        ps.setDouble(3, lastbasevalue);
			int i = 4;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(MysqlHandler.QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<Object> get(Connection conn, String tablename, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + MysqlHandler.Type.MODIFIERBASEVALUE.getValue() 
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(MysqlHandler.QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<Object> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(
						new ModifierBaseValue(
								UUID.fromString(rs.getString("player_uuid")),
								rs.getString("modification_name"),
								rs.getDouble("last_base_value")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<ModifierBaseValue> convert(ArrayList<Object> arrayList)
	{
		ArrayList<ModifierBaseValue> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof ModifierBaseValue)
			{
				l.add((ModifierBaseValue) o);
			}
		}
		return l;
	}
}