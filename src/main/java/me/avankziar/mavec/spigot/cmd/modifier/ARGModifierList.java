package main.java.me.avankziar.mavec.spigot.cmd.modifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
import main.java.me.avankziar.mavec.spigot.objects.Modification;
import main.java.me.avankziar.mavec.spigot.objects.Modifier;
import me.avankziar.ifh.general.modifier.ModifierType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGModifierList extends ArgumentModule
{
	private MAVEC plugin;
	private HashMap<String, Long> cooldown = new HashMap<>();
	private ArgumentConstructor ac = null;
	
	public ARGModifierList(ArgumentConstructor argumentConstructor)
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
		ArrayList<Modification> rg = plugin.getModifier().getRegisteredMod();
		LinkedHashMap<Modification, Double> map = new LinkedHashMap<>();
		int end = page * 10 + 9;
		int i = page * 10;
		while(i < rg.size())
		{
			Modification modi = rg.get(i);
			if(!plugin.getModifier().hasModifier(uuid, modi.getInternName(), null, server, world))
			{
				i++;
				continue;
			}
			map.put(modi, plugin.getModifier().getResult(uuid, 1.0, modi.getInternName(),
					server, world));
			i++;
			if(i >= end)
			{
				break;
			}
		}
		boolean lastpage = rg.size()-9 < page * 10;
		if(map.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerHasNoModifier")
					.replace("%player%", othername)));
			return;
		}
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.Headline")
				.replace("%player%", othername)
				.replace("%page%", String.valueOf(page))));
		bc.add(bc1);
		ArrayList<BaseComponent> bc2 = new ArrayList<>();
		bc2.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.LineTwo")));
		bc.add(bc2);
		ArrayList<BaseComponent> bc4 = new ArrayList<>();
		bc4.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.LineThree")));
		bc.add(bc4);
		ArrayList<BaseComponent> bc5 = new ArrayList<>();
		bc5.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.LineFour")));
		bc.add(bc5);
		for(Entry<Modification, Double> mode : map.entrySet())
		{
			Modification mod = mode.getKey();
			final double d = plugin.getModifier().getLastBaseValue(uuid, mod.getInternName());
			final double sum = plugin.getModifier().getSumValue(uuid, mod.getInternName(), server, world);
			final double mul = plugin.getModifier().getMulltiplyValue(uuid, mod.getInternName(), server, world);
			final double dd = (d + sum) * mul; 
			ArrayList<BaseComponent> bc3 = new ArrayList<>();
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.ModifierDescriptionOne")
					.replace("%displayname%", mod.getDisplayName()),
					HoverEvent.Action.SHOW_TEXT, String.join("~!~", mod.getExplanation())));
			ArrayList<Modifier> modiv = Modifier.convert(
					plugin.getMysqlHandler().getFullList(MysqlHandler.Type.MODIFIER, "`id` ASC",
					"`player_uuid` = ? AND `modification_name` = ?", uuid.toString(), mod.getInternName()));
			ArrayList<String> vlist = new ArrayList<>();
			vlist.add(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.BaseValue")
					.replace("%value%", String.valueOf(d)));
			for(Modifier modi : modiv)
			{
				StringBuilder sb = new StringBuilder();
				if(modi.getType() == ModifierType.ADDITION)
				{
					if(modi.getValue() >= 0)
					{
						sb.append("&#60ec4b(+) &r");
					} else
					{
						sb.append("&#eb2424(+) &r");
					}
				} else
				{
					if(modi.getValue() >= 1.0)
					{
						sb.append("&#60ec4b(x) &r");
					} else
					{
						sb.append("&#eb2424(x) &r");
					}
				}
				sb.append("'"+modi.getValue()+"'");
				sb.append(" >> '"+modi.getDisplayReason()+"'");
				if(modi.getDuration() > 0)
				{
					long dur = modi.getDuration()-System.currentTimeMillis();
					sb.append("&r >> " + TimeHandler.getRepeatingTime(dur, "dd-HH:mm"));
				}
				vlist.add(sb.toString());
			}
			vlist.add(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.EndValue")
					.replace("%start%", String.valueOf(d))
					.replace("%value%", String.valueOf(dd))
					.replace("%sum%", String.valueOf(sum))
					.replace("%mul%", String.valueOf(mul)));
			String value = String.valueOf(dd);
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.List.ModifierDescriptionTwo")
					.replace("%value%", value),
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