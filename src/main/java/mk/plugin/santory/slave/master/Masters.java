package mk.plugin.santory.slave.master;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mk.plugin.playerdata.storage.PlayerData;
import mk.plugin.playerdata.storage.PlayerDataAPI;
import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.slave.Slave;
import mk.plugin.santory.traveler.TravelerStorage;
import org.bukkit.entity.Player;

import java.util.Map;

public class Masters {

    private static final Map<Player, Master> masters = Maps.newHashMap();

    private static final String KEY = "master";

    public static void saveAndClearCache(Player player) {
        save(player);
        masters.remove(player);
    }

    private static void load(Player player) {
        PlayerData data = PlayerDataAPI.get(player, TravelerStorage.HOOK);

        Master m = null;
        if (!data.hasData(KEY)) m = new Master(player.getName(), Lists.newArrayList());
        else {
            Gson gson = new GsonBuilder().create();
            m = gson.fromJson(data.getValue(KEY), Master.class);
        }

        masters.put(player, m);
    }

    public static void save(Player player) {
        Master m = get(player);
        Gson gson = new GsonBuilder().create();
        PlayerData data = PlayerDataAPI.get(player, TravelerStorage.HOOK);
        data.set(KEY, gson.toJson(m));
        data.save();
    }

    public static Master get(Player player) {
        if (masters.containsKey(player)) return masters.get(player);
        load(player);
        return get(player);
    }

    public static void add(Player player, Slave slave) {
        Master m = get(player);
        for (Slave ms : m.getSlaves()) {
            if (ms.getModelID().equalsIgnoreCase(slave.getModelID())) {
                ms.getData().setAscent(Ascent.from(Math.min(5, ms.getData().getAscent().getValue() + 1)));
                return;
            }
        }
        m.getSlaves().add(slave);
    }

}
