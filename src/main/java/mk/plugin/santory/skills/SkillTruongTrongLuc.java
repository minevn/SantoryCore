package mk.plugin.santory.skills;

import com.google.common.collect.Lists;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.utils.Tasks;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillTruongTrongLuc implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        final long time = 5000;

        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        float vmulti = Double.valueOf(scale * 0.08f).floatValue();

        Location l = player.getLocation().clone().add(player.getLocation().getDirection().multiply(5)).add(0, 2, 0);
        l.getWorld().playSound(l, Sound.ENTITY_WITHER_DEATH, 1, 1);

        var list1 = show(l, l.getPitch() - 90, -1 * l.getYaw() + 90, 0);
        var list2 = show(l, l.getPitch() + 45, -1 * l.getYaw() + 90, 0);
        var list3 = show(l, l.getPitch() - 45, -1 * l.getYaw() + 90, 0);

        long start = System.currentTimeMillis();
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= time) {
                    this.cancel();
                    return;
                }
                i += 1;
                var l1 = list1.get(i % list1.size());
                l1.getWorld().spawnParticle(Particle.REDSTONE, l1, 1, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(26, 2, 82), 2));

                var l2 = list2.get((i + 10) % list2.size());
                l2.getWorld().spawnParticle(Particle.REDSTONE, l2, 1, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(26, 2, 82), 2));

                var l3 = list3.get((i + 20) % list3.size());
                l3.getWorld().spawnParticle(Particle.REDSTONE, l3, 1, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(26, 2, 82), 2));

                l.getWorld().spawnParticle(Particle.REDSTONE, l, 3, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.BLACK, 2));
            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), 0, 1);

        Tasks.sync(() -> {
            for (Entity e : l.getWorld().getEntities()) {
                if (e instanceof LivingEntity && e != player && e.getLocation().distanceSquared(l) <= 25) {
                    var le = (LivingEntity) e;
                    if (!(le instanceof Player)) le.setVelocity(l.clone().subtract(le.getLocation().add(0, 1, 0)).toVector().normalize().multiply(vmulti));
                    else le.setVelocity(l.clone().subtract(le.getLocation().add(0, 1, 0)).toVector().normalize().multiply(vmulti / 2));
                }
            }
        }, 0, 2, time);

    }

    public static List<Location> show(Location l, double angleX, double angleY, double angleZ) {
        Location pl = l;
        Location c = l;
        List<Location> list = createCircle(pl, 3.2);
        List<Vector> list2 = Lists.newArrayList();
        for (Location location : list) {
            list2.add(location.clone().subtract(c.clone()).toVector().clone());
        }

        double sinX = Math.sin(Math.toRadians(angleX));
        double cosX = Math.cos(Math.toRadians(angleX));
        list2 = list2.stream().map(vec -> rotateAroundAxisX(vec, cosX, sinX)).collect(Collectors.toList());

        double sinY = Math.sin(Math.toRadians(angleY));
        double cosY = Math.cos(Math.toRadians(angleY));
        list2 = list2.stream().map(vec -> rotateAroundAxisY(vec, cosY, sinY)).collect(Collectors.toList());

        double sinZ = Math.sin(Math.toRadians(angleZ));
        double cosZ = Math.cos(Math.toRadians(angleZ));
        list2 = list2.stream().map(vec -> rotateAroundAxisZ(vec, cosZ, sinZ)).collect(Collectors.toList());

        for (int i = 0; i < list.size(); i++) {
            list.set(i, c.clone().add(list2.get(i).clone()));
        }

        return list;
    }

    public static List<Location> createCircle(Location location, double radius) {
        int amount = new Double(radius * 20).intValue() / 3 * 2;
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();

        for (int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = location.getX() + (radius * Math.cos(angle));
            double z = location.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(location.getWorld(), x, location.getY(), z));
        }

        return locations;
    }

    public static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }



}
