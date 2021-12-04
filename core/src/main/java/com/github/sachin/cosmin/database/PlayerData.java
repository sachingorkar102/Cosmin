package com.github.sachin.cosmin.database;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {


    // private CosminPlayer cPlayer;
    private UUID uuid;
    private String TABLE;

    public PlayerData(UUID uuid){
        this.uuid = uuid;
        this.TABLE = Cosmin.getInstance().MySQL().getTable();

    }   

    
    private Connection getConnection(){
        return Cosmin.getInstance().MySQL().getConnection();
    }

    public boolean playerExists(){
        
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("SELECT * FROM "+TABLE+" WHERE UUID=?");
            pStatement.setString(1, uuid.toString());
            ResultSet result = pStatement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void createPlayerData(){
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("INSERT INTO "+TABLE+"(`Player`,`UUID`,`Contents`,`PurchasedItems`,`PurchasedSets`) VALUES (?,?,?,?,?)");
            pStatement.setString(1, Bukkit.getPlayer(uuid).getName());
            pStatement.setString(2, uuid.toString());
            pStatement.setString(3, " ");
            pStatement.setString(4, " ");
            pStatement.setString(5, " ");
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Bukkit.getScheduler().runTaskAsynchronously(Cosmin.getInstance(), () -> {
        // });
    }

    public void updatePlayerData(String data,Set<String> purchasedItems,Set<String> purchasedSets){
        if(!playerExists()) createPlayerData();
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("UPDATE "+TABLE+" SET Contents=?,PurchasedItems=?,PurchasedSets=? WHERE UUID=?");
            pStatement.setString(1, data); 
            pStatement.setString(2, purchasedItems.toString().replace(" ", "").replace("[", "").replace("]", ""));
            pStatement.setString(3, purchasedSets.toString().replace(" ", "").replace("[", "").replace("]", ""));
            pStatement.setString(4, uuid.toString());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Bukkit.getScheduler().runTaskAsynchronously(Cosmin.getInstance(), () -> {

        // });
    }

    public String getPlayerData(){
        
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("SELECT Contents FROM "+TABLE+" WHERE UUID=?");
            pStatement.setString(1, uuid.toString());
            ResultSet result = pStatement.executeQuery();
            if(result.next()){
                return result.getString(1);
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getPurchasedItems(String type){
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("SELECT "+type+" FROM "+TABLE+" WHERE UUID=?");
            pStatement.setString(1, uuid.toString());
            ResultSet result = pStatement.executeQuery();
            if(result.next()){
                String s = result.getString(1);
                Set<String> items = new HashSet<>(Arrays.asList(s.split(",")));
                items.remove("");
                return items;
            }
            else {
                return new HashSet<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public void clearPlayerData(){
        try {
            PreparedStatement pStatement = getConnection().prepareStatement("DELETE FROM "+TABLE+" WHERE UUID=?");
            pStatement.setString(1, uuid.toString());
            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
