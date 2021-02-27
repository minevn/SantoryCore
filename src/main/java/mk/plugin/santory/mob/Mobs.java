package mk.plugin.santory.mob;

import java.util.Map;
import java.util.UUID;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.google.common.collect.Maps;

import mk.plugin.santory.stat.Stat;

public class Mobs {
	
	private static Map<UUID, Mob> mobs = Maps.newHashMap();
	
	public static Mob get(UUID id) {
		return mobs.getOrDefault(id, null);
	}
	
	public static void remove(UUID id) {
		mobs.remove(id);
	}
	
	public static void removeDamageMulti(Entity e) {
		Mob m = get(e.getUniqueId());
		m.setDamageMulti(1);
	}
	
	public static void setDamageMulti(Entity e, float multi) {
		Mob m = get(e.getUniqueId());
		m.setDamageMulti(multi);
	}
	
	public static float getDamageMulti(Entity e) {
		Mob m = get(e.getUniqueId());
		return m.getDamageMulti();
	}
	
	public static Mob set(LivingEntity e, MobType type, int level) {
		Mob mob = new Mob(e.getUniqueId(), type, level, type.getStats(level));
		
		// Set health
		double hp = Stat.HEALTH.pointsToValue(mob.getStat(Stat.HEALTH));
		e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
		e.setHealth(hp);
		
		mobs.put(e.getUniqueId(), mob);
		
		return mob;
	}
	
}
