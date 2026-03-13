package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.arguments.teams.TeamArgument;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.arguments.PlayerArgument;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

class CommandTeamTransfer extends Command {

    private static final String ARG_TEAM = "team";
    private static final String ARG_PLAYER = "player";

    private final TeamManager teamManager;

    CommandTeamTransfer(TeamManager teamManager) {
        super("transfer");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamTransfer);

        this.addArgument(new TeamArgument(ARG_TEAM, teamManager));
        this.addArgument(new PlayerArgument(ARG_PLAYER));
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player sender = (Player) input.getSender();
        if (!context.has(ARG_PLAYER)) {
            sendHelp(input.getSender());
            return;
        }
        Team team = context.get(ARG_TEAM);
        Player newOwner = context.get(ARG_PLAYER);


        if (!teamManager.isOwner(sender.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(sender, Messages.teamNotOwner);
            return;
        }

        if (!team.isMember(newOwner.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.teamPlayerNotFound);
            return;
        }

        teamManager.transferOwnership(team.getId(), newOwner.getUniqueId());

        TextUtils.sendMessage(sender,
                Messages.teamOwnershipTransferred,
                "player", newOwner.getName()
        );
    }
}