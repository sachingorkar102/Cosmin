package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        // if(!(sender instanceof Player)) return;
        // Player player = (Player) sender;
        if(args.length > 2){
            String playerName = args[2];
            String itemName = args[1];
            if(Bukkit.getPlayer(playerName) == null){
                plugin.getMessageManager().sendMessage(CosminConstants.M_OFFLINE_PLAYER, sender);
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if(plugin.getArmorManager().getInternalNames().contains(itemName) && targetPlayer.isOnline()){
                targetPlayer.getInventory().addItem(plugin.getArmorManager().getArmor(itemName).getItem());
                sender.sendMessage(plugin.getMessageManager().getMessage(CosminConstants.M_GAVE_ITEM).replace("%item%", itemName).replace("%player%", targetPlayer.getName()));
            }
            else{
                plugin.getMessageManager().sendMessage(CosminConstants.M_INVALID_ITEM, sender);
            }
            
        }
        else if(args.length > 1){
            Player player = (Player) sender;
            if(plugin.getArmorManager().getInternalNames().contains(args[1])){
                player.getInventory().addItem(plugin.getArmorManager().getArmor(args[1]).getItem());
                sender.sendMessage(plugin.getMessageManager().getMessage(CosminConstants.M_GAVE_ITEM).replace("%item%", args[1]).replace("%player%", player.getName()));
            }
        }
        
    }
    
}
