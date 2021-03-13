package mk.plugin.santory.command;

import mk.plugin.santory.amulet.Amulet;
import mk.plugin.santory.artifact.Artifact;
import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.artifact.Artifacts;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.gui.GUI;
import mk.plugin.santory.gui.GUIs;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.modifty.ItemEnhances;
import mk.plugin.santory.item.modifty.ItemUpgrades;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.mob.Mobs;
import mk.plugin.santory.slave.*;
import mk.plugin.santory.slave.gui.SlaveSelectGUI;
import mk.plugin.santory.slave.item.SlaveFood;
import mk.plugin.santory.slave.item.SlaveStone;
import mk.plugin.santory.slave.master.Master;
import mk.plugin.santory.slave.master.Masters;
import mk.plugin.santory.traveler.*;
import mk.plugin.santory.wish.Wish;
import mk.plugin.santory.wish.WishRolls;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
				if (args[1].equalsIgnoreCase("setlevel")) {
					Player player = Bukkit.getPlayer(args[2]);
					int level = Integer.parseInt(args[3]);
					boolean allowdesc = args.length == 4 || Boolean.valueOf(args[4]);
					int nextlv = allowdesc ? level : Math.max(player.getLevel(), level);
					
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(TravelerOptions.getTotalExpTo(nextlv));
					Travelers.save(player.getName());
					
					sender.sendMessage("§aDone, Level set!");
				}
				
				else if (args[1].equalsIgnoreCase("setexp")) {
					String player = args[2];
					int exp = Integer.valueOf(args[3]);
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(exp);
					Travelers.save(player);
					sender.sendMessage("§aDone, Exp set!");
				}
				
				else if (args[1].equalsIgnoreCase("addexp")) {
					String player = args[2];
					int exp = Integer.valueOf(args[3]);
					Traveler t = Travelers.get(player);
					TravelerData td = t.getData();
					td.setExp(td.getExp() + exp);
					Travelers.save(player);
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
					player.getInventory().setItemInMainHand(Items.write(player, is, item));
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
					player.getInventory().setItemInMainHand(Items.write(player, is, item));
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
					player.getInventory().setItemInMainHand(Items.write(player, is, item));
					sender.sendMessage("§aDone!");
				}
				
				else if (args[0].equalsIgnoreCase("setdur")) {
					int dur = Integer.valueOf(args[2]);
					ItemStack is = player.getInventory().getItemInMainHand();
					Item item = Items.read(is);
					ItemData data = item.getData();
					data.setDurability(dur);
					Items.update(player, is, item);
					player.getInventory().setItemInMainHand(Items.write(player, is, item));
					sender.sendMessage("§aDone!");
				}
				
			}
			
			
			/*
			 * Wish commands
			 */
			else if (args[0].equalsIgnoreCase("wish")) {
				
				Player player = null;
				if (sender instanceof Player) player = (Player) sender;
				
				if (args[1].equalsIgnoreCase("roll")) {
					String id = args[2];
					if (args.length >= 4)	player = Bukkit.getPlayer(args[3]);
					Wish wish = Configs.getWish(id);
					WishRolls.roll(wish, player);
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
					player.getInventory().addItem(ItemUpgrades.get());
				}
				
				else if (args[1].equalsIgnoreCase("enstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(ItemEnhances.get());
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
				
				else if (args[1].equalsIgnoreCase("upstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(ItemUpgrades.get());
				}
				
				else if (args[1].equalsIgnoreCase("enstone")) {
					if (args.length >= 3) player = Bukkit.getPlayer(args[2]);
					player.getInventory().addItem(ItemEnhances.get());
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
		sender.sendMessage("");
		
		// Item commands
		sender.sendMessage("§6/santory item get <*id>");
		sender.sendMessage("§6/santory item setlevel <*level>");
		sender.sendMessage("§6/santory item setexp <*value>");
		sender.sendMessage("§6/santory item setdesc <*desc>");
		sender.sendMessage("§6/santory item setdur <*desc>");
		sender.sendMessage("");
		
		// Wish commands
		sender.sendMessage("§e/santory wish roll <*id> <player>");
		sender.sendMessage("");
		
		// GUI commands
		sender.sendMessage("§d/santory gui open <*id> <player>");
		sender.sendMessage("§d/santory gui see <*target> <viewer>");
		sender.sendMessage("");
		
		// Custom items
		sender.sendMessage("§9/santory custom amulet <*id> <player>");
		sender.sendMessage("§9/santory custom upstone <player>");
		sender.sendMessage("§9/santory custom enstone <player>");
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

		sender.sendMessage("§2§l=================================================");
		
	}

}
