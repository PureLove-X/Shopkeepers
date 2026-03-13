package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.arguments.teams.TeamArgument;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.arguments.PlayerArgument;
import com.nisovin.shopkeepers.commands.lib.arguments.StringArgument;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

import java.util.Set;

class CommandTeamKick extends Command {

    private static final String ARG_TEAM = "team";
    private static final String ARG_PLAYER = "player";

    private final TeamManager teamManager;

    CommandTeamKick(TeamManager teamManager) {
        super("kick");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamKick);

        this.addArgument(new PlayerArgument(ARG_PLAYER));
        this.addArgument(new TeamArgument(ARG_TEAM, teamManager).optional());
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player sender = (Player) input.getSender();
        if (!context.has(ARG_PLAYER)) {
            sendHelp(input.getSender());
            return;
        }

        Player target = context.get(ARG_PLAYER);

        Team team;

        if (context.has(ARG_TEAM)) {
            team = context.get(ARG_TEAM);
        } else {

            Set<Team> teams = teamManager.getTeamsByMember(sender.getUniqueId());

            if (teams.isEmpty()) {
                TextUtils.sendMessage(sender, Messages.teamNotInTeam);
                return;
            }

            if (teams.size() > 1) {
                TextUtils.sendMessage(sender, Messages.teamSpecifyTeam);
                return;
            }

            team = teams.iterator().next();
        }


        if (!teamManager.isOwner(sender.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(sender, Messages.teamNotOwner);
            return;
        }
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.cannotKickSelf);
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.teamPlayerNotFound);
            return;
        }

        if (team.isOwner(target.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.cannotKickOwner);
            return;
        }

        teamManager.removeMember(team.getId(), target.getUniqueId());

        TextUtils.sendMessage(sender,
                Messages.teamPlayerKicked,
                "player", target.getName()
        );

        TextUtils.sendMessage(target, Messages.teamKicked);
    }
}