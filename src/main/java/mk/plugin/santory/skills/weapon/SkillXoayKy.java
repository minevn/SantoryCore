package mk.plugin.santory.skills.weapon;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.slave.Slaves;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;

public class SkillXoayKy implements SkillExecutor {

    @Override
    public void start(Map<String, Object> map) {
        String slaveID = (String) map.get("slave");
        Player player = Slaves.getMasterPlayer(slaveID);
        LivingEntity slave = Slaves.getSlaveEntity(slaveID);
        int level = (int) map.get("level");
        double basedamage = Slaves.getDamage(slaveID);
        boolean damageUp1 = level >= 2;
        boolean knockback = level >= 3;
        boolean damageUp2 = level >= 4;
        boolean fatal = level >= 5;

        double d = basedamage;
        if (damageUp1) d += basedamage * 0.5;
        if (damageUp2) d += basedamage * 0.5;

        double damage = d;
        slave.getWorld().playSound(slave.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        BukkitRunnable br = new BukkitRunnable() {
            int i = 0;
            double minR = 1.5;
            int amount = 15;
            @Override
            public void run() {
                for (int k = 3 * i ; k < 3 * (i+1) ; k++) {
                    for (int j = 0 ; j < amount ; j ++) {
                        Location l = slave.getLocation().clone();
                        double angle = Math.PI * 2 / (20 + j * 0.2) * k * 3;

                        double newX = l.getX() + (minR + j * 0.1) * Math.sin(angle + l.getYaw() * -1);
                        double newZ = l.getZ() + (minR + j * 0.1) * Math.cos(angle + l.getYaw() * -1);

                        l.setX(newX);
                        l.setZ(newZ);
                        l.setY(l.getY() + 0.6);

                        player.getWorld().spawnParticle(Particle.CRIT, l, 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.CRIT_MAGIC, l, 1, 0, 0, 0, 0);
                    }
                }


                i++;
                if (i * 3 > amount) {
                    this.cancel();
                    Bukkit.getScheduler().runTask(SantoryCore.get(), () -> {
                        slave.getNearbyEntities(3, 3, 3).forEach(e -> {
                            if (e != player && e != slave && e instanceof LivingEntity) {
                                LivingEntity le = (LivingEntity) e;
                                if (!Utils.canAttack(e)) return;
                                Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
                                if (knockback) le.setVelocity(le.getLocation().subtract(slave.getLocation()).toVector().normalize().add(new Vector(0, 0.4, 0)));
                                if (fatal) {
                                    Utils.setGod(slave, 5000);
                                    slave.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                                    player.getWorld().spawnParticle(Particle.FLAME, slave.getLocation(), 30, 0, 0, 0, 0.2);
                                }
                            }
                        });
                    });
                }
            }
        };
        br.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
    }


}
