package mk.plugin.santory.history.histories;

import mk.plugin.santory.history.Histories;
import mk.plugin.santory.history.IHistory;
import mk.plugin.santory.tier.Tier;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EnhanceHistory implements IHistory {

    @Override
    public String getName() {
        return "enhances";
    }

    public void write(Player player, int toLevel, boolean success, boolean hasAmulet) {
        var file = Histories.getFile(this);

        var line = "[" + Histories.getCurrentTime()
                + "] " + player.getName() + " | "
                + (success ? "THANH_CONG" : "THAT_BAI") + " | "
                + "LEVEL_" + toLevel + " | "
                + (hasAmulet ? "CO_BUA" : "KHONG_BUA");

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(line);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(line);
    }

}
