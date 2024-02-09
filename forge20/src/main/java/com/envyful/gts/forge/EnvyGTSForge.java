package com.envyful.gts.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.envyful.api.database.sql.UtilSql;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.platform.ForgePlatformHandler;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.gts.api.GlobalTradeManager;
import com.envyful.gts.api.TradeManager;
import com.envyful.gts.api.discord.DiscordEventManager;
import com.envyful.gts.api.gui.FilterTypeFactory;
import com.envyful.gts.api.sql.EnvyGTSQueries;
import com.envyful.gts.forge.command.GTSCommand;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.config.LocaleConfig;
import com.envyful.gts.forge.impl.filter.*;
import com.envyful.gts.forge.impl.storage.SQLGlobalTradeManager;
import com.envyful.gts.forge.listener.TradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeCreateListener;
import com.envyful.gts.forge.listener.discord.DiscordTradePurchaseListener;
import com.envyful.gts.forge.listener.discord.DiscordTradeRemoveListener;
import com.envyful.gts.forge.player.GTSAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("envygts")
public class EnvyGTSForge {

    public static final String VERSION = "4.5.6";

    private static Logger LOGGER = LogManager.getLogger("EnvyGTS");

    private static EnvyGTSForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private EnvyGTSConfig config;
    private LocaleConfig locale;
    private GuiConfig gui;

    private Database database;
    private GlobalTradeManager tradeManager;

    public EnvyGTSForge() {
        UtilLogger.setLogger(LOGGER);
        UtilSql.registerDriver();
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());

        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        FilterTypeFactory.init();
        FilterTypeFactory.register(new ForgeAllFilterType());
        FilterTypeFactory.register(new ForgeInstantBuyFilterType());
        FilterTypeFactory.register(new ForgeOwnFilterType());
        FilterTypeFactory.register(new ItemFilterType());
        FilterTypeFactory.register(new PokemonFilterType());

        this.playerManager.registerAttribute(GTSAttribute.class, GTSAttribute::new);

        this.loadConfig();

        this.database = new SimpleHikariDatabase(this.config.getDatabaseDetails());
        this.createTables();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        new TradeCreateListener();
        new DiscordTradeCreateListener();
        new DiscordTradePurchaseListener();
        new DiscordTradeRemoveListener();

        UtilConcurrency.runAsync(() -> {
            this.tradeManager = new SQLGlobalTradeManager();
            TradeManager.setPlatformTradeManager(this.tradeManager);
        });
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(EnvyGTSConfig.class);
            this.gui = YamlConfigFactory.getInstance(GuiConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
        } catch (IOException e) {
            LOGGER.error("Error while loading configs", e);
        }

        if (this.config.isEnableWebHooks()) {
            DiscordEventManager.init();
        } else {
            LOGGER.info("Skipping WebHook setup as it is disabled in the config");
        }
    }

    private void createTables() {
        UtilSql.update(this.database)
                .query(EnvyGTSQueries.CREATE_MAIN_TABLE)
                .executeAsync();

        UtilSql.update(this.database)
                .query(EnvyGTSQueries.CREATE_SETTINGS_TABLE)
                .executeAsync();
    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new GTSCommand()));
    }

    public static EnvyGTSForge getInstance() {
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

    public static Logger getLogger() {
        return LOGGER;
    }
}
