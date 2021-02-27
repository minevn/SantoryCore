package mk.plugin.santory.skills;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.skill.SkillExecutor;
import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import net.minecraft.server.v1_12_R1.PacketPlayOutAnimation;

public class WSDapRiu implements SkillExecutor {

	@Override
	public void start(Map<String, Object> components) {
		Player player = (Player) components.get("player");
		double scale = ((double) components.get("scale")) * 0.01;
		double damage = Travelers.getStatValue(player, Stat.DAMAGE) * scale;
		
		player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1.2, 0).add(player.getLocation().getDirection().multiply(1.2)), 1, 0, 0, 0, 0);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
		
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), (byte) 0);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		
		Location main = player.getLocation().add(player.getLocation().getDirection().multiply(1));
		Utils.getLivingEntities(player, main, 3, 3, 3).forEach(le -> {
			if (!Utils.canAttack(le)) return;
			Bukkit.getScheduler().runTask(SantoryCore.get(),() -> {
				Damages.damage(player, (LivingEntity) le, new Damage(damage, DamageType.SKILL), 5);
			});

			player.spawnParticle(Particle.CRIT_MAGIC, le.getLocation(), 10, 0.2, 0.2, 0.2, 1);
			player.spawnParticle(Particle.CRIT, le.getLocation(), 10, 0.2, 0.2, 0.2, 1);
			le.setVelocity(le.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(1.2).setY(1));
		});
	}

}
