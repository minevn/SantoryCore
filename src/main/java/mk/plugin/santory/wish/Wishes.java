package mk.plugin.santory.wish;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.tier.Tier;
import mk.plugin.santory.traveler.Traveler;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.ItemStackManager;
import mk.plugin.santory.utils.ItemStackUtils;
import mk.plugin.santory.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Wishes {

	public static WishRewardItem finalRate(Wish w, Player player) {
		// Check insures
		Traveler t = Travelers.get(player);
		WishData wd = t.getData().getWish(w.getID());
		
		// Add 1 to all
		w.getInsures().keySet().forEach(ti -> wd.setInsure(ti, wd.getInsures().getOrDefault(ti, 0) + 1));

		// Check insure
		Tier it = null;
		for (Tier ti : wd.getInsures().keySet()) {
			int i = wd.getInsures().get(ti);
			if (i > 0 && w.getInsures().containsKey(ti) && i >= w.getInsures().get(ti)) it = ti; 
		}

		// No insure
		if (it == null) {
			Tier tr = rate(w);

			// Set 0 to insure tier
			if (w.getInsures().containsKey(tr)) wd.setInsure(tr, 0);

			t.getData().setWish(w.getID(), wd);
			Travelers.save(player.getName());

			return rate(w, tr);
		}
		
		// Has insure
		wd.setInsure(it, 0);
		Travelers.save(player.getName());
		
		return rate(w, it);
	}
	
	public static Tier rate(Wish w) {
		double s = 0;
		List<Double> check = Lists.newArrayList();
		List<Tier> tiers = Lists.newArrayList();
		for (Tier t : w.getRewards().keySet()) {
			s += w.getRewards().get(t).getChance();
			check.add(s);
			tiers.add(t);
		}

		double random = Utils.random(1, s);
		for (int i = 0 ; i < check.size() ; i++) {
			if (check.get(i) >= random) return tiers.get(i);
		}		
		return tiers.get(check.size() - 1);
	}
	
	public static WishRewardItem rate(Wish wish, Tier tier) {
		WishReward reward = wish.getRewards().getOrDefault(tier, null);
		if (reward == null) return null;
		List<WishRewardItem> items = reward.getItems();
		return items.get(Utils.randomInt(0, items.size() - 1));
	}

	public static ItemStack buildKey(String keyID) {
		var wk = Configs.getWishKey(keyID);
		var is = wk.getItemStack();
		var im = new ItemStackManager(SantoryCore.get(), is);
		im.setTag("wishKey", keyID);

		return is;
	}

	public static String keyFrom(ItemStack is) {
		if (is == null) return null;
		var im = new ItemStackManager(SantoryCore.get(), is);
		if (!im.hasTag("wishKey")) return null;
		return im.getTag("wishkey");
	}
	
}
