package mk.plugin.santory.command;

import com.destroystokyo.paper.Title;
import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.gui.GUI;
import mk.plugin.santory.gui.GUIs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.modifty.ItemEnhances;
import mk.plugin.santory.item.modifty.upgrade.UpgradeStone;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.mob.Mobs;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.slave.gui.SlaveSelectGUI;
import mk.plugin.santory.slave.item.SlaveFood;
import mk.plugin.santory.slave.item.SlaveStone;
import mk.plugin.santory.slave.master.Master;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.traveler.*;
import mk.plugin.santory.wish.Wish;
import mk.plugin.santory.wish.WishRolls;
import mk.plugin.santory.wish.Wishes;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class AdminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		
		if (!sender.hasPermission("santory.admin")) {
			sender.sendMessage("§cNo permisison");
			return false;
		}
		
		try {
			
			if (args[0].equalsIgnoreCase("reload")) {
				Configs.reload(SantoryCore.get());
				sender.sendMessage("§aConfig reloaded!");
				return false;
			}

			else if (args[0].equalsIgnoreCase("setmodel")) {
				var model = Integer.parseInt(args[1]);
				var player = (Player) sender;
				var is = player.getInventory().getItemInMainHand();
				var meta = is.getItemMeta();
				meta.setCustomModelData(model);
				is.setItemMeta(meta);
				sender.sendMessage("§aAll done!");
			}
			
			/*
			 * Player commands
			 */
			if (args[0].equalsIgnoreCase("player")) {

				if (args[1].equalsIgnoreCase("tograde")) {
					Player player = Bukkit.getPlayer(args[2]);
					Grade g = Grade.valueOf(args[3]);
					var t = Travelers.get(player.getName());
					if (t.getData().getGrade().getValue() >= g.getValue()) return false;
					t.getData().setGrade(g);
					Travelers.save(player.getName());

					// Show
					player.sendTitle(new Title("§2§lTHĂNG BẬC " + g.name(), "§aGiới hạn cấp độ mới: " + g.getMaxLevel(), 10, 100, 10));
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

				}

				if (args[1].equalsIgnoreCase("setgrade")) {
					Player player = Bukkit.getPlayer(args[2]);
					Grade g = Grade.valueOf(args[3]);
					var t = Travelers.get(player.getName());
					t.getData().setGrade(g);
					Travelers.save(player.getName());

					// Show
					player.sendTitle(new Title("§2§lTHĂNG BẬC " + g.name(), "§aGiới hạn cấp độ mới: " + g.getMaxLevel(), 10, 100, 10));
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

				}

				else if (args[1].equalsIgnoreCase("setlevel")) {
					Player player = Bukkit.getPlayer(args[2]);
					int level = Integer.parseInt(args[3]);
					boolean allowdesc = args.length == 4 || Boolean.valueOf(args[4]);
					int nextlv = allowdesc ? level : Math.max(player.getLevel(), level);
					
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(TravelerOptions.getTotalExpTo(nextlv));
					Travelers.save(player.getName());
					Travelers.updateLevel(player);
					
					sender.sendMessage("§aDone, Level set!");
				}
				
				else if (args[1].equalsIgnoreCase("setexp")) {
					String player = args[2];
					int exp = Integer.valueOf(args[3]);
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(exp);
					Travelers.save(player);
					if (Bukkit.getPlayer(player) != null) Travelers.updateLevel(Bukkit.getPlayer(player));
					sender.sendMessage("§aDone, Exp set!");
				}
				
				else if (args[1].equalsIgnoreCase("addexp")) {
					String player = args[2];
					int exp = Integer.valueOf(args[3]);
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(td.getExp() + exp);
					Travelers.save(player);

					var p = Bukkit.getPlayer(player);
					if (p != null) {
						p.sendMessage("§a§l§o+" + exp + " Exp");
						p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
						Travelers.updateLevel(p);
					}

					sender.sendMessage("§aDone, Exp added!");
				}
				
				else if (args[1].equalsIgnoreCase("showstats")) {
					Player player = Bukkit.getPlayer(args[2]);
					Traveler t = Travelers.get(player);
					TravelerState ts = t.getState();
					player.sendMessage("");
					ts.getStats().forEach((stat, value) -> {
						player.sendMessage("§c" + stat.getName() + ": §7" + value);
					});
					player.sendMessage("");
				}
			}
			
			
			/*
			 * Item commands
			 */
			else if (args[0].equalsIgnoreCase("item")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				
				if (args[1].equalsIgnoreCase("get")) {
					String id = args[2];
					ItemStack is = Items.build(player, id);
					player.getInventory().addItem(is);
					sender.sendMessage("§aDone!");
				}
				
				else if (args[1].equalsIgnoreCase("setlevel")) {
					int level = Integer.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setLevel(level);
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}
				
				else if (args[1].equalsIgnoreCase("setexp")) {
					int exp = Integer.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setExp(exp);
					if (item.getModel().getType() == ItemType.ARTIFACT) {
						Artifact art = Artifact.parse(item.getModel());
						Artifacts.check(item, art);
					}
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}
				
				else if (args[1].equalsIgnoreCase("setdesc")) {
					String desc = args[2];
					for (int i = 3 ; i < args.length ; i++) {
						String space = i == args.length - 1 ? " " : "";
						desc = args[i] + space;
					}
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setDesc(desc);
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}
				
				else if (args[1].equalsIgnoreCase("setdur")) {
					int dur = Integer.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setDurability(dur);
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}

				else if (args[1].equalsIgnoreCase("setgrade")) {
					Grade g = Grade.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setExp(Configs.getExpRequires().get(g));
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}

				else if (args[1].equalsIgnoreCase("setascent")) {
					Ascent g = Ascent.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setAscent(g);
					Items.update(player, is, item);
					Items.write(player, is, item);
					player.updateInventory();
					sender.sendMessage("§aDone!");
				}

			}
			
			
			/*
			 * Wish commands
			 */
			else if (args[0].equalsIgnoreCase("wish")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				String id = args[2];

				if (args[1].equalsIgnoreCase("roll")) { ;
					if (args.length >= 4) player = Bukkit.getPlayer(args[3]);
					Wish wish = Configs.getWish(id);
					WishRolls.roll(wish, player);
				}

				else if (args[1].equalsIgnoreCase("getkey")) {
					player.getInventory().addItem(Wishes.buildKey(id));
				}

				else if (args[1].equalsIgnoreCase("setinsure")) {
					String pName = null;
					Tier tier = Tier.valueOf(args[3].toUpperCase());
					int amount = Integer.parseInt(args[4]);
					if (args.length >= 6) pName = args[5];
					else pName = sender.getName();

					var t = Travelers.get(pName);
					t.getData().getWish(id).setInsure(tier, amount);
					Travelers.save(pName);

					sender.sendMessage("§aDone!");
				}

			}
			
			/*
			 * GUI commands
			 */
			else if (args[0].equalsIgnoreCase("gui")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				
				if (args[1].equalsIgnoreCase("open")) {
					
					// Other GUIs
					if (args[2].equalsIgnoreCase("artifact")) {
						if (args.length >= 4) player = Bukkit.getPlayer(args[3]);
						ArtifactGUI.open(player);
						return false;
					}
					
					// System GUI
					GUI gui = GUI.valueOf(args[2].toUpperCase());
					if (args.length >= 4) player = Bukkit.getPlayer(args[3]);
					GUIs.open(player, gui);
				}

				else if (args[1].equalsIgnoreCase("see")) {
					Player target = Bukkit.getPlayer(args[2]);
					Player viewer = null;
					if (args.length > 3) viewer = Bukkit.getPlayer(args[3]);
					else viewer = (Player) sender;
					TravelerInfoGUI.open(viewer, target);
				}
			}
			
			/*
			 * Custom item commands
			 */
			else if (args[0].equalsIgnoreCase("custom")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				
				if (args[1].equalsIgnoreCase("amulet")) {
					Amulet a = Amulet.valueOf(args[2].toUpperCase());
					if (args.length >= 4) player = Bukkit.getPlayer(args[3]);
					player.getInventory().addItem(a.get());
				}
				
				else if (args[1].equalsIgnoreCase("upstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					for (var us : UpgradeStone.values()) {
						player.getInventory().addItem(us.build());
					}

				}
				
				else if (args[1].equalsIgnoreCase("enstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(ItemEnhances.get());
				}

				else if (args[1].equalsIgnoreCase("keepstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(Configs.getKeepStone());
				}

				else if (args[1].equalsIgnoreCase("globalspeaker")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(Configs.getGlobalSpeaker());
				}
			}
			
			/*
			 * Mobs commands
			 */
			else if (args[0].equalsIgnoreCase("mob")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				
				if (args[1].equalsIgnoreCase("damage")) {
					UUID mID = UUID.fromString(args[2]);
					UUID pID = UUID.fromString(args[3]);
					float multi = 1;
					if (args.length >= 5) multi = Float.valueOf(args[4]);
					Entity e = Bukkit.getEntity(mID);
					Player p = Bukkit.getPlayer(pID);
					
					Mobs.setDamageMulti(e, multi);
					p.damage(0, e);
				}

			}
			
			/*
			Slave commands
			 */
			else if (args[0].equalsIgnoreCase("slave")) {

				if (args[1].equalsIgnoreCase("add")) {
					Player player = (Player) sender;
					String mid = args[2];
					Slave slave = new Slave(mid, player.getName());
					Masters.add(player, slave);
					Masters.save(player);
					sender.sendMessage("Done!");
				}

				else if (args[1].equalsIgnoreCase("summon")) {
					Player player = (Player) sender;
					Master m = Masters.get(player);
					Slave slave = m.getCurrentSlave();
					if (slave == null) {
						sender.sendMessage("No current slave");
						return false;
					}
					Slaves.summonSlave(player, slave.getID());
				}

				else if (args[1].equalsIgnoreCase("setcurrent")) {
					Player player = (Player) sender;
					String modelID = args[2];
					Master m = Masters.get(player);
					m.setCurrentSlave(m.getSlaveFromModel(modelID));
					sender.sendMessage("OK all done!");
				}

				else if (args[1].equalsIgnoreCase("setlevel")) {
					Player player = (Player) sender;
					int level = Integer.valueOf(args[2]);
					Master m = Masters.get(player);
					Slave slave = m.getCurrentSlave();
					slave.getData().setLevel(level);
					sender.sendMessage("§aAll done!");
				}

				else if (args[1].equalsIgnoreCase("getfood")) {
					Player player = (Player) sender;
					SlaveFood food = SlaveFood.valueOf(args[2].toUpperCase());
					player.getInventory().addItem(food.build());
					sender.sendMessage("§aAll done motherfucker!");
				}

				else if (args[1].equalsIgnoreCase("addexp")) {
					Player player = (Player) sender;
					int exp = Integer.valueOf(args[2]);
					Master m = Masters.get(player);
					Slave slave = m.getCurrentSlave();
					slave.getData().setExp(slave.getData().getExp() + exp);
					sender.sendMessage("§aAll done!");
				}

				else if (args[1].equalsIgnoreCase("getstone")) {
					Player player = (Player) sender;
					SlaveStone st = SlaveStone.valueOf(args[2].toUpperCase());
					player.getInventory().addItem(st.build());
					sender.sendMessage("§aAll done motherfucker!");
				}

				else if (args[1].equalsIgnoreCase("gui")) {
					Player player = null;
					if (args.length > 2) player = Bukkit.getPlayer(args[1]);
					else player = (Player) sender;
					SlaveSelectGUI.open(player);
				}

			}

			/*
			Skin
			 */
			else if (args[0].equalsIgnoreCase("skin")) {
				if (args[1].equalsIgnoreCase("build")) {
					var id = args[2];
					var player = (Player) sender;
					player.getInventory().addItem(Skins.build(id));
				}
			}
			
		}
		catch (ArrayIndexOutOfBoundsException e) {
			sendHelp(sender);
		}
		
		return false;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage("§2§l=================================================");
		sender.sendMessage("");
		
		// System commands
		sender.sendMessage("§a/santory reload");
		sender.sendMessage("§a/santory setmodel <id>");
		sender.sendMessage("");
		
		// Player commands
		sender.sendMessage("§c/santory player setlevel <*player> <*level> <isDecreaseAllowed>");
		sender.sendMessage("§c/santory player setexp <*player> <*exp>");
		sender.sendMessage("§c/santory player addexp <*player> <*exp>");
		sender.sendMessage("§c/santory player showstats <*player>");
		sender.sendMessage("§c/santory player tograde <*player> <grade>");
		sender.sendMessage("§c/santory player setgrade <*player> <grade>");
		sender.sendMessage("");
		
		// Item commands
		sender.sendMessage("§6/santory item get <*id>");
		sender.sendMessage("§6/santory item setlevel <*level>");
		sender.sendMessage("§6/santory item setexp <*value>");
		sender.sendMessage("§6/santory item setdesc <*desc>");
		sender.sendMessage("§6/santory item setdur <*desc>");
		sender.sendMessage("§6/santory item setgrade <*grade>");
		sender.sendMessage("§6/santory item setascent <*ascent>");
		sender.sendMessage("");
		
		// Wish commands
		sender.sendMessage("§e/santory wish roll <*id> <player>");
		sender.sendMessage("§e/santory wish getkey <*id> <player>");
		sender.sendMessage("§e/santory wish setinsure <*id> <*tier> <*amount> <player>");
		sender.sendMessage("");
		
		// GUI commands
		sender.sendMessage("§d/santory gui open <*id> <player>");
		sender.sendMessage("§d/santory gui see <*target> <viewer>");
		sender.sendMessage("");
		
		// Custom items
		sender.sendMessage("§9/santory custom amulet <*id> <player>");
		sender.sendMessage("§9/santory custom upstone <player>");
		sender.sendMessage("§9/santory custom enstone <player>");
		sender.sendMessage("§9/santory custom keepstone <player>");
		sender.sendMessage("§9/santory custom globalspeaker <player>");
		sender.sendMessage("");
		
		// Mobs
		sender.sendMessage("§9/santory mob damage <*mob_uuid> <*player_uuid> <multi>");
		sender.sendMessage("");

		// Slaves
		sender.sendMessage("§a/santory slave add <*modelID>");
		sender.sendMessage("§a/santory slave summon");
		sender.sendMessage("§a/santory slave setcurrent <*modelID>");
		sender.sendMessage("§a/santory slave setlevel <*level>");
		sender.sendMessage("§a/santory slave getfood <*I/II/III>");
		sender.sendMessage("§a/santory slave addexp <*exp>");
		sender.sendMessage("§a/santory slave getstone <*I/II/III/IV/V>");
		sender.sendMessage("§a/santory slave gui <player>");

		// Skins
		sender.sendMessage("§a/santory skin build <id>");

		sender.sendMessage("§2§l=================================================");
		
	}

}
