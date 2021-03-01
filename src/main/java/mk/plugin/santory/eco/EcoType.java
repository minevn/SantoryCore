package mk.plugin.santory.eco;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public enum EcoType {
	
	MONEY("Money", "§f") {

		@Override
		public boolean take(Player player, int value) {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		    if (rsp == null) return false;
			Economy eco = rsp.getProvider();
			double moneyOfPlayer = eco.getBalance(player);
			if (moneyOfPlayer < value) {
				return false;
			}
			eco.withdrawPlayer(player, value);
			return true;
		}

		@Override
		public int get(Player player) {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		    if (rsp == null) return 0;
			Economy eco = rsp.getProvider();
			return new Double(eco.getBalance(player)).intValue();
		}
	},
	POINT("Point", "§e") {

		@Override
		public boolean take(Player player, int value) {
			Plugin pl = Bukkit.getPluginManager().getPlugin("PlayerPoints");
	    	PlayerPoints pp = (PlayerPoints) pl;
			if (value > pp.getAPI().look(player.getName())) {
				return false;
			} 
			pp.getAPI().take(player.getUniqueId(), value);
			return true;
		}

		@Override
		public int get(Player player) {
			Plugin pl = Bukkit.getPluginManager().getPlugin("PlayerPoints");
	    	PlayerPoints pp = (PlayerPoints) pl;
	    	return pp.getAPI().look(player.getUniqueId());
		}

	};
	
	private final String name;
	private final String color;
	
	public abstract int get(Player player);
	public abstract boolean take(Player player, int value);
	
	EcoType(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
}
