package com.envyful.gts.forge.util;

import com.envyful.api.config.type.SQLDatabaseDetails;
import com.envyful.api.database.impl.SimpleHikariDatabase;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SimpleHikariSQLiteDatabase extends SimpleHikariDatabase {
    public SimpleHikariSQLiteDatabase(SQLDatabaseDetails details) {
        super(details);
    }
}


    private HikariDataSource hikari;

    public SimpleHikariDatabase(SQLDatabaseDetails details) {
        this(details.getConnectionUrl(), details.getPoolName(), details.getIp(), details.getPort(),
                details.getUsername(), details.getPassword(), details.getDatabase(), details.getMaxPoolSize(), details.getMaxLifeTimeSeconds()
        );
    }

    public SimpleHikariDatabase(String name, String ip, int port, String username, String password, String database) {
        this(null, name, ip, port, username, password, database, 30, 30);
    }

    public SimpleHikariDatabase(String connectionUrl, String name, String ip, int port, String username,
                                String password, String database, int maxConnections, long maxLifeTime) {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(Math.max(1, maxConnections));
        config.setPoolName(name);

        if (connectionUrl == null) {
            config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database);
        } else {
            config.setJdbcUrl(connectionUrl);
        }

        config.addDataSourceProperty("serverName", ip);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
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
        config.addDataSourceProperty("maxLifetime", TimeUnit.SECONDS.toMillis(maxLifeTime));
        config.setMaxLifetime(TimeUnit.SECONDS.toMillis(maxLifeTime));
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