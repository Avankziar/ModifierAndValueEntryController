package main.java.me.avankziar.mavec.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import main.java.me.avankziar.mavec.spigot.assistance.BackgroundTask;
import main.java.me.avankziar.mavec.spigot.cmd.MAVECCmdExecutor;
import main.java.me.avankziar.mavec.spigot.cmd.TabCompletion;
import main.java.me.avankziar.mavec.spigot.cmd.modifier.ARGModifier;
import main.java.me.avankziar.mavec.spigot.cmd.modifier.ARGModifierAdd;
import main.java.me.avankziar.mavec.spigot.cmd.modifier.ARGModifierList;
import main.java.me.avankziar.mavec.spigot.cmd.modifier.ARGModifierRegistered;
import main.java.me.avankziar.mavec.spigot.cmd.modifier.ARGModifierRemove;
import main.java.me.avankziar.mavec.spigot.cmd.valueentry.ARGValueEntry;
import main.java.me.avankziar.mavec.spigot.cmd.valueentry.ARGValueEntryAdd;
import main.java.me.avankziar.mavec.spigot.cmd.valueentry.ARGValueEntryList;
import main.java.me.avankziar.mavec.spigot.cmd.valueentry.ARGValueEntryRegistered;
import main.java.me.avankziar.mavec.spigot.cmd.valueentry.ARGValueEntryRemove;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.ArgumentModule;
import main.java.me.avankziar.mavec.spigot.cmdtree.BaseConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.CommandConstructor;
import main.java.me.avankziar.mavec.spigot.cmdtree.CommandExecuteType;
import main.java.me.avankziar.mavec.spigot.database.MysqlHandler;
import main.java.me.avankziar.mavec.spigot.database.MysqlSetup;
import main.java.me.avankziar.mavec.spigot.database.YamlHandler;
import main.java.me.avankziar.mavec.spigot.database.YamlManager;
import main.java.me.avankziar.mavec.spigot.ifh.ConditionQueryParserProvider;
import main.java.me.avankziar.mavec.spigot.ifh.ModifierProvider;
import main.java.me.avankziar.mavec.spigot.ifh.ValueEntryProvider;
import main.java.me.avankziar.mavec.spigot.listener.JoinQuitListener;
import main.java.me.avankziar.mavec.spigot.metrics.Metrics;
import main.java.me.avankziar.mavec.spigot.modifiervalueentry.Bypass;
import me.avankziar.ifh.spigot.administration.Administration;

public class MAVEC extends JavaPlugin
{
	public static Logger log;
	private static MAVEC plugin;
	public String pluginName = "ModifierAndValueEntryController";
	private YamlHandler yamlHandler;
	private YamlManager yamlManager;
	private MysqlSetup mysqlSetup;
	private MysqlHandler mysqlHandler;
	private BackgroundTask backgroundTask;
	
	private ArrayList<BaseConstructor> helpList = new ArrayList<>();
	private ArrayList<CommandConstructor> commandTree = new ArrayList<>();
	private LinkedHashMap<String, ArgumentModule> argumentMap = new LinkedHashMap<>();
	
	public static String infoCommand = "/";
	
	private Administration rootAConsumer;
	private ModifierProvider modifierProvider;
	private ValueEntryProvider valueEntryProvider;
	private ConditionQueryParserProvider conditonQueryParserProvider;
	
	public void onEnable()
	{
		plugin = this;
		log = getLogger();
		
		setupIFHAdministration();
		
		//https://patorjk.com/software/taag/#p=display&f=ANSI%20Shadow&t=MAVEC
		log.info(" ███╗   ███╗ █████╗ ██╗   ██╗███████╗ ██████╗ | API-Version: "+plugin.getDescription().getAPIVersion());
		log.info(" ████╗ ████║██╔══██╗██║   ██║██╔════╝██╔════╝ | Author: "+plugin.getDescription().getAuthors().toString());
		log.info(" ██╔████╔██║███████║██║   ██║█████╗  ██║      | Plugin Website: "+plugin.getDescription().getWebsite());
		log.info(" ██║╚██╔╝██║██╔══██║╚██╗ ██╔╝██╔══╝  ██║      | Depend Plugins: "+plugin.getDescription().getDepend().toString());
		log.info(" ██║ ╚═╝ ██║██║  ██║ ╚████╔╝ ███████╗╚██████╗ | SoftDepend Plugins: "+plugin.getDescription().getSoftDepend().toString());
		log.info(" ╚═╝     ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝ ╚═════╝ | LoadBefore: "+plugin.getDescription().getLoadBefore().toString());
		
		yamlHandler = new YamlHandler(plugin);
		
		String path = plugin.getYamlHandler().getConfig().getString("IFHAdministrationPath");
		boolean adm = plugin.getAdministration() != null 
				&& plugin.getYamlHandler().getConfig().getBoolean("useIFHAdministration")
				&& plugin.getAdministration().isMysqlPathActive(path);
		if(adm || yamlHandler.getConfig().getBoolean("Mysql.Status", false) == true)
		{
			mysqlHandler = new MysqlHandler(plugin);
			mysqlSetup = new MysqlSetup(plugin);
		} else
		{
			log.severe("MySQL is not set in the Plugin " + pluginName + "!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(plugin);
			return;
		}
		
		backgroundTask = new BackgroundTask(plugin);
		
		setupBypassPerm();
		setupCommandTree();
		setupListeners();
		setupIFHProvider();
		setupBstats();
	}
	
	public void onDisable()
	{
		Bukkit.getScheduler().cancelTasks(plugin);
		HandlerList.unregisterAll(plugin);
		log.info(pluginName + " is disabled!");
	}

	public static MAVEC getPlugin()
	{
		return plugin;
	}
	
	public YamlHandler getYamlHandler() 
	{
		return yamlHandler;
	}
	
	public YamlManager getYamlManager()
	{
		return yamlManager;
	}

	public void setYamlManager(YamlManager yamlManager)
	{
		this.yamlManager = yamlManager;
	}
	
	public MysqlSetup getMysqlSetup() 
	{
		return mysqlSetup;
	}
	
	public MysqlHandler getMysqlHandler()
	{
		return mysqlHandler;
	}
	
	public BackgroundTask getBackgroundTask()
	{
		return backgroundTask;
	}
	
	public String getServername()
	{
		return getPlugin().getAdministration() != null ? getPlugin().getAdministration().getSpigotServerName() 
				: getPlugin().getYamlHandler().getConfig().getString("ServerName");
	}
	
	private void setupCommandTree()
	{		
		infoCommand += plugin.getYamlHandler().getCommands().getString("mavec.Name");
		
		TabCompletion tab = new TabCompletion(plugin);
		
		ArgumentConstructor modifier_list = new ArgumentConstructor(
				CommandExecuteType.MAVEC_MODIFIER_LIST, "mavec_modifier_list", 1, 1, 4, true, null);
		new ARGModifierList(modifier_list);
		ArgumentConstructor modifier_add = new ArgumentConstructor(
				CommandExecuteType.MAVEC_MODIFIER_ADD, "mavec_modifier_add", 1, 8, 999, true, null);
		new ARGModifierAdd(modifier_add);
		ArgumentConstructor modifier_registered = new ArgumentConstructor(
				CommandExecuteType.MAVEC_MODIFIER_REGISTERED, "mavec_modifier_registered", 1, 1, 2, false, null);
		new ARGModifierRegistered(modifier_registered);
		ArgumentConstructor modifier_remove = new ArgumentConstructor(
				CommandExecuteType.MAVEC_MODIFIER_REMOVE, "mavec_modifier_remove", 1, 3, 4, true, null);
		new ARGModifierRemove(modifier_remove);
		ArgumentConstructor modifier = new ArgumentConstructor(
				CommandExecuteType.MAVEC_MODIFIER, "mavec_modifier", 0, 0, 0, true, null,
				modifier_list, modifier_add, modifier_registered, modifier_remove);
		new ARGModifier(modifier);
		
		ArgumentConstructor valueentry_list = new ArgumentConstructor(
				CommandExecuteType.MAVEC_VALUEENTRY_LIST, "mavec_valueentry_list", 1, 1, 4, true, null);
		new ARGValueEntryList(valueentry_list);
		ArgumentConstructor valueentry_add = new ArgumentConstructor(
				CommandExecuteType.MAVEC_VALUEENTRY_ADD, "mavec_valueentry_add", 1, 7, 999, true, null);
		new ARGValueEntryAdd(valueentry_add);
		ArgumentConstructor valueentry_registered = new ArgumentConstructor(
				CommandExecuteType.MAVEC_VALUEENTRY_REGISTERED, "mavec_valueentry_registered", 1, 1, 2, false, null);
		new ARGValueEntryRegistered(valueentry_registered);
		ArgumentConstructor valueentry_remove = new ArgumentConstructor(
				CommandExecuteType.MAVEC_VALUEENTRY_REMOVE, "mavec_valueentry_remove", 1, 3, 4, true, null);
		new ARGValueEntryRemove(valueentry_remove);
		ArgumentConstructor valueentry = new ArgumentConstructor(
				CommandExecuteType.MAVEC_VALUEENTRY, "mavec_valueentry", 0, 0, 0, true, null,
				valueentry_list, valueentry_add, valueentry_registered, valueentry_remove);
		new ARGValueEntry(valueentry);
		
		CommandConstructor mavec = new CommandConstructor(CommandExecuteType.MAVEC, "mavec", true,
				modifier, valueentry);
		registerCommand(mavec.getPath(), mavec.getName());
		getCommand(mavec.getName()).setExecutor(new MAVECCmdExecutor(plugin, mavec));
		getCommand(mavec.getName()).setTabCompleter(tab);
	}
	
	public void setupBypassPerm()
	{
		String path = "Bypass.";
		for(Bypass.Permission bypass : new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class)))
		{
			Bypass.set(bypass, yamlHandler.getCommands().getString(path+bypass.toString()));
		}
	}
	
	public ArrayList<BaseConstructor> getCommandHelpList()
	{
		return helpList;
	}
	
	public void addingCommandHelps(BaseConstructor... objects)
	{
		for(BaseConstructor bc : objects)
		{
			helpList.add(bc);
		}
	}
	
	public ArrayList<CommandConstructor> getCommandTree()
	{
		return commandTree;
	}
	
	public CommandConstructor getCommandFromPath(String commandpath)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getPath().equalsIgnoreCase(commandpath))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public CommandConstructor getCommandFromCommandString(String command)
	{
		CommandConstructor cc = null;
		for(CommandConstructor coco : getCommandTree())
		{
			if(coco.getName().equalsIgnoreCase(command))
			{
				cc = coco;
				break;
			}
		}
		return cc;
	}
	
	public void registerCommand(String... aliases) 
	{
		PluginCommand command = getCommand(aliases[0], plugin);
	 
		command.setAliases(Arrays.asList(aliases));
		getCommandMap().register(plugin.getDescription().getName(), command);
	}
	 
	private static PluginCommand getCommand(String name, MAVEC plugin) 
	{
		PluginCommand command = null;
	 
		try 
		{
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
	 
			command = c.newInstance(name, plugin);
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		} catch (InstantiationException e) 
		{
			e.printStackTrace();
		} catch (InvocationTargetException e) 
		
		{
			e.printStackTrace();
		} catch (NoSuchMethodException e) 
		{
			e.printStackTrace();
		}
	 
		return command;
	}
	 
	private static CommandMap getCommandMap() 
	{
		CommandMap commandMap = null;
	 
		try {
			if (Bukkit.getPluginManager() instanceof SimplePluginManager) 
			{
				Field f = SimplePluginManager.class.getDeclaredField("commandMap");
				f.setAccessible(true);
	 
				commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
			}
		} catch (NoSuchFieldException e) 
		{
			e.printStackTrace();
		} catch (SecurityException e) 
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
	 
		return commandMap;
	}
	
	public LinkedHashMap<String, ArgumentModule> getArgumentMap()
	{
		return argumentMap;
	}
	
	public void setupListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JoinQuitListener(plugin), plugin);
	}
	
	private boolean setupIFHProvider()
	{
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		modifierProvider = new ModifierProvider(plugin);
    	plugin.getServer().getServicesManager().register(
        me.avankziar.ifh.general.modifier.Modifier.class,
        modifierProvider,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> Modifier.class is provided!");
    	if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		valueEntryProvider = new ValueEntryProvider();
    	plugin.getServer().getServicesManager().register(
        me.avankziar.ifh.general.valueentry.ValueEntry.class,
        valueEntryProvider,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> ValueEntry.class is provided!");
    	for(BaseConstructor bc : getCommandHelpList())
		{
			if(!bc.isPutUpCmdPermToValueEntrySystem())
			{
				continue;
			}
			if(getValueEntry().isRegistered(bc.getValueEntryPath()))
			{
				continue;
			}
			getValueEntry().register(
					bc.getValueEntryPath(),
					bc.getValueEntryDisplayName(),
					bc.getValueEntryExplanation());
		}
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			if(getValueEntry().isRegistered(ept.getValueLable()))
			{
				continue;
			}
			List<String> lar = plugin.getYamlHandler().getMVELang().getStringList(ept.toString()+".Explanation");
			getValueEntry().register(
					ept.getValueLable(),
					plugin.getYamlHandler().getMVELang().getString(ept.toString()+".Displayname", ept.toString()),
					lar.toArray(new String[lar.size()]));
		}
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
			log.severe("IFH is not set in the Plugin " + pluginName + "! Disable plugin!");
			Bukkit.getPluginManager().getPlugin(pluginName).getPluginLoader().disablePlugin(this);
	    	return false;
	    }
		conditonQueryParserProvider = new ConditionQueryParserProvider();
    	plugin.getServer().getServicesManager().register(
        me.avankziar.ifh.general.conditionqueryparser.ConditionQueryParser.class,
        conditonQueryParserProvider,
        this,
        ServicePriority.Normal);
    	log.info(pluginName + " detected InterfaceHub >>> ConditionQueryParser.class is provided!");
		return true;
	}
	
	public ModifierProvider getModifier()
	{
		return modifierProvider;
	}

	private void setupIFHAdministration()
	{ 
		if(!plugin.getServer().getPluginManager().isPluginEnabled("InterfaceHub")) 
	    {
	    	return;
	    }
		RegisteredServiceProvider<me.avankziar.ifh.spigot.administration.Administration> rsp = 
                getServer().getServicesManager().getRegistration(Administration.class);
		if (rsp == null) 
		{
		   return;
		}
		rootAConsumer = rsp.getProvider();
		log.info(pluginName + " detected InterfaceHub >>> Administration.class is consumed!");
	}
	
	public Administration getAdministration()
	{
		return rootAConsumer;
	}
	
	public ValueEntryProvider getValueEntry()
	{
		return valueEntryProvider;
	}
	
	public ConditionQueryParserProvider getConditionQueryParserProvider()
	{
		return conditonQueryParserProvider;
	}
	
	public void setupBstats()
	{
		int pluginId = 18318; //Bungee 18319
        new Metrics(this, pluginId);
	}
}