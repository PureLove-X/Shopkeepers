package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.arguments.teams.TeamArgument;
import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamInvite;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

class CommandTeamAccept extends Command {

    private static final String ARG_TEAM = "team";

    private final TeamManager teamManager;

    CommandTeamAccept(TeamManager teamManager) {
        super("accept");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamAccept);

        this.addArgument(new TeamArgument(ARG_TEAM, teamManager).optional());
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player player = (Player) input.getSender();
        Team team;

        // If player specified a team
        if (context.has(ARG_TEAM)) {
            team = context.get(ARG_TEAM);
        } else {

            Map<UUID, TeamInvite> invites = teamManager.getInvites(player.getUniqueId());

            if (invites.isEmpty()) {
                TextUtils.sendMessage(player, Messages.noPendingInvite);
                return;
            }

            if (invites.size() > 1) {
                TextUtils.sendMessage(player, Messages.teamSpecifyTeam);
                sendHelp(input.getSender());
                return;
            }

            UUID teamId = invites.keySet().iterator().next();
            team = teamManager.getTeam(teamId);

            if (team == null) {
                TextUtils.sendMessage(player, Messages.teamNotFound);
                return;
            }
        }
        if (teamManager.isTeamMember(player.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(player, Messages.teamAlreadyIn);
            return;
        }

        // Check expiration
        if (teamManager.inviteExpired(player.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(player, Messages.teamInviteExpired);
            return;
        }

        // Grab invite before removing it (so we know who invited)
        TeamInvite invite = teamManager.getInvite(player.getUniqueId(), team.getId());

        boolean success = teamManager.acceptInvite(team.getId(), player.getUniqueId());

        if (!success) {
            TextUtils.sendMessage(player, Messages.noPendingInvite);
            return;
        }

        // Notify inviter
        if (invite != null) {
            Player inviter = Bukkit.getPlayer(invite.getInviter());

            if (inviter != null) {
                TextUtils.sendMessage(
                        inviter,
                        Messages.teamInviteAccepted,
                        "player", player.getName(),
                        "team", team.getName()
                );
            }
        }

        TextUtils.sendMessage(
                player,
                Messages.teamJoined,
                "name", team.getName()
        );
    }
}
