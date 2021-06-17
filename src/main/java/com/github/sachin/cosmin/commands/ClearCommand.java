package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.database.PlayerData;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand extends SubCommands{

    private Cosmin plugin;

    public ClearCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_CLEAR;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fclear &9[player_name]";
    }

    @Override
    public String getDescription() {
        return "clears specified player data from files and MySQL database(if connected)";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length > 1){
            String playerName = args[1];
            if(Bukkit.getServer().getPlayer(playerName) == null){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6player dosn't exists or is offline"));
                return;
            }
            Player player = Bukkit.getServer().getPlayer(playerName);
            if(plugin.getPlayerManager().containsPlayer(player)){
                CosminPlayer cPlayer = plugin.getPlayerManager().getPlayer(player);
                cPlayer.clearCosminInv();
                cPlayer.computeAndPutEquipmentPairList();
                cPlayer.setFakeSlotItems();
                cPlayer.sendPacketWithinRange(60);
                plugin.getPlayerManager().removePlayer(player.getUniqueId());
                if(plugin.getConfigUtils().isMySQLEnabled()){
                    if(plugin.MySQL().isConnected()){
                        PlayerData playerData = new PlayerData(player.getUniqueId());
                        if(playerData.playerExists()){
                            playerData.clearPlayerData();
                        }
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6player data removed successfully"));
            }

        }
    }
    
}
