package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.nbtapi.NBTItem;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends SubCommands{

    private Cosmin plugin;

    public GiveCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_GIVE;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fgive &9[armor_name] (player_name)";
    }

    @Override
    public String getDescription() {
        return "Gives player the specified armor piece";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // if(!(sender instanceof Player)) return;
        // Player player = (Player) sender;
        if(args.length > 2){
            String playerName = args[2];
            String itemName = args[1];
            if(Bukkit.getPlayer(playerName) == null){

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Player dosnt exsist"));
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if(plugin.getArmorManager().getInternalNames().contains(itemName) && targetPlayer.isOnline()){
                targetPlayer.getInventory().addItem(plugin.getArmorManager().getArmor(itemName).getItem());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Given "+itemName+" to "+targetPlayer.getName()));
            }
            else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Armor piece could not be found"));
            }
            
        }
        else if(args.length > 1){
            Player player = (Player) sender;
            if(plugin.getArmorManager().getInternalNames().contains(args[1])){
                player.getInventory().addItem(plugin.getArmorManager().getArmor(args[1]).getItem());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Given "+args[1]+" to "+player.getName()));
            }
        }
        
    }
    
}
