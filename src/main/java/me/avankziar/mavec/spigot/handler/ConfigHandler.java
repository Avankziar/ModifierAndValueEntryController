package main.java.me.avankziar.mavec.spigot.handler;

import main.java.me.avankziar.mavec.spigot.cmdtree.BaseConstructor;

public class ConfigHandler
{	
	public enum CountType
	{
		HIGHEST, ADDUP;
	}
	
	public CountType getCountPermType()
	{
		String s = BaseConstructor.getPlugin().getYamlHandler().getConfig().getString("Mechanic.CountPerm", "HIGHEST");
		CountType ct;
		try
		{
			ct = CountType.valueOf(s);
		} catch (Exception e)
		{
			ct = CountType.HIGHEST;
		}
		return ct;
	}
	
	public boolean isMechanicModifierEnabled()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.Modifier", false);
	}
	
	public boolean isMechanicValueEntryEnabled()
	{
		return BaseConstructor.getPlugin().getYamlHandler().getConfig().getBoolean("EnableMechanic.ValueEntry", false);
	}
}