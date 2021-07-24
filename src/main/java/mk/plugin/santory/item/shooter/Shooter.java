package mk.plugin.santory.item.shooter;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public enum Shooter {
	
	BOW {
		@Override
		public Object shoot(Player player, Damage damage, Vector v, Location location) {
			Arrow arrow = null;

			if (location == null) arrow = player.launchProjectile(Arrow.class, v);
			else {
				arrow = player.getWorld().spawnArrow(location, v, 1, 0);
			}

			arrow.setShooter(player);
			arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);

			Damages.setProjectileDamage(arrow, damage);
			player.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1, 1);
			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), arrow::remove, 20);

			return arrow;
		}

		@Override
		public boolean checkRequirements(Player player) {
			// Check has arrows
			boolean has = false;
			ItemStack[] contents = player.getInventory().getContents();
			for (int i = 0 ; i < contents.length ; i++) {
				ItemStack item = contents[i];
				if (item != null && item.isSimilar(new ItemStack(Material.ARROW))) {
					if (item.getAmount() == 1) contents[i] = null;
					else item.setAmount(item.getAmount() - 1);
					player.getInventory().setContents(contents);
					has = true;
					break;
				}
			}
			if (!has) {
				player.sendMessage("§cBạn cần có mũi tên trong người!");
				return false;
			}

			return true;
		}
	},

	WAND {
		@Override
		public Object shoot(Player player, Damage damage, Vector v, Location location) {
			Location main = null;
			if (location == null) main = player.getLocation().add(0, 1.7, 0).add(player.getLocation().getDirection().multiply(3));
			else main = location;

			main.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1, 1);
			Location finalMain = main;
			new BukkitRunnable() {
				int i = 0;
				@Override
				public void run() {
					i++;

					var l = finalMain.clone().add(v.clone().multiply((double) i / 3)).add(0, -0.06 * Math.pow((double) i / 3, 2), 0);
					l.getWorld().spawnParticle(Particle.ASH, l, 5, 0.1f, 0.1f, 0.1f, 0f);
					l.getWorld().spawnParticle(Particle.CRIT, l, 5, 0.1f, 0.1f, 0.1f, 0f);
					l.getWorld().spawnParticle(Particle.CRIT_MAGIC, l, 5, 0.1f, 0.1f, 0.1f, 0f);

					var isBlock = l.getBlock().isSolid();
					var collideEntity = false;
					for (Entity e : l.getWorld().getEntities()) {
						if (e != player && e instanceof LivingEntity && e.getLocation().clone().add(0, 1, 0).distanceSquared(l) <= 2) {
							collideEntity = true;
							break;
						}
					}

					// Boom
					if (i == 25 || collideEntity || isBlock) {
						l.getWorld().spawnParticle(Particle.ASH, l, 20, 1f, 1f, 1f, 0f);
						l.getWorld().spawnParticle(Particle.CRIT, l, 20, 1f, 1f, 1f, 0f);
						l.getWorld().spawnParticle(Particle.CRIT_MAGIC, l, 20, 1f, 1f, 1f, 0f);
						Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
							// Damage
							for (Entity entity : l.getWorld().getEntities()) {
								if (!Utils.canAttack(entity)) continue;
								if (entity != player && entity instanceof LivingEntity && entity.getLocation().distanceSquared(l) < 9) {
									Damages.damage(player, (LivingEntity) entity, damage, 5);
								}
							}
						});

						this.cancel();
					}

				}
			}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);

			return null;
		}

		@Override
		public boolean checkRequirements(Player player) {
			return true;
		}
	};
	
	Shooter() {}
	
	public abstract Object shoot(Player player, Damage damage, Vector v, Location location);
	public abstract boolean checkRequirements(Player player);
	
}
