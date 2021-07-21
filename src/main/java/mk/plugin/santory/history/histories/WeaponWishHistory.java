package mk.plugin.santory.history.histories;

import mk.plugin.santory.history.Histories;
import mk.plugin.santory.history.IHistory;
import mk.plugin.santory.tier.Tier;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WeaponWishHistory implements IHistory {

    @Override
    public String getName() {
        return "weapon_wishes";
    }

    public void write(Player player, Tier tier, String result, boolean isInsure) {
        var file = Histories.getFile(this);

        var line = "[" + Histories.getCurrentTime()
                + "] " + player.getName() + " | "
                + (isInsure ? "BAO_HIEM" : "THUONG") + " | "
                + tier.name() + " | "
                + result;

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(line);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
