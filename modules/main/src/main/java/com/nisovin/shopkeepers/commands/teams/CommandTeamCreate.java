package com.nisovin.shopkeepers.commands.teams;

import java.util.List;

import com.nisovin.shopkeepers.commands.lib.arguments.StringArgument;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;
import org.eclipse.jdt.annotation.NonNull;

class CommandTeamCreate extends Command {

    private final TeamManager teamManager;

    private static final String ARG_NAME = "name";

    CommandTeamCreate(TeamManager teamManager) {
        super("create");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamCreate);

        this.addArgument(new StringArgument(ARG_NAME));
    }


    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player player = (Player) input.getSender();
        if (!context.has(ARG_NAME)) {
            sendHelp(input.getSender());
            return;
        }

        String teamName = context.get(ARG_NAME);

        if (teamManager.getTeamByName(teamName) != null) {
            TextUtils.sendMessage(player, Messages.teamAlreadyExists);
            return;
        }

        if (teamManager.getOwnedTeam(player.getUniqueId()) != null) {
            TextUtils.sendMessage(player, Messages.alreadyOwnTeam);
            return;
        }

        teamManager.createTeam(teamName, player.getUniqueId());

        TextUtils.sendMessage(player, Messages.teamCreated, "name", teamName);
    }

}