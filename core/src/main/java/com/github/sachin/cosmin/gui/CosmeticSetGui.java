package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.player.CosminPlayer;
import com.github.sachin.cosmin.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class CosmeticSetGui extends PagedGui{


    public CosmeticSetGui(Player player) {
        super(player, GuiContext.COSMETIC_SET_GUI);

    }

    public CosmeticSetGui(Player player,Player targetPlayer) {
        super(player,targetPlayer, GuiContext.COSMETIC_SET_GUI);

    }

    @Override
    public void handlePageClicks(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        handleMiscClicks(e);
        if(e.getClickedInventory().getHolder() instanceof CosmeticSetGui){
            if (ItemBuilder.isCosmeticSetIcon(e.getCurrentItem())){
                updateArmorSets(e.getCurrentItem());
            }
        }
        
        
        
    }

    private void updateArmorSets(ItemStack item){
        String armorSetName = ItemBuilder.getCosmeticSetIconValue(item);
        CosminPlayer cPlayer = targetPlayer == null ? plugin.getPlayerManager().getPlayer(player) : plugin.getPlayerManager().getPlayer(targetPlayer);
        cPlayer.updateArmorSets(armorSetName, false);
        revertToFakeGui();

    }

    @Override
    public void setItems() {
        for(CosmeticSet set: plugin.getArmorManager().getCosmeticSets().values()){
            String perm = set.getPermission();
            if(!perm.equals("none")){
                if(!player.hasPermission(perm)){
                    continue;
                }
            }
            if(set.getCost() != 0 && !getCosminPlayer().getPurchasedSets().contains(set.getInternalName())) continue;
            items.add(set.getIcon().getItem());
        }
    }
}
