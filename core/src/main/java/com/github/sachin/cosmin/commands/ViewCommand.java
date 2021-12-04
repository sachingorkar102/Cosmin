package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.utils.CosminConstants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewCommand  extends SubCommands{

    private Cosmin plugin;

    public ViewCommand(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "view";
    }

    @Override
    public String getPermission() {
        return CosminConstants.PERM_COMMAND_VIEW;
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fview &9[player_name]";
    }

    @Override
    public String getDescription() {
        return "Opens specified player's cosmin inventory";
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        if(args.length > 1){
            if(Bukkit.getPlayer(args[1]) == null){
                plugin.getMessageManager().sendMessage(CosminConstants.M_OFFLINE_PLAYER, player);
                return;
            }
            Player tarPlayer = Bukkit.getPlayer(args[1]);
            plugin.guiManager.showFakeGui(player, tarPlayer);
        }
    }
    
}
