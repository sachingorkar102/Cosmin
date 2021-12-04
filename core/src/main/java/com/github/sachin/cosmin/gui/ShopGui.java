package com.github.sachin.cosmin.gui;

import com.github.sachin.cosmin.armor.CosmeticSet;
import com.github.sachin.cosmin.armor.CosminArmor;
import com.github.sachin.cosmin.utils.CosminConstants;
import com.github.sachin.cosmin.utils.ItemBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
                if(getCosminPlayer().getPurchasedItems().contains(s.getInternalName())) continue;
                if(s.getCost() != 0 || s.getPlayerPoints() != 0){
                    
                    ItemStack item = ItemBuilder.updateItemLore(s.getIcon().getItem().clone(), s.getCost(),s.getPlayerPoints());
                    this.items.add(item);
                }
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
                    if(getCosminPlayer().getPurchasedItems().contains(a.getInternalName())) continue;
                    if(a.getCost() != 0 || a.getPlayerPoints() != 0){

                        ItemStack item = ItemBuilder.updateItemLore(a.getItem().clone(), a.getCost(),a.getPlayerPoints());
                        this.items.add(item);
                    }
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

    private boolean canBuy(Player player,CosminArmor armor){
        boolean vaultBool = false;
        boolean pointsBool = false;
        if(plugin.getVaultEco() != null && armor.getCost() != 0){
            vaultBool = plugin.getVaultEco().getBalance(player) < armor.getCost();
        }
        else{
            vaultBool = true;
        }
        if(plugin.getPlayerPointsEco() != null && armor.getPlayerPoints() != 0){
            pointsBool = plugin.getPlayerPointsEco().getBalance(player) < armor.getPlayerPoints();
        }
        else{
            pointsBool = true;
        }
        return vaultBool && pointsBool;
    }

    private boolean canBuy(Player player,CosmeticSet set){
        boolean vaultBool = false;
        boolean pointsBool = false;
        if(plugin.getVaultEco() != null && set.getCost() != 0){
            vaultBool = plugin.getVaultEco().getBalance(player) < set.getCost();
        }
        if(plugin.getPlayerPointsEco() != null && set.getPlayerPoints() != 0){
            pointsBool = plugin.getPlayerPointsEco().getBalance(player) < set.getPlayerPoints();
        }
        return vaultBool && pointsBool;
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
                if(canBuy(player,set)){
                    plugin.getMessageManager().sendMessage(CosminConstants.M_NOT_ENOUGH_BALANCE, player);
                    return;
                }  
            }
            else if (ItemBuilder.isHatItem(clickedItem)){
                CosminArmor armor = plugin.getArmorManager().getArmor(ItemBuilder.getArmorName(clickedItem));
                if(canBuy(player,armor)){
                    plugin.getMessageManager().sendMessage(CosminConstants.M_NOT_ENOUGH_BALANCE, player);
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
