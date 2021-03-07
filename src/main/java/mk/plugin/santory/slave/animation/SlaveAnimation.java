package mk.plugin.santory.slave.animation;

import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.slave.Slaves;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public enum SlaveAnimation {

    SUMMONED {
        @Override
        public void play(LivingEntity e, Player player) {
            Location l = e.getLocation();
            e.getWorld().playSound(l, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1, 1);
            e.getWorld().spawnParticle(Particle.SPIT, l, 20, 0.5, 0.5, 0.5);
        }
    },
    HAPPY {
        @Override
        public void play(LivingEntity le, Player player) {
            Location l = le.getLocation();
            Slaves.lookAt(le, player, 2);
            le.getWorld().spawnParticle(Particle.HEART, l.clone().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 1);
            le.setVelocity(new Vector(0, 0.3, 0));
        }
    },
    ANGERY {
        @Override
        public void play(LivingEntity le, Player player) {
            Location l = le.getLocation();
            long a = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - a > 1000) {
                        this.cancel();
                        return;
                    }
                    le.getWorld().spawnParticle(Particle.REDSTONE, ranXZ(l.clone().add(0, 1, 0), 0.5), 3, new Particle.DustOptions(Color.RED, 1));
                    le.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, l.clone().add(0, 1, 0), 1, 0.1, 0.1, 0.1);
                }
            }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);
        }
    },
    DEATH {
        @Override
        public void play(LivingEntity le, Player player) {
            Location l = le.getLocation();
            le.getWorld().spawnParticle(Particle.SMOKE_LARGE, l.clone().add(0, 0.2, 0), 25, 0.5, 0.5, 0.5, 0.1);
            le.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, l.clone().add(0, 0.2, 0), 20, 0.5, 0.5, 0.5, 0.1);
            l.getWorld().playSound(l, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
        }
    };

    public abstract void play(LivingEntity e, Player player);

    private static Location ranXZ(Location l, double r) {
        Location loc = l.clone();
        loc.setX(random(loc.getX() - r, loc.getX() + r));
        loc.setZ(random(loc.getZ() - r, loc.getZ() + r));
        return loc;
    }

    private static double random(double min, double max) {
        return (new Random().nextInt(new Double((max - min) * 1000).intValue()) + min * 1000) / 1000;
    }

}
