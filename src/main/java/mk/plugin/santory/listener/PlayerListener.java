package mk.plugin.santory.listener;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.DamageType;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Travelers.saveAndClearCache(e.getPlayer().getName());
	}
	
	@EventHandler
	public void onHitMuaTen(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Arrow) {
			Arrow a = (Arrow) e.getDamager();
			if (a.hasMetadata("arrow.MuaTen")) {
				Player player = (Player) a.getShooter();
				a.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, a.getLocation(), 1, 0, 0, 0, 0);
				a.getWorld().playSound(a.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);
				double damage = a.getMetadata("arrow.MuaTen").get(0).asDouble();
				Utils.getLivingEntities(player, a.getLocation(), 2, 2, 2).forEach(le -> {
					if (!Utils.canAttack(le)) return;
					Damages.damage(player, le, new Damage(damage, DamageType.SKILL), 5);
				});
			}
		}
	}
	
}
