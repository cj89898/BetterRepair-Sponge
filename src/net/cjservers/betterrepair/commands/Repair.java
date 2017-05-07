package net.cjservers.betterrepair.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import org.spongepowered.api.data.property.item.UseLimitProperty;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.cjservers.betterrepair.Main;

public class Repair implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (src instanceof Player) {
			Player p = (Player) src;
			if (args.hasAny("all")) {
				if (!(p.hasPermission("betterrepair.repairall"))) {
					p.sendMessage(Text.of("You do not have permission!"));
					return CommandResult.success();
				}
				Inventory inv = p.getInventory();
				for (Inventory slot : inv.slots()) {
					if (slot.peek().isPresent()) {
						ItemStack item = slot.peek().get();
						String name = item.getItem().getType().getName();
						if (item.supports(BlockItemData.class)) {
							// Don't repair
						} else if (Main.instance.disabled.contains(name)) {
							// Don't repair
						} else if (Main.instance.disabledMods.contains(name.split(":")[0])) {
							// Don't repair
						} else if (item.supports(DurabilityData.class)) {
							Integer max = item.getProperty(UseLimitProperty.class).get().getValue();
							item.offer(Keys.ITEM_DURABILITY, max);
						}
						slot.set(item);
					}
				}
				p.sendMessage(Text.builder("Repaired all items!").color(TextColors.GREEN).build());
				return CommandResult.success();
			}
			Optional<ItemStack> check = p.getItemInHand(HandTypes.MAIN_HAND);
			if (check.isPresent()) {
				ItemStack item = check.get();
				String name = item.getItem().getType().getName();
				if (item.supports(BlockItemData.class)) {
					p.sendMessage(Text.builder("Item can't be Repaired!").color(TextColors.RED).build());
					return CommandResult.success();
				} else if (Main.instance.disabled.contains(name)) {
					p.sendMessage(Text.builder("Item can't be Repaired!").color(TextColors.RED).build());
					return CommandResult.success();
				} else if (Main.instance.disabledMods.contains(name.split(":")[0])) {
					p.sendMessage(Text.builder("Item can't be Repaired!").color(TextColors.RED).build());
					return CommandResult.success();
				} else if (item.supports(DurabilityData.class)) {
					Integer max = item.getProperty(UseLimitProperty.class).get().getValue();
					item.offer(Keys.ITEM_DURABILITY, max);
					p.setItemInHand(HandTypes.MAIN_HAND, item);
					p.sendMessage(Text.builder("Repaired!").color(TextColors.GREEN).build());
					return CommandResult.success();
				} else {
					p.sendMessage(Text.builder("Item can't be Repaired!").color(TextColors.RED).build());
					return CommandResult.success();
				}
			} else {
				src.sendMessage(Text.of("Hold something!"));
				return CommandResult.success();
			}
		} else {
			src.sendMessage(Text.builder("Must be a Player!").color(TextColors.GREEN).build());
			return CommandResult.success();
		}
	}
	
}
