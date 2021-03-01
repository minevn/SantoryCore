package mk.plugin.santory.wish;

import mk.plugin.santory.tier.Tier;

import java.util.Map;

public class Wish {
	
	private final String id;
	private final String name;
	private final String desc;
	private final Map<Tier, WishReward> rewards;
	private final Map<Tier, Integer> insures;
	
	public Wish(String id, String name, String desc, Map<Tier, WishReward> rewards, Map<Tier, Integer> insures) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.rewards = rewards;
		this.insures = insures;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public Map<Tier, WishReward> getRewards() {
		return this.rewards;
	}
	
	public Map<Tier, Integer> getInsures() {
		return this.insures;
	}
	
}
