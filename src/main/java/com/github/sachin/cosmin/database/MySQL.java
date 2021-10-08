package com.github.sachin.cosmin.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.Bukkit;

public class MySQL {

    private Cosmin plugin;

    private Connection conn = null;
    private boolean validConnection = false;
    private HikariDataSource dataSource;
    private String table;
    

    public MySQL(Cosmin plugin){
        this.plugin = plugin;
        String host,port,username,password,database,type;
        host = plugin.getConfig().getString(CosminConstants.DB_HOST);
        port = plugin.getConfig().getString(CosminConstants.DB_PORT);
        username = plugin.getConfig().getString(CosminConstants.DB_USERNAME);
        password = plugin.getConfig().getString(CosminConstants.DB_PASSWORD);
        database = plugin.getConfig().getString(CosminConstants.DB_NAME);
        type = plugin.getConfig().getString(CosminConstants.DB_TYPE,"mysql");
        this.table = plugin.getConfig().getString(CosminConstants.DB_TABLE_NAME,"cosmin_player_data");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s",type,host,port,database));
        
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
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
        return conn;
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
