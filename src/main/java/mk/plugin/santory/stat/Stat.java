package mk.plugin.santory.stat;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import mk.plugin.santory.utils.Utils;

public enum Stat{
	
	DAMAGE("Sát thương", 1, "§c") {
		@Override
		public double pointsToValue(int point) {
			return point * 1;
		}

		@Override
		public void set(Player player, int point) {}

		@Override
		public String getSubStat() {
			return "";
		}
	},
	
	HEALTH("Sinh lực", 10, "§a") {
		@Override
		public double pointsToValue(int point) {
			if (point < getMinValue()) point = getMinValue();
			return point * 3;
		}
		
		@Override
		public void set(Player player, int point) {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(pointsToValue(point));
			if (player.getHealth() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			}
		}
		
		@Override
		public String getSubStat() {
			return "";
		}
	},
	
	DEFENSE("Sức thủ", 5, "§f") {
		@Override
		public double pointsToValue(int point) {
			return Utils.round(((double) point / (point + 50))) * 50;
		}
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "%";
		}
	},
	
	DODGE("Né đòn", 5, "§3") {
		@Override
		public double pointsToValue(int point) {
			return Utils.round(((double) point / (point + 50))) * 100;
		}
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "%";
		}
	},
	
	HEAL("Hồi phục", 2, "§2") {
		@Override
		public double pointsToValue(int point) {
			return 1 + point * 0.5;
		}
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "";
		}
	},
	
	LIFE_STEAL("Hút máu", 5, "§6") {
		@Override
		public double pointsToValue(int point) {
			return 5 + Utils.round(((double) point / (point + 50))) * 20;
		}
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "";
		}
	},
	
	CRIT_CHANCE("Chí mạng", 5, "§e") {
		@Override
		public double pointsToValue(int point) {
			return Utils.round(((double) point / (point + 50))) * 100;
		} 
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "%";
		}
	},
	
	ATTACK_SPEED("Tốc đánh", 0, "§3") {
		@Override
		public double pointsToValue(int point) {
			return 1.5 - ((double) point / (point + 30) * 1.5) * 1.2;
		}
		
		@Override
		public void set(Player player, int point) {}
		
		@Override
		public String getSubStat() {
			return "%";
		}
	};
	
	public abstract double pointsToValue(int point);
	public abstract void set(Player player, int point);
	public abstract String getSubStat();
	
	private final String color;
	private final String name;
	private final int minValue;
	
	Stat(String name, int minValue, String color) {
		this.name = name;
		this.minValue = minValue;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getMinValue() {
		return this.minValue;
	}
	
	public String getColor() {
		return this.color;
	}
}
