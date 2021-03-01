package mk.plugin.santory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import mk.plugin.santory.item.Item;

public class ItemToggleEvent extends PlayerEvent {
	
	/*
	 * Detection:
	 * 1. Interact Left Click Air >> Hand animation
	 * 2. Damage >> Hand animation
	 */
	
	private final Item item;
	private final ItemStack itemStack;
	
	public ItemToggleEvent(Player who, Item item, ItemStack itemStack) {
		super(who);
		this.item = item;
		this.itemStack = itemStack;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}