package mk.plugin.santory.tier;

public enum Tier {
	
	COMMON("Thường", "§f", 1),
	UNCOMMON("Trung", "§9", 2),
	RARE("Hiếm", "§c", 3),
	EPIC("Cực phẩm", "§6", 4),
	LEGEND("Huyền thoại", "§a", 5);
	
	private final String color;
	private final String name;
	private final int number;
	
	Tier(String name, String color, int number) {
		this.color = color;
		this.name = name;
		this.number = number;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public static Tier valueOf(int i) {
		for (Tier t : values()) {
			if (t.getNumber() == i) return t;
		}
		return null;
	}
}
