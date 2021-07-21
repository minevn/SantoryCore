package mk.plugin.santory.history.histories;

import mk.plugin.santory.grade.Grade;
import mk.plugin.santory.history.Histories;
import mk.plugin.santory.history.IHistory;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class UpgradeHistory implements IHistory {

    @Override
    public String getName() {
        return "upgrades";
    }

    public void write(Player player, String itemId, int toExp, boolean success, boolean hasAmulet) {
        var file = Histories.getFile(this);

        var line = "[" + Histories.getCurrentTime()
                + "] " + player.getName() + " | "
                + itemId + " | "
                + (success ? "THANH_CONG" : "THAT_BAI") + " | "
                + "EXP_" + toExp + " | "
                + (hasAmulet ? "CO_BUA" : "KHONG_BUA");

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(line);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
