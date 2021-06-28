package com.december.friends.database;


import com.december.friends.Friends;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlManager {

    private final Friends plugin;
    private HikariDataSource source;

    public MysqlManager(Friends plugin) {
        this.plugin = plugin;
        try {
            String host = plugin.getConfig().getString("mysql.host");
            String user = plugin.getConfig().getString("mysql.username");
            String pass = plugin.getConfig().getString("mysql.password");
            String database = plugin.getConfig().getString("mysql.database");
            int port = plugin.getConfig().getInt("mysql.port");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&allowMultiQueries=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false");
            //config.setDriverClassName("com.mysql.jdbc.Driver");
            config.setUsername(user);
            config.setPassword(pass);
            config.setMaxLifetime(1800000L);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(10);
            config.setConnectionTimeout(8000);
            config.setLeakDetectionThreshold(48000L);
            config.addDataSourceProperty("cachePrepStmts", "true");
            source = new HikariDataSource(config);
            createTables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createTables() {
        performUpdate("CREATE TABLE IF NOT EXISTS `users` (`uuid` varchar(255), `username` varchar(255), `friends` JSON, `received` JSON, `sent` JSON)");
    }

    public void close() {
        this.source.close();
    }

    public void performAsyncQuery(String query, QueryCallback successCallback, Object... replacements) {
        performAsyncQuery(query, successCallback, null, replacements);
    }

    public void performAsyncQuery(String query, QueryCallback successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> performQuery(query, successCallback, exceptionCallback, replacements));
    }

    public CachedRowSet performQuery(String query, Object... replacements) {
        return performQuery(query, (ExceptionCallback) null, replacements);
    }

    public void performQuery(String query, QueryCallback successCallBack, Object... replacements) {
        performQuery(query, successCallBack, null, replacements);
    }

    public void performQuery(String query, QueryCallback successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        CachedRowSet set = performQuery(query, exceptionCallback, replacements);

        if (set != null) {
            try {
                successCallback.accept(set);
            } catch (SQLException ex) {
                try {
                    if (exceptionCallback != null) {
                        exceptionCallback.accept(ex);
                    }

                    ex.printStackTrace();
                } catch (CancelExceptionSignal ignored) {

                }
            }
        }
    }

    public CachedRowSet performQuery(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        try (Connection con = source.getConnection()) {
            PreparedStatement s = con.prepareStatement(query);

            int i = 0;

            for (Object replacement : replacements) {
                if (replacement != null) s.setObject(++i, replacement);
            }

            ResultSet set = s.executeQuery();
            CachedRowSet cachedSet = RowSetProvider.newFactory().createCachedRowSet();

            cachedSet.populate(set);

            return cachedSet;
        } catch (SQLException ex) {
            try {
                if (exceptionCallback != null) {
                    exceptionCallback.accept(ex);
                }

                ex.printStackTrace();
            } catch (CancelExceptionSignal ignored) {

            }
        }

        return null;
    }

    public void performAsyncUpdate(String query, Object... replacements) {
        performAsyncUpdate(query, null, null, replacements);
    }

    public void performAsyncUpdate(String query, Runnable successCallback, Object... replacements) {
        performAsyncUpdate(query, successCallback, null, replacements);
    }

    public void performAsyncUpdate(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        performAsyncUpdate(query, null, exceptionCallback, replacements);
    }

    public void performAsyncUpdate(String query, Runnable successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> performUpdate(query, successCallback, exceptionCallback, replacements));
    }

    public void performUpdate(String query, Object... replacements) {
        performUpdate(query, null, null, replacements);
    }

    public void performUpdate(String query, Runnable successCallback, Object... replacements) {
        performUpdate(query, successCallback, null, replacements);
    }

    public void performUpdate(String query, ExceptionCallback exceptionCallback, Object... replacements) {
        performUpdate(query, null, exceptionCallback, replacements);
    }

    public void performUpdate(String query, Runnable successCallback, ExceptionCallback exceptionCallback, Object... replacements) {
        try (Connection con = source.getConnection()) {
            PreparedStatement s = con.prepareStatement(query);

            int i = 0;

            for (Object replacement : replacements) {
                s.setObject(++i, replacement);
            }

            s.executeUpdate();

            if (successCallback != null) {
                successCallback.run();
            }
        } catch (SQLException ex) {
            try {
                if (exceptionCallback != null) {
                    exceptionCallback.accept(ex);
                }

                ex.printStackTrace();
            } catch (CancelExceptionSignal ignored) {

            }
        }
    }

    public static class CancelExceptionSignal extends RuntimeException {

        public final CancelExceptionSignal INSTANCE = new CancelExceptionSignal();
        private final long serialVersionUID = -653468396254880100L;

        private CancelExceptionSignal() {
        }
    }
}
