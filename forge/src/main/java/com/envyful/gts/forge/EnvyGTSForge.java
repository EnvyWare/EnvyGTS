package com.envyful.gts.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.gts.api.GlobalTradeManager;
import com.envyful.gts.api.TradeManager;
import com.envyful.gts.api.discord.DiscordEventManager;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.command.GTSCommand;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.config.LocaleConfig;
import com.envyful.gts.forge.impl.storage.SQLGlobalTradeManager;
import com.envyful.gts.forge.listener.TradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradePurchaseListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeRemoveListener;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Mod(
        modid = "envygts",
        name = "EnvyGTS Forge",
        version = EnvyGTSForge.VERSION,
        acceptableRemoteVersions = "*"
)
public class EnvyGTSForge {

    public static final String VERSION = "2.0.7";

    private static EnvyGTSForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private EnvyGTSConfig config;
    private LocaleConfig locale;
    private GuiConfig gui;

    private Database database;
    private GlobalTradeManager tradeManager;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        instance = this;

        this.loadConfig();

        UtilConcurrency.runAsync(() -> {
            this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());
            this.createTables();
        });
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(EnvyGTSConfig.class);
            this.gui = YamlConfigFactory.getInstance(GuiConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.config.isEnableWebHooks()) {
            DiscordEventManager.init();
        }
    }

    private void createTables() {
        try (Connection connection = this.database.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(EnvyGTSQueries.CREATE_MAIN_TABLE);
             PreparedStatement settingsStatement = connection.prepareStatement(EnvyGTSQueries.CREATE_SETTINGS_TABLE)) {
            preparedStatement.executeUpdate();
            settingsStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.playerManager.registerAttribute(this, GTSAttribute.class);
        this.commandFactory.registerCommand(event.getServer(), new GTSCommand());

        new TradeCreateListener(this);
        new DiscordTradeCreateListener();
        new DiscordTradePurchaseListener();
        new DiscordTradeRemoveListener();

        UtilConcurrency.runAsync(() -> {
            this.tradeManager = new SQLGlobalTradeManager(this);
            TradeManager.setPlatformTradeManager(this.tradeManager);
        });
    }

    public static EnvyGTSForge getInstance() {
        return instance;
    }

    public EnvyGTSConfig getConfig() {
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
