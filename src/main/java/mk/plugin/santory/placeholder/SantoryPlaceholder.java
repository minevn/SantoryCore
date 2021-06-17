package mk.plugin.santory.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import mk.plugin.santory.traveler.Travelers;
import mk.plugin.santory.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

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
			if (Travelers.isHackChecked(player.getName())) {
				return "§a✔ ";
			} else return "§8✔ ";
		}
		
		else if (s.contains("random_int_")) {
			String t = s.replace("random_int_", "");
			int min = Integer.valueOf(t.split("_")[0]);
			int max = Integer.valueOf(t.split("_")[1]);
			return Utils.randomInt(min, max) + "";
		}

		else if (s.startsWith("rank_remain")) {
			var rank = s.replaceAll("rank_remain_", "");
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if (provider != null) {
				LuckPerms api = provider.getProvider();
				for (Node node : api.getUserManager().getUser(player.getUniqueId()).getNodes()) {
					if (node.getKey().equals("group." + rank)) {
						if (!node.hasExpiry()) return "Vĩnh viễn";
						if (node.hasExpired()) return "Hết hạn";

						var d = node.getExpiryDuration().toDaysPart();
						var h = node.getExpiryDuration().toHoursPart();
						var m = node.getExpiryDuration().toMinutesPart();
						var se = node.getExpiryDuration().toSecondsPart();
						return d + "d " + h + "h " + m + "m " + se + "s ";
					}
				}
				return "Không sở hữu";
			}
		}
		
		return "Wrong placeholder";
	}
	
}
