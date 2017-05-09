package net.cjservers.betterrepair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import net.cjservers.betterrepair.commands.AdminCommands;
import net.cjservers.betterrepair.commands.Repair;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

@Plugin(id = "betterrepair", name = "Better Repair", version = "1.1.0", dependencies = @Dependency(id = "worldedit", optional = true))
public class Main {
	
	public static Main instance;
	public List<String> disabled;
	public List<String> disabledMods;
	
	@Inject
	private Logger logger;
	
	@Listener
	public void init(GameInitializationEvent event) {
		instance = this;
		createConfig();
		
		HashMap<String, Integer> repairCmds = new HashMap<String, Integer>();
		repairCmds.put("all", 0);
		HashMap<String, String> adminCmds = new HashMap<String, String>();
		adminCmds.put("add", "add");
		adminCmds.put("remove", "remove");
		adminCmds.put("reload", "reload");
		
		CommandSpec repair = CommandSpec.builder().description(Text.of("Repair current item"))
				.permission("betterrepair.repair")
				.arguments(GenericArguments.optional(GenericArguments.choices(Text.of("all"), repairCmds)))
				.executor(new Repair()).build();
		CommandSpec adminCmd = CommandSpec.builder().description(Text.of("BetterRepair Main Command"))
				.arguments(GenericArguments.optional(GenericArguments.choices(Text.of("subcommand"), adminCmds)))
				.executor(new AdminCommands()).build();
		
		Sponge.getCommandManager().register(this, repair, "repair");
		Sponge.getCommandManager().register(this, adminCmd, "betterrepair", "br");
		
		this.getConfig();
		
		try {
			disabled = getConfig().getNode("disabled-items").getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			disabledMods = getConfig().getNode("disabled-mods").getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Listener
	public void reload(GameReloadEvent event){
		try {
			Main.instance.disabled = Main.instance.getConfig().getList(TypeToken.of(String.class));
		} catch (ObjectMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sponge.getServer().getConsole().sendMessage(Text.builder("[BetterRepair] Config Reloaded!").color(TextColors.GREEN).build());
	}
	
	public Logger getLogger() {
		return logger;
	}
	@Inject @ConfigDir(sharedRoot = false) private Path configDir;
	private ConfigurationLoader<ConfigurationNode> confLoader;
	private ConfigurationNode conf;
	
	public void createConfig() {
		Path configFile = configDir.resolve("betterrepair.yml");
		if(!Files.exists(configFile)){
			try {
				Files.createDirectories(configDir);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Sponge.getAssetManager().getAsset(this, "betterrepair.yml").get().copyToFile(configFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.confLoader = YAMLConfigurationLoader.builder().setPath(configFile).build();
		conf = confLoader.createEmptyNode(ConfigurationOptions.defaults());
		loadConfig();
		saveConfig();
	}
	
	public ConfigurationNode getConfig() {
		return conf;
	}
	
	public void saveConfig() {
		try {
			confLoader.save(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadConfig() {
		try {
			conf = confLoader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
