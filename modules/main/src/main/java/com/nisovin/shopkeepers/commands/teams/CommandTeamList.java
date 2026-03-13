package com.nisovin.shopkeepers.commands.teams;

import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

class CommandTeamList extends Command {

    private final TeamManager teamManager;

    CommandTeamList(TeamManager teamManager) {
        super("list");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamList);
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) {

        Player player = (Player) input.getSender();

        if (teamManager.getTeams().isEmpty()) {
            TextUtils.sendMessage(player, Messages.noTeamsExist);
            return;
        }

        TextUtils.sendMessage(player, Messages.teamListHeader);

        for (Team team : teamManager.getTeams()) {
            TextUtils.sendMessage(
                    player,
                    Messages.teamListEntry,
                    "name", team.getName()
            );
        }
    }
}