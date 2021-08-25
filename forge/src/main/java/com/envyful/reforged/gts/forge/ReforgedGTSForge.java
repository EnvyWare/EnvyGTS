package com.envyful.reforged.gts.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeUpdateBuilder;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.bstats.forge.Metrics;

import java.io.IOException;
import java.nio.file.Paths;

@Mod(
        modid = "reforgedgts",
        name = "ReforgedGTS Forge",
        version = ReforgedGTSForge.VERSION,
        acceptableRemoteVersions = "*"
)
public class ReforgedGTSForge {

    public static final String VERSION = "0.3.0";

    private static ReforgedGTSForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private ReforgedGTSConfig config;
    private Database database;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        instance = this;

        this.loadConfig();

        UtilConcurrency.runAsync(() -> {
            ReforgedGTSConfig.DatabaseDetails databaseDetails = this.config.getDatabaseDetails();
            this.database = new SimpleHikariDatabase(
                    databaseDetails.getPoolName(),
                    databaseDetails.getIp(),
                    databaseDetails.getPort(),
                    databaseDetails.getUsername(),
                    databaseDetails.getPassword(),
                    databaseDetails.getDatabase()
            );
        });

        Metrics metrics = new Metrics(
                Loader.instance().activeModContainer(),
                event.getModLog(),
                Paths.get("config/"),
                12199 //TODO:
        );

        ForgeUpdateBuilder.instance()
                .name("ReforgedGTS")
                .requiredPermission("reforged.gts.update.notify")
                .owner("Pixelmon-Development")
                .repo("ReforgedGTS")
                .version(VERSION)
                .start();
    }

    private void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ReforgedGTSConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.playerManager.registerAttribute(this, GTSAttribute.class);
    }

    public static ReforgedGTSForge getInstance() {
        return instance;
    }

    public ReforgedGTSConfig getConfig() {
        return this.config;
    }

    public Database getDatabase() {
        return this.database;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
