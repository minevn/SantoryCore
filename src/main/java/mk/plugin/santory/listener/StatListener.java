package mk.plugin.santory.listener;

import com.google.common.collect.Lists;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.hologram.Holograms;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.mob.Mob;
import mk.plugin.santory.mob.Mobs;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class StatListener implements Listener {

	/*
	Newbie Protection:
	- No PvP
	- 90% PvE Damage Reduction
	 */

	private final int ENTITY_DEFAULT_DEFENSE = 0;
	private final int LEVEL_PROTECTION = 10;
	
	// Player get damaged by entity not player
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamagedByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		if (e.getDamager() instanceof Player) return;
		if (!(e.getDamager() instanceof LivingEntity)) return;
		
		Player player = (Player) e.getEntity();
		LivingEntity entity = (LivingEntity) e.getDamager();
		
		double ne = Travelers.getStatValue(player, Stat.DODGE);
		if (Utils.rate(ne)) {
			Holograms.hologram(SantoryCore.get(), "§a§oNé", 15, player, entity, 1);
			e.setCancelled(true);
			return;
		}

		var damage = e.getDamage();

		double sucThu = Travelers.getStatValue(player, Stat.DEFENSE);
		damage = e.getDamage() * (1 - sucThu * 0.01);

		if (Configs.getNewbieProtectionWorlds().contains(entity.getLocation().getWorld().getName())) {
			if (player.getLevel() <= LEVEL_PROTECTION) {
				player.sendActionBar("§aĐược giảm 90% sát thương từ quái");
				damage = damage * 0.1;
			}
		}

		e.setDamage(damage);
	}
	
	// Player damage entity
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDamageEntity(EntityDamageByEntityEvent e) {
		// Player Damage entity
		if (e.getDamager() instanceof Player || e.getDamager() instanceof Projectile) {
			if (e.getEntity() instanceof LivingEntity) {
				// Projectile
				boolean isProjectileDamage = false;
				
				Damage d = null;
				double damage = 0;

				// Get player damager
				Player p;
				if (e.getDamager() instanceof Projectile) {
					Projectile a = (Projectile) e.getDamager();

					if (a.getShooter() instanceof Player) {
						p = (Player) a.getShooter();

						// Check projectile
						if (!Damages.hasProjectileDamage(a)) return;
						isProjectileDamage = true;
						d = Damages.getProjectileDamage(a);
						damage = d.getValue();
						if (a instanceof Arrow) {
							a.setBounce(false);
						}
						
						// Check if shooter = entity
						if (p == e.getEntity()) {
							e.setCancelled(true);
							return;
						}

					} else return;
				} else p = (Player) e.getDamager();
				Player player = p;

				LivingEntity entity = (LivingEntity) e.getEntity();

				// Check
				if (!Utils.canAttack(entity)) {
					e.setCancelled(true);
					return;
				}

				// Slave
				if (Slaves.hasSlave(player)) {
					if (Slaves.isMaster(player, entity)) {
						e.setCancelled(true);
						return;
					}
				}

				// Check god
				if (Utils.isGod(entity)) {
					e.setCancelled(true);
					return;
				}

				// Check delay
				if (Damages.isDelayed(entity)) {
					e.setCancelled(true);
					return;
				}

				// Check if not projectile
				if (!isProjectileDamage) {
					// Check is SkyCore damage
					if (!Damages.hasDamage(entity)) {
						e.setCancelled(true);
						return;
					}
					d = Damages.getDamage(entity);
					Damages.removeDamage(entity);
					damage = d.getValue();
				}

				// Hologram list
				List<String> holos = Lists.newArrayList();

				boolean crit = false;
				// Check if attack
				if (d.getType() == DamageType.ATTACK) {
					
					// Chi mang
					double critChance = Travelers.getStatValue(player, Stat.CRIT_CHANCE);
					if (Utils.rate(critChance)) {
						crit = true;
						damage *= 2;
						
						// Effet Crit
						Bukkit.getScheduler().runTaskAsynchronously(SantoryCore.get(), () -> {
							Location loc = entity.getLocation();
							loc.getWorld().spawnParticle(Particle.CLOUD, loc.clone().add(0, 1.5, 0.0), 7);
							player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1f);
						});
					}
					
					// If target is player
					if (e.getEntity() instanceof Player) {
						Player target = (Player) e.getEntity();
						
						// Stat Ne
						if (Utils.rate(Travelers.getStatValue(target, Stat.DODGE))) {
							Location loc = player.getLocation();
							loc.add(loc.getDirection().multiply(1.3f));
							Utils.hologram(Utils.ranLoc(loc, 1), "§2§lNé", 15, target);
							Utils.hologram(Utils.ranLoc(loc, 1), "§c§lNé", 15, player);
							e.setCancelled(true);
							return;
						}
						
					}					
				}
				// End attack

				// Check if PvP
				if (entity instanceof Player) {
					// only 50% damage
					damage *= 0.5;

					// No Newbie PvP
					if (Configs.getNewbieProtectionWorlds().contains(entity.getLocation().getWorld().getName())) {
						var target = (Player) entity;
						if (target.getLevel() <= 10 || player.getLevel() <= 10) {
							e.setCancelled(true);
							player.sendMessage("§cNgười chơi mới không thể PvP (Cấp <10)");
							return;
						}
					}
				}

				// Suc thu, xuyen giap
				if (!(entity instanceof Player)) {
					double defenseValue = ENTITY_DEFAULT_DEFENSE;
					Mob mob = Mobs.get(entity.getEntityId());
					if (mob != null) {
						defenseValue = Stat.DEFENSE.pointsToValue(mob.getStat(Stat.DEFENSE));
					}
					damage = damage * (1 - (defenseValue / 100));
				}

				// End attack
				int dtick = entity.getNoDamageTicks();
				entity.setNoDamageTicks(0);

				// FINAL damage
				e.setDamage(damage);

				// Check dead bug
				if (!(entity instanceof Player) && entity.getHealth() < damage) {
					Tasks.sync(() -> {
						if (!entity.isDead()) entity.remove();
 					}, 20);
				}
				
				// Holograms
				holos.add(0, crit ? "§6§l§o-" + Utils.round(damage) : "§c§l§o-" + Utils.round(damage));

				// After damage
				double lastDamage = damage;
				double lastHealth = entity.getHealth();
				Damage finalD = d;
				new BukkitRunnable() {
					@Override
					public void run() {
						entity.setNoDamageTicks(dtick);

						// Life steal for attack only
						if (finalD.getType() == DamageType.ATTACK) {
							double value = Travelers.getStatValue(player, Stat.LIFE_STEAL);
							Utils.addHealth(player, lastDamage * value / 100);
						}

						// Holograms
						Holograms.hologram(SantoryCore.get(), holos, 15, player, entity, 1);

						// Armor damage
						double realDamage = lastHealth - entity.getHealth();

						// Statistic
						player.setStatistic(Statistic.DAMAGE_DEALT, player.getStatistic(Statistic.DAMAGE_DEALT) + new Double(realDamage).intValue());
						if (e.getEntity() instanceof Player) {
							Player target = (Player) e.getEntity();
							target.setStatistic(Statistic.DAMAGE_TAKEN, target.getStatistic(Statistic.DAMAGE_TAKEN) + new Double(realDamage).intValue());
						}
					}
				}.runTask(SantoryCore.get());
			}
			
			// End event check
		}
		
	}
	
}
