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
import me.avankziar.ifh.general.modifier.ModifierType;

public class Modifier extends me.avankziar.ifh.general.modifier.objects.Modifier implements MysqlHandable
{
	private int id;
	
	public Modifier(){}
	
	public Modifier(int id, UUID uuid, String bonusMalusName, ModifierType type,
			double value, String internReason, String displayReason, String server, String world, long duration)
	{
		super(uuid, bonusMalusName, type, value, internReason, displayReason, server, world, duration);
		setID(id);
	}
	
	public int getID()
	{
		return id;
	}

	public void setID(int id)
	{
		this.id = id;
	}

	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `modification_name`, `modifier_type`, `modifier_value`,"
					+ " `intern_reason`, `display_reason`,"
					+ " `server`, `world`, `duration`) " 
					+ "VALUES("
					+ "?, ?, ?, ?, "
					+ "?, ?, "
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getModificationName());
	        ps.setString(3, getType().toString());
	        ps.setDouble(4, getValue());
	        ps.setString(5, getInternReason());
	        ps.setString(6, getDisplayReason());
	        ps.setString(7, getServer());
	        ps.setString(8, getWorld());
	        ps.setLong(9, getDuration());
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
				+ "` SET `player_uuid` = ?, `modification_name` = ?, `modifier_type` = ?, `modifier_value` = ?,"
				+ " `intern_reason` = ?, `display_reason` = ?,"
				+ " `server` = ?, `world` = ?, `duration` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getModificationName());
	        ps.setString(3, getType().toString());
	        ps.setDouble(4, getValue());
	        ps.setString(5, getInternReason());
	        ps.setString(6, getDisplayReason());
	        ps.setString(7, getServer());
	        ps.setString(8, getWorld());
	        ps.setLong(9, getDuration());
			int i = 10;
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
			String sql = "SELECT * FROM `" + MysqlHandler.Type.MODIFIER.getValue() 
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
						new Modifier(
								rs.getInt("id"),
								UUID.fromString(rs.getString("player_uuid")),
								rs.getString("modification_name"),
								ModifierType.valueOf(rs.getString("modifier_type")),
								rs.getDouble("modifier_value"),
								rs.getString("intern_reason"),
								rs.getString("display_reason"),
								rs.getString("server"),
								rs.getString("world"),
								rs.getLong("duration")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<Modifier> convert(ArrayList<Object> arrayList)
	{
		ArrayList<Modifier> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof Modifier)
			{
				l.add((Modifier) o);
			}
		}
		return l;
	}
}