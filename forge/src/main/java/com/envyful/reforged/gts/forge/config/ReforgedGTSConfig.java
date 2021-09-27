package com.envyful.reforged.gts.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigPath("config/ReforgedGTS/config.yml")
@ConfigSerializable
public class ReforgedGTSConfig extends AbstractYamlConfig {

    private DatabaseDetails databaseDetails = new DatabaseDetails();
    private ConfigInterface guiSettings = new ConfigInterface();

    private int tradeDurationSeconds = 86400;

    public ReforgedGTSConfig() {
        super();
    }

    public DatabaseDetails getDatabaseDetails() {
        return this.databaseDetails;
    }

    public int getTradeDurationSeconds() {
        return this.tradeDurationSeconds;
    }

    public ConfigInterface getGuiSettings() {
        return this.guiSettings;
    }

    @ConfigSerializable
    public static class DatabaseDetails {

        private String poolName = "ReforgedGTS";
        private String ip = "0.0.0.0";
        private int port = 3306;
        private String username = "admin";
        private String password = "admin";
        private String database = "database";

        public String getPoolName() {
            return this.poolName;
        }

        public String getIp() {
            return this.ip;
        }

        public int getPort() {
            return this.port;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public String getDatabase() {
            return this.database;
        }
    }
}
