package com.envyful.gts.forge.api;

import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SimpleHikariSQLiteDatabase implements Database {

    private HikariDataSource hikari;

    public SimpleHikariSQLiteDatabase(SQLDatabaseDetails details) {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(Math.max(1, details.getMaxPoolSize()));
        config.setPoolName(details.getPoolName());

        config.setJdbcUrl("jdbc:sqlite:config/EnvyGTS/gts.sqlite");

        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("characterEncoding","utf8");
        config.addDataSourceProperty("useUnicode","true");
        config.addDataSourceProperty("maxLifetime", TimeUnit.SECONDS.toMillis(details.getMaxLifeTimeSeconds()));
        config.setMaxLifetime(TimeUnit.SECONDS.toMillis(details.getMaxLifeTimeSeconds()));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));
        config.setConnectionTestQuery("/* Ping */ SELECT 1");

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }

    @Override
    public void close() {
        this.hikari.close();
    }
}