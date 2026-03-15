package com.nisovin.shopkeepers.teams;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.shopkeeper.player.AbstractPlayerShopkeeper;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSystem {

    private static TeamSystem instance;

    private final TeamManager teamManager;
    private final TeamStorage teamStorage;

    public TeamSystem(SKShopkeepersPlugin plugin) {
        instance = this;

        this.teamManager = new TeamManager();
        this.teamStorage = new TeamStorage(plugin, teamManager);
    }

    public static TeamSystem get() {
        return instance;
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

    /*
     * Public API helpers
     */

    public Team getTeam(AbstractPlayerShopkeeper shopkeeper) {
        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return null;
        return teamManager.getTeam(teamId);
    }

    public boolean isTeamMember(UUID player, AbstractPlayerShopkeeper shopkeeper) {
        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return false;
        return teamManager.isTeamMember(player, teamId);
    }

    public boolean isTeamMember(Player player, AbstractPlayerShopkeeper shopkeeper) {
        return isTeamMember(player.getUniqueId(), shopkeeper);
    }

    public boolean canAccessShop(Player player, AbstractPlayerShopkeeper shopkeeper) {

        if (shopkeeper.isOwner(player)) return true;
        if (!shopkeeper.isTeamMode()) return false;

        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return false;

        return teamManager.isTeamMember(player.getUniqueId(), teamId);
    }
}