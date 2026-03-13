package com.nisovin.shopkeepers.commands.teams;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.commands.Confirmations;
import com.nisovin.shopkeepers.commands.lib.BaseCommand;
import com.nisovin.shopkeepers.commands.lib.CommandRegistry;
import com.nisovin.shopkeepers.commands.lib.commands.HelpCommand;
import com.nisovin.shopkeepers.api.ShopkeepersPlugin;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.api.internal.util.Unsafe;
import com.nisovin.shopkeepers.teams.TeamManager;

public class TeamCommand extends BaseCommand {

    public TeamCommand(SKShopkeepersPlugin plugin, TeamManager teamManager, Confirmations confirmations) {
        super(plugin, "team");

        this.setPermission(ShopkeepersPlugin.TEAM_PERMISSION);
        this.setDescription(Messages.commandDescriptionTeam);

        CommandRegistry childCommands = this.getChildCommands();

        childCommands.register(new HelpCommand("help", Unsafe.initialized(this)));

        childCommands.register(new CommandTeamCreate(teamManager));
        childCommands.register(new CommandTeamInvite(teamManager));
        childCommands.register(new CommandTeamAccept(teamManager));
        childCommands.register(new CommandTeamDeny(teamManager));
        childCommands.register(new CommandTeamKick(teamManager));
        childCommands.register(new CommandTeamLeave(teamManager));
        childCommands.register(new CommandTeamInfo(teamManager));
        childCommands.register(new CommandTeamTransfer(teamManager));
        childCommands.register(new CommandTeamList(teamManager));
    }

}