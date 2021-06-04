package mk.plugin.santory.skills;

import com.google.common.collect.Lists;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class SkillTuThan implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                i++;
                if (i > 3) {
                    this.cancel();
                    return;
                }
                player.swingMainHand();
                shoot(player, damage);
            }
        }.runTaskTimer(SantoryCore.get(), 0, 15);
    }

    public static void shoot(Player player, double damage) {
        Location l = player.getLocation().clone();
        l.add(0, 1, 0);
        double pitch = Utils.random(20, 170);
        new BukkitRunnable() {
            int c = 0;

            @Override
            public void run() {
                // TODO Auto-generated method stub
                c++;
                if (c > 30) {
                    this.cancel();
                    return;
                }
                double r = 5;
                Location center = l.clone().add(l.getDirection().setY(0).multiply(c * 1.2));
                List<Location> list = show(center, l.getPitch() + pitch, -1 * l.getYaw() + 90, 0);

                // Effect
                if (c % 5 == 0) center.getWorld().playSound(center, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                for (Location loc : list) {
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0, 0, 0, 0);
                }

                for (Entity entity : center.getWorld().getEntities()) {
                    if (entity == player) continue;
                    if (!(entity instanceof LivingEntity)) continue;;
                    boolean canDamage = false;
                    for (Location loc : list) {
                        if (entity.getLocation().distanceSquared(loc) <= 1) {
                            canDamage = true;
                            break;
                        }
                    }
                    if (!canDamage) continue;
                    if (!Utils.canAttack(entity)) continue;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Damages.damage(player, (LivingEntity) entity, new Damage(damage, DamageType.SKILL), 1);
                        }
                    }.runTask(SantoryCore.get());
                }

            }
        }.runTaskTimerAsynchronously(SantoryCore.get(), 2, 0);
    }

    public static List<Location> show(Location l, double angleX, double angleY, double angleZ) {
        Location pl = l;
        Location c = l;
        List<Location> list = createCircle(pl, 4);
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


//    public static List<Location> getCurve(Location loc, double radius) {
//        List<Location> list = Lists.newArrayList();
//
//        int amount = 40;
//        double maxAngle = 180;
//        double angleBetweenArrows = (maxAngle / (amount - 1)) * Math.PI / 180;
//        double pitch = (loc.getPitch() + 90) * Math.PI / 180;
//        double yaw = (loc.getYaw() + 90 - maxAngle / 2) * Math.PI / 180;
//
//        for (int i = 0; i < amount; i++) {
//            double nX = Math.sin(pitch) * Math.cos(yaw + angleBetweenArrows * i);
//            double nY = Math.sin(pitch) * Math.sin(yaw + angleBetweenArrows * i);
//            Vector newDir = new Vector(nX, 0, nY);
//            list.add(loc.clone().add(newDir.multiply(5)));
//        }
//
//        return list;
//    }


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
