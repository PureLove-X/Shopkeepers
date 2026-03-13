package com.nisovin.shopkeepers.commands.arguments.teams;

import java.util.List;

import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.argument.ArgumentParseException;
import com.nisovin.shopkeepers.commands.lib.argument.ArgumentsReader;
import com.nisovin.shopkeepers.commands.lib.argument.CommandArgument;
import com.nisovin.shopkeepers.commands.lib.context.CommandContextView;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.teams.Team;
import com.nisovin.shopkeepers.teams.TeamManager;
import org.bukkit.entity.Player;

public class TeamArgument extends CommandArgument<Team> {

    private final TeamManager teamManager;

    public TeamArgument(String name, TeamManager teamManager) {
        super(name);
        this.teamManager = teamManager;
    }

    @Override
    public Team parseValue(
            CommandInput input,
            CommandContextView context,
            ArgumentsReader argsReader
    ) throws ArgumentParseException {

        if (!argsReader.hasNext()) {
            return null;
        }

        String teamName = argsReader.next();

        Team team = teamManager.getTeamByName(teamName);

        if (team == null) {
            throw new ArgumentParseException(this, Messages.teamNotFound);
        }

        return team;
    }


    @Override
    public List<? extends String> complete(
            CommandInput input,
            CommandContextView context,
            ArgumentsReader argsReader
    ) {

        String prefix = argsReader.peekIfPresent();
        if (prefix == null) prefix = "";

        final String lowerPrefix = prefix.toLowerCase();

        return teamManager.getTeamsByMember(((Player) input.getSender()).getUniqueId())

                .stream()
                .map(Team::getName)
                .filter(name -> name.regionMatches(true, 0, lowerPrefix, 0, lowerPrefix.length()))
                .toList();
    }

}