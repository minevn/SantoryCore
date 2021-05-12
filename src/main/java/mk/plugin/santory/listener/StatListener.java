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
	
	private final int ENTITY_DEFAULT_DEFENSE = 0;
	
	// Player damaged armor
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamaged(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player == false) return;
		Player player = (Player) e.getEntity();
		
		// No break in world pvp
		if (Configs.isPvPWorld(player.getWorld())) return;
		
		// Durability
//		double lastHealth = ((LivingEntity) e.getEntity()).getHealth();
//		Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
//			double realDamage = lastHealth - player.getHealth();
//			if (e.getEntity() instanceof Player) {
//				ItemStack[] armors =  player.getInventory().getArmorContents();
//				for (int i = 0 ; i < armors.length ; i++) {
//					ItemStack is = armors[i];
//					if (Items.is(is) && realDamage > 5) {
//						Item item = Items.read(is);
//						ItemData data = item.getData();
//						if (data.getDurability() == 0) {
//							player.sendMessage("§cMột giáp của bạn có độ bền bằng 0 và mất tác dụng, hãy đi sửa chữa");
//							continue;
//						}
//						data.setDurability(Math.max(0, data.getDurability() - 1));
//						armors[i] = Items.write(player, is, item);
//
//					}
//				}
//				player.getInventory().setArmorContents(armors);
//			}
//		});
	}
	
	// Player get damaged by entity not player
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamagedByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player == false) return;
		if (e.getDamager() instanceof Player || e.getDamager() instanceof LivingEntity == false) return;
		
		Player player = (Player) e.getEntity();
		LivingEntity entity = (LivingEntity) e.getDamager();
		
		double ne = Travelers.getStatValue(player, Stat.DODGE);
		if (Utils.rate(ne)) {
			Holograms.hologram(SantoryCore.get(), "§a§oNé", 15, player, entity, 1);
			e.setCancelled(true);
			return;
		}
		
		double sucThu = Travelers.getStatValue(player, Stat.DEFENSE);
		e.setDamage(e.getDamage() * (1 - sucThu * 0.01));
	}
	
	// Player damage entity
	@EventHandler(priority = EventPriority.HIGHEST)
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
					
					// Suc thu, xuyen giap
					if (!(entity instanceof Player)) {
						double defenseValue = ENTITY_DEFAULT_DEFENSE;
						Mob mob = Mobs.get(entity.getUniqueId());
						if (mob != null) {
							defenseValue = Stat.DEFENSE.pointsToValue(mob.getStat(Stat.DEFENSE));
						}
						damage = damage * (1 - (defenseValue / 100));
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

				// Check world pvp
				if (Configs.isPvPWorld(player.getWorld())) {
					// only 25% damage
					damage *= 0.25;
				}
				
				// End attack
				int dtick = entity.getNoDamageTicks();
				entity.setNoDamageTicks(0);
				entity.setMaximumNoDamageTicks(0);
				e.setDamage(damage);
				
				// Holograms
				holos.add(0, crit ? "§6§l§o-" + Utils.round(damage) : "§c§l§o-" + Utils.round(damage));

				// After damage
				double lastDamage = damage;
				double lastHealth = ((LivingEntity) e.getEntity()).getHealth();
				new BukkitRunnable() {
					@Override
					public void run() {
						entity.setNoDamageTicks(dtick);
						entity.setMaximumNoDamageTicks(dtick);
						// Hut mau
						double value = Travelers.getStatValue(player, Stat.LIFE_STEAL);
						Utils.addHealth(player, lastDamage * value / 100);
						
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
