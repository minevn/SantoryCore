package mk.plugin.santory.grade;

public enum Grade {
	
	I(1, 10, 20),
	II(2, 20, 40),
	III(3, 30, 60),
	IV(4, 40, 80),
	V(5, 50, 100);

    public static final String ICON = "☆";
	
	private final int value;
	private final int maxEnhance;
	private final int maxLevel;
	
	Grade(int value, int maxEnhance, int maxLevel) {
		this.value = value;
		this.maxEnhance = maxEnhance;
		this.maxLevel = maxLevel;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public int getMaxEnhance() {
		return this.maxEnhance;
	}
	
	public int getMaxLevel() {
		return this.maxLevel;
	}

}
