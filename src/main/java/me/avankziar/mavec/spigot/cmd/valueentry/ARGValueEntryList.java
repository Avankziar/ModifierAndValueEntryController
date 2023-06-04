package main.java.me.avankziar.mavec.spigot.cmd.valueentry;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.ifh.general.math.MatchPrimitiveDataTypes;
import main.java.me.avankziar.ifh.general.math.Mathematic;
import main.java.me.avankziar.ifh.general.valueentry.ValueType;
import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.assistance.MatchApi;
import main.java.me.avankziar.mavec.spigot.assistance.TimeHandler;
import main.java.me.avankziar.mavec.spigot.cmd.MAVECCmdExecutor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;
import main.java.me.avankziar.mavec.spigot.modifiervalueentry.Bypass.Permission;
import main.java.me.avankziar.mavec.spigot.modifiervalueentry.ModifierValueEntry;
import main.java.me.avankziar.mavec.spigot.objects.ValueEntry;
import main.java.me.avankziar.mavec.spigot.objects.ValueLable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGValueEntryList extends ArgumentModule
{
	private MAVEC plugin;
	private HashMap<String, Long> cooldown = new HashMap<>();
	private ArgumentConstructor ac = null;
	
	public ARGValueEntryList(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
		this.ac = argumentConstructor;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		if(cooldown.containsKey(player.getName()))
		{
			if(cooldown.get(player.getName()) > System.currentTimeMillis())
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerCmdCooldown")));
				return;
			}
		}
		cooldown.put(player.getName(), System.currentTimeMillis()+1000L*10);
		int page = 0;
		if(args.length >= 3 && MatchApi.isInteger(args[2]))
		{
			page = Integer.parseInt(args[2]);
		}
		String othername = player.getName();
		if(args.length >= 4)
		{
			if(!ModifierValueEntry.hasPermission(player, Permission.OTHERPLAYER))
			{
				player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return;
			}
			othername = args[3];
		}
		Player other = Bukkit.getPlayer(othername);
		if(other == null)
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerNotExist")));
			return;
		}
		final UUID uuid = other.getUniqueId();
		String type = "global";
		String server = null;
		String world = null;
		if(args.length >= 4)
		{
			type = args[3];
			switch(type)
			{
			default:
			case "global":
				break;
			case "server":
				server = plugin.getAdministration() != null ? plugin.getAdministration().getSpigotServerName() 
						: plugin.getYamlHandler().getConfig().getString("ServerName");
				break;
			case "world":
				server = plugin.getAdministration() != null ? plugin.getAdministration().getSpigotServerName() 
						: plugin.getYamlHandler().getConfig().getString("ServerName");
				world = other.getWorld().getName();
				break;
			}
		}
		ArrayList<ValueLable> rg = plugin.getValueEntry().getRegisteredVL();
		LinkedHashMap<ValueLable, String[]> map = new LinkedHashMap<>();
		int end = page * 10 + 9;
		int i = page * 10;
		while(i < rg.size())
		{
			ValueLable c = rg.get(i);
			ArrayList<String> list = new ArrayList<>();
			if(plugin.getValueEntry().hasValueEntry(uuid, c.getInternName(), ValueType.BOOLEAN, null, server, world))
			{
				list.add(plugin.getValueEntry().getBooleanValueEntry(uuid, c.getInternName(), server, world).toString());
			}
			if(plugin.getValueEntry().hasValueEntry(uuid, c.getInternName(), ValueType.NUMBER, null, server, world))
			{
				list.add(String.valueOf(plugin.getValueEntry().getNumberValueEntry(uuid, c.getInternName(), server, world)));
			}
			if(plugin.getValueEntry().hasValueEntry(uuid, c.getInternName(), ValueType.TEXT, null, server, world))
			{
				list.add(plugin.getValueEntry().getTextValueEntry(uuid, c.getInternName(), server, world));
			}
			if(list.isEmpty())
			{
				if(i >= end)
				{
					break;
				}
				i++;
				continue;
			}
			map.put(c, list.toArray(new String[list.size()]));
			i++;
			if(i >= end)
			{
				break;
			}
		}
		boolean lastpage = rg.size()-9 < page * 10;
		if(map.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerHasNoValueEntry")
					.replace("%player%", othername)));
			return;
		}
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.List.Headline")
				.replace("%player%", othername)
				.replace("%page%", String.valueOf(page))));
		bc.add(bc1);
		int j = 0;
		for(Entry<ValueLable, String[]> cme : map.entrySet())
		{
			ValueLable vl = cme.getKey();
			ArrayList<BaseComponent> bc3 = new ArrayList<>();
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.List.ValueEntryDescriptionOne")
					.replace("%displayname%", vl.getDisplayName()),
					HoverEvent.Action.SHOW_TEXT, String.join("~!~", vl.getExplanation())));
			ArrayList<ValueEntry> vev = ValueEntry.convert(
					plugin.getMysqlHandler().getFullList(MysqlHandler.Type.VALUEENTRY, "`id` ASC",
					"`player_uuid` = ? AND `valuelable_name` = ?", uuid.toString(), vl.getInternName()));
			ArrayList<String> vlist = new ArrayList<>();
			vlist.add(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.List.BaseValue"));
			for(ValueEntry ve : vev)
			{
				String v = ve.getValue();
				StringBuilder sb = new StringBuilder();
				if(MatchPrimitiveDataTypes.isBoolean(v))
				{
					boolean boo = MatchPrimitiveDataTypes.getBoolean(v).booleanValue();
					sb.append(boo
							? "&r'"+plugin.getYamlHandler().getLang().getString("IsTrue")+"&r'"
							: "&r'"+plugin.getYamlHandler().getLang().getString("IsFalse")+"&r'");
				} else if(MatchPrimitiveDataTypes.isLong(v))
				{
					sb.append("&r'&e"+Long.parseLong(v)+"&r'");
				} else if(MatchPrimitiveDataTypes.isDouble(v))
				{
					sb.append("&r'&e"+Mathematic.round(Double.parseDouble(v), 2, RoundingMode.DOWN)+"&r'");
				}
				sb.append(" >> &r'"+ve.getDisplayReason()+"&r'");
				if(ve.getDuration() > 0)
				{
					long dur = ve.getDuration()-System.currentTimeMillis();
					sb.append("&r >> " + TimeHandler.getRepeatingTime(dur, "dd-HH:mm"));
				}
				vlist.add(sb.toString());
				j++;
			}
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.List.ValueEntryDescriptionTwo")
					.replace("%value%", String.valueOf(j)),
					HoverEvent.Action.SHOW_TEXT, String.join("~!~", vlist)));
			bc.add(bc3);
		}
		for(ArrayList<BaseComponent> b : bc)
		{
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(b);
			player.spigot().sendMessage(tc);
		}
		MAVECCmdExecutor.pastNextPage(player, page, lastpage, ac.getCommandString(), othername, type);
	}
}