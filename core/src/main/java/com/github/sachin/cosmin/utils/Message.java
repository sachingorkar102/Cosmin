package com.github.sachin.cosmin.utils;

import com.github.sachin.cosmin.Cosmin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Message {

    private Cosmin plugin;
    private FileConfiguration messageConfig;
    public final String fileName = "messages.yml";
    

    public Message(Cosmin plugin){
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(),fileName);
        if(!file.exists()){
            plugin.saveResource(fileName, false);
        }
        try {
            ConfigUpdater.update(plugin, fileName, file, new ArrayList<>());
        } catch (IOException e) {
            plugin.getLogger().info("Error occured while updating "+fileName);
            e.printStackTrace();
        }
        this.messageConfig = YamlConfiguration.loadConfiguration(file);
    }


    public String getMessage(String key){
        String str = messageConfig.getString("prefix")+messageConfig.getString(key);
        String message = ColorUtils.applyColor(str);
        return message;
    }


    public String getMessage(String key,Player player){
        String message = getMessage(key);
        if(plugin.isPAPIEnabled()){
           message = me.clip.placeholderapi.PlaceholderAPI.setBracketPlaceholders(player, message);
        }
        return message;
    }

    public void sendMessage(String key,Player player){
        player.sendMessage(getMessage(key, player));
    }

    public void sendMessage(String key,CommandSender sender){
        sender.sendMessage(getMessage(key));
    }

    public String getPrefix(){
        return messageConfig.getString("prefix");
    }
    
}
