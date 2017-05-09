package net.cjservers.betterrepair.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.reflect.TypeToken;

import net.cjservers.betterrepair.Main;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class AdminCommands implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src instanceof Player) {
			Player p = (Player) src;
			if (args.getAll("subcommand").contains("add")) {
				if (!(p.hasPermission("betterrepair.add"))) {
					src.sendMessage(Text.builder("You do not have permission!").color(TextColors.RED).build());
					return CommandResult.success();
				}
				Optional<ItemStack> itemO = p.getItemInHand(HandTypes.MAIN_HAND);
				if (itemO.isPresent()) {
					ItemStack item = itemO.get();
					String name = item.getItem().getName();
					if (Main.instance.disabled.contains(name)) {
						src.sendMessage(Text.builder("Item is already on the list!").color(TextColors.RED).build());
						return CommandResult.success();
					} else if (item.getItem().getBlock().isPresent()) {
						src.sendMessage(Text.builder("Item is a block!").color(TextColors.RED).build());
						return CommandResult.success();
					} else {
						Main.instance.disabled.add(name);
						Main.instance.getConfig().getNode("disabled-items").setValue(Main.instance.disabled);
						Main.instance.saveConfig();
						src.sendMessage(Text.builder("Item Added!").color(TextColors.GREEN).build());
						return CommandResult.success();
					}
				} else {
					src.sendMessage(Text.of("Hold an item!"));
					return CommandResult.success();
				}
			} else if (args.getAll("subcommand").contains("remove")) {
				if (!(p.hasPermission("betterrepair.remove"))) {
					src.sendMessage(Text.builder("You do not have permission!").color(TextColors.RED).build());
					return CommandResult.success();
				}
				Optional<ItemStack> itemO = p.getItemInHand(HandTypes.MAIN_HAND);
				if (itemO.isPresent()) {
					ItemStack item = itemO.get();
					String name = item.getItem().getName();
					if (Main.instance.disabled.contains(name)) {
						Main.instance.disabled.remove(name);
						Main.instance.getConfig().getNode("disabled-items").setValue(Main.instance.disabled);
						Main.instance.saveConfig();
						src.sendMessage(Text.builder("Item Removed!").color(TextColors.GREEN).build());
						return CommandResult.success();
					} else {
						src.sendMessage(Text.builder("Item isn't on the list!").color(TextColors.RED).build());
						return CommandResult.success();
					}
				} else {
					src.sendMessage(Text.of("Hold an item!"));
					return CommandResult.success();
				}
			} else if (args.getAll("subcommand").contains("reload")) {
				if (!(p.hasPermission("betterrepair.reload"))) {
					src.sendMessage(Text.builder("You do not have permission!").color(TextColors.RED).build());
					return CommandResult.success();
				}
				try {
					Main.instance.disabled = Main.instance.getConfig().getList(TypeToken.of(String.class));
				} catch (ObjectMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				src.sendMessage(Text.builder("Config Reloaded!").color(TextColors.GREEN).build());
				return CommandResult.success();
			}
			helpMenu(src);
			return CommandResult.success();
		} else {
			if ((args.hasAny("add")) || (args.hasAny("remove"))) {
				src.sendMessage(Text.of("Must be a player!"));
				return CommandResult.success();
			} else if (args.hasAny("reload")) {
				try {
					Main.instance.disabled = Main.instance.getConfig().getNode("disabled-items").getList(TypeToken.of(String.class));
				} catch (ObjectMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				src.sendMessage(Text.builder("Config Reloaded!").color(TextColors.GREEN).build());
				return CommandResult.success();
			}
			helpMenu(src);
			return CommandResult.success();
		}
	}
	
	private void helpMenu(CommandSource sender) {
		sender.sendMessage(
				Text.builder("----------------BetterRepair by cj89898----------------").color(TextColors.GOLD).build());
		sender.sendMessage(Text.of("/repair -- Repairs current item"));
		sender.sendMessage(Text.of("/repair all -- Repairs all items including armor"));
		sender.sendMessage(Text.of("/br add -- Adds current item to non-repairable list"));
		sender.sendMessage(Text.of("/br remove -- Removes current item from non-repairable list"));
		sender.sendMessage(Text.of("/br reload -- Reloads Config"));
	}
	
}
