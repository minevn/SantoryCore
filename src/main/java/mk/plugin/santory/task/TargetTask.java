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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class TargetTask extends BukkitRunnable {

	private final Map<LivingEntity, Long> map = Maps.newConcurrentMap();
	
	@Override
	public void run() {
		map.forEach((le, l) -> {
			if (l < System.currentTimeMillis()) {
				map.remove(le);
			}
		});
		Bukkit.getOnlinePlayers().forEach(player -> {
			ItemStack is = player.getInventory().getItemInMainHand();
			if (!Items.is(is)) return;
			Item item = Items.read(is);
			if (item.getModel().getType() == ItemType.WEAPON) {
				Weapon w = Weapon.parse(item.getModel());
				double range = w.getType().isShooter() ? 20 : w.getType().getRange();
				LivingEntity target = Utils.getTarget(player, range);
				if (target == null) return;
				map.put(target, System.currentTimeMillis() + 300);
				Utils.circleParticles(new Particle.DustOptions(Color.RED, 1), target.getEyeLocation().add(0, 0.4, 0), 0.1);
			}
		});
	}
	
}