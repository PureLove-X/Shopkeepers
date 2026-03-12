package com.nisovin.shopkeepers.teams;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;

public class TeamSystem {

    private final SKShopkeepersPlugin plugin;

    private final TeamManager teamManager;
    private final TeamStorage teamStorage;

    public TeamSystem(SKShopkeepersPlugin plugin) {
        this.plugin = plugin;

        this.teamManager = new TeamManager();
        this.teamStorage = new TeamStorage(plugin, teamManager);
    }

    public void onEnable() {
        teamStorage.load();
    }

    public void onDisable() {
        teamStorage.save();
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

}