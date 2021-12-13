package com.github.sachin.cosmin.commands;

import com.github.sachin.cosmin.gui.GuiContext;
import com.github.sachin.cosmin.utils.CItemSlot;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.google.common.base.Enums;
import com.google.common.base.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenStoreCommand extends SubCommands{

    @Override
    public String getName() {
        return "openstore";
    }

    @Override
    public String getPermission() {
        return "cosmin.command.openstore";
    }

    @Override
    public String getUsage() {
        return "&3/cosmin &fopenstore &9[slot] [player-name]";
    }

    @Override
    public String getDescription() {
        return "Opens store menu for specified player";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        
        Optional<CItemSlot> slot = Enums.getIfPresent(CItemSlot.class, args[1]);
        Player player = Bukkit.getPlayer(args[2]);
        if(player != null && player.isOnline() && plugin.getConfig().getBoolean(CosminConstants.ENABLE_STORE,true) && plugin.isEconomyEnabled()){
            if(args[1].equalsIgnoreCase("SET")){
                plugin.guiManager.showShopGui(player, null, GuiContext.COSMETIC_SET_GUI);
            }
            else if(slot.isPresent()){
                plugin.guiManager.showShopGui(player, null, slot.get().getContext());
            }
        }
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }
    
}
