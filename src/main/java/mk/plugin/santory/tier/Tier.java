package mk.plugin.santory.tier;

public enum Tier {
	
	COMMON("Thường", "§f", 1, 2),
	UNCOMMON("Trung", "§9", 2, 3),
	RARE("Hiếm", "§c", 3, 4),
	EPIC("Cực phẩm", "§6", 4, 5),
	LEGEND("Huyền thoại", "§a", 5, 6);
	
	private final String color;
	private final String name;
	private final int number;

	private final int enhanceUp;
	
	Tier(String name, String color, int number, int enhanceUp) {
		this.color = color;
		this.name = name;
		this.number = number;
		this.enhanceUp = enhanceUp;
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

	public int getEnhanceUp() {
		return enhanceUp;
	}

	public static Tier valueOf(int i) {
		for (Tier t : values()) {
			if (t.getNumber() == i) return t;
		}
		return null;
	}
}
