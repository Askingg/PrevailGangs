package envy.syn.gangs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class GangGUI implements Listener {

	public static HashMap<Player, String> open = new HashMap<Player, String>(); // Player, Page
	public static List<Player> rem = new ArrayList<Player>();

	public static Inventory menu(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9);
		ItemStack space = new ItemStack(Material.STAINED_GLASS_PANE);
		space.setDurability((byte) 7);
		ItemMeta spacem = space.getItemMeta();
		spacem.setDisplayName(Core.color("&7"));
		space.setItemMeta(spacem);
		if (open.get(p).equals("main")) {
			if (Gangs.hasGang(p)) {
				String g = Gangs.getGang(p);
				inv = Bukkit.createInventory(null, 45, Core.color("&c&l" + g + " Panel"));
				ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta sm = (SkullMeta) i.getItemMeta();
				List<String> l = new ArrayList<String>();
				UUID owner = Gangs.getOwner(g);
				String ownerName = Core.uuidToName(owner);
				sm.setOwner(ownerName);
				sm.setDisplayName(Core.color("&b" + g));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Created&b " + Core.longToDate(Gangs.getCreated(g))));
				l.add(Core.color("&8 &l*&7 Age&b "
						+ Core.time((int) ((System.currentTimeMillis() - Gangs.getCreated(g)) / 1000))));
				sm.setLore(l);
				i.setItemMeta(sm);
				inv.setItem(4, i);
				i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				sm = (SkullMeta) i.getItemMeta();
				sm.setOwner(p.getName());
				l.clear();
				sm.setDisplayName(Core.color("&bPlayers"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Owner&c " + ownerName));
				List<UUID> officer = Gangs.getOfficers(g);
				if (officer.size() > 0) {
					l.add(Core.color("&7"));
					if (officer.size() == 1) {
						l.add(Core.color("&8 &l*&7 Officier &b" + Core.uuidToName(officer.get(0))));
					} else {
						l.add(Core.color("&8 &l*&7 Officers:"));
						for (int x = 0; x < officer.size(); x++) {
							l.add(Core.color("&8 &8 &l *&b " + Core.uuidToName(officer.get(x))));
						}
					}
				}
				List<UUID> recruits = Gangs.getRecruits(g);
				if (recruits.size() > 0) {
					l.add(Core.color("&7"));
					if (recruits.size() == 1) {
						l.add(Core.color("&8 &l*&7 Recruit &f" + Core.uuidToName(recruits.get(0))));
					} else {
						l.add(Core.color("&8 &l*&7 Recruits:"));
						for (int x = 0; x < recruits.size(); x++) {
							l.add(Core.color("&8 &8 &l *&f " + Core.uuidToName(recruits.get(x))));
						}
					}

				}
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 LeftClick to manage gang members"));
				l.add(Core.color("&8 &l*&7 RightClick to invite players"));
				sm.setLore(l);
				i.setItemMeta(sm);
				inv.setItem(11, i);
				i = new ItemStack(Material.COBBLESTONE);
				ItemMeta m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bBroken"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Total&b " + Gangs.getBroken(g)));
				l.add(Core.color("&8 &l*&7 Breakdown"));
				HashMap<String, Integer> breakdown = Gangs.getBrokenBreakdown(g);
				if (breakdown.size() > 0) {
					for (String s : breakdown.keySet()) {
						l.add(Core.color("&8 &l *&7 " + s + "&b " + breakdown.get(s)));
					}
				}
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(15, i);
				if (Gangs.isOwner(p, g)) {
					i = new ItemStack(Material.BARRIER);
					m = i.getItemMeta();
					l.clear();
					m.setDisplayName(Core.color("&c&lDELETE "));
					l.add(Core.color("&7"));
					l.add(Core.color("&8 &l*&7 Click here to delete your gang."));
					l.add(Core.color("&8 &l*&7 After clicking this item, you"));
					l.add(Core.color("&7&l &7 &7 much type the name of your"));
					l.add(Core.color("&7&l &7 &7 gang in the chat to confirm"));
					l.add(Core.color("&7&l &7 &7 the deletion."));
					l.add(Core.color("&8 &l*&7 This cannot be undone!"));
					m.setLore(l);
					i.setItemMeta(m);
					inv.setItem(31, i);
				} else {
					i = new ItemStack(Material.BARRIER);
					m = i.getItemMeta();
					l.clear();
					m.setDisplayName(Core.color("&c&lLeave " + g));
					l.add(Core.color("&7"));
					l.add(Core.color("&8 &l*&7 DoubleClick here to leave this &c" + g + "&7."));
					l.add(Core.color("&8 &l*&7 You can only join back if invited again."));
					m.setLore(l);
					i.setItemMeta(m);
					inv.setItem(31, i);
				}
				i = new ItemStack(Material.EXP_BOTTLE);
				m = i.getItemMeta();
				l.clear();
				m.setDisplayName(Core.color("&bLevelling"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Level&b " + Gangs.getGangLevel(g)));
				l.add(Core.color("&8 &l*&7 Levelup&b " + Core.decimals(0, Gangs.getLevelupProgress(g)) + "/"
						+ Core.decimals(0, Gangs.getLevelupCost(g))));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Points&b " + Gangs.getPoints(g)));
				l.add(Core.color("&8 &l*&7 Click to go to the point shop."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(22, i);
				for (int x = 0; x < inv.getSize(); x++) {
					if (inv.getItem(x) == null) {
						inv.setItem(x, space);
					}
				}
			} else {
				inv = Bukkit.createInventory(null, 27, Core.color("&b&lCreate Gang"));
				ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta m = (SkullMeta) i.getItemMeta();
				List<String> l = new ArrayList<String>();
				m.setOwner(p.getName());
				m.setDisplayName(Core.color("&bCreate Gang"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Click to create a gang:"));
				l.add(Core.color("&8 &l*&7 After clicking this item, type the name"));
				l.add(Core.color("&7&l &7 &7 of your gang in the chat."));
				l.add(Core.color("&8 &l*&7 Gang names must only contain alphabetical"));
				l.add(Core.color("&7&l &7 &7 characters, and must be between 4 and 12"));
				l.add(Core.color("&7&l &7 &7 characters long."));
				m.setLore(l);
				i.setItemMeta(m);
				inv.setItem(13, i);
				for (int x = 0; x < inv.getSize(); x++) {
					if (inv.getItem(x) == null) {
						inv.setItem(x, space);
					}
				}
			}
		} else if (open.get(p).equals("members")) {
			String g = Gangs.getGang(p);
			inv = Bukkit.createInventory(null, 45, Core.color("&c&l" + g + " Members"));
			Bukkit.createInventory(null, 45, "&b&l" + g + " Members");
			ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE);
			i.setDurability((byte) 14);
			ItemMeta m = i.getItemMeta();
			m.setDisplayName(Core.color("&cOwner"));
			i.setItemMeta(m);
			inv.setItem(9, i);
			i = new ItemStack(Material.STAINED_GLASS_PANE);
			i.setDurability((byte) 3);
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&bOfficers"));
			i.setItemMeta(m);
			inv.setItem(18, i);
			i = new ItemStack(Material.STAINED_GLASS_PANE);
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&fRecruits"));
			i.setItemMeta(m);
			inv.setItem(27, i);
			HashMap<String, Integer> brokenBreakdown = Gangs.getBrokenBreakdown(g);
			String owner = Core.uuidToName(Gangs.getOwner(g));
			i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta sm = (SkullMeta) i.getItemMeta();
			List<String> l = new ArrayList<String>();
			sm.setOwner(owner);
			sm.setDisplayName(Core.color("&c" + owner));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Joined&c " + Core.longToDate(Gangs.getJoinTime(owner, g)) + " GMT"));
			l.add(Core.color("&8 &l*&7 Broken&c " + brokenBreakdown.get(owner)));
			sm.setLore(l);
			i.setItemMeta(sm);
			inv.setItem(10, i);
			List<String> officers = Gangs.getOfficersNames(g);
			if (officers.size() > 0) {
				for (int x = 19; x < 27; x++) {
					if (officers.size() > x - 19) {
						String s = officers.get(x - 19);
						i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
						sm = (SkullMeta) i.getItemMeta();
						l.clear();
						sm.setOwner(s);
						sm.setDisplayName(Core.color("&b" + s));
						l.add(Core.color("&7"));
						l.add(Core.color("&8 &l*&7 Joined&b " + Core.longToDate(Gangs.getJoinTime(s, g)) + " GMT"));
						l.add(Core.color("&8 &l*&7 Broken&b " + brokenBreakdown.get(s)));
						if (Gangs.isOwner(p, g)) {
							l.add(Core.color("&7"));
							l.add(Core.color("&8 &l*&7 LeftClick to &cdemote&7 " + s));
							l.add(Core.color("&8 &l*&7 MiddleMouse to &4kick&7 " + s));
						}
						sm.setLore(l);
						i.setItemMeta(sm);
						inv.setItem(x, i);
					}
				}
			}
			List<String> recruits = Gangs.getRecruitsNames(g);
			if (recruits.size() > 0) {
				for (int x = 28; x < 36; x++) {
					if (recruits.size() > x - 28) {
						String s = recruits.get(x - 28);
						i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
						sm = (SkullMeta) i.getItemMeta();
						l.clear();
						sm.setOwner(s);
						sm.setDisplayName(Core.color("&f" + s));
						l.add(Core.color("&7"));
						l.add(Core.color("&8 &l*&7 Joined&f " + Core.longToDate(Gangs.getJoinTime(s, g)) + " GMT"));
						l.add(Core.color("&8 &l*&7 Broken&f " + brokenBreakdown.get(s)));
						if (Gangs.isOwner(p, g) || Gangs.isOfficer(p, g)) {
							l.add(Core.color("&7"));
							l.add(Core.color("&8 &l*&7 LeftClick to &apromote&7 " + s));
							l.add(Core.color("&8 &l*&7 MiddleMouse to &4kick&7 " + s));
						}
						sm.setLore(l);
						i.setItemMeta(sm);
						inv.setItem(x, i);
					}
				}
			}
		} else if (open.get(p).equals("invite")) {
			String g = Gangs.getGang(p);
			inv = Bukkit.createInventory(null, 45, Core.color("&c&l" + g + " Inviting"));
			for (int x = 0; x < 9; x++) {
				inv.setItem(x, space);
			}
			for (int x = 36; x < 45; x++) {
				inv.setItem(x, space);
			}
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (inv.firstEmpty() == -1) {
					break;
				}
				String pn = pl.getName();
				ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta sm = (SkullMeta) i.getItemMeta();
				List<String> l = new ArrayList<String>();
				l.add(Core.color("&7"));
				sm.setOwner(pn);
				if (Gangs.hasGang(pl)) {
					String pg = Gangs.getGang(pl);
					if (Gangs.isOwner(pl, pg)) {
						sm.setDisplayName(Core.color("&c" + pn));
						l.add(Core.color("&8 &l*&c " + pg + " Owner"));
					} else if (Gangs.isOfficer(pl, pg)) {
						sm.setDisplayName(Core.color("&b" + pn));
						l.add(Core.color("&8 &l*&b " + pg + " Officer"));
					} else {
						sm.setDisplayName(Core.color("&f" + pn));
						l.add(Core.color("&8 &l*&f " + pg + " Recruit"));
					}
				} else {
					sm.setDisplayName(Core.color("&a" + pn));
					l.add(Core.color("&8 &l*&7 LeftClick to invite " + pn));
				}
				sm.setLore(l);
				i.setItemMeta(sm);
				inv.addItem(i);

			}
		} else if (open.get(p).equals("points")) {
			String g = Gangs.getGang(p);
			inv = Bukkit.createInventory(null, 45, Core.color("&c&l" + g + " Points"));
			ItemStack i = new ItemStack(Material.EXP_BOTTLE);
			ItemMeta m = i.getItemMeta();
			List<String> l = new ArrayList<String>();
			m.setDisplayName(Core.color("&bPoints"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Points&B " + Gangs.getPoints(g)));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(4, i);
			i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta sm = (SkullMeta) i.getItemMeta();
			sm.setOwner(p.getName());
			l.clear();
			sm.setDisplayName(Core.color("&bGang Officers"));
			int officers = Gangs.getRawBoost(g, "Officers");
			if (officers < 2) {
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7  Level&b " + officers));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getMaxOfficers(g)));
				l.add(Core.color("&8 &l*&7 Next Level&b " + (Gangs.getMaxOfficers(g) + 1)));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Click to upgrade."));
			} else {
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7  Level&b MAX"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getMaxOfficers(g)));
			}
			sm.setLore(l);
			i.setItemMeta(sm);
			inv.setItem(19, i);
			i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			sm = (SkullMeta) i.getItemMeta();
			sm.setOwner(p.getName());
			l.clear();
			sm.setDisplayName(Core.color("&fGang Recruits"));
			int recruits = Gangs.getRawBoost(g, "Recruits");
			if (officers < 4) {
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7  Level&b " + recruits));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getMaxRecruits(g)));
				l.add(Core.color("&8 &l*&7 Next Level&b " + (Gangs.getMaxRecruits(g) + 1)));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Click to upgrade."));
			} else {
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7  Level&b MAX"));
				l.add(Core.color("&7"));
				l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getMaxRecruits(g)));
			}
			sm.setLore(l);
			i.setItemMeta(sm);
			inv.setItem(20, i);
			i = new ItemStack(Material.STAINED_GLASS);
			i.setDurability((byte) 4);
			l.clear();
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&eGold Boost"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7  Level&b " + Gangs.getRawBoost(g, "Gold")));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getCurrencyBoost(g, "Gold")) + "%");
			l.add(Core.color("&8 &l*&7 Next Level&b " + Gangs.getCurrencyBoost(Gangs.getRawBoost(g, "Gold") + 1))
					+ "%");
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Click to upgrade."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(28, i);
			i = new ItemStack(Material.STAINED_GLASS);
			i.setDurability((byte) 2);
			l.clear();
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&dElixir Boost"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7  Level&b " + Gangs.getRawBoost(g, "Elixir")));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getCurrencyBoost(g, "Elixir")) + "%");
			l.add(Core.color("&8 &l*&7 Next Level&b " + Gangs.getCurrencyBoost(Gangs.getRawBoost(g, "Elixir") + 1))
					+ "%");
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Click to upgrade."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(29, i);
			i = new ItemStack(Material.STAINED_GLASS);
			i.setDurability((byte) 1);
			l.clear();
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&6PureGold Boost"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7  Level&b " + Gangs.getRawBoost(g, "Pure")));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getCurrencyBoost(g, "Pure")) + "%");
			l.add(Core.color("&8 &l*&7 Next Level&b " + Gangs.getCurrencyBoost(Gangs.getRawBoost(g, "Pure") + 1))
					+ "%");
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Click to upgrade."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(30, i);
			i = new ItemStack(Material.STAINED_GLASS);
			i.setDurability((byte) 10);
			l.clear();
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&5DarkElixir Boost"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7  Level&b " + Gangs.getRawBoost(g, "Dark")));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getCurrencyBoost(g, "Dark")) + "%");
			l.add(Core.color("&8 &l*&7 Next Level&b " + Gangs.getCurrencyBoost(Gangs.getRawBoost(g, "Dark") + 1))
					+ "%");
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Click to upgrade."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(31, i);
			i = new ItemStack(Material.DIAMOND_SWORD);
			l.clear();
			m = i.getItemMeta();
			m.setDisplayName(Core.color("&3Damage Boost"));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7  Level&b " + Gangs.getRawBoost(g, "Damage")));
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Current Level&b " + Gangs.getCurrencyBoost(g, "Damage")) + "%");
			l.add(Core.color("&8 &l*&7 Next Level&b " + Gangs.getCurrencyBoost(Gangs.getRawBoost(g, "Damage") + 1))
					+ "%");
			l.add(Core.color("&7"));
			l.add(Core.color("&8 &l*&7 Click to upgrade."));
			m.setLore(l);
			i.setItemMeta(m);
			inv.setItem(32, i);
			for (int x = 0; x < inv.getSize(); x++) {
				if (inv.getItem(x) == null) {
					inv.setItem(x, space);
				}
			}
		}
		return inv;
	}

	public static List<Player> creating = new ArrayList<Player>();
	public static List<Player> deleting = new ArrayList<Player>();
	public static List<Player> leaving = new ArrayList<Player>();

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (open.containsKey(p)) {
			e.setCancelled(true);
			if (open.get(p).equals("main")) {
				if (Gangs.hasGang(p)) {
					String g = Gangs.getGang(p);
					if (e.getRawSlot() < 45) {
						if (e.getRawSlot() == 31) {
							if (Gangs.isOwner(p, g)) {
								deleting.add(p);
								Core.message(Main.prefix
										+ "&cGang Deletion:&7 To confirm the deletion of your gang, type the name of your gang.",
										p);
								p.closeInventory();
								new BukkitRunnable() {
									@Override
									public void run() {
										if (deleting.contains(p)) {
											Core.message(Main.prefix
													+ "Sorry, but you took to long to confirm your gang deletion", p);
											deleting.remove(p);
										}
									}
								}.runTaskLaterAsynchronously(Main.inst, 200);
							} else {
								if (leaving.size() > 0 && leaving.contains(p)) {
									p.closeInventory();
									Gangs.leaveGang(p, g);
								} else {
									leaving.add(p);
									new BukkitRunnable() {
										@Override
										public void run() {
											if (leaving.contains(p)) {
												leaving.remove(p);
											}
										}
									}.runTaskLaterAsynchronously(Main.inst, 10);
								}
							}
							return;
						}
						if (e.getRawSlot() == 11) {
							ClickType click = e.getClick();
							if (click.toString().contains("LEFT")) {
								rem.add(p);
								open.put(p, "members");
								p.openInventory(menu(p));
							} else if (click.toString().contains("RIGHT")) {
								rem.add(p);
								open.put(p, "invite");
								p.openInventory(menu(p));
							}
							return;
						}
						if (e.getRawSlot() == 22) {
							rem.add(p);
							open.put(p, "points");
							p.openInventory(menu(p));
							return;
						}
					}
				} else {
					if (e.getRawSlot() < 27) {
						if (e.getRawSlot() == 13) {
							creating.add(p);
							Core.message(Main.prefix + "&7Type the name of your gang in chat now.", p);
							p.closeInventory();
							new BukkitRunnable() {
								@Override
								public void run() {
									if (creating.contains(p)) {
										Core.message(Main.prefix + "Sorry, but you took to long naming your gang.", p);
										creating.remove(p);
									}
								}
							}.runTaskLaterAsynchronously(Main.inst, 200);
							return;
						}
					}
				}
			} else if (open.get(p).equals("members")) {
				ItemStack ci = e.getCurrentItem();
				if (ci != null && ci.getType() == Material.SKULL_ITEM) {
					String g = Gangs.getGang(p);
					Player pl = Bukkit.getPlayer(((SkullMeta) ci.getItemMeta()).getOwner());
					if (pl != null) {
						ClickType click = e.getClick();
						if (click.toString().contains("LEFT")) {
							if (Gangs.isRecruit(pl, g)) {
								if (Gangs.isOwner(p, g) || Gangs.isOfficer(p, g)) {
									if (Gangs.getOfficers(g).size() < Gangs.getMaxOfficers(g)) {
										Gangs.promote(pl, g);
									} else {
										Core.message(Main.prefix + "Sorry, but &c" + g
												+ "&7 cannot have any more &bOfficers&7.", p);
									}
								}
							} else if (Gangs.isOfficer(pl, g)) {
								if (Gangs.isOwner(p, g)) {
									if (Gangs.getRecruits(g).size() < Gangs.getMaxRecruits(g)) {
										Gangs.demote(pl, g);
									} else {
										Core.message(Main.prefix + "Sorry, but &c" + g
												+ "&7 cannot have any more &fRecruits&7.", p);
									}
								}
							}
							rem.add(p);
							p.openInventory(menu(p));
						}
						if (click.equals(ClickType.MIDDLE)) {
							if (Gangs.isOfficer(pl, g)) {
								if (Gangs.isOwner(p, g)) {
									Gangs.kick(p, pl, g);
								}
							}
							if (Gangs.isRecruit(pl, g)) {
								if (Gangs.isOwner(p, g) || Gangs.isOfficer(p, g)) {
									Gangs.kick(p, pl, g);
								}
							}
							rem.add(p);
							p.openInventory(menu(p));
						}
					} else {
						Core.message(Main.prefix + "Sorry, but that player is not online", p);
					}
				}
			} else if (open.get(p).equals("invite")) {
				String g = Gangs.getGang(p);
				ItemStack ci = e.getCurrentItem();
				if (ci.getType() == Material.SKULL_ITEM) {
					if (Gangs.getRecruits(g).size() < Gangs.getMaxRecruits(g)) {
						Player pl = Bukkit.getPlayer(((SkullMeta) ci.getItemMeta()).getOwner());
						if (pl != null) {
							if (!Gangs.hasGang(pl)) {
								if (!Gangs.invites.containsKey(pl)) {
									Gangs.invites.put(pl, g);
									Core.message(Main.prefix + "You invited &b" + pl.getName() + "&7 to &b" + g, p);
									Core.message(Main.prefix + "&b" + p.getName() + "&7 invited you to &b" + g + "&7:",
											pl);
									Core.hoverRunCMD(pl, "&2 &l*&a Accept Invitation", "/g accept " + g,
											"&7Click to accept the invitation to &b" + g);
									Core.hoverRunCMD(pl, "&4 &l*&c Decline Invitation", "/g decline " + g,
											"&7Click to decline the invitation to &c" + g);
									new BukkitRunnable() {
										@Override
										public void run() {
											if (Gangs.invites.containsKey(pl)) {
												if (Gangs.invites.get(pl).equals(g)) {
													Gangs.invites.remove(pl);
													Core.message(Main.prefix + "The invite you sent to &c"
															+ pl.getName() + "&7 has expired.", p);
													Core.message(
															Main.prefix + "The invite to &c" + g + "&7 has expired.",
															pl);
												}
											}
										}
									}.runTaskLaterAsynchronously(Main.inst, 200);
								} else {
									Core.message(Main.prefix + "Sorry, but that player already has a pending invite.",
											p);
								}
							} else {
								Core.message(Main.prefix + "Sorry, but &c" + pl.getName() + "&7 is already in &c"
										+ Gangs.getGang(pl) + "&7.", p);
							}
						}
					} else {
						if (Gangs.getOfficers(g).size() < Gangs.getMaxOfficers(g)) {
							Core.message(Main.prefix + "Sorry, but &c" + g
									+ "&7 cannot accept anymore &fRecruits&7 currently, promote one of your current &fRecruits&7 to &bOfficer&7 to invite more members.",
									p);
						} else {
							Core.message(
									Main.prefix + "Sorry, but &c" + g + "&7 cannot accept anymore members currently.",
									p);
						}
					}
				}
			} else if (open.get(p).equals("points")) {
				String g = Gangs.getGang(p);
				int x = Gangs.getPoints(g);
				int s = e.getRawSlot();
				if (s == 19 || s == 20 || (s >= 28 && s <= 32)) {
					if (!Gangs.isRecruit(p, g)) {
						if (x > 0) {
							if (s == 19) {
								int officers = Gangs.getMaxOfficers(g);
								if (officers < 2) {
									Gangs.addBoost(g, "Officers", 1);
									Gangs.takePoints(g, 1);
									Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
											+ "'s &bOfficer&7 limit to &b" + Gangs.getMaxOfficers(g));
								} else {
									Core.message(Main.prefix + "Sorry, but &c" + g
											+ "&7 has reached the maxium amount of &bOfficer&7 slots.", p);
								}
							} else if (s == 20) {
								int recruits = Gangs.getMaxRecruits(g);
								if (recruits < 4) {
									Gangs.addBoost(g, "Recruits", 1);
									Gangs.takePoints(g, 1);
									Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
											+ "'s &fRecruit&7 limit to &b" + Gangs.getMaxRecruits(g));
								} else {
									Core.message(Main.prefix + "Sorry, but &c" + g
											+ "&7 has reached the maxium amount of &bOfficer&7 slots.", p);
								}
							} else if (s == 28) {
								Gangs.addBoost(g, "Gold", 1);
								Gangs.takePoints(g, 1);
								Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
										+ "'s&e GoldBoost&7 to &b" + Gangs.getCurrencyBoost(g, "Gold"));
							} else if (s == 29) {
								Gangs.addBoost(g, "Elixir", 1);
								Gangs.takePoints(g, 1);
								Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
										+ "'s&d ElixirBoost&7 to &b" + Gangs.getCurrencyBoost(g, "Elixir"));
							} else if (s == 30) {
								Gangs.addBoost(g, "Pure", 1);
								Gangs.takePoints(g, 1);
								Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
										+ "'s&6 PureBoost&7 to &b" + Gangs.getCurrencyBoost(g, "Pure"));
							} else if (s == 31) {
								Gangs.addBoost(g, "Dark", 1);
								Gangs.takePoints(g, 1);
								Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
										+ "'s&5 DarkBoost&7 to &b" + Gangs.getCurrencyBoost(g, "Dark"));
							} else if (s == 32) {
								Gangs.addBoost(g, "Damage", 1);
								Gangs.takePoints(g, 1);
								Gangs.messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 upgraded &b" + g
										+ "'s&3 DamageBoost&7 to &b" + Gangs.getCurrencyBoost(g, "Damage"));
							}
							rem.add(p);
							p.openInventory(menu(p));
						} else {
							Core.message(Main.prefix + "Sorry, but &c" + g + "&7 doesn't have any points.", p);
						}
					} else {
						Core.message(Main.prefix + "Sorry, but &fRecruits&7 cannot spent points.", p);
					}
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (creating.contains(p)) {
			creating.remove(p);
			e.setCancelled(true);
			String g = e.getMessage().split(" ")[0];
			if (Gangs.createGang(p, g)) {
				open.put(p, "main");
				p.openInventory(menu(p));
			} else {
				open.put(p, "main");
				p.openInventory(menu(p));
			}
		}
		if (deleting.contains(p)) {
			deleting.remove(p);
			e.setCancelled(true);
			if (Gangs.getGang(p).equals(e.getMessage().split(" ")[0])) {
				Gangs.deleteGang(p, Gangs.getGang(p));
			} else {
				Core.message(Main.prefix + "Gang deletion cancelled, name confirmation was invalid", p);
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (open.containsKey(p)) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (rem.contains(p)) {
						rem.remove(p);
					} else {
						open.remove(p);
					}
				}
			}.runTaskLaterAsynchronously(Main.inst, 3);
		}
	}
}
