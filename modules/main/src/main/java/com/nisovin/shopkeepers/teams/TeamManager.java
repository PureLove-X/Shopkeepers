package com.nisovin.shopkeepers.teams;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.shopkeeper.player.AbstractPlayerShopkeeper;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamManager {

    private final Map<UUID, Team> teams = new HashMap<>();
    private final Map<UUID, UUID> ownerTeams = new HashMap<>();
    private final Map<UUID, Set<UUID>> memberTeams = new HashMap<>();
    private final Map<UUID, Map<UUID, TeamInvite>> invites = new HashMap<>();
    private static final long INVITE_TIMEOUT = 5 * 60 * 1000; // 5 minutes
    private TeamManager getTeamManager() {
        return SKShopkeepersPlugin.getInstance()
                .getTeamSystem()
                .getTeamManager();
    }
    public Team createTeam(String name, UUID owner) {

        if (ownerTeams.containsKey(owner)) {
            throw new IllegalStateException("Player already owns a team");
        }

        UUID teamId = UUID.randomUUID();

        Team team = new Team(teamId, name, owner);

        teams.put(teamId, team);
        ownerTeams.put(owner, teamId);

        memberTeams.computeIfAbsent(owner, k -> new HashSet<>()).add(teamId);

        return team;
    }

    public Team loadTeam(UUID id, String name, UUID owner) {

        Team team = new Team(id, name, owner);

        teams.put(id, team);
        ownerTeams.put(owner, id);

        memberTeams.computeIfAbsent(owner, k -> new HashSet<>()).add(id);

        return team;
    }
    public Team getTeam(UUID id) {
        if (id == null) return null;
        return teams.get(id);
    }
    public Team getTeamByName(String name) {
        for (Team team : teams.values()) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }
    public boolean teamExists(UUID id) {
        return teams.containsKey(id);
    }

    public void deleteTeam(UUID teamId) {

        Team team = teams.remove(teamId);
        invites.values().forEach(map -> map.remove(teamId));
        if (team != null) {

            ownerTeams.remove(team.getOwner());

            for (UUID member : team.getMembers()) {

                Set<UUID> teams = memberTeams.get(member);

                if (teams != null) {
                    teams.remove(teamId);

                    if (teams.isEmpty()) {
                        memberTeams.remove(member);
                    }
                }
            }
        }
    }

    public boolean isTeamMember(UUID player, UUID teamId) {

        Set<UUID> teams = memberTeams.get(player);

        if (teams == null) return false;

        return teams.contains(teamId);
    }

    public void transferOwnership(UUID teamId, UUID newOwner) {

        Team team = getTeam(teamId);

        if (team == null) return;

        ownerTeams.remove(team.getOwner());

        team.setOwner(newOwner);

        ownerTeams.put(newOwner, teamId);
    }

    public Collection<Team> getTeams() {
        return teams.values();
    }

    public Team getOwnedTeam(UUID owner) {
        UUID teamId = ownerTeams.get(owner);
        if (teamId == null) return null;
        return teams.get(teamId);
    }


    public boolean isInAnyTeam(UUID player) {
        return memberTeams.containsKey(player);
    }

    public void addMember(UUID teamId, UUID player) {

        Team team = getTeam(teamId);
        if (team == null) return;

        team.addMember(player);

        memberTeams.computeIfAbsent(player, k -> new HashSet<>()).add(teamId);
    }

    public void removeMember(UUID teamId, UUID player) {

        Team team = getTeam(teamId);
        if (team == null) return;

        team.removeMember(player);

        Set<UUID> teams = memberTeams.get(player);

        if (teams != null) {
            teams.remove(teamId);

            if (teams.isEmpty()) {
                memberTeams.remove(player);
            }
        }
    }

    public Set<Team> getTeamsByMember(UUID player) {

        Set<UUID> ids = memberTeams.get(player);

        if (ids == null) return Collections.emptySet();

        Set<Team> result = new HashSet<>();

        for (UUID id : ids) {
            Team team = teams.get(id);
            if (team != null) result.add(team);
        }

        return result;
    }

    public boolean shareTeam(UUID a, UUID b) {

        if (a == null || b == null) return false;

        if (a.equals(b)) return true;

        Set<UUID> teamsA = memberTeams.get(a);
        if (teamsA == null || teamsA.isEmpty()) return false;

        Set<UUID> teamsB = memberTeams.get(b);
        if (teamsB == null || teamsB.isEmpty()) return false;

        for (UUID teamId : teamsA) {
            if (teamsB.contains(teamId)) {
                return true;
            }
        }

        return false;
    }
    public boolean isOwner(UUID player, UUID teamId) {
        Team team = teams.get(teamId);
        if (team == null) return false;
        return team.getOwner().equals(player);
    }
    public boolean canManageTeam(UUID player, UUID teamId) {
        return isOwner(player, teamId);
    }

    private void cleanupExpiredInvites(UUID player) {

        Map<UUID, TeamInvite> playerInvites = invites.get(player);

        if (playerInvites == null) return;

        playerInvites.entrySet().removeIf(entry -> isInviteExpired(entry.getValue()));

        if (playerInvites.isEmpty()) {
            invites.remove(player);
        }
    }
    public Map<UUID, TeamInvite> getInvites(UUID playerId) {

        cleanupExpiredInvites(playerId);

        Map<UUID, TeamInvite> playerInvites = invites.get(playerId);

        if (playerInvites == null) {
            return Collections.emptyMap();
        }

        return playerInvites;
    }

    public void invitePlayer(UUID teamId, UUID inviter, UUID playerId) {

        invites
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(teamId, new TeamInvite(teamId, inviter));
    }
    public TeamInvite getInvite(UUID playerId, UUID teamId) {

        Map<UUID, TeamInvite> playerInvites = invites.get(playerId);
        if (playerInvites == null) return null;

        return playerInvites.get(teamId);
    }


    public boolean acceptInvite(UUID teamId, UUID playerId) {

        Map<UUID, TeamInvite> playerInvites = invites.get(playerId);
        if (playerInvites == null) return false;

        TeamInvite invite = playerInvites.remove(teamId);
        if (invite == null) return false;
        if (playerInvites.isEmpty()) {
            invites.remove(playerId);
        }

        addMember(teamId, playerId);

        return true;
    }

    public TeamInvite declineInvite(UUID teamId, UUID playerId) {

        Map<UUID, TeamInvite> playerInvites = invites.get(playerId);
        if (playerInvites == null) return null;

        if (playerInvites.isEmpty()) {
            invites.remove(playerId);
        }
        return playerInvites.remove(teamId);
    }
    public boolean hasInvite(UUID player, UUID teamId) {

        cleanupExpiredInvites(player);

        Map<UUID, TeamInvite> playerInvites = invites.get(player);

        if (playerInvites == null) return false;

        return playerInvites.containsKey(teamId);
    }


    public boolean inviteExpired(UUID playerId, UUID teamId) {

        TeamInvite invite = getInvite(playerId, teamId);
        if (invite == null) return false;

        long age = System.currentTimeMillis() - invite.getTimestamp();
        boolean expired = age > INVITE_TIMEOUT;

        if (expired) {
            Map<UUID, TeamInvite> playerInvites = invites.get(playerId);
            if (playerInvites != null) {
                playerInvites.remove(teamId);
                if (playerInvites.isEmpty()) {
                    invites.remove(playerId);
                }
            }
        }

        return expired;
    }
    private boolean isInviteExpired(TeamInvite invite) {

        long age = System.currentTimeMillis() - invite.getTimestamp();

        return age > INVITE_TIMEOUT;
    }
    /*
     * ---------------------------------------
     * Shop-aware helper methods
     * ---------------------------------------
     */

    public boolean hasTeam(AbstractPlayerShopkeeper shopkeeper) {
        return shopkeeper.getTeamUUID() != null;
    }

    public Team getTeam(AbstractPlayerShopkeeper shopkeeper) {

        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return null;

        return getTeam(teamId);
    }

    public boolean isTeamMember(UUID playerUUID, AbstractPlayerShopkeeper shopkeeper) {

        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return false;

        return isTeamMember(playerUUID, teamId);
    }

    public boolean isTeamMember(Player player, AbstractPlayerShopkeeper shopkeeper) {
        return isTeamMember(player.getUniqueId(), shopkeeper);
    }

    public boolean isTeamOwner(UUID playerUUID, AbstractPlayerShopkeeper shopkeeper) {

        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) return false;

        return isOwner(playerUUID, teamId);
    }

    public boolean canAccessShop(Player player, AbstractPlayerShopkeeper shopkeeper) {

        if (shopkeeper.isOwner(player)) {
            return true;
        }

        if (!shopkeeper.isTeamMode()) {
            return false;
        }

        UUID teamId = shopkeeper.getTeamUUID();
        if (teamId == null) {
            return false;
        }

        return isTeamMember(player.getUniqueId(), teamId);
    }
}