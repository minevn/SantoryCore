package mk.plugin.santory.skin.system;

import mk.plugin.santory.item.Item;

import java.util.List;

public class PlayerSkin {

    private final String player;
    private List<Item> skins;

    public PlayerSkin(String player, List<Item> skins) {
        this.player = player;
        this.skins = skins;
    }

    public String getPlayer() {
        return player;
    }

    public List<Item> getSkins() {
        return skins;
    }

    public void setSkins(List<Item> skins) {
        this.skins = skins;
    }
}
