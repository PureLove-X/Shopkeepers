package com.nisovin.shopkeepers.teams.ui;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.nisovin.shopkeepers.shopkeeper.AbstractShopkeeper;
import com.nisovin.shopkeepers.ui.lib.AbstractUIType;
import com.nisovin.shopkeepers.ui.lib.UIState;
import com.nisovin.shopkeepers.ui.lib.View;
import com.nisovin.shopkeepers.ui.lib.ViewContext;
import com.nisovin.shopkeepers.ui.lib.ViewProvider;

public class TeamViewProvider extends ViewProvider {

    public TeamViewProvider(AbstractUIType uiType, ViewContext context) {
        super(uiType, context);
    }

    @Override
    public boolean canAccess(@NonNull Player player, boolean silent) {

        Object contextObject = this.getContext().getObject();

        if (!(contextObject instanceof AbstractShopkeeper shopkeeper)) {
            return false;
        }

        return shopkeeper.isValid();
    }

    @Override
    protected @Nullable View createView(@NonNull Player player, @NonNull UIState uiState) {
        return new TeamView(this, player, uiState);
    }
}