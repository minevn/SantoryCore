package mk.plugin.santory.history.histories;

import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.history.Histories;
import mk.plugin.santory.history.IHistory;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TimedHistory implements IHistory {

    @Override
    public String getName() {
        return "timeds";
    }

    public void write(Player player, String itemId) {
        var file = Histories.getFile(this);

        var line = "[" + Histories.getCurrentTime()
                + "] " + player.getName() + " | "
                + itemId;
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