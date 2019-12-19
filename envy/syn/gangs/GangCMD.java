package envy.syn.gangs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangCMD implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String string, String[] args) {
		if (s instanceof Player) {
			Player p = (Player) s;
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("accept")) {
					if (Gangs.invites.containsKey(p)) {
						String g = args[1];
						if (Gangs.getRecruits(g).size() < Gangs.getMaxRecruits(g)) {
							if (Gangs.invites.get(p).equals(g)) {
								Gangs.joinGang(p, g);
								Gangs.invites.remove(p);
							}
						} else {
							Core.message(Main.prefix + "Sorry, but &c" + g
									+ "&7 cannot accept any more &fRecruits&7 currently.", s);
							Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 tried to join &c" + g
									+ "&7 but cannot because there're no free &fRecruit&7 slots.");
						}
					}
					return true;
				} else if (args[0].equalsIgnoreCase("decline")) {
					if (Gangs.invites.containsKey(p)) {
						String g = args[1];
						if (Gangs.invites.get(p).equals(g)) {
							Core.broadcast(Main.prefix + "&b" + p.getName() + "&7 declined the invitation to &c" + g);
							Gangs.invites.remove(p);
						}
					}
					return true;
				}
			}
			GangGUI.open.put(p, "main");
			p.openInventory(GangGUI.menu(p));
		}
		return false;
	}
}
