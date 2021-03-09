package mk.plugin.santory.wish;

import java.util.List;

public class WishReward {
	
	private final double chance;
	private final List<WishRewardItem> items;
	
	public WishReward(double chance, List<WishRewardItem> items) {
		this.chance = chance;
		this.items = items;
	}
	
	public double getChance() {
		return this.chance;
	}
	
	public List<WishRewardItem> getItems() {
		return this.items;
	}
	
}
