package mk.plugin.santory.command;

import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.item.modifty.ModifyGUI;
import mk.plugin.santory.traveler.TravelerInfoGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("artifact")) {
            ArtifactGUI.open(player);
        }

        else if (cmd.getName().equalsIgnoreCase("forge")) {
            ModifyGUI.open(player);
        }

        else if (cmd.getName().equalsIgnoreCase("see")) {
            if (args.length == 0) {
                player.sendMessage("§cDùng bằng cách: /xem <tên> (Ví dụ: /xem Mankaistep)");
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            TravelerInfoGUI.open(player, target);
        }

        else if (cmd.getName().equalsIgnoreCase("player")) {
            TravelerInfoGUI.open(player, player);
        }

        return false;
    }
}
