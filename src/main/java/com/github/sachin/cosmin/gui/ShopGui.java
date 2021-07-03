package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class ShopGui extends PagedGui{

    public ShopGui(Player player,GuiContext context) {
        super(player, context);
    }

    public ShopGui(Player player,Player targetPlayer,GuiContext context){
        super(player,targetPlayer, context);
    }

    @Override
    public void setItems() {
        
        if(context == GuiContext.COSMETIC_SET_GUI){
            for(CosmeticSet s:plugin.getArmorManager().getCosmeticSets().values()){
                String perm = s.getPermission();
                // ItemStack item;
                if(!perm.equals("none")){
                    if(!getPlayer().hasPermission(perm)) continue;
                }
                if(s.getCost() == 0 || getCosminPlayer().getPurchasedSets().contains(s.getInternalName())) continue;
                ItemStack item = ItemBuilder.updateItemLore(s.getIcon().getItem().clone(),s.getCost()); 
                this.items.add(item);
            }
        }
        else{
            for(CosminArmor a:plugin.getArmorManager().getAllArmor()){
                if(a.hide()) continue;
                if(a.getContext() == context){
                    String perm = a.getPermission();
                    if(!perm.equals("none")){
                        if(!player.hasPermission(perm)){
                            continue;
                        }
                    }
                    if(a.getCost() == 0 || getCosminPlayer().getPurchasedItems().contains(a.getInternalName())) continue;
                    ItemStack item = ItemBuilder.updateItemLore(a.getItem().clone(), a.getCost());
                    this.items.add(item);
                }
            }   
        }
    }

    @Override
    public void openPage(){
        if(items.isEmpty() || items == null) return;
        this.inventory = Bukkit.createInventory(this, 54,getTitle());
        setBorderItems();
        inventory.setItem(50, miscItems.getFillerGlass());
        player.openInventory(inventory); 
    }

    @Override
    public String getTitle() {
        String pageString = String.valueOf(page+1);
        
        String title = plugin.getConfig().getString(GuiContext.SHOP_PAGE.getTitle(),"GUI").replace("%page%", pageString);
        if(plugin.isPAPIEnabled()){
            title = PlaceholderAPI.setPlaceholders(getPlayer(), title);
        }
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    @Override
    public void handlePageClicks(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        handleMiscClicks(e);
        ItemStack clickedItem = e.getCurrentItem();
        if((e.getClickedInventory().getHolder() instanceof ShopGui) && CosminConstants.HAT_SLOTS.contains(e.getSlot())){
            
            if(ItemBuilder.isCosmeticSetIcon(clickedItem)){
                CosmeticSet set = plugin.getArmorManager().getSet(ItemBuilder.getCosmeticSetIconValue(clickedItem));
                if(plugin.getEconomy().getBalance(player) < set.getCost()){
                    plugin.getConfigUtils().sendMessage(player, CosminConstants.MESSAGE_LESS_BALANCE);
                    return;
                }  
            }
            else if (ItemBuilder.isHatItem(clickedItem)){
                CosminArmor armor = plugin.getArmorManager().getArmor(ItemBuilder.getArmorName(clickedItem));
                if(plugin.getEconomy().getBalance(player) < armor.getCost()){
                    plugin.getConfigUtils().sendMessage(player, CosminConstants.MESSAGE_LESS_BALANCE);
                    return;
                }    
            }

            if(targetPlayer == null){
                plugin.guiManager.showConfirmGui(player,null, context, clickedItem);
            }
            else {
                plugin.guiManager.showConfirmGui(player, targetPlayer, context, clickedItem);
            }
            
        }
        
    }
}
