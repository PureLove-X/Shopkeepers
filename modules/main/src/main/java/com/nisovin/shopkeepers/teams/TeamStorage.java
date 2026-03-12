package com.nisovin.shopkeepers.teams;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;

public class TeamStorage {

    private final SKShopkeepersPlugin plugin;
    private final TeamManager teamManager;

    private File file;
    private YamlConfiguration config;

    public TeamStorage(SKShopkeepersPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    public void load() {

        File dataFolder = new File(plugin.getDataFolder(), "data");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        file = new File(dataFolder, "teams.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection teamsSection = config.getConfigurationSection("teams");

        if (teamsSection == null) return;

        for (String teamId : teamsSection.getKeys(false)) {

            ConfigurationSection teamSection = teamsSection.getConfigurationSection(teamId);
            if (teamSection == null) continue;

            String ownerString = teamSection.getString("owner");

            if (ownerString == null) continue;

            UUID owner = UUID.fromString(ownerString);

            Team team = teamManager.createTeam(teamId, owner);

            for (String member : teamSection.getStringList("members")) {
                team.addMember(UUID.fromString(member));
            }
        }
    }

    public void save() {

        if (config == null) {
            config = new YamlConfiguration();
        }

        config.set("teams", null);

        ConfigurationSection teamsSection = config.createSection("teams");

        for (Team team : teamManager.getTeams()) {

            ConfigurationSection teamSection = teamsSection.createSection(team.getId());

            teamSection.set("owner", team.getOwner().toString());

            teamSection.set("members", team.getMembers().stream()
                    .map(UUID::toString)
                    .toList());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}