package main.java.me.avankziar.mavec.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import main.java.me.avankziar.ifh.general.valueentry.ValueType;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandable;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class ValueEntry extends main.java.me.avankziar.ifh.general.valueentry.objects.ValueEntry implements MysqlHandable
{
	private int id;

	public ValueEntry() {}
	
	public ValueEntry(int id, UUID uuid, String valueLableName,
			String value, ValueType type,
			String internReason, String displayReason, String server, String world, long duration)
	{
		super(uuid, valueLableName, value, type, internReason, displayReason, server, world, duration);
		setId(id);
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`player_uuid`, `valuelable_name`, `valueentry`, `valueentry_type`,"
					+ " `intern_reason`, `display_reason`,"
					+ " `server`, `world`, `duration`) " 
					+ "VALUES("
					+ "?, ?, ?, "
					+ "?, ?, "
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getValueLableName());
	        ps.setString(3, getValue());
	        ps.setString(4, getType().toString());
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
				+ "` SET `player_uuid` = ?, `valuelable_name` = ?, `valueentry` = ?, `valueentry_type` = ?,"
				+ " `intern_reason` = ?, `display_reason` = ?,"
				+ " `server` = ?, `world` = ?, `duration` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getValueLableName());
	        ps.setString(3, getValue());
	        ps.setString(4, getType().toString());
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
			String sql = "SELECT * FROM `" + MysqlHandler.Type.VALUEENTRY.getValue() 
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
						new ValueEntry(
								rs.getInt("id"),
								UUID.fromString(rs.getString("player_uuid")),
								rs.getString("valuelable_name"),
								rs.getString("valueentry"),
								ValueType.valueOf(rs.getString("valueentry_type")),
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
	
	public static ArrayList<ValueEntry> convert(ArrayList<Object> arrayList)
	{
		ArrayList<ValueEntry> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof ValueEntry)
			{
				l.add((ValueEntry) o);
			}
		}
		return l;
	}
}
