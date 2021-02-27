package mk.plugin.santory.task;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

import com.google.common.collect.Maps;

import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.item.weapon.Weapon;
import mk.plugin.santory.utils.Utils;

public class TargetTask extends BukkitRunnable {

	private Map<LivingEntity, Long> map = Maps.newConcurrentMap();
	
	@Override
	public void run() {
		if (!Bukkit.getPluginManager().isPluginEnabled("GlowAPI")) {
			this.cancel();
			return;
		}
		map.forEach((le, l) -> {
			if (l < System.currentTimeMillis()) {
				GlowAPI.setGlowing(le, false, Bukkit.getOnlinePlayers());
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
				GlowAPI.setGlowing(target, Color.RED, player);
			}
		});
	}
	
}