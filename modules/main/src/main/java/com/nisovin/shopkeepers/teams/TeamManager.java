package com.nisovin.shopkeepers.teams;

import java.util.*;

public class TeamManager {

    private final Map<String, Team> teams = new HashMap<>();
    private final Map<UUID, String> ownerTeams = new HashMap<>();
    private final Map<UUID, Set<String>> memberTeams = new HashMap<>();
    private final Map<UUID, Set<String>> pendingInvites = new HashMap<>();
    public Team createTeam(String id, UUID owner) {

        id = id.toLowerCase();

        if (teams.containsKey(id)) {
            throw new IllegalArgumentException("Team already exists");
        }

        if (ownerTeams.containsKey(owner)) {
            throw new IllegalStateException("Player already owns a team");
        }

        Team team = new Team(id, owner);

        teams.put(id, team);
        ownerTeams.put(owner, id);

        memberTeams.computeIfAbsent(owner, k -> new HashSet<>()).add(id);

        return team;
    }

    public Team getTeam(String id) {
        if (id == null) return null;
        return teams.get(id.toLowerCase());
    }

    public boolean teamExists(String id) {
        return teams.containsKey(id.toLowerCase());
    }

    public void deleteTeam(String id) {

        id = id.toLowerCase();

        Team team = teams.remove(id);

        if (team != null) {

            ownerTeams.remove(team.getOwner());

            for (UUID member : team.getMembers()) {

                Set<String> teams = memberTeams.get(member);

                if (teams != null) {
                    teams.remove(id);

                    if (teams.isEmpty()) {
                        memberTeams.remove(member);
                    }
                }
            }
        }
    }

    public boolean isTeamOwner(UUID player, String teamId) {

        Team team = getTeam(teamId);

        if (team == null) return false;

        return team.isOwner(player);
    }

    public boolean isTeamMember(UUID player, String teamId) {

        Team team = getTeam(teamId);

        if (team == null) return false;

        return team.isMember(player);
    }

    public void transferOwnership(String teamId, UUID newOwner) {

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
        String teamId = ownerTeams.get(owner);
        if (teamId == null) return null;
        return teams.get(teamId);
    }

    public Team getTeamByMember(UUID member) {
        for (Team team : teams.values()) {
            if (team.isMember(member)) {
                return team;
            }
        }
        return null;
    }

    public boolean isInAnyTeam(UUID player) {
        if (ownerTeams.containsKey(player)) {
            return true;
        }

        return getTeamByMember(player) != null;
    }
    public void addMember(String teamId, UUID player) {

        Team team = getTeam(teamId);
        if (team == null) return;

        team.addMember(player);

        memberTeams.computeIfAbsent(player, k -> new HashSet<>()).add(teamId);
    }
    public void removeMember(String teamId, UUID player) {

        Team team = getTeam(teamId);
        if (team == null) return;

        team.removeMember(player);

        Set<String> teams = memberTeams.get(player);

        if (teams != null) {
            teams.remove(teamId);

            if (teams.isEmpty()) {
                memberTeams.remove(player);
            }
        }
    }
    public Set<Team> getTeamsByMember(UUID player) {

        Set<String> ids = memberTeams.get(player);

        if (ids == null) return Collections.emptySet();

        Set<Team> result = new HashSet<>();

        for (String id : ids) {
            Team team = teams.get(id);
            if (team != null) result.add(team);
        }

        return result;
    }
    public void invitePlayer(String teamId, UUID player) {

        if (!teamExists(teamId)) return;

        pendingInvites
                .computeIfAbsent(player, k -> new HashSet<>())
                .add(teamId.toLowerCase());
    }
    public boolean acceptInvite(String teamId, UUID player) {

        if (!hasInvite(player, teamId)) {
            return false;
        }

        Set<String> invites = pendingInvites.get(player);
        invites.remove(teamId.toLowerCase());

        if (invites.isEmpty()) {
            pendingInvites.remove(player);
        }

        addMember(teamId, player);

        return true;
    }
    public boolean declineInvite(String teamId, UUID player) {

        if (!hasInvite(player, teamId)) {
            return false;
        }

        Set<String> invites = pendingInvites.get(player);
        invites.remove(teamId.toLowerCase());

        if (invites.isEmpty()) {
            pendingInvites.remove(player);
        }

        return true;
    }
    public Set<String> getInvites(UUID player) {

        return pendingInvites.getOrDefault(player, Collections.emptySet());
    }
    public boolean hasInvite(UUID player, String teamId) {

        Set<String> invites = pendingInvites.get(player);

        if (invites == null) {
            return false;
        }

        return invites.contains(teamId.toLowerCase());
    }
    public boolean shareTeam(UUID a, UUID b) {

        if (a == null || b == null) {
            return false;
        }

        // Same player shortcut
        if (a.equals(b)) {
            return true;
        }

        Set<String> teamsA = memberTeams.get(a);
        if (teamsA == null || teamsA.isEmpty()) {
            return false;
        }

        Set<String> teamsB = memberTeams.get(b);
        if (teamsB == null || teamsB.isEmpty()) {
            return false;
        }

        for (String teamId : teamsA) {
            if (teamsB.contains(teamId)) {
                return true;
            }
        }

        return false;
    }
}
