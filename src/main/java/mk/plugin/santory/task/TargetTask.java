package mk.plugin.santory.task;

import com.google.common.collect.Maps;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.weapon.Weapon;
import mk.plugin.santory.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.glow.GlowAPI;

import java.util.Map;

public class TargetTask extends BukkitRunnable {

	private Map<Player, LivingEntity> targets;

	public TargetTask() {
		this.targets = Maps.newConcurrentMap();
	}

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			ItemStack is = player.getInventory().getItemInMainHand();
			if (!Items.is(is)) return;
			Item item = Items.read(is);
			if (item.getModel().getType() == ItemType.WEAPON) {
				Weapon w = Weapon.parse(item.getModel());
				double range = w.getType().isShooter() ? 20 : w.getType().getRange();
				LivingEntity target = Utils.getTargetAsync(player, range);
				if (target == null) return;
				player.spawnParticle(Particle.REDSTONE, target.getEyeLocation().add(0, 0.35, 0), 1, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
			}
		});
	}

	public void removePlayer(Player player) {
		this.targets.remove(player);
	}
	
}