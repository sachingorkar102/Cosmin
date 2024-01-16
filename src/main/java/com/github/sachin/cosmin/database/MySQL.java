package com.github.sachin.cosmin.database;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private Cosmin plugin;

    private Connection conn = null;
    private boolean validConnection = false;
    private HikariDataSource dataSource;
    private String table;
    

    public MySQL(Cosmin plugin){
        this.plugin = plugin;
        
        String host,port,username,password,database,type;
        int maxLifeTime;
        boolean autoReconnect;
        host = plugin.getConfig().getString(CosminConstants.DB_HOST);
        port = plugin.getConfig().getString(CosminConstants.DB_PORT);
        username = plugin.getConfig().getString(CosminConstants.DB_USERNAME);
        password = plugin.getConfig().getString(CosminConstants.DB_PASSWORD);
        database = plugin.getConfig().getString(CosminConstants.DB_NAME);
        maxLifeTime = plugin.getConfig().getInt(CosminConstants.DB_MAX_LIFE_TIME);
        type = plugin.getConfig().getString(CosminConstants.DB_TYPE,"mysql");
        autoReconnect = plugin.getConfig().getBoolean(CosminConstants.DB_AUTO_RECONNECT);
        this.table = plugin.getConfig().getString(CosminConstants.DB_TABLE_NAME,"cosmin_player_data");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s",type,host,port,database));
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setMaxLifetime(Integer.toUnsignedLong(maxLifeTime*1000));
//        hikariConfig.getConnectionTestQuery()
        hikariConfig.addDataSourceProperty("autoReconnect",autoReconnect);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        dataSource = new HikariDataSource(hikariConfig);

        connect();
    }

    public void connect(){
        try {
            conn = dataSource.getConnection();
            createTable();
            plugin.getLogger().info("Connected to MySQL database..");
            validConnection = true;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error occured while connecting to database");
            validConnection = false;
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return conn != null;
    }

    public void disconnect(){
        if(isConnected()){
            try {
                conn.close();
            } catch (SQLException e) {
                plugin.getLogger().warning("Error occured while disconnecting with database");
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        if(conn == null && validConnection){
            connect();
        }
        if(isClosed() && validConnection){
            connect();
        }
        return conn;
    }

    public boolean isClosed(){
        try {
            return conn != null && conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public void createTable(){
        Bukkit.getScheduler().runTaskAsynchronously(Cosmin.getInstance(), () -> {
            try {
                PreparedStatement pStatement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+table+" (Player VARCHAR(50),UUID VARCHAR(50),Contents LONGTEXT NOT NULL,PurchasedItems LONGTEXT NOT NULL,PurchasedSets LONGTEXT NOT NULL)");
                pStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public String getTable() {
        return table;
    }
    
}
