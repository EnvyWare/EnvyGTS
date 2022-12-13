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
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.command.GTSCommand;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.config.LocaleConfig;
import com.envyful.gts.forge.impl.filter.ItemFilterType;
import com.envyful.gts.forge.impl.filter.PokemonFilterType;
import com.envyful.gts.forge.impl.storage.SQLGlobalTradeManager;
import com.envyful.gts.forge.listener.TradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradePurchaseListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeRemoveListener;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Mod("envygts")
public class EnvyGTSForge {

    public static final String VERSION = "4.0.1";

    private static EnvyGTSForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private EnvyGTSConfig config;
    private LocaleConfig locale;
    private GuiConfig gui;

    private Database database;
    private GlobalTradeManager tradeManager;

    public EnvyGTSForge() {
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerAboutToStartEvent event) {
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        FilterTypeFactory.init();
        FilterTypeFactory.register(new ItemFilterType());
        FilterTypeFactory.register(new PokemonFilterType());

        this.playerManager.registerAttribute(this, GTSAttribute.class);

        this.loadConfig();

        UtilConcurrency.runAsync(() -> {
            this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());
            this.createTables();
        });
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        new TradeCreateListener();
        new DiscordTradeCreateListener();
        new DiscordTradePurchaseListener();
        new DiscordTradeRemoveListener();

        UtilConcurrency.runAsync(() -> {
            this.tradeManager = new SQLGlobalTradeManager(this);
            TradeManager.setPlatformTradeManager(this.tradeManager);
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

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new GTSCommand());
    }

    public static EnvyGTSForge getInstreance() {
        return instance;
    }

    public static EnvyGTSConfig getConfig() {
        return instance.config;
    }

    public static Database getDatabase() {
        return instance.database;
    }

    public static ForgePlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static GlobalTradeManager getTradeManager() {
        return instance.tradeManager;
    }

    public static LocaleConfig getLocale() {
        return instance.locale;
    }

    public static GuiConfig getGui() {
        return instance.gui;
    }
}
