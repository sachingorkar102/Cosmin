package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommands{

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_RELOAD;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &freload";
    }

    @Override
    public String getDescription() {
        return "Reloads all cosmin config files";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Cosmin.getInstance().reloadAllConfigs();
        if(sender instanceof Player){
            Player p = (Player) sender;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', CosminConstants.MESSAGE_PREFIX+"&6Config files successfully loaded"));
       }
        
    }
    
}
