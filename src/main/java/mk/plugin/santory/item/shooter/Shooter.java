package mk.plugin.santory.item.shooter;

import mk.plugin.santory.damage.Damage;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.main.SantoryCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public enum Shooter {
	
	BOW {
		@Override
		public Arrow shoot(Player player, Damage damage, Vector v, Location location) {
			Arrow arrow = player.getWorld().spawnArrow(location, v, 0, 0);
			arrow.setShooter(player);
			arrow.setVelocity(v);
			Damages.setProjectileDamage(arrow, damage);
			player.playSound(location, Sound.ENTITY_ARROW_SHOOT, 1, 1);
			Bukkit.getScheduler().runTaskLater(SantoryCore.get(), arrow::remove, 10);
			return arrow;
		}

		@Override
		public boolean checkRequirements(Player player) {
			// Check has arrows
			boolean has = false;
			ItemStack[] contents = player.getInventory().getContents();
			for (int i = 0 ; i < contents.length ; i++) {
				ItemStack item = contents[i];
				if (item != null && item.isSimilar(new ItemStack(Material.ARROW))) {
					if (item.getAmount() == 1) contents[i] = null;
					else item.setAmount(item.getAmount() - 1);
					player.getInventory().setContents(contents);
					has = true;
					break;
				}
			}
			if (!has) {
				player.sendMessage("§cBạn cần có mũi tên trong người!");
				return false;
			}

			return true;
		}
	};
	
	Shooter() {}
	
	public abstract Arrow shoot(Player player, Damage damage, Vector v, Location location);
	public abstract boolean checkRequirements(Player player);
	
}
