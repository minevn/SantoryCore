package mk.plugin.santory.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.mob.Mob;
import mk.plugin.santory.mob.MobType;
import mk.plugin.santory.mob.Mobs;
import mk.plugin.santory.stat.Stat;

public class MobListener implements Listener {
	
	@EventHandler
	public void onSpawn(MythicMobSpawnEvent e) {
		String id = e.getMobType().getInternalName();
		if (!Configs.isMob(id)) return;
		
		LivingEntity entity = (LivingEntity) e.getEntity();
		int level = Configs.getLevel(id);
		MobType type = Configs.getType(id);
	
		Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
			Mobs.set(entity, type, level);
		});
	}
	
	@EventHandler
	public void onDead(EntityDeathEvent e) {
		UUID uuid = e.getEntity().getUniqueId();
		Mobs.remove(uuid);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMobDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity().getType() != EntityType.PLAYER) return;
		if (e.getDamager().getType() == EntityType.PLAYER) return;
		
		UUID id = e.getDamager().getUniqueId();
		Mob mob = Mobs.get(id);
		if (mob == null) return;
		
		double d = Stat.DAMAGE.pointsToValue(mob.getStat(Stat.DAMAGE)) * mob.getDamageMulti();
		mob.setDamageMulti(1);

		e.setDamage(d);
	}
	
}
