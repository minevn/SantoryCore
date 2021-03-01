package mk.plugin.santory.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.hologram.Holograms;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemData;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.weapon.WeaponType;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {
	
	public static void circleParticles(Particle particle, Location location, double radius) {
		int amount = new Double(radius * 20).intValue();
		double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        
        for (int i = 0 ; i < amount ; i++) {
            double angle = i * increment;
            double x = location.getX() + (radius * Math.cos(angle));
            double z = location.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(location.getWorld(), x, location.getY(), z));
        }
        
        for (Location l : locations) {
//        	ParticleAPI.sendParticle(e, l, 0, 0, 0, 0, 1);
        	location.getWorld().spawnParticle(particle, l, 1, 0, 0, 0, 0);
        }
	}
	
	public static Location getLandedLocation(Location l) {
		int j = 0;
		Location temp = l.clone();
		while (temp.getBlock().getType() == Material.AIR) {
			j++;
			if (j > 100) {
				return null;
			}
			temp = temp.add(0,-1,0);
		}
		return temp;
	}
	
	@SuppressWarnings("deprecation")
	public static short getColor(DyeColor color) {
		return color.getWoolData();
	}
	
	public static ItemStack getColoredSlot(DyeColor color) {
		ItemStack other = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		other.setDurability(getColor(color));
		ItemMeta meta = other.getItemMeta();
		meta.setDisplayName(" ");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		other.setItemMeta(meta);
		return other;
	}
	
	public static ItemStack getBlackSlot() {
		ItemStack other = new ItemStack(Material.STAINED_GLASS_PANE, 1);
		other.setDurability((short) 15);
		ItemMeta meta = other.getItemMeta();
		meta.setDisplayName(" ");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		other.setItemMeta(meta);
		return other;
	}
	
	public static ItemStack getTieredIcon(Tier tier) {
		switch (tier) {
			case COMMON: return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8);
			case UNCOMMON: return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
			case RARE: return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
			case EPIC: return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
			case LEGEND: return new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
		}
		return null;
	}
	
	public static Color readColor(String s) {
		if (s == null) return null;
		int red = Integer.valueOf(s.split(";")[0]);
		int green = Integer.valueOf(s.split(";")[1]);
		int blue = Integer.valueOf(s.split(";")[2]);
		return Color.fromRGB(red, green, blue);
	}
	
	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);

			String hashtext;
			for (hashtext = number.toString(16); hashtext.length() < 32; hashtext = "0" + hashtext) {
			}

			return hashtext;
		} catch (NoSuchAlgorithmException var5) {
			throw new RuntimeException(var5);
		}
	}
	
	public static UUID getUUIDFromString(String s) {
		String md5 = getMD5(s);
		String uuid = md5.substring(0, 8) + "-" + md5.substring(8, 12) + "-" + md5.substring(12, 16) + "-"
				+ md5.substring(16, 20) + "-" + md5.substring(20);
		return UUID.fromString(uuid);
	}
	
	public static ItemMeta buildSkull(SkullMeta meta, String texture) {
		GameProfile profile;
		Field profileField;
		profile = new GameProfile(getUUIDFromString(texture), null);
		profile.getProperties().put("textures", new Property("textures", texture));
		profileField = null;

		try {
			profileField = meta.getClass().getDeclaredField("profile");
		} catch (SecurityException | NoSuchFieldException var8) {
			var8.printStackTrace();
		}

		profileField.setAccessible(true);

		try {
			profileField.set(meta, profile);
		} catch (IllegalAccessException | IllegalArgumentException var7) {
			var7.printStackTrace();
		}

		return meta;
	}
	
	public static long calPower(Player player) {
		Traveler t = Travelers.get(player);
		int power = 0; 
		if (t == null) return power;
		for (Stat stat : Stat.values()) {
			power += t.getState().getStat(player, stat) * 56; 
		}
		
		return power;
	}
	
	public static List<LivingEntity> getLivingEntities(Player player, Location location, double x, double y, double z) {
		List<LivingEntity> list = Lists.newArrayList();
		location.getWorld().getNearbyEntities(location, 5, 5, 5).stream()
				.filter(e -> e instanceof LivingEntity && e != player).collect(Collectors.toList()).forEach(e -> {
					list.add((LivingEntity) e);
				});
		return list;
	}
	
	public static double random(double min, double max) {
		return (new Random().nextInt(new Double((max - min) * 1000).intValue()) + min * 1000) / 1000;
	}

	public static int randomInt(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}
	
	public static int getStatOfItem(Item item, Stat stat) {
		ItemData data = item.getData();
		int base = data.getStat(stat);
		if (base == 0) return base;
		return Double.valueOf(base * (1 + Items.ASCENT_BONUS * (data.getAscent().getValue() - 1) )).intValue() + Items.ENHANCE_BONUS * data.getLevel();
	}
	
	public static List<String> toList(String s, int length, String start) {
		List<String> result = new ArrayList<String>();
		if (s == null)
			return result;
		if (!s.contains(" ")) {
			result.add(start + s);
			return result;
		}

		String[] words = s.split(" ");
		int l = 0;
		String line = "";
		for (int i = 0; i < words.length; i++) {
			l += words[i].length();
			if (l > length) {
				result.add(line.substring(0, line.length() - 1));
				l = words[i].length();
				line = "";
				line += words[i] + " ";
			} else {
				line += words[i] + " ";
			}
		}

		if (!line.equalsIgnoreCase(" "))
			result.add(line);

		for (int i = 0; i < result.size(); i++) {
			result.set(i, start + result.get(i));
		}

		return result;
	}
	
	public static String toStars(Grade tier) {
		String s = "";
		String star = "⭒";
		for (Grade t : Grade.values()) {
			if (t.getValue() > tier.getValue()) s += "§7" + star;
			else s += "§e" + star;
		}
		return s;
	}
	
	public static String toStars(Ascent tier) {
		String s = "";
		String star = "✝"; 
		for (int i = 0 ; i < tier.getValue() ; i++) s += "§a" + star;
		for (int i = tier.getValue() ; i < Ascent.values().length ; i++) s += "§7" + star;

		return s;
	}
	
	public static void addHealth(Player player, double amount) {
		double currentHealth = player.getHealth();
		double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (player.isDead())
			return;
		player.setHealth(Math.min(maxHealth, Math.max(0, Math.min(currentHealth + amount, maxHealth))));
	}
	
	public static void hologram(Location location, String message, int tick, Player player) {
		Holograms.hologram(SantoryCore.get(), location, message, tick, player);
	}
	
	public static Location ranLoc(Location loc, double max) {
		Vector direct1 = loc.getDirection().clone().setY(0);
		Vector direct2 = direct1.clone().setX(direct1.getZ()).setZ(direct1.getX() * -1f);

		double ranY = (new Random().nextInt(new Double(max * 1000).intValue()) - max * 500) / 1000;
		double ranM = (new Random().nextInt(new Double((max * 1000)).intValue()) - max / 2 * 1000) / 1000;
		Location result = loc.clone();
		result.setY(ranY + loc.getY());
		result.add(direct2.multiply(ranM));

		return result;
	}

	
	public static void setGod(Entity entity, long milis) {
		entity.setMetadata("entity-God", new FixedMetadataValue(SantoryCore.get(), System.currentTimeMillis() + milis));
	}
	
	public static boolean isGod(Entity entity) {
		if (entity.hasMetadata("entity-God")) {
			boolean god = entity.getMetadata("entity-God").get(0).asLong() > System.currentTimeMillis();
			entity.removeMetadata("entity-God", SantoryCore.get());
			return god;
		}
		return false;
	}
	
	public static boolean canAttack(Entity e) {
		return !e.hasMetadata("NPC");
	}
	
	public static boolean rate(double chance) {
		if (chance >= 100)
			return true;
		double rate = chance * 100;
		int random = new Random().nextInt(10000);
		return random < rate;
	}
	
	public static double getRange(Item item) {
		return WeaponType.valueOf(Configs.getModel(item.getModelID()).getMetadata().get("weapon-type")).getRange();
	}
	
	public static double round(double i) {
		return Double.valueOf(new DecimalFormat("#.##").format(i).replace(",", "."));
	}
	
	public static LivingEntity getTarget(Player source, double range) {
		List<Block> blocksInSight = source.getLineOfSight(Sets.newHashSet(Material.AIR), Double.valueOf(range).intValue());
		List<Entity> nearEntities = source.getNearbyEntities(range, range, range);
		
		if (blocksInSight != null && nearEntities != null) {
			for (Block block : blocksInSight) {
				int xBlock = block.getX();
				int yBlock = block.getY();
				int zBlock = block.getZ();

				for (Entity entity : nearEntities) {
					if (!(entity instanceof LivingEntity)) continue;
					Location entityLocation = entity.getLocation();
					int xEntity = entityLocation.getBlockX();
					int yEntity = entityLocation.getBlockY();
					int zEntity = entityLocation.getBlockZ();
					if (xEntity == xBlock && (Math.abs(yBlock - yEntity) < 2) && zEntity == zBlock) {
						return (LivingEntity) entity;
					}
					
				}
			}
		}
		return null;
	}
	
}
