package com.github.sachin.cosmin.gui;

import org.bukkit.entity.Player;

public class ConfirmGui extends GuiHolder{

    private String itemName;

    public ConfirmGui(Player player,Player targetPlayer,GuiContext context,String itemName){
        super(player, targetPlayer, context);
        this.itemName = itemName;
    }


    public String getItemName() {
        return itemName;
    }
    


    
}
