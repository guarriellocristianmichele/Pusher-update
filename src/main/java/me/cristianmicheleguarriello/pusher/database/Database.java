package me.cristianmicheleguarriello.pusher.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Database {

    private final HikariDataSource ds;

    public Database(final String host, final short port, final String database, final String username, final String password) {

        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        this.ds = new HikariDataSource(config);
    }

    private Connection getConnection() {
        try {
            return this.ds.getConnection();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getString(final String table, final String key) {
        final Connection connection = Objects.requireNonNull(this.getConnection(), "SQL Connection is null");

        try {
            final PreparedStatement ps = connection.prepareStatement(String.format("SELECT * FROM %s", table));
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final String result = rs.getString(key);
                rs.close();
                ps.close();
                connection.close();
                return result;
            }
            connection.close();
        } catch (final SQLException e) {
            e.printStackTrace();

            try {
                connection.close();
            } catch (final SQLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

}
