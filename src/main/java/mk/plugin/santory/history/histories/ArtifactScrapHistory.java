package mk.plugin.santory.history.histories;

import mk.plugin.santory.ascent.Ascent;
import mk.plugin.santory.history.Histories;
import mk.plugin.santory.history.IHistory;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ArtifactScrapHistory implements IHistory {

    @Override
    public String getName() {
        return "scraps";
    }

    public void write(Player player, List<String> materials, String result) {
        var file = Histories.getFile(this);

        var line = "[" + Histories.getCurrentTime()
                + "] " + player.getName() + " | "
                + "Result: " + result + " | ";

        for (String m : materials) {
            line += m + " | ";
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(line);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
