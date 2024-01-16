package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.utils.CItemSlot;
import com.google.common.base.Enums;
import com.google.common.base.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenWardrobeCommand extends SubCommands{

    @Override
    public String getName() {
        return "openwardrobe";
    }

    @Override
    public String getPermission() {
        return "cosmin.command.openwardrobe";
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fopenwardrobe &9[slot] [player-name]";
    }

    @Override
    public String getDescription() {
        return "Opens wardrobe menu for specified player";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Optional<CItemSlot> slot = Enums.getIfPresent(CItemSlot.class, args[1]);
        Player player = Bukkit.getPlayer(args[2]);
        if(player != null && player.isOnline() && plugin.getConfig().getBoolean("wardrobe-enabled."+slot.toString(),true)){
            if(args[1].equalsIgnoreCase("SET")){
                plugin.guiManager.showCosmeticSetGui(player, null);
            }
            else if(slot.isPresent()){
                plugin.guiManager.showPagedGui(player, null, slot.get().getContext());
            }
        }
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }
    
}
