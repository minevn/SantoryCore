package mk.plugin.santory.ascent;

public enum Ascent {
	
	I(1),
	II(2),
	III(3),
	IV(4),
	V(5);
	;
	
	public static final String ICON = "♢";
	
	private int value;
	
	private Ascent(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public static Ascent from(int value) {
		for (Ascent a : values()) if (a.getValue() == value) return a;
		return null;
	}
	

}
