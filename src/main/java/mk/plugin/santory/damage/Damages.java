package mk.plugin.santory.damage;

import java.util.HashMap;
import java.util.Map;

import mk.plugin.santory.event.PlayerDamagedEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

import mk.plugin.santory.main.SantoryCore;

public class Damages {
	
	public static final String DAMAGE_TAG = "satory.damage";
	
	private static final Map<LivingEntity, Long> damaged = new HashMap<LivingEntity, Long> ();
	
	public static void damage(Player player, LivingEntity target, Damage damage, int tickDelay) {
		// Check delay
		if (isDelayed(target)) return;
		
		// Add tag
		target.setMetadata(DAMAGE_TAG, new FixedMetadataValue(SantoryCore.get(), damage));
		target.damage(damage.getValue(), player);
		
		if (tickDelay > 0) {
			if (damaged.containsKey(target)) {
				if (damaged.get(target) > System.currentTimeMillis()) return;
			}
			damaged.put(target, System.currentTimeMillis() + (tickDelay * 1000 / 20));
		}

		// Event
		Bukkit.getPluginManager().callEvent(new PlayerDamagedEntityEvent(player, target, damage.getValue(), damage.getType()));
	}
	
	public static boolean isDelayed(LivingEntity target) {
		if (!damaged.containsKey(target)) return false;
		return damaged.get(target) > System.currentTimeMillis();
	}
	
	public static boolean hasDamage(LivingEntity target) {
		return target.hasMetadata(DAMAGE_TAG);
	}
	
	public static Damage getDamage(LivingEntity target) {
		if (!hasDamage(target)) return null;
		return (Damage) target.getMetadata(DAMAGE_TAG).get(0).value();
	}
	
	public static void removeDamage(LivingEntity target) {
		target.removeMetadata(DAMAGE_TAG, SantoryCore.get());
	}
	
	public static void setProjectileDamage(Projectile pj, Damage damage) {
		pj.setMetadata(DAMAGE_TAG, new FixedMetadataValue(SantoryCore.get(), damage));
	}
	
	public static boolean hasProjectileDamage(Projectile pj) {
		return pj.hasMetadata(DAMAGE_TAG);
	}
	
	public static Damage getProjectileDamage(Projectile pj) {
		if (!hasProjectileDamage(pj)) return null;
		return (Damage) pj.getMetadata(DAMAGE_TAG).get(0).value();
	}
	
}
