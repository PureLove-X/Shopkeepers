package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.arguments.teams.TeamArgument;
import org.bukkit.Bukkit;
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

import java.util.Set;

class CommandTeamInfo extends Command {

    private static final String ARG_TEAM = "team";

    private final TeamManager teamManager;

    CommandTeamInfo(TeamManager teamManager) {
        super("info");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamInfo);

        this.addArgument(new TeamArgument(ARG_TEAM, teamManager).optional());
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player player = (Player) input.getSender();
        Team team;

        if (context.has(ARG_TEAM)) {
            team = context.get(ARG_TEAM);
        } else {

            Set<Team> teams = teamManager.getTeamsByMember(player.getUniqueId());

            if (teams.isEmpty()) {
                TextUtils.sendMessage(player, Messages.teamNotInTeam);
                return;
            }

            if (teams.size() > 1) {
                TextUtils.sendMessage(player, Messages.teamSpecifyTeam);
                sendHelp(input.getSender());
                return;
            }

            team = teams.iterator().next();
        }

        TextUtils.sendMessage(player,
                Messages.teamInfoHeader,
                "name", team.getName()
        );

        String ownerName = Bukkit.getOfflinePlayer(team.getOwner()).getName();

        TextUtils.sendMessage(player,
                Messages.teamInfoOwner,
                "owner", ownerName
        );

        String members = team.getMembers()
                .stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");

        TextUtils.sendMessage(player,
                Messages.teamInfoMembers,
                "members", members
        );
    }
}