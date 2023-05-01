package main.java.me.avankziar.mavec.spigot.cmd.valueentry;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.java.me.avankziar.mavec.general.ChatApi;
import main.java.me.avankziar.mavec.spigot.MAVEC;
import main.java.me.avankziar.mavec.spigot.assistance.MatchApi;
import main.java.me.avankziar.mavec.spigot.cmd.MAVECCmdExecutor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mavec.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mavec.spigot.cmdtree.CommandSuggest;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;
import main.java.me.avankziar.mavec.spigot.objects.ValueLable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGValueEntryRegistered extends ArgumentModule
{
	private MAVEC plugin;
	private ArgumentConstructor ac = null;
	
	public ARGValueEntryRegistered(ArgumentConstructor argumentConstructor)
	{
		super(argumentConstructor);
		this.plugin = MAVEC.getPlugin();
		this.ac = argumentConstructor;
	}

	@Override
	public void run(CommandSender sender, String[] args) throws IOException
	{
		Player player = (Player) sender;
		int page = 0;
		if(args.length >= 2 && MatchApi.isInteger(args[1]))
		{
			page = Integer.parseInt(args[1]);
		}
		ArrayList<ValueLable> rg = plugin.getValueEntry().getRegisteredVL();
		ArrayList<ValueLable> map = new ArrayList<>();
		int end = page * 10 + 9;
		for(int i = page * 10; i < rg.size(); i++)
		{
			ValueLable bm = rg.get(i);
			map.add(bm);
			if(i >= end)
			{
				break;
			}
		}
		boolean lastpage = rg.size()-9 < page * 10;
		if(map.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerHasNoValueEntry")));
			return;
		}
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Registered.Headline")
				.replace("%page%", String.valueOf(page))
				.replace("%amount%", String.valueOf(rg.size()))));
		bc.add(bc1);
		for(ValueLable c : map)
		{
			int permcount = plugin.getMysqlHandler().getCount(MysqlHandler.Type.VALUEENTRY,
					"`valuelable_name` = ? AND `duration` < 0", c.getInternName());
			int tempcount = plugin.getMysqlHandler().getCount(MysqlHandler.Type.VALUEENTRY,
					"`valuelable_name` = ? AND `duration` > 0", c.getInternName());
			ArrayList<BaseComponent> bc3 = new ArrayList<>();
			bc3.add(ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Registered.Add")
					.replace("%cmd%", CommandSuggest.get(CommandExecuteType.MAVEC_VALUEENTRY_ADD).strip().replace(" ", "+"))
					.replace("%ve%", c.getInternName())));
			bc3.add(ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Registered.Remove")
					.replace("%cmd%", CommandSuggest.get(CommandExecuteType.MAVEC_VALUEENTRY_REMOVE).strip().replace(" ", "+"))
					.replace("%ve%", c.getInternName())));			
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Registered.ValueEntryDescriptionOne")
					.replace("%displayname%", c.getDisplayName()),
							HoverEvent.Action.SHOW_TEXT, 
							plugin.getYamlHandler().getLang().getString("Cmd.ValueEntry.Registered.ValueEntryDescriptionTwo")
							.replace("%ve%", c.getInternName())
							.replace("%permcount%", String.valueOf(permcount))
							.replace("%tempcount%", String.valueOf(tempcount))
							.replace("%explanation%", String.join("~!~", c.getExplanation()))
							));
			bc.add(bc3);
		}
		for(ArrayList<BaseComponent> b : bc)
		{
			TextComponent tc = ChatApi.tc("");
			tc.setExtra(b);
			player.spigot().sendMessage(tc);
		}
		MAVECCmdExecutor.pastNextPage(player, page, lastpage, ac.getCommandString().strip());
	}
}