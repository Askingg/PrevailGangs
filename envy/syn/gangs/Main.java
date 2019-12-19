package envy.syn.gangs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static String prefix = "&3&lPrevail&b&lPrison &8&l»&7 ";
	public static Main inst;
	public static boolean papi = true;

	public void onEnable() {
		inst = this;
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
			Core.console("&b &b &l»&c PlaceholderAPI&7 will not be used as the plugin was not found.");
			papi = false;
		}
		getServer().getPluginManager().registerEvents(new GangGUI(), this);
		getServer().getPluginManager().registerEvents(new Gangs(), this);
		getCommand("gangs").setExecutor(new GangCMD());
		Files.base();
		Gangs.loadGangs();
		new Placeholders().register();
	}
	
	public void onDisable() {
		Gangs.saveGangs();
	}
}
