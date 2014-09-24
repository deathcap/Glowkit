package org.bukkit.command.defaults;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class TriggerCommand extends VanillaCommand{
    private static final List<String> TABCOMPLETE_ADD_SET = ImmutableList.of("add", "set");

    public TriggerCommand() {
        super("trigger");
        this.description = "Change a player's score for a given objective";
        this.usageMessage = "/trigger";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /trigger <objective> <add|set> <value>");
            return false;
        }

        Objective objective;
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
            objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(args[0]);
        }
        else {
            sender.sendMessage("Only players can use the /trigger command");
            return false;
        }

        if (objective == null) {
            sender.sendMessage(ChatColor.RED + "Invalid trigger name " + args[0]);
            return false;
        }

        if (objective.getScore(player.getName()).getLocked() == true) {
            sender.sendMessage(ChatColor.RED + "Trigger " + objective.getName() + " is not enabled");
            return false;
        }

        int value;

        try {
            value = Integer.valueOf(args[2]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number");
            return false;
        }

        Score score = objective.getScore(player.getName());

        if (args[1].equalsIgnoreCase("add")) {
            score.setScore(score.getScore() + value);
            score.setLocked(true);
            sender.sendMessage("Trigger " + args[0] + " changed with add " + value);
            return true;
        }
        if (args[1].equalsIgnoreCase("set")) {
            score.setScore(value);
            score.setLocked(true);
            sender.sendMessage("Trigger " + args[0] + " changed with set " + value);
            return true;
        }
        sender.sendMessage("Invalid trigger mode " + args[1]);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                List<String> objectives = new ArrayList<String>();
                for (Objective objective: Bukkit.getScoreboardManager().getMainScoreboard().getObjectivesByCriteria(Criteria.TRIGGER)) {
                    objectives.add(objective.getName());
                }
                return objectives;
            }
        }
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], TABCOMPLETE_ADD_SET, new ArrayList<String>(TABCOMPLETE_ADD_SET.size()));
        }

        return ImmutableList.of();
    }
}
