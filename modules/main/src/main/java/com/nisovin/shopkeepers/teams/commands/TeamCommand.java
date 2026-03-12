package com.nisovin.shopkeepers.teams.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    private final SKShopkeepersPlugin plugin;
    private final TeamManager teamManager;

    public TeamCommand(SKShopkeepersPlugin plugin) {
        this.plugin = plugin;
        this.teamManager = plugin.getTeamSystem().getTeamManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("/team create <name>");
            player.sendMessage("/team invite <player>");
            player.sendMessage("/team list");
            player.sendMessage("/team info <team>");
            player.sendMessage("/team kick <player>");
            player.sendMessage("/team leave");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "list": {

                Set<Team> teams = teamManager.getTeamsByMember(player.getUniqueId());

                if (teams.isEmpty()) {
                    player.sendMessage("You are not in any teams.");
                    return true;
                }

                player.sendMessage("Your Teams:");

                for (Team team : teams) {
                    if (team.isOwner(player.getUniqueId())) {
                        player.sendMessage(" - " + team.getId() + " (Owner)");
                    } else {
                        player.sendMessage(" - " + team.getId());
                    }
                }

                return true;
            }

            case "info": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team info <team>");
                    return true;
                }

                Team team = teamManager.getTeam(args[1]);

                if (team == null) {
                    player.sendMessage("Team not found.");
                    return true;
                }

                player.sendMessage("Team: " + team.getId());
                player.sendMessage("Owner: " + Bukkit.getOfflinePlayer(team.getOwner()).getName());

                player.sendMessage("Members:");

                for (UUID member : team.getMembers()) {
                    String name = Bukkit.getOfflinePlayer(member).getName();
                    player.sendMessage(" - " + name);
                }

                return true;
            }

            case "transfer": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team transfer <player>");
                    return true;
                }

                Team team = teamManager.getOwnedTeam(player.getUniqueId());

                if (team == null) {
                    player.sendMessage("You do not own a team.");
                    return true;
                }

                Player newOwner = Bukkit.getPlayer(args[1]);

                if (newOwner == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }

                if (!team.isMember(newOwner.getUniqueId())) {
                    player.sendMessage("Player must be a team member first.");
                    return true;
                }

                teamManager.transferOwnership(team.getId(), newOwner.getUniqueId());

                player.sendMessage("Ownership transferred to " + newOwner.getName());
                newOwner.sendMessage("You are now owner of team " + team.getId());

                return true;
            }
            case "kick": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team kick <player>");
                    return true;
                }

                Team team = teamManager.getOwnedTeam(player.getUniqueId());

                if (team == null) {
                    player.sendMessage("You do not own a team.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }

                if (!team.isMember(target.getUniqueId())) {
                    player.sendMessage("That player is not in your team.");
                    return true;
                }

                if (team.isOwner(target.getUniqueId())) {
                    player.sendMessage("You cannot kick the team owner.");
                    return true;
                }

                teamManager.removeMember(team.getId(), target.getUniqueId());

                player.sendMessage(target.getName() + " was removed from the team.");
                target.sendMessage("You were removed from team " + team.getId());

                return true;
            }
            case "create": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team create <name>");
                    return true;
                }

                if (teamManager.getOwnedTeam(player.getUniqueId()) != null) {
                    player.sendMessage("You already own a team.");
                    return true;
                }

                String name = args[1];

                if (teamManager.teamExists(name)) {
                    player.sendMessage("That team already exists.");
                    return true;
                }

                teamManager.createTeam(name, player.getUniqueId());
                player.sendMessage("Team created: " + name);

                return true;
            }

            case "invite": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team invite <player>");
                    return true;
                }

                Team team = teamManager.getOwnedTeam(player.getUniqueId());

                if (team == null) {
                    player.sendMessage("You do not own a team.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }

                teamManager.invitePlayer(team.getId(), target.getUniqueId());

                player.sendMessage("Invite sent to " + target.getName());
                target.sendMessage("You have been invited to team " + team.getId());
                target.sendMessage("Use /team accept " + team.getId());

                return true;
            }

            case "accept": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team accept <team>");
                    return true;
                }

                String teamId = args[1];

                if (!teamManager.acceptInvite(teamId, player.getUniqueId())) {
                    player.sendMessage("You do not have an invite for that team.");
                    return true;
                }

                player.sendMessage("You joined team " + teamId);

                return true;
            }

            case "decline": {

                if (args.length < 2) {
                    player.sendMessage("Usage: /team decline <team>");
                    return true;
                }

                String declineTeam = args[1];

                if (!teamManager.declineInvite(declineTeam, player.getUniqueId())) {
                    player.sendMessage("No invite found.");
                    return true;
                }

                player.sendMessage("Invite declined.");

                return true;
            }

            case "leave": {

                Team current = teamManager.getTeamByMember(player.getUniqueId());

                if (current == null) {
                    player.sendMessage("You are not in a team.");
                    return true;
                }

                if (current.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage("Owners cannot leave their team.");
                    return true;
                }

                teamManager.removeMember(current.getId(), player.getUniqueId());

                player.sendMessage("You left the team.");

                return true;
            }

        }

        return true;
    }
}