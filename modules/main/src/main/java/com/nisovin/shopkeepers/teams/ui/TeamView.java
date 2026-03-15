package com.nisovin.shopkeepers.teams.ui;

import com.nisovin.shopkeepers.shopkeeper.player.AbstractPlayerShopkeeper;
import com.nisovin.shopkeepers.teams.TeamManager;
import com.nisovin.shopkeepers.teams.TeamSystem;
import com.nisovin.shopkeepers.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.ui.lib.UIState;
import com.nisovin.shopkeepers.ui.lib.View;
import com.nisovin.shopkeepers.util.logging.Log;
import java.util.UUID;

public class TeamView extends View {

    private final AbstractPlayerShopkeeper shopkeeper;
    private TeamUIScreen screen;
    private java.util.List<com.nisovin.shopkeepers.teams.Team> displayedTeams;

    public TeamView(
            TeamViewProvider provider,
            Player player,
            UIState uiState
    ) {
        super(provider, player, uiState);

        this.shopkeeper = (AbstractPlayerShopkeeper) provider.getContext().getObject();
    }
    private TeamManager teamManager() {
        return TeamSystem.get().getTeamManager();
    }
    @Override
    public InventoryView openInventoryView() {

        Player player = getPlayer();

        if (!teamManager().isInAnyTeam(player.getUniqueId())) {
            screen = TeamUIScreen.CREATE;
        }
        else if (shopkeeper.getTeamUUID() == null) {
            screen = TeamUIScreen.SELECT;
        }
        else {
            screen = TeamUIScreen.DASHBOARD;
        }

        String title = switch (screen) {
            case CREATE -> "Create Team";
            case SELECT -> "Select Team";
            case DASHBOARD -> "Team Dashboard";
        };

        Inventory inventory = Bukkit.createInventory(null, 54, title);

        // Initial contents must be drawn here:
        drawScreen(inventory);

        return player.openInventory(inventory);
    }

    @Override
    public void updateInventory() {

        Inventory inv = getInventory();
        inv.clear();

        drawScreen(inv);

        syncInventory();
    }
    private void reopen() {

        var provider = new TeamViewProvider(
                TeamUIType.INSTANCE,
                getContext()
        );

        closeDelayedAndRunTask(() ->
                com.nisovin.shopkeepers.ui.lib.UISessionManager.getInstance().requestUI(
                        provider,
                        getPlayer(),
                        UIState.EMPTY
                )
        );
    }
    private void drawScreen(Inventory inv) {

        switch (screen) {
            case CREATE -> drawCreateScreen(inv);
            case SELECT -> drawTeamSelection(inv);
            case DASHBOARD -> drawDashboard(inv);
        }
    }
    private void drawCreateScreen(Inventory inv) {

        ItemStack create = ItemUtils.createItemStack(
                Material.EMERALD_BLOCK,
                1,
                "§aCreate Team",
                null
        );

        ItemStack back = new ItemStack(Material.ARROW);

        inv.setItem(22, create);
        inv.setItem(45, back);
    }
    private void drawTeamSelection(Inventory inv) {

        displayedTeams = new java.util.ArrayList<>(
                teamManager().getTeamsByMember(getPlayer().getUniqueId())
        );

        int slot = 0;

        for (var team : displayedTeams) {

            ItemStack icon = ItemUtils.createItemStack(
                    Material.BOOK,
                    1,
                    "§e" + team.getName(),
                    null
            );

            inv.setItem(slot++, icon);
        }

        inv.setItem(45, new ItemStack(Material.ARROW));
    }
    private void drawDashboardControls(Inventory inv) {

        inv.setItem(45, new ItemStack(Material.ARROW));      // Back
        inv.setItem(46, new ItemStack(Material.PLAYER_HEAD)); // Members
        inv.setItem(48, new ItemStack(Material.LEVER));       // Toggle mode
        inv.setItem(50, new ItemStack(Material.NAME_TAG));    // Rename
        ItemStack remove = ItemUtils.createItemStack(
                Material.REDSTONE,
                1,
                "§cRemove Team From Shop",
                java.util.List.of("§7This does not delete the team")
        );

        inv.setItem(52, remove);
    }
    private void drawDashboard(Inventory inv) {

        var team = teamManager().getTeam(shopkeeper);
        if (team == null) return;

        int slot = 0;

        for (UUID memberId : team.getMembers()) {

            if (slot >= 36) break;

            var player = Bukkit.getOfflinePlayer(memberId);

            ItemStack head = ItemUtils.createItemStack(
                    Material.PLAYER_HEAD,
                    1,
                    "§e" + player.getName(),
                    null
            );

            inv.setItem(slot++, head);
        }

        drawDashboardControls(inv);
    }


    @Override
    public void onInventoryClickEarly(InventoryClickEvent event) {

        event.setCancelled(true);

        int slot = event.getRawSlot();

        switch (screen) {

            case CREATE -> {
                if (slot == 22) createTeam();
                if (slot == 45) getPlayer().closeInventory();
            }

            case SELECT -> {
                if (slot == 45) getPlayer().closeInventory();
                selectTeam(slot);
            }

            case DASHBOARD -> {
                if (slot == 45) getPlayer().closeInventory();
                //if (slot == 46) openMembers();
                if (slot == 48) toggleTeamMode();
                //if (slot == 50) renameTeam();
                if (slot == 52) removeTeamFromShop();
            }
        }
    }
    private void selectTeam(int slot) {

        if (displayedTeams == null) return;
        if (slot >= displayedTeams.size()) return;

        var team = displayedTeams.get(slot);

        shopkeeper.setTeamUUID(team.getId());
        shopkeeper.markDirty();

        screen = TeamUIScreen.DASHBOARD;

        reopen();
    }

    private void createTeam() {
        Log.info("Creating Team");

        var team = teamManager().createTeam(
                getPlayer().getName() + "'s Team",
                getPlayer().getUniqueId()
        );

        shopkeeper.setTeamUUID(team.getId());
        shopkeeper.markDirty();

        screen = TeamUIScreen.DASHBOARD;

        reopen();
    }

    private void toggleTeamMode() {
        Log.info("Toggleing Team Mode");
        boolean newMode = !shopkeeper.isTeamMode();
        shopkeeper.setTeamMode(newMode);

        updateInventory();
    }

    private void removeTeamFromShop() {

        Log.info("Removing team from shop");

        shopkeeper.setTeamUUID(null);
        shopkeeper.setTeamMode(false);
        shopkeeper.markDirty();

        screen = TeamUIScreen.SELECT;

        reopen();
    }
}