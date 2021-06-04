package mk.plugin.santory.mob;

import com.google.common.collect.Maps;
import mk.plugin.santory.stat.Stat;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Map;

public class Mobs {
	
	private static final Map<Integer, Mob> mobs = Maps.newHashMap();

	public static Map<Integer, Mob> gets() {
		return mobs;
	}

	public static Mob get(Integer id) {
		return mobs.getOrDefault(id, null);
	}
	
	public static void remove(Integer id) {
		mobs.remove(id);
	}
	
	public static void removeDamageMulti(Entity e) {
		Mob m = get(e.getEntityId());
		m.setDamageMulti(1);
	}
	
	public static void setDamageMulti(Entity e, float multi) {
		Mob m = get(e.getEntityId());
		m.setDamageMulti(multi);
	}
	
	public static float getDamageMulti(Entity e) {
		Mob m = get(e.getEntityId());
		return m.getDamageMulti();
	}

	public static Mob set(LivingEntity e, MobType type, int level) {
		return set(e, type, level, true);
	}

	public static Mob set(LivingEntity e, MobType type, int level, boolean setStat) {
		Mob mob = new Mob(e.getEntityId(), type, level, type.getStats(level));

		if (setStat) {
			// Set health
			double hp = Stat.HEALTH.pointsToValue(mob.getStat(Stat.HEALTH));
			e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
			e.setHealth(hp);
		}
		
		mobs.put(e.getEntityId(), mob);
		
		return mob;
	}



}
