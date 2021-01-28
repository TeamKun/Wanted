package dev.krmn.wanted;

import net.teamfruit.voteplugin.VoteAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WantedCommand implements CommandExecutor, TabCompleter {
    private final WantedLevelManager manager = WantedLevelManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("wanted")) {
            return false;
        }
        if (args.length == 0) {
            return false;
        }

        if (command.testPermission(sender)) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Wanted.getInstance().reload();
                    return true;
                }

                return false;
            }
            Player player;
            switch (args[0].toLowerCase()) {
                case "get":
                    player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
                        return true;
                    }
                    sender.sendMessage("手配度: " + Math.round(manager.getLevel(player) * 100) / 100f);
                    break;
                case "set":
                    if (args.length == 2) {
                        return false;
                    }
                    player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
                        return true;
                    }
                    try {
                        double level = Double.parseDouble(args[2]);
                        manager.setLevel(player, level);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "数値を指定してください");
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + "指定可能な最大レベルは" + manager.getMaxLevel() + "です");
                    }
                    break;
                case "v":
                    if (args[1].equalsIgnoreCase("start")) {
                        VoteAPI.getInstance().beginVoting(result -> {
                            Map<String, UUID> uniqueIdMap = result.getUniqueIdMap();
                            for (String name : result.getTop()) {
                                manager.setLevel(uniqueIdMap.get(name), manager.getMaxLevel());
                            }
                        });
                    } else if (args[1].equalsIgnoreCase("end")) {
                        VoteAPI.getInstance().endVoting();
                    } else {
                        return false;
                    }
                    break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList(
                    "get",
                    "set",
                    "v",
                    "reload"
            ));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "get":
                case "set":
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .forEach(result::add);
                    break;
                case "v":
                    result.add("start");
                    result.add("end");
            }
        }

        String last = args[args.length - 1].toLowerCase();
        return result.stream()
                .map(String::toLowerCase)
                .filter(arg -> arg.startsWith(last))
                .collect(Collectors.toList());
    }
}
