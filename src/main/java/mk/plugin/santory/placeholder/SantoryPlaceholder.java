package mk.plugin.santory.placeholder;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;

public class SantoryPlaceholder extends PlaceholderExpansion {
	

	@Override
	public String getAuthor() {
		return "MankaiStep";
	}

	@Override
	public String getIdentifier() {
		return "santory";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
    public String onPlaceholderRequest(Player player, String s){
		if (s.equalsIgnoreCase("player_power")) {
			return Utils.calPower(player) + "";
		}
		
		else if (s.equalsIgnoreCase("player_exp")) {
			return Travelers.get(player).getData().getExp() + "";
		}
		
		else if (s.equalsIgnoreCase("player_xacminh")) {
			if (player.hasMetadata("santory-xacminh")) {
				return "§b✔ ";
			} else return "§7✘ ";
		}
		
		else if (s.contains("random_int_")) {
			String t = s.replace("random_int_", "");
			int min = Integer.valueOf(t.split("_")[0]);
			int max = Integer.valueOf(t.split("_")[1]);
			return Utils.randomInt(min, max) + "";
		}
		
		return "Wrong placeholder";
	}
	
}
