package com.github.sachin.cosmin.integration;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class CosminPAPIExpansion extends PlaceholderExpansion{

    private Cosmin plugin;

    public CosminPAPIExpansion(Cosmin plugin){
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "sachingorkar";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cosmin";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if(plugin.getPlayerManager().containsPlayer(player)){
            CosminPlayer cPlayer = plugin.getPlayerManager().getPlayer(player);
            if(params.equalsIgnoreCase("purchased_items_total")){
                return String.valueOf(cPlayer.getPurchasedItems().size());
            }
            else if(params.equalsIgnoreCase("purchased_items_percentage")){
                return String.valueOf(cPlayer.getPurchasedItems().size()*100/plugin.getArmorManager().getAllArmor().size());
            }
            else if(params.equalsIgnoreCase("purchased_sets")){
                return String.valueOf(cPlayer.getPurchasedSets().size());
            }
            else if(params.equalsIgnoreCase("purchased_sets_percentage")){
                if(plugin.getArmorManager().getCosmeticSets() != null){
                    return String.valueOf(cPlayer.getPurchasedSets().size()*100/plugin.getArmorManager().getCosmeticSets().size());
                }
            }
            else if(params.equalsIgnoreCase("purchased_items_head")){
                return getPurchasedItemAmount(cPlayer, CItemSlot.HEAD);
            }
            else if(params.equalsIgnoreCase("purchased_items_chest")){
                return getPurchasedItemAmount(cPlayer, CItemSlot.CHEST);
            }
            else if(params.equalsIgnoreCase("purchased_items_legs")){
                return getPurchasedItemAmount(cPlayer, CItemSlot.LEGS);
            }
            else if(params.equalsIgnoreCase("purchased_items_feet")){
                return getPurchasedItemAmount(cPlayer, CItemSlot.FEET);
            }
            else if(params.equalsIgnoreCase("purchased_items_offhand")){
                return getPurchasedItemAmount(cPlayer, CItemSlot.OFFHAND);
            }
            
        }
        return null;
    }


    private String getPurchasedItemAmount(CosminPlayer player,CItemSlot slot){
        int a = 0;
        for(CosminArmor armor: plugin.getArmorManager().getAllArmor()){
            if(armor.getSlot() == slot && player.getPurchasedItems().contains(armor.getInternalName())){
                a++;
            }
        }
        return String.valueOf(a);
    }
    
}
