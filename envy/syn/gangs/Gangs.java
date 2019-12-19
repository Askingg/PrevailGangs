package envy.syn.gangs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Gangs implements Listener {

	private static List<String> gangs = new ArrayList<String>();
	private static HashMap<UUID, String> gang = new HashMap<UUID, String>();
	private static HashMap<String, UUID> owner = new HashMap<String, UUID>();
	private static HashMap<String, Long> created = new HashMap<String, Long>();
	private static HashMap<String, Integer> level = new HashMap<String, Integer>();
	private static HashMap<String, Integer> levelup = new HashMap<String, Integer>();
	private static HashMap<String, Integer> points = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostOfficers = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostRecruits = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostGold = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostElixir = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostPure= new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostDark = new HashMap<String, Integer>();
	private static HashMap<String, Integer> boostDamage = new HashMap<String, Integer>();
	private static HashMap<String, List<UUID>> officers = new HashMap<String, List<UUID>>();
	private static HashMap<String, List<UUID>> recruits = new HashMap<String, List<UUID>>();
	private static HashMap<String, Integer> broken = new HashMap<String, Integer>();
	private static HashMap<String, List<String>> brokenBreakdown = new HashMap<String, List<String>>(); // Gang,
																										// UUID;blocksbroken
	private static HashMap<String, List<String>> joined = new HashMap<String, List<String>>(); // gang, UUID;Long

	public static HashMap<Player, String> invites = new HashMap<Player, String>(); // InvitedPlayer, Gang

	public static void loadGangs() {
		ConfigurationSection conf = Files.config.getConfigurationSection("users");
		if (conf != null) {
			for (String s : conf.getKeys(false)) {
				gang.put(UUID.fromString(s), conf.getString(s));
			}
		}
		conf = Files.config.getConfigurationSection("gangs");
		if (conf != null) {
			for (String g : conf.getKeys(false)) {
				gangs.add(g);
				owner.put(g, UUID.fromString(conf.getString(g + ".owner")));
				created.put(g, conf.getLong(g + ".created"));
				level.put(g, conf.getInt(g + ".level"));
				levelup.put(g, conf.getInt(g + ".levelup"));
				points.put(g, conf.getInt(g + ".points"));
				boostOfficers.put(g, conf.getInt(g + ".boost.officers"));
				boostRecruits.put(g, conf.getInt(g + ".boost.Recruits"));
				boostGold.put(g, conf.getInt(g + ".boost.gold"));
				boostElixir.put(g, conf.getInt(g + ".boost.elixir"));
				boostPure.put(g, conf.getInt(g + ".boost.pure"));
				boostDark.put(g, conf.getInt(g + ".boost.dark"));
				boostDamage.put(g, conf.getInt(g + ".boost.damage"));
				List<UUID> gOfficers = new ArrayList<UUID>();
				if (conf.getStringList(g + ".officers").size() > 0) {
					for (String s : conf.getStringList(g + ".officers")) {
						gOfficers.add(UUID.fromString(s));
					}
				}
				officers.put(g, gOfficers);
				List<UUID> gRecruits = new ArrayList<UUID>();
				if (conf.getStringList(g + ".recruits").size() > 0) {
					for (String s : conf.getStringList(g + ".recruits")) {
						gRecruits.add(UUID.fromString(s));
					}
				}
				recruits.put(g, gRecruits);
				broken.put(g, conf.getInt(g + ".broken"));
				brokenBreakdown.put(g, conf.getStringList(g + ".brokenBreakdown"));
				joined.put(g, conf.getStringList(g + ".joined"));
				Core.console("&8 &l*&7 Loaded gang:&b " + g);
			}
		}
	}

	public static void saveGangs() {
		Files.config.set("users", null);
		Files.config.set("gangs", null);
		if (gangs.size() > 0) {
			for (UUID u : gang.keySet()) {
				Files.config.set("users." + u.toString(), gang.get(u));
			}
			for (String g : gangs) {
				Files.config.set("gangs." + g + ".owner", owner.get(g).toString());
				Files.config.set("gangs." + g + ".created", created.get(g));
				Files.config.set("gangs." + g + ".level", level.get(g));
				Files.config.set("gangs." + g + ".levelup", levelup.get(g));
				Files.config.set("gangs." + g + ".points", points.get(g));
				Files.config.set("gangs." + g + ".boost.officers", boostOfficers.get(g));
				Files.config.set("gangs." + g + ".boost.recruits", boostRecruits.get(g));
				Files.config.set("gangs." + g + ".boost.gold", boostGold.get(g));
				Files.config.set("gangs." + g + ".boost.elixir", boostElixir.get(g));
				Files.config.set("gangs." + g + ".boost.pure", boostPure.get(g));
				Files.config.set("gangs." + g + ".boost.dark", boostDark.get(g));
				Files.config.set("gangs." + g + ".boost.damage", boostDamage.get(g));
				List<String> gOfficers = new ArrayList<String>();
				if (officers.get(g).size() > 0) {
					for (UUID u : officers.get(g)) {
						gOfficers.add(u.toString());
					}
				}
				Files.config.set("gangs." + g + ".officers", gOfficers);
				List<String> gRecruits = new ArrayList<String>();
				if (recruits.get(g).size() > 0) {
					for (UUID u : recruits.get(g)) {
						gRecruits.add(u.toString());
					}
				}
				Files.config.set("gangs." + g + ".recruits", gRecruits);
				Files.config.set("gangs." + g + ".broken", broken.get(g));
				Files.config.set("gangs." + g + ".brokenBreakdown", brokenBreakdown.get(g));
				Files.config.set("gangs." + g + ".joined", joined.get(g));
			}
		}
		try {
			Files.config.save(Files.configFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean createGang(Player p, String g) {
		if (!hasGang(p)) {
			if (!isNameTaken(g)) {
				if (g.length() >= 4 && g.length() <= 12) {
					if (StringUtils.isAlpha(g)) {
						UUID u = p.getUniqueId();
						gangs.add(g);
						gang.put(u, g);
						owner.put(g, u);
						level.put(g, 0);
						levelup.put(g, 0);
						points.put(g, 0);
						boostOfficers.put(g, 1);
						boostRecruits.put(g, 1);
						boostGold.put(g, 0);
						boostElixir.put(g, 0);
						boostPure.put(g, 0);
						boostDark.put(g, 0);
						boostDamage.put(g, 0);
						officers.put(g, new ArrayList<UUID>());
						recruits.put(g, new ArrayList<UUID>());
						broken.put(g, 0);
						brokenBreakdown.put(g, Arrays.asList(p.getUniqueId().toString() + ";0"));
						joined.put(g, Arrays.asList(p.getUniqueId().toString() + ";" + System.currentTimeMillis()));
						created.put(g, System.currentTimeMillis());
						Core.broadcast(Main.prefix + "&b" + p.getName() + "&7 created the gang, &b" + g + "&7.");
					} else {
						Core.message(
								Main.prefix + "Sorry, but your gang name must only contain alphabetical characters.",
								p);
					}
				} else {
					Core.message(
							Main.prefix
									+ "Sorry, but the length of your gang name must be between 4 and 12 characters.",
							p);
				}
			} else {
				Core.message(Main.prefix + "Sorry, but the gang name &c" + g + "&7 is already taken.", p);
			}
		} else {
			Core.message(Main.prefix + "Sorry, but you're already in a gang&8 (&c" + getGang(p) + "&8)", p);
		}
		return false;
	}

	public static void deleteGang(Player p, String g) {
		gangs.remove(g);
		gang.remove(owner.get(g));
		List<UUID> gOfficers = getOfficers(g);
		if (gOfficers.size() > 0) {
			for (UUID u : gOfficers) {
				gang.remove(u);
			}
		}
		List<UUID> gRecruits = getRecruits(g);
		if (gRecruits.size() > 0) {
			for (UUID u : gRecruits) {
				gang.remove(u);
			}
		}
		owner.remove(g);
		officers.remove(g);
		recruits.remove(g);
		level.remove(g);
		levelup.remove(g);
		points.remove(g);
		boostOfficers.remove(g);
		boostRecruits.remove(g);
		boostGold.remove(g);
		boostElixir.remove(g);
		boostPure.remove(g);
		boostDark.remove(g);
		boostDamage.remove(g);
		broken.remove(g);
		brokenBreakdown.remove(g);
		joined.remove(g);
		created.remove(g);
		Core.broadcast(Main.prefix + "&7The gang &c" + g + "&7 was deleted by &c" + p.getName() + "&7.");
	}
	
	public static int getLevelupProgress(String g) {
		return levelup.get(g);
	}

	public static int getGangLevel(String g) {
		return level.get(g);
	}
	
	public static int levelupRequirement() {
		return 10000;
	}

	public static int getLevelupCost(String g) {
		int x = levelupRequirement();
		x += x * getGangLevel(g);
		return x;
	}

	public static void levelupGang(String g) {
		int x = level.get(g);
		level.put(g, x + 1);
		Core.broadcast(Main.prefix + "&b" + g + "&7 has levelled up to &b"+(x+1)+"&7.");
	}

	public static boolean joinGang(Player p, String g) {
		if (!hasGang(p)) {
			List<UUID> gRecruits = getRecruits(g);
			if (gRecruits.size() < 3) { // CHANGE 3 ONCE GANG UPGRADES HAVE BEEN CODED
				UUID u = p.getUniqueId();
				gang.put(u, g);
				gRecruits.add(u);
				recruits.put(g, gRecruits);
				List<String> gJoined = new ArrayList<String>();
				gJoined.addAll(joined.get(g));
				gJoined.add(u.toString() + ";" + System.currentTimeMillis());
				joined.put(g, gJoined);
				List<String> gBrokenBreakdown = new ArrayList<String>();
				gBrokenBreakdown.addAll(brokenBreakdown.get(g));
				gBrokenBreakdown.add(u.toString() + ";" + 0);
				brokenBreakdown.put(g, gBrokenBreakdown);
				Core.broadcast(Main.prefix + "&b" + p.getName() + "&7 joined &b" + g + "&7.");
			} else {
				Core.message(Main.prefix + "Sorry, but &c" + g + "&7 cannot accept any more recruits currently.", p);
			}
		} else {
			Core.message(Main.prefix + "Sorry, but you're already in a gang.", p);
		}
		return false;
	}

	public static boolean leaveGang(Player p, String g) {
		UUID u = p.getUniqueId();
		String us = u.toString();
		gang.remove(u);
		if (isOfficer(p, g)) {
			List<UUID> gOfficers = getOfficers(g);
			gOfficers.remove(u);
			officers.put(g, gOfficers);
		} else {
			List<UUID> gRecruits = getRecruits(g);
			gRecruits.remove(u);
			recruits.put(g, gRecruits);
		}
		List<String> gJoined = new ArrayList<String>();
		gJoined.addAll(joined.get(g));
		for (String s : gJoined) {
			String[] str = s.split(";");
			if (str[0].equals(us)) {
				gJoined.remove(s);
				break;
			}
		}
		joined.put(g, gJoined);
		List<String> gBrokenBreakdown = new ArrayList<String>();
		gBrokenBreakdown.addAll(brokenBreakdown.get(g));
		for (String s : gBrokenBreakdown) {
			String[] str = s.split(";");
			if (str[0].equals(us)) {
				gBrokenBreakdown.remove(s);
				break;
			}
		}
		brokenBreakdown.put(g, gBrokenBreakdown);
		Core.broadcast(Main.prefix + "&b" + p.getName() + "&7 left &c" + g + "&7.");
		return true;
	}

	public static boolean kick(Player kicker, Player kicked, String g) {
		UUID u = kicked.getUniqueId();
		String us = u.toString();
		gang.remove(u);
		if (isOfficer(kicked, g)) {
			List<UUID> gOfficers = getOfficers(g);
			gOfficers.remove(u);
			officers.put(g, gOfficers);
		} else {
			List<UUID> gRecruits = getRecruits(g);
			gRecruits.remove(u);
			recruits.put(g, gRecruits);
		}
		List<String> gJoined = new ArrayList<String>();
		gJoined.addAll(joined.get(g));
		for (String s : gJoined) {
			String[] str = s.split(";");
			if (str[0].equals(us)) {
				gJoined.remove(s);
				break;
			}
		}
		joined.put(g, gJoined);
		List<String> gBrokenBreakdown = new ArrayList<String>();
		gBrokenBreakdown.addAll(brokenBreakdown.get(g));
		for (String s : gBrokenBreakdown) {
			String[] str = s.split(";");
			if (str[0].equals(us)) {
				gBrokenBreakdown.remove(s);
				break;
			}
		}
		brokenBreakdown.put(g, gBrokenBreakdown);
		Core.broadcast(Main.prefix + "&c" + kicked.getName() + "&7 was kicked from &c" + g + "&7 by &b"
				+ kicker.getName() + "&7.");
		return true;
	}

	public static boolean promote(Player p, String g) {
		if (isRecruit(p, g)) {
			UUID u = p.getUniqueId();
			List<UUID> gRecruits = getRecruits(g);
			gRecruits.remove(u);
			recruits.put(g, gRecruits);
			List<UUID> gOfficers = getOfficers(g);
			gOfficers.add(u);
			officers.put(g, gOfficers);
			messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 was promoted to &bOfficer");
		}
		return false;
	}

	public static boolean demote(Player p, String g) {
		if (isOfficer(p, g)) {
			UUID u = p.getUniqueId();
			List<UUID> gOfficers = getOfficers(g);
			gOfficers.remove(u);
			officers.put(g, gOfficers);
			List<UUID> gRecruits = getRecruits(g);
			gRecruits.add(u);
			recruits.put(g, gRecruits);
			messageAllMembers(g, Main.prefix + "&b" + p.getName() + "&7 was demoted to &cRecruit");
		}
		return false;
	}

	public static boolean hasGang(Player p) {
		if (gang.containsKey(p.getUniqueId()))
			return true;
		return false;
	}

	public static String getGang(Player p) {
		if (hasGang(p)) {
			return gang.get(p.getUniqueId());
		}
		return null;
	}

	public static void messageAllMembers(String g, String m) {
		Player p = Bukkit.getPlayer(getOwner(g));
		if (p != null) {
			Core.message(m, p);
		}
		List<UUID> gOfficers = getOfficers(g);
		if (gOfficers.size() > 0) {
			for (UUID u : gOfficers) {
				p = Bukkit.getPlayer(u);
				if (p != null) {
					Core.message(m, p);
				}
			}
		}
		List<UUID> gRecruits = getRecruits(g);
		if (gRecruits.size() > 0) {
			for (UUID u : gRecruits) {
				p = Bukkit.getPlayer(u);
				if (p != null) {
					Core.message(m, p);
				}
			}
		}
	}

	public static UUID getOwner(String g) {
		return owner.get(g);
	}

	public static long getJoinTime(Player p, String g) {
		List<String> l = joined.get(g);
		String u = p.getUniqueId().toString();
		for (String s : l) {
			String[] str = s.split(";");
			if (str[0].equals(u)) {
				return Long.valueOf(str[1]);
			}
		}
		return -1;
	}

	@SuppressWarnings("deprecation")
	public static long getJoinTime(String p, String g) {
		List<String> l = joined.get(g);
		String u = Bukkit.getOfflinePlayer(p).getUniqueId().toString();
		for (String s : l) {
			String[] str = s.split(";");
			if (str[0].equals(u)) {
				return Long.valueOf(str[1]);
			}
		}
		return -1;
	}

	public static boolean isOwner(Player p, String g) {
		if (getOwner(g).equals(p.getUniqueId()))
			return true;
		return false;
	}

	public static boolean isOfficer(Player p, String g) {
		List<UUID> l = getOfficers(g);
		if (l.size() > 0 && l.contains(p.getUniqueId())) {
			return true;
		}
		return false;
	}

	public static boolean isRecruit(Player p, String g) {
		List<UUID> l = getRecruits(g);
		if (l.size() > 0 && l.contains(p.getUniqueId())) {
			return true;
		}
		return false;
	}

	public static List<UUID> getOfficers(String g) {
		List<UUID> l = new ArrayList<UUID>();
		if (officers.get(g).size() > 0) {
			l.addAll(officers.get(g));
		}
		return l;
	}

	public static List<String> getOfficersNames(String g) {
		List<String> l = new ArrayList<String>();
		List<UUID> uuids = getOfficers(g);
		if (uuids.size() > 0) {
			for (UUID u : uuids) {
				l.add(Core.uuidToName(u));
			}
		}
		return l;
	}

	public static List<UUID> getRecruits(String g) {
		List<UUID> l = new ArrayList<UUID>();
		if (recruits.get(g).size() > 0) {
			l.addAll(recruits.get(g));
		}
		return l;
	}

	public static List<String> getRecruitsNames(String g) {
		List<String> l = new ArrayList<String>();
		List<UUID> uuids = getRecruits(g);
		if (uuids.size() > 0) {
			for (UUID u : uuids) {
				l.add(Core.uuidToName(u));
			}
		}
		return l;
	}

	public static long getCreated(String g) {
		return created.get(g);
	}

	public static boolean isNameTaken(String g) {
		if (gangs.size() > 0) {
			for (String s : gangs) {
				if (s.equalsIgnoreCase(g))
					return true;
			}
		}
		return false;
	}

	public static int getBroken(String g) {
		return broken.get(g);
	}

	public static HashMap<String, Integer> getBrokenBreakdown(String g) {
		List<String> l = brokenBreakdown.get(g);
		HashMap<String, Integer> m = new HashMap<String, Integer>();
		if (l.size() > 0) {
			for (String s : l) {
				String[] str = s.split(";");
				m.put(Core.uuidToName(UUID.fromString(str[0])), Integer.valueOf(str[1]));
			}
		}
		return m;
	}
	
	public static int getPoints(String g) {
		return points.get(g);
	}
	
	public static void addPoints(String g, int x) {
		points.put(g, getPoints(g)+x);
	}
	
	public static void takePoints(String g, int x) {
		points.put(g, getPoints(g)-x);
	}
	
	public static int getRawBoost(String g, String boost) {
		if (boost.equalsIgnoreCase("Gold")) {
			return boostGold.get(g);
		} else if (boost.equalsIgnoreCase("Officers")) {
			return boostOfficers.get(g);
		} else if (boost.equalsIgnoreCase("Recruits")) {
			return boostRecruits.get(g);
		} else if (boost.equalsIgnoreCase("Elixir")) {
			return boostElixir.get(g);
		} else if (boost.equalsIgnoreCase("Pure")) {
			return boostPure.get(g);
		} else if (boost.equalsIgnoreCase("Dark")) {
			return boostDark.get(g);
		} else if (boost.equalsIgnoreCase("Damage")) {
			return boostDamage.get(g);
		}
		return -1;
	}
	
	public static int getCurrencyBoost(int level) {
		int i = 0;
		for (int x = 0;x<= level;x++) {
			i+=x*5;
		}
		return i;
	}
	
	public static int getCurrencyBoost(String g, String boost) {
		int level = 0;
		if (boost.equalsIgnoreCase("Gold")) {
			level = boostGold.get(g);
		} else if (boost.equalsIgnoreCase("Elixir")) {
			level = boostElixir.get(g);
		}else if (boost.equalsIgnoreCase("Pure")) {
			level = boostPure.get(g);
		} else if (boost.equalsIgnoreCase("Dark")) {
			level = boostDark.get(g);
		} else if (boost.equalsIgnoreCase("Damage")) {
			level = boostDamage.get(g);
		}
		int i = 0;
		for (int x = 0;x<= level;x++) {
			i+=x*5;
		}
		return i;
	}
	
	public static void addBoost(String g, String boost, int levels) {
		if (boost.equalsIgnoreCase("Gold")) {
			boostGold.put(g, boostGold.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Elixir")) {
			boostElixir.put(g, boostElixir.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Officers")) {
			boostOfficers.put(g, boostOfficers.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Recruits")) {
			boostRecruits.put(g, boostRecruits.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Pure")) {
			boostPure.put(g, boostPure.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Dark")) {
			boostDark.put(g, boostDark.get(g)+levels);
		} else if (boost.equalsIgnoreCase("Damage")) {
			boostDamage.put(g, boostDamage.get(g)+levels);
		}
	}
	
	public static int getMaxOfficers(String g) {
		return getRawBoost(g, "Officers");
	}
	
	public static int getMaxRecruits(String g) {
		return getRawBoost(g, "Recruits");
	}

	///////////////////////////////////////////////////////////////

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (hasGang(p)) {
			String g = getGang(p);
			if (WorldGuardPlugin.inst().canBuild(p, e.getBlock())) {
				String u = p.getUniqueId().toString();
				int x = 0;
				broken.put(g, getBroken(g) + 1);
				List<String> l = new ArrayList<String>();
				l.addAll(brokenBreakdown.get(g));
				if (l.size() > 0) {
					for (String s : l) {
						String[] str = s.split(";");
						if (str[0].equals(u)) {
							x = Integer.valueOf(str[1]);
							l.remove(s);
							break;
						}
					}
				}
				x++;
				l.add(u + ";" + x);
				brokenBreakdown.put(g, l);
				levelup.put(g, getLevelupProgress(g)+1);
				if (getLevelupProgress(g)>= getLevelupCost(g)) {
					levelup.put(g, 0);
					levelupGang(g);
					addPoints(g, 1);
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (Bukkit.getOnlinePlayers().size() > 0) {
			if (GangGUI.open.size() > 0) {
				for (Player pl : GangGUI.open.keySet()) {
					if (GangGUI.open.get(p).equals("invite")) {
						Inventory inv = (Inventory) pl.getOpenInventory();
						if (inv.firstEmpty() != -1) {
							String pn = p.getName();
							ItemStack i = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
							SkullMeta sm = (SkullMeta) i.getItemMeta();
							List<String> l = new ArrayList<String>();
							l.add(Core.color("&7"));
							sm.setOwner(pn);
							if (Gangs.hasGang(p)) {
								String pg = Gangs.getGang(p);
								if (Gangs.isOwner(p, pg)) {
									sm.setDisplayName(Core.color("&c" + pn));
									l.add(Core.color("&8 &l*&c " + pg + " Owner"));
								} else if (Gangs.isOfficer(p, pg)) {
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
					}
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Bukkit.getOnlinePlayers().size() > 0) {
			if (GangGUI.open.size() > 0) {
				for (Player pl : GangGUI.open.keySet()) {
					if (GangGUI.open.containsKey(pl) && GangGUI.open.get(pl).equals("invite")) {
						Inventory inv = (Inventory) pl.getOpenInventory();
						for (int x = 9; x < 36; x++) {
							ItemStack i = inv.getItem(x);
							if (i != null) {
								if (((SkullMeta) inv.getItem(x).getItemMeta()).getOwner().equals(p.getName())) {
									inv.removeItem(inv.getItem(x));
								}
							}
						}
					}
				}
			}
		}
	}
}
