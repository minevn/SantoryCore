package mk.plugin.santory.wish;

import java.util.List;

public class WishReward {
	
	private double chance;
	private List<String> items;
	
	public WishReward(double chance, List<String> items) {
		this.chance = chance;
		this.items = items;
	}
	
	public double getChance() {
		return this.chance;
	}
	
	public List<String> getItems() {
		return this.items;
	}
	
}
