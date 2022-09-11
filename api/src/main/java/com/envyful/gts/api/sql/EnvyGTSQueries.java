package com.envyful.gts.api.sql;

public class EnvyGTSQueries {

    public static final String CREATE_MAIN_TABLE = "CREATE TABLE IF NOT EXISTS `envygts_trade`(" +
            "id             INT             UNSIGNED    NOT NULL    AUTO_INCREMENT, " +
            "owner          VARCHAR(64)     NOT NULL, " +
            "ownerName      VARCHAR(16)     NOT NULL, " +
            "originalOwner  VARCHAR(16)      NOT NULL, " +
            "expiry         BIGINT          UNSIGNED    NOT NULL, " +
            "cost           DOUBLE          UNSIGNED    NOT NULL, " +
            "removed        INT             UNSIGNED    NOT NULL, " +
            "purchased      INT             UNSIGNED    NOT NULL, " +
            "type           VARCHAR(20)     NOT NULL, " +
            "content_type   VARCHAR(1)      NOT NULL, " +
            "contents   BLOB            NOT NULL, " +
            "PRIMARY KEY(id));";

    public static final String CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS `envygts_settings`(" +
            "id             INT         UNSIGNED        NOT NULL        AUTO_INCREMENT, " +
            "owner          VARCHAR(64) NOT NULL, " +
            "settings       BLOB        NOT NULL, " +
            "UNIQUE(owner), " +
            "PRIMARY KEY(id));";

    public static final String GET_ALL_TRADES = "SELECT owner, ownerName, originalOwner, expiry, cost, removed, type," +
            " content_type, " +
            "contents, purchased " +
            "FROM `envygts_trade`;";

    public static final String GET_ALL_PLAYER = "SELECT owner, ownerName, originalOwner, expiry, cost, removed, type," +
            " content_type, " +
            "purchased, contents " +
            "FROM `envygts_trade` " +
            "WHERE owner = ?;";

    public static final String UPDATE_PLAYER_NAME = "UPDATE `envygts_trade` SET ownerName = ? WHERE owner = ?;";

    public static final String UPDATE_REMOVED = "UPDATE `envygts_trade` " +
            "SET removed = ?, purchased = ? " +
            "WHERE owner = ? AND expiry = ? AND cost = ? AND content_type = ? AND type = ?;";

    public static final String ADD_TRADE = "INSERT INTO `envygts_trade`" +
            "(owner, ownerName, originalOwner, expiry, cost, removed, type, content_type, contents, purchased)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String REMOVE_TRADE = "DELETE FROM `envygts_trade` " +
            "WHERE owner = ? AND expiry = ? AND cost = ? AND content_type = ? AND type = ?;";

    public static final String UPDATE_OWNER = "UPDATE `envygts_trade` " +
            "SET owner = ?, ownerName = ? " +
            "WHERE owner = ? AND expiry = ? AND cost = ? AND content_type = ? AND type = ?;";

    public static final String GET_PLAYER_SETTINGS = "SELECT settings FROM `envygts_settings` WHERE owner = ?;";

    public static final String UPDATE_OR_CREATE_SETTINGS = "INSERT INTO `envygts_settings`(owner, settings) " +
            "VALUES (?, ?) ON DUPLICATE KEY UPDATE settings = VALUES(`settings`);";

}
