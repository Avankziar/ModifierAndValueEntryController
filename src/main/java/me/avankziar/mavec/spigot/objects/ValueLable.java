package main.java.me.avankziar.mavec.spigot.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import main.java.me.avankziar.mavec.spigot.database.MysqlHandable;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;

public class ValueLable extends me.avankziar.ifh.general.valueentry.objects.ValueLable implements MysqlHandable
{	
	public ValueLable(){}
	
	public ValueLable(String internName, String displayName, String[] explanation)
	{
		super(internName, displayName, explanation);
	}
	
	@Override
	public boolean create(Connection conn, String tablename)
	{
		try
		{
			String sql = "INSERT INTO `" + tablename
					+ "`(`valuelable_name`, `display_name`, `explanation`) " 
					+ "VALUES("
					+ "?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getInternName());
	        ps.setString(2, getDisplayName());
	        ps.setString(3, String.join("~!~", getExplanation()));
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
				+ "` SET `valuelable_name` = ?, `display_name` = ?, `explanation` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getInternName());
	        ps.setString(2, getDisplayName());
	        ps.setString(3, String.join("~!~", getExplanation()));
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
			String sql = "SELECT * FROM `" + MysqlHandler.Type.VALUELABLE.getValue() 
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
						new ValueLable(rs.getString("valuelable_name"),
						rs.getString("display_name"),
						rs.getString("explanation").split("~!~")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public static ArrayList<ValueLable> convert(ArrayList<Object> arrayList)
	{
		ArrayList<ValueLable> l = new ArrayList<>();
		for(Object o : arrayList)
		{
			if(o instanceof ValueLable)
			{
				l.add((ValueLable) o);
			}
		}
		return l;
	}
}