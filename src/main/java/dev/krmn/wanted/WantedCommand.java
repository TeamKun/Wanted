package dev.krmn.wanted;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WantedCommand implements CommandExecutor {
    private final WantedLevelManager manager = WantedLevelManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("wanted")) {
            return false;
        }
        if (!command.testPermission(sender)) {
            return false;
        }

        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
                return true;
            }

            if (args.length == 1) {
                sender.sendMessage("手配度レベル：" + manager.getLevel(player));
                return true;
            }

            try {
                int level = Integer.parseInt(args[1]);
                manager.setLevel(player, level);
                return true;
            } catch (NumberFormatException ignored) {
            }
        }

        return false;
    }
}
