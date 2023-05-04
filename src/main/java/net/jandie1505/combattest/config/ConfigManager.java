package net.jandie1505.combattest.config;

import net.jandie1505.combattest.CombatTest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.logging.Level;

public class ConfigManager {
    private final CombatTest plugin;
    private final File configFile;
    private final boolean overrideDefaults;
    private JSONObject config;

    public ConfigManager(CombatTest plugin, JSONObject defaultConfig, boolean overrideDefaults, String filename) {
        this.plugin = plugin;
        this.configFile = new File(this.plugin.getDataFolder(), filename);
        this.config = new JSONObject(defaultConfig.toString());
        this.overrideDefaults = overrideDefaults;
    }

    private JSONObject loadConfig() throws IOException, JSONException {
        BufferedReader br = new BufferedReader(new FileReader(this.configFile));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        return new JSONObject(sb.toString());
    }

    private void writeConfig() throws IOException {
        FileWriter writer = new FileWriter(this.configFile);
        writer.write(this.config.toString(4));
        writer.flush();
        writer.close();
    }

    public void reloadConfig() {
        try {
            if (!this.configFile.exists()) {
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();
                this.writeConfig();
            }

            JSONObject loadedConfig = this.loadConfig();

            if (this.overrideDefaults) {

                this.config = new JSONObject(loadedConfig.toString());

            } else {

                if (loadedConfig.optBoolean("recreateConfig", false)) {
                    this.writeConfig();
                } else {
                    for (String key : loadedConfig.keySet()) {
                        this.config.put(key, loadedConfig.get(key));
                    }
                }

            }
        } catch (IOException | JSONException e) {
            this.plugin.getLogger().log(Level.WARNING, "Error loading config, using defaults");
        }
    }

    public JSONObject getConfig() {
        return new JSONObject(this.config.toString());
    }
}
