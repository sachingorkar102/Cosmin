package com.github.sachin.cosmin.commands;


import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CosmeticCommand extends BukkitCommand{

    private Cosmin plugin;

    public CosmeticCommand(Cosmin plugin){
        super("cosmetic");
        this.plugin = plugin;
        setAliases(plugin.getConfigUtils().getCommandAliases());
        setDescription("Opens cosmetic armor gui");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(CosminConstants.ISDEMO && !player.isOp()){
                plugin.getMessageManager().sendMessage(CosminConstants.M_NO_PERM, player);
                return false;
            }
            if(player.hasPermission(CosminConstants.PERM_COMMAND_COSMETICS)){
                plugin.guiManager.showFakeGui(player,null);
            }else{
                plugin.getMessageManager().sendMessage(CosminConstants.M_NO_PERM, player);
            }
        }
        return true;
    }
    
}
