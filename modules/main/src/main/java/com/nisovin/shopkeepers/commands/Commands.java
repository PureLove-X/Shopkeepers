package com.nisovin.shopkeepers.commands;

import com.nisovin.shopkeepers.commands.teams.TeamCommand;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.commands.shopkeepers.ShopkeepersCommand;
import com.nisovin.shopkeepers.util.java.Validate;

public class Commands {

	private final SKShopkeepersPlugin plugin;
	private final Confirmations confirmations;

	private @Nullable ShopkeepersCommand shopkeepersCommand;
	private @Nullable TeamCommand teamCommand;

	public Commands(SKShopkeepersPlugin plugin) {
		this.plugin = plugin;
		this.confirmations = new Confirmations(plugin);
	}

	public void onEnable() {
		confirmations.onEnable();
		// Register command executor:
		shopkeepersCommand = new ShopkeepersCommand(plugin, confirmations);
		// Register team command
		teamCommand = new TeamCommand(plugin, plugin.getTeamSystem().getTeamManager(), confirmations);
	}

	public void onDisable() {
		confirmations.onDisable();
	}

	public void onPlayerQuit(Player player) {
		assert player != null;
		confirmations.onPlayerQuit(player);
	}
	public TeamCommand getTeamCommand() {
		return Validate.State.notNull(teamCommand, "The team command has not yet been set up!");
	}
	public ShopkeepersCommand getShopkeepersCommand() {
		return Validate.State.notNull(shopkeepersCommand, "The commands have not yet been set up!");
	}
}
