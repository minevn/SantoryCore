package mk.plugin.santory.listener;

import java.util.UUID;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
		int id = e.getEntity().getEntityId();
		Mobs.remove(id);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMobDamage(EntityDamageByEntityEvent e) {
		LivingEntity damager = null;

		if (e.getDamager() instanceof Projectile) {
			if (((Projectile) e.getDamager()).getShooter() instanceof LivingEntity) {
				damager = (LivingEntity) ((Projectile) e.getDamager()).getShooter();
			}
		}
		else if (e.getDamager() instanceof LivingEntity) {
			damager = (LivingEntity) e.getDamager();
 		}
		if (damager == null) return;

		int id = damager.getEntityId();

		Mob mob = Mobs.get(id);
		if (mob == null) {
			if (MythicMobs.inst().getAPIHelper().isMythicMob(damager)) {
				var mmID = MythicMobs.inst().getAPIHelper().getMythicMobInstance(damager).getMobType();
				if (!Configs.isMob(mmID)) return;

				LivingEntity entity = (LivingEntity) damager;
				int level = Configs.getLevel(mmID);
				MobType type = Configs.getType(mmID);
				Mobs.set(entity, type, level, false);
				mob = Mobs.get(id);
			}
			else return;
		}
		
		double d = Stat.DAMAGE.pointsToValue(mob.getStat(Stat.DAMAGE)) * mob.getDamageMulti();
		mob.setDamageMulti(1);

		e.setDamage(d);
	}
	
}
