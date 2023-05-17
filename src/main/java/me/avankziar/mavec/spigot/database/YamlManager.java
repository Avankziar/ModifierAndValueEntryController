package main.java.me.avankziar.mavec.spigot.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import main.java.me.avankziar.mavec.spigot.database.Language.ISO639_2B;
import main.java.me.avankziar.mavec.spigot.modifiervalueentry.Bypass;

public class YamlManager
{
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configSpigotKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> cbmlanguageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager()
	{
		initConfig();
		initCommands();
		initLanguage();
		initModifierValueEntryLanguage();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigSpigotKey()
	{
		return configSpigotKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getModifierValueEntryLanguageKey()
	{
		return cbmlanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	/*
	 * The main methode to set all paths in the yamls.
	 */
	public void setFileInput(YamlConfiguration yml, LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	public void initConfig() //INFO:Config
	{
		configSpigotKeys.put("useIFHAdministration"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("IFHAdministrationPath"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mavec"}));
		configSpigotKeys.put("ServerName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"hub"}));
		configSpigotKeys.put("Mysql.Status"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.Host"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"127.0.0.1"}));
		configSpigotKeys.put("Mysql.Port"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				3306}));
		configSpigotKeys.put("Mysql.DatabaseName"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"mydatabase"}));
		configSpigotKeys.put("Mysql.SSLEnabled"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.AutoReconnect"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				true}));
		configSpigotKeys.put("Mysql.VerifyServerCertificate"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("Mysql.User"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"admin"}));
		configSpigotKeys.put("Mysql.Password"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				"not_0123456789"}));
		
		configSpigotKeys.put("ValueEntry.OverrulePermission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				false}));
		configSpigotKeys.put("DeleteOldDataTask.RunInSeconds"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				60}));
	}
	
	//INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "mavec";
		commandsInput(path, "mavec", "mavec.cmd.mavec", 
				"/mavec [page]", "/mavec ", false,
				"&c/mavec [Seite] &f| Infoseite für alle Befehle.",
				"&c/mavec [page] &f| Info page for all commands.",
				"&bBefehlsrecht für &f/mavec",
				"&bCommandright for &f/mavec",
				"&eInfoseite für alle Befehle.",
				"&eInfo page for all commands.");
		String perm = "mavec.cmd";
		argumentInput(path+"_modifier", "modifier", perm,
				"/mavec modifier", "/mavec modifier ", false,
				"&c/mavec modifier &f| Zwischenbefehl",
				"&c/mavec modifier &f| Intermediate command",
				"&bBefehlsrecht für &f/mavec modifier",
				"&bCommandright for &f/mavec modifier",
				"&eEin Zwischenbefehl.",
				"&eA intermediate command.");
		perm = "mavec.cmd.modifier";
		argumentInput(path+"_modifier_add", "add", perm,
				"/mavec modifier add <modification> <player> <global/server:servername/world:servername:worldname> <value> <ADDITION/MULTIPLICATION> <0/dd-HH:mm> <internreason> <reason...>", "/mavec modifier add ", false,
				"&c/mavec modifier add <Modifikation> <Spieler> <global/server:servername/world:servername:weltname> <Wert> <ADDITION/MULTIPLICATION> <0/dd-HH:mm> <interner Grung> <Grund...> &f| Fügt dem angegeben Spieler einen Modifikator hinzu.",
				"&c/mavec modifier add <modification> <player> <global/server:servername/world:servername:worldname> <value> <ADDITION/MULTIPLICATION> <0/dd-HH:mm> <internreason> <reason...> &f| Adds a modifier to the specified player.",
				"&bBefehlsrecht für &f/mavec modifier add",
				"&bCommandright for &f/mavec modifier add",
				"&eFügt dem angegeben Spieler einen Modifikator hinzu.",
				"&eAdds a modifier to the specified player.");
		argumentInput(path+"_modifier_list", "list", perm,
				"/mavec modifier list [page] [playername] [global/server/world]", "/mavec modifier list ", false,
				"&c/mavec modifier list [Seite] [Spielername] [global/server/world] &f| Listet alle aktiven Modificatoren des Spielers mit Hovererklärung auf.",
				"&c/mavec modifier list [page] [playername] [global/server/world] &f| Lists all active modifiers of the player with hoverexplanation.",
				"&bBefehlsrecht für &f/mavec modifier list",
				"&bCommandright for &f/mavec modifier list",
				"&eListet alle aktiven Modifikatoren des Spielers mit Hovererklärung auf.",
				"&eLists all active modifiers of the player with hoverexplanation.");
		argumentInput(path+"_modifier_registered", "registered", perm,
				"/mavec modifier registered [page]", "/mavec modifier registered ", false,
				"&c/mavec modifier registered [Seite] &f| Listet alle Pluginbasierende registrierte Modifikationen auf.",
				"&c/mavec modifier registered [page] &f| Lists all plugin based registered modifications.",
				"&bBefehlsrecht für &f/mavec registered",
				"&bCommandright for &f/mavec registered",
				"&eListet alle Pluginbasierende registrierte Modifikationen auf.",
				"&eLists all plugin based registered modifications.");
		argumentInput(path+"_modifier_remove", "remove", perm,
				"/mavec modifier remove <modification> <player> <reason...>", "/mavec modifier remove ", false,
				"&c/mavec modifier remove <Modifikation> <Spieler> <Grund...> &f| Entfernt dem angegeben Spieler einen Modifikator.",
				"&c/mavec modifier remove <modification> <player> <reason...> &f| Remove a modifier to the specified player.",
				"&bBefehlsrecht für &f/mavec modifier remove",
				"&bCommandright for &f/mavec modifier remove",
				"&eEntfernt dem angegeben Spieler einen Modifikator.",
				"&eRemove a modifier to the specified player.");
		perm = "mavec.cmd";
		argumentInput(path+"_valueentry", "valueentry", perm,
				"/mavec valueentry", "/mavec valueentry ", false,
				"&c/mavec valueentry &f| Zwischenbefehl",
				"&c/mavec valueentry &f| Intermediate command",
				"&bBefehlsrecht für &f/mavec valueentry",
				"&bCommandright for &f/mavec valueentry",
				"&eEin Zwischenbefehl.",
				"&eA intermediate command.");
		perm = "mavec.cmd.valueentry";		
		argumentInput(path+"_valueentry_add", "add", perm,
				"/mavec valueentry add <valueentry> <player> <global/server:servername/world:servername:worldname> <value> <0/dd-HH:mm> <internreason> <reason...>", "/mavec valueentry add ", false,
				"&c/mavec valueentry add <Valueentry> <Spieler> <global/server:servername/world:servername:weltname> <Wert> <0/dd-HH:mm> <interner Grung> <Grund...> &f| Fügt dem angegeben Spieler eine ValueEntry hinzu.",
				"&c/mavec valueentry add <valueentry> <player> <global/server:servername/world:servername:worldname> <value> <0/dd-HH:mm> <internreason> <reason...> &f| Adds a valueentry to the specified player.",
				"&bBefehlsrecht für &f/mavec valueentry add",
				"&bCommandright for &f/mavec valueentry add",
				"&eFügt dem angegeben Spieler eine ValueEntry hinzu.",
				"&eAdds a valueentry to the specified player.");
		argumentInput(path+"_valueentry_list", "list", perm,
				"/mavec valueentry list [page] [playername] [global/server/world]", "/mavec valueentry list ", false,
				"&c/mavec valueentry list [Seite] [Spielername] [global/server/world] &f| Listet alle aktive ValueEntry des Spielers mit Hovererklärung auf.",
				"&c/mavec valueentry list [page] [playername] [global/server/world] &f| Lists all active valueentrx of the player with hoverexplanation.",
				"&bBefehlsrecht für &f/mavec valueentry list",
				"&bCommandright for &f/mavec valueentry list",
				"&eListet alle aktive ValueEntry des Spielers mit Hovererklärung auf.",
				"&eLists all active valueentry of the player with hoverexplanation.");
		argumentInput(path+"_valueentry_registered", "registered", perm,
				"/mavec valueentry registered [page]", "/mavec valueentry registered ", false,
				"&c/mavec valueentry registered [Seite] &f| Listet alle Pluginbasierende registrierte ValueEntry auf.",
				"&c/mavec valueentry registered [page] &f| Lists all plugin based registered valueentry.",
				"&bBefehlsrecht für &f/mavec valueentry registered",
				"&bCommandright for &f/mavec valueentry registered",
				"&eListet alle Pluginbasierende registrierte Condition auf.",
				"&eLists all plugin based registered condition.");
		argumentInput(path+"_valueentry_remove", "remove", perm,
				"/mavec valueentry remove <valueentry> <player> <reason...>", "/mavec valueentry remove ", false,
				"&c/mavec valueentry remove <Valueentry> <Spieler> <Grund...> &f| Entfernt dem angegeben Spieler eine ValueEntry.",
				"&c/mavec valueentry remove <valueentry> <player> <reason...> &f| Remove a valueentry to the specified player.",
				"&bBefehlsrecht für &f/mavec remove",
				"&bCommandright for &f/mavec remove",
				"&eEntfernt dem angegeben Spieler eine Condition.",
				"&eRemove a condition to the specified player.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"mavec."+ept.toString().toLowerCase().replace("_", ".")}));
		}
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToValueEntrySystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".ValueEntry.PutUpCommandPerm"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToValueEntrySystem}));
		commandsKeys.put(path+".ValueEntry.Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".ValueEntry.Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToValueEntrySystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".ValueEntry.PutUpCommandPerm"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToValueEntrySystem}));
		commandsKeys.put(path+".ValueEntry.Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".ValueEntry.Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDeine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"&cYour input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu hast dafür keine Rechte!",
						"&cYou dont not have the rights!"}));
		languageKeys.put("NoPlayerExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler existiert nicht!",
						"&cThe player does not exist!"}));
		languageKeys.put("NoNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine ganze Zahl sein.",
						"&cThe argument &f%value% &must be an integer."}));
		languageKeys.put("NoDouble",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine Gleitpunktzahl sein!",
						"&cThe argument &f%value% &must be a floating point number!"}));
		languageKeys.put("IsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDas Argument &f%value% &cmuss eine positive Zahl sein!",
						"&cThe argument &f%value% &must be a positive number!"}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eKlick mich!",
						"&eClick me!"}));
		languageKeys.put("Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e=====&7[&6ModifierAndValueEntryController&7]&e=====",
						"&e=====&7[&6ModifierAndValueEntryController&7]&e====="}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e&nnächste Seite &e==>",
						"&e&nnext page &e==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e<== &nvorherige Seite",
						"&e<== &nprevious page"}));
		languageKeys.put("IsTrue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a✔",
						"&a✔"}));
		languageKeys.put("IsFalse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c✖",
						"&c✖"}));
		languageKeys.put("PlayerCmdCooldown", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDu bist noch im Befehls-Cooldown! Bitte warte etwas!",
						"&cYou are still in the command cooldown! Please wait a little!"}));
		languageKeys.put("PlayerHasNoModifier", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler &f%player% &chat keine Modifikator!",
						"&cThe player &f%player% &chas no modifiers!"}));
		languageKeys.put("PlayerHasNoValueEntry", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Spieler &f%player% &chat keine Valueentry!",
						"&cThe player &f%player% &chas no valueentry!"}));
		languageKeys.put("Cmd.Modifier.OtherCmd",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cBitte nutze den Befehl, mit einem weiteren Argument aus der Tabliste!",
						"&cPlease use the command, with another argument from the tab list!"}));
		languageKeys.put("Cmd.Modifier.Add.IsNotRegistered", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer Modifikator ist nicht registriert!",
						"&cThe Modifikator is not registered!"}));
		languageKeys.put("Cmd.Modifier.Add.AddedPermanent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat permanent den Modifikator &f%mod% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %formula% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas permanently received the modifier &f%mod% &ewith the value &f%value% &eand the following values: &f%type% | %formula% | %internreason% | %reason%"}));
		languageKeys.put("Cmd.Modifier.Add.AddedTemporary", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat den Modifikator &f%mod% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %formula% | %duration% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas received the modifier &f%mod% &ewith the value &f%value% &eand the following values: &f%type% | %formula% | %duration% | %internreason% | %reason%"}));
		languageKeys.put("Cmd.Modifier.Add.AddedTemporaryExtends", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat den Modifikator &f%mod% &emit dem Wert &f%value% &eund den folgenden Werten verlängert: &f%type% | %formula% | %duration% (verlängert um %dura%) | %internreason% | %reason%",
						"&eThe player &f%player% &ehas extends the modifier &f%mod% &ewith the value &f%value% &eand the following values: &f%type% | %formula% | %duration% (extended by %dura%) | %internreason% | %reason%"}));
		languageKeys.put("Cmd.Modifier.List.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fModifikatoren &6von &c%player%&f, Seite %page%&e===",
						"&e===&fModifiers &6from &c%player%&f, page %page%&e==="}));
		languageKeys.put("Cmd.Modifier.List.LineTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7Die Werte, die zuletzt bekannt werden, werden so genommen. Alle anderen werden mit Basiswert von 1.0 berechnet!",
						"&7The values that become known last are taken so. All others are calculated with a base value of 1.0!"}));
		languageKeys.put("Cmd.Modifier.List.LineThree", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&7Es wird berechnet wie folgt:",
						"&7It is calculated as follows:"}));
		languageKeys.put("Cmd.Modifier.List.LineFour", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&f(Basiswert + alle additiven Werte) * alle multiplikativen Werte",
						"&f(base value + all additive values) * all multiplicative values"}));
		languageKeys.put("Cmd.Modifier.List.ModifierDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname%&r: ",
						"%displayname%&r: "}));
		languageKeys.put("Cmd.Modifier.List.ModifierDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% ",
						"%value% "}));
		languageKeys.put("Cmd.Modifier.List.BaseValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&#fc9303Basiswert: &r%value%",
						"&#fc9303Basevalue: &r%value%"}));
		languageKeys.put("Cmd.Modifier.List.EndValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&#d66400Endwert: &r%value% = (%start% + %sum%) * %mul%",
						"&#d66400Endvalue: &r%value% = (%start% + %sum%) * %mul%"}));
		languageKeys.put("Cmd.Modifier.Registered.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fRegistrierte Modifikationen, Seite %page%, GesamtAnzahl: %amount%&e===",
						"&e===&fRegistered modifications, page %page%, totalamount: %amount%&e==="}));
		languageKeys.put("Cmd.Modifier.Registered.ModifierDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname% ",
						"%displayname% "}));
		languageKeys.put("Cmd.Modifier.Registered.ModifierDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bModifikator Name: &f%mod%~!~&9Anzahl permanente Modifikatoren aller Spieler: &f%permcount%~!~&dAnzahl temporäre Modifikatoren aller Spieler: &f%tempcount%~!~&7Erklärung:~!~&f%explanation%",
						"&bModifier Name: &f%mod%~!~&9Number of permanent modifiers of all players: &f%permcount%~!~&dNumber of temporary modifiers of all players: &f%tempcount%~!~&7Explanation:~!~&f%explanation%"}));
		languageKeys.put("Cmd.Modifier.Registered.Add", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%mod%+<Spieler>+<global/server:servername/world:servername:worldname>+<Wert>+<ADDITION/MULTIPLICATION>+<0/dd-HH:mm>+<Interner+Grund>+<Grund...>~hover@SHOW_TEXT@&eKlicke+hier+zum+&ahinzufügen+&eeines+Modifikators+für+einen+Spieler!",
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%mod%+<player>+<global/server:servername/world:servername:worldname>+<value>+<ADDITION/MULTIPLICATION>+<0/dd-HH:mm>+<intern+reason>+<reason...>~hover@SHOW_TEXT@&eClick+here+to+&aadd+&ea+modifier+for+a+player!"}));
		languageKeys.put("Cmd.Modifier.Registered.Remove", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%mod%+<Spieler>+<Grund...>~hover@SHOW_TEXT@&eKlicke+hier+zum+&centfernen+&eeines+Modifikators+für+einen+Spieler!",
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%mod%+<player>+<reason...>~hover@SHOW_TEXT@&eClick+here+to+&cremove+&ea+modifier+for+a+player!"}));
		languageKeys.put("Cmd.Modifier.Remove.Remove", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat die &f%count% &eModifikator &f%mod% &emit dem Grund &f%reason% &cverloren!",
						"&eThe player &f%player% &ehas &clost &ethe &f%count% &emodificator &f%mod% &ewith the reason &f%reason%&e!"}));
		languageKeys.put("Cmd.ValueEntry.Add.IsNotRegistered", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDie Valueentry ist nicht registriert!",
						"&cThe valueentry is not registered!"}));
		languageKeys.put("Cmd.ValueEntry.Add.ValueTypeNotCorrect", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&cDer ValueType &f%value% &cist nicht korrekt! Möglich ist nur: BOOLEAN, NUMBER, TEXT!",
						"&cThe ValueType &f%value% &cis not correct! Possible is only: BOOLEAN, NUMBER, TEXT!"}));
		languageKeys.put("Cmd.ValueEntry.Add.AddedPermanent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat permanent die Valueentry &f%ve% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %dimension% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas permanently received the valueentry &f%ve% &ewith the value &f%value% &eand the following values: &f%type% | %dimension% | %internreason% | %reason%"}));
		languageKeys.put("Cmd.ValueEntry.Add.AddedTemporary", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat die Valueentry &f%ve% &emit dem Wert &f%value% &eund den folgenden Werten erhalten: &f%type% | %dimension% | %duration% | %internreason% | %reason%",
						"&eThe player &f%player% &ehas received the valueentry &f%ve% &ewith the value &f%value% &eand the following values: &f%type%| %dimension% | %duration% | %internreason% | %reason%"}));
		languageKeys.put("Cmd.ValueEntry.List.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fValueentry &6von &c%player%&f, Seite %page%&e===",
						"&e===&fValueentry &6from &c%player%&f, page %page%&e==="}));
		languageKeys.put("Cmd.ValueEntry.List.ValueEntryDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname%&r: ",
						"%displayname%&r: "}));
		languageKeys.put("Cmd.ValueEntry.List.ValueEntryDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%value% Einträge",
						"%value% entrys"}));
		languageKeys.put("Cmd.ValueEntry.List.BaseValue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&#fc9303Wert: &r",
						"&#fc9303Value: &r"}));
		languageKeys.put("Cmd.ValueEntry.Registered.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&e===&fRegistrierte Valueentry, Seite %page%, GesamtAnzahl: %amount%&e===",
						"&e===&fRegistered valueentry, page %page%, totalamount: %amount%&e==="}));
		languageKeys.put("Cmd.ValueEntry.Registered.ValueEntryDescriptionOne", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%displayname% ",
						"%displayname% "}));
		languageKeys.put("Cmd.ValueEntry.Registered.ValueEntryDescriptionTwo", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&bValueentry Name: &f%ve%~!~&9Anzahl permanente Valueentry aller Spieler: &f%permcount%~!~&dAnzahl temporäre Valueentry aller Spieler: &f%tempcount%~!~&7Erklärung:~!~&f%explanation%",
						"&bValueentry Name: &f%ve%~!~&9Number of permanent valueentry of all players: &f%permcount%~!~&dNumber of temporary valueentry of all players: &f%tempcount%~!~&7Explanation:~!~&f%explanation%"}));
		languageKeys.put("Cmd.ValueEntry.Registered.Add", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%ve%+<Spieler>+<BOOLEAN/NUMBER/TEXT>+<global/server:servername/world:servername:worldname>+<Wert>+<0/dd-HH:mm>+<Intern+Grund>+<Grund...>~hover@SHOW_TEXT@&eKlicke+hier+zum+&ahinzufügen+&eeiner+Valueentry+für+einen+Spieler!",
						"&a☑~click@SUGGEST_COMMAND@%cmd%+%ve%+<player>+<BOOLEAN/NUMBER/TEXT>+<global/server:servername/world:servername:worldname>+<value>+<0/dd-HH:mm>+<intern+reason>+<reason...>~hover@SHOW_TEXT@&eClick+here+to+&aadd+&ea+valueentry+for+a+player!"}));
		languageKeys.put("Cmd.ValueEntry.Registered.Remove", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%ve%+<Spieler>+<Intern+Grund>~hover@SHOW_TEXT@&eKlicke+hier+zum+&centfernen+&eeiner+Valueentry+für+einen+Spieler!",
						"&c〼~click@SUGGEST_COMMAND@%cmd%+%ve%+<player>+<intern+reason>~hover@SHOW_TEXT@&eClick+here+to+&cremove+&ea+valueentry+for+a+player!"}));
		languageKeys.put("Cmd.ValueEntry.Remove.Remove", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eDer Spieler &f%player% &ehat die &f%count% &eValueentry &f%ve% &emit dem Grund &f%reason% &cverloren!",
						"&eThe player &f%player% &ehas &clost &ethe &f%count% &evalueentry &f%ve% &ewith the reason &f%reason%&e!"}));
	}
	
	public void initModifierValueEntryLanguage() //INFO:ModifierValueEntryLanguages
	{
		cbmlanguageKeys.put(Bypass.Permission.OTHERPLAYER.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eByasspermission für /mavec modifier list <Zahl> [Spieler]",
						"&eBypasspermission for /mavec modifier list <number> [player]"}));
		cbmlanguageKeys.put(Bypass.Permission.OTHERPLAYER.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"&eByasspermission für",
						"&eden Befehl /mavec modifier list um",
						"&eModifier anderer Spieler zu sehen.",
						"&eBypasspermission for",
						"&ethe /mavec modifier list to see",
						"&emodifiers of other players."}));
	}
}