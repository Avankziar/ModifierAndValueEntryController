package main.java.me.avankziar.mavec.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import main.java.me.avankziar.ifh.general.modifier.ModificationType;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandable;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class Modification extends main.java.me.avankziar.ifh.general.modifier.objects.Modification implements MysqlHandable
{
	public Modification(){}
	
	public Modification(String internName, String displayName,
			ModificationType type, String[] explanation)
	{
		super(internName, displayName, type, explanation);
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`modification_name`, `display_name`,"
					+ " `modification_type`, `explanation`) " 
					+ "VALUES("
					+ "?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getInternName());
	        ps.setString(2, getDisplayName());
	        ps.setString(3, getType().toString());
	        ps.setString(4, String.join("~!~", getExplanation()));
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
				+ "` SET `modification_name` = ?, `display_name` = ?, `modification_type` = ?,"
				+ " `explanation` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getInternName());
	        ps.setString(2, getDisplayName());
	        ps.setString(3, getType().toString());
	        ps.setString(4, String.join("~!~", getExplanation()));
			int i = 5;
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
			String sql = "SELECT * FROM `" + MysqlHandler.Type.MODIFICATION.getValue() 
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
						new Modification(rs.getString("modification_name"),
						rs.getString("display_name"),
						ModificationType.valueOf(rs.getString("modification_type")),
						rs.getString("explanation").split("~!~")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<Modification> convert(ArrayList<Object> arrayList)
	{
		ArrayList<Modification> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof Modification)
			{
				l.add((Modification) o);
			}
		}
		return l;
	}
}