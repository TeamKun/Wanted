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
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("reload") &&
                            args[1].equalsIgnoreCase("config")) {
                        Wanted.getInstance().reload();
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("v")) {
                        if (args[1].equalsIgnoreCase("start")) {
                            VoteAPI.getInstance().beginVoting(result -> {
                                Map<String, UUID> uniqueIdMap = result.getUniqueIdMap();
                                for (String name : result.getTop()) {
                                    manager.setLevel(uniqueIdMap.get(name), manager.getMaxLevel());
                                }
                            });
                            return true;
                        } else if (args[1].equalsIgnoreCase("end")) {
                            VoteAPI.getInstance().endVoting();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
            } else if (args.length == 1) {
                sender.sendMessage("手配度: " + Math.round(manager.getLevel(player) * 100) / 100f);
            } else {
                try {
                    int level = Integer.parseInt(args[1]);
                    manager.setLevel(player, level);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "数値を指定してください");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "指定可能な最大レベルは" + manager.getMaxLevel() + "です");
                }
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
            result.addAll(Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            result.add("v");
            result.add("reload");
        } else {
            if (args[0].equalsIgnoreCase("v")) {
                result.add("start");
                result.add("end");
            } else if (args[0].equalsIgnoreCase("reload")) {
                result.add("config");
            }
        }

        String last = args[args.length - 1].toLowerCase();
        return result.stream()
                .map(String::toLowerCase)
                .filter(arg -> arg.startsWith(last))
                .collect(Collectors.toList());
    }
}
