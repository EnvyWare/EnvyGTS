package com.envyful.gts.forge.api.service;

import com.envyful.api.concurrency.UtilConcurrency;
import com.envyful.gts.forge.EnvyGTSForge;
import com.envyful.gts.forge.api.RemovalInfo;
import com.envyful.gts.forge.api.Sale;
import com.envyful.gts.forge.api.TradeOffer;
import com.envyful.gts.forge.api.item.TradeItem;
import com.envyful.gts.forge.api.money.InstantPurchaseMoney;
import com.envyful.gts.forge.api.player.PlayerInfo;
import com.envyful.gts.forge.api.trade.ActiveTrade;
import com.envyful.gts.forge.api.trade.ExpiredTrade;
import com.envyful.gts.forge.api.trade.RemovedTrade;
import com.envyful.gts.forge.api.trade.PriceStatistics;
import com.envyful.gts.forge.api.trade.SoldTrade;
import com.envyful.gts.forge.api.trade.Trade;
import com.envyful.gts.forge.api.trade.TradeHistory;
import com.envyful.gts.forge.api.trade.TradeQuery;
import com.envyful.gts.forge.api.GTSDatabase;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class jOOQTradeService extends CachedTradeService {

    private static final PlayerInfo UNKNOWN_PLAYER = new PlayerInfo(new UUID(0L, 0L), "Unknown");

    private static final int MAX_HISTORY_RESULTS = 1000;

    private static final int NAME_BACKFILL_BATCH_SIZE = 500;

    private static final char LIKE_ESCAPE = '!';

    private static final List<SelectFieldOrAsterisk> HISTORY_FIELDS = List.of(
            GTSDatabase.TRADES_OFFER_ID,
            GTSDatabase.TRADES_SELLER_UUID,
            GTSDatabase.TRADES_SELLER_NAME,
            GTSDatabase.TRADES_CREATION_TIME,
            GTSDatabase.TRADES_EXPIRY_TIME,
            GTSDatabase.TRADES_PRICE,
            GTSDatabase.TRADE_ITEMS_TYPE,
            GTSDatabase.TRADE_ITEMS_DATA,
            GTSDatabase.TRADE_OUTCOMES_TYPE,
            GTSDatabase.TRADE_OUTCOMES_TIME,
            GTSDatabase.SALES_SALE_ID,
            GTSDatabase.SALES_OFFER_ID,
            GTSDatabase.SALES_BUYER_UUID,
            GTSDatabase.SALES_BUYER_NAME,
            GTSDatabase.SALES_PURCHASE_TIME,
            GTSDatabase.SALES_PURCHASE_PRICE
    );

    private static final AggregateFunction<Integer> SALES_COUNT = DSL.count(GTSDatabase.SALES_SALE_ID);

    private static final AggregateFunction<Double> LOWEST_PRICE = DSL.min(GTSDatabase.SALES_PURCHASE_PRICE);

    private static final AggregateFunction<Double> HIGHEST_PRICE = DSL.max(GTSDatabase.SALES_PURCHASE_PRICE);

    private static final AggregateFunction<BigDecimal> MEAN_PRICE = DSL.avg(GTSDatabase.SALES_PURCHASE_PRICE);

    private static final List<SelectFieldOrAsterisk> STATISTICS_FIELDS = List.of(
            SALES_COUNT,
            LOWEST_PRICE,
            HIGHEST_PRICE,
            MEAN_PRICE
    );

    public jOOQTradeService() {
        super();

        var records = EnvyGTSForge.getDSLContext()
                .select(
                        // Trades
                        GTSDatabase.TRADES_OFFER_ID,
                        GTSDatabase.TRADES_SELLER_UUID,
                        GTSDatabase.TRADES_SELLER_NAME,
                        GTSDatabase.TRADES_CREATION_TIME,
                        GTSDatabase.TRADES_EXPIRY_TIME,
                        GTSDatabase.TRADES_PRICE,

                        // Trade items
                        GTSDatabase.TRADE_ITEMS_TYPE,
                        GTSDatabase.TRADE_ITEMS_DATA
                )
                .from(GTSDatabase.TRADES)
                .join(GTSDatabase.TRADE_ITEMS)
                .on(GTSDatabase.TRADE_ITEMS_OFFER_ID
                        .eq(GTSDatabase.TRADES_OFFER_ID))
                .andNotExists(
                        EnvyGTSForge.getDSLContext().selectOne()
                                .from(GTSDatabase.TRADE_OUTCOMES)
                                .where(GTSDatabase.TRADE_OUTCOMES_OFFER_ID
                                        .eq(GTSDatabase.TRADES_OFFER_ID))
                )
                .fetch();

        try {
            for (var record : records) {
                var tradeId = UUID.fromString(record.get(GTSDatabase.TRADES_OFFER_ID));
                this.activeListings.put(tradeId, new ActiveTrade(
                        new TradeOffer(
                                tradeId,
                                new PlayerInfo(
                                        UUID.fromString(record.get(GTSDatabase.TRADES_SELLER_UUID)),
                                        record.get(GTSDatabase.TRADES_SELLER_NAME)
                                ),
                                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_CREATION_TIME)),
                                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_EXPIRY_TIME)),
                                TradeItem.deserialize(
                                        record.get(GTSDatabase.TRADE_ITEMS_TYPE),
                                        record.get(GTSDatabase.TRADE_ITEMS_DATA)
                                ),
                                new InstantPurchaseMoney(record.get(GTSDatabase.TRADES_PRICE))
                        )
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load active trades from database", e);
        }
    }

    @Override
    public TradeHistory completedListings(TradeQuery query) {
        return this.deserializeHistory(this.historyQuery(this.completedCondition(query))
                .orderBy(GTSDatabase.TRADE_OUTCOMES_TIME.desc())
                .maxRows(MAX_HISTORY_RESULTS)
                .fetch());
    }

    @Override
    public TradeHistory highestPrices(TradeQuery query) {
        return this.soldPrices(query, GTSDatabase.SALES_PURCHASE_PRICE.desc());
    }

    @Override
    public TradeHistory lowestPrices(TradeQuery query) {
        return this.soldPrices(query, GTSDatabase.SALES_PURCHASE_PRICE.asc());
    }

    private TradeHistory soldPrices(TradeQuery query, SortField<Double> priceOrder) {
        return this.deserializeHistory(this.historyQuery(this.soldCondition(query))
                .orderBy(priceOrder, GTSDatabase.SALES_PURCHASE_TIME.desc())
                .maxRows(MAX_HISTORY_RESULTS)
                .fetch());
    }

    @Override
    public PriceStatistics priceStatistics(TradeQuery query) {
        var record = this.query(STATISTICS_FIELDS, this.soldCondition(query)).fetchOne();

        if (record == null || record.get(SALES_COUNT) == null || record.get(SALES_COUNT) == 0) {
            return PriceStatistics.EMPTY;
        }

        return new PriceStatistics(
                record.get(SALES_COUNT),
                orZero(record.get(LOWEST_PRICE)),
                orZero(record.get(HIGHEST_PRICE)),
                orZero(record.get(MEAN_PRICE))
        );
    }

    private static double orZero(Number value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private Condition completedCondition(TradeQuery query) {
        return this.filter(query.since() == null ?
                DSL.noCondition() :
                GTSDatabase.TRADE_OUTCOMES_TIME.ge(query.since().toEpochMilli()), query);
    }

    private Condition soldCondition(TradeQuery query) {
        var condition = GTSDatabase.TRADE_OUTCOMES_TYPE.eq("SOLD")
                .and(GTSDatabase.SALES_SALE_ID.isNotNull());

        if (query.since() != null) {
            condition = condition.and(GTSDatabase.SALES_PURCHASE_TIME.ge(query.since().toEpochMilli()));
        }

        return this.filter(condition, query);
    }

    private Condition filter(Condition condition, TradeQuery query) {
        if (query.itemType() != null) {
            condition = condition.and(GTSDatabase.TRADE_ITEMS_TYPE.eq(query.itemType().id()));
        }

        if (query.hasSearch()) {
            condition = condition.and(searchCondition(query.search()));
        }

        if (query.hasPlayer()) {
            condition = condition.and(playerCondition(query.player()));
        }

        return condition;
    }

    /**
     *
     * Matches trades that the player was either the seller or the buyer of. Names are matched without case, as
     * that is how players type them, and the UUID is matched exactly
     *
     */
    private static Condition playerCondition(String player) {
        var uniqueId = player.toLowerCase(Locale.ROOT);

        return GTSDatabase.TRADES_SELLER_UUID.eq(uniqueId)
                .or(GTSDatabase.TRADES_SELLER_NAME.equalIgnoreCase(player))
                .or(GTSDatabase.SALES_BUYER_UUID.eq(uniqueId))
                .or(GTSDatabase.SALES_BUYER_NAME.equalIgnoreCase(player));
    }

    /**
     *
     * Matches the search against both the name the item was listed under, and the key it can be found by, so
     * that a Pokemon can be found by its species as well as by the nickname it was sold with. Spaces are also
     * tried as underscores, so that "diamond sword" finds "minecraft:diamond_sword"
     *
     */
    private static Condition searchCondition(String search) {
        var normalised = search.toLowerCase(Locale.ROOT).trim();
        var condition = GTSDatabase.TRADE_ITEMS_SEARCH_KEY.lower().like(likePattern(normalised), LIKE_ESCAPE)
                .or(GTSDatabase.TRADE_ITEMS_NAME.lower().like(likePattern(normalised), LIKE_ESCAPE));
        var underscored = normalised.replace(' ', '_');

        if (!underscored.equals(normalised)) {
            condition = condition.or(GTSDatabase.TRADE_ITEMS_SEARCH_KEY.lower()
                    .like(likePattern(underscored), LIKE_ESCAPE));
        }

        return condition;
    }

    private static String likePattern(String search) {
        return "%" + search
                .replace(String.valueOf(LIKE_ESCAPE), LIKE_ESCAPE + String.valueOf(LIKE_ESCAPE))
                .replace("%", LIKE_ESCAPE + "%")
                .replace("_", LIKE_ESCAPE + "_") + "%";
    }

    @Override
    public void addListing(Trade trade) {
        super.addListing(trade);

        UtilConcurrency.runAsync(() -> {
            EnvyGTSForge.getDSLContext()
                    .insertInto(GTSDatabase.TRADES)
                    .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                    .set(GTSDatabase.TRADES_SELLER_UUID, trade.offer().seller().uniqueId().toString())
                    .set(GTSDatabase.TRADES_SELLER_NAME, trade.offer().seller().name())
                    .set(GTSDatabase.TRADES_CREATION_TIME, trade.offer().creationTime().toEpochMilli())
                    .set(GTSDatabase.TRADES_EXPIRY_TIME, trade.offer().expiryTime().toEpochMilli())
                    .set(GTSDatabase.TRADES_PRICE, trade.offer().price().getPrice())
                    .execute();

            EnvyGTSForge.getDSLContext()
                    .insertInto(GTSDatabase.TRADE_ITEMS)
                    .set(GTSDatabase.TRADE_ITEMS_OFFER_ID, trade.offer().id().toString())
                    .set(GTSDatabase.TRADE_ITEMS_TYPE, trade.offer().item().id())
                    .set(GTSDatabase.TRADE_ITEMS_DATA, trade.offer().item().serialize())
                    .set(GTSDatabase.TRADE_ITEMS_NAME, trade.offer().item().displayName())
                    .set(GTSDatabase.TRADE_ITEMS_SEARCH_KEY, trade.offer().item().searchKey())
                    .execute();
        });
    }

    @Override
    protected void onTradeExpire(Trade trade) {
        super.onTradeExpire(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "EXPIRED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void addSale(Sale sale) {
        super.addSale(sale);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.SALES)
                .set(GTSDatabase.SALES_SALE_ID, sale.saleId().toString())
                .set(GTSDatabase.TRADES_OFFER_ID, sale.offerId().toString())
                .set(GTSDatabase.SALES_BUYER_UUID, sale.buyer().uniqueId().toString())
                .set(GTSDatabase.SALES_BUYER_NAME, sale.buyer().name())
                .set(GTSDatabase.SALES_PURCHASE_TIME, sale.time().toEpochMilli())
                .set(GTSDatabase.SALES_PURCHASE_PRICE, sale.purchasePrice())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, sale.offerId().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "SOLD")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void adminRemoveListing(Trade trade) {
        super.adminRemoveListing(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "ADMIN_REMOVED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void ownerRemoveListing(Trade trade) {
        super.ownerRemoveListing(trade);

        EnvyGTSForge.getDSLContext()
                .insertInto(GTSDatabase.TRADE_OUTCOMES)
                .set(GTSDatabase.TRADES_OFFER_ID, trade.offer().id().toString())
                .set(GTSDatabase.TRADE_OUTCOMES_TYPE, "OWNER_REMOVED")
                .set(GTSDatabase.TRADE_OUTCOMES_TIME, System.currentTimeMillis())
                .executeAsync(UtilConcurrency.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public void onServerStarted() {
        UtilConcurrency.runAsync(this::backfillSearchableNames);
    }

    /**
     *
     * Fills in the searchable name of every trade item that was listed before those columns existed, as they
     * can only be read back out of the serialized item data
     *
     */
    private void backfillSearchableNames() {
        try {
            var records = EnvyGTSForge.getDSLContext()
                    .select(
                            GTSDatabase.TRADE_ITEMS_OFFER_ID,
                            GTSDatabase.TRADE_ITEMS_TYPE,
                            GTSDatabase.TRADE_ITEMS_DATA
                    )
                    .from(GTSDatabase.TRADE_ITEMS)
                    .where(GTSDatabase.TRADE_ITEMS_SEARCH_KEY.isNull())
                    .fetch();

            if (records.isEmpty()) {
                return;
            }

            EnvyGTSForge.getLogger().info("Making {} older GTS trade items searchable", records.size());

            var updates = new ArrayList<Query>();
            var failed = 0;

            for (var record : records) {
                try {
                    var item = TradeItem.deserialize(
                            record.get(GTSDatabase.TRADE_ITEMS_TYPE),
                            record.get(GTSDatabase.TRADE_ITEMS_DATA)
                    );

                    updates.add(EnvyGTSForge.getDSLContext()
                            .update(GTSDatabase.TRADE_ITEMS)
                            .set(GTSDatabase.TRADE_ITEMS_NAME, item.displayName())
                            .set(GTSDatabase.TRADE_ITEMS_SEARCH_KEY, item.searchKey())
                            .where(GTSDatabase.TRADE_ITEMS_OFFER_ID.eq(record.get(GTSDatabase.TRADE_ITEMS_OFFER_ID))));
                } catch (Exception e) {
                    ++failed;
                    EnvyGTSForge.getLogger().debug("Could not read the GTS trade item for {}, so it will not be searchable",
                            record.get(GTSDatabase.TRADE_ITEMS_OFFER_ID), e);
                }
            }

            for (int i = 0; i < updates.size(); i += NAME_BACKFILL_BATCH_SIZE) {
                EnvyGTSForge.getDSLContext()
                        .batch(updates.subList(i, Math.min(i + NAME_BACKFILL_BATCH_SIZE, updates.size())))
                        .execute();
            }

            EnvyGTSForge.getLogger().info("Made {} older GTS trade items searchable", updates.size());

            if (failed > 0) {
                EnvyGTSForge.getLogger().warn("{} GTS trade items could not be read, so will not be found by searches",
                        failed);
            }
        } catch (Exception e) {
            EnvyGTSForge.getLogger().error("Failed to make the older GTS trade items searchable", e);
        }
    }

    private SelectConditionStep<Record> historyQuery(Condition condition) {
        return this.query(HISTORY_FIELDS, condition);
    }

    private SelectConditionStep<Record> query(List<SelectFieldOrAsterisk> fields, Condition condition) {
        return EnvyGTSForge.getDSLContext()
                .select(fields)
                .from(GTSDatabase.TRADES)
                .join(GTSDatabase.TRADE_ITEMS)
                .on(GTSDatabase.TRADE_ITEMS_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .join(GTSDatabase.TRADE_OUTCOMES)
                .on(GTSDatabase.TRADE_OUTCOMES_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .leftJoin(GTSDatabase.SALES)
                .on(GTSDatabase.SALES_OFFER_ID.eq(GTSDatabase.TRADES_OFFER_ID))
                .where(condition);
    }

    private TradeHistory deserializeHistory(Result<Record> records) {
        var history = new ArrayList<Trade>();
        var failed = 0;

        for (var record : records) {
            try {
                history.add(this.deserializeHistoricalTrade(record));
            } catch (Exception e) {
                ++failed;
                EnvyGTSForge.getLogger().error("Failed to read historical GTS trade {} from the database",
                        record.get(GTSDatabase.TRADES_OFFER_ID), e);
            }
        }

        if (failed > 0) {
            EnvyGTSForge.getLogger().error("{} of the {} historical GTS trades read could not be deserialized and have been omitted",
                    failed, records.size());
        }

        return new TradeHistory(List.copyOf(history), failed);
    }

    private Trade deserializeHistoricalTrade(Record record) throws CommandSyntaxException {
        var tradeId = UUID.fromString(record.get(GTSDatabase.TRADES_OFFER_ID));
        var offer = new TradeOffer(
                tradeId,
                new PlayerInfo(
                        UUID.fromString(record.get(GTSDatabase.TRADES_SELLER_UUID)),
                        record.get(GTSDatabase.TRADES_SELLER_NAME)
                ),
                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_CREATION_TIME)),
                Instant.ofEpochMilli(record.get(GTSDatabase.TRADES_EXPIRY_TIME)),
                TradeItem.deserialize(
                        record.get(GTSDatabase.TRADE_ITEMS_TYPE),
                        record.get(GTSDatabase.TRADE_ITEMS_DATA)
                ),
                new InstantPurchaseMoney(record.get(GTSDatabase.TRADES_PRICE))
        );

        var outcomeType = record.get(GTSDatabase.TRADE_OUTCOMES_TYPE);
        var outcomeTime = Instant.ofEpochMilli(record.get(GTSDatabase.TRADE_OUTCOMES_TIME));
        var sale = this.deserializeSale(record);

        return switch (outcomeType.toUpperCase(Locale.ROOT)) {
            case "SOLD" -> sale == null ?
                    new RemovedTrade(offer, new RemovalInfo(UNKNOWN_PLAYER, outcomeTime, "SOLD")) :
                    new SoldTrade(offer, sale);
            case "EXPIRED" -> new ExpiredTrade(offer, outcomeTime);
            default -> new RemovedTrade(offer, new RemovalInfo(UNKNOWN_PLAYER, outcomeTime, outcomeType));
        };
    }

    private Sale deserializeSale(Record record) {
        var saleId = record.get(GTSDatabase.SALES_SALE_ID);

        if (saleId == null) {
            return null;
        }

        return new Sale(
                UUID.fromString(saleId),
                UUID.fromString(record.get(GTSDatabase.SALES_OFFER_ID)),
                new PlayerInfo(
                        UUID.fromString(record.get(GTSDatabase.SALES_BUYER_UUID)),
                        record.get(GTSDatabase.SALES_BUYER_NAME)
                ),
                Instant.ofEpochMilli(record.get(GTSDatabase.SALES_PURCHASE_TIME)),
                record.get(GTSDatabase.SALES_PURCHASE_PRICE)
        );
    }
}
