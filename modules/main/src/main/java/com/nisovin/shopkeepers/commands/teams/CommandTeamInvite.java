package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.arguments.PlayerArgument;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.util.bukkit.TextUtils;

import org.bukkit.entity.Player;

class CommandTeamInvite extends Command {

    private static final String ARG_PLAYER = "player";

    private final TeamManager teamManager;

    CommandTeamInvite(TeamManager teamManager) {
        super("invite");

        this.teamManager = teamManager;

        this.setDescription(Messages.commandDescriptionTeamInvite);

        this.addArgument(new PlayerArgument(ARG_PLAYER));
    }

    @Override
    protected void execute(CommandInput input, CommandContextView context) throws CommandException {

        Player sender = (Player) input.getSender();
        if (!context.has(ARG_PLAYER)) {
            sendHelp(input.getSender());
            return;
        }

        Player target = context.get(ARG_PLAYER);

        Team team = teamManager.getOwnedTeam(sender.getUniqueId());

        // Sender must own a team
        if (team == null) {
            TextUtils.sendMessage(sender, Messages.teamNotInTeam);
            return;
        }

        // Safety check (should always be true)
        if (!teamManager.isOwner(sender.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(sender, Messages.teamNotOwner);
            return;
        }

        // Target already in this team
        if (team.isMember(target.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.teamAlreadyIn);
            return;
        }
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            TextUtils.sendMessage(sender, Messages.teamCannotInviteSelf);
            return;
        }

        // Target already invited
        if (teamManager.hasInvite(target.getUniqueId(), team.getId())) {
            TextUtils.sendMessage(
                    sender,
                    Messages.teamInviteAlreadySent,
                    "name", team.getName()
            );
            return;
        }

        // Send invite
        teamManager.invitePlayer(
                team.getId(),
                sender.getUniqueId(),
                target.getUniqueId()
        );

        TextUtils.sendMessage(
                sender,
                Messages.teamInviteSent,
                "player", target.getName()
        );

        TextUtils.sendMessage(
                target,
                Messages.teamInviteReceived,
                "player", sender.getName(),
                "name", team.getName()
        );
    }
}
