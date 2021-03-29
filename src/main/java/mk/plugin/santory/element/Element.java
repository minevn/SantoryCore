package mk.plugin.santory.element;

public enum Element {

    FLAME("Hỏa", "§c"),
    WATER("Thủy", "§b"),
    EARTH("Thổ", "§6"),
    PLANT("Mộc", "§a"),

    LIGHT("Ánh sáng", "§e"),
    DARKNESS("Bóng đêm", "§5");

    private final String name;
    private final String color;

    Element(String name, String color) {
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
