package main.java.me.avankziar.mavec.spigot.cmd.modifier;

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
import main.java.me.avankziar.mavec.spigot.objects.Modification;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ARGModifierRegistered extends ArgumentModule
{
	private MAVEC plugin;
	private ArgumentConstructor ac = null;
	
	public ARGModifierRegistered(ArgumentConstructor argumentConstructor)
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
		if(args.length >= 3 && MatchApi.isInteger(args[2]))
		{
			page = Integer.parseInt(args[1]);
		}
		ArrayList<Modification> rg = plugin.getModifier().getRegisteredMod();
		ArrayList<Modification> map = new ArrayList<>();
		int end = page * 10 + 9;
		for(int i = page * 10; i < rg.size(); i++)
		{
			Modification bm = rg.get(i);
			map.add(bm);
			if(i >= end)
			{
				break;
			}
		}
		boolean lastpage = rg.size()-9 < page * 10;
		if(map.isEmpty())
		{
			player.sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("PlayerHasNoModifier")));
			return;
		}
		ArrayList<ArrayList<BaseComponent>> bc = new ArrayList<>();
		ArrayList<BaseComponent> bc1 = new ArrayList<>();
		bc1.add(ChatApi.tctl(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Registered.Headline")
				.replace("%page%", String.valueOf(page))
				.replace("%amount%", String.valueOf(rg.size()))));
		bc.add(bc1);
		for(Modification bm : map)
		{
			int permcount = plugin.getMysqlHandler().getCount(MysqlHandler.Type.MODIFIER,
					"`modification_name` = ? AND `duration` < 0", bm.getInternName());
			int tempcount = plugin.getMysqlHandler().getCount(MysqlHandler.Type.MODIFIER,
					"`modification_name` = ? AND `duration` > 0", bm.getInternName());
			ArrayList<BaseComponent> bc3 = new ArrayList<>();
			bc3.add(ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Registered.Add")
					.replace("%cmd%", CommandSuggest.get(CommandExecuteType.MAVEC_MODIFIER_ADD).strip().replace(" ", "+"))
					.replace("%mod%", bm.getInternName())));
			bc3.add(ChatApi.generateTextComponent(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Registered.Remove")
					.replace("%cmd%", CommandSuggest.get(CommandExecuteType.MAVEC_MODIFIER_REMOVE).strip().replace(" ", "+"))
					.replace("%mod%", bm.getInternName())));			
			bc3.add(ChatApi.hoverEvent(plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Registered.ModifierDescriptionOne")
					.replace("%displayname%", bm.getDisplayName()),
							HoverEvent.Action.SHOW_TEXT, 
							plugin.getYamlHandler().getLang().getString("Cmd.Modifier.Registered.ModifierDescriptionTwo")
							.replace("%mod%", bm.getInternName())
							.replace("%permcount%", String.valueOf(permcount))
							.replace("%tempcount%", String.valueOf(tempcount))
							.replace("%explanation%", String.join("~!~", bm.getExplanation()))
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