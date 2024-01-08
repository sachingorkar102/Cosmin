package com.github.sachin.cosmin.compat;

import com.github.sachin.cosmin.Cosmin;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.CItemSlot;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
            else if(params.equalsIgnoreCase("armor_head_name")){
                return getArmorName(cPlayer, CItemSlot.HEAD);
            }
            else if(params.equalsIgnoreCase("armor_chest_name")){
                return getArmorName(cPlayer, CItemSlot.CHEST);
            }
            else if(params.equalsIgnoreCase("armor_legs_name")){
                return getArmorName(cPlayer, CItemSlot.LEGS);
            }
            else if(params.equalsIgnoreCase("armor_feet_name")){
                return getArmorName(cPlayer, CItemSlot.FEET);
            }
            else if(params.equalsIgnoreCase("armor_offhand_name")){
                return getArmorName(cPlayer, CItemSlot.OFFHAND);
            }
            
        }
        return "";
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

    private String getArmorName(CosminPlayer player,CItemSlot slot){
        ItemStack item = player.getSlotItem(slot);
        if(item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
            return item.getItemMeta().getDisplayName();
        }
        return "";
    }
    
}
