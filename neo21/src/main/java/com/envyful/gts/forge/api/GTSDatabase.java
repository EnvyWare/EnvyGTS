package com.envyful.gts.forge.api;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class GTSDatabase {

    /**
     *
     * Trades Table
     *
     */

    public static final Table<?> TRADES = DSL.table("envy_gts_trades");

    public static final Field<String> TRADES_OFFER_ID = DSL.field(DSL.name("envy_gts_trades", "offer_id"), String.class);

    public static final Field<String> TRADES_SELLER_UUID = DSL.field(DSL.name("envy_gts_trades", "seller_uuid"), String.class);

    public static final Field<String> TRADES_SELLER_NAME = DSL.field(DSL.name("envy_gts_trades", "seller_name"), String.class);

    public static final Field<Long> TRADES_CREATION_TIME = DSL.field(DSL.name("envy_gts_trades", "creation_time"), Long.class);

    public static final Field<Long> TRADES_EXPIRY_TIME = DSL.field(DSL.name("envy_gts_trades", "expiry_time"), Long.class);

    public static final Field<Double> TRADES_PRICE = DSL.field(DSL.name("envy_gts_trades", "price"), Double.class);

    /**
     *
     * Trade Items
     *
     */

    public static final Table<?> TRADE_ITEMS = DSL.table(DSL.name("trade_items"));

    public static final Field<String> TRADE_ITEMS_OFFER_ID = DSL.field(DSL.name("trade_items", "offer_id"), String.class);

    public static final Field<String> TRADE_ITEMS_TYPE = DSL.field(DSL.name("trade_items", "trade_item_type"), String.class);

    public static final Field<String> TRADE_ITEMS_DATA = DSL.field(DSL.name("trade_items", "item_data"), SQLDataType.CLOB);

    /**
     *
     * Trade Outcomes
     *
     */

    public static final Table<?> TRADE_OUTCOMES = DSL.table("envy_gts_trade_outcomes");

    public static final Field<String> TRADE_OUTCOMES_OFFER_ID = DSL.field(DSL.name("envy_gts_trade_outcomes", "offer_id"), String.class);

    public static final Field<String> TRADE_OUTCOMES_TYPE = DSL.field(DSL.name("envy_gts_trade_outcomes", "outcome_type"), String.class);

    public static final Field<Long> TRADE_OUTCOMES_TIME = DSL.field(DSL.name("envy_gts_trade_outcomes", "outcome_time"), Long.class);

    /**
     *
     * Auctions Table
     *
     */

    public static final Table<?> AUCTIONS = DSL.table("envy_gts_auctions");

    public static final Field<Double> AUCTION_START_PRICE = DSL.field("start_price", Double.class);

    public static final Field<Double> AUCTION_MIN_BID_INCREMENT = DSL.field("min_bid_increment", Double.class);

    /**
     *
     * Sales table
     *
     */

    public static final Table<?> SALES = DSL.table("envy_gts_sales");

    public static final Field<String> SALES_SALE_ID = DSL.field(DSL.name("envy_gts_sales", "sale_id"), String.class);

    public static final Field<String> SALES_OFFER_ID = DSL.field(DSL.name("envy_gts_sales", "offer_id"), String.class);

    public static final Field<String> SALES_BUYER_UUID = DSL.field(DSL.name("envy_gts_sales", "buyer_uuid"), String.class);

    public static final Field<String> SALES_BUYER_NAME = DSL.field(DSL.name("envy_gts_sales", "buyer_name"), String.class);

    public static final Field<Long> SALES_PURCHASE_TIME = DSL.field(DSL.name("envy_gts_sales", "purchase_time"), Long.class);

    public static final Field<Double> SALES_PURCHASE_PRICE = DSL.field(DSL.name("envy_gts_sales", "purchase_price"), Double.class);


    /**
     *
     * Collections table
     *
     */

    public static final Table<?> COLLECTIONS = DSL.table("envy_gts_collections");

    public static final Field<String> COLLECTIONS_OFFER_ID = DSL.field(DSL.name("envy_gts_collections", "offer_id"), String.class);

    public static final Field<String> COLLECTIONS_SALE_ID = DSL.field(DSL.name("envy_gts_collections", "sale_id"), String.class);

    public static final Field<String> COLLECTIONS_PLAYER = DSL.field(DSL.name("envy_gts_collections", "player"), String.class);

}
