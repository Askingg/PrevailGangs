package envy.syn.gangs;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholders extends PlaceholderExpansion {

	public String getIdentifier() {
		return "ppgangs";
	}

	public String getPlugin() {
		return null;
	}

	public String getAuthor() {
		return "Askingg";
	}

	public String getVersion() {
		return "1.0";
	}

	public String onPlaceholderRequest(Player p, String identifier) {

		if (identifier.equalsIgnoreCase("prefix")) {
			if (Gangs.hasGang(p)) {
				return "&7&ki&f&o" + Gangs.getGang(p) + "&7&ki&f ";
			} else {
				return "";
			}
		}
		return null;
	}
}