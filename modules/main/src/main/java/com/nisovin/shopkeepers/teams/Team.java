package com.nisovin.shopkeepers.teams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {

    private final String id;
    private UUID owner;
    private final Set<UUID> members = new HashSet<>();

    public Team(String id, UUID owner) {
        this.id = id.toLowerCase();
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

}