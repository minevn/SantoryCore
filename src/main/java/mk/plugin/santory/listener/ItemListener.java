package mk.plugin.santory.listener;

import com.google.common.collect.Lists;
import mk.plugin.santory.damage.Damages;
import mk.plugin.santory.event.ItemToggleEvent;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.main.SantoryCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class ItemListener implements Listener {
	
	public final String DAMAGE_TAG = "item.damagetag";
	public final String INTERACT_TAG = "item.interact";

	/*
	 *  Animation = last
	 */
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (e.getAction().name().contains("LEFT_CLICK")) {
			player.setMetadata(INTERACT_TAG, new FixedMetadataValue(SantoryCore.get(), ""));
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player player = (Player) e.getDamager();
			LivingEntity target = (LivingEntity) e.getEntity();
			if (Damages.hasDamage(target)) return;
			player.setMetadata(DAMAGE_TAG, new FixedMetadataValue(SantoryCore.get(), ""));
		}
	}
	
	@EventHandler
	public void onAnimation(PlayerAnimationEvent e) {
		Player player = e.getPlayer();
		// Check bypass
		if (PlayerListener.ditmemay.getOrDefault(player.getName(), 0L) >= System.currentTimeMillis()) return;
		if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			// Check
			boolean hasDamage = player.hasMetadata(DAMAGE_TAG);
			boolean hasInteract = player.hasMetadata(INTERACT_TAG);
			
			// Remove tags
			player.removeMetadata(DAMAGE_TAG, SantoryCore.get());
			player.removeMetadata(INTERACT_TAG, SantoryCore.get());
			
			// Event
			if (hasDamage || hasInteract) {
				// Call event
				ItemStack is = player.getInventory().getItemInMainHand();
				if (Items.is(is)) {
					Item item = Items.read(is);
					Bukkit.getPluginManager().callEvent(new ItemToggleEvent(player, item, is));
				}
			}
		}
	}
	
	// Update item
	@EventHandler
	public static void onOpenInventory(InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() == InventoryType.CRAFTING) {
			Player player = (Player) e.getPlayer();
			PlayerInventory pi = player.getInventory();
			List<ItemStack> list = Lists.newArrayList(pi.getArmorContents());
			list.add(pi.getItemInMainHand());
			for (ItemStack is : list) {
				if (Items.is(is)) {
					Item item = Items.read(is);
					Items.update(player, is, item);
				}
			}
		}
	}
	
}
