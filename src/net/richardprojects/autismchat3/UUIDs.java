package net.richardprojects.autismchat3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class UUIDs {

	private final AutismChat3 plugin;
	private YamlConfiguration uuidConfig;
	private File configFile;
	private boolean loaded;

	public UUIDs(AutismChat3 plugin) {
		this.plugin = plugin;
		loaded = false;
	}

	public void load(String filename) {
		configFile = new File(plugin.getDataFolder(), filename);

		if (configFile.exists()) {
			uuidConfig = new YamlConfiguration();
			try {
				uuidConfig.load(configFile);
			} catch (FileNotFoundException localFileNotFoundException) {
			} catch (IOException localIOException) {
			} catch (InvalidConfigurationException localInvalidConfigurationException) {
			}
			loaded = true;
		} else {
			try {
				configFile.createNewFile();
				uuidConfig = new YamlConfiguration();
				uuidConfig.load(configFile);
			} catch (IOException localIOException1) {
			} catch (InvalidConfigurationException localInvalidConfigurationException1) {
			}
		}
	}

	public void save() {
		try {
			uuidConfig.save(configFile);
		} catch (IOException localIOException) {
		}
	}

	public File getFile() {
		return configFile;
	}

	public YamlConfiguration getConfig() {
		if (!loaded) {
			load("uuids.yml");
		}
		return uuidConfig;
	}
}
