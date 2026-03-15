package com.nisovin.shopkeepers.teams.ui;

import com.nisovin.shopkeepers.ui.lib.AbstractUIType;

public class TeamUIType extends AbstractUIType {

    public static final TeamUIType INSTANCE = new TeamUIType();

    private TeamUIType() {
        super("team_selection", null);
    }
}
