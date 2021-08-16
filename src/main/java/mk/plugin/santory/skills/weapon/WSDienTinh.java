package mk.plugin.santory.skills.weapon;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class WSDienTinh implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
		player.getNearbyEntities(2.5, 1, 2.5).forEach(e -> {
			if (!(e instanceof LivingEntity)) return;
			LivingEntity le = (LivingEntity) e;
			if (!Utils.canAttack(e)) return;
			if (e instanceof Player) {
				effectPlayer((Player) e);
			} else effectEntity(le);
			if (e != player) {
				if (le.hasMetadata("NPC")) return;
				Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
			}
		});
	}
	
	public void effectPlayer(Player player) {
		int seconds = 3;
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * seconds, 5));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * seconds, 5));
		long start = System.currentTimeMillis();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - start >= seconds * 1000) {
					this.cancel();
					return;
				}
				player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.3);
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 5);
	}
	
	public void effectEntity(LivingEntity le) {
		int seconds = 4;
		le.setAI(false);
		World world = le.getWorld();
		long start = System.currentTimeMillis();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() - start >= seconds * 1000) {
					this.cancel();
					le.setAI(true);
					return;
				}
				world.spawnParticle(Particle.HEART, le.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.3);
			}
		}.runTaskTimerAsynchronously(SantoryCore.get(), 0, 5);
	}

}
