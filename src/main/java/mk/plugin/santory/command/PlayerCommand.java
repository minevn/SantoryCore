package mk.plugin.santory.command;

import mk.plugin.santory.artifact.ArtifactGUI;
import mk.plugin.santory.config.Configs;
import mk.plugin.santory.item.modifty.ModifyGUI;
import mk.plugin.santory.main.SantoryCore;
import mk.plugin.santory.traveler.TravelerInfoGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
            TravelerInfoGUI.open(player);
        }

        else if (cmd.getName().equalsIgnoreCase("globalspeaker")) {
            for (ItemStack is : player.getInventory().getContents()) {
                if (Configs.isGlobalSpeaker(is)) {
                    is.setAmount(is.getAmount() - 1);
                    player.updateInventory();

                    // Chat
                    String message = "";
                    for (int i = 0 ; i < args.length ; i++) {
                        message += args[i] + " ";
                    }
                    message = ChatColor.stripColor(message);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(stream);
                    String prefix = "§f ;§6§l❖ §a§l[SoraSky] §f§l"+ player.getName() + " §7§l>> §e" ;
                    String suffix = ";§f ";
                    String action = "fsbc";
                    String data2 = "";
                    try {
                        out.writeUTF(action);
                        out.writeUTF(prefix + message + suffix);
                        out.writeUTF(data2);
                        ((PluginMessageRecipient) player).sendPluginMessage(SantoryCore.get(), "fs:minestrike", stream.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return false;
                }
            }

            player.sendMessage("§cBạn cần có Loa thế giới");
        }

        return false;
    }
}
