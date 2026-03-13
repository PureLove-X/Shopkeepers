package com.nisovin.shopkeepers.teams;

import java.util.UUID;

public class TeamInvite {

    private final UUID teamId;
    private final UUID inviter;
    private final long timestamp;

    public TeamInvite(UUID teamId, UUID inviter) {
        this.teamId = teamId;
        this.inviter = inviter;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getTeamId() {
        return teamId;
    }

    public UUID getInviter() {
        return inviter;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

