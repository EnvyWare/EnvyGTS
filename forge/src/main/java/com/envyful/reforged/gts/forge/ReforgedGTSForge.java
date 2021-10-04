package com.envyful.reforged.gts.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.reforged.gts.api.GlobalTradeManager;
import com.envyful.reforged.gts.api.sql.ReforgedGTSQueries;
import com.envyful.reforged.gts.forge.command.GTSCommand;
import com.envyful.reforged.gts.forge.config.GuiConfig;
import com.envyful.reforged.gts.forge.config.LocaleConfig;
import com.envyful.reforged.gts.forge.config.ReforgedGTSConfig;
import com.envyful.reforged.gts.forge.config.discord.DiscordEventManager;
import com.envyful.reforged.gts.forge.impl.storage.SQLGlobalTradeManager;
import com.envyful.reforged.gts.forge.player.GTSAttribute;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    private LocaleConfig locale;
    private GuiConfig gui;

    private Database database;
    private GlobalTradeManager tradeManager;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        instance = this;

        this.loadConfig();

        if (this.config.isEnableWebHooks()) {
            DiscordEventManager.init();
        }

        UtilConcurrency.runAsync(() -> {
            this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());
            this.createTables();
            this.tradeManager = new SQLGlobalTradeManager(this);
        });
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ReforgedGTSConfig.class);
            this.gui = YamlConfigFactory.getInstance(GuiConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Connection connection = this.database.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(ReforgedGTSQueries.CREATE_MAIN_TABLE)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.playerManager.registerAttribute(this, GTSAttribute.class);
        this.commandFactory.registerCommand(event.getServer(), new GTSCommand());
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

    public GlobalTradeManager getTradeManager() {
        return this.tradeManager;
    }

    public LocaleConfig getLocale() {
        return this.locale;
    }

    public GuiConfig getGui() {
        return this.gui;
    }
}
