package com.envyful.gts.forge;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.database.DatabaseDetailsConfig;
import com.envyful.api.config.database.DatabaseDetailsRegistry;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.database.Database;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.chat.ComponentTextFormatter;
import com.envyful.api.neoforge.command.ForgeCommandFactory;
import com.envyful.api.neoforge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.neoforge.gui.factory.ForgeGuiFactory;
import com.envyful.api.neoforge.platform.ForgePlatformHandler;
import com.envyful.api.neoforge.player.ForgePlayerManager;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.sqlite.config.SQLiteDatabaseDetailsConfig;
import com.envyful.gts.forge.api.TradeService;
import com.envyful.gts.forge.api.gui.FilterTypeFactory;
import com.envyful.gts.forge.api.gui.impl.*;
import com.envyful.gts.forge.api.service.jOOQTradeService;
import com.envyful.gts.forge.command.GTSCommand;
import com.envyful.gts.forge.config.EnvyGTSConfig;
import com.envyful.gts.forge.config.GuiConfig;
import com.envyful.gts.forge.config.LocaleConfig;
import com.envyful.gts.forge.listener.TradeCreateListener;
import com.envyful.gts.forge.listener.WebhookListener;
import com.envyful.gts.forge.api.player.GTSAttribute;
import com.envyful.gts.forge.api.GTSDatabase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mod("envygts")
public class EnvyGTSForge {

    private static final Logger LOGGER = LogManager.getLogger("EnvyGTS");

    private static EnvyGTSForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private EnvyGTSConfig config;
    private LocaleConfig locale;
    private GuiConfig gui;

    private Database database;
    private TradeService tradeService;
    private DSLContext dslContext;

    public EnvyGTSForge() {
        SQLiteDatabaseDetailsConfig.register();
        UtilLogger.setLogger(LOGGER);

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setPlayerManager(this.playerManager);
        PlatformProxy.setTextFormatter(ComponentTextFormatter.getInstance());

        NeoForge.EVENT_BUS.register(this);
        instance = this;
    }

    @SubscribeEvent
    public void onServerStarting(ServerAboutToStartEvent event) {
        FilterTypeFactory.init();
        FilterTypeFactory.register(new AllFilterType());
        FilterTypeFactory.register(new InstantBuyFilterType());
        FilterTypeFactory.register(new OwnFilterType());
        FilterTypeFactory.register(new ItemFilterType());
        FilterTypeFactory.register(new PokemonFilterType());

        this.loadConfig();
        this.playerManager.setGlobalSaveMode(DatabaseDetailsRegistry.getRegistry().getKey((Class<DatabaseDetailsConfig>) this.getConfig().getDatabaseDetails().getClass()));

        this.playerManager.registerAttribute(GTSAttribute.class, GTSAttribute::new);

        this.database = this.config.getDatabaseDetails().createDatabase();

        dslContext = DSL.using(this.database, this.config.getDatabaseDetails() instanceof SQLiteDatabaseDetailsConfig ? SQLDialect.SQLITE : SQLDialect.MARIADB);

        createTables();
        tradeService = new jOOQTradeService();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        new TradeCreateListener();
        NeoForge.EVENT_BUS.register(new WebhookListener());
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(EnvyGTSConfig.class);
            this.gui = YamlConfigFactory.getInstance(GuiConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
        } catch (IOException e) {
            LOGGER.error("Error while loading configs", e);
        }
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

    public static TradeService getTradeService() {
        return instance.tradeService;
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

    public static DSLContext getDSLContext() {
        return instance.dslContext;
    }

    private void createTables() {
        dslContext.createTableIfNotExists(GTSDatabase.TRADES)
                .columns(
                        GTSDatabase.TRADES_OFFER_ID,
                        GTSDatabase.TRADES_SELLER_UUID,
                        GTSDatabase.TRADES_SELLER_NAME,
                        GTSDatabase.TRADES_CREATION_TIME,
                        GTSDatabase.TRADES_EXPIRY_TIME,
                        GTSDatabase.TRADES_PRICE
                )
                .primaryKey(GTSDatabase.TRADES_OFFER_ID)
                .execute();

        dslContext.createTableIfNotExists(GTSDatabase.TRADE_ITEMS)
                .columns(
                        GTSDatabase.TRADE_ITEMS_OFFER_ID,
                        GTSDatabase.TRADE_ITEMS_TYPE,
                        GTSDatabase.TRADE_ITEMS_DATA
                )
                .primaryKey(GTSDatabase.TRADES_OFFER_ID)
                .constraint(DSL.foreignKey(GTSDatabase.TRADES_OFFER_ID).references(GTSDatabase.TRADES, GTSDatabase.TRADES_OFFER_ID))
                .execute();

        dslContext.createTableIfNotExists(GTSDatabase.TRADE_OUTCOMES)
                .columns(
                        GTSDatabase.TRADE_OUTCOMES_OFFER_ID,
                        GTSDatabase.TRADE_OUTCOMES_TYPE,
                        GTSDatabase.TRADE_OUTCOMES_TIME
                )
                .primaryKey(GTSDatabase.TRADES_OFFER_ID)
                .constraint(DSL.foreignKey(GTSDatabase.TRADES_OFFER_ID).references(GTSDatabase.TRADES, GTSDatabase.TRADES_OFFER_ID))
                .execute();

        dslContext.createTableIfNotExists(GTSDatabase.SALES)
                .columns(
                        GTSDatabase.SALES_SALE_ID,
                        GTSDatabase.SALES_OFFER_ID,
                        GTSDatabase.SALES_BUYER_UUID,
                        GTSDatabase.SALES_BUYER_NAME,
                        GTSDatabase.SALES_PURCHASE_TIME,
                        GTSDatabase.SALES_PURCHASE_PRICE
                )
                .primaryKey(GTSDatabase.SALES_SALE_ID)
                .constraint(DSL.unique(GTSDatabase.SALES_OFFER_ID))
                .constraint(DSL.foreignKey(GTSDatabase.SALES_OFFER_ID).references(GTSDatabase.TRADES, GTSDatabase.TRADES_OFFER_ID))
                .execute();

        dslContext.createTableIfNotExists(GTSDatabase.COLLECTIONS)
                .columns(
                        GTSDatabase.COLLECTIONS_PLAYER,
                        GTSDatabase.COLLECTIONS_OFFER_ID,
                        GTSDatabase.COLLECTIONS_SALE_ID
                )
                .primaryKey(GTSDatabase.COLLECTIONS_OFFER_ID)
                .constraint(DSL.foreignKey(GTSDatabase.COLLECTIONS_SALE_ID).references(GTSDatabase.SALES, GTSDatabase.SALES_SALE_ID))
                .constraint(DSL.foreignKey(GTSDatabase.COLLECTIONS_OFFER_ID).references(GTSDatabase.TRADES, GTSDatabase.TRADES_OFFER_ID))
                .execute();
    }
}
