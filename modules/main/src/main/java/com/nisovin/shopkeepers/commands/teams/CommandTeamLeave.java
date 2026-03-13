package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.arguments.teams.TeamArgument;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.arguments.StringArgument;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

class CommandTeamLeave extends Command {

    private static final String ARG_TEAM = "team";

    private final TeamManager teamManager;

    CommandTeamLeave(TeamManager teamManager) {
        super("leave");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamLeave);

        this.addArgument(new TeamArgument(ARG_TEAM, teamManager));
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player player = (Player) input.getSender();
        if (!context.has(ARG_TEAM)) {
            sendHelp(input.getSender());
            return;
        }

        Team team = context.get(ARG_TEAM);

        if (!team.isMember(player.getUniqueId())) {
            TextUtils.sendMessage(player, Messages.teamNotInTeam);
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            TextUtils.sendMessage(player, Messages.cannotLeaveOwnTeam);
            return;
        }


        teamManager.removeMember(team.getId(), player.getUniqueId());

        TextUtils.sendMessage(player, Messages.teamLeft);
    }
}