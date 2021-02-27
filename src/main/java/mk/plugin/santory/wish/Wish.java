package mk.plugin.santory.wish;

import java.util.Map;

import mk.plugin.santory.tier.Tier;

public class Wish {
	
	private String id;
	private String name;
	private String desc;
	private Map<Tier, WishReward> rewards;
	private Map<Tier, Integer> insures;
	
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
