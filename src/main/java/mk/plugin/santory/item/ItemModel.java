package mk.plugin.santory.item;

import java.util.Map;

import com.google.common.collect.Maps;

import mk.plugin.santory.stat.Stat;
import mk.plugin.santory.tier.Tier;

public class ItemModel {
	
	private ItemTexture texture;
	private ItemType type;
	private Tier tier;
	private String name;
	private String desc;
	private Map<Stat, Integer> stats;
	private Map<String, String> metadata;
	
	public ItemModel(ItemTexture texture, ItemType type, 
			String name, Tier tier, 
			String desc, Map<Stat, Integer> stats,
			Map<String, String> metadata) {
		this.texture = texture;
		this.type = type;
		this.tier = tier;
		this.name = name;
		this.desc = desc;
		this.stats = stats;
		this.metadata = metadata;
	}
	
	public ItemTexture getTexture() {
		return this.texture;
	}
	
	public ItemType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Tier getTier() {
		return this.tier;
	}
	
	public String getDesc() {
		return this.desc;
	}

	public Map<Stat, Integer> getBaseStats() {
		return Maps.newLinkedHashMap(this.stats);
	}
	
	public Map<String, String> getMetadata() {
		return this.metadata;
	}
	
}
