package mk.plugin.santory.history;

import mk.plugin.santory.main.SantoryCore;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Histories {

    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static File getFile(IHistory history) {
        generate(history);
        return new File(SantoryCore.get().getDataFolder() + "//histories//" + history.getName() + ".txt");
    }

    public static void generate(IHistory history) {
        var name = history.getName();

        // Check folder
        var folder = new File(SantoryCore.get().getDataFolder() + "//histories");
        if (!folder.exists()) folder.mkdirs();

        // Check file
        var file = new File(folder, name + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
