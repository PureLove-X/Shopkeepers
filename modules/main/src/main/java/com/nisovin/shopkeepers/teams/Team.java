package com.nisovin.shopkeepers.teams;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {

    private final UUID id;
    private String name;
    private UUID owner;
    private final Set<UUID> members = new HashSet<>();

    public Team(UUID id, String name, UUID owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;

        members.add(owner);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID newOwner) {
        this.owner = newOwner;
        members.add(newOwner);
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }
}