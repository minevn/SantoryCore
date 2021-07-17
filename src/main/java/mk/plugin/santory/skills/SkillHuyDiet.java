package mk.plugin.santory.skills;

import com.google.common.collect.Lists;
import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Tasks;
import mk.plugin.santory.utils.Utils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class SkillHuyDiet implements SkillExecutor {

    @Override
    public void start(Map<String, Object> components) {
        Player player = (Player) components.get("player");
        double scale = ((double) components.get("scale")) * 0.01;
        double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;

        double r = 2;
        var l = Utils.getLandedLocation(player.getLocation().add(player.getLocation().getDirection().multiply(5)).clone());

        List<ArmorStand> liste = Lists.newArrayList();
        Tasks.sync(() -> {
            var rx = Utils.random(-1 * r, r);
            var rz = Utils.random(-1 * r, r);
            var rl = l.clone().add(rx, 40, rz);
            var entity = spawnEntity(rl);
            entity.setVelocity(new Vector(0, -1.5, 0));
            liste.add(entity);

            // Check ground
            long start = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - start >= 10000) {
                        this.cancel();
                        return;
                    }
                    if (entity.isOnGround()) {
                        this.cancel();
                        l.getWorld().playSound(entity.getLocation(), Sound.ENTITY_BLAZE_HURT, 1, 1);

                        // Damage
                        for (Entity e : l.getWorld().getNearbyEntities(l, 3, 3, 3)) {
                            if (e instanceof LivingEntity && e != player) {
                                if (!Utils.canAttack(e)) continue;
                                Damages.damage(player, (LivingEntity) e, new Damage(5, DamageType.SKILL), 5);
                                e.setVelocity(new Vector(0, 0.3, 0));
                                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5));
                            }
                        }
                    }
                }
            }.runTaskTimer(SantoryCore.get(), 0, 1);
        }, 0, 7, 5);

        Tasks.async(() -> {
            l.getWorld().playSound(l, Sound.ENTITY_GENERIC_BURN, 1, 1);
        }, 40);
        Tasks.async(() -> {
            l.getWorld().spawnParticle(Particle.FLAME, l, 20, 2, 2, 2, 0.05);
        }, 40, 3, 4000L);

        Tasks.sync(() -> {
            l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            for (ArmorStand a : liste) {
                a.remove();
                l.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, a.getLocation().clone().add(0, 1, 0), 6, 1.5, 1.5, 1.5, 0.1);
            }
            for (Entity e : l.getWorld().getNearbyEntities(l, 4, 4, 4)) {
                if (e instanceof LivingEntity && e != player) {
                    if (!Utils.canAttack(e)) continue;
                    Damages.damage(player, (LivingEntity) e, new Damage(damage, DamageType.SKILL), 5);
                    e.setFireTicks(20);
                }
            }
        }, 80);

    }

    public static ArmorStand spawnEntity(Location l) {
        var is = new ItemStack(Material.SPRUCE_SAPLING);
        var meta = is.getItemMeta();
        meta.setCustomModelData(1);
        is.setItemMeta(meta);

        var a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
        a.setInvisible(true);
        a.getEquipment().setHelmet(is);
        a.setBasePlate(false);
        a.setRotation(Utils.randomInt(0, 180), 0);
        a.setCollidable(false);
        a.setMaximumNoDamageTicks(10000);
        a.setNoDamageTicks(10000);

        return a;
    }
}
