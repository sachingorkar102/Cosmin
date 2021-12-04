package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.player.CosminPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;

public class GuiHolder  implements InventoryHolder{

    protected Inventory inventory;
    protected GuiContext context;
    protected Player player;
    protected Player targetPlayer;
    protected String title;
    protected Cosmin plugin;

    public GuiHolder(final Player player,final GuiContext context){
        this.context = context;
        this.player = player;
        this.plugin = Cosmin.getInstance();
    }

    public GuiHolder(final Player player,@Nullable final Player targetPlayer,final GuiContext context){
        this.context = context;
        this.targetPlayer = targetPlayer;
        this.player = player;
        this.plugin = Cosmin.getInstance();
    }


    public GuiContext getContext() {
        return context;
    }
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public CosminPlayer getCosminPlayer(){
        return targetPlayer == null ? plugin.getPlayerManager().getPlayer(player) : plugin.getPlayerManager().getPlayer(targetPlayer);
    }

    public Player getPlayer(){
        return targetPlayer == null ? player : targetPlayer;
    }
    

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    
    
}
